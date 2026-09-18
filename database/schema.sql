-- PaisaFlow — PostgreSQL 15+ schema (PRD §11 Twin data model · DESIGN §45 · RULES §18)
-- Principles: append-only business_events (corrections supersede, never overwrite);
-- provenance on every important value; approvals + audit logged as events; PostGIS for local signals.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS postgis;

-- ---------- enums ----------
CREATE TYPE evidence_label AS ENUM ('FACT','OBSERVATION','ESTIMATE','ASSUMPTION','AI_INFERENCE');
CREATE TYPE actor_type      AS ENUM ('owner','facilitator','system');
CREATE TYPE user_role       AS ENUM ('owner','facilitator','family_viewer','admin');
CREATE TYPE txn_type        AS ENUM ('SALE','PURCHASE','PAYMENT_RECEIVED','PAYMENT_MADE','EXPENSE','RECEIVABLE','PAYABLE');
CREATE TYPE txn_status      AS ENUM ('PENDING','SETTLED','PARTIAL','CANCELLED');
CREATE TYPE stock_move      AS ENUM ('STOCK_IN','STOCK_OUT','ADJUSTMENT');
CREATE TYPE event_type      AS ENUM ('FACT','SALE','PURCHASE','PAYMENT','CUSTOMER_EVENT','INVENTORY_EVENT','DECISION','ACTION','OUTCOME','CORRECTION','GOAL','ASSUMPTION','APPROVAL');
CREATE TYPE action_status   AS ENUM ('PROPOSED','APPROVED','SNOOZED','DISMISSED','DONE');
CREATE TYPE action_kind     AS ENUM ('CUSTOMER_FOLLOWUP','SUPPLIER','FINANCIAL_ADJUSTMENT','EXPERIMENT','RECORD_KEEPING','REPORT_SHARE');
CREATE TYPE draft_channel   AS ENUM ('WHATSAPP','SMS','EMAIL');
CREATE TYPE draft_status    AS ENUM ('DRAFT','APPROVED','SENT','DISCARDED');
CREATE TYPE alert_kind      AS ENUM ('PAYMENT_DELAY','CASH_PRESSURE','INVENTORY','SCENARIO_RISK','BUSINESS_EVENT');
CREATE TYPE scenario_kind   AS ENUM ('BASE','SALES_DOWN','COST_UP','PAYMENT_DELAY','SEASONAL','COMBINED','GROWTH','LOAN','EXPANSION');
CREATE TYPE lang_code       AS ENUM ('hi','hinglish','en','bho','mag','mai');

-- ---------- identity ----------
CREATE TABLE users (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  phone         text UNIQUE NOT NULL,
  name          text,
  language      lang_code NOT NULL DEFAULT 'hi',
  role          user_role NOT NULL DEFAULT 'owner',
  created_at    timestamptz NOT NULL DEFAULT now(),
  last_seen_at  timestamptz
);

CREATE TABLE businesses (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  owner_id      uuid NOT NULL REFERENCES users(id),
  name          text NOT NULL,
  category      text NOT NULL,                      -- dairy | kirana | tailoring | ...
  village       text, block text, district text, state text DEFAULT 'Bihar',
  lat           double precision, lng double precision,
  location      geography(Point,4326),              -- PostGIS: drives Market module
  twin_version  integer NOT NULL DEFAULT 0,
  created_at    timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX businesses_location_gix ON businesses USING GIST (location);

-- facilitator / family access (RULES §18 access control)
CREATE TABLE business_access (
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  user_id       uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role          user_role NOT NULL,
  granted_by    uuid REFERENCES users(id),
  granted_at    timestamptz NOT NULL DEFAULT now(),
  revoked_at    timestamptz,
  PRIMARY KEY (business_id, user_id)
);

CREATE TABLE business_profiles (
  business_id        uuid PRIMARY KEY REFERENCES businesses(id) ON DELETE CASCADE,
  goal               text,                         -- "expansion"
  capital_on_hand    bigint,                       -- INR integer
  monthly_revenue    bigint,
  monthly_costs      bigint,
  fixed_costs        bigint,
  units              integer,                      -- e.g. cows
  unit_label         text,                         -- "cow"
  seasonal_multipliers double precision[12] DEFAULT ARRAY[1,1,1,1,1,1,1,1,1,1,1,1],
  updated_at         timestamptz NOT NULL DEFAULT now()
);

-- ---------- provenance (shared shape) ----------
-- every important value row carries: evidence, source_type, source_ref, confidence, observed_at
-- ---------- memory: append-only event log ----------
CREATE TABLE business_events (
  id            bigserial PRIMARY KEY,
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  type          event_type NOT NULL,
  payload       jsonb NOT NULL,                    -- structured, validated (never raw transcript dumps)
  evidence      evidence_label NOT NULL DEFAULT 'FACT',
  source_type   text NOT NULL,                     -- voice | text | ocr | system | what_if | bhashini
  source_ref    text,                              -- audio log id, document id
  confidence    real,
  actor         actor_type NOT NULL DEFAULT 'owner',
  actor_id      uuid REFERENCES users(id),
  approved      boolean NOT NULL DEFAULT true,
  supersedes_id bigint REFERENCES business_events(id),   -- CORRECTION points at the original
  occurred_at   timestamptz NOT NULL DEFAULT now(),
  recorded_at   timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX business_events_biz_time ON business_events (business_id, occurred_at DESC);
CREATE INDEX business_events_payload_gin ON business_events USING GIN (payload);

-- ---------- approved facts (current view, derived from events) ----------
CREATE TABLE business_facts (
  id            bigserial PRIMARY KEY,
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  key           text NOT NULL,                     -- e.g. "units.cows", "price.milk_per_litre"
  value         jsonb NOT NULL,
  evidence      evidence_label NOT NULL,
  source_event_id bigint REFERENCES business_events(id),
  confidence    real,
  valid_from    timestamptz NOT NULL DEFAULT now(),
  valid_to      timestamptz,                       -- null = current
  UNIQUE (business_id, key, valid_from)
);
CREATE INDEX business_facts_current ON business_facts (business_id, key) WHERE valid_to IS NULL;

-- ---------- money ----------
CREATE TABLE customers (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  name          text NOT NULL,
  alias         text,
  phone         text,                              -- optional, consented (minimal PII)
  consent_at    timestamptz,
  place         text,
  created_at    timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE suppliers (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  name          text NOT NULL, phone text, terms text, lead_time_days integer,
  created_at    timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE transactions (
  id            bigserial PRIMARY KEY,
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  event_id      bigint REFERENCES business_events(id),
  type          txn_type NOT NULL,
  amount        bigint NOT NULL CHECK (amount >= 0),
  customer_id   uuid REFERENCES customers(id),
  supplier_id   uuid REFERENCES suppliers(id),
  item          text, quantity numeric(12,3), unit text,
  status        txn_status NOT NULL DEFAULT 'SETTLED',
  due_date      date,
  settled_at    timestamptz,
  note          text,
  occurred_at   timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX transactions_biz_time ON transactions (business_id, occurred_at DESC);
CREATE INDEX transactions_receivables ON transactions (business_id, due_date) WHERE type='RECEIVABLE' AND status='PENDING';

-- ---------- operations ----------
CREATE TABLE inventory_items (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  name          text NOT NULL, unit text NOT NULL,
  stock         numeric(12,3) NOT NULL DEFAULT 0,
  reorder_level numeric(12,3), unit_cost bigint,
  daily_usage   numeric(12,3),
  supplier_id   uuid REFERENCES suppliers(id),
  updated_at    timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE stock_movements (
  id            bigserial PRIMARY KEY,
  item_id       uuid NOT NULL REFERENCES inventory_items(id) ON DELETE CASCADE,
  event_id      bigint REFERENCES business_events(id),
  kind          stock_move NOT NULL,
  quantity      numeric(12,3) NOT NULL,
  occurred_at   timestamptz NOT NULL DEFAULT now()
);

-- ---------- financing ----------
CREATE TABLE financing (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  lender        text, purpose text,
  principal     bigint NOT NULL, annual_rate_pct numeric(5,2) NOT NULL, tenure_months integer NOT NULL,
  moratorium_months integer NOT NULL DEFAULT 0,
  emi           bigint NOT NULL,
  disbursed_on  date, next_due date,
  is_proposed   boolean NOT NULL DEFAULT true,     -- becomes false only via an approved OUTCOME event
  outcome_event_id bigint REFERENCES business_events(id),
  created_at    timestamptz NOT NULL DEFAULT now()
);

-- ---------- simulation ----------
CREATE TABLE scenarios (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  twin_version  integer NOT NULL,
  kind          scenario_kind NOT NULL,
  decision      jsonb NOT NULL,                    -- {loan, tenure, units, ...}
  assumptions   jsonb NOT NULL,                    -- explicit, visible (RULES §6.4)
  created_by    uuid REFERENCES users(id),
  created_at    timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE simulation_results (
  id            bigserial PRIMARY KEY,
  scenario_id   uuid NOT NULL REFERENCES scenarios(id) ON DELETE CASCADE,
  variant       text NOT NULL,                     -- base | sales_-20 | cost_+15 | winter | combined
  monthly_rows  jsonb NOT NULL,                    -- [{month, revenue, costs, emi, surplus_after_emi, closing_cash, buffer_months}]
  survives      boolean NOT NULL,
  min_buffer_months numeric(6,2), annual_repayment_burden numeric(6,2),
  evidence      evidence_label NOT NULL DEFAULT 'ESTIMATE',
  engine_version text NOT NULL,
  computed_at   timestamptz NOT NULL DEFAULT now(),
  UNIQUE (scenario_id, variant)
);

-- ---------- risk & market ----------
CREATE TABLE risk_events (
  id            bigserial PRIMARY KEY,
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  factor        text NOT NULL,                     -- payment_delay | cash_buffer | repayment_burden | inventory
  severity      text NOT NULL,                     -- low | medium | high
  value         jsonb, explanation text,
  detected_at   timestamptz NOT NULL DEFAULT now(),
  resolved_at   timestamptz
);
CREATE TABLE market_evidence (
  id            bigserial PRIMARY KEY,
  business_id   uuid REFERENCES businesses(id) ON DELETE CASCADE,   -- null = shared regional signal
  kind          text NOT NULL,                     -- competitor | price | demand | scheme
  claim         text NOT NULL,
  value         jsonb,
  evidence      evidence_label NOT NULL DEFAULT 'OBSERVATION',
  source        text NOT NULL, source_url text,
  location      geography(Point,4326),
  observed_at   timestamptz NOT NULL,
  retrieved_at  timestamptz NOT NULL DEFAULT now(),
  confidence    real
);
CREATE INDEX market_evidence_gix ON market_evidence USING GIST (location);

-- ---------- twin snapshots (reproducible from events; cached for speed) ----------
CREATE TABLE twin_snapshots (
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  version       integer NOT NULL,
  caused_by_event_id bigint REFERENCES business_events(id),
  money         jsonb NOT NULL, market jsonb NOT NULL, operations jsonb NOT NULL, risk jsonb NOT NULL,
  computed_at   timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY (business_id, version)
);

-- ---------- actions, drafts, approvals, alerts ----------
CREATE TABLE actions (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  kind          action_kind NOT NULL,
  title         text NOT NULL, reason text NOT NULL,
  priority_score numeric(6,3) NOT NULL,            -- urgency·0.35 + value·0.35 + confidence·0.20 + pref·0.10
  evidence      evidence_label NOT NULL,
  requires_approval boolean NOT NULL,
  status        action_status NOT NULL DEFAULT 'PROPOSED',
  snooze_until  timestamptz,
  outcome_event_id bigint REFERENCES business_events(id),
  created_at    timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX actions_open ON actions (business_id, priority_score DESC) WHERE status IN ('PROPOSED','SNOOZED');

CREATE TABLE communication_drafts (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  action_id     uuid REFERENCES actions(id) ON DELETE CASCADE,
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  channel       draft_channel NOT NULL,
  recipient_customer_id uuid REFERENCES customers(id),
  recipient_supplier_id uuid REFERENCES suppliers(id),
  language      lang_code NOT NULL,
  body          text NOT NULL,
  status        draft_status NOT NULL DEFAULT 'DRAFT',   -- never SENT without an approvals row (RULES §11)
  created_at    timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE approvals (
  id            bigserial PRIMARY KEY,
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  subject_type  text NOT NULL,                     -- draft | action | financing | facilitator_change
  subject_id    text NOT NULL,
  approved_by   uuid NOT NULL REFERENCES users(id),
  method        text NOT NULL,                     -- tap | voice | biometric
  approved_at   timestamptz NOT NULL DEFAULT now(),
  event_id      bigint REFERENCES business_events(id)
);

CREATE TABLE alerts (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  kind          alert_kind NOT NULL,
  what          text NOT NULL, why text NOT NULL, next_step text,
  action_id     uuid REFERENCES actions(id),
  created_at    timestamptz NOT NULL DEFAULT now(),
  remind_at     timestamptz, dismissed_at timestamptz, seen_at timestamptz
);

-- ---------- conversation & voice ----------
CREATE TABLE conversations (
  id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  business_id   uuid NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
  user_id       uuid NOT NULL REFERENCES users(id),
  language      lang_code NOT NULL,
  state         jsonb NOT NULL DEFAULT '{}'::jsonb,   -- intent, known_facts, missing_facts, last_question
  started_at    timestamptz NOT NULL DEFAULT now(),
  ended_at      timestamptz
);
CREATE TABLE voice_logs (                              -- audit trail for "Audio log ID"; audio itself stored off-DB
  id            text PRIMARY KEY,                    -- e.g. VOX-BH-2024-8831
  conversation_id uuid REFERENCES conversations(id) ON DELETE CASCADE,
  language_used lang_code NOT NULL,
  transcript    text NOT NULL,
  asr_provider  text NOT NULL,                     -- bhashini | sample
  confidence    real,
  audio_uri     text,                              -- object storage path, retention-limited
  created_at    timestamptz NOT NULL DEFAULT now()
);

-- ---------- audit (RULES §15, §33) ----------
CREATE TABLE audit_logs (
  id            bigserial PRIMARY KEY,
  business_id   uuid REFERENCES businesses(id) ON DELETE SET NULL,
  actor_id      uuid REFERENCES users(id) ON DELETE SET NULL,
  actor         actor_type NOT NULL,
  action        text NOT NULL,                     -- e.g. draft.approved, facts.corrected, access.granted
  subject       text, details jsonb,
  request_id    text,
  at            timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX audit_logs_biz_time ON audit_logs (business_id, at DESC);

-- ---------- helper: keep businesses.location in sync with lat/lng ----------
CREATE OR REPLACE FUNCTION set_business_location() RETURNS trigger AS $$
BEGIN
  IF NEW.lat IS NOT NULL AND NEW.lng IS NOT NULL THEN
    NEW.location := ST_SetSRID(ST_MakePoint(NEW.lng, NEW.lat), 4326)::geography;
  END IF;
  RETURN NEW;
END $$ LANGUAGE plpgsql;
CREATE TRIGGER businesses_location_trg BEFORE INSERT OR UPDATE OF lat, lng ON businesses
FOR EACH ROW EXECUTE FUNCTION set_business_location();

-- ---------- view: current receivables (feeds Home "Due" tile + alerts) ----------
CREATE VIEW v_open_receivables AS
SELECT t.business_id, c.name AS customer, t.amount, t.due_date,
       GREATEST(0, CURRENT_DATE - t.due_date) AS days_overdue
FROM transactions t LEFT JOIN customers c ON c.id = t.customer_id
WHERE t.type = 'RECEIVABLE' AND t.status = 'PENDING';

# PaisaFlow — UI/UX & Product Design Specification
---

## 1. Design Vision

PaisaFlow should feel like a **trusted business companion**, not an accounting application.

The target entrepreneur should not need to understand:
 Accounting terminology
 Financial dashboards
 Business analytics
 AI/ML
  Spreadsheets
  Long forms
  Complex financial software
  The user should be able to open PaisaFlow and simply say:
> **“Mujhe business expand karna hai.”**
PaisaFlow handles the complexity behind the scenes.
### Core experience
```text
VOICE
  ↓
  UNDERSTAND
  ↓
  BUSINESS DIGITAL TWIN
  ↓
  ANALYSE
  ↓
  WHAT-IF
  ↓
  EXPLAIN
  ↓
  TOP 3 ACTIONS
  ↓
  USER APPROVES
  ↓
  ACTION
  ↓
  MEMORY
```
---
# 2. Design Principles
## 2.1 Voice First
The microphone is the primary interaction.
Primary CTA:
> 🎙️ **BOLIYE**
Typing is available as a fallback.
---
## 2.2 One Question at a Time
Never begin with a long financial form.
### Avoid

```text
Business name
Revenue
Expenses
Inventory
Customers
Loan
Interest
Tenure
Location
...
```
### Prefer
```text
PaisaFlow:
"Mahine ki approx sales kitni hoti hai?"
User:
"₹80,000"
PaisaFlow:
"Monthly kharcha approx kitna hai?"
```
---
## 2.3 Simple Language
Do not simply translate technical terminology.

Instead of:

> “Your DSCR is below the recommended threshold.”
Say:

> **“Loan ki monthly payment aapke current cash-flow par pressure daal sakti hai.”**

Advanced users can open:

> **Details dekhein**

---

## 2.4 Action Over Analytics

Every major analysis should end with:

# **AAJ KE TOP 3 ACTIONS**

Example:

```text
1. 💰 Cash buffer improve karein
2. 📞 Pending payments follow-up karein
3. 🔮 Lower loan scenario compare karein
```
---

## 2.5 Trust Before Intelligence

Important outputs should distinguish:

```text
FACT
OBSERVATION
ESTIMATE
ASSUMPTION
AI EXPLANATION
```

Never hide uncertainty.

---

## 2.6 User Control

PaisaFlow can prepare an action, but consequential external actions require approval.

```text
AI DRAFT
   ↓
USER REVIEW
   ↓
USER APPROVES
   ↓
ACTION
```
---

# 3. Visual Identity

PaisaFlow should feel:

- Trustworthy
- Calm
- Friendly
- Modern
- Indian
- Accessible
- Responsible
- Non-intimidating

Avoid making it look like:

- A stock-trading terminal
- A corporate ERP
- A traditional banking portal
- A generic ChatGPT clone

---

# 4. Color System

## Primary Green

```text
#33B878
```
Use for:

- Primary CTA
- Positive actions
- Growth indicators
- Voice interaction
- Selected states

## Deep Green

```text
#1F7350
```
Use for:

- Headings
- Important labels
- Trust indicators

## Background

```text
#F7FAF8
```

## Surface

```text
#FFFFFF
```

## Text

```text
Primary:   #1F2937
Secondary: #64748B
Muted:     #94A3B8
```
## Status

```text
Success: #33B878
Warning: #F5C85B
Risk:    #E96A6A
Info:    #5BAEEB
```

Status colors must communicate state, not decoration.

---

# 5. Typography

Recommended:

```text
Primary:
Inter / System Sans

Hindi:
Noto Sans Devanagari
```

### Hierarchy

```text
H1      30–36 px
H2      22–28 px
H3      17–20 px
Body    15–17 px
Small   12–14 px
```

For low-literacy users:

- Large text
- High contrast
- Short sentences
- Generous spacing
- Minimal dense paragraphs

---

# 6. Iconography

Use simple, familiar icons.

```text
🎙️ Voice
💰 Money
🏪 Business
📦 Inventory
👥 Customers
💳 Loan
📈 Growth
⚠️ Risk
📍 Local
🧠 Memory
🔮 Simulation
✓ Action
```

Production implementation should use a consistent SVG icon library rather than emoji wherever possible.

---

# 7. Layout System

## Mobile-first

Primary design target:

```text
360–430 px
```

Secondary:

```text
768–1024 px
```

Desktop:

```text
1200 px+
```

Use a responsive grid.

### Mobile

Single-column cards.

### Tablet

Two-column dashboard where appropriate.

### Desktop

Three-column information layout can be used for advanced views.

---

# 8. Navigation

## Mobile

Maximum 4–5 primary destinations.

```text

┌─────────────────────────────┐
│                             │
│          CONTENT            │
│                             │
├─────────────────────────────┤
│ 🏠       🎙️       📊      🧠 │
│ Home     Talk    Business Memory
└─────────────────────────────┘
```

Recommended:

1. Home
2. Talk
3. Business
4. Insights
5. Memory/Profile

The **Talk** action should be visually dominant.

---

# 9. Screen 01 — Splash

## Objective

Establish brand identity and trust.

```text

┌──────────────────────────────┐
│                              │
│                              │
│          PAISAFLOW           │
│                              │
│     Aapke Business Ka        │
│          Saathi              │
│                              │
│     TALK • PLAN • GROW       │
│                              │
└──────────────────────────────┘
```

Duration:

**1–2 seconds**

Avoid unnecessary animation.

---

# 10. Screen 02 — Language Selection

```text

┌──────────────────────────────┐
│ ←                            │
│                              │
│ Aap kis language mein        │
│ baat karna chahenge?         │
│                              │
│ ┌──────────────────────────┐ │
│ │ 🇮🇳 हिंदी                 │ │
│ └──────────────────────────┘ │
│                              │
│ ┌──────────────────────────┐ │
│ │ English                  │ │
│ └──────────────────────────┘ │
│                              │
│ ┌──────────────────────────┐ │
│ │ Hinglish                 │ │
│ └──────────────────────────┘ │
└──────────────────────────────┘
```

Future language expansion can include additional supported regional languages.

---

# 11. Screen 03 — First-Time Onboarding

Do not start with a registration-heavy form.

```text
┌──────────────────────────────┐
│          👋 Namaste!         │
│                              │
│ Main aapke business ko       │
│ samajhne mein help karunga.  │
│                              │
│           🎙️                 │
│         BOLIYE               │
│                              │
│ "Aapka business kya hai?"    │
│                              │
│ [ Ya type karein ]           │
└──────────────────────────────┘
```

---

# 12. Screen 04 — Voice Conversation

This is the core screen.

```text
┌──────────────────────────────┐
│ ← PaisaFlow                  │
│                              │
│        Aap boliye...         │
│                              │
│          ╭─────╮             │
│         │  🎙️  │             │
│          ╰─────╯             │
│                              │
│        Listening...          │
│                              │
│ ──────────────────────────── │
│                              │
│ "Mere paas ₹1 lakh hai..."   │
│                              │
│          [ Stop ]            │
└──────────────────────────────┘
```

### Voice states

```text
IDLE
 ↓
LISTENING
 ↓
PROCESSING
 ↓
UNDERSTANDING
 ↓
RESPONDING
```

---

# 13. Screen 05 — Information Confirmation

Critical captured information should be confirmed.

```text
┌──────────────────────────────┐
│        Maine samjha:         │
│                              │
│ 💰 Capital                   │
│ ₹1,00,000                    │
│                              │
│ 🏪 Business                  │
│ Dairy                        │
│                              │
│ 🎯 Goal                      │
│ Business Expansion           │
│                              │
│ Sahi hai?                    │
│                              │
│ [ ✓ Haan ] [ ✎ Badalna ]    │
└──────────────────────────────┘
```

---

# 14. Screen 06 — Home Dashboard

The dashboard must not look like traditional accounting software.

### Header

```text
Namaste 👋

Aaj business mein kya karna hai?
```

### Primary CTA

```text
┌─────────────────────────────┐
│                             │
│          🎙️                 │
│                             │
│       PAISAFLOW SE          │
│          BOLIYE             │
│                             │
└─────────────────────────────┘
```

### Business snapshot

```text
┌────────────┐ ┌────────────┐
│ 💰 Cash    │ │ 📈 Sales   │
│ ₹82,400    │ │ ₹96,000    │
└────────────┘ └────────────┘

┌────────────┐ ┌────────────┐
│ 📦 Stock   │ │ 💳 Due     │
│ Attention  │ │ ₹18,500    │
└────────────┘ └────────────┘
```

### Top 3 Actions

```text
🎯 AAJ KE 3 KAAM

1. 3 pending payments follow-up
2. Feed stock review
3. Expansion scenario check
```

---

# 15. Screen 07 — Business Digital Twin

This is the product's visual centerpiece.

```text
                 BUSINESS
                    │
           ┌────────┴────────┐
           │  DIGITAL TWIN   │
           └────────┬────────┘
                    │
       ┌────────────┼────────────┐
       ↓            ↓            ↓
     MONEY        MARKET         OPS
       │            │            │
       └────────────┼────────────┘
                    ↓
                   RISK
```

### UI

```text
┌──────────────────────────────┐
│ 🧬 Aapka Business            │
│                              │
│        DIGITAL TWIN          │
│                              │
│   💰 Money     🏪 Market     │
│                              │
│   📦 Ops       ⚠️ Risk       │
│                              │
│ Last updated: Today          │
│                              │
│ [ Business Details ]         │
└──────────────────────────────┘
```

---

# 16. Screen 08 — Business Health

Avoid making a single opaque score the main output.

```text
BUSINESS HEALTH

🟢 Cash-flow
Stable

🟡 Payments
3 delayed

🟢 Inventory
Healthy

🟡 Expansion
Needs review

🔴 Repayment
Stress under downside scenario
```

Then explain:

> **“Sabse important issue: pending payments ki wajah se cash buffer kam ho raha hai.”**

---

# 17. Screen 09 — What-If Simulator

This is a hero feature.

```text
┌──────────────────────────────┐
│ 🔮 WHAT-IF SIMULATOR         │
│                              │
│ Agar main ₹9 lakh loan loon? │
│                              │
│ Loan Amount                  │
│ ─────────●────────           │
│ ₹7L             ₹10L         │
│                              │
│ Sales                        │
│ ───────●──────────           │
│                              │
│ Costs                        │
│ ─────────●────────           │
│                              │
│       [ SIMULATE ]           │
└──────────────────────────────┘
```

---

# 18. Screen 10 — Scenario Results

```text
┌──────────────────────────────┐
│ 🔮 Scenario Result           │
│                              │
│ NORMAL                       │
│ 🟢 Cash-flow manageable      │
│                              │
│ SALES ↓ 20%                  │
│ 🟡 Repayment pressure ↑      │
│                              │
│ COST ↑ 15%                   │
│ 🔴 Cash buffer tight         │
│                              │
│ Seasonal Shock               │
│ 🔴 Higher stress             │
│                              │
│ [ Why? ] [ Compare ]         │
└──────────────────────────────┘
```

---

# 19. Screen 11 — Simple Explanation

```text
PaisaFlow:

"Loan possible scenario hai,
lekin downside situation mein
cash-flow pressure badh sakta hai."

WHY?

• Sales 20% lower
• Costs 15% higher
• Monthly repayment continues

ASSUMPTIONS

Sales: ₹96,000
Costs: ₹58,000
Loan: ₹9,00,000

[ Details Dekhein ]
```

---

# 20. Screen 12 — Evidence Mode

```text
🔎 YE RESULT KAHAN SE AAYA?

BUSINESS DATA
✓ User-provided

CALCULATION
✓ PaisaFlow Finance Engine

MARKET DATA
✓ Source available
Updated: DD/MM/YYYY

ESTIMATE
~ Based on stated assumptions

AI EXPLANATION
Generated from above inputs
```

Important information should never appear more certain than its evidence supports.

---

# 21. Screen 13 — Top 3 Actions

```text
┌──────────────────────────────┐
│ 🎯 AAJ KE TOP 3 ACTIONS      │
│                              │
│ 01                           │
│ 💰 Cash buffer improve       │
│                              │
│ 02                           │
│ 📞 Pending payments followup │
│                              │
│ 03                           │
│ 🔮 Compare ₹7L loan scenario │
│                              │
│ [ Ek-ek karke samjhao ]      │
└──────────────────────────────┘
```

---

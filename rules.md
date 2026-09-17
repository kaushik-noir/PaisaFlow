# PaisaFlow — Product, Engineering & AI Rules
 
**Product:** PaisaFlow  
**Rules Version:** 1.0  
**Status:** Master Project Rules

> **Core Principle: Simple outside. Sophisticated inside.**

This document defines the non-negotiable rules that should govern the design, development, AI behavior, data handling, UX, simulations, and SIH demonstration of PaisaFlow.

---

# 1. Product Identity

PaisaFlow is a:

> **Voice-first AI Business Companion for small and rural/semi-urban entrepreneurs.**

It should help an entrepreneur:

```text
TALK
 ↓
UNDERSTAND
 ↓
MODEL
 ↓
PREDICT
 ↓
SIMULATE
 ↓
EXPLAIN
 ↓
ACT
 ↓
LEARN
```

PaisaFlow is **not** primarily:

- An accounting dashboard
- A generic chatbot
- A loan-approval system
- A stock-trading application
- A traditional ERP
- A replacement for a bank
- A guaranteed financial advisor

---

# 2. Golden Rule

## The user should never need to understand the complexity behind PaisaFlow.

The user should be able to say:

> “Mere paas ₹1 lakh hai, main dairy expand karna chahta hoon.”

PaisaFlow should handle the underlying:

```text
Intent
+
Business Context
+
Memory
+
Digital Twin
+
Finance
+
Simulation
+
Evidence
+
Risk
+
Explanation
```

---

# 3. UX Rules

## Rule 3.1 — Voice First

The primary interaction should be voice.

Primary CTA:

> **🎙️ BOLIYE**

Typing is a fallback.

---

## Rule 3.2 — One Question at a Time

Never overwhelm the user with a long form.

### Wrong

```text
Business Name
Revenue
Expenses
Inventory
Customers
Loan
Interest
Tenure
Location
GST
...
```

### Correct

```text
"Business kis type ka hai?"

↓

"Dairy."

↓

"Monthly sales approx kitni hai?"

↓

"₹80,000."
```

---

## Rule 3.3 — Use Simple Language

Prefer:

> “Har mahine loan ke liye kitna paisa dena padega?”

Instead of:

> “What is your monthly debt-servicing obligation?”

---

## Rule 3.4 — Explain Before Asking the User to Act

Important outputs should follow:

```text
ANSWER
 ↓
WHY
 ↓
ASSUMPTIONS
 ↓
 EVIDENCE
 ↓
ACTION
```

---

## Rule 3.5 — Action Over Analytics

Important analysis should end with practical actions.

Default format:

> **🎯 AAJ KE TOP 3 ACTIONS**


---

## Rule 3.6 — Do Not Create Information Overload

Avoid:

- Dense dashboards
- Too many metrics
- Unnecessary charts
- Long AI messages
- Financial jargon
- Excessive navigation

---
# 4. Low-Literacy Rules

PaisaFlow must be usable by people who may have limited digital or financial literacy.

## Rule 4.1

Every important text interaction should have a voice alternative.

## Rule 4.2

Use familiar words.

## Rule 4.3

Use the user's own business examples.

## Rule 4.4

Confirm important numbers.

## Rule 4.5

Allow:

> **“Mujhe samjhao.”**

## Rule 4.6

Support text when voice is unavailable.

## Rule 4.7

Never punish users for making mistakes.

Correction should be easy:

> **“Badalna hai”**

---

# 5. AI Rules

## Rule 5.1 — LLM Is an Orchestrator, Not the Calculator

The LLM can:

- Understand
- Extract
- Classify
- Ask questions
- Route tasks
- Explain
- Summarize

The LLM should not independently perform critical financial calculations when a deterministic engine can perform them.

---

## Rule 5.2 — Structured Inputs Before Calculation

```text
Natural Language
      ↓
LLM
      ↓
Structured Data
      ↓
Validation
      ↓
Business Engine
```

Example:

```text
"Mujhe ₹9 lakh loan chahiye."

↓

{
      "scenario": "loan",
  "amount": 900000
}
```

---

## Rule 5.3 — Never Trust Unvalidated AI Output

Before critical processing:

```text
AI Output
 ↓
Schema Validation
 ↓
Business Validation
 ↓
Calculation
```

---

## Rule 5.4 — No Hallucinated Facts

If data is unavailable:

> **Say that the data is unavailable.**

Never invent:

- Market statistics
- Government schemes
- Loan approval
- Interest rates
- Customer information
- Local demand
- Business performance
- External evidence

---

## Rule 5.5 — No Fake Precision

Avoid statements like:

> “Your business will earn exactly ₹47,832 next month.”

Prefer:

> “Based on the assumptions, projected sales are around ₹X.”

---

# 6. Financial Rules

## Rule 6.1 — Calculations Must Be Reproducible

Important financial calculations should be deterministic.

Examples:

```text
EMI
Cash-flow
Loan schedule
Revenue
Expenses
Cash buffer
Scenario impact
```

---

## Rule 6.2 — Separate Fact From Estimate

Every important result should distinguish:

```text
FACT
OBSERVATION
ESTIMATE
ASSUMPTION
AI INFERENCE
```

---

## Rule 6.3 — No Guaranteed Outcomes

Never present a simulation as:

- Guaranteed profit
- Guaranteed repayment
- Guaranteed loan approval
- Guaranteed business growth
- Guaranteed future performance

Use scenario language:

> “Is assumption ke under…”

---

## Rule 6.4 — Show Assumptions

A scenario should expose important assumptions.

Example:

```text
Loan: ₹9,00,000
Sales: ₹96,000/month
Costs: ₹58,000/month
Sales shock: -20%
Cost shock: +15%
```

---

# 7. What-If Simulator Rules

The What-If Simulator is a core PaisaFlow feature.

## Rule 7.1

Every simulation must identify the scenario.

Examples:

```text
Base Case
Sales -20%
Costs +15%
Payment Delay
Seasonal Shock
Loan Scenario
Expansion Scenario
```

---


## Rule 7.2

Every result should show:

```text
Scenario
 ↓
Inputs
 ↓
Assumptions
 ↓
Calculated Effects
 ↓
Risk
 ↓
Explanation
 ↓
Actions
```

---

## Rule 7.3

Users should be able to compare scenarios.

Example:

```text
₹7L Loan
vs
₹9L Loan
```

---

## Rule 7.4

Simulation output must not be confused with reality.

Use:

> “Projected”

> “Estimated”

> “Under this scenario”

where appropriate.

---

# 8. Business Digital Twin Rules

The Digital Twin is the central representation of the business.

It should reflect:

```text
Business Profile
Financial State
Transactions
Inventory
Customers
Suppliers
Goals
Risks
Market Evidence
Memory
```

---

## Rule 8.1 — The Twin Must Evolve

When verified business events change:

```text
Event
 ↓
Memory
 ↓
Twin Update
```

---

## Rule 8.2 — Do Not Silently Change Important Facts

Important facts should have provenance and, where appropriate, user confirmation.

---

## Rule 8.3 — Show Last Updated State

The UI should make data freshness understandable.

Example:

> **Updated today**

---

# 9. Memory Rules

Business Memory should preserve meaningful events.

Possible event types:

```text
FACT
SALE
PURCHASE
PAYMENT
CUSTOMER_EVENT
INVENTORY_EVENT
DECISION
ACTION
OUTCOME
CORRECTION
GOAL
ASSUMPTION
```

---

## Rule 9.1

Memory should not be treated as an unlimited dump of conversation text.

Store structured business events where possible.

---

## Rule 9.2

Important memory should retain:

```text
Source
Date
Business
Event
Confidence
Confirmation
```

---

## Rule 9.3

Corrections Must Be Possible

If PaisaFlow records:

> “Ramesh ko ₹850 diya.”

but the user says:

> “Nahi, ₹580 tha.”

the system should allow correction and preserve the relevant audit trail.

---

# 10. Evidence Rules

Evidence should make PaisaFlow trustworthy.

Every meaningful external claim should ideally include:

```text
Source
Timestamp
Freshness
Claim
Evidence Type
Confidence
```

---

## Rule 10.1 — Evidence Types

Use:

```text
USER-PROVIDED
CALCULATED
EXTERNAL SOURCE
ESTIMATE
ASSUMPTION
AI INFERENCE
```

---

## Rule 10.2 — No Unsupported Local Intelligence

If the system does not have evidence for a local claim, do not present it as fact.

---

## Rule 10.3 — Freshness Matters

Market information should display an appropriate update/retrieval time.

---

# 11. Communication Rules

PaisaFlow may prepare communications.

Example:

```text
Customer Follow-up
Payment Reminder
Supplier Message
```

But:

> **PaisaFlow should not automatically perform consequential external actions without user approval.**

Required flow:

```text
DRAFT
 ↓
REVIEW
 ↓
APPROVE
 ↓
SEND / ACTION
```

---

# 12. User Control Rules

The user must remain in control of:

- Business facts
- Corrections
- Scenario assumptions
- Important recommendations
- External communications
- Consequential actions

The system should never make the user feel that an irreversible action happened without their knowledge.

---

# 13. Safety Rules

## Rule 13.1

Do not fabricate financial facts.

## Rule 13.2

Do not present estimates as certainty.

## Rule 13.3

Do not hide assumptions.

## Rule 13.4

Do not hide important uncertainty.

## Rule 13.5

Do not expose private business data to unauthorized users.

## Rule 13.6

Do not execute consequential external actions without approval.

---

# 14. Privacy Rules

Collect only information necessary for the product's declared functions.

Protect:

```text
Business Information
Financial Information
Customer Information
Supplier Information
Voice Data
Authentication Data
Location Data
```

Do not expose private information through:

- Logs
- Debug screens
- Public APIs
- Frontend source
- Error messages

---

# 15. Security Rules

Mandatory principles:

```text
HTTPS
Secure Authentication
Authorization
Input Validation
Rate Limiting
Secret Management
Least Privilege
Audit Logging
```

Never commit:

```text
API keys
Passwords
Database credentials
Private tokens
Production secrets
```

to Git.

---

# 16. API Rules

All APIs should use versioning.

Recommended:

```text
/api/v1/
```
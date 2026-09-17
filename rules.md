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

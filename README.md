# kotoba-occupation

Occupation registry for kotoba-lang and itonami open businesses.

This repository maps ISCO-08-coded sole-proprietor occupations to the
technology capabilities they need in order to operate. `cloud-itonami-isco-*`
uses this to move from a published occupation blueprint to an executable
operating stack — the occupation-classification counterpart to
`kotoba-industry` (ISIC-coded businesses).

## Contract

```clojure
(require '[kotoba.occupation :as occupation])

(occupation/get-occupation "2221")
(occupation/required-technologies "6112")
(occupation/readiness "9312" #{:forms :telemetry :audit-ledger :bpmn :dmn})
```

## Layers

- business: customer-facing open occupation blueprint
- occupation: ISCO-08-coded operating domain (what the worker does)
- technology: reusable kotoba-lang capability stack
- implementation: concrete repos, services and operators

ISIC classifies what a *business* produces; ISCO classifies what a *worker*
does. `cloud-itonami-{ISIC}` publishes industry-vertical business blueprints;
`cloud-itonami-isco-{code}` publishes sole-proprietor occupation blueprints
(1 occupation = 1 independent operator). The `isco-` infix in the repo name is
required — ISIC and ISCO-08 unit-group codes overlap numerically, so a bare
`cloud-itonami-{code}` would be ambiguous between the two registries.

## Current ISCO-08 Blueprints

| ISCO-08 | Occupation | Blueprint | Required technology |
|---:|---|---|---|
| 1321 | Manufacturing Managers | Independent Manufacturing Floor Management (**:implemented** — real actor: `manufacturing-floor.store`/`.governor`, 7 tests) | robotics, identity, dmn, bpmn, audit-ledger, telemetry |
| 2221 | Nursing Professionals | Independent Home Nursing Practice (**:implemented** — real actor: `home-nursing.store`/`.governor`, 8 tests) | robotics, identity, forms, dmn, bpmn, audit-ledger, telemetry |
| 3253 | Community Health Workers | Community Health Outreach Practice (**:implemented** — real actor: `community-health.store`/`.governor`, 7 tests) | robotics, identity, forms, dmn, bpmn, audit-ledger |
| 4321 | Stock Clerks | Independent Warehouse Stock Operations (**:implemented** — real actor: `warehouse-stock.store`/`.governor`, 8 tests) | robotics, forms, telemetry, optimization, audit-ledger, bpmn |
| 5322 | Home-based Personal Care Workers | Independent Home-Based Care Practice (**:implemented** — real actor: `home-care.store`/`.governor`, 7 tests) | robotics, identity, forms, dmn, bpmn, audit-ledger, telemetry |
| 6112 | Market Gardeners and Crop Growers | Independent Market Gardening Operations (**:implemented** — real actor: `market-garden.store`/`.governor`, 7 tests) | robotics, telemetry, optimization, dmn, bpmn, audit-ledger, forms |
| 7126 | Plumbers and Pipe Fitters | Independent Plumbing Practice (**:implemented** — real actor: `plumbing.store`/`.governor`, 7 tests) | robotics, forms, telemetry, dmn, bpmn, audit-ledger |
| 8332 | Heavy Truck and Lorry Drivers | Independent Freight Driving Operations (**:implemented** — real actor: `freight-driving.store`/`.governor`, 9 tests) | robotics, telemetry, identity, dmn, bpmn, audit-ledger |
| 9312 | Civil Engineering Labourers | Independent Civil Labour Crew (**:implemented** — real actor: `civil-labour.store`/`.governor`, 7 tests) | robotics, forms, telemetry, dmn, bpmn, audit-ledger |
| 3141 | Life Science Technicians (excluding Medical) | Independent Field & Lab Science Support Practice | robotics, telemetry, forms, dmn, bpmn, audit-ledger |
| 5223 | Shop Sales Assistants | Independent Retail Floor Sales Practice | robotics, forms, telemetry, audit-ledger, bpmn |
| 6210 | Forestry and Related Workers | Independent Forestry Operations | robotics, telemetry, forms, dmn, bpmn, audit-ledger |
| 7231 | Motor Vehicle Mechanics and Repairers | Independent Auto Repair Practice | robotics, forms, telemetry, dmn, bpmn, audit-ledger |
| 8121 | Metal Processing Plant Operators | Independent Metal Processing Plant Operations | robotics, telemetry, dmn, bpmn, audit-ledger |
| 9111 | Domestic Cleaners and Helpers | Independent Domestic Cleaning Practice | robotics, identity, forms, audit-ledger |
| 1120 | Managing Directors and Chief Executives | Independent Small-Business Executive Practice | robotics, identity, dmn, bpmn, audit-ledger |
| 2512 | Software Developers | Independent Software Development Studio | robotics, identity, forms, dmn, bpmn, audit-ledger |
| 4110 | General Office Clerks | Independent Office Administration Practice | robotics, forms, audit-ledger, bpmn |
| 3213 | Pharmaceutical Technicians and Assistants | Independent Pharmacy Support Practice | robotics, identity, forms, dmn, bpmn, audit-ledger |
| 5153 | Building Caretakers | Independent Building Caretaking Practice | robotics, forms, telemetry, audit-ledger, bpmn |
| 7411 | Building and Related Electricians | Independent Electrical Practice | robotics, forms, telemetry, dmn, bpmn, audit-ledger |
| 2262 | Pharmacists | Independent Pharmacy Practice | robotics, identity, forms, dmn, bpmn, audit-ledger |
| 4222 | Contact Centre Information Clerks | Independent Reception & Contact Practice | robotics, forms, identity, audit-ledger, bpmn |
| 5311 | Child Care Workers | Independent Child Care Practice | robotics, identity, forms, dmn, bpmn, audit-ledger |
| 6130 | Mixed Crop and Animal Producers | Independent Mixed Farming Operations | robotics, telemetry, optimization, dmn, bpmn, audit-ledger, forms |
| 8160 | Food and Related Products Machine Operators | Independent Food Processing Operations | robotics, telemetry, dmn, bpmn, audit-ledger |
| 9412 | Kitchen Helpers | Independent Kitchen Support Practice | robotics, forms, audit-ledger |
| 1330 | Information and Communications Technology Services Managers | Independent IT Services Management Practice | robotics, identity, forms, dmn, bpmn, audit-ledger |
| 3512 | Information and Communications Technology User Support Technicians | Independent IT Support Practice | robotics, identity, forms, dmn, bpmn, audit-ledger |
| 6222 | Inland and Coastal Waters Fishery Workers | Independent Small-Scale Fishery Operations | robotics, telemetry, forms, dmn, bpmn, audit-ledger |
| 7115 | Carpenters and Joiners | Independent Carpentry Practice | robotics, forms, telemetry, dmn, bpmn, audit-ledger |
| 8342 | Earthmoving and Related Plant Operators | Independent Earthmoving Operations | robotics, telemetry, dmn, bpmn, audit-ledger |
| 9622 | Odd-job Persons | Independent Handyman Practice | robotics, forms, audit-ledger, bpmn |
| 1219 | Business Services and Administration Managers Not Elsewhere Classified | Independent Business Administration Management Practice | robotics, identity, forms, dmn, bpmn, audit-ledger |
| 2411 | Accountants | Independent Accounting Practice | robotics, identity, forms, dmn, bpmn, audit-ledger |
| 4413 | Coding, Proofreading and Related Clerks | Independent Document Processing Practice | robotics, forms, audit-ledger |
| 3123 | Construction Supervisors | Independent Construction Supervision Practice | robotics, forms, telemetry, dmn, bpmn, audit-ledger |
| 7212 | Welders and Flame Cutters | Independent Welding Practice | robotics, forms, telemetry, dmn, bpmn, audit-ledger |
| 9333 | Freight Handlers | Independent Freight Handling Practice | robotics, forms, telemetry, audit-ledger, bpmn |
| 2634 | Psychologists | Independent Psychology Practice | robotics, identity, forms, dmn, bpmn, audit-ledger |
| 5120 | Cooks | Independent Culinary Practice | robotics, forms, telemetry, audit-ledger, bpmn |
| 6114 | Mixed Crop Growers | Independent Mixed Crop Growing Operations | robotics, telemetry, optimization, dmn, bpmn, audit-ledger, forms |

4-6 representative unit groups per non-armed-forces ISCO-08 major group
(42/42 across major groups 1-9; major group 0 "Armed Forces Occupations"
is registry-only — a sole-proprietor OSS business blueprint doesn't fit an
armed-forces occupation, mirroring how `kotoba-industry` also leaves some
sections registry-only). 6112, 2221, 7126, 4321, 9312, 5322, 8332, 1321
and 3253 are `:maturity :implemented` (real reference actors exist); the
other 33 are `:maturity :blueprint`. The remaining 394 ISCO-08 unit groups
are registered at `:maturity :spec` (registry-only stub, full ISCO-08
coverage) for future promotion.

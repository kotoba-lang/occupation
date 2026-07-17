(ns kotoba.occupation-test
  (:require [clojure.test :refer [deftest is testing]]
            [kotoba.occupation :as occupation]))

(deftest registry-loads
  (let [reg (occupation/registry)]
    (is (= :kotoba/occupation (:kotoba.registry/id reg)))
    (is (= 436 (count (occupation/occupations reg))))))

(deftest curated-occupations-resolve
  (doseq [isco ["1111" "1311" "1312" "1321" "2221" "3253" "4321" "5322" "6112" "7126" "8332" "9312"
                "3141" "5223" "6210" "7231" "8121" "9111"
                "1120" "2512" "4110"
                "3213" "5153" "7411"
                "2262" "4222" "5311"
                "6130" "8160" "9412"
                "1330" "3512" "6222"
                "7115" "8342" "9622"
                "1219" "2411" "4413"
                "3123" "7212" "9333"
                "2634" "5120" "6114"
                "1349" "4131" "8189"
                "3339" "5164" "8172"
                "2320" "4211" "5169"
                "6221" "7233" "9211"
                "2166" "4229" "8154"
                "1412" "3255" "5162"
                "2422" "4415" "5230"
                "3122" "6111" "8322"
                "2144" "4322" "5249"
                "1341" "1346" "6121" "7318"
                "1439" "4132" "8153"
                "2621" "4224" "7523"
                "1223" "5312" "6122"
                "2431" "4120" "6123"]]
    (is (:business-id (occupation/get-occupation isco)))
    (is (seq (occupation/required-technologies isco)))
    (is (seq (:technology-stack (occupation/execution-plan isco))))))

(deftest readiness-reports-missing-tech
  (let [r (occupation/readiness "2221" #{:identity :forms})]
    (is (false? (:ready? r)))
    (is (contains? (:missing r) :audit-ledger)))
  ;; :robotics is required on every entry (robotics-premise design, mirrors
  ;; kotoba-lang/industry), so it must be in the available set to read ready.
  (is (:ready? (occupation/readiness "9312" #{:robotics :forms :telemetry :dmn :bpmn :audit-ledger}))))

(deftest maturity-tier
  (testing "wave-2 batch #2's final 5 entries -- 3331 (Independent
            Customs Clearing & Freight Forwarding Practice), 5419
            (Independent Protective Services Practice), 8343
            (Independent Crane & Hoist Operations Practice), 9321
            (Independent Packing & Fulfillment Practice), 9329
            (Independent Manufacturing Support Labour Practice) --
            all cleared to :implemented; blueprint tier is empty again"
    (is (= :implemented (occupation/maturity "3331")))
    (is (= :implemented (occupation/maturity "5419")))
    (is (= :implemented (occupation/maturity "8343")))
    (is (= :implemented (occupation/maturity "9321")))
    (is (= :implemented (occupation/maturity "9329"))))
  (testing "the reference actors are :implemented"
    (is (= :implemented (occupation/maturity "3313")))
    (is (= :implemented (occupation/maturity "3314")))
    (is (= :implemented (occupation/maturity "3315")))
    (is (= :implemented (occupation/maturity "3322")))
    (is (= :implemented (occupation/maturity "3324")))
    (is (= :implemented (occupation/maturity "4414")))
    (is (= :implemented (occupation/maturity "3311")))
    (is (= :implemented (occupation/maturity "3323")))
    (is (= :implemented (occupation/maturity "3321")))
    (is (= :implemented (occupation/maturity "5414")))
    (is (= :implemented (occupation/maturity "8331")))
    (is (= :implemented (occupation/maturity "9313")))
    (is (= :implemented (occupation/maturity "3334")))
    (is (= :implemented (occupation/maturity "9334")))
    (is (= :implemented (occupation/maturity "3332")))
    (is (= :implemented (occupation/maturity "3312")))
    (is (= :implemented (occupation/maturity "2412")))
    (is (= :implemented (occupation/maturity "2611")))
    (is (= :implemented (occupation/maturity "2612")))
    (is (= :implemented (occupation/maturity "2619")))
    (is (= :implemented (occupation/maturity "4212")))
    (is (= :implemented (occupation/maturity "4213")))
    (is (= :implemented (occupation/maturity "4214")))
    (is (= :implemented (occupation/maturity "6112")))
    (is (= :implemented (occupation/maturity "2221")))
    (is (= :implemented (occupation/maturity "7126")))
    (is (= :implemented (occupation/maturity "4321")))
    (is (= :implemented (occupation/maturity "9312")))
    (is (= :implemented (occupation/maturity "5322")))
    (is (= :implemented (occupation/maturity "8332")))
    (is (= :implemented (occupation/maturity "1321")))
    (is (= :implemented (occupation/maturity "3253")))
    (is (= :implemented (occupation/maturity "6210")))
    (is (= :implemented (occupation/maturity "5223")))
    (is (= :implemented (occupation/maturity "7231")))
    (is (= :implemented (occupation/maturity "8121")))
    (is (= :implemented (occupation/maturity "9111")))
    (is (= :implemented (occupation/maturity "2512")))
    (is (= :implemented (occupation/maturity "1120")))
    (is (= :implemented (occupation/maturity "4110")))
    (is (= :implemented (occupation/maturity "3213")))
    (is (= :implemented (occupation/maturity "5153")))
    (is (= :implemented (occupation/maturity "7411")))
    (is (= :implemented (occupation/maturity "2262")))
    (is (= :implemented (occupation/maturity "4222")))
    (is (= :implemented (occupation/maturity "5311")))
    (is (= :implemented (occupation/maturity "6130")))
    (is (= :implemented (occupation/maturity "8160")))
    (is (= :implemented (occupation/maturity "1212")))
    (is (= :implemented (occupation/maturity "4411")))
    (is (= :implemented (occupation/maturity "4412")))
    (is (= :implemented (occupation/maturity "1111")))
    (is (= :implemented (occupation/maturity "0110")))
    (is (= :implemented (occupation/maturity "0210")))
    (is (= :implemented (occupation/maturity "1311")))
    (is (= :implemented (occupation/maturity "1312"))))
  (testing "reference chemists (2113) actor is :implemented"
    (is (= :implemented (occupation/maturity "2113"))))
  (testing "reference biologists (2131) actor is :implemented"
    (is (= :implemented (occupation/maturity "2131"))))
  (testing "reference ships' engineers (3151) actor is :implemented"
    (is (= :implemented (occupation/maturity "3151"))))
  (testing "a registry-only unit group entry is :spec"
    (is (= :spec (occupation/maturity "1411"))))
  (testing "maturity-summary counts tiers"
    (let [m (occupation/maturity-summary)]
      (is (= (:total m) (+ (:spec m) (:blueprint m) (:implemented m))))
      (is (= 436 (:total m)))
      ;; 19 -> 23 with the ISCO wave-0 cognitive batch (ADR-2607122700):
      ;; 4311 bookkeeping clerks, 2513 web/multimedia developers, 3511 ICT
      ;; operations technicians, 2519 software QA/analysts NEC — blueprint
      ;; satellites published, no actors claimed; wave 0 = LLM-first, no
      ;; robotics gate (kotoba.occupation.wave/wave-of = 0 for all four).
      ;; 23 -> 27 with batch #2 (ADR-2607122700 addendum): 2514
      ;; applications programmers, 2521 database designers/admins, 2522
      ;; systems administrators, 3513 network/systems technicians — the
      ;; ICT-operations cluster, same wave-0 discipline.
      ;; 27 -> 31 with batch #3 (ADR-2607122700 addenda): 3514 web
      ;; technicians, 4226 receptionists, 4225 inquiry clerks, 4227
      ;; survey/market-research interviewers — the front-office cognitive
      ;; cluster, same wave-0 discipline.
      ;; 31 -> 35 with batch #4 (ADR-2607122700 addenda): 2511 systems
      ;; analysts, 2523 network professionals, 2432 public relations,
      ;; 2434 ICT sales — completes 251x/252x ICT-professional coverage
      ;; at >= :blueprint plus two outbound-heavy business professions
      ;; (external-send always human-gated).
      ;; 35 -> 39 with batch #5 (ADR-2607122700 addenda): 2421 management
      ;; analysts, 2413 financial analysts (analysis only — regulated
      ;; investment advice out of scope), 4312 statistical/finance/
      ;; insurance clerks, 4313 payroll clerks — analysis + back-office
      ;; finance cluster.
      ;; 39 -> 44 with batch #6 (ADR-2607122700 addenda): 2433 technical/
      ;; medical sales, 4419 clerical NEC, 4223 switchboard, 4416
      ;; personnel clerks (new satellites) + 3521 broadcasting/AV
      ;; technicians — a REGISTRY-SYNC: its blueprint repo already
      ;; existed (kawaraban, ADR-2607110200) but the registry still said
      ;; :spec; the entry now points at the real repo.
      ;; 44 -> 50 with batch #7 (ADR-2607122700 addenda): 2423 personnel/
      ;; careers (feeds the 7810 lane), 2424 training, 2529 DB/network
      ;; NEC, 3522 telecom technicians (cognitive scope only — physical
      ;; plant work stays out), 4221 travel clerks, 4323 transport
      ;; clerks. Wave-0 :spec remainder is now 10: judges 2612
      ;; (deliberately never agentized), the sensitive/regulated cluster
      ;; 2611/2619/2412/4212/4213/4214 (deferred pending owner
      ;; judgment), and the small tail 4411/4412/4414 (4412 mail
      ;; carrying has a physical component).
      ;; 50 -> 49 / 70 -> 71: 4311 bookkeeping clerks promoted
      ;; :blueprint -> :implemented — BookkeepingActor (Advisor ⊣
      ;; BookkeepingClerksGovernor, langgraph StateGraph, modeled on
      ;; isco-2411) with two bookkeeping HARD invariants: source-
      ;; document basis (no invented transactions) and double-entry
      ;; balance. 14 tests / 36 assertions green — the loop's first
      ;; vertical (:blueprint -> :implemented) promotion.
      ;; 49 -> 48 / 71 -> 72: 4313 payroll clerks promoted to
      ;; :implemented — PayrollActor consuming kotoba-lang/labor
      ;; (capability-library-wrapping convention, isic-9700 precedent);
      ;; the governor RECOMPUTES wages via kotoba.labor/wages-for and
      ;; HARD-holds any gross/net mismatch (fair pay is arithmetic, not
      ;; opinion). 14 tests / 36 assertions green.
      ;; 48 -> 47 / 72 -> 73: 4226 receptionists promoted to
      ;; :implemented — ReceptionActor with the no-double-booking HARD
      ;; invariant (the governor checks the committed calendar
      ;; deterministically; the advisor's free-slot claim is never
      ;; trusted). 12 tests / 25 assertions green.
      ;; 47 -> 46 / 73 -> 74: 4225 inquiry clerks promoted to
      ;; :implemented — InquiryDeskActor with KB-citation-basis and
      ;; deterministic freshness (stale knowledge unservable) HARD
      ;; invariants. 12 tests / 26 assertions green.
      ;; 46 -> 45 / 74 -> 75: 2424 training professionals promoted to
      ;; :implemented — TrainingActor with deterministic curriculum HARD
      ;; invariants (registered-module basis, prerequisite DAG order,
      ;; hours integrity). 14 tests / 31 assertions green.
      ;; 45 -> 44 / 75 -> 76: 2423 personnel/careers promoted to
      ;; :implemented — CareersActor (feeds the 7810 labour-exchange
      ;; lane) with consent HARD (a person's missing consent is not
      ;; approvable) and deterministic skills-set-inclusion invariants.
      ;; 13 tests / 28 assertions green.
      ;; 44 -> 43 / 76 -> 77: 2521 database designers/administrators
      ;; promoted to :implemented — DatabaseAdministrationActor with the
      ;; backup-before-apply schema-version-equality HARD invariant and
      ;; step-scan destructive-op escalation. 13 tests / 28 assertions
      ;; green.
      ;; 43 -> 42 / 77 -> 78: 2522 systems administrators promoted to
      ;; :implemented — SystemsAdministrationActor with window-
      ;; containment and freeze-non-overlap interval-arithmetic HARD
      ;; invariants. 14 tests / 30 assertions green.
      ;; 42 -> 41 / 78 -> 79: 3513 network/systems technicians promoted
      ;; to :implemented — NetworkSystemsActor with the connectivity-
      ;; preservation HARD invariant (BFS reachability recomputed over
      ;; the registered topology; partition-inducing changes held).
      ;; Completes the ICT-operations cluster (2521/2522/3513).
      ;; 12 tests / 25 assertions green.
      ;; 41 -> 40 / 79 -> 80: 2513 web/multimedia developers promoted
      ;; to :implemented — WebMultimediaActor with license-provenance
      ;; (spec-basis, creative edition) and internal-link-integrity HARD
      ;; invariants. 12 tests / 26 assertions green — the loop's tenth
      ;; vertical promotion.
      ;; 40 -> 39 / 80 -> 81: 2514 applications programmers promoted to
      ;; :implemented — ApplicationsProgrammingActor with requirement-
      ;; basis and commit-scoped test-evidence HARD invariants (a green
      ;; run for another commit proves nothing — evidence freshness is
      ;; identity, not recency). 14 tests / 29 assertions green.
      ;; 39 -> 43 / spec 316 -> 312: the FIRST wave-1 blueprint batch
      ;; (ADR-2607122700 addenda) — functional managers 1211 finance,
      ;; 1213 policy/planning, 1221 sales/marketing, 1222 advertising/PR.
      ;; Management work is cognitive (no robotics gate), sequenced
      ;; after the wave-0 substrate in rollout priority; blueprint
      ;; inventory replenishment for the vertical (actor) line.
      ;; 43 -> 42 / 81 -> 82: 1211 finance managers promoted to
      ;; :implemented — FinanceManagementActor, the FIRST wave-1
      ;; implemented actor. Budget ceiling is a ledger sum (remaining
      ;; recomputed from registered spends; overruns held at any
      ;; confidence; the commit node registers spends so the ceiling
      ;; tightens with each approval). 14 tests / 36 assertions green.
      ;; 42 -> 41 / 82 -> 83: 1221 sales/marketing managers promoted to
      ;; :implemented — SalesMarketingManagementActor with discount-
      ;; ceiling and price-floor HARD invariants (registered number
      ;; tables; the authority table is not a negotiating position).
      ;; 13 tests / 27 assertions green.
      ;; 41 -> 40 / 83 -> 84: 1213 policy & planning managers promoted
      ;; to :implemented — PolicyPlanningManagementActor with mandate-
      ;; set containment and control-point conflict HARD invariants
      ;; (jurisdiction is a registered set; a contradiction is detected
      ;; by comparison, not judgement). 13 tests / 27 assertions green.
      ;; 40 -> 39 / 84 -> 85: 1222 advertising & PR managers promoted
      ;; to :implemented — AdvertisingPRManagementActor with claim-
      ;; substantiation subset and prohibition-intersection HARD
      ;; invariants (advertising is citation of registered evidence;
      ;; the blacklist is arithmetic, not tone). 13 tests / 27
      ;; assertions green.
      ;; tick-34 batch: 2511 systems analysts promoted to :implemented
      ;; — SystemsAnalysisActor with requirement-coverage TOTALITY HARD
      ;; invariant (every registered requirement maps to a non-empty
      ;; registered-component set; 14 tests / 29 assertions green).
      ;; Plus wave-1 batch 2: 2141/2142/2151/2152/2153 engineering
      ;; design professions scaffolded as public blueprint satellites
      ;; (spec -> blueprint). 39 - 1 + 5 = 43 / 312 - 5 = 307 /
      ;; 85 + 1 = 86.
      ;; tick-35 batch: 2523 network professionals + 2519 software dev
      ;; (NEC) promoted to :implemented. 2523: shadow-rule containment
      ;; by set superset check (dead config is set containment, not
      ;; opinion; 14/29 green). 2519: api-surface semver arithmetic —
      ;; removed symbols require MAJOR, additions require MINOR+
      ;; (15/34 green). 43 - 2 = 41 / 86 + 2 = 88.
      ;; 2529 database & network professionals (NEC) promoted to
      ;; :implemented — completes the wave-0 ICT cluster (2511/2523/
      ;; 2529). 3NF transitive-dependency HARD invariant: a non-
      ;; trivial FD is admissible only if its determinant is a
      ;; superkey or every dependent is a prime attribute of some
      ;; candidate key (set membership, not design taste). 12 tests /
      ;; 24 assertions green. 41 -> 40 / 88 -> 89.
      ;; 4312 statistical/finance/insurance clerks promoted to
      ;; :implemented — StatFinanceInsuranceClerksActor with
      ;; aggregation-identity HARD invariant (line-item sum must equal
      ;; header total exactly; ground truth is the line items, not the
      ;; header). 13 tests / 28 assertions green. 40 -> 39 / 89 -> 90.
      ;; 2141 industrial & production engineers promoted to
      ;; :implemented (fifth wave-1 actor) — tolerance-containment
      ;; (measurement within registered [LSL,USL]) + stack-up-limit
      ;; (cumulative tolerance <= registered allowable stack) HARD
      ;; invariants, both arithmetic not judgement. 15 tests / 31
      ;; assertions green. 39 -> 38 / 90 -> 91.
      ;; 2142 civil engineers promoted to :implemented (sixth
      ;; wave-1 actor) — load-capacity margin (utilization =
      ;; load/capacity <= 1.0) + material-grade membership (approved-
      ;; grades set) HARD invariants. 13 tests / 27 assertions green.
      ;; 38 -> 37 / 91 -> 92.
      ;; 2151 electrical engineers promoted to :implemented
      ;; (seventh wave-1 actor) — ampacity-margin (load <= registered
      ;; rating) + voltage-class-match (equality, no substitution)
      ;; HARD invariants. 13 tests / 27 assertions green.
      ;; 37 -> 36 / 92 -> 93.
      ;; 2152 electronics engineers promoted to :implemented
      ;; (eighth wave-1 actor) — power-budget arithmetic (BOM sum
      ;; <= registered budget) + approved-vendor membership HARD
      ;; invariants. 13 tests / 27 assertions green.
      ;; 36 -> 35 / 93 -> 94.
      ;; 2153 telecommunications engineers promoted to :implemented
      ;; (ninth wave-1 actor) — completes wave-1 batch 2, all five
      ;; 21xx engineering design professions now :implemented.
      ;; Link-margin floor + spectrum-containment HARD invariants.
      ;; 14 tests / 30 assertions green. 35 -> 34 / 94 -> 95.
      ;; tick-44 batch: wave-1 batch 3 blueprints published — 1323
      ;; construction / 1324 supply-distribution / 1342 health
      ;; services / 1345 education managers + 2120 mathematicians/
      ;; actuaries/statisticians. spec -> blueprint. 34 + 5 = 39 /
      ;; 307 - 5 = 302.
      ;; 1323 construction managers promoted to :implemented
      ;; (first wave-1 batch-3 actor) — permit-window (interval
      ;; containment) + inspection-completeness (superset, no partial
      ;; credit) HARD invariants. 15 tests / 31 assertions green.
      ;; 39 -> 38 / 95 -> 96.
      ;; 1324 supply/distribution managers promoted to :implemented
      ;; (second wave-1 batch-3 actor) — stock-arithmetic (allocation
      ;; <= on-hand) + carrier-membership HARD invariants. 13 tests /
      ;; 27 assertions green. 38 -> 37 / 96 -> 97.
      ;; 1342 health services managers promoted to :implemented
      ;; (third wave-1 batch-3 actor) — staffing-ratio ceiling
      ;; (patient-safety arithmetic) + license-window (interval
      ;; containment) HARD invariants. 14 tests / 30 assertions
      ;; green. 37 -> 36 / 97 -> 98.
      ;; 1345 education managers promoted to :implemented (fourth
      ;; wave-1 batch-3 actor, completes batch 3) — credit-hour
      ;; arithmetic (completed >= required) + accreditation-window
      ;; (interval containment) HARD invariants. 14 tests / 30
      ;; assertions green. 36 -> 35 / 98 -> 99.
      ;; 2120 mathematicians/actuaries/statisticians promoted to
      ;; :implemented, completes wave-1 batch 3 in full — significance
      ;; arithmetic (p-value <= registered alpha) + method-membership
      ;; (approved-methods set) HARD invariants. 14 tests / 28
      ;; assertions green. 35 -> 34 / 99 -> 100.
      ;; 2413 financial analysts promoted to :implemented — rating-
      ;; validity (registered scale membership) + conflict-disclosure
      ;; (superset of known-conflicts) HARD invariants. 13 tests / 27
      ;; assertions green. 34 -> 33 / 100 -> 101.
      ;; 2421 management/organization analysts promoted to
      ;; :implemented — metric-citation membership (no fabricated
      ;; evidence) + savings-claim ceiling (arithmetic, not marketing)
      ;; HARD invariants. 13 tests / 27 assertions green.
      ;; 33 -> 32 / 101 -> 102.
      ;; 2432 public relations professionals promoted to
      ;; :implemented — embargo-floor (as-of >= registered lift day)
      ;; + spokesperson-membership (approved set) HARD invariants.
      ;; 13 tests / 27 assertions green. 32 -> 31 / 102 -> 103.
      ;; 2433 technical/medical sales promoted to :implemented —
      ;; indication-subset (off-label = subset violation) +
      ;; licensed-buyer-gate (conditional membership for restricted
      ;; products) HARD invariants. 13 tests / 27 assertions green.
      ;; 31 -> 30 / 103 -> 104.
      ;; 2434 ICT sales professionals promoted to :implemented,
      ;; completes the wave-0 professional-sales cluster
      ;; (2432/2433/2434) — seat-count ceiling + component-
      ;; compatibility HARD invariants. 13 tests / 27 assertions
      ;; green. 30 -> 29 / 104 -> 105.
      ;; 3511 ICT operations technicians promoted to :implemented —
      ;; SLA arithmetic (response-time <= registered ceiling) +
      ;; certification-coverage (superset of required certs) HARD
      ;; invariants. 13 tests / 27 assertions green. 29 -> 28 /
      ;; 105 -> 106.
      ;; 3514 web technicians promoted to :implemented —
      ;; conformance-floor (ordinal WCAG scale) + domain-membership
      ;; HARD invariants. 13 tests / 27 assertions green.
      ;; 28 -> 27 / 106 -> 107.
      ;; 3522 telecom engineering technicians promoted to
      ;; :implemented — attenuation-ceiling (arithmetic) +
      ;; calibration-validity (expiry day interval) HARD invariants.
      ;; 13 tests / 28 assertions green. 27 -> 26 / 107 -> 108.
      ;; 4221 travel consultants & clerks promoted to :implemented —
      ;; inventory arithmetic (units <= available) + refund-cutoff
      ;; floor (days-before-departure >= cutoff) HARD invariants.
      ;; 14 tests / 28 assertions green. 26 -> 25 / 108 -> 109.
      ;; 4223 telephone switchboard operators promoted to
      ;; :implemented — extension-membership (no misrouted call) +
      ;; do-not-call exclusion (set membership, not operator
      ;; discretion) HARD invariants. 13 tests / 27 assertions
      ;; green. 25 -> 24 / 109 -> 110.
      ;; 4227 survey/market research interviewers promoted to
      ;; :implemented — segment-basis+quota-ceiling (registered
      ;; segment, count <= quota) + consent-gate (unconsented
      ;; recording is a violation) HARD invariants. 15 tests / 30
      ;; assertions green. 24 -> 23 / 110 -> 111.
      ;; 4323 transport clerks promoted to :implemented — payload-
      ;; ceiling (arithmetic, physics not optimism) + hazmat-class
      ;; subset HARD invariants. 13 tests / 27 assertions green.
      ;; 23 -> 22 / 111 -> 112.
      ;; 4416 personnel clerks promoted to :implemented — field-
      ;; completeness (superset of required fields) + background-
      ;; check gate HARD invariants. 14 tests / 28 assertions
      ;; green. 22 -> 21 / 112 -> 113.
      ;; 4419 clerical support workers (NEC) promoted to
      ;; :implemented, completes the wave-0 clerical cluster run
      ;; (4221/4223/4227/4323/4416/4419) — retention-floor (interval)
      ;; + clearance-ordinal (:public<:internal<:confidential) HARD
      ;; invariants. 15 tests / 29 assertions green. 21 -> 20 /
      ;; 113 -> 114.
      ;; 6122 poultry producers promoted to :implemented — first
      ;; robotics-premise vertical actually implemented (governor
      ;; gates the coop-monitoring robot's dispatched actions; never
      ;; dispatches hardware itself). Per-bird feed ceiling
      ;; (arithmetic) + always-escalate medication/outbreak HARD
      ;; invariants. 13 tests / 27 assertions green. 20 -> 19 /
      ;; 114 -> 115.
      ;; 6123 apiarists/sericulturists promoted to :implemented —
      ;; sustainable-yield ceiling (arithmetic) + always-escalate
      ;; hive-treatment/defensive-colony HARD invariants. 13 tests /
      ;; 27 assertions green. 19 -> 18 / 115 -> 116.
      ;; 6221 aquaculture workers promoted to :implemented — per-
      ;; fish feed ceiling (arithmetic) + dissolved-oxygen floor
      ;; (water chemistry) + always-escalate chemical/deep-water HARD
      ;; invariants. 14 tests / 29 assertions green. 18 -> 17 /
      ;; 116 -> 117.
      ;; 6222 inland/coastal fishery workers promoted to
      ;; :implemented, completes the robotics-premise agriculture/
      ;; aquaculture run (6122/6123/6221/6222) — quota ceiling
      ;; (arithmetic) + protected-zone exclusion (set) + always-
      ;; escalate vessel/bycatch HARD invariants. 14 tests / 29
      ;; assertions green. 17 -> 16 / 117 -> 118.
      ;; 7115 carpenters/joiners promoted to :implemented —
      ;; material-basis (registered stock key) + stock-ceiling
      ;; (arithmetic) + always-escalate powered-saw/height-work HARD
      ;; invariants. 14 tests / 29 assertions green. 16 -> 15 /
      ;; 118 -> 119.
      ;; 7212 welders/flame cutters promoted to :implemented —
      ;; defect-length + alignment-offset ceilings + always-escalate
      ;; arc-welding/flammable-proximity HARD invariants. Milestone:
      ;; 50th vertical shipped by this loop. 14 tests / 29 assertions
      ;; green. 15 -> 14 / 119 -> 120.
      ;; 7233 agricultural/industrial machinery mechanics promoted
      ;; to :implemented — approved-part membership + test-deviation
      ;; ceiling + always-escalate lift/hydraulic HARD invariants.
      ;; 14 tests / 29 assertions green. 14 -> 13 / 120 -> 121.
      ;; 7318 textile/leather handicraft workers promoted to
      ;; :implemented — material-basis/stock-ceiling + spec-
      ;; completeness (superset delivery) + always-escalate sharp-
      ;; equipment/chemical HARD invariants. 17 tests / 33 assertions
      ;; green. 13 -> 12 / 121 -> 122.
      ;; 7523 woodworking machine tool setters/operators promoted
      ;; to :implemented — dimensional-tolerance (interval) +
      ;; production-ceiling (arithmetic) + always-escalate blade/
      ;; quality HARD invariants. 14 tests / 30 assertions green.
      ;; 12 -> 11 / 122 -> 123.
      ;; 8153 sewing machine operators promoted to :implemented —
      ;; stitch-density band (interval) + seam-deviation ceiling
      ;; (arithmetic) + always-escalate needle/quality HARD
      ;; invariants. 14 tests / 30 assertions green. 11 -> 10 /
      ;; 123 -> 124.
      ;; 8154 bleaching/dyeing/fabric cleaning machine operators
      ;; promoted to :implemented — chemical-concentration ceiling
      ;; (arithmetic) + process-temperature envelope (interval) +
      ;; always-escalate concentrated-chemical/pressurized HARD
      ;; invariants. 14 tests / 30 assertions green. 10 -> 9 /
      ;; 124 -> 125.
      ;; 8172 wood processing plant operators promoted to
      ;; :implemented — dust-level ceiling + throughput ceiling +
      ;; always-escalate blade-proximity/blade-change HARD
      ;; invariants. 14 tests / 29 assertions green. 9 -> 8 /
      ;; 125 -> 126.
      ;; 8189 stationary plant/machine operators (NEC) promoted
      ;; to :implemented — pressure envelope (interval) +
      ;; maintenance-due ceiling (arithmetic) + always-escalate
      ;; pressurized-proximity/startup-shutdown HARD invariants. 14
      ;; tests / 30 assertions green. 8 -> 7 / 126 -> 127.
      ;; 8322 car/taxi/van drivers promoted to :implemented —
      ;; duty-hours ceiling (cumulative arithmetic) + inspection
      ;; validity (window) + always-escalate duty-hours-exception/
      ;; high-risk HARD invariants. 14 tests / 30 assertions green.
      ;; 7 -> 6 / 127 -> 128.
      ;; 8342 earthmoving/related plant operators promoted to
      ;; :implemented — cleared-depth ceiling (arithmetic) + cleared-
      ;; zone membership (set) + always-escalate unlocated-utility/
      ;; occupied-zone HARD invariants. 14 tests / 29 assertions
      ;; green. 6 -> 5 / 128 -> 129.
      ;; 9211 crop farm labourers promoted to :implemented,
      ;; 60th loop vertical milestone — carry-weight ceiling +
      ;; rest-interval ceiling + always-escalate heavy-equipment-
      ;; proximity/water-treatment HARD invariants. 14 tests / 29
      ;; assertions green. 5 -> 4 / 129 -> 130.
      ;; 9333 freight handlers promoted to :implemented — weight-
      ;; reconciliation (band) + dock-assignment membership + always-
      ;; escalate dock-proximity/overweight HARD invariants. 14
      ;; tests / 30 assertions green. 4 -> 3 / 130 -> 131.
      ;; 9412 kitchen helpers promoted to :implemented — sanitize-
      ;; temperature band (food-safety spec, not a feel test) +
      ;; restock-quantity ceiling (storage risk, not thrift) + always-
      ;; escalate hot-surface-proximity/sharp-tool-zone-entry HARD
      ;; invariants. 14 tests / 30 assertions green. 3 -> 2 / 131 -> 132.
      ;; 9622 odd-job persons (handyman) promoted to :implemented —
      ;; handling-weight ceiling (strain/injury risk, not effort) +
      ;; task-duration time-box ceiling (site access is time-boxed, not
      ;; open-ended) + always-escalate electrical-plumbing-access/
      ;; working-at-height HARD invariants. 14 tests / 29 assertions
      ;; green. 2 -> 1 / 132 -> 133. Only 3521 (bespoke kawaraban
      ;; design, deliberately deferred) remains :blueprint.
      ;; 3521 broadcasting/AV technicians (media syndication downstream
      ;; of kawaraban) promoted to :implemented — the bespoke design
      ;; deferred since Addendum 46 finally implemented faithfully:
      ;; excerpt-length ceiling (fair-use bound, not full-text
      ;; reproduction) + attribution/link-out exact-match (misattribution
      ;; is not syndication) + always-escalate publish-derivative-
      ;; product/live-on-air-switching HARD invariants. 15 tests / 31
      ;; assertions green. 1 -> 0 / 133 -> 134. Blueprint tier is now
      ;; fully cleared (zero :blueprint entries remain).
      ;; tick 85: wave-0 cognitive batch #8 — 10 spec entries promoted
      ;; to :blueprint (scaffold-only, no src/test yet): 2412/2611/2612/
      ;; 2619 (finance/legal cognitive root) + 4212/4213/4214 (finance-
      ;; adjacent trust services) + 4411/4412/4414 (clerical/services).
      ;; This clears wave 0 (cognitive-substrate-root)'s :spec pool to
      ;; zero (55/55 now blueprint-or-implemented). 0 -> 10 / 302 -> 292.
      ;; 2412 financial and investment advisers promoted to :implemented
      ;; — allocation-percentage suitability ceiling (unsuitable advice,
      ;; not aggressive strategy) + risk-disclosure-attached presence
      ;; check (undisclosed advice is not efficient service) + always-
      ;; escalate trade-execution/fund-transfer HARD invariants. 14
      ;; tests / 29 assertions green. 10 -> 9 / 134 -> 135.
      ;; 2611 lawyers promoted to :implemented — billable-hours
      ;; engagement-scope ceiling (scope creep, not diligence) +
      ;; conflict-check-cleared presence check (ethics violation, not
      ;; efficient service) + always-escalate court-filing/new-
      ;; representation HARD invariants. 14 tests / 29 assertions
      ;; green. 9 -> 8 / 135 -> 136.
      ;; 2612 judges (private arbitration/mediation practice) promoted
      ;; to :implemented — award-amount jurisdictional ceiling (ultra
      ;; vires ruling, not a generous award) + recusal-check-cleared
      ;; presence check (conflict-of-interest violation, not efficient
      ;; service) + always-escalate binding-award-issuance/case-
      ;; acceptance HARD invariants. 14 tests / 29 assertions green.
      ;; 8 -> 7 / 136 -> 137.
      ;; 2619 legal professionals NEC (legal support & compliance
      ;; practice) promoted to :implemented — certified-copies
      ;; authorized-quantity ceiling (unregulated duplication, not
      ;; efficient service) + identity-verified presence check
      ;; (notarization fraud risk, not efficient service) + always-
      ;; escalate certification-issuance/regulatory-filing HARD
      ;; invariants. 14 tests / 29 assertions green. 7 -> 6 / 137 ->
      ;; 138. This clears the entire wave-0 batch #8 legal cluster
      ;; (2611/2612/2619) to :implemented (2412 finance was tick 86,
      ;; a separate cognitive-root cluster).
      ;; 4212 bookmakers/croupiers (gaming table & wagering operations
      ;; practice) promoted to :implemented — payout-amount ceiling
      ;; (unauthorized payout, not a lucky night) + patron-verified
      ;; presence check (underage/fraud risk, not efficient service) +
      ;; always-escalate over-limit-payout/unverified-wager HARD
      ;; invariants. 14 tests / 29 assertions green. 6 -> 5 / 138 ->
      ;; 139.
      ;; 4213 pawnbrokers/money-lenders (pawnbroking & small-loan
      ;; practice) promoted to :implemented — appraised-value ceiling
      ;; (unsecured advance, not a pawn loan) + appraisal-completed
      ;; presence check (unsecured guess, not a pawn valuation) +
      ;; always-escalate over-appraisal-disbursement/unappraised-loan-
      ;; offer HARD invariants. 14 tests / 29 assertions green. 5 -> 4
      ;; / 139 -> 140.
      ;; 4214 debt collectors (debt collection & recovery practice)
      ;; promoted to :implemented — contact-window band (harassment
      ;; risk, not diligence) + no-harassment-flag check (refused by
      ;; construction, not merely discouraged) + always-escalate
      ;; off-hours-contact/settlement-offer HARD invariants. 14 tests
      ;; / 30 assertions green. 4 -> 3 / 140 -> 141. This clears the
      ;; entire wave-0 batch #8 finance-adjacent trust services
      ;; cluster (4212/4213/4214) to :implemented.
      ;; 4411 library clerks promoted to :implemented (LibraryClerkActor,
      ;; modeled on 4214 debt collectors) — patron-verification-basis +
      ;; legal-hold-exclusion HARD invariants; late-fee-waiver-ceiling,
      ;; restrict-account and purge-record always escalate. 16 tests /
      ;; 41 assertions green.
      ;; 4412 mail carriers/sorting clerks promoted to :implemented —
      ;; verified tracking/manifest record presence check (no routing
      ;; without a manifest, not a courtesy lookup) + protected-class
      ;; (certified/registered/court mail) no-autonomous-reroute/no-
      ;; dispose HARD invariants + always-escalate redirect-elsewhere/
      ;; dispose-item. 17 tests / 48 assertions green. 2 -> 1 / 142 -> 143.
      ;; 4414 scribes and related workers promoted to :implemented
      ;; (ScribingActor, modeled on 4214 debt collectors) —
      ;; delegation-of-authority HARD gate (absence is a hard block,
      ;; not diligence) + no-actuation invariant; certify-copy,
      ;; file-on-behalf-of-client and sign-on-behalf-of-client always
      ;; escalate even when the delegation gate passes. 16 tests /
      ;; 44 assertions green. 1 -> 0 / 143 -> 144.
      ;; tick 94: wave-2 (coordination-logistics, ADR-2607121000) batch
      ;; #1 — 10 spec entries promoted to :blueprint (scaffold-only, no
      ;; src/test yet): 3311/3312/3321/3323/3334/3332 (finance/
      ;; coordination associate professionals) + 5414 (protective
      ;; services) + 8331 (drivers) + 9313/9334 (labourers). Wave 1 is
      ;; being worked separately by another session; this loop focuses
      ;; on wave 2. 0 -> 10 / 292 -> 282.
      ;; 3311 securities and finance dealers/brokers (securities
      ;; brokerage practice) promoted to :implemented — order-size
      ;; ceiling (unauthorized trading, not active management) +
      ;; suitability-reviewed presence check (unsuitable execution,
      ;; not efficient service) + always-escalate over-limit-trade/
      ;; margin-call-liquidation HARD invariants. 14 tests / 29
      ;; assertions green. 10 -> 9 / 144 -> 145.
      ;; (spec/blueprint/implemented drifted further via a concurrent
      ;; wave-1 session's own landings, untouched by this loop's
      ;; wave-2 lane — counts below are re-verified against live data
      ;; at tick 96, not hand-derived from the prior comment's delta.)
      ;; 3312 credit and loans officers (loan origination &
      ;; underwriting practice) promoted to :implemented — approved-
      ;; amount ceiling (unauthorized lending, not flexible service) +
      ;; credit-assessment-completed presence check (uninformed
      ;; lending decision, not efficient service) + always-escalate
      ;; over-approved-disbursement/loan-approval-override HARD
      ;; invariants. 14 tests / 29 assertions green.
      ;; 3321 insurance representatives (insurance brokerage practice)
      ;; promoted to :implemented — coverage-limit ceiling (unauthorized
      ;; underwriting, not generous coverage) + risk-disclosure-attached
      ;; presence check on the application record (undisclosed coverage,
      ;; not efficient service) + always-escalate over-limit-binding/
      ;; claims-settlement HARD invariants. 14 tests / 29 assertions
      ;; green. Counts re-verified live (tick 97): 8 -> 7 / 150 -> 151.
      ;; tick-98+: 1311 agricultural & forestry production managers promoted
      ;; to :implemented — FarmManagementActor with site-verification and
      ;; cost-threshold HARD invariants (farm must be registered/verified;
      ;; anomalies and supply orders above threshold require human escalation).
      ;; 14 tests / 36 assertions green. 7 / 269 -> 161
      ;; (note: parallel promotions brought main from 278/151 baseline to 270/160 before 1311).
      ;; A concurrent 3134 promotion appended a duplicate "3134" entry instead
      ;; of editing the existing :spec one (436 -> 437 total, masking 3134's
      ;; real maturity behind the stale duplicate). Removed the stale
      ;; duplicate; counts below reflect the de-duplicated 436-entry registry.
      ;; 3153 (Aircraft Pilots) promoted to :implemented: 269 -> 268 / 160 -> 161.
      ;; 2113 (Chemists) promoted to :implemented: 268 -> 267 / 161 -> 162.
      ;; 3151 (Ships' Engineers) promoted to :implemented: 267 -> 266 / 162 -> 163.
      ;; 0210 (Non-commissioned Armed Forces Officers) promoted to :implemented: 266 -> 265 / 163 -> 164.
      ;; 1344 (Social Welfare Managers) promoted to :implemented: 265 -> 264 / 164 -> 165.
      ;; 3152 (Ships' Deck Officers and Pilots) promoted to :implemented: 264 -> 263 / 165 -> 166.
      ;; 1113 (Traditional Chiefs and Heads of Villages) promoted to :implemented: 263 -> 262 / 166 -> 167.
      ;; 0310 (Armed Forces Occupations, Other Ranks) promoted to :implemented: 262 -> 261 / 167 -> 168.
      ;; 2131 (Biologists) promoted to :implemented: 261 -> 260 / 168 -> 169.
      ;; 3121 (Mining Supervisors) promoted to :implemented: 260 -> 259 / 169 -> 170.
      ;; 1114 (Senior Officials of Special-interest Organizations) and 3113
      ;; (Electrical Engineering Technicians) promoted to :implemented in one
      ;; batch after repeated 409s from concurrent registrations: 258 -> 256 / 171 -> 173.
      ;; 1346 (Financial and Insurance Services Branch Managers) promoted to :implemented: 256 -> 255 / 173 -> 174.
      ;; 1343 (Aged Care Services Managers) promoted to :implemented: 255 -> 254 / 174 -> 175.
      ;; Registry recovery: the on-disk file became unparseable EDN somewhere
      ;; around the 2149 promotion (a subsequent edit round-tripped it through
      ;; `pr-str`, double-escaping the embedded occupations string) and several
      ;; further promotions landed on top of the already-broken file without
      ;; anyone re-validating the parse after merge. Reconstructed from the
      ;; last known-good commit (3132's landing) with all 15 legitimately
      ;; completed promotions since reapplied in place: 2145, 2146, 2149, 2165,
      ;; 3115, 3116, 3117, 3118, 3119, 3133, 3135, 3139, 3142, 3143, 3155.
      ;; 245 -> 230 spec, 184 -> 199 implemented.
      ;; tick 98+: 3323 procurement/buyers promoted to :implemented —
      ;; BuyersActor (ProcurementAdvisor ⊣ ProcurementGovernor); budget-
      ;; ceiling arithmetic (order-amount <= client's :budget-ceiling) +
      ;; supplier-verification presence (:supplier-verified? true) HARD
      ;; invariants; :approve-over-budget-order, :approve-unverified-
      ;; supplier-onboarding always-escalate. 14 tests / 29 assertions green.
      ;; 6 -> 5 / 200 -> 201.
      ;; 3334 real estate agents/property managers promoted to
      ;; :implemented — RealEstateActor (Real Estate Advisor ⊣
      ;; RealEstateGovernor); authorization-ceiling arithmetic
      ;; (execution-amount <= listing's :max-authorized-amount) +
      ;; tenant-screening-completed presence (listing record) HARD
      ;; invariants; :approve-over-authorization-execution,
      ;; :approve-security-deposit-disbursement always-escalate. 14
      ;; tests / 29 assertions green. Counts re-verified live
      ;; (o/maturity-summary) rather than hand-derived from the prior
      ;; comment's delta, per the Addendum 85/86 lesson.
      ;; 3332 conference/event planners promoted to :implemented —
      ;; EventPlanningActor (Event Advisor ⊣ EventPlanningGovernor);
      ;; budget-ceiling arithmetic (contract-amount <= event's
      ;; :budget-ceiling) + venue-capacity-verified presence (event
      ;; record) HARD invariants; :approve-over-budget-vendor-contract,
      ;; :approve-guest-list-disclosure always-escalate. 14 tests / 29
      ;; assertions green. Counts re-verified live.
      ;; 5414 security guards promoted to :implemented —
      ;; SecurityGuardActor (Security Advisor ⊣ SecurityGuardGovernor);
      ;; access-level ceiling (access-level <= site's
      ;; :max-access-level) + identity-verified presence (proposal
      ;; field) HARD invariants; :approve-use-of-force-action,
      ;; :approve-detention-action always-escalate. 14 tests / 29
      ;; assertions green. Counts re-verified live.
      ;; 8331 bus/tram drivers promoted to :implemented —
      ;; PassengerTransportActor (Transport Advisor ⊣
      ;; PassengerTransportGovernor); passenger-capacity ceiling
      ;; (passenger-count <= vehicle's :max-passenger-capacity) +
      ;; pre-trip-inspection-passed presence (vehicle record) HARD
      ;; invariants; :approve-over-capacity-dispatch,
      ;; :approve-emergency-route-deviation always-escalate. 14 tests
      ;; / 29 assertions green. Counts re-verified live.
      ;; 9313 building construction labourers promoted to
      ;; :implemented — ConstructionLabourActor (Labour Advisor ⊣
      ;; ConstructionLabourGovernor); marked-zone membership (work-
      ;; zone in site's registered :marked-zones set) + safety-plan-
      ;; signed-off presence (site record) HARD invariants;
      ;; :approve-unmarked-hazard-zone-work,
      ;; :approve-confined-space-entry always-escalate. 14 tests / 30
      ;; assertions green. Counts re-verified live. Only 9334 remains
      ;; :blueprint in wave-2 batch #1.
      ;; 9334 shelf fillers promoted to :implemented —
      ;; RetailMerchandisingActor (Merchandising Advisor ⊣
      ;; RetailMerchandisingGovernor); price-authorization ceiling
      ;; (price-change-amount <= shop's :max-price-authorization) +
      ;; planogram-compliance-checked presence (shop record) HARD
      ;; invariants; :approve-over-authorization-price-change,
      ;; :approve-inventory-write-off always-escalate. 14 tests / 29
      ;; assertions green. Counts re-verified live. This clears
      ;; wave-2 batch #1 (all 10 entries from tick 94) fully to
      ;; :implemented — blueprint tier is empty again.
      ;; wave-2 batch #2 — 10 spec entries promoted to :blueprint
      ;; (scaffold-only, no src/test yet): 3313/3314/3315/3322/3324/
      ;; 3331 (associate professionals) + 5419 (protective services) +
      ;; 8343 (drivers/plant operators) + 9321/9329 (labourers). Counts
      ;; re-verified live. 0 -> 10 / 230 -> 220.
      ;; 3313 accounting associate professionals promoted to
      ;; :implemented — AccountingSupportActor (Accounting Advisor ⊣
      ;; AccountingSupportGovernor); transaction-amount ceiling
      ;; (transaction-amount <= account's :max-transaction-amount) +
      ;; source-document-attached presence (proposal) HARD invariants;
      ;; :approve-over-ceiling-posting, :approve-period-close
      ;; always-escalate. 14 tests / 29 assertions green. Counts
      ;; re-verified live. First of batch #2 to clear. 10 -> 9 / 206 -> 207.
      ;; 9 -> 8 / 207 -> 208: 3314 statistical/mathematical associate
      ;; professionals promoted to :implemented.
      ;; 8 -> 7 / 208 -> 209: 3322 commercial sales representatives
      ;; promoted to :implemented (SalesRepresentationActor).
      ;; 7 -> 6 / 209 -> 210: 3324 trade brokers promoted to :implemented
      ;; (TradeBrokerageActor).
      ;; 6 -> 0 / 211 -> 216: the remaining 5 wave-2 batch #2 entries
      ;; (3331/5419/8343/9321/9329) all cleared to :implemented in one
      ;; verification-and-implementation pass (each independently built
      ;; from a bare scaffold -- no src/test existed for any of them --
      ;; against the real kotoba-lang/langgraph API, not the reference
      ;; template's actor.cljc, which called a graph/state-graph-builder
      ;; fn that does not exist in that library; every entry independently
      ;; discovered and fixed the same latent defect). Counts re-verified
      ;; live via a direct EDN parse of the registry (211+5=216
      ;; implemented, 220 spec unchanged, 0 blueprint remaining). Blueprint
      ;; tier is empty again.
      ;; 3344 medical secretaries (Medical Secretary Advisor ⊣
      ;; MedicalSecretaryGovernor) promoted to :implemented -- SCHEDULING/
      ;; FILING LOGISTICS ONLY actor, the strictest medical-privacy
      ;; guardrail in the ISCO batch: a permanent, hard scope exclusion
      ;; (never finalize disclosure of a patient's medical record, never
      ;; provide clinical judgment/advice, never authorize a
      ;; prescription/refill) enforced twice -- op-keyword denylist plus a
      ;; defense-in-depth rationale-text scan phrased as action-phrases,
      ;; not bare nouns, to avoid the sibling-track self-tripping
      ;; false-positive bug. :flag-privacy-concern always escalates and is
      ;; never auto-commit-eligible; :coordinate-supply-order escalates
      ;; above a registered cost threshold. 25 tests / 58 assertions
      ;; green. 220 -> 219 spec, 216 -> 217 implemented.
      ;; 3333 employment agents and contractors (Placement Advisor ⊣
      ;; PlacementOps Governor) promoted to :implemented -- placement-
      ;; COORDINATION ONLY actor: a closed four-op proposal allowlist
      ;; (log-candidate-record / propose-placement-match /
      ;; flag-compliance-concern / coordinate-interview-schedule) plus
      ;; candidate/employer provenance-verification HARD invariants, a
      ;; permanent op-not-allowed HARD block outside the allowlist, and a
      ;; content-based scope-exclusion HARD block on any proposal naming a
      ;; finalization/execution action for a hiring, rejection or other
      ;; placement decision that could constitute discriminatory
      ;; screening (never a bare noun, to avoid the sibling-track
      ;; self-tripping false-positive bug). :flag-compliance-concern
      ;; always escalates and is never auto-commit-eligible; a high-stake
      ;; :propose-placement-match always escalates. 20 tests / 51
      ;; assertions green. 219 -> 218 spec, 217 -> 218 implemented.
      ;; 3341 office supervisors (Office Supervision Advisor ⊣
      ;; OfficeSupervisionGovernor) promoted to :implemented -- a closed
      ;; four-op proposal allowlist (log-workflow-record /
      ;; schedule-staff-operation / flag-hr-concern /
      ;; coordinate-supply-order) plus office/workflow/staff-member
      ;; provenance-verification HARD invariants, a permanent
      ;; op-not-allowed HARD block outside the allowlist, and a
      ;; finalization-language HARD block phrased as the finalize-ACTION
      ;; (never a bare noun) so it cannot self-trip on flag-hr-concern's
      ;; own default rationale, which necessarily discusses
      ;; disciplinary/termination/performance-review topics.
      ;; :flag-hr-concern always escalates and is never auto-commit-
      ;; eligible; a :coordinate-supply-order above the workflow's
      ;; registered cost ceiling always escalates too. 20 tests / 61
      ;; assertions green. 218 -> 217 spec, 218 -> 219 implemented.
      ;; 3342 legal secretaries (Legal Secretary Advisor ⊣
      ;; LegalSecretaryGovernor) promoted to :implemented --
      ;; coordination-ONLY actor: closed four-op proposal allowlist
      ;; (log-document-record / schedule-court-operation /
      ;; flag-confidentiality-concern / coordinate-supply-order) plus
      ;; attorney/case provenance-verification HARD invariants, an
      ;; attorney-supplied-deadline HARD invariant for scheduling
      ;; proposals, a permanent unknown-op HARD block outside the
      ;; allowlist, and a content-based scope-exclusion HARD block on
      ;; any proposal naming a finalization/execution action for
      ;; privileged-disclosure/legal-advice/deadline-without-sign-off
      ;; (never a bare noun, to avoid the sibling-track self-tripping
      ;; false-positive bug). :flag-confidentiality-concern always
      ;; escalates and is never auto-commit-eligible; an
      ;; over-cost-ceiling :coordinate-supply-order always escalates.
      ;; 22 tests / 50 assertions green. 217 -> 216 spec, 219 -> 220
      ;; implemented. Counts re-verified live via maturity-summary
      ;; rather than hand-derived from a prior comment's delta.
      ;; 3343 administrative and executive secretaries (Executive Support
      ;; Advisor ⊣ ExecutiveSupportGovernor) promoted to :implemented --
      ;; CORRESPONDENCE/CALENDAR/PROCUREMENT COORDINATION ONLY actor,
      ;; never signing or commitment authority: a closed four-op proposal
      ;; allowlist (log-correspondence-record / schedule-executive-
      ;; operation / flag-authority-concern / coordinate-supply-order)
      ;; plus executive/office-provenance and no-actuation HARD
      ;; invariants, a permanent closed-op-allowlist HARD block outside
      ;; the allowlist, and a content-based scope-exclusion HARD block on
      ;; any proposal naming the finalization/execution action of a
      ;; signature or financial/contractual commitment on the executive's
      ;; behalf (phrased as the action, e.g. "finalize the signature on
      ;; the executive's behalf", never the bare noun, to avoid the
      ;; sibling-track self-tripping false-positive bug -- verified via a
      ;; dedicated regression test that the default mock advisor's
      ;; proposals for all four ops never self-trip it, plus a separate
      ;; true-positive test that a hand-crafted finalize-signature
      ;; rationale still hard-blocks). :flag-authority-concern always
      ;; escalates and is never auto-commit-eligible; a
      ;; :coordinate-supply-order above the executive's registered cost
      ;; ceiling escalates (not a hard block -- routine procurement, not
      ;; a signature/commitment finalization risk). 15 tests / 37
      ;; assertions green. Counts re-verified live via
      ;; (occupation/maturity-summary), not hand-derived from the prior
      ;; comment's delta, per the Addendum 85/86 lesson.
      ;; 3354 government licensing officials promoted to :implemented --
      ;; LicensingCoordinationActor (Licensing Coordination Advisor ⊣
      ;; LicensingCoordinationGovernor); closed four-op proposal allowlist
      ;; (:log-application-record, :schedule-review-appointment,
      ;; :flag-licensing-review, :coordinate-supply-order) -- a
      ;; documentation/logistics-coordination robot ONLY, with NO
      ;; licensing/permitting-decision authority: issuing, denying,
      ;; renewing or revoking a license or permit is structurally absent
      ;; from the closed op-allowlist (not merely gated), plus a
      ;; defense-in-depth finalization-language HARD block phrased as the
      ;; finalize-ACTION (e.g. "issue the license", "deny the license
      ;; application", "revoke the license"), never a bare noun, to
      ;; avoid the sibling-track self-tripping false-positive bug --
      ;; verified via a dedicated regression test that the default mock
      ;; advisor's proposals for all four ops (including
      ;; :flag-licensing-review's own disclaiming rationale) never
      ;; self-trip it. :flag-licensing-review always escalates and is
      ;; never auto-commit-eligible; an over-cost-ceiling
      ;; :coordinate-supply-order always escalates too. 18 tests / 68
      ;; assertions green (cloud-itonami-isco-3354, ADR-2791003354).
      ;; Counts re-verified live via (occupation/maturity-summary) rather
      ;; than hand-derived from a prior comment's delta, per the Addendum
      ;; 85/86 lesson.
      ;; a concurrent sibling ISCO-track session landed another
      ;; promotion between this session's registry PUT and this
      ;; assertion bump; re-verified live via
      ;; (occupation/maturity-summary) against a freshly re-fetched
      ;; origin/main rather than hand-derived: 214 -> 213 spec, 222 ->
      ;; 223 implemented, 436 total unchanged.
      ;; another concurrent sibling ISCO-track session landed a further
      ;; promotion in the same window; re-verified live once more via
      ;; (occupation/maturity-summary) against a freshly re-fetched
      ;; origin/main: 213 -> 212 spec, 223 -> 224 implemented, 436 total
      ;; unchanged.
      ;; 3353 (Government Social Benefits Officials) promoted to
      ;; :implemented -- SocialBenefitsActor (Case Documentation Advisor
      ;; ⊣ SocialBenefitsGovernor); a DOCUMENTATION/LOGISTICS-
      ;; COORDINATION ONLY actor with NO benefits-eligibility-
      ;; determination authority anywhere in its closed four-op
      ;; allowlist (:log-application-record, :schedule-caseworker-
      ;; appointment, :flag-eligibility-review, :coordinate-supply-
      ;; order) -- no op resembling approving, denying or terminating a
      ;; benefit exists structurally, reinforced by a content-based
      ;; scope-exclusion HARD block (phrased as finalization/execution
      ;; actions, never bare nouns, to avoid the sibling-track
      ;; self-tripping false-positive bug -- verified via a dedicated
      ;; regression test). :flag-eligibility-review always escalates
      ;; and is never auto-commit-eligible; an above-cost-threshold
      ;; :coordinate-supply-order always escalates. 25 tests / 60
      ;; assertions green (cloud-itonami-isco-3353, ADR-2790003353).
      ;; Counts re-verified live via maturity-summary rather than
      ;; hand-derived from a prior comment's delta.
      ;; cloud-itonami-isco-3351 (Customs and Border Inspectors --
      ;; documentation/logistics-coordination only, no search/seizure/
      ;; detention/entry-denial authority anywhere in its op-allowlist)
      ;; was promoted to :implemented in this same registry-edit window;
      ;; its registry PUT (211 -> 210 spec, 225 -> 226 implemented) is
      ;; the transition already reflected in the totals directly below --
      ;; re-verified live via (occupation/maturity-summary) against a
      ;; freshly re-fetched origin/main immediately before this edit
      ;; (registry blob sha unchanged since that PUT, so no further
      ;; sibling registry change landed in between), 436 total unchanged.
      ;; cloud-itonami-isco-5412 (Police Officers) promoted to
      ;; :implemented in this same registry-edit window; re-verified
      ;; live via (occupation/maturity-summary) against a freshly
      ;; re-fetched origin/main rather than hand-derived from a prior
      ;; comment's delta (other concurrent sibling ISCO-track sessions
      ;; may also have landed registry promotions in this same window):
      ;; 209 -> 207 spec, 227 -> 229 implemented,
      ;; 436 total unchanged.
      ;; cloud-itonami-isco-5413 (Prison Guards) promoted to
      ;; :implemented -- FacilityOpsActor (Facility Ops Advisor ⊣
      ;; FacilityOpsGovernor); a facility/equipment/scheduling
      ;; DOCUMENTATION/LOGISTICS-COORDINATION ONLY actor with NO
      ;; use-of-force, physical-restraint, disciplinary-sanction or
      ;; movement/confinement-condition-restriction authority anywhere
      ;; in its closed four-op allowlist (:log-facility-record,
      ;; :schedule-staff-operation, :flag-facility-concern,
      ;; :coordinate-supply-order) -- given the captive-population
      ;; vulnerability, this is held to at least the same guardrail
      ;; strictness as the cloud-itonami-isco-3355 (Police Inspectors
      ;; and Detectives) reference, PLUS a domain-specific HARD,
      ;; cost-independent block on any weapon/restraint-device
      ;; procurement item with no analogue in that reference. Verified
      ;; via a closed op-allowlist, a content-based scope-exclusion
      ;; HARD block (phrased as finalization/execution actions, e.g.
      ;; "apply the physical restraint", "impose the disciplinary
      ;; sanction", "restrict movement privileges", never bare nouns,
      ;; to avoid the sibling-track self-tripping false-positive bug --
      ;; verified via a dedicated regression test), and an end-to-end
      ;; rogue-advisor test driving the full compiled StateGraph proving
      ;; every forbidden op/rationale/item hard-blocks even when the
      ;; advisor node itself is compromised. :flag-facility-concern
      ;; always escalates and is never auto-commit-eligible; an
      ;; above-cost-threshold, non-weapon :coordinate-supply-order
      ;; always escalates. 51 tests / 172 assertions green
      ;; (cloud-itonami-isco-5413, ADR-2799005413). Counts re-verified
      ;; live via (occupation/maturity-summary) against a freshly
      ;; re-fetched origin/main immediately before this edit (registry
      ;; blob sha unchanged since that PUT, so no further sibling
      ;; registry change landed in between): 207 -> 206 spec, 229 -> 230
      ;; implemented, 436 total unchanged.
      ;; cloud-itonami-isco-8321 (Motorcycle Drivers) promoted to
      ;; :implemented -- MotorcycleDispatchActor (Motorcycle Dispatch
      ;; Advisor ⊣ MotorcycleDispatchGovernor); a dispatch/logistics-
      ;; SCHEDULING-COORDINATION ONLY actor with NO route/traffic-
      ;; navigation-finalization or rider-safety-judgment-override
      ;; authority anywhere in its closed four-op allowlist
      ;; (:log-delivery-record, :schedule-dispatch-operation,
      ;; :flag-safety-concern, :coordinate-maintenance-order) -- this
      ;; actor never operates the motorcycle. Verified via a closed
      ;; op-allowlist, independently-verified rider/license provenance,
      ;; and a content-based scope-exclusion HARD block (phrased as
      ;; finalization/execution actions, e.g. "finalize the route
      ;; decision", "override the rider's on-road safety judgment",
      ;; never bare nouns, to avoid the sibling-track self-tripping
      ;; false-positive bug -- verified via a dedicated regression
      ;; test). :flag-safety-concern always escalates and is never
      ;; auto-commit-eligible; an above-cost-threshold
      ;; :coordinate-maintenance-order always escalates. 22 tests / 49
      ;; assertions green (cloud-itonami-isco-8321, ADR-2799008321).
      ;; Counts re-verified live via (occupation/maturity-summary)
      ;; against a freshly re-fetched origin/main immediately before
      ;; this edit: 206 -> 204 spec, 230 -> 232
      ;; implemented, 436 total unchanged.
      ;; cloud-itonami-isco-8311 (Locomotive Engine Drivers) promoted to
      ;; :implemented -- RailCrewActor (Rail Crew Advisor ⊣
      ;; RailCrewGovernor); a rail-crew scheduling/logistics
      ;; COORDINATION ONLY actor with NO locomotive-movement/throttle/
      ;; brake-control or signal-override/stop-signal-departure
      ;; authority anywhere in its closed four-op allowlist
      ;; (:log-service-record, :schedule-crew-operation,
      ;; :flag-safety-concern, :coordinate-maintenance-order) -- this
      ;; actor never operates the locomotive. Verified via a closed
      ;; op-allowlist, independently-verified driver/route-license
      ;; provenance, and a content-based scope-exclusion HARD block
      ;; (phrased as finalization/execution actions, e.g. "initiate the
      ;; locomotive movement", "override the stop signal", never bare
      ;; nouns, to avoid the sibling-track self-tripping false-positive
      ;; bug -- verified via a dedicated regression test).
      ;; :flag-safety-concern always escalates and is never
      ;; auto-commit-eligible; an above-cost-ceiling
      ;; :coordinate-maintenance-order always escalates. 22 tests / 48
      ;; assertions green (cloud-itonami-isco-8311, ADR-2799008311).
      ;; Counts re-verified live via (occupation/maturity-summary)
      ;; against a freshly re-fetched origin/main immediately before
      ;; this edit (other concurrent sibling ISCO-track sessions landed
      ;; registry promotions in this same window, e.g. 8321 Motorcycle
      ;; Drivers immediately prior): 204 -> 203 spec, 232 -> 233
      ;; implemented, 436 total unchanged.
      ;; catch-up: 203 -> 200 spec, 233 -> 236
      ;; implemented -- further sibling ISCO-track promotion(s) landed
      ;; concurrently in this fleet batch; recomputed live via
      ;; (occupation/maturity-summary) against a freshly re-fetched
      ;; origin/main immediately before this edit, not assumed
      ;; (cloud-itonami-isco-8321 batch, ADR-2799008321).
            ;; cloud-itonami-isco-8341 (Mobile Farm and Forestry Plant
      ;; Operators) promoted to :implemented -- FarmForestryOpsActor
      ;; (Farm/Forestry Ops Advisor ⊣ FarmForestryOpsGovernor); closed
      ;; four-op allowlist (:log-service-record,
      ;; :schedule-crew-operation, :flag-safety-concern,
      ;; :coordinate-maintenance-order) -- a scheduling/logistics
      ;; coordination robot ONLY, with NO equipment-operation/movement-
      ;; finalization or operator-safety-judgment-override authority
      ;; anywhere in its allowlist (structurally absent, not merely
      ;; gated), backed by a content-based scope-exclusion HARD block
      ;; phrased exclusively as finalization/execution actions, never
      ;; bare nouns, to avoid the sibling-track self-tripping
      ;; false-positive bug (dedicated regression test verifies the
      ;; default mock advisor never self-trips). 19 tests / 44
      ;; assertions green (cloud-itonami-isco-8341, ADR-2799008341).
      ;; Counts re-verified live via (occupation/maturity-summary)
      ;; against a freshly re-fetched origin/main immediately before
      ;; this edit: 200 -> 199 spec, 236 -> 237
      ;; implemented, 436 total unchanged.
            ;; cloud-itonami-isco-9332 (Drivers of Animal-drawn Vehicles and
      ;; Machinery) promoted to :implemented -- CartageActor (Dispatch
      ;; Logistics Advisor ⊣ CartageGovernor); a dispatch/logistics
      ;; SCHEDULING-COORDINATION ONLY actor with NO route/traffic-
      ;; navigation-finalization, animal-welfare/treatment-decision, or
      ;; driver-on-road/animal-handling-judgment-override authority
      ;; anywhere in its closed four-op allowlist (:log-trip-record,
      ;; :schedule-dispatch-operation, :flag-welfare-concern,
      ;; :coordinate-maintenance-order) -- this actor never operates the
      ;; vehicle and never directly handles the animal, and carries BOTH
      ;; a road-safety dimension AND an animal-welfare dimension
      ;; simultaneously excluded from the closed op allowlist. Verified
      ;; via a closed op-allowlist, independently-verified driver/depot
      ;; provenance, two content-shape forbidden-key guardrails
      ;; (trip-record-decision, dispatch-schedule-override), and a
      ;; content-based scope-exclusion HARD block (phrased as
      ;; finalization/execution actions, e.g. "finalize the route",
      ;; "determine the animal's fitness for work", never bare nouns --
      ;; the op is literally named :flag-welfare-concern, so a bare
      ;; "welfare" term would self-trip every legitimate proposal --
      ;; verified via a dedicated regression test). :flag-welfare-concern
      ;; always escalates and is never auto-commit-eligible; an
      ;; above-cost-threshold :coordinate-maintenance-order always
      ;; escalates. 45 tests / 154 assertions green
      ;; (cloud-itonami-isco-9332, ADR-2799009332). Counts re-verified
      ;; live via (occupation/maturity-summary) against a freshly
      ;; re-fetched origin/main immediately before this edit: 198 ->
      ;; 198 spec, 238 -> 238 implemented, 436 total
      ;; unchanged.
            ;; cloud-itonami-isco-9332 (Drivers of Animal-drawn Vehicles and
      ;; Machinery) promoted to :implemented -- CartageActor (Dispatch
      ;; Logistics Advisor ⊣ CartageGovernor); a dispatch/logistics
      ;; SCHEDULING-COORDINATION ONLY actor with NO route/traffic-
      ;; navigation-finalization, animal-welfare/treatment-decision, or
      ;; driver-on-road/animal-handling-judgment-override authority
      ;; anywhere in its closed four-op allowlist (:log-trip-record,
      ;; :schedule-dispatch-operation, :flag-welfare-concern,
      ;; :coordinate-maintenance-order) -- this actor never operates the
      ;; vehicle and never directly handles the animal, and carries BOTH
      ;; a road-safety dimension AND an animal-welfare dimension
      ;; simultaneously excluded from the closed op allowlist. Verified
      ;; via a closed op-allowlist, independently-verified driver/depot
      ;; provenance, two content-shape forbidden-key guardrails
      ;; (trip-record-decision, dispatch-schedule-override), and a
      ;; content-based scope-exclusion HARD block (phrased as
      ;; finalization/execution actions, e.g. "finalize the route",
      ;; "determine the animal's fitness for work", never bare nouns --
      ;; the op is literally named :flag-welfare-concern, so a bare
      ;; "welfare" term would self-trip every legitimate proposal --
      ;; verified via a dedicated regression test). :flag-welfare-concern
      ;; always escalates and is never auto-commit-eligible; an
      ;; above-cost-threshold :coordinate-maintenance-order always
      ;; escalates. 45 tests / 154 assertions green
      ;; (cloud-itonami-isco-9332, ADR-2799009332). Counts re-verified
      ;; live via (occupation/maturity-summary) against a freshly
      ;; re-fetched origin/main immediately before this edit: 197 ->
      ;; 197 spec, 239 -> 239 implemented, 436 total
      ;; unchanged.
      ;; cloud-itonami-isco-9331 (Hand and Pedal Vehicle Drivers)
      ;; promoted to :implemented -- HandPedalDispatchActor (Hand/Pedal
      ;; Vehicle Dispatch Advisor ⊣ HandPedalDispatchGovernor); a
      ;; dispatch/logistics SCHEDULING-COORDINATION ONLY actor with NO
      ;; route/traffic-navigation-finalization or driver-on-road-safety-
      ;; judgment-override authority anywhere in its closed four-op
      ;; allowlist (:log-trip-record, :schedule-dispatch-operation,
      ;; :flag-safety-concern, :coordinate-maintenance-order) -- this
      ;; actor never operates the rickshaw/pedicab/hand-cart. Verified
      ;; via the closed op-allowlist, independently-verified driver
      ;; permit provenance, a registered-vehicle basis for maintenance
      ;; orders, and a content-based route/traffic-navigation
      ;; scope-exclusion HARD block phrased as finalization/execution
      ;; actions (e.g. "override the driver's route judgment", "finalize
      ;; the traffic-navigation decision"), never bare nouns -- a
      ;; dedicated regression test proves the default mock advisor's own
      ;; proposals across all four allowlisted ops never self-trip that
      ;; block. :flag-safety-concern always escalates and is never
      ;; auto-commit-eligible; an above-cost-threshold
      ;; :coordinate-maintenance-order always escalates instead of
      ;; hard-blocking (a cost overrun is a business decision, not a
      ;; safety violation). 20 tests / 46 assertions green
      ;; (cloud-itonami-isco-9331, ADR-2799009331). Counts re-verified
      ;; live via (occupation/maturity-summary) against a freshly
      ;; re-fetched origin/main immediately before this edit.
      ;; 197 -> 195 spec / 239 -> 241 implemented with the
      ;; cloud-itonami-isco-7111 (House Builders) promotion
      ;; (ADR-2799007111) plus one other concurrent sibling promotion
      ;; landed in this same retry batch (net +2 implemented / -2
      ;; spec since the 9331 comment above was written). Counts
      ;; re-verified live via (occupation/maturity-summary) against a
      ;; freshly re-fetched origin/main immediately before this edit,
      ;; not hand-derived from a prior comment's delta.
      ;; 7119 (Building Frame and Related Trades Workers NEC) promoted
      ;; to :implemented -- FramingCrewActor (Framing Crew Advisor ⊣
      ;; FramingCrewGovernor); closed four-op proposal allowlist
      ;; (:log-work-record, :schedule-crew-operation,
      ;; :flag-safety-concern, :coordinate-supply-order) -- a job-site
      ;; scheduling/logistics coordination robot ONLY, never direct
      ;; framing-work-execution authority. Counts re-verified live via
      ;; (occupation/maturity-summary) against a freshly re-fetched
      ;; origin/main immediately before this edit: 195 -> 194 spec /
      ;; 241 -> 242 implemented (net +1/-1, this promotion only -- no
      ;; other concurrent sibling landed between the prior comment and
      ;; this edit).
      ;; cloud-itonami-isco-7121 (Roofers) also promoted to
      ;; :implemented in this same concurrent retry batch (see the
      ;; dedicated roofers-7121-implemented test below for detail).
      ;; This number (192 spec / 244 implemented) is a live
      ;; re-fetch of (occupation/maturity-summary) taken immediately
      ;; before this edit, not hand-derived from any prior comment's
      ;; delta -- several sibling promotions landed concurrently in
      ;; this same batch.
      ;; cloud-itonami-isco-7112 (Bricklayers and Related Workers)
      ;; promotion (ADR-2799007112) plus any other concurrent sibling
      ;; promotions landed in this same retry batch. Counts
      ;; re-verified live via (occupation/maturity-summary) against a
      ;; freshly re-fetched origin/main immediately before this edit.
      ;; cloud-itonami-isco-7123 (Plasterers) promoted to :implemented
      ;; (ADR-2799007123, see plasterers-7123-implemented test below for
      ;; detail). This number (187 spec / 0 blueprint /
      ;; 249 implemented) is a live re-fetch of
      ;; (occupation/maturity-summary) taken immediately before this edit,
      ;; not hand-derived from any prior comment's delta -- several
      ;; sibling promotions may have landed concurrently in this same
      ;; batch.
      ;; cloud-itonami-isco-7125 (Glaziers) also promoted to
      ;; :implemented in this same concurrent retry batch (see the
      ;; dedicated glaziers-7125-implemented test below for detail).
      ;; This number (186 spec / 250 implemented) is a live
      ;; re-fetch of (occupation/maturity-summary) taken immediately
      ;; before this edit, not hand-derived from any prior comment's
      ;; delta -- several sibling promotions may land concurrently in
      ;; this same batch.
      ;; cloud-itonami-isco-7124 (Insulation Workers) promoted to
      ;; :implemented (ADR-2799007124, see
      ;; insulation-workers-7124-implemented test below for detail).
      ;; This count is a live re-fetch of (occupation/maturity-summary)
      ;; taken immediately before this edit, not hand-derived from any
      ;; prior comment's delta -- several sibling promotions may have
      ;; landed concurrently in this same batch.
      ;; cloud-itonami-isco-7131 (Painters and Related Workers) promoted
      ;; to :implemented (ADR-2799007131, see painters-7131-implemented
      ;; test below for detail). Live-recomputed via
      ;; (occupation/maturity-summary) against a freshly re-fetched
      ;; origin/main immediately before this edit: 185 spec / 0
      ;; blueprint / 251 implemented -- the assertions immediately
      ;; below already reflect this promotion (numbers unchanged by
      ;; this edit, only this note is new).
      ;; cloud-itonami-isco-7122 (Floor Layers and Tile Setters) also
      ;; promoted to :implemented in this same concurrent retry batch
      ;; (see the dedicated floor-layers-tile-setters-7122-implemented
      ;; test below for detail, ADR-2799007122). This number (185 spec /
      ;; 251 implemented) is a live re-fetch of
      ;; (occupation/maturity-summary) via a fresh GitHub API fetch of
      ;; registry.edn immediately before this edit, not hand-derived
      ;; from any prior comment's delta -- several sibling promotions
      ;; have landed concurrently in this same batch.
      ;; cloud-itonami-isco-7214 (Structural Metal Preparers and
      ;; Erectors) promoted to :implemented (ADR-2799007214, see
      ;; structural-metal-preparers-erectors-7214-implemented test below
      ;; for detail). This number (184 spec / 252 implemented) is a live
      ;; re-fetch of (occupation/maturity-summary) taken immediately
      ;; before this edit, not hand-derived from any prior comment's
      ;; delta -- several sibling promotions may have landed
      ;; concurrently in this same batch.
      ;; cloud-itonami-isco-7213 (Sheet Metal Workers) also promoted to
      ;; :implemented in this same concurrent retry batch (see the
      ;; dedicated sheet-metal-workers-7213-implemented test below for
      ;; detail, ADR-2799007213). This number (180 spec /
      ;; 256 implemented) is a live re-fetch of
      ;; (occupation/maturity-summary) via a fresh GitHub API fetch of
      ;; registry.edn immediately before this edit, not hand-derived
      ;; from any prior comment's delta -- several sibling promotions
      ;; have landed concurrently in this same batch.
      ;; Retry: live re-fetch of (occupation/maturity-summary) against
      ;; origin/main (attempt 1) taken immediately before this retry-edit shows
      ;; 179 spec / 257 implemented (was 179/257 in the
      ;; just-fetched file) -- further sibling promotions landed
      ;; concurrently in this same batch; this actor's own
      ;; cloud-itonami-isco-7214 promotion remains reflected throughout.
      (is (= 0 (:blueprint m)))
      (is (= 179 (:spec m)))
      (is (= 257 (:implemented m))))))

(deftest maturity-roadmap-reports-next-step
  (testing "an implemented entry is at maturity ceiling"
    (let [r (occupation/maturity-roadmap "6112")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r)))
      (is (= "at maturity ceiling" (:next-action r))))
    (let [r (occupation/maturity-roadmap "2221")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "7126")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "4321")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "9312")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "5322")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "8332")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "1321")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "3253")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "6210")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "5223")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "7231")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "8121")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "9111")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "2512")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "1120")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "4110")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "3213")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "5153")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "7411")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "2262")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "4222")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "5311")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "6130")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "8160")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r))))
    (let [r (occupation/maturity-roadmap "1212")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r)))))
  (testing "an implemented entry that was the last :blueprint (3521,
            promoted tick 83) is at maturity ceiling like any other"
    (let [r (occupation/maturity-roadmap "3521")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r)))
      (is (true? (:has-repo r)))))
  (testing "an implemented entry that was previously :blueprint (4411,
            wave-0 cognitive batch #8 -> implemented) is at maturity
            ceiling like any other"
    (let [r (occupation/maturity-roadmap "4411")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r)))
      (is (true? (:has-repo r)))))
  (testing "an implemented entry that was wave-2 batch #1's last
            remaining :blueprint (9334) is at maturity ceiling like
            any other"
    (let [r (occupation/maturity-roadmap "9334")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r)))
      (is (true? (:has-repo r)))))
  (testing "an implemented entry that was wave-2 batch #2's last
            remaining :blueprint tier (8343, along with 3331/5419/9321/
            9329) is at maturity ceiling like any other"
    (let [r (occupation/maturity-roadmap "8343")]
      (is (= :implemented (:maturity r)))
      (is (nil? (:next-step r)))
      (is (true? (:has-repo r)))))
  (testing "a spec entry's next step is blueprint"
    (let [r (occupation/maturity-roadmap "1411")]
      (is (= :spec (:maturity r)))
      (is (= :blueprint (:next-step r)))
      (is (false? (:has-repo r))))))

(deftest execution-plan-reports-technology-stack
  (let [p (occupation/execution-plan "6112")]
    (is (map? p))
    (is (seq (:technology-stack p)))))

(deftest legal-secretaries-3342-implemented
  (testing "3342 (Legal Secretaries) promoted to :implemented --
            LegalSecretaryActor (Legal Secretary Advisor ⊣
            LegalSecretaryGovernor); closed proposal-op allowlist
            (:log-document-record, :schedule-court-operation,
            :flag-confidentiality-concern, :coordinate-supply-order)
            with attorney/case provenance + attorney-supplied-deadline
            + scope-exclusion HARD invariants (no op can finalize
            disclosure of privileged/confidential case information,
            provide legal advice, or set a filing deadline/legal
            strategy without attorney sign-off);
            :flag-confidentiality-concern always escalates and is
            never auto-commit-eligible; over-cost-ceiling supply
            orders escalate. 22 tests / 50 assertions green
            (cloud-itonami-isco-3342, ADR-2790003342). Counts
            re-verified live via maturity-summary rather than
            hand-derived from a prior comment's delta."
    (is (= :implemented (occupation/maturity "3342")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-3342"
           (:repo (occupation/get-occupation "3342"))))
    (is (= "cloud-itonami-isco-3342"
           (:business-id (occupation/get-occupation "3342"))))))

(deftest government-licensing-officials-3354-implemented
  (testing "3354 (Government Licensing Officials) promoted to
            :implemented -- LicensingCoordinationActor (Licensing
            Coordination Advisor ⊣ LicensingCoordinationGovernor); closed
            proposal-op allowlist (:log-application-record,
            :schedule-review-appointment, :flag-licensing-review,
            :coordinate-supply-order) -- a documentation/logistics-
            coordination robot ONLY. This actor has NO licensing/
            permitting-decision authority: no op that issues, denies,
            renews or revokes a license or permit exists anywhere in the
            allowlist (structurally absent, not merely gated), backed by
            a finalization-language HARD block phrased as the
            finalize-ACTION (never a bare noun) so it cannot self-trip on
            :flag-licensing-review's own disclaiming default rationale.
            :flag-licensing-review always escalates and is never
            auto-commit-eligible; over-cost-ceiling supply orders
            escalate. 18 tests / 68 assertions green
            (cloud-itonami-isco-3354, ADR-2791003354). Counts
            re-verified live via maturity-summary rather than
            hand-derived from a prior comment's delta."
    (is (= :implemented (occupation/maturity "3354")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-3354"
           (:repo (occupation/get-occupation "3354"))))
    (is (= "cloud-itonami-isco-3354"
           (:business-id (occupation/get-occupation "3354"))))))

(deftest government-tax-excise-officials-3352-implemented
  (testing "3352 (Government Tax and Excise Officials) promoted to
            :implemented -- TaxExciseActor (TaxFilingAdvisor ⊣
            TaxExciseGovernor); closed proposal-op allowlist
            (:log-filing-record, :schedule-audit-appointment,
            :flag-compliance-concern, :coordinate-supply-order) -- a
            documentation/logistics-coordination robot ONLY. This actor
            has NO tax-assessment, penalty or collection authority: no
            op that finalizes a tax assessment, imposes a penalty, or
            orders a collection/lien action exists anywhere in the
            allowlist (structurally absent, not merely gated), backed
            by a defense-in-depth enforcement-scope text scan phrased
            as finalize/impose/order ACTIONS (never bare nouns) so it
            cannot self-trip on :flag-compliance-concern's own
            legitimate descriptive rationale.
            :flag-compliance-concern always escalates and is never
            auto-commit-eligible; over-cost-ceiling supply orders and
            over-capacity audit-appointment scheduling escalate/hard-
            block respectively. 24 tests / 54 assertions green
            (cloud-itonami-isco-3352, ADR-2790003352). Counts
            re-verified live via maturity-summary rather than
            hand-derived from a prior comment's delta."
    (is (= :implemented (occupation/maturity "3352")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-3352"
           (:repo (occupation/get-occupation "3352"))))
    (is (= "cloud-itonami-isco-3352"
           (:business-id (occupation/get-occupation "3352"))))))
(deftest government-social-benefits-officials-3353-implemented
  (testing "3353 (Government Social Benefits Officials) promoted to
            :implemented -- SocialBenefitsActor (Case Documentation
            Advisor ⊣ SocialBenefitsGovernor); a DOCUMENTATION/
            LOGISTICS-COORDINATION ONLY actor. This occupation directly
            controls people's livelihoods (benefits eligibility), so it
            has NO op, anywhere in its closed four-op allowlist
            (:log-application-record, :schedule-caseworker-appointment,
            :flag-eligibility-review, :coordinate-supply-order), that
            resembles approving, denying or terminating a benefit -- that
            capability is structurally absent, not merely gated, and is
            reinforced by an independent content-based scope-exclusion
            HARD check on proposal rationale text.
            :flag-eligibility-review is the ONLY channel by which an
            eligibility-adjacent observation may be surfaced, and it
            ALWAYS escalates to a human caseworker, never auto-commit-
            eligible at any phase; an above-cost-threshold
            :coordinate-supply-order always escalates. 25 tests / 60
            assertions green (cloud-itonami-isco-3353, ADR-2790003353).
            Counts re-verified live via maturity-summary rather than
            hand-derived from a prior comment's delta."
    (is (= :implemented (occupation/maturity "3353")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-3353"
           (:repo (occupation/get-occupation "3353"))))
    (is (= "cloud-itonami-isco-3353"
           (:business-id (occupation/get-occupation "3353"))))))
(deftest customs-border-inspectors-3351-implemented
  (testing "3351 (Customs and Border Inspectors) promoted to
            :implemented -- CustomsInspectionActor (Customs Inspection
            Advisor ⊣ CustomsInspectionGovernor); closed four-op
            proposal allowlist (:log-inspection-record,
            :schedule-lane-operation, :flag-inspection-concern,
            :coordinate-supply-order) -- a documentation/logistics-
            coordination robot ONLY. This actor has NO search, seizure,
            detention, entry-denial or citation/penalty authority: no op
            resembling authorizing a search, ordering a seizure,
            ordering a detention, denying entry, or issuing a
            citation/penalty exists anywhere in the allowlist
            (structurally absent, not merely gated), backed by a
            defense-in-depth enforcement-scope text scan phrased as
            finalize/order/deny/issue ACTIONS (never bare nouns, e.g.
            \"authorize the search\", \"order the seizure\", \"deny
            entry\") so it cannot self-trip on the mock advisor's own
            legitimate descriptive rationale (which necessarily mentions
            those bare nouns while explicitly disclaiming them) --
            verified via a dedicated regression test that the default
            mock advisor's proposals for all four ops never self-trip
            it, plus a separate true-positive test that a hand-crafted
            finalize-the-search/order-the-seizure/deny-entry rationale
            still hard-blocks, and an end-to-end actor-level test that a
            directly-constructed request for a phantom enforcement op
            (:authorize-search) holds rather than commits.
            :flag-inspection-concern always escalates and is never
            auto-commit-eligible; an over-cost-ceiling
            :coordinate-supply-order escalates (not a hard block --
            routine procurement above a facility's registered threshold,
            not an enforcement-authority risk). 22 tests / 70 assertions
            green (cloud-itonami-isco-3351, ADR-2791003351). Counts
            re-verified live via (occupation/maturity-summary) against a
            freshly re-fetched origin/main rather than hand-derived from
            a prior comment's delta."
    (is (= :implemented (occupation/maturity "3351")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-3351"
           (:repo (occupation/get-occupation "3351"))))
    (is (= "cloud-itonami-isco-3351"
           (:business-id (occupation/get-occupation "3351"))))))

(deftest police-officers-5412-implemented
  (testing "5412 (Police Officers) promoted to :implemented --
            PrecinctOpsActor (Precinct Coordination Advisor ⊣
            PrecinctOpsGovernor); closed four-op proposal allowlist
            (:log-equipment-record, :schedule-patrol-operation,
            :flag-precinct-concern, :coordinate-supply-order) -- a
            precinct/equipment/patrol-scheduling documentation/
            logistics-coordination robot ONLY, mirroring the sibling
            3355 (Police Inspectors and Detectives) safety pattern at
            equal or greater rigor. This actor has ZERO use-of-force,
            weapon-deployment, arrest, detention, search/seizure-
            authorization, or suspect-pursuit/engagement authority: no
            op resembling any of those six categories exists anywhere
            in the allowlist (structurally absent, not merely gated),
            confirmed by fifteen concretely-named forbidden-op
            fixtures rejected at both the governor-unit level and the
            full-actor-graph level via a rogue advisor, backed by a
            content-based scope-exclusion HARD block phrased as
            finalization/execution ACTIONS (never bare nouns, e.g.
            \"use force\", \"deploy the weapon\", \"make the
            arrest\", \"detain the suspect\", \"authorize the
            search\", \"pursue the suspect\") plus a DEDICATED
            hard block on :coordinate-supply-order naming any weapon/
            ammunition/lethal-equipment item (independent of and in
            addition to the cost-threshold escalation -- blocked even
            at $1) -- verified via a dedicated regression test that
            the default mock advisor's proposals for all four ops
            never self-trip either check, plus an end-to-end
            rogue-advisor integration test driving the full compiled
            StateGraph, proving :commit can never be reached for any
            of the fifteen forbidden ops.
            :flag-precinct-concern always escalates and is never
            auto-commit-eligible; an over-cost-ceiling
            :coordinate-supply-order (for allowed, non-weapon items
            only) escalates -- not a hard block, routine procurement
            above a unit's registered threshold, not itself unsafe.
            54 tests / 203 assertions green (cloud-itonami-isco-5412,
            ADR-2792005412). Counts re-verified live via
            (occupation/maturity-summary) against a freshly re-fetched
            origin/main rather than hand-derived from a prior
            comment's delta."
    (is (= :implemented (occupation/maturity "5412")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-5412"
           (:repo (occupation/get-occupation "5412"))))
    (is (= "cloud-itonami-isco-5412"
           (:business-id (occupation/get-occupation "5412"))))))
(deftest firefighters-5411-implemented
  (testing "5411 (Firefighters) promoted to :implemented --
            FirestationActor (Station Logistics Advisor ⊣
            FirestationGovernor); closed four-op proposal allowlist
            (:log-equipment-record, :schedule-crew-operation,
            :flag-readiness-concern, :coordinate-supply-order) -- a
            station/equipment/administrative-logistics coordination
            robot ONLY. This actor has NO structure-entry, casualty-
            triage/resource-prioritization, medical-treatment, or
            active-incident tactical/operational-command authority: no
            op resembling deciding whether to enter a hazardous
            structure, prioritizing casualties/resources under
            scarcity, providing medical treatment, or issuing a
            tactical/incident-command order exists anywhere in the
            allowlist (structurally absent, not merely gated), backed
            by two content-shape HARD blocks (a log entry can never
            carry a tactical assessment/entry-decision field, a
            schedule proposal can never carry an active-incident
            tactical-assignment field) plus a defense-in-depth
            scope-exclusion text scan phrased exclusively as
            finalization/execution ACTIONS (e.g. \"commit to structure
            entry\", \"make the triage decision\", \"provide medical
            treatment\", \"issue the tactical command\"), never bare
            nouns, so it cannot self-trip on the mock advisor's own
            legitimate rationale -- verified via a dedicated regression
            test that the default mock advisor's proposals for all four
            ops (including both the station-id-present and
            station-id-nil variants of :flag-readiness-concern) never
            self-trip it, plus an end-to-end actor-level test that a
            rogue advisor forcing each of thirteen named
            scope-excluded ops (:enter-structure,
            :authorize-structure-entry, :commit-to-structure-entry,
            :decide-structure-entry, :perform-triage,
            :make-triage-decision, :prioritize-casualty-rescue,
            :prioritize-rescue, :provide-medical-treatment,
            :administer-medical-treatment, :issue-tactical-command,
            :direct-incident-response, :issue-incident-command-order)
            through the full langgraph.graph StateGraph always resolves
            to :hold with zero records committed. :flag-readiness-
            concern always escalates and is never auto-commit-eligible;
            an over-cost-ceiling :coordinate-supply-order escalates
            (not a hard block -- routine procurement above a station's
            registered threshold, not a tactical-authority risk). One
            of the three highest-stakes occupation actors landed in
            this session's batch (alongside ISCO-5412 Police Officers,
            ISCO-5413 Prison Guards). 43 tests / 150 assertions green
            (cloud-itonami-isco-5411, ADR-2799005411). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main rather than hand-derived
            from a prior comment's delta."
    (is (= :implemented (occupation/maturity "5411")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-5411"
           (:repo (occupation/get-occupation "5411"))))
    (is (= "cloud-itonami-isco-5411"
           (:business-id (occupation/get-occupation "5411"))))))

(deftest locomotive-engine-drivers-8311-implemented
  (testing "8311 (Locomotive Engine Drivers) promoted to :implemented --
            RailCrewActor (Rail Crew Advisor ⊣ RailCrewGovernor);
            closed four-op proposal allowlist (:log-service-record,
            :schedule-crew-operation, :flag-safety-concern,
            :coordinate-maintenance-order) -- a rail-crew
            scheduling/logistics-coordination robot ONLY, never direct
            locomotive-operation authority. This actor has ZERO
            locomotive-movement/throttle/brake-control or
            signal-override/stop-signal-departure authority: no op
            resembling either category exists anywhere in the
            allowlist (structurally absent, not merely gated),
            confirmed by the governor's closed op-allowlist HARD check
            plus a content-based scope-exclusion HARD block phrased as
            finalization/execution ACTIONS (never bare nouns, e.g.
            \"initiate the locomotive movement\", \"override the
            stop signal\", \"depart against the stop signal\") --
            verified via a dedicated regression test that the default
            mock advisor's proposals for all four ops never self-trip
            it. :flag-safety-concern always escalates and is never
            auto-commit-eligible; an over-ceiling
            :coordinate-maintenance-order escalates -- not a hard
            block, a legitimate business action that just requires
            sign-off, unlike a locomotive-movement/signal-override
            decision. 22 tests / 48 assertions green
            (cloud-itonami-isco-8311, ADR-2799008311). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main rather than hand-derived
            from a prior comment's delta."
    (is (= :implemented (occupation/maturity "8311")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-8311"
           (:repo (occupation/get-occupation "8311"))))
    (is (= "cloud-itonami-isco-8311"
           (:business-id (occupation/get-occupation "8311"))))))

(deftest mobile-farm-forestry-plant-operators-8341-implemented
  (testing "8341 (Mobile Farm and Forestry Plant Operators) promoted to
            :implemented -- FarmForestryOpsActor (Farm/Forestry Ops
            Advisor ⊣ FarmForestryOpsGovernor); closed four-op proposal
            allowlist (:log-service-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-maintenance-order) -- a
            scheduling/logistics coordination robot ONLY. Mobile farm
            and forestry plant operators run heavy equipment (tractors,
            harvesters, forestry skidders) with real physical-safety
            stakes (rollover, entanglement), so this actor has NO
            equipment-operation/movement-finalization or
            operator-safety-judgment-override authority anywhere in its
            allowlist -- structurally absent, not merely gated -- backed
            by a content-based scope-exclusion HARD block phrased
            exclusively as finalization/execution ACTIONS (e.g.
            \"initiate the equipment movement\", \"override the
            operator's on-site safety judgment\"), never bare nouns like
            \"equipment\"/\"movement\", so it cannot self-trip on the
            mock advisor's own legitimate rationale (which necessarily
            names equipment by id) -- verified via a dedicated
            regression test that the default mock advisor's proposals
            for all four ops never self-trip it, plus an end-to-end
            actor-level test that a rogue advisor forcing an
            equipment-movement-finalizing rationale through the full
            langgraph.graph StateGraph always resolves to :hold with
            zero records committed. :flag-safety-concern always
            escalates and is never auto-commit-eligible; an
            above-cost-threshold :coordinate-maintenance-order
            escalates (not a hard block -- routine over-budget
            procurement, not a safety risk). 19 tests / 44 assertions
            green (cloud-itonami-isco-8341, ADR-2799008341). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main rather than hand-derived
            from a prior comment's delta."
    (is (= :implemented (occupation/maturity "8341")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-8341"
           (:repo (occupation/get-occupation "8341"))))
    (is (= "cloud-itonami-isco-8341"
           (:business-id (occupation/get-occupation "8341"))))))

(deftest cloud-itonami-isco-8321-motorcycle-drivers-promoted-to-implemented
  (testing "cloud-itonami-isco-8321 (Motorcycle Drivers) promoted to
            :implemented -- MotorcycleDispatchActor (Motorcycle Dispatch
            Advisor ⊣ MotorcycleDispatchGovernor); a dispatch/
            logistics-SCHEDULING-COORDINATION ONLY actor with NO route/
            traffic-navigation-finalization or rider-safety-judgment-
            override authority anywhere in its closed four-op allowlist
            (:log-delivery-record, :schedule-dispatch-operation,
            :flag-safety-concern, :coordinate-maintenance-order) -- this
            actor never operates the motorcycle. A route/traffic-
            navigation finalization or rider-safety-judgment override is
            a hard, permanent block regardless of confidence or op.
            :flag-safety-concern always escalates and is never
            auto-commit-eligible; an above-cost-threshold
            :coordinate-maintenance-order always escalates. 22 tests / 49
            assertions green (cloud-itonami-isco-8321, ADR-2799008321)."
    (is (= :implemented (occupation/maturity "8321")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-8321"
           (:repo (occupation/get-occupation "8321"))))
    (is (= "cloud-itonami-isco-8321"
           (:business-id (occupation/get-occupation "8321"))))))

(deftest lifting-truck-operators-8344-implemented
  (testing "8344 (Lifting Truck Operators) promoted to :implemented --
            LiftingTruckOpsActor (Warehouse Logistics Advisor ⊣
            LiftingTruckOpsGovernor); closed four-op proposal allowlist
            (:log-service-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-maintenance-order) -- a
            warehouse-crew scheduling/logistics-coordination robot
            ONLY, never direct lift-operation authority. This actor
            has NO op, anywhere in its allowlist, that resembles
            operating a lifting truck or finalizing a load movement:
            structurally absent, not merely gated, confirmed by the
            governor's closed op-allowlist HARD check plus a
            content-based scope-exclusion HARD block phrased as
            finalization/execution ACTIONS (never bare nouns, e.g.
            \"initiate the lift operation\", \"move the load\",
            \"override the operator's safety judgment\") -- verified
            via a dedicated regression test that the default mock
            advisor's proposals for all four ops never self-trip it,
            plus an end-to-end rogue-advisor test driving the full
            compiled langgraph.graph StateGraph proving every
            forbidden op/rationale/detail hard-blocks even when the
            advisor node itself is compromised.
            :flag-safety-concern always escalates and is never
            auto-commit-eligible; a :coordinate-maintenance-order
            above the cited equipment's own registered
            :max-maintenance-cost escalates -- not a hard block, a
            legitimate business action that just requires sign-off,
            unlike a lift-operation/load-movement decision. 27 tests /
            76 assertions green (cloud-itonami-isco-8344,
            ADR-2799008344). This ADR/registry edit is a RETRY
            completion: a prior attempt was cut off by a session rate
            limit after creating the repo but before pushing any
            source; a concurrent sibling agent in this retry batch
            pushed the actor implementation, independently re-cloned,
            read in full and re-tested from a clean checkout before
            this registry promotion. Counts re-verified live via
            (occupation/maturity-summary) against a freshly re-fetched
            origin/main immediately before this edit, reflecting
            cumulative concurrent sibling landings in this same batch,
            not solely this ADR's own +1."
    (is (= :implemented (occupation/maturity "8344")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-8344"
           (:repo (occupation/get-occupation "8344"))))
    (is (= "cloud-itonami-isco-8344"
           (:business-id (occupation/get-occupation "8344"))))))

(deftest railway-brake-signal-switch-operators-8312-implemented
  (testing "8312 (Railway Brake, Signal and Switch Operators) promoted to
            :implemented -- RailSignalCrewActor (Rail Signal Crew Advisor
            ⊣ RailSignalCrewGovernor); closed four-op proposal allowlist
            (:log-service-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-maintenance-order) -- an
            administrative/logistics-scheduling coordination robot ONLY,
            never direct switch/signal-operation authority. This actor
            has ZERO switch-throw/signal-clearance/track-routing
            finalization authority: no op resembling any of those
            categories exists anywhere in the allowlist (structurally
            absent, not merely gated), confirmed by the governor's closed
            op-allowlist HARD check plus a content-based
            :finalizes-switch-or-signal HARD block scoped to a dedicated
            :action-text field and phrased exclusively as
            finalization/execution ACTIONS (never bare nouns -- e.g.
            \"throw the track switch\", \"clear the signal\", \"set the
            route\", never bare \"switch\"/\"signal\"/\"track\") --
            verified via a dedicated regression test that the default
            mock advisor's proposals for all four ops, against a section
            literally named \"Switch and Signal Box 4\", never self-trip
            it. :flag-safety-concern always escalates and is never
            auto-commit-eligible; a :coordinate-maintenance-order whose
            cost exceeds the cited section's registered
            :max-maintenance-cost ceiling escalates -- not a hard block,
            a legitimate business action that just requires sign-off,
            unlike a switch/signal-finalization decision which is always
            a hard, permanent refusal with no approval path. 21 tests /
            49 assertions green (cloud-itonami-isco-8312,
            ADR-2799008312). Counts re-verified live via
            (occupation/maturity-summary) against a freshly re-fetched
            origin/main rather than hand-derived from a prior comment's
            delta."
    (is (= :implemented (occupation/maturity "8312")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-8312"
           (:repo (occupation/get-occupation "8312"))))
    (is (= "cloud-itonami-isco-8312"
           (:business-id (occupation/get-occupation "8312"))))))

(deftest ships-deck-crews-8350-implemented
  (testing "8350 (Ships' Deck Crews and Related Workers) promoted to
            :implemented -- DeckCrewActor (Deck Crew Advisor ⊣
            DeckCrewGovernor); closed four-op proposal allowlist
            (:log-service-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a
            crew-scheduling/equipment-logistics coordination robot
            ONLY. This actor has NO authority to directly finalize a
            mooring/cargo-handling operational decision, decide a
            heavy-weather deck-work go/no-go, or override a deck
            officer's safety judgment: no such op exists anywhere in
            the closed allowlist (structurally absent, not merely
            gated), backed by a content-based scope-exclusion HARD
            block phrased as finalization/execution ACTIONS (never bare
            nouns, e.g. \"initiate the mooring operation\", \"commence
            deck work in heavy weather\", \"override the deck officer's
            safety judgment\") -- verified via a dedicated regression
            test that the default mock advisor's proposals for all four
            ops (including ones whose default rationale mentions
            \"mooring\"/\"weather\" bare nouns in ordinary in-scope
            proposals, e.g. ordering mooring lines or flagging a
            weather hazard) never self-trip the scope-exclusion check.
            :flag-safety-concern always escalates and is never
            auto-commit-eligible; an over-threshold
            :coordinate-supply-order escalates (not a hard block --
            routine procurement above the registered cost threshold,
            not itself unsafe). This actor coordinates crew
            scheduling/logistics ONLY -- it never performs a deck
            operation itself. 20 tests / 45 assertions green
            (cloud-itonami-isco-8350, ADR-2799008350). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main immediately before this
            edit, not hand-derived from a prior comment's delta."
    (is (= :implemented (occupation/maturity "8350")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-8350"
           (:repo (occupation/get-occupation "8350"))))
    (is (= "cloud-itonami-isco-8350"
           (:business-id (occupation/get-occupation "8350"))))))

(deftest mining-quarrying-labourers-9311-implemented
  (testing "9311 (Mining and Quarrying Labourers) promoted to
            :implemented -- MiningLaborActor (Mining Labor Advisor ⊣
            MiningLaborGovernor); closed four-op proposal allowlist
            (:log-labor-record, :schedule-labor-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a
            labor-scheduling/logistics coordination robot ONLY, never
            direct mine/quarry-site-operation authority. Mining and
            quarrying labourers work in a hazardous physical
            environment (cave-in risk, blast-zone proximity,
            heavy-equipment proximity, dust/gas exposure), so this
            actor has ZERO blast-authorization, mine-entry/
            area-clearance, or site-safety-officer-judgment-override
            authority: no op resembling any of those three categories
            exists anywhere in the allowlist (structurally absent, not
            merely gated), confirmed by the governor's closed
            op-allowlist HARD check, a second independent HARD check
            naming five concretely-forbidden ops
            (:authorize-blast, :finalize-blast-authorization,
            :clear-area-for-mine-entry, :authorize-mine-entry,
            :override-site-safety-officer), and a content-based
            scope-exclusion HARD block phrased as finalization/
            execution ACTIONS (never bare nouns, e.g. \"authorize the
            blast\", \"clear the area for mine entry\", \"override
            the site safety officer\") -- verified via a dedicated
            regression test that the default mock advisor's proposals
            for all four ops never self-trip it, even though the
            advisor's own default rationale text legitimately contains
            the bare nouns \"entry\"/\"blast\"/\"officer\" (e.g.
            \"logged shift entry\", \"blast-adjacent task\", \"site
            safety officer review\"). :flag-safety-concern always
            escalates and is never auto-commit-eligible; an
            above-cost-threshold :coordinate-supply-order escalates --
            not a hard block, routine procurement above the registered
            threshold, not itself unsafe unlike a blast-authorization/
            mine-entry-clearance attempt. 17 tests / 36 assertions
            green (cloud-itonami-isco-9311, ADR-2799009311). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main immediately before this
            edit, reflecting cumulative concurrent sibling landings in
            this same retry batch, not solely this promotion's own
            +1; the maturity-tier test's 197 spec / 239 implemented
            already reflected this promotion at the time of this
            addition, so no further numeric bump was needed here."
    (is (= :implemented (occupation/maturity "9311")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-9311"
           (:repo (occupation/get-occupation "9311"))))
    (is (= "cloud-itonami-isco-9311"
           (:business-id (occupation/get-occupation "9311"))))))

(deftest house-builders-7111-implemented
  (testing "7111 (House Builders) promoted to :implemented --
            HouseBuilderActor (House Builder Advisor ⊣
            HouseBuilderGovernor); closed four-op proposal allowlist
            (:log-work-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a
            job-site scheduling/logistics coordination robot ONLY,
            never direct construction-work-execution authority. House
            Builders perform structural construction work on active
            job sites (falls, structural collapse, equipment injury
            risk), so this actor has ZERO authority to directly
            finalize a structural-work-execution decision (e.g.
            proceeding with a specific framing or foundation step) or
            override a site safety officer's/foreman's judgment: no
            such op exists anywhere in the closed allowlist
            (structurally absent, not merely gated), confirmed by the
            governor's closed op-allowlist HARD check, a second
            independent HARD check naming five concretely-forbidden
            ops (:finalize-framing-decision,
            :authorize-foundation-pour, :proceed-with-structural-work,
            :override-safety-officer-judgment,
            :override-foreman-judgment), and a content-based
            scope-exclusion HARD block phrased as finalization/
            execution ACTIONS (never bare nouns, e.g. \"proceed with
            the framing work\", \"authorize the foundation pour\",
            \"override the foreman's safety judgment\") -- verified
            via a dedicated regression test that the default mock
            advisor's proposals for all four ops never self-trip it,
            even though the advisor's own default rationale text
            legitimately contains the bare nouns \"framing\"/
            \"foundation\"/\"safety officer\" (e.g. \"scheduled crew
            operation for framing task\", \"routed for site safety
            officer review\"). :flag-safety-concern always escalates
            and is never auto-commit-eligible; an
            above-cost-threshold :coordinate-supply-order escalates --
            not a hard block, routine procurement above the registered
            threshold, not itself unsafe unlike a
            structural-work-execution or safety-officer-override
            attempt. 21 tests / 45 assertions green
            (cloud-itonami-isco-7111, ADR-2799007111). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main immediately before this
            edit, reflecting cumulative concurrent sibling landings in
            this same retry batch (the maturity-tier test's 195 spec /
            241 implemented above already reflects this promotion, not
            hand-derived from a prior comment's delta)."
    (is (= :implemented (occupation/maturity "7111")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7111"
           (:repo (occupation/get-occupation "7111"))))
    (is (= "cloud-itonami-isco-7111"
           (:business-id (occupation/get-occupation "7111"))))))

(deftest building-frame-trades-7119-implemented
  (testing "7119 (Building Frame and Related Trades Workers Not
            Elsewhere Classified) promoted to :implemented --
            FramingCrewActor (Framing Crew Advisor ⊣
            FramingCrewGovernor); closed four-op proposal allowlist
            (:log-work-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a
            job-site scheduling/logistics coordination robot ONLY,
            never direct framing-work-execution authority. Building
            Frame Trades Workers perform structural framing
            construction work on active job sites (falls, structural
            collapse, equipment injury risk), so this actor has ZERO
            authority to directly finalize a
            structural-framing-execution decision or override a site
            safety officer's judgment: no such op exists anywhere in
            the closed allowlist (structurally absent, not merely
            gated), confirmed by the governor's closed op-allowlist
            HARD check (:unknown-op), a second independent HARD check
            naming seven concretely-forbidden ops
            (:finalize-framing-execution, :authorize-structural-work,
            :proceed-with-framing-work, :commit-framing-decision,
            :finalize-structural-framing-decision,
            :override-safety-officer-judgment,
            :override-site-safety-officer-judgment)
            (:scope-excluded-op), and a content-based scope-exclusion
            HARD block (:scope-excluded-rationale) phrased as
            finalization/execution ACTIONS (never bare nouns, e.g.
            \"proceed with the framing work\", \"override the site
            safety officer's judgment\") -- verified via a dedicated
            regression test that the default mock advisor's proposals
            for all four ops never self-trip it, even though this
            actor's own :flag-safety-concern op literally contains the
            bare noun \"safety\". :flag-safety-concern always
            escalates and is never auto-commit-eligible; an
            above-ceiling :coordinate-supply-order escalates -- not a
            hard block, routine procurement above the crew's
            registered cost ceiling, not itself unsafe unlike a
            structural-framing-execution or safety-officer-override
            attempt. 20 tests / 54 assertions green
            (cloud-itonami-isco-7119, ADR-2799007119). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main immediately before this
            edit: 195 -> 194 spec / 241 -> 242 implemented (this
            promotion only -- no other concurrent sibling landed
            between the prior comment above and this edit)."
    (is (= :implemented (occupation/maturity "7119")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7119"
           (:repo (occupation/get-occupation "7119"))))
    (is (= "cloud-itonami-isco-7119"
           (:business-id (occupation/get-occupation "7119"))))))



(deftest roofers-7121-implemented
  (testing "7121 (Roofers) promoted to :implemented -- RoofCoordActor
            (Roof Coordination Advisor ⊣ RoofCoordGovernor); closed
            four-op proposal allowlist (:log-work-record,
            :schedule-crew-operation, :flag-safety-concern,
            :coordinate-supply-order) -- a job-site
            scheduling/logistics coordination robot ONLY, never direct
            roofing-work authority. Roofers work at height on active
            job sites, one of the highest fall-risk trades: this actor
            has ZERO authority to finalize a roofing-work-execution
            decision or override a site-safety officer's
            fall-protection judgment -- no op resembling either
            category exists anywhere in the allowlist (structurally
            absent, not merely gated), confirmed by the governor's
            closed op-allowlist HARD check, a site-mismatch
            defense-in-depth check, independently-verified site/worker
            provenance, and a content-based scope-exclusion HARD block
            phrased as finalization/execution ACTIONS (never bare
            nouns, e.g. \"proceed with the roof-work\", \"override
            the site-safety officer\") -- verified via a dedicated
            regression test that the default mock advisor's proposals
            for all four ops never self-trip it, even though the
            advisor's own default rationale/description text
            legitimately contains the bare nouns \"roof\"/\"height\"
            (e.g. a safety-concern flag describing a hazard \"near the
            roof edge at height\"). :flag-safety-concern always
            escalates and is never auto-commit-eligible; an
            above-cost-threshold :coordinate-supply-order escalates --
            not a hard block, routine procurement above the registered
            threshold, not itself unsafe unlike a
            roofing-work-execution or fall-protection-override
            attempt. 22 tests / 52 assertions green
            (cloud-itonami-isco-7121, ADR-2799007121). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main immediately before this
            edit."
    (is (= :implemented (occupation/maturity "7121")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7121"
           (:repo (occupation/get-occupation "7121"))))
    (is (= "cloud-itonami-isco-7121"
           (:business-id (occupation/get-occupation "7121"))))))

(deftest bricklayers-7112-implemented
  (testing "7112 (Bricklayers and Related Workers) promoted to :implemented --
            BricklayingActor (Bricklaying Advisor ⊣
            BricklayingGovernor); closed four-op proposal allowlist
            (:log-work-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a
            job-site scheduling/logistics coordination robot ONLY,
            never direct masonry-work-execution authority. Bricklayers
            perform structural/masonry construction work on active job
            sites (falling materials, wall collapse, scaffold hazard
            risk), so this actor has ZERO authority to directly
            finalize a masonry-work-execution decision (e.g.
            proceeding with a specific wall-laying or mortar-joint
            step) or override a site safety officer's/foreman's
            judgment: no such op exists anywhere in the closed
            allowlist (structurally absent, not merely gated),
            confirmed by the governor's closed op-allowlist HARD
            check, a second independent HARD check naming five
            concretely-forbidden ops
            (:finalize-masonry-work-decision,
            :authorize-wall-construction, :proceed-with-masonry-work,
            :override-safety-officer-judgment,
            :override-foreman-judgment), and a content-based
            scope-exclusion HARD block phrased as finalization/
            execution ACTIONS (never bare nouns, e.g. \"proceed with
            the masonry work\", \"authorize the wall construction\",
            \"override the foreman's safety judgment\") -- verified
            via a dedicated regression test that the default mock
            advisor's proposals for all four ops never self-trip it,
            even though the advisor's own default rationale text
            legitimately contains the bare nouns \"masonry\"/\"wall\"/
            \"safety officer\" (e.g. \"scheduled crew operation for
            masonry task\", \"routed for site safety officer
            review\"). :flag-safety-concern always escalates and is
            never auto-commit-eligible; an above-cost-threshold
            :coordinate-supply-order escalates -- not a hard block,
            routine procurement above the registered threshold, not
            itself unsafe unlike a masonry-work-execution or
            safety-officer-override attempt. 21 tests / 45 assertions
            green (cloud-itonami-isco-7112, ADR-2799007112). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main immediately before this
            edit, reflecting cumulative concurrent sibling landings in
            this same retry batch (the maturity-tier test's counts
            above already reflect this promotion, not hand-derived
            from a prior comment's delta)."
    (is (= :implemented (occupation/maturity "7112")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7112"
           (:repo (occupation/get-occupation "7112"))))
    (is (= "cloud-itonami-isco-7112"
           (:business-id (occupation/get-occupation "7112"))))))
(deftest stonemasons-7113-implemented
  (testing "7113 (Stonemasons, Stone Cutters, Splitters and Carvers)
            promoted to :implemented -- StonemasonryActor (Workshop
            Coordination Advisor ⊣ StonemasonryGovernor); closed
            four-op proposal allowlist (:log-work-record,
            :schedule-crew-operation, :flag-safety-concern,
            :coordinate-supply-order) -- a WORKSHOP SCHEDULING/
            LOGISTICS COORDINATION robot ONLY, never direct
            stonework-execution authority. Stonemasons operate
            cutting/carving tools and handle heavy stone materials
            (silica dust exposure, blade injury, material-handling
            strain risk), so this actor has ZERO authority to
            directly finalize a stone-cutting/carving-execution
            decision or override a workshop safety officer's
            judgment: no such op exists anywhere in the closed
            allowlist (structurally absent, not merely gated),
            confirmed by the governor's closed op-allowlist HARD
            check, a second independent HARD check naming ten
            concretely-forbidden ops
            (:finalize-stone-cutting-execution,
            :finalize-carving-execution, :commit-to-the-cut,
            :proceed-with-the-cut, :execute-stone-cutting-operation,
            :execute-carving-operation,
            :override-safety-officer-judgment,
            :override-workshop-safety-officer-decision,
            :authorize-unsafe-operation, :bypass-safety-hold), and a
            content-based scope-exclusion HARD block phrased as
            finalization/execution ACTIONS (never bare nouns, e.g.
            \"proceed with the stone-cutting work\", \"override the
            workshop safety officer's judgment\") -- verified via a
            dedicated regression test that the default mock advisor's
            proposals for all four ops never self-trip it, even
            though the advisor's own default rationale text
            legitimately contains the bare nouns \"cutting\"/
            \"carving\"/\"safety\" (e.g. \"cut to spec, polished\",
            \"flagging a safety concern for review\"). Two-sided
            provenance (independently registered AND verified
            workshop AND craftsperson), a daily task-hour scheduling
            ceiling HARD block (overwork/fatigue risk, inclusive
            boundary), and two content-shape forbidden-key guardrails
            (work-record-decision, schedule-override) round out the
            nine HARD checks. :flag-safety-concern always escalates
            and is never auto-commit-eligible; an
            above-cost-threshold :coordinate-supply-order escalates
            -- not a hard block, routine stone-materials procurement
            above the registered threshold, not itself unsafe unlike
            a stone-cutting/carving-execution or safety-officer-
            override attempt. Rogue-advisor end-to-end tests prove
            the full compiled StateGraph always resolves to :hold
            with zero records committed for every one of the ten
            forbidden ops, a scope-excluded rationale, a smuggled
            work-record-decision key, and a smuggled schedule-
            override key -- the governor, not the advisor, enforces
            safety. 31 tests / 106 assertions green
            (cloud-itonami-isco-7113, ADR-2799007113). Counts
            re-verified live via (occupation/maturity-summary)
            against a freshly re-fetched origin/main immediately
            before this edit, reflecting cumulative concurrent
            sibling landings in this same retry batch (the
            maturity-tier test's spec/implemented split above
            already reflects this promotion, not hand-derived from a
            prior comment's delta)."
    (is (= :implemented (occupation/maturity "7113")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7113"
           (:repo (occupation/get-occupation "7113"))))
    (is (= "cloud-itonami-isco-7113"
           (:business-id (occupation/get-occupation "7113"))))))

(deftest concrete-placers-7114-implemented
  (testing "7114 (Concrete Placers, Concrete Finishers and Related
            Workers) promoted to :implemented -- ConcreteCrewActor
            (Concrete Crew Coordination Advisor ⊣ ConcreteCrewGovernor);
            closed four-op proposal allowlist (:log-work-record,
            :schedule-crew-operation, :flag-safety-concern,
            :coordinate-supply-order) -- a job-site scheduling/logistics
            coordination robot ONLY, never direct concrete-work
            authority. Concrete Placers and Finishers pour and finish
            concrete on active job sites (material-handling and
            pour-timing coordination stakes, effectively irreversible
            once poured), so this actor has ZERO authority to directly
            finalize a concrete-pour/finishing-execution decision or
            override a site-safety officer's judgment: no such op
            exists anywhere in the closed allowlist (structurally
            absent, not merely gated), confirmed by the governor's
            closed op-allowlist HARD check (:op-not-allowlisted), a
            second independent content-based scope-exclusion HARD check
            (:scope-exclusion) phrased as finalization/execution
            ACTIONS (never bare nouns, e.g. \"proceed with the concrete
            pour\", \"override the site safety officer's judgment\"),
            and independently-verified site/worker provenance HARD
            checks (:site-not-verified, :worker-not-verified -- a
            registered record alone is not enough) -- verified via a
            dedicated regression test that the default mock advisor's
            proposals for all four ops never self-trip the
            scope-exclusion guard, even though this actor's own
            vocabulary legitimately contains the bare nouns \"pour\" and
            \"safety\" (e.g. a :log-work-record task \"pour-timing update
            for footing pour\", a :flag-safety-concern concern \"site
            safety inspection pending before pour\"). :flag-safety-concern
            always escalates and is never auto-commit-eligible; a
            :coordinate-supply-order above the site's registered
            per-site cost ceiling escalates -- not a hard block, routine
            procurement above the registered threshold, not itself
            unsafe unlike a pour-finalization or
            safety-officer-override attempt. 25 tests / 55 assertions
            green (cloud-itonami-isco-7114, ADR-2799007114). Counts
            re-verified live via (occupation/maturity-summary) against a
            freshly re-fetched origin/main immediately before this
            edit."
    (is (= :implemented (occupation/maturity "7114")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7114"
           (:repo (occupation/get-occupation "7114"))))
    (is (= "cloud-itonami-isco-7114"
           (:business-id (occupation/get-occupation "7114"))))))
(deftest plasterers-7123-implemented
  (testing "7123 (Plasterers) promoted to :implemented -- PlastererActor
            (Plasterer Advisor ⊣ PlastererGovernor); closed four-op
            proposal allowlist (:log-work-record,
            :schedule-crew-operation, :flag-safety-concern,
            :coordinate-supply-order) -- a job-site scheduling/logistics
            coordination robot ONLY, never direct plastering-execution
            authority. Plasterers apply wall/ceiling finishing materials
            on active job sites (dust-exposure, scaffold-hazard and
            material-handling stakes), so this actor has ZERO authority
            to directly finalize a plastering-execution decision or
            override a site safety officer's judgment: no such op
            exists anywhere in the closed allowlist (structurally
            absent, not merely gated), confirmed by the governor's
            closed op-allowlist HARD check (:unknown-op), a second
            independent content-based scope-exclusion HARD check
            (:scope-excluded-action) phrased as finalization/execution
            ACTIONS (never bare nouns, e.g. \"proceed with the
            plastering work\", \"override the site safety officer's
            judgment\"), and independently-verified plasterer/site
            provenance HARD checks (:no-plasterer, :no-site -- a
            registered record alone is not enough) -- verified via a
            dedicated regression test that the default mock advisor's
            proposals for all four ops never self-trip the
            scope-exclusion guard, even though this actor's own
            vocabulary legitimately contains the bare nouns
            \"plastering\" and \"safety\" (e.g. a :log-work-record task
            \"routine plastering task\", a :flag-safety-concern concern
            routed for site safety officer review). :flag-safety-concern
            always escalates and is never auto-commit-eligible; a
            :coordinate-supply-order above the registered cost
            threshold escalates -- not a hard block, routine
            procurement above the registered threshold, not itself
            unsafe unlike a plastering-execution or
            safety-officer-override attempt. 21 tests / 45 assertions
            green (cloud-itonami-isco-7123, ADR-2799007123). Counts
            re-verified live via (occupation/maturity-summary) against a
            freshly re-fetched origin/main immediately before this
            edit, reflecting cumulative concurrent sibling landings in
            this same batch (the maturity-tier test's spec/implemented
            split above already reflects this promotion, not
            hand-derived from a prior comment's delta)."
    (is (= :implemented (occupation/maturity "7123")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7123"
           (:repo (occupation/get-occupation "7123"))))
    (is (= "cloud-itonami-isco-7123"
           (:business-id (occupation/get-occupation "7123"))))))


(deftest glaziers-7125-implemented
  (testing "7125 (Glaziers) promoted to :implemented -- GlazierActor
            (Glazier Advisor ⊣ GlazierGovernor); closed four-op
            proposal allowlist (:log-work-record,
            :schedule-crew-operation, :flag-safety-concern,
            :coordinate-supply-order) -- a job-site scheduling/logistics
            coordination robot ONLY, never direct glazing-installation
            authority. Glaziers install glass/glazing on active job
            sites (glass-handling cuts, height-work for building
            facades, heavy-panel handling stakes), so this actor has
            ZERO authority to directly finalize a
            glazing-installation-execution decision or override a site
            safety officer's judgment: no such op exists anywhere in
            the closed allowlist (structurally absent, not merely
            gated), confirmed by the governor's closed op-allowlist
            HARD check (:unknown-op), a second independent
            content-based scope-exclusion HARD check
            (:scope-excluded-action) phrased as finalization/execution
            ACTIONS (never bare nouns, e.g. \"proceed with the glazing
            installation\", \"override the site safety officer's
            judgment\"), and independently-verified glazier/site
            provenance HARD checks (:no-glazier, :no-site -- a
            registered record alone is not enough) -- verified via a
            dedicated regression test that the default mock advisor's
            proposals for all four ops never self-trip the
            scope-exclusion guard, even though this actor's own
            vocabulary legitimately contains the bare nouns \"glass\",
            \"panel\" and \"safety\" (e.g. a :schedule-crew-operation
            rationale \"scheduled crew operation for glazing
            installation task\", a :flag-safety-concern rationale
            \"routed for site safety officer review\").
            :flag-safety-concern always escalates and is never
            auto-commit-eligible; a :coordinate-supply-order above the
            registered per-site cost ceiling (2000) escalates -- not a
            hard block, routine glazing-materials procurement above
            the registered threshold, not itself unsafe unlike a
            glazing-installation-execution or safety-officer-override
            attempt. 22 tests / 47 assertions green
            (cloud-itonami-isco-7125, ADR-2799007125). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main immediately before this
            edit."
    (is (= :implemented (occupation/maturity "7125")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7125"
           (:repo (occupation/get-occupation "7125"))))
    (is (= "cloud-itonami-isco-7125"
           (:business-id (occupation/get-occupation "7125"))))))

(deftest ac-refrigeration-mechanics-7127-implemented
  (testing "7127 (Air Conditioning and Refrigeration Mechanics) promoted
            to :implemented -- HvacMechActor (HVAC Mechanic Advisor ⊣
            HvacMechGovernor); closed four-op proposal allowlist
            (:log-service-record, :schedule-service-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a service
            scheduling/logistics coordination robot ONLY, never direct
            refrigeration-system-service-execution authority. AC and
            Refrigeration Mechanics service pressurized refrigerant
            systems (refrigerant exposure, pressurized-system and
            electrical hazards), so this actor has ZERO authority to
            directly finalize a refrigerant-system-service-execution
            decision or override/bypass a technician-certification
            requirement: no such op exists anywhere in the closed
            allowlist (structurally absent, not merely gated), confirmed
            by the governor's closed op-allowlist HARD check
            (:unknown-op), a second independent content-based
            scope-exclusion HARD check (:scope-excluded-action) phrased
            as finalization/execution ACTIONS (never bare nouns, e.g.
            \"proceed with the refrigerant-system service\", \"override
            the technician-certification requirement\"), and
            independently-verified technician/service-account
            provenance HARD checks (:no-technician, :no-service-account
            -- a registered record alone is not enough, technician
            registration includes certification status) -- verified via
            a dedicated regression test that the default mock advisor's
            proposals for all four ops never self-trip the
            scope-exclusion guard, even though this actor's own
            vocabulary legitimately contains the bare nouns
            \"refrigerant\", \"certification\" and \"technician\" (e.g. a
            :schedule-service-operation task \"refrigerant diagnostic
            task\", a :flag-safety-concern concern routed for certified
            technician review). :flag-safety-concern always escalates
            and is never auto-commit-eligible; a :coordinate-supply-order
            above the registered cost threshold escalates -- not a hard
            block, routine refrigerant/parts procurement above the
            registered threshold, not itself unsafe unlike a
            refrigerant-system-service-execution or
            technician-certification-override/bypass attempt. 22 tests /
            47 assertions green (cloud-itonami-isco-7127,
            ADR-2799007127). Counts re-verified live via
            (occupation/maturity-summary) against a freshly re-fetched
            origin/main immediately before this edit."
    (is (= :implemented (occupation/maturity "7127")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7127"
           (:repo (occupation/get-occupation "7127"))))
    (is (= "cloud-itonami-isco-7127"
           (:business-id (occupation/get-occupation "7127"))))))
(deftest painters-7131-implemented
  (testing "7131 (Painters and Related Workers) promoted to :implemented --
            PaintCrewActor (Paint Crew Advisor ⊣ PaintCrewGovernor); closed
            four-op proposal allowlist (:log-work-record,
            :schedule-crew-operation, :flag-safety-concern,
            :coordinate-supply-order) -- a job-site scheduling/logistics
            coordination robot ONLY, never direct painting-execution
            authority. Painters apply coatings/finishes on active job
            sites (solvent/fume exposure, height-work for exteriors,
            ventilation-requirement stakes), so this actor has ZERO
            authority to directly finalize a painting-execution decision
            or override a site safety officer's judgment: no such op
            exists anywhere in the closed allowlist (structurally
            absent, not merely gated), confirmed by the governor's
            closed op-allowlist HARD check (:unknown-op), a second
            independent content-based scope-exclusion HARD check
            (:scope-excluded-action) phrased as finalization/execution
            ACTIONS (never bare nouns, e.g. \"proceed with the painting
            work\", \"override the site safety officer's judgment\"),
            and independently-verified painter/site provenance HARD
            checks (:no-painter, :no-site -- a registered record alone
            is not enough) -- verified via a dedicated regression test
            that the default mock advisor's proposals for all four ops
            never self-trip the scope-exclusion guard, even though this
            actor's own vocabulary legitimately contains the bare nouns
            \"paint\", \"coating\" and \"safety\" (e.g. a
            :schedule-crew-operation rationale \"scheduled crew
            operation for coating task\", a :flag-safety-concern
            rationale \"routed for site safety officer review\").
            :flag-safety-concern always escalates and is never
            auto-commit-eligible; a :coordinate-supply-order above the
            registered per-site cost ceiling (2000) escalates -- not a
            hard block, routine paint/coating-materials procurement
            above the registered threshold, not itself unsafe unlike a
            painting-execution or safety-officer-override attempt. 21
            tests / 45 assertions green (cloud-itonami-isco-7131,
            ADR-2799007131). Counts re-verified live via
            (occupation/maturity-summary) against a freshly re-fetched
            origin/main immediately before this edit."
    (is (= :implemented (occupation/maturity "7131")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7131"
           (:repo (occupation/get-occupation "7131"))))
    (is (= "cloud-itonami-isco-7131"
           (:business-id (occupation/get-occupation "7131"))))))

(deftest insulation-workers-7124-implemented
  (testing "7124 (Insulation Workers) promoted to :implemented --
            InsulationCrewActor (Insulation Crew Advisor ⊣
            InsulationCrewGovernor); closed four-op proposal allowlist
            (:log-work-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a
            job-site scheduling/logistics coordination robot ONLY,
            never direct insulation-installation-execution authority.
            Insulation Workers install thermal/acoustic insulation
            materials on active job sites (fiber/particulate exposure,
            confined-space work, ventilation hazards), so this actor
            has ZERO authority to directly finalize an
            insulation-installation-execution decision or override a
            site safety officer's judgment -- no op resembling either
            category exists anywhere in the allowlist (structurally
            absent, not merely gated), confirmed by the governor's
            closed op-allowlist HARD check, independently-verified
            worker/site provenance, and a content-based scope-exclusion
            HARD block phrased as finalization/execution ACTIONS (never
            bare nouns, e.g. \"proceed with the insulation
            installation\", \"override the site safety officer's
            judgment\") -- verified via a dedicated regression test
            that the default mock advisor's proposals for all four ops
            never self-trip the scope-exclusion guard, even though this
            actor's own vocabulary legitimately contains the bare nouns
            \"insulation\" and \"safety officer\" (e.g. a
            :log-work-record task \"routine insulation task\", a
            :flag-safety-concern concern routed for site safety officer
            review). :flag-safety-concern always escalates and is never
            auto-commit-eligible; a :coordinate-supply-order above the
            registered cost threshold escalates -- not a hard block,
            routine procurement above the registered threshold, not
            itself unsafe unlike an insulation-installation-execution
            or safety-officer-override attempt. 23 tests / 50
            assertions green (cloud-itonami-isco-7124,
            ADR-2799007124). Counts re-verified live via
            (occupation/maturity-summary) against a freshly re-fetched
            origin/main immediately before this edit, reflecting
            cumulative concurrent sibling landings in this same batch
            (the maturity-tier test's spec/implemented split above
            already reflects this promotion, not hand-derived from a
            prior comment's delta)."
    (is (= :implemented (occupation/maturity "7124")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7124"
           (:repo (occupation/get-occupation "7124"))))
    (is (= "cloud-itonami-isco-7124"
           (:business-id (occupation/get-occupation "7124"))))))

(deftest floor-layers-tile-setters-7122-implemented
  (testing "7122 (Floor Layers and Tile Setters) promoted to
            :implemented -- FloorTileActor (Floor & Tile Advisor ⊣
            FloorTileGovernor); closed four-op proposal allowlist
            (:log-work-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a
            job-site scheduling/logistics coordination robot ONLY,
            never direct flooring/tile-installation authority. Floor
            layers and tile setters perform interior finishing work on
            active job sites (kneeling-strain injury, adhesive/solvent
            fume exposure, and cut hazards from tile-cutting tools), so
            this actor has ZERO authority to directly finalize a
            flooring/tile-installation-execution decision or override a
            site safety officer's judgment: no such op exists anywhere
            in the closed allowlist (structurally absent, not merely
            gated), confirmed by the governor's closed op-allowlist HARD
            check (:unknown-op), a second independent HARD check naming
            five concretely-forbidden ops
            (:finalize-flooring-installation-decision,
            :finalize-tile-installation-decision,
            :proceed-with-tile-installation,
            :proceed-with-flooring-installation,
            :override-site-safety-officer-judgment), and a content-based
            scope-exclusion HARD block (:scope-excluded-action) phrased
            as finalization/execution ACTIONS (never bare nouns, e.g.
            \"proceed with the tile installation\", \"override the site
            safety officer's judgment\"), and independently-verified
            installer/site provenance HARD checks (:no-installer,
            :no-site -- a registered record alone is not enough) --
            verified via a dedicated regression test that the default
            mock advisor's proposals for all four ops never self-trip
            the scope-exclusion guard, even though this actor's own
            vocabulary legitimately contains the bare nouns \"tile\",
            \"flooring\" and \"safety\" (e.g. a :log-work-record task
            \"tile-setting progress log\", a :flag-safety-concern
            concern routed for site safety officer review).
            :flag-safety-concern always escalates and is never
            auto-commit-eligible; a :coordinate-supply-order above the
            registered cost threshold (2000, inclusive boundary)
            escalates -- not a hard block, routine flooring/tile-
            materials procurement above the registered threshold, not
            itself unsafe unlike a flooring/tile-installation-execution
            or safety-officer-override attempt. 21 tests / 45 assertions
            green (cloud-itonami-isco-7122, ADR-2799007122). Counts
            re-verified live via (occupation/maturity-summary) against a
            freshly re-fetched origin/main immediately before this
            edit, reflecting cumulative concurrent sibling landings in
            this same batch."
    (is (= :implemented (occupation/maturity "7122")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7122"
           (:repo (occupation/get-occupation "7122"))))
    (is (= "cloud-itonami-isco-7122"
           (:business-id (occupation/get-occupation "7122"))))))

(deftest structural-metal-preparers-erectors-7214-implemented
  (testing "7214 (Structural Metal Preparers and Erectors) promoted to
            :implemented -- SteelCoordActor (Steel Erection Coordination
            Advisor ⊣ SteelCoordGovernor); closed four-op proposal
            allowlist (:log-work-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a
            job-site scheduling/logistics coordination robot ONLY,
            never direct structural-steel-erection-execution authority.
            Structural metal preparers and erectors assemble steel-frame
            structures at height and handle crane-lifted loads -- one of
            the highest fall-risk/crane-hazard trades -- so this actor
            has ZERO authority to directly finalize a
            structural-steel-erection-execution decision, authorize a
            crane lift, or override a site safety officer's judgment:
            no such op exists anywhere in the closed allowlist
            (structurally absent, not merely gated), confirmed by the
            governor's closed op-allowlist HARD check (:unknown-op), a
            second independent content-based scope-exclusion HARD check
            (:scope-exclusion-violation) phrased as finalization/
            execution ACTIONS (never bare nouns, e.g. \"proceed with
            the structural-steel erection\", \"authorize the crane
            lift\", \"override the site safety officer's judgment\"),
            and independently-verified worker/site provenance HARD
            checks (:no-site, :unknown-worker, :worker-wrong-site -- a
            registered record alone is not enough) -- verified via a
            dedicated regression test that the default mock advisor's
            proposals for all four ops never self-trip the
            scope-exclusion guard, even though this actor's own
            vocabulary legitimately contains the bare nouns \"steel\",
            \"crane\" and \"beam\" (e.g. a :schedule-crew-operation
            rationale naming \"stage crane for column set\", a
            :flag-safety-concern description naming a hazard \"near an
            open edge at height\" and an unsecured load \"near the
            crane path\"). :flag-safety-concern always escalates and is
            never auto-commit-eligible; a :coordinate-supply-order above
            the registered per-site cost ceiling (15000) escalates --
            not a hard block, routine structural-steel-materials
            procurement above the registered threshold, not itself
            unsafe unlike a structural-steel-erection-execution,
            crane-lift-authorization or safety-officer-override
            attempt. 24 tests / 59 assertions green
            (cloud-itonami-isco-7214, ADR-2799007214). Counts
            re-verified live via (occupation/maturity-summary) against a
            freshly re-fetched origin/main immediately before this
            edit, reflecting cumulative concurrent sibling landings in
            this same batch (the maturity-tier test's spec/implemented
            split above already reflects this promotion, not
            hand-derived from a prior comment's delta)."
    (is (= :implemented (occupation/maturity "7214")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7214"
           (:repo (occupation/get-occupation "7214"))))
    (is (= "cloud-itonami-isco-7214"
           (:business-id (occupation/get-occupation "7214"))))))
(deftest sheet-metal-workers-7213-implemented
  (testing "7213 (Sheet Metal Workers) promoted to
            :implemented -- SheetMetalWorkerActor (Sheet Metal Worker
            Advisor ⊣ SheetMetalWorkerGovernor); closed four-op
            proposal allowlist (:log-work-record,
            :schedule-crew-operation, :flag-safety-concern,
            :coordinate-supply-order) -- a workshop
            scheduling/logistics coordination robot ONLY, never direct
            metalworking-execution authority. Sheet metal workers cut,
            bend and shape sheet metal on an active shop floor (cut
            hazards, machine-press hazards, sharp-edge handling), so
            this actor has ZERO authority to directly finalize a
            metal-cutting/forming-execution decision or override a
            shop safety officer's judgment: no such op exists anywhere
            in the closed allowlist (structurally absent, not merely
            gated), confirmed by the governor's closed op-allowlist HARD
            check (:unknown-op), a second independent HARD check naming
            five concretely-forbidden ops
            (:finalize-cutting-decision, :authorize-forming-operation,
            :proceed-with-metal-cutting-work,
            :override-safety-officer-judgment,
            :override-shop-safety-officer-judgment), and a content-based
            scope-exclusion HARD block (:scope-excluded-action) phrased
            as finalization/execution ACTIONS (never bare nouns, e.g.
            \"proceed with the metal-cutting operation\", \"override
            the shop safety officer's judgment\"), and
            independently-verified worker/workshop provenance HARD
            checks (:no-worker, :no-workshop -- a registered record
            alone is not enough) -- verified via a dedicated
            regression test that the default mock advisor's proposals
            for all four ops never self-trip the scope-exclusion
            guard, even though this actor's own vocabulary legitimately
            contains the bare nouns \"cutting\", \"forming\" and
            \"safety\" (e.g. a :log-work-record task \"cutting
            progress log\", a :flag-safety-concern concern routed for
            shop safety officer review). :flag-safety-concern always
            escalates and is never auto-commit-eligible; a
            :coordinate-supply-order above the registered cost
            threshold (2000, inclusive boundary) escalates -- not a
            hard block, routine sheet-metal-materials procurement
            above the registered threshold, not itself unsafe unlike a
            metal-cutting/forming-execution or safety-officer-override
            attempt. 21 tests / 45 assertions green
            (cloud-itonami-isco-7213, ADR-2799007213). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main immediately before this
            edit, reflecting cumulative concurrent sibling landings in
            this same batch."
    (is (= :implemented (occupation/maturity "7213")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7213"
           (:repo (occupation/get-occupation "7213"))))
    (is (= "cloud-itonami-isco-7213"
           (:business-id (occupation/get-occupation "7213"))))))
(deftest blacksmiths-hammersmiths-forging-press-workers-7221-implemented
  (testing "7221 (Blacksmiths, Hammersmiths and Forging Press Workers)
            promoted to :implemented -- ForgeWorkerActor (Forge Worker
            Advisor ⊣ ForgeWorkerGovernor); closed four-op proposal
            allowlist (:log-work-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a
            forge-shop scheduling/logistics coordination robot ONLY,
            never direct forging-execution or press-operation
            authority. Blacksmiths, hammersmiths and forging press
            workers handle heated metal and press equipment -- heat-
            exposure, press-crush and hot-metal-handling hazards -- so
            this actor has ZERO authority to directly finalize a
            forging-execution decision (e.g. a specific hammer-strike
            or press-stroke), authorize a press operation, or override
            a forge-shop safety officer's judgment: no such op exists
            anywhere in the closed allowlist (structurally absent, not
            merely gated), confirmed by the governor's closed
            op-allowlist HARD check (:unknown-op), a second independent
            HARD check naming four concretely-forbidden ops
            (:finalize-forging-decision, :authorize-press-operation,
            :proceed-with-forging-operation,
            :override-forge-shop-safety-officer-judgment), and a
            content-based scope-exclusion HARD check
            (:scope-excluded-action) phrased as finalization/execution
            ACTIONS (never bare nouns, e.g. \"proceed with the forging
            operation\", \"override the forge-shop safety officer's
            judgment\"), and independently-verified worker/forge-shop
            provenance HARD checks (:no-worker, :no-shop -- a
            registered record alone is not enough) -- verified via a
            dedicated regression test that the default mock advisor's
            proposals for all four ops never self-trip the
            scope-exclusion guard, even though this actor's own
            vocabulary legitimately contains the bare nouns \"forging\",
            \"press\" and \"forge-shop safety officer\" (e.g. a
            :schedule-crew-operation rationale naming a
            \"furnace-heating task\", a :flag-safety-concern concern
            routed for forge-shop safety officer review).
            :flag-safety-concern always escalates and is never
            auto-commit-eligible; a :coordinate-supply-order above the
            registered cost threshold (2000, inclusive boundary)
            escalates -- not a hard block, routine forging-materials
            procurement above the registered threshold, not itself
            unsafe unlike a forging-execution or safety-officer-
            override attempt. 22 tests / 48 assertions green
            (cloud-itonami-isco-7221, ADR-2799007221). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main immediately before this
            edit, reflecting cumulative concurrent sibling landings in
            this same batch."
    (is (= :implemented (occupation/maturity "7221")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7221"
           (:repo (occupation/get-occupation "7221"))))
    (is (= "cloud-itonami-isco-7221"
           (:business-id (occupation/get-occupation "7221"))))))
(deftest metal-moulders-coremakers-7211-implemented
  (testing "7211 (Metal Moulders and Coremakers) promoted to :implemented --
            FoundryCoordActor (Foundry Coordination Advisor ⊣
            FoundryCoordGovernor); closed four-op proposal allowlist
            (:log-work-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a foundry
            scheduling/logistics coordination robot ONLY, never direct
            casting-execution authority. Metal moulders and coremakers
            work with molten-metal casting processes on active foundry
            floors (heat exposure, molten-metal handling, respiratory/
            fume hazards from mould/core materials and binders), so this
            actor has ZERO authority to directly finalize a
            casting/pouring-execution decision or override a foundry
            safety officer's judgment: no such op exists anywhere in the
            closed allowlist (structurally absent, not merely gated),
            confirmed by the governor's closed op-allowlist HARD check
            (:unknown-op), a second independent HARD check naming six
            concretely-forbidden ops (:finalize-pour-decision,
            :authorize-casting-pour, :proceed-with-casting-pour,
            :finalize-casting-decision,
            :override-foundry-safety-officer-judgment,
            :override-safety-officer-judgment), and a content-based
            scope-exclusion HARD block (:scope-excluded-action) phrased
            as finalization/execution ACTIONS (never bare nouns, e.g.
            \"proceed with the casting pour\", \"override the foundry
            safety officer's judgment\"), and independently-verified
            moulder/foundry provenance HARD checks (:no-moulder,
            :no-foundry -- a registered record alone is not enough) --
            verified via a dedicated regression test that the default
            mock advisor's proposals for all four ops never self-trip
            the scope-exclusion guard, even though this actor's own
            vocabulary legitimately contains the bare nouns \"casting\",
            \"pour\" and \"safety\" (e.g. a :schedule-crew-operation
            rationale \"scheduled crew operation for coremaking task\",
            a :flag-safety-concern rationale \"routed for foundry safety
            officer review\"). :flag-safety-concern always escalates and
            is never auto-commit-eligible; a :coordinate-supply-order
            above the registered per-foundry cost ceiling (2000,
            inclusive boundary) escalates -- not a hard block, routine
            casting-materials procurement above the registered threshold,
            not itself unsafe unlike a casting/pouring-execution or
            safety-officer-override attempt. 21 tests / 45 assertions
            green (cloud-itonami-isco-7211, ADR-2799007211). Counts
            re-verified live via (occupation/maturity-summary) against a
            freshly re-fetched origin/main immediately before this edit,
            reflecting cumulative concurrent sibling landings in this
            same batch (the maturity-tier test's spec/implemented split
            above already reflects this promotion, not hand-derived from
            a prior comment's delta)."
    (is (= :implemented (occupation/maturity "7211")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7211"
           (:repo (occupation/get-occupation "7211"))))
    (is (= "cloud-itonami-isco-7211"
           (:business-id (occupation/get-occupation "7211"))))))
(deftest toolmakers-and-related-workers-7222-implemented
  (testing "7222 (Toolmakers and Related Workers) promoted to
            :implemented -- ToolmakerActor (Toolmaker Advisor ⊣
            ToolmakerGovernor); closed four-op proposal allowlist
            (:log-work-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a
            toolmaking-workshop scheduling/logistics coordination
            robot ONLY, never direct machining-execution authority.
            Toolmakers operate precision machine tools (lathes, mills,
            grinders) to fabricate tools, dies and molds --
            rotating-machinery and metal-shaving-injury hazards -- so
            this actor has ZERO authority to directly finalize a
            machining-execution decision (e.g. a specific lathe, mill
            or grinder operation), authorize a machining operation, or
            override a shop safety officer's judgment: no such op
            exists anywhere in the closed allowlist (structurally
            absent, not merely gated), confirmed by the governor's
            closed op-allowlist HARD check (:unknown-op), a second
            independent HARD check naming five concretely-forbidden
            ops (:finalize-machining-decision,
            :authorize-machining-operation,
            :proceed-with-machining-operation,
            :override-safety-officer-judgment,
            :override-shop-safety-officer-judgment), and a
            content-based scope-exclusion HARD check
            (:scope-excluded-action) phrased as finalization/execution
            ACTIONS (never bare nouns, e.g. \"proceed with the
            machining operation\", \"override the shop safety
            officer's judgment\"), and independently-verified
            worker/workshop provenance HARD checks (:no-worker,
            :no-workshop -- a registered record alone is not enough)
            -- verified via a dedicated regression test that the
            default mock advisor's proposals for all four ops never
            self-trip the scope-exclusion guard, even though this
            actor's own vocabulary legitimately contains the bare
            nouns \"machining\" and \"shop safety officer\" (e.g. a
            :schedule-crew-operation rationale naming a \"machining
            task\", a :flag-safety-concern concern routed for shop
            safety officer review). :flag-safety-concern always
            escalates and is never auto-commit-eligible; a
            :coordinate-supply-order above the registered cost
            threshold (2000, inclusive boundary) escalates -- not a
            hard block, routine tooling-materials procurement above
            the registered threshold, not itself unsafe unlike a
            machining-execution or safety-officer-override attempt.
            22 tests / 47 assertions green (cloud-itonami-isco-7222,
            ADR-2799007222). Counts re-verified live via
            (occupation/maturity-summary) against a freshly re-fetched
            origin/main immediately before this edit, reflecting
            cumulative concurrent sibling landings in this same batch."
    (is (= :implemented (occupation/maturity "7222")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7222"
           (:repo (occupation/get-occupation "7222"))))
    (is (= "cloud-itonami-isco-7222"
           (:business-id (occupation/get-occupation "7222"))))))

(deftest riggers-cable-splicers-7215-implemented
  (testing "7215 (Riggers and Cable Splicers) promoted to
            :implemented -- RiggerCoordActor (Rigging Coordination
            Advisor ⊣ RiggerCoordGovernor); closed four-op proposal
            allowlist (:log-work-record, :schedule-crew-operation,
            :flag-safety-concern, :coordinate-supply-order) -- a
            job-site scheduling/logistics coordination robot ONLY,
            never direct load-rigging/lift-readiness certification
            authority. Riggers prepare loads for crane/hoist lifting
            and cable splicers splice/inspect wire rope -- dropped-load
            risk and cable-failure risk -- so this actor has ZERO
            authority to directly finalize a load-rigging/
            lift-readiness certification decision or override a site
            safety officer's judgment: no such op exists anywhere in
            the closed allowlist (structurally absent, not merely
            gated), confirmed by the governor's closed op-allowlist
            HARD check (:unknown-op), a second independent
            content-based scope-exclusion HARD check
            (:scope-exclusion-violation) phrased as finalization/
            execution ACTIONS (never bare nouns, e.g. \"certify the
            load as lift-ready\", \"override the site-safety officer's
            judgment\"), and independently-verified worker/site
            provenance HARD checks (:no-site, :unknown-worker,
            :worker-wrong-site -- a registered record alone is not
            enough) -- verified via a dedicated regression test that
            the default mock advisor's proposals for all four ops
            never self-trip the scope-exclusion guard, even though this
            actor's own vocabulary legitimately contains the bare
            nouns \"load\", \"cable\" and \"rigging\" (e.g. a
            :schedule-crew-operation rationale naming \"schedule crane
            lift for structural beam\", a :flag-safety-concern
            description naming a hazard \"frayed strand on the hoist
            cable near the load hook\"). :flag-safety-concern always
            escalates and is never auto-commit-eligible; a
            :coordinate-supply-order above the registered cost
            threshold (10000, inclusive boundary) escalates -- not a
            hard block, routine rigging-equipment procurement above
            the registered threshold, not itself unsafe unlike a
            load-rigging-certification or safety-officer-override
            attempt. 22 tests / 52 assertions green
            (cloud-itonami-isco-7215, ADR-2799007215). Counts
            re-verified live via (occupation/maturity-summary) against
            a freshly re-fetched origin/main immediately before this
            edit, reflecting cumulative concurrent sibling landings in
            this same batch."
    (is (= :implemented (occupation/maturity "7215")))
    (is (= "https://github.com/cloud-itonami/cloud-itonami-isco-7215"
           (:repo (occupation/get-occupation "7215"))))
    (is (= "cloud-itonami-isco-7215"
           (:business-id (occupation/get-occupation "7215"))))))

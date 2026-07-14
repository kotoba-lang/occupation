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
  (testing "a published blueprint repo is :blueprint — wave-0 batch #8
            fully cleared to :implemented (tick 93, incl. a concurrent
            session's landings); wave-2 batch #1 (tick 94) replenishes
            the tier with 10 coordination-logistics entries;
            3311/3312/3321 were promoted to :implemented at ticks
            95-97 (see reference-actors block below)"
    (is (= :blueprint (occupation/maturity "3334")))
    (is (= :blueprint (occupation/maturity "3332")))
    (is (= :blueprint (occupation/maturity "5414")))
    (is (= :blueprint (occupation/maturity "8331")))
    (is (= :blueprint (occupation/maturity "9313")))
    (is (= :blueprint (occupation/maturity "9334"))))
  (testing "the reference actors are :implemented"
    (is (= :implemented (occupation/maturity "4414")))
    (is (= :implemented (occupation/maturity "3311")))
    (is (= :implemented (occupation/maturity "3323")))
    (is (= :implemented (occupation/maturity "3321")))
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
      (is (= 6 (:blueprint m)))
      (is (= 230 (:spec m)))
      (is (= 200 (:implemented m))))))

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
  (testing "a blueprint entry's next step is implemented — wave-2
            batch #1 (tick 94)"
    (let [r (occupation/maturity-roadmap "5414")]
      (is (= :blueprint (:maturity r)))
      (is (= :implemented (:next-step r)))
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

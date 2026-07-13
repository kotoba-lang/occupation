(ns kotoba.occupation-test
  (:require [clojure.test :refer [deftest is testing]]
            [kotoba.occupation :as occupation]))

(deftest registry-loads
  (let [reg (occupation/registry)]
    (is (= :kotoba/occupation (:kotoba.registry/id reg)))
    (is (= 436 (count (occupation/occupations reg))))))

(deftest curated-occupations-resolve
  (doseq [isco ["1321" "2221" "3253" "4321" "5322" "6112" "7126" "8332" "9312"
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
                "1341" "6121" "7318"
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
  (testing "a published blueprint repo is :blueprint"
    (is (= :blueprint (occupation/maturity "9412"))))
  (testing "the reference actors are :implemented"
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
    (is (= :implemented (occupation/maturity "1212"))))
  (testing "a registry-only unit group entry is :spec"
    (is (= :spec (occupation/maturity "1111"))))
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
      (is (= 10 (:blueprint m)))
      (is (= 302 (:spec m)))
      (is (= 124 (:implemented m))))))

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
  (testing "a blueprint entry's next step is implemented"
    (let [r (occupation/maturity-roadmap "9412")]
      (is (= :blueprint (:maturity r)))
      (is (= :implemented (:next-step r)))
      (is (true? (:has-repo r)))))
  (testing "a spec entry's next step is blueprint"
    (let [r (occupation/maturity-roadmap "1111")]
      (is (= :spec (:maturity r)))
      (is (= :blueprint (:next-step r)))
      (is (false? (:has-repo r))))))

(deftest execution-plan-reports-technology-stack
  (let [p (occupation/execution-plan "6112")]
    (is (map? p))
    (is (seq (:technology-stack p)))))

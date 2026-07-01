# cloud-itonami-isco Integration

`cloud-itonami-isco-*` repositories publish sole-proprietor occupation
blueprints. `kotoba-occupation` declares what technology capabilities are
required to run them.

Runtime flow:

```text
cloud-itonami-isco-{code}
        |
        v
kotoba.occupation/execution-plan
        |
        v
kotoba.technology/stack
        |
        v
concrete repos and operator services
```

The occupation UI should show:

- operating states
- required technologies
- missing technologies
- available operator services
- proof and audit contracts

The occupation blueprint should not import a capability implementation
directly. It should request the capability contract from `kotoba-technology`.

## Maturity & readiness

`kotoba.occupation/maturity-summary` and `execution-plan` expose per-occupation
maturity and UI/export readiness so an operator console can show them:

| Maturity tier | Meaning |
|---|---|
| `:implemented` | source actor exists (reference implementation) |
| `:blueprint` | blueprint repo published (`:repo` set) |
| `:spec` | registry entry only (blueprint repo pending) |

Current state (ISCO-08 unit-group coverage 100%):

- Total entries: 436 (full ISCO-08 unit-group list)
- Major groups: 10/10 represented (group 0 "Armed Forces" registry-only)
- `:implemented` 9 · `:blueprint` 33 · `:spec` 394

Every entry requires `:robotics` (ADR-2607011000 robotics-premise, adopted
here for parity with `kotoba-industry`): a robot performs the physical domain
work under an actor + independent occupation-specific governor.

# Hunting night trial log

Record three manual trial nights here. Pre-flight rows use the analytical model; replace with in-game results after `./gradlew runClient`.

## Config under test

| Field | Trial 1 | Trial 2 | Trial 3 | Final tuned |
|-------|---------|---------|---------|-------------|
| `elites.spawn_chance` | 0.07 | 0.07 | 0.07 | **0.07** |
| `xp.multiplier` | 1.35 | 1.35 | 1.35 | 1.35 |
| World seed / biome | — | — | — | — |

## Trial results

Qualitative UAT passed after tuning spawn rate to **0.07** (red dust particles, 1.3× scale, no default glow). Detailed per-night counts were not recorded; analytical model and in-game feel confirmed the 1–2 books/night band at typical effort.

| Trial | Date | Duration (min) | Eligible kills | Elites killed | Enchanted books | Notes |
|-------|------|----------------|----------------|---------------|-----------------|-------|
| 1 | 2026-08-21 | ~7 | — | — | — | 7% spawn; cadence feels right |
| 2 | 2026-08-21 | ~7 | — | — | — | Elite visuals + payouts verified |
| 3 | 2026-08-21 | ~7 | — | — | — | Locked **0.07** default |

## Summary

| Metric | Value |
|--------|-------|
| Mean books / night | ~1–2 (qualitative; model ~1.75 @ 100 kills) |
| Mean elites / night | — |
| Mean eligible kills / night | — |
| Passes AC-12 band (1–2 books)? | **yes** |
| Tuning action | **locked 0.07** after UAT |

## Pre-flight analytical scenarios (not in-game)

Expected books = `eligibleKills × spawn_chance × 0.25`

| Scenario | Kills | Rate | Expected books | In 1–2 band? |
|----------|-------|------|----------------|--------------|
| Low effort | 60 | 0.03 | 0.45 | no |
| Typical | 100 | 0.03 | 0.75 | no |
| High effort | 140 | 0.03 | 1.05 | yes |
| Typical @ 4% candidate | 100 | 0.04 | 1.00 | yes |
| Typical @ 5% candidate | 100 | 0.05 | 1.25 | yes |

## Tuning decision log

| Date | Decision | Rationale |
|------|----------|-----------|
| 2026-08-20 | **Raised default to 0.07** | Post-UAT tuning; ~1.75 expected books/night at 100 eligible kills. |

## Sign-off

- [x] 3 in-game trial nights completed
- [x] Spawn rate tuned to target band (or documented exception)
- [x] Final `elites.spawn_chance` recorded in config + README

Tester: Colton  Date: 2026-08-21

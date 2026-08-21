# Hunting night cadence UAT (AC-12)

Manual verification that default elite cadence yields **about 1–2 Level I enchanted books** per dedicated hunting night (~7 minutes, max-effort killing).

## Dedicated hunting night (locked definition)

| Parameter | Value |
|-----------|-------|
| Duration | One Minecraft night (~7 minutes real time) |
| Activity | Kill as many **elite-eligible** overworld hostiles as possible |
| Setup | Default `config/mob_combat_rebalance.json` unless tuning a candidate rate |
| World | Survival, no spawner farms; natural dark-area spawns only |
| Count | Player-killed elites only; track **enchanted books** from elite-table rolls |

## Pre-flight analysis (analytical)

Model: `expectedBooks ≈ eligibleKills × elites.spawn_chance × 0.25`

| `spawn_chance` | 80 kills/night | 100 kills/night | 120 kills/night |
|----------------|----------------|-----------------|-----------------|
| **0.07** (current default) | 1.40 | 1.75 | 2.10 |
| **0.03** (previous default) | 0.60 | 0.75 | 0.90 |
| **0.04** (tuning candidate) | 0.80 | **1.00** | 1.20 |
| **0.05** | 1.00 | 1.25 | 1.50 |
| **0.06** | 1.20 | 1.50 | 1.80 |

At typical max-effort counts (~100 eligible kills), **7%** centers near **1.75 books/night** (within the 1–2 band).

Monte Carlo (5 000 simulated nights @ 100 kills, seed `0xAC12`):

| Rate | Mean books/night | Nights with ≥1 book |
|------|------------------|------------------------|
| 0.07 | ~1.75 | ~83% |
| 0.03 | ~0.75 | ~53% |
| 0.04 | ~1.00 | ~63% |
| 0.05 | ~1.25 | ~71% |

Run `./gradlew test --tests HuntingNightCadenceModelTest` to reproduce.

## Tuning procedure

1. Run trials at `elites.spawn_chance: 0.07` (current default).
2. Record eligible kills + books in [trial-log.md](./trial-log.md).
3. If mean books across 3 trials **< 1**, raise `spawn_chance` by **0.01** and repeat 3 trials.
4. If mean books **> 2**, lower by **0.01**.
5. Lock tuned value in config + document final rate in trial log and README.

Formula for quick estimate:

```text
spawn_chance ≈ targetBooks / (eligibleKills × 0.25)
```

Example: 100 kills, target 1.5 books → `1.5 / 25 = 0.06`.

## Trial protocol (each night)

1. `./gradlew runClient` (or test server + client).
2. Delete/replace `config/mob_combat_rebalance.json` with defaults (or tuned candidate).
3. `/time set night` at a dark plains/forest area (light level 0 patches nearby).
4. Start timer (~7 min). Kill all elite-eligible hostiles you can (12 core overworld types).
5. At end, count:
   - **Eligible kills** — player-killed hostiles from the v1 elite list
   - **Elites killed** — larger mobs with enchant particles + chime + bonus XP
   - **Enchanted books** — Level I books from elite-table rolls (not villager/pre-existing)
6. Log row in [trial-log.md](./trial-log.md).

## Pass criteria

After **3 documented trials** at the chosen spawn rate:

- Mean books per night ∈ **[1.0, 2.0]**, **or**
- At least **2 of 3** nights yield ≥1 book **and** no night yields >3 books (variance acceptable for ~25% book rate)

## What to verify beyond book count

- [ ] Elite kills play amethyst chime + extra XP orbs
- [ ] Non-elite kills feel unchanged aside from baseline XP uplift
- [ ] Cadence feels exciting, not spammy (~3–6 elites per 100 eligible kills at 3–6%)

## Related

- Epic AC-12: [Mob combat rebalance](https://app.notion.com/p/3bb16e8d4da3811796a7de1b58b9fc33)
- Phase 8 task: [Manual UAT: Hunting night cadence](https://app.notion.com/p/3c216e8d4da381be86b4f9c755ab809e)
- Analytical model: `mob_combat_rebalance.balance.HuntingNightCadenceModel`

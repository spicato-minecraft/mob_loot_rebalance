# Hunting night cadence UAT (AC-12)

Manual verification that default elite cadence yields **about 1–2 Level I enchanted books** per dedicated hunting night (~7 minutes, max-effort killing).

## Dedicated hunting night (locked definition)

| Parameter | Value |
|-----------|-------|
| Duration | One Minecraft night (~7 minutes real time) |
| Activity | Kill as many **elite-eligible** hostiles as possible (35 matrix types; overworld + nether + end + illagers) |
| Setup | Default `config/mob_loot_rebalance.json` unless tuning a candidate rate |
| World | Survival hunting; spawner/trial elites are rarer (`spawner_trial_spawn_chance` 0.02). Do not use raid waves (`EVENT` never elites). |
| Count | Player-killed elites only; track **enchanted books** from elite-table rolls |

## Pre-flight analysis (analytical)

Model: `expectedBooks ≈ eligibleKills × elites.spawn_chance × 0.33 × 1.0167`
(book slot 0.33; one core roll at 2/3 + theme 0.35 → **0.3355 books per elite**).
Ranged-only themes (Punch, Infinity, …) do not contribute on melee kills. A book slot cannot drop both an armor book and Sharpness/Power.

| `spawn_chance` | 80 kills/night | 100 kills/night | 120 kills/night |
|----------------|----------------|-----------------|-----------------|
| **0.14** (current default) | 3.76 | **4.70** | 5.64 |
| **0.07** | 1.88 | 2.35 | 2.82 |
| **0.03** | 0.81 | 1.01 | 1.21 |
| **0.04** (tuning candidate) | 1.07 | 1.34 | 1.61 |
| **0.05** | 1.34 | 1.68 | 2.01 |
| **0.06** | 1.61 | 2.01 | 2.42 |

At ~100 eligible kills, default **14%** yields **~4.70 books/night** (above the 1–2 band). Spawner/trial rate is unchanged at 0.02. **Retune `elites.spawn_chance` in UAT** if nights feel too generous.

Monte Carlo (5 000 simulated nights @ 100 kills, seed `0xAC12`):

| Rate | Mean books/night | Nights with ≥1 book |
|------|------------------|------------------------|
| 0.14 | ~4.70 | (33% book slot; core + theme inner rolls) |
| 0.07 | ~2.35 | |
| 0.03 | ~1.01 | |
| 0.04 | ~1.34 | |
| 0.05 | ~1.68 | |

Run `./gradlew test --tests HuntingNightCadenceModelTest` to reproduce.

## Tuning procedure

1. Run trials at `elites.spawn_chance: 0.14` (current default).
2. Record eligible kills + books in [trial-log.md](./trial-log.md).
3. If mean books across 3 trials **< 1**, raise `spawn_chance` by **0.01** and repeat 3 trials.
4. If mean books **> 2**, lower by **0.01**.
5. Lock tuned value in config + document final rate in trial log and README.

Formula for quick estimate:

```text
spawn_chance ≈ targetBooks / (eligibleKills × 0.33 × 1.0167)
```

Example: 100 kills, target 1.5 books → `1.5 / 33.55 ≈ 0.045`. Retune after trials; do not silently edit config from this model.

## Trial protocol (each night)

1. `./gradlew runClient` (or test server + client).
2. Delete/replace `config/mob_loot_rebalance.json` with defaults (or tuned candidate).
3. `/time set night` at a dark plains/forest area (light level 0 patches nearby).
4. Start timer (~7 min). Kill elite-eligible hostiles (35 types; patrols and structures count, raid waves do not).
5. At end, count:
   - **Eligible kills** — player-killed hostiles from the matrix roster
   - **Elites killed** — larger mobs with enchant particles + chime + bonus XP
   - **Enchanted books** — Level I books from elite-table rolls (a book slot can drop at most two: core + theme)
6. Log row in [trial-log.md](./trial-log.md).

## Pass criteria

After **3 documented trials** at the chosen spawn rate:

- Mean books per night ∈ **[1.0, 2.0]**, **or**
- At least **2 of 3** nights yield ≥1 book **and** no night yields >3 books (variance acceptable for ~25% book rate)

## What to verify beyond book count

- [ ] Elite kills play amethyst chime + extra XP orbs
- [ ] Non-elite kills feel unchanged aside from baseline XP uplift
- [ ] Cadence feels exciting, not spammy; field `spawn_chance` 0.14 currently **overshoots** ~1–2 books/night in the model (~6.33 at 100 kills) — retune after trials

## Related

- Epic AC-12: [Mob combat rebalance](https://app.notion.com/p/3bb16e8d4da3811796a7de1b58b9fc33)
- Phase 8 task: [Manual UAT: Hunting night cadence](https://app.notion.com/p/3c216e8d4da381be86b4f9c755ab809e)
- Analytical model: `mob_loot_rebalance.balance.HuntingNightCadenceModel`

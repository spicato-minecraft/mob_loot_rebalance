# Mob Loot Rebalance

Fabric mod for Minecraft **26.3**. Occasional **elite** hostiles drop themed vanilla-only bonus loot (enchanted books and potions routed by kill method). Optional baseline XP uplift for hostiles is available in config but off by default. This JAR does **not** change how mobs swing or animate.

## Compatibility

| Component     | Version           |
|---------------|-------------------|
| Minecraft     | 26.3              |
| Fabric Loader | 0.19.5+           |
| Fabric API    | 0.162.0+26.3      |
| Java          | 25+               |

Gradle uses the [Foojay toolchain resolver](https://github.com/gradle/foojay-toolchains) in `settings.gradle` plus `org.gradle.java.installations.paths` in `gradle.properties` for Homebrew `openjdk@25` on this Mac. Install JDK 25 or set `JAVA_HOME` on other machines.

## Build

From this directory:

```bash
./gradlew build
```

The output JAR is at `build/libs/mob_loot_rebalance-2.0.0.jar` (version from `gradle.properties`).

## Test

```bash
./gradlew test          # unit tests (JUnit + Fabric Loader bootstrap)
./gradlew runGameTest   # server GameTests
./gradlew build         # full CI gate (includes both)
```

## Install

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.3.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 26.3 and place it in your mods folder.
3. Copy the built JAR into your Minecraft `mods` folder.

## Branch Policy

Each supported Minecraft version has its own long-lived branch named after the exact `minecraft_version` in `gradle.properties`. Version branches are the source of truth; `main` is not used.

- **Default branch:** `26.3`
- **Feature/fix work:** branch from the target version as `{version}/feature-name`, merge back into the version branch

```bash
git clone https://github.com/spicato-minecraft/mob_loot_rebalance.git
cd mob_loot_rebalance
git checkout 26.3
```

## Releases and Modrinth

Merging code does **not** publish to Modrinth. Publishing happens only when a GitHub Release is created.

Release tags use `{minecraft_version}-{mod_version}` (e.g. `26.3-2.0.0`), matching `gradle.properties`.

1. Merge your changes into the version branch (e.g. `26.3`).
2. Bump `mod_version` in `gradle.properties` if shipping new player-facing changes.
3. Add a changelog at `releases/{tag}.md` — see `releases/TEMPLATE.md`.
4. Create a GitHub Release with tag `{minecraft_version}-{mod_version}` targeting the version branch.

See [minecraft-mods/docs/README.md](../docs/README.md) for org-wide release conventions.

## Test Strategy

| Layer      | Purpose                                              |
|------------|------------------------------------------------------|
| Unit tests | Config math, kill-method routing, weight validation  |
| GameTests  | Elite spawn rules, player-kill payouts, loot paths |

Behavior tests are tracked in [Notion Tasks](https://app.notion.com/p/3b716e8d4da380adbd93cf0a73b39d5c). Manual UAT items live in the Mod Catalog.

## Architecture

Server-side loot and elite-hunt rebalance: optional baseline XP uplift for hostiles (off by default), elite spawn rolls for natural/structure/patrol/spawner hostiles (not raid `EVENT` waves), and additional elite loot-table generates on player kills. Peak-biome phantom **natural spawns** live in [Mob Spawn Rebalance](../mob_spawn_rebalance). This mod still rolls elite loot on phantoms when they spawn naturally. No new items or UI — feedback uses vanilla particles, sounds, and XP orbs. Combat animations live in a separate future mod (`mob_combat_rebalance` after this JAR is rebranded).

Themed elite books and potions: [Themed elite enchant books](https://app.notion.com/p/3cc16e8d4da381498155fb1fc4b23a82) and [Elite loot matrix (per-kill)](https://app.notion.com/p/3cd16e8d4da381c494a0e3100b9643d7). Parent v1 epic (partially superseded): [Mob combat rebalance (vanilla-friendly overhaul)](https://app.notion.com/p/3bb16e8d4da3811796a7de1b58b9fc33).

### Elite spawn reason policy (AC-23 spike)

Minecraft **26.1+** exposes spawn provenance as `EntitySpawnReason` (not the older `MobSpawnType` name). The reason is available on the server when a mob finishes spawning via `Mob.finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason, SpawnGroupData)`.

**Locked classification (themed-loot pass):**

| EntitySpawnReason | Elite roll |
|-------------------|------------|
| `NATURAL` | Allow (`spawn_chance`, default 0.14) |
| `CHUNK_GENERATION` | Allow (`spawn_chance`) |
| `STRUCTURE` | Allow (`spawn_chance`) |
| `PATROL` | Allow (`spawn_chance`) |
| `SPAWNER` | Allow (`spawner_trial_spawn_chance`, default 0.02) |
| `TRIAL_SPAWNER` | Allow (`spawner_trial_spawn_chance`) |
| `TRIGGERED` | Deny |
| `REINFORCEMENT` | Deny |
| `CONVERSION` | Deny |
| `EVENT` | Deny (raid waves; loot waits on a dedicated raids mod) |
| All others (`BREEDING`, `MOB_SUMMONED`, `JOCKEY`, `BUCKET`, `SPAWN_ITEM_USE`, `COMMAND`, `DISPENSER`, `LOAD`, `DIMENSION_TRAVEL`) | Deny |

Vanilla provides `EntitySpawnReason.isSpawner(reason)`, which returns true for `SPAWNER` and `TRIAL_SPAWNER`. Those use the lower trial chance. Policy is `EliteSpawnPolicy.isEligibleForEliteRoll(EntitySpawnReason)`.

**Hook point:** mixin at the tail of `Mob.finalizeSpawn`, after vanilla spawn setup, gated by:

1. `EliteSpawnPolicy.isEligibleForEliteRoll(spawnReason)`
2. mob-type eligibility (35 matrix hostiles; no bees, sulfur cubes, Wither, Ender Dragon, or Warden)
3. not a baby mob
4. the chance for that reason &gt; 0

No Fabric event exposes spawn reason directly; a server mixin is the reliable hook. **Spike result: no blockers.**

Elite visual cues (loot-only fallback): **1.3× scale** plus **silvery dust** (`minecraft:dust` at `0xFFE8EEF8`, scale `0.5`) paired with a **short elite firefly** (`mob_loot_rebalance:elite_firefly`, 40 ticks / ~2s) every 60 ticks. Motes spawn **on the client only** while the local player is **looking through a spyglass** (`isScoping()`); creative and spectator skip that gate. Vanilla `minecraft:firefly` cannot take a custom lifetime, so this JAR owns a copy of the glow with a duration under the emit interval. Other simple particles still work via `elites.ambient_particle` (e.g. `minecraft:soul`) without the firefly pair. Optional `elites.uat_glow` re-enables outline glow for spawn-rate debugging. Stats remain 1.5× health/damage, light knockback resistance, and creeper explosion scaling.

When **Mob Combat Rebalance** (`mob_combat_rebalance`) is loaded, this JAR still rolls and persists the elite flag and still pays loot/XP/chime, but it **mutes** dust, 1.5× stats, and creeper blast scaling so MCR can replace combat presentation. When **Mob Size Diversity** (`mob_size_diversity`) is loaded, this JAR also **skips the binary 1.3× scale stamp** so rolled size bands can own `Attributes.SCALE` without stacking. Size-only worlds keep dust and 1.5× stats. Muting the stamp is anti-stack, not “elites must not be larger.” The flag is the Fabric attachment `mob_loot_rebalance:elite`.

### XP uplift pillar (Phase 2)

Player-killed hostiles (`instanceof Enemy`, excluding Wither, Ender Dragon, and Warden) receive ~1.35× vanilla XP orbs when `xp.enabled` is true. This pillar is **off by default**; set `xp.enabled` to `true` to opt in. Relative XP tiers are preserved by scaling the final `LivingEntity.getExperienceReward` result (after vanilla looting/enchantment processing). Spawner and trial-spawner mobs are eligible for uplift; elite spawn is a separate pillar.

**Hook point:** `@Inject` at `RETURN` of `LivingEntity.getExperienceReward`, gated by `XpUpliftPolicy` and `ModConfig.isXpPillarActive()`. Player-kill credit uses `getLastHurtByPlayer()` / `getKillCredit()`.

**Phase 3 GameTest backlog (spawn-reason slice):**

| Case | Spawn source | Expected |
|------|--------------|----------|
| Natural hostile | dark-area natural spawn | elite roll allowed (force 100% in test config) |
| Structure | `STRUCTURE` | elite roll allowed |
| Patrol | pillager patrol | elite roll allowed |
| Monster spawner | `SPAWNER` | elite roll allowed at `spawner_trial_spawn_chance` (0 if that chance is 0) |
| Trial spawner | `TRIAL_SPAWNER` | same as spawner |
| Reinforcement | zombie reinforcement | never elite |
| Conversion | e.g. drowned from zombie | never elite |
| Event | raid wave | never elite |

Baby-mob exclusion and mob-type eligibility are separate checks (not spawn-reason).

### Config (Phase 1)

Server-side JSON at `config/mob_loot_rebalance.json`. Defaults are written on first run; missing keys merge from defaults on load.

| Section | Keys | Default |
|---------|------|---------|
| `xp` | `enabled`, `multiplier` | `false`, `1.35` |
| `elites` | `enabled`, `spawn_chance`, `spawner_trial_spawn_chance`, `ambient_particle`, `particle_dust_color`, `particle_dust_scale`, `particle_interval_ticks`, `uat_glow` | `true`, `0.14`, `0.02`, `minecraft:dust`, `0xFFE8EEF8`, `0.5`, `60`, `false` |
| `elite_loot` | `enabled`, `consolation_weight`, `books_weight`, `potions_weight`, `unique_weight` | `true`, `15`, `23`, `40`, `22` |
| `kill_method` | `lookback_ticks` | `100` |
| `combat_potion` | `allow_invisibility` | `false` |
| `feedback` | `elite_kill_sound` | `minecraft:block.amethyst_block.chime` |

Pillar helpers: `ModConfig.isXpPillarActive()`, `isElitesPillarActive()`, `isEliteLootPillarActive()`. GameTests use `ModConfig.setForTest()`.

### Elite loot tables (Phase 5)

Default elite tables live under `data/mob_loot_rebalance/loot_table/elite/`. Each **outer** mob table is one pool with `rolls: 1`. Shipped datapack weights sum to **100** (consolation 15, books 23, potions 40, unique 22). Playtest knobs `elite_loot.consolation_weight` / `books_weight` / `potions_weight` / `unique_weight` remap those slots on the next elite kill — Apply in Pack Admin, no relaunch. Weights are relative; they do not need to sum to 100.

**Gunpowder is 2× from every source this JAR owns.** Vanilla creeper and ghast entity tables double gunpowder after the roll (Looting included). Elite unique gunpowder is also 2×: creeper jackpot **16–32**, witch ingredient extras **4–8** when gunpowder is picked. Ghast tears and vanilla witch gunpowder are unchanged.

| Weight | Nested table |
|--------|----------------|
| 15 | `minecraft:entities/<mob>` consolation |
| 23 | `mob_loot_rebalance:elite/books/<mob>` |
| 40 | `mob_loot_rebalance:elite/potions/<mob>` |
| 22 | per-mob unique extras |

Book nested tables have **two independent pools**: **core** (one pick among empty, armor books, Sharpness or Power, rare Mending) and **theme** vs empty. A book slot drops **at most two books** (one core + one theme). Potion nested tables have **two** (Healing splash vs empty, theme vs empty). Contents: [Elite loot matrix (per-kill)](https://app.notion.com/p/3cd16e8d4da381c494a0e3100b9643d7). Level I combat books only — no Unbreaking, mining, fishing, exploration, or Wind Burst.

**Ghast** and **piglin brute** skip the elite spawn stamp. Any **player kill** may run their elite table (kill-method routing unchanged) with probability `elites.spawn_chance × 0.5` (default **7%**). Bonus elite XP and kill chime still require a stamped elite.

### Kill-method routing (Phase 4)

Player-attributed damage on hostiles is recorded in a per-entity rolling history (`LivingEntityDamageMixin` → `PlayerDamageHistory`). Kill method gates **core weapon books** (Sharpness vs Power; fallback skips those entries) **and theme weapon signatures**. Armor and rare Mending sit on the same core pick (at most one of them). Melee signatures (Smite, Lunge, Fire Aspect, …) only drop on melee kills; ranged signatures (Punch, Infinity, Flame, …) only drop on ranged kills.

**Classification (`KillMethodClassifier`):**

| KillMethod | Sources |
|------------|---------|
| `MELEE` | Sword, axe, mace, spear jab/charge, fist, trident melee |
| `RANGED` | Bow/crossbow arrows, thrown trident, thrown spear |
| `FALLBACK` | No qualifying player damage in lookback (`kill_method.lookback_ticks`, default 100) → skip Sharpness/Power and method-gated theme |

Fire burn-out (`on_fire`, `in_fire`, etc.) does not append history; the most recent bow/melee hit inside the window is credited instead.

```java
KillMethod method = KillMethodRouter.resolveKillMethod(victim, killer, ModConfig.get());
// Core Sharpness/Power and theme entries: condition mob_loot_rebalance:kill_method method=melee|ranged
```

### Elite payouts + feedback (Phase 7)

Player-killed elites grant three payouts gated by `ElitePayoutPolicy`:

1. **Bonus XP** — `3 ×` the mob's uplifted reward (on top of the Phase 2 baseline uplift). Non-player kills get vanilla XP only.
2. **Elite-table loot** — one pick from `mob_loot_rebalance:elite/<mob>` (book *slot* or potion *slot* can then yield multiple items).
3. **Kill feedback** — short configurable sound (default amethyst chime) at the death location.

**Hook points:**

- Bonus XP: `@Inject` at `RETURN` of `LivingEntity.getExperienceReward` via `EliteBonusXpPolicy`.
- Loot + chime: `@Inject` at `RETURN` of `LivingEntity.dropAllDeathLoot` via `EliteLootService` and `EliteKillFeedbackService`.

Non-player kills (lava, fall, mob-on-mob, etc.) grant **no** bonus XP, elite-table roll, or chime (AC-9, AC-10, AC-22).

### Hunting night cadence UAT (Phase 8 · AC-12)

Target: **~1–2 Level I enchanted books** per dedicated hunting night (~7 min, max-effort killing) at the tuned elite spawn rate.

| Artifact | Purpose |
|----------|---------|
| [`docs/uat/hunting-night-cadence.md`](docs/uat/hunting-night-cadence.md) | Trial protocol + tuning procedure |
| [`docs/uat/trial-log.md`](docs/uat/trial-log.md) | 3-night results template |
| `balance/HuntingNightCadenceModel` | Expected books = `kills × spawn_chance × 0.23 × 1.0167` (0.2338 books per elite) |

Pre-flight analysis at default **14%** field rate and ~100 eligible kills/night yields **~3.27 books** (still above the 1–2 band). Spawner/trial stay at **2%**. Tune `elites.spawn_chance` after UAT if the night feels too generous. See trial log for sign-off.

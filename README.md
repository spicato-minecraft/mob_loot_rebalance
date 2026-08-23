# Mob Combat Rebalance

Fabric mod for Minecraft **1.21.11** that makes combat farming feel more rewarding through occasional elite mobs with vanilla-only bonus loot. Optional baseline XP uplift for hostiles is available in config but off by default.

## Compatibility

| Component     | Version           |
|---------------|-------------------|
| Minecraft     | 1.21.11           |
| Fabric Loader | 0.19.3+           |
| Fabric API    | 0.141.6+1.21.11   |
| Java          | 21+               |

## Build

From this directory:

```bash
./gradlew build
```

The output JAR is at `build/libs/mob_combat_rebalance-1.1.0.jar` (version from `gradle.properties`).

## Test

```bash
./gradlew test          # unit tests (JUnit + Fabric Loader bootstrap)
./gradlew runGameTest   # server GameTests
./gradlew build         # full CI gate (includes both)
```

## Install

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.11.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 1.21.11 and place it in your mods folder.
3. Copy the built JAR into your Minecraft `mods` folder.

## Branch Policy

Each supported Minecraft version has its own long-lived branch named after the exact `minecraft_version` in `gradle.properties`. Version branches are the source of truth; `main` is not used.

- **Default branch:** `1.21.11`
- **Feature/fix work:** branch from the target version as `{version}/feature-name`, merge back into the version branch

```bash
git clone https://github.com/spicato-minecraft/mob_combat_rebalance.git
cd mob_combat_rebalance
git checkout 1.21.11
```

## Releases and Modrinth

Merging code does **not** publish to Modrinth. Publishing happens only when a GitHub Release is created.

Release tags use `{minecraft_version}-{mod_version}` (e.g. `1.21.11-1.0.0`), matching `gradle.properties`.

1. Merge your changes into the version branch (e.g. `1.21.11`).
2. Bump `mod_version` in `gradle.properties` if shipping new player-facing changes.
3. Add a changelog at `releases/{tag}.md` — see `releases/TEMPLATE.md`.
4. Create a GitHub Release with tag `{minecraft_version}-{mod_version}` targeting the version branch.

See [minecraft-mods/docs/README.md](../docs/README.md) for org-wide release conventions.

## Test Strategy

| Layer      | Purpose                                              |
|------------|------------------------------------------------------|
| Unit tests | Config math, kill-method routing, weight validation  |
| GameTests  | Elite spawn rules, player-kill payouts, loot paths   |

Behavior tests are tracked in [Notion Tasks](https://app.notion.com/p/3b716e8d4da380adbd93cf0a73b39d5c). Manual UAT items live in the Mod Catalog.

## Architecture

Server-side config-driven combat rebalance: optional baseline XP uplift for hostiles (off by default), elite spawn rolls at natural spawn time, and additional elite loot-table generates on player kills. No new items or UI — feedback uses vanilla particles, sounds, and XP orbs.

Epic and acceptance criteria: [Mob combat rebalance (vanilla-friendly overhaul)](https://app.notion.com/p/3bb16e8d4da3811796a7de1b58b9fc33).

### Elite spawn reason policy (AC-23 spike)

Minecraft **1.21.11** exposes spawn provenance as `EntitySpawnReason` (not the older `MobSpawnType` name). The reason is available on the server when a mob finishes spawning via `Mob.finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason, SpawnGroupData)`.

**v1 locked classification:**

| EntitySpawnReason | Elite roll |
|-------------------|------------|
| `NATURAL` | Allow |
| `CHUNK_GENERATION` | Allow |
| `STRUCTURE` | Deny |
| `TRIGGERED` | Deny |
| `SPAWNER` | Deny |
| `TRIAL_SPAWNER` | Deny |
| `REINFORCEMENT` | Deny |
| `PATROL` | Deny |
| `CONVERSION` | Deny |
| `EVENT` | Deny |
| All others (`BREEDING`, `MOB_SUMMONED`, `JOCKEY`, `BUCKET`, `SPAWN_ITEM_USE`, `COMMAND`, `DISPENSER`, `LOAD`, `DIMENSION_TRAVEL`) | Deny |

Vanilla provides `EntitySpawnReason.isSpawner(reason)`, which returns true for `SPAWNER` and `TRIAL_SPAWNER`. Policy is implemented in `EliteSpawnPolicy.isEligibleForEliteRoll(EntitySpawnReason)` with an allow-list of two reasons; everything else denies.

**Phase 3 hook point:** mixin at the tail of `Mob.finalizeSpawn`, after vanilla spawn setup, gated by:

1. `EliteSpawnPolicy.isEligibleForEliteRoll(spawnReason)`
2. mob-type eligibility (12 core overworld hostiles)
3. not a baby mob
4. config elite spawn rate &gt; 0

No Fabric event exposes spawn reason directly; a server mixin is the reliable hook. **Spike result: no blockers.**

Elite visual cues: **1.3× scale** plus sparse **red dust specks** (`minecraft:dust`, default color `0xFFFF0000`, scale `0.4`). Other simple particles still work via `elites.ambient_particle` (e.g. `minecraft:soul`). Optional `elites.uat_glow` re-enables outline glow for spawn-rate debugging. Stats remain 1.5× health/damage, light knockback resistance, and creeper explosion scaling.

### XP uplift pillar (Phase 2)

Player-killed hostiles (`instanceof Enemy`, excluding Wither, Ender Dragon, and Warden) receive ~1.35× vanilla XP orbs when `xp.enabled` is true. This pillar is **off by default**; set `xp.enabled` to `true` to opt in. Relative XP tiers are preserved by scaling the final `LivingEntity.getExperienceReward` result (after vanilla looting/enchantment processing). Spawner and trial-spawner mobs are eligible for uplift; elite spawn is a separate pillar.

**Hook point:** `@Inject` at `RETURN` of `LivingEntity.getExperienceReward`, gated by `XpUpliftPolicy` and `ModConfig.isXpPillarActive()`. Player-kill credit uses `getLastHurtByPlayer()` / `getKillCredit()`.

**Phase 3 GameTest backlog (spawn-reason slice):**

| Case | Spawn source | Expected |
|------|--------------|----------|
| Natural hostile | dark-area natural spawn | elite roll allowed (force 100% in test config) |
| Monster spawner | `SPAWNER` | never elite |
| Trial spawner | `TRIAL_SPAWNER` | never elite |
| Reinforcement | zombie reinforcement | never elite |
| Patrol | pillager patrol | never elite |
| Conversion | e.g. drowned from zombie | never elite |
| Event / summoned | raid wave / `/summon` | never elite |

Baby-mob exclusion and mob-type eligibility are separate checks (not spawn-reason), covered in Phase 3 spawn tests.

### Config (Phase 1)

Server-side JSON at `config/mob_combat_rebalance.json`. Defaults are written on first run; missing keys merge from defaults on load.

| Section | Keys | Default |
|---------|------|---------|
| `xp` | `enabled`, `multiplier` | `false`, `1.35` |
| `elites` | `enabled`, `spawn_chance`, `ambient_particle`, `particle_dust_color`, `particle_dust_scale`, `particle_interval_ticks`, `uat_glow` | `true`, `0.07`, `minecraft:dust`, `0xFFFF0000`, `0.4`, `30`, `false` |
| `elite_loot` | `enabled` | `true` |
| `kill_method` | `lookback_ticks` | `100` |
| `combat_potion` | `allow_invisibility` | `false` |
| `feedback` | `elite_kill_sound` | `minecraft:block.amethyst_block.chime` |

Pillar helpers: `ModConfig.isXpPillarActive()`, `isElitesPillarActive()`, `isEliteLootPillarActive()`. GameTests use `ModConfig.setForTest()`.

### Elite loot tables (Phase 5)

Default elite tables live under `data/mob_combat_rebalance/loot_table/elite/`. Each mob table is one pool with `rolls: 1` and integer weights summing to **100** (weight = percent).

Shared pools:

| Table | Role |
|-------|------|
| `enchanting_book_roll` | Stub table; kill-method sub-pools (`pools/armor`, `melee`, `ranged`, `mending`, `fallback`) are rolled in Java at elite death. |
| `combat_potion` | Shared splash combat potions (invisibility excluded by default config). |
| `pools/*` | Level I combat enchant books only — no mining, fishing, exploration, or Wind Burst enchants. |

Per-mob tables reference `mob_combat_rebalance:elite/enchanting_book_roll`, `mob_combat_rebalance:elite/combat_potion`, and `minecraft:entities/<mob>` for the vanilla consolation roll (weight 30).

### Kill-method routing (Phase 4)

Player-attributed damage on hostiles is recorded in a per-entity rolling history (`LivingEntityDamageMixin` → `PlayerDamageHistory`). On elite kill, `EliteLootService` calls the router to pick an enchanting-book sub-pool.

**Classification (`KillMethodClassifier`):**

| KillMethod | Sources |
|------------|---------|
| `MELEE` | Sword, axe, mace, spear jab/charge, fist, trident melee |
| `RANGED` | Bow/crossbow arrows, thrown trident, thrown spear |
| `FALLBACK` | No qualifying player damage in lookback (`kill_method.lookback_ticks`, default 100) |

Fire burn-out (`on_fire`, `in_fire`, etc.) does not append history; the most recent bow/melee hit inside the window is credited instead.

**Router API:**

```java
KillMethod method = KillMethodRouter.resolveKillMethod(victim, killer, ModConfig.get());
Identifier poolId = KillMethodRouter.resolve(victim, killer, ModConfig.get(), serverLevel.getRandom());
```

Weighted rolls: **melee** → armor 4, melee 4, mending 1; **ranged** → armor 1, ranged 2, mending 1 (~2:1 ranged:armor); **fallback** → `elite/pools/fallback`. Enchant lists remain in JSON pools only — the router returns pool ids, not enchant ids.

### Elite payouts + feedback (Phase 7)

Player-killed elites grant three payouts gated by `ElitePayoutPolicy`:

1. **Bonus XP** — `3 ×` the mob's uplifted reward (on top of the Phase 2 baseline uplift). Non-player kills get vanilla XP only.
2. **Elite-table loot** — exactly one roll from `mob_combat_rebalance:elite/<mob>` (Phase 6).
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
| `balance/HuntingNightCadenceModel` | Expected books = `kills × spawn_chance × 0.25` |

Pre-flight analysis at default **7%** and ~100 eligible kills/night yields **~1.75 books** (within the 1–2 band). Tune `elites.spawn_chance` in config if your kill rate differs. See trial log for sign-off.

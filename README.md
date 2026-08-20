# Mob Combat Rebalance

Fabric mod for Minecraft **1.21.11** that makes combat farming feel more rewarding through higher baseline XP and occasional elite mobs with vanilla-only bonus loot.

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

The output JAR is at `build/libs/mob_combat_rebalance-1.0.0.jar` (version from `gradle.properties`).

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

Server-side config-driven combat rebalance: baseline XP uplift for hostiles, elite spawn rolls at natural spawn time, and additional elite loot-table generates on player kills. No new items or UI — feedback uses vanilla particles, sounds, and XP orbs.

Epic and acceptance criteria: [Mob combat rebalance (vanilla-friendly overhaul)](https://app.notion.com/p/3bb16e8d4da3811796a7de1b58b9fc33).

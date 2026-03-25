# Tab Ping — Fabric Mod for Minecraft

## Rules

- Never include Claude as a co-author in commit messages
- Never advertise Claude Code in commit messages or PR descriptions

## Project Overview

Client-side Fabric mod that replaces the vanilla ping bars in the player tab list with numeric ping values (in milliseconds), color-coded by latency.

## Tech Stack

| Component       | Version                    |
|-----------------|----------------------------|
| Minecraft       | 26.1                       |
| Fabric Loader   | 0.18.4                     |
| Fabric Loom     | 1.15-SNAPSHOT              |
| Fabric API      | 0.144.0+26.1               |
| Java            | 25                         |
| Mappings        | Mojang (official)          |

## Project Structure

```
src/main/java/net/natxo/pingtabmod/
├── PingTabMod.java              # Server entrypoint (ModInitializer, minimal)
├── PingTabModClient.java        # Client entrypoint (dev commands)
├── DevPingManager.java          # Dev-only ping simulation utility
└── mixin/
    └── PlayerListHudMixin.java  # Core: replaces ping bars with numbers

src/main/resources/
├── fabric.mod.json
├── pingtabmod.client.mixins.json  # Client-side mixins (PlayerListHudMixin)
├── pingtabmod.accesswidener       # Makes PlayerTabOverlay extendable
└── assets/pingtabmod/icon.png
```

## Inspecting Minecraft API

Use `javap` to inspect Minecraft classes from the remapped JAR:

```bash
# Find the remapped JAR path
find .gradle/loom-cache -name "*.jar" | grep minecraft

# Inspect a class
javap -p -classpath <jar-path> <fully.qualified.ClassName>

# Search for methods
javap -p -classpath <jar-path> <ClassName> 2>&1 | grep -i "methodName"
```

## Build & Run

```bash
./gradlew build         # Compile and produce mod JAR (build/libs/)
./gradlew runClient     # Launch client with mod loaded
```

## How It Works

1. **PlayerListHudMixin** intercepts the vanilla tab list rendering:
   - `increaseEntryWidth()` — adds 30px extra width via `@ModifyArg` on `Math.min` in extractRenderState()
   - `renderPingAsNumber()` — cancels `extractPingIcon` and draws numeric ping instead
2. **Color coding:** Green (<150ms), Yellow (<300ms), Light red (<600ms), Dark red (>=600ms)
3. **Dev mode:** `/devping <ms>` and `/devping off` commands (only in dev environment)

## Key Mixin Target

The critical mixin target is `PlayerTabOverlay`:
- `extractRenderState()` method — for width modification
- `extractPingIcon()` method — for replacing ping bars with numbers

When updating MC versions, these methods MUST be verified with `javap` as they are the most likely to change signatures.

## Publishing & Releases

- **Modrinth:** https://modrinth.com/mod/tab-ping
- **GitHub:** https://github.com/Natxo09/tab-ping

### How to publish a new version

1. Update `mod_version` in `gradle.properties`
2. Create an annotated tag with the changelog as the message:
   ```bash
   git tag -a v1.0.0 -m "Summary of this release

   - Change 1
   - Change 2"
   ```
3. Push the tag: `git push origin v1.0.0`
4. The CI workflow automatically:
   - Builds the mod
   - Publishes to Modrinth with the tag message as changelog
   - Creates a GitHub Release with the JAR attached

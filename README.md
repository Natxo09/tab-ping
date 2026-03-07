# Tab Ping

A client-side Fabric mod that replaces the vanilla ping bars in the player tab list with numeric ping values (in milliseconds), color-coded by latency.

## Features

- [x] Replaces ping bar icons with exact millisecond values
- [x] Color-coded by latency:
  - Green: < 150ms
  - Yellow: < 300ms
  - Light red: < 600ms
  - Dark red: >= 600ms
- [x] Client-side only — no server installation required
- [x] Compatible with vanilla servers

## Requirements

| Component       | Version        |
|-----------------|----------------|
| Minecraft       | 1.21.10        |
| Fabric Loader   | >= 0.18.4      |
| Fabric API      | Required       |
| Java            | >= 21          |

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 1.21.10
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest release from [Modrinth](https://modrinth.com/mod/tab-ping) or [GitHub Releases](https://github.com/Natxo09/tab-ping/releases)
4. Place the `.jar` file in your `.minecraft/mods/` folder

## Building from source

```bash
git clone https://github.com/Natxo09/tab-ping.git
cd tab-ping
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## License

[MIT](LICENSE)

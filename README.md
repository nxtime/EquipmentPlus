# EquipmentPlus

Armor durability HUD for Hytale servers with planned auto-repair functionality.

## Features

### ✅ Armor Durability HUD

- Displays armor icons with durability percentages
- Color-coded indicators (green → yellow → red based on durability)
- Configurable position (left or right side)
- Works with native Hytale UI or [MultipleHUD](https://www.curseforge.com/hytale/mods/multiplehud)
- Event-based updates for optimal performance

### 🔄 Ammo Counter

- Displays arrow count from inventory
- Automatic updates when picking up/using arrows

### 🚧 Auto-Repair (Work in Progress)

- *Coming Soon*: Automatically repair items when they break
- *Coming Soon*: Uses Hytale's repair kit item

## Installation

1. Download the latest `.jar` from [Releases](https://github.com/nxtime/EquipmentPlus/releases)
2. Place in your server's `plugins` folder
3. Restart the server

### Building from Source

```bash
# Copy HytaleServer.jar to lib folder first
mvn clean package
```

## Commands

| Command | Description |
|---------|-------------|
| `/equipmentplus` | Opens the settings GUI |
| `/equipmentplus togglehud` | Toggle HUD visibility |
| `/equipmentplus position` | Toggle HUD position (left/right) |
| `/equipmentplus reload` | Reload configuration |

## Configuration

Settings are stored in `plugins/EquipmentPlus/config.json`:

```json
{
  "hudEnabled": true,
  "hudPosition": "left",
  "hudUpdateTicks": 200
}
```

| Setting | Default | Description |
|---------|---------|-------------|
| `hudEnabled` | `true` | Enable/disable durability HUD |
| `hudPosition` | `"left"` | HUD position: `"left"` or `"right"` |
| `hudUpdateTicks` | `200` | Backup HUD refresh rate in ticks (events handle most updates) |

## Dependencies

- **Optional**: [MultipleHUD](https://www.curseforge.com/hytale/mods/multiplehud) - For HUD compatibility with other mods

## Version History

See [CHANGELOG.md](CHANGELOG.md) for full version history.

**Latest: v0.1.4-alpha**

- Event-based HUD updates for better performance
- Native Hytale HUD support (no longer requires MultipleHUD)
- Fixed text truncation and color display issues

## Technical Details

- Uses `LivingEntityInventoryChangeEvent` for real-time updates
- Throttled updates (50ms) to prevent server overload
- Falls back to polling every 10 seconds as backup

## License

See [LICENSE](LICENSE) file for details.

## Credits

- **Author**: nxtime
- Built for the Hytale modding community

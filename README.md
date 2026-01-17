# EquipmentPlus

Armor durability HUD and auto-repair functionality for Hytale servers.

## Features

### Armor Durability HUD

- Displays armor icons with durability percentages
- Color-coded indicators (green > yellow > red)
- Configurable position (left or right side)
- Integrates with MultipleHUD if installed

### Auto-Repair

- Automatically repairs items when they break
- Uses Hytale's repair kit item
- Searches both main inventory and backpack
- Plays repair sound on success
- Consumes one repair kit per repair

## Installation

1. Download the latest `.jar` from releases
2. Place in your server's `plugins` folder
3. **Important**: Copy `HytaleServer.jar` to the `lib` folder before building
4. Restart the server

## Configuration

Settings are stored in `plugins/EquipmentPlus/config.json`:

```json
{
  "hudEnabled": true,
  "hudPosition": "left",
  "autoRepairEnabled": true,
  "hudUpdateTicks": 20
}
```

| Setting | Default | Description |
|---------|---------|-------------|
| `hudEnabled` | `true` | Enable/disable durability HUD |
| `hudPosition` | `"left"` | HUD position: "left" or "right" |
| `autoRepairEnabled` | `true` | Enable/disable auto-repair |
| `hudUpdateTicks` | `20` | HUD refresh rate (ticks) |

## Dependencies

- **Optional**: [MultipleHUD](https://www.curseforge.com/hytale/mods/multiplehud) - For HUD compatibility with other mods

## Building

```bash
# Copy HytaleServer.jar to lib folder first
mvn clean package
```

## Version History

**0.1.0-alpha** - Initial Release

- Armor durability HUD
- Auto-repair with repair kit

## License

See LICENSE file for details.

## Credits

- nxtime
- Built for the Hytale modding community

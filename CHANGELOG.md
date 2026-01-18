# Changelog

All notable changes to EquipmentPlus will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.1.2-alpha] - 2026-01-17

### Added

- **Hytale Settings-Style UI** - Complete visual overhaul matching native Hytale settings panels (accessible via `/equipmentplus`).
- **Interactive Toggles** - Enable/Disable HUD and switch screen positions directly from the new GUI.
- **ColorConfig Utility** - Centralized cozy pastel color palette for all chat messages, synchronized with HideArmor.
- **Command Overhaul** - Updated `/equipmentplus` to open the GUI by default and use premium chat formatting.

### Fixed

- **Critical Crash** - Resolved "Object reference not set to an instance of an object" error when toggling HUD off.
- **MultipleHUD Compatibility** - Fixed path mismatch in HUD registration that led to client-side inconsistencies.

### Technical

- **Cleanup Logic** - Separated native HUD clearing from MultipleHUD unregistration to prevent client-side race conditions.
- **Safety Check** - Ensured unregistration uses unique file-path keys to avoid accidentally hiding HUDs from other mods.

---

## [0.1.0-alpha] - 2026-01-17

### Added

- **First Implementation**
  - Armor Durability HUD (Helmet, Chestplate, Gauntlets, Leggings).
  - Dynamic Durability Bars (Green > 50%, Yellow > 25%, Red < 25%).
  - Ammo Counter (Automatic arrow detection in inventory).
  - Configurable Anchoring (Left/Right screen positioning).
- **Persistence** - JSON-based configuration storage.

### Known Issues

- **Auto Repair Kit** - Feature is currently non-functional and is planned for a future update.

---

## Version Number Scheme

Format: `MAJOR.MINOR.PATCH-alpha`

- **MAJOR:** Breaking changes or complete rewrites
- **MINOR:** New features, backward compatible
- **PATCH:** Bug fixes and small improvements
- **-alpha:** Pre-release status for Hytale Early Access

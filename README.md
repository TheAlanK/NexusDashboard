# NexusDashboard

A **fleet dashboard** mod for [Starsector](https://fractalsoftworks.com/) built on the [NexusUI](https://github.com/TheAlanK/NexusUI) framework. Displays a visual overview of your fleet composition, combat readiness, faction relations, cargo, and colonies in an in-game overlay panel.

![Starsector 0.98a-RC7](https://img.shields.io/badge/Starsector-0.98a--RC7-blue)
![Version 0.9.0-beta](https://img.shields.io/badge/Version-0.9.0--beta-orange)
![License: MIT](https://img.shields.io/badge/License-MIT-green)

## Features

- **Fleet Composition** — Ship count breakdown by hull size (Capital, Cruiser, Destroyer, Frigate)
- **Combat Readiness** — CR bars for each ship with color-coded status
- **Faction Relations** — Visual relation bars for all known factions
- **Cargo Overview** — Top commodities with quantity bars
- **Colony Summary** — Population size, stability, and income for each colony

## Installation

1. Install [LazyLib](https://fractalsoftworks.com/forum/index.php?topic=5444.0)
2. Install [NexusUI](https://github.com/TheAlanK/NexusUI)
3. Download the latest release or clone this repository
4. Copy the `NexusDashboard` folder into your `Starsector/mods/` directory
5. Enable **NexusDashboard** in the Starsector launcher

## Usage

1. Load a save game with all three mods enabled
2. On the campaign screen, click the floating **N** button (or drag it to reposition)
3. The overlay opens with a **Fleet Dashboard** tab showing your fleet data
4. Data refreshes automatically via the NexusUI bridge API

## Dependencies

- [LazyLib](https://fractalsoftworks.com/forum/index.php?topic=5444.0)
- [NexusUI](https://github.com/TheAlanK/NexusUI) v0.9.0+

## Building from Source

Requires JDK 8+ and `NexusUI.jar` on the classpath. Run `build.bat` in the mod root.

## License

[MIT](LICENSE)

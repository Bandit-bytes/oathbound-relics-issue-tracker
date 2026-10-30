# Oathbound Relics

Cursed curios, powerful relics, unique weapons, and progression tied to the **Oathbound Relic**.
Become Branded, survive its penalties, and unlock dangerous new abilities, effects, and gear through long-term sacrifice.

**Minecraft 1.21.1 · NeoForge**

## Features

### The Oath
Equip the **Oathbound Relic** to become **Branded**. The Oath gives strong blessings and harsh curses:

- **Curses:** Frailty, Provocation, Shattered Plate, Blood Toll, Living Ember, Soul Fracture, and Wakeful Doom
- **Blessings:** bonus Looting, Fortune, experience, enchanting power, attack speed, and emergency absorption

The relic stays with you through death. To end the Oath, you must survive the **Rite of Severance**.

### Relics & Curios
- **Bound curios:** relics that only awaken for the Branded, often working with the Oath's own curses
- **Free-bound relics:** relics anyone can wear
- **Bearer curios:** heavier relics with build-defining passives and sharp tradeoffs
- **Rings:** Gold, Cyan, and Nebula rings for steady armor and extra ring slots

### Titan's Remnants
Six remnants (Colossus Heart, Ember Seed, Tide Pearl, Skybrand Feather, Nebula Lens, and Void Pearl) grow through six stages, from Dormant to Apex. Higher stages need the Oath, and Apex needs long service to it.

### Sin Weapons
Forge weapons of Sloth, Pride, Envy, and Wrath from tablets and idols: the **Lethargic Greatsword**, **Vanity's Edge**, **Covetfang**, and **Ruinwake**.

### Rituals
- **Rite of Severance:** tear the Oath free and destroy the relic, if you survive the rite
- **Rite of Repose:** a quiet altar that lets the Branded sleep nearby despite Wakeful Doom

### Also
- **Aredrite** gems, ore, and armor
- **Soul Lantern** and **Soul Gem**
- **Oblivion Stones** and **Void Ashes** from bosses and End creatures
- Relics hidden in structure loot across the Overworld, Nether, and End

### The Oathbound Codex
An in-game Patchouli guide explains every relic, curse, ritual, and forging path. New players receive it on first join.

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1 or newer |
| [Curios API](https://www.curseforge.com/minecraft/mc-mods/curios) | 9.5.1 or newer |
| [Patchouli](https://www.curseforge.com/minecraft/mc-mods/patchouli) | 1.21.1 |

Install Oathbound Relics and its dependencies on both the client and the server.

## Configuration

Almost every curse, blessing, relic, weapon, and ritual can be toggled and tuned in the server config:

```
<world>/serverconfig/oathboundrelics-server.toml
```

To apply defaults to new worlds, place a copy in the `defaultconfigs` folder.

## Commands

`/oathbound branded_time` lets operators inspect and adjust a player's Branded time, which gates Apex remnants and some sin weapons.

## Building from Source

```
./gradlew build
```

The built jar is written to `build/libs/`.

## Reporting Issues

Please report bugs on the [issue tracker](https://github.com/Bandit-bytes/oathbound-relics-issue-tracker/issues).

## License

Copyright © 2026 Bandit Bytes. All Rights Reserved. See [LICENSE.md](LICENSE.md).

<p align="center"><img src="https://i.imgur.com/mBUvVNO.png" alt="Parkour Logo"></p>

[![discord server](https://img.shields.io/discord/328154925949517824.svg)](https://discord.gg/h9d2fSd)
[![travis-ci](https://travis-ci.org/A5H73Y/Parkour.svg?branch=master)](https://travis-ci.org/A5H73Y/Parkour/branches)
[![license: MIT](https://img.shields.io/badge/license-MIT-lightgrey.svg)](https://tldrlegal.com/license/mit-license)
[![releases](https://img.shields.io/github/v/release/A5H73Y/Parkour.svg?label=github%20release)](https://github.com/A5H73Y/Parkour/releases/latest)

Parkour is the original, most powerful Parkour based plugin available! 
First released in November 2012, and has been updated since. Parkour is now open source, allowing you to contribute ideas and enhancements, or create your own spin on the plugin.<p />
Add a whole new element of fun to any server, highly competitive gameplay with rewards and leaderboards.<p />

## Quick Links
- [Tutorials / Documentation](https://a5h73y.github.io/Parkour/)
- [Support Server (Discord)](https://discord.gg/Gc8RGYr)
- [Spigot Page](https://www.spigotmc.org/resources/parkour.23685/)
- [Bukkit Page (archive)](https://dev.bukkit.org/projects/parkour/)
- [Plugin Statistics](https://bstats.org/plugin/bukkit/Parkour)

## Supported plugins
| Plugin        | Description  |
| ------------- | ------------- |
| [Vault](https://dev.bukkit.org/projects/vault) | Add economy support to the plugin, reward or penalise the player. <br>[GitHub Project by MilkBowl](https://github.com/MilkBowl/Vault) |
| [BountifulAPI](https://www.spigotmc.org/resources/bountifulapi-1-8-1-9-1-10.1394/) | Add title and actionbar support to the plugin. Works very nicely with the plugin. <br>[GitHub Project by ConnorLinfoot](https://github.com/ConnorLinfoot/BountifulAPI) |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | Add placeholder support to the plugin. Allowing use of Parkour variables for external use. <br>[GitHub Project by clip](https://github.com/PlaceholderAPI/PlaceholderAPI) |
| [Parkour Top Ten](https://www.spigotmc.org/resources/parkour-top-ten.46268/) | Create a top ten display of player heads for Parkour courses​. <br>[GitHub Project by steve4744](https://github.com/steve4744/ParkourTopTen) |

## Maven
```
<repository>
    <id>a5h73y-repo</id>
    <url>https://dl.bintray.com/a5h73y/repo/</url>
</repository>
```

```
<dependency>
    <groupId>io.github.a5h73y</groupId>
    <artifactId>Parkour</artifactId>
    <version>6.5</version>
    <type>jar</type>
    <scope>provided</scope>
</dependency>
```

## Gradle
```
repositories { 
    maven { 
        url "https://dl.bintray.com/a5h73y/repo"
    } 
}
```

```
compile 'io.github.a5h73y:Parkour:6.5'
```

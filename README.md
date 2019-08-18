<p align="center"><img src="http://i.imgur.com/OayXKol.png" alt="Parkour Logo"></p>

[![travis-ci](https://travis-ci.org/A5H73Y/Parkour.svg?branch=master)](https://travis-ci.org/A5H73Y/Parkour/branches)
[![tutorials](https://img.shields.io/badge/tutorials-github-brightgreen.svg)](https://a5h73y.github.io/Parkour/)
[![bStats](https://img.shields.io/badge/statistics-bstats-brightgreen.svg)](https://bstats.org/plugin/bukkit/Parkour)
[![license: MIT](https://img.shields.io/badge/license-MIT-lightgrey.svg)](https://tldrlegal.com/license/mit-license)
[![repo](https://api.bintray.com/packages/a5h73y/repo/Parkour/images/download.svg)](https://bintray.com/a5h73y/repo/Parkour/_latestVersion)

Parkour is the original, most powerful Parkour based plugin available! 
First released in November 2012, and has been updated since. Parkour is now open source, allowing you to contribute ideas and enhancements, or create your own spin on the plugin.<p />
Add a whole new element of fun to any server, highly competitive gameplay with rewards and leaderboards.<p />

[<img src="https://i.imgur.com/aMmpMyj.png" alt="Discord Support">](https://discord.gg/Gc8RGYr)<p />

## Installation
* Install [Spigot](https://www.spigotmc.org/threads/buildtools-updates-information.42865/) _(1.8 to 1.14)._
* Download Parkour from [dev.bukkit.org/projects/parkour/files](https://dev.bukkit.org/projects/parkour/files)
* Place the _Parkour.jar_ into the _/plugins_ folder of the server.
* Start your server and check the server logs to ensure the plugin started successfully.
* Check the _config.yml_ and configure it to your preference before fully implementing the plugin.

## Supported plugins
| Plugin        | Description  |
| ------------- | ------------- |
| [Vault](https://dev.bukkit.org/projects/vault) | Add economy support to the plugin, reward or penalise the player. <br>[GitHub Project by MilkBowl](https://github.com/MilkBowl/Vault) |
| [BountifulAPI](https://www.spigotmc.org/resources/bountifulapi-1-8-1-9-1-10.1394/) | Add title and actionbar support to the plugin. Works very nicely with the plugin. <br>[GitHub Project by ConnorLinfoot](https://github.com/ConnorLinfoot/BountifulAPI) |
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
    <groupId>me.A5H73Y</groupId>
    <artifactId>Parkour</artifactId>
    <version>5.3</version>
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
compile 'me.A5H73Y:Parkour:5.3'
```

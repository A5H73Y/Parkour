Compatible Plugins
======

## Economy / Vault

Parkour supports the ability to use Economy through the Vault plugin, created by Sleaker.

The configuration option in the `config.yml` is `Other.Economy.Enabled` and must be set to `true`. When Parkour starts up, it will print a message whether it has connected to the Vault plugin successfully.

Once successfully linked, you are able to reward players with an amount of currency, as well as charge them for joining a Course.

If there are Courses that exist before the plugin was linked with Vault, these need adding to the economy.yml, which is made easy by entering `/pa econ recreate`.

Once the process is complete it will notify you how many Courses were updated. At this stage the economy.yml file should contain all the courses on your server, and a prize and fee set to 0.

The joining fee and reward amount can be set in game, using `/pa econ setprize (course) (amount)` and `/pa econ setfee (course) (amount)`. The amount is the amount of currency to add / charge.

## PlaceholderAPI

PlaceholderAPI allows you to use Parkour values within other plugins.  
_Please note that Parkour Expansion is no longer needed as it has been implemented within the Parkour plugin._

[All of the Parkour Placeholders are available here](essential/placeholders.md).


## Holographic Displays (v3.0.0+)

Allows you to create dynamic and nice looking Parkour holograms.

Holographic Displays now natively supports placeholders, so no other plugins are needed:
* [Holographic Displays](https://dev.bukkit.org/projects/holographic-displays?gameCategorySlug=bukkit-plugins&projectID=75097)


Note that Holographic Displays v3.0.0 is still in beta and has some known issues including:
_PlaceholderAPI's placeholders are refreshed every tick (configurable in the future) and they are not optimized yet._

Parkour will cache database results, but until Holographic Displays includes a refresh rate
_performance may be affected especially if there are slow placeholders._


Once the plugin has installed successfully, we can create a few examples of what is possible. For demonstration purposes, I will be using a Course named "tutorial".


### Parkour Leaderboards
First we create a new Parkour leaderboard Hologram using the command and giving it a title.  
`/hd create Leaderboard_tutorial Parkour Leaderboard - Tutorial`

Add a line for each position you want on the leaderboard (up to 10):

`/hd addline Leaderboard_tutorial {papi: parkour_topten_tutorial_1}`  
`/hd addline Leaderboard_tutorial {papi: parkour_topten_tutorial_2}`  
`/hd addline Leaderboard_tutorial {papi: parkour_topten_tutorial_3}`

Above we are using the Parkour placeholder `%parkour_topten_(course)_(position)%`.
There is an entry in the `strings.yml` named `PlaceholderAPI.TopTenResult` which will allow you to customise the appearance and colours used.

### Parkour Course Best Player
First we create a new Parkour leader Hologram using the command and giving it a title.  
`/hd create Leader_tutorial Parkour Leader - Tutorial`

Add a line for each detail you want to display:

`/hd addline Leader_tutorial Best Player: {papi: parkour_leaderboard_tutorial_1_player}`  
`/hd addline Leader_tutorial Time: {papi: parkour_leaderboard_tutorial_1_time}`  
`/hd addline Leader_tutorial Deaths: {papi: parkour_leaderboard_tutorial_1_deaths}`


## Holographic Displays (v2.x.x)

Allows you to create dynamic and nice looking Parkour holograms.

You will need to install a few plugins to achieve this:
* [Holographic Displays](https://dev.bukkit.org/projects/holographic-displays?gameCategorySlug=bukkit-plugins&projectID=75097)
* [Holographic Extension](https://www.spigotmc.org/resources/holographic-extension.18461/)
* [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

Once the plugins have all installed successfully, we can create a few examples of what is possible. For demonstration purposes, I will be using a Course named "tutorial".

Parkour will cache database results, so we still suggest to using the `slow` refresh rate. The supported refresh rates are:
* `{slowest}` - 10 seconds
* `{slow}` - 5 seconds
* `{medium}` - 1 second
* `{fast}` - 0.5 seconds
* `{fastest}` - 0.1 seconds

### Parkour Leaderboards
First we create a new Parkour leaderboard Hologram using the command and giving it a title.  
`/hd create Leaderboard_tutorial Parkour Leaderboard - Tutorial`

Add a line for each position you want on the leaderboard (up to 10):

`/hd addline Leaderboard_tutorial {slow}%parkour_topten_tutorial_1%`  
`/hd addline Leaderboard_tutorial {slow}%parkour_topten_tutorial_2%`  
`/hd addline Leaderboard_tutorial {slow}%parkour_topten_tutorial_3%`

Above we are using the Parkour placeholder `%parkour_topten_(course)_(position)%`.
There is an entry in the `strings.yml` named `PlaceholderAPI.TopTenResult` which will allow you to customise the appearance and colours used.

### Parkour Course Best Player
First we create a new Parkour leader Hologram using the command and giving it a title.  
`/hd create Leader_tutorial Parkour Leader - Tutorial`

Add a line for each detail you want to display:

`/hd addline Leader_tutorial {slow}Best Player: %parkour_leaderboard_tutorial_1_player%`  
`/hd addline Leader_tutorial {slow}Time: %parkour_leaderboard_tutorial_1_time%`  
`/hd addline Leader_tutorial {slow}Deaths: %parkour_leaderboard_tutorial_1_deaths%`


## Parkour Top Ten

Allows for the player's Head to be proudly displayed next to their best times, great for a competitive Parkour server.

![Parkour Top Ten](https://i.imgur.com/c2n6QUM.png "Parkour Top Ten")

Plugin and image created by steve4744, available here: [https://www.spigotmc.org/resources/parkour-top-ten.46268/](https://www.spigotmc.org/resources/parkour-top-ten.46268/)

## PlateCommands

PlateCommands was created as an extension to Parkour with more powerful functionality. A pressure plate can perform command(s) that can be executed by the server or player.

### Multiple acting Checkpoints
Multiple pressure plates can achieve the same Checkpoint, instead of the limitation of a single pressure plate in Parkour:

`/pc create pac setcheckpoint %player% 1`

### Teleport the Player
Whilst on a Course you may want the Player to be teleported to a different location, this can be done using:

`/pc create tp %player% x y z`

### Other Player actions
You can let the Player enter pre-determined commands using the "player:" prefix in the command which will execute the command as if the Player entered it.
An example could be walking on a pressure plate to leave the Course:

`/pc create player:pac leave %player%`

Plugin created by A5H73Y, available here: [https://www.spigotmc.org/resources/platecommands.90578/](https://www.spigotmc.org/resources/platecommands.90578/).

## LeaderHeads

Leaderheads is a plugin that allows you to create all-time, daily, weekly, and monthly leaderboards from placeholders that return a numeric value.

When used with Parkour, course leaderboards can be created using placeholder `%parkour_player_personal_best_(course)_milliseconds%`

### Creating a LeaderHeads Sign
First place a sign, and then while looking at the sign, type:

`/leaderheads setsign %parkour_player_personal_best_tutorial_milliseconds% 1 weekly`

In this example the sign will update to show the number 1 ranked player on course 'tutorial' for the week with the time displayed as milliseconds.

![LeaderHeads Example 1](https://i.imgur.com/LTJ9Dw3.png "LeaderHeads Example 1")

In the file `statistics/parkour_player_personal_best_tutorial_milliseconds.yml`, set the statistic type to `time-milliseconds` and the order to `ascending`:

```
statistic-type: time-milliseconds
order-mode: ascending
```

Reload LeaderHeads, and the sign will update to display a formatted time.

![LeaderHeads Example 2](https://i.imgur.com/swbtPkt.png "LeaderHeads Example 2")

The time format can be changed in LeaderHeads's `config.yml`, for example:

`time-format: "{hours}:{minutes}:{seconds}"`

![LeaderHeads Example 3](https://i.imgur.com/XzwLLSL.png "LeaderHeads Example 3")

Currently, LeaderHeads does not appear to support displaying milliseconds as part of the formatted time on the sign.

Plugin created by RobiRami, available here: [https://www.spigotmc.org/resources/leaderheads.2079/](https://www.spigotmc.org/resources/leaderheads.2079/).

Compatible Plugins
======

## Economy / Vault

Parkour supports the ability to use Economy through the Vault plugin, created by Sleaker.

The configuration option in the `config.yml` is `Other.Economy.Enabled` and must be set to `true`. When Parkour starts up, it will print a message whether it has connected to the Vault plugin successfully.

Once successfully linked, you are able to reward players with an amount of currency, as well as charge them for joining a Course.

If there are Courses that exist before the plugin was linked with Vault, these need adding to the economy.yml, which is made easy by entering `/pa econ recreate`.

Once the process is complete it will notify you how many Courses were updated. At this stage the economy.yml file should contain all the courses on your server, and a prize and fee set to 0.

The joining fee and reward amount can be set in game, using `/pa econ setprize (course) (amount)` and `/pa econ setfee (course) (amount)`. Amount being the amount of currency to add / retract.

## PlaceholderAPI

PlaceholderAPI allows you to use Parkour values within other plugins.  
_Please note that Parkour Expansion is no longer needed as it has been implemented within the Parkour plugin._

### Parkour Placeholders

Here are each of the available Placeholders that Parkour provides.

```
%parkour_global_version%
%parkour_global_course_count%
%parkour_global_player_count%
%parkour_player_level%
%parkour_player_rank%
%parkour_player_parkoins%
%parkour_player_last_completed%
%parkour_player_last_joined%
%parkour_player_courses_completed%
%parkour_player_courses_uncompleted%
%parkour_player_prize_delay_(course)%
%parkour_player_personal_best_(course)_time%
%parkour_player_personal_best_(course)_deaths%
%parkour_course_record_player_(course)%
%parkour_course_record_time_(course)%
%parkour_course_record_deaths_(course)%
%parkour_course_completed_(course)%
%parkour_course_completions_(course)%
%parkour_course_views_(course)%
%parkour_current_course_name%
%parkour_current_course_deaths%
%parkour_current_course_timer%
%parkour_current_course_checkpoints%
%parkour_current_course_completed%
%parkour_current_course_record_player%
%parkour_current_course_record_time%
%parkour_current_course_record_deaths%
%parkour_current_course_personal_best_time%
%parkour_current_course_personal_best_deaths%
%parkour_current_checkpoint%
%parkour_leaderboard_(course)_(position)_player%
%parkour_leaderboard_(course)_(position)_time%
%parkour_leaderboard_(course)_(position)_deaths%
%parkour_topten_(course)_(position)%
```

## Holographic Displays

Allows you to create dynamic and nice looking Parkour holograms.

You will need to install a few plugins to achieve this:
* [Holographic Displays](https://dev.bukkit.org/projects/holographic-displays?gameCategorySlug=bukkit-plugins&projectID=75097)
* [Holographic Extension](https://www.spigotmc.org/resources/holographic-extension.18461/)
* [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

Once the plugins have all installed successfully, we can create a few examples of what is possible. For demonstration purposes, I will be using a Course named "tutorial".

Parkour will cache these results, we still suggest to using the `slow` refresh rate. The supported refresh rates are:
* `{slow}` - 10 seconds
* `{medium}` - 1 seconds
* `{fast}` - 0.1 seconds

### Parkour Leaderboards
First we create a new Parkour leaderboard Hologram using the command and giving it a title.  
`/hd create Leaderboard_tutorial Parkour Leaderboard - Tutorial`

Add a line for each position you want on the leaderboard (up to 10):

`/hd addline Leaderboard_tutorial {slow}%parkour_topten_tutorial_1%`  
`/hd addline Leaderboard_tutorial {slow}%parkour_topten_tutorial_2%`  
`/hd addline Leaderboard_tutorial {slow}%parkour_topten_tutorial_3%`

Above we are using the Parkour placeholder `%parkour_topten_(course)_(position)`.
There is an entry in the `strings.yml` named `PlaceholderAPI.TopTenResult` which will allow you to customise the appearance and colours used.

### Parkour Course Best Player
First we create a new Parkour leader Hologram using the command and giving it a title.  
`/hd create Leader_tutorial Parkour Leader - Tutorial`

Add a line for each detail you want to display:

`/hd addline Leader_tutorial {slow}Best Player: %parkour_leaderboard_tutorial_1_player%`  
`/hd addline Leader_tutorial {slow}Time: %parkour_leaderboard_tutorial_1_time%`  
`/hd addline Leader_tutorial {slow}Deaths: %parkour_leaderboard_tutorial_1_deaths%`

## BountifulAPI

BountifulAPI supports the ability to display titles and various other notifications on older server versions, rather than printing the messages into the chat.

_Spigot's implementation will be attempted first, then fallback to BountifulAPI if installed._

![Bountiful Example 1](https://i.imgur.com/E8BighB.png "Bountiful Example 1")
![Bountiful Example 2](https://i.imgur.com/fDsUmHV.png "Bountiful Example 2")
![Bountiful Example 3](https://i.imgur.com/bRvhdp8.png "Bountiful Example 3")

Created by connorlinfoot, available here: https://www.spigotmc.org/resources/bountifulapi-1-8-1-13.1394/


## Parkour Top Ten

Allows for the player's Head to be proudly displayed next to their best times, great for a competitive Parkour server.

![Parkour Top Ten](https://i.imgur.com/c2n6QUM.png "Parkour Top Ten")

Plugin and image created by steve4744, available here: https://www.spigotmc.org/resources/parkour-top-ten.46268/


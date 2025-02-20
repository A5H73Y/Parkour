Courses
======

A Parkour Course is a physical path that you've created for the Player to use while using the plugin. A Player can join a Course (a.k.a. level, arena, track), and the plugin will track the Player's progress and apply any effects they interact with.

## Course Settings GUI

You are able to quickly access the Course settings using a GUI, this will act as a shortcut of using traditional commands. Each Item in the GUI will act as either a setting toggle, or start a new conversation to take user input for a new value.

![Course Settings Toggle](https://i.imgur.com/s0CANvK.png "Course Settings Toggle")
![Course Settings Conversation](https://i.imgur.com/5YahlJI.png "Course Settings Conversation")

_Command: `/pa settings (course)`_

## Course Settings

### challengeonly

Ability to prevent a Player from joining a Course unless they are currently part of a Challenge. The Player will be notified upon trying to join the Course that they must be in a Challenge to join.

_Command: `/pa setcourse (course) challengeonly (true/false)`_

### creator

Each Course must have a Creator, by default this will be set to the Player who used the create Course command.
Overwriting the creator of a Course to a different Player, will allow the specified Player to have elevated permissions for that Course.

_Command: `/pa setcourse (course) creator (player)`_

### dieinlava

If the Player were to enter lava, should this be considered a death.

_Command: `/pa setcourse (course) dieinlava [true/false]`_

### dieinwater

If the Player were to enter water, should this be considered a death.

_Command: `/pa setcourse (course) dieinwater [true/false]`_

### dieinvoid

If the Player were to enter the void should this be considered a death.

_Command: `/pa setcourse (course) dieinvoid [true/false]`_

### displayname

Each Course has the ability to have a display name. As Course names have to be a unique and strict format to be valid, you are able to set the display name to any text you like, including spaces or color symbols. The Course's display name will be displayed to the Player instead of the course name.

_Command: `/pa setcourse (course) displayname (value...)`_

### linkedcourse

Once the Player finishes a Course, you are able to make them join straight into a different Course.

_Command: `/pa setcourse (course) linkedcourse (course)`_

### linkedlobby

Once the Player finishes a Course, by default they are teleported to the default Lobby. This can be changed to the specified Lobby so the Player is teleported to that Lobby once they complete the Course.

By default, the Player will be teleported to the default lobby when they *leave* a Course, this can be changed in the config.yml by setting `OnLeave.TeleportToLinkedLobby` to `true`.

_Note that the Player must still abide to the minimum ParkourLevel requirement for the Lobby to successfully join it._

_Command: `/pa setcourse (course) linkedlobby (lobby)`_

### manualcheckpoints

When enabled, the Player's checkpoint position can be manually set whenever you want. This will allow the Player (and console) to use the `/pa manualcheckpoint` / `pac manualcheckpoint (player)` command, which sets the Player's current position to their Checkpoint. This can be used as an external way to set the Player's checkpoint position.

In addition, when `OnCourse.ManualCheckpointAnyPressurePlate` is enabled, any pressure plate the Player triggers whilst on a Course will set their checkpoint to that location.

Once activated, the Player will be notified that a checkpoint has been set. If the Player dies, they will be taken back to the last checkpoint they set. This allows you to create alternate routes through a Course.

_Command: `/pa setcourse (course) manualcheckpoints [true/false]`_

### maxdeaths

You are able to limit the amount of deaths a Player can accumulate before they are kicked off the Course, for extra challenge. An example could be a quick challenging Course that allows the Player just 1 life to complete.

_Command: `/pa setcourse (course) maxdeaths (amount)`_

### maxfallticks

You are able to limit the amount of ticks (time) a Player can fall for before they are killed.

Setting the value to 0 will disable the MaxFallTicks check and allow them to fall infinitely (this can be used in Dropper styled Courses).

_Command: `/pa setcourse (course) maxfallticks (amount)`_

### maxtime

Set a limit for the amount of time a Player can reach before they are kicked off the Course, for extra challenge. An example could be a challenging Course that must be completed within a certain time.

_Command: `/pa setcourse (course) maxtime (seconds)`_

### minimumlevel

Set the minimum required ParkourLevel to join the Course.  
_[More information on ParkourLevels.](/tutorials/parkour-level-ranks)_

_Command: `/pa setcourse (course) minimumlevel (level)`_

### parkourkit

Set the associated ParkourKit for the Course.  
_[More information on ParkourKits.](/tutorials/parkour-kits)_

_Command: `/pa setcourse (course) parkourkit (parkour-kit)`_

### parkourmode

Set the ParkourMode for the Course.  
_[More information on ParkourModes.](/tutorials/parkour-modes)_

_Command: `/pa setcourse (course) parkourmode (parkour-mode)`_

### playerlimit

Set a limit on the number of Players that can play the Course concurrently.

_Command: `/pa setcourse (course) playerlimit (amount)`_

### ready

By default, Players can not join a Course that has not been set to ready (so they don't join a half-finished Course), this can be disabled by changing `OnJoin.EnforceReady` to `false` in the `config.yml`.

Only Courses marked as Ready will be cached, to allow for better performance when Players join it.

_Command: `/pa setcourse (course) ready` OR `/pa ready [course]`_

### resumable

By default, a Player's progress will be deleted when they leave a Course. To retain the Player's progress set `OnLeave.DestroyCourseProgress` to `false` in the `config.yml`; each Course will then be "resumable" meaning they can leave at any point and rejoin back, which will restore their last achieved checkpoint with their accumulated time and amount of deaths.

However, you can toggle the resumable status of the Course to set a Course to be non-resumable to prevent their progress from being saved.

_Command: `/pa setcourse (course) resumable [true/false]`_

### rewarddelay

The amount of time that must pass before the Player can achieve the Course's prize again.  
The time can be a fraction of hours. For example, `0.5` would be `30 minutes`, `48` would be `2 full days`.

_Command: `/pa setcourse (course) rewarddelay (hours)`_

### rewardlevel

The ParkourLevel the Player should be set to for completing the Course.  
_[More information on reward ParkourLevels.](/tutorials/parkour-level-ranks?id=rewardlevel)_

_Command: `/pa setcourse (course) rewardlevel (level)`_

### rewardleveladd

The amount of ParkourLevel the Player should have added for completing the Course.  
_[More information on reward ParkourLevels.](/tutorials/parkour-level-ranks?id=rewardleveladd)_

_Command: `/pa setcourse (course) rewardleveladd (level)`_

### rewardonce

A Course can be set to give the Prize reward **only** for the first time they complete a Course. This will include any ParkourLevel rewards given, which is especially important if you've chosen to use "rewardleveladd" functionality.

_Command: `/pa setcourse (course) rewardonce [true/false]`_

### rewardparkoins

Parkoins are a currency within the Parkour plugin that can be configured like any other prize.

With the easy developer API, you can create a plugin that can build on top of Parkour that uses the currency. An example could be requiring a certain amount of Parkoins before a Course can be purchased to join.

_Command: `/pa setcourse (course) parkoins (amount)`_

### autostart

Create an AutoStart for the Course at the Player's current position.  
_[More information on AutoStarts.](/tutorials/parkour-autostart)_

_Command: `/pa setcourse (course) autostart`_

### prize

Begin a Conversation which will let you set the Course's prize, these can be stacked meaning you can include all options when the Player finishes a Course.

_Command: `/pa setcourse (course) prize`_

### rename

If you don't like the name of the Course you can rename it to something else, this will keep all the Course data and leaderboards intact.

Know that you can set a `displayname` to give the Course a better name for the Player to see if preferred, instead of renaming.

_Command: `/pa setcourse (course) rename (new-name)`_

### resetlink

To remove the linked Course or linked Lobby from a Course, reset the link. This will result in the Player being teleported to the default lobby on Course completion.

_Command: `/pa setcourse (course) resetlink`_

### start

You can change the starting position of a Course; where the command is executed is the exact position which will be saved.

_Command: `/pa setcourse (course) start`_

### message

You can override the default Parkour messages to a custom per-course message for each event.

[Internal Parkour placeholders](/tutorials/parkour-courses?id=parkour-internal-placeholders) AND [PlaceholderAPI](/guides/compatible-plugins?id=placeholderapi) values will be evaluated. [Available event values](/tutorials/parkour-courses?id=parkour-internal-events).

![Event Message Example](https://i.imgur.com/ZyOeOom.png "Event Message Example")

![Event Message Set](https://i.imgur.com/5pacqjk.png "Event Message Set")

_Command: `/pa setcourse (course) message (event) (event)`_

### command

Allow each Course to execute multiple commands for each Parkour event.

You are able to specify that the command should be executed by the Player by prefixing the command with `player:`, otherwise it will be executed as the Console.

[Internal Parkour placeholders](/tutorials/parkour-courses?id=parkour-internal-placeholders) AND [PlaceholderAPI](/guides/compatible-plugins?id=placeholderapi) values will be evaluated. [Available event values](/tutorials/parkour-courses?id=parkour-internal-events).

![Event Command Example](https://i.imgur.com/patqUxL.png "Event Command Example")

_Command: `/pa setcourse (course) command (event) (command)`_

## Parkour Events

Parkour allows you to customise behaviours of each Parkour Event to display a message and / or execute a command.

### Parkour internal events

* `Join` joining a Course
* `Leave` leaving a Course
* `Prize` prize given to Player for Course completion
* `NoPrize` prize **not** given due to RewardOnce on Course completion
* `Finish` every time a Player finishes a Course
* `Checkpoint` achieving a Checkpoint on a Course
* `CheckpointAll` achieving **all** checkpoints on a Course
* `Death` dying whilst on a Course
* `PlayerCourseRecord` player beats their best time on Course
* `GlobalCourseRecord` player beats the global best time on Course

### Parkour internal placeholders

_They **must** be uppercase._
* `%PLAYER%` the Player's name
* `%PLAYER_DISPLAY%` the Player's display name
* `%COURSE%` the Course's name
* `%DEATHS%` the amount of deaths accumulated
* `%CHECKPOINT%` the current checkpoint number
* `%TIME%` a formatted output of time accumulated 
* `%TIME_MS%` the total amount of milliseconds accumulated
* `%TIME_S%` the total amount of seconds accumulated
* `%TIME_M%` the total amount of minutes accumulated
* `%TIME_H%` the total amount of hours accumulated

## Adding a Join Item

When a Player joins a Course you are able to give them items which they can use throughout the Course. An example could be an Elytra to help navigate the Course, instead of relying on a chest / another plugin.

For the easiest convenience, you are able to hold the ItemStack you would like to set as a Join Item in your main hand, then run the command which will save and load the ItemStack exactly as presented.

Optionally, additional arguments can be provided to specify the Material, amount and an optional label to display in the inventory. The items can be made unbreakable by providing a boolean as the final argument.

![Add JoinItem](https://i.imgur.com/ZQeDY5K.png "Add JoinItem")

![Add JoinItem Inventory](https://i.imgur.com/WoYOdxb.png "Add JoinItem Inventory")

_Command: `/pa addjoinitem (course) [material] [amount] [label] [unbreakable]`_  
_Example 1: `/pa addjoinitem tutorial`_  
_Example 2: `/pa addjoinitem tutorial ELYTRA 64`_

## Deleting / Resetting Course Data

[Administration - Reset a Prize](/tutorials/administration?id=reset-a-prize)

[Administration - Reset a Course](/tutorials/administration?id=reset-a-course)

[Administration - Delete a Course](/tutorials/administration?id=delete-a-course)

[Administration - Delete a Checkpoint](/tutorials/administration?id=delete-a-checkpoint)

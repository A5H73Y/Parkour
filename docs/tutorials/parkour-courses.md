Courses
======

A Parkour Course is a physical path that you've created for the Player to use while using the plugin. A Player can join a Course (a.k.a. level, arena, track), and the plugin will track the Player's progress and apply any effects they interact with.

## Course Settings GUI

You are able to quickly access the Course settings using a GUI, this will act as a shortcut of using traditional commands. Each Item in the GUI will act as either a setting toggle, or start a new conversation to take user input for a new value.

![Course Settings Toggle](https://i.imgur.com/s0CANvK.png "Course Settings Toggle")
![Course Settings Conversation](https://i.imgur.com/5YahlJI.png "Course Settings Conversation") 

_Command: `/pa settings (course)`_

## Ready Status

By default, Players can not join a Course that has not been set to ready (so they don't join a half-finished Course), this can be disabled by changing `OnJoin.EnforceReady` to `false` in the `config.yml`.

Only Courses marked as Ready will be added to Cache to allow for better performance when Players join it.

_Command: `/pa ready [course]`_

## Reward Once Status

A Course can be marked to only give the Prize reward for the first time they complete a Course. This will include any Parkour Level rewards given, which is especially important if you've chosen to use "rewardleveladd" functionality.

_Command: `/pa rewardonce [course]`_

## Challenge Only Status

A Course can be marked to only be joinable if the Player is part of a Challenge. The Player will be notified upon trying to join the Course that they must be in a Challenge to join.

_Command: `/pa challengeonly [course]`_

## Resumable Course progress

By default, a Player's progress will be deleted when they leave a Course. To retain the Player's progress set `OnLeave.DestroyCourseProgress` to `false` in the `config.yml`; each Course will then be "resumable" meaning they can leave at any point and rejoin back to their last achieved checkpoint (with their accumulated time and amount of deaths restored).
However, you can toggle the resumable status of the Course to set a Course to be non-resumable to prevent their progress from being saved.

_Command: `/pa resumable [course]`_

## Course Creator

Each Course must have a Creator, by default this will be set to the Player who used the create Course command. 
If you want to overwrite the creator of a Course to a different Player, this will now allow the chosen Player to have elevated permissions for that Course.

_Command: `/pa setcreator (course) (player)`_

## Set Maximum Deaths

You are able to limit the amount of deaths a Player can accumilate before they are kicked off the Course for extra challenge. An example could be a quick challenging Course that allows the Player just 1 life to complete.

_Command: `/pa setmaxdeath (course) (amount)`_

## Set Maximum Time

Set a limit the amount of time a Player can reach before they are kicked off the Course for extra challenge. An example could be a challenging Course that must be completed in a certain time.

_Command: `/pa setmaxtime (course) (seconds)`_

## Set Player Limit

Set a limit on the number of Players that can play the Course concurrently.

_Command: `/pa setplayerlimit (course) (amount)`_

## Adding a Join Item

When a Player joins a Course you are able to give them items which they can use throughout the Course. An example could be an Elytra to help navigate the Course, instead of relying on a chest / another plugin.

The items can be given a label to display in the inventory. The items can be made unbreakable by providing a boolean as the final argument.

![Add JoinItem](https://i.imgur.com/ZQeDY5K.png "Add JoinItem")

![Add JoinItem Inventory](https://i.imgur.com/WoYOdxb.png "Add JoinItem Inventory")

_Command: `/pa addjoinitem (course) (material) (amount) [label] [unbreakable]`_  
_Example: `/pa addjoinitem tutorial ELYTRA 64`_

## Linking a Course after completion

We can change what happens to a Player after they complete a Course; They can either be teleported to the default Lobby, a custom Lobby, or join straight into a new Course. Firstly, we must select a Course to decide the outcome, this is achieved by entering `/pa select (course)`.

After we have created a custom Lobby, we can link this Course to teleport us to this Lobby once we complete the Course, this is achieved with the following command `/pa link lobby (lobby name)`.

![Course Linked to Lobby](https://i.imgur.com/gc7UVkX.jpg "Course Linked to Lobby")

If we want to link a Course to another Course, the command becomes `/pa link course (course name)`.

![Course Linked to Course](https://i.imgur.com/1YvM8zV.jpg "Course Linked to Course")

If you want to remove the link, simply enter `/pa link reset` which will result in the Player being teleported to the default Lobby.

## Changing the Start of a Course

If you want to change the starting position of a Course, you can enter `/pa setstart`.

_Remember that you must have the Course selected (editing), which can be achieved by entering `/pa select (course)`._

## Parkour Events

Parkour allows you to customise behaviours of each Parkour Event to display a message and / or execute a command.

The Parkour events include:
* Join
* Leave
* Prize _(prize given to Player for completion)_
* NoPrize _(prize not given due to RewardOnce)_
* Finish _(every time the player finished a Course)_
* Checkpoint
* CheckpointAll _(all the checkpoints have been achieved)_
* Death
* PlayerCourseRecord _(player beats their best time)_
* GlobalCourseRecord _(player beats the global best time)_

You are able to use the following internal placeholders:
* %PLAYER% _(player name)_
* %PLAYER_DISPLAY% _(player display name)_
* %COURSE% _(course name)_
* %DEATHS% _(amount of deaths)_
* %TIME% _(current total time)_
* %CHECKPOINT% _(current checkpoint number)_

### Running Custom Event Commands

This will allow you to link Parkour to trigger other plugins using a command. 

![Event Command Example](https://i.imgur.com/patqUxL.png "Event Command Example")

You are able to specify the command should be executed by the Player, by prefixing the command with `player:`.

_Command: `/pa setcourse (course) command (event) (command)`_

### Custom Event Messages

You can override the default Parkour messages to a custom per-course message for each event.

![Event Message Example](https://i.imgur.com/ZyOeOom.png "Event Message Example")

![Event Message Set](https://i.imgur.com/5pacqjk.png "Event Message Set")

_Command: `/pa setcourse (course) message (event) (event)`_

## Resetting Course Data

### Reset a Course

This will delete all the statistics stored, which includes leaderboards and various Parkour attributes. This will NOT affect the spawn / checkpoints.  
_Command: `/pa reset course (course)`_

### Reset a Prize

This will reset all the prizes for a Course, causing it to use the default prize specified in the `config.yml`.  
_Command: `/pa reset prize (course)`_

## Deleting Course Data

### Delete a Course

If you delete a Course it will remove all information stored on the server about it, including all references from the database so only use as a last resort. If preferred, you can reset a Course which will keep its structure (start location & checkpoints), but reset all of its stats and leaderboards.

![Deleting Course](https://i.imgur.com/apa5azA.png "Deleting Course")

![Delete Confirm](https://i.imgur.com/8ucihM7.png "Delete Confirm")  
_Command: `/pa delete course (course)`_

### Delete a Checkpoint

If you want to delete a checkpoint, it will start with the highest number and decrease to the lowest, for safety reasons. For example if your Course has 5 checkpoints, and you enter `/pa delete checkpoint (course)` it will ask if you want to delete checkpoint 5, if you execute the command again it will ask if you want to delete checkpoint 4, etc.

Note that you may want to overwrite a checkpoint if it needs moving, instead of deleting many. For example if you had 5 checkpoints and wanted to move the location of checkpoint 2, you can enter `/pa checkpoint 2` in the desired location.  
_Command: `/pa delete checkpoint (course)`_

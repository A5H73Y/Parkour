Configuring a Course
======

## Course Settings GUI

You are able to quickly access the Course settings using a GUI, this will act as a shortcut of using traditional commands. Each Item in the GUI will act as either a setting toggle, or start a new conversation to take user input for a new value.

![Course Settings Conversation](https://i.imgur.com/y03TTJd.png "Course Settings Conversation") 
![Course Settings Toggle](https://i.imgur.com/3rg09P6.png "Course Settings Toggle")

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

## Course Creator

Each Course must have a Creator, by default this will be set to the Player who used the create Course command. 
If you want to overwrite the creator of a Course to a different Player, this will now allow the chosen Player to have elevated permissions for that Course.

_Command: `/pa setcreator (course) (player)`_

## ParkourLevel Restrictions

ParkourLevels can be used to apply restrictions to Courses and lobbies. The validation will be based on the minimum level requirement. For example, if a Course has a minimum level requirement of 10, and the Player has a ParkourLevel of 12, then the teleportation will be allowed otherwise the Player will be notified.

_Command: `/pa setminlevel (course) (level)`_  
Set the minimum level of the specified Course to the specified ParkourLevel. Any Player that now attempts to join this Course will be prevented from doing so unless they have a high enough ParkourLevel.

_Command: `/pa setlobby (name) (level)`_  
Create a Lobby and set the minimum level requirement for teleportation to the ParkourLevel specified. This is used if you organize your Courses into separate lobbies and wish to prevent the Player to teleport unless they have a high enough ParkourLevel.

## Set Maximum Deaths

You are able to limit the amount of deaths a Player can accumilate before they are kicked off the Course for extra challenge. An example could be a quick challenging Course that allows the Player just 1 life to complete.

_Command: `/pa setmaxdeath (course) (amount)`_

## Set Maximum Time

Set a limit the amount of time a Player can reach before they are kicked off the Course for extra challenge. An example could be a challenging Course that must be completed in a certain time.

_Command: `/pa setmaxtime (course) (seconds)`_

## Set Player Limit

Set a limit on the number of Players that can play the Course concurrently.

_Command: `/pa setplayerlimit (course) (amount)`_

## Creating an AutoStart

An AutoStart pressure plate will trigger an automatic joining of a Course, instead of using the traditional Join signs or command.

![Create AutoStart](https://i.imgur.com/jIEpcFy.png "Create AutoStart")

_Command: `/pa setautostart (course)`_  
Stand where you would like the AutoStart location to be, and a configurable block will be placed below you with a pressure plate on top.

## Adding a Join Item

When a Player joins a Course you are able to give them items which they can use throughout the Course. An example could be an Elytra to help navigate the Course, instead of relying on a chest / another plugin.

The items can be given a label to display in the inventory. The items can be made unbreakable by providing a boolean as the final argument.

![Add JoinItem](https://i.imgur.com/ZQeDY5K.png "Add JoinItem")

![Add JoinItem Inventory](https://i.imgur.com/WoYOdxb.png "Add JoinItem Inventory")

_Command: `/pa addjoinitem (course) (material) (amount) [label] [unbreakable]` Example: `/pa addjoinitem tutorial ELYTRA 64`_

## Creating a Parkour Lobby

Parkour can allow Courses to be split into different Lobbys, which could be used to add stages to the server, for example easy, medium and hard Courses. This is made easy by entering `/pa setlobby (name)`. Once submitted, the Lobby will be joinable by all Players.

![Parkour Lobby Created](https://i.imgur.com/AGl0p1A.jpg "Parkour Lobby Created")

If you wish to create a Lobby that is restricted to only certain Players, this can be achieved using Parkour Levels, which we will cover in the Administration tutorials. This will enforce the Player to reach a certain level in the Courses to be able to join the Lobby, for example completing all the Courses in the easy Lobby to be able to join the hard Lobby. This is achieved by providing a minimum Parkour level required to join.

_Command: `/pa setlobby (name) [level]`_  
The Lobby will only be joinable by Players with the required Parkour level (when provided).

![Parkour Level Lobby Created](https://i.imgur.com/py34xti.jpg "Parkour Level Lobby Created")

To teleport to the Lobby, simply enter `/pa lobby (name)`.

## Linking a Course after completion

We can change what happens to a Player after they complete a Course; They can either be teleported to the default Lobby, a custom Lobby, or join straight into a new Course. Firstly, we must select a Course to decide the outcome, this is achieved by entering `/pa select (course)`.

After we have created a custom Lobby, we can link this Course to teleport us to this Lobby once we complete the Course, this is achieved with the following command `/pa link lobby (lobby name)`.

![Course Linked to Lobby](https://i.imgur.com/gc7UVkX.jpg "Course Linked to Lobby")

If we want to link a Course to another Course, the command becomes `/pa link course (course name)`.

![Course Linked to Course](https://i.imgur.com/1YvM8zV.jpg "Course Linked to Course")

If you want to remove the link, simply enter `/pa link reset` which will result in the Player being teleported to the default Lobby.

## Overwriting the Start of a Course

If you want to change the starting position of a Course, you can enter `/pa setstart`.

_Remember that you must have the Course selected (editing), which can be achieved by entering `/pa select (course)`._

## Parkour Events

Parkour allows you to customise behaviours of each Parkour Event to display a message or execute a command.

These events include:
* Join
* Leave
* Prize
* Finish (Only when RewardOnce is set and achieved)
* Checkpoint
* CheckpointAll
* Death

### Running Event Commands

This will allow you to link Parkour to trigger other plugins using a command. You are able to use a `%PLAYER%` placeholder to insert the Player name.

TODO

### Custom Event Messages

TODO

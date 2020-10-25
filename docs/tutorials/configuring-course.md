Configuring a Course
======

## ParkourLevel Restrictions

ParkourLevels can be used to apply restrictions to Courses and lobbies. The validation will be based on the minimum level requirement. So if a course has a minimum level requirement of 10, and the player has a ParkourLevel of 12, the player will be allowed.

`/pa setminlevel (course) (level)` set the minimum level of the specified course to the specified ParkourLevel. Any player that now attempts to join this course will be prevented from doing so unless they have a high enough ParkourLevel.

`/pa setlobby (name) (level)` create a lobby and set the minimum level requirement for teleportation to the ParkourLevel specified. This is used if you organize your Courses into separate lobbies and wish to prevent the player to teleport unless they have a high enough ParkourLevel.

## Creating an AutoStart

It is super easy to create an AutoStart pressure plate that will trigger the joining of a Course, instead of using the traditional Join signs.

Stand where you would like the AutoStart location to be, and enter `/pa setautostart (course)` which will create a configurable block below you, and a pressure plate on top.

![Create AutoStart](https://i.imgur.com/jIEpcFy.png "Create AutoStart")

## Adding a Join Item

When a player joins a Course you are able to give them items which they can use throughout the course. An example could be an Elytra to help navigate the course, instead of relying on a chest / another plugin.

The items can be given a label to display in the inventory. The items can be made unbreakable by providing a boolean as the final argument.

This is achieved by entering `/pa addjoinitem (course) (material) (amount) [label] [unbreakable]` an example could be `/pa addjoinitem tutorial ELYTRA 64`.

![Add JoinItem](https://i.imgur.com/ZQeDY5K.png "Add JoinItem")

![Add JoinItem Inventory](https://i.imgur.com/WoYOdxb.png "Add JoinItem Inventory")

## Set Maximum Deaths

You are able to limit the amount of deaths a player can accumilate before they are kicked off the Course for extra challenge. An example could be a quick challenging course that allows the player just 1 life to complete.

This is achieved by entering `/pa setmaxdeath (course) (amount)`.

## Set Maximum Time

You are able to limit the amount of time a player can reach before they are kicked off the course for extra challenge. An example could be a challenging course that must be completed in a certain time.

This is achieved by entering `/pa setmaxtime (course) (seconds)`.

## Creating a Parkour lobby

Parkour can allow courses to be split into different lobbys, which could be used to add stages to the server, for example easy, medium and hard courses. This is made easy by entering `/pa setlobby (name)`. Once submitted, the lobby will be joinable by all players.

![Parkour Lobby Created](https://i.imgur.com/AGl0p1A.jpg "Parkour Lobby Created")

If you wish to create a lobby that is restricted to only certain players, this can be achieved using Parkour Levels, which we will cover in the Administration tutorials. This will enforce the player to reach a certain level in the courses to be able to join the lobby, for example completing all the courses in the easy lobby to be able to join the hard lobby. This is achieved by providing a minimum Parkour level required to join.

This is done by providing a level parameter to the command `/pa setlobby (name) (level)`. Once submitted, the lobby will be joinable by players with the required Parkour level.

![Parkour Level Lobby Created](https://i.imgur.com/py34xti.jpg "Parkour Level Lobby Created")

To teleport to the lobby, simply enter `/pa lobby (name)`.

## Linking a Course after completion

We can change what happens to a player after they complete a course; They can either be teleported to the default lobby, a custom lobby, or join straight into a new course. Firstly, we must select a course to decide the outcome, this is achieved by entering `/pa select (course)`.

After we have created a custom lobby, we can link this course to teleport us to this lobby once we complete the course, this is achieved with the following command `/pa link lobby (lobby name)`.

https://i.imgur.com/gc7UVkX.jpg

If we want to link a course to another course, the command becomes `/pa link course (course name)`.

https://i.imgur.com/1YvM8zV.jpg

If you want to remove the link, simply enter `/pa link reset` which will result in the player being teleported to the default lobby.

## Overwriting the Creator of a Course

If you want to overwrite the creator of a course to a different player, you can enter `/pa setcreator (course) (player)` this will now allow the chosen player to have administration permissions for that course.

_The creator of a course has exclusive permissions to be able to edit that course._

## Overwriting the Start of a Course

If you want to change the starting position of a course, you can enter `/pa setstart`.

_Remember that you must have the course selected (editing), which can be achieved by entering `/pa select (course)`._
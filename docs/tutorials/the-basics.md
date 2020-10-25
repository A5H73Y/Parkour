The Basics
======

## Beginning Steps

You are recommended to create a world dedicated for Parkour Courses, although Parkour does have multi-world support. Once you have your world chosen, you should create an area where the players can choose which Courses to join; This area is called a 'lobby'. Stand where you want the lobby to be saved and enter the command `/pa setlobby`. This only needs to be done once!

When you want to teleport to the Parkour lobby, enter `/pa lobby`. By default, this is where players are teleported to when they complete or leave a Course. We can create many lobbies which have different locations and requirements, which will be covered later in the tutorials.

You should now have the latest version of Parkour installed, and have a Parkour lobby created.

## Frequently Asked Questions

##### Parkour Welcome Message

To disable the Parkour Join message, in the `config.yml` find `Other.Display.JoinWelcomeMessage` and set it to `false`.

##### Update Player Database Time

To keep a single database row per player per Course, in the `config.yml` find `OnFinish.UpdatePlayerDatabaseTime` and set it to `true`.

##### Stuck Pressure Plates

To allow for multiple players to stand on a pressure plate at once, in the `config.yml` find `OnCourse.PreventPlateStick` and set it to `true`.

## Permissions

| Permission Node | Description |
|-|-|
| **Parkour.\*** | **All Parkour permissions.** |
|  |  |
| **Parkour.Basic.\*** | **All Basic Parkour permissions.** |
| Parkour.Basic.Create | Create a Parkour Course. |
| Parkour.Basic.Kit | Receive a ParkourKit. |
| Parkour.Basic.TP | Teleport to a Course. |
| Parkour.Basic.TPC | Teleport to a Course Checkpoint. |
| Parkour.Basic.Leaderboard | View Leaderboard results via command. |
| Parkour.Basic.Challenge | Send Parkour Challenges to other players. |
| Parkour.Basic.JoinAll | Use the Join All Courses GUI. |
| Parkour.Basic.Signs | Ability to use Parkour Signs only if Other.Parkour.SignUsePermissions is set to true. |
| Parkour.Basic.Commands | Ability to use Parkour Commands only if Other.Parkour.CommandUsePermissions is set to true. |
|  |
| **Parkour.Admin.\*** | **All Admin Parkour permissions.** |
| Parkour.Admin.Course | Perform administration tasks to a Course. |
| Parkour.Admin.Prize | Configure a Prize for a Course. |
| Parkour.Admin.Delete | Ability to delete Courses / checkpoints / lobbies / parkourkits. |
| Parkour.Admin.Reset | Ability to reset Courses, players / leaderboards / prizes. |
| Parkour.Admin.TestMode | Ability to activate TestMode. |
| Parkour.Admin.ReadyBypass | Bypass the ready requirement of a Course. |
| Parkour.Admin.LevelBypass | Bypass the Parkour Level requirement of a Course. |
|  |
| **Parkour.CreateSign.\*** | **All Create Parkour Sign permissions.** |
| Parkour.CreateSign.Join | Create a Join Course sign. |
| Parkour.CreateSign.Finish | Create a Finish Course sign. |
| Parkour.CreateSign.Leave | Create a Leave Course sign. |
| Parkour.CreateSign.Lobby | Create a Parkour Lobby sign. |
| Parkour.CreateSign.Effect | Create an Effect sign. |
| Parkour.CreateSign.Stats | Create a Course Stats sign. |
| Parkour.CreateSign.Leaderboards | Create a Leaderboards sign. |
|  |
| **Parkour.Level.(LEVEL)** | **Give the player a specific Parkour Level.** |
|  |
| **Parkour.Course.(COURSE)** | **Give the player permission to join a specific Parkour Course.** |

## Command Help

To display the Parkour commands menu, enter `/pa cmds` which will bring up the available command menus.

![Parkour Commands Menu](https://i.imgur.com/csrgDFJ.png "Parkour Commands Menu")

To display a specific menu, enter the corresponding value you want, i.e. `/pa cmds 1`

![Parkour Commands Menu 1](https://i.imgur.com/i4FV4Rd.png "Parkour Commands Menu 1")

If you want to understand more information about a command, you can enter `/pa help (command)` which will display everything you need to know, i.e. `/pa help join`

![Parkour Join Command Help](https://i.imgur.com/f9Qs12M.png "Parkour Join Command Help")


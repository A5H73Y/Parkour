Essential information
======

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
| Parkour.Basic.Challenge | Send Parkour Challenges to other Players. |
| Parkour.Basic.JoinAll | Use the Join All Courses GUI. |
| Parkour.Basic.Signs | Ability to use Parkour Signs only if Other.Parkour.SignUsePermissions is set to true. |
| Parkour.Basic.Commands | Ability to use Parkour Commands only if Other.Parkour.CommandUsePermissions is set to true. |
|  |  |
| **Parkour.Admin.\*** | **All Admin Parkour permissions.** |
| Parkour.Admin.Course | Perform administration tasks to a Course. |
| Parkour.Admin.Prize | Configure a Prize for a Course. |
| Parkour.Admin.Delete | Ability to delete Courses / checkpoints / lobbies / parkourkits. |
| Parkour.Admin.Reset | Ability to reset Courses, Players / leaderboards / prizes. |
| Parkour.Admin.TestMode | Ability to activate TestMode. |
| Parkour.Admin.ReadyBypass | Bypass the ready requirement of a Course. |
| Parkour.Admin.LevelBypass | Bypass the Parkour Level requirement of a Course. |
|  |  |
| **Parkour.CreateSign.\*** | **All Create Parkour Sign permissions.** |
| Parkour.CreateSign.Join | Create a Join Course sign. |
| Parkour.CreateSign.Finish | Create a Finish Course sign. |
| Parkour.CreateSign.Leave | Create a Leave Course sign. |
| Parkour.CreateSign.Lobby | Create a Parkour Lobby sign. |
| Parkour.CreateSign.Effect | Create an Effect sign. |
| Parkour.CreateSign.Stats | Create a Course Stats sign. |
| Parkour.CreateSign.Leaderboards | Create a Leaderboards sign. |
|  |  |
| **Parkour.Level.(LEVEL)** | **Give the Player a specific Parkour Level.** |
|  |  |
| **Parkour.Course.(COURSE)** | **Give the Player permission to join a specific Parkour Course.** |

## Parkour Commands

## Command Help

To display the Parkour commands menu, enter `/pa cmds` which will bring up the available command menus.

![Parkour Commands Menu](https://i.imgur.com/csrgDFJ.png "Parkour Commands Menu")

To display a specific menu, enter the corresponding value you want, i.e. `/pa cmds 1`

![Parkour Commands Menu 1](https://i.imgur.com/i4FV4Rd.png "Parkour Commands Menu 1")

If you want to understand more information about a command, you can enter `/pa help (command)` which will display everything you need to know, i.e. `/pa help join`

![Parkour Join Command Help](https://i.imgur.com/f9Qs12M.png "Parkour Join Command Help")

## Parkour Placeholders

## Frequently Asked Questions

##### Parkour Welcome Message

To disable the Parkour Join message, in the `config.yml` find `Other.Display.JoinWelcomeMessage` and set it to `false`.

##### Update Player Database Time

To keep a single database row per Player per Course, in the `config.yml` find `OnFinish.UpdatePlayerDatabaseTime` and set it to `true`.

##### Stuck Pressure Plates

To allow for multiple Players to stand on a pressure plate at once, in the `config.yml` find `OnCourse.PreventPlateStick` and set it to `true`.

##### Available Placeholders

Each of the Parkour PlaceholderAPI placeholders are available here: [Parkour Placeholders](/tutorials/compatible-plugins?id=parkour-placeholders)

## Terms / Meanings

#### Course

A Parkour Course is a physical path that you've created for the Player to use while using the plugin. You can join a Course (a.k.a level, arena, track), and the plugin will track the Player's progress and apply any effects you interact with.

#### Lobby

A Lobby is simply a location that allows you to join Parkour Courses. It also acts as a place for the Player to teleport to when they complete or leave a Course.

#### ParkourKit

A ParkourKit is a set of Materials which act as a set of toolbox for building a Course. Each Material in a ParkourKit must have an action, for example "death", "speed", etc. A Course must have a ParkourKit, even if it's empty.

#### Config

Parkour is incredibly customisable, allowing you to modify the plugin exactly to what your server requires. In your server, Parkour will have a folder of many configuration files, shortened to config. `config.yml` and `strings.yml` are the only files we suggest you edit, unless you know what you're doing. Some server implementions don't save the changes upon restarting the server, so we highly suggest you use the **/pa reload** when you've made any config changes, then you'll be safe to restart your server without losing any changes.

#### ParkourLevel

A Player can earn levels will unlock new Courses for them to join. An example is 'course2' will be locked until they complete 'course1'. The reward can either be an exact level, or an addition to their current level. For example you can finish all the 'easy' Courses in any order before having enough ParkourLevel to join the 'medium' Courses. The Player's ParkourLevel can never decrease, only increase or remain the same.

#### ParkourRank

When the Parkour chat is enabled, the Player's ParkourRank will be included in their message prefix. The Player can achieve a new ParkourRank when passing the required ParkourLevel to unlock it. This can be a status symbol for the Player's that have completed many of the harder Courses for example.

<script>
    insertCommandsMarkup();
    insertPlaceholdersMarkup();
</script>

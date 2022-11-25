Plugin Configuration
======

Parkour is incredibly customisable, allowing you to modify the plugin exactly to what your server requires.

To make changes, simply change the values you want, and save the file. _It is no longer required to use /pa reload_.

It's important to know that YAML has very strict formatting rules and will fail if any of those are broken. If you are having any issues please use a [YAML Validator](https://codebeautify.org/yaml-validator).

## config.yml

This configuration file is very large and can look daunting at first, but each section is broken up into logical groups with clear names to hopefully make it easier.

<details><summary>config.yml (Click to expand)</summary>

!>  Some properties require the server to restart to apply the changes, these include changes to scoreboard, adding 3rd party plugin support, etc.

```yaml
# All the options for when a Player joins a Course
OnJoin:
  # Should the Player be in the same World as the Course before being allowed to Join
  EnforceWorld: false
  # Should the Course be marked as Ready before it can be joined
  EnforceReady: true
  # Should the Player's health by filled upon Joining a Course, and to what amount
  FillHealth:
    Enabled: true
    Amount: 20
  # What GameMode should the Player be while on the Course. Options include: CREATIVE, SURVIVAL, ADVENTURE, SPECTATOR, KEEP
  SetGameMode: SURVIVAL
  # Treat the first Checkpoint as the start of the Course. The timer will be started upon achieving the first checkpoint.
  TreatFirstCheckpointAsStart: false
  # Require a permission for every single course (Parkour.Course.(CourseName))
  PerCoursePermission: false
  # Should the Player be teleported to the starting point. Can be disabled for AutoStarts for a seamless start of a Course.
  TeleportPlayer: true
  # What is the join broadcast message level, options include:
  # "GLOBAL" = Every Player on the server, "WORLD" = Every Player on the World, "PARKOUR" = Every Parkour Player, "PLAYER" = Just the Player, nobody else. 
  BroadcastLevel: NONE
 
# All the options for when the Player is on a Course
OnCourse:
  # Should all Players be able to break and place blocks as usual
  AnybodyPlaceBreakBlocks: false
  # Should Admins be able to break and place blocks are usual
  AdminPlaceBreakBlocks: true
  # Should Parkour attempt to perform fewer checks for the ParkourKits. (This may break some behaviour)
  AttemptLessChecks: false
  # Which Material should the Checkpoint pressure plates be made out of
  CheckpointMaterial: STONE_PLATE
  # Should the Player be prevented from dropping items
  DisableItemDrop: false
  # Should the Player be prevented from picking up items
  DisableItemPickup: false
  # Should ALL Player damage be prevented
  DisablePlayerDamage: false
  # Should Fall Damage be prevented
  DisableFallDamage: false
  # Should the Player be prevented from trying to Fly
  DisableFly: true
  # Should the plugin have the ability to display a Live Timer (either action bar or Scoreboard)
  DisplayLiveTime: false
  # Should the player be prevented from interacting with non-Parkour signs
  EnforceParkourSigns: true
  # Should the player be prevented from teleporting to another World while on a Course
  EnforceWorld:
    Enabled: true
    # If they are allowed to be teleported away, should they leave the Course as a result
    LeaveCourse: false
  # Prevent the Pressure Plate from being 'stuck' in a pressed position when a Player is stood on it
  # This will allow people to still achieve the Checkpoint while others are on a Plate. This will mean that Redstone no longer being fired from it
  PreventPlateStick: false
  # Should the Player be prevented from opening any non-player inventories
  PreventOpeningOtherInventories: false
  # Should the Player be prevented from attacking other entities
  PreventAttackingEntities: false
  # Should the Player be prevented from taking damage from other entities attacking them
  PreventEntitiesAttacking: true
  # Should the Player be prevented from joining another Course whilst on one
  PreventJoiningDifferentCourse: false
  # Should players have their collisions removed. You need to have the Scoreboard enabled for this to work.
  PreventPlayerCollisions: false
  # Should players be prevented from taking fire damage
  PreventFireDamage: true
  # Should the Player only be allowed to achieve checkpoints sequentially (1 - 2 - 3...)
  # Or can they be allowed to skip checkpoints (1 - 3 - 4...)
  SequentialCheckpoints:
    Enabled: true
    # Notify the Player when they've achieved a checkpoint which was non-sequential (i.e. they've missed a checkpoint)
    AlertPlayer: true
  # Should the Players have to be sneaking to activate the Parkour Tools
  SneakToInteractItems: true
  # Should achieving the final Checkpoint trigger the Course finish for the Player
  TreatLastCheckpointAsFinish: false
  # Should ParkourKits be enabled. If this is set to false, finish blocks will no longer work and Courses must be finished using a Finish Sign or other means.
  UseParkourKit: true
  # Should the Player be prevented from using non-Parkour commands
  EnforceParkourCommands:
    Enabled: true
    # These commands are the exception and are still allowed
    Whitelist:
    - login
 
# All the options for when a Player finishes a Course
OnFinish:
  # What is the finish broadcast message level, options include:
  # "GLOBAL" = Every Player on the server, "WORLD" = Every Player on the World, "PARKOUR" = Every Parkour Player, "PLAYER" = Just the Player, nobody else. 
  BroadcastLevel: GLOBAL
  # Should a message be displayed when a new record has been beaten
  DisplayNewRecords: false
  # Should the player be sent a summary of their stats after finishing
  DisplayStats: true
  # Should Course Prizes be enabled, this includes every kind of prize such as ParkourLevels & ParkourRanks etc.
  EnablePrizes: true
  # Should the Player have to achieve all the Checkpoints before being able to finish
  # Prevents cheaters from skipping checkpoints
  EnforceCompletion: true
  # What GameMode should the Player be when finishing / leaving the Course. Options include: CREATIVE, SURVIVAL, ADVENTURE, SPECTATOR, KEEP, RESTORE
  SetGameMode: SURVIVAL
  # Should the Player be teleported away after finishing a Course
  TeleportAway: true
  # Should the Player be Teleported BEFORE the Prize is given
  TeleportBeforePrize: false
  # Should there be a delay (in ticks) before being teleported away
  TeleportDelay: 0
  # Should the Player be teleported back to the Location they were in before joining the Course
  TeleportToJoinLocation: false
  # Should the Player's database time be updated every time the beat it, instead of inserting a new time with every completion
  UpdatePlayerDatabaseTime: true
 
# All the options for when the Player leaves a Course
OnLeave:
  # Should the Player be teleported to the Linked Lobby, instead of the default Lobby
  TeleportToLinkedLobby: false
  # Should the Player's progress be destroyed when they leave a Course
  # If this is false, the Player will be able to re-join the same Course at the checkpoint and time accumulated as before
  DestroyCourseProgress: true
  # Should the Player be teleported away to their destination
  TeleportAway: true

# All the options for when the Player restarts the Course
OnRestart:
  # When the Player restarts the Course should it do the full Leave and Join cycle, or should it just reset their progress
  FullPlayerRestart: false
  # Should be Player be asked to confirm if they want to restart their progress in case they accidentally use the Restart Tool
  RequireConfirmation: false
 
# All the options for when the Player dies on a Course
OnDie:
  # Should the Player's time be reset if they have yet to achieve a Checkpoint
  ResetProgressWithNoCheckpoint: false
  # Should the Player's XP Bar be set to the number of deaths accumulated. Their original XP Level will be restored upon finishing / leaving.
  SetXPBarToDeathCount: false
 
# All the options for when the Player leaves the server while on a Course
OnLeaveServer:
  # Should the Player be kicked from the Course
  LeaveCourse: false
  # Should the Player be teleported back to the last Checkpoint
  TeleportToLastCheckpoint: false

# All the options for when the Server restarts
OnServerRestart:
  # Should all Players be kicked from a Course when the server starts up
  KickPlayerFromCourse: false

# All the Default Course settings
CourseDefault:
  # Settings which will be defaulted to on ALL Courses
  # These can be overridden on a per-Course basis
  Settings:
    HasFallDamage: true
    MaxFallTicks: 80
    DieInLiquid: false
    DieInVoid: false
    RewardOnce: false
    RewardDelay: 0
    RewardLevelAdd: 0
    JoinItems: []
  # Default Course Prize
  Prize:
    Material: DIAMOND
    Amount: 1
    Label: ''
    XP: 0
  # Should the per-course commands be combined with the default commands below
  Commands:
    CombinePerCourseCommands: true
  # Default command to be run for each Parkour event while on a Course
  # See /tutorials/parkour-courses?id=parkour-events for more information on each event
  Command:
    ...
 
# Configuration for the Items the Player receives when Joining a Course, also known as Parkour Tools
# The Material can be set to AIR if not wanted
ParkourTool:
  LastCheckpoint:
    Material: ARROW
    Slot: 0
  HideAll:
    Material: BONE
    Slot: 1
    # Should it hide all players (global), or just Parkour players
    Global: true
    # Should all Players be hidden by default when joining a Course
    ActivateOnJoin: false
  HideAllEnabled:
    Material: BONE
    Slot: 1
  Leave:
    Material: OAK_SAPLING
    Slot: 2
  Restart:
    Material: STICK
    Slot: 3
    # How many seconds must pass between each Restart usage
    SecondCooldown: 1
  Freedom:
    Material: REDSTONE_TORCH
    Slot: 4
    # How many seconds must pass between each Save Checkpoint usage
    SecondCooldown: 1
  Rockets:
    Material: FIREWORK_ROCKET
    Slot: 4
 
# All the options for Parkour Challenges
ParkourChallenge:
  # Should the Challenge participants be hidden from each other
  HidePlayers: true
  # What should the countdown start from
  CountdownFrom: 5
    # Should the Player be prepared for the Challenge (teleported to the Course unable to move) when they accept the challenge
  PrepareOnAccept: false
 
# All the options for ParkourModes
ParkourModes:
  # Increase the Players walk speed
  Speedy:
    SetSpeed: 0.7
    ResetSpeed: 0.2
  # Allows the Player to have a Rocket which launches the Player
  Rockets:
    # Should the velocity be inverted (teleported forwards instead of backwards)
    Invert: false
    # Seconds delay before being able to fire again
    SecondCooldown: 1
    # Amount of force received from the rocket launching
    LaunchForce: 1.5
 
# All the options for displaying titles
# Choose the durations for each stage, and choose which will be presented in a Title 
DisplayTitle:
  FadeIn: 5
  FadeOut: 5
  JoinCourse: 
    Enabled: true
    Stay: 20
  Checkpoint:
    Enabled: true
    Stay: 20
  RewardLevel:
    Enabled: true
    Stay: 20
  Death:
    Enabled: true
    Stay: 20
  Leave:
    Enabled: true
    Stay: 20
  Finish:
    Enabled: true
    Stay: 20
 
# All the options for AutoStarts
AutoStart:
  Enabled: true
  # Material identifying a AutoStart. This is used for performance reasons
  Material: BEDROCK
  # Delay before triggering the Course Join
  TickDelay: 0
  # Include the world name in the AutoStart to allow multi-world support
  IncludeWorldName: true
 
# All the options for displaying a Scoreboard while on a Course
# Each entry can be disabled and the order changed
Scoreboard:
  Enabled: false
  CourseName:
    Enabled: true
    Sequence: 1
  BestTimeEver:
    Enabled: true
    Sequence: 2
  BestTimeEverName:
    Enabled: true
    Sequence: 3
  MyBestTime:
    Enabled: true
    Sequence: 4
  CurrentDeaths:
    Enabled: true
    Sequence: 5
  Checkpoints:
    Enabled: true
    Sequence: 6
  LiveTimer:
    Enabled: true
    Sequence: 7
  RemainingDeaths:
    Enabled: false
    Sequence: 8
 
# All the options for the various event Sounds
# Each entry allows you to enable / disable the sound, also choose the Sound and the volume and pitch
Sounds:
  Enabled: false
  JoinCourse:
    Enabled: true
    Sound: BLOCK_NOTE_BLOCK_PLING
    Volume: 0.05
    Pitch: 1.75
  SecondIncrement:
    Enabled: true
    Sound: BLOCK_NOTE_BLOCK_PLING
    Volume: 0.05
    Pitch: 1.75
  SecondDecrement:
    Enabled: true
    Sound: BLOCK_NOTE_BLOCK_PLING
    Volume: 0.05
    Pitch: 4.0
  PlayerDeath:
    Enabled: true
    Sound: ENTITY_PLAYER_DEATH
    Volume: 0.1
    Pitch: 1.75
  CheckpointAchieved:
    Enabled: true
    Sound: BLOCK_NOTE_BLOCK_CHIME
    Volume: 0.1
    Pitch: 1.75
  CourseFinished:
    Enabled: true
    Sound: BLOCK_CONDUIT_ACTIVATE
    Volume: 0.1
    Pitch: 1.75
  CourseFailed:
    Enabled: true
    Sound: BLOCK_CONDUIT_DEACTIVATE
    Volume: 0.1
    Pitch: 1.75
  ReloadRocket:
    Enabled: true
    Sound: ENTITY_PHANTOM_HURT
    Volume: 0.1
    Pitch: 1.75
 
# ParkourGUI settings
ParkourGUI:
  Material: BOOK
  # What should the empty space be filled with
  FillerMaterial: CYAN_STAINED_GLASS_PANE

# ParkourKit settings
ParkourKit:
  # When a Kit is requested, should it replace the Player's inventory
  ReplaceInventory: true
  # When a Kit is requested, should a Sign be included in the Kit
  GiveSign: true
  # Should the plugin use the legacy ground detection. 
  # This will always check what is below the Player, for example when standing on a LILYPAD on WATER will consider the Material WATER, similar for CARPET.
  LegacyGroundDetection: false

# ParkourRank Chat settings
ParkourRankChat:
  # Should the plugin insert the Player's ParkourRank into the Chat
  Enabled: false
  # Should the plugin override the Chat with its own format, otherwise it will simply replace the %RANK%, or PlaceholderAPI placeholder
  OverrideChat: true
 
# Everything else
Other:
  # Should the Plugin use AutoTabCompletion - this is highly recommended
  UseAutoTabCompletion: true
  # Should the Plugin check for updates on start up
  CheckForUpdates: true
  # Should certain events (delete / reset) be logged to a file
  LogAdminTasksToFile: true
  # Should the Plugin attempt to check if the Checkpoint is being placed on a valid Material
  EnforceSafeCheckpoints: true
  # Should the Player's config files be named using their UUID, otherwise their name
  PlayerConfigUsePlayerUUID: true
  
  Parkour:
    # Should destroying Parkour Signs be prevented by non-admins
    SignProtection: true
    # Should Parkour control the Player's inventory when joining / leaving a Course
    InventoryManagement: true
    # Should the Player require an additional Permission to interact with Parkour signs
    SignUsePermissions: false
    # Should the Player require an additional Permission to use Parkour commands
    CommandUsePermissions: false
    # What should be the maximum achievable ParkourLevel 
    MaximumParkourLevel: 99999999
    # Should the Player's potion effects be reset during Parkour events such as joining, dying, leaving, finishing etc.
    ResetPotionEffects: true
    
  Display:
    # Should the plugin display a Join message to the Player "This server uses Parkour v6.X"
    JoinWelcomeMessage: true
    # Should the Player be notified when they earn a new ParkourLevel
    LevelReward: true
    # Should the Player be notified of the remaining Course Prize Cooldown
    PrizeCooldown: true
    # Should the Course list exclude courses that aren't marked as ready
    OnlyReadyCourses: false
    # Should the Player be notified if they have already completed the joined Course
    CompletedCourseJoinMessage: false
    # Should deprecated commands be included in auto-tab and help menus
    IncludeDeprecatedCommands: false
    
  # Time output settings
  # Colour codes can be used, however they need to be in a format which is ignored by the format processor. Surround each colour code with ''&b'' for it to be ignored.
  # For Example: DetailedFormat: '''&b''mm''&4'':''&5''ss''&4'':''&6''SSS'
  Time:
    # The Standard Time output format, with no millisecond information. Used with whole seconds 
    StandardFormat: "HH:mm:ss"
    # The Detailed Time output format, with millisecond information. Used in leaderboard times
    DetailedFormat: "HH:mm:ss:SSS"
    # The TimeZone to use. Only change if you're having weird output.
    TimeZone: "GMT"
 
  # Should the Parkour config files be backed up after every server shutdown
  OnServerShutdown:
    BackupFiles: false
 
  # Should the Player's Parkour Information be deleted if Parkour detects they've been banned
  # For example if a Player has been banned for cheating in many Courses, all of their times will be deleted automatically upon banning
  OnPlayerBan:
    ResetParkourInfo: false
 
  # When the Player's ParkourLevel is manually set, should their ParkourRank be re-applied
  OnSetPlayerParkourLevel:
    UpdateParkourRank: true
 
  # When the Player takes Void damage, should the Player be teleported to the closest Lobby when not on a Course
  OnVoid:
    TeleportToLobby: false
 
# Each Parkour compatible plugin config
Plugin:
  # BountifulAPI is required for titles and action bar messages on servers before 1.11
  BountifulAPI:
    Enabled: true
  # Vault is required to use economy functionality
  Vault:
    Enabled: true
  # Allows Parkour to offer external placeholders, also allows using placeholderapi values in multiple places 
  PlaceholderAPI:
    Enabled: true
    # How many seconds should database results be cached for
    CacheTime: 15
 
# How many time results should be cached per Course
Database:
  MaximumCoursesCached: 10
 
# Override the path to the SQLite Database. Only change if you know what you're doing
SQLite:
  PathOverride: ''
 
# MySQL Connection settings
# Values will need to be updated to match your sql server before a connection can be made. Check server start up logs for any connection issues.
# Replace each placeholder provided with your values i.e. (PORT) -> 3306
MySQL:
  Use: false
  URL: jdbc:mysql://HOST:PORT/DATABASE?useSSL=false
  Username: Username
  Password: Password
  LegacyDriver: false
 
# Should the Player be in the same world as the Lobby before being allowed to join
LobbySettings:
  EnforceWorld: false
 
# The installed version of the plugin, used by the updater to automatically update your config
Version: '7.0.0'
```

_This is correct as of Parkour v7.0.0_

</details>

## strings.yml

This file lets you change most Parkour messages that get presented to the Players.

As YAML has very strict formatting, it is recommended to paste your entire `strings.yml` contents through a validator such as: [https://codebeautify.org/yaml-validator](https://codebeautify.org/yaml-validator) which should identify any problems before you save your changes.

<details><summary>Remove messages (Click to expand)</summary>

To prevent a message from being sent to the Player, you can set the value to empty.  
For example `AllCheckpoints: ''`  
_Do not delete the entire translation entry, as it will be regenerated by the plugin._

</details>

<details><summary>Multiple line messages (Click to expand)</summary>

Messages can be sent across multiple messages by inserting a `|` then following with your message:
```
AllCheckpoints: |
  Congratulations you've completed all %TOTAL% Checkpoints.
  Now finish the Course to receive your Prize.
```

_More information here: [https://stackoverflow.com/a/21699210](https://stackoverflow.com/a/21699210)_

</details>

<details><summary>User submitted translations (Click to expand)</summary>

Here are translations submitted by users for a specific language, I take no responsibility for their accuracy.

[Chinese / Mandarin (CH)](https://a5h73y.github.io/Parkour/files/translations/ch/strings.yml)

[Spanish (ES)](https://a5h73y.github.io/Parkour/files/translations/es/strings.yml)

</details>

## courses folder

Each Course created has its own JSON file, containing the various Course properties and any checkpoints.

## players folder

Any Player which uses the plugin will have their own JSON file, containing any Parkour related data.

By default, the name of the JSON will be the Player's UUID; however you can change this to be the Player's name by setting `Other.PlayerConfigUsePlayerUUID` to `false` in the `config.yml`.

## sessions folder

This folder is used to store each Player's sessions across any number of Courses. This is best utilised when Courses are [resumable](/tutorials/parkour-courses?id=resumable).

An example could be, if they get to the 5th checkpoint on Course1, then they can leave the Course, join any other Courses, and later rejoin back to Course1 to continue their progress at the 5th Checkpoint with their time and deaths restored.

By default, the name of the Player's folder will be the Player's UUID; however you can change this to be the Player's name by setting `Other.PlayerConfigUsePlayerUUID` to `false` in the `config.yml`.

## sqlite-db folder

This folder is used to store the plugin's leaderboards in a local SQLite database. The alternative is to configure a MySQL connection in the config.yml.

## other folder

### auto-starts.yml

Stores every [AutoStart](/tutorials/parkour-autostart) and its associated Course to join.

### course-completions.yml

Stores every single Course each Player has completed.

### parkour-kits.yml

Stores every [ParkourKit](/tutorials/parkour-kits) that currently exists.

### parkour-lobbies.yml

Stores every [Lobby](/tutorials/parkour-lobby) that currently exists.

### parkour-ranks.yml

Stores every [ParkourRank](/tutorials/parkour-level-ranks?id=what-is-a-parkourrank), with the required ParkourLevel to achieve each.

### quiet-players.yml

Stores all the Player's who have requested the plugin to be 'quiet', receiving less unimportant messages.

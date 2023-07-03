Changelogs
======

Please note that each version of Parkour is backwards compatible with the previous version and will automatically upgrade your config upon server start up. There will be no manual intervention, unless stated in breaking changes.

## 7.2.0

* Added ability to set a one-time fee for a course
* Added "ParkourTool.RemoveRightClickRestriction" to support geyser controls
* Added "Other.Display.CurrencyName" to show or hide currency name in messages
* Added Throwables to be ParkourTools
* Ability to disable nested parkour commands
* Added ability to affect vehicles with ParkourKit (OnCourse.ParkourKit.IncludeVehicles)
* Added ability to return items achieved while on Course (OnFinish.GiveGainedItemsBack & OnLeave.GiveGainedItemsBack)
* Fixed decimal rewarddelay not working
* Fixed teleporting away from a Course detection
* Fixed Restart cooldown being reset
* Fixed existing session NPE
* Small ParkourKit optimisation
* Update dependencies

## 7.1.0
* Improved ParkourTools to be more structured
* Added ability for each ParkourTool to have individual cooldown and cooldown message
* Added Rockets ParkourTool amount limit
* Added ability for Material custom model data using "MATERIAL:123"
* Added Setting "AutoStart.RestartWhenOnCourse.Enabled" to reset progress on AutoStart action
* Added Setting "AutoStart.RestartWhenOnCourse.Teleport" to teleport whilst on Course
* Added console command "pac setpropertyforeverysinglecourse (property) [value]"
* Added console command "pac setpropertyforeverysingleplayer (property) [value]"
* Added ability for per-checkpoint commands (currently a manual process)
* Added missing "%parkour_current_checkpoint_next%" placeholder
* Added option to set display name on Material prizes
* Fixed "/pa addjoinitem (course)"
* Fixed losing Player's JoinLocation
* Fixed bug when merging default and per-course commands
* Fixed various placeholders
* Fixed full restart resetting player data
* Fixed Restart events firing in more places
* Fixed Too many open files
* Player's cooldowns are reset on Restart
* Changed BestTime events after finish event

### Breaking Changes
* Please reapply your translations to the `ParkourTool` section.

## 7.0.6
* Added "/pa reset commands (course)"
* Updated docs to include joining course detail
* Small bug fix in placeholder logic
* Added "Other.Time.PlaceholderFormat" to change time output from Placeholders
* Removed limitation for linked course & linked lobby

## 7.0.5
* Fixed ParkourModes not being restored after quick restart
* Fixed Player's data being reset prior to linked course
* Parkour can now be registered as an Economy plugin using Vault (Plugin.Vault.RegisterParkoins)
* Added more economy command (add / deduct / amount)
* Fixed Join Locations being null

## 7.0.4

* Added option "Other.Parkour.OpsBypassGameModeSet" to prevent op's GameMode changing
* Decreased default PlaceholderAPI Cache

## 7.0.3

* Automatically apply JoinItems (thanks Steve)
* Added "pac finish (player)" to trigger finish for Player
* Update dependency versions

## 7.0.2

* Fix error if wager amount contains $
* Added Multiverse as a soft-depend to fix server startup world missing errors
* Removed race condition of rewardonce
* Rewardonce check changed to use course-completions file
* Fixed Course defaults
* Fixed not resetting Players completed courses
* Update dependency versions

## 7.0.1

* Fixed JoinItems, and to include default join items
* Updated "/pa addjoinitem" to allow for no argument to add to default config
* Fixed upgrader for Lobby
* Fixed upgrader for JoinItems
* Fixed upgrader for DROPPER courses
* Removed 'achieved' placeholder for now as it's a pain

## 7.0.0
### Java 11
The plugin now requires a minimum Java version of 11 to function.

### Configuration Changes
Parkour 6.7.1 used a config system which hadn't changed since the Plugin's inception in 2012, by combining all the Courses and Players data into their single config file. This resulted in insanely slow performance when changing and saving data regularly.

Parkour 7.0.0 introduced a new system of configuration, where each Player and each Course has its own JSON config file with its own data and nothing else. 
The performance improvements are outstanding: ![7.0.0 Performance](https://i.imgur.com/t3t4gbD.png "7.0.0 Performance")

Many new smaller config files have been introduced to store appropriate data.
### Default Course Settings
There is a new section in the config.yml that specifies default Course settings. A few examples include the "RewardOnce" flag, "MaxFallTicks" and "RewardDelay".

These can now be overridden on a per-course basis, using the "/pa setcourse (course)" command.
### Changes
* New AutoTabCompleter system to allow for more dynamic prompts
* New Config files (parkour-ranks.yml, auto-starts.yml, course-completions.yml, quiet-players.yml, parkour-lobbies.yml)
* Changed "/pa create" to allow for many creations
* Added 'achieved' column to 'time' table
* Added "/pa session" and "/pa parkourkit" commands
* Added "/pa delete rank (parkour-level)"
* Added "/pa admin" for administration commands
* Added "pac manualcheckpoint (player)" 
* Added "pac leaveall" console command to kick ALL Players from Courses
* Added "OnFinish.TeleportBeforePrize" to change finish order
* Added Course status Placeholders, with translations
* Added "Restore" option to "OnFinish.SetGameMode" to restore the Player's GameMode
* Added "%parkour_current_checkpoint_hologram_(course)_(number)%" placeholder for use with Holograms
* Added option to warn players if they've missed checkpoints
* Added "/pa delete leaderboardrow (course) (row)" to delete leaderboard rows manually
* Fixed the inconsistent Course finish time
* External Plugin is no longer disabled when not found on first startup. 
* Cancel void damage if DisablePlayerDamage is enabled
* More messages are processed through PlaceholderAPI
* Added 'OnServerRestart.KickPlayerFromCourse' config option
* ParkourSessions are now stored as JSON, instead of serialized objects as they were problematic
* Improvements to prevent Players floating on death blocks
* Allow Player look-up to use UUIDs
* Parkour Commands can now be disabled
* Improved SQL to use PreparedStatements for improved security
* Added a default entry for each event command in the config.yml
* Allowed for Per-Course event commands to be combined with default commands
* Allowed JoinItems to be ItemStacks
* Allowed "MaxFallTick" to be 0 which disables the check
* Removed individual CreateSign permissions, replaced with parkour.admin.createsign
* Tons of fixes and performance improvements
* Project now requires Java 11 to function

_Thank you to steve4744, and the various contributors for helping with this major update._  
_Additional thanks to the kind users who helped test early development builds!_

## 6.7.1
### Changes
* Clear PlaceholderAPI cache on database updates
* Fixes to Parkour messages being sent to Players

## 6.7
### Changes
* Added Potion ParkourBlock
* Added configurable time formats
* Added "setcheckpoint" console command to allow for external actions to increase the player's current checkpoint.
* Added option to confirm Restart tool
* Added ability to prevent GameMode being set
* Added Freedom ParkourMode cooldown option
* Added ability to insert PlaceholderAPI values in the ParkourRank chat
* Added ability to remove AutoStart by breaking pressure plates
* Added option to not reset Player's potions
* Added more Course Placeholders
* Fix setting Freedom location in air
* Fix NPE with missing command group
* Improvements to "safe" checkpoints
* Fixes for titles not appearing
* Reintroduced BountifulAPI support

## 6.6
### Changes
* Added ability for entities to attack player (OnCourse.PreventEntities.Attacking)
* Added "/pa setlobbycommand (lobby) (command)" command
* Added "OnRestart.FullPlayerRestart" config to allow for a 'quick' restart alternative
* Added / Changed Parkour event types to allow for better customisation ([more info](/tutorials/configuring-course?id=parkour-events))
* Added "/pa manualcheckpoint" for FreeCheckpoint ParkourMode (ParkourModes.FreeCheckpoint.ManualCheckpointCommandEnabled)
* Fixed "course_completed" placeholder
* Fixed rewardonce not working correctly
* Fixes to prevent NPEs
* Removed BountifulAPI dependency
* Fix concurrency issues in Placeholders
* Improvements to hide / show player & changed event order (thanks szumielxd)
* Changed Upgrader to be sync to prevent placeholder issues

### Breaking Changes
* [Parkour Event Types](/tutorials/configuring-course?id=parkour-events) have been updated / renamed, so you'll have to reconfigure the Course events in some cases.

## 6.5
### Changes
* Added Course display names `/pa setcourse (course) displayname (value)` (with new placeholders)
* Added Join Broadcast (OnJoin.BroadcastLevel)
* Added "JoinCourse" Sounds
* Added "%parkour_player_course_completed_(course)%" placeholder
* Added "%parkour_course_joinfee_(course)%" placeholder
* Added "%parkour_course_ecoreward_(course)%" placeholder
* Added "%parkour_course_players_(course)%" placeholder
* Added "%parkour_course_playerlist_(course)%" placeholder
* Added ability for commands to be run from Player starting with prefix "player:"
* Added "addlevel" to "setplayer" command
* Added optional Maximum ParkourLevel (ParkourTool.Restart.SecondCooldown)
* Added "ParkourTool.Restart.SecondCooldown" for Restart tool countdown (Other.Parkour.MaximumParkourLevel)
* Added new 'PlayerParkourRankEvent' event
* Added "/pa placeholder (placeholder)" to quickly test placeholder integration and expected output.
* Improvement to not send messages if blank
* Fix `PlayerParkourLevelEvent` not being fired
* Fix for 'Unbreakable' flag on 1.8 servers
* Made Parkour messages more consistent (replacing internal Parkour placeholders)
* Renamed "ResetTimeWithNoCheckpoint" to "ResetProgressWithNoCheckpoint" as all session details are reset now
* Changed AutoStart reactivation to reset session rather than restart course
* Fixed config issues after a fresh install

## 6.4
### Changes
* Added ability to remove Parkour messages
* Added remaining lives placeholder
* Added remaining lives to scoreboard
* Added "/pa hideall" command
* Added selected course shortcut for "/pa setprize" and "/pa setmode"
* Added ability for each course to be "resumable".
* Added default course command prize
* Fix Checkpoint signs
* Fix NPE when something removes the Scoreboard Parkour created
* Fix NPE on server restart
* Fix OnJoin with existing session
* Fix HideAll toggle
* Fixes and improvements for free_checkpoint ParkourMode
* Fixed norun ParkourMode
* Fix Hide All enabled swap
* Fix ParkourSession time after a server restart
* Fix for player losing walk speed on Challenge
* Fix for GUI on 1.8 servers
* Fix messages not being sent during some events
* Improve Restart message to use titles and respect Quiet mode
* Remove players reappeared message after leaving / finishing
* Changed "OnJoin.Item" to be "ParkourTool." in the config.yml

## 6.3
### Changes
* Added Option to Automatically hide players on Course join
* Added %DEATHS% as a placeholder in TopTen Placeholder
* Added new milliseconds Placeholder
* Added new Parkour event "course_record"
* Corrected "course_record" Placeholder to be more consistent
* Fix for Potion ParkourMode on death
* Fix for Player's health being too large
* Reset FallTicks when teleporting player
* Apply PreventPlateStick setting to AutoStart pressure plates
* Fix timer after a server reconnect
* Fix showing player when HideAll enabled

### Breaking Changes
* The `course_record` placeholder variables have changed order, now `%parkour_course_record_(course)_(value)%`

## 6.2
### Changes
* Option to treat last checkpoint as Finish
* Fixed ParkourLevel and ParkourRank format
* Added option to not teleport Player on Leave
* Treat AutoStart as a Restart if already on Course to prevent exploitation
* Option to teleport to the nearest Lobby when taking void damage (not on Course) Thanks to FrankHeijden
* Fix Placeholders when cache expired

## 6.1
### Changes
* Reimplemented placeholders: `%parkour_player_personal_best_(course)_time%` & `%parkour_player_personal_best_(course)_deaths%`
* Placeholders fix for not matching lowercase
* Added option to append the world name to AutoStart
* Various fixes and improvements

## 6.0
This update is a rewrite of v5.3 with performance and usability being at the forefront of every decision.

**If you have 5.3 installed, Parkour 6.0 will automatically upgrade your configuration.**

* Simply stop the server, delete the `Parkour.jar` and replace with `Parkour-6.0.jar`. 
* Start the server and check the console logs, the upgrade process will display the progress made and may take several minutes depending on the scale of your existing config. 
* Once completed you will be prompted to restart the server.

### Main Changes
- Player data is now stored under their UUID, not player name.
- `/pa finish` has been renamed to `/pa ready`
- ParkourSessions are now physically persisted instead of held in memory, this should mean considerable speed improvements for large servers
- PlaceholderAPI placeholders have been modified
- Improvements to Scoreboard
- Console Commands overhaul, most commands are now possible
- New and improved documentation
- Permission 'Parkour.Sign' renamed to 'Parkour.CreateSign'
- Tons of fixes and general usage improvements

### New Features
- New Course Settings GUI
- New `Potion` ParkourMode for applying Potion Effects.
- Option to save and restore join location
- `/pa setcourse` is now used for course options
- `/pa setplayer` is now used for player options
- Ability to display custom messages on join / leave / checkpoint / finish etc.
- Ability to execute commands on join / leave / checkpoint / finish etc.
- Customisable Sounds on join / leave / checkpoint / finish etc.
- Ability to leave a Course and rejoin at the same checkpoint
- New 'FREE_CHECKPOINT' ParkourMode treats any pressure plate as a Checkpoint.
- New 'POTION' ParkourMode, allowing you to apply a PotionType to the Player upon joining.

### Technical Information
- The main performance improvement in Parkour 6.0 is caching, almost every entity is stored in memory instead of read from the config files each time. Courses and their associated data is cached when needed, including the top leaderboard times for each course is cached.
- The `Info` utility classes now offer more consistent results

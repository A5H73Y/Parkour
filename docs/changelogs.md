Changelogs
======

Please note that each version of Parkour is backwards compatible with the previous version and will automatically upgrade your config upon start up. There will be no manual intervention unless stated in breaking changes.

## Parkour 6.5
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

## Parkour 6.4
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

## Parkour 6.3
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

## Parkour 6.2
### Changes
* Option to treat last checkpoint as Finish
* Fixed ParkourLevel and ParkourRank format
* Added option to not teleport Player on Leave
* Treat AutoStart as a Restart if already on Course to prevent exploitation
* Option to teleport to the nearest Lobby when taking void damage (not on Course) Thanks to FrankHeijden
* Fix Placeholders when cache expired

## Parkour 6.1
### Changes
* Reimplemented placeholders: `%parkour_player_personal_best_(course)_time%` & `%parkour_player_personal_best_(course)_deaths%`
* Placeholders fix for not matching lowercase
* Added option to append the world name to AutoStart
* Various fixes and improvements

## Parkour 6.0
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

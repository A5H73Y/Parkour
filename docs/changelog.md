Parkour 6.0
======

This update is a rewrite of v5.3 with performance and usability being at the forefront of every decision.

**If you have 5.3 installed, Parkour 6.0 will automatically upgrade your configuration.**

* Simply stop the server, delete the `Parkour.jar` and replace with `Parkour-6.0.jar`. 
* Start the server and check the console logs, the upgrade process will display the progress made and may take several minutes depending on the scale of your existing config. 
* Once completed you will be prompted to restart the server.

## Main Changes

- Player data is now stored under their UUID, not player name.
- `/pa finish` has been renamed to `/pa ready`
- ParkourSessions are now physically persisted instead of held in memory, this should mean considerable speed improvements for large servers
- PlaceholderAPI placeholders have been modified
- Improvements to Scoreboard
- Console Commands overhaul, most commands are now possible
- New and improved documentation
- Permission 'Parkour.Sign' renamed to 'Parkour.CreateSign'
- Tons of fixes and general usage improvements

## New Features
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


## Technical Information
- The main performance improvement in Parkour 6.0 is caching, almost every entity is stored in memory instead of read from the config files each time. Courses and their associated data is cached when needed, including the top leaderboard times for each course is cached.
- The `Info` utility classes now offer more consistent results

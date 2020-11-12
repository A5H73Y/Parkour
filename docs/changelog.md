Parkour 6.0
======

This update is a rewrite of v5.3 with performance and usability being the forefront of every decision.

**If you have 5.3 installed, stop the server and delete the `Parkour.jar` and replace with `Parkour-6.0.jar`. Start the server and check the logs, the upgrade process will display the progress made and may take several minutes depending on the scale of your existing config. Once completed you will be prompted to restart the server.**

## Main Changes

- `/pa finish` has been renamed to `/pa ready`
- All player data is now stored under their UUID, not player name.
- ParkourSessions are now physically persisted instead of held in memory, this should mean considerable speed improvements for large servers
- New PlaceholderAPI values
- Improvements to Scoreboard 
- New Sound system
- Tons of fixes and general usage improvements
- New and improved documentation

## New Features
- Option to store join location to lobby
- `/pa setcourse` is now used for course options
- `/pa setplayer` is now used for player options
- New `Potion` ParkourMode for applying Potion Effects.
- Ability to display custom messages on join / leave / checkpoint / finish etc.


## Technical Information
The main performance improvement in Parkour 6.0 is caching, almost every entity is stored in memory instead of read from the config files each time. Courses and their associated data is cached when needed, including the top leaderboard times for each course is cached.

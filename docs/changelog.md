Parkour 6.0
======

This update is a rewrite of v5.3 with performance and usability being the forefront of every decision.

## Main Changes

- `/pa finish` has been renamed to `/pa ready`.

## Technical Information
The main performance improvement in Parkour 6.0 is caching, almost every entity is stored in memory instead of read from the config files each time. Courses and their associated data is cached when needed, including the top leaderboard times for each course is cached.
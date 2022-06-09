Administration
======

## Delete Commands

Using the delete command, you are able to delete various things. Using the syntax: `/pa delete (choice) (argument)`.

All of which require conformation before the action is completed.

### Delete a Course

If you delete a Course it will remove all information stored on the server about it, including all references from the database so only use as a last resort. If preferred, you can reset a Course which will keep its structure (start location & checkpoints), but reset all of its stats and leaderboards.

![Deleting Course](https://i.imgur.com/apa5azA.png "Deleting Course")

![Delete Confirm](https://i.imgur.com/8ucihM7.png "Delete Confirm")  
_Command: `/pa delete course (course)`_

### Delete a Checkpoint

If you want to delete a checkpoint, it will start with the highest number and decrease to the lowest, for safety reasons. For example if your Course has 5 checkpoints, and you enter `/pa delete checkpoint (course)` it will ask if you want to delete checkpoint 5, if you execute the command again it will ask if you want to delete checkpoint 4, etc.

Note that you may want to overwrite a checkpoint if it needs moving, instead of deleting many. For example if you had 5 checkpoints and wanted to move the location of checkpoint 2, you can enter `/pa create checkpoint (course) 2` in the desired location.  
_Command: `/pa delete checkpoint (course)`_

### Delete a Lobby

If there are any Courses linked to this lobby, it will error and present a list of Courses still dependent on this lobby. You must link these Courses to a different Lobby before you are able to delete it.

![Delete Validation](https://i.imgur.com/wCO9jrU.png "Delete Validation")

_Command: `/pa delete lobby (lobby)`_

### Delete a ParkourKit

If there are any Courses linked to the ParkourKit, it will error and present a list of Courses still dependent on this kit. You must link these Courses to a different ParkourKit before you are able to delete it.

_Command: `/pa delete kit (kit)`_

### Delete a ParkourRank

You are able to delete a ParkourRank for the ParkourLevel provided which will remove the ability for players to unlock it. Note that Players which have already unlocked it will not be affected.

_Command: `/pa delete rank (level)`_

### Delete a Leaderboard Row

You can quickly delete the desired row from the specified Course leaderboard. Note that it's recommended to use [reset player]() or [reset player leaderboard]() first to remove any undesirable Player's leaderboards.

This will simply remove the row from the database, so the leaderboards will be recalculated afterwards.

_Command: `/pa delete leaderboardrow (row)`_

## Reset Commands

Using the reset command, you are able to reset a Course, Player, Leaderboard or Prize. Using the syntax: `/pa reset (choice) (argument)`.

All operations will have to be confirmed or cancelled before the change is made.

![Reset Command](https://i.imgur.com/r1gzO05.png "Reset Command")

### Reset a Course

This will delete all the statistics stored, which includes leaderboards and various Parkour attributes. This will NOT affect the spawn / checkpoints.  
_Command: `/pa reset course (course)`_

### Reset a Player

This will delete all their leaderboards across all Courses and delete all various Parkour attributes.  
_Command: `/pa reset player (player)`_

### Reset a Course Leaderboard

This action will remove **all** the times for the specified Course.  
_Command: `/pa reset leaderboard (course)`_

### Reset a Player Course Leaderboard

This action will remove the specified Player's times from a specified Course, when provided.  
_Command: `/pa reset leaderboard (course) [player]`_

### Reset a Prize

This will reset all the prizes for a Course, causing it to use the default prize specified in the `config.yml`.  
_Command: `/pa reset prize (course)`_

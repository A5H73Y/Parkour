Administration
======

## Delete Commands

Using the delete command, you are able to delete a Course, checkpoint, lobby or kit. Using the syntax: `/pa delete (choice) (argument)`.

All of which you will have to confirm before the action is complete.

### Delete a Course

If you delete a Course it will remove all information stored on the server about it, including all references from the database so only use as a last resort. If preferred, you can reset a Course which will keep it's structure (start location & checkpoints), but reset all of it's stats and leaderboards.

![Deleting Course](https://i.imgur.com/apa5azA.png "Deleting Course")
 
![Delete Confirm](https://i.imgur.com/8ucihM7.png "Delete Confirm")

### Delete a Checkpoint

If you want to delete a checkpoint, it will start with the highest number and decrease to the lowest, for safety reasons. For example if your Course has 5 checkpoints, and you enter `/pa delete checkpoint (course)` it will ask if you want to delete checkpoint 5, if you execute the command again it will ask if you want to delete checkpoint 4, etc.

Note that you may want to overwrite a checkpoint if it needs moving, instead of deleting many. For example if you had 5 checkpoints and wanted to move the location of checkpoint 2, you can enter `/pa checkpoint 2` in the desired location.

### Delete a Lobby

If you want to delete a lobby, it will remove all trace of the lobby from the server. To delete a lobby, simply enter `/pa delete lobby (lobby)`.

If there are any Courses that are linked to the lobby, it will alert you and you must address these before it can be executed, for safety reasons.

![Delete Validation](https://i.imgur.com/wCO9jrU.png "Delete Validation")

You must set the dependent Courses to use a different lobby before this change can be made.

### Delete a ParkourKit

You can delete a ParkourKit by using the command `/pa delete kit (kit name)` this will preform validation to check to see if any Courses are using this kit, that would break if the course were to be deleted.

If there are any Courses still using this kit it will error and present a list of courses still dependent on this kit. You must link these courses to a different kit before you are able to delete it.

### Delete an AutoStart

If you want to delete an AutoStart, it will remove the ability to use the location as an AutoStart for the Course.

You must break the pressureplate, then stand it its place and enter `/pa delete autostart (course)`.

## Reset Commands

Using the reset command, you are able to reset a Course, Player, Leaderboard or Prize. Using the syntax: `/pa reset (choice) (argument)`.

All operations will have to be confirmed or cancelled before the change is made.

![Reset Command](https://i.imgur.com/r1gzO05.png "Reset Command")

### Reset a Course

This will delete all the statistics stored, which includes leaderboards and various Parkour attributes. This will NOT affect the spawn / checkpoints.

### Reset a Player

This will delete all their leaderboards across all Courses and delete all various Parkour attributes.

### Reset a Leaderboard

Will remove all the leaderboards for a specified Course.

### Reset a Prize

This will reset all the prizes for a Course, causing it to use the default prize specified in the config.yml.

## Database Information

You have the choice of MySQL or SQLite for the database implementation. If you are unsure what your plugin has configured, enter `/pa sql` to view a summary.

### MySQL

MySQL is typically a remote connection and will usually be given to you if you have a online server management system, it is generally more powerful but can come of a cost of speed if the connection is poor.

The connection properties are found in the config.yml. The server will require a restart if you have made any changes.

### SQLite

SQLite is stored locally on the server and exists in "plugins/Parkour/sqlite-db/parkour.db". This is the default choice when you first install Parkour but can be configured at any point to change it. If you want to view the contents of the database you must use a 3rd party software, I would recommend SQLite Browser.

You are able to modify the path of the SQLite location in the config.yml, but only do this if you know what you are doing.

If you are going to modify the contents of the database, I strongly suggest you stop the server, make your changes and then start your server again to avoid any problems.

## Database Troubleshooting

### MySQL won't connect

If your MySQL connection fails, there will be an error in the server console for you to read. It will typically be incorrect login details, so make sure you enter them exactly into the config.yml.

### No times are appearing

Sometimes Parkour gets a bit confused, especially when swapping from SQLite to MySQL and vice versa. Luckily there is a command `/pa recreate` that forces the database to populate the entries of all the Courses that exist on to the server.


Leaderboard Database
======

You have the choice of MySQL or SQLite for the database implementation. If you are unsure what your plugin has configured, enter `/pa sql` to view a summary.

## Implementations

### MySQL

MySQL is typically a remote connection and will usually be provided to you if you have a package online server management system. It is generally more powerful but can come of a cost of speed if the connection is poor.

The plugin uses HikariCP to connect to a database, and the connection URL and properties can be configured in the `config.yml`. Any changes made will require a restart to apply.

_The technology is not just limited to MySQL but also includes support for: Apache Derby, Firebird, H2, HSQLDB, IBM DB2, IBM Informix, MS SQL Server, MySQL, MariaDB, Oracle, OrientDB, PostgreSQL, PostgreSQL, SAP MaxDB, SQLite, SyBase._

### SQLite

SQLite is stored locally on the server and exists in `plugins/Parkour/sqlite-db/parkour.db`. This is the default choice when you first install Parkour, but can be configured at any point to change it. If you want to view the contents of the database you must use a 3rd party software. I would recommend SQLite Browser.

You are able to modify the path of the SQLite location in the `config.yml`, but only do this if you know what you are doing.

If you modify the contents of the database, it is strongly suggested to stop the server, make your changes and then start your server again to avoid any problems.

## Resetting Data

### Reset a Course Leaderboard

This action will remove **all** the times for the specified Course.  
_Command: `/pa reset leaderboard (course)`_

### Reset a Player Course Leaderboard

This action will remove the specified Player's times from a specified Course, when provided.
_Command: `/pa reset leaderboard (course) [player]`_


## Database Troubleshooting

### MySQL won't connect

If your MySQL connection fails, there will be an error in the server console for you to read. It will typically be incorrect login details, so make sure you enter them exactly into the `config.yml`.

### No times appear

Sometimes Parkour gets a bit confused, especially when swapping from SQLite to MySQL and vice versa. Luckily there is a command `/pa recreate` that forces the database to populate the entries of all the Courses that exist on to the server. This will be executed when the server starts to synchronise any changes.

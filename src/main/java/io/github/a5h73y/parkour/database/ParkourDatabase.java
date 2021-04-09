package io.github.a5h73y.parkour.database;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.ParkourConstants;
import io.github.a5h73y.parkour.type.Cacheable;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pro.husk.Database;
import pro.husk.mysql.MySQL;

/**
 * Parkour Database Manager.
 * Database Utility methods and the database implementation instance are managed here.
 * Caching is used on course-specific related information.
 */
public class ParkourDatabase extends AbstractPluginReceiver implements Cacheable<List<TimeEntry>> {

    private Database database;

    private final Map<String, Integer> courseIdCache = new HashMap<>();
    private final Map<String, List<TimeEntry>> resultsCache = new HashMap<>();

    public ParkourDatabase(final Parkour parkour) {
        super(parkour);
        initiateConnection();
    }

    /**
     * Get the course's unique ID based on its name in the database.
     * Will display an error if the name doesn't match a database entry.
     *
     * @param courseName name of the course
     * @return course ID
     */
    public int getCourseId(String courseName) {
        return getCourseId(courseName, true);
    }

    /**
     * Get the course's unique ID based on its name in the database.
     *
     * @param courseName name of the course
     * @param printError display error if it doesn't exist
     * @return course ID
     */
    public int getCourseId(String courseName, boolean printError) {
        if (courseIdCache.containsKey(courseName.toLowerCase())) {
            PluginUtils.debug("Cached value found for " + courseName + ": " + courseIdCache.get(courseName.toLowerCase()));
            return courseIdCache.get(courseName.toLowerCase());
        }

        PluginUtils.debug("Finding course ID for " + courseName);
        int courseId = -1;

        String courseIdQuery = "SELECT courseId FROM course WHERE name = '" + courseName + "';";
        try (ResultSet rs = database.query(courseIdQuery)) {
            if (rs.next()) {
                courseId = rs.getInt("courseId");
            }
            rs.getStatement().close();
            if (courseId != -1) {
                courseIdCache.put(courseName.toLowerCase(), courseId);
            }
        } catch (SQLException e) {
            logSqlException(e);
        }

        if (courseId == -1 && printError) {
            PluginUtils.log("Course '" + courseName + "' was not found in the database. "
                    + "Run command '/pa recreate' to fix.", 1);
        }
        PluginUtils.debug("Found " + courseId);
        return courseId;
    }

    /**
     * Find the fastest times for the course.
     * Limit the results based on the parameter.
     *
     * @param courseName name of the course
     * @param limit amount of results
     * @return {@link TimeEntry} results
     */
    public List<TimeEntry> getTopCourseResults(String courseName, int limit) {
        List<TimeEntry> times = new ArrayList<>();
        int maxEntries = calculateResultsLimit(limit);
        int courseId = getCourseId(courseName.toLowerCase());
        PluginUtils.debug("Getting top " + maxEntries + " results for " + courseName);

        if (courseId == -1) {
            return times;
        }

        String courseResultsQuery = "SELECT * FROM time WHERE courseId=" + courseId
                + " ORDER BY time LIMIT " + maxEntries;

        try (ResultSet rs = database.query(courseResultsQuery)) {
            times = processTimes(rs);
            rs.getStatement().close();
        } catch (SQLException e) {
            logSqlException(e);
        }

        return times;
    }

    /**
     * Find the fastest times for the player on a course.
     * Limit the results based on the parameter.
     *
     * @param player target offline player
     * @param courseName name of the course
     * @param limit amount of results
     * @return {@link TimeEntry} results
     */
    public List<TimeEntry> getTopPlayerCourseResults(OfflinePlayer player, String courseName, int limit) {
        List<TimeEntry> times = new ArrayList<>();
        int maxEntries = calculateResultsLimit(limit);
        int courseId = getCourseId(courseName.toLowerCase());
        PluginUtils.debug("Getting top " + maxEntries + " results for " + player.getName() + " on " + courseName);

        if (courseId == -1) {
            return times;
        }

        String playerResultsQuery = "SELECT * FROM time WHERE courseId=" + courseId
                + " AND playerId='" + getPlayerId(player) + "' ORDER BY time LIMIT " + maxEntries;

        try (ResultSet rs = database.query(playerResultsQuery)) {
            times = processTimes(rs);
            rs.getStatement().close();
        } catch (SQLException e) {
            logSqlException(e);
        }

        return times;
    }

    /**
     * Determine if the player has achieved a time on the course.
     *
     * @param player target offline player
     * @param courseName name of the course
     * @return a time exists
     */
    public boolean hasPlayerAchievedTime(OfflinePlayer player, String courseName) {
        boolean timeExists = false;
        int courseId = getCourseId(courseName.toLowerCase());

        if (courseId == -1) {
            return timeExists;
        }

        String timeExistsQuery = "SELECT 1 FROM time WHERE courseId=" + courseId
                + " AND playerId='" + getPlayerId(player) + "' LIMIT 1;";

        try (ResultSet rs = database.query(timeExistsQuery)) {
            timeExists = rs.next();
            rs.getStatement().close();
        } catch (SQLException e) {
            logSqlException(e);
        }
        return timeExists;
    }

    /**
     * Calculate position of the time on the leaderboard.
     * If a player is specified, the leaderboards will be limited to them only.
     * Used to calculate their position for finishing, if they've beaten records etc.
     *
     * @param player target offline player
     * @param courseName name of the course
     * @param time time in milliseconds
     * @return leaderboard position
     */
    public int getPositionOnLeaderboard(@Nullable OfflinePlayer player, String courseName, long time) {
        int courseId = getCourseId(courseName);
        if (courseId == -1) {
            return -1;
        }

        String leaderboardPositionQuery = "SELECT 1 FROM time WHERE courseId=" + courseId + " AND time < " + time;

        if (player != null) {
            leaderboardPositionQuery += " AND playerId='" + getPlayerId(player) + "';";
        }

        PluginUtils.debug("Checking leaderboard position for: " + leaderboardPositionQuery);
        int position = 1;

        try (ResultSet rs = database.query(leaderboardPositionQuery)) {
            while (rs.next()) {
                position++;
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            logSqlException(e);
        }
        PluginUtils.debug("Leaderboard position is: " + position);
        return position;
    }

    /**
     * Calculate position of the time on the leaderboard.
     * Used to calculate the position for finishing, if they've beaten records etc.
     *
     * @param courseName name of the course
     * @param time time in milliseconds
     * @return global leaderboard position
     */
    public int getPositionOnLeaderboard(String courseName, long time) {
        return getPositionOnLeaderboard(null, courseName, time);
    }

    /**
     * Determine if this is the best time on the course.
     *
     * @param courseName name of the course
     * @param time time in milliseconds
     * @return is best course time
     */
    public boolean isBestCourseTime(String courseName, long time) {
        return getPositionOnLeaderboard(courseName, time) == 1;
    }

    /**
     * Determine if this is the best time on the course.
     *
     * @param courseName name of the course
     * @param time time in milliseconds
     * @return is best course time
     */
    public boolean isBestCourseTime(OfflinePlayer player, String courseName, long time) {
        return getPositionOnLeaderboard(player, courseName, time) == 1;
    }

    /**
     * Insert a Course into the Database.
     * Once a course has been created, a record will be entered into the database giving it a unique numeric identifier.
     * This identifier is then used to reference the course throughout the database.
     *
     * @param courseName name of the course
     */
    public void insertCourse(String courseName) {
        String insertCourseUpdate = "INSERT INTO course (name) VALUES ('" + courseName + "');";
        PluginUtils.debug("Inserted course: " + insertCourseUpdate);

        try {
            database.update(insertCourseUpdate);
        } catch (SQLException e) {
            logSqlException(e);
        }
    }

    /**
     * Insert a time record into the database for the player's time.
     *
     * @param courseName name of the course
     * @param player target Player
     * @param time time in milliseconds
     * @param deaths deaths accumulated
     */
    public void insertTime(String courseName, Player player, long time, int deaths) {
        int courseId = getCourseId(courseName);
        if (courseId == -1) {
            return;
        }

        String insertTimeUpdate = "INSERT INTO time (courseId, playerId, playerName, time, deaths) VALUES ("
                + courseId + ", '" + getPlayerId(player) + "', '"
                + player.getName() + "', " + time + ", " + deaths + ");";
        PluginUtils.debug("Inserting time: " + insertTimeUpdate);

        try {
            database.updateAsync(insertTimeUpdate).get();
            resultsCache.remove(courseName.toLowerCase());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert or Update player's time on course.
     * A time will be inserted or updated depending on a config option.
     * Updating will only apply once the player has beaten their best time.
     *
     * @param courseName name of the course
     * @param player target Player
     * @param time time in milliseconds
     * @param deaths deaths accumulated
     */
    public void insertOrUpdateTime(String courseName, Player player, long time, int deaths, boolean isNewRecord) {
        boolean updatePlayerTime = parkour.getConfig().getBoolean("OnFinish.UpdatePlayerDatabaseTime");
        PluginUtils.debug("Potentially Inserting or Updating Time for player: " + player.getName()
                + ", isNewRecord: " + isNewRecord + ", updatePlayerTime: " + updatePlayerTime);

        if (isNewRecord && updatePlayerTime) {
            PluginUtils.debug("Updating the Time for player " + player.getName());
            deletePlayerCourseTimes(player, courseName);
            insertTime(courseName, player, time, deaths);

        } else if (!updatePlayerTime) {
            PluginUtils.debug("Inserting a Time for player " + player);
            insertTime(courseName, player, time, deaths);
        }
    }

    /**
     * Delete all of the Player's leaderboard entries.
     * For usage if a player has been banned for cheating etc.
     *
     * @param player target offline player
     */
    public void deletePlayerTimes(OfflinePlayer player) {
        PluginUtils.debug("Deleting all Player times for " + player.getName());
        try {
            database.updateAsync("DELETE FROM time WHERE playerId='" + getPlayerId(player) + "'").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete all of the times for the course.
     *
     * @param courseName name of the course
     */
    public void deleteCourseTimes(String courseName) {
        int courseId = getCourseId(courseName);
        if (courseId == -1) {
            return;
        }

        PluginUtils.debug("Deleting all Course times for " + courseName);
        try {
            database.updateAsync("DELETE FROM time WHERE courseId=" + courseId).get();
            resultsCache.remove(courseName.toLowerCase());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete player times from a certain course.
     *
     * @param player target offline player
     * @param courseName name of the course
     */
    public void deletePlayerCourseTimes(OfflinePlayer player, String courseName) {
        int courseId = getCourseId(courseName);
        if (courseId == -1) {
            return;
        }

        PluginUtils.debug("Deleting all times for player " + player.getName() + " for course " + courseName);
        try {
            database.updateAsync("DELETE FROM time WHERE playerId='" + getPlayerId(player)
                    + "' AND courseId=" + courseId).get();
            resultsCache.remove(courseName.toLowerCase());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete all references to the Course from the database.
     * For usage if a course has been deleted.
     * Will remove the times and the course entry from the database.
     *
     * @param courseName name of the course
     */
    public void deleteCourseAndReferences(String courseName) {
        PluginUtils.debug("Completely deleting course " + courseName);
        try {
            database.updateAsync("DELETE FROM course WHERE name='" + courseName + "'").get();
            resultsCache.remove(courseName.toLowerCase());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        // reset the cache
        courseIdCache.clear();
    }

    /**
     * Display Database Connection summary details.
     *
     * @param sender requesting sender
     */
    public void displayInformation(CommandSender sender) {
        try {
            TranslationUtils.sendHeading("Parkour Database", sender);
            String databaseType = database instanceof MySQL ? "MySQL" : "SQLite";
            TranslationUtils.sendValue(sender, "Database Type", databaseType);

            ResultSet count = database.query("SELECT COUNT(*) FROM course;");
            TranslationUtils.sendValue(sender, "Courses", count.getInt(1));

            count = database.query("SELECT COUNT(*) FROM time;");
            TranslationUtils.sendValue(sender, "Times", count.getInt(1));

            count.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Recreate Parkour Courses.
     * Attempt to recreate all the parkour courses into the database.
     * This is required when the database becomes out of sync through manual editing.
     * Times cannot be stored until the course exists in the database.
     */
    public void recreateAllCourses(boolean displayMessage) {
        Bukkit.getScheduler().runTaskAsynchronously(parkour, () -> {
            if (displayMessage) {
                PluginUtils.log("Starting recreation of courses process...");
            }
            int changes = 0;
            for (String courseName : CourseInfo.getAllCourseNames()) {
                if (getCourseId(courseName, false) == -1) {
                    insertCourse(courseName);
                    changes++;
                }
            }
            if (displayMessage) {
                PluginUtils.log("Process complete. Courses recreated: " + changes);
            }
            if (changes > 0) {
                PluginUtils.logToFile("Courses recreated: " + changes);
            }
        });
    }

    /**
     * Request to close the database connection.
     * Performed on server shutdown.
     */
    public void closeConnection() {
        PluginUtils.debug("Closing the SQL connection.");
        try {
            this.database.closeConnection();
        } catch (SQLException e) {
            logSqlException(e);
        }
    }

    /**
     * Find the nth best time for the course.
     * Uses cache to quickly find the result based on position in the list.
     *
     * @param courseName course
     * @param position position
     * @return matching {@link TimeEntry}
     */
    public TimeEntry getNthBestTime(String courseName, int position) {
        List<TimeEntry> cachedResults = getCourseCache(courseName);
        return position > cachedResults.size() ? null : cachedResults.get(position - 1);
    }

    /**
     * Display Leaderboards Entries to player.
     *
     * @param sender target player
     * @param courseName name of the course
     * @param times {@link TimeEntry} results
     */
    public void displayTimeEntries(CommandSender sender, String courseName, List<TimeEntry> times) {
        if (times.isEmpty()) {
            TranslationUtils.sendMessage(sender, "No results were found!");
            return;
        }

        String heading = TranslationUtils.getTranslation("Parkour.LeaderboardHeading", false)
                .replace(ParkourConstants.COURSE_PLACEHOLDER, courseName)
                .replace(ParkourConstants.AMOUNT_PLACEHOLDER, String.valueOf(times.size()));

        TranslationUtils.sendHeading(heading, sender);

        for (int i = 0; i < times.size(); i++) {
            TimeEntry entry = times.get(i);
            String translation = TranslationUtils.getTranslation("Parkour.LeaderboardEntry", false)
                    .replace("%POSITION%", String.valueOf(i + 1))
                    .replace(ParkourConstants.PLAYER_PLACEHOLDER, entry.getPlayerName())
                    .replace(ParkourConstants.TIME_PLACEHOLDER, DateTimeUtils.displayCurrentTime(entry.getTime()))
                    .replace(ParkourConstants.DEATHS_PLACEHOLDER, String.valueOf(entry.getDeaths()));

            sender.sendMessage(translation);
        }
    }

    /**
     * Find the nth best time for the course.
     * Uses cache to quickly find the result based on position in the list.
     *
     * @param courseName course
     * @param results results
     * @return matching {@link TimeEntry}
     */
    public List<TimeEntry> getTopBestTimes(String courseName, int results) {
        List<TimeEntry> cachedResults = getCourseCache(courseName);
        int maxResults = Math.min(results, cachedResults.size());
        return cachedResults.subList(0, maxResults);
    }

    public Database getDatabase() {
        return database;
    }

    /**
     * Initialise connection to the configured Database source.
     * SQLite will be the default (and fallback) unless MySQL is correctly configured.
     */
    private void initiateConnection() {
        PluginUtils.debug("Initialising SQL Connection.");
        if (parkour.getConfig().getBoolean("MySQL.Use")) {
            PluginUtils.debug("Opting to use MySQL.");
            this.database = new MySQL(parkour.getConfig().getString("MySQL.URL"),
                    parkour.getConfig().getString("MySQL.Username"),
                    parkour.getConfig().getString("MySQL.Password"),
                    parkour.getConfig().getBoolean("MySQL.LegacyDriver"));
        } else {
            PluginUtils.debug("Opting to use SQLite.");
            String pathOverride = parkour.getConfig().getString("SQLite.PathOverride", "");
            String path = pathOverride.isEmpty()
                    ? parkour.getDataFolder() + File.separator + "sqlite-db" + File.separator : pathOverride;

            this.database = new SQLite(path, "parkour.db");
        }

        try {
            // attempt to create the required SQL tables
            // this will be the first time a connection is opened
            // if the attempt fails, it will fallback to SQLite by disabling MySQL (if enabled)
            setupTables();
        } catch (SQLException ex) {
            handleSqlConnectionException(ex);
        }
    }

    private void setupTables() throws SQLException {
        String createCourseTable = "CREATE TABLE IF NOT EXISTS course ("
                + "courseId INTEGER PRIMARY KEY AUTO_INCREMENT, "
                + "name VARCHAR(15) NOT NULL UNIQUE, "
                + "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);";

        String createTimesTable = "CREATE TABLE IF NOT EXISTS time ("
                + "timeId INTEGER PRIMARY KEY AUTO_INCREMENT, "
                + "courseId INTEGER NOT NULL, "
                + "playerId CHAR(36) CHARACTER SET ascii NOT NULL, "
                + "playerName VARCHAR(16) NOT NULL, "
                + "time DECIMAL(13,0) NOT NULL, "
                + "deaths INT(5) NOT NULL, "
                + "FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE);";

        // seems to be the only syntactic difference between them
        if (database instanceof SQLite) {
            createCourseTable = createCourseTable.replace("AUTO_INCREMENT", "AUTOINCREMENT");
            createTimesTable = createTimesTable.replace("AUTO_INCREMENT", "AUTOINCREMENT")
                    .replace("CHARACTER SET ascii", "");
        }

        PluginUtils.debug("Attempting to create necessary tables.");
        database.update(createCourseTable);
        database.update(createTimesTable);
        database.closeConnection();
        PluginUtils.debug("Successfully created necessary tables.");
    }

    private void handleSqlConnectionException(SQLException e) {
        PluginUtils.log("[SQL] Connection problem: " + e.getMessage(), 2);
        e.printStackTrace();

        // if they were trying to use MySQL
        if (parkour.getConfig().getBoolean("MySQL.Use")) {
            parkour.getConfig().set("MySQL.Use", false);
            parkour.saveConfig();

            PluginUtils.log("[SQL] Defaulting to SQLite...", 1);
            initiateConnection();
        } else {
            PluginUtils.log("[SQL] Failed to connect to SQLite.", 2);
        }
    }

    private void logSqlException(SQLException e) {
        PluginUtils.log("[SQL] Error occurred: " + e.getMessage(), 2);
        e.printStackTrace();
    }

    /**
     * Processes a ResultSet and returns TimeEntry results.
     *
     * @param rs ResultSet
     * @return time object results
     */
    private List<TimeEntry> processTimes(ResultSet rs) throws SQLException {
        List<TimeEntry> times = new ArrayList<>();

        while (rs.next()) {
            TimeEntry time = new TimeEntry(
                    rs.getString("playerId"),
                    rs.getString("playerName"),
                    rs.getLong("time"),
                    rs.getInt("deaths"));
            times.add(time);
        }
        return times;
    }

    /**
     * Calculate the number of results.
     * Must be within 1 and the maximum.
     *
     * @param limit limit results
     * @return amount of results
     */
    private int calculateResultsLimit(int limit) {
        return Math.max(1, Math.min(limit, parkour.getConfig().getMaximumCoursesCached()));
    }

    private List<TimeEntry> getCourseCache(String courseName) {
        if (!resultsCache.containsKey(courseName.toLowerCase())) {
            PluginUtils.debug("Populating times cache for " + courseName);
            resultsCache.put(courseName.toLowerCase(),
                    getTopCourseResults(courseName, parkour.getConfig().getMaximumCoursesCached()));
        }

        return resultsCache.get(courseName.toLowerCase());
    }

    public static String getPlayerId(OfflinePlayer player) {
        return player.getUniqueId().toString().replace("-", "");
    }

    @Override
    public int getCacheSize() {
        return resultsCache.size();
    }

    @Override
    public void clearCache() {
        resultsCache.clear();
    }
}

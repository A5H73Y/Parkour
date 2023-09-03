package io.github.a5h73y.parkour.database;

import static io.github.a5h73y.parkour.other.ParkourConstants.AMOUNT_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.COURSE_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.DEATHS_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PLAYER_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.POSITION_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TIME_PLACEHOLDER;
import static io.github.a5h73y.parkour.utility.PluginUtils.readContentsOfResource;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.impl.DefaultConfig;
import io.github.a5h73y.parkour.type.CacheableParkourManager;
import io.github.a5h73y.parkour.type.Initializable;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.husk.Database;
import pro.husk.mysql.MySQL;

/**
 * Parkour Database Manager.
 * Database Utility methods and the database implementation instance are managed here.
 * Caching is used on course-specific related information.
 */
public class DatabaseManager extends CacheableParkourManager implements Initializable {

    private static final String SELECT_TIME_DATA_QUERY = "SELECT courseId, playerId, time, deaths FROM time";

    private Database database;

    private final Map<String, Integer> courseIdCache = new HashMap<>();
    private final Map<String, List<TimeEntry>> resultsCache = new HashMap<>();

    public DatabaseManager(final Parkour parkour) {
        super(parkour);
        initiateConnection();
    }

    /**
     * Get the Player ID which is used in the Database.
     * @param player player
     * @return player ID
     */
    public static String getPlayerId(@NotNull OfflinePlayer player) {
        return player.getUniqueId().toString().replace("-", "");
    }

    /**
     * Get the course's unique ID based on its name in the database.
     * Will display an error if the name doesn't match a database entry.
     *
     * @param courseName name of the course
     * @return course ID
     */
    public int getCourseId(@NotNull String courseName) {
        return getCourseId(courseName, true);
    }

    /**
     * Get the course's unique ID based on its name in the database.
     *
     * @param courseNameRaw name of the course
     * @param printError display error if it doesn't exist
     * @return course ID
     */
    public int getCourseId(@NotNull String courseNameRaw, boolean printError) {
        int courseId = -1;

        if (!ValidationUtils.isStringValid(courseNameRaw)) {
            return courseId;
        }

        String courseName = courseNameRaw.toLowerCase();
        if (courseIdCache.containsKey(courseName)) {
            PluginUtils.debug("Cached value found for " + courseName + ": " + courseIdCache.get(courseName));
            return courseIdCache.get(courseName);
        }

        PluginUtils.debug("Finding course ID for " + courseName);
        String courseIdQuery = "SELECT courseId FROM course WHERE name = ?;";

        try (PreparedStatement statement = getDatabaseConnection().prepareStatement(courseIdQuery)) {
            statement.setString(1, courseName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                courseId = resultSet.getInt("courseId");
                courseIdCache.put(courseName, courseId);
            }
            resultSet.getStatement().close();
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
     * @param resultsLimit amount of results
     * @return {@link TimeEntry} results
     */
    public List<TimeEntry> getTopCourseResults(@NotNull String courseName, int resultsLimit) {
        List<TimeEntry> results = new ArrayList<>();
        int courseId = getCourseId(courseName.toLowerCase());

        if (courseId > 0) {
            int maxEntries = calculateResultsLimit(resultsLimit);
            PluginUtils.debug("Getting top " + maxEntries + " results for " + courseName);
            String courseResultsQuery = SELECT_TIME_DATA_QUERY + " WHERE courseId=? ORDER BY time LIMIT ?";

            try (PreparedStatement statement = getDatabaseConnection().prepareStatement(courseResultsQuery)) {
                statement.setInt(1, courseId);
                statement.setInt(2, resultsLimit);
                ResultSet resultSet = statement.executeQuery();
                results = extractTimeEntries(resultSet);
                resultSet.getStatement().close();
            } catch (SQLException e) {
                logSqlException(e);
            }
        }

        return results;
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
        List<TimeEntry> results = new ArrayList<>();
        int courseId = getCourseId(courseName.toLowerCase());

        if (courseId > 0) {
            int maxEntries = calculateResultsLimit(limit);
            PluginUtils.debug("Getting top " + maxEntries + " results for " + player.getName() + " on " + courseName);
            String playerResultsQuery = SELECT_TIME_DATA_QUERY + " WHERE courseId=? AND playerId=? ORDER BY time LIMIT ?";

            try (PreparedStatement statement = getDatabaseConnection().prepareStatement(playerResultsQuery)) {
                statement.setInt(1, courseId);
                statement.setString(2, getPlayerId(player));
                statement.setInt(3, maxEntries);
                ResultSet resultSet = statement.executeQuery();
                results = extractTimeEntries(resultSet);
                resultSet.getStatement().close();
            } catch (SQLException e) {
                logSqlException(e);
            }
        }

        return results;
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
    public int getPositionEntryOnLeaderboard(@Nullable OfflinePlayer player, String courseName, long time) {
        int result = -1;
        int courseId = getCourseId(courseName);

        if (courseId > 0) {
            result = 1;
            String leaderboardPositionQuery = "SELECT 1 FROM time WHERE courseId=? AND time < ?";

            if (player != null) {
                leaderboardPositionQuery += " AND playerId=?";
            }

            PluginUtils.debug("Checking leaderboard position for: " + leaderboardPositionQuery);

            try (PreparedStatement statement = getDatabaseConnection().prepareStatement(leaderboardPositionQuery)) {
                statement.setInt(1, courseId);
                statement.setLong(2, time);

                if (player != null) {
                    statement.setString(3, getPlayerId(player));
                }
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result++;
                }
                resultSet.getStatement().close();
            } catch (SQLException e) {
                logSqlException(e);
            }
            PluginUtils.debug("Leaderboard position is: " + result);
        }
        return result;
    }

    /**
     * Calculate position of the time on the leaderboard.
     * Used to calculate the position for finishing, if they've beaten records etc.
     *
     * @param courseName name of the course
     * @param time time in milliseconds
     * @return global leaderboard position
     */
    public int getPositionEntryOnLeaderboard(String courseName, long time) {
        return getPositionEntryOnLeaderboard(null, courseName, time);
    }

    /**
     * Get the Player's best time position on specified Course.
     * 
     * @param player player
     * @param courseName course name
     * @return leaderboard position
     */
    public int getPositionOnLeaderboard(OfflinePlayer player, String courseName) {
        int result = -1;
        int courseId = getCourseId(courseName);

        if (courseId > 0 && hasPlayerAchievedTime(player, courseName)) {
            String leaderboardPositionQuery = "SELECT COUNT(*) AS position FROM time WHERE courseId=? AND time < "
                    + "(SELECT time FROM time WHERE courseId=? AND playerId=? ORDER BY time LIMIT 1)";

            PluginUtils.debug("Checking leaderboard position for: " + leaderboardPositionQuery);

            try (PreparedStatement statement = getDatabaseConnection().prepareStatement(leaderboardPositionQuery)) {
                statement.setInt(1, courseId);
                statement.setInt(2, courseId);
                statement.setString(3, getPlayerId(player));

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    result = resultSet.getInt("position");
                }
                resultSet.getStatement().close();
            } catch (SQLException e) {
                logSqlException(e);
            }
            PluginUtils.debug("Leaderboard position is: " + result);
        }
        return result;
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

    /**
     * Find the nth best time for the course.
     * Uses cache to quickly find the result based on position in the list.
     *
     * @param courseName course
     * @param position position
     * @return matching {@link TimeEntry}
     */
    @Nullable
    public TimeEntry getNthBestTime(String courseName, int position) {
        List<TimeEntry> cachedResults = getCourseCache(courseName);
        return position > cachedResults.size() ? null : cachedResults.get(position - 1);
    }

    /**
     * Determine if the player has achieved a time on the course.
     *
     * @param player target offline player
     * @param courseName name of the course
     * @return a time exists
     */
    public boolean hasPlayerAchievedTime(OfflinePlayer player, String courseName) {
        boolean result = false;
        int courseId = getCourseId(courseName.toLowerCase());

        if (courseId > 0) {
            String timeExistsQuery = "SELECT 1 FROM time WHERE courseId=? AND playerId=? LIMIT 1;";

            try (PreparedStatement statement = getDatabaseConnection().prepareStatement(timeExistsQuery)) {
                statement.setInt(1, courseId);
                statement.setString(2, getPlayerId(player));
                ResultSet resultSet = statement.executeQuery();

                result = resultSet.next();
                resultSet.getStatement().close();
            } catch (SQLException e) {
                logSqlException(e);
            }
        }
        return result;
    }

    /**
     * Get the sum of Player's accumulated deaths.
     * @param player target offline player
     * @return total deaths
     */
    public int getAccumulatedDeaths(@NotNull OfflinePlayer player) {
        int result = 0;
        try (PreparedStatement statement = getDatabaseConnection().prepareStatement(
                "SELECT SUM(deaths) AS total FROM time WHERE playerId = ?")) {
            statement.setString(1, getPlayerId(player));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                result = resultSet.getInt("total");
            }
            resultSet.getStatement().close();
        } catch (SQLException e) {
            logSqlException(e);
        }

        return result;
    }

    /**
     * Get the sum of Player's accumulated time in milliseconds.
     * @param player target offline player
     * @return total time in ms
     */
    public long getAccumulatedTime(@NotNull OfflinePlayer player) {
        long result = 0;
        try (PreparedStatement statement = getDatabaseConnection().prepareStatement(
                "SELECT SUM(time) AS total FROM time WHERE playerId = ?")) {
            statement.setString(1, getPlayerId(player));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                result = resultSet.getLong("total");
            }
            resultSet.getStatement().close();
        } catch (SQLException e) {
            logSqlException(e);
        }

        return result;
    }

    /**
     * Get the sum of Player's recorded times.
     * @param player target offline player
     * @return total times
     */
    public int getAccumulatedTimes(@NotNull OfflinePlayer player) {
        int result = 0;
        try (PreparedStatement statement = getDatabaseConnection().prepareStatement(
                "SELECT COUNT(*) AS total FROM time WHERE playerId = ?")) {
            statement.setString(1, getPlayerId(player));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                result = resultSet.getInt("total");
            }
            resultSet.getStatement().close();
        } catch (SQLException e) {
            logSqlException(e);
        }

        return result;
    }



    /**
     * Determine if this is the best time on the course.
     *
     * @param courseName name of the course
     * @param time time in milliseconds
     * @return is best course time
     */
    public boolean isBestCourseTime(String courseName, long time) {
        return getPositionEntryOnLeaderboard(courseName, time) == 1;
    }

    /**
     * Determine if this is the best time on the course.
     *
     * @param courseName name of the course
     * @param time time in milliseconds
     * @return is best course time
     */
    public boolean isBestCourseTime(OfflinePlayer player, String courseName, long time) {
        return getPositionEntryOnLeaderboard(player, courseName, time) == 1;
    }

    /**
     * Insert a Course into the Database.
     * Once a course has been created, a record will be entered into the database giving it a unique numeric identifier.
     * This identifier is then used to reference the course throughout the database.
     *
     * @param courseName name of the course
     */
    public void insertCourse(String courseName) {
        String insertCourseUpdate = "INSERT INTO course (name) VALUES (?);";
        PluginUtils.debug("Inserted course: " + insertCourseUpdate);

        try (PreparedStatement statement = getDatabaseConnection().prepareStatement(insertCourseUpdate)) {
            statement.setString(1, courseName);
            statement.executeUpdate();
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

        if (courseId > 0) {
            String insertTimeUpdate = "INSERT INTO time (courseId, playerId, time, deaths) VALUES (?, ?, ?, ?);";
            PluginUtils.debug("Inserting time for: " + player.getName());

            try {
                CompletableFuture.supplyAsync(() -> {
                    int results = 0;
                    try (PreparedStatement statement = getDatabaseConnection().prepareStatement(insertTimeUpdate)) {
                        statement.setInt(1, courseId);
                        statement.setString(2, getPlayerId(player));
                        statement.setLong(3, time);
                        statement.setInt(4, deaths);
                        results = statement.executeUpdate();
                    } catch (SQLException e) {
                        logSqlException(e);
                    }
                    return results;
                }).get();
                resultsCache.remove(courseName.toLowerCase());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
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
    public void insertOrUpdateTime(@NotNull String courseName,
                                   @NotNull Player player,
                                   long time, int deaths, boolean isNewRecord) {
        boolean updatePlayerTime = getConfig().getBoolean("OnFinish.UpdatePlayerDatabaseTime");
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
     * Rename the specified course name to the desired course name.
     *
     * @param targetCourseName target course name
     * @param desiredCourseName desired course name
     */
    public void renameCourse(String targetCourseName, String desiredCourseName) {
        PluginUtils.debug("Renaming course " + targetCourseName + " to " + desiredCourseName);
        String renameCourseQuery = "UPDATE course SET name=? WHERE name=?";

        try {
            CompletableFuture.supplyAsync(() -> {
                int results = 0;
                try (PreparedStatement statement = getDatabaseConnection().prepareStatement(renameCourseQuery)) {
                    statement.setString(1, targetCourseName);
                    statement.setString(2, desiredCourseName);
                    results = statement.executeUpdate();
                } catch (SQLException e) {
                    logSqlException(e);
                }
                return results;
            }).get();
            resultsCache.remove(targetCourseName);
            courseIdCache.remove(targetCourseName);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete all the Player's leaderboard entries.
     * For usage if a player has been banned for cheating etc.
     *
     * @param player target offline player
     */
    public void deletePlayerTimes(@NotNull OfflinePlayer player) {
        String deletePlayerTimesUpdate = "DELETE FROM time WHERE playerId=?";
        PluginUtils.debug("Deleting all Player times for " + player.getName());

        try {
            CompletableFuture.supplyAsync(() -> {
                int results = 0;
                try (PreparedStatement statement = getDatabaseConnection().prepareStatement(deletePlayerTimesUpdate)) {
                    statement.setString(1, getPlayerId(player));
                    results = statement.executeUpdate();
                } catch (SQLException e) {
                    logSqlException(e);
                }
                return results;
            }).get();
            clearCache();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete all the times for the course.
     *
     * @param courseName name of the course
     */
    public void deleteCourseTimes(@NotNull String courseName) {
        int courseId = getCourseId(courseName);
        if (courseId > 0) {
            String deleteCourseTimesUpdate = "DELETE FROM time WHERE courseId=?";
            PluginUtils.debug("Deleting all Course times for " + courseName);

            try {
                CompletableFuture.supplyAsync(() -> {
                    int results = 0;
                    try (PreparedStatement statement = getDatabaseConnection().prepareStatement(deleteCourseTimesUpdate)) {
                        statement.setInt(1, courseId);
                        results = statement.executeUpdate();
                    } catch (SQLException e) {
                        logSqlException(e);
                    }
                    return results;
                }).get();
                resultsCache.remove(courseName.toLowerCase());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete player times from a certain course.
     *
     * @param player target offline player
     * @param courseName name of the course
     */
    public void deletePlayerCourseTimes(@NotNull OfflinePlayer player, @NotNull String courseName) {
        int courseId = getCourseId(courseName);

        if (courseId > 0) {
            String deletePlayerCourseTimes = "DELETE FROM time WHERE playerId=? AND courseId=?";
            PluginUtils.debug("Deleting all times for player " + player.getName() + " for course " + courseName);

            try {
                CompletableFuture.supplyAsync(() -> {
                    int results = 0;
                    try (PreparedStatement statement = getDatabaseConnection().prepareStatement(deletePlayerCourseTimes)) {
                        statement.setString(1, getPlayerId(player));
                        statement.setInt(2, courseId);
                        results = statement.executeUpdate();
                    } catch (SQLException e) {
                        logSqlException(e);
                    }
                    return results;
                }).get();
                resultsCache.remove(courseName.toLowerCase());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete all references to the Course from the database.
     * For usage if a course has been deleted.
     * Will remove the times and the course entry from the database.
     *
     * @param courseNameRaw name of the course
     */
    public void deleteCourseAndReferences(@NotNull String courseNameRaw) {
        PluginUtils.debug("Completely deleting course " + courseNameRaw);
        String courseName = courseNameRaw.toLowerCase();
        String deleteCourseUpdate = "DELETE FROM course WHERE name=?";

        try {
            CompletableFuture.supplyAsync(() -> {
                int results = 0;
                try (PreparedStatement statement = getDatabaseConnection().prepareStatement(deleteCourseUpdate)) {
                    statement.setString(1, courseName);
                    results = statement.executeUpdate();
                } catch (SQLException e) {
                    logSqlException(e);
                }
                return results;
            }).get();
            resultsCache.remove(courseName);
            courseIdCache.remove(courseName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Display Database Connection summary details.
     *
     * @param commandSender command sender
     */
    public void displayInformation(CommandSender commandSender) {
        try {
            TranslationUtils.sendHeading("Parkour Database", commandSender);
            String databaseType = database instanceof MySQL ? "MySQL" : "SQLite";
            TranslationUtils.sendValue(commandSender, "Database Type", databaseType);

            ResultSet count = database.query("SELECT COUNT(*) FROM course;");
            TranslationUtils.sendValue(commandSender, "Courses", count.getInt(1));

            count = database.query("SELECT COUNT(*) FROM time;");
            TranslationUtils.sendValue(commandSender, "Times", count.getInt(1));

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
            for (String courseName : parkour.getCourseManager().getCourseNames()) {
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
     * Display Leaderboards Entries to player.
     *
     * @param commandSender command sender
     * @param courseName name of the course
     * @param times {@link TimeEntry} results
     */
    public void displayTimeEntries(@NotNull CommandSender commandSender,
                                   @NotNull String courseName,
                                   @Nullable List<TimeEntry> times) {
        if (times == null || times.isEmpty()) {
            TranslationUtils.sendMessage(commandSender, "No results were found!");
            return;
        }

        String heading = TranslationUtils.getTranslation("Parkour.LeaderboardHeading", false)
                .replace(COURSE_PLACEHOLDER, courseName)
                .replace(AMOUNT_PLACEHOLDER, String.valueOf(times.size()));

        TranslationUtils.sendHeading(heading, commandSender);

        for (int i = 0; i < times.size(); i++) {
            TimeEntry entry = times.get(i);
            String translation = TranslationUtils.getTranslation("Parkour.LeaderboardEntry", false)
                    .replace(POSITION_PLACEHOLDER, String.valueOf(i + 1))
                    .replace(PLAYER_PLACEHOLDER, entry.getPlayerName())
                    .replace(TIME_PLACEHOLDER, DateTimeUtils.displayCurrentTime(entry.getTime()))
                    .replace(DEATHS_PLACEHOLDER, String.valueOf(entry.getDeaths()));

            commandSender.sendMessage(translation);
        }
    }

    public Database getDatabase() {
        return database;
    }

    @Override
    public int getCacheSize() {
        return resultsCache.size();
    }

    @Override
    public void clearCache() {
        resultsCache.clear();
    }

    @Override
    public int getInitializeSequence() {
        return 2;
    }

    @Override
    public void initialize() {
        recreateAllCourses(false);
    }

    @Override
    protected DefaultConfig getConfig() {
        return parkour.getParkourConfig();
    }

    @Override
    public void teardown() {
        closeConnection();
    }

    /**
     * Initialise connection to the configured Database source.
     * SQLite will be the default (and fallback) unless MySQL is correctly configured.
     */
    private void initiateConnection() {
        PluginUtils.debug("Initialising SQL Connection.");
        if (getConfig().getBoolean("MySQL.Use")) {
            PluginUtils.debug("Opting to use MySQL.");
            this.database = new MySQL(getConfig().getString("MySQL.URL"),
                    getConfig().getString("MySQL.Username"),
                    getConfig().getString("MySQL.Password"),
                    getConfig().getBoolean("MySQL.LegacyDriver"));
        } else {
            PluginUtils.debug("Opting to use SQLite.");
            String pathOverride = getConfig().getString("SQLite.PathOverride");
            String path = pathOverride.isEmpty()
                    ? parkour.getDataFolder() + File.separator + "sqlite-db" + File.separator : pathOverride;

            this.database = new SQLite(path, "parkour.db");
        }

        try {
            // attempt to create the required SQL tables
            // this will be the first time a connection is opened
            // if the attempt fails, it will fall back to SQLite by disabling MySQL (if enabled)
            setupTables();
        } catch (SQLException ex) {
            handleSqlConnectionException(ex);
        }
    }

    private void setupTables() throws SQLException {
        String sqlResourcePrefix = "sql/" + (database instanceof MySQL ? "mysql" : "sqlite") + "/";
        PluginUtils.debug("Attempting to create necessary tables.");
        try {
            database.update(readContentsOfResource(sqlResourcePrefix + "course.sql"));
            database.update(readContentsOfResource(sqlResourcePrefix + "time.sql"));
            PluginUtils.debug("Successfully created necessary tables.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSqlConnectionException(SQLException e) {
        PluginUtils.log("[SQL] Connection problem: " + e.getMessage(), 2);
        e.printStackTrace();

        // if they were trying to use MySQL
        if (getConfig().getBoolean("MySQL.Use")) {
            getConfig().set("MySQL.Use", false);
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
     * @param resultSet ResultSet
     * @return time object results
     */
    private List<TimeEntry> extractTimeEntries(ResultSet resultSet) throws SQLException {
        List<TimeEntry> times = new ArrayList<>();

        while (resultSet.next()) {
            TimeEntry time = new TimeEntry(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getLong(3),
                    resultSet.getInt(4));

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
        return Math.max(1, Math.min(limit, getConfig().getMaximumCoursesCached()));
    }

    private List<TimeEntry> getCourseCache(String courseName) {
        if (!resultsCache.containsKey(courseName.toLowerCase())) {
            PluginUtils.debug("Populating times cache for " + courseName);
            resultsCache.put(courseName.toLowerCase(),
                    getTopCourseResults(courseName, getConfig().getMaximumCoursesCached()));
        }

        return resultsCache.get(courseName.toLowerCase());
    }

    /**
     * Checks if a connection is open with the database.
     *
     * @return true if the connection is open
     * @throws SQLException if the connection cannot be checked
     */
    private Connection getDatabaseConnection() throws SQLException {
        return database.getConnection();
    }
}

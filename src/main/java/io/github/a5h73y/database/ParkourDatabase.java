package io.github.a5h73y.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.github.a5h73y.Parkour;
import io.github.a5h73y.course.CourseInfo;
import io.github.a5h73y.utilities.Utils;
import org.bukkit.Bukkit;
import pro.husk.Database;
import pro.husk.mysql.MySQL;

public class ParkourDatabase {

    private final Parkour parkour;
    private Database database;

    private Map<String, Integer> courseIdCache = new HashMap<>();

    public ParkourDatabase(final Parkour parkour) {
        this.parkour = parkour;
        initiateConnection();
    }

    /**
     * Return the course's unique ID based on its name in the database.
     * Will display an error if the name doesn't match a database entry.
     *
     * @param courseName name of the course
     * @return course ID
     */
    public int getCourseId(String courseName) {
        return getCourseId(courseName, true);
    }

    /**
     * Return the course's unique ID based on its name in the database.
     *
     * @param courseName name of the course
     * @param printError display error if it doesn't exist
     * @return course ID
     */
    public int getCourseId(String courseName, boolean printError) {
        if (courseIdCache.containsKey(courseName.toLowerCase())) {
            Utils.debug("Cached value found for " + courseName);
            return courseIdCache.get(courseName.toLowerCase());
        }

        Utils.debug("Finding course ID for " + courseName);
        int courseId = -1;

        String courseIdQuery = "SELECT courseId FROM course WHERE name = '" + courseName + "';";
        try (ResultSet rs = database.query(courseIdQuery)) {
            if (rs.next()) {
                courseId = rs.getInt("courseId");
            }
            rs.getStatement().close();
            courseIdCache.put(courseName.toLowerCase(), courseId);
        } catch (SQLException e) {
            logSQLException(e);
        }

        if (courseId == -1 && printError) {
            Utils.log("Course '" + courseName + "' was not found in the database. "
                    + "Run command '/pa recreate' to fix.", 1);
        }
        Utils.debug("Found " + courseId);
        return courseId;
    }

    /**
     * Find the top results for a course.
     *
     * @param courseName
     * @param limit
     * @return Time Objects
     */
    public List<TimeObject> getTopCourseResults(String courseName, int limit) {
        List<TimeObject> times = new ArrayList<>();
        int maxEntries = calculateResultsLimit(limit);
        int courseId = getCourseId(courseName.toLowerCase());
        Utils.debug("Getting top " + maxEntries + " results for " + courseName);

        if (courseId == -1) {
            return times;
        }

        String courseResultsQuery = "SELECT player, time, deaths FROM time"
                + " WHERE courseId=" + courseId + " ORDER BY time LIMIT " + maxEntries;

        try (ResultSet rs = database.query(courseResultsQuery)) {
            times = processTimes(rs);
            rs.getStatement().close();
        } catch (SQLException e) {
            logSQLException(e);
        }

        return times;
    }

    /**
     * Find the top player results for a course.
     *
     * @param playerName
     * @param courseName
     * @param limit
     * @return Time Objects
     */
    public List<TimeObject> getTopPlayerCourseResults(String playerName, String courseName, int limit) {
        List<TimeObject> times = new ArrayList<>();
        int maxEntries = calculateResultsLimit(limit);
        int courseId = getCourseId(courseName.toLowerCase());
        Utils.debug("Getting top " + maxEntries + " results for " + playerName + " on " + courseName);

        if (courseId == -1) {
            return times;
        }

        String playerResultsQuery = "SELECT player, time, deaths FROM time"
                + " WHERE courseId=" + courseId + " AND player='" + playerName + "' ORDER BY time LIMIT " + maxEntries;

        try (ResultSet rs = database.query(playerResultsQuery)) {
            times = processTimes(rs);
            rs.getStatement().close();
        } catch (SQLException e) {
            logSQLException(e);
        }

        return times;
    }

    /**
     * Determine if the player has a time record for the course.
     *
     * @param playerName
     * @param courseName
     * @return record found
     */
    public boolean hasPlayerAchievedTime(String playerName, String courseName) {
        boolean timeExists = false;
        int courseId = getCourseId(courseName.toLowerCase());

        if (courseId == -1) {
            return timeExists;
        }

        String timeExistsQuery = "SELECT 1 FROM time" +
                " WHERE courseId=" + courseId + " AND player='" + playerName + "' LIMIT 1;";

        try (ResultSet rs = database.query(timeExistsQuery)) {
            timeExists = rs.next();
            rs.getStatement().close();
        } catch (SQLException e) {
            logSQLException(e);
        }
        return timeExists;
    }

    /**
     * Determine if this is the best time for the player.
     *
     * @param playerName
     * @param courseName
     * @param time
     * @return is players best time
     */
    public boolean isBestPlayerTime(String playerName, String courseName, long time) {
        boolean bestPlayerTime = false;
        int courseId = getCourseId(courseName);
        if (courseId == -1) {
            return bestPlayerTime;
        }

        String bestPlayerQuery = "SELECT 1 FROM time"
                + " WHERE player='" + playerName + "' AND courseId=" + courseId + " AND time < " + time + ";";
        Utils.debug("Checking is best player time: " + bestPlayerQuery);

        try (ResultSet rs = database.query(bestPlayerQuery)) {
            bestPlayerTime = !rs.next();
            rs.getStatement().close();
        } catch (SQLException e) {
            logSQLException(e);
        }
        return bestPlayerTime;
    }

    /**
     * Determine if this is the best time on the course.
     *
     * @param courseName
     * @param time
     * @return is best course time
     */
    public boolean isBestCourseTime(String courseName, long time) {
        boolean bestCourseTime = false;
        int courseId = getCourseId(courseName);
        if (courseId == -1) {
            return bestCourseTime;
        }

        String bestCourseQuery = "SELECT 1 FROM time"
                + " WHERE courseId=" + courseId + " AND time < " + time + ";";
        Utils.debug("Checking is best course time: " + bestCourseQuery);

        try (ResultSet rs = database.query(bestCourseQuery)) {
            bestCourseTime = !rs.next();
            rs.getStatement().close();
        } catch (SQLException e) {
            logSQLException(e);
        }
        return bestCourseTime;
    }

    /**
     * Once a course has been created, a record will be entered into the database, giving it a unique numeric identifier.
     * This identifier is then used to reference the course throughout the database.
     *
     * @param courseName
     * @param playerName
     */
    public void insertCourse(String courseName, String playerName) {
        String insertCourseUpdate = "INSERT INTO course (name, author) VALUES ('" + courseName + "', '" + playerName + "');";
        Utils.debug("Inserted course: " + insertCourseUpdate);

        try {
            database.update(insertCourseUpdate);
        } catch (SQLException e) {
            logSQLException(e);
        }
    }

    /**
     * Insert a time record into the database for the player's time.
     *
     * @param courseName
     * @param playerName
     * @param time
     * @param deaths
     */
    private void insertTime(String courseName, String playerName, long time, int deaths) {
        int courseId = getCourseId(courseName);
        if (courseId == -1) {
            return;
        }

        String insertTimeUpdate = "INSERT INTO time (courseId, player, time, deaths) "
                + "VALUES (" + courseId + ", '" + playerName + "', " + time + ", " + deaths + ");";
        Utils.debug("Inserting time: " + insertTimeUpdate);

        try {
            database.updateAsync(insertTimeUpdate).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert or Update player's time on course.
     * A time will be inserted or updated depending on a config option.
     * Updating will only apply once the player has beaten their best time.
     *
     * @param courseName
     * @param playerName
     * @param time
     * @param deaths
     */
    public void insertOrUpdateTime(String courseName, String playerName, long time, int deaths, boolean isNewRecord) {
        boolean updatePlayerTime = parkour.getConfig().getBoolean("OnFinish.UpdatePlayerDatabaseTime");
        Utils.debug("Potentially Inserting or Updating Time for player: " + playerName + ", isNewRecord: " + isNewRecord + ", updatePlayerTime: " + updatePlayerTime);

        if (isNewRecord && updatePlayerTime) {
            Utils.debug("Updating the Time for player " + playerName);
            deletePlayerCourseTimes(playerName, courseName);
            insertTime(courseName, playerName, time, deaths);

        } else if (!updatePlayerTime) {
            Utils.debug("Inserting a Time for player " + playerName);
            insertTime(courseName, playerName, time, deaths);
        }
    }

    /**
     * Delete all Players leaderboard times.
     * For usage if a player has been banned for cheating etc.
     *
     * @param playerName
     */
    public void deletePlayerTimes(String playerName) {
        Utils.debug("Deleting all Player times for " + playerName);
        try {
            database.updateAsync("DELETE FROM time WHERE player='" + playerName + "'").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete all the times for the course.
     *
     * @param courseName
     */
    public void deleteCourseTimes(String courseName) {
        int courseId = getCourseId(courseName);
        if (courseId == -1) {
            return;
        }

        Utils.debug("Deleting all Course times for " + courseName);
        try {
            database.updateAsync("DELETE FROM time WHERE courseId=" + courseId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete player times from a certain course.
     *
     * @param playerName
     * @param courseName
     */
    public void deletePlayerCourseTimes(String playerName, String courseName) {
        int courseId = getCourseId(courseName);
        if (courseId == -1) {
            return;
        }

        Utils.debug("Deleting all times for player " + playerName + " for course " + courseName);
        try {
            database.updateAsync("DELETE FROM time"
                    + " WHERE player='" + playerName + "' AND courseId=" + courseId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete all references to the Course from the database.
     * For usage if a course has been deleted.
     * Will remove the times and the course entry from the database.
     *
     * @param courseName
     */
    public void deleteCourseAndReferences(String courseName) {
        Utils.debug("Completely deleting course " + courseName);
        try {
            database.updateAsync("DELETE FROM course WHERE name='" + courseName + "'").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        // reset the cache
        courseIdCache.clear();
    }

    /**
     * Attempt to reinsert all the parkour courses into the database.
     * This is required when the database becomes out of sync through manual editing.
     * Times cannot be stored until the course exists in the database.
     */
    public void recreateAllCourses() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Parkour.getInstance(), () -> {
            Utils.log("Starting recreation of courses process...");
            int changes = 0;
            for (String courseName : CourseInfo.getAllCourses()) {
                if (getCourseId(courseName, false) == -1) {
                    insertCourse(courseName, CourseInfo.getCreator(courseName));
                    changes++;
                }
            }
            Utils.log("Process complete. Courses recreated: " + changes);
            if (changes > 0) {
                Utils.logToFile("Courses recreated: " + changes);
            }
        }, 1);
    }

    /**
     * Request to close the database connection.
     * Performed on server shutdown.
     */
    public void closeConnection() {
        Utils.debug("Closing the SQL connection.");
        try {
            this.database.closeConnection();
        } catch (SQLException e) {
            logSQLException(e);
        }
    }

    /**
     * Initialise connection to the configured Database source.
     * SQLite will be the default (and fallback) unless MySQL is correctly configured.
     */
    private void initiateConnection() {
        Utils.debug("Initialising SQL Connection.");
        if (parkour.getConfig().getBoolean("MySQL.Use")
                && !parkour.getConfig().getString("MySQL.Host").equals("Host")) {
            Utils.debug("Opting to use MySQL.");

            this.database = new MySQL(
                    parkour.getConfig().getString("MySQL.Host"),
                    parkour.getConfig().getString("MySQL.Port"),
                    parkour.getConfig().getString("MySQL.Database"),
                    parkour.getConfig().getString("MySQL.User"),
                    parkour.getConfig().getString("MySQL.Password"),
                    parkour.getConfig().getString("MySQL.Params"));
        } else {
            Utils.debug("Opting to use SQLite.");
            String pathOverride = Parkour.getInstance().getConfig().getString("SQLite.PathOverride", "");
            String path = pathOverride.isEmpty() ? parkour.getDataFolder() + "/sqlite-db/" : pathOverride;

            this.database = new SQLite(path, "parkour.db");
        }

        try {
            // attempt to create the required SQL tables
            // this will be the first time a connection is opened
            // if the attempt fails, it will fallback to SQLite by disabling MySQL (if enabled)
            setupTables();
        } catch (SQLException ex) {
            logSQLConnectionException(ex);
        }
    }

    private void setupTables() throws SQLException {
        String createCourseTable = "CREATE TABLE IF NOT EXISTS course (courseId INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(15) NOT NULL UNIQUE, author VARCHAR(20) NOT NULL, created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);";
        String createTimesTable = "CREATE TABLE IF NOT EXISTS time (timeId INTEGER PRIMARY KEY AUTO_INCREMENT, courseId INTEGER NOT NULL, player VARCHAR(20) NOT NULL, time DECIMAL(13,0) NOT NULL, deaths INT(5) NOT NULL, FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE);";

        // seems to be the only syntactic difference between them
        if (database instanceof SQLite) {
            createCourseTable = createCourseTable.replace("AUTO_INCREMENT", "AUTOINCREMENT");
            createTimesTable = createTimesTable.replace("AUTO_INCREMENT", "AUTOINCREMENT");
        }

        Utils.debug("Attempting to create necessary tables.");
        database.update(createCourseTable);
        database.update(createTimesTable);
        database.closeConnection();
        Utils.debug("Successfully created necessary tables.");
    }

    private void logSQLConnectionException(SQLException e) {
        Utils.log("[SQL] Connection problem: " + e.getMessage(), 2);
        e.printStackTrace();

        // if they were trying to use MySQL
        if (parkour.getConfig().getBoolean("MySQL.Use")) {
            Parkour.getInstance().getConfig().set("MySQL.Use", false);
            Parkour.getInstance().saveConfig();

            Utils.log("[SQL] Defaulting to SQLite...", 1);
            initiateConnection();
        } else {
            Utils.log("[SQL] Failed to connect to SQLite.", 2);
        }
    }

    private void logSQLException(SQLException e) {
        Utils.log("[SQL] Error occurred: " + e.getMessage(), 2);
        e.printStackTrace();
    }

    /**
     * Processes a ResultSet and returns a list of TimeObjects.
     *
     * @param rs ResultSet
     * @return time object results
     * @throws SQLException
     */
    private List<TimeObject> processTimes(ResultSet rs) throws SQLException {
        List<TimeObject> times = new ArrayList<>();

        while (rs.next()) {
            TimeObject time = new TimeObject(
                    rs.getString("player"),
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
     * @param limit
     * @return amount of results
     */
    private int calculateResultsLimit(int limit) {
        return Math.max(1, Math.min(limit, Parkour.getSettings().getLeaderboardMaxEntries()));
    }
}

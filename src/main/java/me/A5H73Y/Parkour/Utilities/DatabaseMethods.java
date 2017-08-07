package me.A5H73Y.Parkour.Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Enums.DatabaseType;
import me.A5H73Y.Parkour.Other.TimeObject;

import org.bukkit.Bukkit;

import com.huskehhh.mysql.Database;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class DatabaseMethods extends Database {

    public static DatabaseType type;

    public static void setupTables() {
        try {
            if (type.equals(DatabaseType.SQLite)) {
                String tableScript =
                        "CREATE TABLE IF NOT EXISTS course (courseId INTEGER PRIMARY KEY, name VARCHAR(15) NOT NULL UNIQUE, author VARCHAR(20) NOT NULL, created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL); " +

                                "CREATE TABLE IF NOT EXISTS time (timeId INTEGER PRIMARY KEY, courseId INTEGER NOT NULL, player VARCHAR(20) NOT NULL, time DECIMAL(13,0) NOT NULL, deaths INT(5) NOT NULL, FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE); " +

                                "CREATE TABLE IF NOT EXISTS vote (courseId INTEGER NOT NULL, player VARCHAR(20) NOT NULL, liked BIT NOT NULL, PRIMARY KEY (courseId, player), FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE); ";

                Parkour.getDatabaseObj().updateSQL(tableScript);

            } else if (type.equals(DatabaseType.MySQL)) {
                String tableScript = "CREATE TABLE IF NOT EXISTS course (courseId INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(15) NOT NULL UNIQUE, author VARCHAR(20) NOT NULL, created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL); ";
                Parkour.getDatabaseObj().updateSQL(tableScript);
                tableScript = "CREATE TABLE IF NOT EXISTS time (timeId INTEGER PRIMARY KEY AUTO_INCREMENT, courseId INTEGER NOT NULL, player VARCHAR(20) NOT NULL, time DECIMAL(13,0) NOT NULL, deaths INT(5) NOT NULL, FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE); ";
                Parkour.getDatabaseObj().updateSQL(tableScript);
                tableScript = "CREATE TABLE IF NOT EXISTS vote (courseId INTEGER NOT NULL, player VARCHAR(20) NOT NULL, liked BIT NOT NULL, PRIMARY KEY (courseId, player), FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE); ";
                Parkour.getDatabaseObj().updateSQL(tableScript);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
    }

    @Override
    public Connection openConnection() {
        return null;
    }

    /**
     * Return the course's unique ID based on its name in the database.
     * @param courseName
     * @return
     * @throws SQLException
     */
    public static int getCourseId(String courseName) {
        int courseId = 0;

        try{
            PreparedStatement ps = Parkour.getDatabaseObj().openConnection().prepareStatement("SELECT courseId FROM course WHERE name = ?;");
            ps.setString(1, courseName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                courseId =  rs.getInt("courseId");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }

        if (courseId == 0)
            Utils.log("Course '" + courseName + "' was not found in the database. Run command '/pa recreate' to fix.", 1);

        return courseId;
    }

    /**
     * Processes a ResultSet and returns a list of TimeObjects
     * @param rs ResultSet
     * @return
     * @throws SQLException
     */
    private static List<TimeObject> processTimes(ResultSet rs) throws SQLException{
        List<TimeObject> times = new ArrayList<>();

        while (rs.next()){
            TimeObject time = new TimeObject(
                    rs.getString("player"),
                    rs.getLong("time"),
                    rs.getInt("deaths"));
            times.add(time);
        }
        return times;
    }

    /**
     * Once a course has been created, a record will be entered into the database, giving it a unique numeric identifier.
     * This identifier is then used to reference the course throughout the database.
     * @param courseName
     * @param playerName
     */
    public static void insertCourse(String courseName, String playerName) {
        try {
            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("INSERT INTO `course` (`name`, `author`) VALUES (?, ?);");
            ps.setString(1, courseName);
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
    }

    /**
     * Insert a time into the database for the players course Progress.
     * There are no unique constraints on the times table, so the user is able to have many times for many courses
     * @param courseName
     * @param playerName
     * @param time
     * @param deaths
     */
    public static void insertTime(String courseName, String playerName, long time, int deaths){
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return;

            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("INSERT INTO `time` (`courseId`, `player`, `time`, `deaths`) VALUES (?, ?, ?, ?);");
            ps.setInt(1, courseId);
            ps.setString(2, playerName);
            ps.setLong(3, time);
            ps.setInt(4, deaths);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
    }

    public static void updateTime(String courseName, String playerName, long time, int deaths){
        List<TimeObject> results = getTopPlayerCourseResults(playerName, courseName, 1);

        if (results == null || results.isEmpty()) {
            insertTime(courseName, playerName, time, deaths);
            return;
        }

        TimeObject result = results.get(0);

        if (result.getTime() <= time)
            return;

        deletePlayerCourseTimes(playerName, courseName);
        insertTime(courseName, playerName, time, deaths);
    }

    /**
     * When a player votes whether or not a player likes course.
     * @param courseName
     * @param playerName
     * @param like
     */
    public static void insertVote(String courseName, String playerName, Boolean like){
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return;

            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("INSERT INTO `vote` (courseId, player, liked) VALUES (?, ?, ?);");
            ps.setInt(1, courseId);
            ps.setString(2, playerName);
            ps.setBoolean(3, like);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
    }

    /**
     * Calculate the percentage of how many players liked the course.
     * @param courseName
     * @return
     */
    public static double getVotePercent(String courseName){
        double percentage = 0;
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return 0;

            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("SELECT count(*) AS votes, (SELECT count(*) FROM vote WHERE liked = 1 AND courseId=?) AS likes FROM vote WHERE courseId=?;");
            ps.setInt(1, courseId);
            ps.setInt(2, courseId);

            final ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("votes");
                int likes = rs.getInt("likes");

                if (total > 0) {
                    percentage = ((likes * 1.0 / total) * 100);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
        return percentage;
    }

    public static boolean hasVoted(String courseName, String playerName){
        boolean voted = true;
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return true;

            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("SELECT 1 FROM vote WHERE courseId=? AND player=? LIMIT 1;");
            ps.setInt(1, courseId);
            ps.setString(2, playerName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()){
                voted = false;
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
        return voted;
    }

    /**
     * For usage if a player has been banned for cheating etc.
     * May add an option to hook into deleting a player to automatically remove all their times (if configured)
     * @param playerName
     */
    public static void deleteAllTimesForPlayer(String playerName){
        try {
            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("DELETE FROM `time` WHERE `player`=?;");
            ps.setString(1, playerName);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
    }

    /**
     * For usage if a course has been deleted.
     * Remove the times, votes and actual course from the database.
     * @param courseName
     */
    public static void deleteCourseAndReferences(String courseName){
        try {
            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("DELETE FROM `course` WHERE `name`=?;");
            ps.setString(1, courseName);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
    }

    /**
     * Delete all the times for the course
     * @param courseName
     */
    public static void deleteCourseTimes(String courseName){
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return;

            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("DELETE FROM `time` WHERE `courseId`=?;");
            ps.setInt(1, courseId);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
    }

    public static void deletePlayerCourseTimes(String playerName, String courseName) {
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return;

            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("DELETE FROM `time` WHERE `player`=? AND `courseId`=?;");

            ps.setString(1, playerName);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
    }

    public static List<TimeObject> getTopCourseResults(String courseName){
        return getTopCourseResults(courseName, 5);
    }

    public static List<TimeObject> getTopCourseResults(String courseName, int limit){
        limit = limit < 1 ? 1 : limit > 20 ? 20 : limit;

        List<TimeObject> times = new ArrayList<>();
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return times;

            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("SELECT player, time, deaths FROM time WHERE courseId=? ORDER BY time LIMIT ?;");
            ps.setInt(1, courseId);
            ps.setInt(2, limit);

            times = processTimes(ps.executeQuery());

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
        return times;
    }

    public static List<TimeObject> getTopPlayerCourseResults(String playerName, String courseName){
        return getTopPlayerCourseResults(playerName, courseName, 5);
    }

    public static List<TimeObject> getTopPlayerCourseResults(String playerName, String courseName, int limit){
        limit = limit < 1 ? 1 : limit > 20 ? 20 : limit;

        List<TimeObject> times = new ArrayList<>();
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return times;

            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("SELECT player, time, deaths FROM time WHERE courseId=? AND player=? ORDER BY time LIMIT ?;");
            ps.setInt(1, courseId);
            ps.setString(2, playerName);
            ps.setInt(3, limit);

            times = processTimes(ps.executeQuery());

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
        return times;
    }

    public static boolean hasPlayerCompleted(String playerName, String courseName) {
        boolean completed = true;
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return true;

            PreparedStatement ps = Parkour.getDatabaseObj().openConnection()
                    .prepareStatement("SELECT 1 FROM time WHERE courseId=? AND player=? LIMIT 1;");
            ps.setInt(1, courseId);
            ps.setString(2, playerName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()){
                completed = false;
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Parkour.getDatabaseObj().closeConnection();
        }
        return completed;
    }

    public static void recreateAllCourses() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Parkour.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Utils.logToFile("Started courses recreation.");
                Utils.log("Starting recreation of courses process...");
                int changes = 0;
                for (String courseName : Static.getCourses()){
                    if (getCourseId(courseName) == 0){
                        insertCourse(courseName, Parkour.getParkourConfig().getCourseData().getString(courseName + ".Creator"));
                        changes++;
                    }
                }
                Utils.log("Process complete. Courses recreated: " + changes);
            }
        }, 1);
    }
}

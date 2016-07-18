package me.A5H73Y.Parkour.Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.A5H73Y.Parkour.Parkour;

import com.huskehhh.mysql.Database;
import com.huskehhh.mysql.TimeObject;

public class DatabaseMethods extends Database{

	public static void setupTables() {
		try {
			String tableScript =
					"CREATE TABLE IF NOT EXISTS course ( " +

							"id		INTEGER	NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
							"name 	VARCHAR(15) NOT NULL UNIQUE, " +
							"author 	VARCHAR(20) NOT NULL, " +
							"created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL " +
							"); " +


					"CREATE TABLE IF NOT EXISTS time ( " +

							"id 			INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
							"courseId 	INTEGER NOT NULL, " +
							"player	 	VARCHAR(20) NOT NULL, " +
							"time		DECIMAL(13,0) NOT NULL, " +
							"deaths		INT(5) NOT NULL, " +

							"FOREIGN KEY (courseId)  " +
							"REFERENCES course(id) " +
							"ON DELETE CASCADE ON UPDATE CASCADE  " +
							"); " +

					"CREATE TABLE IF NOT EXISTS vote ( " +

							"courseId 	INTEGER NOT NULL, " +
							"player	 	VARCHAR(20) NOT NULL, " +
							"liked		BIT NOT NULL, " +

							"PRIMARY KEY (courseId, player), " +

							"FOREIGN KEY (courseId) " +
							"REFERENCES course(id) " +
							"ON DELETE CASCADE ON UPDATE CASCADE " +
							"); " +

					"CREATE TABLE IF NOT EXISTS trail ( " +

							"trailtype	 VARCHAR(20) PRIMARY KEY NOT NULL, " +
							"parkourLevel INT(5), " +
							"cost	 DECIMAL(8,2) " +
							");";

			//Only syntax difference between MySQL and SQLite
			if (Parkour.getDatabaseObj().getType().equals("SQLite"))
				tableScript = tableScript.replace("AUTO_INCREMENT", "AUTOINCREMENT");

			Parkour.getDatabaseObj().updateSQL(tableScript);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection openConnection() throws SQLException, ClassNotFoundException {
		return null;
	}

	@Override
	public String getType() {
		return Parkour.getDatabaseObj().getType();
	}

	/**
	 * Return the course's unique ID based on its name in the database.
	 * @param courseName
	 * @return
	 * @throws SQLException
	 */
	public static int getCourseId(String courseName) throws SQLException {
		PreparedStatement ps = Parkour.getDatabaseObj().getConnection().prepareStatement("SELECT id FROM course WHERE name = ?;");
		ps.setString(1, courseName);
		
		ResultSet rs = ps.executeQuery();
		return rs != null ? rs.getInt("id") : 0;
	}

	/**
	 * Processes a ResultSet and returns a list of TimeObjects 
	 * @param ResultSet
	 * @return
	 * @throws SQLException
	 */
	public static List<TimeObject> processTimes(ResultSet rs) throws SQLException{
		List<TimeObject> times = new ArrayList<TimeObject>();

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
			PreparedStatement ps = Parkour.getDatabaseObj().getConnection()
					.prepareStatement("INSERT INTO `course` (`name`, `author`) VALUES (?, ?);");
			ps.setString(1, courseName);
			ps.setString(2, playerName);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
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

			PreparedStatement ps = Parkour.getDatabaseObj().getConnection()
					.prepareStatement("INSERT INTO `time` (`courseId`, `player`, `time`, `deaths`) VALUES (?, ?, ?, ?);");
			ps.setInt(1, courseId);
			ps.setString(2, playerName);
			ps.setLong(3, time);
			ps.setInt(4, deaths);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

			PreparedStatement ps = Parkour.getDatabaseObj().getConnection()
					.prepareStatement("INSERT INTO `vote` (courseId, player, liked) VALUES (?, ?, ?);");
			ps.setInt(1, courseId);
			ps.setString(2, playerName);
			ps.setBoolean(3, like);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
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

			PreparedStatement ps = Parkour.getDatabaseObj().getConnection()
					.prepareStatement("SELECT count(*) AS votes, (SELECT count(*) FROM vote WHERE liked = 1 AND courseId=?) AS likes FROM vote WHERE courseId=?;");
			ps.setInt(1, courseId);
			ps.setInt(2, courseId);
			ResultSet rs = ps.getResultSet();

			int total = rs.getInt("votes");
			int likes = rs.getInt("likes");

			percentage = (likes / total) * 100;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return percentage;
	}

	/**
	 * For usage if a player has been banned for cheating etc.
	 * May add an option to hook into deleting a player to automatically remove all their times (if configured)
	 * @param playerName
	 */
	public static void deleteAllTimesForPlayer(String playerName){
		try {
			PreparedStatement ps = Parkour.getDatabaseObj().getConnection()
					.prepareStatement("DELETE FROM `time` WHERE `player`=?;");
			ps.setString(1, playerName);
			ps.executeUpdate();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	/**
	 * For usage if a course has been deleted.
	 * Remove the times, votes and actual course from the database.
	 * @param courseName
	 */
	public static void deleteCourseAndReferences(String courseName){
		try {
			PreparedStatement ps = Parkour.getDatabaseObj().getConnection()
					.prepareStatement("DELETE FROM `course` WHERE `name`=?;");
			ps.setString(1, courseName);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
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

			PreparedStatement ps = Parkour.getDatabaseObj().getConnection()
					.prepareStatement("DELETE FROM `time` WHERE `courseId`=?;");
			ps.setInt(1, courseId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<TimeObject> getTopCourseResults(String courseName){
		return getTopCourseResults(courseName, 5);
	}

	public static List<TimeObject> getTopCourseResults(String courseName, int limit){
		List<TimeObject> times = new ArrayList<TimeObject>();
		try {
			int courseId = getCourseId(courseName);
			if (courseId == 0)
				return times;

			PreparedStatement ps = Parkour.getDatabaseObj().getConnection()
					.prepareStatement("SELECT player, time, deaths FROM time WHERE courseId=? LIMIT ?;");
			ps.setInt(1, courseId);
			ps.setInt(2, limit);

			times = processTimes(ps.executeQuery());

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return times;
	}

	public static List<TimeObject> getTopPlayerCourseResults(String playerName, String courseName){
		return getTopPlayerCourseResults(playerName, courseName, 5);
	}

	public static List<TimeObject> getTopPlayerCourseResults(String playerName, String courseName, int limit){
		List<TimeObject> times = new ArrayList<TimeObject>();
		try {
			int courseId = getCourseId(courseName);
			if (courseId == 0)
				return times;

			PreparedStatement ps = Parkour.getDatabaseObj().getConnection()
					.prepareStatement("SELECT player, time, deaths FROM time WHERE courseId=? AND player=? LIMIT ?;");
			ps.setInt(1, courseId);
			ps.setString(2, playerName);
			ps.setInt(3, limit);

			times = processTimes(ps.executeQuery());

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return times;
	}
}

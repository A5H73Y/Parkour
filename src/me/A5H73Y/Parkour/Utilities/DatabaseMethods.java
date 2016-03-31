package me.A5H73Y.Parkour.Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import me.A5H73Y.Parkour.Parkour;

import com.huskehhh.mysql.*;

public class DatabaseMethods extends Database{

	@Override
	public void setupTables() {
	}

	@Override
	public Connection openConnection() throws SQLException, ClassNotFoundException {
		return null;
	}
	
	public static int returnPosition(long id){
		return 0;
	}

	public static int insertTime(String courseName, String playerName, long time, int deaths){
		try {
			PreparedStatement ps = Parkour.getDatabaseObj().getConnection()
					.prepareStatement("INSERT INTO `times` (`course`, `player`, `time`, `deaths`) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, courseName);
			ps.setString(2, playerName);
			ps.setLong(3, time);
			ps.setInt(4, deaths);
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			long id = rs.getLong(1);
			return returnPosition(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static void deleteAllTimesForPlayer(String playerName){
		try {
			PreparedStatement ps = Parkour.getDatabaseObj().getConnection()
					.prepareStatement("DELETE FROM `times` WHERE `player`=?;");
			ps.setString(1, playerName);
			ps.executeUpdate();
		} catch (SQLException e){
			e.printStackTrace();
		}

	}

	public static void deleteAllTimesForCourse(String courseName){
		try {
			Parkour.getDatabaseObj().updateSQL("DELETE FROM times WHERE course='" + courseName + "';");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<TimeObject> getTopCourseResults(String courseName){
		return getTopCourseResults(courseName, 5);
	}

	public List<TimeObject> getTopCourseResults(String courseName, int amount){
		List<TimeObject> times = new ArrayList<TimeObject>();
		try {
			ResultSet rs = querySQL("SELECT course, player, time, deaths FROM times WHERE course='" + courseName + "' LIMIT " + amount + ";");
			while (rs.next()){
				TimeObject time = new TimeObject(
						rs.getString("course"),
						rs.getString("player"),
						rs.getLong("time"),
						rs.getInt("deaths"));
				times.add(time);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return times;
	}

	public List<TimeObject> getTopPlayerCourseResults(String playerName, String courseName){
		return getTopPlayerCourseResults(playerName, courseName, 5);
	}

	public List<TimeObject> getTopPlayerCourseResults(String playerName, String courseName, int amount){
		List<TimeObject> times = new ArrayList<TimeObject>();
		try {
			ResultSet rs = querySQL("SELECT course, player, time, deaths FROM times WHERE course='"+courseName+"' AND player='"+playerName+"' LIMIT "+amount+";");
			while (rs.next()){
				TimeObject time = new TimeObject(
						rs.getString("course"),
						rs.getString("player"),
						rs.getLong("time"),
						rs.getInt("deaths"));
				times.add(time);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return times;
	}

	@Override
	public String getType() {
		return Parkour.getDatabaseObj().getType();
	}


}

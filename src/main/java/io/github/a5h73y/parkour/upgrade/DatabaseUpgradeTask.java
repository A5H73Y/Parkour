package io.github.a5h73y.parkour.upgrade;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.database.ParkourDatabase;
import io.github.a5h73y.parkour.database.SQLite;
import io.github.a5h73y.parkour.database.TimeEntry;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import pro.husk.Database;
import pro.husk.mysql.MySQL;

public class DatabaseUpgradeTask extends TimedUpgradeTask {

	public DatabaseUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader);
	}

	private final Map<Integer, String> courseIdToName = new HashMap<>();
	private final Map<String, List<TimeEntry>> playerNameToTimes = new HashMap<>();

	@Override
	protected String getTitle() {
		return "Database";
	}

	@Override
	protected boolean doWork() {
		boolean success = true;
		Database database = openExistingConnection();

		// check we have tables / rows to begin with

		String query;

		if (database instanceof MySQL) {
			query = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = N'time';";
		} else {
			query = "SELECT * FROM sqlite_master WHERE type='table' AND name='time';";
		}

		try {
			ResultSet rs = database.query(query);
			if (rs.next()) {
				getParkourUpgrader().getLogger().info("Tables found, starting upgrade...");

				ResultSet courses = database.query("SELECT * FROM course ORDER BY courseId");
				while (courses.next()) {
					courseIdToName.put(courses.getInt("courseId"), courses.getString("name"));
				}

				getParkourUpgrader().getLogger().info("Found " + courseIdToName.size() + " courses...");

				ResultSet playerTimes = database.query("SELECT * FROM time ORDER BY player");
				while (playerTimes.next()) {
					String playerName = playerTimes.getString("player");
					List<TimeEntry> results = playerNameToTimes.getOrDefault(
							playerTimes.getString("player"), new ArrayList<>());

					results.add(new TimeEntry(
							playerTimes.getString("courseId"),
							null,
							playerTimes.getString("player"),
							playerTimes.getLong("time"),
							playerTimes.getInt("deaths")));
					playerNameToTimes.put(playerName, results);
				}

				getParkourUpgrader().getLogger().info("Found " + playerNameToTimes.size() + " player times...");
				// create an exact copy, in case something goes bang
				if (database instanceof MySQL) {
					database.update("SELECT * INTO course_backup FROM course;");
					database.update("SELECT * INTO time_backup FROM time;");
				} else {
					database.update("CREATE TABLE course_backup AS SELECT * FROM course;");
					database.update("CREATE TABLE time_backup AS SELECT * FROM time;");
				}
				getParkourUpgrader().getLogger().info("Created backup tables...");
				database.closeConnection();

				database = openExistingConnection();
				// drop the original tables
				database.update("DROP TABLE time;");
				database.update("DROP TABLE course;");
				database.update("DROP TABLE vote;"); // TODO if exists
				getParkourUpgrader().getLogger().info("Dropped old tables...");

				// close this temporary connection
				database.closeConnection();
			}
		} catch (SQLException e) {
			getParkourUpgrader().getLogger().severe("SQL Connection Error: " + e.getMessage());
			e.printStackTrace();
			success = false;
		}

		return success;
	}

	protected boolean doMoreWork() {
		boolean success = true;
		// create a proper Parkour connection
		// which also setups up the new tables
		Database pDatabase = Parkour.getInstance().getDatabase().getDatabase();

		// TODO check new tables exist
		getParkourUpgrader().getLogger().info("Transferring data to new tables.");
		getParkourUpgrader().getLogger().info("Re-inserting course data...");

		try {
			for (Map.Entry<Integer, String> result : courseIdToName.entrySet()) {
				pDatabase.update("INSERT INTO course (courseId, name) VALUES "
						+ "('" + result.getKey() + "', '" + result.getValue() + "');");
			}

			getParkourUpgrader().getLogger().info("Re-inserting time data, this might take a while...");

			for (Map.Entry<String, List<TimeEntry>> playerEntry : playerNameToTimes.entrySet()) {
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerEntry.getKey());
				String playerId = ParkourDatabase.getPlayerId(offlinePlayer);

				for (TimeEntry timeEntry : playerEntry.getValue()) {
					pDatabase.update("INSERT INTO time (courseId, playerId, playerName, time, deaths) VALUES "
							+ "(" + timeEntry.getCourseId() + ", '" + playerId + "', '" + playerEntry.getKey() + "', " + timeEntry.getTime() + ", " + timeEntry.getDeaths() + ");");
				}
			}

			getParkourUpgrader().getLogger().info("Done adding players");
			getParkourUpgrader().getLogger().info("Cleaning up temporary tables");

			pDatabase.update("DROP TABLE course_backup;");
			pDatabase.update("DROP TABLE time_backup;");

		} catch (SQLException e) {
			success = false;
		}

		return success;
	}

	private Database openExistingConnection() {
		Database database;
		FileConfiguration defaultConfig = getParkourUpgrader().getDefaultConfig();

		if (defaultConfig.getBoolean("MySQL.Use")) {
			String connectionURL = "jdbc:mysql://" + defaultConfig.getString("MySQL.Host") + ":" + defaultConfig.getString("MySQL.Port") + "/" + defaultConfig.getString("MySQL.Database");
			database = new MySQL(connectionURL, defaultConfig.getString("MySQL.User"), defaultConfig.getString("MySQL.Password"));

		} else {
			String pathOverride = defaultConfig.getString("SQLite.PathOverride", "");
			String path = pathOverride.isEmpty()
					? Parkour.getInstance().getDataFolder() + File.separator + "sqlite-db" + File.separator : pathOverride;

			database = new SQLite(path, "parkour.db");
		}
		return database;
	}
}

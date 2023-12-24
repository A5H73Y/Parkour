package io.github.a5h73y.parkour.upgrade.major;

import static io.github.a5h73y.parkour.utility.PluginUtils.readContentsOfResource;

import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedUpgradeTask;
import io.github.a5h73y.parkour.utility.PluginUtils;
import java.io.IOException;
import java.sql.SQLException;
import pro.husk.Database;
import pro.husk.mysql.MySQL;

public class DatabaseUpgradeTask extends TimedUpgradeTask {

	public DatabaseUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader);
	}

	@Override
	protected String getTitle() {
		return "Database";
	}

	@Override
	protected boolean doWork() {
		Database database = getParkourUpgrader().getDatabase();
		String sqlResourcePrefix = "sql/" + (database instanceof MySQL ? "mysql" : "sqlite") + "/";

		try {
			getParkourUpgrader().getLogger().info("Creating backup table...");
			database.update("CREATE TABLE time_backup AS SELECT * FROM time;");
			database.update("DROP TABLE time;");

			getParkourUpgrader().getLogger().info("Creating new table...");
			database.update(readContentsOfResource(sqlResourcePrefix + "time.sql"));

			getParkourUpgrader().getLogger().info("Transferring times back...");
			database.update("INSERT INTO time (courseId, playerId, time, deaths) "
					+ "SELECT courseId, playerId, time, deaths FROM time_backup;");
			database.update("DROP TABLE time_backup;");
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}

		return true;
	}
}

package io.github.a5h73y.parkour.upgrade.major;

import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedUpgradeTask;

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
//		try {
//			getParkourUpgrader().getDatabase().update("ALTER TABLE time DROP COLUMN playerName;");
//			getParkourUpgrader().getDatabase().update("ALTER TABLE time ADD COLUMN achieved TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;");

			// sqlite doesn't let you add a new column containing a timestamp default
			// only alternative is to drop the table and recreate it - bloody nightmare.
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

		return true;
	}
}

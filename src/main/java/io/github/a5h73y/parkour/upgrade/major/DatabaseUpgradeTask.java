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

		// Database - drop playerName column, add new achieved column

		return true;
	}
}

package io.github.a5h73y.parkour.upgrade;

import io.github.a5h73y.parkour.Parkour;

public abstract class TimedUpgradeTask {

	private final ParkourUpgrader parkourUpgrader;

	protected abstract String getTitle();

	protected abstract boolean doWork();

	protected TimedUpgradeTask(ParkourUpgrader parkourUpgrader) {
		this.parkourUpgrader = parkourUpgrader;
	}

	/**
	 * Start the Upgrade Task.
	 * The time difference between start and end will be output on completion.
	 *
	 * @return upgrade success
	 */
	public boolean start() {
		Parkour.getInstance().getLogger().info(this.getTitle() + " Upgrade started...");
		long startTime = System.currentTimeMillis();

		boolean success = doWork();

		Parkour.getInstance().getLogger().info(this.getTitle() + " Upgrade complete. Time taken: "
				+ (System.currentTimeMillis() - startTime) + "ms");
		return success;
	}

	public ParkourUpgrader getParkourUpgrader() {
		return parkourUpgrader;
	}
}

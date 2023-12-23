package io.github.a5h73y.parkour.upgrade.minor;

import io.github.a5h73y.parkour.configuration.impl.DefaultConfig;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;

public class ConfigMinorUpgradeTask extends TimedConfigUpgradeTask<DefaultConfig> {

	public ConfigMinorUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader, parkourUpgrader.getNewConfigManager().getDefaultConfig());
	}

	@Override
	protected String getTitle() {
		return "config.yml";
	}

	@Override
	protected boolean doWork() {
		updateConfigEntry("ParkourModes.Rockets.SecondCooldown", "ParkourTool.Rockets.SecondCooldown");
		updateConfigEntry("OnCourse.UseParkourKit", "ParkourKit.Enabled");
		updateConfigEntry("OnCourse.ParkourKit.Enabled", "ParkourKit.Enabled");
		updateConfigEntry("OnCourse.ParkourKit.IncludeVehicles", "ParkourKit.IncludeVehicles");
		updateConfigEntry("OnCourse.ParkourKit.FloatingClosestBlock", "ParkourKit.FloatingClosestBlock");
		this.config.forceReload();
		return true;
	}
}

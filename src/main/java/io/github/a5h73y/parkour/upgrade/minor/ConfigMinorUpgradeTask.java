package io.github.a5h73y.parkour.upgrade.minor;

import static io.github.a5h73y.parkour.configuration.impl.DefaultConfig.COURSE_DEFAULT_SETTINGS;
import static io.github.a5h73y.parkour.type.course.CourseConfig.DIE_IN_LAVA;
import static io.github.a5h73y.parkour.type.course.CourseConfig.DIE_IN_WATER;

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

		if (config.contains(COURSE_DEFAULT_SETTINGS + "DieInLiquid")) {
			boolean dieInLiquid = config.getBoolean(COURSE_DEFAULT_SETTINGS + "DieInLiquid");
			config.set(COURSE_DEFAULT_SETTINGS + DIE_IN_LAVA, dieInLiquid);
			config.set(COURSE_DEFAULT_SETTINGS + DIE_IN_WATER, dieInLiquid);
			config.remove(COURSE_DEFAULT_SETTINGS + "DieInLiquid");
		}

		config.set("OnLeave.SetGameMode", config.getBoolean("OnFinish.SetGameMode"));
		config.set("OnLeave.TeleportToJoinLocation", config.getBoolean("OnFinish.TeleportToJoinLocation"));

		this.config.forceReload();
		return true;
	}
}

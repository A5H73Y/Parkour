package io.github.a5h73y.parkour.upgrade;

import org.bukkit.configuration.file.FileConfiguration;

public abstract class TimedConfigUpgradeTask extends TimedUpgradeTask {

	private final FileConfiguration configuration;

	protected TimedConfigUpgradeTask(ParkourUpgrader parkourUpgrader, FileConfiguration configuration) {
		super(parkourUpgrader);
		this.configuration = configuration;
	}

	protected void updateConfigEntry(String fromPath, String toPath) {
		configuration.set(toPath, configuration.get(fromPath));
		configuration.set(fromPath, null);
	}

	protected FileConfiguration getConfig() {
		return configuration;
	}
}

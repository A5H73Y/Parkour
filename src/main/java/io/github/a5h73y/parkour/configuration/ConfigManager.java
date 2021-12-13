package io.github.a5h73y.parkour.configuration;

import io.github.a5h73y.parkour.configuration.impl.CheckpointsConfig;
import io.github.a5h73y.parkour.configuration.impl.CoursesConfig;
import io.github.a5h73y.parkour.configuration.impl.DefaultConfig;
import io.github.a5h73y.parkour.configuration.impl.EconomyConfig;
import io.github.a5h73y.parkour.configuration.impl.ParkourKitConfig;
import io.github.a5h73y.parkour.configuration.impl.StringsConfig;
import io.github.a5h73y.parkour.enums.ConfigType;
import java.io.File;
import java.util.EnumMap;

/**
 * Parkour Configuration Manager.
 * Manages and stores references to each of the available Config files.
 */
public class ConfigManager {

	private final File dataFolder;

	private final EnumMap<ConfigType, ParkourConfiguration> parkourConfigs = new EnumMap<>(ConfigType.class);

	/**
	 * Initialise the Config Manager.
	 * Will invoke setup for each available config type.
	 *
	 * @param dataFolder where to store the configs
	 */
	public ConfigManager(File dataFolder) {
		this.dataFolder = dataFolder;
		createParkourFolder();

		parkourConfigs.put(ConfigType.DEFAULT, new DefaultConfig());
		parkourConfigs.put(ConfigType.STRINGS, new StringsConfig());
		parkourConfigs.put(ConfigType.COURSES, new CoursesConfig());
		parkourConfigs.put(ConfigType.CHECKPOINTS, new CheckpointsConfig());
		parkourConfigs.put(ConfigType.PARKOURKIT, new ParkourKitConfig());
		parkourConfigs.put(ConfigType.ECONOMY, new EconomyConfig());

		for (ParkourConfiguration parkourConfig: parkourConfigs.values()) {
			parkourConfig.setupFile(this.dataFolder);
		}
	}

	/**
	 * Get matching ParkourConfiguration for the ConfigType.
	 *
	 * @param type requested config type
	 * @return matching ParkourConfiguration
	 */
	public ParkourConfiguration get(ConfigType type) {
		return parkourConfigs.get(type);
	}

	/**
	 * Reload each of the configuration files.
	 */
	public void reloadConfigs() {
		for (ParkourConfiguration parkourConfig : parkourConfigs.values()) {
			parkourConfig.reload();
		}
	}

	private void createParkourFolder() {
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}
	}
}

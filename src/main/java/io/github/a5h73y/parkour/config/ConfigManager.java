package io.github.a5h73y.parkour.config;

import java.util.EnumMap;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.config.impl.CheckpointsFile;
import io.github.a5h73y.parkour.config.impl.CoursesFile;
import io.github.a5h73y.parkour.config.impl.DefaultFile;
import io.github.a5h73y.parkour.config.impl.EconomyFile;
import io.github.a5h73y.parkour.config.impl.InventoryFile;
import io.github.a5h73y.parkour.config.impl.ParkourKitFile;
import io.github.a5h73y.parkour.config.impl.PlayersFile;
import io.github.a5h73y.parkour.config.impl.StringsFile;
import io.github.a5h73y.parkour.enums.ConfigType;

public class ConfigManager {

	private EnumMap<ConfigType, ParkourConfiguration> parkourConfigs = new EnumMap<>(ConfigType.class);

	public ConfigManager() {
		createParkourFolder();

		parkourConfigs.put(ConfigType.DEFAULT, new DefaultFile());
		parkourConfigs.put(ConfigType.STRINGS, new StringsFile());
		parkourConfigs.put(ConfigType.COURSES, new CoursesFile());
		parkourConfigs.put(ConfigType.CHECKPOINTS, new CheckpointsFile());
		parkourConfigs.put(ConfigType.PLAYERS, new PlayersFile());
		parkourConfigs.put(ConfigType.INVENTORY, new InventoryFile());
		parkourConfigs.put(ConfigType.PARKOURKIT, new ParkourKitFile());
		parkourConfigs.put(ConfigType.ECONOMY, new EconomyFile());

		for (ParkourConfiguration parkourConfig: parkourConfigs.values()) {
			parkourConfig.setupFile();
		}
	}

	private void createParkourFolder() {
		if (!Parkour.getInstance().getDataFolder().exists()) {
			Parkour.getInstance().getDataFolder().mkdirs();
		}
	}

	public ParkourConfiguration get(ConfigType type) {
		return parkourConfigs.get(type);
	}

	public void reloadConfigs() {
		for (ParkourConfiguration parkourConfig: parkourConfigs.values()) {
			parkourConfig.reload();
		}
	}
}

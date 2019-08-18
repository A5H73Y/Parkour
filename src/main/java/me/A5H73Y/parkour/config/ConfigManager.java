package me.A5H73Y.parkour.config;

import java.io.File;
import java.util.EnumMap;

import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.config.impl.CheckpointsFile;
import me.A5H73Y.parkour.config.impl.CoursesFile;
import me.A5H73Y.parkour.config.impl.DefaultFile;
import me.A5H73Y.parkour.config.impl.EconomyFile;
import me.A5H73Y.parkour.config.impl.InventoryFile;
import me.A5H73Y.parkour.config.impl.ParkourKitFile;
import me.A5H73Y.parkour.config.impl.PlayersFile;
import me.A5H73Y.parkour.config.impl.StringsFile;
import me.A5H73Y.parkour.enums.ConfigType;

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

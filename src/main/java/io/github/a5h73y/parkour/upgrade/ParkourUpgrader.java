package io.github.a5h73y.parkour.upgrade;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Backup;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ParkourUpgrader extends AbstractPluginReceiver {

	private final File defaultFile;
	private final File playerFile;
	private final File inventoryFile;
	private final File coursesFile;
	private final File checkpointsFile;
	private final File stringsFile;

	private final FileConfiguration defaultConfig;
	private final FileConfiguration playerConfig;
	private final FileConfiguration inventoryConfig;
	private final FileConfiguration coursesConfig;
	private final FileConfiguration checkpointsConfig;
	private final FileConfiguration stringsConfig;

	public ParkourUpgrader(Parkour parkour) {
		super(parkour);

		defaultFile = new File(parkour.getDataFolder(), "config.yml");
		playerFile = new File(parkour.getDataFolder(), "players.yml");
		inventoryFile = new File(parkour.getDataFolder(), "inventory.yml");
		coursesFile = new File(parkour.getDataFolder(), "courses.yml");
		checkpointsFile = new File(parkour.getDataFolder(), "checkpoints.yml");
		stringsFile = new File(parkour.getDataFolder(), "strings.yml");

		defaultConfig = YamlConfiguration.loadConfiguration(defaultFile);
		playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		inventoryConfig = YamlConfiguration.loadConfiguration(inventoryFile);
		coursesConfig = YamlConfiguration.loadConfiguration(coursesFile);
		checkpointsConfig = YamlConfiguration.loadConfiguration(checkpointsFile);
		stringsConfig = YamlConfiguration.loadConfiguration(stringsFile);
	}

	public void begin() {
		Bukkit.getScheduler().runTaskAsynchronously(parkour, () -> {
			parkour.getLogger().info("=== Beginning Parkour Upgrade ===");
			parkour.getLogger().info(String.format("Upgrading from v%s to v%s",
					defaultConfig.getString("Version"), parkour.getDescription().getVersion()));

			parkour.getLogger().info("Creating backup of current install...");
			Backup.backupNow(true);

			if (!new PlayerInfoUpgradeTask(this).start()) {
				return;
			}

			if (!new CourseInfoUpgradeTask(this).start()) {
				return;
			}

			if (!new StringsConfigUpgradeTask(this).start()) {
				return;
			}

			if (!new DefaultConfigUpgradeTask(this).start()) {
				return;
			}

			// the database upgrade has to be done in two steps
			// transferring the existing times to a temporary table, deleting the tables
			// initialise the actual databases, then transfer the times back into it
			DatabaseUpgradeTask databaseUpgrade = new DatabaseUpgradeTask(this);
			if (!databaseUpgrade.start()) {
				return;
			}

			parkour.getLogger().info("Setting up Configs and Database...");
			parkour.registerEssentialManagers();

			if (!databaseUpgrade.doMoreWork()) {
				return;
			}

			parkour.getLogger().info("Parkour successfully upgraded to " + parkour.getDescription().getVersion());
		});
	}

	public FileConfiguration getDefaultConfig() {
		return defaultConfig;
	}

	public FileConfiguration getPlayerConfig() {
		return playerConfig;
	}

	public FileConfiguration getInventoryConfig() {
		return inventoryConfig;
	}

	public FileConfiguration getCoursesConfig() {
		return coursesConfig;
	}

	public FileConfiguration getCheckpointsConfig() {
		return checkpointsConfig;
	}

	public FileConfiguration getStringsConfig() {
		return stringsConfig;
	}

	public Logger getLogger() {
		return parkour.getLogger();
	}

	public void saveDefaultConfig() throws IOException {
		defaultConfig.save(defaultFile);
	}

	public void savePlayerConfig() throws IOException {
		playerConfig.save(playerFile);
	}

	public void saveInventoryConfig() throws IOException {
		inventoryConfig.save(inventoryFile);
	}

	public void saveCoursesConfig() throws IOException {
		coursesConfig.save(coursesFile);
	}

	public void saveCheckpointsConfig() throws IOException {
		checkpointsConfig.save(checkpointsFile);
	}

	public void saveStringsConfig() throws IOException {
		stringsConfig.save(stringsFile);
	}
}

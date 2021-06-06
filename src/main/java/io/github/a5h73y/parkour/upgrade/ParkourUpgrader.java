package io.github.a5h73y.parkour.upgrade;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Backup;
import io.github.a5h73y.parkour.upgrade.major.CourseInfoUpgradeTask;
import io.github.a5h73y.parkour.upgrade.major.DatabaseUpgradeTask;
import io.github.a5h73y.parkour.upgrade.major.DefaultConfigUpgradeTask;
import io.github.a5h73y.parkour.upgrade.major.PlayerInfoUpgradeTask;
import io.github.a5h73y.parkour.upgrade.major.StringsConfigUpgradeTask;
import io.github.a5h73y.parkour.upgrade.minor.PartialUpgradeTask;
import io.github.g00fy2.versioncompare.Version;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
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

	/**
	 * Initialise the Parkour Upgrader.
	 * @param parkour plugin instance
	 */
	public ParkourUpgrader(final Parkour parkour) {
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

	/**
	 * Begin the Parkour upgrade process.
	 * @return upgrade success
	 */
	public boolean beginUpgrade() {
		parkour.getLogger().info("=== Beginning Parkour Upgrade ===");
		parkour.getLogger().info(String.format("Upgrading from v%s to v%s",
				defaultConfig.getString("Version"), parkour.getDescription().getVersion()));

		parkour.getLogger().info("Creating backup of current install...");
		Backup.backupNow(true);

		boolean success;
		Version existingVersion = new Version(defaultConfig.getString("Version"));

		if (existingVersion.isLowerThan("5.0")) {
			parkour.getLogger().warning("This version is too outdated.");
			parkour.getLogger().warning("Please upgrade to 4.8 and then reinstall this version.");
			success = false;

		} else if (existingVersion.isLowerThan("6.0")) {
			success = performFullUpgrade();

		} else {
			success = performPartialUpgrade();
		}

		if (success) {
			try {
				defaultConfig.set("Version", parkour.getDescription().getVersion());
				saveDefaultConfig();
				parkour.reloadConfig();
				parkour.getLogger().info("Parkour successfully upgraded to v"
						+ parkour.getDescription().getVersion());
				parkour.getLogger().info("The plugin will now start up...");

			} catch (IOException e) {
				parkour.getLogger().severe("An error occurred during upgrade: " + e.getMessage());
				e.printStackTrace();
				success = false;
			}
		}
		return success;
	}

	private boolean performFullUpgrade() {
		if (!new PlayerInfoUpgradeTask(this).start()) {
			return false;
		}

		if (!new CourseInfoUpgradeTask(this).start()) {
			return false;
		}

		if (!new StringsConfigUpgradeTask(this).start()) {
			return false;
		}

		if (!new DefaultConfigUpgradeTask(this).start()) {
			return false;
		}

		// the database upgrade has to be done in two steps
		// transferring the existing times to a temporary table, deleting the tables
		// initialise the actual databases, then transfer the times back into it
		DatabaseUpgradeTask databaseUpgrade = new DatabaseUpgradeTask(this);
		if (!databaseUpgrade.start()) {
			return false;
		}

		parkour.getLogger().info("Setting up Configs and Database...");
		parkour.registerEssentialManagers();
		return databaseUpgrade.doMoreWork();
	}

	private boolean performPartialUpgrade() {
		return new PartialUpgradeTask(this, defaultConfig.getString("Version")).start();
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

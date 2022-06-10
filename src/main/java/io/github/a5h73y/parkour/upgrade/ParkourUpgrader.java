package io.github.a5h73y.parkour.upgrade;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ConfigManager;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.PluginBackupUtil;
import io.github.a5h73y.parkour.upgrade.major.CopyConfigUpgradeTask;
import io.github.a5h73y.parkour.upgrade.major.CourseDataUpgradeTask;
import io.github.a5h73y.parkour.upgrade.major.DatabaseUpgradeTask;
import io.github.a5h73y.parkour.upgrade.major.DefaultConfigUpgradeTask;
import io.github.a5h73y.parkour.upgrade.major.LobbyConfigUpgradeTask;
import io.github.a5h73y.parkour.upgrade.major.PlayerDataUpgradeTask;
import io.github.g00fy2.versioncompare.Version;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pro.husk.Database;

public class ParkourUpgrader extends AbstractPluginReceiver {

	private PlayerDataUpgradeTask playerDataUpgradeTask;

	private final File defaultFile;
	private final File stringsFile;
	private final File playerFile;
	private final File inventoryFile;
	private final File coursesFile;
	private final File checkpointsFile;
	private final File economyFile;
	private final File parkourKitFile;

	private final FileConfiguration defaultConfig;
	private final FileConfiguration stringsConfig;
	private final FileConfiguration playerConfig;
	private final FileConfiguration inventoryConfig;
	private final FileConfiguration coursesConfig;
	private final FileConfiguration checkpointsConfig;
	private final FileConfiguration economyConfig;
	private final FileConfiguration parkourKitConfig;

	/**
	 * Initialise the Parkour Upgrader.
	 *
	 * @param parkour plugin instance
	 */
	public ParkourUpgrader(final Parkour parkour) {
		super(parkour);

		defaultFile = new File(parkour.getDataFolder(), "config.yml");
		stringsFile = new File(parkour.getDataFolder(), "strings.yml");
		playerFile = new File(parkour.getDataFolder(), "players.yml");
		inventoryFile = new File(parkour.getDataFolder(), "inventory.yml");
		coursesFile = new File(parkour.getDataFolder(), "courses.yml");
		checkpointsFile = new File(parkour.getDataFolder(), "checkpoints.yml");
		economyFile = new File(parkour.getDataFolder(), "economy.yml");
		parkourKitFile = new File(parkour.getDataFolder(), "parkourkit.yml");

		defaultConfig = YamlConfiguration.loadConfiguration(defaultFile);
		stringsConfig = YamlConfiguration.loadConfiguration(stringsFile);
		playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		inventoryConfig = YamlConfiguration.loadConfiguration(inventoryFile);
		coursesConfig = YamlConfiguration.loadConfiguration(coursesFile);
		checkpointsConfig = YamlConfiguration.loadConfiguration(checkpointsFile);
		economyConfig = YamlConfiguration.loadConfiguration(economyFile);
		parkourKitConfig = YamlConfiguration.loadConfiguration(parkourKitFile);
	}

	/**
	 * Begin the Parkour upgrade process.
	 *
	 * @return upgrade success
	 */
	public boolean beginUpgrade() {
		parkour.getLogger().info("=== Beginning Parkour Upgrade ===");
		parkour.getLogger().info(String.format("Upgrading from v%s to v%s",
				defaultConfig.getString("Version"), parkour.getDescription().getVersion()));

		parkour.getLogger().info("Creating backup of current install...");
		PluginBackupUtil.backupNow(true);

		boolean success = false;
		Version existingVersion = new Version(defaultConfig.getString("Version"));

		if (existingVersion.isLowerThan("6.0")) {
			parkour.getLogger().warning("This version is too outdated.");
			parkour.getLogger().warning("Please upgrade to 6.7.1 and then reinstall this version.");

		} else if (existingVersion.isLowerThan("7.0.0")) {
			success = performFullUpgrade();
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
		playerDataUpgradeTask = new PlayerDataUpgradeTask(this);

		if (!playerDataUpgradeTask.start()) {
			return false;
		}

		if (!new CourseDataUpgradeTask(this).start()) {
			return false;
		}

		if (!new DatabaseUpgradeTask(this).start()) {
			return false;
		}

		if (!new CopyConfigUpgradeTask(this, "ParkourKit Config",
				getParkourKitConfig(), getNewConfigManager().getParkourKitConfig(), "ParkourKit.").start()) {
			return false;
		}

		if (!new LobbyConfigUpgradeTask(this).start()) {
			return false;
		}

		if (!new DefaultConfigUpgradeTask(this).start()) {
			return false;
		}

		saveStringsConfig();
		getLogger().info("Configuration files updated - removing old files");

		coursesFile.delete();
		checkpointsFile.delete();
		playerFile.delete();
		inventoryFile.delete();
		economyFile.delete();
		parkourKitFile.delete();

		return true;
	}

	/**
	 * Upgrade Player ParkourSessions.
	 * Reason it has to be done after plugin start up so all managers are registered.
	 */
	public void upgradeParkourSessions() {
		if (playerDataUpgradeTask != null) {
			playerDataUpgradeTask.updateParkourSessions();
		}
	}

	public FileConfiguration getDefaultConfig() {
		return defaultConfig;
	}

	public FileConfiguration getStringsConfig() {
		return stringsConfig;
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

	public FileConfiguration getEconomyConfig() {
		return economyConfig;
	}

	public FileConfiguration getParkourKitConfig() {
		return parkourKitConfig;
	}

	public void saveDefaultConfig() throws IOException {
		defaultConfig.save(defaultFile);
	}

	/**
	 * Named thus to avoid using any old references.
	 * @return config manager
	 */
	public ConfigManager getNewConfigManager() {
		return parkour.getConfigManager();
	}

	public Logger getLogger() {
		return parkour.getLogger();
	}

	public Database getDatabase() {
		return parkour.getDatabaseManager().getDatabase();
	}

	private void saveStringsConfig() {
		try {
			stringsConfig.save(stringsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

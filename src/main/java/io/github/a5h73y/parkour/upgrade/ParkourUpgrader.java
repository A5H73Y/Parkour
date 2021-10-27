package io.github.a5h73y.parkour.upgrade;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Backup;
import io.github.g00fy2.versioncompare.Version;
import java.io.File;
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
	 *
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
	 *
	 * @return upgrade success
	 */
	public boolean beginUpgrade() {
		parkour.getLogger().info("=== Beginning Parkour Upgrade ===");
		parkour.getLogger().info(String.format("Upgrading from v%s to v%s",
				defaultConfig.getString("Version"), parkour.getDescription().getVersion()));

		parkour.getLogger().info("Creating backup of current install...");
		Backup.backupNow(true);

		boolean success = false;
		Version existingVersion = new Version(defaultConfig.getString("Version"));

		if (existingVersion.isLowerThan("6.0")) {
			parkour.getLogger().warning("This version is too outdated.");
			parkour.getLogger().warning("Please upgrade to 6.7.1 and then reinstall this version.");

		} else if (existingVersion.isLowerThan("7.0")) {
			success = performFullUpgrade();
		}

		if (success) {
//			try {
				defaultConfig.set("Version", parkour.getDescription().getVersion());

				parkour.reloadConfig();
				parkour.getLogger().info("Parkour successfully upgraded to v"
						+ parkour.getDescription().getVersion());
				parkour.getLogger().info("The plugin will now start up...");

//			} catch (IOException e) {
//				parkour.getLogger().severe("An error occurred during upgrade: " + e.getMessage());
//				e.printStackTrace();
//				success = false;
//			}
		}
		return success;
	}

	private boolean performFullUpgrade() {
		// Players and Courses need converting into their own file
		// Checkpoints get shoved into matching Course name file
		// Database - drop playerName column, add new achieved column
		return true;
	}
}

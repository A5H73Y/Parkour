package io.github.a5h73y.parkour.other;

import io.github.a5h73y.parkour.Parkour;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class UpgradeParkour extends AbstractPluginReceiver {

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

	public UpgradeParkour(Parkour parkour) {
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
		Bukkit.getScheduler().runTaskAsynchronously(Parkour.getInstance(), () -> {
			parkour.getLogger().info("=== Beginning Parkour Upgrade ===");
			parkour.getLogger().info("Upgrading from v() to v" + parkour.getDescription().getVersion());

			parkour.getLogger().info("Creating backup of current install...");
			Backup.backupNow(true);

			if (!new PlayerInfoUpgradeTask().start()) return;

			if (!new CourseInfoUpgradeTask().start()) return;

			if (!new StringsUpgradeTask().start()) return;

			if (!convertDatabase()) return;

			if (!convertDefaultConfig()) return;

			parkour.getLogger().info("Parkour successfully updated to " + parkour.getDescription().getVersion());
			parkour.getLogger().info("Enabling Parkour...");
//			parkour.onEnable();
		});
	}

	private boolean convertDatabase() {
		// maybe create a temporary table to transfer times to, then drop the original database and create the new ones
		return true;
	}

	private boolean convertDefaultConfig() {
		// change gamemode to enum from number
		// change announcement level from number to string
		defaultConfig.set("Version", parkour.getDescription().getVersion());
//			defaultConfig.save(defaultFile);
		return true;
	}

	private void convertPlayerInventory(String playerName, OfflinePlayer player) {
		Object inventoryData = inventoryConfig.get(playerName + ".Inventory");
		Object armorData = inventoryConfig.get(playerName + ".Armor");

		if (inventoryData == null) {
			parkour.getLogger().info(playerName + "'s Inventory couldn't be found.");
			return;
		}

		// this is how it was done in < 6.0
		ItemStack[] inventory = null;
		ItemStack[] armor = null;
		if (inventoryData instanceof ItemStack[]) {
			inventory = (ItemStack[]) inventoryData;
		} else if (inventoryData instanceof List) {
			List<?> lista = (List<?>) inventoryData;
			inventory = lista.toArray(new ItemStack[0]);
		}
		if (armorData instanceof ItemStack[]) {
			armor = (ItemStack[]) armorData;
		} else if (armorData instanceof List) {
			List<?> listb = (List<?>) armorData;
			armor = listb.toArray(new ItemStack[0]);
		}

		inventoryConfig.set(player.getUniqueId() + ".Inventory", Arrays.asList(inventory));
		inventoryConfig.set(player.getUniqueId() + ".Armor", Arrays.asList(armor));
		inventoryConfig.set(playerName, null);
	}

	private class PlayerInfoUpgradeTask extends AbstractUpgradeTask {

		@Override
		boolean doWork() {
			ConfigurationSection playerInfoSection = playerConfig.getConfigurationSection("PlayerInfo");

			if (playerInfoSection == null) {
				parkour.getLogger().info("Players are already upgraded...");
				return true;
			}

			boolean success = true;
			Set<String> playerNames = playerInfoSection.getKeys(false);

			parkour.getLogger().info("Converting " + playerNames.size() + " players.");
			parkour.getLogger().info("This might take a few minutes...");
			int interval = Math.max(playerNames.size() / 10, 1);
			int count = 0;

			for (String playerName : playerNames) {
				if (count % interval == 0) {
					double percent = Math.ceil((count * 100.0d) / playerNames.size());
					parkour.getLogger().info(percent + "% complete...");
				}

				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
				Set<String> settings = playerConfig.getConfigurationSection("PlayerInfo." + playerName).getKeys(false);

				for (String setting : settings) {
					playerConfig.set(offlinePlayer.getUniqueId() + "." + setting, playerConfig.get("PlayerInfo." + playerName + "." + setting));
				}

				if (inventoryConfig.contains(playerName)) {
					convertPlayerInventory(playerName, offlinePlayer);
				}

				count++;
			}

			playerConfig.set("PlayerInfo", null);
			try {
				playerConfig.save(playerFile);
				inventoryConfig.save(inventoryFile);
			} catch (IOException e) {
				parkour.getLogger().severe("An error occurred during upgrade: " + e.getMessage());
				e.printStackTrace();
				success = false;
			}
			return success;
		}

		@Override
		String getTitle() {
			return "PlayerInfo";
		}
	}

	private class CourseInfoUpgradeTask extends AbstractUpgradeTask {

		@Override
		boolean doWork() {
			boolean success = true;
			List<String> courseNames = coursesConfig.getStringList("Courses");

			parkour.getLogger().info("Converting " + courseNames.size() + " courses.");
			int interval = Math.max(courseNames.size() / 10, 1);
			int count = 0;

			for (String courseName : courseNames) {
				if (count % interval == 0) {
					double percent = Math.ceil((count * 100.0d) / courseNames.size());
					parkour.getLogger().info(percent + "% complete...");
				}

				int checkpoints = coursesConfig.getInt(courseName + ".Points");
				coursesConfig.set(courseName + ".Checkpoints", checkpoints);
				coursesConfig.set(courseName + ".Points", null);

				for (int i = 0; i < checkpoints + 1; i++) {

					// first we do the checkpoints.yml file
					ConfigurationSection courseConfigSection = coursesConfig.getConfigurationSection(courseName + "." + i);

					if (courseConfigSection == null) {
						parkour.getLogger().info("Course " + courseName + " is already upgraded...");
						continue;
					}

					if (i > 0) {
						ConfigurationSection checkpointConfigSection = checkpointsConfig.getConfigurationSection(courseName + "." + i);
						Set<String> xyz = checkpointConfigSection.getKeys(false);
						for (String coordinate : xyz) {
							checkpointsConfig.set(courseName + "." + i + ".Plate" + coordinate, checkpointsConfig.get(courseName + "." + i + "." + coordinate));
							checkpointsConfig.set(courseName + "." + i + "." + coordinate, null);
						}
					}

					// then transfer data from courses.yml to checkpoints.yml
					Set<String> checkpointData = courseConfigSection.getKeys(false);
					for (String checkpointDatum : checkpointData) {
						checkpointsConfig.set(courseName + "." + i + "." + checkpointDatum, coursesConfig.getDouble(courseName + "." + i + "." + checkpointDatum));
						coursesConfig.set(courseName + "." + i, null);
					}
				}

				count++;
			}

			try {
				checkpointsConfig.save(checkpointsFile);
				coursesConfig.save(coursesFile);
			} catch (IOException e) {
				parkour.getLogger().severe("An error occurred during upgrade: " + e.getMessage());
				e.printStackTrace();
				success = false;
			}
			return success;
		}

		@Override
		String getTitle() {
			return "CourseInfo";
		}
	}

	private class StringsUpgradeTask extends AbstractUpgradeTask {

		@Override
		boolean doWork() {
			boolean success = true;
			Set<String> strings = stringsConfig.getConfigurationSection("").getKeys(true);
			Pattern pattern = Pattern.compile("%.*?%", Pattern.DOTALL);

			for (String string : strings) {
				String value = stringsConfig.getString(string);

				if (value != null && !value.isEmpty() && !value.startsWith("MemorySection")) {
					Matcher matcher = pattern.matcher(value);
					int results = countMatches(matcher);

					// we only want to replace the entries with a single value placeholder
					if (results == 1) {
						stringsConfig.set(string, matcher.replaceAll("%VALUE%"));
					}
				}
			}
			try {
				stringsConfig.save(stringsFile);
			} catch (IOException e) {
				parkour.getLogger().severe("An error occurred during upgrade: " + e.getMessage());
				e.printStackTrace();
				success = false;
			}
			return success;
		}

		@Override
		String getTitle() {
			return "Strings";
		}

		int countMatches(Matcher matcher) {
			int counter = 0;
			while (matcher.find())
				counter++;
			return counter;
		}
	}

	private abstract class AbstractUpgradeTask {

		long startTime;

		boolean start() {
			parkour.getLogger().info(this.getTitle() + " Upgrade started...");
			startTime = System.currentTimeMillis();

			boolean success = doWork();

			parkour.getLogger().info(this.getTitle() + " Upgrade complete. Time taken: "
					+ (System.currentTimeMillis() - startTime) + "ms");
			return success;
		}

		abstract boolean doWork();

		abstract String getTitle();

	}
}

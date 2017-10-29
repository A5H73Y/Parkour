package me.A5H73Y.Parkour.Other;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Enums.DatabaseType;
import me.A5H73Y.Parkour.ParkourPlaceholders;
import me.A5H73Y.Parkour.Player.ParkourSession;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Settings;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.huskehhh.mysql.Database;
import com.huskehhh.mysql.mysql.MySQL;
import com.huskehhh.mysql.sqlite.SQLite;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class StartPlugin {

	private static boolean freshInstall = false;
	private static boolean updateExisting = false;
	private static boolean fromBeforeVersion4 = false;

	public static void run() {
		checkConvertToLatest();
		Parkour.getParkourConfig().setupConfig();
		Static.initiate();
		initiateSQL();
		setupExternalPlugins();
		populatePlayers();
		Utils.log("Enabled Parkour v" + Static.getVersion() + "!");
	}

	/**
	 * Unfortunately this has to be run before the configuration can initialize.
	 * Just makes onEnable look ugly
	 * @return
	 */
	public static boolean isFreshInstall(){
		if (new File(Parkour.getPlugin().getDataFolder().toString() + File.separator + "config.yml").exists())
			return false;

		Utils.log("Fresh install as no previous version was found.");
		freshInstall = true;
		return true;
	}

	private static void setupExternalPlugins() {
		setupVault();
		setupBountifulAPI();
		setupPlaceholderAPI();
	}

	private static void setupVault() {
		if (!Parkour.getPlugin().getConfig().getBoolean("Other.Economy.Enabled"))
			return;

		PluginManager pm = Parkour.getPlugin().getServer().getPluginManager();
		Plugin vault = pm.getPlugin("Vault");
		if (vault != null && vault.isEnabled()) {
			if (setupEconomy()) {
				Utils.log("[Vault] Successfully linked. Version: " + vault.getDescription().getVersion());
				Parkour.getParkourConfig().initiateEconomy();
			} else {
				Utils.log("[Vault] Attempted to link with Vault, but something went wrong.", 2);
				Parkour.getPlugin().getConfig().set("Other.Economy.Enabled", false);
				Parkour.getPlugin().saveConfig();
			}
		} else {
			Utils.log("[Vault] Plugin is missing, disabling Economy Use.", 1);
			Parkour.getPlugin().getConfig().set("Other.Economy.Enabled", false);
			Parkour.getPlugin().saveConfig();
		}
	}

	private static void initiateSQL() {
		initiateSQL(false);
	}

	private static void initiateSQL(boolean forceSQLite) {
		Database database;
		FileConfiguration config = Parkour.getPlugin().getConfig();

		// Only use MySQL if they have enabled it, configured it, and we aren't
		// forcing SQLite (MySQL failed)
		if (!forceSQLite && config.getBoolean("MySQL.Use") && !config.getString("MySQL.Host").equals("Host") ) {
			database = new MySQL(config.getString("MySQL.Host"), config.getString("MySQL.Port"), config.getString("MySQL.Database"), config.getString("MySQL.User"), config.getString("MySQL.Password"));
			DatabaseMethods.type = DatabaseType.MySQL;
		} else {
			database = new SQLite("parkour.db");
			DatabaseMethods.type = DatabaseType.SQLite;
		}

		try {
			database.openConnection();
			Parkour.setDatabaseObj(database);
			DatabaseMethods.setupTables();

			if (updateExisting){
				for (String courseName : Static.getCourses()){
					DatabaseMethods.insertCourse(courseName, Parkour.getParkourConfig().getCourseData().getString(courseName + ".Creator"));
				}
			}
		} catch (Exception ex) {
			failedSQL(ex);
		}
	}

	private static void failedSQL(Exception ex) {
		Utils.log("[SQL] Connection problem: " + ex.getMessage(), 2);
		Utils.log("[SQL] Defaulting to SQLite...", 1);
		Parkour.getPlugin().getConfig().set("MySQL.Use", false);
		Parkour.getPlugin().saveConfig();
		initiateSQL(true);
	}

	private static boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Parkour.getPlugin().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			Parkour.setEconomy(economyProvider.getProvider());
		}
		return (Parkour.getEconomy() != null);
	}

	private static void setupBountifulAPI() {
		if (!Parkour.getPlugin().getConfig().getBoolean("Other.BountifulAPI.Enabled"))
			return;

		Plugin bountifulAPI = Parkour.getPlugin().getServer().getPluginManager()
				.getPlugin("BountifulAPI");

		if (bountifulAPI != null && bountifulAPI.isEnabled()) {
			Utils.log("[BountifulAPI] Successfully linked. Version: " + bountifulAPI.getDescription().getVersion());
			Static.enableBountifulAPI();
		} else {
			Utils.log("[BountifulAPI] Plugin is missing, disabling config option.", 1);
			Parkour.getPlugin().getConfig().set("Other.BountifulAPI.Enabled", false);
			Parkour.getPlugin().saveConfig();
		}
	}

	private static void setupPlaceholderAPI() {
		if (!Parkour.getPlugin().getConfig().getBoolean("Other.PlaceholderAPI.Enabled"))
			return;

		Plugin placeholderAPI = Parkour.getPlugin().getServer().getPluginManager()
				.getPlugin("PlaceholderAPI");

		if (placeholderAPI != null && placeholderAPI.isEnabled()) {
			Utils.log("[PlaceholderAPI] Successfully linked. Version: " + placeholderAPI.getDescription().getVersion());
			Static.enablePlaceholderAPI();

            if (Static.isPlaceholderAPI()) {
                new ParkourPlaceholders(Parkour.getPlugin()).hook();
            }

		} else {
			Utils.log("[PlaceholderAPI] Plugin is missing, disabling config option.", 1);
			Parkour.getPlugin().getConfig().set("Other.PlaceholderAPI.Enabled", false);
			Parkour.getPlugin().saveConfig();
		}
	}

	private static void populatePlayers() {
		if (!new File(Static.PATH).exists())
			return;

		try {
			@SuppressWarnings("unchecked")
			HashMap<String, ParkourSession> players = (HashMap<String, ParkourSession>) Utils.loadAllPlaying(Static.PATH);
			PlayerMethods.setPlaying(players);

			for (Entry<String, ParkourSession> entry : players.entrySet()) {
				Player playingp = Parkour.getPlugin().getServer().getPlayer(entry.getKey());
				if (playingp == null)
					continue;

				playingp.sendMessage(Utils.getTranslation("Parkour.Continue")
						.replace("%COURSE%", entry.getValue().getCourse().getName()));
			}
		} catch (Exception e) {
			Utils.log("Failed to load players: " + e.getMessage(), 2);
			PlayerMethods.setPlaying(new HashMap<String, ParkourSession>());
		}
	}

	/**
	 * We only want to update completely, if the config version (previous version) is less than 4.0 (new system)
	 */
	private static void checkConvertToLatest() {
		if (freshInstall)
			return;

		double configVersion = Parkour.getPlugin().getConfig().getDouble("Version");
		double currentVersion = Double.parseDouble(Parkour.getPlugin().getDescription().getVersion());

		if (configVersion >= currentVersion)
			return;

		if (configVersion >= 4.0)
		    fromBeforeVersion4 = true;

		updateExisting = true;
		Utils.log("[Backup] Updating config to " + currentVersion + "...");
		// We backup all their files first before touching them
		Backup.backupNow(false);
		convertToLatest();
		Parkour.getPlugin().getConfig().set("Version", currentVersion);
		Parkour.getPlugin().saveConfig();
	}

	private static void convertToLatest() {
		try {
			// Update existing checkpoints to use lowercase course names
			Path path = Paths.get(Parkour.getPlugin().getDataFolder().getPath(), "checkpoints.yml");
			String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			for (String course : Parkour.getParkourConfig().getAllCourses()) {
				content = content.replace(course, course.toLowerCase());
			}
			Files.write(path, content.getBytes(StandardCharsets.UTF_8));

			// Update the existing courses to use lowercase course names
			path = Paths.get(Parkour.getPlugin().getDataFolder().getPath(), "courses.yml");
			content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			for (String course : Parkour.getParkourConfig().getAllCourses()) {
				content = content.replace(course, course.toLowerCase());
			}
			Files.write(path, content.getBytes(StandardCharsets.UTF_8));

			String[] lobbyData = getLobbyData();

			if (fromBeforeVersion4) {
                // Reset current config
                for (String key : Parkour.getPlugin().getConfig().getKeys(false)) {
                    Parkour.getPlugin().getConfig().set(key, null);
                }
                Utils.broadcastMessage("[Backup] Your existing config has been backed up. We have generated a new config, please reapply the settings you want.", "Parkour.Admin");
            }

			Parkour.getPlugin().saveConfig();
			Parkour.getParkourConfig().reload();

			Parkour.getParkourConfig().setupConfig();
			Parkour.setSettings(new Settings());

			setLobbyData(lobbyData);

			Static.initiate();

			Utils.log("[Backup] Complete.");
		} catch (Exception ex) {
			Utils.log("[Backup] Failed: " + ex.getMessage());
		}
	}

	private static String[] getLobbyData() {
		String[] details = new String[6];

		details[0] = Parkour.getPlugin().getConfig().getString("Lobby.World");
		details[1] = Parkour.getPlugin().getConfig().getString("Lobby.X");
		details[2] = Parkour.getPlugin().getConfig().getString("Lobby.Y");
		details[3] = Parkour.getPlugin().getConfig().getString("Lobby.Z");
		details[4] = Parkour.getPlugin().getConfig().getString("Lobby.Pitch");
		details[5] = Parkour.getPlugin().getConfig().getString("Lobby.Yaw");

		return details;
	}

	private static void setLobbyData(String[] lobbyData) {
		Parkour.getPlugin().getConfig().set("Lobby.Set", true);
		Parkour.getPlugin().getConfig().set("Lobby.World", lobbyData[0]);
		Parkour.getPlugin().getConfig().set("Lobby.X", lobbyData[1]);
		Parkour.getPlugin().getConfig().set("Lobby.Y", lobbyData[2]);
		Parkour.getPlugin().getConfig().set("Lobby.Z", lobbyData[3]);
		Parkour.getPlugin().getConfig().set("Lobby.Pitch", lobbyData[4]);
		Parkour.getPlugin().getConfig().set("Lobby.Yaw", lobbyData[5]);
	}
}

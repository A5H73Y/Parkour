package me.A5H73Y.Parkour.Other;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Player.PPlayer;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.huskehhh.mysql.Database;
import com.huskehhh.mysql.mysql.MySQL;
import com.huskehhh.mysql.sqlite.SQLite;

import de.bg.derh4nnes.TitleActionBarAPI;

public class StartPlugin {

	static Plugin vault, barAPI;

	public static void run(){
		Static.initiate();
		initiateSQL();
		setupVault();
		setupBarAPI();
		populatePlayers();
		//Updater
		convertToLatest();
		Utils.log("Enabled Parkour v" + Static.getVersion() + "!");
	}

	private static void setupVault() {
		if (!Parkour.getParkourConfig().getConfig().getBoolean("Other.Use.Economy"))
			return;

		PluginManager pm = Parkour.getPlugin().getServer().getPluginManager();
		vault = pm.getPlugin("Vault");
		if (vault != null && vault.isEnabled()) {
			if (!setupEconomy()) {
				Utils.log("Attempted to link with Vault, but something went wrong.");
				Parkour.getPlugin().getConfig().set("Other.Use.Economy", false);
				Parkour.getPlugin().saveConfig();
			} else {
				Utils.log("Linked with Economy v" + vault.getDescription().getVersion());
			}
		} else {
			Utils.log("Vault is missing, disabling Economy Use.");
			Parkour.getPlugin().getConfig().set("Other.Use.Economy", false);
			Parkour.getPlugin().saveConfig();
		}
	}

	public static void initiateSQL(){
		Database database;
		FileConfiguration config = Parkour.getParkourConfig().getConfig();

		if (config.getBoolean("MySQL.Use")){
			database = new MySQL(config.getString("MySQL.Host"), config.getString("MySQL.Port"), config.getString("MySQL.Database"), config.getString("MySQL.User"), config.getString("MySQL.Password"));
		}else{
			database = new SQLite(Parkour.getPlugin().getDataFolder().toString() + File.separator + "parkour.db");
		}

		try {
			database.openConnection();
			database.setupTables();
			Parkour.setDatabaseObj(database);
		} catch (ClassNotFoundException e) {
			Utils.log("SQL connection problem: " + e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			Utils.log("SQL connection problem: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Parkour.getPlugin().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			Parkour.setEconomy(economyProvider.getProvider());
		}
		return (Parkour.getEconomy() != null);
	}


	@SuppressWarnings("unchecked")
	private static void populatePlayers(){
		if (!new File(Static.PATH).exists())
			return;

		try {
			HashMap<String, PPlayer> players = (HashMap<String, PPlayer>) Utils.loadAllPlaying(Static.PATH);
			PlayerMethods.setPlaying(players);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setupBarAPI(){
		PluginManager pm = Parkour.getPlugin().getServer().getPluginManager();
		barAPI = pm.getPlugin("TitleActionbarAPI");
		if (barAPI != null && barAPI.isEnabled()) {
			if (TitleActionBarAPI.isValidVersion()){ //TitleActionBarAPI.getValid()
				Utils.log("Linked with TitleActionbarAPI v" + barAPI.getDescription().getVersion());
				Static.setBarAPI(true);
			}else{
				Utils.log("Attempted to Link with TitleActionbarAPI, but server version is not supported");
			}
		}
	}

	private static void convertToLatest(){
		double configVersion = Parkour.getParkourConfig().getConfig().getDouble("Version");

		if (configVersion < Static.getVersion()){
			Utils.log("Updating config to " + Static.getVersion() + "...");
			
			Backup.backupNow(false);
			
			try{
				Charset charset = StandardCharsets.UTF_8;

				Path path = Paths.get(Parkour.getPlugin().getDataFolder().getPath(), "courses.yml");
				String content = new String(Files.readAllBytes(path), charset);

				for (String course : Static.getCourses()){
					content = content.replaceAll(course, course.toLowerCase());
				}

				Files.write(path, content.getBytes(charset));
				
				Static.initiate();
				Utils.log("Done.");
			} catch (Exception ex){
				Utils.log("Failed: " + ex.getMessage());
			}
			//Parkour.getParkourConfig().getConfig().set("Version", Static.getVersion());

		}

	}
}

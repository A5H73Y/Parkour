package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Other.Configurations;
import me.A5H73Y.Parkour.Other.StartPlugin;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Settings;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.java.JavaPlugin;

import com.huskehhh.mysql.Database;

public class Parkour extends JavaPlugin {

	private static JavaPlugin plugin;
	private static Configurations config;
	private static Database database;
	private static Economy economy;
	private static Settings settings;

	public void onEnable() {
		plugin = this;
		StartPlugin.isFreshInstall();
		config = new Configurations();
		StartPlugin.run();

		settings = new Settings();
		getServer().getPluginManager().registerEvents(new ParkourListener(), this);
		getServer().getPluginManager().registerEvents(new ParkourSignListener(), this);
		getCommand("parkour").setExecutor(new ParkourCommands());
	}

	public void onDisable() {
		Utils.saveAllPlaying(PlayerMethods.getPlaying(), Static.PATH);
		config.saveAll();
		getParkourConfig().reload();
		database.closeConnection();
		Utils.log("Disabled Parkour v" + Static.getVersion());
		plugin = null;
	}

	public static void setDatabaseObj(Database databaseObj) {
		database = databaseObj;
	}

	public static void setEconomy(Economy newEconomy) {
		economy = newEconomy;
	}

	public static void setSettings(Settings newSettings) {
		settings = newSettings;
	}

	// Getters
	public static JavaPlugin getPlugin() {
		return plugin;
	}

	public static Configurations getParkourConfig() {
		return config;
	}

	public static Settings getSettings() {
		return settings;
	}

	public static Database getDatabaseObj() {
		return database;
	}

	public static Economy getEconomy() {
		return economy;
	}
}
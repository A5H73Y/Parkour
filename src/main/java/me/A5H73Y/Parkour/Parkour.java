package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Listeners.*;
import me.A5H73Y.Parkour.Other.Configurations;
import me.A5H73Y.Parkour.Other.StartPlugin;
import me.A5H73Y.Parkour.Other.Updater;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Managers.ScoreboardManager;
import me.A5H73Y.Parkour.Utilities.Settings;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import net.milkbowl.vault.economy.Economy;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import com.huskehhh.mysql.Database;

public class Parkour extends JavaPlugin {

	private static Parkour instance;
	private Configurations config;
	private Database database;
	private Economy economy;
	private Settings settings;

	private ScoreboardManager scoreboardManager;

    public void onEnable() {
		instance = this;
		config = new Configurations();
		StartPlugin.run();
		settings = new Settings();

		registerEvents();
		registerCommands();

        new Metrics(this);
        updatePlugin();
	}

	public void onDisable() {
		Utils.saveAllPlaying(PlayerMethods.getPlaying(), Static.PLAYING_BIN_PATH);
		config.saveAll();
		getParkourConfig().reload();
		database.closeConnection();
		Utils.log("Disabled Parkour v" + Static.getVersion());
		instance = null;
	}

    public static Parkour getPlugin() {
        return instance;
    }

	private void registerEvents() {
        getServer().getPluginManager().registerEvents(new BlockListener(),  this);
        getServer().getPluginManager().registerEvents(new ChatListener(),  this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(),  this);
        getServer().getPluginManager().registerEvents(new PlayerListener(),  this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(),  this);
        getServer().getPluginManager().registerEvents(new SignListener(),  this);
    }

    private void registerCommands() {
        getCommand("parkour").setExecutor(new ParkourCommands());
        getCommand("parkour").setTabCompleter(new ParkourAutoTabCompleter());
    }

	public static Configurations getParkourConfig() {
		return getPlugin().config;
	}

	public static Settings getSettings() {
		return getPlugin().settings;
	}

	public static Economy getEconomy() {
		return getPlugin().economy;
	}

    public static Database getDatabase() {
        return getPlugin().database;
    }

    public static ScoreboardManager getScoreboardManager() {
        if (getPlugin().scoreboardManager == null) {
            getPlugin().scoreboardManager = new ScoreboardManager();
        }
        return getPlugin().scoreboardManager;
    }

    public static void setDatabase(Database database) {
        getPlugin().database = database;
    }

    public static void setEconomy(Economy economy) {
        getPlugin().economy = economy;
    }

	private void updatePlugin() {
		if (Parkour.getPlugin().getConfig().getBoolean("Other.CheckForUpdates"))
			new Updater(this, 42615, this.getFile(), Updater.UpdateType.DEFAULT, true);
	}
}
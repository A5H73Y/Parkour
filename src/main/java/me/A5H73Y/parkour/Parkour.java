package me.A5H73Y.parkour;

import com.huskehhh.mysql.Database;
import me.A5H73Y.parkour.commands.ParkourAutoTabCompleter;
import me.A5H73Y.parkour.commands.ParkourCommands;
import me.A5H73Y.parkour.commands.ParkourConsoleCommands;
import me.A5H73Y.parkour.listener.*;
import me.A5H73Y.parkour.manager.ScoreboardManager;
import me.A5H73Y.parkour.other.Backup;
import me.A5H73Y.parkour.other.Configurations;
import me.A5H73Y.parkour.other.StartPlugin;
import me.A5H73Y.parkour.other.Updater;
import me.A5H73Y.parkour.player.PlayerMethods;
import me.A5H73Y.parkour.utilities.Settings;
import me.A5H73Y.parkour.utilities.Static;
import me.A5H73Y.parkour.utilities.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

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
        if (instance.getConfig().getBoolean("Other.OnServerShutdown.BackupFiles")) {
            Backup.backupNow();
        }
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
        getServer().getPluginManager().registerEvents(new PlayerInventoryListener(), this);
    }

    private void registerCommands() {
        getCommand("parkour").setExecutor(new ParkourCommands());
        getCommand("paconsole").setExecutor(new ParkourConsoleCommands());

        if (this.getConfig().getBoolean("Other.UseAutoTabCompletion")) {
            getCommand("parkour").setTabCompleter(new ParkourAutoTabCompleter());
        }
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
        if (Parkour.getPlugin().getConfig().getBoolean("Other.CheckForUpdates")) {
            new Updater(this, 42615, this.getFile(), Updater.UpdateType.DEFAULT, true);
        }
    }
}
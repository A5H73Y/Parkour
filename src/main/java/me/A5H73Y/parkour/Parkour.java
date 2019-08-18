package me.A5H73Y.parkour;

import com.huskehhh.mysql.Database;
import me.A5H73Y.parkour.commands.ParkourAutoTabCompleter;
import me.A5H73Y.parkour.commands.ParkourCommands;
import me.A5H73Y.parkour.commands.ParkourConsoleCommands;
import me.A5H73Y.parkour.config.ConfigManager;
import me.A5H73Y.parkour.config.ParkourConfiguration;
import me.A5H73Y.parkour.enums.ConfigType;
import me.A5H73Y.parkour.listener.BlockListener;
import me.A5H73Y.parkour.listener.ChatListener;
import me.A5H73Y.parkour.listener.PlayerInteractListener;
import me.A5H73Y.parkour.listener.PlayerInventoryListener;
import me.A5H73Y.parkour.listener.PlayerListener;
import me.A5H73Y.parkour.listener.PlayerMoveListener;
import me.A5H73Y.parkour.listener.SignListener;
import me.A5H73Y.parkour.manager.ScoreboardManager;
import me.A5H73Y.parkour.other.Backup;
import me.A5H73Y.parkour.other.StartPlugin;
import me.A5H73Y.parkour.other.Updater;
import me.A5H73Y.parkour.player.PlayerMethods;
import me.A5H73Y.parkour.utilities.Settings;
import me.A5H73Y.parkour.utilities.Static;
import me.A5H73Y.parkour.utilities.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Parkour extends JavaPlugin {

    private static Parkour instance;
    private ConfigManager configManager;
    private Database database;
    private Economy economy;
    private Settings settings;

    private ScoreboardManager scoreboardManager;

    public static Parkour getInstance() {
        return instance;
    }

    public static Settings getSettings() {
        return getInstance().settings;
    }

    public static Economy getEconomy() {
        return getInstance().economy;
    }

    public static void setEconomy(Economy economy) {
        getInstance().economy = economy;
    }

    public static Database getDatabase() {
        return getInstance().database;
    }

    public static void setDatabase(Database database) {
        getInstance().database = database;
    }

    public static ScoreboardManager getScoreboardManager() {
        if (getInstance().scoreboardManager == null) {
            getInstance().scoreboardManager = new ScoreboardManager();
        }
        return getInstance().scoreboardManager;
    }

    public static ParkourConfiguration getConfig(ConfigType type) {
        return instance.configManager.get(type);
    }

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        StartPlugin.run();
        settings = new Settings();

        registerEvents();
        registerCommands();

        new Metrics(this);
        updatePlugin();

        Utils.log("v6.0 is a very unstable build, please expect problems to occur and raise them in the Discord server.", 2);
    }

    @Override
    public void onDisable() {
        Utils.saveAllPlaying(PlayerMethods.getPlaying(), Static.PLAYING_BIN_PATH);
        if (getConfig().getBoolean("Other.OnServerShutdown.BackupFiles")) {
            Backup.backupNow();
        }
        database.closeConnection();
	    // configManager.reloadConfigs(); needed?
        Utils.log("Disabled Parkour v" + Static.getVersion());
        instance = null;
    }

    @Override
    public FileConfiguration getConfig() {
        return getConfig(ConfigType.DEFAULT);
    }

    @Override
    public void saveConfig() {
        getConfig(ConfigType.DEFAULT).save();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInventoryListener(), this);
    }

    private void registerCommands() {
        getCommand("parkour").setExecutor(new ParkourCommands());
        getCommand("paconsole").setExecutor(new ParkourConsoleCommands());

        if (this.getConfig().getBoolean("Other.UseAutoTabCompletion")) {
            getCommand("parkour").setTabCompleter(new ParkourAutoTabCompleter());
        }
    }

    public void reloadConfigurations() {
        configManager.reloadConfigs();
        settings.resetSettings();
        Static.initiate();
    }

    private void updatePlugin() {
        if (Parkour.getInstance().getConfig().getBoolean("Other.CheckForUpdates")) {
            new Updater(this, 42615, this.getFile(), Updater.UpdateType.DEFAULT, true);
        }
    }
}

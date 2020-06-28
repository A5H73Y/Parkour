package io.github.a5h73y.parkour;

import io.github.a5h73y.parkour.commands.ParkourAutoTabCompleter;
import io.github.a5h73y.parkour.commands.ParkourCommands;
import io.github.a5h73y.parkour.commands.ParkourConsoleCommands;
import io.github.a5h73y.parkour.config.ConfigManager;
import io.github.a5h73y.parkour.config.ParkourConfiguration;
import io.github.a5h73y.parkour.database.ParkourDatabase;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.listener.BlockListener;
import io.github.a5h73y.parkour.listener.ChatListener;
import io.github.a5h73y.parkour.listener.PlayerInteractListener;
import io.github.a5h73y.parkour.listener.PlayerInventoryListener;
import io.github.a5h73y.parkour.listener.PlayerListener;
import io.github.a5h73y.parkour.listener.PlayerMoveListener;
import io.github.a5h73y.parkour.listener.SignListener;
import io.github.a5h73y.parkour.manager.ScoreboardManager;
import io.github.a5h73y.parkour.other.Backup;
import io.github.a5h73y.parkour.other.ParkourUpdater;
import io.github.a5h73y.parkour.other.StartPlugin;
import io.github.a5h73y.parkour.player.PlayerMethods;
import io.github.a5h73y.parkour.utilities.Settings;
import io.github.a5h73y.parkour.utilities.Static;
import io.github.a5h73y.parkour.utilities.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Parkour extends JavaPlugin {

    private static final int BUKKIT_PLUGIN_ID = 42615;
    private static final int SPIGOT_PLUGIN_ID = 23685;
    private static Parkour instance;

    private ConfigManager configManager;
    private ParkourDatabase database;
    private Economy economy;
    private Settings settings;

    private ScoreboardManager scoreboardManager;

    /**
     * Get the plugin's instance.
     *
     * @return Parkour plugin instance.
     */
    public static Parkour getInstance() {
        return instance;
    }

    /**
     * Initialise the Parkour plugin.
     */
    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        StartPlugin.run();
        database = new ParkourDatabase(this);
        settings = new Settings();

        registerEvents();
        registerCommands();

        new MetricsLite(this, BUKKIT_PLUGIN_ID);
        updatePlugin();

        Utils.log("v6.0 is currently a very unstable build, expect problems to occur and please raise them in the Discord server.", 2);
    }

    @Override
    public void onDisable() {
        Utils.saveAllPlaying(PlayerMethods.getPlaying(), Static.PLAYING_BIN_PATH);
        if (getConfig().getBoolean("Other.OnServerShutdown.BackupFiles")) {
            Backup.backupNow();
        }
        // configManager.reloadConfigs(); TODO needed?
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

    public static Settings getSettings() {
        return instance.settings;
    }

    public static Economy getEconomy() {
        return instance.economy;
    }

    public static void setEconomy(Economy economy) {
        instance.economy = economy;
    }

    public static ParkourDatabase getDatabase() {
        return instance.database;
    }

    public static void setDatabase(ParkourDatabase database) {
        instance.database = database;
    }

    public static ScoreboardManager getScoreboardManager() {
        if (instance.scoreboardManager == null) {
            instance.scoreboardManager = new ScoreboardManager();
        }
        return instance.scoreboardManager;
    }

    public static ParkourConfiguration getConfig(ConfigType type) {
        return instance.configManager.get(type);
    }

    public void reloadConfigurations() {
        configManager.reloadConfigs();
        settings.resetSettings();
        Static.initiate();
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

    private void updatePlugin() {
        if (Parkour.getInstance().getConfig().getBoolean("Other.CheckForUpdates")) {
            new ParkourUpdater(this, SPIGOT_PLUGIN_ID).checkForUpdateAsync();
        }
    }
}

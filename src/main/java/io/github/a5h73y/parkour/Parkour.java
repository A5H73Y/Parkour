package io.github.a5h73y.parkour;

import com.google.gson.GsonBuilder;
import io.github.a5h73y.parkour.commands.ParkourAutoTabCompleter;
import io.github.a5h73y.parkour.commands.ParkourCommands;
import io.github.a5h73y.parkour.commands.ParkourConsoleCommands;
import io.github.a5h73y.parkour.configuration.ConfigManager;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.configuration.impl.DefaultConfig;
import io.github.a5h73y.parkour.database.ParkourDatabase;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.gui.ParkourGuiManager;
import io.github.a5h73y.parkour.listener.BlockListener;
import io.github.a5h73y.parkour.listener.ChatListener;
import io.github.a5h73y.parkour.listener.PlayerInteractListener;
import io.github.a5h73y.parkour.listener.PlayerListener;
import io.github.a5h73y.parkour.listener.PlayerMoveListener;
import io.github.a5h73y.parkour.listener.SignListener;
import io.github.a5h73y.parkour.manager.ChallengeManager;
import io.github.a5h73y.parkour.manager.QuestionManager;
import io.github.a5h73y.parkour.manager.ScoreboardManager;
import io.github.a5h73y.parkour.other.Backup;
import io.github.a5h73y.parkour.other.CommandUsage;
import io.github.a5h73y.parkour.other.ParkourUpdater;
import io.github.a5h73y.parkour.plugin.BountifulApi;
import io.github.a5h73y.parkour.plugin.EconomyApi;
import io.github.a5h73y.parkour.plugin.PlaceholderApi;
import io.github.a5h73y.parkour.type.checkpoint.CheckpointManager;
import io.github.a5h73y.parkour.type.course.CourseManager;
import io.github.a5h73y.parkour.type.kit.ParkourKitManager;
import io.github.a5h73y.parkour.type.lobby.LobbyManager;
import io.github.a5h73y.parkour.type.player.PlayerManager;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class Parkour extends JavaPlugin {

    private static final int BUKKIT_PLUGIN_ID = 42615;
    private static final int SPIGOT_PLUGIN_ID = 23685;
    private static Parkour instance;

    private BountifulApi bountifulApi;
    private EconomyApi economyApi;
    private PlaceholderApi placeholderApi;

    private ParkourDatabase database;
    private List<CommandUsage> commandUsages;

    private ConfigManager configManager;
    private ScoreboardManager scoreboardManager;
    private ChallengeManager challengeManager;
    private QuestionManager questionManager;
    private PlayerManager playerManager;
    private CourseManager courseManager;
    private CheckpointManager checkpointManager;
    private LobbyManager lobbyManager;
    private ParkourKitManager parkourKitManager;
    private ParkourGuiManager guiManager;

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

        if (parkourNeedsUpgrading()) {
            new ParkourUpgrader(this).begin();
            return;
        }

        registerManagers();
        registerCommands();
        registerEvents();

        setupPlugins();

        getLogger().info("Enabled Parkour v" + getDescription().getVersion());
        new Metrics(this, BUKKIT_PLUGIN_ID);
        checkForUpdates();

        PluginUtils.log("v6.0 is currently a very unstable build, "
                + "expect problems to occur and please raise them in the Discord server.", 2);
    }

    /**
     * Shutdown the plugin.
     */
    @Override
    public void onDisable() {
        if (getConfig().getBoolean("Other.OnServerShutdown.BackupFiles")) {
            Backup.backupNow();
        }
        getPlayerManager().teardownParkourPlayers();
        getDatabase().closeConnection();
        PluginUtils.log("Disabled Parkour v" + getDescription().getVersion());
        instance = null;
    }

    /**
     * Get the Default config.
     * Overrides the default getConfig() method.
     *
     * @return default config
     */
    @Override
    public DefaultConfig getConfig() {
        return (DefaultConfig) this.configManager.get(ConfigType.DEFAULT);
    }

    /**
     * Get the matching {@link ParkourConfiguration} for the given {@link ConfigType}.
     *
     * @param type {@link ConfigType}
     * @return matching {@link ParkourConfiguration}
     */
    public static ParkourConfiguration getConfig(ConfigType type) {
        return instance.configManager.get(type);
    }

    /**
     * Save the Default config.
     * Overrides the default saveConfig() method.
     */
    @Override
    public void saveConfig() {
        this.configManager.get(ConfigType.DEFAULT).save();
    }

    /**
     * Get the default config.yml file.
     *
     * @return {@link DefaultConfig}
     */
    public static DefaultConfig getDefaultConfig() {
        return instance.getConfig();
    }

    /**
     * The Parkour message prefix.
     *
     * @return parkour prefix from the config.
     */
    public static String getPrefix() {
        return TranslationUtils.getTranslation("Parkour.Prefix", false);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ParkourDatabase getDatabase() {
        return database;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

    public QuestionManager getQuestionManager() {
        return questionManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public CourseManager getCourseManager() {
        return courseManager;
    }

    public CheckpointManager getCheckpointManager() {
        return checkpointManager;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public ParkourKitManager getParkourKitManager() {
        return parkourKitManager;
    }

    public ParkourGuiManager getGuiManager() {
        return guiManager;
    }

    public BountifulApi getBountifulApi() {
        return bountifulApi;
    }

    public EconomyApi getEconomyApi() {
        return economyApi;
    }

    public PlaceholderApi getPlaceholderApi() {
        return placeholderApi;
    }

    public List<CommandUsage> getCommandUsages() {
        return commandUsages;
    }

    public void registerEssentialManagers() {
        configManager = new ConfigManager(this.getDataFolder());
        database = new ParkourDatabase(this);
    }

    private void setupPlugins() {
        bountifulApi = new BountifulApi();
        economyApi = new EconomyApi();
        placeholderApi = new PlaceholderApi();
    }

    private void registerManagers() {
        registerEssentialManagers();
        scoreboardManager = new ScoreboardManager(this);
        challengeManager = new ChallengeManager(this);
        questionManager = new QuestionManager(this);
        courseManager = new CourseManager(this);
        checkpointManager = new CheckpointManager(this);
        parkourKitManager = new ParkourKitManager(this);
        playerManager = new PlayerManager(this);
        lobbyManager = new LobbyManager(this);
        guiManager = new ParkourGuiManager(this);
    }

    private void registerCommands() {
        getCommand("parkour").setExecutor(new ParkourCommands(this));
        getCommand("paconsole").setExecutor(new ParkourConsoleCommands(this));

        if (this.getConfig().getBoolean("Other.UseAutoTabCompletion")) {
            getCommand("parkour").setTabCompleter(new ParkourAutoTabCompleter(this));
        }

        String json = new BufferedReader(new InputStreamReader(getResource("parkourCommands.json"), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        commandUsages = Arrays.asList(new GsonBuilder().create().fromJson(json, CommandUsage[].class));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new SignListener(this), this);
    }

    private void checkForUpdates() {
        if (getConfig().getBoolean("Other.CheckForUpdates")) {
            new ParkourUpdater(this, SPIGOT_PLUGIN_ID).checkForUpdateAsync();
        }
    }

    private boolean parkourNeedsUpgrading() {
        if (super.getConfig().contains("Version")) {
            double existingVersion = super.getConfig().getDouble("Version");
            double currentVersion = Double.parseDouble(this.getDescription().getVersion());
            return existingVersion < currentVersion;
        }
        return false;
    }
}

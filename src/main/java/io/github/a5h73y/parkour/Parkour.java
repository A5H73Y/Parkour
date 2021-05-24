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
import io.github.a5h73y.parkour.manager.QuestionManager;
import io.github.a5h73y.parkour.manager.ScoreboardManager;
import io.github.a5h73y.parkour.manager.SoundsManager;
import io.github.a5h73y.parkour.other.Backup;
import io.github.a5h73y.parkour.other.CommandUsage;
import io.github.a5h73y.parkour.other.ParkourUpdater;
import io.github.a5h73y.parkour.plugin.AacApi;
import io.github.a5h73y.parkour.plugin.BountifulApi;
import io.github.a5h73y.parkour.plugin.EconomyApi;
import io.github.a5h73y.parkour.plugin.PlaceholderApi;
import io.github.a5h73y.parkour.type.challenge.ChallengeManager;
import io.github.a5h73y.parkour.type.checkpoint.CheckpointManager;
import io.github.a5h73y.parkour.type.course.CourseInfo;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import io.github.g00fy2.versioncompare.Version;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Parkour extends JavaPlugin {

    public static final String PLUGIN_NAME = "parkour";

    private static final int BSTATS_ID = 1181;
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
    private SoundsManager soundsManager;

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
            upgradeParkour();
            return;
        }

        registerManagers();
        registerCommands();
        registerEvents();

        setupPlugins();

        getLogger().info("Enabled Parkour v" + getDescription().getVersion());
        submitAnalytics();
        checkForUpdates();
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
        Bukkit.getScheduler().cancelTasks(this);
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
    @NotNull
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
        getConfig().save();
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

    public SoundsManager getSoundsManager() {
        return soundsManager;
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
        new AacApi();
    }

    private void registerManagers() {
        registerEssentialManagers();
        scoreboardManager = new ScoreboardManager(this);
        challengeManager = new ChallengeManager(this);
        questionManager = new QuestionManager(this);
        courseManager = new CourseManager(this);
        checkpointManager = new CheckpointManager(this);
        parkourKitManager = new ParkourKitManager(this);
        lobbyManager = new LobbyManager(this);
        playerManager = new PlayerManager(this);
        guiManager = new ParkourGuiManager(this);
        soundsManager = new SoundsManager(this);
        database.recreateAllCourses(false);
    }

    private void registerCommands() {
        getCommand(PLUGIN_NAME).setExecutor(new ParkourCommands(this));
        getCommand("paconsole").setExecutor(new ParkourConsoleCommands(this));

        if (this.getConfig().getBoolean("Other.UseAutoTabCompletion")) {
            getCommand(PLUGIN_NAME).setTabCompleter(new ParkourAutoTabCompleter(this));
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

    /**
     * Check to see if a newer version exists on Spigot.
     */
    private void checkForUpdates() {
        if (getConfig().getBoolean("Other.CheckForUpdates")) {
            new ParkourUpdater(this, SPIGOT_PLUGIN_ID).checkForUpdateAsync();
        }
    }

    /**
     * Check to see if the Parkour needs to upgrade.
     * @return parkour needs to upgrade
     */
    private boolean parkourNeedsUpgrading() {
        if (super.getConfig().contains("Version")) {
            Version existingVersion = new Version(super.getConfig().getString("Version"));
            return existingVersion.isLowerThan(this.getDescription().getVersion());
        }
        return false;
    }

    private void upgradeParkour() {
        CompletableFuture.supplyAsync(() -> new ParkourUpgrader(this).getAsBoolean())
                .thenAccept(success -> {
                    if (success) {
                        onEnable();
                    }
                });
    }

    /**
     * Submit bStats analytics.
     * Can be disabled through the bStats config.yml.
     */
    private void submitAnalytics() {
        Metrics metrics = new Metrics(this, BSTATS_ID);
        metrics.addCustomChart(new SimplePie("number_of_courses", () ->
                String.valueOf(CourseInfo.getAllCourseNames().size())));
        metrics.addCustomChart(new SingleLineChart("parkour_players", () ->
                getPlayerManager().getNumberOfParkourPlayer()));
    }
}

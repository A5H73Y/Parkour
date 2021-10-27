package io.github.a5h73y.parkour;

import com.google.gson.GsonBuilder;
import io.github.a5h73y.parkour.commands.CommandUsage;
import io.github.a5h73y.parkour.commands.ParkourAutoTabCompleter;
import io.github.a5h73y.parkour.commands.ParkourCommands;
import io.github.a5h73y.parkour.commands.ParkourConsoleCommands;
import io.github.a5h73y.parkour.configuration.ConfigManager;
import io.github.a5h73y.parkour.configuration.impl.DefaultConfig;
import io.github.a5h73y.parkour.database.DatabaseManager;
import io.github.a5h73y.parkour.gui.ParkourGuiManager;
import io.github.a5h73y.parkour.listener.BlockListener;
import io.github.a5h73y.parkour.listener.ChatListener;
import io.github.a5h73y.parkour.listener.PlayerInteractListener;
import io.github.a5h73y.parkour.listener.PlayerListener;
import io.github.a5h73y.parkour.listener.PlayerMoveListener;
import io.github.a5h73y.parkour.listener.SignListener;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Backup;
import io.github.a5h73y.parkour.other.ParkourUpdater;
import io.github.a5h73y.parkour.plugin.BountifulApi;
import io.github.a5h73y.parkour.plugin.EconomyApi;
import io.github.a5h73y.parkour.plugin.PlaceholderApi;
import io.github.a5h73y.parkour.type.Teardownable;
import io.github.a5h73y.parkour.type.challenge.ChallengeManager;
import io.github.a5h73y.parkour.type.checkpoint.CheckpointManager;
import io.github.a5h73y.parkour.type.course.CourseManager;
import io.github.a5h73y.parkour.type.course.autostart.AutoStartConfig;
import io.github.a5h73y.parkour.type.course.autostart.AutoStartManager;
import io.github.a5h73y.parkour.type.kit.ParkourKitConfig;
import io.github.a5h73y.parkour.type.kit.ParkourKitManager;
import io.github.a5h73y.parkour.type.lobby.LobbyConfig;
import io.github.a5h73y.parkour.type.lobby.LobbyManager;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.type.player.PlayerManager;
import io.github.a5h73y.parkour.type.player.quiet.QuietModeManager;
import io.github.a5h73y.parkour.type.player.rank.ParkourRankManager;
import io.github.a5h73y.parkour.type.player.scoreboard.ScoreboardManager;
import io.github.a5h73y.parkour.type.question.QuestionManager;
import io.github.a5h73y.parkour.type.sounds.SoundsManager;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.g00fy2.versioncompare.Version;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Parkour extends JavaPlugin {

    public static final String PLUGIN_NAME = "parkour";

    private static final int BSTATS_ID = 1181;
    private static final int SPIGOT_PLUGIN_ID = 23685;
    private static Parkour instance;

    private AutoStartManager autoStartManager;
    private ChallengeManager challengeManager;
    private CheckpointManager checkpointManager;
    private ConfigManager configManager;
    private CourseManager courseManager;
    private DatabaseManager databaseManager;
    private LobbyManager lobbyManager;
    private PlayerManager playerManager;
    private ParkourGuiManager guiManager;
    private ParkourKitManager parkourKitManager;
    private ParkourRankManager parkourRankManager;
    private QuestionManager questionManager;
    private QuietModeManager quietModeManager;
    private ScoreboardManager scoreboardManager;
    private SoundsManager soundsManager;

    private List<CommandUsage> commandUsages;
    private final List<AbstractPluginReceiver> managers = new ArrayList<>();

    private BountifulApi bountifulApi;
    private EconomyApi economyApi;
    private PlaceholderApi placeholderApi;

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
        if (getParkourConfig().getBoolean("Other.OnServerShutdown.BackupFiles")) {
            Backup.backupNow();
        }

        teardownManagers();
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
    public FileConfiguration getConfig() {
        throw new UnsupportedOperationException("Use getDefaultConfig()");
    }

    public DefaultConfig getParkourConfig() {
        return configManager.getDefaultConfig();
    }

    /**
     * Get the default config.yml file.
     *
     * @return {@link DefaultConfig}
     */
    public static DefaultConfig getDefaultConfig() {
        return instance.getParkourConfig();
    }

    public static ParkourKitConfig getParkourKitConfig() {
        return instance.getConfigManager().getParkourKitConfig();
    }

    public static LobbyConfig getLobbyConfig() {
        return instance.getConfigManager().getLobbyConfig();
    }

    public static AutoStartConfig getAutoStartConfig() {
        return instance.getConfigManager().getAutoStartConfig();
    }

    public static PlayerConfig getPlayerConfig(OfflinePlayer player) {
        return PlayerConfig.getConfig(player);
    }

    /**
     * Save the Default config.
     * Overrides the default saveConfig() method.
     */
    @Override
    public void saveConfig() {
        getParkourConfig().write();
    }

    public void registerEssentialManagers() {
        configManager = new ConfigManager(this.getDataFolder());
        databaseManager = (DatabaseManager) registerManager(new DatabaseManager(this));
    }

    private void registerManagers() {
        registerEssentialManagers();
        scoreboardManager = (ScoreboardManager) registerManager(new ScoreboardManager(this));
        challengeManager = (ChallengeManager) registerManager(new ChallengeManager(this));
        questionManager = (QuestionManager) registerManager(new QuestionManager(this));
        parkourKitManager = (ParkourKitManager) registerManager(new ParkourKitManager(this));
        courseManager = (CourseManager) registerManager(new CourseManager(this));
        checkpointManager = (CheckpointManager) registerManager(new CheckpointManager(this));
        lobbyManager = (LobbyManager) registerManager(new LobbyManager(this));
        parkourRankManager = (ParkourRankManager) registerManager(new ParkourRankManager(this));
        playerManager = (PlayerManager) registerManager(new PlayerManager(this));
        guiManager = (ParkourGuiManager) registerManager(new ParkourGuiManager(this));
        soundsManager = (SoundsManager) registerManager(new SoundsManager(this));
        autoStartManager = (AutoStartManager) registerManager(new AutoStartManager(this));
        quietModeManager = (QuietModeManager) registerManager(new QuietModeManager(this));
        databaseManager.recreateAllCourses(false);
    }

    private AbstractPluginReceiver registerManager(AbstractPluginReceiver manager) {
        this.managers.add(manager);
        return manager;
    }

    private void setupPlugins() {
        bountifulApi = new BountifulApi(this);
        economyApi = new EconomyApi(this);
        placeholderApi = new PlaceholderApi(this);
    }

    private void registerCommands() {
        getCommand(PLUGIN_NAME).setExecutor(new ParkourCommands(this));
        getCommand("paconsole").setExecutor(new ParkourConsoleCommands(this));

        if (this.getParkourConfig().getBoolean("Other.UseAutoTabCompletion")) {
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
        if (getParkourConfig().getBoolean("Other.CheckForUpdates")) {
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
        if (new ParkourUpgrader(this).beginUpgrade()) {
            onEnable();
        }
    }

    private void teardownManagers() {
        managers.stream()
                .filter(Teardownable.class::isInstance)
                .map(Teardownable.class::cast)
                .forEach(Teardownable::teardown);
    }

    /**
     * Submit bStats analytics.
     * Can be disabled through the bStats config.yml.
     */
    private void submitAnalytics() {
        Metrics metrics = new Metrics(this, BSTATS_ID);
        metrics.addCustomChart(new SimplePie("number_of_courses", () ->
                String.valueOf(courseManager.getCourseNames().size())));
        metrics.addCustomChart(new SingleLineChart("parkour_players", () ->
                getPlayerManager().getNumberOfParkourPlayer()));
    }

    public AutoStartManager getAutoStartManager() {
        return autoStartManager;
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

    public CheckpointManager getCheckpointManager() {
        return checkpointManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CourseManager getCourseManager() {
        return courseManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ParkourGuiManager getGuiManager() {
        return guiManager;
    }

    public ParkourKitManager getParkourKitManager() {
        return parkourKitManager;
    }

    public ParkourRankManager getParkourRankManager() {
        return parkourRankManager;
    }

    public QuestionManager getQuestionManager() {
        return questionManager;
    }

    public QuietModeManager getQuietModeManager() {
        return quietModeManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public SoundsManager getSoundsManager() {
        return soundsManager;
    }

    public List<CommandUsage> getCommandUsages() {
        return commandUsages;
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
}

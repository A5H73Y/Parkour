package io.github.a5h73y.parkour;

import io.github.a5h73y.parkour.commands.ParkourAutoTabCompleter;
import io.github.a5h73y.parkour.commands.ParkourCommands;
import io.github.a5h73y.parkour.commands.ParkourConsoleCommands;
import io.github.a5h73y.parkour.configuration.ConfigManager;
import io.github.a5h73y.parkour.configuration.impl.DefaultConfig;
import io.github.a5h73y.parkour.database.DatabaseManager;
import io.github.a5h73y.parkour.gui.ParkourGuiManager;
import io.github.a5h73y.parkour.listener.BreakPlaceListener;
import io.github.a5h73y.parkour.listener.ChatListener;
import io.github.a5h73y.parkour.listener.PlayerInteractListener;
import io.github.a5h73y.parkour.listener.PlayerListener;
import io.github.a5h73y.parkour.listener.PlayerMoveListener;
import io.github.a5h73y.parkour.listener.SignListener;
import io.github.a5h73y.parkour.listener.interact.AutoStartListener;
import io.github.a5h73y.parkour.listener.move.ParkourBlockListener;
import io.github.a5h73y.parkour.listener.move.VehicleMoveListener;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.ParkourUpdater;
import io.github.a5h73y.parkour.other.PluginBackupUtil;
import io.github.a5h73y.parkour.plugin.BountifulApi;
import io.github.a5h73y.parkour.plugin.EconomyApi;
import io.github.a5h73y.parkour.plugin.PermissionVault;
import io.github.a5h73y.parkour.plugin.PlaceholderApi;
import io.github.a5h73y.parkour.type.Initializable;
import io.github.a5h73y.parkour.type.Teardownable;
import io.github.a5h73y.parkour.type.admin.AdministrationManager;
import io.github.a5h73y.parkour.type.challenge.ChallengeManager;
import io.github.a5h73y.parkour.type.checkpoint.CheckpointManager;
import io.github.a5h73y.parkour.type.course.CourseManager;
import io.github.a5h73y.parkour.type.course.CourseSettingsManager;
import io.github.a5h73y.parkour.type.course.autostart.AutoStartConfig;
import io.github.a5h73y.parkour.type.course.autostart.AutoStartManager;
import io.github.a5h73y.parkour.type.kit.ParkourKitConfig;
import io.github.a5h73y.parkour.type.kit.ParkourKitManager;
import io.github.a5h73y.parkour.type.lobby.LobbyConfig;
import io.github.a5h73y.parkour.type.lobby.LobbyManager;
import io.github.a5h73y.parkour.type.player.PlayerManager;
import io.github.a5h73y.parkour.type.player.quiet.QuietModeManager;
import io.github.a5h73y.parkour.type.player.rank.ParkourRankManager;
import io.github.a5h73y.parkour.type.player.scoreboard.ScoreboardManager;
import io.github.a5h73y.parkour.type.player.session.ParkourSessionManager;
import io.github.a5h73y.parkour.type.question.QuestionManager;
import io.github.a5h73y.parkour.type.sounds.SoundsManager;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.g00fy2.versioncompare.Version;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
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
    private CourseSettingsManager courseSettingsManager;
    private DatabaseManager databaseManager;
    private LobbyManager lobbyManager;
    private ParkourSessionManager parkourSessionManager;
    private PlayerManager playerManager;
    private ParkourGuiManager guiManager;
    private ParkourKitManager parkourKitManager;
    private ParkourRankManager parkourRankManager;
    private QuestionManager questionManager;
    private QuietModeManager quietModeManager;
    private ScoreboardManager scoreboardManager;
    private SoundsManager soundsManager;
    private AdministrationManager administrationManager;

    private ParkourCommands parkourCommands;
    private final List<AbstractPluginReceiver> managers = new ArrayList<>();

    private BountifulApi bountifulApi;
    private EconomyApi economyApi;
    private PermissionVault permissionVault;
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
            PluginBackupUtil.backupNow();
        }

        teardownManagers();
        Bukkit.getScheduler().cancelTasks(this);
        PluginUtils.log("Disabled Parkour v" + getDescription().getVersion());
        instance = null;
    }

    /**
     * Get the Default config.
     * Overrides the default getConfig() method.
     * @deprecated use getDefaultConfig()
     *
     * @return default config
     */
    @Deprecated
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
        courseSettingsManager = (CourseSettingsManager) registerManager(new CourseSettingsManager(this));
        courseManager = (CourseManager) registerManager(new CourseManager(this));
        checkpointManager = (CheckpointManager) registerManager(new CheckpointManager(this));
        lobbyManager = (LobbyManager) registerManager(new LobbyManager(this));
        parkourRankManager = (ParkourRankManager) registerManager(new ParkourRankManager(this));
        soundsManager = (SoundsManager) registerManager(new SoundsManager(this));
        parkourSessionManager = (ParkourSessionManager) registerManager(new ParkourSessionManager(this));
        playerManager = (PlayerManager) registerManager(new PlayerManager(this));
        guiManager = (ParkourGuiManager) registerManager(new ParkourGuiManager(this));
        autoStartManager = (AutoStartManager) registerManager(new AutoStartManager(this));
        quietModeManager = (QuietModeManager) registerManager(new QuietModeManager(this));
        administrationManager = (AdministrationManager) registerManager(new AdministrationManager(this));

        setupPlugins();
        initializeManagers();
    }

    private AbstractPluginReceiver registerManager(AbstractPluginReceiver manager) {
        this.managers.add(manager);
        return manager;
    }

    private void setupPlugins() {
        bountifulApi = new BountifulApi(this);
        economyApi = new EconomyApi(this);
        permissionVault = new PermissionVault(this);
        placeholderApi = new PlaceholderApi(this);
    }

    private void registerCommands() {
        parkourCommands = new ParkourCommands(this);
        PluginCommand parkourCommand = getCommand(PLUGIN_NAME);
        parkourCommand.setExecutor(parkourCommands);
        if (this.getParkourConfig().getBoolean("Other.UseAutoTabCompletion")) {
            parkourCommand.setTabCompleter(new ParkourAutoTabCompleter(this));
        }

        getCommand("paconsole").setExecutor(new ParkourConsoleCommands(this));
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BreakPlaceListener(this), this);
        pluginManager.registerEvents(new ChatListener(this), this);
        pluginManager.registerEvents(new PlayerInteractListener(this), this);
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new PlayerMoveListener(this), this);
        pluginManager.registerEvents(new SignListener(this), this);

        if (getDefaultConfig().getBoolean("AutoStart.Enabled")) {
            pluginManager.registerEvents(new AutoStartListener(this), this);
        }
        if (getDefaultConfig().getBoolean("OnCourse.ParkourKit.Enabled")) {
            pluginManager.registerEvents(new ParkourBlockListener(this), this);
        }
        if (getDefaultConfig().getBoolean("OnCourse.ParkourKit.IncludeVehicles")) {
            pluginManager.registerEvents(new VehicleMoveListener(this), this);
        }
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
        // easier to create the new config system, then update them.
        registerEssentialManagers();
        new ParkourUpgrader(this).beginUpgrade();
    }

    private void initializeManagers() {
        managers.stream()
                .filter(Initializable.class::isInstance)
                .map(Initializable.class::cast)
                .sorted(Comparator.comparing(Initializable::getInitializeSequence))
                .forEach(Initializable::initialize);
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
                getParkourSessionManager().getNumberOfParkourPlayers()));
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

    public CourseSettingsManager getCourseSettingsManager() {
        return courseSettingsManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public ParkourSessionManager getParkourSessionManager() {
        return parkourSessionManager;
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

    public AdministrationManager getAdministrationManager() {
        return administrationManager;
    }

    public ParkourCommands getParkourCommands() {
        return parkourCommands;
    }

    public BountifulApi getBountifulApi() {
        return bountifulApi;
    }

    public EconomyApi getEconomyApi() {
        return economyApi;
    }

    public PermissionVault getPermissionVault() {
        return permissionVault;
    }

    public PlaceholderApi getPlaceholderApi() {
        return placeholderApi;
    }
}

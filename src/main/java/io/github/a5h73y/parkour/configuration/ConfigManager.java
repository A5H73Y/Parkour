package io.github.a5h73y.parkour.configuration;

import io.github.a5h73y.parkour.configuration.impl.DefaultConfig;
import io.github.a5h73y.parkour.configuration.impl.StringsConfig;
import io.github.a5h73y.parkour.configuration.serializable.CourseSerializable;
import io.github.a5h73y.parkour.configuration.serializable.ItemStackSerializable;
import io.github.a5h73y.parkour.configuration.serializable.LocationSerializable;
import io.github.a5h73y.parkour.type.course.autostart.AutoStartConfig;
import io.github.a5h73y.parkour.type.kit.ParkourKitConfig;
import io.github.a5h73y.parkour.type.lobby.LobbyConfig;
import io.github.a5h73y.parkour.type.player.completion.CourseCompletionConfig;
import io.github.a5h73y.parkour.type.player.quiet.QuietModeConfig;
import io.github.a5h73y.parkour.type.player.rank.ParkourRankConfig;
import io.github.a5h73y.parkour.utility.PluginUtils;
import java.io.File;
import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.internal.serialize.LightningSerializer;

/**
 * Parkour Configuration Manager.
 * Manages and stores references to each of the available Config files.
 */
public class ConfigManager {

	private final File dataFolder;

	// core
	private final DefaultConfig defaultConfig;
	private final StringsConfig stringsConfig;

	// others
	private final ParkourKitConfig parkourKitConfig;
	private final ParkourRankConfig parkourRankConfig;
	private final AutoStartConfig autoStartConfig;
	private final CourseCompletionConfig courseCompletionsConfig;
	private final QuietModeConfig quietModeConfig;
	private final LobbyConfig lobbyConfig;

	// directories
	private final File playersDir;
	private final File sessionsDir;
	private final File coursesDir;
	private final File otherDir;

	/**
	 * Initialise the Config Manager.
	 * Will invoke setup for each available config type.
	 *
	 * @param dataFolder where to store the configs
	 */
	public ConfigManager(File dataFolder) {
		this.dataFolder = dataFolder;
		playersDir = new File(dataFolder, "players");
		sessionsDir = new File(dataFolder, "sessions");
		coursesDir = new File(dataFolder, "courses");
		otherDir = new File(dataFolder, "other");
		createParkourFolders();

		defaultConfig = new DefaultConfig(new File(dataFolder, "config.yml"));
		stringsConfig = new StringsConfig(new File(dataFolder, "strings.yml"));

		// everything else
		parkourKitConfig = new ParkourKitConfig(new File(otherDir, "parkour-kits.yml"));
		parkourRankConfig = new ParkourRankConfig(new File(otherDir, "parkour-ranks.yml"));
		autoStartConfig = new AutoStartConfig(new File(otherDir, "auto-starts.yml"));
		courseCompletionsConfig = new CourseCompletionConfig(new File(otherDir, "course-completions.yml"));
		quietModeConfig = new QuietModeConfig(new File(otherDir, "quiet-players.yml"));
		lobbyConfig = new LobbyConfig(new File(otherDir, "parkour-lobbies.yml"));

		LightningSerializer.registerSerializable(new ItemStackSerializable());
		LightningSerializer.registerSerializable(new LocationSerializable());
		LightningSerializer.registerSerializable(new CourseSerializable());
	}

	/**
	 * Reload each of the configuration files.
	 */
	public void reloadConfigs() {
		for (FlatFile configs : getAllConfigs()) {
			configs.forceReload();
		}
	}

	public DefaultConfig getDefaultConfig() {
		return defaultConfig;
	}

	public StringsConfig getStringsConfig() {
		return stringsConfig;
	}

	public ParkourKitConfig getParkourKitConfig() {
		return parkourKitConfig;
	}

	public ParkourRankConfig getParkourRankConfig() {
		return parkourRankConfig;
	}

	public AutoStartConfig getAutoStartConfig() {
		return autoStartConfig;
	}

	public CourseCompletionConfig getCourseCompletionsConfig() {
		return courseCompletionsConfig;
	}

	public QuietModeConfig getQuietModeConfig() {
		return quietModeConfig;
	}

	public LobbyConfig getLobbyConfig() {
		return lobbyConfig;
	}

	public File getPlayersDir() {
		return playersDir;
	}

	public File getSessionsDir() {
		return sessionsDir;
	}

	public File getCoursesDir() {
		return coursesDir;
	}

	public File getOtherDir() {
		return otherDir;
	}

	private void createParkourFolders() {
		File[] parkourFolders = {dataFolder, playersDir, sessionsDir, coursesDir, otherDir};

		for (File folder : parkourFolders) {
			if (!folder.exists() && folder.mkdirs()) {
				PluginUtils.log("Created folder: " + folder.getName());
			}
		}
	}

	private FlatFile[] getAllConfigs() {
		return new FlatFile[]{
				defaultConfig,
				stringsConfig,
				parkourKitConfig,
				parkourRankConfig,
				autoStartConfig,
				courseCompletionsConfig,
				quietModeConfig,
				lobbyConfig
		};
	}
}

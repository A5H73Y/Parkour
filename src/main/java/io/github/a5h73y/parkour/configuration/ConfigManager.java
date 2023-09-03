package io.github.a5h73y.parkour.configuration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.internal.serialize.SimplixSerializer;
import io.github.a5h73y.parkour.configuration.impl.DefaultConfig;
import io.github.a5h73y.parkour.configuration.impl.StringsConfig;
import io.github.a5h73y.parkour.configuration.serializable.CourseSerializable;
import io.github.a5h73y.parkour.configuration.serializable.ItemStackArraySerializable;
import io.github.a5h73y.parkour.configuration.serializable.ItemStackSerializable;
import io.github.a5h73y.parkour.configuration.serializable.LocationSerializable;
import io.github.a5h73y.parkour.configuration.serializable.ParkourSessionSerializable;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.course.autostart.AutoStartConfig;
import io.github.a5h73y.parkour.type.kit.ParkourKitConfig;
import io.github.a5h73y.parkour.type.lobby.LobbyConfig;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.type.player.completion.CourseCompletionConfig;
import io.github.a5h73y.parkour.type.player.quiet.QuietModeConfig;
import io.github.a5h73y.parkour.type.player.rank.ParkourRankConfig;
import io.github.a5h73y.parkour.utility.PluginUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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

	// cache
	private final Map<UUID, PlayerConfig> playerConfigCache;
	private final Map<String, CourseConfig> courseConfigCache;

	// directories
	private final File playersDir;
	private final File parkourSessionsDir;
	private final File coursesDir;
	private final File otherDir;

	// serializers
	private final ItemStackSerializable itemStackSerializable = new ItemStackSerializable();

	/**
	 * Initialise the Config Manager.
	 * Will invoke setup for each available config type.
	 *
	 * @param dataFolder where to store the configs
	 */
	public ConfigManager(File dataFolder) {
		this.dataFolder = dataFolder;
		playersDir = new File(dataFolder, "players");
		parkourSessionsDir = new File(dataFolder, "sessions");
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

		this.playerConfigCache = new HashMap<>();
		this.courseConfigCache = new HashMap<>();

		SimplixSerializer.registerSerializable(itemStackSerializable);
		SimplixSerializer.registerSerializable(new ItemStackArraySerializable());
		SimplixSerializer.registerSerializable(new LocationSerializable());
		SimplixSerializer.registerSerializable(new CourseSerializable());
		SimplixSerializer.registerSerializable(new ParkourSessionSerializable());
	}

	/**
	 * Get the Player's JSON config file.
	 *
	 * @param player offline player
	 * @return player's config
	 */
	@NotNull
	public PlayerConfig getPlayerConfig(@NotNull OfflinePlayer player) {
		UUID key = player.getUniqueId();
		return playerConfigCache.computeIfAbsent(key, id -> PlayerConfig.getConfig(player));
	}

	/**
	 * Get the Course's JSON config file.
	 *
	 * @param courseName course name
	 * @return course's config
	 */
	@NotNull
	public CourseConfig getCourseConfig(@NotNull String courseName) {
		String key = courseName.toLowerCase();
		return courseConfigCache.computeIfAbsent(key, CourseConfig::getConfig);
	}

	/**
	 * Find every single Player UUID known to Parkour.
	 */
	public List<String> getAllPlayerUuids() {
		return findEveryJsonInDir(getPlayersDir().toURI());
	}

	/**
	 * Find every single Course name known to Parkour.
	 */
	public List<String> getAllCourseNames() {
		return findEveryJsonInDir(getCoursesDir().toURI());
	}

	private List<String> findEveryJsonInDir(URI uri) {
		List<String> results = new ArrayList<>();
		try (Stream<Path> paths = Files.walk(Paths.get(uri), 1)) {
			results = paths.filter(Files::isRegularFile)
					.map(p -> p.getFileName().toString().toLowerCase())
					.filter(path -> path.endsWith(".json"))
					.map(path -> path.split("\\.")[0])
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return results;
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

	public File getParkourSessionsDir() {
		return parkourSessionsDir;
	}

	public File getCoursesDir() {
		return coursesDir;
	}

	public File getOtherDir() {
		return otherDir;
	}

	public ItemStackSerializable getItemStackSerializable() {
		return itemStackSerializable;
	}

	private void createParkourFolders() {
		File[] parkourFolders = {dataFolder, playersDir, parkourSessionsDir, coursesDir, otherDir};

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

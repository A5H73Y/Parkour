package io.github.a5h73y.parkour.type.player.session;

import static io.github.a5h73y.parkour.other.ParkourConstants.TEST_MODE;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.ParkourConstants;
import io.github.a5h73y.parkour.type.Initializable;
import io.github.a5h73y.parkour.type.Teardownable;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParkourSessionManager extends AbstractPluginReceiver implements Teardownable, Initializable {

	private final Map<UUID, ParkourSession> parkourPlayers = new WeakHashMap<>();

	public ParkourSessionManager(final Parkour parkour) {
		super(parkour);
	}

	@Override
	public int getInitializeSequence() {
		return 0;
	}

	@Override
	public void initialize() {
		populateParkourPlayers();
	}

	/**
	 * Add the player and their session to parkour players.
	 *
	 * @param player target player
	 * @param session ParkourSession
	 */
	public ParkourSession addPlayer(Player player, ParkourSession session) {
		parkourPlayers.put(player.getUniqueId(), session);
		return session;
	}

	/**
	 * Remove the player and their session from the parkour players.
	 *
	 * @param player target player
	 */
	public void removePlayer(Player player) {
		if (isPlaying(player)) {
			parkourPlayers.remove(player.getUniqueId());
		}
	}

	public Map<UUID, ParkourSession> getParkourPlayers() {
		return parkourPlayers;
	}


	/**
	 * Get the Player's {@link ParkourSession}.
	 *
	 * @param player requesting player
	 * @return player's ParkourSession
	 */
	@Nullable
	public ParkourSession getParkourSession(Player player) {
		return parkourPlayers.get(player.getUniqueId());
	}

	/**
	 * Check whether a Player has a ParkourSession.
	 *
	 * @param player requesting player
	 * @return player session exists
	 */
	public boolean isPlaying(Player player) {
		return parkourPlayers.containsKey(player.getUniqueId());
	}

	public boolean isPlayingParkourCourse(Player player) {
		ParkourSession session = getParkourSession(player);
		return session != null && !isTestModeSession(session);
	}

	/**
	 * Check whether the Player is in TestMode.
	 * To be treated differently to a regular Course ParkourSession.
	 *
	 * @param player target player
	 * @return player in TestMode
	 */
	public boolean isPlayerInTestMode(Player player) {
		ParkourSession session = getParkourSession(player);
		return session != null && isTestModeSession(session);
	}

	/**
	 * Get the number of online Parkour Players.
	 *
	 * @return number of parkour players
	 */
	public int getNumberOfParkourPlayers() {
		return parkourPlayers.size();
	}

	/**
	 * Get Online Parkour Players.
	 * Find all the currently online players that are on a Parkour Course.
	 *
	 * @return parkour players
	 */
	public List<Player> getOnlineParkourPlayers() {
		return Bukkit.getServer().getOnlinePlayers().stream()
				.filter(this::isPlaying)
				.collect(Collectors.toList());
	}

	/**
	 * Get Number of Players on a Course.
	 *
	 * @param courseName course name
	 * @return number of players
	 */
	public int getNumberOfPlayersOnCourse(@NotNull String courseName) {
		return (int) parkourPlayers.values().stream()
				.filter(parkourSession -> parkourSession.getCourseName().equalsIgnoreCase(courseName))
				.count();
	}

	/**
	 * Get the Player names currently on a Course.
	 *
	 * @param courseName course name
	 * @return list of player names
	 */
	public List<String> getPlayerNamesOnCourse(@NotNull String courseName) {
		return parkourPlayers.entrySet().stream()
				.filter(session -> session.getValue().getCourseName().equals(courseName))
				.map(session -> Bukkit.getPlayer(session.getKey()))
				.filter(Objects::nonNull)
				.map(HumanEntity::getName)
				.collect(Collectors.toList());
	}

	/**
	 * Teardown all active Parkour Players.
	 * Remove all in-memory references to each player, persisting any data to a file.
	 */
	@Override
	public void teardown() {
		for (UUID uuid : parkourPlayers.keySet()) {
			Player player = Bukkit.getPlayer(uuid);
			stashParkourSession(player, false);
		}
		parkourPlayers.clear();
	}

	/**
	 * Stash the Player's ParkourSession.
	 * Persist the ParkourSession to a file under the Player's UUID.
	 * Mark the current time accumulated, for the time difference to be recalculated when the Player rejoins.
	 *
	 * @param player player
	 */
	public void stashParkourSession(Player player, boolean removePlaying) {
		ParkourSession session = getParkourSession(player);
		if (session != null && !session.getCourseName().equals(TEST_MODE)) {
			session.markTimeAccumulated();
			PlayerConfig.getConfig(player).setExistingSessionCourseName(session.getCourseName());
		}
		createParkourSessionFile(player);
		if (removePlaying) {
			parkourPlayers.remove(player);
		}
	}

	/**
	 * Read the ParkourSession from the Player's UUID.
	 * Find the matching file and deserialize the data.
	 *
	 * @param player player
	 * @return ParkourSession file
	 */
	private ParkourSession readParkourSession(Player player, String courseName) {
		ParkourSession session = null;
		File sessionFile = PlayerConfig.getPlayerSessionFile(player, courseName);

		if (sessionFile.exists()) {
			try (
					FileInputStream fout = new FileInputStream(sessionFile);
					ObjectInputStream oos = new ObjectInputStream(fout)
			) {
				session = (ParkourSession) oos.readObject();
			} catch (IOException | ClassNotFoundException e) {
				PluginUtils.log("Player's session couldn't be loaded: " + e.getMessage(), 2);
				e.printStackTrace();
			}
		}
		return session;
	}

	/**
	 * Generate path for Player's Session file.
	 *
	 * @param player player
	 * @return session path
	 */
	@Nullable
	public File generatePlayerSessionPath(Player player, ParkourSession session) {
		File playerPath = null;

		if (session != null) {
			playerPath = PlayerConfig.getPlayerSessionFile(player, session.getCourseName());
		}

		return playerPath;
	}

	/**
	 * Validate if their session is still valid.
	 * Try to get their existing session and check the name matches.
	 *
	 * @param player player
	 * @param course course
	 * @return player can load ParkourSession
	 */
	public boolean hasValidParkourSessionFile(Player player, Course course) {
		ParkourSession session = readParkourSession(player, course.getName());
		return session != null && session.getCourseName().equals(course.getName());
	}

	private void createParkourSessionFile(Player player) {
		ParkourSession session = getParkourSession(player);
		File sessionFile = generatePlayerSessionPath(player, session);

		if (sessionFile != null) {
			if (!sessionFile.exists()) {
				try {
					sessionFile.getParentFile().mkdirs();
					sessionFile.createNewFile();
				} catch (IOException e) {
					PluginUtils.log("Player's session couldn't be created: " + e.getMessage(), 2);
					e.printStackTrace();
				}
			}

			try (
					FileOutputStream fout = new FileOutputStream(sessionFile);
					ObjectOutputStream oos = new ObjectOutputStream(fout)
			) {
				oos.writeObject(session);
			} catch (IOException e) {
				PluginUtils.log("Player's session couldn't be saved: " + e.getMessage(), 2);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Load the Player's ParkourSession.
	 * Retrieve the ParkourSession for the Player, recalculate the details and continue the Course.
	 *
	 * @param player player
	 * @return loaded ParkourSession
	 */
	public ParkourSession loadParkourSession(Player player, String courseName) {
		ParkourSession session = readParkourSession(player, courseName);

		if (session != null) {
			if (parkour.getCourseManager().doesCourseExist(session.getCourseName())) {
				session.setCourse(parkour.getCourseManager().findCourse(session.getCourseName()));
				session.recalculateTime();
				session.setStartTimer(true);
				addPlayer(player, session);

			} else {
				TranslationUtils.sendTranslation("Error.InvalidSession", player);
				deleteParkourSession(player, courseName);
				parkour.getLobbyManager().justTeleportToDefaultLobby(player);
			}
		}

		return session;
	}

	/**
	 * Delete the Player's ParkourSession.
	 *
	 * @param player player
	 */
	public void deleteParkourSession(OfflinePlayer player, String courseName) {
		File sessionFile = PlayerConfig.getPlayerSessionFile(player, courseName);

		if (sessionFile.exists()) {
			try {
				sessionFile.delete();
			} catch (SecurityException e) {
				PluginUtils.log("Player's session couldn't be deleted: " + e.getMessage(), 2);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Delete all Parkour Sessions for Player.
	 * Individually removes all files from within the folder, then the folder itself.
	 *
	 * @param player player
	 */
	public void deleteParkourSessions(OfflinePlayer player) {
		File playersFolder = new File(parkour.getConfigManager().getSessionsDir()  + File.separator + player.getUniqueId());
		if (Files.notExists(playersFolder.toPath())) {
			return;
		}

		try (Stream<Path> paths = Files.walk(playersFolder.toPath())) {
			paths.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(File::delete);
		} catch (SecurityException | IOException e) {
			PluginUtils.log("Player's session couldn't be deleted: " + e.getMessage(), 2);
			e.printStackTrace();
		}
	}

	private boolean isTestModeSession(@NotNull ParkourSession session) {
		return ParkourConstants.TEST_MODE.equals(session.getCourse().getName());
	}

	/**
	 * Populate Parkour Players.
	 * As part of a server reload, there would be online players who need their Parkour session restored.
	 */
	private void populateParkourPlayers() {
		boolean kickPlayer = parkour.getParkourConfig().getBoolean("OnServerRestart.KickPlayerFromCourse");

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (!PlayerConfig.hasPlayerConfig(onlinePlayer)) {
				continue;
			}

			PlayerConfig playerConfig = PlayerConfig.getConfig(onlinePlayer);
			if (!playerConfig.hasExistingSessionCourseName()) {
				continue;
			}

			ParkourSession session = loadParkourSession(onlinePlayer,
					playerConfig.getExistingSessionCourseName());

			if (!isPlaying(onlinePlayer)) {
				continue;
			}

			if (kickPlayer) {
				parkour.getPlayerManager().leaveCourse(onlinePlayer);
				continue;
			}

			parkour.getPlayerManager().setupParkourMode(onlinePlayer);
			parkour.getScoreboardManager().addScoreboard(onlinePlayer, session);

			String currentCourse = getParkourSession(onlinePlayer).getCourse().getName();
			TranslationUtils.sendValueTranslation("Parkour.Continue", currentCourse, onlinePlayer);
		}
	}
}

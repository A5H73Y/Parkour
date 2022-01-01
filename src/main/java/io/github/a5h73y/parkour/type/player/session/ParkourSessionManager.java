package io.github.a5h73y.parkour.type.player.session;

import static io.github.a5h73y.parkour.other.ParkourConstants.TEST_MODE;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.ParkourConstants;
import io.github.a5h73y.parkour.type.Initializable;
import io.github.a5h73y.parkour.type.Teardownable;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
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
			saveParkourSession(player, false);
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
	public void saveParkourSession(Player player, boolean removePlaying) {
		ParkourSession session = getParkourSession(player);

		if (session != null && !session.getCourseName().equals(TEST_MODE)) {
			session.markTimeAccumulated();
			PlayerConfig.getConfig(player).setExistingSessionCourseName(session.getCourseName());
			ParkourSessionConfig.getConfig(player, session.getCourseName()).saveParkourSession(session);
		}

		if (removePlaying) {
			removePlayer(player);
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
		ParkourSession result = null;

		if (ParkourSessionConfig.hasParkourSessionConfig(player, courseName)) {
			ParkourSessionConfig config = ParkourSessionConfig.getConfig(player, courseName);
			ParkourSession session = config.getParkourSession();

			// course is populated by deserializing
			if (session != null && session.getCourse() != null) {
				session.recalculateTime();
				session.setStartTimer(true);
				addPlayer(player, session);
				result = session;

			} else {
				TranslationUtils.sendTranslation("Error.InvalidSession", player);
				deleteParkourSession(player, courseName);
				parkour.getLobbyManager().justTeleportToDefaultLobby(player);
			}
		}

		return result;
	}

	/**
	 * Delete the Player's ParkourSession.
	 *
	 * @param player player
	 */
	public void deleteParkourSession(OfflinePlayer player, String courseName) {
		ParkourSessionConfig.deleteParkourSessionFile(player, courseName);
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

	public void deleteParkourSessions(OfflinePlayer targetPlayer) {
		ParkourSessionConfig.deleteParkourSessions(targetPlayer);
	}

	public boolean hasValidParkourSessionFile(Player player, Course course) {
		return ParkourSessionConfig.hasParkourSessionConfig(player, course.getName());
	}
}

package io.github.a5h73y.parkour.type.player.session;

import static io.github.a5h73y.parkour.other.ParkourConstants.TEST_MODE;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.CommandProcessor;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.ParkourConstants;
import io.github.a5h73y.parkour.type.Initializable;
import io.github.a5h73y.parkour.type.Teardownable;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parkour Session Manager.
 * Holds reference to Players currently on a Course.
 * Adds actions to perform to the Player whilst on a Course.
 */
public class ParkourSessionManager extends AbstractPluginReceiver implements CommandProcessor, Teardownable, Initializable {

	private final Map<UUID, ParkourSession> parkourPlayers = new WeakHashMap<>();
	private final Set<UUID> hiddenPlayers = new HashSet<>();

	private final Map<String, Consumer<Player>> sessionActions = new HashMap<>();

	public ParkourSessionManager(final Parkour parkour) {
		super(parkour);
		populateSessionActions();
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
	 * Save the Player's ParkourSession.
	 * Persist the ParkourSession to a file under the Player's UUID.
	 * Mark the current time accumulated, for the time difference to be recalculated when the Player rejoins.
	 *
	 * @param player player
	 */
	public void saveParkourSession(Player player, boolean removePlaying) {
		ParkourSession session = getParkourSession(player);

		if (session != null && !session.getCourseName().equals(TEST_MODE)) {
			session.markTimeAccumulated();
			parkour.getConfigManager().getPlayerConfig(player).setExistingSessionCourseName(session.getCourseName());
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

			// course is populated by deserializing
			if (parkour.getCourseManager().doesCourseExist(config.getCourseName())) {
				ParkourSession session = config.getParkourSession();
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

	public void toggleVisibility(Player player) {
		toggleVisibility(player, false);
	}

	/**
	 * Toggle Player's Visibility.
	 * Either hide or show all online players. Override can be applied to
	 * Used when on a Course and want to remove distraction of other Players.
	 *
	 * @param player requesting player
	 */
	public void toggleVisibility(Player player, boolean silent) {
		toggleVisibility(player, hasHiddenPlayers(player), silent);
	}

	private void toggleVisibility(Player player, boolean showPlayers, boolean silent) {
		hideOrShowPlayers(player, showPlayers);

		if (showPlayers) {
			removeHidden(player);
			if (!silent) {
				TranslationUtils.sendTranslation("Event.HideAll1", player);
			}

		} else {
			addHidden(player);
			if (!silent) {
				TranslationUtils.sendTranslation("Event.HideAll2", player);
			}
		}
	}

	public void showVisibility(Player player, boolean silent) {
		toggleVisibility(player, true, silent);
	}

	public void hideVisibility(Player player, boolean silent) {
		toggleVisibility(player, false, silent);
	}

	private void hideOrShowPlayers(Player player, boolean showPlayers) {
		Collection<Player> playerScope = getHideAllTargetPlayers();

		for (Player eachPlayer : playerScope) {
			if (showPlayers) {
				PlayerUtils.showPlayer(player, eachPlayer);
			} else {
				PlayerUtils.hidePlayer(player, eachPlayer);
			}
		}
	}

	private Collection<Player> getHideAllTargetPlayers() {
		if (parkour.getParkourConfig().getBoolean("ParkourTool.HideAll.Global")) {
			return (Collection<Player>) Bukkit.getOnlinePlayers();
		} else {
			return parkour.getParkourSessionManager().getOnlineParkourPlayers();
		}
	}

	/**
	 * Force the Player to be visible to all (unless chosen to hide all).
	 *
	 * @param player target player
	 */
	public void forceVisible(Player player) {
		for (Player eachPlayer : Bukkit.getOnlinePlayers()) {
			if (!hasHiddenPlayers(eachPlayer)) {
				PlayerUtils.showPlayer(eachPlayer, player);
			}
		}
		if (hasHiddenPlayers(player)) {
			hideOrShowPlayers(player, true);
			removeHidden(player);
		}
	}

	/**
	 * Force the Player to be invisible to all.
	 *
	 * @param player target player
	 */
	public void forceInvisible(Player player) {
		for (Player players : Bukkit.getOnlinePlayers()) {
			PlayerUtils.hidePlayer(players, player);
		}
		addHidden(player);
	}

	/**
	 * Has requested to Hide Players.
	 * @param player requesting player
	 * @return player has hidden others
	 */
	public boolean hasHiddenPlayers(Player player) {
		return hiddenPlayers.contains(player.getUniqueId());
	}

	/**
	 * Add Player to Hidden Players.
	 * @param player requesting player
	 */
	private void addHidden(Player player) {
		hiddenPlayers.add(player.getUniqueId());
	}

	/**
	 * Remove Player from Hidden Players.
	 * @param player requesting player
	 */
	private void removeHidden(Player player) {
		hiddenPlayers.remove(player.getUniqueId());
	}

	public void removeHiddenPlayer(@NotNull Player player) {
		hiddenPlayers.remove(player.getUniqueId());
	}

	@Override
	public void processCommand(CommandSender commandSender, String... args) {
		if (!(commandSender instanceof Player) || !isPlaying((Player) commandSender)) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", commandSender);
			return;
		}

		if (args.length >= 1) {
			String action = args[0].toLowerCase();
			if (!sessionActions.containsKey(action)) {
				TranslationUtils.sendMessage(commandSender, "Unknown Session action command!");
			} else {
				sessionActions.get(action).accept((Player) commandSender);
			}
		}
	}

	private void populateSessionActions() {
		sessionActions.put("hideall", this::toggleVisibility);
		sessionActions.put("restart", player -> parkour.getPlayerManager().restartCourse(player));
		sessionActions.put("back", player -> parkour.getPlayerManager().playerDie(player));
		sessionActions.put("leave", player -> parkour.getPlayerManager().leaveCourse(player));
		sessionActions.put("manualcheckpoint", player -> parkour.getPlayerManager().setManualCheckpoint(player));
		sessionActions.put("quiet", player -> parkour.getQuietModeManager().toggleQuietMode(player));
	}
}

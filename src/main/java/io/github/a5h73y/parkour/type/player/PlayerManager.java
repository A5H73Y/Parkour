package io.github.a5h73y.parkour.type.player;

import static io.github.a5h73y.parkour.enums.ParkourEventType.CHECKPOINT;
import static io.github.a5h73y.parkour.enums.ParkourEventType.CHECKPOINT_ALL;
import static io.github.a5h73y.parkour.enums.ParkourEventType.COURSE_RECORD;
import static io.github.a5h73y.parkour.enums.ParkourEventType.DEATH;
import static io.github.a5h73y.parkour.enums.ParkourEventType.FINISH;
import static io.github.a5h73y.parkour.enums.ParkourEventType.JOIN;
import static io.github.a5h73y.parkour.enums.ParkourEventType.LEAVE;
import static io.github.a5h73y.parkour.enums.ParkourEventType.PRIZE;
import static io.github.a5h73y.parkour.other.ParkourConstants.COURSE_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_INVALID_AMOUNT;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_UNKNOWN_PLAYER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PARKOUR_LEVEL_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PARKOUR_RANK_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TEST_MODE;
import static io.github.a5h73y.parkour.utility.TranslationUtils.sendConditionalValue;
import static io.github.a5h73y.parkour.utility.TranslationUtils.sendValue;

import com.cryptomorin.xseries.XPotion;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.conversation.SetPlayerConversation;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.enums.ParkourEventType;
import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.enums.SoundType;
import io.github.a5h73y.parkour.event.PlayerAchieveCheckpointEvent;
import io.github.a5h73y.parkour.event.PlayerDeathEvent;
import io.github.a5h73y.parkour.event.PlayerFinishCourseEvent;
import io.github.a5h73y.parkour.event.PlayerJoinCourseEvent;
import io.github.a5h73y.parkour.event.PlayerLeaveCourseEvent;
import io.github.a5h73y.parkour.event.PlayerParkourLevelEvent;
import io.github.a5h73y.parkour.event.PlayerParkourRankEvent;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.ParkourConstants;
import io.github.a5h73y.parkour.other.ParkourValidation;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.kit.ParkourKit;
import io.github.a5h73y.parkour.type.lobby.LobbyInfo;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parkour Player Manager.
 * Does not use a public cache, as the player's ParkourSession state is managed here only.
 */
public class PlayerManager extends AbstractPluginReceiver {

	private final Map<Player, ParkourSession> parkourPlayers = new WeakHashMap<>();

	private final Map<Player, Long> playerDelay = new HashMap<>();
	private final Map<Integer, String> parkourRanks = new TreeMap<>();

	private final List<Player> hiddenPlayers = new ArrayList<>();

	/**
	 * Initialise the Parkour Player Manager.
	 * @param parkour plugin instance
	 */
	public PlayerManager(final Parkour parkour) {
		super(parkour);
		populateParkourPlayers();
		populateParkourRanks();
		startLiveTimerRunnable();
	}

	/**
	 * Get the Player's {@link ParkourSession}.
	 *
	 * @param player requesting player
	 * @return player's ParkourSession
	 */
	@Nullable
	public ParkourSession getParkourSession(Player player) {
		return parkourPlayers.get(player);
	}

	/**
	 * Check whether a Player has a ParkourSession.
	 *
	 * @param player requesting player
	 * @return player session exists
	 */
	public boolean isPlaying(Player player) {
		return parkourPlayers.containsKey(player);
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
		return session != null && ParkourConstants.TEST_MODE.equals(session.getCourse().getName());
	}

	/**
	 * Get the number of online Parkour Players.
	 *
	 * @return number of parkour players
	 */
	public int getNumberOfParkourPlayer() {
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
				.map(session -> session.getKey().getName())
				.collect(Collectors.toList());
	}

	/**
	 * Request to Join the Player to the Course.
	 * We can assume that if they are requesting the join using the course name it needs to validated.
	 *
	 * @param player requesting player
	 * @param courseName course name
	 */
	public void joinCourse(Player player, String courseName) {
		Course course = parkour.getCourseManager().findCourse(courseName);

		if (course == null) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, player);
			return;
		}

		if (!ParkourValidation.canJoinCourse(player, course)) {
			return;
		}

		joinCourse(player, course);
	}

	/**
	 * Join the Player to a Course.
	 * Prepare the player for a Parkour course.
	 * Non-silent joining of Course.
	 *
	 * @param player requesting player
	 * @param course target course
	 */
	public void joinCourse(Player player, Course course) {
		joinCourse(player, course, false);
	}

	/**
	 * Join the Player to a Course.
	 * Prepare the player for a Parkour course.
	 * Silent signifies that the player should not be notified.
	 *
	 * @param player target player
	 * @param course target course
	 * @param silent silently join the course
	 */
	public void joinCourse(Player player, Course course, boolean silent) {
		if (!silent && parkour.getConfig().isTeleportToJoinLocation()) {
			PlayerInfo.setJoinLocation(player);
		}

		if (parkour.getConfig().getBoolean("OnJoin.TeleportPlayer")) {
			PlayerUtils.teleportToLocation(player, course.getCheckpoints().get(0).getLocation());
		}
		preparePlayerForCourse(player, course.getName());
		CourseInfo.incrementViews(course.getName());
		PlayerInfo.setLastPlayedCourse(player, course.getName());
		parkour.getSoundsManager().playSound(player, SoundType.JOIN_COURSE);

		// already on a different course
		if (isPlaying(player) && !getParkourSession(player).getCourseName().equals(course.getName())) {
			removePlayer(player);
		}

		// set up their session
		ParkourSession session;
		if (canLoadParkourSession(player, course)) {
			session = loadParkourSession(player, course.getName());
			PlayerUtils.teleportToLocation(player, determineDestination(session));
			TranslationUtils.sendValueTranslation("Parkour.Continue", session.getCourse().getDisplayName(), player);
		} else {
			session = addPlayer(player, new ParkourSession(course));
		}

		displayJoinMessage(player, silent, session);
		setupParkourMode(player);

		parkour.getScoreboardManager().addScoreboard(player, session);
		if (!silent) {
			parkour.getCourseManager().runEventCommands(player, session, JOIN);
		}
		if (!parkour.getConfig().isTreatFirstCheckpointAsStart()
				&& !parkour.getChallengeManager().hasPlayerBeenChallenged(player)) {
			session.setStartTimer(true);
		}

		Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinCourseEvent(player, course.getName(), silent));
	}

	/**
	 * Delay the Joining of the Player to a Course.
	 *
	 * @param player requesting player
	 * @param courseName target course name
	 * @param delay delay in milliseconds
	 */
	public void joinCourseButDelayed(Player player, String courseName, int delay) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(parkour, () -> joinCourse(player, courseName), delay);
	}

	/**
	 * Request Player to leave Course.
	 * Teleports the player to their designated location.
	 *
	 * @param player requesting player
	 */
	public void leaveCourse(Player player) {
		leaveCourse(player, false);
	}

	/**
	 * Request Player to leave Course.
	 * Teleports the player to their designated location.
	 * If ParkourSession is not due to be deleted it will be resumed upon rejoining the Course.
	 * Silent signifies that the player should not be notified or teleported.
	 *
	 * @param player requesting player
	 * @param silent silently leave the course
	 */
	public void leaveCourse(Player player, boolean silent) {
		if (!isPlaying(player)) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);
			return;
		}

		ParkourSession session = getParkourSession(player);

		if (parkour.getConfig().isLeaveDestroyCourseProgress() || !CourseInfo.getResumable(session.getCourseName())) {
			session.setMarkedForDeletion(true);
		}

		teardownParkourMode(player);
		if (session.isMarkedForDeletion()) {
			deleteParkourSession(player, session.getCourseName());
			removePlayer(player);
		} else {
			stashParkourSession(player, true);
		}
		preparePlayer(player, parkour.getConfig().getString("OnFinish.SetGameMode"));
		restoreHealthHunger(player);
		restoreXpLevel(player);
		loadInventoryArmor(player);
		parkour.getChallengeManager().forfeitChallenge(player);

		if (!silent) {
			parkour.getSoundsManager().playSound(player, SoundType.COURSE_FAILED);
			parkour.getBountifulApi().sendSubTitle(player,
					TranslationUtils.getCourseEventMessage(session, LEAVE, "Parkour.Leave"),
					parkour.getConfig().getBoolean("DisplayTitle.Leave"));

			if (parkour.getConfig().getBoolean("OnLeave.TeleportAway")) {
				if (parkour.getConfig().isTeleportToJoinLocation()
						&& PlayerInfo.hasJoinLocation(player)) {
					PlayerUtils.teleportToLocation(player, PlayerInfo.getJoinLocation(player));
				} else {
					parkour.getLobbyManager().teleportToLeaveDestination(player, session);
				}
			}
			parkour.getCourseManager().runEventCommands(player, session, LEAVE);
		}

		forceVisible(player);
		parkour.getScoreboardManager().removeScoreboard(player);
		PlayerInfo.setExistingSessionCourseName(player, null);
		Bukkit.getServer().getPluginManager().callEvent(
				new PlayerLeaveCourseEvent(player, session.getCourse().getName(), silent));
	}

	/**
	 * Increase ParkourSession Checkpoint.
	 * The Player will be notified and their Session Checkpoint will be increased.
	 *
	 * @param player requesting player.
	 */
	public void increaseCheckpoint(Player player) {
		ParkourSession session = getParkourSession(player);

		if (session == null) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);
			return;
		}

		session.increaseCheckpoint();
		parkour.getCourseManager().runEventCommands(player, session, CHECKPOINT);

		ParkourEventType eventType = CHECKPOINT;
		String checkpointTranslation = "Event.Checkpoint";

		if (session.hasAchievedAllCheckpoints()) {
			if (parkour.getConfig().getBoolean("OnCourse.TreatLastCheckpointAsFinish")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(parkour, () -> finishCourse(player));
				return;
			}

			parkour.getCourseManager().runEventCommands(player, session, CHECKPOINT_ALL);
			eventType = CHECKPOINT_ALL;
			checkpointTranslation = "Event.AllCheckpoints";
		}

		parkour.getSoundsManager().playSound(player, SoundType.CHECKPOINT_ACHIEVED);
		parkour.getScoreboardManager().updateScoreboardCheckpoints(player, session);

		boolean showTitle = parkour.getConfig().getBoolean("DisplayTitle.Checkpoint");

		String checkpointMessage = TranslationUtils.getCourseEventMessage(session, eventType, checkpointTranslation)
				.replace("%CURRENT%", String.valueOf(session.getCurrentCheckpoint()))
				.replace("%TOTAL%", String.valueOf(session.getCourse().getNumberOfCheckpoints()));

		parkour.getBountifulApi().sendSubTitle(player, checkpointMessage, showTitle);

		Bukkit.getServer().getPluginManager().callEvent(
				new PlayerAchieveCheckpointEvent(player, session.getCourse().getName(), session.getCheckpoint()));
	}

	/**
	 * Player Death on Course.
	 * This can be triggered by real events (like taking too much damage), or native Parkour deaths (death blocks).
	 * The Player will be teleported to their most recent checkpoint, and their deaths increased.
	 *
	 * @param player requesting player
	 */
	public void playerDie(Player player) {
		if (!isPlaying(player)) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);
			return;
		}

		ParkourSession session = getParkourSession(player);
		parkour.getSoundsManager().playSound(player, SoundType.PLAYER_DEATH);
		session.increaseDeath();

		if (session.getCourse().hasMaxDeaths()) {
			if (session.getCourse().getMaxDeaths() > session.getDeaths()) {
				parkour.getBountifulApi().sendSubTitle(player,
						TranslationUtils.getValueTranslation("Parkour.LifeCount",
								String.valueOf(session.getRemainingDeaths()), false),
						parkour.getConfig().getBoolean("DisplayTitle.Death"));

			} else {
				TranslationUtils.sendValueTranslation("Parkour.MaxDeaths",
						String.valueOf(session.getCourse().getMaxDeaths()), player);
				leaveCourse(player);
				return;
			}
		}

		PlayerUtils.teleportToLocation(player, determineDestination(session));

		// if the Player is in Test Mode, we don't need to run the rest
		if (isPlayerInTestMode(player)) {
			TranslationUtils.sendTranslation("Parkour.Die1", player);
			return;
		}

		parkour.getScoreboardManager().updateScoreboardDeaths(player, session.getDeaths(), session.getRemainingDeaths());
		parkour.getCourseManager().runEventCommands(player, session, DEATH);

		// they haven't yet achieved a checkpoint
		if (session.getCurrentCheckpoint() == 0 && session.getFreedomLocation() == null) {
			String message = TranslationUtils.getCourseEventMessage(session, DEATH, "Parkour.Die1");

			if (parkour.getConfig().getBoolean("OnDie.ResetProgressWithNoCheckpoint")) {
				session.resetProgress();
				message += " " + TranslationUtils.getTranslation("Parkour.TimeReset", false);
			}

			if (!PlayerInfo.isQuietMode(player)) {
				TranslationUtils.sendMessage(player, message);
			}
		} else {
			if (!PlayerInfo.isQuietMode(player)) {
				TranslationUtils.sendValueTranslation("Parkour.Die2",
						String.valueOf(session.getCurrentCheckpoint()), player);
			}
		}

		if (parkour.getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			player.setLevel(session.getDeaths());
		}

		preparePlayer(player, parkour.getConfig().getString("OnJoin.SetGameMode"));
		Bukkit.getServer().getPluginManager().callEvent(new PlayerDeathEvent(player, session.getCourse().getName()));
	}

	/**
	 * Teardown all active Parkour Players.
	 * Remove all in-memory references to each player, persisting any data to a file.
	 */
	public void teardownParkourPlayers() {
		for (Player player : parkourPlayers.keySet()) {
			stashParkourSession(player, false);
		}
		parkourPlayers.clear();
	}

	/**
	 * Teardown a Parkour Player.
	 * Remove all in-memory references to the player, persisting any data to a file.
	 *
	 * @param player parkour player
	 */
	public void teardownParkourPlayer(Player player) {
		parkour.getChallengeManager().forfeitChallenge(player);
		parkour.getQuestionManager().removeQuestion(player);
		hiddenPlayers.remove(player);
		playerDelay.remove(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(parkour, () -> stashParkourSession(player, true));
	}

	/**
	 * Player Finished a Course.
	 * Once the Player is validated, the player will be notified of Course Completion.
	 * Their inventory and armor will be restored and their prize will be rewarded to them.
	 * They will be teleported to the configured location, to either lobby, course or initial join location.
	 * If configured, a Time entry will be added to the database.
	 *
	 * @param player requesting player
	 */
	public void finishCourse(final Player player) {
		if (!isPlaying(player)) {
			return;
		}

		if (isPlayerInTestMode(player)) {
			return;
		}

		ParkourSession session = getParkourSession(player);

		if (parkour.getConfig().getBoolean("OnFinish.EnforceCompletion")
				&& !session.hasAchievedAllCheckpoints()) {

			TranslationUtils.sendTranslation("Error.Cheating1", player);
			TranslationUtils.sendValueTranslation("Error.Cheating2",
					String.valueOf(session.getCourse().getNumberOfCheckpoints()), player);
			playerDie(player);
			return;
		}

		final String courseName = session.getCourse().getName();

		session.markTimeFinished();
		parkour.getSoundsManager().playSound(player, SoundType.COURSE_FINISHED);
		preparePlayer(player, parkour.getConfig().getString("OnFinish.SetGameMode"));

		if (hasHiddenPlayers(player)) {
			hideOrShowPlayers(player, true, true);
			removeHidden(player);
		}

		announceCourseFinishMessage(player, session);
		CourseInfo.incrementCompletions(courseName);
		teardownParkourMode(player);
		removePlayer(player);

		parkour.getChallengeManager().completeChallenge(player);

		if (parkour.getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			restoreXpLevel(player);
		}

		final long delay = parkour.getConfig().getLong("OnFinish.TeleportDelay");
		final boolean teleportAway = parkour.getConfig().getBoolean("OnFinish.TeleportAway");

		Bukkit.getScheduler().scheduleSyncDelayedTask(parkour, () -> {
			restoreHealthHunger(player);
			loadInventoryArmor(player);
			rewardPrize(player, session);
			parkour.getScoreboardManager().removeScoreboard(player);
			if (teleportAway) {
				teleportCourseCompletion(player, courseName);
			}
			boolean recordTime = isNewRecord(player, session);
			parkour.getDatabase().insertOrUpdateTime(
					courseName, player, session.getTimeFinished(), session.getDeaths(), recordTime);

			if (recordTime) {
				parkour.getCourseManager().runEventCommands(player, session, COURSE_RECORD);
			}
		}, delay);

		PlayerInfo.setLastCompletedCourse(player, courseName);
		PlayerInfo.addCompletedCourse(player, courseName);
		PlayerInfo.setExistingSessionCourseName(player, null);

		forceVisible(player);
		deleteParkourSession(player, courseName);
		Bukkit.getServer().getPluginManager().callEvent(new PlayerFinishCourseEvent(player, courseName));
	}

	/**
	 * Restart Course progress.
	 * Will trigger a silent leave and rejoin of the Course.
	 *
	 * @param player requesting player
	 */
	public void restartCourse(Player player) {
		restartCourse(player, false);
	}

	/**
	 * Restart Course progress.
	 * Will trigger a silent leave and rejoin of the Course.
	 *
	 * @param player requesting player
	 * @param doNotTeleport do not teleport the player manually
	 */
	public void restartCourse(Player player, boolean doNotTeleport) {
		if (!isPlaying(player)) {
			return;
		}

		Course course = getParkourSession(player).getCourse();
		leaveCourse(player, true);
		deleteParkourSession(player, course.getName());
		joinCourse(player, course, true);
		// if they are restarting the Course, we need to teleport them back
		if (!doNotTeleport && !parkour.getConfig().getBoolean("OnJoin.TeleportPlayer")) {
			PlayerUtils.teleportToLocation(player, course.getCheckpoints().get(0).getLocation());
		}

		boolean displayTitle = parkour.getConfig().getBoolean("DisplayTitle.JoinCourse");
		parkour.getBountifulApi().sendSubTitle(player,
				TranslationUtils.getTranslation("Parkour.Restarting", false), displayTitle);
	}

	/**
	 * Reward the Player with the Course Prize.
	 * A Prize Delay validation be applied after the Player has completed the Course too recently.
	 * If 'Reward Once' is enabled and they've completed the Course, only the {@code ParkourEventType.FINISH} event will fire.
	 *
	 * @param player requesting player
	 * @param session parkour session
	 */
	public void rewardPrize(Player player, ParkourSession session) {
		String courseName = session.getCourseName();
		if (!parkour.getConfig().getBoolean("OnFinish.EnablePrizes")) {
			return;
		}

		if (CourseInfo.getRewardOnce(courseName)
				&& parkour.getDatabase().hasPlayerAchievedTime(player, courseName)) {
			parkour.getCourseManager().runEventCommands(player, session, FINISH);
			return;
		}

		// check if the Course has a reward delay
		if (CourseInfo.hasRewardDelay(courseName)) {
			// if the player has not exceeded the Course delay, no prize will be given
			if (!hasPrizeCooldownDurationPassed(player, courseName, true)) {
				return;
			}
			// otherwise make a note of last time rewarded, and let them continue
			PlayerInfo.setLastRewardedTime(player, courseName, System.currentTimeMillis());
		}

		Material material;
		int amount;

		// Use Custom prize
		if (CourseInfo.hasMaterialPrize(courseName)) {
			material = CourseInfo.getMaterialPrize(courseName);
			amount = CourseInfo.getMaterialPrizeAmount(courseName);

		} else {
			material = MaterialUtils.lookupMaterial(parkour.getConfig().getString("OnFinish.DefaultPrize.Material"));
			amount = parkour.getConfig().getInt("OnFinish.DefaultPrize.Amount", 0);
		}

		if (material != null && amount > 0) {
			player.getInventory().addItem(new ItemStack(material, amount));
		}

		// Give XP to player
		int xp = CourseInfo.getXpPrize(courseName);

		if (xp == 0) {
			xp = parkour.getConfig().getInt("OnFinish.DefaultPrize.XP");
		}

		if (xp > 0) {
			player.giveExp(xp);
		}

		rewardParkourLevel(player, courseName);
		rewardParkoins(player, CourseInfo.getRewardParkoins(courseName));
		parkour.getEconomyApi().giveEconomyPrize(player, courseName);

		if (CourseInfo.hasEventCommands(courseName, PRIZE)) {
			parkour.getCourseManager().runEventCommands(player, session, PRIZE);

		} else if (ValidationUtils.isStringValid(parkour.getConfig().getDefaultPrizeCommand())) {
			parkour.getCourseManager().dispatchServerPlayerCommand(
					parkour.getConfig().getDefaultPrizeCommand(), player, session);
		}
		player.updateInventory();
	}

	/**
	 * Rocket Launch the Player.
	 * Will apply a fake explosion to the player and give them velocity.
	 * The direction of the velocity can be configured.
	 *
	 * @param player target player
	 */
	public void rocketLaunchPlayer(Player player) {
		double force = parkour.getConfig().getBoolean("ParkourModes.Rockets.Invert") ? 1.5 : -1.5;

		Vector velocity = player.getLocation().getDirection().normalize();
		velocity = velocity.multiply(force);
		velocity = velocity.setY(velocity.getY() / 2);
		player.setVelocity(velocity);
		player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 500);
	}

	/**
	 * Toggle Player Quiet Mode.
	 * Strange method because the action bar notification is only sent if they aren't currently in Quiet Mode.
	 *
	 * @param player requesting player
	 */
	public void toggleQuietMode(Player player) {
		if (!delayPlayerWithMessage(player, 2)) {
			return;
		}
		boolean currentlyQuiet = PlayerInfo.isQuietMode(player);

		if (currentlyQuiet) {
			PlayerInfo.toggleQuietMode(player);
		}

		String messageKey = currentlyQuiet ? "Parkour.QuietOff" : "Parkour.QuietOn";

		parkour.getBountifulApi().sendActionBar(player,
				TranslationUtils.getTranslation(messageKey, false), true);

		if (!currentlyQuiet) {
			PlayerInfo.toggleQuietMode(player);
		}
	}

	/**
	 * Toggle the Visibility of the Player.
	 *
	 * @param player player
	 */
	public void toggleVisibility(Player player) {
		toggleVisibility(player, false);
	}

	/**
	 * Toggle Player's Visibility.
	 * Either hide or show all online players. Override can be applied to
	 * Used when on a Course and want to remove distraction of other Players.
	 *
	 * @param player requesting player
	 * @param forceVisible override to force visibility
	 */
	public void toggleVisibility(Player player, boolean forceVisible) {
		boolean showPlayers = forceVisible || hasHiddenPlayers(player);
		hideOrShowPlayers(player, showPlayers, forceVisible);

		if (showPlayers) {
			removeHidden(player);
			TranslationUtils.sendTranslation("Event.HideAll1", player);

		} else {
			addHidden(player);
			TranslationUtils.sendTranslation("Event.HideAll2", player);
		}
	}

	private void hideOrShowPlayers(Player player, boolean showPlayers, boolean allPlayers) {
		Collection<Player> playerScope;

		if (parkour.getConfig().getBoolean("ParkourTool.HideAll.Global") || allPlayers) {
			playerScope = (List<Player>) Bukkit.getOnlinePlayers();
		} else {
			playerScope = getOnlineParkourPlayers();
		}

		for (Player eachPlayer : playerScope) {
			if (showPlayers) {
				player.showPlayer(eachPlayer);
			} else {
				player.hidePlayer(eachPlayer);
			}
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
				eachPlayer.showPlayer(player);
			}
		}
		if (hasHiddenPlayers(player)) {
			hideOrShowPlayers(player, true, true);
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
			players.hidePlayer(player);
		}
		addHidden(player);
	}

	/**
	 * Has requested to Hide Players.
	 * @param player requesting player
	 * @return player has hidden others
	 */
	public boolean hasHiddenPlayers(Player player) {
		return hiddenPlayers.contains(player);
	}

	/**
	 * Delay the Player's Requested Event with message.
	 * Some actions may require a cooldown, the event will only be permitted if enough time has passed.
	 *
	 * @param player requesting player
	 * @param secondsToWait seconds elapsed before permitted again
	 * @return player allowed to perform action
	 */
	public boolean delayPlayerWithMessage(Player player, int secondsToWait) {
		return delayPlayer(player, secondsToWait, "Error.Cooldown", false);
	}

	/**
	 * Delay the Player's Requested Event.
	 * Some actions may require a cooldown, the event will only be permitted if enough time has passed.
	 *
	 * @param player requesting player
	 * @param secondsToWait seconds elapsed before permitted again
	 * @return player allowed to perform action
	 */
	public boolean delayPlayer(Player player, int secondsToWait) {
		return delayPlayer(player, secondsToWait, null, false);
	}

	/**
	 * Delay the Player's Requested Event.
	 * Some actions may require a cooldown, the event will only be permitted if enough time has passed.
	 * If requested, operators can be exempt from the cooldown.
	 *
	 * @param player requesting player
	 * @param secondsToWait seconds elapsed before permitted again
	 * @param displayMessageKey the cooldown message key
	 * @param opsBypass operators bypass cooldown
	 * @return player allowed to perform action
	 */
	public boolean delayPlayer(Player player, int secondsToWait, @Nullable String displayMessageKey, boolean opsBypass) {
		if (player.isOp() && opsBypass) {
			return true;
		}

		if (!playerDelay.containsKey(player)) {
			playerDelay.put(player, System.currentTimeMillis());
			return true;
		}

		long lastAction = playerDelay.get(player);
		int secondsElapsed = (int) ((System.currentTimeMillis() - lastAction) / 1000);

		if (secondsElapsed >= secondsToWait) {
			playerDelay.put(player, System.currentTimeMillis());
			return true;
		}

		if (displayMessageKey != null) {
			TranslationUtils.sendValueTranslation(displayMessageKey,
					String.valueOf(secondsToWait - secondsElapsed), player);
		}
		return false;
	}

	/**
	 * Has the Course Prize cooldown passed for the Player.
	 *
	 * @param player requesting player
	 * @param courseName course name
	 * @param displayMessage display cooldown message
	 * @return course prize cooldown passed
	 */
	public boolean hasPrizeCooldownDurationPassed(Player player, String courseName, boolean displayMessage) {
		double rewardDelay = CourseInfo.getRewardDelay(courseName);

		if (rewardDelay <= 0) {
			return true;
		}

		long lastRewardTime = PlayerInfo.getLastRewardedTime(player, courseName);

		if (lastRewardTime <= 0) {
			return true;
		}

		long timeDifference = System.currentTimeMillis() - lastRewardTime;
		long hoursDelay = DateTimeUtils.convertHoursToMilliseconds(rewardDelay);

		if (timeDifference > hoursDelay) {
			return true;
		}

		if (parkour.getConfig().isDisplayPrizeCooldown() && displayMessage) {
			TranslationUtils.sendValueTranslation("Error.PrizeCooldown",
					DateTimeUtils.getDelayTimeRemaining(player, courseName), player);
		}
		return false;
	}

	/**
	 * Prepare the player for Parkour.
	 * Executed when the player dies, will reset them to a prepared state so they can continue.
	 *
	 * @param player player
	 * @param gameModeName GameMode name
	 */
	public void preparePlayer(Player player, String gameModeName) {
		PlayerUtils.removeAllPotionEffects(player);
		ParkourSession session = getParkourSession(player);

		if (session != null && session.getParkourMode() == ParkourMode.POTION) {
			XPotion.addPotionEffectsFromString(player,
					CourseInfo.getPotionParkourModeEffects(session.getCourseName()));
		}

		if (!isPlayerInTestMode(player)) {
			player.setGameMode(PluginUtils.getGameMode(gameModeName));
		}

		Damageable playerDamage = player;
		playerDamage.setHealth(playerDamage.getMaxHealth());
		player.setFallDistance(0);
		player.setFireTicks(0);
		player.eject();
	}

	/**
	 * Save the Player's Inventory and Armour.
	 * Once saved, the players inventory and armour is cleared.
	 * Will not overwrite the inventory if data is already saved. Can be disabled.
	 *
	 * @param player player
	 */
	public void saveInventoryArmor(Player player) {
		if (!parkour.getConfig().getBoolean("Other.Parkour.InventoryManagement")) {
			return;
		}

		ParkourConfiguration inventoryConfig = Parkour.getConfig(ConfigType.INVENTORY);
		if (inventoryConfig.contains(player.getUniqueId() + ".Inventory")) {
			return;
		}

		PlayerInfo.saveInventoryArmor(player);

		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);

		player.updateInventory();
	}

	/**
	 * Load the Player's original Inventory.
	 * When they leave or finish a course, their inventory and armour will be restored to them.
	 * Will delete the inventory from the config once loaded.
	 *
	 * @param player player
	 */
	public void loadInventoryArmor(Player player) {
		if (!parkour.getConfig().getBoolean("Other.Parkour.InventoryManagement")) {
			return;
		}

		ItemStack[] inventoryContents = PlayerInfo.getSavedInventoryContents(player);

		if (inventoryContents == null) {
			TranslationUtils.sendMessage(player, "No saved inventory to load.");
			return;
		}

		player.getInventory().clear();
		player.getInventory().setContents(inventoryContents);

		ItemStack[] armorContents = PlayerInfo.getSavedArmorContents(player);
		player.getInventory().setArmorContents(armorContents);
		player.updateInventory();

		Parkour.getConfig(ConfigType.INVENTORY).set(player.getUniqueId().toString(), null);
		Parkour.getConfig(ConfigType.INVENTORY).save();
	}

	/**
	 * Reward Parkoins to the Player.
	 * Increase the amount of Parkoins the Player has by an amount.
	 *
	 * @param player player
	 * @param parkoins amount of Parkoins
	 */
	public void rewardParkoins(Player player, double parkoins) {
		if (parkoins <= 0) {
			return;
		}

		PlayerInfo.increaseParkoins(player, parkoins);
		player.sendMessage(TranslationUtils.getTranslation("Parkour.RewardParkoins")
				.replace(ParkourConstants.AMOUNT_PLACEHOLDER, String.valueOf(parkoins))
				.replace("%TOTAL%", String.valueOf(PlayerInfo.getParkoins(player))));
	}

	/**
	 * Deduct Parkoins from the Player.
	 * Reduce the amount of Parkoins the Player has by an amount.
	 *
	 * @param player player
	 * @param parkoins amount of Parkoins
	 */
	public void deductParkoins(Player player, double parkoins) {
		if (parkoins <= 0) {
			return;
		}

		double current = PlayerInfo.getParkoins(player);
		double amountToDeduct = Math.min(current, parkoins);

		PlayerInfo.setParkoins(player, current - amountToDeduct);
		TranslationUtils.sendMessage(player, parkoins + " Parkoins deducted! New total: &b"
				+ PlayerInfo.getParkoins(player));
	}

	/**
	 * Display a Permissions Summary.
	 * Each of the Player's Parkour permissions will be listed.
	 *
	 * @param player player
	 */
	public void displayPermissions(Player player) {
		TranslationUtils.sendHeading("Parkour Permissions", player);

		boolean hasPermission = false;
		for (Permission permission : Permission.values()) {
			if (PermissionUtils.hasPermission(player, permission, false)) {
				TranslationUtils.sendMessage(player, "* " + permission.getPermission(), false);
				hasPermission = true;
			}
		}
		if (!hasPermission) {
			TranslationUtils.sendMessage(player, "* You don't have any Parkour permissions.", false);
		}
	}

	/**
	 * Toggle Test Mode.
	 * When enabled, the Player will join a special course named "Test Mode".
	 * This will allow them to test a ParkourKits's functionality quickly.
	 * Will set the Course start location to the Player's location.
	 *
	 * @param player player
	 */
	public void toggleTestMode(@NotNull Player player, @Nullable String kitName) {
		if (isPlaying(player)) {
			if (isPlayerInTestMode(player)) {
				removePlayer(player);
				parkour.getBountifulApi().sendActionBar(player,
						TranslationUtils.getTranslation("Parkour.TestModeOff", false), true);
			} else {
				TranslationUtils.sendMessage(player, "You are not in Test Mode.");
			}
		} else {
			ParkourKit kit = parkour.getParkourKitManager().getParkourKit(kitName);

			if (kit == null) {
				TranslationUtils.sendMessage(player, "ParkourKit " + kitName + " doesn't exist!");

			} else {
				List<Checkpoint> checkpoints = Collections.singletonList(
						parkour.getCheckpointManager().createCheckpointFromPlayerLocation(player));
				ParkourSession session = new ParkourSession(
						new Course(TEST_MODE, checkpoints, kit, ParkourMode.NONE));
				addPlayer(player, session);
				parkour.getBountifulApi().sendActionBar(player, TranslationUtils.getValueTranslation(
						"Parkour.TestModeOn", kitName, false), true);
			}
		}
	}

	/**
	 * Display Parkour Player's Information.
	 * Finds and displays the target player's stored statistics and any current Course information.
	 *
	 * @param sender requesting sender
	 * @param targetPlayer target layer
	 */
	public void displayParkourInfo(CommandSender sender, OfflinePlayer targetPlayer) {
		if (!PlayerInfo.hasPlayerInfo(targetPlayer)) {
			TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, sender);
			return;
		}

		ParkourSession session = getParkourSession(targetPlayer.getPlayer());
		TranslationUtils.sendHeading(targetPlayer.getName() + "'s information", sender);

		if (session != null) {
			sendValue(sender, "Course", session.getCourse().getName());
			sendValue(sender, "Deaths", session.getDeaths());
			sendValue(sender, "Time", session.getDisplayTime());
			sendValue(sender, "Checkpoint", session.getCurrentCheckpoint());
		}

		sendConditionalValue(sender, "ParkourLevel", PlayerInfo.getParkourLevel(targetPlayer));
		sendConditionalValue(sender, "ParkourRank", PlayerInfo.getParkourRank(targetPlayer));
		sendConditionalValue(sender, "Parkoins", PlayerInfo.getParkoins(targetPlayer));
		sendConditionalValue(sender, "Editing", PlayerInfo.getSelectedCourse(targetPlayer));

		sendConditionalValue(sender, "Courses Completed", parkour.getConfig().isCompletedCoursesEnabled(),
				PlayerInfo.getNumberOfCompletedCourses(targetPlayer)
						+ " / " + CourseInfo.getAllCourseNames().size());

	}

	/**
	 * Set the Player's ParkourLevel.
	 * Used by administrators to manually set the ParkourLevel of a Player.
	 *
	 * @param sender command sender
	 * @param targetPlayer target player
	 * @param value desired parkour level
	 */
	public void setParkourLevel(CommandSender sender, OfflinePlayer targetPlayer, String value, boolean addition) {
		if (!ValidationUtils.isPositiveInteger(value)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
			return;
		}

		if (!PlayerInfo.hasPlayerInfo(targetPlayer)) {
			TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, sender);
			return;
		}

		int newLevel = Integer.parseInt(value);
		if (addition) {
			newLevel += PlayerInfo.getParkourLevel(targetPlayer);
		}
		newLevel = Math.min(newLevel, parkour.getConfig().getInt("Other.Parkour.MaximumParkourLevel"));
		PlayerInfo.setParkourLevel(targetPlayer, newLevel);
		TranslationUtils.sendMessage(sender, targetPlayer.getName() + "'s ParkourLevel was set to &b" + newLevel);

		if (parkour.getConfig().getBoolean("Other.OnSetPlayerParkourLevel.UpdateParkourRank")) {
			String parkourRank = getUnlockedParkourRank(targetPlayer, newLevel);
			if (parkourRank != null) {
				setParkourRank(sender, targetPlayer, parkourRank);
			}
		}
	}

	/**
	 * Set the Player's ParkourRank.
	 * Used by administrators to manually set the ParkourRank of a Player.
	 *
	 * @param sender command sender
	 * @param targetPlayer target player
	 * @param value desired parkour rank
	 */
	public void setParkourRank(CommandSender sender, OfflinePlayer targetPlayer, String value) {
		if (!PlayerInfo.hasPlayerInfo(targetPlayer)) {
			TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, sender);
			return;
		}

		PlayerInfo.setParkourRank(targetPlayer, value);
		TranslationUtils.sendMessage(sender, targetPlayer.getName() + "'s Parkour was set to " + value);
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
			if (parkour.getCourseManager().doesCourseExists(session.getCourseName())) {
				session.setCourse(parkour.getCourseManager().findCourse(session.getCourseName()));
				session.recalculateTime();
				session.setStartTimer(true);
				parkourPlayers.put(player, session);

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
		File sessionFile = getPlayerSessionPath(player, courseName);

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
		File playersFolder = new File(getParkourSessionsDirectory() + File.separator + player.getUniqueId().toString());
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

	/**
	 * Display a summary of the Parkour Players.
	 * Each of the online Parkour Players will have their session details displayed.
	 *
	 * @param sender command sender
	 */
	public void displayParkourPlayers(CommandSender sender) {
		if (getNumberOfParkourPlayer() == 0) {
			TranslationUtils.sendMessage(sender, "Nobody is playing Parkour!");
			return;
		}

		TranslationUtils.sendMessage(sender, getNumberOfParkourPlayer() + " players using Parkour: ");

		String playingMessage = TranslationUtils.getTranslation("Parkour.Playing", false);
		for (Map.Entry<Player, ParkourSession> entry : parkourPlayers.entrySet()) {
			sender.sendMessage(TranslationUtils.replaceAllParkourPlaceholders(
					playingMessage, entry.getKey(), entry.getValue()));
		}
	}

	/**
	 * Display all ParkourRanks available.
	 *
	 * @param sender command sender
	 */
	public void displayParkourRanks(CommandSender sender) {
		TranslationUtils.sendHeading("Parkour Ranks", sender);
		parkourRanks.forEach((parkourLevel, parkourRank) ->
				sender.sendMessage(TranslationUtils.getTranslation("Parkour.RankInfo", false)
						.replace(PARKOUR_LEVEL_PLACEHOLDER, parkourLevel.toString())
						.replace(PARKOUR_RANK_PLACEHOLDER, parkourRank)));
	}

	/**
	 * Set a ParkourRank reward for a ParkourLevel.
	 * A ParkourRank will be awarded to the Player when the pass the threshold of the ParkourLevel required.
	 *
	 * @param sender command sender
	 * @param parkourLevel associated parkour level
	 * @param parkourRank parkour rank rewarded
	 */
	public void setRewardParkourRank(CommandSender sender, String parkourLevel, String parkourRank) {
		if (!ValidationUtils.isPositiveInteger(parkourLevel)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
			return;
		}

		if (!ValidationUtils.isStringValid(parkourRank)) {
			TranslationUtils.sendMessage(sender, "ParkourRank is not valid.");
			return;
		}

		PlayerInfo.setRewardParkourRank(Integer.parseInt(parkourLevel), parkourRank);
		populateParkourRanks();
		TranslationUtils.sendPropertySet(sender, "ParkourRank", "ParkourLevel " + parkourLevel,
				StringUtils.colour(parkourRank));
	}

	/**
	 * Process the "setplayer" Command.
	 *
	 * @param sender command sender
	 * @param args command arguments
	 */
	public void processSetCommand(CommandSender sender, String... args) {
		OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);

		if (!PlayerInfo.hasPlayerInfo(targetPlayer)) {
			TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, sender);
			return;
		}

		if (args.length == 2 && sender instanceof Player) {
			new SetPlayerConversation((Player) sender).withTargetPlayerName(args[1].toLowerCase()).begin();

		} else if (args.length >= 4) {
			SetPlayerConversation.performAction(sender, targetPlayer, args[2], args[3]);

		} else {
			TranslationUtils.sendInvalidSyntax(sender, "setplayer", "(player) [level / leveladd / rank] [value]");
		}
	}

	/**
	 * Has Player selected a known Course.
	 *
	 * @param player player
	 * @return selected valid course
	 */
	public boolean hasSelectedValidCourse(Player player) {
		String selected = PlayerInfo.getSelectedCourse(player);
		return parkour.getCourseManager().doesCourseExists(selected);
	}

	/**
	 * Reset the Player's Parkour Information.
	 *
	 * @param sender command sender
	 * @param targetPlayerName target player name
	 */
	public void resetPlayer(CommandSender sender, String targetPlayerName) {
		OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

		if (!PlayerInfo.hasPlayerInfo(targetPlayer)) {
			TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, sender);
			return;
		}

		resetPlayer(targetPlayer);
		TranslationUtils.sendValueTranslation("Parkour.Reset", targetPlayerName, sender);
		PluginUtils.logToFile(targetPlayerName + " player was reset by " + sender.getName());
	}

	/**
	 * Reset the Player's Parkour data.
	 * All stats, current course info and database records will be deleted.
	 * @param targetPlayer target player
	 */
	public void resetPlayer(OfflinePlayer targetPlayer) {
		PlayerInfo.resetPlayerData(targetPlayer);
		deleteParkourSessions(targetPlayer);
		parkour.getDatabase().deletePlayerTimes(targetPlayer);
		removePlayer(targetPlayer.getPlayer());
	}

	/**
	 * Find the unlocked ParkourRank for new ParkourLevel.
	 * The highest ParkourRank available will be found first, gradually decreasing until a match.
	 *
	 * @param player target player
	 * @param rewardLevel rewarded ParkourLevel
	 * @return unlocked ParkourRank
	 */
	@Nullable
	public String getUnlockedParkourRank(OfflinePlayer player, int rewardLevel) {
		int currentLevel = PlayerInfo.getParkourLevel(player);
		String result = null;

		while (currentLevel < rewardLevel) {
			if (parkourRanks.containsKey(rewardLevel)) {
				result = parkourRanks.get(rewardLevel);
				break;
			}
			rewardLevel--;
		}
		return result;
	}

	/**
	 * Prepare the Player for the ParkourMode.
	 *
	 * @param player target player
	 */
	public void setupParkourMode(Player player) {
		ParkourSession session = getParkourSession(player);
		ParkourMode courseMode = session.getParkourMode();

		if (courseMode == ParkourMode.NONE) {
			return;
		}

		if (courseMode == ParkourMode.FREEDOM) {
			TranslationUtils.sendTranslation("Mode.Freedom.JoinText", player);
			giveParkourTool(player, "ParkourTool.Freedom", "ParkourTool.Freedom");

		} else if (courseMode == ParkourMode.SPEEDY) {
			float speed = Float.parseFloat(parkour.getConfig().getString("ParkourModes.Speedy.SetSpeed"));
			player.setWalkSpeed(speed);

		} else if (courseMode == ParkourMode.ROCKETS) {
			TranslationUtils.sendTranslation("Mode.Rockets.JoinText", player);
			giveParkourTool(player, "ParkourTool.Rockets", "ParkourTool.Rockets");

		} else if (courseMode == ParkourMode.POTION) {
			XPotion.addPotionEffectsFromString(player,
					CourseInfo.getPotionParkourModeEffects(session.getCourseName()));
			if (CourseInfo.hasPotionJoinMessage(session.getCourseName())) {
				TranslationUtils.sendMessage(player, CourseInfo.getPotionJoinMessage(session.getCourseName()));
			}
		}
	}

	/**
	 * Give Player Parkour Tool.
	 *
	 * @param player player
	 * @param configPath config path to tool Material
	 * @param translationKey label translation key
	 */
	public void giveParkourTool(Player player, String configPath, String translationKey) {
		Material material = Material.getMaterial(parkour.getConfig()
				.getString(configPath + ".Material", "AIR").toUpperCase());

		if (material != null && material != Material.AIR && !player.getInventory().contains(material)) {
			int slot = parkour.getConfig().getInt(configPath + ".Slot");
			player.getInventory().setItem(slot, MaterialUtils.createItemStack(material,
					TranslationUtils.getTranslation(translationKey, false)));
		}
	}

	/**
	 * Stash the Player's ParkourSession.
	 * Persist the ParkourSession to a file under the Player's UUID.
	 * Mark the current time accumulated, for the time difference to be recalculated when the Player rejoins.
	 *
	 * @param player player
	 */
	private void stashParkourSession(Player player, boolean removePlaying) {
		ParkourSession session = getParkourSession(player);
		if (session != null && !session.getCourseName().equals(TEST_MODE)) {
			session.markTimeAccumulated();
			PlayerInfo.setExistingSessionCourseName(player, session.getCourseName());
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
		File sessionFile = getPlayerSessionPath(player, courseName);

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
			playerPath = getPlayerSessionPath(player, session.getCourseName());
		}

		return playerPath;
	}

	/**
	 * Get the path to the Player's Session folder.
	 *
	 * @param player player
	 * @param courseName course name
	 * @return path to player's session
	 */
	public File getPlayerSessionPath(OfflinePlayer player, String courseName) {
		return new File(getParkourSessionsDirectory() + File.separator + player.getUniqueId().toString(), courseName);
	}

	/**
	 * Get the Path to the Sessions directory.
	 * @return parkour session directory file
	 */
	private File getParkourSessionsDirectory() {
		return new File(parkour.getDataFolder() + File.separator + "sessions");
	}

	/**
	 * Announce the Course Finish Message.
	 * The scope of the message is configurable.
	 *
	 * @param player player
	 * @param session parkour session
	 */
	private void announceCourseFinishMessage(Player player, ParkourSession session) {
		if (parkour.getConfig().getBoolean("OnFinish.DisplayStats")) {
			parkour.getBountifulApi().sendFullTitle(player,
					TranslationUtils.getCourseEventMessage(session, FINISH, "Parkour.FinishCourse1"),
					TranslationUtils.replaceAllParkourPlaceholders(
							TranslationUtils.getTranslation("Parkour.FinishCourse2", false),
							player, session),
					parkour.getConfig().getBoolean("DisplayTitle.Finish"));
		}

		// don't announce the time if the course isn't ready
		if (!CourseInfo.getReadyStatus(session.getCourseName())) {
			return;
		}

		String finishBroadcast = TranslationUtils.replaceAllParkourPlaceholders(
				TranslationUtils.getTranslation("Parkour.FinishBroadcast"), player, session);

		String scope = parkour.getConfig().getString("OnFinish.BroadcastLevel", "WORLD");
		TranslationUtils.announceParkourMessage(player, scope, finishBroadcast);
	}

	/**
	 * Restore the Player's Health and Food Level.
	 * The values are stored upon joining the Course, and are restored after they finish Parkour.
	 *
	 * @param player player
	 */
	private void restoreHealthHunger(Player player) {
		double health = PlayerInfo.getSavedHealth(player);
		health = Math.min(Math.max(0, health), player.getMaxHealth());
		player.setHealth(health);
		player.setFoodLevel(PlayerInfo.getSavedFoodLevel(player));
		PlayerInfo.resetSavedHealthFoodLevel(player);
	}

	/**
	 * Restore the Player's XP Level.
	 * When enabled, the value is stored upon joining the Course, and is restored after they finish Parkour.
	 *
	 * @param player player
	 */
	private void restoreXpLevel(Player player) {
		if (!parkour.getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			return;
		}

		player.setLevel(PlayerInfo.getSavedXpLevel(player));
		PlayerInfo.resetSavedXpLevel(player);
	}

	/**
	 * Prepare Player for joining the Course.
	 * Any Player information which can be restored upon finishing the Parkour will be saved.
	 * The Player's inventory will be populated with appropriate Parkour tools and any additional Join Items.
	 *
	 * @param player player
	 * @param courseName course name
	 */
	private void preparePlayerForCourse(Player player, String courseName) {
		saveInventoryArmor(player);
		PlayerInfo.saveHealthFoodLevel(player);
		preparePlayer(player, parkour.getConfig().getString("OnJoin.SetGameMode"));

		if (CourseInfo.getCourseMode(courseName) == ParkourMode.NORUN) {
			player.setFoodLevel(6);

		} else if (parkour.getConfig().getBoolean("OnJoin.FillHealth.Enabled")) {
			player.setFoodLevel(parkour.getConfig().getInt("OnJoin.FillHealth.Amount"));
		}

		if (parkour.getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			PlayerInfo.saveXpLevel(player);
			player.setLevel(0);
		}

		if (parkour.getConfig().getBoolean("OnCourse.DisableFly")) {
			player.setAllowFlight(false);
			player.setFlying(false);
		}

		if (parkour.getConfig().getBoolean("ParkourTool.HideAll.ActivateOnJoin")) {
			hideOrShowPlayers(player, false, false);
			addHidden(player);
		}

		giveParkourTool(player, "ParkourTool.LastCheckpoint", "ParkourTool.LastCheckpoint");
		giveParkourTool(player, "ParkourTool.HideAll", "ParkourTool.HideAll");
		giveParkourTool(player, "ParkourTool.Leave", "ParkourTool.Leave");
		giveParkourTool(player, "ParkourTool.Restart", "ParkourTool.Restart");

		for (ItemStack joinItem : CourseInfo.getJoinItems(courseName)) {
			player.getInventory().addItem(joinItem);
		}

		player.updateInventory();
	}

	/**
	 * Check whether the player's time is a new course or personal record.
	 *
	 * @param player player
	 * @param session parkour session
	 */
	private boolean isNewRecord(Player player, ParkourSession session) {
		// if they aren't updating the row, it will be inserted whether or not it's their best time
		// for sake of performance, if we don't care if it's their best time just return
		if (!parkour.getConfig().getBoolean("OnFinish.DisplayNewRecords")
				&& !parkour.getConfig().getBoolean("OnFinish.UpdatePlayerDatabaseTime")) {
			return false;
		}

		if (parkour.getDatabase().isBestCourseTime(session.getCourse().getName(), session.getTimeFinished())) {
			if (parkour.getConfig().getBoolean("OnFinish.DisplayNewRecords")) {
				parkour.getBountifulApi().sendFullTitle(player,
						TranslationUtils.getCourseEventMessage(
								session, COURSE_RECORD, "Parkour.CourseRecord"),
						DateTimeUtils.displayCurrentTime(session.getTimeFinished()), true);
			}
			return true;
		}

		if (parkour.getDatabase().isBestCourseTime(player, session.getCourse().getName(), session.getTimeFinished())) {
			if (parkour.getConfig().getBoolean("OnFinish.DisplayNewRecords")) {
				parkour.getBountifulApi().sendFullTitle(player,
						TranslationUtils.getCourseEventMessage(
								session, COURSE_RECORD, "Parkour.BestTime"),
						DateTimeUtils.displayCurrentTime(session.getTimeFinished()), true);
			}
			return true;
		}
		return false;
	}

	/**
	 * Add the player and their session to parkour players.
	 *
	 * @param player target player
	 * @param session ParkourSession
	 */
	private ParkourSession addPlayer(Player player, ParkourSession session) {
		parkourPlayers.put(player, session);
		return session;
	}

	/**
	 * Remove the player and their session from the parkour players.
	 *
	 * @param player target player
	 */
	private void removePlayer(Player player) {
		ParkourSession session = parkourPlayers.get(player);
		if (session != null) {
			parkourPlayers.remove(player);
		}
	}

	/**
	 * Populate Parkour Players.
	 * As part of a server reload, there would be online players who need their Parkour session restored.
	 */
	private void populateParkourPlayers() {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (!PlayerInfo.hasExistingSessionCourseName(onlinePlayer)) {
				continue;
			}

			ParkourSession session = loadParkourSession(onlinePlayer,
					PlayerInfo.getExistingSessionCourseName(onlinePlayer));

			if (!isPlaying(onlinePlayer)) {
				continue;
			}

			setupParkourMode(onlinePlayer);
			parkour.getScoreboardManager().addScoreboard(onlinePlayer, session);

			String currentCourse = getParkourSession(onlinePlayer).getCourse().getName();
			TranslationUtils.sendValueTranslation("Parkour.Continue", currentCourse, onlinePlayer);
		}
	}

	private void populateParkourRanks() {
		parkourRanks.clear();
		ParkourConfiguration playerConfig = Parkour.getConfig(ConfigType.PLAYERS);
		ConfigurationSection section = playerConfig.getConfigurationSection("ServerInfo.Levels");

		if (section != null) {
			Set<String> levels = section.getKeys(false);
			List<Integer> orderedLevels = levels.stream()
					.mapToInt(Integer::parseInt).sorted().boxed()
					.collect(Collectors.toList());

			for (Integer level : orderedLevels) {
				String rank = playerConfig.getString("ServerInfo.Levels." + level + ".Rank");
				if (rank != null) {
					parkourRanks.put(level, StringUtils.colour(rank));
				}
			}
		}
	}

	/**
	 * Start the Course Live Timer.
	 * Will be enabled / displayed when the Scoreboard LiveTimer is enabled, or as a live Action Bar timer.
	 * Course Timer may increase or decrease based on whether the Course has a maximum time.
	 */
	private void startLiveTimerRunnable() {
		if (!parkour.getConfig().getBoolean("OnCourse.DisplayLiveTime")
				&& !(parkour.getConfig().getBoolean("Scoreboard.Enabled")
				&& parkour.getConfig().getBoolean("Scoreboard.LiveTimer.Enabled"))) {
			return;
		}

		Bukkit.getScheduler().runTaskTimer(parkour, () -> {
			for (Map.Entry<Player, ParkourSession> parkourPlayer : parkourPlayers.entrySet()) {
				Player player = parkourPlayer.getKey();
				ParkourSession session = parkourPlayer.getValue();

				if (!session.isStartTimer()) {
					continue;
				}

				Course course = session.getCourse();

				int seconds = session.calculateSeconds();
				String liveTimer = DateTimeUtils.convertSecondsToTime(seconds);

				if (course.hasMaxTime()) {
					parkour.getSoundsManager().playSound(player, SoundType.SECOND_DECREMENT);
					if (seconds <= 5 || seconds == 10) {
						liveTimer = ChatColor.RED + liveTimer;
					}
				} else {
					parkour.getSoundsManager().playSound(player, SoundType.SECOND_INCREMENT);
				}

				if (!PlayerInfo.isQuietMode(player)
						&& parkour.getConfig().getBoolean("OnCourse.DisplayLiveTime")) {
					parkour.getBountifulApi().sendActionBar(player, liveTimer, true);
				}

				parkour.getScoreboardManager().updateScoreboardTimer(player, liveTimer);

				if (course.hasMaxTime() && seconds <= 0) {
					session.setMarkedForDeletion(true);
					String maxTime = DateTimeUtils.convertSecondsToTime(course.getMaxTime());
					TranslationUtils.sendValueTranslation("Parkour.MaxTime", maxTime, player);
					leaveCourse(player);
				}
			}
		}, 0, 20);
	}

	private Location determineDestination(ParkourSession session) {
		if ((session.getParkourMode() == ParkourMode.FREEDOM || session.getParkourMode() == ParkourMode.FREE_CHECKPOINT)
				&& session.getFreedomLocation() != null) {
			return session.getFreedomLocation();
		} else {
			return session.getCheckpoint().getLocation();
		}
	}

	/**
	 * Reward ParkourLevel for Course completion.
	 * If the Course has a ParkourLevel or ParkourLevelIncrease set then update the Player's ParkourLevel.
	 * If the new ParkourLevel has passed the requirement for a new ParkourRank, the highest one will be awarded.
	 *
	 * @param player requesting player
	 * @param courseName course name
	 */
	private void rewardParkourLevel(Player player, String courseName) {
		int currentLevel = PlayerInfo.getParkourLevel(player);
		int newParkourLevel = currentLevel;

		// set parkour level
		int rewardLevel = CourseInfo.getRewardParkourLevel(courseName);
		if (rewardLevel > 0 && currentLevel < rewardLevel) {
			newParkourLevel = rewardLevel;
		}

		// increase parkour level
		int rewardAddLevel = CourseInfo.getRewardParkourLevelIncrease(courseName);
		if (rewardAddLevel > 0) {
			newParkourLevel = currentLevel + rewardAddLevel;
		}

		newParkourLevel = Math.min(newParkourLevel, parkour.getConfig().getInt("Other.Parkour.MaximumParkourLevel"));

		// if their parkour level has increased
		if (newParkourLevel > currentLevel) {
			// update parkour rank
			String rewardRank = getUnlockedParkourRank(player, newParkourLevel);
			if (rewardRank != null) {
				PlayerInfo.setParkourRank(player, rewardRank);
				TranslationUtils.sendValueTranslation("Parkour.RewardRank", rewardRank, player);
				Bukkit.getServer().getPluginManager().callEvent(
						new PlayerParkourRankEvent(player, courseName, rewardRank));
			}

			// update parkour level
			PlayerInfo.setParkourLevel(player, newParkourLevel);
			if (parkour.getConfig().getBoolean("Other.Display.LevelReward")) {
				player.sendMessage(TranslationUtils.getTranslation("Parkour.RewardLevel")
						.replace(PARKOUR_LEVEL_PLACEHOLDER, String.valueOf(newParkourLevel))
						.replace(COURSE_PLACEHOLDER, courseName));
			}
			Bukkit.getServer().getPluginManager().callEvent(
					new PlayerParkourLevelEvent(player, courseName, newParkourLevel));
		}
	}

	/**
	 * Teleport the Player after Course Completion.
	 * Based on config, the Player may or may not be teleported after completion.
	 * If the Course is linked, these will take priority.
	 *
	 * @param player requesting player
	 * @param courseName course name
	 */
	private void teleportCourseCompletion(Player player, String courseName) {
		if (CourseInfo.hasLinkedCourse(courseName)) {
			String linkedCourseName = CourseInfo.getLinkedCourse(courseName);
			joinCourse(player, linkedCourseName);
			return;

		} else if (CourseInfo.hasLinkedLobby(courseName)) {
			String lobbyName = CourseInfo.getLinkedLobby(courseName);

			if (LobbyInfo.doesLobbyExist(lobbyName)) {
				parkour.getLobbyManager().joinLobby(player, lobbyName);
				return;
			}

		} else if (parkour.getConfig().isTeleportToJoinLocation()) {
			PlayerUtils.teleportToLocation(player, PlayerInfo.getJoinLocation(player));
			TranslationUtils.sendTranslation("Parkour.JoinLocation", player);
			return;
		}

		parkour.getLobbyManager().joinLobby(player, DEFAULT);
	}

	/**
	 * Validate if their session is still valid.
	 * Try to get their existing session and check the name matches.
	 *
	 * @param player player
	 * @param course course
	 * @return player can load ParkourSession
	 */
	private boolean canLoadParkourSession(Player player, Course course) {
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

	private void teardownParkourMode(Player player) {
		ParkourMode courseMode = getParkourSession(player).getParkourMode();

		if (courseMode == ParkourMode.NONE) {
			return;
		}

		if (courseMode == ParkourMode.SPEEDY) {
			float speed = Float.parseFloat(parkour.getConfig().getString("ParkourModes.Speedy.ResetSpeed"));
			player.setWalkSpeed(speed);
		}
	}

	private void displayJoinMessage(Player player, boolean silent, ParkourSession session) {
		Course course = session.getCourse();

		if (!PlayerInfo.isQuietMode(player) && !silent) {
			boolean displayTitle = parkour.getConfig().getBoolean("DisplayTitle.JoinCourse");

			String subTitle = "";
			if (course.hasMaxDeaths() && course.hasMaxTime()) {
				subTitle = TranslationUtils.getTranslation("Parkour.JoinLivesAndTime", false)
						.replace("%LIVES%", String.valueOf(course.getMaxDeaths()))
						.replace("%MAXTIME%", DateTimeUtils.convertSecondsToTime(course.getMaxTime()));

			} else if (course.hasMaxDeaths()) {
				subTitle = TranslationUtils.getValueTranslation(
						"Parkour.JoinLives", String.valueOf(course.getMaxDeaths()), false);

			} else if (course.hasMaxTime()) {
				subTitle = TranslationUtils.getValueTranslation("Parkour.JoinTime",
						DateTimeUtils.convertSecondsToTime(course.getMaxTime()), false);
			}

			parkour.getBountifulApi().sendFullTitle(player,
					TranslationUtils.getCourseEventMessage(session, JOIN, "Parkour.Join"),
					subTitle, displayTitle);

			if (parkour.getConfig().isCompletedCoursesEnabled()
					&& PlayerInfo.hasCompletedCourse(player, course.getName())
					&& parkour.getConfig().getBoolean("OnFinish.CompletedCourses.JoinMessage")) {
				TranslationUtils.sendValueTranslation("Parkour.AlreadyCompleted",
						course.getDisplayName(), player);
			}

			String joinBroadcast = TranslationUtils.replaceAllParkourPlaceholders(
					TranslationUtils.getTranslation("Parkour.JoinBroadcast"), player, session);

			String scope = parkour.getConfig().getString("OnJoin.BroadcastLevel");
			TranslationUtils.announceParkourMessage(player, scope, joinBroadcast);
		}
	}

	/**
	 * Add Player to Hidden Players.
	 * @param player requesting player
	 */
	private void addHidden(Player player) {
		hiddenPlayers.add(player);
	}

	/**
	 * Remove Player from Hidden Players.
	 * @param player requesting player
	 */
	private void removeHidden(Player player) {
		hiddenPlayers.remove(player);
	}

	public void setManualCheckpoint(Player player) {
		if (!isPlaying(player)) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);
			return;
		}

		ParkourSession session = getParkourSession(player);

		if (ParkourMode.FREE_CHECKPOINT == session.getParkourMode()
				&& parkour.getConfig().getBoolean("ParkourModes.FreeCheckpoint.ManualCheckpointCommandEnabled")) {
			session.setFreedomLocation(player.getLocation());
			TranslationUtils.sendTranslation("Event.FreeCheckpoints", player);

		} else {
			TranslationUtils.sendMessage(player, "You are currently unable to set a Checkpoint.");
		}
	}
}

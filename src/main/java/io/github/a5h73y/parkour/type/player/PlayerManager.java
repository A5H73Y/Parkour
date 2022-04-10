package io.github.a5h73y.parkour.type.player;

import static io.github.a5h73y.parkour.other.ParkourConstants.COURSE_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_INVALID_AMOUNT;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_UNKNOWN_PLAYER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PARKOUR_LEVEL_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TEST_MODE;
import static io.github.a5h73y.parkour.type.course.ParkourEventType.CHECKPOINT;
import static io.github.a5h73y.parkour.type.course.ParkourEventType.CHECKPOINT_ALL;
import static io.github.a5h73y.parkour.type.course.ParkourEventType.DEATH;
import static io.github.a5h73y.parkour.type.course.ParkourEventType.FINISH;
import static io.github.a5h73y.parkour.type.course.ParkourEventType.GLOBAL_COURSE_RECORD;
import static io.github.a5h73y.parkour.type.course.ParkourEventType.JOIN;
import static io.github.a5h73y.parkour.type.course.ParkourEventType.LEAVE;
import static io.github.a5h73y.parkour.type.course.ParkourEventType.NO_PRIZE;
import static io.github.a5h73y.parkour.type.course.ParkourEventType.PLAYER_COURSE_RECORD;
import static io.github.a5h73y.parkour.type.course.ParkourEventType.PRIZE;
import static io.github.a5h73y.parkour.utility.TranslationUtils.sendConditionalValue;
import static io.github.a5h73y.parkour.utility.TranslationUtils.sendValue;

import com.cryptomorin.xseries.XPotion;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.CommandProcessor;
import io.github.a5h73y.parkour.conversation.SetPlayerConversation;
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
import io.github.a5h73y.parkour.other.TriConsumer;
import io.github.a5h73y.parkour.type.Initializable;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.course.ParkourEventType;
import io.github.a5h73y.parkour.type.kit.ParkourKit;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.type.sounds.SoundType;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
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
public class PlayerManager extends AbstractPluginReceiver implements Initializable, CommandProcessor {

	private final Map<UUID, Long> playerDelay = new HashMap<>();
	// player actions to set data
	private final Map<String, TriConsumer<CommandSender, OfflinePlayer, String>> playerActions = new HashMap<>();

	/**
	 * Initialise the Parkour Player Manager.
	 * @param parkour plugin instance
	 */
	public PlayerManager(final Parkour parkour) {
		super(parkour);
		populateSetPlayerActions();
	}

	@Override
	public int getInitializeSequence() {
		return 1;
	}

	@Override
	public void initialize() {
		startLiveTimerRunnable();
	}

	public Set<String> getSetPlayerActions() {
		return playerActions.keySet();
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
		PlayerConfig playerConfig = parkour.getConfigManager().getPlayerConfig(player);
		playerConfig.setPlayerDataSnapshot(player);
		playerConfig.setLastPlayedCourse(course.getName());

		if (parkour.getParkourConfig().getBoolean("OnJoin.TeleportPlayer")) {
			PlayerUtils.teleportToLocation(player, course.getCheckpoints().get(0).getLocation());
		}
		preparePlayerForCourse(player, course.getName());

		// already on a different course
		if (parkour.getParkourSessionManager().isPlaying(player)
				&& !parkour.getParkourSessionManager().getParkourSession(player).getCourseName().equals(course.getName())) {
			parkour.getParkourSessionManager().removePlayer(player);
		}

		// set up their session
		ParkourSession session;
		if (parkour.getParkourSessionManager().hasValidParkourSessionFile(player, course)) {
			session = parkour.getParkourSessionManager().loadParkourSession(player, course.getName());
			PlayerUtils.teleportToLocation(player, determineJoinDestination(session));
			TranslationUtils.sendValueTranslation("Parkour.Continue", session.getCourse().getDisplayName(), player);
		} else {
			session = parkour.getParkourSessionManager().addPlayer(player, new ParkourSession(course));
		}

		displayJoinMessages(player, session);
		setupParkourMode(player);

		parkour.getScoreboardManager().addScoreboard(player, session);
		if (!silent) {
			parkour.getCourseManager().runEventCommands(player, session, JOIN);
		}
		if (!parkour.getParkourConfig().isTreatFirstCheckpointAsStart()
				&& !parkour.getChallengeManager().hasPlayerBeenChallenged(player)) {
			session.setStartTimer(true);
		}

		parkour.getSoundsManager().playSound(player, SoundType.JOIN_COURSE);
		parkour.getConfigManager().getCourseConfig(course.getName()).incrementViews();
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
		if (!parkour.getParkourSessionManager().isPlaying(player)) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);
			return;
		}

		ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

		if (parkour.getParkourConfig().isLeaveDestroyCourseProgress()
				|| !parkour.getConfigManager().getCourseConfig(session.getCourseName()).getResumable()) {
			session.setMarkedForDeletion(true);
		}

		teardownParkourMode(player);
		if (session.isMarkedForDeletion()) {
			parkour.getParkourSessionManager().deleteParkourSession(player, session.getCourseName());
			parkour.getParkourSessionManager().removePlayer(player);
		} else {
			parkour.getParkourSessionManager().saveParkourSession(player, true);
		}

		PlayerConfig playerConfig = parkour.getConfigManager().getPlayerConfig(player);

		if (!silent) {
			parkour.getSoundsManager().playSound(player, SoundType.COURSE_FAILED);
			parkour.getBountifulApi().sendSubTitle(player,
					TranslationUtils.getCourseEventMessage(session, LEAVE, "Parkour.Leave"),
					parkour.getParkourConfig().getBoolean("DisplayTitle.Leave"));

			if (parkour.getParkourConfig().getBoolean("OnLeave.TeleportAway")) {
				if (parkour.getParkourConfig().isTeleportToJoinLocation()) {
					PlayerUtils.teleportToLocation(player, playerConfig.getSnapshotJoinLocation());
				} else {
					parkour.getLobbyManager().teleportToLeaveDestination(player, session);
				}
			}
			parkour.getCourseManager().runEventCommands(player, session, LEAVE);
		}
		prepareParkourPlayer(player);
		restorePlayerData(player, playerConfig);

		parkour.getParkourSessionManager().forceVisible(player);
		parkour.getScoreboardManager().removeScoreboard(player);
		parkour.getChallengeManager().forfeitChallenge(player);
		playerConfig.resetPlayerDataSnapshot();
		playerConfig.removeExistingSessionCourseName();

		Bukkit.getServer().getPluginManager().callEvent(
				new PlayerLeaveCourseEvent(player, session.getCourse().getName(), silent));
	}

	private void setGameMode(Player player, String gameModeName) {
		if (!parkour.getParkourSessionManager().isPlayerInTestMode(player)) {
			PlayerUtils.setGameMode(player, gameModeName);
		}
	}

	private void restoreGameMode(Player player) {
		String gameMode = parkour.getParkourConfig().getString("OnFinish.SetGameMode");

		if (gameMode.equalsIgnoreCase("RESTORE")) {
			PlayerConfig playerConfig = parkour.getConfigManager().getPlayerConfig(player);
			if (PluginUtils.doesGameModeExist(playerConfig.getSnapshotGameMode())) {
				PlayerUtils.setGameMode(player, playerConfig.getSnapshotGameMode());
				return;
			}
		}

		setGameMode(player, gameMode);
	}

	/**
	 * Manually Increase Player Checkpoint.
	 * Set the Player's checkpoint to the checkpoint provided.
	 * @param player player
	 * @param checkpoint checkpoint
	 */
	public void manuallyIncreaseCheckpoint(Player player, int checkpoint) {
		if (!parkour.getParkourSessionManager().isPlaying(player)) {
			TranslationUtils.sendTranslation("Error.NotOnCourse", player);
			return;
		}

		ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

		if (session.hasAchievedAllCheckpoints()
				|| session.getCurrentCheckpoint() >= checkpoint
				|| session.getCurrentCheckpoint() + 1 < checkpoint) {
			return;
		}

		increaseCheckpoint(player, null);
	}

	/**
	 * Increase ParkourSession Checkpoint.
	 * The Player will be notified and their Session Checkpoint will be increased.
	 *
	 * @param player requesting player.
	 */
	public void increaseCheckpoint(Player player, @Nullable Integer desiredCheckpoint) {
		ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

		if (session == null) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);
			return;
		}

		if (desiredCheckpoint != null && desiredCheckpoint > session.getCourse().getNumberOfCheckpoints()) {
			TranslationUtils.sendMessage(player, "Invalid Checkpoint number.");
			return;
		}

		if (desiredCheckpoint == null) {
			session.increaseCheckpoint();
		} else {
			session.setCurrentCheckpoint(desiredCheckpoint);
		}
		parkour.getCourseManager().runEventCommands(player, session, CHECKPOINT);

		ParkourEventType eventType = CHECKPOINT;
		String checkpointTranslation = "Event.Checkpoint";

		if (session.hasAchievedAllCheckpoints()) {
			if (parkour.getParkourConfig().getBoolean("OnCourse.TreatLastCheckpointAsFinish")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(parkour, () -> finishCourse(player));
				return;
			}

			parkour.getCourseManager().runEventCommands(player, session, CHECKPOINT_ALL);
			eventType = CHECKPOINT_ALL;
			checkpointTranslation = "Event.AllCheckpoints";
		}

		parkour.getSoundsManager().playSound(player, SoundType.CHECKPOINT_ACHIEVED);
		parkour.getScoreboardManager().updateScoreboardCheckpoints(player, session);

		boolean showTitle = parkour.getParkourConfig().getBoolean("DisplayTitle.Checkpoint");

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
		if (!parkour.getParkourSessionManager().isPlaying(player)) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);
			return;
		}

		ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);
		parkour.getSoundsManager().playSound(player, SoundType.PLAYER_DEATH);
		session.increaseDeath();

		if (session.getCourse().getSettings().hasMaxDeaths()) {
			if (session.getCourse().getSettings().getMaxDeaths() > session.getDeaths()) {
				parkour.getBountifulApi().sendSubTitle(player,
						TranslationUtils.getValueTranslation("Parkour.LifeCount",
								String.valueOf(session.getRemainingDeaths()), false),
						parkour.getParkourConfig().getBoolean("DisplayTitle.Death"));

			} else {
				TranslationUtils.sendValueTranslation("Parkour.MaxDeaths",
						String.valueOf(session.getCourse().getSettings().getMaxDeaths()), player);
				leaveCourse(player);
				return;
			}
		}

		PlayerUtils.teleportToLocation(player, determineJoinDestination(session));

		// if the Player is in Test Mode, we don't need to run the rest
		if (parkour.getParkourSessionManager().isPlayerInTestMode(player)) {
			TranslationUtils.sendTranslation("Parkour.Die1", player);
			return;
		}

		parkour.getCourseManager().runEventCommands(player, session, DEATH);
		parkour.getScoreboardManager().updateScoreboardDeaths(player, session.getDeaths(), session.getRemainingDeaths());

		boolean inQuietMode = parkour.getQuietModeManager().isQuietMode(player);
		// they haven't yet achieved a checkpoint
		if (session.getCurrentCheckpoint() == 0 && session.getFreedomLocation() == null) {
			if (parkour.getParkourConfig().getBoolean("OnDie.ResetProgressWithNoCheckpoint")) {
				session.resetProgress();
			}

			if (!inQuietMode) {
				StringBuilder message = new StringBuilder(
						TranslationUtils.getCourseEventMessage(session, DEATH, "Parkour.Die1"));

				if (parkour.getParkourConfig().getBoolean("OnDie.ResetProgressWithNoCheckpoint")) {
					if (message.length() != 0) {
						message.append(" ");
					}
					message.append(TranslationUtils.getTranslation("Parkour.TimeReset", false));
				}

				TranslationUtils.sendMessage(player, message.toString());
			}
		} else if (!inQuietMode) {
			TranslationUtils.sendValueTranslation("Parkour.Die2",
					String.valueOf(session.getCurrentCheckpoint()), player);
		}

		if (parkour.getParkourConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			player.setLevel(session.getDeaths());
		}

		prepareParkourPlayer(player);
		setGameMode(player, parkour.getParkourConfig().getString("OnJoin.SetGameMode"));
		Bukkit.getServer().getPluginManager().callEvent(new PlayerDeathEvent(player, session.getCourse().getName()));
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
		parkour.getParkourSessionManager().removeHiddenPlayer(player);
		parkour.getParkourSessionManager().saveParkourSession(player, true);
		playerDelay.remove(player.getUniqueId());
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
		if (!parkour.getParkourSessionManager().isPlayingParkourCourse(player)) {
			return;
		}

		ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

		if (parkour.getParkourConfig().getBoolean("OnFinish.EnforceCompletion")
				&& !session.hasAchievedAllCheckpoints()) {

			TranslationUtils.sendTranslation("Error.Cheating1", player);
			TranslationUtils.sendValueTranslation("Error.Cheating2",
					String.valueOf(session.getCourse().getNumberOfCheckpoints()), player);
			playerDie(player);
			return;
		}

		final String courseName = session.getCourse().getName();
		session.markTimeFinished();
		Bukkit.getServer().getPluginManager().callEvent(new PlayerFinishCourseEvent(player, courseName));

		announceCourseFinishMessage(player, session);
		teardownParkourMode(player);
		parkour.getParkourSessionManager().removePlayer(player);
		prepareParkourPlayer(player);
		parkour.getChallengeManager().completeChallenge(player);
		parkour.getSoundsManager().playSound(player, SoundType.COURSE_FINISHED);
		PlayerConfig playerConfig = parkour.getConfigManager().getPlayerConfig(player);

		Bukkit.getScheduler().scheduleSyncDelayedTask(parkour, () -> {
			parkour.getScoreboardManager().removeScoreboard(player);
			if (parkour.getParkourConfig().getBoolean("OnFinish.TeleportBeforePrize")) {
				teleportCourseCompletion(player, courseName);
				restorePlayerData(player, playerConfig);
				rewardPrize(player, session);
			} else {
				restorePlayerData(player, playerConfig);
				rewardPrize(player, session);
				teleportCourseCompletion(player, courseName);
			}
			playerConfig.resetPlayerDataSnapshot();
		}, parkour.getParkourConfig().getLong("OnFinish.TeleportDelay"));

		playerConfig.setLastCompletedCourse(courseName);
		playerConfig.setExistingSessionCourseName(null);

		submitPlayerLeaderboard(player, session);
		parkour.getParkourSessionManager().forceVisible(player);
		parkour.getParkourSessionManager().deleteParkourSession(player, courseName);
		parkour.getCourseManager().runEventCommands(player, session, FINISH);

		parkour.getConfigManager().getCourseCompletionsConfig().addCompletedCourse(player, courseName);
		parkour.getConfigManager().getCourseConfig(courseName).incrementCompletions();
	}

	/**
	 * Restart Course progress.
	 * Will trigger a silent leave and rejoin of the Course.
	 *
	 * @param player requesting player
	 */
	public void restartCourse(Player player) {
		if (!parkour.getParkourSessionManager().isPlaying(player)) {
			return;
		}

		ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);
		Course course = session.getCourse();

		if (parkour.getParkourConfig().getBoolean("OnRestart.FullPlayerRestart")) {
			leaveCourse(player, true);
			parkour.getParkourSessionManager().deleteParkourSession(player, course.getName());
			joinCourse(player, course, true);

			// if they are restarting the Course, we need to teleport them back
			// this is because the joinCourse will not teleport the Player if disabled
			if (!parkour.getParkourConfig().getBoolean("OnJoin.TeleportPlayer")) {
				PlayerUtils.teleportToLocation(player, course.getCheckpoints().get(0).getLocation());
			}
		} else {
			session.resetProgress();
			session.setFreedomLocation(null);
			preparePlayerForCourse(player, course.getName());
			PlayerUtils.teleportToLocation(player, session.getCheckpoint().getLocation());
			parkour.getScoreboardManager().addScoreboard(player, session);

			if (parkour.getParkourConfig().isTreatFirstCheckpointAsStart()) {
				session.setStartTimer(false);
			}
		}

		boolean displayTitle = parkour.getParkourConfig().getBoolean("DisplayTitle.JoinCourse");
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
		if (!parkour.getParkourConfig().getBoolean("OnFinish.EnablePrizes")) {
			return;
		}

		CourseConfig courseConfig = parkour.getConfigManager().getCourseConfig(courseName);

		if (courseConfig.getRewardOnce() && parkour.getDatabaseManager().hasPlayerAchievedTime(player, courseName)) {
			parkour.getCourseManager().runEventCommands(player, session, NO_PRIZE);
			return;
		}

		// check if the Course has a reward delay
		if (courseConfig.hasRewardDelay()) {
			// if the player has not exceeded the Course delay, no prize will be given
			if (!hasPrizeCooldownDurationPassed(player, courseName, true)) {
				return;
			}
			// otherwise make a note of last time rewarded, and let them continue
			parkour.getConfigManager().getPlayerConfig(player).setLastRewardedTime(courseName, System.currentTimeMillis());
		}

		ItemStack courseMaterialPrize = getCourseMaterialPrize(courseConfig);
		if (courseMaterialPrize != null) {
			player.getInventory().addItem(courseMaterialPrize);
		}
		player.giveExp(getCourseXpPrize(courseConfig));
		rewardParkourLevel(player, courseName);
		rewardParkoins(player, courseConfig.getRewardParkoins());
		parkour.getEconomyApi().giveEconomyPrize(player, courseName);
		parkour.getCourseManager().runEventCommands(player, session, PRIZE);
		player.updateInventory();
	}

	private ItemStack getCourseMaterialPrize(CourseConfig courseConfig) {
		ItemStack result = null;
		Material material;
		int amount;

		if (courseConfig.hasMaterialPrize()) {
			material = courseConfig.getMaterialPrize();
			amount = courseConfig.getMaterialPrizeAmount();

		} else {
			material = MaterialUtils.lookupMaterial(parkour.getParkourConfig().getString("CourseDefault.Prize.Material"));
			amount = parkour.getParkourConfig().getOrDefault("CourseDefault.Prize.Amount", 0);
		}

		if (material != null && amount > 0) {
			result = new ItemStack(material, amount);
		}
		return result;
	}

	private int getCourseXpPrize(CourseConfig courseConfig) {
		int xp = courseConfig.getXpPrize();

		if (xp == 0) {
			xp = parkour.getParkourConfig().getInt("CourseDefault.Prize.XP");
		}

		return xp;
	}

	/**
	 * Rocket Launch the Player.
	 * Will apply a fake explosion to the player and give them velocity.
	 * The direction of the velocity can be configured.
	 *
	 * @param player target player
	 */
	public void rocketLaunchPlayer(Player player) {
		double force = parkour.getParkourConfig().getDouble("ParkourModes.Rockets.LaunchForce");
		if (parkour.getParkourConfig().getBoolean("ParkourModes.Rockets.Invert")) {
			force = -force;
		}

		Vector velocity = player.getLocation().getDirection().normalize();
		velocity = velocity.multiply(force);
		velocity = velocity.setY(velocity.getY() / 2);
		player.setVelocity(velocity);
		player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 500);
		parkour.getSoundsManager().playSound(player, SoundType.RELOAD_ROCKET);
	}

	private void submitPlayerLeaderboard(Player player, ParkourSession session) {
		TimeResult timeResult = calculateTimeResult(player, session);

		parkour.getDatabaseManager().insertOrUpdateTime(session.getCourseName(), player, session.getTimeFinished(),
				session.getDeaths(), timeResult != TimeResult.NONE);

		if (timeResult != TimeResult.NONE) {
			ParkourEventType eventType = timeResult == TimeResult.GLOBAL_BEST
					? GLOBAL_COURSE_RECORD : PLAYER_COURSE_RECORD;
			String fallbackKey = timeResult == TimeResult.GLOBAL_BEST ? "Parkour.CourseRecord" : "Parkour.BestTime";

			parkour.getPlaceholderApi().clearCache();
			parkour.getCourseManager().runEventCommands(player, session, eventType);

			if (parkour.getParkourConfig().getBoolean("OnFinish.DisplayNewRecords")) {
				String displayTime = DateTimeUtils.displayCurrentTime(session.getTimeFinished());

				parkour.getBountifulApi().sendFullTitle(player,
						TranslationUtils.getCourseEventMessage(session, eventType, fallbackKey),
						displayTime, false);
			}
		}
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

		if (!playerDelay.containsKey(player.getUniqueId())) {
			playerDelay.put(player.getUniqueId(), System.currentTimeMillis());
			return true;
		}

		long lastAction = playerDelay.get(player.getUniqueId());
		int secondsElapsed = (int) ((System.currentTimeMillis() - lastAction) / 1000);

		if (secondsElapsed >= secondsToWait) {
			playerDelay.put(player.getUniqueId(), System.currentTimeMillis());
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
		double rewardDelay = parkour.getConfigManager().getCourseConfig(courseName).getRewardDelay();

		if (rewardDelay <= 0) {
			return true;
		}

		long lastRewardTime = parkour.getConfigManager().getPlayerConfig(player).getLastRewardedTime(courseName);

		if (lastRewardTime <= 0) {
			return true;
		}

		long timeDifference = System.currentTimeMillis() - lastRewardTime;
		long hoursDelay = DateTimeUtils.convertHoursToMilliseconds(rewardDelay);

		if (timeDifference > hoursDelay) {
			return true;
		}

		if (parkour.getParkourConfig().isDisplayPrizeCooldown() && displayMessage) {
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
	 */
	public void prepareParkourPlayer(Player player) {
		if (parkour.getParkourConfig().getBoolean("Other.Parkour.ResetPotionEffects")) {
			PlayerUtils.removeAllPotionEffects(player);
		}

		ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

		if (session != null && session.getParkourMode() == ParkourMode.POTION) {
			XPotion.addEffects(player,
					parkour.getConfigManager().getCourseConfig(session.getCourseName()).getPotionParkourModeEffects());
		}

		Damageable playerDamage = player;
		playerDamage.setHealth(playerDamage.getMaxHealth());
		player.setFallDistance(0);
		player.setFireTicks(0);
		player.eject();
	}

	/**
	 * Load the Player's original Inventory.
	 * When they leave or finish a course, their inventory and armour will be restored to them.
	 * Will delete the inventory from the config once loaded.
	 *
	 * @param player player
	 */
	public void restoreInventoryArmor(Player player, PlayerConfig playerConfig) {
		if (!parkour.getParkourConfig().getBoolean("Other.Parkour.InventoryManagement")) {
			return;
		}

		ItemStack[] inventoryContents = playerConfig.getSnapshotInventory();

		if (inventoryContents == null) {
			TranslationUtils.sendMessage(player, "No saved inventory to load.");
			return;
		}

		player.getInventory().clear();
		player.getInventory().setContents(inventoryContents);

		ItemStack[] armorContents = playerConfig.getSnapshotArmor();
		player.getInventory().setArmorContents(armorContents);
		player.updateInventory();
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

		PlayerConfig playerConfig = parkour.getConfigManager().getPlayerConfig(player);
		playerConfig.increaseParkoins(parkoins);
		player.sendMessage(TranslationUtils.getTranslation("Parkour.RewardParkoins")
				.replace(ParkourConstants.AMOUNT_PLACEHOLDER, String.valueOf(parkoins))
				.replace("%TOTAL%", String.valueOf(playerConfig.getParkoins())));
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

		PlayerConfig playerConfig = parkour.getConfigManager().getPlayerConfig(player);
		double current = playerConfig.getParkoins();
		double amountToDeduct = Math.min(current, parkoins);

		playerConfig.setParkoins(current - amountToDeduct);
		TranslationUtils.sendMessage(player, parkoins + " Parkoins deducted! New total: &b" + playerConfig.getParkoins());
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
		if (parkour.getParkourSessionManager().isPlaying(player)) {
			if (parkour.getParkourSessionManager().isPlayerInTestMode(player)) {
				parkour.getParkourSessionManager().removePlayer(player);
				parkour.getBountifulApi().sendActionBar(player,
						TranslationUtils.getTranslation("Parkour.TestModeOff", false));
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
						new Course(TEST_MODE, TEST_MODE, checkpoints, kit, ParkourMode.NONE, null)); //TODO
				parkour.getParkourSessionManager().addPlayer(player, session);
				parkour.getBountifulApi().sendActionBar(player, TranslationUtils.getValueTranslation(
						"Parkour.TestModeOn", kitName, false));
			}
		}
	}

	/**
	 * Display Parkour Player's Information.
	 * Finds and displays the target player's stored statistics and any current Course information.
	 *
	 * @param commandSender command sender
	 * @param targetPlayer target layer
	 */
	public void displayParkourInfo(CommandSender commandSender, OfflinePlayer targetPlayer) {
		if (!PlayerConfig.hasPlayerConfig(targetPlayer)) {
			TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, commandSender);
			return;
		}

		PlayerConfig playerConfig = PlayerConfig.getConfig(targetPlayer);
		ParkourSession session = parkour.getParkourSessionManager().getParkourSession(targetPlayer.getPlayer());
		TranslationUtils.sendHeading(targetPlayer.getName() + "'s information", commandSender);

		if (session != null) {
			sendValue(commandSender, "Course", session.getCourse().getName());
			sendValue(commandSender, "Deaths", session.getDeaths());
			sendValue(commandSender, "Time", session.getDisplayTime());
			sendValue(commandSender, "Checkpoint", session.getCurrentCheckpoint());
		}

		sendConditionalValue(commandSender, "ParkourLevel", playerConfig.getParkourLevel());
		sendConditionalValue(commandSender, "ParkourRank", StringUtils.colour(playerConfig.getParkourRank()));
		sendConditionalValue(commandSender, "Parkoins", playerConfig.getParkoins());
		sendConditionalValue(commandSender, "Editing", playerConfig.getSelectedCourse());

		int completedCourses = parkour.getConfigManager().getCourseCompletionsConfig().getNumberOfCompletedCourses(targetPlayer);

		sendValue(commandSender, "Courses Completed",
				completedCourses + " / " + parkour.getCourseManager().getCourseNames().size());

		if (PermissionUtils.hasPermission(commandSender, Permission.ADMIN_ALL, false)) {
			sendValue(commandSender, "Config Path", parkour.getParkourConfig().getPlayerConfigName(targetPlayer));
		}
	}

	/**
	 * Set the Player's ParkourLevel.
	 * Used by administrators to manually set the ParkourLevel of a Player.
	 *
	 * @param commandSender command sender
	 * @param targetPlayer target player
	 * @param value desired parkour level
	 */
	public void setParkourLevel(CommandSender commandSender, OfflinePlayer targetPlayer, String value, boolean addition) {
		if (!ValidationUtils.isPositiveInteger(value)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		if (!PlayerConfig.hasPlayerConfig(targetPlayer)) {
			TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, commandSender);
			return;
		}

		PlayerConfig playerConfig = PlayerConfig.getConfig(targetPlayer);
		int newLevel = Integer.parseInt(value);
		if (addition) {
			newLevel += playerConfig.getParkourLevel();
		}
		newLevel = Math.min(newLevel, parkour.getParkourConfig().getInt("Other.Parkour.MaximumParkourLevel"));
		playerConfig.setParkourLevel(newLevel);
		TranslationUtils.sendMessage(commandSender, targetPlayer.getName() + "'s ParkourLevel was set to &b" + newLevel);

		if (parkour.getParkourConfig().getBoolean("Other.OnSetPlayerParkourLevel.UpdateParkourRank")) {
			String parkourRank = parkour.getParkourRankManager().getUnlockedParkourRank(targetPlayer, newLevel);
			if (parkourRank != null) {
				setParkourRank(commandSender, targetPlayer, parkourRank);
			}
		}
	}

	/**
	 * Set the Player's ParkourRank.
	 * Used by administrators to manually set the ParkourRank of a Player.
	 *
	 * @param commandSender command sender
	 * @param targetPlayer target player
	 * @param value desired parkour rank
	 */
	public void setParkourRank(CommandSender commandSender, OfflinePlayer targetPlayer, String value) {
		if (!PlayerConfig.hasPlayerConfig(targetPlayer)) {
			TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, commandSender);
			return;
		}

		PlayerConfig.getConfig(targetPlayer).setParkourRank(value);
		TranslationUtils.sendMessage(commandSender, targetPlayer.getName() + "'s Parkour was set to " + value);
	}

	/**
	 * Display a summary of the Parkour Players.
	 * Each of the online Parkour Players will have their session details displayed.
	 *
	 * @param commandSender command sender
	 */
	public void displayParkourPlayers(CommandSender commandSender) {
		int parkourPlayers = parkour.getParkourSessionManager().getNumberOfParkourPlayers();
		if (parkourPlayers == 0) {
			TranslationUtils.sendMessage(commandSender, "Nobody is playing Parkour!");
			return;
		}

		TranslationUtils.sendMessage(commandSender, parkourPlayers + " players using Parkour: ");

		String playingMessage = TranslationUtils.getTranslation("Parkour.Playing", false);
		for (Map.Entry<UUID, ParkourSession> entry : parkour.getParkourSessionManager().getParkourPlayers().entrySet()) {
			commandSender.sendMessage(TranslationUtils.replaceAllParkourPlaceholders(
					playingMessage, Bukkit.getPlayer(entry.getKey()), entry.getValue()));
		}
	}

	/**
	 * Process the "setplayer" Command.
	 *
	 * @param commandSender command sender
	 * @param args command arguments
	 */
	public void processSetCommand(CommandSender commandSender, String... args) {
		OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);

		if (!PlayerConfig.hasPlayerConfig(targetPlayer)) {
			TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, commandSender);
			return;
		}

		if (args.length == 2 && commandSender instanceof Player) {
			new SetPlayerConversation((Player) commandSender).withTargetPlayerName(args[1].toLowerCase()).begin();

		} else if (args.length >= 4) {
			processCommand(commandSender, targetPlayer, args[2], args[3]);

		} else {
			parkour.getParkourCommands().sendInvalidSyntax(commandSender, "setplayer");
		}
	}

	/**
	 * Has Player selected a known Course.
	 *
	 * @param player player
	 * @return selected valid course
	 */
	public boolean hasSelectedValidCourse(Player player) {
		String selected = parkour.getConfigManager().getPlayerConfig(player).getSelectedCourse();
		return parkour.getCourseManager().doesCourseExist(selected);
	}

	/**
	 * Reset the Player's Parkour Information.
	 *
	 * @param commandSender command sender
	 * @param targetPlayerId target player identifier
	 */
	public void resetPlayer(CommandSender commandSender, String targetPlayerId) {
		OfflinePlayer targetPlayer;

		if (ValidationUtils.isUuidFormat(targetPlayerId)) {
			targetPlayer = Bukkit.getOfflinePlayer(UUID.fromString(targetPlayerId));
		} else {
			targetPlayer = Bukkit.getOfflinePlayer(targetPlayerId);
		}

		if (!PlayerConfig.hasPlayerConfig(targetPlayer)) {
			TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, commandSender);
			return;
		}

		resetPlayer(targetPlayer);
		TranslationUtils.sendValueTranslation("Parkour.Reset", targetPlayerId, commandSender);
		PluginUtils.logToFile(targetPlayerId + " player was reset by " + commandSender.getName());
	}

	/**
	 * Reset the Player's Parkour data.
	 * All stats, current course info and database records will be deleted.
	 * @param targetPlayer target player
	 */
	public void resetPlayer(OfflinePlayer targetPlayer) {
		PlayerConfig.deletePlayerData(targetPlayer);
		parkour.getParkourSessionManager().deleteParkourSessions(targetPlayer);
		parkour.getDatabaseManager().deletePlayerTimes(targetPlayer);
		parkour.getPlaceholderApi().clearCache();
		parkour.getParkourSessionManager().removePlayer(targetPlayer.getPlayer());
	}

	/**
	 * Prepare the Player for the ParkourMode.
	 *
	 * @param player target player
	 */
	public void setupParkourMode(Player player) {
		ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);
		ParkourMode courseMode = session.getParkourMode();

		if (courseMode == ParkourMode.NONE) {
			return;
		}

		if (courseMode == ParkourMode.FREEDOM) {
			TranslationUtils.sendTranslation("Mode.Freedom.JoinText", player);
			giveParkourTool(player, "ParkourTool.Freedom");

		} else if (courseMode == ParkourMode.SPEEDY) {
			float speed = Float.parseFloat(parkour.getParkourConfig().getString("ParkourModes.Speedy.SetSpeed"));
			player.setWalkSpeed(speed);

		} else if (courseMode == ParkourMode.ROCKETS) {
			TranslationUtils.sendTranslation("Mode.Rockets.JoinText", player);
			giveParkourTool(player, "ParkourTool.Rockets");

		} else if (courseMode == ParkourMode.POTION) {
			CourseConfig courseConfig = parkour.getConfigManager().getCourseConfig(session.getCourseName());
			XPotion.addEffects(player, courseConfig.getPotionParkourModeEffects());
			TranslationUtils.sendMessage(player, courseConfig.getPotionJoinMessage());
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
		Material material = Material.getMaterial(parkour.getParkourConfig()
				.getOrDefault(configPath + ".Material", "AIR").toUpperCase());

		if (material != null && material != Material.AIR && !player.getInventory().contains(material)) {
			int slot = parkour.getParkourConfig().getInt(configPath + ".Slot");
			player.getInventory().setItem(slot, MaterialUtils.createItemStack(material,
					TranslationUtils.getTranslation(translationKey, false)));
		}
	}

	/**
	 * Give Player Parkour Tool.
	 *
	 * @param player player
	 * @param configPath config path to tool Material with matching translation path.
	 */
	public void giveParkourTool(Player player, String configPath) {
		giveParkourTool(player, configPath, configPath);
	}

	/**
	 * Set a Manual Checkpoint at the Player's Location.
	 * @param player player
	 */
	public void setManualCheckpoint(Player player) {
		if (!parkour.getParkourSessionManager().isPlaying(player)) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);
			return;
		}

		ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

		if (session.getCourse().getSettings().isManualCheckpoints()) {
			session.setFreedomLocation(player.getLocation());
			TranslationUtils.sendTranslation("Event.FreeCheckpoints", player);

		} else {
			TranslationUtils.sendMessage(player, "You are currently unable to set a manual Checkpoint.");
		}
	}

	public int getNumberOfUncompletedCourses(OfflinePlayer player) {
		return getUncompletedCourses(player).size();
	}

	/**
	 * Get the Uncompleted Course names for Player.
	 * The Courses the player has yet to complete on the server.
	 * @return uncompleted course names
	 */
	public List<String> getUncompletedCourses(OfflinePlayer player) {
		List<String> completedCourses = parkour.getConfigManager().getCourseCompletionsConfig().getCompletedCourses(player);
		return parkour.getCourseManager().getCourseNames().stream()
				.filter(course -> !completedCourses.contains(course))
				.collect(Collectors.toList());
	}


	/**
	 * Select the Course for editing.
	 * This is used for commands that do not require a course parameter, such as "/pa checkpoint".
	 *
	 * @param player requesting player
	 * @param courseName course name
	 */
	public void selectCourse(final Player player, final String courseName) {
		if (!parkour.getCourseManager().doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, player);
			return;
		}

		parkour.getConfigManager().getPlayerConfig(player).setSelectedCourse(courseName);
		TranslationUtils.sendValueTranslation("Parkour.Selected", courseName, player);
	}

	/**
	 * Deselect the Course for editing.
	 *
	 * @param player requesting player
	 */
	public void deselectCourse(final Player player) {
		PlayerConfig playerConfig = parkour.getConfigManager().getPlayerConfig(player);

		if (playerConfig.hasSelectedCourse()) {
			playerConfig.resetSelected();
			TranslationUtils.sendTranslation("Parkour.Deselected", player);

		} else {
			TranslationUtils.sendTranslation("Error.Selected", player);
		}
	}

	/**
	 * Announce the Course Finish Message.
	 * The scope of the message is configurable.
	 *
	 * @param player player
	 * @param session parkour session
	 */
	private void announceCourseFinishMessage(Player player, ParkourSession session) {
		if (parkour.getParkourConfig().getBoolean("OnFinish.DisplayStats")) {
			parkour.getBountifulApi().sendFullTitle(player,
					TranslationUtils.getCourseEventMessage(session, FINISH, "Parkour.FinishCourse1"),
					TranslationUtils.replaceAllParkourPlaceholders(
							TranslationUtils.getTranslation("Parkour.FinishCourse2", false),
							player, session),
					parkour.getParkourConfig().getBoolean("DisplayTitle.Finish"));
		}

		// don't announce the time if the course isn't ready
		if (!parkour.getConfigManager().getCourseConfig(session.getCourseName()).getReadyStatus()) {
			return;
		}

		String finishBroadcast = TranslationUtils.replaceAllParkourPlaceholders(
				TranslationUtils.getTranslation("Parkour.FinishBroadcast"), player, session);

		String scope = parkour.getParkourConfig().getOrDefault("OnFinish.BroadcastLevel", "WORLD");
		TranslationUtils.announceParkourMessage(player, scope, finishBroadcast);
	}

	/**
	 * Restore the Player's Health and Food Level.
	 * The values are stored upon joining the Course, and are restored after they finish Parkour.
	 *
	 * @param player player
	 */
	private void restorePlayerData(Player player, PlayerConfig playerConfig) {
		player.setHealth(Math.min(Math.max(1, playerConfig.getSnapshotHealth()), player.getMaxHealth()));
		player.setFoodLevel(playerConfig.getSnapshotHunger());
		player.setLevel(playerConfig.getSnapshotXpLevel());
		restoreInventoryArmor(player, playerConfig);
		restoreGameMode(player);
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
		CourseConfig courseConfig = parkour.getConfigManager().getCourseConfig(courseName);
		populatePlayersInventory(player, courseConfig.getJoinItems());
		prepareParkourPlayer(player);
		setGameMode(player, parkour.getParkourConfig().getString("OnJoin.SetGameMode"));

		if (courseConfig.getCourseMode() == ParkourMode.NORUN) {
			player.setFoodLevel(6);

		} else if (parkour.getParkourConfig().getBoolean("OnJoin.FillHealth.Enabled")) {
			player.setFoodLevel(parkour.getParkourConfig().getInt("OnJoin.FillHealth.Amount"));
		}

		resetDeathCounter(player);

		if (parkour.getParkourConfig().getBoolean("OnCourse.DisableFly")) {
			player.setAllowFlight(false);
			player.setFlying(false);
		}

		if (parkour.getParkourConfig().getBoolean("ParkourTool.HideAll.ActivateOnJoin")) {
			parkour.getParkourSessionManager().hideVisibility(player, true);
		}
	}

	private void resetDeathCounter(Player player) {
		if (parkour.getParkourConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			player.setLevel(0);
		}
	}

	/**
	 * Populate the Player's inventory / hotbar for the Course.
	 * If Parkour is allowed to control their inventory then we clear it to do a full replacement.
	 * Each Tool and JoinItem can be disabled - leaving no impact on their inventory.
	 *
	 * @param player player
	 * @param joinItems course join items
	 */
	private void populatePlayersInventory(Player player, List<ItemStack> joinItems) {
		if (parkour.getParkourConfig().getBoolean("Other.Parkour.InventoryManagement")) {
			PlayerUtils.clearInventoryArmor(player);
		}

		giveParkourTool(player, "ParkourTool.LastCheckpoint");
		giveParkourTool(player, "ParkourTool.HideAll");
		giveParkourTool(player, "ParkourTool.Leave");
		giveParkourTool(player, "ParkourTool.Restart");

		if (joinItems != null) {
			for (ItemStack joinItem : joinItems) {
				player.getInventory().addItem(joinItem);
			}
		}

		player.updateInventory();
	}

	/**
	 * Check whether the player's time is a new course or personal record.
	 *
	 * @param player player
	 * @param session parkour session
	 */
	private TimeResult calculateTimeResult(Player player, ParkourSession session) {
		TimeResult result = TimeResult.NONE;

		// if they aren't updating the row, it will be inserted whether it's their best time
		if (parkour.getParkourConfig().getBoolean("OnFinish.DisplayNewRecords")
				|| parkour.getParkourConfig().getBoolean("OnFinish.UpdatePlayerDatabaseTime")) {

			if (parkour.getDatabaseManager().isBestCourseTime(session.getCourse().getName(), session.getTimeFinished())) {
				result = TimeResult.GLOBAL_BEST;

			} else if (parkour.getDatabaseManager().isBestCourseTime(player,
					session.getCourse().getName(), session.getTimeFinished())) {
				result = TimeResult.PLAYER_BEST;
			}
		}
		return result;
	}

	/**
	 * Start the Course Live Timer.
	 * Will be enabled / displayed when the Scoreboard LiveTimer is enabled, or as a live Action Bar timer.
	 * Course Timer may increase or decrease based on whether the Course has a maximum time.
	 */
	private void startLiveTimerRunnable() {
		final boolean displayLiveTimer = parkour.getParkourConfig().getBoolean("OnCourse.DisplayLiveTime");

		if (!displayLiveTimer
				&& !(parkour.getParkourConfig().getBoolean("Scoreboard.Enabled")
				&& parkour.getParkourConfig().getBoolean("Scoreboard.LiveTimer.Enabled"))) {
			return;
		}

		Bukkit.getScheduler().runTaskTimer(parkour, () -> {
			for (Map.Entry<UUID, ParkourSession> parkourPlayer : parkour.getParkourSessionManager().getParkourPlayers().entrySet()) {
				Player player = Bukkit.getPlayer(parkourPlayer.getKey());
				ParkourSession session = parkourPlayer.getValue();

				if (player == null || !session.isStartTimer()) {
					continue;
				}

				Course course = session.getCourse();

				int seconds = session.calculateSeconds();
				String liveTimer = DateTimeUtils.convertSecondsToTime(seconds);

				if (course.getSettings().hasMaxTime()) {
					parkour.getSoundsManager().playSound(player, SoundType.SECOND_DECREMENT);
					if (seconds <= 5 || seconds == 10) {
						liveTimer = ChatColor.RED + liveTimer;
					}
				} else {
					parkour.getSoundsManager().playSound(player, SoundType.SECOND_INCREMENT);
				}

				if (displayLiveTimer && parkour.getBountifulApi().hasTitleSupport()) {
					parkour.getBountifulApi().sendActionBar(player, liveTimer);
				}

				parkour.getScoreboardManager().updateScoreboardTimer(player, liveTimer);

				if (course.getSettings().hasMaxTime() && seconds <= 0) {
					session.setMarkedForDeletion(true);
					String maxTime = DateTimeUtils.convertSecondsToTime(course.getSettings().getMaxTime());
					TranslationUtils.sendValueTranslation("Parkour.MaxTime", maxTime, player);
					leaveCourse(player);
				}
			}
		}, 0, 20);
	}

	private Location determineJoinDestination(ParkourSession session) {
		if (session.getFreedomLocation() != null) {
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
		PlayerConfig playerConfig = parkour.getConfigManager().getPlayerConfig(player);
		CourseConfig courseConfig = parkour.getConfigManager().getCourseConfig(courseName);
		int currentLevel = playerConfig.getParkourLevel();
		int newParkourLevel = currentLevel;

		// set parkour level
		int rewardLevel = courseConfig.getRewardParkourLevel();
		if (rewardLevel > 0 && currentLevel < rewardLevel) {
			newParkourLevel = rewardLevel;
		}

		// increase parkour level
		int rewardAddLevel = courseConfig.getRewardParkourLevelIncrease();
		if (rewardAddLevel > 0) {
			newParkourLevel = currentLevel + rewardAddLevel;
		}

		newParkourLevel = Math.min(newParkourLevel, parkour.getParkourConfig().getInt("Other.Parkour.MaximumParkourLevel"));

		// if their parkour level has increased
		if (newParkourLevel > currentLevel) {
			// update parkour rank
			String rewardRank = parkour.getParkourRankManager().getUnlockedParkourRank(player, newParkourLevel);
			if (rewardRank != null) {
				playerConfig.setParkourRank(rewardRank);
				TranslationUtils.sendValueTranslation("Parkour.RewardRank", rewardRank, player);
				Bukkit.getServer().getPluginManager().callEvent(
						new PlayerParkourRankEvent(player, courseName, rewardRank));
			}

			// update parkour level
			playerConfig.setParkourLevel(newParkourLevel);
			if (parkour.getParkourConfig().getBoolean("Other.Display.LevelReward")) {
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
		if (!parkour.getParkourConfig().getBoolean("OnFinish.TeleportAway")) {
			return;
		}

		CourseConfig courseConfig = parkour.getConfigManager().getCourseConfig(courseName);
		if (courseConfig.hasLinkedCourse()) {
			String linkedCourseName = courseConfig.getLinkedCourse();
			joinCourse(player, linkedCourseName);
			return;

		} else if (courseConfig.hasLinkedLobby()) {
			String lobbyName = courseConfig.getLinkedLobby();

			if (parkour.getConfigManager().getLobbyConfig().doesLobbyExist(lobbyName)) {
				parkour.getLobbyManager().joinLobby(player, lobbyName);
				return;
			}

		} else if (parkour.getParkourConfig().isTeleportToJoinLocation()) {
			PlayerUtils.teleportToLocation(player, parkour.getConfigManager().getPlayerConfig(player).getSnapshotJoinLocation());
			TranslationUtils.sendTranslation("Parkour.JoinLocation", player);
			return;
		}

		parkour.getLobbyManager().joinLobby(player, DEFAULT);
	}

	private void teardownParkourMode(Player player) {
		ParkourMode courseMode = parkour.getParkourSessionManager().getParkourSession(player).getParkourMode();

		if (courseMode == ParkourMode.NONE) {
			return;
		}

		if (courseMode == ParkourMode.SPEEDY) {
			float speed = Float.parseFloat(parkour.getParkourConfig().getString("ParkourModes.Speedy.ResetSpeed"));
			player.setWalkSpeed(speed);
		}
	}

	private void displayJoinMessages(Player player, ParkourSession session) {
		sendJoinSubTitle(player, session);
		Course course = session.getCourse();

		if (parkour.getConfigManager().getCourseCompletionsConfig().hasCompletedCourse(player, course.getName())
				&& parkour.getParkourConfig().getBoolean("OnFinish.CompletedCourses.JoinMessage")) {
			TranslationUtils.sendValueTranslation("Parkour.AlreadyCompleted",
					course.getDisplayName(), player);
		}

		String joinBroadcast = TranslationUtils.replaceAllParkourPlaceholders(
				TranslationUtils.getTranslation("Parkour.JoinBroadcast"), player, session);

		String scope = parkour.getParkourConfig().getString("OnJoin.BroadcastLevel");
		TranslationUtils.announceParkourMessage(player, scope, joinBroadcast);
	}

	private void sendJoinSubTitle(Player player, ParkourSession session) {
		Course course = session.getCourse();

		String subTitle = "";
		if (course.getSettings().hasMaxDeaths() && course.getSettings().hasMaxTime()) {
			subTitle = TranslationUtils.getTranslation("Parkour.JoinLivesAndTime", false)
					.replace("%LIVES%", String.valueOf(course.getSettings().getMaxDeaths()))
					.replace("%MAXTIME%", DateTimeUtils.convertSecondsToTime(course.getSettings().getMaxTime()));

		} else if (course.getSettings().hasMaxDeaths()) {
			subTitle = TranslationUtils.getValueTranslation("Parkour.JoinLives",
					String.valueOf(course.getSettings().getMaxDeaths()), false);

		} else if (course.getSettings().hasMaxTime()) {
			subTitle = TranslationUtils.getValueTranslation("Parkour.JoinTime",
					DateTimeUtils.convertSecondsToTime(course.getSettings().getMaxTime()), false);
		}

		boolean displayTitle = parkour.getParkourConfig().getBoolean("DisplayTitle.JoinCourse");
		parkour.getBountifulApi().sendFullTitle(player,
				TranslationUtils.getCourseEventMessage(session, JOIN, "Parkour.Join"),
				subTitle, displayTitle);
	}

	private void populateSetPlayerActions() {
		playerActions.put("rank", (this::setParkourRank));
		playerActions.put("level", (sender, targetPlayer, value) -> setParkourLevel(sender, targetPlayer, value, false));
		playerActions.put("leveladd", (sender, targetPlayer, value) -> setParkourLevel(sender, targetPlayer, value, true));
	}

	@Override
	public void processCommand(CommandSender commandSender, String... args) {
		processCommand(commandSender, Bukkit.getOfflinePlayer(args[1]), args[0], args[2]);
	}

	public void processCommand(CommandSender commandSender, OfflinePlayer targetPlayer, String action, String value) {
		if (!PlayerConfig.hasPlayerConfig(targetPlayer)) {
			TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, commandSender);
			return;
		}

		if (!playerActions.containsKey(action.toLowerCase())) {
			TranslationUtils.sendMessage(commandSender, "Unknown Player action command");
			return;
		}

		playerActions.get(action.toLowerCase()).accept(commandSender, targetPlayer, value);
	}
}

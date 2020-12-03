package io.github.a5h73y.parkour.type.player;

import static io.github.a5h73y.parkour.other.Constants.DEFAULT;
import static io.github.a5h73y.parkour.utility.TranslationUtils.sendConditionalValue;
import static io.github.a5h73y.parkour.utility.TranslationUtils.sendValue;

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
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Constants;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.kit.ParkourKit;
import io.github.a5h73y.parkour.type.kit.ParkourKitInfo;
import io.github.a5h73y.parkour.type.lobby.LobbyInfo;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import io.github.a5h73y.parkour.utility.MaterialUtils;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/**
 * Parkour Player Manager.
 * Does not use a public cache, as the player's ParkourSession state is managed here only.
 */
public class PlayerManager extends AbstractPluginReceiver {

	private final transient Map<Player, ParkourSession> parkourPlayers = new WeakHashMap<>();

	private final transient Map<Player, Long> playerDelay = new HashMap<>();
	private final transient Map<Integer, String> parkourRanks = new TreeMap<>();

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
		return session != null && Constants.TEST_MODE.equals(session.getCourse().getName());
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
	 * Request to Join the Player to the Course.
	 * We can assume that if they are requesting the join using the course name it needs to validated.
	 *
	 * @param player requesting player
	 * @param courseName course name
	 */
	public void joinCourse(Player player, String courseName) {
		Course course = parkour.getCourseManager().findCourse(courseName);

		if (course == null) {
			TranslationUtils.sendValueTranslation("Error.NoExist", courseName, player);
			return;
		}

		if (!Validation.courseJoining(player, course)) {
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

		player.teleport(course.getCheckpoints().get(0).getLocation());
		preparePlayerForCourse(player, course.getName());
		CourseInfo.incrementViews(course.getName());
		PlayerInfo.setLastPlayedCourse(player, course.getName());

		// already on a different course
		if (isPlaying(player) && !getParkourSession(player).getCourseName().equals(course.getName())) {
			removePlayer(player);
		}

		// join message
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
				subTitle = TranslationUtils.getValueTranslation(
						"Parkour.JoinTime", DateTimeUtils.convertSecondsToTime(course.getMaxTime()), false);
			}

			parkour.getBountifulApi().sendFullTitle(player,
					TranslationUtils.getCourseMessage(course.getName(), "JoinMessage", "Parkour.Join"),
					subTitle, displayTitle);

			if (parkour.getConfig().isCompletedCoursesEnabled()
					&& PlayerInfo.getCompletedCourses(player).contains(course.getName())
					&& parkour.getConfig().getBoolean("Other.Display.CourseCompleted")) {
				TranslationUtils.sendValueTranslation("Parkour.AlreadyCompleted", course.getName(), player);
			}
		}
		ParkourSession session;
		if (canLoadParkourSession(player, course)) {
			session = loadParkourSession(player);
			player.teleport(determineDestination(session));
			TranslationUtils.sendValueTranslation("Parkour.Continue", session.getCourseName(), player);
		} else {
			session = addPlayer(player, new ParkourSession(course));
		}
		setupParkourMode(player);
		parkour.getScoreboardManager().addScoreboard(player, session);
		if (!silent) {
			parkour.getCourseManager().runEventCommands(player, course.getName(), ParkourEventType.JOIN);
		}
		if (!parkour.getConfig().isFirstCheckAsStart() && !parkour.getChallengeManager().hasPlayerBeenChallenged(player)) {
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

		if (parkour.getConfig().getBoolean("OnLeave.DestroyCourseProgress")) {
			session.setMarkedForDeletion(true);
		}

		teardownParkourMode(player);
		if (session.isMarkedForDeletion()) {
			deleteParkourSession(player);
			removePlayer(player);
		} else {
			stashParkourSession(player);
		}
		preparePlayer(player, parkour.getConfig().getString("OnFinish.SetGameMode"));
		restoreHealth(player);
		if (parkour.getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			restoreXPLevel(player);
		}
		loadInventory(player);
		parkour.getChallengeManager().forfeitChallenge(player);

		if (!silent) {
			parkour.getSoundsManager().playSound(player, SoundType.COURSE_FAILED);
			parkour.getBountifulApi().sendSubTitle(player,
					TranslationUtils.getCourseMessage(session.getCourse().getName(), "LeaveMessage", "Parkour.Leave"),
					parkour.getConfig().getBoolean("DisplayTitle.Leave"));

			if (parkour.getConfig().isTeleportToJoinLocation()
					&& PlayerInfo.hasJoinLocation(player)) {
				player.teleport(PlayerInfo.getJoinLocation(player));
			} else {
				parkour.getLobbyManager().teleportToLeaveDestination(player, session);
			}
			parkour.getCourseManager().runEventCommands(player, session.getCourseName(), ParkourEventType.LEAVE);
		}

		forceVisible(player);
		parkour.getScoreboardManager().removeScoreboard(player);
		Bukkit.getServer().getPluginManager().callEvent(new PlayerLeaveCourseEvent(player, session.getCourse().getName(), silent));
	}

	/**
	 * Increase the ParkourSession checkpoint number
	 * Once a player activates a new checkpoint, it will setup the next checkpoint ready.
	 * A message will be sent to the player notifying them of their progression.
	 *
	 * @param player
	 */

	/**
	 * Increase ParkourSession Checkpoint.
	 *
	 * @param player
	 */
	public void increaseCheckpoint(Player player) {
		ParkourSession session = getParkourSession(player);

		if (session == null) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);
			return;
		}

		String courseName = session.getCourseName();
		session.increaseCheckpoint();
		parkour.getSoundsManager().playSound(player, SoundType.CHECKPOINT_ACHIEVED);
		parkour.getScoreboardManager().updateScoreboardCheckpoints(player, session);
		parkour.getCourseManager().runEventCommands(player, courseName, ParkourEventType.CHECKPOINT);

		boolean showTitle = parkour.getConfig().getBoolean("DisplayTitle.Checkpoint");

		String checkpointCourseTranslation = "CheckpointMessage";
		String checkpointTranslation = "Event.Checkpoint";

		if (session.hasAchievedAllCheckpoints()) {
			checkpointCourseTranslation  = "CheckpointallMessage";
			checkpointTranslation = "Event.AllCheckpoints";
		}

		String checkpointMessage = TranslationUtils.getCourseMessage(session.getCourse().getName(), checkpointCourseTranslation , checkpointTranslation)
				.replace("%CURRENT%", String.valueOf(session.getCurrentCheckpoint()))
				.replace("%TOTAL%", String.valueOf(session.getCourse().getNumberOfCheckpoints()));

		parkour.getBountifulApi().sendSubTitle(player, checkpointMessage, showTitle);

		Bukkit.getServer().getPluginManager().callEvent(
				new PlayerAchieveCheckpointEvent(player, session.getCourse().getName(), session.getCheckpoint()));
	}

	/**
	 * Player dies while on a course.
	 * Called when the player 'dies' this can be from real events (Like falling
	 * from too high), or native Parkour deaths (walking on a deathblock).
	 *
	 * @param player target player
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
				int remainingLives = session.getCourse().getMaxDeaths() - session.getDeaths();

				parkour.getBountifulApi().sendSubTitle(player,
						TranslationUtils.getValueTranslation("Parkour.LifeCount", String.valueOf(remainingLives), false),
						parkour.getConfig().getBoolean("DisplayTitle.Death"));

			} else {
				TranslationUtils.sendValueTranslation("Parkour.MaxDeaths", String.valueOf(session.getCourse().getMaxDeaths()));
				leaveCourse(player);
				return;
			}
		}

		parkour.getScoreboardManager().updateScoreboardDeaths(player, session.getDeaths());
		parkour.getCourseManager().runEventCommands(player, session.getCourseName(), ParkourEventType.DEATH);

		player.teleport(determineDestination(session));

		// if it's the first checkpoint
		if (session.getCurrentCheckpoint() == 0) {
			String message = TranslationUtils.getTranslation("Parkour.Die1");

			if (parkour.getConfig().getBoolean("OnDie.ResetTimeWithNoCheckpoint")) {
				session.resetTime();
				message += " " + TranslationUtils.getTranslation("Parkour.TimeReset", false);
			}

			if (!PlayerInfo.isQuietMode(player)) {
				player.sendMessage(message);
			}
		} else {
			if (!PlayerInfo.isQuietMode(player)) {
				TranslationUtils.sendValueTranslation("Parkour.Die2", String.valueOf(session.getCurrentCheckpoint()), player);
			}
		}

		if (parkour.getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			player.setLevel(session.getDeaths());
		}

		preparePlayer(player, parkour.getConfig().getString("OnJoin.SetGameMode"));
		Bukkit.getServer().getPluginManager().callEvent(new PlayerDeathEvent(player, session.getCourse().getName()));
	}

	/**
	 * Start the visual timer either on the ActionBar if DisplayLiveTime is true, or in the scoreboard
	 * if the scoreboard is enabled and the display current time option is true.
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

				if (!PlayerInfo.isQuietMode(player) && parkour.getConfig().getBoolean("OnCourse.DisplayLiveTime")) {
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

	/**
	 * Teardown a Parkour player.
	 * Remove all in-memory references to the player, persisting any data to a file.
	 *
	 * @param player parkour player
	 */
	public void teardownParkourPlayer(Player player) {
		parkour.getChallengeManager().forfeitChallenge(player);
		parkour.getQuestionManager().removeQuestion(player);
		hiddenPlayers.remove(player);
		playerDelay.remove(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(parkour, () -> stashParkourSession(player));
	}

	public void teardownParkourPlayers() {
		for (Player player : parkourPlayers.keySet()) {
			createParkourSessionFile(player);
		}
	}

	/**
	 * Player finishes a course.
	 * This will be called when the player completes the course.
	 * Their reward will be given here, as well as a time entry to the database.
	 * Inventory is restored before the player is teleported. If the teleport is delayed,
	 * restore the inventory after the delay.
	 *
	 * @param player
	 */
	public void finishCourse(final Player player) {
		if (!isPlaying(player)) {
			return;
		}

		if (isPlayerInTestMode(player)) {
			return;
		}

		ParkourSession session = getParkourSession(player);
		final String courseName = session.getCourse().getName();

		if (parkour.getConfig().getBoolean("OnFinish.EnforceCompletion")
				&& !session.hasAchievedAllCheckpoints()) {

			TranslationUtils.sendTranslation("Error.Cheating1", player);
			TranslationUtils.sendValueTranslation("Error.Cheating2",
					String.valueOf(session.getCourse().getNumberOfCheckpoints()), player);
			playerDie(player);
			return;
		}

		session.markTimeFinished();
		parkour.getSoundsManager().playSound(player, SoundType.COURSE_FINISHED);
		preparePlayer(player, parkour.getConfig().getString("OnFinish.SetGameMode"));

		if (hasHiddenPlayers(player)) {
			toggleVisibility(player, true);
		}

		displayFinishMessage(player, session);
		CourseInfo.incrementCompletions(courseName);
		teardownParkourMode(player);
		removePlayer(player);

		parkour.getChallengeManager().completeChallenge(player);

		if (parkour.getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			restoreXPLevel(player);
		}

		final long delay = parkour.getConfig().getLong("OnFinish.TeleportDelay");
		final boolean teleportAway = parkour.getConfig().getBoolean("OnFinish.TeleportAway");

		if (delay <= 0) {
			restoreHealth(player);
			loadInventory(player);
			rewardPrize(player, courseName);
			parkour.getScoreboardManager().removeScoreboard(player);
			if (teleportAway) {
				teleportCourseCompletion(player, courseName);
			}
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(parkour, () -> {
				restoreHealth(player);
				loadInventory(player);
				rewardPrize(player, courseName);
				parkour.getScoreboardManager().removeScoreboard(player);
				if (teleportAway) {
					teleportCourseCompletion(player, courseName);
				}
			}, delay);
		}

		boolean recordTime = isNewRecord(player, session);
		parkour.getDatabase().insertOrUpdateTime(
				courseName, player, session.getTimeFinished(), session.getDeaths(), recordTime);

		PlayerInfo.setLastCompletedCourse(player, courseName);
		PlayerInfo.addCompletedCourse(player, courseName);

		forceVisible(player);
		deleteParkourSession(player);
		Bukkit.getServer().getPluginManager().callEvent(new PlayerFinishCourseEvent(player, courseName));
	}

	/**
	 * Restart the course progress
	 * Time and deaths are reset.
	 * Will take into account treating the first checkpoint as start
	 *
	 * @param player
	 */
	public void restartCourse(Player player) {
		if (!isPlaying(player)) {
			return;
		}

		Course course = getParkourSession(player).getCourse();
		TranslationUtils.sendTranslation("Parkour.Restarting", player);
		leaveCourse(player, true);
		deleteParkourSession(player);
		joinCourse(player, course, true);
	}

	/**
	 * Reward a player with several forms of prize after course completion.
	 *
	 * @param player
	 * @param courseName
	 */
	public void rewardPrize(Player player, String courseName) {
		if (!parkour.getConfig().getBoolean("OnFinish.EnablePrizes")) {
			return;
		}

		if (CourseInfo.getRewardOnce(courseName)
				&& parkour.getDatabase().hasPlayerAchievedTime(player, courseName)) {
			parkour.getCourseManager().runEventCommands(player, courseName, ParkourEventType.FINISH);
			return;
		}

		// Check how often prize can be rewarded
		if (CourseInfo.hasRewardDelay(courseName)) {
			// if we still have to wait, return out of this function
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
		int xp = CourseInfo.getXPPrize(courseName);

		if (xp == 0) {
			xp = parkour.getConfig().getInt("OnFinish.DefaultPrize.XP");
		}

		if (xp > 0) {
			player.giveExp(xp);
		}

		// Give level reward to player
		playerLevelUp(player, courseName);

		parkour.getCourseManager().runEventCommands(player, courseName, ParkourEventType.PRIZE);
		rewardParkoins(player, CourseInfo.getRewardParkoins(courseName));

		parkour.getEconomyApi().giveEconomyPrize(player, courseName);
		player.updateInventory();
	}

	private void playerLevelUp(Player player, String courseName) {
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

		// if their parkour level has increased
		if (newParkourLevel > currentLevel) {
			// update parkour rank
			String rewardRank = getUnlockedParkourRank(player, newParkourLevel);
			if (rewardRank != null) {
				PlayerInfo.setParkourRank(player, rewardRank);
				TranslationUtils.sendValueTranslation("Parkour.RewardRank", rewardRank, player);
			}

			PlayerInfo.setParkourLevel(player, newParkourLevel);
			if (parkour.getConfig().getBoolean("Other.Display.LevelReward")) {
				player.sendMessage(TranslationUtils.getTranslation("Parkour.RewardLevel")
						.replace("%LEVEL%", String.valueOf(newParkourLevel))
						.replace(Constants.COURSE_PLACEHOLDER, courseName));
			}
		}
	}

	/**
	 * Teleport player after course completion
	 * Based on the linked course or lobby
	 *
	 * @param player
	 * @param courseName
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
			player.teleport(PlayerInfo.getJoinLocation(player));
			TranslationUtils.sendTranslation("Parkour.JoinLocation", player);
			return;
		}

		parkour.getLobbyManager().joinLobby(player, DEFAULT);
	}

	public void rocketLaunchPlayer(Player player) {
		double force = parkour.getConfig().getBoolean("ParkourModes.Rockets.Invert") ? 1.5 : -1.5;

		Vector velocity = player.getLocation().getDirection().normalize();
		velocity = velocity.multiply(force);
		velocity = velocity.setY(velocity.getY() / 2);
		player.setVelocity(velocity);
		player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 500);
	}

	/**
	 * Apply an effect to the player
	 *
	 * @param lines
	 * @param player
	 */
	public void applyEffect(Player player, String[] lines) {
		if (lines[2].equalsIgnoreCase("heal")) {
			Damageable playerDamage = player;
			playerDamage.setHealth(playerDamage.getMaxHealth());
			player.sendMessage(Parkour.getPrefix() + "Healed!");

		} else if (lines[2].equalsIgnoreCase("gamemode")) {
			if (PluginUtils.doesGameModeExist(lines[3].toUpperCase())) {
				GameMode gameMode = GameMode.valueOf(lines[3].toUpperCase());
				if (gameMode != player.getGameMode()) {
					player.setGameMode(gameMode);
					player.sendMessage(Parkour.getPrefix() + "GameMode set to " + StringUtils.standardizeText(gameMode.name()));
				}
			} else {
				player.sendMessage(Parkour.getPrefix() + "GameMode not recognised.");
			}

		} else {
			// if the user enters 'FIRE_RESISTANCE' or 'DAMAGE_RESIST' treat them the same
			String effect = lines[2].toUpperCase().replace("RESISTANCE", "RESIST").replace("RESIST", "RESISTANCE");
			PotionEffectType potionType = PotionEffectType.getByName(effect);

			if (potionType == null) {
				player.sendMessage(Parkour.getPrefix() + "Unknown Effect!");
				return;
			}
			String[] args = lines[3].split(":");
			if (args.length == 2) {
				player.addPotionEffect(new PotionEffect(potionType, Integer.parseInt(args[1]), Integer.parseInt(args[0])));
				player.sendMessage(Parkour.getPrefix() + potionType.getName() + " Effect Applied!");
			} else {
				player.sendMessage(Parkour.getPrefix() + "Invalid syntax, must follow '(duration):(strength)' example '1000:6'.");
			}
		}
	}

	/**
	 * Setup the outcome of having a Parkour Mode
	 *
	 * @param player
	 */
	private void setupParkourMode(Player player) {
		ParkourSession session = getParkourSession(player);
		ParkourMode courseMode = session.getParkourMode();

		if (courseMode == ParkourMode.NONE) {
			return;
		}

		if (courseMode == ParkourMode.FREEDOM) {
			TranslationUtils.sendTranslation("Mode.Freedom.JoinText", player);
			player.getInventory().addItem(MaterialUtils.createItemStack(XMaterial.REDSTONE_TORCH.parseMaterial(),
					TranslationUtils.getTranslation("Mode.Freedom.ItemName", false)));

		} else if (courseMode == ParkourMode.SPEEDY) {
			float speed = Float.parseFloat(parkour.getConfig().getString("ParkourModes.Speedy.SetSpeed"));
			player.setWalkSpeed(speed);

		} else if (courseMode == ParkourMode.ROCKETS) {
			TranslationUtils.sendTranslation("Mode.Rockets.JoinText", player);
			player.getInventory().addItem(MaterialUtils.createItemStack(
					XMaterial.FIREWORK_ROCKET.parseMaterial(), TranslationUtils.getTranslation("Mode.Rockets.ItemName", false)));

		} else if (courseMode == ParkourMode.POTION) {
			XPotion.addPotionEffectsFromString(player, CourseInfo.getPotionParkourModeEffects(session.getCourseName()));
			if (CourseInfo.hasPotionJoinMessage(session.getCourseName())) {
				player.sendMessage(StringUtils.colour(CourseInfo.getPotionJoinMessage(session.getCourseName())));
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

	/**
	 * Toggle Player Quiet Mode.
	 * Strange method because the action bar notification is only sent if they aren't currently in Quiet Mode.
	 * @param player requesting player
	 */
	public void toggleQuietMode(Player player) {
		if (delayPlayer(player, 2, true)) {
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
	}

	/**
	 * Get all players that are online and on a course using the plugin
	 *
	 * @return List<Player>
	 */
	public List<Player> getOnlineParkourPlayers() {
		List<Player> onlineParkourPlayers = new ArrayList<>();

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (isPlaying(player)) {
				onlineParkourPlayers.add(player);
			}
		}

		return onlineParkourPlayers;
	}

	public void toggleVisibility(Player player) {
		toggleVisibility(player, false);
	}

	/**
	 * Toggle visibility of all players for the player
	 * Can be overwritten to force the reappearance of all players (i.e. when a player leaves / finishes a course)
	 * Option can be chosen whether to hide all online players, or just parkour players
	 *
	 * @param player
	 * @param override
	 */
	public void toggleVisibility(Player player, boolean override) {
		boolean enabled = override || hasHiddenPlayers(player);
		List<Player> playerScope;

		if (parkour.getConfig().getBoolean("OnJoin.Item.HideAll.Global") || override) {
			playerScope = (List<Player>) Bukkit.getOnlinePlayers();
		} else {
			playerScope = getOnlineParkourPlayers();
		}

		for (Player players : playerScope) {
			if (enabled) {
				player.showPlayer(players);
			} else {
				player.hidePlayer(players);
			}
		}
		if (enabled) {
			removeHidden(player);
			TranslationUtils.sendTranslation("Event.HideAll1", player);

		} else {
			addHidden(player);
			TranslationUtils.sendTranslation("Event.HideAll2", player);
		}
	}

	public void forceVisible(Player player) {
		for (Player players : Bukkit.getOnlinePlayers()) {
			players.showPlayer(player);
		}
		if (hasHiddenPlayers(player)) {
			toggleVisibility(player, true);
		}
	}

	public void forceInvisible(Player player) {
		for (Player players : Bukkit.getOnlinePlayers()) {
			players.hidePlayer(player);
		}
		addHidden(player);
	}

	public boolean hasHiddenPlayers(Player player) {
		return hiddenPlayers.contains(player);
	}

	public void addHidden(Player player) {
		hiddenPlayers.add(player);
	}

	public void removeHidden(Player player) {
		hiddenPlayers.remove(player);
	}

	public boolean delayPlayer(Player player, int secondsToWait, boolean displayMessage) {
		return delayPlayer(player, secondsToWait, displayMessage, true);
	}


	/**
	 * Delay certain actions
	 * This check can be bypassed by admins / ops
	 *
	 * @param player
	 * @param secondsToWait
	 * @param displayMessage display cooldown error
	 * @return boolean (wait expired)
	 */
	public boolean delayPlayer(Player player, int secondsToWait, boolean displayMessage, boolean opsBypass) {
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

		if (displayMessage) {
			TranslationUtils.sendValueTranslation("Error.Cooldown",
					String.valueOf(secondsToWait - secondsElapsed), player);
		}
		return false;
	}

	/**
	 * Check to see if the minimum amount of time has passed (in hours) to allow the plugin to provide the prize again
	 *
	 * @param player
	 * @param courseName
	 * @param displayMessage
	 * @return boolean
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
					DateTimeUtils.getTimeRemaining(player, courseName), player);
		}
		return false;
	}

	/**
	 * Prepare the player for Parkour.
	 * Executed when the player dies, will reset them to a normal state so they can continue.
	 *
	 * @param player
	 * @param gameMode
	 */
	public void preparePlayer(Player player, String gameMode) {
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

		if (!isPlayerInTestMode(player)) {
			player.setGameMode(PluginUtils.getGameMode(gameMode));
		}

		Damageable playerDamage = player;
		playerDamage.setHealth(playerDamage.getMaxHealth());
		player.setFallDistance(0);
		player.setFireTicks(0);
		player.eject();
	}

	/**
	 * Save the player's Inventory and Armour.
	 * Once saved, the players inventory and armour is cleared.
	 * Will not overwrite the inventory if one is already saved. Can be disabled.
	 *
	 * @param player
	 */
	public void saveInventory(Player player) {
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
	 * Load the players original inventory
	 * When they leave or finish a course, their inventory and armour will be restored to them.
	 * Will delete the inventory from the config once loaded.
	 *
	 * @param player
	 */
	public void loadInventory(Player player) {
		if (!parkour.getConfig().getBoolean("Other.Parkour.InventoryManagement")) {
			return;
		}

		ItemStack[] inventoryContents = PlayerInfo.getSavedInventoryContents(player);
		ItemStack[] armorContents = PlayerInfo.getSavedArmorContents(player);

		if (inventoryContents == null) {
			player.sendMessage(Parkour.getPrefix() + "No saved inventory to load");
			return;
		}

		player.getInventory().clear();
		player.getInventory().setContents(inventoryContents);
		player.getInventory().setArmorContents(armorContents);

		player.updateInventory();

		Parkour.getConfig(ConfigType.INVENTORY).set(player.getUniqueId().toString(), null);
		Parkour.getConfig(ConfigType.INVENTORY).save();
	}

	/**
	 * Increase the amount of Parkoins the player has by the parameter value.
	 *
	 * @param player
	 * @param parkoins
	 */
	public void rewardParkoins(Player player, int parkoins) {
		if (parkoins <= 0) {
			return;
		}

		double total = parkoins + PlayerInfo.getParkoins(player);
		PlayerInfo.setParkoins(player, total);
		player.sendMessage(TranslationUtils.getTranslation("Parkour.RewardParkoins")
				.replace("%AMOUNT%", String.valueOf(parkoins))
				.replace("%TOTAL%", String.valueOf(total)));
	}

	/**
	 * Decrease the amount of Parkoins the player has by the parameter value.
	 *
	 * @param player
	 * @param parkoins
	 */
	public void deductParkoins(Player player, int parkoins) {
		if (parkoins <= 0) {
			return;
		}

		double current = PlayerInfo.getParkoins(player);
		double amountToDeduct = Math.min(current, parkoins);

		PlayerInfo.setParkoins(player, current - amountToDeduct);
		player.sendMessage(Parkour.getPrefix() + parkoins + " Parkoins deducted! New total: "
				+ ChatColor.AQUA + PlayerInfo.getParkoins(player));
	}

	/**
	 * Display the players Parkour permissions.
	 *
	 * @param player
	 */
	public void displayPermissions(Player player, boolean detailed) {
		TranslationUtils.sendHeading("Parkour Permissions", player);

		if (detailed) {
			for (Permission permission : Permission.values()) {
				if (player.hasPermission(permission.getPermission())) {
					player.sendMessage("* " + permission.getPermission());
				}
			}
		} else {
			if (player.isOp() || player.hasPermission(Permission.PARKOUR_ALL.getPermission())) {
				player.sendMessage("* " + Permission.PARKOUR_ALL.getPermission());
				return;
			}

			boolean anyPerms = hasDisplayPermission(player, Permission.BASIC_ALL)
					|| hasDisplayPermission(player, Permission.CREATE_SIGN_ALL)
					|| hasDisplayPermission(player, Permission.ADMIN_ALL);

			if (!anyPerms) {
				player.sendMessage("* You don't have any Parkour permissions.");
			}
		}
	}

	/**
	 * Executed via "/pa kit", will clear and populate the players inventory
	 * with the default Parkour tools.
	 *
	 * @param player
	 */
	public void giveParkourKit(Player player, String kitName) {
		if (parkour.getConfig().getBoolean("Other.ParkourKit.ReplaceInventory")) {
			player.getInventory().clear();
		}

		ParkourKit kit = parkour.getParkourKitManager().getParkourKit(kitName);

		if (kit == null) {
			player.sendMessage(Parkour.getPrefix() + "Invalid ParkourKit: " + ChatColor.RED + kitName);
			return;
		}

		for (Material material : kit.getMaterials()) {
			String actionName = ParkourKitInfo.getActionTypeForMaterial(kitName, material.name());

			if (actionName == null) {
				continue;
			}

			actionName = StringUtils.standardizeText(actionName);

			ItemStack itemStack = MaterialUtils.createItemStack(material,
					TranslationUtils.getTranslation("Kit." + actionName, false));
			player.getInventory().addItem(itemStack);
		}

		if (parkour.getConfig().getBoolean("Other.ParkourKit.GiveSign")) {
			ItemStack itemStack = MaterialUtils.createItemStack(XMaterial.OAK_SIGN.parseMaterial(),
					TranslationUtils.getTranslation("Kit.Sign", false));
			player.getInventory().addItem(itemStack);
		}

		player.updateInventory();
		TranslationUtils.sendValueTranslation("Other.Kit", kitName, player);
		PluginUtils.logToFile(player.getName() + " received the kit");
	}

	/**
	 * Toggle Test Mode
	 * This will enable / disable the testmode functionality, by creating a
	 * dummy "Test Mode" course for the player.
	 *
	 * @param player
	 */
	public void toggleTestMode(Player player, String kitName) {
		if (isPlaying(player)) {
			if (isPlayerInTestMode(player)) {
				removePlayer(player);
				parkour.getBountifulApi().sendActionBar(player,
						TranslationUtils.getTranslation("Parkour.TestModeOff", false), true);
			} else {
				player.sendMessage(Parkour.getPrefix() + "You are not in Test Mode.");
			}
		} else {
			ParkourKit kit = parkour.getParkourKitManager().getParkourKit(kitName);

			if (kit == null) {
				player.sendMessage(Parkour.getPrefix() + "ParkourKit " + kitName + " doesn't exist!");

			} else {
				List<Checkpoint> checkpoints = Collections.singletonList(parkour.getCheckpointManager().createCheckpointFromPlayerLocation(player));
				ParkourSession session = new ParkourSession(new Course(Constants.TEST_MODE, checkpoints, kit, ParkourMode.NONE));
				addPlayer(player, session);
				parkour.getBountifulApi().sendActionBar(player,
						TranslationUtils.getValueTranslation("Parkour.TestModeOn", kitName, false), true);
			}
		}
	}

	/**
	 * Lookup and display the Player's Parkour information.
	 * Will display their stored statistics as well as their current information if they're on a course.
	 *
	 * @param args
	 * @param sender
	 */
	public void displayParkourInfo(CommandSender sender, String[] args) {
		OfflinePlayer targetPlayer = args.length <= 1 ? (OfflinePlayer) sender : Bukkit.getOfflinePlayer(args[1]);

		if (!PlayerInfo.hasPlayerInfo(targetPlayer)) {
			TranslationUtils.sendTranslation("Error.UnknownPlayer", sender);
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
				PlayerInfo.getNumberOfCoursesCompleted(targetPlayer) + " / " + CourseInfo.getAllCourseNames().size());

	}

	public void setParkourLevel(CommandSender sender, OfflinePlayer targetPlayer, String value) {
		if (!ValidationUtils.isPositiveInteger(value)) {
			TranslationUtils.sendTranslation("Error.InvalidAmount", sender);
			return;
		}

		if (!PlayerInfo.hasPlayerInfo(targetPlayer)) {
			TranslationUtils.sendTranslation("Error.UnknownPlayer", sender);
			return;
		}

		int newLevel = Integer.parseInt(value);
		PlayerInfo.setParkourLevel(targetPlayer, newLevel);

		sender.sendMessage(Parkour.getPrefix() + targetPlayer.getName() + "'s Level was set to " + newLevel);
	}

	public void setParkourRank(CommandSender sender, OfflinePlayer targetPlayer, String value) {
		if (!PlayerInfo.hasPlayerInfo(targetPlayer)) {
			TranslationUtils.sendTranslation("Error.UnknownPlayer", sender);
			return;
		}

		PlayerInfo.setParkourRank(targetPlayer, value);
		sender.sendMessage(Parkour.getPrefix() + targetPlayer.getName() + "'s Rank was set to " + value);
	}

	public void stashParkourSession(Player player) {
		if (isPlaying(player)) {
			getParkourSession(player).markTimeAccumulated();
		}
		createParkourSessionFile(player);
		parkourPlayers.remove(player);
	}

	private void createParkourSessionFile(Player player) {
		if (!getSessionsPath().exists()) {
			getSessionsPath().mkdirs();
		}

		ParkourSession session = getParkourSession(player);

		if (session != null) {
			File sessionFile = new File(getSessionsPath(), player.getUniqueId().toString());

			if (!sessionFile.exists()) {
				try {
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

	public ParkourSession loadParkourSession(Player player) {
		ParkourSession session = readParkourSession(player);

		if (session != null) {
			if (parkour.getCourseManager().doesCourseExists(session.getCourseName())) {
				session.setCourse(parkour.getCourseManager().findCourse(session.getCourseName()));
				session.recalculateTime();
				parkourPlayers.put(player, session);

			} else {
				TranslationUtils.sendTranslation("Error.InvalidSession", player);
				deleteParkourSession(player);
				parkour.getLobbyManager().joinLobby(player, DEFAULT);
			}
		}

		return session;
	}

	private boolean canLoadParkourSession(Player player, Course course) {
		ParkourSession session = readParkourSession(player);
		return session != null && session.getCourseName().equals(course.getName());
	}

	public ParkourSession readParkourSession(Player player) {
		ParkourSession session = null;
		File sessionFile = new File(getSessionsPath(), player.getUniqueId().toString());

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

	public void deleteParkourSession(OfflinePlayer player) {
		File sessionFile = new File(getSessionsPath(), player.getUniqueId().toString());

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
	 * Display a list of all the players on a Course.
	 * Will display:
	 * * the course they are on
	 * * the amount of times they've died
	 * * how long they've been on the course.
	 *
	 * @param sender
	 */
	public void displayParkourPlayers(CommandSender sender) {
		if (getNumberOfParkourPlayer() == 0) {
			sender.sendMessage(Parkour.getPrefix() + "Nobody is playing Parkour!");
			return;
		}

		sender.sendMessage(Parkour.getPrefix() + getNumberOfParkourPlayer() + " players using Parkour: ");

		String playingTemplate = TranslationUtils.getTranslation("Parkour.Playing", false);
		for (Map.Entry<Player, ParkourSession> entry : parkourPlayers.entrySet()) {
			sender.sendMessage(playingTemplate
					.replace(Constants.PLAYER_PLACEHOLDER, entry.getKey().getName())
					.replace(Constants.COURSE_PLACEHOLDER, entry.getValue().getCourse().getName())
					.replace("%DEATHS%", String.valueOf(entry.getValue().getDeaths()))
					.replace("%TIME%", entry.getValue().getDisplayTime()));
		}
	}

	public int getNumberOfPlayersOnCourse(String courseName) {
		return (int) parkourPlayers.values().stream()
				.filter(parkourSession -> parkourSession.getCourseName().equals(courseName.toLowerCase()))
				.count();
	}

	private File getSessionsPath() {
		return new File(parkour.getDataFolder() + File.separator + "sessions" + File.separator);
	}

	/**
	 * Display the course finish information
	 * Will send to the chosen amount of players
	 *
	 * @param player
	 * @param session
	 */
	private void displayFinishMessage(Player player, ParkourSession session) {
		if (parkour.getConfig().getBoolean("OnFinish.DisplayStats")) {
			parkour.getBountifulApi().sendFullTitle(player,
					TranslationUtils.getCourseMessage(session.getCourse().getName(), "FinishMessage", "Parkour.FinishCourse1"),
					TranslationUtils.getTranslation("Parkour.FinishCourse2", false)
							.replace("%DEATHS%", String.valueOf(session.getDeaths()))
							.replace("%TIME%", session.getDisplayTime()),
					parkour.getConfig().getBoolean("DisplayTitle.Finish"));
		}

		// don't announce the time if the course isn't ready
		if (!CourseInfo.getReadyStatus(session.getCourseName())) {
			return;
		}

		String finishBroadcast = TranslationUtils.getTranslation("Parkour.FinishBroadcast")
				.replace(Constants.PLAYER_PLACEHOLDER, player.getName())
				.replace("%PLAYER_DISPLAY%", player.getDisplayName())
				.replace(Constants.COURSE_PLACEHOLDER, session.getCourse().getName())
				.replace("%DEATHS%", String.valueOf(session.getDeaths()))
				.replace("%TIME%", session.getDisplayTime());

		switch (parkour.getConfig().getString("OnFinish.BroadcastLevel", "WORLD").toUpperCase()) {
			case "GLOBAL":
				for (Player players : Bukkit.getServer().getOnlinePlayers()) {
					players.sendMessage(finishBroadcast);
				}
				return;
			case "WORLD":
				for (Player players : player.getWorld().getPlayers()) {
					players.sendMessage(finishBroadcast);
				}
				return;
			case "PARKOUR":
				for (Player players : getOnlineParkourPlayers()) {
					players.sendMessage(finishBroadcast);
				}
				return;
			case "PLAYER":
				player.sendMessage(finishBroadcast);
				return;
			default:
		}
	}

	/**
	 * Prepare the player for Parkour
	 * Store the player's health and hunger.
	 *
	 * @param player
	 */
	private void saveHealth(Player player) {
		ParkourConfiguration inventoryConfig = Parkour.getConfig(ConfigType.INVENTORY);
		inventoryConfig.set(player.getUniqueId() + ".Health", player.getHealth());
		inventoryConfig.set(player.getUniqueId() + ".Hunger", player.getFoodLevel());
		inventoryConfig.save();
	}

	/**
	 * Load the players original health.
	 * When they leave or finish a course, their hunger and exhaustion will be restored to them.
	 * Will delete the health from the config once loaded.
	 *
	 * @param player
	 */
	private void restoreHealth(Player player) {
		ParkourConfiguration inventoryConfig = Parkour.getConfig(ConfigType.INVENTORY);

		double health = Math.min(player.getMaxHealth(), inventoryConfig.getDouble(player.getUniqueId() + ".Health", player.getMaxHealth()));
		player.setHealth(health);
		player.setFoodLevel(inventoryConfig.getInt(player.getUniqueId() + ".Hunger"));
		inventoryConfig.set(player.getUniqueId() + ".Health", null);
		inventoryConfig.set(player.getUniqueId() + ".Hunger", null);
		inventoryConfig.save();
	}

	/**
	 * Prepare the player for Parkour.
	 * Store the player's XP level.
	 *
	 * @param player
	 */
	private void saveXPLevel(Player player) {
		ParkourConfiguration inventoryConfig = Parkour.getConfig(ConfigType.INVENTORY);
		inventoryConfig.set(player.getUniqueId() + ".XPLevel", player.getLevel());
		inventoryConfig.save();
	}

	/**
	 * Load the players original XP level.
	 * When they leave or finish a course, their XP level will be restored to them.
	 * Will delete the XP level from the config once loaded.
	 *
	 * @param player
	 */
	private void restoreXPLevel(Player player) {
		ParkourConfiguration inventoryConfig = Parkour.getConfig(ConfigType.INVENTORY);
		player.setLevel(inventoryConfig.getInt(player.getUniqueId() + ".XPLevel"));
		inventoryConfig.set(player.getUniqueId() + ".XPLevel", null);
		inventoryConfig.save();
	}

	private boolean hasDisplayPermission(Player player, Permission permission) {
		if (player.hasPermission(permission.getPermission())) {
			player.sendMessage("* " + permission.getPermission());
			return true;
		}

		return false;
	}

	/**
	 * Prepare a player for joining a course
	 * Will save and clear the inventory of the player,
	 * then populate their inventory with appropriate Parkour tools.
	 *
	 * @param player
	 * @param courseName
	 */
	private void preparePlayerForCourse(Player player, String courseName) {
		saveInventory(player);
		saveHealth(player);
		preparePlayer(player, parkour.getConfig().getString("OnJoin.SetGameMode"));

		if (parkour.getConfig().getBoolean("OnJoin.FillHealth")) {
			player.setFoodLevel(20);
		}

		if (parkour.getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			saveXPLevel(player);
			player.setLevel(0);
		}

		if (parkour.getConfig().getBoolean("OnCourse.DisableFly")) {
			player.setAllowFlight(false);
			player.setFlying(false);
		}

		giveParkourTool(player, "OnJoin.Item.LastCheckpoint", "Other.Item.LastCheckpoint");
		giveParkourTool(player, "OnJoin.Item.HideAll", "Other.Item.HideAll");
		giveParkourTool(player, "OnJoin.Item.Leave", "Other.Item.Leave");
		giveParkourTool(player, "OnJoin.Item.Restart", "Other.Item.Restart");

		for (ItemStack joinItem : CourseInfo.getJoinItems(courseName)) {
			player.getInventory().addItem(joinItem);
		}

		player.updateInventory();
	}

	private void giveParkourTool(Player player, String configPath, String translationKey) {
		Material material = Material.getMaterial(parkour.getConfig().getString(configPath + ".Material", "AIR"));

		if (material != null && material != Material.AIR && !player.getInventory().contains(material)) {
			int slot = parkour.getConfig().getInt(configPath + ".Slot");
			player.getInventory().setItem(slot, MaterialUtils.createItemStack(material, TranslationUtils.getTranslation(translationKey, false)));
		}
	}

	/**
	 * Check if the player's time is a new course or personal record.
	 *
	 * @param player
	 * @param session
	 */
	private boolean isNewRecord(Player player, ParkourSession session) {
		// if they aren't updating the row, it will be inserted whether or not it's their best time
		// for sake of performance, if we don't care if it's their best time just return
		if (!parkour.getConfig().getBoolean("OnFinish.DisplayNewRecords") &&
				!parkour.getConfig().getBoolean("OnFinish.UpdatePlayerDatabaseTime")) {
			return false;
		}

		if (parkour.getDatabase().isBestCourseTime(session.getCourse().getName(), session.getTimeFinished())) {
			if (parkour.getConfig().getBoolean("OnFinish.DisplayNewRecords")) {
				parkour.getBountifulApi().sendFullTitle(player,
						TranslationUtils.getTranslation("Parkour.CourseRecord", false),
						DateTimeUtils.displayCurrentTime(session.getTimeFinished()), true);
			}
			return true;
		}

		if (parkour.getDatabase().isBestCourseTime(player, session.getCourse().getName(), session.getTimeFinished())) {
			if (parkour.getConfig().getBoolean("OnFinish.DisplayNewRecords")) {
				parkour.getBountifulApi().sendFullTitle(player,
						TranslationUtils.getTranslation("Parkour.BestTime", false),
						DateTimeUtils.displayCurrentTime(session.getTimeFinished()), true);
			}
			return true;
		}
		return false;
	}

	public void displayParkourRanks(CommandSender sender) {
		TranslationUtils.sendHeading("Parkour Ranks", sender);
		parkourRanks.forEach((parkourLevel, parkourRank) ->
				sender.sendMessage(TranslationUtils.getTranslation("Parkour.RankInfo", false)
						.replace("%LEVEL%", parkourLevel.toString())
						.replace("%RANK%", parkourRank)));
	}

	/**
	 * Set the ParkourRank reward.
	 * Not to be confused with rewardLevel or rewardPrize, this associates a
	 * Parkour level with a message prefix. A rank is just a visual prefix. E.g.
	 * Level 10: Pro, Level 99: God
	 *
	 * @param sender
	 */
	public void setRewardParkourRank(CommandSender sender, String parkourLevel, String parkourRank) {
		if (!ValidationUtils.isPositiveInteger(parkourLevel)) {
			TranslationUtils.sendTranslation("Error.InvalidAmount", sender);
			return;
		}

		if (!ValidationUtils.isStringValid(parkourRank)) {
			sender.sendMessage(Parkour.getPrefix() + "ParkourRank is not valid.");
			return;
		}

		PlayerInfo.setRewardParkourRank(Integer.parseInt(parkourLevel), parkourRank);
		populateParkourRanks();
		TranslationUtils.sendPropertySet(sender, "ParkourRank", "ParkourLevel " + parkourLevel,
				StringUtils.colour(parkourRank));
	}

	public void processSetCommand(CommandSender sender, String[] args) {
		OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);

		if (!PlayerInfo.hasPlayerInfo(targetPlayer)) {
			TranslationUtils.sendTranslation("Error.UnknownPlayer", sender);
			return;
		}

		if (args.length == 2 && sender instanceof Player) {
			new SetPlayerConversation((Player) sender).withTargetPlayerName(args[1].toLowerCase()).begin();

		} else if (args.length >= 4) {
			SetPlayerConversation.performAction(sender, targetPlayer, args[2], args[3]);

		} else {
			TranslationUtils.sendInvalidSyntax(sender, "setplayer", "(player) [level / rank] [value]");
		}
	}

	public boolean hasSelectedValidCourse(Player player) {
		String selected = PlayerInfo.getSelectedCourse(player);
		return parkour.getCourseManager().doesCourseExists(selected);
	}

	public void resetPlayer(OfflinePlayer targetPlayer) {
		PlayerInfo.resetPlayerData(targetPlayer);
		deleteParkourSession(targetPlayer);
		parkour.getDatabase().deletePlayerTimes(targetPlayer);
	}

	/**
	 * Find the unlocked ParkourRank for new ParkourLevel.
	 * The highest Rank available should be found first, gradually decreasing until a match.
	 *
	 * @param player target player
	 * @param rewardLevel rewarded ParkourLevel
	 * @return unlocked ParkourRank
	 */
	public String getUnlockedParkourRank(Player player, int rewardLevel) {
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
			ParkourSession session = loadParkourSession(onlinePlayer);

			if (!isPlaying(onlinePlayer)) {
				continue;
			}

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

	private Location determineDestination(ParkourSession session) {
		if ((session.getParkourMode() == ParkourMode.FREEDOM || session.getParkourMode() == ParkourMode.FREE_CHECKPOINT)
				&& session.getFreedomLocation() != null) {
			return session.getFreedomLocation();
		} else {
			return session.getCheckpoint().getLocation();
		}
	}
}

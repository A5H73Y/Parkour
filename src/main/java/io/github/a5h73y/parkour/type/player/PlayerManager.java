package io.github.a5h73y.parkour.type.player;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.enums.Permission;
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
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.support.XMaterial;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
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

public class PlayerManager extends AbstractPluginReceiver {

	private final Map<Player, ParkourSession> parkourPlayers = new WeakHashMap<>();
	private final Map<Player, Long> playerDelay = new HashMap<>();
	private final Map<Integer, String> parkourRanks = new TreeMap<>();

	private final List<Player> quietPlayers = new ArrayList<>();
	private final List<Player> hiddenPlayers = new ArrayList<>();

	private final String quietOnMessage = TranslationUtils.getTranslation("Parkour.QuietOn", false);
	private final String quietOffMessage = TranslationUtils.getTranslation("Parkour.QuietOff", false);

	public PlayerManager(final Parkour parkour) {
		super(parkour);
		populateParkourPlayers();
		populateParkourRanks();
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
	 * Populate Parkour Players.
	 * As part of a server reload, there would be online players who need their Parkour session restored.
	 */
	private void populateParkourPlayers() {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			loadParkourSession(onlinePlayer);

			if (PlayerInfo.isQuietMode(onlinePlayer)) {
				enableQuietMode(onlinePlayer);
			}

			if (!isPlaying(onlinePlayer)) {
				return;
			}

			String currentCourse = getParkourSession(onlinePlayer).getCourse().getName();
			TranslationUtils.sendValueTranslation("Parkour.Continue", currentCourse, onlinePlayer);
			parkour.getScoreboardManager().addScoreboard(onlinePlayer);
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
	 * Retrieve a ParkourSession for the player.
	 *
	 * @param player target player
	 * @return ParkourSession
	 */
	public ParkourSession getParkourSession(Player player) {
		return parkourPlayers.get(player);
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
//			session.cancelVisualTimer();
			parkourPlayers.remove(player);
		}
	}

	/**
	 * Teardown a Parkour player.
	 * Remove all in-memory references to the player, persisting any data to a file.
	 *
	 * @param player parkour player
	 */
	public void teardownParkourPlayer(Player player) {
		parkour.getChallengeManager().terminateChallenge(player);
		parkour.getQuestionManager().removeQuestion(player);
		storeParkourSession(player);
		quietPlayers.remove(player);
		hiddenPlayers.remove(player);
		playerDelay.remove(player);
		PlayerInfo.setQuietMode(player, isInQuietMode(player));
		PlayerInfo.persistChanges();
	}

	public void teardownParkourPlayers() {
		for (Player player : parkourPlayers.keySet()) {
			createParkourSessionFile(player);
		}
	}

	/**
	 * Determine if the player is on a course.
	 *
	 * @param player target player
	 * @return playing course
	 */
	public boolean isPlaying(Player player) {
		return parkourPlayers.containsKey(player);
	}

	/**
	 * Returns whether the player is in Test Mode.
	 * Used for validation, not to be treated as a normal Parkour course.
	 *
	 * @param player target player
	 * @return player is in TestMode
	 */
	public boolean isPlayerInTestMode(Player player) {
		ParkourSession session = getParkourSession(player);
		return session != null && Constants.TEST_MODE.equals(session.getCourse().getName());
	}

	/**
	 * Get the number of Parkour players.
	 * @return parkour players
	 */
	public int getNumberOfParkourPlayer() {
		return parkourPlayers.size();
	}

	public void joinCourse(Player player, String courseName) {
		Course course = parkour.getCourseManager().getCourse(courseName);

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
	 *
	 * @param player target player
	 * @param course target course
	 */
	public void joinCourse(Player player, Course course) {
		if (parkour.getConfig().isTeleportToJoinLocation()) {
			PlayerInfo.setJoinLocation(player);
		}
		player.teleport(course.getCheckpoints().get(0).getLocation());
		preparePlayerForCourse(player, course.getName());
		CourseInfo.increaseView(course.getName());
		CourseInfo.persistChanges();
		PlayerInfo.setLastPlayedCourse(player, course.getName());
		PlayerInfo.persistChanges();

		// they are aren't on a course
		if (!isPlaying(player) && !isInQuietMode(player)) {
			boolean displayTitle = parkour.getConfig().getBoolean("DisplayTitle.JoinCourse");

			String subTitle = "";
			if (course.hasMaxDeaths()) {
				subTitle = TranslationUtils.getValueTranslation(
						"Parkour.JoinLives", String.valueOf(course.getMaxDeaths()), false);
			}

			parkour.getBountifulApi().sendFullTitle(player,
					TranslationUtils.getValueTranslation("Parkour.Join", course.getName(), false),
					subTitle,  displayTitle);

			if (parkour.getConfig().isCompletedCoursesEnabled()
					&& PlayerInfo.getCompletedCourses(player).contains(course.getName())) {
				TranslationUtils.sendValueTranslation("Parkour.AlreadyCompleted", course.getName(), player);
			}
		} else {
			removePlayer(player);
			if (!isInQuietMode(player)) {
				TranslationUtils.sendTranslation("Parkour.TimeReset", player);
			}
		}

		ParkourSession session = addPlayer(player, new ParkourSession(course));
		setupParkourMode(player);
		parkour.getScoreboardManager().addScoreboard(player);
//		session.startVisualTimer(player);
		Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinCourseEvent(player, course.getName()));
	}


	/**
	 * Execute the joinCourse after a delay.
	 *
	 * @param player
	 * @param courseName
	 */
	public void joinCourseButDelayed(Player player, String courseName, int delay) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Parkour.getInstance(), () -> joinCourse(player, courseName), delay);
	}

	/**
	 * Leave the player from a course.
	 * Will remove the player from the players, which will also dispose of their course session.
	 *
	 * @param player target player
	 * @param silent silently leave the course
	 */
	public void leaveCourse(Player player, boolean silent) {
		if (!isPlaying(player)) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);
			return;
		}

		ParkourSession session = getParkourSession(player);
		if (!silent) {
			parkour.getBountifulApi().sendSubTitle(player,
					TranslationUtils.getValueTranslation("Parkour.Leave", session.getCourse().getName(), false),
					parkour.getConfig().getBoolean("DisplayTitle.Leave"));
		}

		teardownParkourMode(player);
		removePlayer(player);
		preparePlayer(player, parkour.getConfig().getString("OnFinish.SetGameMode"));
		restoreHealth(player);
		loadInventory(player);

		parkour.getChallengeManager().terminateChallenge(player);

		if (parkour.getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			player.setLevel(0);
		}

		if (!silent) {
			parkour.getLobbyManager().teleportToLeaveDestination(player, session);
		}

		if (hasHiddenPlayers(player)) {
			toggleVisibility(player, true);
		}

		forceVisible(player);
		parkour.getScoreboardManager().removeScoreboard(player);
		deleteParkourSession(player);
		Bukkit.getServer().getPluginManager().callEvent(new PlayerLeaveCourseEvent(player, session.getCourse().getName()));
	}

	/**
	 * Leave the player from a course.
	 * Will remove the player from the players, which will also dispose of their course session.
	 * Teleports the player to their designated location.
	 *
	 * @param player target player
	 */
	public void leaveCourse(Player player) {
		leaveCourse(player, false);
	}

	/**
	 * Increase the ParkourSession checkpoint number
	 * Once a player activates a new checkpoint, it will setup the next checkpoint ready.
	 * A message will be sent to the player notifying them of their progression.
	 *
	 * @param player
	 */
	public void increaseCheckpoint(Player player) {
		ParkourSession session = getParkourSession(player);

		if (session == null) {
			TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);
			return;
		}

		session.increaseCheckpoint();

		if (parkour.getScoreboardManager().isEnabled()) {
			parkour.getScoreboardManager().updateScoreboardCheckpoints(player, session.getCurrentCheckpoint() + " / " + session.getCourse().getNumberOfCheckpoints());
		}

		boolean showTitle = parkour.getConfig().getBoolean("DisplayTitle.Checkpoint");
		if (session.hasAchievedAllCheckpoints()) {
			parkour.getBountifulApi().sendSubTitle(player, TranslationUtils.getTranslation("Event.AllCheckpoints", false), showTitle);

		} else {
			String checkpointMessage = TranslationUtils.getTranslation("Event.Checkpoint", false)
					.replace("%CURRENT%", String.valueOf(session.getCurrentCheckpoint()))
					.replace("%TOTAL%", String.valueOf(session.getCourse().getNumberOfCheckpoints()));

			parkour.getBountifulApi().sendSubTitle(player, checkpointMessage, showTitle);
		}
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

		if (parkour.getScoreboardManager().isEnabled()) {
			parkour.getScoreboardManager().updateScoreboardDeaths(player, String.valueOf(session.getDeaths()));
		}

		if ((session.getParkourMode() == ParkourMode.FREEDOM || session.getParkourMode() == ParkourMode.FREE_CHECKPOINT)
				&& session.getFreedomLocation() != null) {
			player.teleport(session.getFreedomLocation());
		} else {
			player.teleport(session.getCheckpoint().getLocation());
		}

		// if it's the first checkpoint
		if (session.getCurrentCheckpoint() == 0) {
			if (parkour.getConfig().getBoolean("OnDie.ResetTimeWithNoCheckpoint")) {
				session.resetTimeStarted();
				if (!isInQuietMode(player)) {
					player.sendMessage(TranslationUtils.getTranslation("Parkour.Die1") + TranslationUtils.getTranslation("Parkour.TimeReset", false));
				}
			} else {
				if (!isInQuietMode(player)) {
					TranslationUtils.sendTranslation("Parkour.Die1", player);
				}
			}
		} else {
			if (!isInQuietMode(player)) {
				TranslationUtils.sendValueTranslation("Parkour.Die2", String.valueOf(session.getCurrentCheckpoint()), player);
			}
		}

		if (parkour.getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			player.setLevel(session.getDeaths());
		}

		//TODO sounds

		preparePlayer(player, parkour.getConfig().getString("OnJoin.SetGameMode"));
		Bukkit.getServer().getPluginManager().callEvent(new PlayerDeathEvent(player, session.getCourse().getName()));
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
			TranslationUtils.sendValueTranslation("Error.Cheating2", String.valueOf(session.getCourse().getNumberOfCheckpoints()), player);
			playerDie(player);
			return;
		}

		session.setTimeFinished();
		preparePlayer(player, parkour.getConfig().getString("OnFinish.SetGameMode"));

		if (hasHiddenPlayers(player)) {
			toggleVisibility(player, true);
		}

		displayFinishMessage(player, session);
		CourseInfo.increaseComplete(courseName);
		teardownParkourMode(player);
		removePlayer(player);

		parkour.getChallengeManager().completeChallenge(player);

		if (parkour.getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
			player.setLevel(0);
		}

		final long delay = parkour.getConfig().getLong("OnFinish.TeleportDelay");
		final boolean teleportAway = parkour.getConfig().getBoolean("OnFinish.TeleportAway");

		if (delay <= 0) {
			restoreHealth(player);
			loadInventory(player);
			rewardPrize(player, courseName);
			if (teleportAway) {
				teleportCourseCompletion(player, courseName);
			}
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(parkour, () -> {
				restoreHealth(player);
				loadInventory(player);
				rewardPrize(player, courseName);
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
		PlayerInfo.persistChanges();

		forceVisible(player);
		parkour.getScoreboardManager().removeScoreboard(player);
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
		joinCourse(player, course);
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

		// TODO - move to a different method - Level player
		int currentLevel = PlayerInfo.getParkourLevel(player);
		int newParkourLevel = currentLevel;

		// set parkour level
		int rewardLevel = CourseInfo.getRewardLevel(courseName);
		if (rewardLevel > 0 && currentLevel < rewardLevel) {
			newParkourLevel = rewardLevel;
		}

		// increase parkour level
		int rewardAddLevel = CourseInfo.getRewardLevelAdd(courseName);
		if (rewardAddLevel > 0) {
			newParkourLevel = currentLevel + rewardAddLevel;
		}

		// if their parkour level has increased
		if (newParkourLevel > currentLevel) {
			// update parkour rank
			String rewardRank = getUnlockedParkourRank(player, newParkourLevel);
			if (rewardRank != null) {
				PlayerInfo.setRank(player, rewardRank);
				TranslationUtils.sendValueTranslation("Parkour.RewardRank", rewardRank, player);
			}

			PlayerInfo.setParkourLevel(player, newParkourLevel);
			if (parkour.getConfig().getBoolean("Other.Display.LevelReward")) {
				player.sendMessage(TranslationUtils.getTranslation("Parkour.RewardLevel")
						.replace("%LEVEL%", String.valueOf(rewardLevel))
						.replace("%COURSE%", courseName));
			}
		}

		// Execute the command
		if (CourseInfo.hasCommandPrize(courseName)) {
			for (String command : CourseInfo.getCommandsPrize(courseName)) {
				parkour.getServer().dispatchCommand(
						parkour.getServer().getConsoleSender(),
						command.replace("%PLAYER%", player.getName()));
			}
		}

		// Give player Parkoins
		int parkoins = CourseInfo.getRewardParkoins(courseName);
		if (parkoins > 0) {
			rewardParkoins(player, parkoins);
		}

		parkour.getEconomyApi().giveEconomyPrize(player, courseName);
		player.updateInventory();
		PlayerInfo.persistChanges();
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

		} else if (CourseInfo.hasLinkedLobby(courseName)) {
			String lobbyName = CourseInfo.getLinkedLobby(courseName);

			if (parkour.getConfig().contains("Lobby." + lobbyName + ".World")) {
				String[] args = {null, lobbyName};
				parkour.getLobbyManager().joinLobby(args, player);
				return;
			}
		}

		parkour.getLobbyManager().joinLobby(null, player);
	}

	public void rocketLaunchPlayer(Player player) {
		Vector velocity = player.getLocation().getDirection().normalize();
		velocity = velocity.multiply(-1.5);
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
	public void applyEffect(String[] lines, Player player) {
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
		ParkourMode courseMode = getParkourSession(player).getParkourMode();

		if (courseMode == ParkourMode.NONE) {
			return;
		}

		if (courseMode == ParkourMode.FREEDOM) {
			TranslationUtils.sendTranslation("Mode.Freedom.JoinText", player);
			player.getInventory().addItem(MaterialUtils.createItemStack(
					XMaterial.REDSTONE_TORCH.parseMaterial(), TranslationUtils.getTranslation("Mode.Freedom.ItemName", false)));

		} else if (courseMode == ParkourMode.SPEEDY) {
			float speed = Float.parseFloat(parkour.getConfig().getString("ParkourModes.Speedy.SetSpeed"));
			player.setWalkSpeed(speed);

		} else if (courseMode == ParkourMode.ROCKETS) {
			TranslationUtils.sendTranslation("Mode.Rockets.JoinText", player);
			player.getInventory().addItem(MaterialUtils.createItemStack(
					XMaterial.FIREWORK_ROCKET.parseMaterial(), TranslationUtils.getTranslation("Mode.Rockets.ItemName", false)));
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

	public void enableQuietMode(Player player) {
		parkour.getBountifulApi().sendActionBar(player, quietOnMessage, true);
		quietPlayers.add(player);
	}

	public void disableQuietMode(Player player) {
		quietPlayers.remove(player);
		parkour.getBountifulApi().sendActionBar(player, quietOffMessage, true);
	}

	public boolean isInQuietMode(Player player) {
		return quietPlayers.contains(player);
	}

	/**
	 * Toggle quiet mode.
	 * Will add / remove the player from the list of quiet players.
	 * If enabled, will limit the amount of Parkour messages displayed to the player.
	 *
	 * @param player requesting player
	 */
	public void toggleQuietMode(Player player) {
		if (isInQuietMode(player)) {
			disableQuietMode(player);

		} else {
			enableQuietMode(player);
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
			TranslationUtils.sendTranslation("Event.HideAll1");

		} else {
			addHidden(player);
			TranslationUtils.sendTranslation("Event.HideAll2");
		}
	}

	public void forceVisible(Player player) {
		for (Player players : Bukkit.getOnlinePlayers()) {
			players.showPlayer(player);
		}
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

	/**
	 * Delay certain actions
	 * This check can be bypassed by admins / ops
	 *
	 * @param player
	 * @param secondsToWait
	 * @param displayMessage display cooldown error
	 * @return boolean (wait expired)
	 */
	public boolean delayPlayer(Player player, int secondsToWait, boolean displayMessage) {
		if (player.isOp()) {
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
	 * Check to see if the minimum amount of time has passed (in days) to allow the plugin to provide the prize again
	 *
	 * @param player
	 * @param courseName
	 * @param displayMessage
	 * @return boolean
	 */
	public boolean hasPrizeCooldownDurationPassed(Player player, String courseName, boolean displayMessage) {
		int rewardDelay = CourseInfo.getRewardDelay(courseName);

		if (rewardDelay <= 0) {
			return true;
		}

		long lastRewardTime = PlayerInfo.getLastRewardedTime(player, courseName);

		if (lastRewardTime <= 0) {
			return true;
		}

		long timeDifference = System.currentTimeMillis() - lastRewardTime;
		long daysDelay = DateTimeUtils.convertDaysToMilliseconds(rewardDelay);

		if (timeDifference > daysDelay) {
			return true;
		}

		if (parkour.getConfig().isDisplayPrizeCooldown() && displayMessage) {
			TranslationUtils.sendValueTranslation("Error.PrizeCooldown",
					DateTimeUtils.getTimeRemaining(player, courseName), player.getPlayer());
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
		if (inventoryConfig.contains(player.getName() + ".Inventory")) {
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

		Parkour.getConfig(ConfigType.INVENTORY).set(player.getName(), null);
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

		int total = parkoins + PlayerInfo.getParkoins(player);
		PlayerInfo.setParkoins(player, total);
		PlayerInfo.persistChanges();
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

		int current = PlayerInfo.getParkoins(player);
		current = (current < parkoins) ? 0 : (current - parkoins);

		PlayerInfo.setParkoins(player, current);
		PlayerInfo.persistChanges();
		player.sendMessage(Parkour.getPrefix() + parkoins + " Parkoins deducted! New total: " + ChatColor.AQUA + current);
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
					|| hasDisplayPermission(player, Permission.SIGN_ALL)
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
	public void giveParkourKit(String[] args, Player player) {
		if (parkour.getConfig().getBoolean("Other.ParkourKit.ReplaceInventory")) {
			player.getInventory().clear();
		}

		String kitName = args != null && args.length == 2 ? args[1] : Constants.DEFAULT;
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
		player.sendMessage(TranslationUtils.getTranslation("Other.Kit"));
		PluginUtils.logToFile(player.getName() + " received the kit");
	}

	/**
	 * Toggle Test Mode
	 * This will enable / disable the testmode functionality, by creating a
	 * dummy "Test Mode" course for the player.
	 *
	 * @param player
	 */
	public void toggleTestMode(String[] args, Player player) {
		if (isPlaying(player)) {
			if (isPlayerInTestMode(player)) {
				removePlayer(player);
				parkour.getBountifulApi().sendActionBar(player,
						TranslationUtils.getTranslation("Parkour.TestModeOff", false), true);
			} else {
				player.sendMessage(Parkour.getPrefix() + "You are not in Test Mode.");
			}
		} else {
			String kitName = args.length == 2 ? args[1].toLowerCase() : Constants.DEFAULT;
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
	 * @param player
	 */
	public void displayParkourInfo(String[] args, Player player) {
		OfflinePlayer target = args.length <= 1 ? player : Bukkit.getOfflinePlayer(args[1]);
		ParkourSession session = getParkourSession(target.getPlayer());

		if (session == null && !PlayerInfo.hasPlayerInfo(target)) {
			player.sendMessage(Parkour.getPrefix() + "Player has never played Parkour. What is wrong with them?!");
			return;
		}

		TranslationUtils.sendHeading(target.getName() + "'s information", player);

		if (session != null) {
			player.sendMessage("Course: " + ChatColor.AQUA + session.getCourse().getName());
			player.sendMessage("Deaths: " + ChatColor.AQUA + session.getDeaths());
			player.sendMessage("Time: " + ChatColor.AQUA + session.displayTime());
			player.sendMessage("Checkpoint: " + ChatColor.AQUA + session.getCurrentCheckpoint());
		}

		if (PlayerInfo.hasPlayerInfo(target)) {
			int level = PlayerInfo.getParkourLevel(target);
			String selected = PlayerInfo.getSelectedCourse(target);

			if (level > 0) {
				player.sendMessage("Level: " + ChatColor.AQUA + level);
			}

			if (selected != null && selected.length() > 0) {
				player.sendMessage("Editing: " + ChatColor.AQUA + selected);
			}

			if (PlayerInfo.getParkoins(target) > 0) {
				player.sendMessage("Parkoins: " + ChatColor.AQUA + PlayerInfo.getParkoins(target));
			}

			if (parkour.getConfig().isCompletedCoursesEnabled()) {
				player.sendMessage("Courses Completed: " + ChatColor.AQUA + PlayerInfo.getNumberOfCoursesCompleted(target) + " / " + CourseInfo.getAllCourses().size());
			}
		}
	}

	public void setParkourLevel(String[] args, CommandSender sender) {
		if (!Validation.isPositiveInteger(args[2])) {
			sender.sendMessage(Parkour.getPrefix() + "Minimum level is not valid.");
			return;
		}

		Player target = Bukkit.getPlayer(args[1]);

		if (target == null) {
			sender.sendMessage(Parkour.getPrefix() + "That player is not online.");
			return;
		}

		int newLevel = Integer.parseInt(args[2]);
		PlayerInfo.setParkourLevel(target, newLevel);
		PlayerInfo.persistChanges();

		sender.sendMessage(Parkour.getPrefix() + target.getName() + "'s Level was set to " + newLevel);
	}

	public void setParkourRank(String[] args, CommandSender sender) {
		Player target = Bukkit.getPlayer(args[1]);

		if (target == null) {
			sender.sendMessage(Parkour.getPrefix() + "That player is not online.");
			return;
		}

		PlayerInfo.setRank(target, args[2]);
		PlayerInfo.persistChanges();
		sender.sendMessage(Parkour.getPrefix() + target.getName() + "'s Rank was set to " + args[2]);
	}

	public void storeParkourSession(Player player) {
		saveParkourSession(player);
		parkourPlayers.remove(player);
	}

	public void saveParkourSession(Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(parkour, () -> {
			createParkourSessionFile(player);
		});
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
		ParkourSession session = null;
		File sessionFile = new File(getSessionsPath(), player.getUniqueId().toString());

		if (sessionFile.exists()) {
			try (
					FileInputStream fout = new FileInputStream(sessionFile);
					ObjectInputStream oos = new ObjectInputStream(fout)
			) {
				session = (ParkourSession) oos.readObject();
				if (session != null) {
					session.setCourse(parkour.getCourseManager().getCourse(session.getCourseName()));
					parkourPlayers.put(player, session);
				}
			} catch (IOException | ClassNotFoundException e) {
				PluginUtils.log("Player's session couldn't be loaded: " + e.getMessage(), 2);
				e.printStackTrace();
			}
		}
		return session;
	}

	private void deleteParkourSession(Player player) {
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
					.replace("%PLAYER%", entry.getKey().getName())
					.replace("%COURSE%", entry.getValue().getCourse().getName())
					.replace("%DEATHS%", String.valueOf(entry.getValue().getDeaths()))
					.replace("%TIME%", entry.getValue().displayTime()));
		}
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
					TranslationUtils.getValueTranslation("Parkour.FinishCourse1", session.getCourse().getName(), false),
					TranslationUtils.getTranslation("Parkour.FinishCourse2", false)
							.replace("%DEATHS%", String.valueOf(session.getDeaths()))
							.replace("%TIME%", session.displayTime()),
					parkour.getConfig().getBoolean("DisplayTitle.Finish"));
		}

		String finishBroadcast = TranslationUtils.getTranslation("Parkour.FinishBroadcast")
				.replace("%PLAYER%", player.getName())
				.replace("%COURSE%", session.getCourse().getName())
				.replace("%DEATHS%", String.valueOf(session.getDeaths()))
				.replace("%TIME%", session.displayTime());

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
		ParkourConfiguration config = Parkour.getConfig(ConfigType.INVENTORY);
		config.set(player.getName() + ".Health", player.getHealth());
		config.set(player.getName() + ".Hunger", player.getFoodLevel());
		config.save();
	}

	/**
	 * Load the players original health.
	 * When they leave or finish a course, their hunger and exhaustion will be restored to them.
	 * Will delete the health from the config once loaded.
	 *
	 * @param player
	 */
	private void restoreHealth(Player player) {
		ParkourConfiguration invConfig = Parkour.getConfig(ConfigType.INVENTORY);

		double health = Math.min(player.getMaxHealth(), invConfig.getDouble(player.getName() + ".Health"));
		player.setHealth(health);
		player.setFoodLevel(invConfig.getInt(player.getName() + ".Hunger"));
		invConfig.set(player.getName() + ".Health", null);
		invConfig.set(player.getName() + ".Hunger", null);
		invConfig.save();
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
			parkour.getBountifulApi().sendFullTitle(player,
					TranslationUtils.getTranslation("Parkour.CourseRecord", false),
					DateTimeUtils.displayCurrentTime(session.getTimeFinished()), true);
			return true;
		}

		if (parkour.getDatabase().isBestCourseTime(player, session.getCourse().getName(), session.getTimeFinished())) {
			parkour.getBountifulApi().sendFullTitle(player,
					TranslationUtils.getTranslation("Parkour.BestTime", false),
					DateTimeUtils.displayCurrentTime(session.getTimeFinished()), true);
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
	 * @param args
	 * @param sender
	 */
	public void setRewardParkourRank(String[] args, CommandSender sender) {
		if (!Validation.isPositiveInteger(args[1])) {
			sender.sendMessage(Parkour.getPrefix() + "ParkourLevel needs to be numeric.");
			return;
		}

		if (!Validation.isStringValid(args[2])) {
			sender.sendMessage(Parkour.getPrefix() + "ParkourRank is not valid.");
			return;
		}

		PlayerInfo.setRewardRank(Integer.parseInt(args[1]), args[2]);
		PlayerInfo.persistChanges();
		populateParkourRanks();
		sender.sendMessage(Parkour.getPrefix() + "ParkourRank for ParkourLevel " + args[1] + " was set to " + StringUtils.colour(args[2]));
	}
}

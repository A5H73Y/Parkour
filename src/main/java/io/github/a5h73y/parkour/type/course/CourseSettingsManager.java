package io.github.a5h73y.parkour.type.course;

import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_INVALID_AMOUNT;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;

import com.google.common.io.Files;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.CoursePrizeConversation;
import io.github.a5h73y.parkour.conversation.ParkourModeConversation;
import io.github.a5h73y.parkour.conversation.SetCourseConversation;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.TriConsumer;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class CourseSettingsManager extends AbstractPluginReceiver {

	// course actions to set data
	private final Map<String, TriConsumer<CommandSender, String, String>> courseSettingActions = new HashMap<>();

	public CourseSettingsManager(Parkour parkour) {
		super(parkour);
		populateCourseSettingActions();
	}

	public Set<String> getCourseSettingActions() {
		return courseSettingActions.keySet();
	}

	/**
	 * Check if Course is known by Parkour.
	 *
	 * @param courseName course name
	 * @return course exists
	 */
	private boolean doesCourseExist(final String courseName) {
		return parkour.getCourseManager().doesCourseExist(courseName);
	}

	private void clearCourseCache() {
		parkour.getCourseManager().clearCache();
	}

	private void clearCourseCache(String courseName) {
		parkour.getCourseManager().clearCache(courseName);
	}

	/**
	 * Process Set Course Command.
	 * This Command can be used to set various attributes about the Course in one place.
	 *
	 * @param commandSender command sender
	 * @param args command arguments
	 */
	public void processSetCommand(final CommandSender commandSender, final String... args) {
		if (!doesCourseExist(args[1])) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, args[1], commandSender);
			return;
		}

		if (args.length == 2) {
			new SetCourseConversation((Conversable) commandSender).withCourseName(args[1]).begin();

		} else if (args.length == 3 || args.length == 4) {
			performAction(commandSender, args[1], args[2], args.length == 4 ? args[3] : null);

		} else {
			if (args[2].equalsIgnoreCase("message")) {
				setCourseMessage(commandSender, args[1], args[3], StringUtils.extractMessageFromArgs(args, 4));

			} else if (args[2].equalsIgnoreCase("command")) {
				setCourseCommand(commandSender, args[1], args[3], StringUtils.extractMessageFromArgs(args, 4));

			} else if (args[2].equalsIgnoreCase("displayname")) {
				performAction(commandSender, args[1], args[2], StringUtils.extractMessageFromArgs(args, 3));

			} else {
				parkour.getParkourCommands().sendInvalidSyntax(commandSender, "setcourse");
			}
		}
	}

	public void performAction(CommandSender commandSender, String courseName, String action, String value) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!courseSettingActions.containsKey(action.toLowerCase())) {
			TranslationUtils.sendMessage(commandSender, "Unknown Course action command");
			return;
		}

		courseSettingActions.get(action.toLowerCase()).accept(commandSender, courseName, value);
	}

	private void notifyActionChange(CommandSender commandSender, String property, String courseName, String newValue) {
		TranslationUtils.sendPropertySet(commandSender, property, courseName, newValue);
		PluginUtils.logToFile("The " + property + " for " + courseName + " was set to " + newValue + " by " + commandSender.getName());
	}

	/**
	 * Set the ChallengeOnly status of the Course.
	 * Set whether the Player can only join the Course if they are part of a Challenge.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 */
	public void setChallengeOnlyStatus(CommandSender commandSender, String courseName, boolean value) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setChallengeOnly(value);
		notifyActionChange(commandSender, "Challenge Only Status", courseName, String.valueOf(value));
	}

	/**
	 * Set the Creator of the Course.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param value new creator name
	 */
	public void setCreator(final CommandSender commandSender, final String courseName, final String value) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setCreator(value);
		notifyActionChange(commandSender, "Creator", courseName, value);
	}

	/**
	 * Set the Course Display name for the Course.
	 * This will be presented when sending a message to Player(s) instead of the unique storage name.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param input course display name
	 */
	public void setDisplayName(CommandSender commandSender, String courseName, String input) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!ValidationUtils.isStringValid(input)) {
			TranslationUtils.sendTranslation("Error.InvalidValue", commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setCourseDisplayName(input);
		clearCourseCache(courseName);
		notifyActionChange(commandSender, "Course Display Name", courseName, StringUtils.colour(input));
	}

	/**
	 * Set the Course's linked Course.
	 * Target Course will be joined on completion of Course.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param targetCourse target course name
	 */
	public void setCourseToCourseLink(final CommandSender commandSender, final String courseName, final String targetCourse) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!doesCourseExist(targetCourse)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, targetCourse, commandSender);
			return;
		}

		CourseConfig courseConfig = CourseConfig.getConfig(courseName);
		if (courseConfig.hasLinkedLobby()) {
			TranslationUtils.sendMessage(commandSender, "This Course is linked to a Lobby!");
			return;
		}

		courseConfig.setLinkedCourse(targetCourse);
		notifyActionChange(commandSender, "Linked Course", courseName, targetCourse);
	}

	/**
	 * Set the Course's linked Lobby.
	 * Target Lobby will be joined on completion of Course.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param targetLobby target lobby name
	 */
	public void setCourseToLobbyLink(final CommandSender commandSender, final String courseName, String targetLobby) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!parkour.getConfigManager().getLobbyConfig().doesLobbyExist(targetLobby)) {
			TranslationUtils.sendValueTranslation("Error.UnknownLobby", targetLobby, commandSender);
			return;
		}

		CourseConfig courseConfig = CourseConfig.getConfig(courseName);
		if (courseConfig.hasLinkedCourse()) {
			TranslationUtils.sendMessage(commandSender, "This Course is linked to a Course!");
			return;
		}

		courseConfig.setLinkedLobby(targetLobby);
		notifyActionChange(commandSender, "Linked Lobby", courseName, targetLobby);
	}

	/**
	 * Set the Manual Checkpoints flag value.
	 * Allows the Course to have manual checkpoints set on it, either by Player or by external source.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param value flag value
	 */
	public void setManualCheckpoints(CommandSender commandSender, String courseName, boolean value) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setManualCheckpoints(value);
		notifyActionChange(commandSender, "Manual Checkpoints", courseName, String.valueOf(value));
	}

	/**
	 * Set MaxDeaths for Course.
	 * Set the maximum amount of deaths a player can accumulate before failing the course.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param value new maximum deaths
	 */
	public void setMaxDeaths(final CommandSender commandSender, final String courseName, final String value) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!ValidationUtils.isPositiveInteger(value)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setMaximumDeaths(Integer.parseInt(value));
		clearCourseCache(courseName);
		notifyActionChange(commandSender, "Maximum Deaths", courseName, value);
	}

	/**
	 * Set Maximum Fall Tick for Course.
	 * Set the maximum amount of ticks the Player can fall before dying.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param fallTicksValue maximum fall ticks value
	 */
	public void setMaxFallTicks(final CommandSender commandSender, final String courseName, final String fallTicksValue) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!ValidationUtils.isPositiveInteger(fallTicksValue)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setMaximumFallTicks(Integer.parseInt(fallTicksValue));
		clearCourseCache(courseName);
		notifyActionChange(commandSender, "Maximum Fall Ticks", courseName, fallTicksValue);
	}

	/**
	 * Set Maximum Time for Course in seconds.
	 * Set the maximum amount of seconds a player can accumulate before failing the course.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param secondsValue new maximum seconds
	 */
	public void setMaxTime(final CommandSender commandSender, final String courseName, final String secondsValue) {
		if (!parkour.getParkourConfig().getBoolean("OnCourse.DisplayLiveTime")
				&& !(parkour.getParkourConfig().getBoolean("Scoreboard.Enabled")
				&& parkour.getParkourConfig().getBoolean("Scoreboard.LiveTimer.Enabled"))) {
			TranslationUtils.sendMessage(commandSender, "The live timer is disabled!");
			return;
		}

		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!ValidationUtils.isPositiveInteger(secondsValue)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		int seconds = Integer.parseInt(secondsValue);
		CourseConfig.getConfig(courseName).setMaximumTime(seconds);
		clearCourseCache(courseName);
		notifyActionChange(commandSender, "Maximum Time Limit", courseName, DateTimeUtils.convertSecondsToTime(seconds));
	}

	/**
	 * Set the minimum ParkourLevel required to join the Course.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param value new minimum ParkourLevel
	 */
	public void setMinimumParkourLevel(final CommandSender commandSender, final String courseName, final String value) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!ValidationUtils.isPositiveInteger(value)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setMinimumParkourLevel(Integer.parseInt(value));
		notifyActionChange(commandSender, "Minimum ParkourLevel", courseName, value);
	}

	/**
	 * Link ParkourKit to Course.
	 * The ParkourKit will be linked to the Course to be loaded upon Course join.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param kitName parkour kit name
	 */
	public void setParkourKit(final CommandSender commandSender, final String courseName, final String kitName) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!parkour.getConfigManager().getParkourKitConfig().doesParkourKitExist(kitName)) {
			TranslationUtils.sendTranslation("Error.UnknownParkourKit", commandSender);
			return;
		}

		CourseConfig courseConfig = CourseConfig.getConfig(courseName);

		if (courseConfig.hasParkourKit()) {
			TranslationUtils.sendMessage(commandSender, "This Course is already linked to a ParkourKit, continuing anyway...");
		}

		courseConfig.setParkourKit(kitName);
		clearCourseCache(courseName);
		notifyActionChange(commandSender, "ParkourKit", courseName, kitName);
	}

	public void setParkourMode(final CommandSender commandSender, final String courseName, final ParkourMode value) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setParkourMode(value);
		notifyActionChange(commandSender, "ParkourMode", courseName, value.getDisplayName());
	}

	public void setPotionParkourMode(final CommandSender commandSender, final String courseName,
	                                 String potionEffectType, @Nullable String durationAmplifier, @Nullable String joinMessage) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		CourseConfig courseConfig = CourseConfig.getConfig(courseName);
		courseConfig.setParkourMode(ParkourMode.POTION);
		courseConfig.addPotionParkourModeEffect(potionEffectType, durationAmplifier);
		courseConfig.setPotionJoinMessage(joinMessage);
		notifyActionChange(commandSender, "Potion ParkourMode", courseName, potionEffectType + " " + durationAmplifier);
	}

	/**
	 * Set the Player Limit for Course.
	 * Set a limit on the number of players that can play the Course concurrently.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param limit player limit
	 */
	public void setPlayerLimit(final CommandSender commandSender, final String courseName, final String limit) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!ValidationUtils.isPositiveInteger(limit)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setPlayerLimit(Integer.parseInt(limit));
		notifyActionChange(commandSender, "Player Limit", courseName, limit);
	}

	/**
	 * Set the Course's Ready status.
	 * When configured a Course may not be joinable by others until it has been flagged as 'ready'.
	 * A Course's data will only be cached when it has been marked as Ready.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 */
	public void setReadyStatus(final CommandSender commandSender, final String courseName, boolean value) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setReadyStatus(value);
		notifyActionChange(commandSender, "Ready Status", courseName, String.valueOf(value));
	}

	/**
	 * Rename the Course.
	 * Name of the Course will be changed to target Course name.
	 * Leaderboard reference will be updated.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param targetCourseName target course name
	 */
	public void setRenameCourse(final CommandSender commandSender, final String courseName, final String targetCourseName) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (doesCourseExist(targetCourseName)) {
			TranslationUtils.sendValueTranslation("Error.Exist", targetCourseName, commandSender);
			return;
		}

		try {
			Files.copy(CourseConfig.getCourseConfigFile(courseName), CourseConfig.getCourseConfigFile(targetCourseName));
			CourseConfig.getConfig(targetCourseName).set("Name", targetCourseName.toLowerCase());
			CourseConfig.deleteCourseData(courseName);
			parkour.getDatabaseManager().renameCourse(courseName, targetCourseName);
			clearCourseCache(courseName);
			parkour.getCourseManager().getCourseNames().remove(courseName);
			parkour.getCourseManager().getCourseNames().add(targetCourseName);
			notifyActionChange(commandSender, "Name", courseName, targetCourseName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resetCourseLinks(final CommandSender commandSender, final String courseName) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).resetLinks();
		notifyActionChange(commandSender, "Linked Status", courseName, "none");
	}

	/**
	 * Set the Course's Resumable status.
	 * Set whether the Player can resume the Course after leaving the Course.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 */
	public void setResumable(CommandSender commandSender, String courseName, boolean value) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (parkour.getParkourConfig().isLeaveDestroyCourseProgress()) {
			TranslationUtils.sendMessage(commandSender,
					"Disable 'OnLeave.DestroyCourseProgress' in the plugin configuration to allow for Courses to be resumable.");
			return;
		}

		CourseConfig.getConfig(courseName).setResumable(value);
		notifyActionChange(commandSender, "Resumable", courseName, String.valueOf(value));
	}

	/**
	 * Set the Reward Delay for the Course.
	 * Set the number of hours that must elapse before the Prize can be awarded again to the same Player.
	 * The Delay can be a decimal, so "0.5" would be 30 minutes, and "48" would be 2 days.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param delay prize delay
	 */
	public void setRewardDelay(final CommandSender commandSender, final String courseName, final String delay) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!ValidationUtils.isPositiveDouble(delay)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setRewardDelay(Double.parseDouble(delay));
		long milliseconds = DateTimeUtils.convertHoursToMilliseconds(Double.parseDouble(delay));
		notifyActionChange(commandSender, "Reward Delay", courseName,
				DateTimeUtils.convertMillisecondsToDateTime(milliseconds));
	}

	/**
	 * Set Course's ParkourLevel reward.
	 * Set to reward the Player with the ParkourLevel on course completion.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param parkourLevel parkour level
	 */
	public void setRewardParkourLevel(final CommandSender commandSender, final String courseName, final String parkourLevel) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!ValidationUtils.isPositiveInteger(parkourLevel)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setRewardParkourLevel(Integer.parseInt(parkourLevel));
		notifyActionChange(commandSender, "ParkourLevel reward", courseName, parkourLevel);
	}

	/**
	 * Set Course's ParkourLevel reward increase.
	 * Set to reward the Player with an increment to ParkourLevel on course completion.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param parkourLevelIncrease parkour level increase
	 */
	public void setRewardParkourLevelIncrease(final CommandSender commandSender, final String courseName,
	                                          final String parkourLevelIncrease) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!ValidationUtils.isPositiveInteger(parkourLevelIncrease)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setRewardParkourLevelIncrease(parkourLevelIncrease);
		notifyActionChange(commandSender, "ParkourLevel increase reward", courseName, parkourLevelIncrease);
	}

	/**
	 * Set the Course's RewardOnce status.
	 * Set whether the Player only gets the prize for the first completion of the Course.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 */
	public void setRewardOnceStatus(final CommandSender commandSender, final String courseName, boolean value) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setRewardOnce(value);
		notifyActionChange(commandSender, "Reward Once Status", courseName, String.valueOf(value));
	}

	/**
	 * Set the Parkoins Reward for the Course.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @param reward amount to reward
	 */
	public void setRewardParkoins(final CommandSender commandSender, final String courseName, final String reward) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!ValidationUtils.isPositiveDouble(reward)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		CourseConfig.getConfig(courseName).setRewardParkoins(Double.parseDouble(reward));
		notifyActionChange(commandSender, "Parkoins reward", courseName, reward);
	}

	/**
	 * Set Course Start Location.
	 * Overwrite the Starting location of a Course to the Player's position.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 */
	public void setStartLocation(final CommandSender commandSender, final String courseName) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (commandSender instanceof Player) {
			CourseConfig.getConfig(courseName).createCheckpointData(((Player) commandSender).getLocation(), 0);
			notifyActionChange(commandSender, "Start Location", courseName, "your position");
		} else {
			TranslationUtils.sendMessage(commandSender, "This command is limited to Players.");
		}
	}

	public void setCourseMessage(CommandSender commandSender, String courseName, String eventTypeName, String message) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!SetCourseConversation.PARKOUR_EVENT_TYPE_NAMES.contains(eventTypeName.toLowerCase())) {
			TranslationUtils.sendInvalidSyntax(commandSender, "setcourse",
					"(courseName) message (" + ParkourEventType.getAllParkourEventTypes() + ") (value)");
		}

		ParkourEventType eventType = ParkourEventType.valueOf(eventTypeName.toUpperCase());
		CourseConfig.getConfig(courseName).setEventMessage(eventType, message);
		notifyActionChange(commandSender,
				StringUtils.standardizeText(eventTypeName) + " Message", courseName, StringUtils.colour(message));
	}

	public void setCourseCommand(CommandSender commandSender, String courseName, String eventTypeName, String message) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		if (!SetCourseConversation.PARKOUR_EVENT_TYPE_NAMES.contains(eventTypeName.toLowerCase())) {
			TranslationUtils.sendInvalidSyntax(commandSender, "setcourse",
					"(courseName) command (" + ParkourEventType.getAllParkourEventTypes() + ") (value)");
		}

		ParkourEventType eventType = ParkourEventType.valueOf(eventTypeName.toUpperCase());
		CourseConfig.getConfig(courseName).addEventCommand(eventType, message);
		notifyActionChange(commandSender,
				StringUtils.standardizeText(eventTypeName) + " Command", courseName, "/" + message);
	}

	/**
	 * Add a Join Item to the Course.
	 * A Material and the Amount can be provided to add the ItemStack(s) to the Player's inventory on Course join.
	 * An optional Material label can be provided to display.
	 * An optional 'unbreakable' flag can be provided to be applied to the Item.
	 *
	 * @param commandSender command sender
	 * @param args command arguments
	 */
	public void addJoinItem(final CommandSender commandSender, final String... args) {
		if (!doesCourseExist(args[1])) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, args[1], commandSender);
			return;
		}

		Material material = MaterialUtils.lookupMaterial(args[2].toUpperCase());
		if (material == null) {
			TranslationUtils.sendValueTranslation("Error.UnknownMaterial", args[2].toUpperCase(), commandSender);
			return;
		}

		if (!ValidationUtils.isPositiveInteger(args[3])) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		int amount = MaterialUtils.parseItemStackAmount(args[3]);
		String label = args.length >= 5 ? args[4] : StringUtils.standardizeText(material.name());
		boolean unbreakable = args.length == 6 && Boolean.parseBoolean(args[5]);

		CourseConfig.getConfig(args[1]).addJoinItem(material, amount, label, unbreakable);
		notifyActionChange(commandSender, "Add Join Item", args[1], material.name() + " x" + amount);
	}

	public void startCoursePrizeConversation(CommandSender commandSender, String courseName) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		new CoursePrizeConversation((Conversable) commandSender).withCourseName(courseName).begin();
	}

	public void startParkourModeConversation(CommandSender commandSender, String courseName) {
		if (!doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return;
		}

		new ParkourModeConversation((Conversable) commandSender).withCourseName(courseName).begin();
	}

	private void populateCourseSettingActions() {
		courseSettingActions.put("challengeonly", (commandSender, courseName, value) -> setChallengeOnlyStatus(commandSender, courseName, Boolean.parseBoolean(value)));
		courseSettingActions.put("creator", this::setCreator);
		courseSettingActions.put("displayname", this::setDisplayName);
		courseSettingActions.put("linkedcourse", this::setCourseToCourseLink);
		courseSettingActions.put("linkedlobby", this::setCourseToLobbyLink);
		courseSettingActions.put("manualcheckpoints", (commandSender, courseName, value) -> setManualCheckpoints(commandSender, courseName, Boolean.parseBoolean(value)));
		courseSettingActions.put("maxdeath", this::setMaxDeaths);
		courseSettingActions.put("maxfallticks", this::setMaxFallTicks);
		courseSettingActions.put("maxtime", this::setMaxTime);
		courseSettingActions.put("minlevel", this::setMinimumParkourLevel);
		courseSettingActions.put("parkourkit", this::setParkourKit);
		courseSettingActions.put("parkourmode", (commandSender, courseName, value) -> startParkourModeConversation(commandSender, courseName));
		courseSettingActions.put("playerlimit", this::setPlayerLimit);
		courseSettingActions.put("prize", (commandSender, courseName, value) -> startCoursePrizeConversation(commandSender, courseName));
		courseSettingActions.put("ready", (commandSender, courseName, value) -> setReadyStatus(commandSender, courseName, Boolean.parseBoolean(value)));
		courseSettingActions.put("rename", this::setRenameCourse);
		courseSettingActions.put("resetlink", (commandSender, courseName, value) -> resetCourseLinks(commandSender, courseName));
		courseSettingActions.put("resumable", (commandSender, courseName, value) -> setResumable(commandSender, courseName, Boolean.parseBoolean(value)));
		courseSettingActions.put("rewarddelay", this::setRewardDelay);
		courseSettingActions.put("rewardlevel", this::setRewardParkourLevel);
		courseSettingActions.put("rewardleveladd", this::setRewardParkourLevelIncrease);
		courseSettingActions.put("rewardonce", (commandSender, courseName, value) -> setRewardOnceStatus(commandSender, courseName, Boolean.parseBoolean(value)));
		courseSettingActions.put("rewardparkoins", this::setRewardParkoins);
		courseSettingActions.put("start", ((commandSender, courseName, value) -> setStartLocation(commandSender, courseName)));
	}
}

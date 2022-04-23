package io.github.a5h73y.parkour.type.admin;

import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_UNKNOWN_PLAYER;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdministrationManager extends AbstractPluginReceiver {

	public AdministrationManager(final Parkour parkour) {
		super(parkour);
	}

	/**
	 * Delete Command.
	 * Possible choices include Course, Checkpoint, Lobby, ParkourKit.
	 * Each option will create a Question for the Sender to confirm.
	 *
	 * @param commandSender command sender
	 * @param command command choice
	 * @param argument argument value
	 */
	public void processDeleteCommand(@NotNull CommandSender commandSender,
	                                 @NotNull String command,
	                                 @NotNull String argument) {
		switch (command.toLowerCase()) {
			case "course":
				if (!canDeleteCourse(commandSender, argument)) {
					return;
				}

				parkour.getQuestionManager().askDeleteCourseQuestion(commandSender, argument);
				break;

			case "checkpoint":
				if (!canDeleteCheckpoint(commandSender, argument)) {
					return;
				}

				int checkpoints = parkour.getConfigManager().getCourseConfig(argument).getCheckpointAmount();
				parkour.getQuestionManager().askDeleteCheckpointQuestion(commandSender, argument, checkpoints);
				break;

			case "lobby":
				if (!canDeleteLobby(commandSender, argument)) {
					return;
				}

				parkour.getQuestionManager().askDeleteLobbyQuestion(commandSender, argument);
				break;

			case "kit":
			case "parkourkit":
				if (!canDeleteParkourKit(commandSender, argument)) {
					return;
				}

				parkour.getQuestionManager().askDeleteKitQuestion(commandSender, argument);
				break;

			case "rank":
			case "parkourrank":
				if (!canDeleteParkourRank(commandSender, argument)) {
					return;
				}

				parkour.getQuestionManager().askDeleteParkourRank(commandSender, argument);
				break;

			default:
				parkour.getParkourCommands().sendInvalidSyntax(commandSender, "delete");
				break;
		}
	}

	/**
	 * Reset Command.
	 * Possible choices include Course, Player, Leaderboard, Prize.
	 * Each option will create a Question for the Sender to confirm.
	 *
	 * @param commandSender command sender
	 * @param command command choice
	 * @param argument argument value
	 * @param extraArgument extra argument value
	 */
	public void processResetCommand(@NotNull CommandSender commandSender,
	                                @NotNull String command,
	                                @NotNull String argument,
	                                @Nullable String extraArgument) {
		switch (command.toLowerCase()) {
			case "course":
				if (!parkour.getCourseManager().doesCourseExist(argument)) {
					TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, argument, commandSender);
					return;
				}

				parkour.getQuestionManager().askResetCourseQuestion(commandSender, argument);
				break;

			case "player":
				OfflinePlayer targetPlayer;

				if (ValidationUtils.isUuidFormat(argument)) {
					targetPlayer = Bukkit.getOfflinePlayer(UUID.fromString(argument));
				} else {
					targetPlayer = Bukkit.getOfflinePlayer(argument);
				}

				if (!PlayerConfig.hasPlayerConfig(targetPlayer)) {
					TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, commandSender);
					return;
				}

				parkour.getQuestionManager().askResetPlayerQuestion(commandSender, argument);
				break;

			case "leaderboard":
				if (!parkour.getCourseManager().doesCourseExist(argument)) {
					TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, argument, commandSender);
					return;
				}

				if (extraArgument != null) {
					parkour.getQuestionManager().askResetPlayerLeaderboardQuestion(commandSender, argument, extraArgument);
				} else {
					parkour.getQuestionManager().askResetLeaderboardQuestion(commandSender, argument);
				}
				break;

			case "prize":
				if (!parkour.getCourseManager().doesCourseExist(argument)) {
					TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, argument, commandSender);
					return;
				}

				parkour.getQuestionManager().askResetPrizeQuestion(commandSender, argument);
				break;

			default:
				parkour.getParkourCommands().sendInvalidSyntax(commandSender, "reset");
				break;
		}
	}

	/**
	 * Cache Command.
	 * View the number of results in each cache.
	 * Provide an argument to clear the selected cache.
	 *
	 * @param commandSender command sender
	 * @param argument argument value
	 */
	public void processCacheCommand(@NotNull CommandSender commandSender,
	                                @Nullable String argument) {
		if (argument != null) {
			switch (argument.toLowerCase()) {
				case "course":
				case "courses":
					parkour.getCourseManager().clearCache();
					break;
				case "database":
					parkour.getDatabaseManager().clearCache();
					break;
				case "lobby":
				case "lobbies":
					parkour.getLobbyManager().clearCache();
					break;
				case "parkourkit":
				case "parkourkits":
					parkour.getParkourKitManager().clearCache();
					break;
				case "sound":
				case "sounds":
					parkour.getSoundsManager().clearCache();
					break;
				case "all":
				case "clear":
					clearAllCache();
					break;
				default:
					parkour.getParkourCommands().sendInvalidSyntax(commandSender, "cache");
					return;
			}
			TranslationUtils.sendPropertySet(commandSender, "Cache", StringUtils.standardizeText(argument), "empty");

		} else {
			TranslationUtils.sendHeading("Parkour Cache", commandSender);
			TranslationUtils.sendValue(commandSender, "Courses Cached", parkour.getCourseManager().getCacheSize());
			TranslationUtils.sendValue(commandSender, "Database Times Cached", parkour.getDatabaseManager().getCacheSize());
			TranslationUtils.sendValue(commandSender, "Lobbies Cached", parkour.getLobbyManager().getCacheSize());
			TranslationUtils.sendValue(commandSender, "ParkourKits Cached", parkour.getParkourKitManager().getCacheSize());
			TranslationUtils.sendValue(commandSender, "Sounds Cached", parkour.getSoundsManager().getCacheSize());
		}
	}

	public void processAdminCommand(@NotNull CommandSender commandSender,
	                                @NotNull String command,
	                                @NotNull String argument) {

		switch (command.toLowerCase()) {
			case "addwhitelist":
				addCommandToWhitelist(commandSender, argument);
				break;

			case "removewhitelist":
				removeCommandFromWhitelist(commandSender, argument);
				break;

			case "disablecommand":
				disableParkourCommand(commandSender, argument);
				break;

			case "enablecommand":
				enableParkourCommand(commandSender, argument);
				break;

			default:
				parkour.getParkourCommands().sendInvalidSyntax(commandSender, "admin");
				break;
		}
	}

	/**
	 * Clear all the Caches.
	 */
	public void clearAllCache() {
		parkour.getCourseManager().clearCache();
		parkour.getDatabaseManager().clearCache();
		parkour.getLobbyManager().clearCache();
		parkour.getParkourKitManager().clearCache();
		parkour.getSoundsManager().clearCache();
	}

	public void addCommandToWhitelist(@Nullable CommandSender commandSender, @Nullable String command) {
		List<String> whitelistedCommands = parkour.getConfigManager().getDefaultConfig().getWhitelistedCommands();
		if (command != null && whitelistedCommands.contains(command.toLowerCase())) {
			TranslationUtils.sendMessage(commandSender, "This command is already whitelisted!");

		} else {
			parkour.getConfigManager().getDefaultConfig().addWhitelistCommand(command);
			TranslationUtils.sendMessage(commandSender, "Command &b" + command + "&f added to the whitelisted commands!");
		}
	}

	public void removeCommandFromWhitelist(@Nullable CommandSender commandSender, @Nullable String command) {
		List<String> whitelistedCommands = parkour.getConfigManager().getDefaultConfig().getWhitelistedCommands();
		if (command != null && !whitelistedCommands.contains(command.toLowerCase())) {
			TranslationUtils.sendMessage(commandSender, "This command is not whitelisted!");

		} else {
			parkour.getConfigManager().getDefaultConfig().removeWhitelistCommand(command);
			TranslationUtils.sendMessage(commandSender, "Command &b" + command + "&f removed from the whitelisted commands!");
		}
	}

	public void disableParkourCommand(@Nullable CommandSender commandSender, @NotNull String command) {
		parkour.getConfigManager().getDefaultConfig().addDisabledParkourCommand(command);
		TranslationUtils.sendMessage(commandSender, "Parkour Command &b" + command + "&f has been disabled!");
	}

	public void enableParkourCommand(@Nullable CommandSender commandSender, @NotNull String command) {
		parkour.getConfigManager().getDefaultConfig().removeDisabledParkourCommand(command);
		TranslationUtils.sendMessage(commandSender, "Parkour Command &b" + command + "&f has been enabled!");
	}

	/**
	 * Validate Sender deleting a Course.
	 *
	 * @param commandSender command sender
	 * @param courseName course name
	 * @return command sender can delete course
	 */
	public boolean canDeleteCourse(CommandSender commandSender, String courseName) {
		if (!parkour.getCourseManager().doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return false;
		}

		courseName = courseName.toLowerCase();
		List<String> dependentCourses = new ArrayList<>();

		for (String course : parkour.getCourseManager().getCourseNames()) {
			String linkedCourse = parkour.getConfigManager().getCourseConfig(courseName).getLinkedCourse();

			if (courseName.equals(linkedCourse)) {
				dependentCourses.add(course);
			}
		}

		if (!dependentCourses.isEmpty()) {
			TranslationUtils.sendMessage(commandSender,
					"This Course can not be deleted as there are other dependent Courses: " + dependentCourses);
			return false;
		}

		return true;
	}

	/**
	 * Validate Sender deleting a Checkpoint.
	 * @param commandSender command sender
	 * @param courseName course name
	 * @return command sender can delete checkpoint
	 */
	public boolean canDeleteCheckpoint(CommandSender commandSender, String courseName) {
		if (!parkour.getCourseManager().doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, commandSender);
			return false;
		}

		int checkpoints = parkour.getConfigManager().getCourseConfig(courseName).getCheckpointAmount();
		// if it has no checkpoints
		if (checkpoints <= 0) {
			TranslationUtils.sendMessage(commandSender, courseName + " has no Checkpoints!");
			return false;
		}
		return true;
	}

	/**
	 * Validate Sender deleting a Lobby.
	 *
	 * @param commandSender command sender
	 * @param lobbyName lobby name
	 * @return command sender can delete lobby
	 */
	public boolean canDeleteLobby(CommandSender commandSender, String lobbyName) {
		if (!Parkour.getLobbyConfig().doesLobbyExist(lobbyName)) {
			TranslationUtils.sendValueTranslation("Error.UnknownLobby", lobbyName, commandSender);
			return false;
		}

		lobbyName = lobbyName.toLowerCase();
		List<String> dependentCourses = new ArrayList<>();

		for (String course : parkour.getCourseManager().getCourseNames()) {
			String linkedLobby = parkour.getConfigManager().getCourseConfig(course).getLinkedLobby();

			if (lobbyName.equals(linkedLobby)) {
				dependentCourses.add(course);
			}
		}

		if (!dependentCourses.isEmpty()) {
			TranslationUtils.sendMessage(commandSender,
					"This Lobby can not be deleted as there are dependent Courses: " + dependentCourses);
			return false;
		}

		return true;
	}

	/**
	 * Validate Sender deleting a ParkourKit.
	 *
	 * @param commandSender command sender
	 * @param parkourKit kit name
	 * @return command sender can delete parkour kit
	 */
	public boolean canDeleteParkourKit(CommandSender commandSender, String parkourKit) {
		if (!Parkour.getParkourKitConfig().doesParkourKitExist(parkourKit)) {
			TranslationUtils.sendTranslation("Error.UnknownParkourKit", commandSender);
			return false;
		}

		parkourKit = parkourKit.toLowerCase();
		List<String> dependentCourses = Parkour.getParkourKitConfig().getDependentCourses(parkourKit);

		if (!dependentCourses.isEmpty()) {
			TranslationUtils.sendMessage(commandSender,
					"This ParkourKit can not be deleted as there are dependent Courses: " + dependentCourses);
			return false;
		}

		return true;
	}

	/**
	 * Validate Sender deleting a ParkourRank from ParkourLevel.
	 * @param commandSender command sender
	 * @param parkourLevel parkour level value
	 * @return command sender can delete parkour rank
	 */
	public boolean canDeleteParkourRank(CommandSender commandSender, String parkourLevel) {
		if (parkourLevel == null || !ValidationUtils.isPositiveInteger(parkourLevel)) {
			TranslationUtils.sendMessage(commandSender, "Invalid ParkourLevel provided.");
			return false;
		}

		if (!parkour.getParkourRankManager().parkourRankExists(Integer.parseInt(parkourLevel))) {
			TranslationUtils.sendMessage(commandSender, "ParkourRank not found for provided ParkourLevel.");
			return false;
		}

		return true;
	}
}

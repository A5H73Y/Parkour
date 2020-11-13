package io.github.a5h73y.parkour.manager;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.QuestionType;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * Question Manager.
 * Manage the questions that require confirmation from the player
 * Usually caused by actions that could change the outcome of the plugin / course
 */
public class QuestionManager extends AbstractPluginReceiver {

    private final Map<CommandSender, Question> questionMap = new WeakHashMap<>();

    public QuestionManager(Parkour parkour) {
        super(parkour);
    }

    public boolean hasPlayerBeenAskedQuestion(CommandSender sender) {
        return questionMap.containsKey(sender);
    }

    public void askResetPlayerQuestion(CommandSender sender, String targetName) {
        sender.sendMessage(Parkour.getPrefix() + "You are about to reset player " + ChatColor.AQUA + targetName + ChatColor.WHITE + "...");
        sender.sendMessage(ChatColor.GRAY + "Resetting a player will delete all their times across all courses and delete all various Parkour attributes.");
        askQuestion(sender, targetName, QuestionType.RESET_PLAYER);
    }

    public void askResetCourseQuestion(CommandSender sender, String courseName) {
        courseName = courseName.toLowerCase();
        sender.sendMessage(Parkour.getPrefix() + "You are about to reset " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        sender.sendMessage(ChatColor.GRAY + "Resetting a course will delete all the statistics stored, which includes leaderboards and various Parkour attributes. This will NOT affect the spawn or checkpoints.");
        askQuestion(sender, courseName, QuestionType.RESET_COURSE);
    }

    public void askResetLeaderboardQuestion(CommandSender sender, String courseName) {
        courseName = courseName.toLowerCase();
        sender.sendMessage(Parkour.getPrefix() + "You are about to reset " + ChatColor.AQUA + courseName + ChatColor.WHITE + " leaderboards...");
        sender.sendMessage(ChatColor.GRAY + "Resetting the leaderboards will remove all times from the database for this course. This will NOT affect the course in any other way.");
        askQuestion(sender, courseName, QuestionType.RESET_LEADERBOARD);
    }

    public void askResetPlayerLeaderboardQuestion(CommandSender sender, String courseName, String targetPlayerName) {
        courseName = courseName.toLowerCase();
        sender.sendMessage(Parkour.getPrefix() + "You are about to reset " + ChatColor.AQUA + targetPlayerName + ChatColor.WHITE + " leaderboards on course " + ChatColor.AQUA + courseName + "...");
        sender.sendMessage(ChatColor.GRAY + "Resetting the player's leaderboards will remove all times they have from the database for this course. This will NOT affect the player or course in any other way.");
        askQuestion(sender, targetPlayerName+ ";" + courseName, QuestionType.RESET_PLAYER_LEADERBOARD);
    }

    public void askResetPrizeQuestion(CommandSender sender, String courseName) {
        courseName = courseName.toLowerCase();
        sender.sendMessage(Parkour.getPrefix() + "You are about to reset the prizes for " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        sender.sendMessage(ChatColor.GRAY + "Resetting the prizes for this course will set the prize to the default prize found in the main configuration file.");
        askQuestion(sender, courseName, QuestionType.RESET_PRIZES);
    }

    public void askDeleteCourseQuestion(CommandSender sender, String courseName) {
        courseName = courseName.toLowerCase();
        sender.sendMessage(Parkour.getPrefix() + "You are about to delete course " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        sender.sendMessage(ChatColor.GRAY + "This will remove all information about the course ever existing, which includes all leaderboard data, course statistics and everything else the plugin knows about it.");
        askQuestion(sender, courseName, QuestionType.DELETE_COURSE);
    }

    public void askDeleteCheckpointQuestion(CommandSender sender, String courseName, int checkpoint) {
        courseName = courseName.toLowerCase();
        sender.sendMessage(Parkour.getPrefix() + "You are about to delete checkpoint " + ChatColor.AQUA + checkpoint + ChatColor.WHITE + " for course " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        sender.sendMessage(ChatColor.GRAY + "Deleting a checkpoint will impact everybody that is currently playing on " + courseName + ". You should not set a course to ready and then continue to make changes.");
        askQuestion(sender, courseName, QuestionType.DELETE_CHECKPOINT);
    }

    public void askDeleteLobbyQuestion(CommandSender sender, String lobbyName) {
        sender.sendMessage(Parkour.getPrefix() + "You are about to delete lobby " + ChatColor.AQUA + lobbyName + ChatColor.WHITE + "...");
        sender.sendMessage(ChatColor.GRAY + "Deleting a lobby will remove all information about it from the server.");
        askQuestion(sender, lobbyName, QuestionType.DELETE_LOBBY);
    }

    public void askDeleteKitQuestion(CommandSender sender, String kitName) {
        sender.sendMessage(Parkour.getPrefix() + "You are about to delete ParkourKit " + ChatColor.AQUA + kitName + ChatColor.WHITE + "...");
        sender.sendMessage(ChatColor.GRAY + "Deleting a ParkourKit will remove all information about it from the server.");
        askQuestion(sender, kitName, QuestionType.DELETE_KIT);
    }

    public void askDeleteAutoStartQuestion(CommandSender sender, String coordinates) {
        sender.sendMessage(Parkour.getPrefix() + "You are about to delete the autostart at this location...");
        sender.sendMessage(ChatColor.GRAY + "Deleting an autostart will remove all information about it from the server.");
        askQuestion(sender, coordinates, QuestionType.DELETE_AUTOSTART);
    }

    public void answerQuestion(CommandSender sender, String argument) {
        if (argument.equalsIgnoreCase("yes")) {
            Question question = questionMap.get(sender);
            question.confirm(sender, question.getType(), question.getArgument());
            removeQuestion(sender);

        } else if (argument.equalsIgnoreCase("no")) {
            sender.sendMessage(Parkour.getPrefix() + "Question cancelled!");
            removeQuestion(sender);

        } else {
            TranslationUtils.sendTranslation("Error.InvalidQuestionAnswer", sender);
            TranslationUtils.sendTranslation("Error.QuestionAnswerChoices", false, sender);
        }
    }

    public void removeQuestion(CommandSender sender) {
        questionMap.remove(sender);
    }

    private void askQuestion(CommandSender sender, String argument, QuestionType type) {
        TranslationUtils.sendTranslation("Parkour.Question", false, sender);
        questionMap.put(sender, new Question(type, argument));
    }

    private class Question {

        private final QuestionType type;
        private final String argument;

        public Question(QuestionType type, String argument) {
            this.type = type;
            this.argument = argument;
        }

        public QuestionType getType() {
            return type;
        }

        public String getArgument() {
            return argument;
        }

        private void confirm(CommandSender sender, QuestionType type, String argument) {
            switch (type) {
                case DELETE_COURSE:
                    parkour.getCourseManager().deleteCourse(sender, argument);
                    PluginUtils.logToFile(argument + " was deleted by " + sender.getName());
                    return;

                case DELETE_CHECKPOINT:
                    parkour.getCheckpointManager().deleteCheckpoint(sender, argument);
                    PluginUtils.logToFile(argument + "'s checkpoint " + CourseInfo.getCheckpointAmount(argument) + " was deleted by " + sender.getName());
                    return;

                case DELETE_LOBBY:
                    parkour.getLobbyManager().deleteLobby(sender, argument);
                    PluginUtils.logToFile("lobby " + argument + " was deleted by " + sender.getName());
                    return;

                case DELETE_KIT:
                    parkour.getParkourKitManager().deleteKit(argument);
                    TranslationUtils.sendValueTranslation("Parkour.Delete", argument + " ParkourKit", sender);
                    PluginUtils.logToFile("ParkourKit " + argument + " was deleted by " + sender.getName());
                    return;

                case DELETE_AUTOSTART:
                    parkour.getCourseManager().deleteAutoStart(argument);
                    TranslationUtils.sendValueTranslation("Parkour.Delete", "AutoStart", sender);
                    PluginUtils.logToFile("AutoStart at " + argument + " was deleted by " + sender.getName());
                    return;

                case RESET_COURSE:
                    parkour.getCourseManager().resetCourse(argument);
                    TranslationUtils.sendValueTranslation("Parkour.Reset", argument, sender);
                    PluginUtils.logToFile(argument + " was reset by " + sender.getName());
                    return;

                case RESET_PLAYER:
                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(argument);

                    if (!PlayerInfo.hasPlayerInfo(targetPlayer) || !targetPlayer.hasPlayedBefore()) {
                        TranslationUtils.sendTranslation("Error.UnknownPlayer", sender);
                        return;
                    }

                    PlayerInfo.resetPlayer(targetPlayer);
                    parkour.getPlayerManager().deleteParkourSession(targetPlayer);
                    TranslationUtils.sendValueTranslation("Parkour.Reset", argument, sender);
                    PluginUtils.logToFile("player " + argument + " was reset by " + sender.getName());
                    return;

                case RESET_LEADERBOARD:
                    parkour.getDatabase().deleteCourseTimes(argument);
                    TranslationUtils.sendValueTranslation("Parkour.Reset", argument + " Leaderboards", sender);
                    PluginUtils.logToFile(argument + " leaderboards were reset by " + sender.getName());
                    return;

                case RESET_PLAYER_LEADERBOARD:
                    String[] arguments = argument.split(";");
                    targetPlayer = Bukkit.getOfflinePlayer(arguments[0]);

                    if (!PlayerInfo.hasPlayerInfo(targetPlayer) || !targetPlayer.hasPlayedBefore()) {
                        TranslationUtils.sendTranslation("Error.UnknownPlayer", sender);
                        return;
                    }

                    parkour.getDatabase().deletePlayerCourseTimes(targetPlayer, arguments[1]);
                    TranslationUtils.sendValueTranslation("Parkour.Reset", arguments[0] + " leaderboards for " + arguments[1], sender);
                    PluginUtils.logToFile(arguments[0] + " leaderboards were reset on course + " + arguments[1] + " by " + sender.getName());
                    return;

                case RESET_PRIZES:
                    CourseInfo.resetPrizes(argument);
                    TranslationUtils.sendValueTranslation("Parkour.Reset", argument + " Prizes", sender);
                    PluginUtils.logToFile(argument + " prizes were reset by " + sender.getName());
                    return;
            }
        }
    }
}

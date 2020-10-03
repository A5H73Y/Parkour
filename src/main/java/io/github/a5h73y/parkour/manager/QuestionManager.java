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
import org.bukkit.entity.Player;

/**
 * Question Manager.
 * Manage the questions that require confirmation from the player
 * Usually caused by actions that could change the outcome of the plugin / course
 */
public class QuestionManager extends AbstractPluginReceiver {

    private final Map<Player, Question> questionMap = new WeakHashMap<>();

    public QuestionManager(Parkour parkour) {
        super(parkour);
    }

    public boolean hasPlayerBeenAskedQuestion(Player player) {
        return questionMap.containsKey(player);
    }

    public void askResetPlayerQuestion(Player player, String targetName) {
        player.sendMessage(Parkour.getPrefix() + "You are about to reset player " + ChatColor.AQUA + targetName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Resetting a player will delete all their times across all courses and delete all various Parkour attributes.");
        askQuestion(player, targetName, QuestionType.RESET_PLAYER);
    }

    public void askResetCourseQuestion(Player player, String courseName) {
        courseName = courseName.toLowerCase();
        player.sendMessage(Parkour.getPrefix() + "You are about to reset " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Resetting a course will delete all the statistics stored, which includes leaderboards and various Parkour attributes. This will NOT affect the spawn or checkpoints.");
        askQuestion(player, courseName, QuestionType.RESET_COURSE);
    }

    public void askResetLeaderboardQuestion(Player player, String courseName) {
        courseName = courseName.toLowerCase();
        player.sendMessage(Parkour.getPrefix() + "You are about to reset " + ChatColor.AQUA + courseName + ChatColor.WHITE + " leaderboards...");
        player.sendMessage(ChatColor.GRAY + "Resetting the leaderboards will remove all times from the database for this course. This will NOT affect the course in any other way.");
        askQuestion(player, courseName, QuestionType.RESET_LEADERBOARD);
    }

    public void askResetPlayerLeaderboardQuestion(Player player, String courseName, String targetPlayerName) {
        courseName = courseName.toLowerCase();
        player.sendMessage(Parkour.getPrefix() + "You are about to reset " + ChatColor.AQUA + targetPlayerName + ChatColor.WHITE + " leaderboards on course " + ChatColor.AQUA + "...");
        player.sendMessage(ChatColor.GRAY + "Resetting the player's leaderboards will remove all times they have from the database for this course. This will NOT affect the player or course in any other way.");
        askQuestion(player, targetPlayerName+ ";" + courseName, QuestionType.RESET_PLAYER_LEADERBOARD);
    }

    public void askResetPrizeQuestion(Player player, String courseName) {
        courseName = courseName.toLowerCase();
        player.sendMessage(Parkour.getPrefix() + "You are about to reset the prizes for " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Resetting the prizes for this course will set the prize to the default prize found in the main configuration file.");
        askQuestion(player, courseName, QuestionType.RESET_PRIZES);
    }

    public void askDeleteCourseQuestion(Player player, String courseName) {
        courseName = courseName.toLowerCase();
        player.sendMessage(Parkour.getPrefix() + "You are about to delete course " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "This will remove all information about the course ever existing, which includes all leaderboard data, course statistics and everything else the plugin knows about it.");
        askQuestion(player, courseName, QuestionType.DELETE_COURSE);
    }

    public void askDeleteCheckpointQuestion(Player player, String courseName, int checkpoint) {
        courseName = courseName.toLowerCase();
        player.sendMessage(Parkour.getPrefix() + "You are about to delete checkpoint " + ChatColor.AQUA + checkpoint + ChatColor.WHITE + " for course " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Deleting a checkpoint will impact everybody that is currently playing on " + courseName + ". You should not set a course to ready and then continue to make changes.");
        askQuestion(player, courseName, QuestionType.DELETE_CHECKPOINT);
    }

    public void askDeleteLobbyQuestion(Player player, String lobbyName) {
        player.sendMessage(Parkour.getPrefix() + "You are about to delete lobby " + ChatColor.AQUA + lobbyName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Deleting a lobby will remove all information about it from the server.");
        askQuestion(player, lobbyName, QuestionType.DELETE_LOBBY);
    }

    public void askDeleteKitQuestion(Player player, String kitName) {
        player.sendMessage(Parkour.getPrefix() + "You are about to delete ParkourKit " + ChatColor.AQUA + kitName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Deleting a ParkourKit will remove all information about it from the server.");
        askQuestion(player, kitName, QuestionType.DELETE_KIT);
    }

    public void askDeleteAutoStartQuestion(Player player, String coordinates) {
        player.sendMessage(Parkour.getPrefix() + "You are about to delete the autostart at this location...");
        player.sendMessage(ChatColor.GRAY + "Deleting an autostart will remove all information about it from the server.");
        askQuestion(player, coordinates, QuestionType.DELETE_AUTOSTART);
    }

    public void answerQuestion(Player player, String argument) {
        if (argument.equalsIgnoreCase("yes")) {
            Question question = questionMap.get(player);
            question.confirm(player, question.getType(), question.getArgument());
            removeQuestion(player);

        } else if (argument.equalsIgnoreCase("no")) {
            player.sendMessage(Parkour.getPrefix() + "Question cancelled!");
            removeQuestion(player);

        } else {
            player.sendMessage(Parkour.getPrefix() + ChatColor.RED + "Invalid question answer.");
            player.sendMessage("Please use either " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " or " + ChatColor.AQUA + "/pa no");
        }
    }

    public void removeQuestion(Player player) {
        questionMap.remove(player);
    }

    private void askQuestion(Player player, String argument, QuestionType type) {
        player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
        questionMap.put(player, new Question(type, argument));
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

        private void confirm(Player player, QuestionType type, String argument) {
            switch (type) {
                case DELETE_COURSE:
                    parkour.getCourseManager().deleteCourse(argument, player);
                    PluginUtils.logToFile(argument + " was deleted by " + player.getName());
                    return;

                case DELETE_CHECKPOINT:
                    parkour.getCheckpointManager().deleteCheckpoint(argument, player);
                    PluginUtils.logToFile(argument + "'s checkpoint " + CourseInfo.getCheckpointAmount(argument) + " was deleted by " + player.getName());
                    return;

                case DELETE_LOBBY:
                    parkour.getLobbyManager().deleteLobby(argument, player);
                    PluginUtils.logToFile("lobby " + argument + " was deleted by " + player.getName());
                    return;

                case DELETE_KIT:
                    parkour.getParkourKitManager().deleteKit(argument);
                    player.sendMessage(Parkour.getPrefix() + "ParkoutKit " + ChatColor.AQUA + argument + ChatColor.WHITE + " deleted...");
                    PluginUtils.logToFile("ParkourKit " + argument + " was deleted by " + player.getName());
                    return;

                case DELETE_AUTOSTART:
                    parkour.getCourseManager().deleteAutoStart(argument, player);
                    player.sendMessage(Parkour.getPrefix() + "AutoStart deleted...");
                    PluginUtils.logToFile("AutoStart at " + argument + " was deleted by " + player.getName());
                    return;

                case RESET_COURSE:
                    parkour.getCourseManager().resetCourse(argument);
                    TranslationUtils.sendValueTranslation("Parkour.Reset", argument, player);
                    PluginUtils.logToFile(argument + " was reset by " + player.getName());
                    return;

                case RESET_PLAYER:
                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(argument);

                    if (!PlayerInfo.hasPlayerInfo(targetPlayer) || !targetPlayer.hasPlayedBefore()) {
                        TranslationUtils.sendTranslation("Error.UnknownPlayer", player);
                        return;
                    }

                    PlayerInfo.resetPlayer(targetPlayer);
                    player.sendMessage(Parkour.getPrefix() + ChatColor.AQUA + argument + ChatColor.WHITE + " has been reset.");
                    PluginUtils.logToFile("player " + argument + " was reset by " + player.getName());
                    return;

                case RESET_LEADERBOARD:
                    parkour.getDatabase().deleteCourseTimes(argument);
                    player.sendMessage(Parkour.getPrefix() + ChatColor.AQUA + argument + ChatColor.WHITE + " leaderboards have been reset.");
                    PluginUtils.logToFile(argument + " leaderboards were reset by " + player.getName());
                    return;

                case RESET_PLAYER_LEADERBOARD:
                    String[] arguments = argument.split(";");
                    targetPlayer = Bukkit.getOfflinePlayer(arguments[0]);

                    if (!PlayerInfo.hasPlayerInfo(targetPlayer) || !targetPlayer.hasPlayedBefore()) {
                        TranslationUtils.sendTranslation("Error.UnknownPlayer", player);
                        return;
                    }

                    parkour.getDatabase().deletePlayerCourseTimes(targetPlayer, arguments[1]);
                    PluginUtils.logToFile(arguments[0] + " leaderboards were reset on course + " + arguments[1] + " by " + player.getName());
                    return;

                case RESET_PRIZES:
                    CourseInfo.resetPrizes(argument);
                    player.sendMessage(Parkour.getPrefix() + ChatColor.AQUA + argument + ChatColor.WHITE + " prizes have been reset.");
                    PluginUtils.logToFile(argument + " prizes were reset by " + player.getName());
                    return;
            }
        }
    }
}

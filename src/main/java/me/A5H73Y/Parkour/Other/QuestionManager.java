package me.A5H73Y.Parkour.Other;

import me.A5H73Y.Parkour.Course.CourseInfo;
import me.A5H73Y.Parkour.Course.LobbyMethods;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CheckpointMethods;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Enums.QuestionType;
import me.A5H73Y.Parkour.Player.PlayerInfo;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Manage the questions that require confirmation from the player
 * Usually caused by actions that could change the outcome of the plugin / course
 */
public class QuestionManager {

    private static QuestionManager instance = null;
    private Map<String, Question> questionMap = new HashMap<>();

    private QuestionManager() {}

    public static QuestionManager getInstance() {
        if (instance == null) {
            instance = new QuestionManager();
        }
        return instance;
    }

    public static boolean hasPlayerBeenAskedQuestion(String playerName) {
        return getInstance().questionMap.containsKey(playerName);
    }

    public static void askResetPlayerQuestion(Player player, String targetName) {
        player.sendMessage(Static.getParkourString() + "You are about to reset player " + ChatColor.AQUA + targetName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Resetting a player will delete all their times across all courses and delete all various Parkour attributes.");
        getInstance().askQuestion(player, targetName, QuestionType.RESET_PLAYER);
    }

    public static void askResetCourseQuestion(Player player, String courseName) {
        courseName = courseName.toLowerCase();
        player.sendMessage(Static.getParkourString() + "You are about to reset " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Resetting a course will delete all the statistics stored, which includes leaderboards and various Parkour attributes. This will NOT affect the spawn or checkpoints.");
        getInstance().askQuestion(player, courseName, QuestionType.RESET_COURSE);
    }

    public static void askResetLeaderboardQuestion(Player player, String courseName) {
        courseName = courseName.toLowerCase();
        player.sendMessage(Static.getParkourString() + "You are about to reset " + ChatColor.AQUA + courseName + ChatColor.WHITE + " leaderboards...");
        player.sendMessage(ChatColor.GRAY + "Resetting the leaderboards will remove all times from the database for this course. This will NOT affect the course in any other way.");
        getInstance().askQuestion(player, courseName, QuestionType.RESET_LEADERBOARD);
    }

    public static void askResetPrizeQuestion(Player player, String courseName) {
        courseName = courseName.toLowerCase();
        player.sendMessage(Static.getParkourString() + "You are about to reset the prizes for " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Resetting the prizes for this course will set the prize to the default prize found in the main configuration file.");
        getInstance().askQuestion(player, courseName, QuestionType.RESET_PRIZES);
    }

    public static void askDeleteCourseQuestion(Player player, String courseName) {
        courseName = courseName.toLowerCase();
        player.sendMessage(Static.getParkourString() + "You are about to delete course " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "This will remove all information about the course ever existing, which includes all leaderboard data, course statistics and everything else the plugin knows about it.");
        getInstance().askQuestion(player, courseName, QuestionType.DELETE_COURSE);
    }

    public static void askDeleteCheckpointQuestion(Player player, String courseName, int checkpoint) {
        courseName = courseName.toLowerCase();
        player.sendMessage(Static.getParkourString() + "You are about to delete checkpoint " + ChatColor.AQUA + checkpoint + ChatColor.WHITE + " for course " + ChatColor.AQUA + courseName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Deleting a checkpoint will impact everybody that is currently playing on " + courseName + ". You should not set a course to finished and then continue to make changes.");
        getInstance().askQuestion(player, courseName, QuestionType.DELETE_CHECKPOINT);
    }

    public static void askDeleteLobbyQuestion(Player player, String lobbyName) {
        player.sendMessage(Static.getParkourString() + "You are about to delete lobby " + ChatColor.AQUA + lobbyName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Deleting a lobby will remove all information about it from the server.");
        getInstance().askQuestion(player, lobbyName, QuestionType.DELETE_LOBBY);
    }

    public static void askDeleteKitQuestion(Player player, String kitName) {
        player.sendMessage(Static.getParkourString() + "You are about to delete ParkourKit " + ChatColor.AQUA + kitName + ChatColor.WHITE + "...");
        player.sendMessage(ChatColor.GRAY + "Deleting a ParkourKit will remove all information about it from the server.");
        getInstance().askQuestion(player, kitName, QuestionType.DELETE_KIT);
    }

    public static void answerQuestion(Player player, String argument) {
        if (argument.equalsIgnoreCase("yes")) {
            Question question = getInstance().questionMap.get(player.getName());
            question.confirm(player, question.getType(), question.getArgument());
            getInstance().questionMap.remove(player.getName());

        } else if (argument.equalsIgnoreCase("no")) {
            player.sendMessage(Static.getParkourString() + "Question cancelled!");
            getInstance().questionMap.remove(player.getName());

        } else {
            player.sendMessage(Static.getParkourString() + ChatColor.RED + "Invalid question answer.");
            player.sendMessage("Please use either " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " or " + ChatColor.AQUA + "/pa no");
        }
    }

    private void askQuestion(Player player, String argument, QuestionType type) {
        player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
        questionMap.put(player.getName(), new Question(type, argument));
    }

    private static class Question {

        private QuestionType type;
        private String argument;

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
                    CourseMethods.deleteCourse(argument, player);
                    Utils.logToFile(argument + " was deleted by " + player.getName());
                    return;

                case DELETE_CHECKPOINT:
                    CheckpointMethods.deleteCheckpoint(argument, player);
                    Utils.logToFile(argument + "'s checkpoint " + CourseInfo.getCheckpointAmount(argument) + " was deleted by " + player.getName());
                    return;

                case DELETE_LOBBY:
                    LobbyMethods.deleteLobby(argument, player);
                    Utils.logToFile("lobby " + argument + " was deleted by " + player.getName());
                    return;

                case DELETE_KIT:
                    ParkourKit.deleteKit(argument);
                    player.sendMessage(Static.getParkourString() + "ParkoutKit " + ChatColor.AQUA + argument + ChatColor.WHITE + " deleted...");
                    Utils.logToFile("ParkourKit " + argument + " was deleted by " + player.getName());
                    return;

                case RESET_COURSE:
                    CourseMethods.resetCourse(argument);
                    player.sendMessage(Utils.getTranslation("Parkour.Reset").replace("%COURSE%", argument));
                    Utils.logToFile(argument + " was reset by " + player.getName());
                    return;

                case RESET_PLAYER:
                    OfflinePlayer target = Bukkit.getOfflinePlayer(argument);

                    if (target != null) {
                        PlayerInfo.resetPlayer(target);
                        player.sendMessage(Static.getParkourString() + ChatColor.AQUA + argument + ChatColor.WHITE + " has been reset.");
                        Utils.logToFile("player " + argument + " was reset by " + player.getName());
                    } else {
                        player.sendMessage(Static.getParkourString() + ChatColor.AQUA + argument + ChatColor.WHITE + " does not exist.");
                    }
                    return;

                case RESET_LEADERBOARD:
                    DatabaseMethods.deleteCourseTimes(argument);
                    player.sendMessage(Static.getParkourString() + ChatColor.AQUA + argument + ChatColor.WHITE + " leaderboards have been reset.");
                    Utils.logToFile(argument + " leaderboards were reset by " + player.getName());
                    return;

                case RESET_PRIZES:
                    CourseInfo.resetPrizes(argument);
                    player.sendMessage(Static.getParkourString() + ChatColor.AQUA + argument + ChatColor.WHITE + " prizes have been reset.");
                    Utils.logToFile(argument + " prizes were reset by " + player.getName());
                    return;
            }
        }
    }
}

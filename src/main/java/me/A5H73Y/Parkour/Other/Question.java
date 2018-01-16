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

public class Question {

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

    public void questionPlayer(Player player, String message) {
        if (message.startsWith("/pa yes") || message.startsWith("/parkour yes") || message.startsWith("/pkr yes")) {
            Question question = Static.getQuestion(player.getName());
            question.confirm(player, question.getType(), question.getArgument());
            Static.removeQuestion(player.getName());

        } else if (message.startsWith("/pa no") || message.startsWith("/parkour no") || message.startsWith("/pkr no")) {
            player.sendMessage(Static.getParkourString() + "Question cancelled!");
            Static.removeQuestion(player.getName());

        } else {
            player.sendMessage(Static.getParkourString() + ChatColor.RED + "Invalid question answer.");
            player.sendMessage("Please use either " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " or " + ChatColor.AQUA + "/pa no");
        }
    }

    private void confirm(Player player, QuestionType type, String argument) {
        switch(type) {

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
                    player.sendMessage(Static.getParkourString() + ChatColor.AQUA + argument + ChatColor.WHITE + " does not exists.");
                }
                return;

            case RESET_LEADERBOARD:
                DatabaseMethods.deleteCourseTimes(argument);
                player.sendMessage(Static.getParkourString() + ChatColor.AQUA + argument + ChatColor.WHITE + " leaderboards have been reset.");
                Utils.logToFile(argument + " leaderboards were reset by " + player.getName());
        }
    }
}

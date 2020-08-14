package io.github.a5h73y.parkour.utility;

import io.github.a5h73y.parkour.database.TimeEntry;
import java.util.List;
import org.bukkit.entity.Player;

@Deprecated
public final class Utils {

    /**
     * Delete command method
     * Possible arguments include Course, Checkpoint, Lobby, Kit and AutoStart
     * This will only add a Question object with the relevant data until the player confirms the action later on.
     *
     * @param args
     * @param player
     */
    public static void deleteCommand(String[] args, Player player) {
//        if (args[1].equalsIgnoreCase("course")) {
//            if (!CourseManager.courseExists(args[2])) {
//                TranslationUtils.sendValueTranslation("Error.NoExist", args[2], player);
//                return;
//            }
//
//            if (!Validation.deleteCourse(args[2], player)) {
//                return;
//            }
//
//            QuestionManager.getInstance().askDeleteCourseQuestion(player, args[2]);
//
//        } else if (args[1].equalsIgnoreCase("checkpoint")) {
//            if (!CourseManager.courseExists(args[2])) {
//                TranslationUtils.sendValueTranslation("Error.NoExist", args[2], player);
//                return;
//            }
//
//            int checkpoints = CourseInfo.getCheckpointAmount(args[2]);
//            // if it has no checkpoints
//            if (checkpoints <= 0) {
//                player.sendMessage(Parkour.getPrefix() + args[2] + " has no checkpoints!");
//                return;
//            }
//
//            QuestionManager.getInstance().askDeleteCheckpointQuestion(player, args[2], checkpoints);
//
//        } else if (args[1].equalsIgnoreCase("lobby")) {
//            if (!Parkour.getDefaultConfig().contains("Lobby." + args[2].toLowerCase() + ".World")) {
//                player.sendMessage(Parkour.getPrefix() + "This lobby does not exist!");
//                return;
//            }
//
//            if (!Validation.deleteLobby(args[2], player)) {
//                return;
//            }
//
//            QuestionManager.getInstance().askDeleteLobbyQuestion(player, args[2]);
//
//        } else if (args[1].equalsIgnoreCase("kit")) {
//            if (!ParkourKitInfo.doesParkourKitExist(args[2])) {
//                player.sendMessage(Parkour.getPrefix() + "This ParkourKit does not exist!");
//                return;
//            }
//
//            if (!Validation.deleteParkourKit(args[2], player)) {
//                return;
//            }
//
//            QuestionManager.getInstance().askDeleteKitQuestion(player, args[2]);
//
//        } else if (args[1].equalsIgnoreCase("autostart")) {
//            Location location = player.getLocation();
//            if (CourseManager.getAutoStartCourse(location) == null) {
//                player.sendMessage(Parkour.getPrefix() + "There is no autostart at this location");
//                return;
//            }
//
//            String coordinates = location.getBlockX() + "-" + location.getBlockY() + "-" + location.getBlockZ();
//            if (!Validation.deleteAutoStart(args[2], coordinates, player)) {
//                return;
//            }
//
//            QuestionManager.getInstance().askDeleteAutoStartQuestion(player, coordinates);
//
//        } else {
//            player.sendMessage(invalidSyntax("delete", "(course / checkpoint / lobby / kit / autostart) (name)"));
//        }
    }

    /**
     * Reset command method
     * Possible arguments include Course, Player and Leaderboard
     * This will only add a Question object with the relevant data until the player confirms the action later on.
     *
     * @param args
     * @param player
     */
    public static void resetCommand(String[] args, Player player) {
//        if (args[1].equalsIgnoreCase("course")) {
//            if (!CourseManager.courseExists(args[2])) {
//                TranslationUtils.sendValueTranslation("Error.NoExist", args[2], player);
//                return;
//            }
//
//            QuestionManager.getInstance().askResetCourseQuestion(player, args[2]);
//
//        } else if (args[1].equalsIgnoreCase("player")) {
//            OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
//            if (target == null || !PlayerInfo.hasPlayerInfo(target)) {
//                player.sendMessage(getTranslation("Error.UnknownPlayer"));
//                return;
//            }
//
//            QuestionManager.getInstance().askResetPlayerQuestion(player, args[2]);
//
//        } else if (args[1].equalsIgnoreCase("leaderboard")) {
//            if (!CourseManager.courseExists(args[2])) {
//                TranslationUtils.sendValueTranslation("Error.NoExist", args[2], player);
//                return;
//            }
//
//            if (args.length > 3) {
//                QuestionManager.getInstance().askResetPlayerLeaderboardQuestion(player, args[2], args[3]);
//            } else {
//                QuestionManager.getInstance().askResetLeaderboardQuestion(player, args[2]);
//            }
//
//        } else if (args[1].equalsIgnoreCase("prize")) {
//            if (!CourseManager.courseExists(args[2])) {
//                TranslationUtils.sendValueTranslation("Error.NoExist", args[2], player);
//                return;
//            }
//
//            QuestionManager.getInstance().askResetPrizeQuestion(player, args[2]);
//
//        } else {
//            player.sendMessage(invalidSyntax("reset", "(course / player / leaderboard / prize) (argument)"));
//        }
    }

    /**
     * Add a whitelisted command
     *
     * @param args
     * @param player
     */
    public static void addWhitelistedCommand(String[] args, Player player) {
//        if (Parkour.getDefaultConfig().getWhitelistedCommands().contains(args[1].toLowerCase())) {
//            player.sendMessage(Parkour.getPrefix() + "This command is already whitelisted!");
//            return;
//        }
//
//        Static.addWhitelistedCommand(args[1].toLowerCase());
//        player.sendMessage(Parkour.getPrefix() + "Command " + ChatColor.AQUA + args[1] + ChatColor.WHITE + " added to the whitelisted commands!");
    }

}

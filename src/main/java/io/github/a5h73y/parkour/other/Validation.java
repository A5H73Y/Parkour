package io.github.a5h73y.parkour.other;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.plugin.EconomyApi;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Validation {

    /**
     * Validate if the argument is numeric
     * "1" = true, "Hi" = false
     *
     * @param input
     * @return whether the input is numeric
     */
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Validate if the argument is numeric
     * "1" = true, "1.0" = true, "Hi" = false
     *
     * @param input
     * @return whether the input is numeric
     */
    public static boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Validate if the argument is numeric
     * "1" = true, "Hi" = false, "-1" = false
     *
     * @param input
     * @return whether the input is numeric
     */
    public static boolean isPositiveInteger(String input) {
        return isInteger(input) && Integer.parseInt(input) >= 0;
    }

    /**
     * Validate if the argument is numeric
     * "1" = true, "Hi" = false, "-1" = false
     *
     * @param input
     * @return whether the input is numeric
     */
    public static boolean isPositiveDouble(String input) {
        return isDouble(input) && Double.parseDouble(input) >= 0;
    }

    /**
     * Validate if the input is a populated String
     *
     * @param input
     * @return whether the input is a valid String
     */
    public static boolean isStringValid(String input) {
        return input != null && input.trim().length() != 0;
    }

    /**
     * Validate course creation
     *
     * @param courseName
     * @param player
     * @return
     */
    public static boolean courseCreation(String courseName, Player player) {
        if (courseName.length() > 15) {
            player.sendMessage(Parkour.getPrefix() + "Course name is too long!");
            return false;

        } else if (courseName.contains(".")) {
            player.sendMessage(Parkour.getPrefix() + "Course name can not contain '.'");
            return false;

        } else if (isInteger(courseName)) {
            player.sendMessage(Parkour.getPrefix() + "Course name can not only be numeric");
            return false;

        } else if (Parkour.getInstance().getCourseManager().courseExists(courseName)) {
            TranslationUtils.sendTranslation("Error.Exist", player);
            return false;
        }

        return true;
    }

    /**
     * Validate player join course
     *
     * @param player
     * @param course
     * @return
     */
    public static boolean courseJoining(Player player, Course course) {

        /* Player in wrong world */
        if (Parkour.getDefaultConfig().isJoinEnforceWorld()) {
            if (!player.getLocation().getWorld().getName().equals(course.getCheckpoints().get(0).getWorld())) {
                TranslationUtils.sendTranslation("Error.WrongWorld", player);
                return false;
            }
        }

        /* Players level isn't high enough */
        int minimumLevel = CourseInfo.getMinimumLevel(course.getName());

        if (minimumLevel > 0) {
            if (!PermissionUtils.hasPermission(player, Permission.ADMIN_LEVEL_BYPASS, false)
                    && !PermissionUtils.hasSpecificPermission(player, Permission.PARKOUR_LEVEL, String.valueOf(minimumLevel), false)) {
                int currentLevel = PlayerInfo.getParkourLevel(player);

                if (currentLevel < minimumLevel) {
                    TranslationUtils.sendValueTranslation("Error.RequiredLvl", String.valueOf(minimumLevel), player);
                    return false;
                }
            }
        }

        /* Course isn't finished */
        if (!CourseInfo.getFinished(course.getName())) {
            if (Parkour.getInstance().getConfig().getBoolean("OnJoin.EnforceFinished")) {
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player, Permission.ADMIN_FINISH_BYPASS, course.getName())) {
                    TranslationUtils.sendTranslation("Error.Finished1", player);
                    return false;
                }
            } else {
                TranslationUtils.sendTranslation("Error.Finished2", player);
            }
        }

        /* Check if player has enough currency to join */
        if (!Parkour.getInstance().getEconomyApi().validateCourseJoin(player, course.getName())) {
            return false;
        }

        /* Check if the player is allowed to leave the course for another */
        if (Parkour.getInstance().getConfig().getBoolean("OnCourse.PreventJoiningDifferentCourse")) {
            if (Parkour.getInstance().getPlayerManager().isPlaying(player.getName())) {
                TranslationUtils.sendTranslation("Error.JoiningAnotherCourse", player);
                return false;
            }
        }

        return true;
    }

    /**
     * Validate player joining course, without sending messages
     *
     * @param player
     * @param courseName
     * @return
     */
    public static boolean courseJoiningNoMessages(Player player, String courseName) {
        /* Player in wrong world */
        if (Parkour.getDefaultConfig().isJoinEnforceWorld()) {
            if (!player.getLocation().getWorld().getName().equals(CourseInfo.getWorld(courseName))) {
                return false;
            }
        }

        /* Players level isn't high enough */
        int minimumLevel = CourseInfo.getMinimumLevel(courseName);

        if (minimumLevel > 0) {
            if (!PermissionUtils.hasPermission(player, Permission.ADMIN_LEVEL_BYPASS, false)
                    && !PermissionUtils.hasSpecificPermission(player, Permission.PARKOUR_LEVEL, String.valueOf(minimumLevel), false)) {
                int currentLevel = PlayerInfo.getParkourLevel(player);

                if (currentLevel < minimumLevel) {
                    return false;
                }
            }
        }

        /* Course isn't finished */
        if (!CourseInfo.getFinished(courseName)
                && Parkour.getInstance().getConfig().getBoolean("OnJoin.EnforceFinished")
                && !PermissionUtils.hasPermissionOrCourseOwnership(player, Permission.ADMIN_FINISH_BYPASS, courseName)) {
            return false;
        }

        /* Check if player has enough currency to join */
        if (Parkour.getInstance().getEconomyApi().isEnabled()) {
            int joinFee = CourseInfo.getEconomyJoiningFee(courseName);
            return joinFee <= 0 || Parkour.getInstance().getEconomyApi().hasAmount(player, joinFee);
        }

        return true;
    }

    /**
     * Validate challenging a player
     *
     * @param args
     * @param player
     * @return
     */
    public static boolean challengePlayer(String[] args, Player player) {
        String courseName = args[1].toLowerCase();
        String targetPlayerName = args[2];

        if (!Parkour.getInstance().getCourseManager().courseExists(courseName)) {
            TranslationUtils.sendValueTranslation("Error.NoExist", courseName, player);
            return false;
        }
        if (Bukkit.getPlayer(targetPlayerName) == null) {
            player.sendMessage(Parkour.getPrefix() + "This player is not online!");
            return false;
        }
        if (Parkour.getInstance().getPlayerManager().isPlaying(player.getName())) {
            player.sendMessage(Parkour.getPrefix() + "You are already on a course!");
            return false;
        }
        if (Parkour.getInstance().getPlayerManager().isPlaying(targetPlayerName)) {
            player.sendMessage(Parkour.getPrefix() + "This player is already on a course!");
            return false;
        }
        if (player.getName().equalsIgnoreCase(targetPlayerName)) {
            player.sendMessage(Parkour.getPrefix() + "You can't challenge yourself!");
            return false;
        }

        Player target = Bukkit.getPlayer(targetPlayerName);

        if (!courseJoiningNoMessages(player, courseName)) {
            player.sendMessage(Parkour.getPrefix() + "You are not able to join this course!");
            return false;
        }
        if (!courseJoiningNoMessages(target, courseName)) {
            player.sendMessage(Parkour.getPrefix() + "They are not able to join this course!");
            return false;
        }
        if (args.length == 4) {
            String wagerAmount = args[3];
            EconomyApi economyApi = Parkour.getInstance().getEconomyApi();

            if (!economyApi.isEnabled()) {
                player.sendMessage(Parkour.getPrefix() + "Economy is disabled, no wager will be made.");

            } else if (!isPositiveDouble(wagerAmount)) {
                player.sendMessage(Parkour.getPrefix() + "Wager must be a positive number.");
                return false;

            } else if (!economyApi.hasAmount(player, Double.parseDouble(wagerAmount))) {
                player.sendMessage(Parkour.getPrefix() + "You do not have enough funds for this wager.");
                return false;

            } else if (!economyApi.hasAmount(target, Double.parseDouble(wagerAmount))) {
                player.sendMessage(Parkour.getPrefix() + "They do not have enough funds for this wager.");
                return false;
            }
        }

        return true;
    }

    /**
     * Validate joining a lobby
     *
     * @param player
     * @return boolean
     */
    public static boolean lobbyJoiningSet(Player player) {
        if (Parkour.getInstance().getConfig().getBoolean("Lobby.Set")) {
            return true;
        }

        if (PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
            player.sendMessage(Parkour.getPrefix() + ChatColor.RED + "Lobby has not been set!");
            player.sendMessage(StringUtils.colour("Type &b'/pa setlobby'&f where you want the lobby to be set."));

        } else {
            player.sendMessage(Parkour.getPrefix() + ChatColor.RED + "Lobby has not been set! Please tell the Owner!");
        }
        return false;
    }

    /**
     * Validate joining a custom lobby
     *
     * @param player
     * @param lobby
     * @return
     */
    public static boolean lobbyJoiningCustom(Player player, String lobby) {
        if (!Parkour.getInstance().getConfig().contains("Lobby." + lobby + ".World")) {
            player.sendMessage(Parkour.getPrefix() + "Lobby does not exist!");
            return false;
        }

        int level = Parkour.getInstance().getConfig().getInt("Lobby." + lobby + ".Level");

        if (level > 0 && !PermissionUtils.hasPermission(player, Permission.ADMIN_LEVEL_BYPASS, false)) {
            if (PlayerInfo.getParkourLevel(player) < level) {
                TranslationUtils.sendValueTranslation("Error.RequiredLvl", String.valueOf(level), player);
                return false;
            }
        }
        return true;
    }

    /**
     * Validate creating a course checkpoint
     *
     * @param args
     * @param player
     * @return
     */
    public static boolean createCheckpoint(String[] args, Player player) {
        String selected = PlayerInfo.getSelectedCourse(player).toLowerCase();

        if (!Parkour.getInstance().getCourseManager().courseExists(selected)) {
            TranslationUtils.sendValueTranslation("Error.NoExist", selected, player);
            return false;
        }

        int pointcount = CourseInfo.getCheckpointAmount(selected) + 1;

        if (!(args.length <= 1)) {
            if (!isPositiveInteger(args[1])) {
                player.sendMessage(Parkour.getPrefix() + "Checkpoint specified is not numeric!");
                return false;
            }
            if (pointcount < Integer.parseInt(args[1])) {
                player.sendMessage(Parkour.getPrefix() + "This checkpoint does not exist! " + ChatColor.RED + "Creation cancelled.");
                return false;
            }

            pointcount = Integer.parseInt(args[1]);
        }

        if (pointcount < 1) {
            player.sendMessage(Parkour.getPrefix() + "Invalid checkpoint number.");
            return false;
        }
        return true;
    }

    /**
     * Validate a course before deleting it
     *
     * @param courseName
     * @param player
     * @return
     */
    public static boolean deleteCourse(String courseName, Player player) {
        courseName = courseName.toLowerCase();
        List<String> dependentCourses = new ArrayList<>();

        for (String course : CourseInfo.getAllCourses()) {
            String linkedCourse = CourseInfo.getLinkedCourse(courseName);

            if (courseName.equals(linkedCourse)) {
                dependentCourses.add(course);
            }
        }

        if (dependentCourses.size() > 0) {
            player.sendMessage(Parkour.getPrefix() + "This course can not be deleted as there are dependent courses: " + dependentCourses);
            return false;
        }

        return true;
    }

    /**
     * Validate a lobby before deleting it
     *
     * @param courseName
     * @param player
     * @return
     */
    public static boolean deleteLobby(String courseName, Player player) {
        courseName = courseName.toLowerCase();
        List<String> dependentCourses = new ArrayList<>();

        for (String course : CourseInfo.getAllCourses()) {
            String linkedCourse = CourseInfo.getLinkedLobby(courseName);

            if (courseName.equals(linkedCourse)) {
                dependentCourses.add(course);
            }
        }

        if (dependentCourses.size() > 0) {
            player.sendMessage(Parkour.getPrefix() + "This lobby can not be deleted as there are dependent courses: " + dependentCourses);
            return false;
        }

        return true;
    }

    /**
     * Validate a kit before deleting it
     *
     * @param parkourKit
     * @param player
     * @return
     */
    public static boolean deleteParkourKit(String parkourKit, Player player) {
        parkourKit = parkourKit.toLowerCase();
        List<String> dependentCourses = new ArrayList<>();

        for (String course : CourseInfo.getAllCourses()) {
            String linkedKit = CourseInfo.getParkourKit(course);

            if (parkourKit.equals(linkedKit)) {
                dependentCourses.add(course);
            }
        }

        if (dependentCourses.size() > 0) {
            player.sendMessage(Parkour.getPrefix() + "This ParkourKit can not be deleted as there are dependent courses: " + dependentCourses);
            return false;
        }

        return true;
    }

    /**
     * Validate the autostart before deleting it
     *
     * @param courseName
     * @param coordinates
     * @param player
     * @return
     */
    public static boolean deleteAutoStart(String courseName, String coordinates, Player player) {
        courseName = courseName.toLowerCase();
        if (!Parkour.getConfig(ConfigType.COURSES).getString("CourseInfo.AutoStart." + coordinates).equalsIgnoreCase(courseName)) {
            player.sendMessage(Parkour.getPrefix() + "This autostart can not be deleted as it is not linked to course: " + courseName);
            return false;
        }

        return true;
    }

}
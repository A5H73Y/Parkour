package io.github.a5h73y.parkour.other;

import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.plugin.EconomyApi;
import io.github.a5h73y.parkour.type.challenge.Challenge;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Validation for the various Parkour events.
 */
public class ParkourValidation {

    /**
     * Validate the Default Lobby is set.
     *
     * @param player player
     * @return default lobby set
     */
    public static boolean isDefaultLobbySet(Player player) {
        boolean lobbySet = Parkour.getLobbyConfig().doesDefaultLobbyExist();

        if (!lobbySet) {
            if (PermissionUtils.hasPermission(player, Permission.ADMIN_ALL, false)) {
                TranslationUtils.sendMessage(player, "&cDefault Lobby has not been set!");
                TranslationUtils.sendMessage(player, "Type &b'/pa setlobby' &fwhere you want the lobby to be set.");

            } else {
                TranslationUtils.sendMessage(player, "&cDefault Lobby has not been set! Please tell the Owner!");
            }
        } else if (Bukkit.getWorld(Parkour.getLobbyConfig().getLobbyWorld(DEFAULT)) == null) {
            TranslationUtils.sendTranslation("Error.UnknownWorld", player);
            lobbySet = false;
        }
        return lobbySet;
    }

    /**
     * Validate Player joining Lobby.
     *
     * @param player player
     * @param lobbyName lobby name
     * @return player can join lobby
     */
    public static boolean canJoinLobby(Player player, String lobbyName) {
        if (!Parkour.getLobbyConfig().doesLobbyExist(lobbyName)) {
            TranslationUtils.sendValueTranslation("Error.UnknownLobby", lobbyName, player);
            return false;
        }

        if (Bukkit.getWorld(Parkour.getLobbyConfig().getLobbyWorld(lobbyName)) == null) {
            TranslationUtils.sendTranslation("Error.UnknownWorld", player);
            return false;
        }

        if (Parkour.getDefaultConfig().getBoolean("LobbySettings.EnforceWorld")
                && !player.getWorld().getName().equals(Parkour.getLobbyConfig().getLobbyWorld(lobbyName))) {
            TranslationUtils.sendTranslation("Error.WrongWorld", player);
            return false;
        }

        int level = Parkour.getLobbyConfig().getRequiredLevel(lobbyName);

        if (level > 0 && PlayerConfig.getConfig(player).getParkourLevel() < level
                && !PermissionUtils.hasPermission(player, Permission.ADMIN_LEVEL_BYPASS, false)) {
            TranslationUtils.sendValueTranslation("Error.RequiredLvl", String.valueOf(level), player);
            return false;
        }

        return true;
    }

    /**
     * Validate Player joining Lobby Silently.
     *
     * @param player player
     * @param lobbyName lobby name
     * @return player can join lobby
     */
    public static boolean canJoinLobbySilent(Player player, String lobbyName) {
        if (!Parkour.getLobbyConfig().doesLobbyExist(lobbyName)) {
            return false;
        }

        if (Bukkit.getWorld(Parkour.getDefaultConfig().getString("Lobby." + lobbyName + ".World")) == null) {
            return false;
        }

        if (Parkour.getDefaultConfig().getBoolean("LobbySettings.EnforceWorld")
                && !player.getWorld().getName().equals(Parkour.getLobbyConfig().getLobbyWorld(lobbyName))) {
            return false;
        }

        int level = Parkour.getLobbyConfig().getRequiredLevel(lobbyName);

        if (level > 0 && PlayerConfig.getConfig(player).getParkourLevel() < level
                && !PermissionUtils.hasPermission(player, Permission.ADMIN_LEVEL_BYPASS, false)) {
            return false;
        }

        return true;
    }

    /**
     * Validate Player joining Course.
     *
     * @param player player
     * @param course course
     * @return player can join course
     */
    public static boolean canJoinCourse(Player player, Course course) {
        /* World doesn't exist */
        if (course.getCheckpoints().isEmpty()) {
            TranslationUtils.sendTranslation("Error.UnknownWorld", player);
            return false;
        }

        Parkour parkour = Parkour.getInstance();

        /* Player in wrong world */
        if (parkour.getParkourConfig().isJoinEnforceWorld()
                && !player.getLocation().getWorld().getName().equals(course.getCheckpoints().get(0).getWorldName())) {
            TranslationUtils.sendTranslation("Error.WrongWorld", player);
            return false;
        }

        CourseConfig courseConfig = CourseConfig.getConfig(course.getName());
        /* Players level isn't high enough */
        int minimumLevel = courseConfig.getMinimumParkourLevel();

        if (minimumLevel > 0 && !PermissionUtils.hasPermission(player, Permission.ADMIN_LEVEL_BYPASS, false)
                && !PermissionUtils.hasSpecificPermission(
                player, Permission.PARKOUR_LEVEL, String.valueOf(minimumLevel), false)) {
            int currentLevel = PlayerConfig.getConfig(player).getParkourLevel();

            if (currentLevel < minimumLevel) {
                TranslationUtils.sendValueTranslation("Error.RequiredLvl", String.valueOf(minimumLevel), player);
                return false;
            }
        }

        /* Permission system */
        if (Parkour.getDefaultConfig().getBoolean("OnJoin.PerCoursePermission")
                && !PermissionUtils.hasSpecificPermission(player, Permission.PARKOUR_COURSE, course.getName(), true)) {
            return false;
        }

        /* Course isn't ready */
        if (!courseConfig.getReadyStatus()) {
            if (Parkour.getDefaultConfig().getBoolean("OnJoin.EnforceReady")) {
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player, Permission.ADMIN_READY_BYPASS, course.getName())) {
                    TranslationUtils.sendTranslation("Error.NotReady", player);
                    return false;
                }
            } else {
                TranslationUtils.sendTranslation("Error.NotReadyWarning", player);
            }
        }

        /* Check if the player can leave the course for another */
        if (parkour.getPlayerManager().isPlaying(player)) {
            if (Parkour.getDefaultConfig().getBoolean("OnCourse.PreventJoiningDifferentCourse")) {
                TranslationUtils.sendTranslation("Error.JoiningAnotherCourse", player);
                return false;
            }
            if (Parkour.getDefaultConfig().isCourseEnforceWorld()
                    && !player.getLocation().getWorld().getName().equals(course.getCheckpoints().get(0).getWorldName())) {
                TranslationUtils.sendTranslation("Error.WrongWorld", player);
                return false;
            }
        }

        /* Check if player limit exceeded */
        if (courseConfig.hasPlayerLimit()
                && parkour.getPlayerManager().getNumberOfPlayersOnCourse(course.getName())
                >= courseConfig.getPlayerLimit()) {
            TranslationUtils.sendTranslation("Error.LimitExceeded", player);
            return false;
        }

        if (courseConfig.getChallengeOnly() && !parkour.getChallengeManager().hasPlayerBeenChallenged(player)) {
            TranslationUtils.sendTranslation("Error.ChallengeOnly", player);
            return false;
        }

        if (parkour.getChallengeManager().hasPlayerBeenChallenged(player)) {
            Challenge challenge = parkour.getChallengeManager().getChallengeForPlayer(player);

            if (challenge != null && !challenge.getCourseName().equals(course.getName())) {
                TranslationUtils.sendTranslation("Error.OnChallenge", player);
                return false;
            }
        }

        /* Check if player has enough currency to join */
        return parkour.getEconomyApi().validateAndChargeCourseJoin(player, course.getName());
    }

    /**
     * Validate Player joining Course Silently.
     * No messages will be sent to the requesting player, only checks if they could join.
     *
     * @param player player
     * @param courseName course name
     * @return player could join course
     */
    public static boolean canJoinCourseSilent(Player player, String courseName) {
        CourseConfig courseConfig = CourseConfig.getConfig(courseName);

        /* Player in wrong world */
        if (Parkour.getDefaultConfig().isJoinEnforceWorld()
                && !player.getLocation().getWorld().getName().equals(courseConfig.getWorld())) {
            return false;
        }

        /* Players level isn't high enough */
        int minimumLevel = courseConfig.getMinimumParkourLevel();

        if (minimumLevel > 0
                && !PermissionUtils.hasPermission(player, Permission.ADMIN_LEVEL_BYPASS, false)
                && !PermissionUtils.hasSpecificPermission(
                player, Permission.PARKOUR_LEVEL, String.valueOf(minimumLevel), false)) {
            int currentLevel = PlayerConfig.getConfig(player).getParkourLevel();

            if (currentLevel < minimumLevel) {
                return false;
            }
        }

        /* Permission system */
        if (Parkour.getDefaultConfig().getBoolean("OnJoin.PerCoursePermission")
                && !PermissionUtils.hasSpecificPermission(player, Permission.PARKOUR_COURSE, courseName, false)) {
            return false;
        }

        /* Course isn't ready */
        if (!courseConfig.getReadyStatus() && Parkour.getDefaultConfig().getBoolean("OnJoin.EnforceReady")) {
            return false;
        }

        /* Check if player has enough currency to join */
        if (Parkour.getInstance().getEconomyApi().isEnabled()) {
            double joinFee = courseConfig.getEconomyJoiningFee();
            return joinFee <= 0 || Parkour.getInstance().getEconomyApi().hasAmount(player, joinFee);
        }

        return true;
    }

    /**
     * Can Target Player join Challenge.
     *
     * @param player player
     * @param targetPlayer target player
     * @param challenge challenge
     * @return target player can join challenge
     */
    public static boolean canJoinChallenge(Player player, Player targetPlayer, Challenge challenge) {
        if (!canJoinCourseSilent(targetPlayer, challenge.getCourseName())) {
            TranslationUtils.sendMessage(player, "Player is not able to join this Course!");
            return false;
        }

        Parkour parkour = Parkour.getInstance();

        if (challenge.getWager() != null
                && !parkour.getEconomyApi().hasAmount(targetPlayer, challenge.getWager())) {
            TranslationUtils.sendMessage(player, "Player can not afford this wager.");
            return false;
        }

        // they've accepted a challenge, but they haven't started the course yet
        if (parkour.getChallengeManager().hasPlayerBeenChallenged(targetPlayer)) {
            TranslationUtils.sendMessage(player, "Player has been Challenged already!");
            return false;
        }

        return true;
    }

    /**
     * Validate Player creating Course.
     *
     * @param player player
     * @param courseName desired course name
     * @return player can create course
     */
    public static boolean canCreateCourse(Player player, String courseName) {
        if (courseName.length() > 15) {
            TranslationUtils.sendMessage(player, "Course name is too long!");
            return false;

        } else if (courseName.contains(".")) {
            TranslationUtils.sendMessage(player, "Course name can not contain &4.&f!");
            return false;

        } else if (courseName.contains("_")) {
            TranslationUtils.sendMessage(player,
                    "Course name can not contain &4_&f as it will break Placeholders. Instead, set a Course Display name.");
            return false;

        } else if (ValidationUtils.isInteger(courseName)) {
            TranslationUtils.sendMessage(player, "Course name can not only be numeric!");
            return false;

        } else if (Parkour.getInstance().getCourseManager().doesCourseExists(courseName)) {
            TranslationUtils.sendTranslation("Error.Exist", player);
            return false;
        }

        return true;
    }

    /**
     * Validate Player creating a Course Checkpoint.
     *
     * @param player player
     * @param checkpoint checkpoint
     * @return player is able to create checkpoint
     */
    public static boolean canCreateCheckpoint(Player player, @Nullable Integer checkpoint) {
        String selectedCourse = PlayerConfig.getConfig(player).getSelectedCourse().toLowerCase();

        if (!Parkour.getInstance().getCourseManager().doesCourseExists(selectedCourse)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, selectedCourse, player);
            return false;
        }

        int checkpoints = CourseConfig.getConfig(selectedCourse).getCheckpointAmount() + 1;

        if (checkpoint != null) {
            if (checkpoint < 1) {
                TranslationUtils.sendMessage(player, "Checkpoint specified is not valid!");
                return false;
            }
            if (checkpoints < checkpoint) {
                TranslationUtils.sendMessage(player, "This checkpoint does not exist! &4Creation cancelled.");
                return false;
            }

            checkpoints = checkpoint;
        }

        if (checkpoints < 1) {
            TranslationUtils.sendMessage(player, "Invalid checkpoint number.");
            return false;
        }
        return true;
    }

    /**
     * Validate Player creating a Challenge.
     *
     * @param player player
     * @param courseNameInput course name
     * @param wager wager amount
     * @return player can create challenge
     */
    public static boolean canCreateChallenge(Player player, String courseNameInput, String wager) {
        if (!PermissionUtils.hasPermission(player, Permission.BASIC_CHALLENGE)) {
            return false;
        }

        String courseName = courseNameInput.toLowerCase();
        Parkour parkour = Parkour.getInstance();

        if (!parkour.getCourseManager().doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, player);
            return false;
        }

        if (parkour.getPlayerManager().isPlaying(player)) {
            TranslationUtils.sendMessage(player, "You are already on a Course!");
            return false;
        }

        if (!canJoinCourseSilent(player, courseName)) {
            TranslationUtils.sendMessage(player, "You are not able to join this Course!");
            return false;
        }

        if (!parkour.getPlayerManager().delayPlayerWithMessage(player, 8)) {
            return false;
        }

        if (wager != null) {
            EconomyApi economyApi = parkour.getEconomyApi();

            if (!economyApi.isEnabled()) {
                TranslationUtils.sendMessage(player, "Economy is disabled, no wager will be made.");

            } else if (!ValidationUtils.isPositiveDouble(wager)) {
                TranslationUtils.sendMessage(player, "Wager must be a positive number.");
                return false;

            } else if (!economyApi.hasAmount(player, Double.parseDouble(wager))) {
                TranslationUtils.sendMessage(player, "You do not have enough funds for this wager.");
                return false;
            }
        }
        return true;
    }

    /**
     * Validate Player Challenging target Player.
     *
     * @param player player
     * @param challenge challenge
     * @param targetPlayerName target player name
     * @return player can challenge player
     */
    public static boolean canChallengePlayer(Player player, Challenge challenge, String targetPlayerName) {
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        Parkour parkour = Parkour.getInstance();

        if (targetPlayer == null) {
            TranslationUtils.sendMessage(player, "This player is not online!");
            return false;
        }

        if (parkour.getPlayerManager().isPlaying(targetPlayer)) {
            TranslationUtils.sendMessage(player, "This player is already on a Course!");
            return false;
        }

        if (player.getName().equalsIgnoreCase(targetPlayer.getName())) {
            TranslationUtils.sendMessage(player, "You can't challenge yourself!");
            return false;
        }

        return canJoinChallenge(player, targetPlayer, challenge);
    }

    /**
     * Validate Sender deleting a Course.
     *
     * @param sender command sender
     * @param courseName course name
     * @return command sender can delete course
     */
    public static boolean canDeleteCourse(CommandSender sender, String courseName) {
        if (!Parkour.getInstance().getCourseManager().doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return false;
        }

        courseName = courseName.toLowerCase();
        List<String> dependentCourses = new ArrayList<>();

        for (String course : Parkour.getInstance().getCourseManager().getCourseNames()) {
            String linkedCourse = CourseConfig.getConfig(courseName).getLinkedCourse();

            if (courseName.equals(linkedCourse)) {
                dependentCourses.add(course);
            }
        }

        if (!dependentCourses.isEmpty()) {
            TranslationUtils.sendMessage(sender,
                    "This Course can not be deleted as there are other dependent Courses: " + dependentCourses);
            return false;
        }

        return true;
    }

    /**
     * Validate Sender deleting a Checkpoint.
     * @param sender command sender
     * @param courseName course name
     * @return command sender can delete checkpoint
     */
    public static boolean canDeleteCheckpoint(CommandSender sender, String courseName) {
        if (!Parkour.getInstance().getCourseManager().doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return false;
        }

        int checkpoints = CourseConfig.getConfig(courseName).getCheckpointAmount();
        // if it has no checkpoints
        if (checkpoints <= 0) {
            TranslationUtils.sendMessage(sender, courseName + " has no Checkpoints!");
            return false;
        }
        return true;
    }

    /**
     * Validate Sender deleting a Lobby.
     *
     * @param sender command sender
     * @param lobbyName lobby name
     * @return command sender can delete lobby
     */
    public static boolean canDeleteLobby(CommandSender sender, String lobbyName) {
        if (!Parkour.getLobbyConfig().doesLobbyExist(lobbyName)) {
            TranslationUtils.sendValueTranslation("Error.UnknownLobby", lobbyName, sender);
            return false;
        }

        lobbyName = lobbyName.toLowerCase();
        List<String> dependentCourses = new ArrayList<>();

        for (String course : Parkour.getInstance().getCourseManager().getCourseNames()) {
            String linkedLobby = CourseConfig.getConfig(course).getLinkedLobby();

            if (lobbyName.equals(linkedLobby)) {
                dependentCourses.add(course);
            }
        }

        if (!dependentCourses.isEmpty()) {
            TranslationUtils.sendMessage(sender,
                    "This Lobby can not be deleted as there are dependent Courses: " + dependentCourses);
            return false;
        }

        return true;
    }

    /**
     * Validate Sender deleting a ParkourKit.
     *
     * @param sender command sender
     * @param parkourKit kit name
     * @return command sender can delete parkour kit
     */
    public static boolean canDeleteParkourKit(CommandSender sender, String parkourKit) {
        if (!Parkour.getParkourKitConfig().doesParkourKitExist(parkourKit)) {
            TranslationUtils.sendTranslation("Error.UnknownParkourKit", sender);
            return false;
        }

        parkourKit = parkourKit.toLowerCase();
        List<String> dependentCourses = Parkour.getParkourKitConfig().getDependentCourses(parkourKit);

        if (!dependentCourses.isEmpty()) {
            TranslationUtils.sendMessage(sender,
                    "This ParkourKit can not be deleted as there are dependent Courses: " + dependentCourses);
            return false;
        }

        return true;
    }
}

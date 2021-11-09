package io.github.a5h73y.parkour.type.course;

import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_INVALID_AMOUNT;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_UNKNOWN_PLAYER;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.LeaderboardConversation;
import io.github.a5h73y.parkour.conversation.SetCourseConversation;
import io.github.a5h73y.parkour.database.TimeEntry;
import io.github.a5h73y.parkour.gui.impl.CourseSettingsGui;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.ParkourValidation;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parkour Course Manager.
 * Keeps a lazy Cache of {@link Course} which can be reused by other players.
 */
public class CourseManager extends AbstractPluginReceiver {

    // all known course names, regardless of ready status
    private final List<String> courseNames = new ArrayList<>();
    // cached Course data, populated with checkpoints etc.
    private final Map<String, Course> courseCache = new HashMap<>();

    public CourseManager(final Parkour parkour) {
        super(parkour);
        populateCourseCache();
    }

    public List<String> getCourseNames() {
        return courseNames;
    }

    /**
     * Check if Course is known by Parkour.
     *
     * @param courseName course name
     * @return course exists
     */
    public boolean doesCourseExists(final String courseName) {
        if (!ValidationUtils.isStringValid(courseName)) {
            return false;
        }

        return courseNames.contains(courseName.trim().toLowerCase());
    }

    /**
     * Get Course by input field.
     * If the Course numeric index is provided, that will be retrieved.
     * Otherwise a search will be made by Course name.
     *
     * @param input course name
     * @return matching Course
     */
    @Nullable
    public Course findCourse(final String input) {
        if (ValidationUtils.isInteger(input)) {
            return findByIndex(Integer.parseInt(input));

        } else {
            return findByName(input);
        }
    }

    /**
     * Find Course by unique name.
     * If the Course is cached it can be returned directly.
     * Otherwise the entire Course will be populated, including Checkpoints and ParkourKit.
     * If the Course is in 'Ready' status, it will be added to the Cache.
     *
     * @param courseNameInput course name
     * @return populated {@link Course}
     */
    @Nullable
    public Course findByName(@NotNull final String courseNameInput) {
        String courseName = courseNameInput.toLowerCase();

        if (courseCache.containsKey(courseName)) {
            return courseCache.get(courseName);
        }

        if (!doesCourseExists(courseName)) {
            return null;
        }

        Course course = null;

        try {
            CourseConfig config = CourseConfig.getConfig(courseName);
            // deserialize Course from config
            course = config.getCourse();

            // if course is ready, cache it.
            if (config.getReadyStatus()) {
                courseCache.put(courseName, course);
            }
        } catch (Exception ex) {
            PluginUtils.log("Failed to load Course " + courseName, 2);
            ex.printStackTrace();
        }

        return course;
    }

    /**
     * Find Course by list Index.
     * When displaying the list of courses available, a player can use the shorthand
     * list index number to join the Course. Results can be found using "/pa list courses"
     * The ID will be based on where it is in the list of courses, meaning it can change.
     *
     * @param courseIndex course index
     * @return {@link Course}
     */
    @Nullable
    public Course findByIndex(final int courseIndex) {
        if (courseIndex <= 0 || courseIndex > courseNames.size()) {
            return null;
        }

        String courseName = courseNames.get(courseIndex - 1);
        return findByName(courseName);
    }

    /**
     * Find the current Course for a Player.
     * Their matching ParkourSession will be used to retrieve the Course.
     *
     * @param player target player
     * @return {@link Course}
     */
    @Nullable
    public Course findByPlayer(final Player player) {
        if (!parkour.getPlayerManager().isPlaying(player)) {
            return null;
        }

        return parkour.getPlayerManager().getParkourSession(player).getCourse();
    }

    /**
     * Create a new Parkour Course.
     * The requested Course must first be validated for being unique and valid.
     * The Course data is created, and an entry will be added to checkpoints data.
     * The world in which the Course is created is used throughout the Course.
     * A Course entry will be added to the database to store Player times against it.
     *
     * @param player requesting player
     * @param courseNameInput requested course name
     */
    public void createCourse(final Player player, final String courseNameInput) {
        if (!ParkourValidation.canCreateCourse(player, courseNameInput)) {
            return;
        }

        String courseName = courseNameInput.toLowerCase();

        CourseConfig.getConfig(courseName).createCourseData(player);
        courseNames.add(courseName);
        PlayerConfig.getConfig(player).setSelectedCourse(courseName);

        TranslationUtils.sendValueTranslation("Parkour.Created", courseName, player);
        TranslationUtils.sendValueTranslation("Parkour.WhenReady", courseName, false, player);
        parkour.getDatabaseManager().insertCourse(courseName);
    }

    /**
     * Select the Course for editing.
     * This is used for commands that do not require a course parameter, such as "/pa checkpoint".
     *
     * @param player requesting player
     * @param courseName course name
     */
    public void selectCourse(final Player player, final String courseName) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, player);
            return;
        }

        PlayerConfig.getConfig(player).setSelectedCourse(courseName);
        TranslationUtils.sendValueTranslation("Parkour.Selected", courseName, player);
    }

    /**
     * Deselect the Course for editing.
     *
     * @param player requesting player
     */
    public void deselectCourse(final Player player) {
        PlayerConfig playerConfig = PlayerConfig.getConfig(player);

        if (playerConfig.hasSelectedCourse()) {
            playerConfig.resetSelected();
            TranslationUtils.sendTranslation("Parkour.Deselected", player);

        } else {
            TranslationUtils.sendTranslation("Error.Selected", player);
        }
    }

    /**
     * Delete a Course from the Server.
     * Remove all information stored about the course, including all references from the database.
     *
     * @param sender requesting sender
     * @param courseName course name
     */
    public void deleteCourse(final CommandSender sender, final String courseName) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        CourseConfig.deleteCourseData(courseName);
        parkour.getDatabaseManager().deleteCourseAndReferences(courseName);
        courseNames.remove(courseName);
        clearCache(courseName);
        parkour.getConfigManager().getCourseCompletionsConfig().removeCompletedCourse(courseName);
        TranslationUtils.sendValueTranslation("Parkour.Delete", courseName, sender);
        PluginUtils.logToFile(courseName + " course was deleted by " + sender.getName());
    }

    /**
     * Set Course Start Location.
     * Overwrite the Starting location of a Course to the Player's position.
     *
     * @param player requesting player
     * @param courseName course name
     */
    public void setStartLocation(final Player player, final String courseName) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, player);
            return;
        }

        CourseConfig.getConfig(courseName).createCheckpointData(player.getLocation(), 0);
        PluginUtils.logToFile(courseName + " start location was reset by " + player.getName());
        TranslationUtils.sendPropertySet(player, "Start Location", courseName, "your position");
    }

    /**
     * Overwrite the Creator of the Course.
     *
     * @param sender requesting player
     * @param courseName course name
     * @param value new creator name
     */
    public void setCreator(final CommandSender sender, final String courseName, final String value) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        CourseConfig.getConfig(courseName).setCreator(value);
        TranslationUtils.sendPropertySet(sender, "Creator", courseName, value);
    }

    /**
     * Set MaxDeaths for Course.
     * Set the maximum amount of deaths a player can accumulate before failing the course.
     *
     * @param sender requesting player
     * @param courseName course name
     * @param value new maximum deaths
     */
    public void setMaxDeaths(final CommandSender sender, final String courseName, final String value) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        if (!ValidationUtils.isPositiveInteger(value)) {
            TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
            return;
        }

        CourseConfig.getConfig(courseName).setMaximumDeaths(Integer.parseInt(value));
        clearCache(courseName);
        TranslationUtils.sendPropertySet(sender, "Maximum Deaths", courseName, value);
    }

    /**
     * Set Maximum Time for Course in seconds.
     * Set the maximum amount of seconds a player can accumulate before failing the course.
     *
     * @param sender requesting player
     * @param courseName course name
     * @param secondsValue new maximum seconds
     */
    public void setMaxTime(final CommandSender sender, final String courseName, final String secondsValue) {
        if (!parkour.getParkourConfig().getBoolean("OnCourse.DisplayLiveTime")
                && !(parkour.getParkourConfig().getBoolean("Scoreboard.Enabled")
                && parkour.getParkourConfig().getBoolean("Scoreboard.LiveTimer.Enabled"))) {
            TranslationUtils.sendMessage(sender, "The live timer is disabled!");
            return;
        }

        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        if (!ValidationUtils.isPositiveInteger(secondsValue)) {
            TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
            return;
        }

        int seconds = Integer.parseInt(secondsValue);
        CourseConfig.getConfig(courseName).setMaximumTime(seconds);
        clearCache(courseName);
        TranslationUtils.sendPropertySet(sender, "Maximum Time Limit", courseName, DateTimeUtils.convertSecondsToTime(seconds));
    }

    /**
     * Toggle the Course's Ready status.
     * When configured a Course may not be joinable by others until it has been flagged as 'ready'.
     * A Course's data will only be cached when it has been marked as Ready.
     * If a Course argument is supplied it will be used, otherwise it will use the Player's selected Course.
     *
     * @param player requesting player
     * @param courseNameInput course name
     */
    public void toggleCourseReadyStatus(final Player player, @Nullable final String courseNameInput) {
        PlayerConfig playerConfig = PlayerConfig.getConfig(player);
        String courseName = courseNameInput == null ? playerConfig.getSelectedCourse() : courseNameInput;

        if (!ValidationUtils.isStringValid(courseName)) {
            TranslationUtils.sendMessage(player, "Please select a Course, or provide a Course name");
            return;
        }

        if (!PermissionUtils.hasPermissionOrCourseOwnership(player, Permission.ADMIN_COURSE, courseName)) {
            return;
        }

        CourseConfig courseConfig = CourseConfig.getConfig(courseName);

        boolean ready = !courseConfig.getReadyStatus();
        courseConfig.setReadyStatus(ready);

        if (ready) {
            playerConfig.resetSelected();
        }

        TranslationUtils.sendPropertySet(player, "Ready Status", courseName, String.valueOf(ready));
        PluginUtils.logToFile(courseName + " ready status was set to " + ready + " by " + player.getName());
    }

    /**
     * Toggle the Course's RewardOnce status.
     * Set whether the Player only gets the prize for the first completion of the Course.
     *
     * @param sender requesting sender
     * @param courseName course name
     */
    public void toggleRewardOnceStatus(final CommandSender sender, final String courseName) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        // invert the existing value
        CourseConfig courseConfig = CourseConfig.getConfig(courseName);
        boolean isEnabled = !courseConfig.getRewardOnce();
        courseConfig.setRewardOnce(isEnabled);
        TranslationUtils.sendPropertySet(sender, "Reward Once Status", courseName, String.valueOf(isEnabled));
    }

    /**
     * Toggle the Course's ChallengeOnly status.
     * Set whether the Player can only join the Course if they are part of a Challenge.
     *
     * @param sender requesting sender
     * @param courseName course name
     */
    public void toggleChallengeOnlyStatus(CommandSender sender, String courseName) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        // invert the existing value
        CourseConfig courseConfig = CourseConfig.getConfig(courseName);
        boolean isEnabled = !courseConfig.getChallengeOnly();
        courseConfig.setChallengeOnly(isEnabled);
        TranslationUtils.sendPropertySet(sender, "Challenge Only Status", courseName, String.valueOf(isEnabled));
    }

    /**
     * Toggle the Course's Resumable status.
     * Set whether the Player can resume the Course after leaving the Course.
     *
     * @param sender requesting sender
     * @param courseName course name
     */
    public void toggleResumable(CommandSender sender, String courseName) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        if (parkour.getParkourConfig().isLeaveDestroyCourseProgress()) {
            TranslationUtils.sendMessage(sender,
                    "Disable Course progress destruction in the plugin configuration to allow for Courses to be resumable.");
            return;
        }

        // invert the existing value
        CourseConfig courseConfig = CourseConfig.getConfig(courseName);
        boolean isEnabled = !courseConfig.getResumable();
        courseConfig.setResumable(isEnabled);
        TranslationUtils.sendPropertySet(sender, "Resumable", courseName, String.valueOf(isEnabled));
    }

    /**
     * Set the Course Display name for the Course.
     * This will be presented when sending a message to Player(s) instead of storage.
     * @param sender requesting sender
     * @param courseName course name
     * @param input course display name
     */
    public void setDisplayName(CommandSender sender, String courseName, String input) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        if (!ValidationUtils.isStringValid(input)) {
            TranslationUtils.sendTranslation("Error.InvalidValue", sender);
            return;
        }

        CourseConfig.getConfig(courseName).setCourseDisplayName(input);
        TranslationUtils.sendPropertySet(sender, "Course Display Name", courseName, StringUtils.colour(input));
    }

    /**
     * Set the minimum ParkourLevel required to join the Course.
     *
     * @param sender requesting player
     * @param courseName course name
     * @param value new minimum ParkourLevel
     */
    public void setMinimumParkourLevel(final CommandSender sender, final String courseName, final String value) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        if (!ValidationUtils.isPositiveInteger(value)) {
            TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
            return;
        }

        CourseConfig.getConfig(courseName).setMinimumParkourLevel(Integer.parseInt(value));
        TranslationUtils.sendPropertySet(sender, "Minimum ParkourLevel", courseName, value);
    }

    /**
     * Set the Course to be linked to another Course or Lobby.
     *
     * @param player requesting player
     * @param args command arguments
     */
    public void setCourseLink(final Player player, final String... args) {
        String selectedCourse = PlayerConfig.getConfig(player).getSelectedCourse();

        if (args.length >= 3 && args[1].equalsIgnoreCase("course")) {
            if (!doesCourseExists(args[2])) {
                TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, args[2], player);
                return;
            }

            CourseConfig courseConfig = CourseConfig.getConfig(selectedCourse);
            if (courseConfig.hasLinkedLobby()) {
                TranslationUtils.sendMessage(player, "This Course is linked to a Lobby!");
                return;
            }

            courseConfig.setLinkedCourse(args[2]);
            TranslationUtils.sendPropertySet(player, "Linked Course", selectedCourse, args[2]);

        } else if (args.length >= 3 && args[1].equalsIgnoreCase("lobby")) {
            if (!parkour.getConfigManager().getLobbyConfig().doesLobbyExist(args[2])) {
                TranslationUtils.sendValueTranslation("Error.UnknownLobby", args[2], player);
                return;
            }

            CourseConfig courseConfig = CourseConfig.getConfig(selectedCourse);
            if (courseConfig.hasLinkedCourse()) {
                TranslationUtils.sendMessage(player, "This Course is linked to a Course!");
                return;
            }

            courseConfig.setLinkedLobby(args[2]);
            TranslationUtils.sendPropertySet(player, "Linked Lobby", selectedCourse, args[2]);

        } else if (args[1].equalsIgnoreCase("reset")) {
            CourseConfig.getConfig(selectedCourse).resetLinks();
            TranslationUtils.sendPropertySet(player, "Linked Status", selectedCourse, "none");

        } else {
            TranslationUtils.sendInvalidSyntax(player, "Link", "(course / lobby / reset) (courseName / lobbyName)");
        }
    }

    /**
     * Set the Player Limit for Course.
     * Set a limit on the number of players that can play the Course concurrently.
     *
     * @param sender requesting sender
     * @param courseName course name
     * @param limit player limit
     */
    public void setPlayerLimit(final CommandSender sender, final String courseName, final String limit) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        if (!ValidationUtils.isPositiveInteger(limit)) {
            TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
            return;
        }

        CourseConfig.getConfig(courseName).setPlayerLimit(Integer.parseInt(limit));
        TranslationUtils.sendPropertySet(sender, "Player Limit", courseName, limit);
    }

    /**
     * Link ParkourKit to Course.
     * The ParkourKit will be linked to the Course to be loaded upon Course join.
     *
     * @param sender requesting sender
     * @param courseName course name
     * @param kitName parkour kit name
     */
    public void setParkourKit(final CommandSender sender, final String courseName, final String kitName) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        if (!parkour.getConfigManager().getParkourKitConfig().doesParkourKitExist(kitName)) {
            TranslationUtils.sendTranslation("Error.UnknownParkourKit", sender);
            return;
        }

        CourseConfig courseConfig = CourseConfig.getConfig(courseName);

        if (courseConfig.hasParkourKit()) {
            TranslationUtils.sendMessage(sender, "This Course is already linked to a ParkourKit, continuing anyway...");
        }

        courseConfig.setParkourKit(kitName);
        clearCache(courseName);
        TranslationUtils.sendPropertySet(sender, "ParkourKit", courseName, kitName);
    }

    /**
     * Set Course's ParkourLevel reward.
     * Set to reward the Player with the ParkourLevel on course completion.
     *
     * @param sender requesting sender
     * @param courseName course name
     * @param parkourLevel parkour level
     */
    public void setRewardParkourLevel(final CommandSender sender, final String courseName, final String parkourLevel) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        if (!ValidationUtils.isPositiveInteger(parkourLevel)) {
            TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
            return;
        }

        CourseConfig.getConfig(courseName).setRewardParkourLevel(Integer.parseInt(parkourLevel));
        TranslationUtils.sendPropertySet(sender, "ParkourLevel reward", courseName, parkourLevel);
    }

    /**
     * Set Course's ParkourLevel reward increase.
     * Set to reward the Player with an increment to ParkourLevel on course completion.
     *
     * @param sender requesting sender
     * @param courseName course name
     * @param parkourLevelIncrease parkour level increase
     */
    public void setRewardParkourLevelIncrease(final CommandSender sender, final String courseName,
                                              final String parkourLevelIncrease) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        if (!ValidationUtils.isPositiveInteger(parkourLevelIncrease)) {
            TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
            return;
        }

        CourseConfig.getConfig(courseName).setRewardParkourLevelIncrease(parkourLevelIncrease);
        TranslationUtils.sendPropertySet(sender, "ParkourLevel increase reward", courseName, parkourLevelIncrease);
    }

    /**
     * Set the Reward Delay for the Course.
     * Set the number of hours that must elapse before the Prize can be awarded again to the same Player.
     * The Delay can be a decimal, so "0.5" would be 30 minutes, and "48" would be 2 days.
     *
     * @param sender requesting sender
     * @param courseName course name
     * @param delay prize delay
     */
    public void setRewardDelay(final CommandSender sender, final String courseName, final String delay) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        if (!ValidationUtils.isPositiveDouble(delay)) {
            TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
            return;
        }

        CourseConfig.getConfig(courseName).setRewardDelay(Double.parseDouble(delay));
        long milliseconds = DateTimeUtils.convertHoursToMilliseconds(Double.parseDouble(delay));
        TranslationUtils.sendPropertySet(sender, "Reward Delay", courseName,
                DateTimeUtils.convertMillisecondsToDateTime(milliseconds));
    }

    /**
     * Set the Parkoins Reward for the Course.
     *
     * @param sender requesting sender
     * @param courseName course name
     * @param reward amount to reward
     */
    public void setRewardParkoins(final CommandSender sender, final String courseName, final String reward) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        if (!ValidationUtils.isPositiveDouble(reward)) {
            TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
            return;
        }

        CourseConfig.getConfig(courseName).setRewardParkoins(Double.parseDouble(reward));
        TranslationUtils.sendPropertySet(sender, "Parkoins reward", courseName, reward);
    }

    /**
     * Add a Join Item to the Course.
     * A Material and the Amount can be provided to add the ItemStack(s) to the Player's inventory on Course join.
     * An optional Material label can be provided to display.
     * An optional 'unbreakable' flag can be provided to be applied to the Item.
     *
     * @param sender requesting sender
     * @param args command arguments
     */
    public void addJoinItem(final CommandSender sender, final String... args) {
        if (!doesCourseExists(args[1])) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, args[1], sender);
            return;
        }

        Material material = MaterialUtils.lookupMaterial(args[2].toUpperCase());
        if (material == null) {
            TranslationUtils.sendValueTranslation("Error.UnknownMaterial", args[2].toUpperCase(), sender);
            return;
        }

        if (!ValidationUtils.isPositiveInteger(args[3])) {
            TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
            return;
        }

        int amount = MaterialUtils.parseItemStackAmount(args[3]);
        String label = args.length >= 5 ? args[4] : StringUtils.standardizeText(material.name());
        boolean unbreakable = args.length == 6 && Boolean.parseBoolean(args[5]);

        CourseConfig.getConfig(args[1]).addJoinItem(material, amount, label, unbreakable);
        TranslationUtils.sendPropertySet(sender, "Add Join Item", args[1], material.name() + " x" + amount);
    }

    /**
     * Reset the Course information.
     * Resets all the statistics of Course as if it were just created.
     * All course times will be deleted from the database.
     * The Checkpoints will be unaffected.
     *
     * @param courseNameInput target course name
     */
    public void resetCourse(final CommandSender sender, final String courseNameInput) {
        if (!doesCourseExists(courseNameInput)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseNameInput, sender);
            return;
        }

        String courseName = courseNameInput.toLowerCase();

        CourseConfig.getConfig(courseName).resetCourseData();
        parkour.getDatabaseManager().deleteCourseTimes(courseName);
        parkour.getConfigManager().getCourseCompletionsConfig().removeCompletedCourse(courseName);
        TranslationUtils.sendValueTranslation("Parkour.Reset", courseName, sender);
        PluginUtils.logToFile(courseName + " course was reset by " + sender.getName());
    }

    /**
     * Reset the Course Leaderboards.
     * Each of the time entries for the Course will be deleted.
     *
     * @param sender sender
     * @param courseName course name
     */
    public void resetCourseLeaderboards(CommandSender sender, String courseName) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        parkour.getDatabaseManager().deleteCourseTimes(courseName);
        parkour.getPlaceholderApi().clearCache();
        TranslationUtils.sendValueTranslation("Parkour.Reset", courseName + " Leaderboards", sender);
        PluginUtils.logToFile(courseName + " leaderboards were reset by " + sender.getName());
    }

    /**
     * Reset the Course Leaderboards.
     * Each of the time entries for the Course will be deleted.
     *
     * @param sender sender
     * @param courseName course name
     */
    public void resetPlayerCourseLeaderboards(CommandSender sender, String targetPlayerName, String courseName) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

        if (!PlayerConfig.hasPlayerConfig(targetPlayer)) {
            TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, sender);
            return;
        }

        parkour.getDatabaseManager().deletePlayerCourseTimes(targetPlayer, courseName);
        parkour.getPlaceholderApi().clearCache();
        TranslationUtils.sendValueTranslation("Parkour.Reset", targetPlayerName + "'s "
                + courseName + " Leaderboards", sender);
        PluginUtils.logToFile(targetPlayerName + "'s " + courseName + " leaderboards were reset by " + sender.getName());
    }

    /**
     * Reset the Course Prize.
     * Will result in the Course using the Default prize.
     *
     * @param sender command sender
     * @param courseName course name
     */
    public void resetPrize(CommandSender sender, String courseName) {
        if (!doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        CourseConfig.getConfig(courseName).resetPrizes();
        TranslationUtils.sendValueTranslation("Parkour.Reset", courseName + " Prizes", sender);
        PluginUtils.logToFile(courseName + " prizes were reset by " + sender.getName());
    }

    /**
     * Execute the appropriate Event Commands for Course.
     * When a Course has a matching Command for the {@link ParkourEventType}, execute each of them.
     * The Commands will be dispatched from the Console Sender, and allow for a %PLAYER% placeholder.
     *
     * @param player requesting player
     * @param session parkour session
     * @param eventType event type
     */
    public void runEventCommands(final Player player, final ParkourSession session, final ParkourEventType eventType) {
        CourseConfig courseConfig = CourseConfig.getConfig(session.getCourseName());
        if (courseConfig.hasEventCommands(eventType)) {
            for (String command : courseConfig.getEventCommands(eventType)) {
                PlayerUtils.dispatchServerPlayerCommand(command, player, session);
            }
        }
    }

    /**
     * Process Set Course Command.
     * This Command can be used to set various attributes about the Course in one place.
     *
     * @param sender requesting sender
     * @param args command arguments
     */
    public void processSetCommand(final CommandSender sender, final String... args) {
        if (!doesCourseExists(args[1])) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, args[1], sender);
            return;
        }

        if (args.length == 2 && sender instanceof Player) {
            new SetCourseConversation((Player) sender).withCourseName(args[1]).begin();

        } else if (args.length == 4) {
            SetCourseConversation.performAction(sender, args[1], args[2], args[3]);

        } else if (args.length >= 5) {
            if (args[2].equalsIgnoreCase("message")) {
                if (SetCourseConversation.PARKOUR_EVENT_TYPE_NAMES.contains(args[3].toLowerCase())) {
                    String message = StringUtils.extractMessageFromArgs(args, 4);
                    CourseConfig.getConfig(args[1]).setEventMessage(args[3], message);
                    TranslationUtils.sendPropertySet(sender, StringUtils.standardizeText(args[3]) + " Message", args[1],
                            StringUtils.colour(message));

                } else {
                    TranslationUtils.sendInvalidSyntax(sender, "setcourse",
                            "(courseName) message [" + ParkourEventType.getAllParkourEventTypes() + "] [value]");
                }
            } else if (args[2].equalsIgnoreCase("command")) {
                if (SetCourseConversation.PARKOUR_EVENT_TYPE_NAMES.contains(args[3].toLowerCase())) {
                    String message = StringUtils.extractMessageFromArgs(args, 4);
                    ParkourEventType type = ParkourEventType.valueOf(args[3].toUpperCase());
                    CourseConfig.getConfig(args[1]).addEventCommand(type, message);
                    TranslationUtils.sendPropertySet(sender,
                            StringUtils.standardizeText(args[3]) + " Command", args[1], "/" + message);

                } else {
                    TranslationUtils.sendInvalidSyntax(sender, "setcourse",
                            "(courseName) command [" + ParkourEventType.getAllParkourEventTypes() + "] [value]");
                }
            } else if (args[2].equalsIgnoreCase("displayname")) {
                SetCourseConversation.performAction(sender, args[1], args[2], StringUtils.extractMessageFromArgs(args, 3));

            } else {
                TranslationUtils.sendInvalidSyntax(sender, "setcourse",
                        "(courseName) [displayname, creator, minlevel, maxdeath, maxtime, message] [value]");
            }
        } else {
            TranslationUtils.sendInvalidSyntax(sender, "setcourse",
                    "(courseName) [displayname, creator, minlevel, maxdeath, maxtime, message] [value]");
        }
    }

    /**
     * Display the Contents of a Reference Data List.
     * Possible selections include: players, courses, ranks, lobbies
     *
     * @param sender requesting sender
     * @param args command arguments
     */
    public void displayList(final CommandSender sender, final String... args) {
        if (args.length < 2) {
            TranslationUtils.sendInvalidSyntax(sender, "list", "(players / courses / ranks / lobbies)");
            return;
        }

        if (args[1].equalsIgnoreCase("players")) {
            parkour.getPlayerManager().displayParkourPlayers(sender);

        } else if (args[1].equalsIgnoreCase("courses")) {
            int page = args.length == 3 && args[2] != null
                    && ValidationUtils.isPositiveInteger(args[2]) ? Integer.parseInt(args[2]) : 1;
            displayCourses(sender, page);

        } else if (args[1].equalsIgnoreCase("ranks")) {
            parkour.getParkourRankManager().displayParkourRanks(sender);

        } else if (args[1].equalsIgnoreCase("lobbies")) {
            if (!PermissionUtils.hasPermission(sender, Permission.ADMIN_ALL)) {
                return;
            }
            parkour.getLobbyManager().displayLobbies(sender);

        } else {
            TranslationUtils.sendInvalidSyntax(sender, "list", "(players / courses / ranks / lobbies)");
        }
    }

    /**
     * Request to display Course Leaderboards.
     * When provided with sufficient command arguments the results will be displayed in the Player's chat.
     * Otherwise, the Leaderboard Conversation will start to allow the Player to choose which results to display.
     *
     * @param player requesting player
     * @param args command arguments
     */
    public void displayLeaderboards(final Player player, final String... args) {
        if (!parkour.getPlayerManager().delayPlayerWithMessage(player, 1)) {
            return;
        }

        if (args.length == 1) {
            new LeaderboardConversation(player).begin();
            return;
        }

        if (!doesCourseExists(args[1])) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, args[1], player);
            return;
        }

        int limit = 5;
        boolean personal = false;

        if (args.length >= 4) {
            String choice = args[3].toLowerCase();
            if (choice.equals("personal") || choice.equals("local") || choice.equals("mine")) {
                personal = true;
            } else {
                TranslationUtils.sendMessage(player, "Unknown leaderboard scope, for your results use 'local'.");
            }
        }

        if (args.length >= 3) {
            if (!ValidationUtils.isPositiveInteger(args[2])) {
                TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, player);
                return;
            }
            limit = Integer.parseInt(args[2]);
        }

        List<TimeEntry> results;
        if (personal) {
            results = parkour.getDatabaseManager().getTopPlayerCourseResults(player, args[1], limit);
        } else {
            results = parkour.getDatabaseManager().getTopCourseResults(args[1], limit);
        }
        parkour.getDatabaseManager().displayTimeEntries(player, args[1], results);
    }

    /**
     * Open the {@link CourseSettingsGui} Inventory to the Player.
     *
     * @param player player
     * @param courseName course name
     */
    public void displaySettingsGui(Player player, String courseName) {
        if (!parkour.getCourseManager().doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, player);
            return;
        }

        parkour.getGuiManager().showMenu(player, new CourseSettingsGui(courseName));
    }

    /**
     * Clear the specified Course information.
     *
     * @param courseName target course name
     */
    public void clearCache(final String courseName) {
        courseCache.remove(courseName.toLowerCase());
    }

    public void clearCache() {
        courseCache.clear();
    }

    public int getCacheSize() {
        return courseCache.size();
    }

    /**
     * Display all available Parkour Courses.
     * Courses are presented in Menus, in groups of 10.
     * Information including their name, index number, ready status and level rewards.
     *
     * @param sender requesting sender
     * @param page desired page number
     */
    private void displayCourses(CommandSender sender, int page) {
        if (courseNames.isEmpty()) {
            TranslationUtils.sendMessage(sender, "There are no Parkour courses!");
            return;
        }

        if (page <= 0) {
            TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
            return;
        }

        int results = 10;
        int fromIndex = (page - 1) * results;

        List<String> courseList = courseNames;
        if (parkour.getParkourConfig().getBoolean("Other.Display.OnlyReadyCourses")
                && !PermissionUtils.hasPermission(sender, Permission.ADMIN_READY_BYPASS, false)) {
            courseList = new ArrayList<>(courseCache.keySet());
        }

        if (courseList.size() <= fromIndex) {
            TranslationUtils.sendMessage(sender, "This page doesn't exist.");
            return;
        }

        TranslationUtils.sendMessage(sender, courseList.size() + " courses available:");
        List<String> limited = courseList.subList(fromIndex, Math.min(fromIndex + results, courseList.size()));

        for (int i = 0; i < limited.size(); i++) {
            String courseName = limited.get(i);
            CourseConfig courseConfig = CourseConfig.getConfig(courseName);
            boolean ready = courseConfig.getReadyStatus();

            StringBuilder sb = new StringBuilder();
            sb.append(fromIndex + (i + 1))
                    .append(") ")
                    .append(ready ? ChatColor.AQUA : ChatColor.RED)
                    .append(courseName);

            if (courseConfig.hasMinimumParkourLevel()) {
                sb.append(ChatColor.RED).append(" (")
                        .append(courseConfig.getMinimumParkourLevel()).append(')');
            }
            if (courseConfig.hasRewardParkourLevel()) {
                sb.append(ChatColor.GREEN).append(" (")
                        .append(courseConfig.getRewardParkourLevel()).append(')');

            } else if (courseConfig.hasRewardParkourLevelIncrease()) {
                sb.append(ChatColor.GREEN).append(" (+")
                        .append(courseConfig.getRewardParkourLevelIncrease()).append(')');
            }

            sender.sendMessage(sb.toString());
        }

        sender.sendMessage("== " + page + " / " + ((courseList.size() + results - 1) / results) + " ==");
    }

    private void populateCourseCache() {
        try (Stream<Path> paths = Files.walk(Paths.get(parkour.getConfigManager().getCoursesDir().toURI()), 1)) {
            paths.filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString().toLowerCase())
                    .filter(path -> path.endsWith(".json"))
                    .map(path -> path.split("\\.")[0])
                    .forEach(courseName -> {
                        courseNames.add(courseName);
                        findByName(courseName);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            PluginUtils.log(courseNames.size() + " courses found.");
            PluginUtils.log(courseCache.size() + " courses cached.");
        }
    }
}

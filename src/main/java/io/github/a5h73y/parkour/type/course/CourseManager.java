package io.github.a5h73y.parkour.type.course;

import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_INVALID_AMOUNT;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_UNKNOWN_PLAYER;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.LeaderboardConversation;
import io.github.a5h73y.parkour.database.TimeEntry;
import io.github.a5h73y.parkour.gui.impl.CourseSettingsGui;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.ParkourValidation;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    public boolean doesCourseExist(final String courseName) {
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

        if (!doesCourseExist(courseName) && !CourseConfig.hasCourseConfig(courseName)) {
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
            if (parkour.getParkourConfig().getBoolean("Debug")) {
                ex.printStackTrace();
            } else {
                PluginUtils.log("Reason: " + ex.getMessage(), 2);
            }
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
        if (!parkour.getParkourSessionManager().isPlaying(player)) {
            return null;
        }

        return parkour.getParkourSessionManager().getParkourSession(player).getCourse();
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
     * Delete a Course from the Server.
     * Remove all information stored about the course, including all references from the database.
     *
     * @param sender requesting sender
     * @param courseName course name
     */
    public void deleteCourse(final CommandSender sender, final String courseName) {
        if (!doesCourseExist(courseName)) {
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
     * Reset the Course information.
     * Resets all the statistics of Course as if it were just created.
     * All course times will be deleted from the database.
     * The Checkpoints will be unaffected.
     *
     * @param courseNameInput target course name
     */
    public void resetCourse(final CommandSender sender, final String courseNameInput) {
        if (!doesCourseExist(courseNameInput)) {
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
        if (!doesCourseExist(courseName)) {
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
     * @param targetPlayerId target player identifier
     * @param courseName course name
     */
    public void resetPlayerCourseLeaderboards(CommandSender sender, String targetPlayerId, String courseName) {
        if (!doesCourseExist(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, sender);
            return;
        }

        OfflinePlayer targetPlayer;

        if (ValidationUtils.isUuidFormat(targetPlayerId)) {
            targetPlayer = Bukkit.getOfflinePlayer(UUID.fromString(targetPlayerId));
        } else {
            targetPlayer = Bukkit.getOfflinePlayer(targetPlayerId);
        }

        if (!PlayerConfig.hasPlayerConfig(targetPlayer)) {
            TranslationUtils.sendTranslation(ERROR_UNKNOWN_PLAYER, sender);
            return;
        }

        parkour.getDatabaseManager().deletePlayerCourseTimes(targetPlayer, courseName);
        parkour.getPlaceholderApi().clearCache();
        TranslationUtils.sendValueTranslation("Parkour.Reset", targetPlayerId + "'s "
                + courseName + " Leaderboards", sender);
        PluginUtils.logToFile(targetPlayerId + "'s " + courseName + " leaderboards were reset by " + sender.getName());
    }

    /**
     * Reset the Course Prize.
     * Will result in the Course using the Default prize.
     *
     * @param sender command sender
     * @param courseName course name
     */
    public void resetPrize(CommandSender sender, String courseName) {
        if (!doesCourseExist(courseName)) {
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
     * The Commands will be dispatched either from the Console Sender or Player, based on prefix.
     * The command will allow for a various internal placeholders to be replaced by ParkourSession values.
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
     * Display the Contents of a Reference Data List.
     * Possible selections include: players, courses, ranks, lobbies
     *
     * @param sender requesting sender
     * @param args command arguments
     */
    public void displayList(final CommandSender sender, final String... args) {
        if (args.length < 2) {
            parkour.getParkourCommands().sendInvalidSyntax(sender, "list");
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

        if (args.length <= 1) {
            new LeaderboardConversation(player).begin();
            return;
        }

        if (!doesCourseExist(args[1])) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, args[1], player);
            return;
        }

        int limit = 5;
        boolean personal = false;

        if (args.length >= 4) {
            String choice = args[3].toLowerCase();
            if (choice.equals("personal") || choice.equals("local") || choice.equals("mine")) {
                personal = true;
            } else if (!choice.equals("global")) {
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
        if (!parkour.getCourseManager().doesCourseExist(courseName)) {
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

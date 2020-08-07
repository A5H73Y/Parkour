package io.github.a5h73y.parkour.type.course;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.conversation.CoursePrizeConversation;
import io.github.a5h73y.parkour.conversation.LeaderboardConversation;
import io.github.a5h73y.parkour.conversation.ParkourModeConversation;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.type.kit.ParkourKit;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.Utils;
import io.github.a5h73y.parkour.utility.support.XMaterial;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CourseManager extends AbstractPluginReceiver {

    // TODO have configuration useCourseCache
    // If you have a crazy amount of courses / checkpoints, caching might be a bad idea

    private final Map<String, Course> courseCache = new HashMap<>();

    public CourseManager(final Parkour parkour) {
        super(parkour);
    }


    /**
     * Does course exist.
     * Check if a course exists on the server
     *
     * @param courseName
     * @return course found
     */
    public boolean courseExists(String courseName) {
        if (!Validation.isStringValid(courseName)) {
            return false;
        }

        courseName = courseName.trim().toLowerCase();

        for (String course : CourseInfo.getAllCourses()) {
            if (courseName.equals(course)) {
                return true;
            }
        }

        return false;
    }

    public Course getCourse(String argument) {
        if (Validation.isInteger(argument)) {
            return findByNumber(Integer.parseInt(argument));

        } else {
            return findByName(argument);
        }
    }

    /**
     * Retrieve a course based on its unique name.
     * Everything is populated from this method (all checkpoints & information).
     * This should only be used when necessary.
     *
     * @param courseName
     * @return Course
     */
    public Course findByName(String courseName) {
        courseName = courseName.toLowerCase();

        if (courseCache.containsKey(courseName)) {
            return courseCache.get(courseName);
        }

        if (!courseExists(courseName)) {
            return null;
        }

        Course course = populateCourse(courseName);
        // if course is finished, cache it.
        if (CourseInfo.getFinished(courseName)) {
            courseCache.put(courseName, course);
        }

        return course;
    }

    private Course populateCourse(String courseName) {
        courseName = courseName.toLowerCase();
        String parkourKitName = CourseInfo.getParkourKit(courseName);
        ParkourKit parkourKit = parkour.getParkourKitManager().getParkourKit(parkourKitName);
        ParkourMode parkourMode = getCourseMode(courseName);
        List<Checkpoint> checkpoints = parkour.getCheckpointManager().getCheckpoints(courseName);

        return new Course(courseName, checkpoints, parkourKit, parkourMode);
    }

    /**
     * Retrieve a course based on its list index.
     * The ID will be based on where it is in the list of courses, meaning it can change.
     * Find the current ID using "/pa list courses"
     *
     * @param courseNumber
     * @return Course
     */
    public Course findByNumber(int courseNumber) {
        if (courseNumber <= 0 || courseNumber > CourseInfo.getAllCourses().size()) {
            return null;
        }

        String courseName = CourseInfo.getAllCourses().get(courseNumber - 1);
        return findByName(courseName);
    }

    /**
     * Retrieve a course based on the ParkourSession for a player.
     *
     * @param playerName
     * @return
     */
    public Course findByPlayer(String playerName) {
        if (!parkour.getPlayerManager().isPlaying(playerName)) {
            return null;
        }

        return parkour.getPlayerManager().getPlaying().get(playerName).getCourse();
    }

    /**
     * Create a new Parkour course.
     * The start of the course is located in courses.yml as checkpoint '0'.
     * The world is assumed once the course is joined, so having 2 different checkpoints in 2 different worlds is not an option.
     * All course names are stored in a string list, which is also held in memory.
     * Course is entered into the database so leaderboards can be associated with it.
     *
     * @param courseName
     * @param player
     */
    public void createCourse(String courseName, Player player) {
        if (!Validation.courseCreation(courseName, player)) {
            return;
        }

        String name = courseName.toLowerCase();
        Location location = player.getLocation();
        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);

        courseConfig.set(name + ".Creator", player.getName());
        courseConfig.set(name + ".Views", 0);
        courseConfig.set(name + ".Completed", 0);
        courseConfig.set(name + ".World", location.getWorld().getName());

        parkour.getCheckpointManager().createCheckpointData(name, player.getLocation(), 0);

        List<String> courseList = CourseInfo.getAllCourses();
        courseList.add(name);
        Collections.sort(courseList);
        courseConfig.set("Courses", courseList);
        courseConfig.save();

        PlayerInfo.setSelectedCourse(player, name);

        TranslationUtils.sendValueTranslation("Parkour.Created", courseName, player);
        Parkour.getInstance().getDatabase().insertCourse(name, player.getName());
    }

    /**
     * Delete a course.
     * Remove all information stored on the server about the course, including all references from the database.
     *
     * @param courseName
     * @param player
     */
    public void deleteCourse(String courseName, Player player) {
        if (!courseExists(courseName)) {
            TranslationUtils.sendValueTranslation("Error.NoExist", courseName, player);
            return;
        }

        CourseInfo.deleteCourse(courseName);
        TranslationUtils.sendValueTranslation("Parkour.Delete", courseName, player);
    }

    public void clearCache(String courseName) {
        courseCache.remove(courseName.toLowerCase());
    }

    public void clearCache() {
        courseCache.clear();
    }

    /**
     * Displays a list to the player.
     * Available options: players, courses, ranks & lobbies
     *
     * @param args
     * @param sender
     */
    public void displayList(String[] args, CommandSender sender) {
        if (args.length < 2) {
            TranslationUtils.sendInvalidSyntax(sender, "list", "(players / courses / ranks / lobbies)");
            return;
        }

        if (args[1].equalsIgnoreCase("players")) {
            displayPlaying(sender);

        } else if (args[1].equalsIgnoreCase("courses")) {
            int page = (args.length == 3 && args[2] != null && Validation.isPositiveInteger(args[2]) ? Integer.parseInt(args[2]) : 1);
            displayCourses(sender, page);

        } else if (args[1].equalsIgnoreCase("ranks")) {
            if (!PermissionUtils.hasPermission(sender, Permission.ADMIN_ALL)) {
                return;
            }
            displayRanks(sender);

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
     * Display a list of all the players on a Course.
     * Will display:
     * * the course they are on
     * * the amount of times they've died
     * * how long they've been on the course.
     *
     * @param sender
     */
    private void displayPlaying(CommandSender sender) {
        if (parkour.getPlayerManager().getPlaying().size() == 0) {
            sender.sendMessage(Parkour.getPrefix() + "Nobody is playing Parkour!");
            return;
        }

        sender.sendMessage(Parkour.getPrefix() + parkour.getPlayerManager().getPlaying().size() + " players using Parkour: ");

        String playingTemplate = TranslationUtils.getTranslation("Parkour.Playing", false);
        for (Map.Entry<String, ParkourSession> entry : parkour.getPlayerManager().getPlaying().entrySet()) {
            sender.sendMessage(playingTemplate
                    .replace("%PLAYER%", entry.getKey())
                    .replace("%COURSE%", entry.getValue().getCourse().getName())
                    .replace("%DEATHS%", String.valueOf(entry.getValue().getDeaths()))
                    .replace("%TIME%", entry.getValue().displayTime()));
        }
    }

    /**
     * Display a list of all the Parkour courses on the server.
     * Prints the name and unique course ID, separated into pages of 8 results.
     *
     * @param sender
     * @param page
     */
    private void displayCourses(CommandSender sender, int page) {
        if (CourseInfo.getAllCourses().size() == 0) {
            sender.sendMessage(Parkour.getPrefix() + "There are no Parkour courses!");
            return;
        }

        if (page <= 0) {
            sender.sendMessage(Parkour.getPrefix() + "Please enter a valid page number.");
            return;
        }

        int results = 8;
        int fromIndex = (page - 1) * results;
        List<String> courseList = CourseInfo.getAllCourses();

        if (courseList.size() <= fromIndex) {
            sender.sendMessage(Parkour.getPrefix() + "This page doesn't exist.");
            return;
        }

        sender.sendMessage(Parkour.getPrefix() + courseList.size() + " courses available:");
        List<String> limited = courseList.subList(fromIndex, Math.min(fromIndex + results, courseList.size()));

        for (int i = 0; i < limited.size(); i++) {
            String courseName = limited.get(i);
            int minimumLevel = CourseInfo.getMinimumLevel(courseName);
            int rewardLevel = CourseInfo.getRewardLevel(courseName);
            boolean finished = CourseInfo.getFinished(courseName);

            StringBuilder sb = new StringBuilder();
            sb.append(((fromIndex) + (i + 1)));
            sb.append(") ");
            sb.append(finished ? ChatColor.AQUA : ChatColor.RED);
            sb.append(courseName);

            if (minimumLevel > 0) {
                sb.append(ChatColor.RED).append(" (").append(minimumLevel).append(")");
            }
            if (rewardLevel > 0) {
                sb.append(ChatColor.GREEN).append(" (").append(rewardLevel).append(")");
            }

            sender.sendMessage(sb.toString());
        }

        sender.sendMessage("== " + page + " / " + ((courseList.size() + results - 1) / results) + " ==");
    }

    //TODO move me
    private void displayRanks(CommandSender sender) {
        TranslationUtils.sendHeading("Parkour Ranks", sender);
        ParkourConfiguration playerConfig = Parkour.getConfig(ConfigType.PLAYERS);
        ConfigurationSection section = playerConfig.getConfigurationSection("ServerInfo.Levels");

        if (section == null) {
            sender.sendMessage(Parkour.getPrefix() + "No Ranks set.");

        } else {
            Set<String> levels = section.getKeys(false);
            List<Integer> orderedLevels = levels.stream().mapToInt(Integer::parseInt).sorted().boxed().collect(Collectors.toList());

            for (Integer level : orderedLevels) {
                String rank = playerConfig.getString("ServerInfo.Levels." + level + ".Rank");
                if (rank != null) {
                    sender.sendMessage(TranslationUtils.getTranslation("Parkour.RankInfo", false)
                            .replace("%LEVEL%", level.toString())
                            .replace("%RANK%", StringUtils.colour(rank)));
                }
            }
        }
    }

    /**
     * Start editing a course.
     * Used for functions that do not require a course parameter, such as "/pa checkpoint".
     *
     * @param args
     * @param player
     */
    public void selectCourse(String[] args, Player player) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], player);
            return;
        }

        String courseName = args[1].trim().toLowerCase();
        player.sendMessage(Parkour.getPrefix() + "Now Editing: " + ChatColor.AQUA + args[1]);
        int pointcount = CourseInfo.getCheckpointAmount(courseName);
        player.sendMessage(Parkour.getPrefix() + "Checkpoints: " + ChatColor.AQUA + pointcount);
        PlayerInfo.setSelectedCourse(player, courseName);
    }

    /**
     * Finish editing a course.
     *
     * @param player
     */
    public void deselectCourse(Player player) {
        if (PlayerInfo.hasSelected(player)) {
            PlayerInfo.setDelected(player);
            player.sendMessage(Parkour.getPrefix() + "Finished editing.");

        } else {
            TranslationUtils.sendTranslation("Error.Selected", false);
        }
    }

    /**
     * Set the course prize.
     * Starts a Prize conversation that allows you to configure what the player gets awarded with when the complete the course.
     * A course could have every type of Prize if configured so.
     *
     * @param courseName
     * @param player
     */
    public void setPrize(String courseName, Player player) {
        if (!courseExists(courseName)) {
            TranslationUtils.sendValueTranslation("Error.NoExist", courseName, player);
            return;
        }

        new CoursePrizeConversation(player).withCourseName(courseName.toLowerCase()).begin();
    }

    /**
     * Overwrite the start location of the course.
     *
     * @param player
     */
    public void setStart(Player player) {
        String selectedCourse = PlayerInfo.getSelectedCourse(player);

        if (!courseExists(selectedCourse)) {
            TranslationUtils.sendValueTranslation("Error.NoExist", selectedCourse, player);
            return;
        }

        parkour.getCheckpointManager().createCheckpointData(selectedCourse, player.getLocation(), 0);
        PluginUtils.logToFile(selectedCourse + " spawn was reset by " + player.getName());
        player.sendMessage(Parkour.getPrefix() + "Spawn for " + ChatColor.AQUA + selectedCourse + ChatColor.WHITE + " has been set to your position");
    }

    /**
     * Create an AutoStart for a course.
     * Create an AutoStart pressure plate at the player's location for the specified course.
     *
     * @param args
     * @param player
     */
    public void setAutoStart(String[] args, Player player) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], player);
            return;
        }

        Block block = player.getLocation().getBlock();
        String coordinates = block.getX() + "-" + block.getY() + "-" + block.getZ();
        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);

        if (courseConfig.contains("CourseInfo.AutoStart." + coordinates)) {
            player.sendMessage(Parkour.getPrefix() + "There is already an AutoStart here!");
            return;
        }

        courseConfig.set("CourseInfo.AutoStart." + coordinates, args[1].toLowerCase());
        courseConfig.save();
        player.sendMessage(Parkour.getPrefix() + "AutoStart for " + args[1] + " created!");

        block.setType(XMaterial.STONE_PRESSURE_PLATE.parseMaterial());
        Block blockUnder = block.getRelative(BlockFace.DOWN);
        blockUnder.setType(Parkour.getDefaultConfig().getAutoStartMaterial());
    }

    /**
     * Find matching Course for AutoStart Location.
     * Triggered when the player walks on a pressure plate matching AutoStart.
     * Look for matching config entry for location.
     *
     * @param location
     * @return Course name
     */
    public String getAutoStartCourse(Location location) {
        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);
        String coordinates = location.getBlockX() + "-" + location.getBlockY() + "-" + location.getBlockZ();

        ConfigurationSection entries = courseConfig.getConfigurationSection("CourseInfo.AutoStart");

        if (entries != null) {
            // Go through each entry to find matching coordinates, then return the course
            for (String entry : entries.getKeys(false)) {
                if (entry.equals(coordinates)) {
                    return courseConfig.getString("CourseInfo.AutoStart." + entry);
                }
            }
        }

        return null;
    }

    /**
     * Delete the AutoStart with the given coordinates
     *
     * @param coordinates
     * @param player
     */
    public void deleteAutoStart(String coordinates, Player player) {
        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);
        courseConfig.set("CourseInfo.AutoStart." + coordinates, null);
        courseConfig.save();
    }

    /**
     * Overwrite the creator of the course.
     *
     * @param args
     * @param player
     */
    public void setCreator(String[] args, Player player) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], player);
            return;
        }

        CourseInfo.setCreator(args[1], args[2]);
        player.sendMessage(Parkour.getPrefix() + "Creator of " + ChatColor.DARK_AQUA + args[1] + ChatColor.WHITE + " was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Set MaxDeaths for Course.
     * Set the maximum amount of deaths a player can accumulate before failing the course.
     *
     * @param args
     * @param sender
     */
    public void setMaxDeaths(String[] args, CommandSender sender) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], sender);
            return;
        }

        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Parkour.getPrefix() + "Amount of deaths is not valid.");
            return;
        }

        CourseInfo.setMaximumDeaths(args[1], Integer.parseInt(args[2]));
        sender.sendMessage(Parkour.getPrefix() + ChatColor.AQUA + args[1] + ChatColor.WHITE + " maximum deaths was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Set MaxTime for Course.
     * Set the maximum amount of deaths a player can accumulate before failing the course.
     *
     * @param args
     * @param sender
     */
    public void setMaxTime(String[] args, CommandSender sender) {
        if (!Parkour.getDefaultConfig().getBoolean("OnCourse.DisplayLiveTime")) {
            sender.sendMessage(Parkour.getPrefix() + "Live Time is disabled!");
            return;
        }

        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], sender);
            return;
        }

        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Parkour.getPrefix() + "Amount of seconds is not valid.");
            return;
        }

        int seconds = Integer.parseInt(args[2]);
        CourseInfo.setMaximumTime(args[1], seconds);
        sender.sendMessage(Parkour.getPrefix() + ChatColor.AQUA + args[1] + ChatColor.WHITE + " maximum time limit was set to "
                + ChatColor.AQUA + DateTimeUtils.convertSecondsToTime(seconds));
    }

    /**
     * Set the mimimum Parkour level required to join the course.
     *
     * @param args
     * @param sender
     */
    public void setMinLevel(String[] args, CommandSender sender) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], sender);
            return;
        }
        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Parkour.getPrefix() + "Minimum level is not valid.");
            return;
        }

        CourseInfo.setMinimumLevel(args[1], Integer.parseInt(args[2]));
        sender.sendMessage(Parkour.getPrefix() + ChatColor.AQUA + args[1] + ChatColor.WHITE + " minimum level requirement was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Set Course's ParkourLevel reward.
     * Reward the player with the Parkour level on course completion.
     *
     * @param args
     * @param sender
     */
    public void setRewardParkourLevel(String[] args, CommandSender sender) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], sender);
            return;
        }

        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Parkour.getPrefix() + "Reward level needs to be numeric.");
            return;
        }

        CourseInfo.setRewardLevel(args[1], Integer.parseInt(args[2]));
        sender.sendMessage(Parkour.getPrefix() + args[1] + "'s reward level was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Set Course's ParkourLevel reward addition.
     * Increment the player's ParkourLevel by this value on course completion.
     *
     * @param args
     * @param sender
     */
    public void setRewardParkourLevelAddition(String[] args, CommandSender sender) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], sender);
            return;
        }

        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Parkour.getPrefix() + "Reward level addition needs to be numeric.");
            return;
        }

        CourseInfo.setRewardLevelAdd(args[1], args[2]);
        sender.sendMessage(Parkour.getPrefix() + args[1] + "'s reward level addition was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Set RewardOnce status of Course.
     * Set whether the player only gets the prize for the first time they complete the course.
     *
     * @param args
     * @param sender
     */
    public void setRewardOnce(String[] args, CommandSender sender) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], sender);
            return;
        }

        // invert the existing value
        boolean isEnabled = !CourseInfo.getRewardOnce(args[1]);
        CourseInfo.setRewardOnce(args[1], isEnabled);
        sender.sendMessage(Parkour.getPrefix() + args[1] + "'s reward one time was set to " + ChatColor.AQUA + isEnabled);
    }

    /**
     * Set the Course's ParkourRank reward.
     * Not to be confused with rewardLevel or rewardPrize, this associates a
     * Parkour level with a message prefix. A rank is just a visual prefix. E.g.
     * Level 10: Pro, Level 99: God
     *
     * @param args
     * @param sender
     */
    public void setRewardParkourRank(String[] args, CommandSender sender) {
        if (!Validation.isPositiveInteger(args[1])) {
            sender.sendMessage(Parkour.getPrefix() + "ParkourLevel needs to be numeric.");
            return;
        }

        if (!Validation.isStringValid(args[2])) {
            sender.sendMessage(Parkour.getPrefix() + "ParkourRank is not valid.");
            return;
        }

        PlayerInfo.setRewardRank(Integer.parseInt(args[1]), args[2]);
        sender.sendMessage(Parkour.getPrefix() + "ParkourRank for ParkourLevel " + args[1] + " was set to " + StringUtils.colour(args[2]));
    }

    /**
     * Set Reward delay for course.
     * Set the number of days that have to elapse before the prize can be won again by the same player.
     *
     * @param args
     * @param sender
     */
    public void setRewardDelay(String[] args, CommandSender sender) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], sender);
            return;
        }
        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Parkour.getPrefix() + "Reward delay needs to be numeric.");
            return;
        }

        CourseInfo.setRewardDelay(args[1], Integer.parseInt(args[2]));
        sender.sendMessage(Parkour.getPrefix() + args[1] + "'s reward delay was set to " + args[2] + " day(s).");
    }

    /**
     * Reward the player with Parkoins on course completions.
     * Parkoins are then spent in the store for unlockables.
     *
     * @param args
     * @param sender
     */
    public void setRewardParkoins(String[] args, CommandSender sender) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], sender);
            return;
        }

        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Parkour.getPrefix() + "Parkoins reward needs to be numeric.");
            return;
        }

        CourseInfo.setRewardParkoins(args[1], Integer.parseInt(args[2]));
        sender.sendMessage(Parkour.getPrefix() + args[1] + "'s Parkoins reward was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Toggle the Course's completion status.
     * If a course argument is supplied it will use this, otherwise it will use the player's selected course.
     * A player will not be able to join a course if it's not set to finished.
     *
     * @param args
     * @param player
     */
    public void setCompletionStatus(String[] args, Player player) {
        String courseName = args.length > 1 ? args[1].toLowerCase() : PlayerInfo.getSelectedCourse(player);

        if (!Validation.isStringValid(courseName)) {
            player.sendMessage(Parkour.getPrefix() + "Please select a course, or provide a course argument");
            return;
        }

        if (!PermissionUtils.hasPermissionOrCourseOwnership(player, Permission.ADMIN_COURSE, courseName)) {
            return;
        }

        boolean finished = CourseInfo.getFinished(courseName);
        CourseInfo.setFinished(courseName, !finished);

        if (finished) {
            player.sendMessage(Parkour.getPrefix() + ChatColor.AQUA + courseName + ChatColor.WHITE + " has been set to unfinished!");
            PluginUtils.logToFile(courseName + " was set to unfinished by " + player.getName());

        } else {
            TranslationUtils.sendValueTranslation("Parkour.Finish", courseName, player);
            PluginUtils.logToFile(courseName + " was set to finished by " + player.getName());
            PlayerInfo.setDelected(player);
        }
    }

    /**
     * Link a course to a lobby or another course on completion.
     *
     * @param args
     * @param player
     */
    public void linkCourse(String[] args, Player player) {
        String selected = PlayerInfo.getSelectedCourse(player);

        if (args.length >= 3 && args[1].equalsIgnoreCase("course")) {
            if (!courseExists(args[2])) {
                TranslationUtils.sendValueTranslation("Error.NoExist", args[2], player);
                return;
            }

            if (CourseInfo.hasLinkedLobby(selected)) {
                player.sendMessage(Parkour.getPrefix() + "This course is linked to a lobby!");
                return;
            }

            CourseInfo.setLinkedCourse(selected, args[2]);
            player.sendMessage(Parkour.getPrefix() + ChatColor.DARK_AQUA + selected + ChatColor.WHITE + " is now linked to " + ChatColor.AQUA + args[2]);

        } else if (args.length >= 3 && args[1].equalsIgnoreCase("lobby")) {
            if (!Parkour.getDefaultConfig().contains("Lobby." + args[2] + ".World")) { // TODO
                player.sendMessage(Parkour.getPrefix() + "Lobby " + args[2] + " does not exist.");
                return;
            }

            if (CourseInfo.hasLinkedCourse(selected)) {
                player.sendMessage(Parkour.getPrefix() + "This course is linked to a course!");
                return;
            }

            CourseInfo.setLinkedLobby(selected, args[2]);
            player.sendMessage(Parkour.getPrefix() + ChatColor.DARK_AQUA + selected + ChatColor.WHITE + " is now linked to " + ChatColor.AQUA + args[2]);

        } else if (args[1].equalsIgnoreCase("reset")) {
            CourseInfo.resetLinks(selected);
            player.sendMessage(Parkour.getPrefix() + ChatColor.DARK_AQUA + selected + ChatColor.WHITE + " is no longer linked.");

        } else {
            TranslationUtils.sendInvalidSyntax(player, "Link", "(course / lobby / reset) (courseName / lobbyName)");
        }
    }

    /**
     * Resets all the statistics to 0 as if the course was just created.
     * The structure of the course remains untouched.
     * All course times will be deleted from the database.
     *
     * @param courseName
     */
    public void resetCourse(String courseName) {
        if (!courseExists(courseName)) {
            return;
        }

        courseName = courseName.toLowerCase();
        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);

        courseConfig.set(courseName + ".Views", 0);
        courseConfig.set(courseName + ".Completed", 0);
        courseConfig.set(courseName + ".Finished", false);
        courseConfig.set(courseName + ".Level", null);
        courseConfig.set(courseName + ".MinimumLevel", null);
        courseConfig.set(courseName + ".LevelAdd", null);
        courseConfig.set(courseName + ".MaxDeaths", null);
        courseConfig.set(courseName + ".Parkoins", null);
        courseConfig.set(courseName + ".LinkedLobby", null);
        courseConfig.set(courseName + ".LinkedCourse", null);
        courseConfig.set(courseName + ".ParkourKit", null);
        courseConfig.set(courseName + ".Mode", null);

        //TODO go through ConfigSection, setting each value to null (except for structural)

        courseConfig.save();
        Parkour.getInstance().getDatabase().deleteCourseTimes(courseName);
    }

    /**
     * Link ParkourKit to a course.
     * The name of the ParkourKit must be specified, then will be applied when a player joins the course.
     *
     * @param args
     * @param player
     */
    public void linkParkourKit(String[] args, Player player) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], player);
            return;
        }
        if (!Parkour.getConfig(ConfigType.PARKOURKIT).contains("ParkourKit." + args[2].toLowerCase())) {
            player.sendMessage(Parkour.getPrefix() + "ParkourKit doesn't exist!");
            return;
        }
        if (CourseInfo.hasParkourKit(args[1])) {
            player.sendMessage(Parkour.getPrefix() + "This course is already linked to a ParkourKit, continuing anyway...");
        }

        CourseInfo.setParkourKit(args[1], args[2]);
        player.sendMessage(Parkour.getPrefix() + args[1] + " is now linked to ParkourKit " + args[2]);
    }

    /**
     * Challenge a player to a Course.
     * Invites a player to challenge them in a course, must be confirmed by the recipient before the process initiates.
     *
     * @param args
     * @param player
     */
    public void challengePlayer(String[] args, Player player) {
        if (!Validation.challengePlayer(args, player)) {
            return;
        }

        if (!parkour.getPlayerManager().delayPlayer(player, 10, true)) {
            return;
        }

        String wagerString = "";
        Double wager = null;

        if (args.length == 4 && Parkour.getInstance().getEconomyApi().isEnabled() && Validation.isPositiveDouble(args[3])) {
            String currencyName = Parkour.getInstance().getEconomyApi().getCurrencyName();
            wager = Double.parseDouble(args[3]);
            wagerString = TranslationUtils.getValueTranslation("Parkour.Challenge.Wager", wager + currencyName, false);
        }

        Player target = Bukkit.getPlayer(args[2]);
        String courseName = args[1].toLowerCase();

        target.sendMessage(TranslationUtils.getTranslation("Parkour.Challenge.Receive")
                .replace("%PLAYER%", player.getName())
                .replace("%COURSE%", courseName) + wagerString);
        TranslationUtils.sendTranslation("Parkour.Accept", false, target);

        player.sendMessage(TranslationUtils.getTranslation("Parkour.Challenge.Send")
                .replace("%PLAYER%", target.getName())
                .replace("%COURSE%", courseName) + wagerString);
        Parkour.getInstance().getChallengeManager().createChallenge(player.getName(), target.getName(), courseName, wager);
    }

    /**
     * Set JoinItem for a course.
     * Specify the material and the amount for the course, which will then be given to the player once they join.
     *
     * @param args
     * @param sender
     */
    public void addJoinItem(String[] args, CommandSender sender) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], sender);
            return;
        }

        if (MaterialUtils.lookupMaterial(args[2].toUpperCase()) == null) {
            sender.sendMessage(Parkour.getPrefix() + "Invalid material: " + args[2].toUpperCase());
            TranslationUtils.sendValueTranslation("Error.UnknownMaterial", args[2].toUpperCase(), sender);
            return;
        }

        if (!Validation.isPositiveInteger(args[3])) {
            sender.sendMessage(Parkour.getPrefix() + "Amount needs to be numeric.");
            return;
        }

        if (args.length == 5 && !Validation.isPositiveInteger(args[4])) {
            sender.sendMessage(Parkour.getPrefix() + "Invalid Material Data");
        }

        int amount = MaterialUtils.parseItemStackAmount(args[3]);
        CourseInfo.addJoinItem(args[1], args[2], amount, args[4], false); //TODO unbreakable
        sender.sendMessage(Parkour.getPrefix() + "Join item for " + args[1] + " set to " + args[2] + " (" + amount + ")");
    }

    /**
     * Retrieve and display Leaderboard for a course
     * Can be specified direction using appropriate commands,
     * or the Leaderboard conversation can be started, to specify what you want to view based on the options
     *
     * @param args
     * @param player
     */
    public void getLeaderboards(String[] args, Player player) {
        if (!parkour.getPlayerManager().delayPlayer(player, 3, true)) {
            return;
        }

        if (args.length == 1) {
            new LeaderboardConversation(player).begin();
            return;
        }

        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], player);
            return;
        }

        int limit = 5;
        boolean personal = true;

        if (args.length >= 4) {
            if (args[3].equalsIgnoreCase("global")) {
                personal = false;
            } else if (!args[3].equalsIgnoreCase("personal")) {
                player.sendMessage(Parkour.getPrefix() + "Defaulting to 'personal', alternative option: 'global'");
            }
        }

        if (args.length >= 3) {
            if (!Validation.isPositiveInteger(args[2])) {
                player.sendMessage(Parkour.getPrefix() + "Amount of results needs to be numeric.");
                return;
            }
            limit = Integer.parseInt(args[2]);
        }

        if (personal) {
            Utils.displayLeaderboard(player, Parkour.getInstance().getDatabase()
                    .getTopPlayerCourseResults(player.getName(), args[1], limit), args[1]);
        } else {
            Utils.displayLeaderboard(player, Parkour.getInstance().getDatabase()
                    .getTopCourseResults(args[1], limit), args[1]);
        }
    }

    /**
     * Set the ParkourMode of a course
     * Starts a Conversation to set the mode of the course.
     *
     * @param args
     * @param player
     */
    public void setCourseMode(String[] args, Player player) {
        if (!courseExists(args[1])) {
            TranslationUtils.sendValueTranslation("Error.NoExist", args[1], player);
            return;
        }
        new ParkourModeConversation(player).withCourseName(args[1].toLowerCase()).begin();
    }

    /**
     * Retrieves the ParkourMode of a course
     * Will default to NONE if a mode hasn't been specified.
     *
     * @param courseName
     * @return ParkourMode
     */
    public ParkourMode getCourseMode(String courseName) {
        String mode = CourseInfo.getMode(courseName).toUpperCase();
        return ParkourMode.valueOf(mode);
    }
}

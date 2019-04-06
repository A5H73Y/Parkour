package me.A5H73Y.Parkour.Course;

import me.A5H73Y.Parkour.Conversation.CoursePrizeConversation;
import me.A5H73Y.Parkour.Conversation.LeaderboardConversation;
import me.A5H73Y.Parkour.Conversation.ParkourModeConversation;
import me.A5H73Y.Parkour.Enums.ParkourMode;
import me.A5H73Y.Parkour.Managers.ChallengeManager;
import me.A5H73Y.Parkour.Other.Validation;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.ParkourKit.ParkourKit;
import me.A5H73Y.Parkour.Player.ParkourSession;
import me.A5H73Y.Parkour.Player.PlayerInfo;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import me.A5H73Y.Parkour.Utilities.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CourseMethods {

    /**
     * Does course exist.
     * Check if a course exists on the server
     *
     * @param courseName
     * @return course found
     */
    public static boolean exist(String courseName) {
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

    /**
     * Retrieve a course based on its unique name.
     * Everything is populated from this method (all checkpoints & information).
     * This should only be used when necessary.
     *
     * @param courseName
     * @return Course
     */
    public static Course findByName(String courseName) {
        if (!exist(courseName)) {
            return null;
        }

        courseName = courseName.toLowerCase();

        Checkpoint checkpoint = CheckpointMethods.getNextCheckpoint(courseName, 0);
        Course course = null;

        if (CourseInfo.hasParkourKit(courseName)) {
            String name = CourseInfo.getParkourKit(courseName);
            ParkourKit kit = ParkourKit.getParkourKit(name);
            if (kit != null) {
                course = new Course(courseName, checkpoint, kit);
            }
        }

        if (course == null) {
            course = new Course(courseName, checkpoint);
        }

        return course;
    }

    /**
     * Retrieve a course based on its list index.
     * The ID will be based on where it is in the list of courses, meaning it can change.
     * Find the current ID using "/pa list courses"
     *
     * @param courseNumber
     * @return Course
     */
    public static Course findByNumber(int courseNumber) {
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
    public static Course findByPlayer(String playerName) {
        if (!PlayerMethods.isPlaying(playerName)) {
            return null;
        }

        return PlayerMethods.getPlaying().get(playerName).getCourse();
    }

    /**
     * Create a new Parkour course.
     * The start of the course is located in courses.yml as checkpoint '0'.
     * The world is assumed once the course is joined, so having 2 different checkpoints in 2 different worlds is not an option.
     * All course names are stored in a string list, which is also held in memory.
     * Course is entered into the database so leaderboards can be associated with it.
     *
     * @param args
     * @param player
     */
    public static void createCourse(String[] args, Player player) {
        if (!Validation.courseCreation(args, player)) {
            return;
        }

        String name = args[1].toLowerCase();
        Location location = player.getLocation();
        FileConfiguration courseData = Parkour.getParkourConfig().getCourseData();

        courseData.set(name + ".Creator", player.getName());
        courseData.set(name + ".Views", 0);
        courseData.set(name + ".Completed", 0);
        courseData.set(name + ".XP", 0);
        courseData.set(name + ".Points", 0);
        courseData.set(name + ".World", location.getWorld().getName());
        courseData.set(name + ".0.X", location.getBlockX() + 0.5);
        courseData.set(name + ".0.Y", location.getBlockY() + 0.5);
        courseData.set(name + ".0.Z", location.getBlockZ() + 0.5);
        courseData.set(name + ".0.Yaw", location.getYaw());
        courseData.set(name + ".0.Pitch", location.getPitch());

        List<String> courseList = CourseInfo.getAllCourses();
        courseList.add(name);
        Collections.sort(courseList);
        courseData.set("Courses", courseList);
        Parkour.getParkourConfig().saveCourses();

        PlayerInfo.setSelected(player, name);

        player.sendMessage(Utils.getTranslation("Parkour.Created").replace("%COURSE%", args[1]));
        DatabaseMethods.insertCourse(name, player.getName());
    }

    /**
     * Execute the joinCourse after a delay.
     *
     * @param player
     * @param courseName
     */
    public static void joinCourseButDelayed(Player player, String courseName, int delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Parkour.getPlugin(), () -> joinCourse(player, courseName), delay);
    }

    /**
     * Join Player to Course.
     * This method serves as validation for the player to join the course.
     * Once validated, the player then joins the course via the PlayerMethods class.
     *
     * @param player
     * @param courseName
     */
    public static void joinCourse(Player player, String courseName) {
        Course course;
        if (Validation.isPositiveInteger(courseName)) {
            course = findByNumber(Integer.parseInt(courseName));
        } else {
            course = findByName(courseName);
        }

        if (course == null) {
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
            return;
        }

        if (!Validation.courseJoining(player, course)) {
            return;
        }

        PlayerMethods.playerJoin(player, course);
    }

    /**
     * Delete a course.
     * Remove all information stored on the server about the course, including all references from the database.
     *
     * @param courseName
     * @param player
     */
    public static void deleteCourse(String courseName, Player player) {
        if (!exist(courseName)) {
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
            return;
        }

        CourseInfo.deleteCourse(courseName);
        player.sendMessage(Utils.getTranslation("Parkour.Delete").replace("%COURSE%", courseName));
    }

    /**
     * Displays a list to the player.
     * Available options: players, courses, ranks & lobbies
     *
     * @param args
     * @param sender
     */
    public static void displayList(String[] args, CommandSender sender) {
        if (args.length < 2) {
            sender.sendMessage(Utils.invalidSyntax("list", "(players / courses / ranks / lobbies)"));
            return;
        }

        if (args[1].equalsIgnoreCase("players")) {
            displayPlaying(sender);

        } else if (args[1].equalsIgnoreCase("courses")) {
            int page = (args.length == 3 && args[2] != null && Validation.isPositiveInteger(args[2]) ? Integer.parseInt(args[2]) : 1);
            displayCourses(sender, page);

        } else if (args[1].equalsIgnoreCase("ranks")) {
            if (sender instanceof Player && !Utils.hasPermission((Player) sender, "Parkour.Admin")) {
                return;
            }
            displayRanks(sender);

        } else if (args[1].equalsIgnoreCase("lobbies")) {
            if (sender instanceof Player && !Utils.hasPermission((Player) sender, "Parkour.Admin")) {
                return;
            }
            LobbyMethods.displayLobbies(sender);

        } else {
            sender.sendMessage(Utils.invalidSyntax("list", "(players / courses / ranks / lobbies)"));
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
    private static void displayPlaying(CommandSender sender) {
        if (PlayerMethods.getPlaying().size() == 0) {
            sender.sendMessage(Static.getParkourString() + "Nobody is playing Parkour!");
            return;
        }

        sender.sendMessage(Static.getParkourString() + PlayerMethods.getPlaying().size() + " players using Parkour: ");

        String playingTemplate = Utils.getTranslation("Parkour.Playing", false);
        for (Map.Entry<String, ParkourSession> entry : PlayerMethods.getPlaying().entrySet()) {
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
    private static void displayCourses(CommandSender sender, int page) {
        if (CourseInfo.getAllCourses().size() == 0) {
            sender.sendMessage(Static.getParkourString() + "There are no Parkour courses!");
            return;
        }

        if (page <= 0) {
            sender.sendMessage(Static.getParkourString() + "Please enter a valid page number.");
            return;
        }

        int results = 8;
        int fromIndex = (page - 1) * results;
        List<String> courseList = CourseInfo.getAllCourses();

        if (courseList.size() <= fromIndex) {
            sender.sendMessage(Static.getParkourString() + "This page doesn't exist.");
            return;
        }

        sender.sendMessage(Static.getParkourString() + courseList.size() + " courses available:");
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
    private static void displayRanks(CommandSender sender) {
        sender.sendMessage(Utils.getStandardHeading("Parkour Ranks"));
        ConfigurationSection section = Parkour.getParkourConfig().getUsersData().getConfigurationSection("ServerInfo.Levels");

        if (section == null) {
            sender.sendMessage(Static.getParkourString() + "No Ranks set.");
        } else {
            Set<String> levels = section.getKeys(false);
            List<Integer> orderedLevels = levels.stream().mapToInt(Integer::parseInt).sorted().boxed().collect(Collectors.toList());

            for (Integer level : orderedLevels) {
                String rank = Parkour.getParkourConfig().getUsersData().getString("ServerInfo.Levels." + level + ".Rank");
                if (rank != null) {
                    sender.sendMessage(Utils.getTranslation("Parkour.RankInfo", false)
                            .replace("%LEVEL%", level.toString())
                            .replace("%RANK%", Utils.colour(rank)));
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
    public static void selectCourse(String[] args, Player player) {
        if (!exist(args[1])) {
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }

        String courseName = args[1].trim().toLowerCase();
        player.sendMessage(Static.getParkourString() + "Now Editing: " + ChatColor.AQUA + args[1]);
        int pointcount = CourseInfo.getCheckpointAmount(courseName);
        player.sendMessage(Static.getParkourString() + "Checkpoints: " + ChatColor.AQUA + pointcount);
        PlayerInfo.setSelected(player, courseName);
    }

    /**
     * Finish editing a course.
     *
     * @param player
     */
    public static void deselectCourse(Player player) {
        if (PlayerInfo.hasSelected(player)) {
            PlayerInfo.setDelected(player);
            player.sendMessage(Static.getParkourString() + "Finished editing.");
        } else {
            player.sendMessage(Utils.getTranslation("Error.Selected"));
        }
    }

    /**
     * Set the course prize.
     * Starts a Prize conversation that allows you to configure what the player gets awarded with when the complete the course.
     * A course could have every type of Prize if configured so.
     *
     * @param args
     * @param player
     */
    public static void setPrize(String[] args, Player player) {
        if (!exist(args[1])) {
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }

        new CoursePrizeConversation(player).withCourseName(args[1].toLowerCase()).begin();
    }

    /**
     * Overwrite the start location of the course.
     *
     * @param player
     */
    public static void setStart(Player player) {
        String selected = PlayerInfo.getSelected(player);

        if (!CourseMethods.exist(selected)) {
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", selected));
            return;
        }

        selected = selected.toLowerCase();
        Parkour.getParkourConfig().getCourseData().set(selected + ".0.X", player.getLocation().getX());
        Parkour.getParkourConfig().getCourseData().set(selected + ".0.Y", player.getLocation().getY());
        Parkour.getParkourConfig().getCourseData().set(selected + ".0.Z", player.getLocation().getZ());
        Parkour.getParkourConfig().getCourseData().set(selected + ".0.Yaw", player.getLocation().getYaw());
        Parkour.getParkourConfig().getCourseData().set(selected + ".0.Pitch", player.getLocation().getPitch());
        Utils.logToFile(selected + " spawn was reset by " + player.getName());
        player.sendMessage(Static.getParkourString() + "Spawn for " + ChatColor.AQUA + selected + ChatColor.WHITE + " has been set to your position");
    }

    /**
     * Create an AutoStart for a course.
     * Create an AutoStart pressure plate at the player's location for the specified course.
     *
     * @param args
     * @param player
     */
    public static void setAutoStart(String[] args, Player player) {
        if (!exist(args[1])) {
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }

        Block block = player.getLocation().getBlock();
        String coordinates = block.getX() + "-" + block.getY() + "-" + block.getZ();

        if (Parkour.getParkourConfig().getCourseData().contains("CourseInfo.AutoStart." + coordinates)) {
            player.sendMessage(Static.getParkourString() + "There is already an AutoStart here!");
            return;
        }

        Parkour.getParkourConfig().getCourseData().set("CourseInfo.AutoStart." + coordinates, args[1].toLowerCase());
        Parkour.getParkourConfig().saveCourses();
        player.sendMessage(Static.getParkourString() + "AutoStart for " + args[1] + " created!");

        block.setType(XMaterial.STONE_PRESSURE_PLATE.parseMaterial());
        Block blockUnder = block.getRelative(BlockFace.DOWN);
        blockUnder.setType(Parkour.getSettings().getAutoStartMaterial());
    }

    /**
     * Find matching Course for AutoStart Location.
     * Triggered when the player walks on a pressure plate matching AutoStart.
     * Look for matching config entry for location.
     *
     * @param location
     * @return Course name
     */
    public static String getAutoStartCourse(Location location) {
        FileConfiguration config = Parkour.getParkourConfig().getCourseData();
        String coordinates = location.getBlockX() + "-" + location.getBlockY() + "-" + location.getBlockZ();

        ConfigurationSection entries = config.getConfigurationSection("CourseInfo.AutoStart");

        if (entries != null) {
            // Go through each entry to find matching coordinates, then return the course
            for (String entry : entries.getKeys(false)){
                if (entry.equals(coordinates)) {
                    return config.getString("CourseInfo.AutoStart." + entry);
                }
            }
        }

        return null;
    }

    /**
     * Overwrite the creator of the course.
     *
     * @param args
     * @param player
     */
    public static void setCreator(String[] args, Player player) {
        if (!CourseMethods.exist(args[1])) {
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }

        CourseInfo.setCreator(args[1], args[2]);
        player.sendMessage(Static.getParkourString() + "Creator of " + ChatColor.DARK_AQUA + args[1] + ChatColor.WHITE + " was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Set MaxDeaths for Course.
     * Set the maximum amount of deaths a player can accumulate before failing the course.
     *
     * @param args
     * @param sender
     */
    public static void setMaxDeaths(String[] args, CommandSender sender) {
        if (!CourseMethods.exist(args[1])) {
            sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }

        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Static.getParkourString() + "Amount of deaths is not valid.");
            return;
        }

        CourseInfo.setMaximumDeaths(args[1], Integer.parseInt(args[2]));
        sender.sendMessage(Static.getParkourString() + ChatColor.AQUA + args[1] + ChatColor.WHITE + " maximum deaths was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Set MaxTime for Course.
     * Set the maximum amount of deaths a player can accumulate before failing the course.
     *
     * @param args
     * @param sender
     */
    public static void setMaxTime(String[] args, CommandSender sender) {
        if (!Parkour.getPlugin().getConfig().getBoolean("OnCourse.DisplayLiveTime")) {
            sender.sendMessage(Static.getParkourString() + "Live Time is disabled!");
            return;
        }

        if (!CourseMethods.exist(args[1])) {
            sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }

        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Static.getParkourString() + "Amount of seconds is not valid.");
            return;
        }

        Integer seconds = Integer.parseInt(args[2]);
        CourseInfo.setMaximumTime(args[1], seconds);
        sender.sendMessage(Static.getParkourString() + ChatColor.AQUA + args[1] + ChatColor.WHITE + " maximum time limit was set to " + ChatColor.AQUA + Utils.convertSecondsToTime(seconds));
    }

    /**
     * Set the mimimum Parkour level required to join the course.
     *
     * @param args
     * @param sender
     */
    public static void setMinLevel(String[] args, CommandSender sender) {
        if (!CourseMethods.exist(args[1])) {
            sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }
        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Static.getParkourString() + "Minimum level is not valid.");
            return;
        }

        CourseInfo.setMinimumLevel(args[1], Integer.parseInt(args[2]));
        sender.sendMessage(Static.getParkourString() + ChatColor.AQUA + args[1] + ChatColor.WHITE + " minimum level requirement was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Set Course's ParkourLevel reward.
     * Reward the player with the Parkour level on course completion.
     *
     * @param args
     * @param sender
     */
    public static void setRewardParkourLevel(String[] args, CommandSender sender) {
        if (!CourseMethods.exist(args[1])) {
            sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }
        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Static.getParkourString() + "Reward level needs to be numeric.");
            return;
        }

        CourseInfo.setRewardLevel(args[1], Integer.parseInt(args[2]));
        sender.sendMessage(Static.getParkourString() + args[1] + "'s reward level was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Set Course's ParkourLevel reward addition.
     * Increment the player's ParkourLevel by this value on course completion.
     *
     * @param args
     * @param sender
     */
    public static void setRewardParkourLevelAddition(String[] args, CommandSender sender) {
        if (!CourseMethods.exist(args[1])) {
            sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }
        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Static.getParkourString() + "Reward level addition needs to be numeric.");
            return;
        }

        CourseInfo.setRewardLevelAdd(args[1], args[2]);
        sender.sendMessage(Static.getParkourString() + args[1] + "'s reward level addition was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Set RewardOnce status of Course.
     * Set whether the player only gets the prize for the first time they complete the course.
     *
     * @param args
     * @param sender
     */
    public static void setRewardOnce(String[] args, CommandSender sender) {
        if (!CourseMethods.exist(args[1])) {
            sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }

        boolean enabled = CourseInfo.getRewardOnce(args[1]);
        CourseInfo.setRewardOnce(args[1], !enabled);
        sender.sendMessage(Static.getParkourString() + args[1] + "'s reward one time was set to " + ChatColor.AQUA + enabled);
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
    public static void setRewardParkourRank(String[] args, CommandSender sender) {
        if (!Validation.isPositiveInteger(args[1])) {
            sender.sendMessage(Static.getParkourString() + "ParkourLevel needs to be numeric.");
            return;
        }

        if (!Validation.isStringValid(args[2])) {
            sender.sendMessage(Static.getParkourString() + "ParkourRank is not valid.");
            return;
        }

        CourseInfo.setRewardRank(Integer.valueOf(args[1]), args[2]);
        sender.sendMessage(Static.getParkourString() + "ParkourRank for ParkourLevel " + args[1] + " was set to " + Utils.colour(args[2]));
    }

    /**
     * Set Reward delay for course.
     * Set the number of days that have to elapse before the prize can be won again by the same player.
     *
     * @param args
     * @param sender
     */
    public static void setRewardDelay(String[] args, CommandSender sender) {
        if (!CourseMethods.exist(args[1])) {
            sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }
        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Static.getParkourString() + "Reward delay needs to be numeric.");
            return;
        }

        CourseInfo.setRewardDelay(args[1], Integer.parseInt(args[2]));
        sender.sendMessage(Static.getParkourString() + args[1] + "'s reward delay was set to " + args[2] + " day(s).");
    }

    /**
     * Reward the player with Parkoins on course completions.
     * Parkoins are then spent in the store for unlockables.
     *
     * @param args
     * @param sender
     */
    public static void setRewardParkoins(String[] args, CommandSender sender) {
        if (!CourseMethods.exist(args[1])) {
            sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }
        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Static.getParkourString() + "Parkoins reward needs to be numeric.");
            return;
        }

        CourseInfo.setRewardParkoins(args[1], Integer.parseInt(args[2]));
        sender.sendMessage(Static.getParkourString() + args[1] + "'s Parkoins reward was set to " + ChatColor.AQUA + args[2]);
    }

    /**
     * Toggle the Course's completion status.
     * If a course argument is supplied it will use this, otherwise it will use the player's selected course.
     * A player will not be able to join a course if it's not set to finished.
     *
     * @param args
     * @param player
     */
    public static void setCompletionStatus(String[] args, Player player) {
        String courseName = args.length > 1 ? args[1].toLowerCase() : PlayerInfo.getSelected(player);

        if (!Validation.isStringValid(courseName)) {
            player.sendMessage(Static.getParkourString() + "Please select a course, or provide a course argument");
            return;
        }

        if (!Utils.hasPermissionOrCourseOwnership(player, "Parkour.Admin", "Course", courseName)) {
            return;
        }

        boolean finished = CourseInfo.getFinished(courseName);
        CourseInfo.setFinished(courseName, !finished);

        if (finished) {
            player.sendMessage(Static.getParkourString() + ChatColor.AQUA + courseName + ChatColor.WHITE + " has been set to unfinished!");
            Utils.logToFile(courseName + " was set to unfinished by " + player.getName());
        } else {
            player.sendMessage(Utils.getTranslation("Parkour.Finish").replace("%COURSE%", courseName));
            Utils.logToFile(courseName + " was set to finished by " + player.getName());
        }
    }

    /**
     * Link a course to a lobby or another course on completion.
     *
     * @param args
     * @param player
     */
    public static void linkCourse(String[] args, Player player) {
        if (args.length < 2) {
            player.sendMessage(Utils.invalidSyntax("Link", "(course / lobby / reset) (courseName / lobbyName)"));
            return;
        }

        String selected = PlayerInfo.getSelected(player);

        if (args.length >= 3 && args[1].equalsIgnoreCase("course")) {
            if (!CourseMethods.exist(args[2])) {
                player.sendMessage(Utils.getTranslation("Error.Unknown"));
                return;
            }

            if (CourseInfo.hasLinkedLobby(selected)) {
                player.sendMessage(Static.getParkourString() + "This course is linked to a lobby!");
                return;
            }

            CourseInfo.setLinkedCourse(selected, args[2]);
            player.sendMessage(Static.getParkourString() + ChatColor.DARK_AQUA + selected + ChatColor.WHITE + " is now linked to " + ChatColor.AQUA + args[2]);

        } else if (args.length >= 3 && args[1].equalsIgnoreCase("lobby")) {
            if (!Parkour.getPlugin().getConfig().contains("Lobby." + args[2] + ".World")) { // TODO
                player.sendMessage(Static.getParkourString() + "Lobby " + args[2] + " does not exist.");
                return;
            }

            if (CourseInfo.hasLinkedCourse(selected)) {
                player.sendMessage(Static.getParkourString() + "This course is linked to a course!");
                return;
            }

            CourseInfo.setLinkedLobby(selected, args[2]);
            player.sendMessage(Static.getParkourString() + ChatColor.DARK_AQUA + selected + ChatColor.WHITE + " is now linked to " + ChatColor.AQUA + args[2]);

        } else if (args[1].equalsIgnoreCase("reset")) {
            CourseInfo.resetLinks(selected);
            player.sendMessage(Static.getParkourString() + ChatColor.DARK_AQUA + selected + ChatColor.WHITE + " is no longer linked.");

        } else {
            player.sendMessage(Utils.invalidSyntax("Link", "(course / lobby / reset) (courseName / lobbyName)"));
        }
    }

    /**
     * Resets all the statistics to 0 as if the course was just created.
     * The structure of the course remains untouched.
     * All course times will be deleted from the database.
     *
     * @param courseName
     */
    public static void resetCourse(String courseName) {
        if (!exist(courseName)) {
            return;
        }

        courseName = courseName.toLowerCase();
        FileConfiguration config = Parkour.getParkourConfig().getCourseData();

        config.set(courseName + ".Views", 0);
        config.set(courseName + ".Completed", 0);
        config.set(courseName + ".Finished", false);
        config.set(courseName + ".XP", null);
        config.set(courseName + ".Level", null);
        config.set(courseName + ".MinimumLevel", null);
        config.set(courseName + ".LevelAdd", null);
        config.set(courseName + ".MaxDeaths", null);
        config.set(courseName + ".Parkoins", null);
        config.set(courseName + ".LinkedLobby", null);
        config.set(courseName + ".LinkedCourse", null);
        config.set(courseName + ".ParkourKit", null);
        config.set(courseName + ".Mode", null);
        Parkour.getParkourConfig().saveCourses();
        DatabaseMethods.deleteCourseTimes(courseName);
    }

    /**
     * Rate the course.
     * After completion of a course, the player has the ability to vote whether they liked the course or not.
     * These are only used for statistics.
     *
     * @param args
     * @param player
     */
    public static void rateCourse(String[] args, Player player) {
        String courseName;

        if (args.length > 1) {
            courseName = args[1].toLowerCase();
        } else {
            courseName = PlayerInfo.getLastCompletedCourse(player);
        }

        if (!CourseMethods.exist(courseName)) {
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
            return;
        }

        if (!DatabaseMethods.hasPlayerCompleted(player.getName(), courseName)) {
            player.sendMessage(Utils.getTranslation("Error.NotCompleted").replace("%COURSE%", courseName));
            return;
        }

        if (DatabaseMethods.hasVoted(courseName, player.getName())) {
            player.sendMessage(Utils.getTranslation("Error.AlreadyVoted").replace("%COURSE%", courseName));
            return;
        }

        boolean liked = args[0].equalsIgnoreCase("like");
        DatabaseMethods.insertVote(courseName, player.getName(), liked);

        if (liked) {
            player.sendMessage(Static.getParkourString() + "You " + ChatColor.GREEN + "liked " + ChatColor.WHITE + courseName);
        } else {
            player.sendMessage(Static.getParkourString() + "You " + ChatColor.RED + "disliked " + ChatColor.WHITE + courseName);
        }
    }

    /**
     * Link ParkourKit to a course.
     * The name of the ParkourKit must be specified, then will be applied when a player joins the course.
     *
     * @param args
     * @param player
     */
    public static void linkParkourKit(String[] args, Player player) {
        if (!CourseMethods.exist(args[1])) {
            player.sendMessage(Utils.getTranslation("Error.Unknown"));
            return;
        }
        if (!Parkour.getParkourConfig().getParkourKitData().contains("ParkourKit." + args[2].toLowerCase())) {
            player.sendMessage(Static.getParkourString() + "ParkourKit doesn't exist!");
            return;
        }
        if (CourseInfo.hasParkourKit(args[1])) {
            player.sendMessage(Static.getParkourString() + "This course is already linked to a ParkourKit, continuing anyway...");
        }

        CourseInfo.setParkourKit(args[1], args[2]);
        player.sendMessage(Static.getParkourString() + args[1] + " is now linked to ParkourKit " + args[2]);
    }

    /**
     * Challenge a player to a Course.
     * Invites a player to challenge them in a course, must be confirmed by the recipient before the process initiates.
     *
     * @param args
     * @param player
     */
    public static void challengePlayer(String[] args, Player player) {
        if (args.length < 3) {
            player.sendMessage(Utils.invalidSyntax("challenge", "(player) (course) [wager]"));
            return;
        }

        if (!Validation.challengePlayer(args, player)) {
            return;
        }

        if (!Utils.delayPlayer(player, 10, true)) {
            return;
        }

        String wagerString = "";
        Double wager = null;

        if (args.length == 4 && Static.getEconomy() && Validation.isPositiveDouble(args[3])) {
            String currencyName = Parkour.getEconomy().currencyNamePlural() == null ?
                    "" : " " + Parkour.getEconomy().currencyNamePlural();
            wager = Double.valueOf(args[3]);
            wagerString = Utils.getTranslation("Parkour.Challenge.Wager", false)
                    .replace("%AMOUNT%", wager + currencyName);
        }

        Player target = Bukkit.getPlayer(args[2]);
        String courseName = args[1].toLowerCase();

        target.sendMessage(Utils.getTranslation("Parkour.Challenge.Receive")
                .replace("%PLAYER%", player.getName())
                .replace("%COURSE%", courseName) + wagerString);
        target.sendMessage(Utils.getTranslation("Parkour.Accept", false));
        player.sendMessage(Utils.getTranslation("Parkour.Challenge.Send")
                .replace("%PLAYER%", target.getName())
                .replace("%COURSE%", courseName) + wagerString);
        ChallengeManager.getInstance().createChallenge(player.getName(), target.getName(), courseName, wager);
    }

    /**
     * Set JoinItem for a course.
     * Specify the material and the amount for the course, which will then be given to the player once they join.
     *
     * @param args
     * @param sender
     */
    public static void setJoinItem(String[] args, CommandSender sender) {
        if (!CourseMethods.exist(args[1])) {
            sender.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }
        if (Utils.lookupMaterial(args[2].toUpperCase()) == null) {
            sender.sendMessage(Static.getParkourString() + "Invalid material: " + args[2].toUpperCase());
            return;
        }
        if (!Validation.isPositiveInteger(args[3])) {
            sender.sendMessage(Static.getParkourString() + "Amount needs to be numeric.");
            return;
        }

        int amount = Utils.parseMaterialAmount(args[3]);

        CourseInfo.setJoinItem(args[1], args[2], amount);
        sender.sendMessage(Static.getParkourString() + "Join item for " + args[1] + " set to " + args[2] + " (" + amount + ")");
    }

    /**
     * Retrieve and display Leaderboard for a course
     * Can be specified direction using appropriate commands,
     * or the Leaderboard conversation can be started, to specify what you want to view based on the options
     *
     * @param args
     * @param player
     */
    public static void getLeaderboards(String[] args, Player player) {
        if (!Utils.delayPlayer(player, 3, true)) {
            return;
        }

        if (args.length == 1) {
            new LeaderboardConversation(player).begin();
            return;
        }
        if (!CourseMethods.exist(args[1])) {
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }

        int limit = 5;
        boolean personal = true;

        if (args.length >= 4) {
            if (args[3].equalsIgnoreCase("global")) {
                personal = false;
            } else if (!args[3].equalsIgnoreCase("personal")) {
                player.sendMessage(Static.getParkourString() + "Defaulting to 'personal', alternative option: 'global'");
            }
        }

        if (args.length >= 3) {
            if (!Validation.isPositiveInteger(args[2])) {
                player.sendMessage(Static.getParkourString() + "Amount of results needs to be numeric.");
                return;
            }
            limit = Integer.parseInt(args[2]);
        }

        if (personal) {
            Utils.displayLeaderboard(player, DatabaseMethods.getTopPlayerCourseResults(player.getName(), args[1], limit), args[1]);
        } else {
            Utils.displayLeaderboard(player, DatabaseMethods.getTopCourseResults(args[1], limit), args[1]);
        }
    }

    /**
     * Set the ParkourMode of a course
     * Starts a Conversation to set the mode of the course.
     *
     * @param args
     * @param player
     */
    public static void setCourseMode(String[] args, Player player) {
        if (!CourseMethods.exist(args[1])) {
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
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
    public static ParkourMode getCourseMode(String courseName) {
        String mode = CourseInfo.getMode(courseName).toUpperCase();
        return ParkourMode.valueOf(mode);
    }
}
package me.A5H73Y.Parkour.Course;

import me.A5H73Y.Parkour.Other.Constants;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class CourseInfo {

    /**
     * Get list of all Parkour course names.
     * @return List Parkour course names
     */
    public static List<String> getAllCourses() {
        return Parkour.getParkourConfig().getCourseData().getStringList("Courses");
    }

    /**
     * Return the linked Course of the specified course.
     * Will validate it's a valid course before returning
     * @param courseName
     * @return linkedCourse
     */
    public static String getLinkedCourse(String courseName) {
        String linkedCourse = Parkour.getParkourConfig().getCourseData()
                .getString(courseName.toLowerCase() + ".LinkedCourse");

        if (CourseMethods.exist(linkedCourse)) {
            return linkedCourse;
        }

        return null;
    }

    /**
     * Check if Course is linked to another Course.
     * @param courseName
     * @return if linked course is found
     */
    public static boolean hasLinkedCourse(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".LinkedCourse");
    }

    /**
     * Set the Course to link to another Course.
     * @param courseName
     * @param linkedCourse
     */
    public static void setLinkedCourse(String courseName, String linkedCourse) {
        Parkour.getParkourConfig().getCourseData()
                .set(courseName.toLowerCase() + ".LinkedCourse", linkedCourse.toLowerCase());
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Return the linked lobby of the Course.
     * Will validate it's a valid lobby before returning
     * @param courseName
     * @return linkedLobby
     */
    public static String getLinkedLobby(String courseName) {
        String linkedLobby = Parkour.getParkourConfig().getCourseData()
                .getString(courseName.toLowerCase() + ".LinkedLobby");

        if (linkedLobby != null && LobbyMethods.getCustomLobbies().contains(linkedLobby)) {
            return linkedLobby;
        }

        return null;
    }

    /**
     * Does this course have a linked lobby
     * @param courseName
     * @return if linked lobby is found
     */
    public static boolean hasLinkedLobby(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".LinkedLobby");
    }

    /**
     * Set the course to link to a lobby
     * @param courseName
     * @param lobbyName
     */
    public static void setLinkedLobby(String courseName, String lobbyName) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".LinkedLobby", lobbyName);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get the Mode for the course
     * @param courseName
     * @return mode
     */
    public static String getMode(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".Mode", "NONE");
    }

    /**
     * Set ParkourMode of a course
     * @param courseName
     * @param mode
     */
    public static void setMode(String courseName, String mode) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Mode", mode);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get amount of checkpoints on a course
     * @param courseName
     * @return
     */
    public static int getCheckpointAmount(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Points");
    }

    /**
     * Get creator of course
     * @param courseName
     * @return
     */
    public static String getCreator(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".Creator");
    }

    /**
     * Set creator of course
     * @param courseName
     * @param playerName
     */
    public static void setCreator(String courseName, String playerName) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Creator", playerName);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get minimum level required to join course
     * @param courseName
     * @return
     */
    public static int getMinimumLevel(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".MinimumLevel");
    }

    /**
     * Set minimum level required to join course
     * @param courseName
     * @param level
     */
    public static void setMinimumLevel(String courseName, int level) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".MinimumLevel", level);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get ParkourKit set for the course
     * @param courseName
     * @return
     */
    public static String getParkourKit(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".ParkourKit", Constants.DEFAULT);
    }

    /**
     * Does this course have a ParkourKit set
     * @param courseName
     * @return
     */
    public static boolean hasParkourKit(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".ParkourKit");
    }

    /**
     * Set ParkourKit for this course
     * @param courseName
     * @param parkourKitName
     */
    public static void setParkourKit(String courseName, String parkourKitName) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".ParkourKit", parkourKitName.toLowerCase());
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get the maximum deaths for course
     * @param courseName
     * @return death count
     */
    public static int getMaximumDeaths(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".MaxDeaths", 0);
    }

    /**
     * Get the maximum time limit for course
     * @param courseName
     * @return seconds
     */
    public static int getMaximumTime(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".MaxTime", 0);
    }

    /**
     * Set the maximum deaths for course
     * @param courseName
     * @param amount
     */
    public static void setMaximumDeaths(String courseName, int amount) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".MaxDeaths", amount);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Set the maximum time limit for course
     * @param courseName
     * @param seconds
     */
    public static void setMaximumTime(String courseName, int seconds) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".MaxTime", seconds);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Returns whether the course is ready or not.
     * @param courseName
     * @return boolean
     */
    public static boolean getFinished(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getBoolean(courseName.toLowerCase() + ".Finished");
    }

    /**
     * Set the finish status of the course
     * @param courseName
     * @param finished
     */
    public static void setFinished(String courseName, boolean finished) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Finished", finished);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get the prize commands for the course
     * @param courseName
     * @return
     */
    public static List<String> getCommandsPrize(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getStringList(courseName.toLowerCase() + ".Prize.CMD");
    }

    /**
     * Does this course have prize commands
     * @param courseName
     * @return
     */
    public static boolean hasCommandPrize(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".Prize.CMD");
    }

    /**
     * Add a command to the list of prize commands for completing course
     * @param courseName
     * @param command
     */
    public static void addCommandPrize(String courseName, String command) {
        List<String> commands = getCommandsPrize(courseName);
        commands.add(command);

        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Prize.CMD", commands);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Set the prize material and amount for completing course
     * @param courseName
     * @param material
     * @param amount
     */
    public static void setMaterialPrize(String courseName, String material, int amount) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Prize.Material", material);
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Prize.Amount", amount);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get the amount of XP for course
     * @param courseName
     * @return
     */
    public static int getXPPrize(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Prize.XP");
    }

    /**
     * Set the amount of XP for completing course
     * @param courseName
     * @param amount
     */
    public static void setXPPrize(String courseName, int amount) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Prize.XP", amount);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Increase the Complete count of the course
     * @param courseName
     */
    public static void increaseComplete(String courseName) {
        int completed = Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Completed");
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Completed", completed + 1);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Increase the amount of views of the course
     * @param courseName
     */
    public static void increaseView(String courseName) {
        int views = Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Views");
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Views", views + 1);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get the reward level for course
     * @param courseName
     * @return
     */
    public static int getRewardLevel(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Level");
    }

    /**
     * Set the reward level for course
     * @param courseName
     * @param level
     */
    public static void setRewardLevel(String courseName, int level) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Level", level);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get the reward level increase for course
     * @param courseName
     * @return
     */
    public static int getRewardLevelAdd(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".LevelAdd");
    }

    /**
     * Set the reward level increase for course
     * @param courseName
     * @param amount
     */
    public static void setRewardLevelAdd(String courseName, String amount) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".LevelAdd", Integer.parseInt(amount));
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get the reward once status for course
     * @param courseName
     * @return
     */
    public static boolean getRewardOnce(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getBoolean(courseName.toLowerCase() + ".RewardOnce");
    }

    /**
     * Set the reward once status for course
     * @param courseName
     * @param enabled
     */
    public static void setRewardOnce(String courseName, boolean enabled) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".RewardOnce", enabled);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get RewardRank for a ParkourLevel
     * @param parkourLevel
     * @return
     */
    public static String getRewardRank(int parkourLevel) {
        return Parkour.getParkourConfig().getUsersData().getString("ServerInfo.Levels." + parkourLevel + ".Rank");
    }

    /**
     * Set ParkourRank for a ParkourLevel
     * @param parkourLevel
     * @param rank
     */
    public static void setRewardRank(int parkourLevel, String rank) {
        Parkour.getParkourConfig().getUsersData().set("ServerInfo.Levels." + parkourLevel + ".Rank", rank);
        Parkour.getParkourConfig().saveUsers();
    }

    public static boolean hasRewardDelay(String courseName) {
        return getRewardDelay(courseName) > 0;
    }

    public static int getRewardDelay(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".RewardDelay", 0);
    }

    public static void setRewardDelay(String courseName, int rewardDelay) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".RewardDelay", rewardDelay);
        Parkour.getParkourConfig().saveCourses();
    }

    public static int getRewardParkoins(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Parkoins");
    }

    public static void setRewardParkoins(String courseName, int parkoins) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Parkoins", parkoins);
        Parkour.getParkourConfig().saveCourses();
    }

    public static Material getJoinItem(String courseName) {
        return Utils.lookupMaterial(Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".JoinItemMaterial"));
    }

    public static int getJoinItemAmount(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".JoinItemAmount", 1);
    }

    public static String getJoinItemLabel(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".JoinItemLabel");
    }

    public static boolean hasJoinItem(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".JoinItemMaterial");
    }

    public static void setJoinItem(String courseName, String material, int amount) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".JoinItemMaterial", material.toUpperCase());
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".JoinItemAmount", amount);
        Parkour.getParkourConfig().saveCourses();
    }


    public static int getEconomyFinishReward(String courseName) {
        return Parkour.getParkourConfig().getEconData().getInt("Price." + courseName.toLowerCase() + ".Finish");
    }

    public static int getEconomyJoiningFee(String courseName) {
        return Parkour.getParkourConfig().getEconData().getInt("Price." + courseName.toLowerCase() + ".JoinFee");
    }

    public static String getWorld(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".World");
    }

    public static Material getMaterialPrize(String courseName) {
        return Utils.lookupMaterial(Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".Prize.Material"));
    }

    public static int getMaterialPrizeAmount(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Prize.Amount", 0);
    }

    public static boolean hasMaterialPrize(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".Prize.Material");
    }

    public static void resetLinks(String courseName) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".LinkedLobby", null);
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".LinkedCourse", null);
        Parkour.getParkourConfig().saveCourses();
    }

    public static void deleteCourse(String courseName) {
        courseName = courseName.toLowerCase();

        List<String> courseList = getAllCourses();
        courseList.remove(courseName);
        Parkour.getParkourConfig().getCourseData().set(courseName, null);
        Parkour.getParkourConfig().getCourseData().set("Courses", courseList);
        Parkour.getParkourConfig().saveCourses();
        DatabaseMethods.deleteCourseAndReferences(courseName);

        if (Parkour.getPlugin().getConfig().getBoolean("OnFinish.SaveUserCompletedCourses")) {
            FileConfiguration userConfig = Parkour.getParkourConfig().getUsersData();
            ConfigurationSection playersSection = userConfig.getConfigurationSection("PlayerInfo");

            if (playersSection != null) {
                Set<String> playerNames = playersSection.getKeys(false);

                for (String playerName : playerNames) {
                	String completedPath = "PlayerInfo." + playerName + ".Completed";
                    List<String> completedCourses = userConfig.getStringList(completedPath);

                    if (completedCourses.contains(completedPath)) {
	                    completedCourses.remove(courseName);
	                    userConfig.set(completedPath, completedCourses);
                    }
                }
            }
            Parkour.getParkourConfig().saveUsers();
        }
    }

    public static void resetPrizes(String courseName) {
        courseName = courseName.toLowerCase();

        Parkour.getParkourConfig().getCourseData().set(courseName + ".Prize", null);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Displays all the information stored about a course.
     * Accessed via "/pa stats (course)", will only display applicable information.
     * Yeah, it's super ugly.
     *
     * @param courseName
     * @param player
     */
    public static void displayCourseInfo(String courseName, Player player) {
        if (!CourseMethods.exist(courseName)) {
            player.sendMessage(Utils.getTranslation("Error.Unknown"));
            return;
        }

        courseName = courseName.toLowerCase();
        FileConfiguration config = Parkour.getParkourConfig().getCourseData();
        ChatColor aqua = ChatColor.AQUA;

        int views = config.getInt(courseName + ".Views");
        int completed = config.getInt(courseName + ".Completed");
        int checkpoints = config.getInt(courseName + ".Points");
        int maxDeaths = config.getInt(courseName + ".MaxDeaths");
        int maxTime = config.getInt(courseName + ".MaxTime");
        int minLevel = config.getInt(courseName + ".MinimumLevel");
        int rewardLevel = config.getInt(courseName + ".Level");
        int rewardLevelAdd = config.getInt(courseName + ".LevelAdd");
        int XP = config.getInt(courseName + ".XP");
        int parkoins = config.getInt(courseName + ".Parkoins");

        String linkedLobby = config.getString(courseName + ".LinkedLobby");
        String linkedCourse = config.getString(courseName + ".LinkedCourse");
        String creator = config.getString(courseName + ".Creator");
        boolean finished = config.getBoolean(courseName + ".Finished");
        String parkourKit = config.getString(courseName + ".ParkourKit");
        String mode = config.getString(courseName + ".Mode");

        double completePercent = Math.round((completed * 1.0 / views) * 100);

        player.sendMessage(Utils.getStandardHeading(Utils.standardizeText(courseName) + " statistics"));

        player.sendMessage("Views: " + aqua + views);
        player.sendMessage("Completed: " + aqua + completed + " times (" + completePercent + "%)");
        player.sendMessage("Checkpoints: " + aqua + checkpoints);
        player.sendMessage("Creator: " + aqua + creator);
        player.sendMessage("Finished: " + aqua + finished);

        if (minLevel > 0) {
            player.sendMessage("Level Required: " + aqua + minLevel);
        }

        if (rewardLevel > 0) {
            player.sendMessage("Level Reward: " + aqua + rewardLevel);
        }

        if (rewardLevelAdd > 0) {
            player.sendMessage("Level Reward Addon: " + aqua + rewardLevelAdd);
        }

        if (linkedLobby != null && linkedLobby.length() > 0) {
            player.sendMessage("Linked Lobby: " + aqua + linkedLobby);
        }

        if (linkedCourse != null && linkedCourse.length() > 0) {
            player.sendMessage("Linked Course: " + aqua + linkedCourse);
        }

        if (XP > 0) {
            player.sendMessage("XP Reward: " + aqua + XP);
        }

        if (parkoins > 0) {
            player.sendMessage("Parkoins Reward: " + aqua + parkoins);
        }

        if (parkourKit != null && parkourKit.length() > 0) {
            player.sendMessage("ParkourKit: " + aqua + parkourKit);
        }

        if (mode != null && !"none".equalsIgnoreCase(mode)) {
            player.sendMessage("ParkourMode: " + aqua + mode);
        }

        if (maxDeaths > 0) {
            player.sendMessage("Max Deaths: " + aqua + maxDeaths);
        }

        if (maxTime > 0) {
            player.sendMessage("Time Limit: " + aqua + Utils.convertSecondsToTime(maxTime));
        }

        if (Static.getEconomy()) {
            int joinFee = Parkour.getParkourConfig().getEconData()
                    .getInt("Price." + courseName + ".JoinFee");

            if (joinFee > 0) {
                player.sendMessage("Join Fee: " + aqua + joinFee);
            }
        }

        double likePercent = Math.round(DatabaseMethods.getVotePercent(courseName));

        if (likePercent > 0) {
            player.sendMessage("Liked: " + aqua + likePercent + "%");
        }

        if (hasRewardDelay(courseName) && Parkour.getSettings().isDisplayPrizeCooldown()) {
        	player.sendMessage("Reward Cooldown (days): " + aqua + getRewardDelay(courseName));
        	if (!Utils.hasPrizeCooldownDurationPassed(player, courseName, false)) {
        		player.sendMessage("Cooldown Remaining: " + aqua + Utils.getTimeRemaining(player, courseName));
        	}
        }
    }
}

package io.github.a5h73y.parkour.course;

import java.util.List;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.config.ParkourConfiguration;
import io.github.a5h73y.parkour.other.Constants;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.utilities.Utils;
import io.github.a5h73y.parkour.player.PlayerInfo;
import io.github.a5h73y.parkour.utilities.Static;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CourseInfo {

    public static ParkourConfiguration getConfig() {
        return Parkour.getConfig(ConfigType.COURSES);
    }

    /**
     * Get list of all Parkour course names.
     *
     * @return List Parkour course names
     */
    public static List<String> getAllCourses() {
        return getConfig().getStringList("Courses");
    }

    /**
     * Return the linked Course of the specified course.
     * Will validate it's a valid course before returning
     *
     * @param courseName
     * @return linkedCourse
     */
    public static String getLinkedCourse(String courseName) {
        String linkedCourse = getConfig().getString(courseName.toLowerCase() + ".LinkedCourse");

        if (CourseMethods.exist(linkedCourse)) {
            return linkedCourse;
        }

        return null;
    }

    /**
     * Check if Course is linked to another Course.
     *
     * @param courseName
     * @return if linked course is found
     */
    public static boolean hasLinkedCourse(String courseName) {
        return getConfig().contains(courseName.toLowerCase() + ".LinkedCourse");
    }

    /**
     * Set the Course to link to another Course.
     *
     * @param courseName
     * @param linkedCourse
     */
    public static void setLinkedCourse(String courseName, String linkedCourse) {
        getConfig().set(courseName.toLowerCase() + ".LinkedCourse", linkedCourse.toLowerCase());
        getConfig().save();
    }

    /**
     * Return the linked lobby of the Course.
     * Will validate it's a valid lobby before returning
     *
     * @param courseName
     * @return linkedLobby
     */
    public static String getLinkedLobby(String courseName) {
        String linkedLobby = getConfig().getString(courseName.toLowerCase() + ".LinkedLobby");

        if (linkedLobby != null && LobbyMethods.getCustomLobbies().contains(linkedLobby)) {
            return linkedLobby;
        }

        return null;
    }

    /**
     * Does this course have a linked lobby
     *
     * @param courseName
     * @return if linked lobby is found
     */
    public static boolean hasLinkedLobby(String courseName) {
        return getConfig().contains(courseName.toLowerCase() + ".LinkedLobby");
    }

    /**
     * Set the course to link to a lobby
     *
     * @param courseName
     * @param lobbyName
     */
    public static void setLinkedLobby(String courseName, String lobbyName) {
        getConfig().set(courseName.toLowerCase() + ".LinkedLobby", lobbyName);
        getConfig().save();
    }

    /**
     * Get the Mode for the course
     *
     * @param courseName
     * @return mode
     */
    public static String getMode(String courseName) {
        return getConfig().getString(courseName.toLowerCase() + ".Mode", "NONE");
    }

    /**
     * Set ParkourMode of a course
     *
     * @param courseName
     * @param mode
     */
    public static void setMode(String courseName, String mode) {
        getConfig().set(courseName.toLowerCase() + ".Mode", mode);
        getConfig().save();
    }

    /**
     * Get amount of checkpoints on a course
     *
     * @param courseName
     * @return
     */
    public static int getCheckpointAmount(String courseName) {
        return getConfig().getInt(courseName.toLowerCase() + ".Points");
    }

    /**
     * Get creator of course
     *
     * @param courseName
     * @return
     */
    public static String getCreator(String courseName) {
        return getConfig().getString(courseName.toLowerCase() + ".Creator");
    }

    /**
     * Set creator of course
     *
     * @param courseName
     * @param playerName
     */
    public static void setCreator(String courseName, String playerName) {
        getConfig().set(courseName.toLowerCase() + ".Creator", playerName);
        getConfig().save();
    }

    /**
     * Get minimum level required to join course
     *
     * @param courseName
     * @return
     */
    public static int getMinimumLevel(String courseName) {
        return getConfig().getInt(courseName.toLowerCase() + ".MinimumLevel");
    }

    /**
     * Set minimum level required to join course
     *
     * @param courseName
     * @param level
     */
    public static void setMinimumLevel(String courseName, int level) {
        getConfig().set(courseName.toLowerCase() + ".MinimumLevel", level);
        getConfig().save();
    }

    /**
     * Get ParkourKit set for the course
     *
     * @param courseName
     * @return
     */
    public static String getParkourKit(String courseName) {
        return getConfig().getString(courseName.toLowerCase() + ".ParkourKit", Constants.DEFAULT);
    }

    /**
     * Does this course have a ParkourKit set
     *
     * @param courseName
     * @return
     */
    public static boolean hasParkourKit(String courseName) {
        return getConfig().contains(courseName.toLowerCase() + ".ParkourKit");
    }

    /**
     * Set ParkourKit for this course
     *
     * @param courseName
     * @param parkourKitName
     */
    public static void setParkourKit(String courseName, String parkourKitName) {
        getConfig().set(courseName.toLowerCase() + ".ParkourKit", parkourKitName.toLowerCase());
        getConfig().save();
    }

    /**
     * Get the maximum deaths for course
     *
     * @param courseName
     * @return death count
     */
    public static int getMaximumDeaths(String courseName) {
        return getConfig().getInt(courseName.toLowerCase() + ".MaxDeaths", 0);
    }

    /**
     * Get the maximum time limit for course
     *
     * @param courseName
     * @return seconds
     */
    public static int getMaximumTime(String courseName) {
        return getConfig().getInt(courseName.toLowerCase() + ".MaxTime", 0);
    }

    /**
     * Set the maximum deaths for course
     *
     * @param courseName
     * @param amount
     */
    public static void setMaximumDeaths(String courseName, int amount) {
        getConfig().set(courseName.toLowerCase() + ".MaxDeaths", amount);
        getConfig().save();
    }

    /**
     * Set the maximum time limit for course
     *
     * @param courseName
     * @param seconds
     */
    public static void setMaximumTime(String courseName, int seconds) {
        getConfig().set(courseName.toLowerCase() + ".MaxTime", seconds);
        getConfig().save();
    }

    /**
     * Returns whether the course is ready or not.
     *
     * @param courseName
     * @return boolean
     */
    public static boolean getFinished(String courseName) {
        return getConfig().getBoolean(courseName.toLowerCase() + ".Finished");
    }

    /**
     * Set the finish status of the course
     *
     * @param courseName
     * @param finished
     */
    public static void setFinished(String courseName, boolean finished) {
        getConfig().set(courseName.toLowerCase() + ".Finished", finished);
        getConfig().save();
    }

    /**
     * Get the prize commands for the course
     *
     * @param courseName
     * @return
     */
    public static List<String> getCommandsPrize(String courseName) {
        return getConfig().getStringList(courseName.toLowerCase() + ".Prize.CMD");
    }

    /**
     * Does this course have prize commands
     *
     * @param courseName
     * @return
     */
    public static boolean hasCommandPrize(String courseName) {
        return getConfig().contains(courseName.toLowerCase() + ".Prize.CMD");
    }

    /**
     * Add a command to the list of prize commands for completing course
     *
     * @param courseName
     * @param command
     */
    public static void addCommandPrize(String courseName, String command) {
        List<String> commands = getCommandsPrize(courseName);
        commands.add(command);

        getConfig().set(courseName.toLowerCase() + ".Prize.CMD", commands);
        getConfig().save();
    }

    /**
     * Set the prize material and amount for completing course
     *
     * @param courseName
     * @param material
     * @param amount
     */
    public static void setMaterialPrize(String courseName, String material, int amount) {
        getConfig().set(courseName.toLowerCase() + ".Prize.Material", material);
        getConfig().set(courseName.toLowerCase() + ".Prize.Amount", amount);
        getConfig().save();
    }

    /**
     * Get the amount of XP for course
     *
     * @param courseName
     * @return
     */
    public static int getXPPrize(String courseName) {
        return getConfig().getInt(courseName.toLowerCase() + ".Prize.XP");
    }

    /**
     * Set the amount of XP for completing course
     *
     * @param courseName
     * @param amount
     */
    public static void setXPPrize(String courseName, int amount) {
        getConfig().set(courseName.toLowerCase() + ".Prize.XP", amount);
        getConfig().save();
    }

    /**
     * Increase the Complete count of the course
     *
     * @param courseName
     */
    public static void increaseComplete(String courseName) {
        int completed = getConfig().getInt(courseName.toLowerCase() + ".Completed");
        getConfig().set(courseName.toLowerCase() + ".Completed", completed + 1);
        getConfig().save();
    }

    /**
     * Get the number of times the course has been completed
     * @param courseName
     * @return amount
     */
    public static int getCompletions(String courseName) {
        return getConfig().getInt(courseName + ".Completed", 0);
    }

    /**
     * Increase the amount of views of the course
     *
     * @param courseName
     */
    public static void increaseView(String courseName) {
        int views = getConfig().getInt(courseName.toLowerCase() + ".Views");
        getConfig().set(courseName.toLowerCase() + ".Views", views + 1);
        getConfig().save();
    }

    /**
     * Get the reward level for course
     *
     * @param courseName
     * @return
     */
    public static int getRewardLevel(String courseName) {
        return getConfig().getInt(courseName.toLowerCase() + ".Level");
    }

    /**
     * Set the reward level for course
     *
     * @param courseName
     * @param level
     */
    public static void setRewardLevel(String courseName, int level) {
        getConfig().set(courseName.toLowerCase() + ".Level", level);
        getConfig().save();
    }

    /**
     * Get the reward level increase for course
     *
     * @param courseName
     * @return
     */
    public static int getRewardLevelAdd(String courseName) {
        return getConfig().getInt(courseName.toLowerCase() + ".LevelAdd");
    }

    /**
     * Set the reward level increase for course
     *
     * @param courseName
     * @param amount
     */
    public static void setRewardLevelAdd(String courseName, String amount) {
        getConfig().set(courseName.toLowerCase() + ".LevelAdd", Integer.parseInt(amount));
        getConfig().save();
    }

    /**
     * Get the reward once status for course
     *
     * @param courseName
     * @return
     */
    public static boolean getRewardOnce(String courseName) {
        return getConfig().getBoolean(courseName.toLowerCase() + ".RewardOnce");
    }

    /**
     * Set the reward once status for course
     *
     * @param courseName
     * @param enabled
     */
    public static void setRewardOnce(String courseName, boolean enabled) {
        getConfig().set(courseName.toLowerCase() + ".RewardOnce", enabled);
        getConfig().save();
    }

    public static boolean hasRewardDelay(String courseName) {
        return getRewardDelay(courseName) > 0;
    }

    public static int getRewardDelay(String courseName) {
        return getConfig().getInt(courseName.toLowerCase() + ".RewardDelay", 0);
    }

    public static void setRewardDelay(String courseName, int rewardDelay) {
        getConfig().set(courseName.toLowerCase() + ".RewardDelay", rewardDelay);
        getConfig().save();
    }

    public static int getRewardParkoins(String courseName) {
        return getConfig().getInt(courseName.toLowerCase() + ".Parkoins");
    }

    public static void setRewardParkoins(String courseName, int parkoins) {
        getConfig().set(courseName.toLowerCase() + ".Parkoins", parkoins);
        getConfig().save();
    }

    public static Material getJoinItem(String courseName) {
        return Utils.lookupMaterial(getConfig().getString(courseName.toLowerCase() + ".JoinItemMaterial"));
    }

    public static int getJoinItemAmount(String courseName) {
        return getConfig().getInt(courseName.toLowerCase() + ".JoinItemAmount", 1);
    }

    public static String getJoinItemLabel(String courseName) {
        return getConfig().getString(courseName.toLowerCase() + ".JoinItemLabel");
    }

    public static boolean hasJoinItem(String courseName) {
        return getConfig().contains(courseName.toLowerCase() + ".JoinItemMaterial");
    }

    public static void setJoinItem(String courseName, String material, int amount) {
        getConfig().set(courseName.toLowerCase() + ".JoinItemMaterial", material.toUpperCase());
        getConfig().set(courseName.toLowerCase() + ".JoinItemAmount", amount);
        getConfig().save();
    }

    public static String getWorld(String courseName) {
        return getConfig().getString(courseName.toLowerCase() + ".World");
    }

    public static Material getMaterialPrize(String courseName) {
        return Utils.lookupMaterial(getConfig().getString(courseName.toLowerCase() + ".Prize.Material"));
    }

    public static int getMaterialPrizeAmount(String courseName) {
        return getConfig().getInt(courseName.toLowerCase() + ".Prize.Amount", 0);
    }

    public static boolean hasMaterialPrize(String courseName) {
        return getConfig().contains(courseName.toLowerCase() + ".Prize.Material");
    }

    public static void resetLinks(String courseName) {
        getConfig().set(courseName.toLowerCase() + ".LinkedLobby", null);
        getConfig().set(courseName.toLowerCase() + ".LinkedCourse", null);
        getConfig().save();
    }

    public static void deleteCourse(String courseName) {
        courseName = courseName.toLowerCase();

        List<String> courseList = getAllCourses();
        courseList.remove(courseName);
        getConfig().set(courseName, null);
        getConfig().set("Courses", courseList);
        getConfig().save();
        Parkour.getDatabase().deleteCourseAndReferences(courseName);

        PlayerInfo.removeCompletedCourse(courseName);
    }

    public static void resetPrizes(String courseName) {
        courseName = courseName.toLowerCase();

        getConfig().set(courseName + ".Prize", null);
        getConfig().save();
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
        FileConfiguration config = getConfig();
        ChatColor aqua = ChatColor.AQUA;

        int views = config.getInt(courseName + ".Views");
        int completed = config.getInt(courseName + ".Completed");
        int checkpoints = config.getInt(courseName + ".Points");
        int maxDeaths = config.getInt(courseName + ".MaxDeaths");
        int maxTime = config.getInt(courseName + ".MaxTime");
        int minLevel = config.getInt(courseName + ".MinimumLevel");
        int rewardLevel = config.getInt(courseName + ".Level");
        int rewardLevelAdd = config.getInt(courseName + ".LevelAdd");
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
            int joinFee = getEconomyJoiningFee(courseName);
            int finishReward = getEconomyFinishReward(courseName);
            if (joinFee > 0) {
                player.sendMessage("Join Fee: " + aqua + joinFee);
            }
            if (finishReward > 0) {
                player.sendMessage("Economy Reward: " + aqua + finishReward);
            }
        }

        if (hasMaterialPrize(courseName) && getMaterialPrizeAmount(courseName) > 0) {
            player.sendMessage("Material Prize: " + aqua + getMaterialPrize(courseName) + " x " + getMaterialPrizeAmount(courseName));
        }
        if (hasCommandPrize(courseName)) {
            player.sendMessage("Command Prize: " + aqua + getCommandsPrize(courseName));
        }
        if (getXPPrize(courseName) > 0) {
            player.sendMessage("XP Prize: " + aqua + getXPPrize(courseName));
        }

        if (hasRewardDelay(courseName) && Parkour.getSettings().isDisplayPrizeCooldown()) {
            player.sendMessage("Reward Cooldown (days): " + aqua + getRewardDelay(courseName));
            if (!Utils.hasPrizeCooldownDurationPassed(player, courseName, false)) {
                player.sendMessage("Cooldown Remaining: " + aqua + Utils.getTimeRemaining(player, courseName));
            }
        }
    }

    public static int getEconomyFinishReward(String courseName) {
        return Parkour.getConfig(ConfigType.ECONOMY).getInt("Price." + courseName.toLowerCase() + ".Finish");
    }

    public static int getEconomyJoiningFee(String courseName) {
        return Parkour.getConfig(ConfigType.ECONOMY).getInt("Price." + courseName.toLowerCase() + ".JoinFee");
    }
}

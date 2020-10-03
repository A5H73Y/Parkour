package io.github.a5h73y.parkour.type.course;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.other.Constants;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CourseInfo {

    public static ParkourConfiguration getCourseConfig() {
        return Parkour.getConfig(ConfigType.COURSES);
    }

    public static void persistChanges() {
        getCourseConfig().save();
    }

    /**
     * Get list of all Parkour course names.
     *
     * @return List Parkour course names
     */
    public static List<String> getAllCourses() {
        return getCourseConfig().getStringList("Courses");
    }

    /**
     * Return the linked Course of the specified course.
     * Will validate it's a valid course before returning
     *
     * @param courseName
     * @return linkedCourse
     */
    public static String getLinkedCourse(String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".LinkedCourse");
    }

    /**
     * Check if Course is linked to another Course.
     *
     * @param courseName
     * @return if linked course is found
     */
    public static boolean hasLinkedCourse(String courseName) {
        return getCourseConfig().contains(courseName.toLowerCase() + ".LinkedCourse");
    }

    /**
     * Set the Course to link to another Course.
     *
     * @param courseName
     * @param linkedCourse
     */
    public static void setLinkedCourse(String courseName, String linkedCourse) {
        getCourseConfig().set(courseName.toLowerCase() + ".LinkedCourse", linkedCourse.toLowerCase());
        persistChanges();
    }

    /**
     * Return the linked lobby of the Course.
     * Will validate it's a valid lobby before returning
     *
     * @param courseName
     * @return linkedLobby
     */
    public static String getLinkedLobby(String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".LinkedLobby");
    }

    /**
     * Does this course have a linked lobby
     *
     * @param courseName
     * @return if linked lobby is found
     */
    public static boolean hasLinkedLobby(String courseName) {
        return getCourseConfig().contains(courseName.toLowerCase() + ".LinkedLobby");
    }

    /**
     * Set the course to link to a lobby
     *
     * @param courseName
     * @param lobbyName
     */
    public static void setLinkedLobby(String courseName, String lobbyName) {
        getCourseConfig().set(courseName.toLowerCase() + ".LinkedLobby", lobbyName);
        persistChanges();
    }

    /**
     * Get the Mode for the course
     *
     * @param courseName
     * @return mode
     */
    public static String getMode(String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".Mode", "NONE");
    }

    /**
     * Set ParkourMode of a course
     *
     * @param courseName
     * @param mode
     */
    public static void setMode(String courseName, String mode) {
        getCourseConfig().set(courseName.toLowerCase() + ".Mode", mode);
        persistChanges();
    }

    /**
     * Get amount of checkpoints on a course
     *
     * @param courseName
     * @return
     */
    public static int getCheckpointAmount(String courseName) {
        return Parkour.getConfig(ConfigType.CHECKPOINTS).getInt(courseName.toLowerCase() + ".Checkpoints");
    }

    /**
     * Get creator of course
     *
     * @param courseName
     * @return
     */
    public static String getCreator(String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".Creator");
    }

    /**
     * Set creator of course
     *
     * @param courseName
     * @param playerName
     */
    public static void setCreator(String courseName, String playerName) {
        getCourseConfig().set(courseName.toLowerCase() + ".Creator", playerName);
        persistChanges();
    }

    /**
     * Get minimum level required to join course
     *
     * @param courseName
     * @return
     */
    public static int getMinimumLevel(String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".MinimumLevel");
    }

    /**
     * Set minimum level required to join course
     *
     * @param courseName
     * @param level
     */
    public static void setMinimumLevel(String courseName, int level) {
        getCourseConfig().set(courseName.toLowerCase() + ".MinimumLevel", level);
        persistChanges();
    }

    /**
     * Get ParkourKit set for the course
     *
     * @param courseName
     * @return
     */
    public static String getParkourKit(String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".ParkourKit", Constants.DEFAULT);
    }

    /**
     * Does this course have a ParkourKit set
     *
     * @param courseName
     * @return
     */
    public static boolean hasParkourKit(String courseName) {
        return getCourseConfig().contains(courseName.toLowerCase() + ".ParkourKit");
    }

    /**
     * Set ParkourKit for this course
     *
     * @param courseName
     * @param parkourKitName
     */
    public static void setParkourKit(String courseName, String parkourKitName) {
        getCourseConfig().set(courseName.toLowerCase() + ".ParkourKit", parkourKitName.toLowerCase());
        persistChanges();
    }

    /**
     * Get the maximum deaths for course
     *
     * @param courseName
     * @return death count
     */
    public static int getMaximumDeaths(String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".MaxDeaths", -1);
    }

    /**
     * Get the maximum time limit for course
     *
     * @param courseName
     * @return seconds
     */
    public static int getMaximumTime(String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".MaxTime", -1);
    }

    /**
     * Set the maximum deaths for course
     *
     * @param courseName
     * @param amount
     */
    public static void setMaximumDeaths(String courseName, int amount) {
        getCourseConfig().set(courseName.toLowerCase() + ".MaxDeaths", amount);
        persistChanges();
    }

    /**
     * Set the maximum time limit for course
     *
     * @param courseName
     * @param seconds
     */
    public static void setMaximumTime(String courseName, int seconds) {
        getCourseConfig().set(courseName.toLowerCase() + ".MaxTime", seconds);
        persistChanges();
    }

    /**
     * Returns whether the course is ready or not.
     *
     * @param courseName
     * @return ready status
     */
    public static boolean getReadyStatus(String courseName) {
        return getCourseConfig().getBoolean(courseName.toLowerCase() + ".Ready");
    }

    /**
     * Set the ready status of the course
     *
     * @param courseName
     * @param ready ready status
     */
    public static void setReadyStatus(String courseName, boolean ready) {
        getCourseConfig().set(courseName.toLowerCase() + ".Ready", ready);
        persistChanges();
    }

    /**
     * Get the prize commands for the course
     *
     * @param courseName
     * @return
     */
    public static List<String> getCommandsPrize(String courseName) {
        return getCourseConfig().getStringList(courseName.toLowerCase() + ".Prize.CMD");
    }

    /**
     * Does this course have prize commands
     *
     * @param courseName
     * @return
     */
    public static boolean hasCommandPrize(String courseName) {
        return getCourseConfig().contains(courseName.toLowerCase() + ".Prize.CMD");
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

        getCourseConfig().set(courseName.toLowerCase() + ".Prize.CMD", commands);
        persistChanges();
    }

    /**
     * Set the prize material and amount for completing course
     *
     * @param courseName
     * @param material
     * @param amount
     */
    public static void setMaterialPrize(String courseName, String material, int amount) {
        getCourseConfig().set(courseName.toLowerCase() + ".Prize.Material", material);
        getCourseConfig().set(courseName.toLowerCase() + ".Prize.Amount", amount);
        persistChanges();
    }

    /**
     * Get the amount of XP for course
     *
     * @param courseName
     * @return
     */
    public static int getXPPrize(String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".Prize.XP");
    }

    /**
     * Set the amount of XP for completing course
     *
     * @param courseName
     * @param amount
     */
    public static void setXPPrize(String courseName, int amount) {
        getCourseConfig().set(courseName.toLowerCase() + ".Prize.XP", amount);
        persistChanges();
    }

    /**
     * Increase the Complete count of the course
     *
     * @param courseName
     */
    public static void increaseComplete(String courseName) {
        int completed = getCourseConfig().getInt(courseName.toLowerCase() + ".Completed");
        getCourseConfig().set(courseName.toLowerCase() + ".Completed", completed + 1);
    }

    /**
     * Get the number of times the course has been completed
     * @param courseName
     * @return amount
     */
    public static int getCompletions(String courseName) {
        return getCourseConfig().getInt(courseName + ".Completed", 0);
    }

    /**
     * Increase the amount of views of the course
     *
     * @param courseName
     */
    public static void increaseView(String courseName) {
        int views = getCourseConfig().getInt(courseName.toLowerCase() + ".Views");
        getCourseConfig().set(courseName.toLowerCase() + ".Views", views + 1);
    }

    /**
     * Get the reward level for course
     *
     * @param courseName
     * @return
     */
    public static int getRewardLevel(String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".RewardLevel");
    }

    /**
     * Set the reward level for course
     *
     * @param courseName
     * @param level
     */
    public static void setRewardLevel(String courseName, int level) {
        getCourseConfig().set(courseName.toLowerCase() + ".RewardLevel", level);
        persistChanges();
    }

    /**
     * Get the reward level increase for course
     *
     * @param courseName
     * @return
     */
    public static int getRewardLevelAdd(String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".RewardLevelAdd");
    }

    /**
     * Set the reward level increase for course
     *
     * @param courseName
     * @param amount
     */
    public static void setRewardLevelAdd(String courseName, String amount) {
        getCourseConfig().set(courseName.toLowerCase() + ".RewardLevelAdd", Integer.parseInt(amount));
        persistChanges();
    }

    /**
     * Get the reward once status for course
     *
     * @param courseName
     * @return
     */
    public static boolean getRewardOnce(String courseName) {
        return getCourseConfig().getBoolean(courseName.toLowerCase() + ".RewardOnce");
    }

    /**
     * Set the reward once status for course
     *
     * @param courseName
     * @param enabled
     */
    public static void setRewardOnce(String courseName, boolean enabled) {
        getCourseConfig().set(courseName.toLowerCase() + ".RewardOnce", enabled);
        persistChanges();
    }

    public static boolean hasRewardDelay(String courseName) {
        return getRewardDelay(courseName) > 0;
    }

    public static int getRewardDelay(String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".RewardDelay", 0);
    }

    public static void setRewardDelay(String courseName, int rewardDelay) {
        getCourseConfig().set(courseName.toLowerCase() + ".RewardDelay", rewardDelay);
        persistChanges();
    }

    public static int getRewardParkoins(String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".Parkoins");
    }

    public static void setRewardParkoins(String courseName, int parkoins) {
        getCourseConfig().set(courseName.toLowerCase() + ".Parkoins", parkoins);
        persistChanges();
    }

    public static List<ItemStack> getJoinItems(String courseName) {
        List<ItemStack> joinItems = new ArrayList<>();
        ConfigurationSection joinItemStack = getCourseConfig().getConfigurationSection(courseName.toLowerCase() + ".JoinItems");

        if (joinItemStack != null) {
            Set<String> materials = joinItemStack.getKeys(false);

            for (String materialName : materials) {
                Material material = MaterialUtils.lookupMaterial(materialName);
                if (material == null) {
                    continue;
                }

                int amount = joinItemStack.getInt(materialName + ".Amount");
                String displayName = joinItemStack.getString(materialName + ".Label");
                boolean unbreakable = joinItemStack.getBoolean(materialName + ".Unbreakable", false);

                ItemStack itemStack = new ItemStack(material, amount);
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (displayName != null) {
                    itemMeta.setDisplayName(displayName);
                }
                itemMeta.setUnbreakable(unbreakable);
                itemStack.setItemMeta(itemMeta);
                joinItems.add(itemStack);
            }
        }

        return joinItems;
    }

    public static void addJoinItem(String courseName, Material material, int amount, String label, boolean unbreakable) {
        courseName = courseName.toLowerCase();

        getCourseConfig().set(courseName + ".JoinItems." + material.name() + ".Amount", amount);
        getCourseConfig().set(courseName + ".JoinItems." + material.name() + ".Label", label);
        getCourseConfig().set(courseName + ".JoinItems." + material.name() + ".Unbreakable", unbreakable);
        persistChanges();
    }

    public static String getWorld(String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".World");
    }

    public static Material getMaterialPrize(String courseName) {
        return MaterialUtils.lookupMaterial(getCourseConfig().getString(courseName.toLowerCase() + ".Prize.Material"));
    }

    public static int getMaterialPrizeAmount(String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".Prize.Amount", 0);
    }

    public static boolean hasMaterialPrize(String courseName) {
        return getCourseConfig().contains(courseName.toLowerCase() + ".Prize.Material");
    }

    public static void resetLinks(String courseName) {
        getCourseConfig().set(courseName.toLowerCase() + ".LinkedLobby", null);
        getCourseConfig().set(courseName.toLowerCase() + ".LinkedCourse", null);
        persistChanges();
    }

    public static void deleteCourse(String courseName) {
        courseName = courseName.toLowerCase();

        List<String> courseList = getAllCourses();
        courseList.remove(courseName);
        getCourseConfig().set(courseName, null);
        getCourseConfig().set("Courses", courseList);
        persistChanges();

        Parkour.getInstance().getCheckpointManager().deleteCheckpointData(courseName);
        Parkour.getInstance().getDatabase().deleteCourseAndReferences(courseName);

        PlayerInfo.removeCompletedCourse(courseName);
    }

    public static void resetPrizes(String courseName) {
        getCourseConfig().set(courseName.toLowerCase() + ".Prize", null);
        persistChanges();
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
        if (!Parkour.getInstance().getCourseManager().courseExists(courseName)) {
            TranslationUtils.sendValueTranslation("Error.NoExist", courseName, player);
            return;
        }

        courseName = courseName.toLowerCase();
        FileConfiguration config = getCourseConfig();
        ChatColor aqua = ChatColor.AQUA;

        int views = config.getInt(courseName + ".Views");
        int completed = config.getInt(courseName + ".Completed");
        int maxDeaths = config.getInt(courseName + ".MaxDeaths");
        int maxTime = config.getInt(courseName + ".MaxTime");
        int minLevel = config.getInt(courseName + ".MinimumLevel");
        int rewardLevel = config.getInt(courseName + ".RewardLevel");
        int rewardLevelAdd = config.getInt(courseName + ".RewardLevelAdd");
        int parkoins = config.getInt(courseName + ".Parkoins");

        String linkedLobby = config.getString(courseName + ".LinkedLobby");
        String linkedCourse = config.getString(courseName + ".LinkedCourse");
        String creator = config.getString(courseName + ".Creator");
        boolean ready = config.getBoolean(courseName + ".Ready");
        String parkourKit = config.getString(courseName + ".ParkourKit");
        String mode = config.getString(courseName + ".Mode");

        double completePercent = Math.round((completed * 1.0 / views) * 100);
        TranslationUtils.sendHeading(StringUtils.standardizeText(courseName) + " statistics", player);

        player.sendMessage("Views: " + aqua + views);
        player.sendMessage("Completed: " + aqua + completed + " times (" + completePercent + "%)");
        player.sendMessage("Checkpoints: " + aqua + getCheckpointAmount(courseName));
        player.sendMessage("Creator: " + aqua + creator);
        player.sendMessage("Ready: " + aqua + ready);

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
            player.sendMessage("Time Limit: " + aqua + DateTimeUtils.convertSecondsToTime(maxTime));
        }

        if (Parkour.getInstance().getEconomyApi().isEnabled()) {
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

        if (hasRewardDelay(courseName) && Parkour.getDefaultConfig().isDisplayPrizeCooldown()) {
            player.sendMessage("Reward Cooldown (days): " + aqua + getRewardDelay(courseName));
            if (!Parkour.getInstance().getPlayerManager().hasPrizeCooldownDurationPassed(player, courseName, false)) {
                player.sendMessage("Cooldown Remaining: " + aqua + DateTimeUtils.getTimeRemaining(player, courseName));
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

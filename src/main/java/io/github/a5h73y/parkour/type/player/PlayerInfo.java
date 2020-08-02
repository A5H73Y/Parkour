package io.github.a5h73y.parkour.type.player;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.event.PlayerParkourLevelEvent;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Centralize player's information retrieval and modifications
 * Massive thanks to horgeon for the inspiration of this change
 */
public class PlayerInfo {

    public static ParkourConfiguration getConfig() {
        return Parkour.getConfig(ConfigType.PLAYERS);
    }

    public static void persistChanges() {
        getConfig().save();
    }

    /**
     * Retrieve the player's selected course.
     *
     * @param player
     * @return selected course
     */
    public static String getSelectedCourse(OfflinePlayer player) {
        return getConfig().getString("PlayerInfo." + player.getName() + ".Selected");
    }

    /**
     * Set the player's selected course.
     *
     * @param player
     * @param courseName
     */
    public static void setSelectedCourse(OfflinePlayer player, String courseName) {
        getConfig().set("PlayerInfo." + player.getName() + ".Selected", courseName.toLowerCase());
    }

    /**
     * Deselect the player's selected course.
     *
     * @param player
     */
    public static void setDelected(OfflinePlayer player) {
        getConfig().set("PlayerInfo." + player.getName() + ".Selected", null);
    }

    /**
     * Returns if the player has selected a course.
     *
     * @param player
     * @return boolean
     */
    public static boolean hasSelected(Player player) {
        String selected = getSelectedCourse(player);
        if (!Validation.isStringValid(selected)) {
            TranslationUtils.sendTranslation("Error.Selected", player);
            player.sendMessage(ChatColor.GRAY + "Usage: " + ChatColor.WHITE + "/pa select " + ChatColor.AQUA + "(course)");
            return false;
        }
        return true;
    }

    /**
     * Returns the amount of Parkoins a player has accumulated.
     * Parkoins allow you to interact with the new store, making purchases etc.
     * Points will be rewarded on course completion etc.
     *
     * @param player
     * @return int
     */
    public static int getParkoins(OfflinePlayer player) {
        return getConfig().getInt("PlayerInfo." + player.getName() + ".Parkoins");
    }

    /**
     * Set the amount of player's Parkoins.
     *
     * @param player
     * @param amount
     */
    public static void setParkoins(OfflinePlayer player, int amount) {
        getConfig().set("PlayerInfo." + player.getName() + ".Parkoins", amount);
    }

    /**
     * Return the name of the course the player last completed.
     *
     * @param player
     * @return courseName
     */
    public static String getLastCompletedCourse(OfflinePlayer player) {
        return getConfig().getString("PlayerInfo." + player.getName() + ".LastCompleted");
    }

    /**
     * Return the name of the course the player last attempted.
     *
     * @param player
     * @return courseName
     */
    public static String getLastPlayedCourse(OfflinePlayer player) {
        return getConfig().getString("PlayerInfo." + player.getName() + ".LastPlayed");
    }

    /**
     * Get the player's ParkourLevel.
     *
     * @param player
     * @return parkourLevel
     */
    public static int getParkourLevel(OfflinePlayer player) {
        return getConfig().getInt("PlayerInfo." + player.getName() + ".Level");
    }

    /**
     * Set the player's ParkourLevel.
     *
     * @param player
     * @param level
     */
    public static void setParkourLevel(OfflinePlayer player, int level) {
        getConfig().set("PlayerInfo." + player.getName() + ".Level", level);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerParkourLevelEvent((Player) player, null, level));
    }

    /**
     * Set the last completed course for the player.
     * Update the completed list of courses for the player.
     *
     * @param player
     * @param courseName
     */
    public static void setCompletedCourseInfo(OfflinePlayer player, String courseName) {
        getConfig().set("PlayerInfo." + player.getName() + ".LastCompleted", courseName.toLowerCase());

        if (Parkour.getInstance().getConfig().getBoolean("OnFinish.SaveUserCompletedCourses")) {
            List<String> completedCourses = getConfig().getStringList("PlayerInfo." + player.getName() + ".Completed");

            if (!completedCourses.contains(courseName)) {
                completedCourses.add(courseName);
                getConfig().set("PlayerInfo." + player.getName() + ".Completed", completedCourses);
            }
        }
    }

    /**
     * Set the last played course for the player.
     *
     * @param player
     * @param courseName
     */
    public static void setLastPlayedCourse(OfflinePlayer player, String courseName) {
        getConfig().set("PlayerInfo." + player.getName() + ".LastPlayed", courseName.toLowerCase());
    }

    /**
     * Get the player's ParkourRank.
     * If the player has an achieved ParkourRank it will use this,
     * otherwise the default rank will be returned
     *
     * @param player
     * @return
     */
    public static String getRank(OfflinePlayer player) {
        String rank = getConfig().getString("PlayerInfo." + player.getName() + ".Rank");
        return rank == null ? TranslationUtils.getTranslation("Event.DefaultRank", false) : rank;
    }

    /**
     * Set the player's ParkourRank.
     *
     * @param player
     * @param rank
     */
    public static void setRank(OfflinePlayer player, String rank) {
        getConfig().set("PlayerInfo." + player.getName() + ".Rank", rank);
    }

    /**
     * Get the time the player last won the reward for the course.
     *
     * @param player
     * @param courseName
     * @return
     */
    public static long getLastRewardedTime(OfflinePlayer player, String courseName) {
        return getConfig().getLong("PlayerInfo." + player.getName() + ".LastRewarded." + courseName.toLowerCase(), 0);
    }

    /**
     * Set the time the player wins the reward for the course.
     *
     * @param player
     * @param courseName
     * @param rewardTime
     */
    public static void setLastRewardedTime(OfflinePlayer player, String courseName, long rewardTime) {
        getConfig().set("PlayerInfo." + player.getName() + ".LastRewarded." + courseName.toLowerCase(), rewardTime);
    }

    /**
     * Determine if the plugin has any saved information about a player.
     *
     * @param player
     * @return
     */
    public static boolean hasPlayerInfo(OfflinePlayer player) {
        return getConfig().contains("PlayerInfo." + player.getName());
    }

    /**
     * Reset player's Parkour information.
     * This will remove all trace of the player from the plugin.
     * All SQL time entries from the player will be removed, and their parkour stats will be deleted from the config.
     *
     * @param player
     */
    public static void resetPlayer(OfflinePlayer player) {
        getConfig().set("PlayerInfo." + player.getName(), null);
        Parkour.getInstance().getDatabase().deletePlayerTimes(player.getName());
    }

    /**
     * Return the number of Parkour courses completed for player.
     *
     * @param player
     * @return results
     */
    public static String getNumberOfCoursesCompleted(OfflinePlayer player) {
        List<String> completedCourse = getConfig().getStringList("PlayerInfo." + player.getName() + ".Completed");
        return String.valueOf(completedCourse.size());
    }

    /**
     * Get RewardRank for a ParkourLevel
     *
     * @param parkourLevel
     * @return
     */
    public static String getRewardRank(int parkourLevel) {
        return getConfig().getString("ServerInfo.Levels." + parkourLevel + ".Rank");
    }

    /**
     * Set ParkourRank for a ParkourLevel
     *
     * @param parkourLevel
     * @param rank
     */
    public static void setRewardRank(int parkourLevel, String rank) {
        getConfig().set("ServerInfo.Levels." + parkourLevel + ".Rank", rank);
    }

    /**
     * Save the Player's Inventory and Armor contents.
     * @param player
     */
    public static void saveInventoryArmor(Player player) {
        ParkourConfiguration inventoryConfig = Parkour.getConfig(ConfigType.INVENTORY);
        inventoryConfig.set(player.getName() + ".Inventory", Arrays.asList(player.getInventory().getContents()));
        inventoryConfig.set(player.getName() + ".Armor", Arrays.asList(player.getInventory().getArmorContents()));
        inventoryConfig.save();
    }

    public static ItemStack[] getSavedInventoryContents(Player player) {
        List<ItemStack> contents = (List<ItemStack>) Parkour.getConfig(ConfigType.INVENTORY).getList(player.getName() + ".Inventory");
        return contents != null ? contents.toArray(new ItemStack[contents.size()]) : null;
    }

    public static ItemStack[] getSavedArmorContents(Player player) {
        List<ItemStack> contents = (List<ItemStack>) Parkour.getConfig(ConfigType.INVENTORY).getList(player.getName() + ".Armor");
        return contents != null ? contents.toArray(new ItemStack[contents.size()]) : null;
    }

    public static void removeCompletedCourse(String courseName) {
        if (Parkour.getInstance().getConfig().getBoolean("OnFinish.SaveUserCompletedCourses")) {
            ConfigurationSection playersSection = getConfig().getConfigurationSection("PlayerInfo");

            if (playersSection != null) {
                Set<String> playerNames = playersSection.getKeys(false);

                for (String playerName : playerNames) {
                    String completedPath = "PlayerInfo." + playerName + ".Completed";
                    List<String> completedCourses = getConfig().getStringList(completedPath);

                    if (completedCourses.contains(completedPath)) {
                        completedCourses.remove(courseName);
                        getConfig().set(completedPath, completedCourses);
                    }
                }
            }
            getConfig().save();
        }
    }

    /** Set the location from which the player has joined a course.
     *
     * @param player
     */
    public static void setJoinLocation(Player player) {
        getConfig().set("PlayerInfo." + player.getName() + ".JoinLocation", player.getLocation());
    }

    /**
     * Get the location from which the player joined the course.
     * This is used to return the player to their original location on course completion.
     *
     * @param player
     * @return location
     */
    public static Location getJoinLocation(Player player) {
        return (Location) getConfig().get("PlayerInfo." + player.getName() + ".JoinLocation");
    }
}

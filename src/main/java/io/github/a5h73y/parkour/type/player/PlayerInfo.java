package io.github.a5h73y.parkour.type.player;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Player Information Utility class.
 * Convenience methods for accessing the player configuration file.
 */
public class PlayerInfo {

    /**
     * Check if the Player is known to Parkour.
     * @param player requesting player
     * @return player has Parkour information
     */
    public static boolean hasPlayerInfo(OfflinePlayer player) {
        return player != null && getPlayersConfig().contains(player.getUniqueId().toString());
    }

    /**
     * Get the Player's selected Course name.
     * @param player requesting player
     * @return selected course name
     */
    public static String getSelectedCourse(OfflinePlayer player) {
        return getPlayersConfig().getString(player.getUniqueId() + ".Selected");
    }

    /**
     * Check if Player has selected a Course.
     * This does not guarantee they've selected a valid course.
     * @param player requesting player
     * @return player has selected course
     */
    public static boolean hasSelectedCourse(OfflinePlayer player) {
        return getSelectedCourse(player) != null;
    }

    /**
     * Set the Player's selected Course name.
     * @param player requesting player
     * @param courseName selected course name
     */
    public static void setSelectedCourse(OfflinePlayer player, @NotNull String courseName) {
        getPlayersConfig().set(player.getUniqueId() + ".Selected", courseName.toLowerCase());
        persistChanges();
    }

    /**
     * Reset the Player's selected Course Name.
     * @param player requesting player
     */
    public static void resetSelected(OfflinePlayer player) {
        getPlayersConfig().set(player.getUniqueId() + ".Selected", null);
        persistChanges();
    }


    /**
     * Get the Player's last played Course.
     * The course they most recently joined, but may not have finished.
     * @param player requesting player
     * @return course name last played
     */
    public static String getLastPlayedCourse(OfflinePlayer player) {
        return getPlayersConfig().getString(player.getUniqueId() + ".LastPlayed");
    }

    /**
     * Set the Player's last played Course.
     * @param player requesting player
     * @param courseName course name last played
     */
    public static void setLastPlayedCourse(OfflinePlayer player, String courseName) {
        getPlayersConfig().set(player.getUniqueId() + ".LastPlayed", courseName.toLowerCase());
        persistChanges();
    }

    /**
     * Get the Player's last completed Course.
     * @param player requesting player
     * @return course name last completed
     */
    public static String getLastCompletedCourse(OfflinePlayer player) {
        return getPlayersConfig().getString(player.getUniqueId() + ".LastCompleted");
    }

    /**
     * Set the Player's last completed Course.
     * @param player requesting player
     * @param courseName course name last completed
     */
    public static void setLastCompletedCourse(OfflinePlayer player, String courseName) {
        getPlayersConfig().set(player.getUniqueId() + ".LastCompleted", courseName.toLowerCase());
        persistChanges();
    }

    /**
     * Get the number of Courses completed by Player.
     * @param player requesting player
     * @return number of courses completed
     */
    public static int getNumberOfCompletedCourses(OfflinePlayer player) {
        return getCompletedCourses(player).size();
    }

    /**
     * Get the Completed Course names for Player.
     * @param player requesting player
     * @return completed course names
     */
    public static List<String> getCompletedCourses(OfflinePlayer player) {
        return getPlayersConfig().getStringList(player.getUniqueId() + ".Completed");
    }

    public static boolean hasCompletedCourse(Player player, String name) {
        return getCompletedCourses(player).contains(name.toLowerCase());
    }

    public static int getNumberOfUncompletedCourses(OfflinePlayer player) {
        return getUncompletedCourses(player).size();
    }

    /**
     * Get the Uncompleted Course names for Player.
     * The Courses the player has yet to complete on the server.
     * @param player requesting player
     * @return uncompleted course names
     */
    public static List<String> getUncompletedCourses(OfflinePlayer player) {
        List<String> completedCourses = getCompletedCourses(player);
        return CourseInfo.getAllCourseNames().stream()
                .filter(s -> !completedCourses.contains(s))
                .collect(Collectors.toList());
    }

    /**
     * Add a Course name to the Player's completions.
     * @param player requesting player
     * @param courseName completed course name
     */
    public static void addCompletedCourse(OfflinePlayer player, String courseName) {
        if (!Parkour.getDefaultConfig().isCompletedCoursesEnabled()) {
            return;
        }

        List<String> completedCourses = getCompletedCourses(player);

        if (!completedCourses.contains(courseName)) {
            completedCourses.add(courseName);
            getPlayersConfig().set(player.getUniqueId() + ".Completed", completedCourses);
            persistChanges();
        }
    }

    /**
     * Remove a Course from each Player's completed courses.
     * When a Course gets deleted, it must be removed from each player's completed courses.
     * @param courseName course name
     */
    public static void removeCompletedCourse(String courseName) {
        if (!Parkour.getDefaultConfig().isCompletedCoursesEnabled()) {
            return;
        }

        Set<String> playersIds = getPlayersConfig().getConfigurationSection("").getKeys(false);

        for (String uuid : playersIds) {
            String completedPath = uuid + ".Completed";
            List<String> completedCourses = getPlayersConfig().getStringList(completedPath);

            if (completedCourses.contains(courseName)) {
                completedCourses.remove(courseName);
                getPlayersConfig().set(completedPath, completedCourses);
            }
        }
        persistChanges();
    }

    /**
     * Get the Player's ParkourLevel.
     * @param player requesting player
     * @return parkour level value
     */
    public static int getParkourLevel(OfflinePlayer player) {
        return getPlayersConfig().getInt(player.getUniqueId() + ".ParkourLevel");
    }

    /**
     * Increase the Player's ParkourLevel by the amount.
     * @param player requesting player
     * @param amount amount to increase
     */
    public static void increaseParkourLevel(OfflinePlayer player, int amount) {
        setParkourLevel(player, getParkourLevel(player) + amount);
    }

    /**
     * Set the Player's ParkourLevel.
     * @param player requesting player
     * @param level new parkour level value
     */
    public static void setParkourLevel(OfflinePlayer player, int level) {
        getPlayersConfig().set(player.getUniqueId() + ".ParkourLevel", level);
        persistChanges();
    }

    /**
     * Get the Player's ParkourRank.
     * The default rank value will be used if not set.
     * @param player requesting player
     * @return player's parkour rank
     */
    public static String getParkourRank(OfflinePlayer player) {
        return getPlayersConfig().getString(player.getUniqueId() + ".Rank",
                TranslationUtils.getTranslation("Event.DefaultRank", false));
    }

    /**
     * Set the Player's ParkourRank.
     * @param player target player
     * @param parkourRank parkour rank value
     */
    public static void setParkourRank(OfflinePlayer player, String parkourRank) {
        getPlayersConfig().set(player.getUniqueId() + ".Rank", parkourRank);
        persistChanges();
    }

    /**
     * Get last rewarded time for Course.
     * Get the timestamp the Player last received a reward for the completing Course.
     * @param player requesting player
     * @param courseName course name
     * @return last rewarded time
     */
    public static long getLastRewardedTime(OfflinePlayer player, String courseName) {
        return getPlayersConfig().getLong(player.getUniqueId() + ".LastRewarded." + courseName.toLowerCase(), 0);
    }

    /**
     * Set the last rewarded time for Course.
     * @param player requesting player
     * @param courseName course name
     * @param rewardTime time of reward given
     */
    public static void setLastRewardedTime(OfflinePlayer player, String courseName, long rewardTime) {
        getPlayersConfig().set(player.getUniqueId() + ".LastRewarded." + courseName.toLowerCase(), rewardTime);
        persistChanges();
    }

    /**
     * Reset Player's Parkour data.
     * @param player target player
     */
    public static void resetPlayerData(OfflinePlayer player) {
        getPlayersConfig().set(player.getUniqueId().toString(), null);
        persistChanges();
    }

    /**
     * Set the ParkourRank reward for a ParkourLevel.
     * @param parkourLevel parkour level
     * @param parkourRank parkour rank value
     */
    public static void setRewardParkourRank(int parkourLevel, String parkourRank) {
        getPlayersConfig().set("ServerInfo.Levels." + parkourLevel + ".Rank", parkourRank);
        persistChanges();
    }

    /**
     * Save the Player's Inventory and Armor contents.
     * @param player requesting player
     */
    public static void saveInventoryArmor(Player player) {
        ParkourConfiguration inventoryConfig = Parkour.getConfig(ConfigType.INVENTORY);
        inventoryConfig.set(player.getUniqueId() + ".Inventory", Arrays.asList(player.getInventory().getContents()));
        inventoryConfig.set(player.getUniqueId() + ".Armor", Arrays.asList(player.getInventory().getArmorContents()));
        inventoryConfig.save();
    }

    /**
     * Retrieve the Player's saved Inventory contents.
     * @param player requesting player
     * @return player's inventory contents
     */
    public static ItemStack[] getSavedInventoryContents(Player player) {
        List<ItemStack> contents = (List<ItemStack>) Parkour.getConfig(ConfigType.INVENTORY)
                .getList(player.getUniqueId() + ".Inventory");
        return contents != null ? contents.toArray(new ItemStack[contents.size()]) : null;
    }

    /**
     * Retrieve the Player's saved Armor contents.
     * @param player requesting player
     * @return player's armor contents
     */
    public static ItemStack[] getSavedArmorContents(Player player) {
        List<ItemStack> contents = (List<ItemStack>) Parkour.getConfig(ConfigType.INVENTORY)
                .getList(player.getUniqueId() + ".Armor");
        return contents != null ? contents.toArray(new ItemStack[contents.size()]) : null;
    }

    /**
     * Get the Player's Join Location.
     * The {@link Location} from which the player joined the course.
     * @param player requesting player
     * @return saved join location
     */
    public static Location getJoinLocation(Player player) {
        return (Location) getPlayersConfig().get(player.getUniqueId() + ".JoinLocation");
    }

    /**
     * Check whether the Player has a Join Location set.
     * @param player requesting player
     * @return join location set
     */
    public static boolean hasJoinLocation(Player player) {
        return getPlayersConfig().contains(player.getUniqueId() + ".JoinLocation");
    }

    /**
     * Set the Player's Join Location.
     * The player's current position will be saved as their join location.
     * @param player player
     */
    public static void setJoinLocation(Player player) {
        getPlayersConfig().set(player.getUniqueId() + ".JoinLocation", player.getLocation());
        persistChanges();
    }

    /**
     * Reset the Player's Join Location.
     * @param player player
     */
    public static void resetJoinLocation(Player player) {
        getPlayersConfig().set(player.getUniqueId() + ".JoinLocation", null);
        persistChanges();
    }

    /**
     * Check if the Player is in Quiet Mode.
     * Quite Mode will not message the player as often with non-important messages.
     * @param player requesting player
     * @return is quiet mode enabled
     */
    public static boolean isQuietMode(Player player) {
        return getPlayersConfig().getBoolean(player.getUniqueId() + ".QuietMode");
    }

    /**
     * Toggle the Player's Quiet Mode status.
     * @param player requesting player
     */
    public static void toggleQuietMode(Player player) {
        setQuietMode(player, !isQuietMode(player));
    }

    /**
     * Set the Player's Quiet Mode status.
     * @param player target player
     * @param inQuietMode value to set
     */
    public static void setQuietMode(Player player, boolean inQuietMode) {
        getPlayersConfig().set(player.getUniqueId() + ".QuietMode", inQuietMode);
        persistChanges();
    }

    /**
     * Get the number of accumulated Parkoins for Player.
     * @param player requesting player
     * @return number of Parkoins
     */
    public static double getParkoins(OfflinePlayer player) {
        return getPlayersConfig().getDouble(player.getUniqueId() + ".Parkoins", 0);
    }

    /**
     * Increase the amount of Parkoins the Player has.
     * @param player requesting player
     * @param amount amount to increase by
     */
    public static void increaseParkoins(OfflinePlayer player, double amount) {
        setParkoins(player, getParkoins(player) + amount);
    }

    /**
     * Set the number of Parkoins for Player.
     * @param player requesting player
     * @param amount amount to set
     */
    public static void setParkoins(OfflinePlayer player, double amount) {
        getPlayersConfig().set(player.getUniqueId() + ".Parkoins", amount);
        persistChanges();
    }

    /**
     * Get the Players {@link ParkourConfiguration}.
     * @return the players.yml configuration
     */
    private static ParkourConfiguration getPlayersConfig() {
        return Parkour.getConfig(ConfigType.PLAYERS);
    }

    /**
     * Persist any changes made to the players.yml file.
     */
    private static void persistChanges() {
        getPlayersConfig().save();
    }

}

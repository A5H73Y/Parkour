package io.github.a5h73y.parkour.type.player;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.configuration.impl.UserDataConfig;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.TranslationUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Player Information Utility class.
 * Convenience methods for accessing the player configuration file.
 */
public class PlayerInfo {
	
	
	private static Pattern UUID_PATTERN = Pattern.compile("/[0-9A-Fa-f]{8}(?:-[0-9A-Fa-f]{4}){3}-[0-9A-Fa-f]{12}/i");
	

    /**
     * Check if the Player is known to Parkour.
     * @param player player
     * @return player has Parkour information
     */
    public static boolean hasPlayerInfo(OfflinePlayer player) {
        return player != null && Parkour.getInstance().getUserDataManager().exists(player.getUniqueId());
    }

    /**
     * Get the Player's selected Course name.
     * @param player player
     * @return selected course name
     */
    public static String getSelectedCourse(OfflinePlayer player) {
        return getPlayersConfig(player.getUniqueId()).getString("Selected");
    }

    /**
     * Check if Player has selected a Course.
     * This does not guarantee they've selected a valid course.
     * @param player player
     * @return player has selected course
     */
    public static boolean hasSelectedCourse(OfflinePlayer player) {
        return getSelectedCourse(player) != null;
    }

    /**
     * Set the Player's selected Course name.
     * @param player player
     * @param courseName selected course name
     */
    public static void setSelectedCourse(OfflinePlayer player, @NotNull String courseName) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
    	data.set("Selected", courseName.toLowerCase());
    	data.save();
    }

    /**
     * Reset the Player's selected Course Name.
     * @param player player
     */
    public static void resetSelected(OfflinePlayer player) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
    	data.set("Selected", null);
    	data.save();
    }


    /**
     * Get the Player's last played Course.
     * The course they most recently joined, but may not have finished.
     * @param player player
     * @return course name last played
     */
    public static String getLastPlayedCourse(OfflinePlayer player) {
        return getPlayersConfig(player.getUniqueId()).getString("LastPlayed");
    }

    /**
     * Set the Player's last played Course.
     * @param player player
     * @param courseName course name last played
     */
    public static void setLastPlayedCourse(OfflinePlayer player, String courseName) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
        data.set("LastPlayed", courseName.toLowerCase());
    	data.save();
    }

    /**
     * Get the Player's last completed Course.
     * @param player player
     * @return course name last completed
     */
    public static String getLastCompletedCourse(OfflinePlayer player) {
        return getPlayersConfig(player.getUniqueId()).getString("LastCompleted");
    }

    /**
     * Set the Player's last completed Course.
     * @param player player
     * @param courseName course name last completed
     */
    public static void setLastCompletedCourse(OfflinePlayer player, String courseName) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
    	data.set("LastCompleted", courseName.toLowerCase());
        data.save();
    }

    /**
     * Get the number of Courses completed by Player.
     * @param player player
     * @return number of courses completed
     */
    public static int getNumberOfCompletedCourses(OfflinePlayer player) {
        return getCompletedCourses(player).size();
    }

    /**
     * Get the Completed Course names for Player.
     * @param player player
     * @return completed course names
     */
    public static List<String> getCompletedCourses(OfflinePlayer player) {
        return getPlayersConfig(player.getUniqueId()).getStringList("Completed");
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
     * @param player player
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
     * @param player player
     * @param courseName completed course name
     */
    public static void addCompletedCourse(OfflinePlayer player, String courseName) {
        if (!Parkour.getDefaultConfig().isCompletedCoursesEnabled()) {
            return;
        }

        List<String> completedCourses = getCompletedCourses(player);

        if (!completedCourses.contains(courseName)) {
            completedCourses.add(courseName);
            UserDataConfig data = getPlayersConfig(player.getUniqueId());
            data.set("Completed", completedCourses);
            data.save();
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

        File dir = Parkour.getInstance().getUserDataManager().getFolder();
        Stream.of(Parkour.getInstance().getUserDataManager().getFolder().list((File d, String n) -> dir.equals(d) && UUID_PATTERN.matcher(n).matches())).parallel().map(n -> n.substring(0, 36)).map(UUID::fromString).forEach((uuid) -> {
        	UserDataConfig cfg = Parkour.getUserdata(uuid);
        	List<String> completedCourses = cfg.getStringList("Completed");
        	if (completedCourses.contains(courseName)) {
        		completedCourses.remove(courseName);
        		cfg.set("Completed", completedCourses);
        		cfg.save();
        	}
        });
    }

    /**
     * Get the Player's ParkourLevel.
     * @param player player
     * @return parkour level value
     */
    public static int getParkourLevel(OfflinePlayer player) {
        return getPlayersConfig(player.getUniqueId()).getInt("ParkourLevel");
    }

    /**
     * Increase the Player's ParkourLevel by the amount.
     * @param player player
     * @param amount amount to increase
     */
    public static void increaseParkourLevel(OfflinePlayer player, int amount) {
        setParkourLevel(player, getParkourLevel(player) + amount);
    }

    /**
     * Set the Player's ParkourLevel.
     * @param player player
     * @param level new parkour level value
     */
    public static void setParkourLevel(OfflinePlayer player, int level) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
    	data.set("ParkourLevel", level);
    	data.save();
    }

    /**
     * Get the Player's ParkourRank.
     * The default rank value will be used if not set.
     * @param player player
     * @return player's parkour rank
     */
    public static String getParkourRank(OfflinePlayer player) {
        return getPlayersConfig(player.getUniqueId()).getString("ParkourRank",
                TranslationUtils.getTranslation("Event.DefaultRank", false));
    }

    /**
     * Set the Player's ParkourRank.
     * @param player target player
     * @param parkourRank parkour rank value
     */
    public static void setParkourRank(OfflinePlayer player, String parkourRank) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
    	data.set("ParkourRank", parkourRank);
    	data.save();
    }

    /**
     * Get last rewarded time for Course.
     * Get the timestamp the Player last received a reward for the completing Course.
     * @param player player
     * @param courseName course name
     * @return last rewarded time
     */
    public static long getLastRewardedTime(OfflinePlayer player, String courseName) {
        return getPlayersConfig(player.getUniqueId()).getLong("LastRewarded." + courseName.toLowerCase(), 0);
    }

    /**
     * Set the last rewarded time for Course.
     * @param player player
     * @param courseName course name
     * @param rewardTime time of reward given
     */
    public static void setLastRewardedTime(OfflinePlayer player, String courseName, long rewardTime) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
    	data.set("LastRewarded." + courseName.toLowerCase(), rewardTime);
    	data.save();
    }

    /**
     * Reset Player's Parkour data.
     * @param player target player
     */
    public static void resetPlayerData(OfflinePlayer player) {
        UserDataConfig data = getPlayersConfig(player.getUniqueId());
        data.set("LastPlayed", null);
        data.set("LastCompleted", null);
        data.set("LastRewarded", null);
        data.set("Completed", null);
        data.set("ParkourLevel", null);
        data.set("ParkourRank", null);
        data.set("JoinLocation", null);
        data.set("Selected", null);
        data.set("QuietMode", null);
        data.set("Parkoins", null);
        data.set("ExistingSessionCourseName", null);
        data.save();
    }

    /**
     * Set the ParkourRank reward for a ParkourLevel.
     * @param parkourLevel parkour level
     * @param parkourRank parkour rank value
     */
    public static void setRewardParkourRank(int parkourLevel, String parkourRank) {
    	ParkourConfiguration serverInfo = Parkour.getConfig(ConfigType.SERVER_INFO);
    	serverInfo.set("Levels." + parkourLevel + ".Rank", parkourRank);
    	serverInfo.save();
    }

    /**
     * Save the Player's Inventory and Armor contents.
     * @param player player
     */
    public static void saveInventoryArmor(Player player) {
    	UserDataConfig inventoryConfig = Parkour.getUserdata(player.getUniqueId());
        inventoryConfig.set("Inventory", Arrays.asList(player.getInventory().getContents()));
        inventoryConfig.set("Armor", Arrays.asList(player.getInventory().getArmorContents()));
        inventoryConfig.save();
    }

    /**
     * Get the Player's saved Health.
     * @param player player
     * @return stored health
     */
    public static double getSavedHealth(Player player) {
        return Parkour.getUserdata(player.getUniqueId()).getDouble("Health");
    }

    /**
     * Get the Player's saved Health.
     * @param player player
     * @return stored health
     */
    public static int getSavedFoodLevel(Player player) {
        return Parkour.getUserdata(player.getUniqueId()).getInt("Hunger");
    }

    /**
     * Save the Player's Health and Food Level.
     * @param player player
     */
    public static void saveHealthFoodLevel(Player player) {
    	UserDataConfig inventoryConfig = Parkour.getUserdata(player.getUniqueId());
        inventoryConfig.set("Health", player.getHealth());
        inventoryConfig.set("Hunger", player.getFoodLevel());
        inventoryConfig.save();
    }

    /**
     * Reset the saved Health and Food Level.
     * @param player player
     */
    public static void resetSavedHealthFoodLevel(Player player) {
    	UserDataConfig inventoryConfig = Parkour.getUserdata(player.getUniqueId());
        inventoryConfig.set("Health", null);
        inventoryConfig.set("Hunger", null);
        inventoryConfig.save();
    }

    /**
     * Get the Player's Saved XP Level.
     * @param player player
     * @return saved XP level
     */
    public static int getSavedXpLevel(Player player) {
        return Parkour.getUserdata(player.getUniqueId()).getInt(player.getUniqueId() + ".XPLevel");
    }

    /**
     * Save the Player's XP Level.
     * @param player player
     */
    public static void saveXpLevel(Player player) {
    	UserDataConfig inventoryConfig = Parkour.getUserdata(player.getUniqueId());
        inventoryConfig.set("XPLevel", player.getLevel());
        inventoryConfig.save();
    }

    /**
     * Reset the Player's saved XP Level.
     * @param player player
     */
    public static void resetSavedXpLevel(Player player) {
    	UserDataConfig inventoryConfig = Parkour.getUserdata(player.getUniqueId());
        inventoryConfig.set("XPLevel", null);
        inventoryConfig.save();
    }

    /**
     * Retrieve the Player's saved Inventory contents.
     * @param player player
     * @return player's inventory contents
     */
    public static ItemStack[] getSavedInventoryContents(Player player) {
        List<ItemStack> contents = (List<ItemStack>) Parkour.getUserdata(player.getUniqueId())
                .getList("Inventory");
        return contents != null ? contents.toArray(new ItemStack[0]) : null;
    }

    /**
     * Retrieve the Player's saved Armor contents.
     * @param player player
     * @return player's armor contents
     */
    public static ItemStack[] getSavedArmorContents(Player player) {
        List<ItemStack> contents = (List<ItemStack>) Parkour.getUserdata(player.getUniqueId())
                .getList("Armor");
        return contents != null ? contents.toArray(new ItemStack[0]) : null;
    }

    /**
     * Get the Player's Join Location.
     * The {@link Location} from which the player joined the course.
     * @param player player
     * @return saved join location
     */
    public static Location getJoinLocation(Player player) {
        return (Location) getPlayersConfig(player.getUniqueId()).get("JoinLocation");
    }

    /**
     * Check whether the Player has a Join Location set.
     * @param player player
     * @return join location set
     */
    public static boolean hasJoinLocation(Player player) {
        return getPlayersConfig(player.getUniqueId()).contains("JoinLocation");
    }

    /**
     * Set the Player's Join Location.
     * The player's current position will be saved as their join location.
     * @param player player
     */
    public static void setJoinLocation(Player player) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
    	data.set("JoinLocation", player.getLocation());
    	data.save();
    }

    /**
     * Reset the Player's Join Location.
     * @param player player
     */
    public static void resetJoinLocation(Player player) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
    	data.set("JoinLocation", null);
    	data.save();
    }

    /**
     * Check if the Player is in Quiet Mode.
     * Quite Mode will not message the player as often with non-important messages.
     * @param player player
     * @return is quiet mode enabled
     */
    public static boolean isQuietMode(Player player) {
        return getPlayersConfig(player.getUniqueId()).getBoolean("QuietMode");
    }

    /**
     * Toggle the Player's Quiet Mode status.
     * @param player player
     */
    public static void toggleQuietMode(Player player) {
        setQuietMode(player, !isQuietMode(player));
    }

    /**
     * Set the Player's Quiet Mode status.
     * @param player target player
     * @param quietMode value to set
     */
    public static void setQuietMode(Player player, boolean quietMode) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
    	data.set("QuietMode", quietMode);
    	data.save();
    }

    /**
     * Get the number of accumulated Parkoins for Player.
     * @param player player
     * @return number of Parkoins
     */
    public static double getParkoins(OfflinePlayer player) {
        return getPlayersConfig(player.getUniqueId()).getDouble("Parkoins", 0);
    }

    /**
     * Increase the amount of Parkoins the Player has.
     * @param player player
     * @param amount amount to increase by
     */
    public static void increaseParkoins(OfflinePlayer player, double amount) {
        setParkoins(player, getParkoins(player) + amount);
    }

    /**
     * Set the number of Parkoins for Player.
     * @param player player
     * @param amount amount to set
     */
    public static void setParkoins(OfflinePlayer player, double amount) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
    	data.set("Parkoins", amount);
        data.save();
    }

    /**
     * Get the existing Session Course name.
     * The name of the Course they were on when leaving the server.
     * @param player player
     * @return session course name
     */
    @Nullable
    public static String getExistingSessionCourseName(Player player) {
        return getPlayersConfig(player.getUniqueId()).getString("ExistingSessionCourseName");
    }

    /**
     * Player has existing Session Course name.
     * @param player player
     * @return player has existing session course name
     */
    public static boolean hasExistingSessionCourseName(Player player) {
        return getPlayersConfig(player.getUniqueId()).contains("ExistingSessionCourseName");
    }

    /**
     * Set the existing Session Course name.
     * @param player player
     * @param courseName course name
     */
    public static void setExistingSessionCourseName(Player player, String courseName) {
    	UserDataConfig data = getPlayersConfig(player.getUniqueId());
    	data.set("ExistingSessionCourseName", courseName);
    	data.save();
    }

    /**
     * Get the Players {@link ParkourConfiguration}.
     * @return the players.yml configuration
     */
    private static UserDataConfig getPlayersConfig(UUID uuid) {
        return Parkour.getUserdata(uuid);
    }

    private PlayerInfo() {
        throw new IllegalStateException("Utility class");
    }
}

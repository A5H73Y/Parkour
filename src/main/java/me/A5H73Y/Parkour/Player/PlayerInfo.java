package me.A5H73Y.Parkour.Player;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Centralize player's information retrieval and modifications
 * Massive thanks to horgeon for the inspiration of this change
 */
public class PlayerInfo {

    /**
     * Retrieve the player's selected course.
     * @param player
     * @return selected course
     */
    public static String getSelected(OfflinePlayer player) {
        return Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + player.getName() + ".Selected");
    }

    /**
     * Set the player's selected course.
     * @param player
     * @param courseName
     */
    public static void setSelected(OfflinePlayer player, String courseName) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Selected", courseName.toLowerCase());
        Parkour.getParkourConfig().saveUsers();
    }

    public static void setDelected(OfflinePlayer player) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Selected", null);
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Returns if the player has selected a course.
     * @param player
     * @return boolean
     */
    public static boolean hasSelected(Player player) {
        String selected = getSelected(player);
        if (selected == null || selected.length() == 0) {
            player.sendMessage(Utils.getTranslation("Error.Selected"));
            player.sendMessage(ChatColor.GRAY + "Usage: " + ChatColor.WHITE + "/pa select " + ChatColor.AQUA + "(course)");
            return false;
        }
        return true;
    }

    /**
     * Returns the amount of Parkoins a player has accumulated
     * Parkoins allow you to interact with the new store, making purchases etc.
     * Points will be rewarded on course completion etc.
     *
     * @param player
     * @return int
     */
    public static int getParkoins(OfflinePlayer player) {
        return Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Parkoins");
    }

    /**
     * Set the amount of player's Parkoins
     * @param player
     * @param amount
     */
    public static void setParkoins(OfflinePlayer player, int amount) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Parkoins", amount);
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Return the name of the course the player last completed
     * @param player
     * @return courseName
     */
    public static String getLastCompletedCourse(OfflinePlayer player) {
        return Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + player.getName() + ".LastCompleted");
    }

    /**
     * Return the name of the course the player last attempted
     * @param player
     * @return courseName
     */
    public static String getLastPlayedCourse(OfflinePlayer player) {
        return Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + player.getName() + ".LastPlayed");
    }

    /**
     * Get the player's ParkourLevel
     * @param player
     * @return parkourLevel
     */
    public static int getParkourLevel(OfflinePlayer player) {
        return Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Level");
    }

    /**
     * Set the player's ParkourLevel
     * @param player
     * @param level
     */
    public static void setParkourLevel(OfflinePlayer player, int level) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Level", level);
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Set the last completed course for the player
     * @param player
     * @param courseName
     */
    public static void setLastCompletedCourse(OfflinePlayer player, String courseName) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".LastCompleted", courseName.toLowerCase());
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Set the last played course for the player
     * @param player
     * @param courseName
     */
    public static void setLastPlayedCourse(OfflinePlayer player, String courseName) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".LastPlayed", courseName.toLowerCase());
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Get the player's ParkourRank
     * If the player has an achieved ParkourRank it will use this,
     * otherwise the default rank will be returned
     * @param player
     * @return
     */
    public static String getRank(OfflinePlayer player) {
        String rank = Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + player.getName() + ".Rank");
        return rank == null ? Utils.getTranslation("Event.DefaultRank", false) : rank;
    }

    /**
     * Set the player's PatrkourRank
     * @param player
     * @param rank
     */
    public static void setRank(OfflinePlayer player, String rank) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Rank", rank);
        Parkour.getParkourConfig().saveUsers();
    }
    
    /**
     * Get the time the player last won the reward for the course
     * @param player
     * @param courseName
     * @return
     */
    public static long getLastRewardedTime(OfflinePlayer player, String courseName) {
    	return Parkour.getParkourConfig().getUsersData()
                .getLong("PlayerInfo." + player.getName() + ".LastRewarded." + courseName.toLowerCase(), 0);
    }
    
    /**
     * Set the time the player wins the reward for the course
     * @param player
     * @param courseName
     * @param rewardTime
     */
    public static void setLastRewardedTime(OfflinePlayer player, String courseName, long rewardTime) {
    	Parkour.getParkourConfig().getUsersData()
                .set("PlayerInfo." + player.getName() + ".LastRewarded." + courseName.toLowerCase(), rewardTime);
    	Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Determine if the Parkour has any saved information about a player
     * @param player
     * @return
     */
    public static boolean hasPlayerInfo(OfflinePlayer player) {
        return Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + player.getName());
    }

    /**
     * Reset player's Parkour information.
     * This will remove all trace of the player from the plugin.
     * All SQL time entries from the player will be removed, and their parkour stats will be deleted from the config.
     *
     * @param player
     */
    public static void resetPlayer(OfflinePlayer player) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName(), null);
        Parkour.getParkourConfig().saveUsers();
        DatabaseMethods.deleteAllTimesForPlayer(player.getName());
    }
}

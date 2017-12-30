package me.A5H73Y.Parkour.Player;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Centralize player's information retrieval and modifications
 * Massive thanks to horgeon for the inspiration of this change
 */
public class PlayerInfo {

    /**
     * Retrieve the player's selected course.
     * @param playerName
     * @return selected course
     */
    public static String getSelected(String playerName) {
        return Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + playerName + ".Selected");
    }

    /**
     * Set the player's selected course.
     * @param playerName
     * @param courseName
     */
    public static void setSelected(String playerName, String courseName) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + playerName + ".Selected", courseName.toLowerCase());
        Parkour.getParkourConfig().saveUsers();
    }

    public static void setDelected(String playerName) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + playerName + ".Selected", null);
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Returns if the player has selected a course.
     * @param player
     * @return boolean
     */
    public static boolean hasSelected(Player player) {
        String selected = getSelected(player.getName());
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
     * @param playerName
     * @return int
     */
    public static int getParkoins(String playerName) {
        return Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + playerName + ".Parkoins");
    }

    /**
     * Set the amount of player's Parkoins
     * @param playerName
     * @param amount
     */
    public static void setParkoins(String playerName, int amount) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + playerName + ".Parkoins", amount);
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Return the name of the course the player last completed
     * @param playerName
     * @return courseName
     */
    public static String getLastCompletedCourse(String playerName) {
        return Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + playerName + ".LastCompleted");
    }

    /**
     * Return the name of the course the player last attempted
     * @param playerName
     * @return courseName
     */
    public static String getLastPlayedCourse(String playerName) {
        return Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + playerName + ".LastPlayed");
    }

    /**
     * Get the player's ParkourLevel
     * @param playerName
     * @return parkourLevel
     */
    public static int getParkourLevel(String playerName) {
        return Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + playerName + ".Level");
    }

    /**
     * Set the player's ParkourLevel
     * @param playerName
     * @param level
     */
    public static void setParkourLevel(String playerName, int level) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + playerName + ".Level", level);
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Set the last completed course for the player
     * @param playerName
     * @param courseName
     */
    public static void setLastCompletedCourse(String playerName, String courseName) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + playerName + ".LastCompleted", courseName);
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Set the last played course for the player
     * @param playerName
     * @param courseName
     */
    public static void setLastPlayedCourse(String playerName, String courseName) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + playerName + ".LastPlayed", courseName);
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Get the player's ParkourRank
     * If the player has an achieved ParkourRank it will use this,
     * otherwise the default rank will be returned
     * @param playerName
     * @return
     */
    public static String getRank(String playerName) {
        String rank = Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + playerName + ".Rank");
        return rank == null ? Utils.getTranslation("Event.DefaultRank", false) : rank;
    }

    /**
     * Set the player's PatrkourRank
     * @param playerName
     * @param rank
     */
    public static void setRank(String playerName, String rank) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + playerName + ".Rank", rank);
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Determine if the Parkour has any saved information about a player
     * @param playerName
     * @return
     */
    public static boolean hasPlayerInfo(String playerName) {
        return Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + playerName);
    }

    /**
     * Reset player's Parkour information.
     * This will remove all trace of the player from the plugin.
     * All SQL time entries from the player will be removed, and their parkour stats will be deleted from the config.
     *
     * @param playerName
     */
    public static void resetPlayer(String playerName) {
        Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + playerName, null);
        Parkour.getParkourConfig().saveUsers();
        DatabaseMethods.deleteAllTimesForPlayer(playerName);
    }
}

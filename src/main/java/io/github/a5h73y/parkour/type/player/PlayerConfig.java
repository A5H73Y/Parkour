package io.github.a5h73y.parkour.type.player;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import static io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable.getMapValue;
import java.io.File;
import de.leonhard.storage.Json;
import de.leonhard.storage.internal.FileType;
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
public class PlayerConfig extends Json {

    public PlayerConfig(File playerFile) {
        super(playerFile);
    }

    public static File getPlayerSessionFile(OfflinePlayer player, String courseName) {
        return new File(Parkour.getInstance().getConfigManager().getSessionsDir()
                + File.separator + getPlayerConfigName(player), courseName);
    }

    public static boolean hasPlayerConfig(OfflinePlayer player) {
        return getPlayerJsonFile(player).exists();
    }

    public static PlayerConfig getConfig(OfflinePlayer player) {
        return new PlayerConfig(getPlayerJsonFile(player));
    }

    /**
     * Delete Player's Parkour data.
     */
    public static void deletePlayerData(OfflinePlayer player) {
        File playerConfig = getPlayerJsonFile(player);

        if (playerConfig.exists()) {
            playerConfig.delete();
        }
    }

    /**
     * Get the Player's selected Course name.
     * @return selected course name
     */
    @Nullable
    public String getSelectedCourse() {
        return this.get("Selected", null);
    }

    /**
     * Check if Player has selected a Course.
     * This does not guarantee they've selected a valid course.
     * @return player has selected course
     */
    public boolean hasSelectedCourse() {
        return getSelectedCourse() != null;
    }

    /**
     * Set the Player's selected Course name.
     * @param courseName selected course name
     */
    public void setSelectedCourse(@NotNull String courseName) {
        this.set("Selected", courseName.toLowerCase());
    }

    /**
     * Reset the Player's selected Course Name.
     */
    public void resetSelected() {
        this.set("Selected", null);
    }

    /**
     * Get the Player's last played Course.
     * The course they most recently joined, but may not have finished.
     * @return course name last played
     */
    @Nullable
    public String getLastPlayedCourse() {
        return this.get("LastPlayed", null);
    }

    /**
     * Set the Player's last played Course.
     * @param courseName course name last played
     */
    public void setLastPlayedCourse(String courseName) {
        this.set("LastPlayed", courseName.toLowerCase());
    }

    /**
     * Get the Player's last completed Course.
     * @return course name last completed
     */
    @Nullable
    public String getLastCompletedCourse() {
        return this.get("LastCompleted", null);
    }

    /**
     * Set the Player's last completed Course.
     * @param courseName course name last completed
     */
    public void setLastCompletedCourse(String courseName) {
        this.set("LastCompleted", courseName.toLowerCase());
    }

    /**
     * Get the Player's ParkourLevel.
     * @return parkour level value
     */
    public int getParkourLevel() {
        return this.getInt("ParkourLevel");
    }

    /**
     * Increase the Player's ParkourLevel by the amount.
     * @param amount amount to increase
     */
    public void increaseParkourLevel(int amount) {
        setParkourLevel(getParkourLevel() + amount);
    }

    /**
     * Set the Player's ParkourLevel.
     * @param level new parkour level value
     */
    public void setParkourLevel(int level) {
        this.set("ParkourLevel", level);
    }

    /**
     * Get the Player's ParkourRank.
     * The default rank value will be used if not set.
     * @return player's parkour rank
     */
    public String getParkourRank() {
        return this.getOrDefault("ParkourRank",
                TranslationUtils.getTranslation("Event.DefaultRank", false));
    }

    /**
     * Set the Player's ParkourRank.
     * @param parkourRank parkour rank value
     */
    public void setParkourRank(String parkourRank) {
        this.set("ParkourRank", parkourRank);
    }

    /**
     * Get last rewarded time for Course.
     * Get the timestamp the Player last received a reward for the completing Course.
     * @param courseName course name
     * @return last rewarded time
     */
    public long getLastRewardedTime(String courseName) {
        return this.getLong("LastRewarded-" + courseName.toLowerCase());
    }

    /**
     * Set the last rewarded time for Course.
     * @param courseName course name
     * @param rewardTime time of reward given
     */
    public void setLastRewardedTime(String courseName, long rewardTime) {
        this.set("LastRewarded-" + courseName.toLowerCase(), rewardTime);
    }

    public boolean hasInventoryData() {
        return this.contains("Inventory");
    }

    /**
     * Save the Player's Inventory and Armor contents.
     */
    public void saveInventoryArmorData(Player player) {
        this.setSerializable("Inventory", player.getInventory().getContents());
        this.setSerializable("Armor", player.getInventory().getArmorContents());
    }

    /**
     * Reset the Player's Inventory and Armor contents.
     */
    public void resetInventoryArmorData() {
        this.set("Inventory", null);
        this.set("Armor", null);
    }

    /**
     * Retrieve the Player's saved Inventory contents.
     * @return player's inventory contents
     */
    public ItemStack[] getSavedInventoryContents() {
        return this.getSerializable("Inventory", ItemStack[].class);
    }

    /**
     * Retrieve the Player's saved Armor contents.
     * @return player's armor contents
     */
    public ItemStack[] getSavedArmorContents() {
        return this.getSerializable("Armor", ItemStack[].class);
    }

    /**
     * Get the Player's saved Health.
     * @return stored health
     */
    public double getSavedHealth() {
        return this.getDouble("Health");
    }

    /**
     * Get the Player's saved Health.
     * @return stored health
     */
    public int getSavedFoodLevel() {
        return this.getInt("Hunger");
    }

    /**
     * Save the Player's Health and Food Level.
     */
    public void saveHealthFoodLevel(Player player) {
        this.set("Health", player.getHealth());
        this.set("Hunger", player.getFoodLevel());
    }

    /**
     * Reset the saved Health and Food Level.
     */
    public void resetSavedHealthFoodLevel() {
        this.set("Health", null);
        this.set("Hunger", null);
    }

    /**
     * Get the Player's Saved XP Level.
     * @return saved XP level
     */
    public int getSavedXpLevel() {
        return this.getInt("XPLevel");
    }

    /**
     * Save the Player's XP Level.
     */
    public void saveXpLevel(Player player) {
        this.set("XPLevel", player.getLevel());
    }

    /**
     * Reset the Player's saved XP Level.
     */
    public void resetSavedXpLevel() {
        this.set("XPLevel", null);
    }

    /**
     * Get the Player's Join Location.
     * The {@link Location} from which the player joined the course.
     * @return saved join location
     */
    public Location getJoinLocation() {
        return Location.deserialize(getMapValue((this.get("JoinLocation"))));
    }

    /**
     * Check whether the Player has a Join Location set.
     * @return join location set
     */
    public boolean hasJoinLocation() {
        return this.contains("JoinLocation");
    }

    /**
     * Set the Player's Join Location.
     * The player's current position will be saved as their join location.
     */
    public void setJoinLocation(Location location) {
        this.set("JoinLocation", location.serialize());
    }

    /**
     * Check if the Player is in Quiet Mode.
     * Quite Mode will not message the player as often with non-important messages.
     * @return is quiet mode enabled
     */
    public boolean isQuietMode() {
        return this.getBoolean("QuietMode");
    }

    /**
     * Toggle the Player's Quiet Mode status.
     */
    public void toggleQuietMode() {
        setQuietMode(!isQuietMode());
    }

    /**
     * Set the Player's Quiet Mode status.

     * @param quietMode value to set
     */
    public void setQuietMode(boolean quietMode) {
        this.set("QuietMode", quietMode);
    }

    /**
     * Get the number of accumulated Parkoins for Player.
     * @return number of Parkoins
     */
    public double getParkoins() {
        return this.getDouble("Parkoins");
    }

    /**
     * Increase the amount of Parkoins the Player has.
     * @param amount amount to increase by
     */
    public void increaseParkoins(double amount) {
        setParkoins(getParkoins() + amount);
    }

    /**
     * Set the number of Parkoins for Player.
     * @param amount amount to set
     */
    public void setParkoins(double amount) {
        this.set("Parkoins", amount);
    }

    /**
     * Get the existing Session Course name.
     * The name of the Course they were on when leaving the server.
     * @return session course name
     */
    @Nullable
    public String getExistingSessionCourseName() {
        return this.get("ExistingSessionCourseName", null);
    }

    /**
     * Player has existing Session Course name.
     * @return player has existing session course name
     */
    public boolean hasExistingSessionCourseName() {
        return this.contains("ExistingSessionCourseName");
    }

    /**
     * Set the existing Session Course name.
     * @param courseName course name
     */
    public void setExistingSessionCourseName(String courseName) {
        this.set("ExistingSessionCourseName", courseName);
    }

    /**
     * Generates the file name for the Player config file.
     * Can either be the Player's UUID or the Player's name as the file name.
     * @param player player
     * @return player file name
     */
    private static String getPlayerConfigName(OfflinePlayer player) {
        return Parkour.getDefaultConfig().getBoolean("Other.PlayerConfigUsePlayerUUID")
                ? player.getUniqueId().toString() : player.getName();
    }

    private static String getPlayerJsonPath(OfflinePlayer player) {
        return getPlayerConfigName(player) + "." + FileType.JSON.getExtension();
    }

    private static File getPlayerJsonFile(OfflinePlayer player) {
        return new File(Parkour.getInstance().getConfigManager().getPlayersDir(),
                getPlayerJsonPath(player));
    }
}

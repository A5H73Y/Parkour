package io.github.a5h73y.parkour.type.player;

import de.leonhard.storage.Json;
import de.leonhard.storage.internal.FileType;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.io.File;
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

    public static final String LAST_PLAYED = "LastPlayed";
    public static final String LAST_COMPLETED = "LastCompleted";
    public static final String PARKOUR_LEVEL = "ParkourLevel";
    public static final String PARKOUR_RANK = "ParkourRank";
    public static final String LAST_REWARDED = "LastRewarded.";
    public static final String SNAPSHOT = "Snapshot.";
    public static final String INVENTORY = "Inventory";
    public static final String ARMOR = "Armor";
    public static final String HEALTH = "Health";
    public static final String HUNGER = "Hunger";
    public static final String XP_LEVEL = "XPLevel";
    public static final String JOIN_LOCATION = "JoinLocation";
    public static final String GAMEMODE = "GameMode";
    public static final String PARKOINS = "Parkoins";
    public static final String EXISTING_SESSION_COURSE_NAME = "ExistingSessionCourseName";


    public PlayerConfig(File playerFile) {
        super(playerFile);
    }

    public static boolean hasPlayerConfig(OfflinePlayer player) {
        return getPlayerJsonFile(player).exists();
    }

    /**
     * Will create a new instance of the Player's json config.
     * Note that this may introduce dataloss of different instances exist,
     * Cached values from {@link io.github.a5h73y.parkour.configuration.ConfigManager} is recommended.
     *
     * @param player player
     * @return PlayerConfig instance
     */
    @NotNull
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
     * Get the Player's last played Course.
     * The course they most recently joined, but may not have finished.
     *
     * @return course name last played
     */
    @NotNull
    public String getLastPlayedCourse() {
        return this.getString(LAST_PLAYED);
    }

    /**
     * Set the Player's last played Course.
     *
     * @param courseName course name last played
     */
    public void setLastPlayedCourse(String courseName) {
        this.set(LAST_PLAYED, courseName.toLowerCase());
    }

    /**
     * Get the Player's last completed Course.
     *
     * @return course name last completed
     */
    @NotNull
    public String getLastCompletedCourse() {
        return this.getString(LAST_COMPLETED);
    }

    /**
     * Set the Player's last completed Course.
     *
     * @param courseName course name last completed
     */
    public void setLastCompletedCourse(String courseName) {
        this.set(LAST_COMPLETED, courseName.toLowerCase());
    }

    /**
     * Get the Player's ParkourLevel.
     *
     * @return parkour level value
     */
    public int getParkourLevel() {
        return this.getInt(PARKOUR_LEVEL);
    }

    /**
     * Increase the Player's ParkourLevel by the amount.
     *
     * @param amount amount to increase
     */
    public void increaseParkourLevel(int amount) {
        setParkourLevel(getParkourLevel() + amount);
    }

    /**
     * Set the Player's ParkourLevel.
     *
     * @param level new parkour level value
     */
    public void setParkourLevel(int level) {
        this.set(PARKOUR_LEVEL, level);
    }

    /**
     * Get the Player's ParkourRank.
     * The default rank value will be used if not set.
     *
     * @return player's parkour rank
     */
    public String getParkourRank() {
        return this.getOrDefault(PARKOUR_RANK,
                TranslationUtils.getTranslation("Event.DefaultRank", false));
    }

    /**
     * Set the Player's ParkourRank.
     *
     * @param parkourRank parkour rank value
     */
    public void setParkourRank(String parkourRank) {
        this.set(PARKOUR_RANK, parkourRank);
    }

    /**
     * Get last rewarded time for Course.
     * Get the timestamp the Player last received a reward for the completing Course.
     *
     * @param courseName course name
     * @return last rewarded time
     */
    public long getLastRewardedTime(String courseName) {
        return this.getLong(LAST_REWARDED + courseName.toLowerCase());
    }

    /**
     * Set the last rewarded time for Course.
     *
     * @param courseName course name
     * @param rewardTime time of reward given
     */
    public void setLastRewardedTime(String courseName, long rewardTime) {
        this.set(LAST_REWARDED + courseName.toLowerCase(), rewardTime);
    }

    /**
     * Save a Snapshot of the Player's data prior to joining a Course.
     * This data can be optionally restored before being deleted.
     * Data will never be overridden, instead deleted / saved.
     *
     * @param player player
     */
    public void setPlayerDataSnapshot(Player player) {
        if (!hasPlayerDataSnapshot()) {
            this.setSerializable(SNAPSHOT + INVENTORY, player.getInventory().getContents());
            this.setSerializable(SNAPSHOT + ARMOR, player.getInventory().getArmorContents());
            if (!hasSnapshotJoinLocation()) {
                this.setSerializable(JOIN_LOCATION, player.getLocation());
            }
            this.set(SNAPSHOT + HEALTH, player.getHealth());
            this.set(SNAPSHOT + HUNGER, player.getFoodLevel());
            this.set(SNAPSHOT + XP_LEVEL, player.getLevel());
            this.set(SNAPSHOT + GAMEMODE, player.getGameMode().name());
        }
    }

    public boolean hasPlayerDataSnapshot() {
        return this.contains(SNAPSHOT);
    }

    /**
     * Reset the Player's Snapshot data.
     */
    public void resetPlayerDataSnapshot() {
        this.remove(SNAPSHOT);
    }

    /**
     * Get the Player's Inventory Snapshot.
     * @return ItemStacks representing inventory
     */
    public ItemStack[] getSnapshotInventory() {
        return this.getSerializable(SNAPSHOT + INVENTORY, ItemStack[].class);
    }

    /**
     * Get the Player's Armor Snapshot.
     * @return ItemStacks representing armor
     */
    public ItemStack[] getSnapshotArmor() {
        return this.getSerializable(SNAPSHOT + ARMOR, ItemStack[].class);
    }

    /**
     * Get the Player's Join Location Snapshot.
     * The {@link Location} from which the player joined the course.
     *
     * @return join location
     */
    public boolean hasSnapshotJoinLocation() {
        return this.contains(JOIN_LOCATION);
    }

    /**
     * Get the Player's Join Location Snapshot.
     * The {@link Location} from which the player joined the course.
     *
     * @return join location
     */
    public Location getSnapshotJoinLocation() {
        return this.getSerializable(JOIN_LOCATION, Location.class);
    }

    public void resetSessionJoinLocation() {
        this.remove(JOIN_LOCATION);
    }

    /**
     * Get the Player's Health Snapshot.
     * @return stored health
     */
    public double getSnapshotHealth() {
        return this.getDouble(SNAPSHOT + HEALTH);
    }

    /**
     * Get the Player's Hunger Snapshot.
     * @return stored hunger
     */
    public int getSnapshotHunger() {
        return this.getInt(SNAPSHOT + HUNGER);
    }

    /**
     * Get the Player's XP Level Snapshot.
     * @return stored xp level
     */
    public int getSnapshotXpLevel() {
        return this.getInt(SNAPSHOT + XP_LEVEL);
    }

    /**
     * Get the Player's GameMode Snapshot.
     * @return saved gamemode name
     */
    public String getSnapshotGameMode() {
        return this.getString(SNAPSHOT + GAMEMODE).toUpperCase();
    }

    /**
     * Get the number of accumulated Parkoins for Player.
     * @return number of Parkoins
     */
    public double getParkoins() {
        return this.getDouble(PARKOINS);
    }

    /**
     * Increase the amount of Parkoins the Player has.
     * @param amount amount to increase by
     */
    public void increaseParkoins(double amount) {
        setParkoins(getParkoins() + amount);
    }

    /**
     * Increase the amount of Parkoins the Player has.
     * @param amount amount to increase by
     */
    public void decreaseParkoins(double amount) {
        setParkoins(getParkoins() - amount);
    }

    /**
     * Set the number of Parkoins for Player.
     * @param amount amount to set
     */
    public void setParkoins(double amount) {
        this.set(PARKOINS, amount);
    }

    /**
     * Get the existing Session Course name.
     * The name of the Course they were on when leaving the server.
     * @return session course name
     */
    @Nullable
    public String getExistingSessionCourseName() {
        return this.get(EXISTING_SESSION_COURSE_NAME, null);
    }

    /**
     * Player has existing Session Course name.
     * @return player has existing session course name
     */
    public boolean hasExistingSessionCourseName() {
        return this.contains(EXISTING_SESSION_COURSE_NAME);
    }

    /**
     * Set the existing Session Course name.
     * @param courseName course name
     */
    public void setExistingSessionCourseName(String courseName) {
        this.set(EXISTING_SESSION_COURSE_NAME, courseName);
    }

    public void removeExistingSessionCourseName() {
        this.remove(EXISTING_SESSION_COURSE_NAME);
    }

    private static String getPlayerJsonPath(OfflinePlayer player) {
        return Parkour.getDefaultConfig().getPlayerConfigName(player) + "." + FileType.JSON.getExtension();
    }

    private static File getPlayerJsonFile(OfflinePlayer player) {
        return new File(Parkour.getInstance().getConfigManager().getPlayersDir(),
                getPlayerJsonPath(player));
    }
}

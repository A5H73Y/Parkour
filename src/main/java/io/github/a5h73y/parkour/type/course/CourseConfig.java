package io.github.a5h73y.parkour.type.course;

import static io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable.getMapValue;
import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;
import static io.github.a5h73y.parkour.utility.TranslationUtils.sendConditionalValue;
import static io.github.a5h73y.parkour.utility.TranslationUtils.sendValue;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import de.leonhard.storage.Json;
import de.leonhard.storage.internal.FileType;
import de.leonhard.storage.internal.serialize.LightningSerializer;
import de.leonhard.storage.sections.FlatFileSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Course Information Utility class.
 * Convenience methods for accessing the course configuration file.
 */
public class CourseConfig extends Json {

    public static final String MINIMUM_LEVEL = "MinimumLevel";
    public static final String DISPLAY_NAME = "DisplayName";
    public static final String LINKED_COURSE = "LinkedCourse";
    public static final String LINKED_LOBBY = "LinkedLobby";
    public static final String PARKOUR_MODE = "ParkourMode";
    public static final String CHECKPOINTS = "Checkpoints";
    public static final String CREATOR = "Creator";
    public static final String PARKOUR_KIT = "ParkourKit";
    public static final String MAX_DEATHS = "MaxDeaths";
    public static final String MAX_TIME = "MaxTime";
    public static final String MAX_FALL_TICKS = "MaxFallTicks";
    public static final String READY = "Ready";
    public static final String MANUAL_CHECKPOINTS = "ManualCheckpoints";

    private String courseName;

    public CourseConfig(String courseName, File file) {
        super(file);
        this.courseName = courseName;
    }

    public static File getCourseConfigFile(String courseName) {
        return new File(Parkour.getInstance().getConfigManager().getCoursesDir(),
                courseName.toLowerCase() + "." + FileType.JSON.getExtension());
    }

    public static boolean hasCourseConfig(String courseName) {
        return getCourseConfigFile(courseName).exists();
    }

    public static CourseConfig getConfig(String courseName) {
        return new CourseConfig(courseName, getCourseConfigFile(courseName));
    }

    /**
     * Delete Course data.
     */
    public static void deleteCourseData(String courseName) {
        File courseConfigFile = getCourseConfigFile(courseName);

        if (courseConfigFile.exists()) {
            courseConfigFile.delete();
        }
    }

    public Course getCourse() {
        return LightningSerializer.deserialize(this.getData(), Course.class);
    }

    public void saveCourse(Course course) {
        getMapValue(LightningSerializer.serialize(course)).forEach(this::set);
    }

    public String getCourseName() {
        return this.getName().toLowerCase().replace(".json", "");
    }

    /**
     * Get the Course's display name.
     * Fallback to the course name.
     * @return course display name
     */
    public String getCourseDisplayName() {
        return StringUtils.colour(this.getOrDefault(DISPLAY_NAME, getCourseName()));
    }

    /**
     * Check if the Course has a Display Name.
     * @return course has display name
     */
    public boolean hasCourseDisplayName() {
        return this.contains(DISPLAY_NAME);
    }

    /**
     * Set the Course's display name.
     * @param courseDisplayName course display name
     */
    public void setCourseDisplayName(@NotNull String courseDisplayName) {
        this.set(DISPLAY_NAME, courseDisplayName);
    }

    /**
     * Get the linked Course of the specified course.
     * The result may still need to be verified as existing.
     * @return linkedCourse
     */
    @Nullable
    public String getLinkedCourse() {
        return this.get(LINKED_COURSE, null);
    }

    /**
     * Check if Course is linked to another Course.
     * @return if linked course is found
     */
    public boolean hasLinkedCourse() {
        return this.contains(LINKED_COURSE);
    }

    /**
     * Set the linked Course destination to a different Course.
     * @param linkedCourse linked course name
     */
    public void setLinkedCourse(@NotNull String linkedCourse) {
        this.set(LINKED_COURSE, linkedCourse.toLowerCase());
    }

    /**
     * Find the linked Lobby of the Course.
     * The result may still need to be verified as existing.
     * @return linked Lobby name
     */
    @Nullable
    public String getLinkedLobby() {
        return this.get(LINKED_LOBBY, null);
    }

    /**
     * Check if Course is linked to a Lobby.
     * @return if linked lobby is found
     */
    public boolean hasLinkedLobby() {
        return this.contains(LINKED_LOBBY);
    }

    /**
     * Set the linked Course destination to a Lobby.
     * @param lobbyName lobby name
     */
    public void setLinkedLobby(@NotNull String lobbyName) {
        this.set(LINKED_LOBBY, lobbyName.toLowerCase());
    }

    /**
     * Get the ParkourMode name for the Course.
     * @return the parkour mode name set
     */
    public String getParkourModeName() {
        return this.getOrDefault(PARKOUR_MODE, ParkourMode.NONE.name());
    }

    /**
     * Get the matching {@link ParkourMode} for the Course.
     * @return the ParkourMode
     */
    public ParkourMode getCourseMode() {
        ParkourMode result;
        try {
            result = ParkourMode.valueOf(getParkourModeName().toUpperCase());
        } catch (IllegalArgumentException exception) {
            setParkourMode(ParkourMode.NONE);
            result = ParkourMode.NONE;
        }
        return result;
    }

    /**
     * Check if the Course has a ParkourMode defined.
     * None is not considered a ParkourMode.
     * @return course has ParkourMode
     */
    public boolean hasParkourMode() {
        return !ParkourMode.NONE.name().equals(getParkourModeName());
    }

    /**
     * Set the {@link ParkourMode} for the Course.
     * @param mode ParkourMode
     */
    public void setParkourMode(@NotNull ParkourMode mode) {
        this.set(PARKOUR_MODE, mode.name());
    }

    /**
     * Get the number of Checkpoints on a Course.
     * @return number of checkpoints
     */
    public int getCheckpointAmount() {
        return this.getInt(CHECKPOINTS);
    }

    /**
     * Get the Creator's name of the Course.
     * @return creator's name
     */
    @Nullable
    public String getCreator() {
        return this.get(CREATOR, null);
    }

    /**
     * Set the Creator's name of the Course.
     * @param playerName player's name
     */
    public void setCreator(@NotNull String playerName) {
        this.set(CREATOR, playerName);
    }

    /**
     * Get the Minimum ParkourLevel of a Course.
     * @return minimum ParkourLevel to join Course
     */
    public int getMinimumParkourLevel() {
        return this.getInt(MINIMUM_LEVEL);
    }

    /**
     * Check if the Course has a Minimum ParkourLevel.
     * @return course has minimum ParkourLevel
     */
    public boolean hasMinimumParkourLevel() {
        return getMinimumParkourLevel() > 0;
    }

    /**
     * Set the Minimum ParkourLevel for a Course.
     * @param parkourLevel minimum parkour level
     */
    public void setMinimumParkourLevel(int parkourLevel) {
        this.set(MINIMUM_LEVEL, parkourLevel);
    }

    /**
     * Get ParkourKit name for the Course.
     * @return ParkourKit name.
     */
    @Nullable
    public String getParkourKit() {
        return this.getOrDefault(PARKOUR_KIT, DEFAULT);
    }

    /**
     * Check if the Course has a ParkourKit set.
     * @return course has ParkourKit
     */
    public boolean hasParkourKit() {
        return this.contains(PARKOUR_KIT);
    }

    /**
     * Set the ParkourKit name for the Course.
     * @param parkourKitName parkour kit name
     */
    public void setParkourKit(@NotNull String parkourKitName) {
        this.set(PARKOUR_KIT, parkourKitName.toLowerCase());
    }

    /**
     * Get the Maximum number of Deaths for a Course.
     * Maximum number of deaths a player can accumulate before a loss.
     * @return maximum deaths
     */
    public int getMaximumDeaths() {
        return this.getInt(MAX_DEATHS);
    }

    /**
     * Check if the Course has a Maximum number of Deaths set.
     * @return course has maximum deaths set
     */
    public boolean hasMaximumDeaths() {
        return getMaximumDeaths() > 0;
    }

    /**
     * Set the Maximum number of Deaths for Course.
     * @param amount amount of deaths
     */
    public void setMaximumDeaths(int amount) {
        this.set(MAX_DEATHS, amount);
    }

    /**
     * Get the Maximum Time for Course.
     * Maximum number of seconds a player can accumulate before a loss.
     * @return maximum time in seconds
     */
    public int getMaximumTime() {
        return this.getInt(MAX_TIME);
    }

    /**
     * Check if the Course has a Maximum Time set.
     * @return course has Maximum Time
     */
    public boolean hasMaximumTime() {
        return getMaximumTime() > 0;
    }

    /**
     * Set the Maximum Time for the Course.
     * @param seconds number of seconds
     */
    public void setMaximumTime(int seconds) {
        this.set(MAX_TIME, seconds);
    }

    /**
     * Get the Maximum Fall Ticks for Course.
     * Maximum number of fall ticks a player can accumulate before a death.
     * @return maximum time in seconds
     */
    public int getMaximumFallTicks() {
        return this.getInt(MAX_FALL_TICKS);
    }

    /**
     * Set the Maximum Fall Ticks.
     * @param ticks number of server ticks
     */
    public void setMaximumFallTicks(int ticks) {
        this.set(MAX_FALL_TICKS, ticks);
    }

    /**
     * Get Course Ready Status.
     * @return course ready status
     */
    public boolean getReadyStatus() {
        return this.getBoolean(READY);
    }

    /**
     * Set the Course Ready status.
     * @param ready ready status
     */
    public void setReadyStatus(boolean ready) {
        this.set(READY, ready);
    }

    public boolean toggleReadyStatus() {
        setReadyStatus(!getReadyStatus());
        return getReadyStatus();
    }

    /**
     * Check if Course has a Material Prize.
     * @return course has Material prize
     */
    public boolean hasMaterialPrize() {
        return this.contains("Prize.Material");
    }

    /**
     * Get Material Prize for Course.
     * @return material of reward
     */
    @Nullable
    public Material getMaterialPrize() {
        return MaterialUtils.lookupMaterial(this.get("Prize.Material", null));
    }

    /**
     * Get amount of Material prize for Course.
     * @return amount of material prize
     */
    public int getMaterialPrizeAmount() {
        return this.getInt("Prize.Amount");
    }

    /**
     * Set the Material Prize for the Course.
     * The Material and Amount to be rewarded for finishing the Course.
     * @param materialName prize material
     * @param amount prize amount
     */
    public void setMaterialPrize(@NotNull String materialName, int amount) {
        this.set("Prize.Material", materialName);
        this.set("Prize.Amount", amount);
    }

    /**
     * Get the amount of XP prize for the Course.
     * @return XP prize
     */
    public int getXpPrize() {
        return this.getInt("Prize.XP");
    }

    /**
     * Set the amount of XP to reward for completing the Course.
     * @param amount xp amount
     */
    public void setXpPrize(int amount) {
        this.set("Prize.XP", amount);
    }

    /**
     * Get the number of times the Course has been Completed.
     * @return number of completions
     */
    public int getCompletions() {
        return this.getInt("Completed");
    }

    /**
     * Get the percentage of Views to Completions for Course.
     * @return percentage of completions
     */
    public double getCompletionPercent() {
        return Math.round((getCompletions() * 1.0 / getViews()) * 100);
    }

    /**
     * Increment the number of Completions for the Course.
     */
    public void incrementCompletions() {
        this.set("Completed", getCompletions() + 1);
    }

    /**
     * Get the number of a views a Course has had.
     * @return number of views
     */
    public int getViews() {
        return this.getInt("Views");
    }

    /**
     * Increment the number of Views for the Course.
     */
    public void incrementViews() {
        this.set("Views", getViews() + 1);
    }

    /**
     * Get the ParkourLevel reward for completing Course.
     * @return ParkourLevel reward
     */
    public int getRewardParkourLevel() {
        return this.getInt("RewardLevel");
    }

    /**
     * Has a ParkourLevel reward for completing Course.
     * @return has ParkourLevel reward
     */
    public boolean hasRewardParkourLevel() {
        return getRewardParkourLevel() > 0;
    }

    /**
     * Set the ParkourLevel reward for completing Course.
     * @param level ParkourLevel reward
     */
    public void setRewardParkourLevel(int level) {
        this.set("RewardLevel", level);
    }

    /**
     * Get the ParkourLevel increase reward for completing Course.
     * This is the number added to their current ParkourLevel.
     * @return ParkourLevel increase reward
     */
    public int getRewardParkourLevelIncrease() {
        return this.getInt("RewardLevelAdd");
    }

    /**
     * Has a ParkourLevel increase reward for completing Course.
     * @return has ParkourLevel increase reward
     */
    public boolean hasRewardParkourLevelIncrease() {
        return getRewardParkourLevelIncrease() > 0;
    }

    /**
     * Set the ParkourLevel increase reward for completing Course.
     * @param amount ParkourLevel increase reward
     */
    public void setRewardParkourLevelIncrease(@NotNull String amount) {
        this.set("RewardLevelAdd", Integer.parseInt(amount));
    }

    /**
     * Get the Reward Once status of the Course.
     * @return reward once status
     */
    public boolean getRewardOnce() {
        return this.getBoolean("RewardOnce");
    }

    /**
     * Set the Reward Once status for the Course.
     * @param enabled reward once enabled
     */
    public void setRewardOnce(boolean enabled) {
        this.set("RewardOnce", enabled);
    }

    /**
     * Toggle the Reward Once status of the Course.
     */
    public boolean toggleRewardOnce() {
        setRewardOnce(!getRewardOnce());
        return getRewardOnce();
    }

    /**
     * Get the Challenge Only status of the Course.
     * @return challenge only status
     */
    public boolean getChallengeOnly() {
        return this.getBoolean("ChallengeOnly");
    }

    /**
     * Set the Challenge Only status of the Course.
     * @param enabled challenge only enabled
     */
    public void setChallengeOnly(boolean enabled) {
        this.set("ChallengeOnly", enabled);
    }

    /**
     * Toggle the Challenge Only status of the Course.
     */
    public boolean toggleChallengeOnly() {
        setChallengeOnly(!getChallengeOnly());
        return getChallengeOnly();
    }

    /**
     * Get Reward Delay in Hours for the Course.
     * @return number of hours delay for Course
     */
    public double getRewardDelay() {
        return this.getDouble("RewardDelay");
    }

    /**
     * Check if the Course has a Reward Delay set.
     * @return course has reward delay
     */
    public boolean hasRewardDelay() {
        return getRewardDelay() > 0;
    }

    /**
     * Set the Reward Delay for the Course.
     * @param rewardDelay number of hours delay
     */
    public void setRewardDelay(double rewardDelay) {
        this.set("RewardDelay", rewardDelay);
    }

    /**
     * Get number of Parkoins awarded for completing the Course.
     * @return Parkoins to reward
     */
    public double getRewardParkoins() {
        return this.getDouble("Parkoins");
    }

    /**
     * Set number of Parkoins awarded for completing the Course.
     * @param parkoins Parkoins to reward
     */
    public void setRewardParkoins(double parkoins) {
        this.set("Parkoins", parkoins);
    }

    /**
     * Get Join Items for Course.
     * Each Join item has customisable and optional settings.
     * Build the list of each Material in an ItemStack based on options.
     * @return populated Join Items
     */
    @NotNull
    public List<ItemStack> getJoinItems() {
        List<ItemStack> joinItems = new ArrayList<>();
        FlatFileSection joinItemStack = this.getSection(courseName.toLowerCase() + ".JoinItems");

        Set<String> materials = joinItemStack.singleLayerKeySet();

        for (String materialName : materials) {
            Material material = MaterialUtils.lookupMaterial(materialName);
            if (material == null) {
                continue;
            }

            int amount = joinItemStack.getInt(materialName + ".Amount");
            String displayName = joinItemStack.getString(materialName + ".Label");
            boolean unbreakable = joinItemStack.getBoolean(materialName + ".Unbreakable");

            ItemStack itemStack = new ItemStack(material, amount);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (displayName != null) {
                itemMeta.setDisplayName(displayName);
            }
            if (unbreakable) {
                itemMeta.setUnbreakable(true);
            }
            itemStack.setItemMeta(itemMeta);
            joinItems.add(itemStack);
        }

        return joinItems;
    }

    /**
     * Add a Join Item to the Course.
     * @param material material to add
     * @param amount amount of material
     * @param label item label
     * @param unbreakable unbreakable flag
     */
    public void addJoinItem(@NotNull Material material, int amount,
                            @Nullable String label, boolean unbreakable) {
        courseName = courseName.toLowerCase();
        this.set("JoinItems." + material.name() + ".Amount", amount);
        this.set("JoinItems." + material.name() + ".Label", label);
        this.set("JoinItems." + material.name() + ".Unbreakable", unbreakable);
    }

    /**
     * Get the World the Course is in.
     * @return world name
     */
    @Nullable
    public String getStartingWorldName() {
        return this.get("Checkpoint.0.Location.world", null);
    }

    /**
     * Reset Prizes for Course completion.
     */
    public void resetPrizes() {
        this.remove("Prize");
    }

    /**
     * Reset the Links of the Course.
     * Removes any linked Lobby or Course.
     */
    public void resetLinks() {
        this.remove("LinkedLobby");
        this.remove("LinkedCourse");
    }

    /**
     * Get Potion ParkourMode Effects for Course.
     * The effects are stored in a "EFFECT,DURATION,AMPLIFIER" format to be used by XPotion library.
     * @return list of potion effects
     */
    @NotNull
    public List<String> getPotionParkourModeEffects() {
        return this.get("PotionParkourMode.Effects", null);
    }

    /**
     * Add a Potion ParkourMode Effect to the Course.
     * @param potionEffectType potion effect type name
     * @param durationAmplifier duration and amplifier provided
     */
    public void addPotionParkourModeEffect(@NotNull String potionEffectType,
                                           @Nullable String durationAmplifier) {
        List<String> potionEffects = getPotionParkourModeEffects();
        String potionEffect = potionEffectType;

        if (durationAmplifier != null) {
            potionEffect += "," + durationAmplifier;
        }

        potionEffects.add(potionEffect);
        this.set("PotionParkourMode.Effects", potionEffects);
    }

    /**
     * Get Potion ParkourMode join message.
     * @return join message
     */
    @Nullable
    public String getPotionJoinMessage() {
        return this.get("PotionParkourMode.JoinMessage", null);
    }

    /**
     * Check if the Course has a Potion join message.
     * @return course has potion join message
     */
    public boolean hasPotionJoinMessage() {
        return this.contains("PotionParkourMode.JoinMessage");
    }

    /**
     * Set Potion ParkourMode join message.
     * @param joinMessage join message
     */
    public void setPotionJoinMessage(@Nullable String joinMessage) {
        this.set("PotionParkourMode.JoinMessage", joinMessage);
    }

    /**
     * Get Economic Reward for finishing Course.
     * @return finish reward
     */
    public double getEconomyFinishReward() {
        return this.getDouble("EconomyFinishReward");
    }

    /**
     * Check if the Course has a Economy Finish Reward.
     * @return has finish reward
     */
    public boolean hasEconomyFinishReward() {
        return this.contains("EconomyFinishReward");
    }

    /**
     * Set Economy Finish reward.
     * @param finishReward finish reward
     */
    public void setEconomyFinishReward(@Nullable Double finishReward) {
        this.set("EconomyFinishReward", finishReward);
    }

    /**
     * Get Economic Fee for joining Course.
     * @return joining fee
     */
    public double getEconomyJoiningFee() {
        return this.getDouble("EconomyJoiningFee");
    }

    /**
     * Check if the Course has a Economy Joining fee.
     * @return has joining fee
     */
    public boolean hasEconomyJoiningFee() {
        return this.contains("EconomyJoiningFee");
    }

    /**
     * Set Economy Joining fee.
     * @param joinFee join fee
     */
    public void setEconomyJoiningFee(@Nullable Double joinFee) {
        this.set("EconomyJoiningFee", joinFee);
    }

    /**
     * Get number of Player limit for Course.
     * @return player limit
     */
    public int getPlayerLimit() {
        return this.getInt("PlayerLimit");
    }

    /**
     * Check if Course has Player Limit.
     * @return course has player limit
     */
    public boolean hasPlayerLimit() {
        return getPlayerLimit() > 0;
    }

    /**
     * Set Player limit for Course.
     * @param limit player limit
     */
    public void setPlayerLimit(int limit) {
        this.set("PlayerLimit", limit);
    }

    /**
     * Get Resumable flag for Course.
     * @return course resumable
     */
    public boolean getResumable() {
        return this.getOrDefault("Resumable", true);
    }

    /**
     * Set the Resumable flag for Course.
     * @param value flag value
     */
    public void setResumable(boolean value) {
        this.set("Resumable", value);
    }

    public boolean toggleResumable() {
        setResumable(!getResumable());
        return getResumable();
    }

    /**
     * Get Resumable flag for Course.
     * @return course resumable
     */
    public boolean getManualCheckpoints() {
        return this.getOrDefault(MANUAL_CHECKPOINTS, true);
    }

    /**
     * Set the Resumable flag for Course.
     * @param value flag value
     */
    public void setManualCheckpoints(boolean value) {
        this.set(MANUAL_CHECKPOINTS, value);
    }

    public boolean toggleManualCheckpoints() {
        setManualCheckpoints(!getManualCheckpoints());
        return getManualCheckpoints();
    }

    /**
     * Get the Event Message for this Course.
     * Possible events found within {@link ParkourEventType}.
     * @param eventType event type name
     * @return matching event message
     */
    @Nullable
    public String getEventMessage(@NotNull ParkourEventType eventType) {
        return this.get("Message." + eventType.getConfigEntry(), null);
    }

    /**
     * Check if the Course has Event Type Message for the Course.
     * @param eventType selected {@link ParkourEventType}
     * @return course has the event type commands
     */
    public boolean hasEventMessage(@NotNull ParkourEventType eventType) {
        return this.contains("Message." + eventType.getConfigEntry());
    }

    /**
     * Set Event Message for Course.
     * Possible events found within {@link ParkourEventType}.
     * @param eventType event type name
     * @param value message value
     */
    public void setEventMessage(@NotNull ParkourEventType eventType, @Nullable String value) {
        this.set("Message." + eventType.getConfigEntry(), value);
    }

    /**
     * Get the Event Type Commands for the Course.
     * The Commands found will be run during the matching Event.
     * @param eventType selected {@link ParkourEventType}
     * @return commands for that event
     */
    @NotNull
    public List<String> getEventCommands(@NotNull ParkourEventType eventType) {
        return this.get( "Command." + eventType.getConfigEntry(), new ArrayList<>());
    }

    /**
     * Check if the Course has Event Type Commands for the Course.
     * @param eventType selected {@link ParkourEventType}
     * @return course has the event type commands
     */
    public boolean hasEventCommands(@NotNull ParkourEventType eventType) {
        return this.contains("Command." + eventType.getConfigEntry());
    }

    /**
     * Add the Event Type Command to the Course.
     * @param eventType selected {@link ParkourEventType}
     * @param value command to run
     */
    public void addEventCommand(@NotNull ParkourEventType eventType,
                                @NotNull String value) {
        List<String> commands = getEventCommands(eventType);
        commands.add(value);
        this.set("Command." + eventType.getConfigEntry(), commands);
    }

    public void resetCourseData() {
        this.removeAll(this.singleLayerKeySet().stream().filter(property ->
                        !"Creator".equals(property)
                                && !"Checkpoint".equals(property)
                                && !"Name".equals(property))
                .toArray(String[]::new));
    }

    public void createCourseData(Player player) {
        this.set("Name", this.courseName);
        this.set("Creator", player.getName());
        createCheckpointData(player.getLocation(), 0);
    }

    /**
     * Create and persist Checkpoint data.
     * The Location will be used to store the location to teleport to, and to mark the position of the pressure plate.
     *
     * @param location checkpoint location
     * @param checkpoint checkpoint being saved
     */
    public void createCheckpointData(Location location, int checkpoint) {
        int maximumCheckpoints = Math.max(checkpoint, this.getInt("Checkpoints"));

        String prefix = "Checkpoint." + checkpoint + ".";
        this.getFileData().insert(prefix + "Location.x", location.getBlockX() + 0.5);
        this.getFileData().insert(prefix + "Location.y", location.getBlockY() + 0.5);
        this.getFileData().insert(prefix + "Location.z", location.getBlockZ() + 0.5);
        this.getFileData().insert(prefix + "Location.yaw", location.getYaw());
        this.getFileData().insert(prefix + "Location.pitch", location.getPitch());
        this.getFileData().insert(prefix + "Location.world", location.getWorld().getName());

        this.getFileData().insert(prefix + "CheckpointX", location.getBlockX());
        this.getFileData().insert(prefix + "CheckpointY", location.getBlockY() - 1);
        this.getFileData().insert(prefix + "CheckpointZ", location.getBlockZ());

        this.set("Checkpoints", maximumCheckpoints);
    }

    public void deleteCheckpoint() {
        Json courseConfig = this;
        int checkpoint = courseConfig.getInt(CHECKPOINTS);

        if (checkpoint > 0) {
            courseConfig.remove("Checkpoint." + checkpoint);
            courseConfig.set("Checkpoints", checkpoint - 1);
        }
    }

    /**
     * Displays all information stored about Course.
     * Dynamically displays data based on what has been set.
     * @param commandSender command sender
     * @param courseNameRaw course name
     */
    public static void displayCourseInfo(@NotNull CommandSender commandSender, String courseNameRaw) {
        if (!Parkour.getInstance().getCourseManager().doesCourseExist(courseNameRaw)) {
            TranslationUtils.sendValueTranslation("Error.NoExist", courseNameRaw, commandSender);
            return;
        }
        String courseName = courseNameRaw.toLowerCase();
        TranslationUtils.sendHeading(StringUtils.standardizeText(courseName) + " statistics", commandSender);
        CourseConfig config = getConfig(courseName);

        sendConditionalValue(commandSender, "Display Name", config.hasCourseDisplayName(), config.getCourseDisplayName());

        sendValue(commandSender, "Views", config.getViews());
        sendValue(commandSender, "Completions", config.getCompletions()
                + " (" + config.getCompletionPercent() + "%)");
        sendValue(commandSender, CHECKPOINTS, config.getCheckpointAmount());
        sendValue(commandSender, "Creator", config.getCreator());
        sendValue(commandSender, "Ready Status", String.valueOf(config.getReadyStatus()));
        sendValue(commandSender, "Challenge Only", String.valueOf(config.getChallengeOnly()));

        sendConditionalValue(commandSender, "Resumable", !Parkour.getDefaultConfig().isLeaveDestroyCourseProgress(),
                String.valueOf(config.getResumable()));
        sendConditionalValue(commandSender, "Minimum ParkourLevel", config.getMinimumParkourLevel());
        sendConditionalValue(commandSender, "ParkourLevel Reward", config.getRewardParkourLevel());
        sendConditionalValue(commandSender, "ParkourLevel Reward Increase", config.getRewardParkourLevelIncrease());
        sendConditionalValue(commandSender, "Parkoins Reward", config.getRewardParkoins());
        sendConditionalValue(commandSender, "Max Deaths", config.getMaximumDeaths());
        sendConditionalValue(commandSender, "Max Time", config.hasMaximumTime(),
                DateTimeUtils.convertSecondsToTime(config.getMaximumTime()));

        sendConditionalValue(commandSender, "Linked Course", config.hasLinkedCourse(), config.getLinkedCourse());
        sendConditionalValue(commandSender, "Linked Lobby", config.hasLinkedLobby(), config.getLinkedLobby());
        sendConditionalValue(commandSender, PARKOUR_KIT, config.hasParkourKit(), config.getParkourKit());
        sendConditionalValue(commandSender, PARKOUR_MODE, config.hasParkourMode(), config.getParkourModeName());

        sendConditionalValue(commandSender, "Material Prize",
                config.hasMaterialPrize() && config.getMaterialPrizeAmount() > 0,
                config.getMaterialPrize() + " x " + config.getMaterialPrizeAmount());
        sendConditionalValue(commandSender, "XP Prize", config.getXpPrize());

        if (Parkour.getInstance().getEconomyApi().isEnabled()) {
            sendConditionalValue(commandSender, "Join Fee", config.getEconomyJoiningFee());
            sendConditionalValue(commandSender, "Economy Reward", config.getEconomyFinishReward());
        }

        if (config.hasRewardDelay()) {
            sendValue(commandSender, "Reward Cooldown", DateTimeUtils.convertMillisecondsToDateTime(
                    DateTimeUtils.convertHoursToMilliseconds(config.getRewardDelay())));

            if (commandSender instanceof Player && !Parkour.getInstance().getPlayerManager().hasPrizeCooldownDurationPassed(
                    (Player) commandSender, courseName, false)) {
                sendValue(commandSender, "Cool down Remaining", DateTimeUtils.getDelayTimeRemaining((Player) commandSender, courseName));
            }
        }
        for (ParkourEventType value : ParkourEventType.values()) {
            if (config.hasEventCommands(value)) {
                TranslationUtils.sendMessage(commandSender, "&3" + value.getConfigEntry() + " Commands", false);
                config.getEventCommands(value).forEach(commandSender::sendMessage);
            }
        }
    }
}

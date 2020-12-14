package io.github.a5h73y.parkour.type.course;

import static io.github.a5h73y.parkour.utility.TranslationUtils.sendConditionalValue;
import static io.github.a5h73y.parkour.utility.TranslationUtils.sendValue;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.enums.ParkourEventType;
import io.github.a5h73y.parkour.enums.ParkourMode;
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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Course Information Utility class.
 * Convenience methods for accessing the course configuration file.
 */
public class CourseInfo {

    /**
     * Get List of all Parkour course names.
     * @return List Parkour course names
     */
    public static List<String> getAllCourseNames() {
        return getCourseConfig().getStringList("Courses");
    }

    /**
     * Get the linked Course of the specified course.
     * The result may still need to be verified as existing.
     * @param courseName course name
     * @return linkedCourse
     */
    @Nullable
    public static String getLinkedCourse(@NotNull String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".LinkedCourse");
    }

    /**
     * Check if Course is linked to another Course.
     * @param courseName course name
     * @return if linked course is found
     */
    public static boolean hasLinkedCourse(@NotNull String courseName) {
        return getCourseConfig().contains(courseName.toLowerCase() + ".LinkedCourse");
    }

    /**
     * Set the linked Course destination to a different Course.
     * @param courseName course name
     * @param linkedCourse linked course name
     */
    public static void setLinkedCourse(@NotNull String courseName, @NotNull String linkedCourse) {
        getCourseConfig().set(courseName.toLowerCase() + ".LinkedCourse", linkedCourse.toLowerCase());
        persistChanges();
    }

    /**
     * Find the linked Lobby of the Course.
     * The result may still need to be verified as existing.
     * @param courseName course name
     * @return linked Lobby name
     */
    @Nullable
    public static String getLinkedLobby(@NotNull String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".LinkedLobby");
    }

    /**
     * Check if Course is linked to a Lobby.
     * @param courseName course name
     * @return if linked lobby is found
     */
    public static boolean hasLinkedLobby(@NotNull String courseName) {
        return getCourseConfig().contains(courseName.toLowerCase() + ".LinkedLobby");
    }

    /**
     * Set the linked Course destination to a Lobby.
     * @param courseName course name
     * @param lobbyName lobby name
     */
    public static void setLinkedLobby(@NotNull String courseName, @NotNull String lobbyName) {
        getCourseConfig().set(courseName.toLowerCase() + ".LinkedLobby", lobbyName.toLowerCase());
        persistChanges();
    }

    /**
     * Get the ParkourMode name for the Course.
     * @param courseName course name
     * @return the parkour mode name set
     */
    public static String getParkourModeName(@NotNull String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".Mode", ParkourMode.NONE.name());
    }

    /**
     * Get the matching {@link ParkourMode} for the Course.
     * @param courseName course name
     * @return the ParkourMode
     */
    public static ParkourMode getCourseMode(@NotNull String courseName) {
        return ParkourMode.valueOf(getParkourModeName(courseName).toUpperCase());
    }

    /**
     * Check if the Course has a ParkourMode defined.
     * None is not considered a ParkourMode.
     * @param courseName courseName
     * @return course has ParkourMode
     */
    public static boolean hasParkourMode(@NotNull String courseName) {
        return !ParkourMode.NONE.name().equals(getParkourModeName(courseName));
    }

    /**
     * Set the {@link ParkourMode} for the Course.
     * @param courseName course name
     * @param mode ParkourMode
     */
    public static void setParkourMode(@NotNull String courseName, @NotNull ParkourMode mode) {
        getCourseConfig().set(courseName.toLowerCase() + ".Mode", mode.name());
        persistChanges();
    }

    /**
     * Get the number of Checkpoints on a Course.
     * @param courseName course name
     * @return number of checkpoints
     */
    public static int getCheckpointAmount(@NotNull String courseName) {
        return Parkour.getConfig(ConfigType.CHECKPOINTS).getInt(courseName.toLowerCase() + ".Checkpoints");
    }

    /**
     * Get the Creator's name of the Course.
     * @param courseName course name
     * @return creator's name
     */
    @Nullable
    public static String getCreator(@NotNull String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".Creator");
    }

    /**
     * Set the Creator's name of the Course.
     * @param courseName course name
     * @param playerName player's name
     */
    public static void setCreator(@NotNull String courseName, @NotNull String playerName) {
        getCourseConfig().set(courseName.toLowerCase() + ".Creator", playerName);
        persistChanges();
    }

    /**
     * Get the Minimum ParkourLevel of a Course.
     * @param courseName course name
     * @return minimum ParkourLevel to join Course
     */
    public static int getMinimumParkourLevel(@NotNull String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".MinimumLevel");
    }

    /**
     * Check if the Course has a Minimum ParkourLevel.
     * @param courseName course name
     * @return course has minimum ParkourLevel
     */
    public static boolean hasMinimumParkourLevel(@NotNull String courseName) {
        return getMinimumParkourLevel(courseName) > 0;
    }

    /**
     * Set the Minimum ParkourLevel for a Course.
     * @param courseName course name
     * @param parkourLevel minimum parkour level
     */
    public static void setMinimumParkourLevel(@NotNull String courseName, int parkourLevel) {
        getCourseConfig().set(courseName.toLowerCase() + ".MinimumLevel", parkourLevel);
        persistChanges();
    }

    /**
     * Get ParkourKit name for the Course.
     * @param courseName course name
     * @return ParkourKit name.
     */
    @Nullable
    public static String getParkourKit(@NotNull String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".ParkourKit", Constants.DEFAULT);
    }

    /**
     * Check if the Course has a ParkourKit set.
     * @param courseName course name
     * @return course has ParkourKit
     */
    public static boolean hasParkourKit(@NotNull String courseName) {
        return getCourseConfig().contains(courseName.toLowerCase() + ".ParkourKit");
    }

    /**
     * Set the ParkourKit name for the Course.
     * @param courseName course name
     * @param parkourKitName parkour kit name
     */
    public static void setParkourKit(@NotNull String courseName, @NotNull String parkourKitName) {
        getCourseConfig().set(courseName.toLowerCase() + ".ParkourKit", parkourKitName.toLowerCase());
        persistChanges();
    }

    /**
     * Get the Maximum number of Deaths for a Course.
     * Maximum number of deaths a player can accumulate before a loss.
     * @param courseName course name
     * @return maximum deaths
     */
    public static int getMaximumDeaths(@NotNull String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".MaxDeaths", -1);
    }

    /**
     * Check if the Course has a Maximum number of Deaths set.
     * @param courseName course name
     * @return course has maximum deaths set
     */
    public static boolean hasMaximumDeaths(@NotNull String courseName) {
        return getMaximumDeaths(courseName) > 0;
    }

    /**
     * Set the Maximum number of Deaths for Course.
     * @param courseName course name
     * @param amount amount of deaths
     */
    public static void setMaximumDeaths(@NotNull String courseName, int amount) {
        getCourseConfig().set(courseName.toLowerCase() + ".MaxDeaths", amount);
        persistChanges();
    }

    /**
     * Get the Maximum Time for Course.
     * Maximum number of seconds a player can accumulate before a loss.
     * @param courseName course name
     * @return maximum time in seconds
     */
    public static int getMaximumTime(@NotNull String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".MaxTime", -1);
    }

    /**
     * Check if the Course has a Maximum Time set.
     * @param courseName course name
     * @return course has Maximum Time
     */
    public static boolean hasMaximumTime(@NotNull String courseName) {
        return getMaximumTime(courseName) > 0;
    }

    /**
     * Set the Maximum Time for the Course.
     * @param courseName course name
     * @param seconds number of seconds
     */
    public static void setMaximumTime(@NotNull String courseName, int seconds) {
        getCourseConfig().set(courseName.toLowerCase() + ".MaxTime", seconds);
        persistChanges();
    }

    /**
     * Get Course Ready Status.
     * @param courseName course name
     * @return course ready status
     */
    public static boolean getReadyStatus(@NotNull String courseName) {
        return getCourseConfig().getBoolean(courseName.toLowerCase() + ".Ready", false);
    }

    /**
     * Set the Course Ready status.
     * @param courseName course name
     * @param ready ready status
     */
    public static void setReadyStatus(@NotNull String courseName, boolean ready) {
        getCourseConfig().set(courseName.toLowerCase() + ".Ready", ready);
        persistChanges();
    }

    public static void toggleReadyStatus(@NotNull String courseName) {
        setReadyStatus(courseName, !getReadyStatus(courseName));
    }

    /**
     * Get Material Prize for Course.
     * @param courseName course name
     * @return material of reward
     */
    @Nullable
    public static Material getMaterialPrize(@NotNull String courseName) {
        return MaterialUtils.lookupMaterial(getCourseConfig().getString(courseName.toLowerCase() + ".Prize.Material"));
    }

    /**
     * Get amount of Material prize for Course.
     * @param courseName course name
     * @return amount of material prize
     */
    public static int getMaterialPrizeAmount(@NotNull String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".Prize.Amount", 0);
    }

    /**
     * Check if Course has a Material Prize.
     * @param courseName course name
     * @return course has Material prize
     */
    public static boolean hasMaterialPrize(@NotNull String courseName) {
        return getCourseConfig().contains(courseName.toLowerCase() + ".Prize.Material");
    }

    /**
     * Set the Material Prize for the Course.
     * The Material and Amount to be rewarded for finishing the Course.
     * @param courseName course name
     * @param materialName prize material
     * @param amount prize amount
     */
    public static void setMaterialPrize(@NotNull String courseName, @NotNull String materialName, int amount) {
        getCourseConfig().set(courseName.toLowerCase() + ".Prize.Material", materialName);
        getCourseConfig().set(courseName.toLowerCase() + ".Prize.Amount", amount);
        persistChanges();
    }

    /**
     * Get the amount of XP prize for the Course.
     * @param courseName course name
     * @return XP prize
     */
    public static int getXpPrize(@NotNull String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".Prize.XP");
    }

    /**
     * Set the amount of XP to reward for completing the Course.
     * @param courseName course name
     * @param amount xp amount
     */
    public static void setXpPrize(@NotNull String courseName, int amount) {
        getCourseConfig().set(courseName.toLowerCase() + ".Prize.XP", amount);
        persistChanges();
    }

    /**
     * Get the number of times the Course has been Completed.
     * @param courseName course name
     * @return number of completions
     */
    public static int getCompletions(@NotNull String courseName) {
        return getCourseConfig().getInt(courseName + ".Completed", 0);
    }

    /**
     * Get the percentage of Views to Completions for Course.
     * @param courseName course
     * @return percentage of completions
     */
    public static double getCompletionPercent(@NotNull String courseName) {
        return Math.round((getCompletions(courseName) * 1.0 / getViews(courseName)) * 100);
    }

    /**
     * Increment the number of Completions for the Course.
     * @param courseName course name
     */
    public static void incrementCompletions(@NotNull String courseName) {
        getCourseConfig().set(courseName.toLowerCase() + ".Completed", getCompletions(courseName) + 1);
        persistChanges();
    }

    /**
     * Get the number of a views a Course has had.
     * @param courseName course name
     * @return number of views
     */
    public static int getViews(@NotNull String courseName) {
        return getCourseConfig().getInt(courseName + ".Views", 0);
    }

    /**
     * Increment the number of Views for the Course.
     * @param courseName course name
     */
    public static void incrementViews(@NotNull String courseName) {
        getCourseConfig().set(courseName.toLowerCase() + ".Views", getViews(courseName) + 1);
    }

    /**
     * Get the ParkourLevel reward for completing Course.
     * @param courseName course name
     * @return ParkourLevel reward
     */
    public static int getRewardParkourLevel(@NotNull String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".RewardLevel");
    }

    /**
     * Has a ParkourLevel reward for completing Course.
     * @param courseName course name
     * @return has ParkourLevel reward
     */
    public static boolean hasRewardParkourLevel(@NotNull String courseName) {
        return getRewardParkourLevel(courseName) > 0;
    }

    /**
     * Set the ParkourLevel reward for completing Course.
     * @param courseName course name
     * @param level ParkourLevel reward
     */
    public static void setRewardParkourLevel(@NotNull String courseName, int level) {
        getCourseConfig().set(courseName.toLowerCase() + ".RewardLevel", level);
        persistChanges();
    }

    /**
     * Get the ParkourLevel increase reward for completing Course.
     * This is the number added to their current ParkourLevel.
     * @param courseName course name
     * @return ParkourLevel increase reward
     */
    public static int getRewardParkourLevelIncrease(@NotNull String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".RewardLevelAdd");
    }

    /**
     * Has a ParkourLevel increase reward for completing Course.
     * @param courseName course name
     * @return has ParkourLevel increase reward
     */
    public static boolean hasRewardParkourLevelIncrease(@NotNull String courseName) {
        return getRewardParkourLevelIncrease(courseName) > 0;
    }

    /**
     * Set the ParkourLevel increase reward for completing Course.
     * @param courseName course name
     * @param amount ParkourLevel increase reward
     */
    public static void setRewardParkourLevelIncrease(@NotNull String courseName, @NotNull String amount) {
        getCourseConfig().set(courseName.toLowerCase() + ".RewardLevelAdd", Integer.parseInt(amount));
        persistChanges();
    }

    /**
     * Get the Reward Once status of the Course.
     * @param courseName course name
     * @return reward once status
     */
    public static boolean getRewardOnce(@NotNull String courseName) {
        return getCourseConfig().getBoolean(courseName.toLowerCase() + ".RewardOnce");
    }

    /**
     * Set the Reward Once status for the Course.
     * @param courseName course name
     * @param enabled reward once enabled
     */
    public static void setRewardOnce(@NotNull String courseName, boolean enabled) {
        getCourseConfig().set(courseName.toLowerCase() + ".RewardOnce", enabled);
        persistChanges();
    }

    /**
     * Toggle the Reward Once status of the Course.
     * @param courseName course name
     */
    public static void toggleRewardOnce(@NotNull String courseName) {
        setRewardOnce(courseName, !getRewardOnce(courseName));
    }

    /**
     * Get the Challenge Only status of the Course.
     * @param courseName course name
     * @return challenge only status
     */
    public static boolean getChallengeOnly(@NotNull String courseName) {
        return getCourseConfig().getBoolean(courseName.toLowerCase() + ".ChallengeOnly");
    }

    /**
     * Set the Challenge Only status of the Course.
     * @param courseName course name
     * @param enabled challenge only enabled
     */
    public static void setChallengeOnly(@NotNull String courseName, boolean enabled) {
        getCourseConfig().set(courseName.toLowerCase() + ".ChallengeOnly", enabled);
        persistChanges();
    }

    /**
     * Toggle the Challenge Only status of the Course.
     * @param courseName course name
     */
    public static void toggleChallengeOnly(@NotNull String courseName) {
        setChallengeOnly(courseName, !getChallengeOnly(courseName));
    }

    /**
     * Get Reward Delay in Hours for the Course.
     * @param courseName course name
     * @return number of hours delay for Course
     */
    public static double getRewardDelay(@NotNull String courseName) {
        return getCourseConfig().getDouble(courseName.toLowerCase() + ".RewardDelay", 0);
    }

    /**
     * Check if the Course has a Reward Delay set.
     * @param courseName course name
     * @return course has reward delay
     */
    public static boolean hasRewardDelay(@NotNull String courseName) {
        return getRewardDelay(courseName) > 0;
    }

    /**
     * Set the Reward Delay for the Course.
     * @param courseName course name
     * @param rewardDelay number of hours delay
     */
    public static void setRewardDelay(@NotNull String courseName, double rewardDelay) {
        getCourseConfig().set(courseName.toLowerCase() + ".RewardDelay", rewardDelay);
        persistChanges();
    }

    /**
     * Get number of Parkoins awarded for completing the Course.
     * @param courseName course name
     * @return Parkoins to reward
     */
    public static double getRewardParkoins(@NotNull String courseName) {
        return getCourseConfig().getDouble(courseName.toLowerCase() + ".Parkoins", 0);
    }

    /**
     * Set number of Parkoins awarded for completing the Course.
     * @param courseName course name
     * @param parkoins Parkoins to reward
     */
    public static void setRewardParkoins(@NotNull String courseName, double parkoins) {
        getCourseConfig().set(courseName.toLowerCase() + ".Parkoins", parkoins);
        persistChanges();
    }

    /**
     * Get Join Items for Course.
     * Each Join item has customisable and optional settings.
     * Build the list of each Material in an ItemStack based on options.
     * @param courseName course name
     * @return populated Join Items
     */
    @NotNull
    public static List<ItemStack> getJoinItems(@NotNull String courseName) {
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

    /**
     * Add a Join Item to the Course.
     * @param courseName course name
     * @param material material to add
     * @param amount amount of material
     * @param label item label
     * @param unbreakable unbreakable flag
     */
    public static void addJoinItem(@NotNull String courseName, @NotNull Material material, int amount,
                                   @Nullable String label, boolean unbreakable) {
        courseName = courseName.toLowerCase();

        getCourseConfig().set(courseName + ".JoinItems." + material.name() + ".Amount", amount);
        getCourseConfig().set(courseName + ".JoinItems." + material.name() + ".Label", label);
        getCourseConfig().set(courseName + ".JoinItems." + material.name() + ".Unbreakable", unbreakable);
        persistChanges();
    }

    /**
     * Get the World the Course is in.
     * @param courseName course name
     * @return world name
     */
    @Nullable
    public static String getWorld(@NotNull String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".World");
    }

    /**
     * Delete the Course and all associated data.
     * @param courseNameInput course name
     */
    public static void deleteCourse(@NotNull String courseNameInput) {
        String courseName = courseNameInput.toLowerCase();

        List<String> courseList = getAllCourseNames();
        courseList.remove(courseName);
        getCourseConfig().set(courseName, null);
        getCourseConfig().set("Courses", courseList);
        persistChanges();

        Parkour.getInstance().getCheckpointManager().deleteCheckpointData(courseName);
        Parkour.getInstance().getDatabase().deleteCourseAndReferences(courseName);
        Parkour.getInstance().getCourseManager().clearCache(courseName);

        PlayerInfo.removeCompletedCourse(courseName);
    }

    /**
     * Reset Prizes for Course completion.
     * @param courseName course name
     */
    public static void resetPrizes(@NotNull String courseName) {
        getCourseConfig().set(courseName.toLowerCase() + ".Prize", null);
        persistChanges();
    }

    /**
     * Reset the Links of the Course.
     * Removes any linked Lobby or Course.
     * @param courseName course name
     */
    public static void resetLinks(@NotNull String courseName) {
        getCourseConfig().set(courseName.toLowerCase() + ".LinkedLobby", null);
        getCourseConfig().set(courseName.toLowerCase() + ".LinkedCourse", null);
        persistChanges();
    }

    /**
     * Displays all information stored about Course.
     * Dynamically displays data based on what has been set.
     * @param sender requesting player
     * @param courseName course name
     */
    public static void displayCourseInfo(@NotNull CommandSender sender, @NotNull String courseName) {
        if (!Parkour.getInstance().getCourseManager().doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation("Error.NoExist", courseName, sender);
            return;
        }

        TranslationUtils.sendHeading(StringUtils.standardizeText(courseName) + " statistics", sender);

        sendValue(sender, "Views", getViews(courseName));
        sendValue(sender, "Completions", getCompletions(courseName)
                + " (" + getCompletionPercent(courseName) + "%)");
        sendValue(sender, "Checkpoints", getCheckpointAmount(courseName));
        sendValue(sender, "Creator", getCreator(courseName));
        sendValue(sender, "Ready Status", String.valueOf(getReadyStatus(courseName)));

        sendConditionalValue(sender, "Minimum ParkourLevel", getMinimumParkourLevel(courseName));
        sendConditionalValue(sender, "ParkourLevel Reward", getRewardParkourLevel(courseName));
        sendConditionalValue(sender, "ParkourLevel Reward Increase", getRewardParkourLevelIncrease(courseName));
        sendConditionalValue(sender, "Parkoins Reward", getRewardParkoins(courseName));
        sendConditionalValue(sender, "Max Deaths", getMaximumDeaths(courseName));
        sendConditionalValue(sender, "Max Time", hasMaximumTime(courseName),
                DateTimeUtils.convertSecondsToTime(getMaximumTime(courseName)));

        sendConditionalValue(sender, "Linked Course", hasLinkedCourse(courseName), getLinkedCourse(courseName));
        sendConditionalValue(sender, "Linked Lobby", hasLinkedLobby(courseName), getLinkedLobby(courseName));
        sendConditionalValue(sender, "ParkourKit", hasParkourKit(courseName), getParkourKit(courseName));
        sendConditionalValue(sender, "ParkourMode", hasParkourMode(courseName), getParkourModeName(courseName));

        sendConditionalValue(sender, "Material Prize",
                hasMaterialPrize(courseName) && getMaterialPrizeAmount(courseName) > 0,
                getMaterialPrize(courseName) + " x " + getMaterialPrizeAmount(courseName));
        sendConditionalValue(sender, "XP Prize", getXpPrize(courseName));

        if (Parkour.getInstance().getEconomyApi().isEnabled()) {
            sendConditionalValue(sender, "Join Fee", getEconomyJoiningFee(courseName));
            sendConditionalValue(sender, "Economy Reward", getEconomyFinishReward(courseName));
        }

        if (hasRewardDelay(courseName) && Parkour.getDefaultConfig().isDisplayPrizeCooldown()) {
            sendValue(sender, "Reward Cooldown", DateTimeUtils.displayTimeRemaining(
                    DateTimeUtils.convertHoursToMilliseconds(getRewardDelay(courseName))));

            if (sender instanceof Player && !Parkour.getInstance().getPlayerManager().hasPrizeCooldownDurationPassed(
                    (Player) sender, courseName, false)) {
                sendValue(sender, "Cool down Remaining", DateTimeUtils.getTimeRemaining((Player) sender, courseName));
            }
        }
        for (ParkourEventType value : ParkourEventType.values()) {
            if (hasEventCommands(courseName, value)) {
                sender.sendMessage(ChatColor.AQUA + value.getConfigEntry() + " Commands");
                getEventCommands(courseName, value).forEach(sender::sendMessage);
            }
        }
    }

    /**
     * Get Potion ParkourMode Effects for Course.
     * The effects are stored in a "EFFECT,DURATION,AMPLIFIER" format to be used by XPotion library.
     * @param courseName course name
     * @return list of potion effects
     */
    @NotNull
    public static List<String> getPotionParkourModeEffects(@NotNull String courseName) {
        return getCourseConfig().getStringList(courseName.toLowerCase() + ".PotionParkourMode.Effects");
    }

    /**
     * Add a Potion ParkourMode Effect to the Course.
     * @param courseName course name
     * @param potionEffectType potion effect type name
     * @param durationAmplifier duration and amplifier provided
     */
    public static void addPotionParkourModeEffect(@NotNull String courseName,
                                                  @NotNull String potionEffectType,
                                                  @Nullable String durationAmplifier) {
        List<String> potionEffects = getPotionParkourModeEffects(courseName);
        String potionEffect = potionEffectType;

        if (durationAmplifier != null) {
            potionEffect += "," + durationAmplifier;
        }

        potionEffects.add(potionEffect);
        getCourseConfig().set(courseName.toLowerCase() + ".PotionParkourMode.Effects", potionEffects);
        persistChanges();
    }

    /**
     * Get Potion ParkourMode join message.
     * @param courseName course name
     * @return join message
     */
    @Nullable
    public static String getPotionJoinMessage(@NotNull String courseName) {
        return getCourseConfig().getString(courseName.toLowerCase() + ".PotionParkourMode.JoinMessage");
    }

    /**
     * Check if the Course has a Potion join message.
     * @param courseName course name
     * @return course has potion join message
     */
    public static boolean hasPotionJoinMessage(@NotNull String courseName) {
        return getCourseConfig().contains(courseName.toLowerCase() + ".PotionParkourMode.JoinMessage");
    }

    /**
     * Set Potion ParkourMode join message.
     * @param courseName course name
     * @param joinMessage join message
     */
    public static void setPotionJoinMessage(@NotNull String courseName, @Nullable String joinMessage) {
        getCourseConfig().set(courseName.toLowerCase() + ".PotionParkourMode.JoinMessage", joinMessage);
        persistChanges();
    }

    /**
     * Get Economic Reward for finishing Course.
     * @param courseName course name
     * @return finish reward
     */
    public static int getEconomyFinishReward(@NotNull String courseName) {
        return Parkour.getConfig(ConfigType.ECONOMY).getInt("Price." + courseName.toLowerCase() + ".Finish");
    }

    /**
     * Get Economic Fee for joining Course.
     * @param courseName course name
     * @return joining fee
     */
    public static int getEconomyJoiningFee(@NotNull String courseName) {
        return Parkour.getConfig(ConfigType.ECONOMY).getInt("Price." + courseName.toLowerCase() + ".JoinFee");
    }

    /**
     * Get the Event Message for this Course.
     * Possible events found within {@link ParkourEventType}.
     * @param courseName course name
     * @param typeName event type name
     * @return matching event message
     */
    @Nullable
    public static String getEventMessage(@NotNull String courseName, @NotNull String typeName) {
        return getCourseConfig().getString(courseName + "." + StringUtils.standardizeText(typeName) + "Message");
    }

    /**
     * Get the Event Message for this Course.
     * @param courseName course name
     * @param type event type
     * @return matching event message
     */
    @Nullable
    public static String getEventMessage(@NotNull String courseName, @NotNull ParkourEventType type) {
        return getEventMessage(courseName, type.name());
    }

    /**
     * Set Event Message for Course.
     * Possible events found within {@link ParkourEventType}.
     * @param courseName course name
     * @param type event type name
     * @param value message value
     */
    public static void setEventMessage(@NotNull String courseName, @NotNull String type, @Nullable String value) {
        getCourseConfig().set(courseName + "." + StringUtils.standardizeText(type) + "Message", value);
        persistChanges();
    }

    /**
     * Get number of Player limit for Course.
     * @param courseName course name
     * @return player limit
     */
    public static int getPlayerLimit(@NotNull String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".PlayerLimit");
    }

    /**
     * Check if Course has Player Limit.
     * @param courseName course name
     * @return course has player limit
     */
    public static boolean hasPlayerLimit(@NotNull String courseName) {
        return getCourseConfig().getInt(courseName.toLowerCase() + ".PlayerLimit") > 0;
    }

    /**
     * Set Player limit for Course.
     * @param courseName course name
     * @param limit player limi
     */
    public static void setPlayerLimit(@NotNull String courseName, int limit) {
        getCourseConfig().set(courseName.toLowerCase() + ".PlayerLimit", limit);
        persistChanges();
    }

    /**
     * Get the Event Type Commands for the Course.
     * The Commands found will be run during the matching Event.
     * @param courseName course name
     * @param eventType selected {@link ParkourEventType}
     * @return commands for that event
     */
    @NotNull
    public static List<String> getEventCommands(@NotNull String courseName, @NotNull ParkourEventType eventType) {
        return getCourseConfig().getStringList(courseName.toLowerCase() + "." + eventType.getConfigEntry() + "Command");
    }

    /**
     * Check if the Course has Event Type Commands for the Course.
     * @param courseName course name
     * @param eventType selected {@link ParkourEventType}
     * @return course has the event type commands
     */
    public static boolean hasEventCommands(@NotNull String courseName, @NotNull ParkourEventType eventType) {
        return getCourseConfig().contains(courseName.toLowerCase() + "." + eventType.getConfigEntry() + "Command");
    }

    /**
     * Add the Event Type Command to the Course.
     * @param courseName course name
     * @param eventType selected {@link ParkourEventType}
     * @param value command to run
     */
    public static void addEventCommand(@NotNull String courseName,
                                       @NotNull ParkourEventType eventType,
                                       @NotNull String value) {
        List<String> commands = getEventCommands(courseName, eventType);
        commands.add(value);

        getCourseConfig().set(courseName.toLowerCase() + "." + eventType.getConfigEntry() + "Command", commands);
        persistChanges();
    }

    /**
     * Delete the AutoStart at the provided coordinates.
     * @param coordinates requested coordinates
     */
    public static void deleteAutoStart(@NotNull String coordinates) {
        getCourseConfig().set("CourseInfo.AutoStart." + coordinates, null);
        persistChanges();
    }

    /**
     * Get the Courses {@link ParkourConfiguration}.
     * @return the courses.yml configuration
     */
    private static ParkourConfiguration getCourseConfig() {
        return Parkour.getConfig(ConfigType.COURSES);
    }

    /**
     * Persist any changes made to the courses.yml file.
     */
    private static void persistChanges() {
        getCourseConfig().save();
    }
}

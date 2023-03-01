package io.github.a5h73y.parkour.utility;

import io.github.a5h73y.parkour.Parkour;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Delay sensitive tasks.
 */
public enum TaskCooldowns {

    INSTANCE;

    private final Map<String, Long> taskDelays = new HashMap<>();

    TaskCooldowns() {
        initialiseCleanup();
    }

    public static TaskCooldowns getInstance() {
        return INSTANCE;
    }

    /**
     * Delay the Player's Requested Event with message.
     * Some actions may require a cooldown, the event will only be permitted if enough time has passed.
     *
     * @param player requesting player
     * @param secondsToWait seconds elapsed before permitted again
     * @return whether player allowed to perform the action
     */
    public boolean delayPlayerWithMessage(Player player, int secondsToWait) {
        return delayPlayerWithMessage(player, "", secondsToWait);
    }

    public boolean delayPlayerWithMessage(Player player, String eventName, int secondsToWait) {
        return delayPlayer(player, eventName, secondsToWait, "Error.Cooldown", false);
    }

    /**
     * Delay the Player's Requested Event.
     * Some actions may require a cooldown, the event will only be permitted if enough time has passed.
     *
     * @param player requesting player
     * @param secondsToWait seconds elapsed before permitted again
     * @return whether player allowed to perform the action
     */
    public boolean delayPlayer(Player player, int secondsToWait) {
        return delayPlayer(player, "", secondsToWait);
    }

    public boolean delayPlayer(Player player, String eventName, int secondsToWait) {
        return delayPlayer(player, eventName, secondsToWait, null, false);
    }

    /**
     * Delay the Player's Requested Event.
     * Some actions may require a cooldown, the event will only be permitted if enough time has passed.
     * If requested, operators can be exempt from the cooldown.
     *
     * @param player requesting player
     * @param eventName the associated event name
     * @param secondsToWait seconds elapsed before permitted again
     * @param displayMessageKey the cooldown message key
     * @param opsBypass operators bypass cooldown
     * @return whether player is allowed to perform action
     */
    public boolean delayPlayer(@NotNull Player player,
                               @Nullable String eventName,
                               int secondsToWait,
                               @Nullable String displayMessageKey,
                               boolean opsBypass) {
        if (player.isOp() && opsBypass) {
            return true;
        }

        if (!taskDelays.containsKey(player.getUniqueId() + eventName)) {
            taskDelays.put(player.getUniqueId() + eventName, System.currentTimeMillis());
            return true;
        }

        long lastAction = taskDelays.get(player.getUniqueId() + eventName);
        int secondsElapsed = (int) ((System.currentTimeMillis() - lastAction) / 1000);

        if (secondsElapsed >= secondsToWait) {
            taskDelays.put(player.getUniqueId() + eventName, System.currentTimeMillis());
            return true;
        }

        if (TranslationUtils.containsTranslation(displayMessageKey)) {
            TranslationUtils.sendValueTranslation(displayMessageKey,
                    String.valueOf(secondsToWait - secondsElapsed), player);
        }
        return false;
    }

    public void clearCoolDowns() {
        taskDelays.clear();
    }

    public void clearCoolDowns(Player player) {
        taskDelays.entrySet()
                .removeIf(entry -> entry.getKey().startsWith(String.valueOf(player.getUniqueId())));
    }

    public void clearCoolDowns(Predicate<Map.Entry<String, Long>> filter) {
        taskDelays.entrySet().removeIf(filter);
    }

    public void clearCoolDown(Player player, String eventName) {
        taskDelays.remove(player.getUniqueId() + eventName);
    }

    /**
     * Clear the cleanup cache every hour.
     * To keep the size of the map relatively small.
     */
    private void initialiseCleanup() {
        new BukkitRunnable() {
            @Override
            public void run() {
                taskDelays.clear();
            }
        }.runTaskTimer(Parkour.getInstance(), 0, 3600000);
    }
}

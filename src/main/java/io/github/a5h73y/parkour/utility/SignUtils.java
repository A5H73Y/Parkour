package io.github.a5h73y.parkour.utility;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.other.Validation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * Parkour Sign related utility methods.
 */
public class SignUtils {

    /**
     * Create a standard Parkour sign.
     * No additional information is necessary.
     *
     * @param signEvent sign change event
     * @param player target player
     * @param signType requested sign type
     */
    public static void createStandardSign(SignChangeEvent signEvent, Player player, String signType) {
        if (!PermissionUtils.hasSignPermission(player, signType, signEvent)) {
            return;
        }

        signEvent.setLine(1, signType);
        signEvent.setLine(2, "");
        signEvent.setLine(3, "-----");
        TranslationUtils.sendValueTranslation("Parkour.SignCreated", signType);
    }

    /**
     * Create a standard Parkour course sign.
     * Will display a success message to the player.
     *
     * @param signEvent sign change event
     * @param player target player
     * @param signType requested sign type
     */
    public static void createStandardCourseSign(SignChangeEvent signEvent, Player player, String signType) {
        createStandardCourseSign(signEvent, player, signType, true);
    }

    /**
     * Create standard Parkour course sign.
     * Validates the course exists, before populating sign.
     *
     * @param signEvent sign change event
     * @param player target player
     * @param signType requested sign type
     * @param displayMessage display success message
     * @return sign create success
     */
    public static boolean createStandardCourseSign(SignChangeEvent signEvent, Player player, String signType, boolean displayMessage) {
        if (!PermissionUtils.hasSignPermission(player, signType, signEvent)) {
            breakSignAndCancelEvent(signEvent);
            return false;
        }

        if (!Parkour.getInstance().getCourseManager().courseExists(signEvent.getLine(2))) {
            TranslationUtils.sendValueTranslation("Error.NoExist", signEvent.getLine(2), player);
            breakSignAndCancelEvent(signEvent);
            return false;
        }

        signEvent.setLine(1, signType);

        if (displayMessage) {
            TranslationUtils.sendValueTranslation("Parkour.SignCreated", signType);
        }
        return true;
    }

    /**
     * Create course join sign.
     * If a minimum level requirement exists, it will be displayed on the join sign.
     *
     * @param signEvent sign change event
     * @param player target player
     */
    public static void createJoinCourseSign(SignChangeEvent signEvent, Player player) {
        if (!createStandardCourseSign(signEvent, player, "Join", false)) {
            return;
        }

        int minimumLevel = CourseInfo.getMinimumLevel(signEvent.getLine(2));

        if (minimumLevel > 0) {
            signEvent.setLine(3, ChatColor.RED + String.valueOf(minimumLevel));
        }

        TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Join");
    }

    /**
     * Create lobby join sign.
     * Will allow for a custom lobby to be specified on line 2.
     *
     * @param signEvent
     * @param player
     */
    public static void createLobbyJoinSign(SignChangeEvent signEvent, Player player) {
        if (!PermissionUtils.hasSignPermission(player, "Lobby", signEvent)) {
            return;
        }

        signEvent.setLine(1, "Lobby");

        if (signEvent.getLine(2).isEmpty()) {
            signEvent.setLine(3, "");
            TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Lobby");

        } else {
            // TODO maybe some lowercase?
            if (Parkour.getDefaultConfig().contains("Lobby." + signEvent.getLine(2))) {
                if (Parkour.getDefaultConfig().contains("Lobby." + signEvent.getLine(2) + ".RequiredLevel")) {
                    signEvent.setLine(3, ChatColor.RED + Parkour.getDefaultConfig().getString("Lobby." + signEvent.getLine(2) + ".RequiredLevel"));
                }
                TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Lobby");

            } else {
                player.sendMessage(Parkour.getPrefix() + "That custom lobby does not exist!");
                signEvent.setLine(2, "");
                signEvent.setLine(3, "-----");
            }
        }
    }

    public static void createEffectSign(SignChangeEvent signEvent, Player player) {
        if (!PermissionUtils.hasSignPermission(player, "Effect", signEvent)) {
            return;
        }

        signEvent.setLine(1, "Effect");

        if (signEvent.getLine(2).equalsIgnoreCase("heal")) {
            signEvent.setLine(2, "Heal");
            TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Heal Effect");

        } else if (signEvent.getLine(2).equalsIgnoreCase("gamemode")) {
            signEvent.setLine(2, "GameMode");

            if (PluginUtils.doesGameModeExist(signEvent.getLine(3))) {
                signEvent.setLine(3, signEvent.getLine(3).toUpperCase());
                TranslationUtils.sendValueTranslation("Parkour.SignCreated", "GameMode Effect");

            } else {
                signEvent.getBlock().breakNaturally();
                player.sendMessage(Parkour.getPrefix() + "GameMode not recognised.");
            }

        } else {
            String effect = signEvent.getLine(2).toUpperCase().replace("RESISTANCE", "RESIST").replace("RESIST", "RESISTANCE");
            PotionEffectType potionType = PotionEffectType.getByName(effect);

            if (potionType == null) {
                player.sendMessage(Parkour.getPrefix() + "Unknown Effect!");
                signEvent.getBlock().breakNaturally();
                return;
            }

            String[] args = signEvent.getLine(3).split(":");
            if (args.length != 2) {
                signEvent.getBlock().breakNaturally();
                player.sendMessage(Parkour.getPrefix() + "Invalid syntax, must follow '(duration):(strength)' example '1000:6'.");
            } else {
                player.sendMessage(Parkour.getPrefix() + potionType.getName() + " effect sign created, with a strength of " + args[0] + " and a duration of " + args[1]);
            }
        }
    }

    public static void createLeaderboardsSign(SignChangeEvent signEvent, Player player) {
        if (!createStandardCourseSign(signEvent, player, "Leaderboards", false)) {
            return;
        }

        if (!signEvent.getLine(3).isEmpty()) {
            if (!Validation.isPositiveInteger(signEvent.getLine(3))) {
                signEvent.setLine(3, "");
            }
        }

        TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Leaderboard");
    }

    public static void createCheckpointSign(SignChangeEvent signEvent, Player player, String checkpoint) {
        if (!createStandardCourseSign(signEvent, player, "Checkpoint", false)) {
            return;
        }

        if (signEvent.getLine(3).isEmpty() || !Validation.isPositiveInteger(signEvent.getLine(3))) {
            signEvent.getBlock().breakNaturally();
            player.sendMessage(Parkour.getPrefix() + "Please specify checkpoint on bottom line!");
            return;
        }

        TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Checkpoint");
    }

    public static void breakSignAndCancelEvent(SignChangeEvent event) {
        event.getBlock().breakNaturally();
        event.setCancelled(true);
    }
}

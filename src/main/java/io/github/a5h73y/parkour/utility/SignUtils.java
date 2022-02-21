package io.github.a5h73y.parkour.utility;

import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
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
        TranslationUtils.sendValueTranslation("Parkour.SignCreated", signType, player);
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
    public static boolean createStandardCourseSign(SignChangeEvent signEvent, Player player,
                                                   String signType, boolean displayMessage) {
        if (!PermissionUtils.hasSignPermission(player, signType, signEvent)) {
            breakSignAndCancelEvent(signEvent);
            return false;
        }

        if (!Parkour.getInstance().getCourseManager().doesCourseExist(signEvent.getLine(2))) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, signEvent.getLine(2), player);
            breakSignAndCancelEvent(signEvent);
            return false;
        }

        signEvent.setLine(1, signType);

        if (displayMessage) {
            TranslationUtils.sendValueTranslation("Parkour.SignCreated", signType, player);
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

        int minimumLevel = Parkour.getInstance().getConfigManager().getCourseConfig(signEvent.getLine(2)).getMinimumParkourLevel();

        if (minimumLevel > 0) {
            signEvent.setLine(3, ChatColor.RED + String.valueOf(minimumLevel));
        }

        TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Join", player);
    }

    /**
     * Create Lobby join sign.
     * Will allow for a custom lobby to be specified on line 2.
     *
     * @param signEvent sign change event
     * @param player player
     */
    public static void createLobbyJoinSign(SignChangeEvent signEvent, Player player) {
        if (!PermissionUtils.hasSignPermission(player, "Lobby", signEvent)) {
            return;
        }

        signEvent.setLine(1, "Lobby");

        if (signEvent.getLine(2).isEmpty()) {
            signEvent.setLine(3, "");
            TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Lobby", player);

        } else {
            String lobbyName = signEvent.getLine(2);

            if (!Parkour.getLobbyConfig().doesLobbyExist(lobbyName)) {
                TranslationUtils.sendValueTranslation("Error.UnknownLobby", signEvent.getLine(2), player);
                signEvent.setLine(2, "");
                signEvent.setLine(3, "-----");
                return;
            }

            if (Parkour.getLobbyConfig().hasRequiredLevel(lobbyName)) {
                signEvent.setLine(3, ChatColor.RED + Parkour.getLobbyConfig().getRequiredLevel(lobbyName).toString());
            }
            TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Lobby", player);
        }
    }

    /**
     * Create Effect Sign.
     * "heal" and "gamemode" are reserved Effect names.
     * Other options include any {@link PotionEffectType} names, these must have a duration and amplifier.
     *
     * @param signEvent sign change event
     * @param player player
     */
    public static void createEffectSign(SignChangeEvent signEvent, Player player) {
        if (!PermissionUtils.hasSignPermission(player, "Effect", signEvent)) {
            return;
        }

        signEvent.setLine(1, "Effect");

        if (signEvent.getLine(2).equalsIgnoreCase("heal")) {
            signEvent.setLine(2, "Heal");
            TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Heal Effect", player);

        } else if (signEvent.getLine(2).equalsIgnoreCase("gamemode")) {
            signEvent.setLine(2, "GameMode");

            if (PluginUtils.doesGameModeExist(signEvent.getLine(3))) {
                signEvent.setLine(3, signEvent.getLine(3).toUpperCase());
                TranslationUtils.sendValueTranslation("Parkour.SignCreated", "GameMode Effect", player);

            } else {
                signEvent.getBlock().breakNaturally();
                TranslationUtils.sendMessage(player, "GameMode not recognised.");
            }

        } else {
            String effect = signEvent.getLine(2).toUpperCase().replace("RESISTANCE", "RESIST").replace("RESIST", "RESISTANCE");
            PotionEffectType potionType = PotionEffectType.getByName(effect);

            if (potionType == null) {
                TranslationUtils.sendMessage(player, "Unknown Effect!");
                signEvent.getBlock().breakNaturally();
                return;
            }

            String[] args = signEvent.getLine(3).split(":");
            if (args.length != 2) {
                signEvent.getBlock().breakNaturally();
                TranslationUtils.sendMessage(player, "Invalid syntax, must follow '(duration):(amplifier)' example '1000:6'.");
            } else {
                TranslationUtils.sendMessage(player, potionType.getName() + " effect sign created, with a strength of "
                        + args[0] + " and a duration of " + args[1]);
            }
        }
    }

    /**
     * Create Leaderboard Sign.
     *
     * @param signEvent sign change event
     * @param player player
     */
    public static void createLeaderboardsSign(SignChangeEvent signEvent, Player player) {
        if (!createStandardCourseSign(signEvent, player, "Leaderboards", false)) {
            return;
        }

        if (!signEvent.getLine(3).isEmpty() && !ValidationUtils.isPositiveInteger(signEvent.getLine(3))) {
            signEvent.setLine(3, "");
        }

        TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Leaderboard", player);
    }

    /**
     * Create Checkpoint Sign.
     *
     * @param signEvent sign change event
     * @param player player
     */
    public static void createCheckpointSign(SignChangeEvent signEvent, Player player) {
        if (!createStandardCourseSign(signEvent, player, "Checkpoint", false)) {
            return;
        }

        if (signEvent.getLine(3).isEmpty() || !ValidationUtils.isPositiveInteger(signEvent.getLine(3))) {
            signEvent.getBlock().breakNaturally();
            TranslationUtils.sendMessage(player, "Please specify checkpoint on bottom line!");
            return;
        }

        TranslationUtils.sendValueTranslation("Parkour.SignCreated", "Checkpoint", player);
    }

    /**
     * Break the Sign and Cancel Change Event.
     *
     * @param event sign change event
     */
    public static void breakSignAndCancelEvent(SignChangeEvent event) {
        event.getBlock().breakNaturally();
        event.setCancelled(true);
    }
}

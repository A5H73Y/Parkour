package io.github.a5h73y.parkour.listener;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import io.github.a5h73y.parkour.utility.SignUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener extends AbstractPluginReceiver implements Listener {

    public SignListener(final Parkour parkour) {
        super(parkour);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignCreate(SignChangeEvent event) {
        if (!event.getLine(0).equalsIgnoreCase("[parkour]")
                && !event.getLine(0).equalsIgnoreCase("[pa]")) {
            return;
        }

        Player player = event.getPlayer();

        switch (event.getLine(1).toLowerCase()) {
            case "join":
            case "j":
                SignUtils.createJoinCourseSign(event, player);
                break;

            case "finish":
            case "f":
                SignUtils.createStandardCourseSign(event, player, "Finish");
                break;

            case "lobby":
            case "l":
                SignUtils.createLobbyJoinSign(event, player);
                break;

            case "leave":
            case "le":
                SignUtils.createStandardSign(event, player, "Leave");
                break;

            case "effect":
            case "e":
                SignUtils.createEffectSign(event, player);
                break;

            case "stats":
            case "s":
                SignUtils.createStandardCourseSign(event, player, "Stats");
                break;

            case "leaderboards":
            case "lb":
                SignUtils.createLeaderboardsSign(event, player);
                break;

            case "checkpoint":
            case "c":
                SignUtils.createCheckpointSign(event, player, "Checkpoint");
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownSignCommand", player);
                TranslationUtils.sendTranslation("Help.SignCommands", player);
                SignUtils.breakSignAndCancelEvent(event);
                return;
        }

        event.setLine(0, parkour.getConfig().getSignHeader());
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignBreak(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null ||
                !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        if (!parkour.getConfig().getBoolean("Other.Parkour.SignProtection")) {
            return;
        }

        String[] lines = ((Sign) event.getClickedBlock().getState()).getLines();

        if (!ChatColor.stripColor(lines[0]).equalsIgnoreCase(parkour.getConfig().getStrippedSignHeader())) {
            return;
        }

        if (!PermissionUtils.hasPermission(event.getPlayer(), Permission.ADMIN_ALL)) {
            TranslationUtils.sendTranslation("Error.SignProtected", event.getPlayer());
            event.setCancelled(true);

        } else {
            event.getClickedBlock().breakNaturally();
            TranslationUtils.sendTranslation("Parkour.SignRemoved", event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSignInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null ||
                !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) event.getClickedBlock().getState();
        String[] lines = sign.getLines();

        if (!ChatColor.stripColor(lines[0]).equalsIgnoreCase(parkour.getConfig().getStrippedSignHeader())) {
            if (!parkour.getPlayerManager().isPlaying(event.getPlayer().getName())) {
                return;
            }

            if (!parkour.getConfig().getBoolean("OnCourse.EnforceParkourSigns")) {
                return;
            }

            TranslationUtils.sendTranslation("Error.Sign", event.getPlayer());
            event.setCancelled(true);
            return;
        }

        if (parkour.getConfig().isPermissionForSignInteraction()
                && !PermissionUtils.hasPermission(event.getPlayer(), Permission.BASIC_SIGNS)) {
            return;
        }

        Player player = event.getPlayer();
        event.setCancelled(true);

        switch (lines[1].toLowerCase()) {
            case "join":
                if (lines[2].isEmpty() || !parkour.getCourseManager().courseExists(lines[2])) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", lines[2], player);
                    return;
                }

                parkour.getPlayerManager().joinCourse(player, lines[2]);
                break;

            case "checkpoint":
                if (lines[2].isEmpty() || !parkour.getCourseManager().courseExists(lines[2])) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", lines[2], player);
                    return;

                } else if (!parkour.getPlayerManager().isPlaying(player.getName())) {
                    TranslationUtils.sendTranslation("Error.NotOnCourse", player);
                    return;
                }

                ParkourSession session = parkour.getPlayerManager().getParkourSession(player.getName());

                if (lines[3].isEmpty() || !Validation.isPositiveInteger(lines[3])) {
                    return;
                }

                if (session.hasAchievedAllCheckpoints()) {
                    return;
                }

                if (session.getCurrentCheckpoint() >= Integer.parseInt(lines[3])) {
                    return;
                }

                if (session.getCurrentCheckpoint() + 1 < Integer.parseInt(lines[3])) {
                    return;
                }

                parkour.getPlayerManager().increaseCheckpoint(player);
                break;

            case "lobby":
                if (lines[2].isEmpty()) {
                    parkour.getLobbyManager().joinLobby(new String[0], player);

                } else {
                    String[] args = {"", lines[2]};
                    parkour.getLobbyManager().joinLobby(args, player);
                }
                break;

            case "stats":
                if (lines[2].isEmpty() || !parkour.getCourseManager().courseExists(lines[2])) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", lines[2], player);
                    return;
                }

                CourseInfo.displayCourseInfo(lines[2], player);
                break;

            case "leave":
                parkour.getPlayerManager().leaveCourse(player);
                break;

            case "finish":
                if (lines[2].isEmpty() || !parkour.getCourseManager().courseExists(lines[2])) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", lines[2], player);

                } else if (!parkour.getPlayerManager().isPlaying(player.getName())) {
                    TranslationUtils.sendTranslation("Error.NotOnCourse", player);

                } else if (!parkour.getPlayerManager().getParkourSession(player.getName()).getCourse().getName().equals(lines[2].toLowerCase())) {
                    TranslationUtils.sendTranslation("Error.NotOnCourse", player);

                } else {
                    parkour.getPlayerManager().finishCourse(player);
                }
                break;

            case "effect":
                parkour.getPlayerManager().applyEffect(lines, player);
                break;

            case "leaderboards":
                if (lines[2].isEmpty() || !parkour.getCourseManager().courseExists(lines[2])) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", lines[2], player);

                } else if (parkour.getPlayerManager().delayPlayer(player, 4, true)) {
                    int amount = lines[3].isEmpty() ? 5 : Integer.parseInt(lines[3]);

                    Utils.displayLeaderboard(player,
                            parkour.getDatabase().getTopCourseResults(lines[2], amount), lines[2]);
                }
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownSignCommand", player);
                break;
        }
    }
}

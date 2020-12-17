package io.github.a5h73y.parkour.listener;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.GuiMenu;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.SignUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
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

            case "joinall":
            case "ja":
                SignUtils.createStandardSign(event, player, "JoinAll");
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

            case "challenge":
            case "ch":
                SignUtils.createStandardCourseSign(event, player, "Challenge");
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

        if (event.getClickedBlock() == null
                || !(event.getClickedBlock().getState() instanceof Sign)) {
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

        if (event.getClickedBlock() == null
                || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) event.getClickedBlock().getState();
        String[] lines = sign.getLines();

        if (!ChatColor.stripColor(lines[0]).equalsIgnoreCase(parkour.getConfig().getStrippedSignHeader())) {
            if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
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
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExists(lines[2])) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", lines[2], player);
                    return;
                }

                parkour.getPlayerManager().joinCourse(player, lines[2]);
                break;

            case "joinall":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_JOINALL)) {
                    return;
                }

                parkour.getGuiManager().showMenu(player, GuiMenu.JOIN_COURSES);
                break;

            case "checkpoint":
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExists(lines[2])) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", lines[2], player);
                    return;

                } else if (!parkour.getPlayerManager().isPlaying(player)) {
                    TranslationUtils.sendTranslation("Error.NotOnCourse", player);
                    return;
                }

                ParkourSession session = parkour.getPlayerManager().getParkourSession(player);

                if (lines[3].isEmpty() || !ValidationUtils.isPositiveInteger(lines[3])) {
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
                parkour.getLobbyManager().joinLobby(player, lines[2].isEmpty() ? null : lines[2]);
                break;

            case "stats":
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExists(lines[2])) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", lines[2], player);
                    return;
                }

                CourseInfo.displayCourseInfo(player, lines[2]);
                break;

            case "leave":
                parkour.getPlayerManager().leaveCourse(player);
                break;

            case "finish":
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExists(lines[2])) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", lines[2], player);

                } else if (!parkour.getPlayerManager().isPlaying(player)) {
                    TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);

                } else if (!parkour.getPlayerManager().getParkourSession(player).getCourse().getName().equals(lines[2].toLowerCase())) {
                    TranslationUtils.sendTranslation("Error.NotOnCourse", player);

                } else {
                    parkour.getPlayerManager().finishCourse(player);
                }
                break;

            case "effect":
                applyEffect(player, lines[2], lines[3]);
                break;

            case "leaderboards":
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExists(lines[2])) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", lines[2], player);

                } else if (parkour.getPlayerManager().delayPlayer(player, 4, true)) {
                    int amount = lines[3].isEmpty() ? 5 : Integer.parseInt(lines[3]);
                    parkour.getDatabase().displayTimeEntries(player, lines[2],
                            parkour.getDatabase().getTopCourseResults(lines[2], amount));
                }
                break;

            case "challenge":
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExists(lines[2])) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", lines[2], player);
                    return;
                }

                parkour.getChallengeManager().createOrJoinChallenge(player, lines[2], lines[3]);
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownSignCommand", player);
                break;
        }
    }

    private void applyEffect(Player player, String effect, String argument) {
        if (effect.equalsIgnoreCase("heal")) {
            PlayerUtils.fullyHealPlayer(player);

        } else if (effect.equalsIgnoreCase("gamemode")) {
            PlayerUtils.applyGameModeChange(player, argument);

        } else {
            // if the user enters 'FIRE_RESISTANCE' or 'DAMAGE_RESIST' treat them the same
            String effectName = effect.toUpperCase().replace("RESISTANCE", "RESIST").replace("RESIST", "RESISTANCE");

            String[] args = argument.split(":");
            if (args.length == 2) {
                PlayerUtils.applyPotionEffect(effectName, Integer.parseInt(args[1]), Integer.parseInt(args[0]), player);
                TranslationUtils.sendMessage(player, effectName + " Effect Applied!");
            } else {
                TranslationUtils.sendMessage(player, "Invalid syntax, must follow '(duration):(strength)' example '1000:6'.");
            }
        }
    }
}

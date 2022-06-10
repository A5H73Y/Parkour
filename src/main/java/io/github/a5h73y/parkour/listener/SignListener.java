package io.github.a5h73y.parkour.listener;

import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.gui.GuiMenu;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.SignUtils;
import io.github.a5h73y.parkour.utility.TaskCooldowns;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
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

    /**
     * Handle Sign Change.
     * Check that the Player has the correct permissions to create Parkour signs.
     *
     * @param event SignChangeEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onSignCreate(SignChangeEvent event) {
        if (!"[parkour]".equalsIgnoreCase(event.getLine(0))
                && !"[pa]".equalsIgnoreCase(event.getLine(0))) {
            return;
        }

        Player player = event.getPlayer();

        if (!PermissionUtils.hasSignPermission(player, event)) {
            SignUtils.breakSignAndCancelEvent(event);
            return;
        }

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
                SignUtils.createCheckpointSign(event, player);
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

        event.setLine(0, parkour.getParkourConfig().getSignHeader());
    }

    /**
     * Handle Sign Interaction.
     * When the Player attempts to break a Parkour sign, check they have Permission.
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onSignBreak(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null
                || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        if (!parkour.getParkourConfig().getBoolean("Other.Parkour.SignProtection")) {
            return;
        }

        String[] lines = ((Sign) event.getClickedBlock().getState()).getLines();

        if (!ChatColor.stripColor(lines[0]).equalsIgnoreCase(parkour.getParkourConfig().getStrippedSignHeader())) {
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

    /**
     * Handle Sign Interaction.
     * When the Player attempts to interact with a Parkour Sign, process the request.
     * Prevent the player from interaction with non-parkour signs.
     *
     * @param event PlayerInteractEvent
     */
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

        if (!ChatColor.stripColor(lines[0]).equalsIgnoreCase(parkour.getParkourConfig().getStrippedSignHeader())) {
            if (!parkour.getParkourSessionManager().isPlaying(event.getPlayer())) {
                return;
            }

            if (!parkour.getParkourConfig().getBoolean("OnCourse.EnforceParkourSigns")) {
                return;
            }

            TranslationUtils.sendTranslation("Error.Sign", event.getPlayer());
            event.setCancelled(true);
            return;
        }

        if (parkour.getParkourConfig().isPermissionForSignInteraction()
                && !PermissionUtils.hasPermission(event.getPlayer(), Permission.BASIC_SIGNS)) {
            return;
        }

        Player player = event.getPlayer();
        event.setCancelled(true);

        switch (lines[1].toLowerCase()) {
            case "join":
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExist(lines[2])) {
                    TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, lines[2], player);
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
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExist(lines[2])) {
                    TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, lines[2], player);
                    return;
                }

                if (lines[3].isEmpty() || !ValidationUtils.isPositiveInteger(lines[3])) {
                    return;
                }

                parkour.getPlayerManager().manuallyIncreaseCheckpoint(player, Integer.parseInt(lines[3]));
                break;

            case "lobby":
                parkour.getLobbyManager().joinLobby(player, lines[2].isEmpty() ? null : lines[2]);
                break;

            case "stats":
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExist(lines[2])) {
                    TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, lines[2], player);
                    return;
                }

                CourseConfig.displayCourseInfo(player, lines[2]);
                break;

            case "leave":
                parkour.getPlayerManager().leaveCourse(player);
                break;

            case "finish":
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExist(lines[2])) {
                    TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, lines[2], player);

                } else if (!parkour.getParkourSessionManager().isPlaying(player)) {
                    TranslationUtils.sendTranslation("Error.NotOnAnyCourse", player);

                } else if (!parkour.getParkourSessionManager().getParkourSession(player).getCourse().getName()
                        .equalsIgnoreCase(lines[2])) {
                    TranslationUtils.sendTranslation("Error.NotOnCourse", player);

                } else {
                    parkour.getPlayerManager().finishCourse(player);
                }
                break;

            case "effect":
                applyEffect(player, lines[2], lines[3]);
                break;

            case "leaderboards":
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExist(lines[2])) {
                    TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, lines[2], player);

                } else if (TaskCooldowns.getInstance().delayPlayerWithMessage(player, "leaderboards", 4)) {
                    int amount = lines[3].isEmpty() ? 5 : Integer.parseInt(lines[3]);
                    parkour.getDatabaseManager().displayTimeEntries(player, lines[2],
                            parkour.getDatabaseManager().getTopCourseResults(lines[2], amount));
                }
                break;

            case "challenge":
                if (lines[2].isEmpty() || !parkour.getCourseManager().doesCourseExist(lines[2])) {
                    TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, lines[2], player);
                    return;
                }

                parkour.getChallengeManager().createOrJoinChallenge(player, lines[2],
                        lines[3].trim().isEmpty() ? null : lines[3]);
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
            PlayerUtils.changeGameMode(player, argument);

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

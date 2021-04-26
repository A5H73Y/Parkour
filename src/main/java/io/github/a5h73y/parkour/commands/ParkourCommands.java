package io.github.a5h73y.parkour.commands;

import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_INVALID_AMOUNT;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.CoursePrizeConversation;
import io.github.a5h73y.parkour.conversation.CreateParkourKitConversation;
import io.github.a5h73y.parkour.conversation.EditParkourKitConversation;
import io.github.a5h73y.parkour.conversation.ParkourModeConversation;
import io.github.a5h73y.parkour.enums.GuiMenu;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Player related Parkour commands handling.
 */
public class ParkourCommands extends AbstractPluginReceiver implements CommandExecutor {

    public ParkourCommands(final Parkour parkour) {
        super(parkour);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String... args) {
        if (!(sender instanceof Player)) {
            TranslationUtils.sendMessage(sender, "'/parkour' is only available in game.");
            TranslationUtils.sendMessage(sender, "Use '/pac' for console commands.");
            return false;
        }

        Player player = (Player) sender;

        if (Parkour.getDefaultConfig().isPermissionsForCommands()
                && !PermissionUtils.hasPermission(player, Permission.BASIC_COMMANDS)) {
            return false;
        }

        if (args.length == 0) {
            TranslationUtils.sendMessage(player, "Plugin proudly created by &bA5H73Y &f& &bsteve4744");
            TranslationUtils.sendTranslation("Help.Commands", player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "addjoinitem":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 4, 6)) {
                    return false;
                }

                parkour.getCourseManager().addJoinItem(player, args);
                break;

            case "cache":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                PluginUtils.cacheCommand(player, args.length == 2 ? args[1] : null);
                break;

            case "challenge":
                if (!ValidationUtils.validateArgs(player, args, 2, 10)) {
                    return false;
                }

                parkour.getChallengeManager().processCommand(player, args);
                break;

            case "challengeonly":
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player,
                        Permission.ADMIN_COURSE, getChosenCourseName(player, args, 1))) {
                    return false;
                }

                parkour.getCourseManager().toggleChallengeOnlyStatus(player, getChosenCourseName(player, args, 1));
                break;

            case "checkpoint":
                if (!parkour.getPlayerManager().hasSelectedValidCourse(player)) {
                    TranslationUtils.sendTranslation("Error.Selected", player);
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, PlayerInfo.getSelectedCourse(player))) {
                    return false;

                } else if (args.length == 2 && !ValidationUtils.isPositiveInteger(args[1])) {
                    TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
                    return false;
                }

                parkour.getCheckpointManager().createCheckpoint(player, args.length == 2 ? Integer.parseInt(args[1]) : null);
                break;

            case "cmds":
                ParkourCommandHelp.processCommand(player, args);
                break;

            case "create":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_CREATE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().createCourse(player, args[1]);
                break;

            case "createkit":
            case "createparkourkit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;
                }

                new CreateParkourKitConversation(player).begin();
                break;

            case "delete":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_DELETE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                PluginUtils.deleteCommand(player, args[1], args[2]);
                break;

            case "done":
            case "deselect":
            case "stopselect":
                parkour.getCourseManager().deselectCourse(player);
                break;

            case "econ":
            case "economy":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 4)) {
                    return false;
                }

                parkour.getEconomyApi().processCommand(player, args);
                break;

            case "editkit":
            case "editparkourkit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;
                }

                new EditParkourKitConversation(player).begin();
                break;

            case "help":
                ParkourCommandHelp.displayCommandHelp(player, args);
                break;

            case "hideall":
                if (!parkour.getPlayerManager().isPlaying(player)) {
                    TranslationUtils.sendTranslation("Error.NotOnCourse", player);
                    return false;
                }
                parkour.getPlayerManager().toggleVisibility(player);
                break;

            case "info":
            case "player":
                parkour.getPlayerManager().displayParkourInfo(player,
                        args.length <= 1 ? (OfflinePlayer) sender : Bukkit.getOfflinePlayer(args[1]));
                break;

            case "invite":
                if (!ValidationUtils.validateArgs(player, args, 2, 10)) {
                    return false;
                }

                player.performCommand("parkour challenge invite " + StringUtils.extractMessageFromArgs(args, 1));
                break;

            case "join":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;

                } else if (!parkour.getConfig().getBoolean("OnJoin.AllowViaCommand")) {
                    TranslationUtils.sendTranslation("Error.AllowViaCommand", player);
                    return false;
                }

                parkour.getPlayerManager().joinCourse(player, args[1]);
                break;

            case "joinall":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_JOINALL)) {
                    return false;
                }

                parkour.getGuiManager().showMenu(player, GuiMenu.JOIN_COURSES);
                break;

            case "kit":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_KIT)) {
                    return false;
                }

                parkour.getParkourKitManager().giveParkourKit(player, args.length == 2 ? args[1] : DEFAULT);
                break;

            case "leaderboard":
            case "leaderboards":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_LEADERBOARD)) {
                    return false;
                }

                parkour.getCourseManager().displayLeaderboards(player, args);
                break;

            case "leave":
                parkour.getPlayerManager().leaveCourse(player);
                break;

            case "link":
                if (!ValidationUtils.validateArgs(player, args, 2, 3)) {
                    return false;

                } else if (!parkour.getPlayerManager().hasSelectedValidCourse(player)) {
                    TranslationUtils.sendTranslation("Error.Selected", player);
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, PlayerInfo.getSelectedCourse(player))) {
                    return false;
                }

                parkour.getCourseManager().setCourseLink(player, args);
                break;

            case "linkkit":
                if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, args[1])) {
                    return false;
                }

                parkour.getCourseManager().setParkourKit(player, args[1], args[2]);
                break;

            case "list":
                parkour.getCourseManager().displayList(player, args);
                break;

            case "listkit":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_KIT)) {
                    return false;
                }

                parkour.getParkourKitManager().displayParkourKits(player, args);
                break;

            case "lobby":
                parkour.getLobbyManager().joinLobby(player, args.length > 1 ? args[1] : DEFAULT);
                break;

            case "manualcheckpoint":
                parkour.getPlayerManager().setManualCheckpoint(player);
                break;

            case "material":
                MaterialUtils.lookupMaterialInformation(player, args);
                break;

            case "perms":
            case "permissions":
                parkour.getPlayerManager().displayPermissions(player);
                break;

            case "placeholder":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                parkour.getPlaceholderApi().evaluatePlaceholder(player, args[1]);
                break;

            case "prize":
            case "setprize":
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player,
                        Permission.ADMIN_COURSE, getChosenCourseName(player, args, 1))) {
                    return false;
                }

                new CoursePrizeConversation(player).withCourseName(getChosenCourseName(player, args, 1)).begin();
                break;

            case "quiet":
                parkour.getPlayerManager().toggleQuietMode(player);
                break;

            case "ready":
            case "finish":
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player,
                        Permission.ADMIN_COURSE, getChosenCourseName(player, args, 1))) {
                    return false;
                }

                parkour.getCourseManager().toggleCourseReadyStatus(player, getChosenCourseName(player, args, 1));
                break;

            case "recreate":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                TranslationUtils.sendMessage(player, "Recreating courses...");
                parkour.getDatabase().recreateAllCourses(true);
                break;

            case "reload":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                parkour.getConfigManager().reloadConfigs();
                PluginUtils.clearAllCache();
                TranslationUtils.sendTranslation("Parkour.ConfigReloaded", player);
                PluginUtils.logToFile(player.getName() + " reloaded the Parkour config");
                break;

            case "request":
            case "bug":
                TranslationUtils.sendMessage(player, "To Request a feature or to Report a bug...");
                TranslationUtils.sendMessage(player, "Click here:&3 https://github.com/A5H73Y/Parkour/issues", false);
                break;

            case "reset":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_RESET)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3, 4)) {
                    return false;
                }

                PluginUtils.resetCommand(player, args[1], args[2], args.length == 4 ? args[3] : null);
                break;

            case "respawn":
            case "back":
            case "die":
                parkour.getPlayerManager().playerDie(player);
                break;

            case "restart":
                parkour.getPlayerManager().restartCourse(player);
                break;

            case "resumable":
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player,
                        Permission.ADMIN_COURSE, getChosenCourseName(player, args, 1))) {
                    return false;
                }

                parkour.getCourseManager().toggleResumable(player, getChosenCourseName(player, args, 1));
                break;

            case "rewarddelay":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardDelay(player, args[1], args[2]);
                break;

            case "rewardlevel":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourLevel(player, args[1], args[2]);
                break;

            case "rewardleveladd":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourLevelIncrease(player, args[1], args[2]);
                break;

            case "rewardonce":
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player,
                        Permission.ADMIN_COURSE, getChosenCourseName(player, args, 1))) {
                    return false;
                }

                parkour.getCourseManager().toggleRewardOnceStatus(player, getChosenCourseName(player, args, 1));
                break;

            case "rewardparkoins":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkoins(player, args[1], args[2]);
                break;

            case "rewardrank":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getPlayerManager().setRewardParkourRank(player, args[1], args[2]);
                break;

            case "select":
            case "edit":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, args[1])) {
                    return false;
                }

                parkour.getCourseManager().selectCourse(player, args[1]);
                break;

            case "setautostart":
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player,
                        Permission.ADMIN_COURSE, getChosenCourseName(player, args, 1))) {
                    return false;
                }

                parkour.getCourseManager().createAutoStart(player, getChosenCourseName(player, args, 1));
                break;

            case "setcourse":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 100)) {
                    return false;
                }

                parkour.getCourseManager().processSetCommand(player, args);
                break;

            case "setcreator":
            case "setmaxdeath":
            case "setmaxtime":
            case "setminlevel":
                if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                player.performCommand("parkour setcourse " + args[1] + " " + args[0].replace("set", "") + " " + args[2]);
                break;

            case "setlevel":
            case "setrank":
                if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                player.performCommand("parkour setplayer " + args[1] + " " + args[0].replace("set", "") + " " + args[2]);
                break;

            case "setlobby":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                parkour.getLobbyManager().createLobby(player,
                        args.length > 1 ? args[1] : DEFAULT,
                        args.length == 3 ? args[2] : null);
                break;

            case "setmode":
            case "setparkourmode":
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player,
                        Permission.ADMIN_COURSE, getChosenCourseName(player, args, 1))) {
                    return false;
                }

                new ParkourModeConversation(player).withCourseName(getChosenCourseName(player, args, 1)).begin();
                break;

            case "setplayer":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 100)) {
                    return false;
                }

                parkour.getPlayerManager().processSetCommand(player, args);
                break;

            case "setplayerlimit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setPlayerLimit(player, args[1], args[2]);
                break;

            case "setstart":
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player,
                        Permission.ADMIN_COURSE, getChosenCourseName(player, args, 1))) {
                    return false;
                }

                parkour.getCourseManager().setStartLocation(player, getChosenCourseName(player, args, 1));
                break;

            case "settings":
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player,
                        Permission.ADMIN_COURSE, getChosenCourseName(player, args, 1))) {
                    return false;
                }

                parkour.getCourseManager().displaySettingsGui(player, getChosenCourseName(player, args, 1));
                break;

            case "sql":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                parkour.getDatabase().displayInformation(player);
                break;

            case "stats":
            case "course":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                CourseInfo.displayCourseInfo(player, args[1]);
                break;

            case "test":
            case "testmode":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_TESTMODE)) {
                    return false;
                }

                parkour.getPlayerManager().toggleTestMode(player, args.length == 2 ? args[1].toLowerCase() : DEFAULT);
                break;

            case "tp":
            case "teleport":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_TELEPORT)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCheckpointManager().teleportCheckpoint(player, args[1], null);
                break;

            case "tpc":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_TELEPORT_CHECKPOINT)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;

                } else if (!ValidationUtils.isPositiveInteger(args[2])) {
                    TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
                    return false;
                }

                parkour.getCheckpointManager().teleportCheckpoint(player, args[1], Integer.parseInt(args[2]));
                break;

            case "tutorial":
                TranslationUtils.sendMessage(player, "To follow the official Parkour tutorials...");
                TranslationUtils.sendMessage(player, "Click here:&3 https://a5h73y.github.io/Parkour/", false);
                break;

            case "validatekit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;
                }

                parkour.getParkourKitManager().validateParkourKit(player, args.length == 2 ? args[1] : DEFAULT);
                break;

            case "whitelist":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getConfig().addWhitelistedCommand(player, args[1]);
                break;

            //Other commands//
            case "support":
            case "contact":
            case "about":
            case "ver":
            case "version":
                TranslationUtils.sendMessage(player, "Server is running Parkour &7" + parkour.getDescription().getVersion());
                TranslationUtils.sendMessage(player, "Plugin proudly created by &bA5H73Y &f& &bsteve4744", false);
                TranslationUtils.sendMessage(player, "Project Page:&b https://www.spigotmc.org/resources/parkour.23685/", false);
                TranslationUtils.sendMessage(player, "Discord Server:&b https://discord.gg/Gc8RGYr", false);
                break;

            case "accept":
                parkour.getChallengeManager().acceptChallengeInvite(player);
                break;

            case "decline":
                parkour.getChallengeManager().declineChallenge(player);
                break;

            case "yes":
            case "no":
                TranslationUtils.sendTranslation("Error.NoQuestion", player);
                break;

            case "config":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                if (player.isOp() && !args[1].startsWith("MySQL")) {
                    TranslationUtils.sendValue(sender, args[1], parkour.getConfig().getString(args[1]));
                }
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownCommand", player);
                TranslationUtils.sendTranslation("Help.Commands", player);
                break;
        }
        return true;
    }

    /**
     * Get the Player's chosen course.
     * If they have provided a course parameter, use that.
     * Otherwise fallback to the player's selected course.
     *
     * @param player player
     * @param args command args
     * @param courseArg position of course argument
     * @return chosen course name
     */
    private String getChosenCourseName(Player player, String[] args, int courseArg) {
        return args.length != courseArg + 1 ? PlayerInfo.getSelectedCourse(player) : args[courseArg];
    }
}

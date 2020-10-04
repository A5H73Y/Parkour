package io.github.a5h73y.parkour.commands;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.CreateParkourKitConversation;
import io.github.a5h73y.parkour.conversation.EditParkourKitConversation;
import io.github.a5h73y.parkour.enums.GuiMenu;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Help;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.ChatColor;
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
                             @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Parkour.getPrefix() + "'/pa' is only available in game.");
            sender.sendMessage(Parkour.getPrefix() + "Use '/pac' for console commands.");
            return false;
        }

        Player player = (Player) sender;

        if (Parkour.getDefaultConfig().isPermissionsForCommands()
                && !PermissionUtils.hasPermission(player, Permission.BASIC_COMMANDS)) {
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Parkour.getPrefix() + "Plugin proudly created by " + ChatColor.AQUA + "A5H73Y & steve4744");
            TranslationUtils.sendTranslation("Help.Commands", player);
            return true;
        }

        switch (args[0].toLowerCase()) {
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

            case "back":
            case "die":
            case "respawn":
                parkour.getPlayerManager().playerDie(player);
                break;

            case "create":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_CREATE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().createCourse(args[1], player);
                break;

            case "leave":
                parkour.getPlayerManager().leaveCourse(player);
                break;

            case "restart":
                parkour.getPlayerManager().restartCourse(player);
                break;

            case "info":
                parkour.getPlayerManager().displayParkourInfo(args, player);
                break;

            case "stats":
            case "course":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                CourseInfo.displayCourseInfo(args[1], player);
                break;

            case "lobby":
                parkour.getLobbyManager().joinLobby(args, player);
                break;

            case "setlobby":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                parkour.getLobbyManager().createLobby(args, player);
                break;

            case "checkpoint":
                if (!PlayerInfo.hasSelectedValidCourse(player)) {
                    TranslationUtils.sendTranslation("Error.Selected", player);
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, PlayerInfo.getSelectedCourse(player))) {
                    return false;
                }

                parkour.getCheckpointManager().createCheckpoint(args, player);
                break;

            case "finish":
            case "ready":
                if (!PlayerInfo.hasSelectedValidCourse(player)
                        && !PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;
                }

                parkour.getCourseManager().setCourseReadyStatus(args, player);
                break;

            case "setstart":
                if (!PlayerInfo.hasSelectedValidCourse(player)) {
                    TranslationUtils.sendTranslation("Error.Selected", player);
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, PlayerInfo.getSelectedCourse(player))) {
                    return false;
                }

                parkour.getCourseManager().setStart(player);
                break;

            case "setautostart":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setAutoStart(args, player);
                break;

            case "prize":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_PRIZE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setPrize(args[1], player);
                break;

            case "checkpointprize":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_PRIZE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setCheckpointPrize(args[1], player);
                break;

            case "perms":
                parkour.getPlayerManager().displayPermissions(player, args.length > 2);
                break;

            case "kit":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_KIT)) {
                    return false;
                }

                parkour.getPlayerManager().giveParkourKit(args, player);
                break;

            case "delete":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_DELETE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                PluginUtils.deleteCommand(args, player);
                break;

            case "select":
            case "edit":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, args[1])) {
                    return false;
                }

                parkour.getCourseManager().selectCourse(args, player);
                break;

            case "done":
            case "deselect":
            case "stopselect":
                parkour.getCourseManager().deselectCourse(player);
                break;

            case "tp":
            case "teleport":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_TELEPORT)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCheckpointManager().teleportCheckpoint(args, player, false);
                break;

            case "tpc":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_TELEPORT_CHECKPOINT)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCheckpointManager().teleportCheckpoint(args, player, true);
                break;

            case "link":
                if (!ValidationUtils.validateArgs(player, args, 3, 4)) {
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, PlayerInfo.getSelectedCourse(player))) {
                    return false;
                }

                parkour.getCourseManager().linkCourse(args, player);
                break;

            case "addjoinitem":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 4, 6)) {
                    return false;
                }

                parkour.getCourseManager().addJoinItem(args, player);
                break;

            case "rewardonce":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setRewardOnce(args, player);
                break;

            case "rewardlevel":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourLevel(args, player);
                break;

            case "rewardleveladd":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourLevelAddition(args, player);
                break;

            case "rewardrank":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getPlayerManager().setRewardParkourRank(args, player);
                break;

            case "rewarddelay":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardDelay(args, player);
                break;

            case "rewardparkoins":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkoins(args, player);
                break;

            case "quiet":
                parkour.getPlayerManager().toggleQuietMode(player);
                break;

            case "reset":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_RESET)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3, 4)) {
                    return false;
                }

                PluginUtils.resetCommand(args, player);
                break;

            case "test":
            case "testmode":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_TESTMODE)) {
                    return false;
                }

                parkour.getPlayerManager().toggleTestMode(args, player);
                break;

            case "econ":
            case "economy":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 4)) {
                    return false;
                }

                parkour.getEconomyApi().processEconomyCommand(args, player);
                break;

            case "setmode":
            case "setparkourmode":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setCourseParkourMode(args, player);
                break;

            case "createkit":
            case "createparkourkit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;
                }

                new CreateParkourKitConversation(player).begin();
                break;

            case "editkit":
            case "editparkourkit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;
                }

                new EditParkourKitConversation(player).begin();
                break;

            case "linkkit":
                if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, args[1])) {
                    return false;
                }

                parkour.getCourseManager().linkParkourKit(args, player);
                break;

            case "listkit":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_KIT)) {
                    return false;
                }

                parkour.getParkourKitManager().listParkourKit(args, player);
                break;

            case "validatekit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;
                }

                parkour.getParkourKitManager().validateParkourKit(args, player);
                break;

            case "challenge":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_CHALLENGE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3, 4)) {
                    return false;
                }

                parkour.getCourseManager().challengePlayer(args, player);
                break;

            case "list":
                parkour.getCourseManager().displayList(args, player);
                break;

            case "help":
                Help.lookupCommandHelp(args, player);
                break;

            case "leaderboard":
            case "leaderboards":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_LEADERBOARD)) {
                    return false;
                }

                parkour.getCourseManager().getLeaderboards(args, player);
                break;

            case "recreate":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                player.sendMessage(Parkour.getPrefix() + "Recreating courses...");
                parkour.getDatabase().recreateAllCourses();
                break;

            case "whitelist":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getConfig().addWhitelistedCommand(player, args[1]);
                break;

            case "setcourse":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 100)) {
                    return false;
                }

                parkour.getCourseManager().processSetCommand(args, player);
                break;

            case "setplayer":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 100)) {
                    return false;
                }

                parkour.getPlayerManager().processSetCommand(args, player);
                break;

            case "material":
                MaterialUtils.lookupMaterialInformation(args, player);
                break;

            case "sql":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                player.sendMessage("TODO");
                //TODO show connection successful, number of rows in each table etc.
                break;

            case "cache":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                player.sendMessage("TODO");
                // TODO no args = show number of courses cached, number of database times cached
                // TODO [clear] = empty the cache
                break;

            //Other commands//
            case "about":
            case "ver":
            case "version":
                player.sendMessage(Parkour.getPrefix() + "Server is running Parkour " + ChatColor.GRAY
                        + parkour.getDescription().getVersion());
                player.sendMessage("This plugin was developed by " + ChatColor.GOLD + "A5H73Y & steve4744");
                break;

            case "contact":
                player.sendMessage(Parkour.getPrefix() + "For information or help please contact me:");
                player.sendMessage("Spigot: " + ChatColor.AQUA + "A5H73Y");
                player.sendMessage("Project Page: " + ChatColor.AQUA + "https://www.spigotmc.org/resources/parkour.23685/");
                player.sendMessage("Discord Server: " + ChatColor.AQUA + "https://discord.gg/Gc8RGYr");
                break;

            case "request":
            case "bug":
                player.sendMessage(Parkour.getPrefix() + "To Request a feature or to Report a bug...");
                player.sendMessage("Click here: " + ChatColor.DARK_AQUA + "https://github.com/A5H73Y/Parkour/issues");
                break;

            case "tutorial":
                player.sendMessage(Parkour.getPrefix() + "To follow the official Parkour tutorials...");
                player.sendMessage("Click here: " + ChatColor.DARK_AQUA + "https://a5h73y.github.io/Parkour/");
                break;

            case "settings":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                Help.displaySettings(player);
                break;

            case "cmds":
                Help.processCommandsInput(args, player);
                break;

            case "accept":
                parkour.getChallengeManager().acceptChallenge(player);
                break;

            case "decline":
                parkour.getChallengeManager().declineChallenge(player);
                break;

            case "yes":
            case "no":
                TranslationUtils.sendTranslation("Error.NoQuestion", player);
                break;

            case "reload":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                parkour.getConfigManager().reloadConfigs();
                TranslationUtils.sendTranslation("Other.Reload", player);
                PluginUtils.logToFile(player.getName() + " reloaded the Parkour config");
                break;

            case "setminlevel":
            case "setmaxtime":
            case "setmaxdeath":
            case "setcreator":
                if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                player.performCommand("parkour setcourse " + args[1] + " " + args[0].replace("set", "") + " " + args[2]);
                break;

            case "setrank":
            case "setlevel":
                if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                player.performCommand("parkour setplayer " + args[1] + " " + args[0].replace("set", "") + " " + args[2]);
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownCommand", player);
                TranslationUtils.sendTranslation("Help.Commands", player);
                break;
        }
        return true;
    }
}

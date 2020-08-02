package io.github.a5h73y.parkour.commands;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.CreateParkourKitConversation;
import io.github.a5h73y.parkour.conversation.EditParkourKitConversation;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.enums.GuiMenu;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Help;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.Utils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParkourCommands extends AbstractPluginReceiver implements CommandExecutor {

    public ParkourCommands(final Parkour parkour) {
        super(parkour);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command,
                             final String label, final String[] args) {
        if (!command.getName().equalsIgnoreCase("parkour")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("For console commands, please use: paconsole (command)");
            return false;
        }

        Player player = (Player) sender;

        if (Parkour.getDefaultConfig().isPermissionsForCommands()
                && !PermissionUtils.hasPermission(player, Permission.BASIC_COMMANDS)) {
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Parkour.getPrefix() + "Plugin proudly created by " + ChatColor.AQUA + "A5H73Y & steve4744");
            TranslationUtils.sendTranslation("Help.Commands", false, player);
            return true;
        }

        String firstArg = args[0].toLowerCase();

        switch (firstArg) {
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

            case "setcreator":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setCreator(args, player);
                break;

            case "checkpoint":
                if (!PlayerInfo.hasSelected(player)) {
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, PlayerInfo.getSelectedCourse(player))) {
                    return false;
                }

                parkour.getCheckpointManager().createCheckpoint(args, player);
                break;

            case "finish":
                parkour.getCourseManager().setCompletionStatus(args, player);
                break;

            case "setstart":
                if (!PlayerInfo.hasSelected(player)) {
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

                Utils.deleteCommand(args, player);
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

                } else if (!PlayerInfo.hasSelected(player)) {
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, PlayerInfo.getSelectedCourse(player))) {
                    return false;
                }

                parkour.getCourseManager().linkCourse(args, player);
                break;

            case "setminlevel":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setMinLevel(args, player);
                break;

            case "setmaxdeath":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setMaxDeaths(args, player);
                break;

            case "setmaxtime":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setMaxTime(args, player);
                break;

            case "setjoinitem":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 4)) {
                    return false;
                }

                parkour.getCourseManager().addJoinItem(args, player);
                break;

            case "rewardonce":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setRewardOnce(args, player);
                break;

            case "rewardlevel":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourLevel(args, player);
                break;

            case "rewardleveladd":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourLevelAddition(args, player);
                break;

            case "rewardrank":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourRank(args, player);
                break;

            case "rewarddelay":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardDelay(args, player);
                break;

            case "rewardparkoins":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
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

                Utils.resetCommand(args, player);
                break;

            case "test":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_TESTMODE)) {
                    return false;
                }

                parkour.getPlayerManager().toggleTestMode(args, player);
                break;

            case "economy":
            case "econ":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 4)) {
                    return false;
                }

                parkour.getEconomyApi().processEconomyCommand(args, player);
                break;

            case "setmode":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setCourseMode(args, player);
                break;

            case "createparkourkit":
            case "createkit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                new CreateParkourKitConversation(player).begin();
                break;

            case "editparkourkit":
            case "editkit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
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
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
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

                Utils.addWhitelistedCommand(args, player);
                break;

            case "setlevel":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getPlayerManager().setParkourLevel(args, player);
                break;

            case "setrank":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getPlayerManager().setParkourRank(args, player);
                break;

            case "material":
                MaterialUtils.lookupMaterialInformation(args, player);
                break;

            //Other commands//
            case "about":
            case "ver":
            case "version":
                player.sendMessage(Parkour.getPrefix() + "Server is running Parkour " + ChatColor.GRAY + parkour.getDescription().getVersion());
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

            default:
                TranslationUtils.sendTranslation("Error.UnknownCommand", player);
                TranslationUtils.sendTranslation("Help.Commands", false, player);
                break;
        }
        return true;
    }
}
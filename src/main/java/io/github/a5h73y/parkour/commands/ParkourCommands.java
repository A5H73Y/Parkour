package io.github.a5h73y.parkour.commands;

import static io.github.a5h73y.parkour.other.Constants.DEFAULT;

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

                player.sendMessage("TODO");
                // TODO no args = show number of courses cached, number of database times cached
                // TODO [clear] = empty the cache
                break;

            case "challenge":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_CHALLENGE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3, 4)) {
                    return false;
                }

                parkour.getCourseManager().challengePlayer(player, args);
                break;

            case "checkpoint":
                if (!PlayerInfo.hasSelectedValidCourse(player)) {
                    TranslationUtils.sendTranslation("Error.Selected", player);
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, PlayerInfo.getSelectedCourse(player))) {
                    return false;
                }

                parkour.getCheckpointManager().createCheckpoint(player, args);
                break;

            case "checkpointprize":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_PRIZE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setCheckpointPrize(player, args[1]);
                break;

            case "cmds":
                Help.processCommandsInput(player, args);
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

                parkour.getEconomyApi().processEconomyCommand(player, args);
                break;

            case "editkit":
            case "editparkourkit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;
                }

                new EditParkourKitConversation(player).begin();
                break;

            case "help":
                Help.lookupCommandHelp(player, args);
                break;

            case "info":
                parkour.getPlayerManager().displayParkourInfo(player, args);
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

                parkour.getPlayerManager().giveParkourKit(player, args.length == 2 ? args[1] : DEFAULT);
                break;

            case "leaderboard":
            case "leaderboards":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_LEADERBOARD)) {
                    return false;
                }

                parkour.getCourseManager().getLeaderboards(player, args);
                break;

            case "leave":
                parkour.getPlayerManager().leaveCourse(player);
                break;

            case "link":
                if (!ValidationUtils.validateArgs(player, args, 2, 3)) {
                    return false;

                } else if (!PlayerInfo.hasSelectedValidCourse(player)) {
                    TranslationUtils.sendTranslation("Error.Selected", player);
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, PlayerInfo.getSelectedCourse(player))) {
                    return false;
                }

                parkour.getCourseManager().linkCourse(player, args);
                break;

            case "linkkit":
                if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(
                        player, Permission.ADMIN_COURSE, args[1])) {
                    return false;
                }

                parkour.getCourseManager().linkParkourKit(player, args[1], args[2]);
                break;

            case "list":
                parkour.getCourseManager().displayList(player, args);
                break;

            case "listkit":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_KIT)) {
                    return false;
                }

                parkour.getParkourKitManager().listParkourKit(player, args);
                break;

            case "lobby":
                parkour.getLobbyManager().joinLobby(player, args.length > 1 ? args[1] : DEFAULT);
                break;

            case "material":
                MaterialUtils.lookupMaterialInformation(player, args);
                break;

            case "perms":
            case "permissions":
                parkour.getPlayerManager().displayPermissions(player, args.length > 2);
                break;

            case "prize":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_PRIZE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setPrize(player, args[1]);
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

                parkour.getCourseManager().setCourseReadyStatus(player, getChosenCourseName(player, args, 1));
                break;

            case "recreate":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                player.sendMessage(Parkour.getPrefix() + "Recreating courses...");
                parkour.getDatabase().recreateAllCourses();
                break;

            case "reload":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                parkour.getConfigManager().reloadConfigs();
                TranslationUtils.sendTranslation("Parkour.ConfigReloaded", player);
                PluginUtils.logToFile(player.getName() + " reloaded the Parkour config");
                break;

            case "request":
            case "bug":
                player.sendMessage(Parkour.getPrefix() + "To Request a feature or to Report a bug...");
                player.sendMessage("Click here: " + ChatColor.DARK_AQUA + "https://github.com/A5H73Y/Parkour/issues");
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

                parkour.getCourseManager().setRewardParkourLevelAddition(player, args[1], args[2]);
                break;

            case "rewardonce":
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player,
                        Permission.ADMIN_COURSE, getChosenCourseName(player, args, 1))) {
                    return false;
                }

                parkour.getCourseManager().setRewardOnce(player, getChosenCourseName(player, args, 1));
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

                parkour.getCourseManager().setAutoStart(player, getChosenCourseName(player, args, 1));
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

                parkour.getLobbyManager().createLobby(player, args);
                break;

            case "setmode":
            case "setparkourmode":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setCourseParkourMode(player, args[1]);
                break;

            case "setplayer":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 100)) {
                    return false;
                }

                parkour.getPlayerManager().processSetCommand(player, args);
                break;

            case "setstart":
                if (!PermissionUtils.hasPermissionOrCourseOwnership(player,
                        Permission.ADMIN_COURSE, getChosenCourseName(player, args, 1))) {
                    return false;
                }

                parkour.getCourseManager().setStart(player, getChosenCourseName(player, args, 1));
                break;

            case "settings":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                Help.displaySettings(player);
                break;

            case "sql":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                player.sendMessage("TODO");
                //TODO show connection successful, number of rows in each table etc.
                break;

            case "stats":
            case "course":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                CourseInfo.displayCourseInfo(args[1], player);
                break;

            case "test":
            case "testmode":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_TESTMODE)) {
                    return false;
                }

                parkour.getPlayerManager().toggleTestMode(player, args.length == 2 ? args[1].toLowerCase() : DEFAULT);
                break;

            case "setplayerlimit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setPlayerLimit(player, args[1], args[2]);
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
                }

                parkour.getCheckpointManager().teleportCheckpoint(player, args[1], args[2]);
                break;

            case "tutorial":
                player.sendMessage(Parkour.getPrefix() + "To follow the official Parkour tutorials...");
                player.sendMessage("Click here: " + ChatColor.DARK_AQUA + "https://a5h73y.github.io/Parkour/");
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
            case "about":
            case "ver":
            case "version":
                player.sendMessage(Parkour.getPrefix() + "Server is running Parkour " + ChatColor.GRAY
                        + parkour.getDescription().getVersion());
                player.sendMessage("This plugin was developed by " + ChatColor.GOLD + "A5H73Y & steve4744");
                player.sendMessage("Project Page: " + ChatColor.AQUA + "https://www.spigotmc.org/resources/parkour.23685/");
                player.sendMessage("Discord Server: " + ChatColor.AQUA + "https://discord.gg/Gc8RGYr");
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

            default:
                TranslationUtils.sendTranslation("Error.UnknownCommand", player);
                TranslationUtils.sendTranslation("Help.Commands", player);
                break;
        }
        return true;
    }

    /**
     * Get the Player's chosen course.
     * If the have provided a course parameter, use that.
     * Otherwise fallback to the player's selected course.
     *
     * @param player requesting player
     * @param args command args
     * @param courseArg position of course argument
     * @return chosen course name
     */
    private String getChosenCourseName(Player player, String[] args, int courseArg) {
        return args.length != courseArg + 1 ? PlayerInfo.getSelectedCourse(player) : args[courseArg];
    }
}

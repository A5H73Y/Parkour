package io.github.a5h73y.parkour.commands;

import io.github.a5h73y.parkour.GUI.ParkourCoursesInventory;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.CreateParkourKitConversation;
import io.github.a5h73y.parkour.conversation.EditParkourKitConversation;
import io.github.a5h73y.parkour.course.CheckpointMethods;
import io.github.a5h73y.parkour.course.CourseInfo;
import io.github.a5h73y.parkour.course.CourseMethods;
import io.github.a5h73y.parkour.course.LobbyMethods;
import io.github.a5h73y.parkour.kit.ParkourKitInfo;
import io.github.a5h73y.parkour.manager.ChallengeManager;
import io.github.a5h73y.parkour.manager.QuietModeManager;
import io.github.a5h73y.parkour.other.Help;
import io.github.a5h73y.parkour.player.PlayerInfo;
import io.github.a5h73y.parkour.player.PlayerMethods;
import io.github.a5h73y.parkour.utilities.Static;
import io.github.a5h73y.parkour.utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParkourCommands implements CommandExecutor {

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

        if (Parkour.getSettings().isPermissionsForCommands()
                && !Utils.hasPermission(player, "Parkour.Basic", "Commands")) {
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Static.getParkourString() + "Plugin proudly created by " + ChatColor.AQUA + "A5H73Y & steve4744");
            player.sendMessage(Utils.getTranslation("Help.Commands", false));
            return true;
        }

        String firstArg = args[0].toLowerCase();

        switch (firstArg) {
            case "join":
                if (!Utils.validateArgs(player, args, 2)) {
                    return false;

                } else if (!Parkour.getInstance().getConfig().getBoolean("OnJoin.AllowViaCommand")) {
                    player.sendMessage(Utils.getTranslation("Error.AllowViaCommand"));
                    return false;
                }

                CourseMethods.joinCourse(player, args[1]);
                break;

            case "joinall":
                if (!Utils.hasPermission(player, "Parkour.Basic", "JoinAll")) {
                    return false;
                }

                player.openInventory(new ParkourCoursesInventory().buildInventory(player, 1));
                break;

            case "create":
                if (!Utils.hasPermission(player, "Parkour.Basic", "Create")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 2)) {
                    return false;
                }

                CourseMethods.createCourse(args[1], player);
                break;

            case "leave":
                PlayerMethods.playerLeave(player);
                break;

            case "info":
                PlayerMethods.displayPlayerInfo(args, player);
                break;

            case "stats":
            case "course":
                if (!Utils.validateArgs(player, args, 2)) {
                    return false;
                }

                CourseInfo.displayCourseInfo(args[1], player);
                break;

            case "lobby":
                LobbyMethods.joinLobby(args, player);
                break;

            case "setlobby":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;
                }

                LobbyMethods.createLobby(args, player);
                break;

            case "setcreator":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                CourseMethods.setCreator(args, player);
                break;

            case "checkpoint":
                if (!PlayerInfo.hasSelected(player)) {
                    return false;

                } else if (!Utils.hasPermissionOrCourseOwnership(
                        player, "Parkour.Admin", "Course", PlayerInfo.getSelected(player))) {
                    return false;
                }

                CheckpointMethods.createCheckpoint(args, player);
                break;

            case "finish":
                CourseMethods.setCompletionStatus(args, player);
                break;

            case "setstart":
                if (!PlayerInfo.hasSelected(player)) {
                    return false;

                } else if (!Utils.hasPermissionOrCourseOwnership(
                        player, "Parkour.Admin", "Course", PlayerInfo.getSelected(player))) {
                    return false;
                }

                CourseMethods.setStart(player);
                break;

            case "setautostart":
                if (!Utils.hasPermission(player, "Parkour.Admin", "Course")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 2)) {
                    return false;
                }

                CourseMethods.setAutoStart(args, player);
                break;

            case "prize":
                if (!Utils.hasPermission(player, "Parkour.Admin", "Prize")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 2)) {
                    return false;
                }

                CourseMethods.setPrize(args[1], player);
                break;

            case "perms":
                PlayerMethods.displayPermissions(player);
                break;

            case "kit":
                if (!Utils.hasPermission(player, "Parkour.Basic", "Kit")) {
                    return false;
                }

                PlayerMethods.givePlayerKit(args, player);
                break;

            case "delete":
                if (!Utils.hasPermission(player, "Parkour.Admin", "Delete")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                Utils.deleteCommand(args, player);
                break;

            case "select":
            case "edit":
                if (!Utils.validateArgs(player, args, 2)) {
                    return false;

                } else if (!Utils.hasPermissionOrCourseOwnership(
                        player, "Parkour.Admin", "Course", args[1])) {
                    return false;
                }

                CourseMethods.selectCourse(args, player);
                break;

            case "done":
            case "deselect":
            case "stopselect":
                CourseMethods.deselectCourse(player);
                break;

            case "tp":
            case "teleport":
                if (!Utils.hasPermission(player, "Parkour.Basic", "TP")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 2)) {
                    return false;
                }

                CheckpointMethods.teleportCheckpoint(args, player, false);
                break;

            case "tpc":
                if (!Utils.hasPermission(player, "Parkour.Basic", "TPC")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                CheckpointMethods.teleportCheckpoint(args, player, true);
                break;

            case "link":
                if (!Utils.validateArgs(player, args, 3, 4)) {
                    return false;

                } else if (!PlayerInfo.hasSelected(player)) {
                    return false;

                } else if (!Utils.hasPermissionOrCourseOwnership(
                        player, "Parkour.Admin", "Course", PlayerInfo.getSelected(player))) {
                    return false;
                }

                CourseMethods.linkCourse(args, player);
                break;

            case "setminlevel":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                CourseMethods.setMinLevel(args, player);
                break;

            case "setmaxdeath":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                CourseMethods.setMaxDeaths(args, player);
                break;

            case "setmaxtime":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                CourseMethods.setMaxTime(args, player);
                break;

            case "setjoinitem":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 4)) {
                    return false;
                }

                CourseMethods.setJoinItem(args, player);
                break;

            case "rewardonce":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 2)) {
                    return false;
                }

                CourseMethods.setRewardOnce(args, player);
                break;

            case "rewardlevel":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                CourseMethods.setRewardParkourLevel(args, player);
                break;

            case "rewardleveladd":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                CourseMethods.setRewardParkourLevelAddition(args, player);
                break;

            case "rewardrank":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                CourseMethods.setRewardParkourRank(args, player);
                break;

            case "rewarddelay":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                CourseMethods.setRewardDelay(args, player);
                break;

            case "rewardparkoins":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                CourseMethods.setRewardParkoins(args, player);
                break;

            case "quiet":
                QuietModeManager.getInstance().toggleQuietMode(player);
                break;

            case "reset":
                if (!Utils.hasPermission(player, "Parkour.Admin", "Reset")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3, 4)) {
                    return false;
                }

                Utils.resetCommand(args, player);
                break;

            case "test":
                if (!Utils.hasPermission(player, "Parkour.Admin", "Testmode")) {
                    return false;
                }

                PlayerMethods.toggleTestmode(args, player);
                break;

            case "economy":
            case "econ":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 2, 4)) {
                    return false;
                }

                Help.displayEconomy(args, player);
                break;

            case "invite":
                if (!Utils.hasPermission(player, "Parkour.Basic", "Invite")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 2)) {
                    return false;
                }

                PlayerMethods.invitePlayer(args, player);
                break;

            case "setmode":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 2)) {
                    return false;
                }

                CourseMethods.setCourseMode(args, player);
                break;

            case "createparkourkit":
            case "createkit":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;
                }

                new CreateParkourKitConversation(player).begin();
                break;

            case "editparkourkit":
            case "editkit":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;
                }

                new EditParkourKitConversation(player).begin();
                break;

            case "linkkit":
                if (!Utils.validateArgs(player, args, 3)) {
                    return false;

                } else if (!Utils.hasPermissionOrCourseOwnership(
                        player, "Parkour.Admin", "Course", args[1])) {
                    return false;
                }

                CourseMethods.linkParkourKit(args, player);
                break;

            case "listkit":
                if (!Utils.hasPermission(player, "Parkour.Basic", "Kit")) {
                    return false;
                }

                Utils.listParkourKit(args, player);
                break;

            case "validatekit":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;
                }

                ParkourKitInfo.validateParkourKit(args, player);
                break;

            case "challenge":
                if (!Utils.hasPermission(player, "Parkour.Basic", "Challenge")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3, 4)) {
                    return false;
                }

                CourseMethods.challengePlayer(args, player);
                break;

            case "list":
                CourseMethods.displayList(args, player);
                break;

            case "help":
                Help.lookupCommandHelp(args, player);
                break;

            case "leaderboard":
            case "leaderboards":
                if (!Utils.hasPermission(player, "Parkour.Basic", "Leaderboard")) {
                    return false;
                }

                CourseMethods.getLeaderboards(args, player);
                break;

            case "recreate":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;
                }

                player.sendMessage(Static.getParkourString() + "Recreating courses...");
                Parkour.getDatabase().recreateAllCourses();
                break;

            case "whitelist":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 2)) {
                    return false;
                }

                Utils.addWhitelistedCommand(args, player);
                break;

            case "setlevel":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                PlayerMethods.setLevel(args, player);
                break;

            case "setrank":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;

                } else if (!Utils.validateArgs(player, args, 3)) {
                    return false;
                }

                PlayerMethods.setRank(args, player);
                break;

            case "material":
                Utils.lookupMaterialInformation(args, player);
                break;

            //Other commands//
            case "about":
            case "ver":
            case "version":
                player.sendMessage(Static.getParkourString() + "Server is running Parkour " + ChatColor.GRAY + Static.getVersion());
                player.sendMessage("This plugin was developed by " + ChatColor.GOLD + "A5H73Y & steve4744");
                break;

            case "contact":
                player.sendMessage(Static.getParkourString() + "For information or help please contact me:");
                player.sendMessage("Spigot: " + ChatColor.AQUA + "A5H73Y");
                player.sendMessage("Project Page: " + ChatColor.AQUA + "https://www.spigotmc.org/resources/parkour.23685/");
                player.sendMessage("Discord Server: " + ChatColor.AQUA + "https://discord.gg/Gc8RGYr");
                break;

            case "request":
            case "bug":
                player.sendMessage(Static.getParkourString() + "To Request a feature or to Report a bug...");
                player.sendMessage("Click here: " + ChatColor.DARK_AQUA + "https://github.com/A5H73Y/Parkour/issues");
                break;

            case "tutorial":
                player.sendMessage(Static.getParkourString() + "To follow the official Parkour tutorials...");
                player.sendMessage("Click here: " + ChatColor.DARK_AQUA + "https://a5h73y.github.io/Parkour/");
                break;

            case "settings":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;
                }

                Help.displaySettings(player);
                break;

            case "cmds":
                Help.processCommandsInput(args, player);
                break;

            case "accept":
                ChallengeManager.getInstance().acceptChallenge(player);
                break;

            case "yes":
            case "no":
                player.sendMessage(Utils.getTranslation("Error.NoQuestion"));
                break;

            case "reload":
                if (!Utils.hasPermission(player, "Parkour.Admin")) {
                    return false;
                }

                Parkour.getInstance().reloadConfigurations();
                player.sendMessage(Utils.getTranslation("Other.Reload"));
                Utils.logToFile(player.getName() + " reloaded the Parkour config");
                break;

            default:
                player.sendMessage(Utils.getTranslation("Error.UnknownCommand"));
                player.sendMessage(Utils.getTranslation("Help.Commands", false));
                break;
        }
        return true;
    }
}

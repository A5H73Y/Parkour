package io.github.a5h73y.parkour.commands;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.kit.ParkourKitInfo;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Backup;
import io.github.a5h73y.parkour.other.Help;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ParkourConsoleCommands extends AbstractPluginReceiver implements CommandExecutor {

    public ParkourConsoleCommands(final Parkour parkour) {
        super(parkour);
    }

    private static void displayCommands() {
        PluginUtils.log("pac reload");
        PluginUtils.log("pac recreate");
        PluginUtils.log("pac setminlevel (course) (level)");
        PluginUtils.log("pac setmaxdeath (course) (deaths)");
        PluginUtils.log("pac setmaxtime (course) (seconds)");
        PluginUtils.log("pac setjoinitem (course) (item) (amount)");
        PluginUtils.log("pac rewardonce (course)");
        PluginUtils.log("pac rewardlevel (course) (level)");
        PluginUtils.log("pac rewardrank (level) (rank)");
        PluginUtils.log("pac rewardparkoins (course) (amount)");
        PluginUtils.log("pac setlevel (player) (level)");
        PluginUtils.log("pac setrank (player) (rank)");
        PluginUtils.log("pac list (courses / players)");
        PluginUtils.log("pac listkit [kit]");
        PluginUtils.log("pac settings");
        PluginUtils.log("pac help (command)");
        PluginUtils.log("pac backup : Create a backup zip of the Parkour config folder");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command,
                             final String label, final String[] args) {
        if (!command.getName().equalsIgnoreCase("paconsole")) {
            return false;
        }

        if (!(sender instanceof ConsoleCommandSender)) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("v" + parkour.getDescription().getVersion() + " installed. Plugin created by A5H73Y & steve4744.");
            sender.sendMessage("Enter 'pac cmds' to display all console commands.");
            return true;
        }

        String firstArg = args[0].toLowerCase();

        switch (firstArg) {
            case "reload":
                parkour.getConfigManager().reloadConfigs();
                sender.sendMessage("Config reloaded!");
                break;

            case "join":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                Player player = Bukkit.getPlayer(args[2]);

                if (player == null) {
                    sender.sendMessage("Player is not online");
                    return false;
                }

                parkour.getPlayerManager().joinCourse(player, args[1]);
                break;

            case "recreate":
                parkour.getDatabase().recreateAllCourses();
                break;

            case "setminlevel":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setMinLevel(args, sender);
                break;

            case "setmaxdeath":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setMaxDeaths(args, sender);
                break;

            case "setmaxtime":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setMaxTime(args, sender);
                break;

            case "setjoinitem":
                if (!ValidationUtils.validateArgs(sender, args, 4)) {
                    return false;
                }

                parkour.getCourseManager().addJoinItem(args, sender);
                break;

            case "rewardonce":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setRewardOnce(args, sender);
                break;

            case "rewardlevel":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourLevel(args, sender);
                break;

            case "rewardleveladd":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourLevelAddition(args, sender);
                break;

            case "rewardrank":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourRank(args, sender);
                break;

            case "rewardparkoins":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkoins(args, sender);
                break;

            case "setlevel":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getPlayerManager().setParkourLevel(args, sender);
                break;

            case "setrank":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getPlayerManager().setParkourRank(args, sender);
                break;

            case "list":
                parkour.getCourseManager().displayList(args, sender);
                break;

            case "listkit":
                ParkourKitInfo.listParkourKit(args, sender);
                break;

            case "settings":
                Help.displaySettings(sender);
                break;

            case "help":
                Help.lookupCommandHelp(args, sender);
                break;

            case "cmds":
                ParkourConsoleCommands.displayCommands();
                break;

            case "backup":
                Backup.backupNow(true);
                break;

            default:
                sender.sendMessage("Unknown Command. Enter 'pac cmds' to display all console commands.");
                break;
        }
        return true;
    }
}

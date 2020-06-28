package io.github.a5h73y.parkour.commands;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.course.CourseMethods;
import io.github.a5h73y.parkour.other.Backup;
import io.github.a5h73y.parkour.other.Help;
import io.github.a5h73y.parkour.player.PlayerMethods;
import io.github.a5h73y.parkour.utilities.Static;
import io.github.a5h73y.parkour.utilities.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class ParkourConsoleCommands implements CommandExecutor {

    private static void displayCommands() {
        Utils.log("pac reload");
        Utils.log("pac recreate");
        Utils.log("pac setminlevel (course) (level)");
        Utils.log("pac setmaxdeath (course) (deaths)");
        Utils.log("pac setmaxtime (course) (seconds)");
        Utils.log("pac setjoinitem (course) (item) (amount)");
        Utils.log("pac rewardonce (course)");
        Utils.log("pac rewardlevel (course) (level)");
        Utils.log("pac rewardrank (level) (rank)");
        Utils.log("pac rewardparkoins (course) (amount)");
        Utils.log("pac setlevel (player) (level)");
        Utils.log("pac setrank (player) (rank)");
        Utils.log("pac list (courses / players)");
        Utils.log("pac listkit [kit]");
        Utils.log("pac settings");
        Utils.log("pac help (command)");
        Utils.log("pac backup : Create a backup zip of the Parkour config folder");
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
            Utils.log("v" + Static.getVersion() + " installed. Plugin created by A5H73Y.");
            Utils.log("Enter 'pac cmds' to display all console commands.");
            return true;
        }

        String firstArg = args[0].toLowerCase();

        switch (firstArg) {
            case "reload":
                Parkour.getInstance().reloadConfigurations();
                Utils.log("Config reloaded!");
                break;

            case "recreate":
                Parkour.getDatabase().recreateAllCourses();
                break;

            case "setminlevel":
                if (!Utils.validateArgs(sender, args, 3)) {
                    return false;
                }

                CourseMethods.setMinLevel(args, sender);
                break;

            case "setmaxdeath":
                if (!Utils.validateArgs(sender, args, 3)) {
                    return false;
                }

                CourseMethods.setMaxDeaths(args, sender);
                break;

            case "setmaxtime":
                if (!Utils.validateArgs(sender, args, 3)) {
                    return false;
                }

                CourseMethods.setMaxTime(args, sender);
                break;

            case "setjoinitem":
                if (!Utils.validateArgs(sender, args, 4)) {
                    return false;
                }

                CourseMethods.setJoinItem(args, sender);
                break;

            case "rewardonce":
                if (!Utils.validateArgs(sender, args, 2)) {
                    return false;
                }

                CourseMethods.setRewardOnce(args, sender);
                break;

            case "rewardlevel":
                if (!Utils.validateArgs(sender, args, 3)) {
                    return false;
                }

                CourseMethods.setRewardParkourLevel(args, sender);
                break;

            case "rewardleveladd":
                if (!Utils.validateArgs(sender, args, 3)) {
                    return false;
                }

                CourseMethods.setRewardParkourLevelAddition(args, sender);
                break;

            case "rewardrank":
                if (!Utils.validateArgs(sender, args, 3)) {
                    return false;
                }

                CourseMethods.setRewardParkourRank(args, sender);
                break;

            case "rewardparkoins":
                if (!Utils.validateArgs(sender, args, 3)) {
                    return false;
                }

                CourseMethods.setRewardParkoins(args, sender);
                break;

            case "setlevel":
                if (!Utils.validateArgs(sender, args, 3)) {
                    return false;
                }

                PlayerMethods.setLevel(args, sender);
                break;

            case "setrank":
                if (!Utils.validateArgs(sender, args, 3)) {
                    return false;
                }

                PlayerMethods.setRank(args, sender);
                break;

            case "list":
                CourseMethods.displayList(args, sender);
                break;

            case "listkit":
                Utils.listParkourKit(args, sender);
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
                Utils.log("Unknown Command. Enter 'pac cmds' to display all console commands.");
                break;
        }
        return true;
    }
}

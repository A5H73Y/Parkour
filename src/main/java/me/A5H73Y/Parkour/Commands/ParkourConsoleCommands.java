package me.A5H73Y.Parkour.Commands;

import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.Backup;
import me.A5H73Y.Parkour.Other.Help;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class ParkourConsoleCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("paconsole"))
            return false;

        if (!(sender instanceof ConsoleCommandSender))
            return false;

        if (args.length == 0) {
            Utils.log("v" + Static.getVersion() + " installed. Plugin created by A5H73Y.");
            Utils.log("Enter 'pac cmds' to display all console commands.");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            Utils.reloadConfig();
            Utils.log("Config reloaded!");

        } else if (args[0].equalsIgnoreCase("recreate")) {
            DatabaseMethods.recreateAllCourses();

        } else if (args[0].equalsIgnoreCase("setminlevel")) {
            if (!Utils.validateArgs(sender, args, 3))
                return false;

            CourseMethods.setMinLevel(args, sender);

        } else if (args[0].equalsIgnoreCase("setmaxdeath")) {
            if (!Utils.validateArgs(sender, args, 3))
                return false;

            CourseMethods.setMaxDeaths(args, sender);

        } else if (args[0].equalsIgnoreCase("setjoinitem")) {
            if (!Utils.validateArgs(sender, args, 4))
                return false;

            CourseMethods.setJoinItem(args, sender);

        } else if (args[0].equalsIgnoreCase("rewardonce")) {
            if (!Utils.validateArgs(sender, args, 2))
                return false;

            CourseMethods.setRewardOnce(args, sender);

        } else if (args[0].equalsIgnoreCase("rewardlevel")) {
            if (!Utils.validateArgs(sender, args, 3))
                return false;

            CourseMethods.setRewardLevel(args, sender);

        } else if (args[0].equalsIgnoreCase("rewardleveladd")) {
            if (!Utils.validateArgs(sender, args, 3))
                return false;

            CourseMethods.setRewardLevelAdd(args, sender);

        } else if (args[0].equalsIgnoreCase("rewardrank")) {
            if (!Utils.validateArgs(sender, args, 3))
                return false;

            CourseMethods.setRewardRank(args, sender);

        } else if (args[0].equalsIgnoreCase("rewardparkoins")) {
            if (!Utils.validateArgs(sender, args, 3))
                return false;

            CourseMethods.setRewardParkoins(args, sender);

        } else if (args[0].equalsIgnoreCase("setlevel")) {
            if (!Utils.validateArgs(sender, args, 3))
                return false;

            PlayerMethods.setLevel(args, sender);

        } else if (args[0].equalsIgnoreCase("setrank")) {
            if (!Utils.validateArgs(sender, args, 3))
                return false;

            PlayerMethods.setRank(args, sender);

        } else if (args[0].equalsIgnoreCase("list")) {
            CourseMethods.displayList(args, sender);

        } else if (args[0].equalsIgnoreCase("listkit")) {
            Utils.listParkourKit(args, sender);

        } else if (args[0].equalsIgnoreCase("settings")) {
            Help.displaySettings(sender);

        } else if (args[0].equalsIgnoreCase("help")) {
            Help.lookupCommandHelp(args, sender);

        } else if (args[0].equalsIgnoreCase("cmds")) {
            ParkourConsoleCommands.displayCommands();

        } else if (args[0].equalsIgnoreCase("backup")) {
            Backup.backupNow(true);

        } else {
            Utils.log("Unknown Command. Enter 'pac cmds' to display all console commands.");
        }
        return true;
    }

    private static void displayCommands() {
        Utils.log("pac reload");
        Utils.log("pac recreate");
        Utils.log("pac setminlevel (course) (level)");
        Utils.log("pac setmaxdeath (course) (amount)");
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
}

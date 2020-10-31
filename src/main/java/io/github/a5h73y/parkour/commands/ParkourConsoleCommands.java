package io.github.a5h73y.parkour.commands;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Backup;
import io.github.a5h73y.parkour.other.Help;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Console related Parkour commands handling.
 */
public class ParkourConsoleCommands extends AbstractPluginReceiver implements CommandExecutor {

    public ParkourConsoleCommands(final Parkour parkour) {
        super(parkour);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(Parkour.getPrefix() + "Use '/parkour' for player commands.");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(Parkour.getPrefix() + "proudly created by A5H73Y & steve4744.");
            TranslationUtils.sendTranslation("Help.ConsoleCommands", sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                parkour.getConfigManager().reloadConfigs();
                TranslationUtils.sendTranslation("Parkour.ConfigReloaded", sender);
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

            case "setcourse":
                if (!ValidationUtils.validateArgs(sender, args, 2, 100)) {
                    return false;
                }

                parkour.getCourseManager().processSetCommand(sender, args);
                break;

            case "setplayer":
                if (!ValidationUtils.validateArgs(sender, args, 2, 100)) {
                    return false;
                }

                parkour.getPlayerManager().processSetCommand(sender, args);
                break;

            case "addjoinitem":
                if (!ValidationUtils.validateArgs(sender, args, 4, 6)) {
                    return false;
                }

                parkour.getCourseManager().addJoinItem(sender, args);
                break;

            case "rewardonce":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().setRewardOnce(sender, args[1]);
                break;

            case "rewardlevel":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourLevel(sender, args[1], args[2]);
                break;

            case "rewardleveladd":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkourLevelAddition(sender, args[1], args[2]);
                break;

            case "rewardrank":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getPlayerManager().setRewardParkourRank(sender, args[1], args[2]);
                break;

            case "rewardparkoins":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkoins(sender, args[1], args[2]);
                break;

            case "rewarddelay":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardDelay(sender, args[1], args[2]);
                break;

            case "list":
                parkour.getCourseManager().displayList(sender, args);
                break;

            case "listkit":
                parkour.getParkourKitManager().listParkourKit(sender, args);
                break;

            case "settings":
                Help.displaySettings(sender);
                break;

            case "help":
                Help.lookupCommandHelp(sender, args);
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

    private static void displayCommands() {
        PluginUtils.log("pac reload");
        PluginUtils.log("pac recreate");
        PluginUtils.log("pac setcourse (course) (command) (value)");
        PluginUtils.log("pac setplayer (player) (command) (value)");
        PluginUtils.log("pac addjoinitem (course) (item) (amount) [label] [unbreakable]");
        PluginUtils.log("pac rewardonce (course)");
        PluginUtils.log("pac rewardlevel (course) (level)");
        PluginUtils.log("pac rewardleveladd (course) (level)");
        PluginUtils.log("pac rewardrank (level) (rank)");
        PluginUtils.log("pac rewardparkoins (course) (amount)");
        PluginUtils.log("pac rewarddelay (course) (delay)");
        PluginUtils.log("pac list (courses / players)");
        PluginUtils.log("pac listkit [kit]");
        PluginUtils.log("pac settings");
        PluginUtils.log("pac help (command)");
        PluginUtils.log("pac backup : Create a backup zip of the Parkour config folder");
    }
}

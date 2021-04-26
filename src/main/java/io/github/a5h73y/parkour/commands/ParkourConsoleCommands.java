package io.github.a5h73y.parkour.commands;

import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.CoursePrizeConversation;
import io.github.a5h73y.parkour.conversation.CreateParkourKitConversation;
import io.github.a5h73y.parkour.conversation.EditParkourKitConversation;
import io.github.a5h73y.parkour.conversation.ParkourModeConversation;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Backup;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                             @NotNull String... args) {
        if (sender instanceof Player) {
            TranslationUtils.sendMessage(sender, "Use '/parkour' for player commands.");
            return false;
        }

        if (args.length == 0) {
            TranslationUtils.sendMessage(sender, "Plugin proudly created by &bA5H73Y &f& &bsteve4744");
            TranslationUtils.sendTranslation("Help.ConsoleCommands", sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "addjoinitem":
                if (!ValidationUtils.validateArgs(sender, args, 4, 6)) {
                    return false;
                }

                parkour.getCourseManager().addJoinItem(sender, args);
                break;

            case "cache":
                PluginUtils.cacheCommand(sender, args.length == 2 ? args[1] : null);
                break;

            case "challengeonly":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().toggleChallengeOnlyStatus(sender, args[1]);
                break;

            case "cmds":
                displayConsoleCommands();
                break;

            case "createkit":
            case "createparkourkit":
                new CreateParkourKitConversation((Conversable) sender).begin();
                break;

            case "delete":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                PluginUtils.deleteCommand(sender, args[1], args[2]);
                break;

            case "econ":
            case "economy":
                if (!ValidationUtils.validateArgs(sender, args, 2, 4)) {
                    return false;
                }

                parkour.getEconomyApi().processCommand(sender, args);
                break;

            case "editkit":
            case "editparkourkit":
                new EditParkourKitConversation((Conversable) sender).begin();
                break;

            case "help":
                ParkourCommandHelp.displayCommandHelp(sender, args);
                break;

            case "info":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                parkour.getPlayerManager().displayParkourInfo(sender, Bukkit.getOfflinePlayer(args[1]));
                break;

            case "join":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;

                } else if (findPlayer(sender, args[2]) == null) {
                    return false;
                }

                parkour.getPlayerManager().joinCourse(findPlayer(sender, args[2]), args[1]);
                break;

            case "leaderboard":
            case "leaderboards":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getDatabase().displayTimeEntries(sender, args[1],
                        parkour.getDatabase().getTopCourseResults(args[1], Integer.parseInt(args[2])));
                break;

            case "leave":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;

                } else if (findPlayer(sender, args[1]) == null) {
                    return false;
                }

                parkour.getPlayerManager().leaveCourse(findPlayer(sender, args[1]));
                break;

            case "linkkit":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setParkourKit(sender, args[1], args[2]);
                break;

            case "list":
                parkour.getCourseManager().displayList(sender, args);
                break;

            case "listkit":
                parkour.getParkourKitManager().displayParkourKits(sender, args);
                break;

            case "prize":
            case "setprize":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                new CoursePrizeConversation((Conversable) sender).withCourseName(args[1]).begin();
                break;

            case "recreate":
                parkour.getDatabase().recreateAllCourses(true);
                break;

            case "reload":
                PluginUtils.clearAllCache();
                parkour.getConfigManager().reloadConfigs();
                TranslationUtils.sendTranslation("Parkour.ConfigReloaded", sender);
                break;

            case "reset":
                if (!ValidationUtils.validateArgs(sender, args, 3, 4)) {
                    return false;
                }

                PluginUtils.resetCommand(sender, args[1], args[2], args.length == 4 ? args[3] : null);
                break;

            case "respawn":
            case "back":
            case "die":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;

                } else if (findPlayer(sender, args[1]) == null) {
                    return false;
                }

                parkour.getPlayerManager().playerDie(findPlayer(sender, args[1]));
                break;

            case "restart":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;

                } else if (findPlayer(sender, args[1]) == null) {
                    return false;
                }

                parkour.getPlayerManager().restartCourse(findPlayer(sender, args[1]));
                break;

            case "resumable":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().toggleResumable(sender, args[1]);
                break;

            case "rewarddelay":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardDelay(sender, args[1], args[2]);
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

                parkour.getCourseManager().setRewardParkourLevelIncrease(sender, args[1], args[2]);
                break;

            case "rewardonce":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                parkour.getCourseManager().toggleRewardOnceStatus(sender, args[1]);
                break;

            case "rewardparkoins":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getCourseManager().setRewardParkoins(sender, args[1], args[2]);
                break;

            case "rewardrank":
                if (!ValidationUtils.validateArgs(sender, args, 3)) {
                    return false;
                }

                parkour.getPlayerManager().setRewardParkourRank(sender, args[1], args[2]);
                break;

            case "setcourse":
                if (!ValidationUtils.validateArgs(sender, args, 2, 100)) {
                    return false;
                }

                parkour.getCourseManager().processSetCommand(sender, args);
                break;

            case "setmode":
            case "setparkourmode":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                new ParkourModeConversation((Conversable) sender).withCourseName(args[1]).begin();
                break;

            case "setplayer":
                if (!ValidationUtils.validateArgs(sender, args, 2, 100)) {
                    return false;
                }

                parkour.getPlayerManager().processSetCommand(sender, args);
                break;

            case "setplayerlimit":
                parkour.getCourseManager().setPlayerLimit(sender, args[1], args[2]);
                break;

            case "sql":
                parkour.getDatabase().displayInformation(sender);
                break;

            case "stats":
            case "course":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                CourseInfo.displayCourseInfo(sender, args[1]);
                break;

            case "validatekit":
                parkour.getParkourKitManager().validateParkourKit(sender, args.length == 2 ? args[1] : DEFAULT);
                break;

            case "whitelist":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                parkour.getConfig().addWhitelistedCommand(sender, args[1]);
                break;

            case "yes":
            case "no":
                if (!parkour.getQuestionManager().hasBeenAskedQuestion(sender)) {
                    TranslationUtils.sendTranslation("Error.NoQuestion", sender);
                    break;
                }

                parkour.getQuestionManager().answerQuestion(sender, args[0]);
                break;

            case "config":
                if (!ValidationUtils.validateArgs(sender, args, 2)) {
                    return false;
                }

                if (!args[1].startsWith("MySQL")) {
                    TranslationUtils.sendValue(sender, args[1], parkour.getConfig().getString(args[1]));
                }
                break;

            case "backup":
                Backup.backupNow(true);
                break;

            default:
                TranslationUtils.sendMessage(sender, "Unknown Command. Enter 'pac cmds' to display all console commands.");
                break;
        }
        return true;
    }

    private void displayConsoleCommands() {
        parkour.getCommandUsages().stream()
                .filter(commandUsage -> commandUsage.getConsoleSyntax() != null)
                .forEach(commandUsage -> PluginUtils.log(commandUsage.getConsoleSyntax()));
    }

    /**
     * Attempt to find matching Player by name.
     *
     * @param sender requesting sender
     * @param playerName target player name
     * @return {@link Player}
     */
    @Nullable
    private Player findPlayer(CommandSender sender, String playerName) {
        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (targetPlayer == null) {
            TranslationUtils.sendMessage(sender, "Player is not online");
        }
        return targetPlayer;
    }
}

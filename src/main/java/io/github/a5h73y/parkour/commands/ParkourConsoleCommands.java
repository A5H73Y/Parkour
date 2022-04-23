package io.github.a5h73y.parkour.commands;

import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.CoursePrizeConversation;
import io.github.a5h73y.parkour.conversation.CreateParkourKitConversation;
import io.github.a5h73y.parkour.conversation.EditParkourKitConversation;
import io.github.a5h73y.parkour.conversation.ParkourModeConversation;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.PluginBackupUtil;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
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
    public boolean onCommand(@NotNull CommandSender commandSender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String... args) {
        if (commandSender instanceof Player) {
            TranslationUtils.sendMessage(commandSender, "Use '/parkour' for player commands.");
            return false;
        }

        if (args.length == 0) {
            TranslationUtils.sendMessage(commandSender, "Plugin proudly created by &bA5H73Y &f& &bsteve4744");
            TranslationUtils.sendTranslation("Help.ConsoleCommands", commandSender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "addjoinitem":
                if (!ValidationUtils.validateArgs(commandSender, args, 4, 6)) {
                    return false;
                }

                parkour.getCourseSettingsManager().addJoinItem(commandSender, args);
                break;

            case "admin":
            case "administrator":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;
                }

                parkour.getAdministrationManager().processAdminCommand(commandSender, args[1], args[2]);
                break;

            case "cache":
                parkour.getAdministrationManager().processCacheCommand(commandSender, args.length == 2 ? args[1] : null);
                break;

            case "challengeonly":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;
                }

                parkour.getCourseSettingsManager().setChallengeOnlyStatus(commandSender, args[1], null);
                break;

            case "cmds":
                displayConsoleCommands();
                break;

            case "createkit":
            case "createparkourkit":
                new CreateParkourKitConversation((Conversable) commandSender).begin();
                break;

            case "delete":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;
                }

                parkour.getAdministrationManager().processDeleteCommand(commandSender, args[1], args[2]);
                break;

            case "econ":
            case "economy":
                if (!ValidationUtils.validateArgs(commandSender, args, 2, 4)) {
                    return false;
                }

                parkour.getEconomyApi().processCommand(commandSender, args);
                break;

            case "editkit":
            case "editparkourkit":
                new EditParkourKitConversation((Conversable) commandSender).begin();
                break;

            case "help":
                parkour.getParkourCommands().displayCommandHelp(commandSender, args);
                break;

            case "info":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;
                }

                parkour.getPlayerManager().displayParkourInfo(commandSender, Bukkit.getOfflinePlayer(args[1]));
                break;

            case "join":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;

                } else if (findPlayer(commandSender, args[2]) == null) {
                    return false;
                }

                parkour.getPlayerManager().joinCourse(findPlayer(commandSender, args[2]), args[1]);
                break;

            case "leaderboard":
            case "leaderboards":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;
                }

                parkour.getDatabaseManager().displayTimeEntries(commandSender, args[1],
                        parkour.getDatabaseManager().getTopCourseResults(args[1], Integer.parseInt(args[2])));
                break;

            case "leave":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;

                } else if (findPlayer(commandSender, args[1]) == null) {
                    return false;
                }

                parkour.getPlayerManager().leaveCourse(findPlayer(commandSender, args[1]));
                break;

            case "leaveall":
                TranslationUtils.sendMessage(commandSender,
                        parkour.getParkourSessionManager().getNumberOfParkourPlayers() + " Players leaving Courses.");
                parkour.getParkourSessionManager().getOnlineParkourPlayers()
                        .forEach(player -> parkour.getPlayerManager().leaveCourse(player));
                break;

            case "linkkit":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;
                }

                parkour.getCourseSettingsManager().setParkourKit(commandSender, args[1], args[2]);
                break;

            case "list":
                parkour.getCourseManager().displayList(commandSender, args);
                break;

            case "listkit":
                parkour.getParkourKitManager().displayParkourKits(commandSender, args.length > 1 ? args[1] : DEFAULT);
                break;

            case "manualcheckpoint":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;

                } else if (findPlayer(commandSender, args[1]) == null) {
                    return false;
                }

                parkour.getPlayerManager().setManualCheckpoint(findPlayer(commandSender, args[1]));
                break;

            case "prize":
            case "setprize":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;
                }

                new CoursePrizeConversation((Conversable) commandSender).withCourseName(args[1]).begin();
                break;

            case "recreate":
                parkour.getDatabaseManager().recreateAllCourses(true);
                break;

            case "reload":
                parkour.getAdministrationManager().clearAllCache();
                parkour.getConfigManager().reloadConfigs();
                TranslationUtils.sendTranslation("Parkour.ConfigReloaded", commandSender);
                break;

            case "reset":
                if (!ValidationUtils.validateArgs(commandSender, args, 3, 4)) {
                    return false;
                }

                parkour.getAdministrationManager().processResetCommand(commandSender, args[1], args[2], args.length == 4 ? args[3] : null);
                break;

            case "respawn":
            case "back":
            case "die":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;

                } else if (findPlayer(commandSender, args[1]) == null) {
                    return false;
                }

                parkour.getPlayerManager().playerDie(findPlayer(commandSender, args[1]));
                break;

            case "restart":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;

                } else if (findPlayer(commandSender, args[1]) == null) {
                    return false;
                }

                parkour.getPlayerManager().restartCourse(findPlayer(commandSender, args[1]));
                break;

            case "resumable":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;
                }

                parkour.getCourseSettingsManager().setResumable(commandSender, args[1], null);
                break;

            case "rewarddelay":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;
                }

                parkour.getCourseSettingsManager().setRewardDelay(commandSender, args[1], args[2]);
                break;

            case "rewardlevel":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;
                }

                parkour.getCourseSettingsManager().setRewardParkourLevel(commandSender, args[1], args[2]);
                break;

            case "rewardleveladd":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;
                }

                parkour.getCourseSettingsManager().setRewardParkourLevelIncrease(commandSender, args[1], args[2]);
                break;

            case "rewardonce":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;
                }

                parkour.getCourseSettingsManager().setRewardOnceStatus(commandSender, args[1], null);
                break;

            case "rewardparkoins":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;
                }

                parkour.getCourseSettingsManager().setRewardParkoins(commandSender, args[1], args[2]);
                break;

            case "rewardrank":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;
                }

                parkour.getParkourRankManager().setRewardParkourRank(commandSender, args[1], args[2]);
                break;

            case "setcheckpoint":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;

                } else if (findPlayer(commandSender, args[1]) == null) {
                    return false;

                } else if (!ValidationUtils.isPositiveInteger(args[2])) {
                    return false;
                }

                parkour.getPlayerManager().manuallyIncreaseCheckpoint(
                        findPlayer(commandSender, args[1]), Integer.parseInt(args[2]));
                break;


            case "setcourse":
                if (!ValidationUtils.validateArgs(commandSender, args, 2, 100)) {
                    return false;
                }

                parkour.getCourseSettingsManager().processCommand(commandSender, args);
                break;

            case "setlobbycommand":
                if (!ValidationUtils.validateArgs(commandSender, args, 3, 100)) {
                    return false;
                }

                parkour.getLobbyManager().addLobbyCommand(commandSender, args[1], StringUtils.extractMessageFromArgs(args, 2));
                break;

            case "setmode":
            case "setparkourmode":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;
                }

                new ParkourModeConversation((Conversable) commandSender).withCourseName(args[1]).begin();
                break;

            case "setplayer":
                if (!ValidationUtils.validateArgs(commandSender, args, 2, 100)) {
                    return false;
                }

                parkour.getPlayerManager().processSetCommand(commandSender, args);
                break;

            case "setplayerlimit":
                parkour.getCourseSettingsManager().setPlayerLimit(commandSender, args[1], args[2]);
                break;

            case "sql":
                parkour.getDatabaseManager().displayInformation(commandSender);
                break;

            case "stats":
            case "course":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;
                }

                CourseConfig.displayCourseInfo(commandSender, args[1]);
                break;

            case "validatekit":
                parkour.getParkourKitManager().validateParkourKit(commandSender, args.length == 2 ? args[1] : DEFAULT);
                break;

            case "yes":
            case "no":
                if (!parkour.getQuestionManager().hasBeenAskedQuestion(commandSender)) {
                    TranslationUtils.sendTranslation("Error.NoQuestion", commandSender);
                    break;
                }

                parkour.getQuestionManager().answerQuestion(commandSender, args[0]);
                break;

            case "config":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;
                }

                if (!args[1].startsWith("MySQL")) {
                    TranslationUtils.sendValue(commandSender, args[1], parkour.getParkourConfig().getString(args[1]));
                }
                break;

            case "backup":
                PluginBackupUtil.backupNow(true);
                break;

            default:
                TranslationUtils.sendMessage(commandSender, "Unknown Command. Enter 'pac cmds' to display all console commands.");
                break;
        }
        return true;
    }

    private void displayConsoleCommands() {
        parkour.getParkourCommands().getCommandUsages().stream()
                .filter(commandUsage -> commandUsage.getConsoleSyntax() != null)
                .forEach(commandUsage -> PluginUtils.log(commandUsage.getConsoleSyntax()));
    }

    /**
     * Attempt to find matching Player by name.
     *
     * @param commandSender command sender
     * @param playerName target player name
     * @return {@link Player}
     */
    @Nullable
    private Player findPlayer(CommandSender commandSender, String playerName) {
        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (targetPlayer == null) {
            TranslationUtils.sendMessage(commandSender, "Player is not online");
        }
        return targetPlayer;
    }
}

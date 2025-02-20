package io.github.a5h73y.parkour.commands;

import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.CoursePrizeConversation;
import io.github.a5h73y.parkour.conversation.parkourkit.CreateParkourKitConversation;
import io.github.a5h73y.parkour.conversation.parkourkit.EditParkourKitConversation;
import io.github.a5h73y.parkour.gui.impl.JoinAllGui;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.PluginBackupUtil;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
                if (!ValidationUtils.validateArgs(commandSender, args, 3, 99)) {
                    return false;
                }

                parkour.getAdministrationManager().processAdminCommand(commandSender, args[1], args[2], args);
                break;

            case "cache":
                parkour.getAdministrationManager().processCacheCommand(commandSender, args.length == 2 ? args[1] : null);
                break;

            case "cmds":
                displayConsoleCommands();
                break;

            case "config":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;
                }

                if (!args[1].startsWith("MySQL")) {
                    TranslationUtils.sendValue(commandSender, args[1], parkour.getParkourConfig().getString(args[1]));
                }
                break;

            case "stats":
            case "course":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;
                }

                CourseConfig.displayCourseInfo(commandSender, args[1]);
                break;

            case "createkit":
            case "createparkourkit":
                new CreateParkourKitConversation((Conversable) commandSender).begin();
                break;

            case "delete":
                if (!ValidationUtils.validateArgs(commandSender, args, 3, 4)) {
                    return false;
                }

                parkour.getAdministrationManager().processDeleteCommand(
                        commandSender, args[1], args[2], args.length > 3 ? args[3] : null);
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

            case "finish":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;

                } else if (findPlayer(commandSender, args[1]) == null) {
                    return false;
                }

                parkour.getPlayerManager().finishCourse(findPlayer(commandSender, args[1]));
                break;

            case "help":
                parkour.getParkourCommands().displayCommandHelp(commandSender, args);
                break;

            case "info":
            case "player":
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

            case "joinall":
                if (!ValidationUtils.validateArgs(commandSender, args, 2, 100)) {
                    return false;

                } else if (findPlayer(commandSender, args[1]) == null) {
                    return false;
                }

                parkour.getGuiManager().showMenu(findPlayer(commandSender, args[1]), new JoinAllGui(Arrays.asList(args).subList(1, args.length)));
                break;

            case "leaderboard":
            case "leaderboards":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;
                }

                parkour.getDatabaseManager().displayTimeEntries(commandSender, args[1],
                        parkour.getDatabaseManager().getTopCourseResults(args[1], Integer.parseInt(args[2])));
                break;

            case "list":
                parkour.getAdministrationManager().processListCommand(commandSender, args);
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

            case "listkit":
                parkour.getParkourKitManager().displayParkourKits(commandSender, args.length > 1 ? args[1] : DEFAULT);
                break;

            case "manualcheckpoint":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;

                } else if (findPlayer(commandSender, args[1]) == null) {
                    return false;
                }

                parkour.getPlayerManager().setManualCheckpoint(findPlayer(commandSender, args[1]), null);
                break;

            case "placeholder":
            case "parse":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;

                } else if (findPlayer(commandSender, args[2]) == null) {
                    return false;
                }

                parkour.getPlaceholderApi().evaluatePlaceholder(findPlayer(commandSender, args[2]), args[1]);
                break;

            case "printplaceholdercache":
                parkour.getPlaceholderApi().printCacheSummary(commandSender);
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

                parkour.getAdministrationManager().processResetCommand(
                        commandSender, args[1], args[2], args.length == 4 ? args[3] : null);
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

            case "rewardrank":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;
                }

                parkour.getParkourRankManager().setRewardParkourRank(commandSender, args[1], args[2]);
                break;

            case "session":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;

                } else if (findPlayer(commandSender, args[2]) == null) {
                    return false;
                }

                parkour.getParkourSessionManager().processCommand(findPlayer(commandSender, args[2]), args[1]);
                break;

            case "setcheckpoint":
                if (!ValidationUtils.validateArgs(commandSender, args, 3)) {
                    return false;

                } else if (findPlayer(commandSender, args[1]) == null) {
                    return false;

                } else if (!ValidationUtils.isPositiveInteger(args[2])) {
                    return false;
                }

                // TODO if (--force) then parkour.getPlayerManager().increaseCheckpoint(

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

            case "setplayer":
                if (!ValidationUtils.validateArgs(commandSender, args, 2, 100)) {
                    return false;
                }

                parkour.getPlayerManager().processSetCommand(commandSender, args);
                break;

            case "sql":
                parkour.getDatabaseManager().displayInformation(commandSender);
                break;

            case "validatekit":
                parkour.getParkourKitManager().validateParkourKit(commandSender, args.length == 2 ? args[1] : DEFAULT);
                break;

            case "tutorial":
            case "request":
            case "bug":
                TranslationUtils.sendMessage(commandSender, "To follow the official Parkour tutorials...");
                TranslationUtils.sendMessage(commandSender, "Click here:&3 https://a5h73y.github.io/Parkour/", false);
                TranslationUtils.sendMessage(commandSender, "To Request a feature or to Report a bug...");
                TranslationUtils.sendMessage(commandSender, "Click here:&3 https://github.com/A5H73Y/Parkour/issues", false);
                break;

            case "support":
            case "contact":
            case "about":
            case "version":
                TranslationUtils.sendMessage(commandSender, "Server is running Parkour &6"
                        + parkour.getDescription().getVersion());
                TranslationUtils.sendMessage(commandSender, "Plugin proudly created by &bA5H73Y &f& &bsteve4744", false);
                TranslationUtils.sendMessage(commandSender, "Project Page:&b https://www.spigotmc.org/resources/parkour.23685/", false);
                TranslationUtils.sendMessage(commandSender, "Tutorials:&b https://a5h73y.github.io/Parkour/", false);
                TranslationUtils.sendMessage(commandSender, "Discord Server:&b https://discord.gg/Gc8RGYr", false);
                break;

            case "yes":
            case "no":
                if (!parkour.getQuestionManager().hasBeenAskedQuestion(commandSender)) {
                    TranslationUtils.sendTranslation("Error.NoQuestion", commandSender);
                } else {
                    parkour.getQuestionManager().answerQuestion(commandSender, args[0]);
                }
                break;

            case "backup":
                PluginBackupUtil.backupNow(true);
                break;

            case "setpropertyforeverysingleplayer":
                if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
                    return false;
                }

                setPropertyForEverySinglePlayerConfig(args[1], args.length == 3 ? args[2] : null);
                break;

            case "setpropertyforeverysinglecourse":
                if (!ValidationUtils.validateArgs(commandSender, args, 2, 3)) {
                    return false;
                }

                setPropertyForEverySingleCourseConfig(args[1], args.length == 3 ? args[2] : null);
                break;

            default:
                TranslationUtils.sendMessage(commandSender, "Unknown Command. Enter 'pac cmds' to display all console commands.");
                break;
        }
        return true;
    }

    private void setPropertyForEverySinglePlayerConfig(String property, String value) {
        parkour.getConfigManager().getAllPlayerUuids()
                .forEach(uuid -> {
                    OfflinePlayer player = PlayerUtils.findPlayer(uuid);
                    PlayerConfig config = parkour.getConfigManager().getPlayerConfig(player);
                    if (value == null || value.trim().isEmpty()) {
                        config.remove(property);
                    } else {
                        config.set(property, value);
                    }
                });
    }

    private void setPropertyForEverySingleCourseConfig(String property, String value) {
        parkour.getConfigManager().getAllCourseNames()
                .forEach(courseName -> {
                    CourseConfig config = parkour.getConfigManager().getCourseConfig(courseName);
                    if (value == null || value.trim().isEmpty()) {
                        config.remove(property);
                    } else {
                        config.set(property, value);
                    }
                });
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

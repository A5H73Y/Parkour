package io.github.a5h73y.parkour.commands;

import static io.github.a5h73y.parkour.other.ParkourConstants.COMMAND_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_INVALID_AMOUNT;

import com.google.gson.GsonBuilder;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.gui.GuiMenu;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Player related Parkour commands handling.
 */
public class ParkourCommands extends AbstractPluginReceiver implements CommandExecutor {

    private static final String BASIC_COMMANDS = "1";
    private static final String CREATE_COMMANDS = "2";
    private static final String COURSE_COMMANDS = "3";
    private static final String PLAYER_COMMANDS = "4";
    private static final String ADMIN_COMMANDS = "5";

    private final Map<String, CommandUsage> commandUsages = new HashMap<>();

    public ParkourCommands(final Parkour parkour) {
        super(parkour);
        populateCommandUsages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String... args) {
        if (!(sender instanceof Player)) {
            TranslationUtils.sendMessage(sender, "'/parkour' is only available in game.");
            TranslationUtils.sendMessage(sender, "Use '/pac' for console commands.");
            return false;
        }

        Player player = (Player) sender;

        if (parkour.getParkourConfig().isPermissionsForCommands()
                && !PermissionUtils.hasPermission(player, Permission.BASIC_COMMANDS)) {
            return false;
        }

        if (args.length == 0) {
            TranslationUtils.sendMessage(player, "Plugin proudly created by &bA5H73Y &f& &bsteve4744");
            TranslationUtils.sendTranslation("Help.Commands", player);
            return true;
        }

        String combinedLabels = String.join(" ", args).toLowerCase();

        if (parkour.getParkourConfig().getBoolean("DisableCommand." + combinedLabels)
                && !PermissionUtils.hasPermission(player, Permission.PARKOUR_ALL, false)) {
            TranslationUtils.sendTranslation("Error.DisabledCommand", player);
            return false;
        }

        String commandLabel = args[0].toLowerCase();
        switch (commandLabel) {
            case "addjoinitem":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 1, 6)) {
                    return false;
                }

                parkour.getCourseSettingsManager().addJoinItem(player, args);
                break;

            case "admin":
            case "administrator":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3, 99)) {
                    return false;
                }

                parkour.getAdministrationManager().processAdminCommand(player, args[1], args[2], args);
                break;

            case "cache":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                parkour.getAdministrationManager().processCacheCommand(player, args.length == 2 ? args[1] : null);
                break;

            case "challenge":
                if (!ValidationUtils.validateArgs(player, args, 2, 10)) {
                    return false;
                }

                parkour.getChallengeManager().processCommand(player, args);
                break;

            case "checkpoint":
                if (!ValidationUtils.validateArgs(player, args, 2, 3)) {
                    return false;

                } else if (!PermissionUtils.hasPermissionOrCourseOwnership(player, Permission.ADMIN_COURSE, args[1])) {
                    return false;

                } else if (args.length == 3 && !ValidationUtils.isPositiveInteger(args[2])) {
                    TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
                    return false;
                }

                parkour.getCheckpointManager().createCheckpoint(player,
                        args[1], args.length == 3 ? Integer.parseInt(args[2]) : null);
                break;

            case "cmds":
                processListCommands(player, args);
                break;

            case "config":
                if (!PermissionUtils.hasPermission(player, Permission.PARKOUR_ALL)) {
                    return false;
                }

                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                if (!args[1].startsWith("MySQL")) {
                    TranslationUtils.sendValue(sender, args[1], parkour.getParkourConfig().getString(args[1]));
                }
                break;

            case "course":
            case "stats":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                CourseConfig.displayCourseInfo(player, args[1]);
                break;

            case "create":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_CREATE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 5)) {
                    return false;
                }

                parkour.getAdministrationManager().processCreateCommand(player,
                        args[1],
                        args.length > 2 ? args[2] : null,
                        args.length > 3 ? args[3] : null);
                break;

            case "delete":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_DELETE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3, 4)) {
                    return false;
                }

                parkour.getAdministrationManager().processDeleteCommand(
                        player, args[1], args[2], args.length > 3 ? args[3] : null);
                break;

            case "econ":
            case "economy":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 5)) {
                    return false;
                }

                parkour.getEconomyApi().processCommand(player, args);
                break;

            case "help":
                displayCommandHelp(player, args);
                break;

            case "info":
            case "player":
                parkour.getPlayerManager().displayParkourInfo(player,
                        args.length <= 1 ? (OfflinePlayer) sender : Bukkit.getOfflinePlayer(args[1]));
                break;

            case "invite":
                if (!ValidationUtils.validateArgs(player, args, 2, 10)) {
                    return false;
                }

                parkour.getChallengeManager().processCommand(player, "", commandLabel,
                        StringUtils.extractMessageFromArgs(args, 1));
                break;

            case "join":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
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

                parkour.getParkourKitManager().giveParkourKit(player, args.length == 2 ? args[1] : DEFAULT);
                break;

            case "leaderboard":
            case "leaderboards":
                if (!PermissionUtils.hasPermission(player, Permission.BASIC_LEADERBOARD)) {
                    return false;
                }

                parkour.getCourseManager().displayLeaderboards(player, args);
                break;

            case "list":
                parkour.getAdministrationManager().processListCommand(player, args);
                break;

            case "lobby":
                parkour.getLobbyManager().joinLobby(player, args.length > 1 ? args[1] : DEFAULT);
                break;

            case "material":
                MaterialUtils.lookupMaterialInformation(player, args.length > 1 ? args[1] : null);
                break;

            case "parkourkit":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 100)) {
                    return false;
                }

                parkour.getParkourKitManager().processParkourKitCommand(player, args[1],
                        args.length > 2 ? args[2] : null,
                        args.length > 3 ? args[3] : null);
                break;

            case "perms":
            case "permissions":
                parkour.getPlayerManager().displayPermissions(player);
                break;

            case "placeholder":
            case "parse":
                if (!ValidationUtils.validateArgs(player, args, 2, 3)) {
                    return false;
                }

                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                parkour.getPlaceholderApi().evaluatePlaceholder(player, args[1]);
                break;

            case "quiet":
                parkour.getQuietModeManager().toggleQuietMode(player);
                break;

            case "recreate":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                TranslationUtils.sendMessage(player, "Recreating courses...");
                parkour.getDatabaseManager().recreateAllCourses(true);
                break;

            case "reload":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                parkour.getConfigManager().reloadConfigs();
                parkour.getAdministrationManager().clearAllCache();
                TranslationUtils.sendTranslation("Parkour.ConfigReloaded", player);
                PluginUtils.logToFile(player.getName() + " reloaded the Parkour config");
                break;

            case "reset":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_RESET)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3, 4)) {
                    return false;
                }

                parkour.getAdministrationManager().processResetCommand(player,
                        args[1], args[2], args.length == 4 ? args[3] : null);
                break;

            case "respawn":
            case "back":
            case "die":
                parkour.getPlayerManager().playerDie(player);
                break;

            case "rewardrank":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getParkourRankManager().setRewardParkourRank(player, args[1], args[2]);
                break;

            case "session":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                parkour.getParkourSessionManager().processCommand(player, args[1]);
                break;

            case "setcourse":
                if (!ValidationUtils.validateArgs(player, args, 2, 100)) {
                    return false;
                }

                parkour.getCourseSettingsManager().processCommand(player, args);
                break;

            case "setlobby":
                parkour.getLobbyManager().createLobby(player,
                        args.length > 1 ? args[1] : DEFAULT,
                        args.length > 2 ? args[2] : null);
                break;

            case "setlobbycommand":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                if (!ValidationUtils.validateArgs(player, args, 3, 100)) {
                    return false;
                }

                parkour.getLobbyManager().addLobbyCommand(player, args[1], StringUtils.extractMessageFromArgs(args, 2));
                break;

            case "setplayer":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;

                } else if (!ValidationUtils.validateArgs(player, args, 2, 100)) {
                    return false;
                }

                parkour.getPlayerManager().processSetCommand(player, args);
                break;

            case "settings":
                if (!ValidationUtils.validateArgs(player, args, 2)) {
                    return false;
                }

                if (!PermissionUtils.hasPermissionOrCourseOwnership(player, Permission.ADMIN_COURSE, args[1])) {
                    return false;
                }

                parkour.getCourseManager().displaySettingsGui(player, args[1]);
                break;

            case "sql":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                    return false;
                }

                parkour.getDatabaseManager().displayInformation(player);
                break;

            case "test":
            case "testmode":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_TESTMODE)) {
                    return false;
                }

                parkour.getPlayerManager().toggleTestMode(player, args.length == 2 ? args[1].toLowerCase() : DEFAULT);
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

                } else if (!ValidationUtils.isPositiveInteger(args[2])) {
                    TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, sender);
                    return false;
                }

                parkour.getCheckpointManager().teleportCheckpoint(player, args[1], Integer.parseInt(args[2]));
                break;

            case "tutorial":
            case "request":
            case "bug":
                TranslationUtils.sendMessage(player, "To follow the official Parkour tutorials...");
                TranslationUtils.sendMessage(player, "Click here:&3 https://a5h73y.github.io/Parkour/", false);
                TranslationUtils.sendMessage(player, "To Request a feature or to Report a bug...");
                TranslationUtils.sendMessage(player, "Click here:&3 https://github.com/A5H73Y/Parkour/issues", false);
                break;

            case "support":
            case "contact":
            case "about":
            case "version":
                TranslationUtils.sendMessage(player, "Server is running Parkour &6" + parkour.getDescription().getVersion());
                TranslationUtils.sendMessage(player, "Plugin proudly created by &bA5H73Y &f& &bsteve4744", false);
                TranslationUtils.sendMessage(player, "Project Page:&b https://www.spigotmc.org/resources/parkour.23685/", false);
                TranslationUtils.sendMessage(player, "Tutorials:&b https://a5h73y.github.io/Parkour/", false);
                TranslationUtils.sendMessage(player, "Discord Server:&b https://discord.gg/Gc8RGYr", false);
                break;

            case "accept":
                parkour.getChallengeManager().acceptChallengeInvite(player);
                break;

            case "decline":
                parkour.getChallengeManager().declineChallenge(player);
                break;

            case "yes":
            case "no":
                if (!parkour.getQuestionManager().hasBeenAskedQuestion(player)) {
                    TranslationUtils.sendTranslation("Error.NoQuestion", player);
                } else {
                    parkour.getQuestionManager().answerQuestion(player, args[1]);
                }
                break;

            // player aliases
            case "setlevel":
            case "setleveladd":
            case "setrank":
                if (!ValidationUtils.validateArgs(player, args, 3)) {
                    return false;
                }

                parkour.getPlayerManager().processCommand(player, commandLabel.replace("set", ""), args[1], args[2]);
                break;

            // session aliases
            case "manualcheckpoint":
            case "hideall":
            case "leave":
            case "restart":
                this.onCommand(sender, command, label, "session", commandLabel);
                break;

            // parkourkit aliases
            case "listkit":
            case "linkkit":
            case "createkit":
            case "editkit":
            case "validatekit":
                parkour.getParkourKitManager().processParkourKitCommand(player,
                        commandLabel.replace("kit", ""),
                        args.length > 1 ? args[1] : null,
                        args.length > 2 ? args[2] : null);
                break;

            // setcourse aliases
            case "rewarddelay":
            case "rewardlevel":
            case "rewardleveladd":
            case "rewardparkoins":
            case "setplayerlimit":
            case "setstart":
            case "resumable":
            case "rewardonce":
            case "ready":
            case "setautostart":
            case "setcreator":
            case "setmaxdeaths":
            case "setmaxtime":
            case "setminimumlevel":
            case "maxfallticks":
            case "challengeonly":
            case "prize":
            case "setprize":
            case "setparkourmode":
                if (!PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE)) {
                    return false;
                }

                if (!ValidationUtils.validateArgs(player, args, 2, 3)) {
                    return false;
                }

                parkour.getCourseSettingsManager().performAction(player, args[1],
                        commandLabel.replace("set", ""), args.length > 2 ? args[2] : null);
                break;

            default:
                TranslationUtils.sendTranslation("Error.UnknownCommand", player);
                TranslationUtils.sendTranslation("Help.Commands", player);
                break;
        }
        return true;
    }

    /**
     * Lookup helpful information for Command.
     *
     * @param commandSender command sender
     * @param args arguments
     */
    public void displayCommandHelp(CommandSender commandSender, String... args) {
        if (args.length == 1) {
            TranslationUtils.sendValueTranslation("Help.Command", "(command)", commandSender);
            return;
        }

        CommandUsage commandUsage = getCommandUsage(args[1].toLowerCase());

        if (commandUsage != null) {
            commandUsage.displayHelpInformation(commandSender);

        } else {
            TranslationUtils.sendMessage(commandSender, "Unrecognised Parkour command.");
            TranslationUtils.sendTranslation("Help.Commands", commandSender);
        }
    }

    /**
     * Process List Command input.
     * Each of the valid commands will be processed based on input.
     *
     * @param player player
     * @param args command arguments
     */
    public void processListCommands(Player player, String... args) {
        if (args.length == 1) {
            displayParkourCommandsMenu(player);
            return;
        }

        switch (args[1].toLowerCase()) {
            case BASIC_COMMANDS:
            case "basic":
                displayGroupCommands(player, "Basic Commands", BASIC_COMMANDS);
                break;

            case CREATE_COMMANDS:
            case "create":
                displayGroupCommands(player, "Create Commands", CREATE_COMMANDS);
                break;

            case COURSE_COMMANDS:
            case "configure":
                displayGroupCommands(player, "Configuration Commands", COURSE_COMMANDS);
                break;

            case PLAYER_COMMANDS:
            case "player":
                displayGroupCommands(player, "Player Commands", PLAYER_COMMANDS);
                break;

            case ADMIN_COMMANDS:
            case "admin":
                displayGroupCommands(player, "Admin Commands", ADMIN_COMMANDS);
                break;

            case "signs":
                displaySignCommands(player);
                break;

            default:
                TranslationUtils.sendMessage(player, "Invalid page!");
                displayParkourCommandsMenu(player);
                break;
        }
    }

    /**
     * Display all the matching Commands for the group type.
     * @param commandSender command sender
     * @param commandGroup command group key
     */
    public void displayCommands(CommandSender commandSender, String commandGroup) {
        commandUsages.values().stream()
                .filter(commandUsage -> commandGroup.equals(commandUsage.getCommandGroup()))
                .sorted(Comparator.comparing(CommandUsage::getCommand))
                .forEach(commandUsage -> commandUsage.displayCommandUsage(commandSender));
    }

    /**
     * Display all the console commands.
     * @param commandSender command sender
     */
    public void displayConsoleCommands(CommandSender commandSender) {
        commandUsages.values().stream()
                .filter(commandUsage -> commandUsage.getConsoleSyntax() != null)
                .forEach(commandUsage -> commandSender.sendMessage(commandUsage.getConsoleSyntax()));
    }

    @Nullable
    public CommandUsage getCommandUsage(@NotNull String command) {
        return commandUsages.get(command.toLowerCase());
    }

    /**
     * Display command invalid syntax.
     * The correct syntax will be displayed to the User.
     *
     * @param commandSender command sender
     * @param command requested command
     */
    public void sendInvalidSyntax(CommandSender commandSender, String command) {
        CommandUsage commandUsage = getCommandUsage(command);

        if (commandUsage != null) {
            String arguments = commandUsage.getArguments() != null ? " " + commandUsage.getArguments() : "";
            TranslationUtils.sendInvalidSyntax(commandSender, command, arguments);
        }
    }

    /**
     * Get all Parkour command usages.
     * @return command usages
     */
    public Collection<CommandUsage> getCommandUsages() {
        return commandUsages.values();
    }

    /**
     * Display the Parkour Commands Menu.
     * @param player player
     */
    private void displayParkourCommandsMenu(Player player) {
        TranslationUtils.sendHeading("Parkour Commands Menu", player);

        TranslationUtils.sendMessage(player, "Please choose the desired command type:", false);
        TranslationUtils.sendMessage(player, " 1 &8: &7Basics", false);
        TranslationUtils.sendMessage(player, " 2 &8: &7Creating a Course", false);
        TranslationUtils.sendMessage(player, " 3 &8: &7Configuring a Course", false);
        TranslationUtils.sendMessage(player, " 4 &8: &7Player commands", false);
        TranslationUtils.sendMessage(player, " 5 &8: &7Administration", false);
        TranslationUtils.sendMessage(player, " signs &8: &7Sign Commands", false);

        player.sendMessage("");
        TranslationUtils.sendValueTranslation("Help.CommandSyntax", "cmds 1", false, player);
        TranslationUtils.sendMessage(player, "&8Remember: &b() &7means required, &b[] &7means optional.", false);
    }

    /**
     * Display all the available Parkour Sign Commands.
     * @param player player
     */
    private void displaySignCommands(Player player) {
        TranslationUtils.sendHeading("Parkour Sign Commands", player);

        TranslationUtils.sendMessage(player, "&3[pa]");
        displaySignCommandUsage(player, "Join", "(j)", "Join sign for a Parkour course");
        displaySignCommandUsage(player, "Checkpoint", "(c)", "Checkpoint for course");
        displaySignCommandUsage(player, "Finish", "(f)", "Finish sign for a Parkour course");
        displaySignCommandUsage(player, "Lobby", "(l)", "Teleport to Parkour lobby");
        displaySignCommandUsage(player, "Leave", "(le)", "Leave the current course");
        displaySignCommandUsage(player, "Effect", "(e)", "Apply a Parkour effect");
        displaySignCommandUsage(player, "Stats", "(s)", "Display course stats");
        displaySignCommandUsage(player, "Leaderboards", "(lb)", "Display course leaderboards");
        TranslationUtils.sendMessage(player, "&e() = shortcuts");
    }

    /**
     * Display the Parkour sign Command usage.
     * @param player player
     * @param command command
     * @param shortcut command shortcut
     * @param description command description
     */
    private void displaySignCommandUsage(Player player, String command, String shortcut, String description) {
        player.sendMessage(TranslationUtils.getTranslation("Help.SignUsage", false)
                .replace(COMMAND_PLACEHOLDER, command)
                .replace("%SHORTCUT%", shortcut)
                .replace("%DESCRIPTION%", description));
    }

    private void displayGroupCommands(Player player, String heading, String group) {
        TranslationUtils.sendHeading(heading, player);
        displayCommands(player, group);
    }

    private void populateCommandUsages() {
        String json = new BufferedReader(new InputStreamReader(
                parkour.getResource("parkourCommands.json"), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        List<CommandUsage> commandUsageContents = Arrays.asList(new GsonBuilder().create().fromJson(json, CommandUsage[].class));
        boolean includeDeprecated = parkour.getParkourConfig().getBoolean("Other.Display.IncludeDeprecatedCommands");
        commandUsageContents.forEach(commandUsage -> {
            if (includeDeprecated || commandUsage.getDeprecated() == null) {
                commandUsages.put(commandUsage.getCommand(), commandUsage);
            }
        });
    }
}

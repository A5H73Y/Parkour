package io.github.a5h73y.parkour.commands;

import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;

import com.google.common.reflect.ClassPath;
import com.google.gson.GsonBuilder;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AbstractParkourCommand;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand.AllowedSender;
import io.github.a5h73y.parkour.commands.type.SynonymParkourCommand;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.PluginBackupUtil;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private static final String CONFIG_COMMANDS = "3";
    private static final String ADMIN_COMMANDS = "4";

    private final Map<String, AbstractParkourCommand> parkourActionCommands = new HashMap<>();
    private final Map<String, CommandUsage> commandUsages = new HashMap<>();

    public ParkourCommands(final Parkour parkour) {
        super(parkour);
        populateParkourActionCommands();
        populateCommandUsages();
    }

    //TODO remove me
    @Deprecated
    public Map<String, AbstractParkourCommand> getParkourActionCommands() {
        return parkourActionCommands;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String... args) {

        if (parkour.getParkourConfig().isPermissionsForCommands()
                && !PermissionUtils.hasPermission(commandSender, Permission.BASIC_COMMANDS)) {
            return false;
        }

        if (args.length == 0) {
            TranslationUtils.sendMessage(commandSender, "Plugin proudly created by &bA5H73Y &f& &bsteve4744");
            TranslationUtils.sendTranslation("Help.Commands", commandSender);
            return false;
        }

        if (!parkourActionCommands.containsKey(args[0].toLowerCase())) {
            TranslationUtils.sendTranslation("Error.UnknownCommand", commandSender);
            TranslationUtils.sendTranslation("Help.Commands", commandSender);
            return false;
        }

        parkourActionCommands.get(args[0].toLowerCase()).executeCommand(commandSender, args);
        return true;
    }

    private void populateCommandUsages() {
        String json = new BufferedReader(new InputStreamReader(parkour.getResource("parkourCommands.json"), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        List<CommandUsage> commandUsageContents = Arrays.asList(new GsonBuilder().create().fromJson(json, CommandUsage[].class));
        commandUsageContents.forEach(commandUsage -> {
            // TODO REMOVE ME
            if (!parkourActionCommands.containsKey(commandUsage.getCommand())) {
                System.out.println("uh oh - " + commandUsage.getCommand() + " action was missing ");
            }
            commandUsages.put(commandUsage.getCommand(), commandUsage);
        });

        // TODO remove me
        commandUsageContents.stream().sorted(Comparator.comparing(CommandUsage::getCommand)).forEach(commandUsage -> System.out.println(commandUsage.getCommand()));
    }

    private void populateParkourActionCommands() {
        try {
            final ClassPath path = ClassPath.from(parkour.getClass().getClassLoader());
            for (final ClassPath.ClassInfo info : path.getTopLevelClasses(this.getClass().getPackage().getName() + ".command")) {
                final Class<?> clazz = info.load();
                Constructor<?> constructor = clazz.getConstructor(Parkour.class);
                BasicParkourCommand instance = (BasicParkourCommand) constructor.newInstance(parkour);
                Arrays.stream(instance.getCommandLabels()).forEach(commandLabel ->
                        parkourActionCommands.put(commandLabel, instance));
            }
        } catch (Exception e) {
            PluginUtils.log(e.getMessage(), 2);
            e.printStackTrace();
        }

        addAnySenderCommands();
        addPlayerCommands();
        addConsoleCommands();
    }

    private void addAnySenderCommands() {
        createActionCommand(AllowedSender.ANY,
                Permission.ADMIN_COURSE,
                (commandSender, args) -> parkour.getCourseSettingsManager().addJoinItem(commandSender, args),
                "addjoinitem");

        createActionCommand(AllowedSender.ANY,
                Permission.ADMIN_ALL,
                (commandSender, args) -> PluginUtils.processCacheCommand(commandSender, args.length == 2 ? args[1] : null),
                "cache");

        createActionCommand(AllowedSender.ANY,
                (commandSender, args) -> parkour.getCourseManager().displayList(commandSender, args),
                "list");

        createActionCommand(AllowedSender.ANY,
                (commandSender, args) -> parkour.getParkourKitManager().processParkourKitCommand(commandSender, args),
                "parkourkit");

        createActionCommand(AllowedSender.ANY,
                (commandSender, args) -> parkour.getParkourCommands().displayCommandHelp(commandSender, args),
                "help");

        createActionCommand(AllowedSender.ANY,
                Permission.ADMIN_ALL,
                (commandSender, args) -> parkour.getDatabaseManager().displayInformation(commandSender),
                "sql");
    }

    private void addPlayerCommands() {
        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> parkour.getPlayerManager().leaveCourse((Player) commandSender),
                "leave");

        createActionCommand(AllowedSender.PLAYER,
                Permission.BASIC_KIT,
                (commandSender, args) -> parkour.getParkourKitManager().giveParkourKit((Player) commandSender, args.length == 2 ? args[1] : DEFAULT),
                "kit");

        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> parkour.getLobbyManager().joinLobby((Player) commandSender, args.length > 1 ? args[1] : DEFAULT),
                "lobby");

        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> parkour.getPlayerManager().setManualCheckpoint((Player) commandSender),
                "manualcheckpoint");

        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> MaterialUtils.lookupMaterialInformation((Player) commandSender, args),
                "material");

        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> parkour.getPlayerManager().displayPermissions((Player) commandSender),
                "perms", "permissions");

        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> parkour.getQuietModeManager().toggleQuietMode((Player) commandSender),
                "quiet");

        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> parkour.getPlayerManager().playerDie((Player) commandSender),
                "die", "back", "respawn");

        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> parkour.getPlayerManager().restartCourse((Player) commandSender),
                "restart");

        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> parkour.getChallengeManager().acceptChallengeInvite((Player) commandSender),
                "accept");

        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> parkour.getChallengeManager().declineChallenge((Player) commandSender),
                "decline");

        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> TranslationUtils.sendTranslation("Error.NoQuestion", commandSender),
                "yes", "no");

        createActionCommand(AllowedSender.PLAYER,
                (commandSender, args) -> parkour.getPlayerManager().deselectCourse((Player) commandSender),
                "deselect", "done");

        createActionCommand(AllowedSender.PLAYER,
                Permission.ADMIN_TESTMODE,
                (commandSender, args) -> parkour.getPlayerManager().toggleTestMode((Player) commandSender, args.length == 2 ? args[1].toLowerCase() : DEFAULT),
                "test", "testmode");

        createActionCommand(AllowedSender.PLAYER,
                Permission.ADMIN_ALL,
                (commandSender, args) -> parkour.getLobbyManager().createLobby((Player) commandSender,
                        args.length > 1 ? args[1] : DEFAULT, args.length == 3 ? args[2] : null),
                "setlobby");

        createActionSynonym("tp", 2, args -> "course teleport " + args[1]);
        createActionSynonym("tpc", 2, args -> "course teleport " + args[1] + " " + args[2]);
    }

    private void addConsoleCommands() {
        createActionCommand(AllowedSender.CONSOLE,
                (commandSender, args) -> PluginBackupUtil.backupNow(true),
                "backup");
    }

    private void createActionCommand(AllowedSender sender,
                                     BiConsumer<CommandSender, String[]> action,
                                     String... commandLabels) {
        createActionCommand(sender, null, action, commandLabels);
    }

    private void createActionCommand(AllowedSender sender,
                                     @Nullable Permission permission,
                                     BiConsumer<CommandSender, String[]> action,
                                     String... commandLabels) {
        BasicParkourCommand actionCommand = new BasicParkourCommand(parkour, sender, commandLabels) {
            @Override
            public void performAction(CommandSender commandSender, String[] args) {
                action.accept(commandSender, args);
            }

            @Override
            protected Permission getRequiredPermission() {
                return permission;
            }
        };

        for (String commandLabel : actionCommand.getCommandLabels()) {
            parkourActionCommands.put(commandLabel, actionCommand);
        }
    }

    private void createActionSynonym(String commandLabel,
                                     int minimumArgs,
                                     Function<String[], String> action) {

        parkourActionCommands.put(commandLabel, new SynonymParkourCommand(parkour, minimumArgs) {
            @Override
            public void performAction(CommandSender commandSender, String[] args) {
                String[] newArgs = action.apply(args).split(" ");
                parkourActionCommands.get(newArgs[0].toLowerCase()).executeCommand(commandSender, newArgs);
            }
        });
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

        // TODO FIX THE GROUPING
        // TODO WHY IS 'setplayer' in CONFIG_COMMANDS?

        switch (args[1].toLowerCase()) {
            case BASIC_COMMANDS:
            case "basic":
                displayBasicCommands(player);
                break;

            case CREATE_COMMANDS:
            case "create":
                displayCreatingCommands(player);
                break;

            case CONFIG_COMMANDS:
            case "configure":
                displayConfigureCommands(player);
                break;

            case ADMIN_COMMANDS:
            case "admin":
                displayAdminCommands(player);
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
     * Display the Parkour Commands Menu.
     * @param player player
     */
    private void displayParkourCommandsMenu(Player player) {
        TranslationUtils.sendHeading("Parkour Commands Menu", player);

        TranslationUtils.sendMessage(player, "Please choose the desired command type:", false);
        TranslationUtils.sendMessage(player, " 1 &8: &7Basics", false);
        TranslationUtils.sendMessage(player, " 2 &8: &7Creating a Course", false);
        TranslationUtils.sendMessage(player, " 3 &8: &7Configuring a Course", false);
        TranslationUtils.sendMessage(player, " 4 &8: &7Administration", false);
        TranslationUtils.sendMessage(player, " signs &8: &7Sign Commands", false);

        player.sendMessage("");
        TranslationUtils.sendValueTranslation("Help.CommandSyntax", "cmds 1", false, player);
        TranslationUtils.sendMessage(player, "&8Remember: &b() &7means required, &b[] &7means optional.", false);
    }

    /**
     * Display all the matching Commands for the group type.
     * @param commandSender command sender
     * @param commandGroup command group key
     */
    private void displayCommands(CommandSender commandSender, String commandGroup) {
        commandUsages.values().stream()
                .filter(commandUsage -> commandGroup.equals(commandUsage.getCommandGroup()))
                .sorted(Comparator.comparing(CommandUsage::getCommand))
                .forEach(commandUsage -> commandUsage.displayCommandUsage(commandSender));
    }

    public void displayConsoleCommands(CommandSender commandSender) {
        commandUsages.values().stream()
                .filter(commandUsage -> commandUsage.getConsoleSyntax() != null)
                .forEach(commandUsage -> commandSender.sendMessage(commandUsage.getConsoleSyntax()));
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
                .replace("%COMMAND%", command)
                .replace("%SHORTCUT%", shortcut)
                .replace("%DESCRIPTION%", description));
    }

    private void displayBasicCommands(Player player) {
        TranslationUtils.sendHeading("Basic Commands", player);
        displayCommands(player, BASIC_COMMANDS);
    }

    private void displayCreatingCommands(Player player) {
        TranslationUtils.sendHeading("Create Commands", player);
        displayCommands(player, CREATE_COMMANDS);
    }

    private void displayConfigureCommands(Player player) {
        TranslationUtils.sendHeading("Configuration Commands", player);
        displayCommands(player, CONFIG_COMMANDS);
    }

    private void displayAdminCommands(Player player) {
        TranslationUtils.sendHeading("Admin Commands", player);
        displayCommands(player, ADMIN_COMMANDS);
    }

    @Nullable
    public CommandUsage getCommandUsage(@NotNull String command) {
        return commandUsages.get(command.toLowerCase());
    }

    public void sendInvalidSyntax(CommandSender commandSender, String command) {
        CommandUsage commandUsage = getCommandUsage(command);

        if (commandUsage != null) {
            TranslationUtils.sendInvalidSyntax(commandSender, command, commandUsage.getArguments());
        }
    }

    public Collection<CommandUsage> getCommandUsages() {
        return commandUsages.values();
    }
}

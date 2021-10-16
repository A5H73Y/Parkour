package io.github.a5h73y.parkour.commands;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Parkour command help lookup.
 */
public final class ParkourCommandHelp {

    private static final String BASIC_COMMANDS = "1";
    private static final String CREATE_COMMANDS = "2";
    private static final String CONFIG_COMMANDS = "3";
    private static final String ADMIN_COMMANDS = "4";

    // Check the actual descriptions, update to meet new updates / standards.

    /**
     * Lookup helpful information for Command.
     *
     * @param sender command sender
     * @param args arguments
     */
    public static void displayCommandHelp(CommandSender sender, String... args) {
        if (args.length == 1) {
            TranslationUtils.sendValueTranslation("Help.Command", "(command)", sender);
            return;
        }

        String command = args[1].toLowerCase();

        Optional<CommandUsage> matching = Parkour.getInstance().getCommandUsages().stream()
                .filter(commandUsage -> commandUsage.getCommand().equals(command))
                .findAny();

        if (matching.isPresent()) {
            matching.get().displayHelpInformation(sender);

        } else {
            TranslationUtils.sendMessage(sender, "Unrecognised Parkour command.");
            TranslationUtils.sendTranslation("Help.Commands", sender);
        }
    }

    /**
     * Process Help Command input.
     * Each of the valid commands will be processed based on input.
     *
     * @param player player
     * @param args command arguments
     */
    public static void processCommand(Player player, String... args) {
        if (args.length == 1) {
            displayParkourCommandsMenu(player);
            return;
        }

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
    private static void displayParkourCommandsMenu(Player player) {
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
     * @param player player
     * @param commandGroup command group key
     */
    private static void displayCommands(Player player, String commandGroup) {
        Parkour.getInstance().getCommandUsages().stream()
                .filter(commandUsage -> commandGroup.equals(commandUsage.getCommandGroup()))
                .forEach(commandUsage -> commandUsage.displayCommandUsage(player));
    }

    /**
     * Display all the available Parkour Sign Commands.
     * @param player player
     */
    private static void displaySignCommands(Player player) {
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
    private static void displaySignCommandUsage(Player player, String command, String shortcut, String description) {
        player.sendMessage(TranslationUtils.getTranslation("Help.SignUsage", false)
                .replace("%COMMAND%", command)
                .replace("%SHORTCUT%", shortcut)
                .replace("%DESCRIPTION%", description));
    }

    private static void displayBasicCommands(Player player) {
        TranslationUtils.sendHeading("Basic Commands", player);
        displayCommands(player, BASIC_COMMANDS);
    }

    private static void displayCreatingCommands(Player player) {
        TranslationUtils.sendHeading("Create Commands", player);
        displayCommands(player, CREATE_COMMANDS);
    }

    private static void displayConfigureCommands(Player player) {
        TranslationUtils.sendHeading("Configuration Commands", player);
        displayCommands(player, CONFIG_COMMANDS);
    }

    private static void displayAdminCommands(Player player) {
        TranslationUtils.sendHeading("Admin Commands", player);
        displayCommands(player, ADMIN_COMMANDS);
    }

    private ParkourCommandHelp() {}
}

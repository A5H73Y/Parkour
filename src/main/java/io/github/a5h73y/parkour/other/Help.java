package io.github.a5h73y.parkour.other;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class Help {

    // Check that console commands match syntax
    // Update group number based on help command
    // Check that the formatting is consistent (ends with a period, 'Course')
    // Check the actual descriptions, update to meet new updates / standards.
    // Change console commands to pac
    // Add aliases?

    /**
     * Lookup and display the syntax and description for each Parkour command.
     *
     * @param args
     * @param sender
     */
    public static void lookupCommandHelp(String[] args, CommandSender sender) {
        if (args.length == 1) {
            sender.sendMessage(Parkour.getPrefix() + "Find helpful information about any Parkour command:");
            sender.sendMessage("             /pa help " + ChatColor.AQUA + "(command)");
            return;
        }

        String command = args[1].toLowerCase();

        Optional<CommandUsage> matching = Parkour.getInstance().getCommandUsages().stream()
                .filter(commandUsage -> commandUsage.getCommand().equals(command))
                .findAny();

        if (matching.isPresent()) {
            matching.get().displayHelpInformation(sender);

        } else {
            sender.sendMessage(Parkour.getPrefix() + "Unrecognised command. Please find all available commands using '/pa cmds'");
        }
    }

    /**
     * Display the sign command usage
     *
     * @param player
     * @param title
     * @param shortcut
     * @param description
     */
    private static void displaySignCommandUsage(Player player, String title, String shortcut, String description) {
        player.sendMessage(ChatColor.AQUA + title + ChatColor.YELLOW + " " + shortcut + ChatColor.BLACK + " : " + ChatColor.WHITE + description);
    }

    /**
     * Display relevant command pages
     * If signs is specified, will display the available sign commands.
     * If no page is specified, will display the commands menu.
     *
     * @param args
     * @param player
     */
    public static void processCommandsInput(String[] args, Player player) {
        if (args.length == 1) {
            displayCommandsIndex(player);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "1":
            case "basic":
                displayBasicCommands(player);
                break;

            case "2":
            case "create":
                displayCreatingCommands(player);
                break;

            case "3":
            case "configure":
                displayConfigureCommands(player);
                break;

            case "4":
            case "admin":
                displayAdminCommands(player);
                break;

            case "signs":
                displaySignCommands(player);
                break;

            default:
                player.sendMessage(Parkour.getPrefix() + "Invalid page!");
                displayCommandsIndex(player);
                break;
        }
    }

    /**
     * Display commands menu
     *
     * @param player
     */
    private static void displayCommandsIndex(Player player) {
        TranslationUtils.sendHeading("Parkour Commands Menu", player);

        player.sendMessage("Please choose the desired command type:");
        player.sendMessage(" 1" + ChatColor.DARK_GRAY + " : " + ChatColor.GRAY + "Basics");
        player.sendMessage(" 2" + ChatColor.DARK_GRAY + " : " + ChatColor.GRAY + "Creating a course");
        player.sendMessage(" 3" + ChatColor.DARK_GRAY + " : " + ChatColor.GRAY + "Configuring a course");
        player.sendMessage(" 4" + ChatColor.DARK_GRAY + " : " + ChatColor.GRAY + "Admin");
        player.sendMessage(" signs" + ChatColor.DARK_GRAY + " : " + ChatColor.GRAY + "Sign commands");
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_GRAY + "Example: " + ChatColor.GRAY + "/pa cmds 1");
        player.sendMessage(ChatColor.DARK_GRAY + "Remember: " + ChatColor.AQUA + "()" + ChatColor.GRAY + " means required, " + ChatColor.AQUA + "[]" + ChatColor.GRAY + " means optional.");
    }

    /**
     * Display the basic Parkour commands
     *
     * @param player
     */
    private static void displayBasicCommands(Player player) {
        TranslationUtils.sendHeading("Basic Commands", player);
        displayCommands(player, "1");
    }

    /**
     * Display the creating Parkour commands
     *
     * @param player
     */
    private static void displayCreatingCommands(Player player) {
        TranslationUtils.sendHeading("Create Commands", player);
        displayCommands(player, "2");
    }

    /**
     * Display the configure Parkour commands
     *
     * @param player
     */
    private static void displayConfigureCommands(Player player) {
        TranslationUtils.sendHeading("Configuration Commands", player);
        displayCommands(player, "3");
    }

    /**
     * Display the admin Parkour commands
     *
     * @param player
     */
    private static void displayAdminCommands(Player player) {
        TranslationUtils.sendHeading("Admin Commands", player);
        displayCommands(player, "4");
    }

    private static void displayCommands(Player player, String key) {
        Parkour.getInstance().getCommandUsages().stream()
                .filter(commandUsage -> commandUsage.getCommandGroup().equals(key))
                .forEach(commandUsage -> commandUsage.displayCommandUsage(player));
    }


    /**
     * Display the sign Parkour commands
     *
     * @param player
     */
    private static void displaySignCommands(Player player) {
        TranslationUtils.sendHeading("Parkour Sign Commands", player);

        player.sendMessage(ChatColor.DARK_AQUA + "[pa]");
        displaySignCommandUsage(player, "Join", "(j)", "Join sign for a Parkour course");
        displaySignCommandUsage(player, "Checkpoint", "(c)", "Checkpoint for course");
        displaySignCommandUsage(player, "Finish", "(f)", "Finish sign for a Parkour course");
        displaySignCommandUsage(player, "Lobby", "(l)", "Teleport to Parkour lobby");
        displaySignCommandUsage(player, "Leave", "(le)", "Leave the current course");
        displaySignCommandUsage(player, "Effect", "(e)", "Apply a Parkour effect");
        displaySignCommandUsage(player, "Stats", "(s)", "Display course stats");
        displaySignCommandUsage(player, "Leaderboards", "(lb)", "Display course leaderboards");

        player.sendMessage(ChatColor.YELLOW + "() = shortcuts");
    }

    /**
     * Display all relevant Parkour settings
     *
     * @param sender
     */
    public static void displaySettings(CommandSender sender) {
        TranslationUtils.sendHeading("Parkour Settings", sender);
        Parkour parkour = Parkour.getInstance();

        sender.sendMessage("Version: " + ChatColor.AQUA + parkour.getDescription().getVersion());
        sender.sendMessage("Economy: " + ChatColor.AQUA + parkour.getEconomyApi().isEnabled());
        sender.sendMessage("BountifulAPI: " + ChatColor.AQUA + parkour.getBountifulApi().isEnabled());
        sender.sendMessage("PlaceholderAPI: " + ChatColor.AQUA + parkour.getPlaceholderApi().isEnabled());
        sender.sendMessage("Disable Commands: " + ChatColor.AQUA + Parkour.getDefaultConfig().isDisableCommandsOnCourse());
        sender.sendMessage("Enforce world: " + ChatColor.AQUA + Parkour.getDefaultConfig().isJoinEnforceWorld());
        sender.sendMessage("Less checks: " + ChatColor.AQUA + Parkour.getDefaultConfig().isAttemptLessChecks());
    }
}

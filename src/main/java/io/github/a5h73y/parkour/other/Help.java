package io.github.a5h73y.parkour.other;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.gui.impl.CourseSettingsGui;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public final class Help {

    private static final String BASIC_COMMANDS = "1";
    private static final String CREATE_COMMANDS = "2";
    private static final String CONFIG_COMMANDS = "3";
    private static final String ADMIN_COMMANDS = "4";

    // Check that the formatting is consistent (ends with a period, 'Course')
    // Check the actual descriptions, update to meet new updates / standards.
    // Add aliases?

    /**
     * Lookup and display the syntax and description for each Parkour command.
     *
     * @param args
     * @param sender
     */
    public static void lookupCommandHelp(CommandSender sender, String... args) {
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
            sender.sendMessage(Parkour.getPrefix() + "Unrecognised Parkour command.");
            TranslationUtils.sendTranslation("Help.Commands", sender);
        }
    }

    /**
     * Display the sign command usage
     *
     * @param player
     * @param command
     * @param shortcut
     * @param description
     */
    private static void displaySignCommandUsage(Player player, String command, String shortcut, String description) {
        player.sendMessage(TranslationUtils.getTranslation("Help.SignUsage", false)
                .replace("%COMMAND%", command)
                .replace("%SHORTCUT%", shortcut)
                .replace("%DESCRIPTION%", description));
    }

    /**
     * Display relevant command pages
     * If signs is specified, will display the available sign commands.
     * If no page is specified, will display the commands menu.
     *
     * @param args
     * @param player
     */
    public static void processCommandsInput(Player player, String... args) {
        if (args.length == 1) {
            displayCommandsIndex(player);
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
        player.sendMessage(TranslationUtils.getValueTranslation("Help.CommandSyntax", "cmds 1", false));
        player.sendMessage(ChatColor.DARK_GRAY + "Remember: " + ChatColor.AQUA + "()" + ChatColor.GRAY + " means required, " + ChatColor.AQUA + "[]" + ChatColor.GRAY + " means optional.");
    }

    /**
     * Display the basic Parkour commands
     *
     * @param player
     */
    private static void displayBasicCommands(Player player) {
        TranslationUtils.sendHeading("Basic Commands", player);
        displayCommands(player, BASIC_COMMANDS);
    }

    /**
     * Display the creating Parkour commands
     *
     * @param player
     */
    private static void displayCreatingCommands(Player player) {
        TranslationUtils.sendHeading("Create Commands", player);
        displayCommands(player, CREATE_COMMANDS);
    }

    /**
     * Display the configure Parkour commands
     *
     * @param player
     */
    private static void displayConfigureCommands(Player player) {
        TranslationUtils.sendHeading("Configuration Commands", player);
        displayCommands(player, CONFIG_COMMANDS);
    }

    /**
     * Display the admin Parkour commands
     *
     * @param player
     */
    private static void displayAdminCommands(Player player) {
        TranslationUtils.sendHeading("Admin Commands", player);
        displayCommands(player, ADMIN_COMMANDS);
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
    public static void displaySettings(CommandSender sender, @Nullable String courseName) {
        Parkour parkour = Parkour.getInstance();
        if (sender instanceof Player && courseName != null) {
            if (!parkour.getCourseManager().doesCourseExists(courseName)) {
                TranslationUtils.sendValueTranslation("Error.NoExist", courseName, sender);
                return;
            }

            parkour.getGuiManager().showMenu((Player) sender, new CourseSettingsGui(courseName));

        } else {
            TranslationUtils.sendHeading("Parkour Settings", sender);

            sender.sendMessage("Version: " + ChatColor.AQUA + parkour.getDescription().getVersion());
            sender.sendMessage("Economy: " + ChatColor.AQUA + parkour.getEconomyApi().isEnabled());
            sender.sendMessage("BountifulAPI: " + ChatColor.AQUA + parkour.getBountifulApi().isEnabled());
            sender.sendMessage("PlaceholderAPI: " + ChatColor.AQUA + parkour.getPlaceholderApi().isEnabled());
            sender.sendMessage("Disable Commands: " + ChatColor.AQUA + Parkour.getDefaultConfig().isDisableCommandsOnCourse());
            sender.sendMessage("Enforce world: " + ChatColor.AQUA + Parkour.getDefaultConfig().isJoinEnforceWorld());
            sender.sendMessage("Less checks: " + ChatColor.AQUA + Parkour.getDefaultConfig().isAttemptLessChecks());
        }
    }
}

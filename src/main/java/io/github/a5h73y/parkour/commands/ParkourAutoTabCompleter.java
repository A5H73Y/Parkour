package io.github.a5h73y.parkour.commands;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.SetCourseConversation;
import io.github.a5h73y.parkour.conversation.SetPlayerConversation;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.kit.ParkourKitInfo;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Tab auto-completion for Parkour commands.
 */
public class ParkourAutoTabCompleter extends AbstractPluginReceiver implements TabCompleter {

    private static final List<String> NO_PERMISSION_COMMANDS = Arrays.asList(
            "join", "info", "course", "lobby", "perms", "quiet", "list", "help", "material", "about", "contact", "cmds");

    private static final List<String> ADMIN_ONLY_COMMANDS = Arrays.asList(
            "setlobby", "reset", "economy", "recreate", "whitelist", "setlevel", "setrank", "settings",
            "sql", "cache", "reload");

    private static final List<String> ADMIN_COURSE_COMMANDS = Arrays.asList(
            "checkpoint", "ready", "setstart", "setcourse", "setautostart", "select", "done", "link", "linkkit",
            "addjoinitem", "rewardonce", "rewardlevel", "rewardleveladd", "rewardrank", "rewarddelay", "rewardparkoins",
            "setmode", "createkit", "editkit", "validatekit");

    private static final List<String> ON_COURSE_COMMANDS = Arrays.asList(
            "back", "leave");

    private static final List<String> QUESTION_ANSWER_COMMANDS = Arrays.asList(
            "yes", "no");

    private static final List<String> RESET_COMMANDS = Arrays.asList(
            "course", "player", "leaderboard", "prize");

    private static final List<String> DELETE_COMMANDS = Arrays.asList(
            "autostart", "checkpoint", "course", "lobby", "kit");

    private static final List<String> LIST_COMMANDS = Arrays.asList(
            "courses", "players", "ranks", "lobbies");

    public ParkourAutoTabCompleter(Parkour parkour) {
        super(parkour);
    }

    /**
     * List of commands will be built based on the configuration and player permissions.
     * {@inheritDoc}
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command cmd,
                                      @NotNull String alias,
                                      @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        final Player player = (Player) sender;
        List<String> allowedCommands = new ArrayList<>();
        List<String> filteredCommands = new ArrayList<>();

        if (args.length == 1) {
            allowedCommands = populateMainCommands(player);

        } else if (args.length == 2) {
            allowedCommands = populateFirstChildCommands(args[0]);

        } else if (args.length == 3) {
            allowedCommands = populateSecondChildCommands(args[0], args[1]);
        }

        for (String allowedCommand : allowedCommands) {
            if (allowedCommand.startsWith(args[args.length - 1])) {
                filteredCommands.add(allowedCommand);
            }
        }

        return filteredCommands.isEmpty() ? allowedCommands : filteredCommands;
    }

    private List<String> populateMainCommands(Player player) {
        // if they have an outstanding question, make those the only options
        if (parkour.getQuestionManager().hasPlayerBeenAskedQuestion(player)) {
            return QUESTION_ANSWER_COMMANDS;
        }

        List<String> allowedCommands = new ArrayList<>(NO_PERMISSION_COMMANDS);

        if (parkour.getPlayerManager().isPlaying(player)) {
            allowedCommands.addAll(ON_COURSE_COMMANDS);
        }
        // the player has an outstanding challenge request
        if (parkour.getChallengeManager().isPlayerInChallenge(player.getName())) {
            allowedCommands.add("accept");
            allowedCommands.add("decline");
        }

        // basic commands
        if (PermissionUtils.hasPermission(player, Permission.BASIC_JOINALL, false)) {
            allowedCommands.add("joinall");
        }
        if (PermissionUtils.hasPermission(player, Permission.BASIC_CREATE, false)) {
            allowedCommands.add("create");
        }
        if (PermissionUtils.hasPermission(player, Permission.BASIC_KIT, false)) {
            allowedCommands.add("kit");
            allowedCommands.add("listkit");
        }
        if (PermissionUtils.hasPermission(player, Permission.BASIC_CHALLENGE, false)) {
            allowedCommands.add("challenge");
        }
        if (PermissionUtils.hasPermission(player, Permission.BASIC_TELEPORT, false)) {
            allowedCommands.add("tp");
        }
        if (PermissionUtils.hasPermission(player, Permission.BASIC_TELEPORT_CHECKPOINT, false)) {
            allowedCommands.add("tpc");
        }
        if (PermissionUtils.hasPermission(player, Permission.BASIC_LEADERBOARD, false)) {
            allowedCommands.add("leaderboard");
        }

        // admin commands
        if (PermissionUtils.hasPermission(player, Permission.ADMIN_PRIZE, false)) {
            allowedCommands.add("prize");
        }
        if (PermissionUtils.hasPermission(player, Permission.ADMIN_DELETE, false)) {
            allowedCommands.add("delete");
        }
        if (PermissionUtils.hasPermission(player, Permission.ADMIN_TESTMODE, false)) {
            allowedCommands.add("test");
        }
        // they've selected a known course, or they have admin course permission
        if (PlayerInfo.hasSelectedValidCourse(player)
                || PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE, false)) {
            allowedCommands.addAll(ADMIN_COURSE_COMMANDS);
        }
        if (PermissionUtils.hasPermission(player, Permission.ADMIN_ALL, false)) {
            allowedCommands.addAll(ADMIN_ONLY_COMMANDS);
        }

        return allowedCommands;
    }

    private List<String> populateFirstChildCommands(String command) {
        List<String> allowedCommands = new ArrayList<>();

        switch (command.toLowerCase()) {
            case "reset":
                allowedCommands = RESET_COMMANDS;
                break;
            case "delete":
                allowedCommands = DELETE_COMMANDS;
                break;
            case "list":
                allowedCommands = LIST_COMMANDS;
                break;
            case "join":
            case "course":
            case "ready":
            case "setautostart":
            case "prize":
            case "select":
            case "tp":
            case "tpc":
            case "setcourse":
            case "addjoinitem":
            case "rewardonce":
            case "rewardlevel":
            case "rewardleveladd":
            case "rewarddelay":
            case "rewardparkoins":
            case "setmode":
            case "leaderboard":
                allowedCommands = CourseInfo.getAllCourses();
                break;
            case "kit":
            case "listkit":
            case "validatekit":
                allowedCommands = new ArrayList<>(ParkourKitInfo.getParkourKitNames());
                break;
            default:
                break;
        }

        return allowedCommands;
    }

    private List<String> populateSecondChildCommands(String command, String firstArg) {
        List<String> allowedCommands = new ArrayList<>();

        switch (command.toLowerCase()) {
            case "setcourse":
                allowedCommands = SetCourseConversation.SET_COURSE_OPTIONS;
                break;
            case "setplayer":
                allowedCommands = SetPlayerConversation.SET_PLAYER_OPTIONS;
                break;
            case "TODO": //TODO more things here
                break;
        }
        return allowedCommands;
    }
}

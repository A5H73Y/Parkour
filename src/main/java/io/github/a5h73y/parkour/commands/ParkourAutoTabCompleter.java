package io.github.a5h73y.parkour.commands;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.SetCourseConversation;
import io.github.a5h73y.parkour.conversation.SetPlayerConversation;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.kit.ParkourKitInfo;
import io.github.a5h73y.parkour.type.lobby.LobbyInfo;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
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
            "join", "info", "course", "lobby", "perms", "quiet", "list", "help", "material", "about",
            "contact", "cmds", "version", "challenge");

    private static final List<String> ADMIN_ONLY_COMMANDS = Arrays.asList(
            "setlobby", "reset", "economy", "recreate", "whitelist", "setlevel", "setplayer", "setrank", "settings",
            "sql", "cache", "reload");

    private static final List<String> ADMIN_COURSE_COMMANDS = Arrays.asList(
            "checkpoint", "ready", "setstart", "setcourse", "setautostart", "select", "deselect", "done", "link", "linkkit",
            "addjoinitem", "rewardonce", "rewardlevel", "rewardleveladd", "rewardrank", "rewarddelay", "rewardparkoins",
            "setmode", "createkit", "editkit", "validatekit", "setplayerlimit", "challengeonly", "resumable");

    private static final List<String> ON_COURSE_COMMANDS = Arrays.asList(
            "back", "leave", "respawn", "restart");

    private static final List<String> QUESTION_ANSWER_COMMANDS = Arrays.asList(
            "yes", "no");

    private static final List<String> RESET_COMMANDS = Arrays.asList(
            "course", "player", "leaderboard", "prize");

    private static final List<String> DELETE_COMMANDS = Arrays.asList(
            "autostart", "checkpoint", "course", "lobby", "kit");

    private static final List<String> LIST_COMMANDS = Arrays.asList(
            "courses", "players", "ranks", "lobbies");

    private static final List<String> ECONOMY_COMMANDS = Arrays.asList(
            "info", "recreate", "setprize", "setfee");

    private static final List<String> LINK_COMMANDS = Arrays.asList(
            "course", "lobby", "reset");

    private static final List<String> CACHE_COMMANDS = Arrays.asList(
            "course", "lobby", "database", "parkourkit", "sound", "clear");

    private static final List<String> COMMANDS_MENU = Arrays.asList(
            "1", "2", "3", "4", "signs");

    private static final List<String> CHALLENGE_COMMANDS = Arrays.asList(
            "create", "invite", "begin", "accept", "decline", "terminate", "info");

    public ParkourAutoTabCompleter(final Parkour parkour) {
        super(parkour);
    }

    /**
     * List of tab-able commands will be built based on the configuration and player permissions.
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command cmd,
                                      @NotNull String alias,
                                      @NotNull String... args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
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

        } else if (args.length == 4) {
            allowedCommands = populateThirdChildCommands(args[0], args[1], args[2]);
        }

        for (String allowedCommand : allowedCommands) {
            if (allowedCommand.startsWith(args[args.length - 1])) {
                filteredCommands.add(allowedCommand);
            }
        }

        return filteredCommands.isEmpty() ? allowedCommands : filteredCommands;
    }

    /**
     * Populate the main command options.
     * @param player player
     * @return allowed commands
     */
    private List<String> populateMainCommands(Player player) {
        // if they have an outstanding question, make those the only options
        if (parkour.getQuestionManager().hasBeenAskedQuestion(player)) {
            return QUESTION_ANSWER_COMMANDS;
        }

        List<String> allowedCommands = new ArrayList<>(NO_PERMISSION_COMMANDS);

        if (parkour.getPlayerManager().isPlaying(player)) {
            allowedCommands.addAll(ON_COURSE_COMMANDS);
        }
        // the player has an outstanding challenge request
        if (parkour.getChallengeManager().hasPlayerBeenInvited(player)) {
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
        if (PlayerInfo.hasSelectedCourse(player)
                || PermissionUtils.hasPermission(player, Permission.ADMIN_COURSE, false)) {
            allowedCommands.addAll(ADMIN_COURSE_COMMANDS);
        }
        if (PermissionUtils.hasPermission(player, Permission.ADMIN_ALL, false)) {
            allowedCommands.addAll(ADMIN_ONLY_COMMANDS);
        }

        return allowedCommands;
    }

    /**
     * Populate the first argument command options.
     * @param mainCommand main command
     * @return allowed commands
     */
    private List<String> populateFirstChildCommands(String mainCommand) {
        List<String> allowedCommands = new ArrayList<>();

        switch (mainCommand.toLowerCase()) {
            case "reset":
                allowedCommands = RESET_COMMANDS;
                break;
            case "delete":
                allowedCommands = DELETE_COMMANDS;
                break;
            case "list":
                allowedCommands = LIST_COMMANDS;
                break;
            case "economy":
                allowedCommands = ECONOMY_COMMANDS;
                break;
            case "link":
                allowedCommands = LINK_COMMANDS;
                break;
            case "cache":
                allowedCommands = CACHE_COMMANDS;
                break;
            case "cmds":
                allowedCommands = COMMANDS_MENU;
                break;
            case "challenge":
                allowedCommands = CHALLENGE_COMMANDS;
                break;
            case "lobby":
                allowedCommands = new ArrayList<>(LobbyInfo.getAllLobbyNames());
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
            case "linkkit":
            case "setplayerlimit":
            case "stats":
            case "settings":
            case "challengeonly":
            case "resumable":
                allowedCommands = CourseInfo.getAllCourseNames();
                break;
            case "test":
            case "kit":
            case "listkit":
            case "validatekit":
                allowedCommands = new ArrayList<>(ParkourKitInfo.getAllParkourKitNames());
                break;
            default:
                break;
        }

        return allowedCommands;
    }

    /**
     * Populate the second argument command options.
     * @param mainCommand main command
     * @param arg1 first argument
     * @return allowed commands
     */
    private List<String> populateSecondChildCommands(String mainCommand, String arg1) {
        List<String> allowedCommands = new ArrayList<>();

        switch (mainCommand.toLowerCase()) {
            case "setcourse":
                allowedCommands = SetCourseConversation.SET_COURSE_OPTIONS;
                break;
            case "setplayer":
                allowedCommands = SetPlayerConversation.SET_PLAYER_OPTIONS;
                break;
            case "economy":
                switch (arg1) {
                    case "setfee":
                    case "setprize":
                        allowedCommands = CourseInfo.getAllCourseNames();
                        break;
                    default:
                        break;
                }
                break;
            case "linkkit":
                allowedCommands = new ArrayList<>(ParkourKitInfo.getAllParkourKitNames());
                break;
            case "delete":
            case "link":
            case "reset":
                switch (arg1) {
                    case "autostart":
                    case "checkpoint":
                    case "course":
                    case "leaderboard":
                    case "prize":
                        allowedCommands = CourseInfo.getAllCourseNames();
                        break;
                    case "kit":
                        allowedCommands = new ArrayList<>(ParkourKitInfo.getAllParkourKitNames());
                        break;
                    case "lobby":
                        allowedCommands = new ArrayList<>(LobbyInfo.getAllLobbyNames());
                        break;
                    case "player":
                        allowedCommands = getAllOnlinePlayerNames();
                        break;
                    default:
                        break;
                }
                break;
            case "challenge":
                switch (arg1) {
                    case "create":
                        allowedCommands = CourseInfo.getAllCourseNames();
                        break;
                    case "invite":
                        allowedCommands = getAllOnlinePlayerNames();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return allowedCommands;
    }

    /**
     * Populate the third argument command options.
     * @param mainCommand main command
     * @param arg1 first argument
     * @param arg2 second argument
     * @return allowed commands
     */
    private List<String> populateThirdChildCommands(String mainCommand, String arg1, String arg2) {
        List<String> allowedCommands = new ArrayList<>();

        switch (mainCommand.toLowerCase()) {
            case "setcourse":
                switch (arg2.toLowerCase()) {
                    case "message":
                    case "command":
                        allowedCommands = SetCourseConversation.PARKOUR_EVENT_TYPE_NAMES;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        return allowedCommands;
    }

    /**
     * Get all Online Player names.
     * @return online player names
     */
    private List<String> getAllOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}

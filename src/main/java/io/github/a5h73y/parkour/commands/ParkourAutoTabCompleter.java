package io.github.a5h73y.parkour.commands;

import static io.github.a5h73y.parkour.commands.CommandUsage.ARRAY_OPEN;
import static io.github.a5h73y.parkour.commands.CommandUsage.FORMULA_OPEN;
import static io.github.a5h73y.parkour.commands.CommandUsage.SUBSTITUTION_OPEN;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.SetCourseConversation;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private final Map<String, Collection<String>> substitutions = new HashMap<>();

    private static final List<String> QUESTION_ANSWER_COMMANDS = Arrays.asList("yes", "no");

    public ParkourAutoTabCompleter(final Parkour parkour) {
        super(parkour);
        substitutions.put("(course)", parkour.getCourseManager().getCourseNames());
        substitutions.put("(parkourkit)", parkour.getConfigManager().getParkourKitConfig().getAllParkourKitNames());
        substitutions.put("(lobby)", parkour.getConfigManager().getLobbyConfig().getAllLobbyNames());
        substitutions.put("(player)", getAllOnlinePlayerNames());
        substitutions.put("(parkourevent)", SetCourseConversation.PARKOUR_EVENT_TYPE_NAMES);
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

        if (args.length <= 1) {
            allowedCommands = populateMainCommands(player);

        } else {
            Optional<CommandUsage> parentCommand = parkour.getCommandUsages().stream()
                    .filter(commandUsage -> args[0].toLowerCase().equals(commandUsage.getCommand()))
                    .filter(commandUsage -> commandUsage.getAutoTabSyntax() != null)
                    .filter(commandUsage -> commandUsage.getPermission() == null
                            || player.hasPermission(commandUsage.getPermission()))
                    .findAny();

            if (parentCommand.isPresent()) {
                CommandUsage selectedCommand = parentCommand.get();
                String[] syntaxArgs = selectedCommand.getAutoTabSyntaxArgs();

                if (syntaxArgs.length > args.length - 2) {
                    String nextArgument = syntaxArgs[args.length - 2];

                    if (nextArgument.startsWith(FORMULA_OPEN)) {
                        nextArgument = selectedCommand.resolveFormulaValue(nextArgument, args);
                    }

                    if (nextArgument.startsWith(ARRAY_OPEN)) {
                        allowedCommands = Arrays.asList(selectedCommand.getAutoTabArraySelection(nextArgument));
                    } else if (nextArgument.startsWith(SUBSTITUTION_OPEN) && substitutions.containsKey(nextArgument)) {
                        if (nextArgument.equalsIgnoreCase("(player)")) {
                            substitutions.put("(player)", getAllOnlinePlayerNames());
                        }
                        allowedCommands = new ArrayList<>(substitutions.get(nextArgument));
                    } else {
                        allowedCommands = Collections.singletonList(nextArgument);
                    }
                }
            }
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

        return parkour.getCommandUsages().stream()
                .filter(commandUsage -> commandUsage.getPermission() == null
                        || player.hasPermission(commandUsage.getPermission()))
                .map(CommandUsage::getCommand)
                .collect(Collectors.toList());
    }

    /**
     * Get all Online Player names.
     * @return online player names
     */
    private List<String> getAllOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}

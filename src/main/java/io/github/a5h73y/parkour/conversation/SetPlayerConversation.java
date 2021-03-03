package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetPlayerConversation extends ParkourConversation {

    public static final List<String> SET_PLAYER_OPTIONS = Collections.unmodifiableList(
            Arrays.asList("level", "leveladd", "rank"));

    public SetPlayerConversation(Player player) {
        super(player);
    }

    @NotNull
    @Override
    public Prompt getEntryPrompt() {
        return new ChooseSetPlayerOption();
    }

    private static class ChooseSetPlayerOption extends FixedSetPrompt {
        ChooseSetPlayerOption() {
            super(SET_PLAYER_OPTIONS.toArray(new String[0]));
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What player option would you like to set?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String choice) {
            context.setSessionData("setOption", choice);
            return new SetPlayerOptionValue();
        }
    }

    private static class SetPlayerOptionValue extends StringPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What value would you like to set?";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context,
                                  @Nullable String input) {

            String setOption = (String) context.getSessionData("setOption");

            if (input == null || input.trim().isEmpty()) {
                return null;
            }

            String playerName = (String) context.getSessionData(SESSION_PLAYER_NAME);
            String targetPlayerName = (String) context.getSessionData(SESSION_TARGET_PLAYER_NAME);
            Player player = Bukkit.getPlayer(playerName);
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

            performAction(player, targetPlayer, setOption, input);

            return Prompt.END_OF_CONVERSATION;
        }
    }

    /**
     * Perform Set Player Command.
     *
     * @param sender command sender
     * @param targetPlayer target player
     * @param setOption option to set
     * @param input input value
     */
    public static void performAction(CommandSender sender, OfflinePlayer targetPlayer, String setOption, String input) {
        Parkour parkour = Parkour.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(parkour, () -> {
            switch (setOption) {
                case "level":
                    parkour.getPlayerManager().setParkourLevel(sender, targetPlayer, input, false);
                    break;

                case "leveladd":
                    parkour.getPlayerManager().setParkourLevel(sender, targetPlayer, input, true);
                    break;

                case "rank":
                    parkour.getPlayerManager().setParkourRank(sender, targetPlayer, input);
                    break;

                default:
                    TranslationUtils.sendInvalidSyntax(sender, "setplayer",
                            "(player) [level / leveladd / rank] [value]");
            }
        });
    }
}

package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.other.ParkourConversation;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetPlayerConversation extends ParkourConversation {

    public static final List<String> SET_PLAYER_OPTIONS = List.of("level", "leveladd", "rank");

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

            if (!ValidationUtils.isStringValid(input)) {
                return null;
            }

            String playerName = (String) context.getSessionData(SESSION_PLAYER_NAME);
            String targetPlayerName = (String) context.getSessionData(SESSION_TARGET_PLAYER_NAME);
            String setOption = (String) context.getSessionData("setOption");
            Player player = Bukkit.getPlayer(playerName);
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

            Parkour parkour = Parkour.getInstance();
            Bukkit.getScheduler().runTaskAsynchronously(parkour, () ->
                // for messages to be sent - do it async
                parkour.getPlayerManager().processCommand(player, targetPlayer, setOption, input)
            );

            return Prompt.END_OF_CONVERSATION;
        }
    }
}

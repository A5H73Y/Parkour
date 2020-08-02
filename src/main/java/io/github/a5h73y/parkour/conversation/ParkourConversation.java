package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public abstract class ParkourConversation implements ConversationAbandonedListener {

    private final ConversationFactory conversationFactory;
    private final Player player;
    private String courseName;

    public abstract Prompt getEntryPrompt();

    public ParkourConversation(Player player) {
        this.player = player;

        conversationFactory = new ConversationFactory(Parkour.getInstance())
                .withEscapeSequence("cancel")
                .withTimeout(30)
                .thatExcludesNonPlayersWithMessage("This is only possible in game, sorry.")
                .addConversationAbandonedListener(this)
                .withFirstPrompt(getEntryPrompt());

        player.sendMessage(ChatColor.GRAY + "Note: Enter 'cancel' to quit the conversation.");
    }

    public static void sendErrorMessage(ConversationContext context, String message) {
        context.getForWhom().sendRawMessage(ChatColor.RED + message + ". Please try again...");
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent event) {
        if (!event.gracefulExit()) {
            event.getContext().getForWhom().sendRawMessage(Parkour.getPrefix() + "Conversation aborted...");
        }
    }

    public ParkourConversation withCourseName(String courseName) {
        this.courseName = courseName;
        return this;
    }

    public void begin() {
        Conversation convo = conversationFactory.buildConversation(player);
        convo.getContext().setSessionData("playerName", player.getName());
        if (courseName != null) {
            convo.getContext().setSessionData("courseName", courseName);
        }
        convo.begin();
    }
}

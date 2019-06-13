package me.A5H73Y.parkour.conversation;

import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.utilities.Static;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public abstract class ParkourConversation implements ConversationAbandonedListener {

    private ConversationFactory conversationFactory;

    public abstract Prompt getEntryPrompt();

    private String courseName;
    private Player player;

    public ParkourConversation(Player player) {
        this.player = player;

        conversationFactory = new ConversationFactory(Parkour.getPlugin())
                .withEscapeSequence("cancel")
                .withTimeout(30)
                .thatExcludesNonPlayersWithMessage("This is only possible in game, sorry.")
                .addConversationAbandonedListener(this)
                .withFirstPrompt(getEntryPrompt());

        player.sendMessage(ChatColor.GRAY + "Note: Enter 'cancel' to quit the conversation.");
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent event) {
        if (!event.gracefulExit()) {
            event.getContext().getForWhom().sendRawMessage(Static.getParkourString() + "Conversation aborted...");
        }
    }

    public static void sendErrorMessage(ConversationContext context, String message) {
        context.getForWhom().sendRawMessage(ChatColor.RED + message + ". Please try again...");
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

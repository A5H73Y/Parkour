package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class ParkourConversation implements ConversationAbandonedListener {

    public static final String SESSION_PLAYER_NAME = "playerName";
    public static final String SESSION_TARGET_PLAYER_NAME = "targetPlayerName";
    public static final String SESSION_COURSE_NAME = "courseName";

    private final ConversationFactory conversationFactory;
    private final Conversable conversable;
    private String courseName;
    private String targetPlayerName;

    public abstract Prompt getEntryPrompt();

    public ParkourConversation(Conversable conversable) {
        this.conversable = conversable;

        conversationFactory = new ConversationFactory(Parkour.getInstance())
                .withEscapeSequence("cancel")
                .withTimeout(30)
                .addConversationAbandonedListener(this)
                .withFirstPrompt(getEntryPrompt());

        conversable.sendRawMessage(ChatColor.GRAY + "Note: Enter 'cancel' to quit the conversation.");
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

    public ParkourConversation withCourseName(@NotNull String courseName) {
        this.courseName = courseName.toLowerCase();
        return this;
    }

    public ParkourConversation withTargetPlayerName(String targetPlayerName) {
        this.targetPlayerName = targetPlayerName;
        return this;
    }

    public void begin() {
        if (courseName != null && !Parkour.getInstance().getCourseManager().doesCourseExists(courseName)) {
            conversable.sendRawMessage(TranslationUtils.getValueTranslation("Error.NoExist", courseName));
            return;
        }

        Conversation conversation = conversationFactory.buildConversation(conversable);
        String conversableName = conversable instanceof Player ? ((Player) conversable).getName() : "Console";
        conversation.getContext().setSessionData(SESSION_PLAYER_NAME, conversableName);
        if (courseName != null) {
            conversation.getContext().setSessionData(SESSION_COURSE_NAME, courseName);
        }
        if (targetPlayerName != null) {
            conversation.getContext().setSessionData(SESSION_TARGET_PLAYER_NAME, targetPlayerName);
        }
        conversation.begin();
    }
}

package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class ParkourModeConversation extends ParkourConversation {

    public ParkourModeConversation(Player player) {
        super(player);
    }

    @Override
    public Prompt getEntryPrompt() {
        return new ChooseParkourMode();
    }

    private class ChooseParkourMode extends FixedSetPrompt {

        ChooseParkourMode() {
            super("freedom", "darkness", "drunk", "speedy", "moon", "dropper", "rockets", "none");
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What type of ParkourMode would you like to set?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
            String courseName = (String) context.getSessionData("courseName");

            CourseInfo.setMode(courseName, choice);

            context.getForWhom().sendRawMessage(TranslationUtils.getTranslation("Parkour.SetMode")
                    .replace("%COURSE%", courseName)
                    .replace("%MODE%", choice));

            return Prompt.END_OF_CONVERSATION;
        }
    }
}
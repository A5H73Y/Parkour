package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CheckpointPrizeConversation extends CoursePrizeConversation {

    public CheckpointPrizeConversation(Player player) {
        super(player);
    }

    @Override
    public Prompt getEntryPrompt() {
        return new ChooseCommand();
    }

    @Override
    MessagePrompt getCommandProcessCompletePrompt() {
        return new CommandProcessComplete();
    }

    private static class CommandProcessComplete extends MessagePrompt {

        @NotNull
        public String getPromptText(ConversationContext context) {
            String courseName = context.getSessionData(SESSION_COURSE_NAME).toString();
            CourseInfo.addCheckpointCommandPrize(courseName, context.getSessionData("command").toString());

            return TranslationUtils.getPropertySet("Checkpoint Command Prize", courseName,
                    "/" + context.getSessionData("command"));
        }

        @Override
        protected Prompt getNextPrompt(@NotNull ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }
    }
}

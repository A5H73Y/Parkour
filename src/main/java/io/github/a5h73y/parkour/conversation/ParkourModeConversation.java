package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.stream.Stream;
import com.cryptomorin.xseries.XPotion;
import org.bukkit.ChatColor;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParkourModeConversation extends ParkourConversation {

    public ParkourModeConversation(Conversable conversable) {
        super(conversable);
    }

    @Override
    public Prompt getEntryPrompt() {
        return new ChooseParkourMode();
    }

    private static class ChooseParkourMode extends FixedSetPrompt {

        ChooseParkourMode() {
            super(Stream.of(ParkourMode.values()).map(mode -> mode.name().toLowerCase()).toArray(String[]::new));
        }

        @Override
        @NotNull
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What type of ParkourMode would you like to set?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String choice) {

            if (choice.equals(ParkourMode.POTION.name().toLowerCase())) {
                return new ChoosePotionEffect();
            }

            String courseName = (String) context.getSessionData(SESSION_COURSE_NAME);
            CourseInfo.setParkourMode(courseName, choice);
            Parkour.getInstance().getCourseManager().clearCache(courseName);

            context.getForWhom().sendRawMessage(TranslationUtils.getPropertySet("ParkourMode", courseName, choice));
            return Prompt.END_OF_CONVERSATION;
        }
    }

    private static class ChoosePotionEffect extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context,
                                       @NotNull String input) {
            return XPotion.matchXPotion(input.toUpperCase()).isPresent();
        }

        @Override
        @Nullable
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String input) {
            context.setSessionData("potion", input.toUpperCase());
            return new WantSpecifyDurationAmplifier();
        }

        @Override
        @NotNull
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What type of Potion Effect would you like to apply?";
        }

        @Override
        @Nullable
        protected String getFailedValidationText(@NotNull ConversationContext context, @NotNull String invalidInput) {
            return invalidInput + " is an unknown Potion Effect Type.";
        }
    }

    private static class WantSpecifyDurationAmplifier extends BooleanPrompt {

        @Override
        @Nullable
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              boolean input) {
            return input ? new SpecifyDurationAmplifier() : new WantSpecifyJoinMessage();
        }

        @Override
        @NotNull
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Would you like to provide a duration and amplifier?";
        }
    }

    private static class SpecifyDurationAmplifier extends ValidatingPrompt {

        private static final String NUMBER_REGEX = "^\\d*,\\d+\\.?\\d*$";

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
            return input.matches(NUMBER_REGEX);
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
            context.setSessionData("durationAmplifier", input);
            return new WantSpecifyJoinMessage();
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Please specify the duration and amplifier in the format of " +
                    ChatColor.GREEN + "(duration),(amplifier)";
        }
    }

    private static class WantSpecifyJoinMessage extends BooleanPrompt {

        @Override
        @Nullable
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              boolean input) {
            return input ? new SpecifyJoinMessage() : new ParkourModePotionDone();
        }

        @Override
        @NotNull
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Would you like to provide a join message?";
        }
    }

    private static class SpecifyJoinMessage extends StringPrompt {

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Please specify the join message.";
        }

        @Override
        public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
            context.setSessionData("joinMessage", input);
            return new ParkourModePotionDone();
        }
    }

    private static class ParkourModePotionDone extends MessagePrompt {

        @Override
        @Nullable
        protected Prompt getNextPrompt(@NotNull ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        @NotNull
        public String getPromptText(@NotNull ConversationContext context) {
            String courseName = (String) context.getSessionData(SESSION_COURSE_NAME);
            String potionEffect = (String) context.getSessionData("potion");
            String durationAmplifier = (String) context.getSessionData("durationAmplifier");
            String joinMessage = (String) context.getSessionData("joinMessage");
            String potionParkourMode = ParkourMode.POTION.name().toLowerCase();

            CourseInfo.setParkourMode(courseName, potionParkourMode);
            CourseInfo.addPotionParkourModeEffect(courseName, potionEffect, durationAmplifier);
            CourseInfo.setPotionJoinMessage(courseName, joinMessage);

            Parkour.getInstance().getCourseManager().clearCache(courseName);

            return TranslationUtils.getPropertySet("ParkourMode", courseName,
                    potionParkourMode + " (" + potionEffect + ")");
        }
    }
}

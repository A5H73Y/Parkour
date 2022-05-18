package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.other.ParkourConversation;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import java.util.Arrays;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.potion.PotionEffectType;
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
            super(Stream.of(ParkourMode.values()).map(ParkourMode::getDisplayName).toArray(String[]::new));
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What type of ParkourMode would you like to set?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String choice) {

            if (choice.equals(ParkourMode.POTION.getDisplayName())) {
                return new ChoosePotionEffect();
            }

            ParkourMode parkourMode = ParkourMode.valueOf(choice.toUpperCase());
            String courseName = (String) context.getSessionData(SESSION_COURSE_NAME);

            Bukkit.getScheduler().runTaskAsynchronously(Parkour.getInstance(), () ->
                Parkour.getInstance().getCourseSettingsManager()
                        .setParkourMode((CommandSender) context.getForWhom(), courseName, parkourMode));
            return Prompt.END_OF_CONVERSATION;
        }
    }

    private static class ChoosePotionEffect extends FixedSetPrompt {

        ChoosePotionEffect() {
            super(Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).toArray(String[]::new));
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What type of Potion Effect would you like to apply?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
            return super.isInputValid(context, input.toUpperCase());
        }

        @NotNull
        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String input) {
            context.setSessionData("potion", input.toUpperCase());
            return new WantSpecifyDurationAmplifier();
        }

        @Nullable
        @Override
        protected String getFailedValidationText(@NotNull ConversationContext context,
                                                 @NotNull String invalidInput) {
            return invalidInput + " is an unknown Potion Effect Type.";
        }
    }

    private static class WantSpecifyDurationAmplifier extends BooleanPrompt {

        @NotNull
        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              boolean input) {
            return input ? new SpecifyDurationAmplifier() : new WantSpecifyJoinMessage();
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Would you like to provide a duration and amplifier?";
        }
    }

    private static class SpecifyDurationAmplifier extends ValidatingPrompt {

        private static final String NUMBER_REGEX = "^\\d*,\\d+\\.?\\d*$";

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Please specify the duration and amplifier in the format of "
                    + ChatColor.GREEN + "(duration),(amplifier)";
        }

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
            return input.matches(NUMBER_REGEX);
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
            context.setSessionData("durationAmplifier", input);
            return new WantSpecifyJoinMessage();
        }
    }

    private static class WantSpecifyJoinMessage extends BooleanPrompt {

        @NotNull
        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              boolean input) {
            return input ? new SpecifyJoinMessage() : new ParkourModePotionDone();
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Would you like to provide a join message?";
        }
    }

    private static class SpecifyJoinMessage extends StringPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Please specify the join message.";
        }

        @NotNull
        @Override
        public Prompt acceptInput(@NotNull ConversationContext context,
                                  @Nullable String input) {
            context.setSessionData("joinMessage", input);
            return new ParkourModePotionDone();
        }
    }

    private static class ParkourModePotionDone extends MessagePrompt {

        @NotNull
        @Override
        protected Prompt getNextPrompt(@NotNull ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            String courseName = (String) context.getSessionData(SESSION_COURSE_NAME);
            String potionEffect = (String) context.getSessionData("potion");
            String durationAmplifier = (String) context.getSessionData("durationAmplifier");
            String joinMessage = (String) context.getSessionData("joinMessage");
            Bukkit.getScheduler().runTaskAsynchronously(Parkour.getInstance(), () ->
                Parkour.getInstance().getCourseSettingsManager().setPotionParkourMode(
                        (CommandSender) context.getForWhom(), courseName, potionEffect, durationAmplifier, joinMessage));
            return "";
        }
    }
}

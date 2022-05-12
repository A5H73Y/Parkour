package io.github.a5h73y.parkour.conversation;

import static io.github.a5h73y.parkour.other.ParkourConstants.PLAYER_PLACEHOLDER;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.other.ParkourConversation;
import io.github.a5h73y.parkour.type.course.ParkourEventType;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;

public class CoursePrizeConversation extends ParkourConversation {

    private static final String MATERIAL = "material";
    private static final String COMMAND = "command";
    private static final String AMOUNT = "amount";

    public CoursePrizeConversation(Conversable conversable) {
        super(conversable);
    }

    @Override
    public Prompt getEntryPrompt() {
        return new PrizeType();
    }

    private class PrizeType extends FixedSetPrompt {

        public PrizeType() {
            super(MATERIAL, COMMAND, "xp");
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Which type of prize would you like to set?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String choice) {
            if (choice.equalsIgnoreCase(MATERIAL)) {
                return new ChooseBlock();
            }

            if (choice.equalsIgnoreCase(COMMAND)) {
                return new ChooseCommand();
            }

            if (choice.equalsIgnoreCase("xp")) {
                return new ChooseXP();
            }

            return null;
        }
    }

    /* BEGIN MATERIAL PRIZE */
    private static class ChooseBlock extends StringPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Material do you want to reward the player with?";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context, String message) {
            Material material = MaterialUtils.lookupMaterial(message.toUpperCase());

            if (material == null) {
                sendErrorMessage(context, TranslationUtils.getValueTranslation("Error.UnknownMaterial",
                        message.toUpperCase(), false));
                return this;
            }

            context.setSessionData(MATERIAL, message.toUpperCase());
            return new ChooseAmount();
        }
    }

    private static class ChooseAmount extends NumericPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " How many would you like to reward the player with?";
        }

        @Override
        protected boolean isNumberValid(@NotNull ConversationContext context, Number input) {
            return input.intValue() > 0 && input.intValue() <= 255;
        }

        @Override
        protected String getFailedValidationText(@NotNull ConversationContext context,
                                                 @NotNull Number invalidInput) {
            return "Amount must be between 1 and 255.";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number amount) {
            context.setSessionData(AMOUNT, amount.intValue());
            return new MaterialProcessComplete();
        }
    }

    private static class MaterialProcessComplete extends MessagePrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            String courseName = context.getSessionData(SESSION_COURSE_NAME).toString();
            Parkour.getInstance().getConfigManager().getCourseConfig(courseName).setMaterialPrize(
                    context.getSessionData(MATERIAL).toString(),
                    Integer.parseInt(context.getSessionData(AMOUNT).toString()));

            return TranslationUtils.getPropertySet("Material Prize", courseName,
                    context.getSessionData(AMOUNT) + " " + context.getSessionData(MATERIAL));
        }

        @Override
        protected Prompt getNextPrompt(@NotNull ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }
    }

    /* BEGIN COMMAND PRIZE */
    class ChooseCommand extends StringPrompt {

        @NotNull
        @Override
        public String getPromptText(ConversationContext context) {
            context.getForWhom().sendRawMessage(ChatColor.GRAY
                    + "Remember you can include %PLAYER% to apply it to that player.\nExample: 'kick %PLAYER%'");
            return ChatColor.LIGHT_PURPLE + " What would you like the Command prize to be?";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context, String message) {
            String command = message.replace("/", "");
            context.setSessionData(COMMAND, command);

            return new ChooseRunNow();
        }
    }

    private class ChooseRunNow extends BooleanPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Would you like to run this command now? (to test)\n"
                    + ChatColor.GREEN + "[yes, no]";
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, boolean runNow) {
            if (runNow) {
                Parkour.getInstance().getServer().dispatchCommand(
                        Parkour.getInstance().getServer().getConsoleSender(),
                        context.getSessionData(COMMAND).toString()
                                .replace(PLAYER_PLACEHOLDER, context.getSessionData(SESSION_PLAYER_NAME).toString()));
            }
            return new CommandProcessComplete();
        }
    }

    private static class CommandProcessComplete extends MessagePrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            String courseName = context.getSessionData(SESSION_COURSE_NAME).toString();
            Parkour.getInstance().getConfigManager().getCourseConfig(courseName)
                    .addEventCommand(ParkourEventType.PRIZE, context.getSessionData(COMMAND).toString());

            return TranslationUtils.getPropertySet("Command Prize", courseName,
                    "/" + context.getSessionData(COMMAND));
        }

        @Override
        protected Prompt getNextPrompt(@NotNull ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }
    }

    /* BEGIN XP PRIZE */
    private static class ChooseXP extends NumericPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " How much XP would you like to reward the player with?";
        }

        @Override
        protected boolean isNumberValid(@NotNull ConversationContext context, Number input) {
            return input.intValue() > 0 && input.intValue() <= 10000;
        }

        @Override
        protected String getFailedValidationText(@NotNull ConversationContext context,
                                                 @NotNull Number invalidInput) {
            return "Amount must be between 1 and 10,000.";
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, Number amount) {
            context.setSessionData(AMOUNT, amount.intValue());

            return new XpProcessComplete();
        }
    }

    private static class XpProcessComplete extends MessagePrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            String courseName = context.getSessionData(SESSION_COURSE_NAME).toString();
            String amount = context.getSessionData(AMOUNT).toString();
            Parkour.getInstance().getConfigManager().getCourseConfig(courseName).setXpPrize(Integer.parseInt(amount));

            return TranslationUtils.getPropertySet("XP Prize", courseName, amount);
        }

        @Override
        protected Prompt getNextPrompt(@NotNull ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }
    }
}

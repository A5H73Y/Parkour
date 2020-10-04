package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CoursePrizeConversation extends ParkourConversation {

    public CoursePrizeConversation(Player player) {
        super(player);
    }

    @Override
    public Prompt getEntryPrompt() {
        return new PrizeType();
    }

    MessagePrompt getCommandProcessCompletePrompt() {
        return new CommandProcessComplete();
    }

    private class PrizeType extends FixedSetPrompt {

        public PrizeType() {
            super("material", "command", "xp");
        }

        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What type of prize would you like to set?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String choice) {
            if (choice.equalsIgnoreCase("material")) {
                return new ChooseBlock();
            }

            if (choice.equalsIgnoreCase("command")) {
                return new ChooseCommand();
            }

            if (choice.equalsIgnoreCase("xp")) {
                return new ChooseXP();
            }

            return null;
        }
    }

    /* BEGIN MATERIAL PRIZE */
    private class ChooseBlock extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Material do you want to reward the player with?";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String message) {
            Material material = MaterialUtils.lookupMaterial(message.toUpperCase());

            if (material == null) {
                sendErrorMessage(context, "This is not a valid material");
                return this;
            }

            context.setSessionData("material", message.toUpperCase());
            return new ChooseAmount();
        }
    }

    private class ChooseAmount extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " How many would you like to reward the player with?";
        }

        @Override
        protected boolean isNumberValid(ConversationContext context, Number input) {
            return input.intValue() > 0 && input.intValue() <= 255;
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
            return "Amount must be between 1 and 255.";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number amount) {
            context.setSessionData("amount", amount.intValue());

            return new MaterialProcessComplete();
        }
    }

    private static class MaterialProcessComplete extends MessagePrompt {

        public String getPromptText(ConversationContext context) {
            String courseName = context.getSessionData(SESSION_COURSE_NAME).toString();
            CourseInfo.setMaterialPrize(courseName,
                    context.getSessionData("material").toString(),
                    Integer.parseInt(context.getSessionData("amount").toString()));

            return TranslationUtils.getPropertySet("Material Prize", courseName,
                    context.getSessionData("amount") + " " + context.getSessionData("material"));
        }

        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }
    }

    /* BEGIN COMMAND PRIZE */
    class ChooseCommand extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            context.getForWhom().sendRawMessage(ChatColor.GRAY + "Remember you can include %PLAYER% to apply it to that player.\nExample: 'kick %PLAYER%'");
            return ChatColor.LIGHT_PURPLE + " What would you like the Command prize to be?";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String message) {
            String command = message.replace("/", "");
            context.setSessionData("command", command);

            return new ChooseRunNow();
        }
    }

    private class ChooseRunNow extends BooleanPrompt {

        @Override
        public String getPromptText(ConversationContext arg0) {
            return ChatColor.LIGHT_PURPLE + " Would you like to run this command now? (to test)\n" +
                    ChatColor.GREEN + "[yes, no]";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean runNow) {
            if (runNow) {
                Parkour.getInstance().getServer().dispatchCommand(
                        Parkour.getInstance().getServer().getConsoleSender(),
                        context.getSessionData("command").toString()
                                .replace("%PLAYER%", context.getSessionData(SESSION_PLAYER_NAME).toString()));
            }
            return getCommandProcessCompletePrompt();
        }
    }

    private static class CommandProcessComplete extends MessagePrompt {

        public String getPromptText(ConversationContext context) {
            String courseName = context.getSessionData(SESSION_COURSE_NAME).toString();
            CourseInfo.addCommandPrize(courseName, context.getSessionData("command").toString());

            return TranslationUtils.getPropertySet("Command Prize", courseName,
                    "/" + context.getSessionData("command"));
        }

        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }
    }

    /* BEGIN XP PRIZE */
    private class ChooseXP extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " How much XP would you like to reward the player with?";
        }

        @Override
        protected boolean isNumberValid(ConversationContext context, Number input) {
            return input.intValue() > 0 && input.intValue() <= 10000;
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
            return "Amount must be between 1 and 10,000.";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number amount) {
            context.setSessionData("amount", amount.intValue());

            return new XPProcessComplete();
        }
    }

    private static class XPProcessComplete extends MessagePrompt {

        public String getPromptText(ConversationContext context) {
            String courseName = context.getSessionData(SESSION_COURSE_NAME).toString();
            String amount = context.getSessionData("amount").toString();
            CourseInfo.setXPPrize(courseName, Integer.parseInt(amount));

            return TranslationUtils.getPropertySet("XP Prize", courseName, amount);
        }

        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }
    }
}

package io.github.a5h73y.parkour.conversation.parkourkit;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.other.ParkourConversation;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;

/**
 * Conversation to create a new {@link io.github.a5h73y.parkour.type.kit.ParkourKit}.
 */
public class CreateParkourKitConversation extends ParkourKitConversation {

    /**
     * Construct a Parkour Conversation.
     *
     * @param conversable conversable user
     */
    public CreateParkourKitConversation(@NotNull Conversable conversable) {
        super(conversable);
    }

    @Override
    protected boolean isProvidedNameValid() {
        return this.kitName != null
                && !this.kitName.isEmpty()
                && !this.kitName.contains(" ")
                && !Parkour.getParkourKitConfig().doesParkourKitExist(this.kitName);
    }

    @Override
    protected StringPrompt getProvideValidKitPrompt() {
        return new ChooseNewKitNamePrompt();
    }

    @Override
    protected Prompt getFirstPrompt() {
        return new ShouldUseStandardBlocksPrompt();
    }

    private static class ChooseNewKitNamePrompt extends StringPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What would you like to name your ParkourKit?";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context, String name) {
            if (name == null || name.isEmpty()) {
                return Prompt.END_OF_CONVERSATION;
            }

            if (name.contains(" ")) {
                ParkourConversation.sendErrorMessage(context, "The ParkourKit name cannot include spaces");
                return this;
            }

            name = name.toLowerCase();

            if (Parkour.getParkourKitConfig().doesParkourKitExist(name)) {
                ParkourConversation.sendErrorMessage(context, "This ParkourKit already exists");
                return this;
            }

            context.setSessionData(PARKOUR_KIT_NAME, name);
            return new ShouldUseStandardBlocksPrompt();
        }
    }

    private static class ShouldUseStandardBlocksPrompt extends BooleanPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Would you like to start with the standard blocks?\n"
                    + ChatColor.GREEN + "[yes, no]";
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, boolean input) {
            String name = context.getSessionData(PARKOUR_KIT_NAME).toString();

            if (input) {
                context.getForWhom().sendRawMessage(TranslationUtils.getPluginPrefix() + name + " will use standard blocks...");
                Parkour.getParkourKitConfig().createStandardKit(name);
            }

            return new AddKitItemConversation(Prompt.END_OF_CONVERSATION, name).startConversation();
        }
    }
}

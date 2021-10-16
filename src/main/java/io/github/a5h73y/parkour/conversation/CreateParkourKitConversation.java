package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.other.AddKitItemConversation;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;

public class CreateParkourKitConversation extends ParkourConversation {

    public CreateParkourKitConversation(Conversable conversable) {
        super(conversable);
    }

    @Override
    public Prompt getEntryPrompt() {
        return new ChooseKitName();
    }

    private static class ChooseKitName extends StringPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What would you like to name your ParkourKit?";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context, String name) {
            if (name.isEmpty()) {
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

            context.setSessionData("name", name);
            return new UseStandardKit();
        }
    }

    private static class UseStandardKit extends BooleanPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Would you like to start with the standard blocks?\n"
                    + ChatColor.GREEN + "[yes, no]";
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, boolean input) {
            String name = context.getSessionData("name").toString();

            if (input) {
                context.getForWhom().sendRawMessage(TranslationUtils.getPluginPrefix() + name + " will use standard blocks...");
                Parkour.getParkourKitConfig().createStandardKit(name);
            }

            return new AddKitItemConversation(Prompt.END_OF_CONVERSATION, name).startConversation();
        }
    }
}

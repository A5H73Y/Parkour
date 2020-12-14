package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.impl.ParkourKitConfig;
import io.github.a5h73y.parkour.conversation.other.AddKitItemConversation;
import io.github.a5h73y.parkour.enums.ConfigType;
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

        public Prompt acceptInput(@NotNull ConversationContext context, String name) {
            if (name.length() == 0) {
                return Prompt.END_OF_CONVERSATION;
            }

            if (name.contains(" ")) {
                ParkourConversation.sendErrorMessage(context, "The ParkourKit name cannot include spaces");
                return this;
            }

            name = name.toLowerCase();

            if (Parkour.getConfig(ConfigType.PARKOURKIT).contains("ParkourKit." + name)) {
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
            ParkourKitConfig parkourKitFile = (ParkourKitConfig) Parkour.getConfig(ConfigType.PARKOURKIT);

            if (input) {
                context.getForWhom().sendRawMessage(Parkour.getPrefix() + name + " will use standard blocks...");
                parkourKitFile.createStandardKit(name);
                parkourKitFile.save();
            }

            return new AddKitItemConversation(Prompt.END_OF_CONVERSATION, name).startConversation();
        }
    }
}

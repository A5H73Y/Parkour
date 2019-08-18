package me.A5H73Y.parkour.conversation;

import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.config.impl.ParkourKitFile;
import me.A5H73Y.parkour.conversation.other.AddKitItemConversation;
import me.A5H73Y.parkour.utilities.Static;
import org.bukkit.ChatColor;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import static me.A5H73Y.parkour.enums.ConfigType.PARKOURKIT;

public class CreateParkourKitConversation extends ParkourConversation {

    public CreateParkourKitConversation(Player player) {
        super(player);
    }

    @Override
    public Prompt getEntryPrompt() {
        return new ChooseKitName();
    }

    private class ChooseKitName extends StringPrompt {

        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What would you like to name your ParkourKit?";
        }

        public Prompt acceptInput(ConversationContext context, String name) {
            if (name.length() == 0) {
                return Prompt.END_OF_CONVERSATION;
            }

            if (name.contains(" ")) {
                ParkourConversation.sendErrorMessage(context, "The ParkourKit name cannot include spaces");
                return this;
            }

            name = name.toLowerCase();

            if (Parkour.getConfig(PARKOURKIT).contains("ParkourKit." + name)) {
                ParkourConversation.sendErrorMessage(context, "This ParkourKit already exists");
                return this;
            }

            context.setSessionData("name", name);
            return new UseStandardKit();
        }
    }

    private class UseStandardKit extends BooleanPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Would you like to start with the standard blocks?\n" +
                    ChatColor.GREEN + "[yes, no]";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean input) {
            String name = context.getSessionData("name").toString();
            ParkourKitFile parkourKitFile = (ParkourKitFile) Parkour.getConfig(PARKOURKIT);

            if (input) {
                context.getForWhom().sendRawMessage(Static.getParkourString() + name + " will use standard blocks...");
                parkourKitFile.createStandardKit(name);
                parkourKitFile.save();
            }

            return new AddKitItemConversation(Prompt.END_OF_CONVERSATION, name).startConversation();
        }
    }
}

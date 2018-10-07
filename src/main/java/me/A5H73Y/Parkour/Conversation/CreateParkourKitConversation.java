package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Conversation.other.AddKitItemConversation;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.ParkourKit.ParkourKitInfo;
import me.A5H73Y.Parkour.Utilities.Static;
import org.bukkit.ChatColor;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class CreateParkourKitConversation extends StringPrompt {

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

        if (Parkour.getParkourConfig().getParkourKitData().contains("ParkourKit." + name)) {
            ParkourConversation.sendErrorMessage(context, "This ParkourKit already exists");
            return this;
        }

        context.setSessionData("name", name);
        return new UseStandardKit();
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

            if (input) {
                context.getForWhom().sendRawMessage(Static.getParkourString() + name + " will use standard blocks...");
                ParkourKitInfo.createStandardKit(Parkour.getParkourConfig().getParkourKitData(), name);
                Parkour.getParkourConfig().saveParkourKit();
            }

            return new AddKitItemConversation(Prompt.END_OF_CONVERSATION, name).startConversation();
        }
    }
}

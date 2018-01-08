package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Conversation.other.AddKitItemConversation;
import me.A5H73Y.Parkour.Other.ParkourKit;
import me.A5H73Y.Parkour.Parkour;

import me.A5H73Y.Parkour.Utilities.Static;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class CreateParkourKitConversation extends StringPrompt {

    public String getPromptText(ConversationContext context) {
        return ChatColor.LIGHT_PURPLE + " What would you like to name your ParkourKit?";
    }

    public Prompt acceptInput(ConversationContext context, String name) {
        if (name.length() == 0) {
            return Prompt.END_OF_CONVERSATION;
        }

        name = name.toLowerCase().replace(' ', '_');

        if (Parkour.getParkourConfig().getParkourKitData().contains("ParkourKit." + name)){
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
                ParkourKit.createStandardKit(Parkour.getParkourConfig().getParkourKitData(), name);
                Parkour.getParkourConfig().saveParkourKit();
            }

            return new AddKitItemConversation(Prompt.END_OF_CONVERSATION, name).startConversation();
        }
    }
}

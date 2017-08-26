package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Conversation.other.AddKitItemConversation;
import me.A5H73Y.Parkour.Parkour;

import me.A5H73Y.Parkour.Utilities.Static;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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

    public Prompt acceptInput(ConversationContext context, String message) {
        if (message.length() == 0) {
            return Prompt.END_OF_CONVERSATION;
        }

        message = message.toLowerCase();

        if (Parkour.getParkourConfig().getParkourKitData().contains("ParkourKit." + message)){
            ParkourConversation.sendErrorMessage(context, "This ParkourKit already exists");
            return this;
        }

        return new AddKitItemConversation(Prompt.END_OF_CONVERSATION, message).startConversation();
    }
}

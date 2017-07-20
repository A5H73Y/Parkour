package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Other.ParkourKit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

import java.util.Arrays;

public class EditParkourKitConversation extends FixedSetPrompt {

    EditParkourKitConversation(){
        super(Arrays.toString(ParkourKit.getAllParkourKits().toArray()));
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return ChatColor.LIGHT_PURPLE + " What ParkourKit would you like to edit?\n" +
                ChatColor.GREEN + formatFixedSet();
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
        context.setSessionData("kit", choice);
        return new ChooseOption();
    }


    private class ChooseOption extends FixedSetPrompt {
        ChooseOption(){
            super("add", "remove");
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What option would you like to perform?\n" +
                    ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
            if (choice.equals("add")) {
                //TODO
            }
            return null;
        }
    }
}

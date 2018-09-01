package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Conversation.other.AddKitItemConversation;
import me.A5H73Y.Parkour.Other.ParkourKit;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.Static;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class EditParkourKitConversation extends FixedSetPrompt {

    EditParkourKitConversation() {
        super(ParkourKit.getAllParkourKits().toArray(new String[0]));
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
        ChooseOption() {
            super("add", "remove");
        }

        ChooseOption(boolean displayCancel) {
            super("add", "remove", "cancel");
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What option would you like to perform?\n" +
                    ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
            if (choice.equals("add")) {
                String kitName = context.getSessionData("kit").toString();
                return new AddKitItemConversation(new ChooseOption(true), kitName).startConversation();
            }
            return new RemoveMaterial();
        }
    }

    private class RemoveMaterial extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What material would you like to remove?";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String message) {
            Material material = Material.getMaterial(message.toUpperCase());

            if (material == null) {
                ParkourConversation.sendErrorMessage(context, "This isn't a valid Material.");
                return this;
            }

            String kitName = (String) context.getSessionData("kit");

            if (!Parkour.getParkourConfig().getParkourKitData().contains("ParkourKit." + kitName + "." + material.name())) {
                ParkourConversation.sendErrorMessage(context, "This Material hasn't got an entry");
                return this;
            }

            context.getForWhom().sendRawMessage(Static.getParkourString() + material.name() + " removed from " + kitName);
            Parkour.getParkourConfig().getParkourKitData().set("ParkourKit." + kitName + "." + material.name(), null);
            Parkour.getParkourConfig().saveParkourKit();
            ParkourKit.clearMemory(kitName);
            return new ChooseOption(true);
        }
    }
}

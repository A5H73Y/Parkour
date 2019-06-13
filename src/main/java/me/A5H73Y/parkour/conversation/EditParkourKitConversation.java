package me.A5H73Y.parkour.conversation;

import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.conversation.other.AddKitItemConversation;
import me.A5H73Y.parkour.kit.ParkourKit;
import me.A5H73Y.parkour.kit.ParkourKitInfo;
import me.A5H73Y.parkour.utilities.Static;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class EditParkourKitConversation extends ParkourConversation {

    public EditParkourKitConversation(Player player) {
        super(player);
    }

    @Override
    public Prompt getEntryPrompt() {
        return new ChooseParkourKit();
    }

    private class ChooseParkourKit extends FixedSetPrompt {

        ChooseParkourKit() {
            super(ParkourKitInfo.getParkourKitNames().toArray(new String[0]));
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

            return new RemoveMaterial(context);
        }
    }

    private class RemoveMaterial extends FixedSetPrompt {

        // TODO super ugly, but can't find a better way?
        RemoveMaterial(ConversationContext context) {
            super(ParkourKitInfo.getParkourKitContents(context.getSessionData("kit").toString()).toArray(new String[0]));
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Material would you like to remove?\n" +
                    ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String material) {
            String kitName = (String) context.getSessionData("kit");

            Parkour.getParkourConfig().getParkourKitData().set("ParkourKit." + kitName + "." + material, null);
            Parkour.getParkourConfig().saveParkourKit();
            ParkourKit.clearMemory(kitName);
            context.getForWhom().sendRawMessage(Static.getParkourString() + material + " removed from " + kitName);
            return new ChooseOption(true);
        }
    }
}

package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Parkour;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class ParkourKitConversation extends StringPrompt {

	private String[] actionTypes = {"death", "finish", "climb", "launch", "bounce", "speed", "norun", "nopotion"};

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

		context.setSessionData("name", message);
		return new chooseMaterial();
	}

	private class chooseMaterial extends StringPrompt {
		@Override
		public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Material do you want to choose?";
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String message) {
            Material material = Material.getMaterial(message.toUpperCase());

			if (material == null){
				ParkourConversation.sendErrorMessage(context, message.toUpperCase() + " is not a valid Material");
				return this;
			}

			context.setSessionData("material", material.name());
			return new chooseAction();
		}		
	}

    private class chooseAction extends FixedSetPrompt {

        chooseAction(){
            super(actionTypes);
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Action should " + context.getSessionData("material") + " do?\n" +
                    ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
            context.setSessionData("action", choice);
            return new ProcessComplete();
        }
    }

	private class ProcessComplete extends BooleanPrompt {

        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " ParkourKit saved! Would you like to add another Material?\n" +
            ChatColor.GREEN + "[yes, no]";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean input) {
            String name = context.getSessionData("name").toString();
            String material = context.getSessionData("material").toString();
            String action = context.getSessionData("action").toString();

            Parkour.getParkourConfig().getParkourKitData()
                    .set("ParkourKit." + name + "." + material + ".Action", action);
            Parkour.getParkourConfig().saveParkourKit();

            if (!input) {
                return Prompt.END_OF_CONVERSATION;
            }

            return new chooseMaterial();
        }
    }
}

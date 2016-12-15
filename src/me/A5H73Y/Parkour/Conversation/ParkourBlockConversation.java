package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Parkour;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class ParkourBlockConversation extends StringPrompt {

	private String[] blockTypes = {"Death", "Finish", "Climb", "Launch", "Speed", "Repulse"};

	public String getPromptText(ConversationContext context) {
		return ChatColor.LIGHT_PURPLE + " What would you like to name your ParkourBlocks?";
	}

	public Prompt acceptInput(ConversationContext context, String message) {
		if (message.length() == 0) {
			return Prompt.END_OF_CONVERSATION;
		}

		if (Parkour.getParkourConfig().getConfig().contains("ParkourBlocks." + message)){
			ParkourConversation.sendErrorMessage(context, "This ParkourBlock already exists");
			return this;
		}

		context.setSessionData("name", message);
		return new chooseBlock();
	}

	private class chooseBlock extends StringPrompt {
		@Override
		public String getPromptText(ConversationContext context) {
			int stage = getBlockStage(context);
			
			String blockType = blockTypes[stage];
			
			String material = Parkour.getParkourConfig().getConfig().getString("DefaultBlocks." + blockType + ".Material").toUpperCase();

			return ChatColor.LIGHT_PURPLE + " What material do you want for the " + ChatColor.WHITE + blockType + ChatColor.LIGHT_PURPLE + " block?\n "
					+ ChatColor.DARK_GRAY + "Default: " + ChatColor.GRAY + material;
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String message) {

			int stage = getBlockStage(context);
			Material material;

			if (message.equalsIgnoreCase("default")){
				String blockType = blockTypes[stage];
				material = Material.getMaterial(
						Parkour.getParkourConfig().getConfig().getString("DefaultBlocks." + blockType + ".Material").toUpperCase());

			} else {
				material = Material.getMaterial(message.toUpperCase());
			}

			if (material == null){
				ParkourConversation.sendErrorMessage(context, "This is not a valid material");
				return this;
			}

			context.setSessionData(stage, material.name());

			if (stage < blockTypes.length - 1){
				context.setSessionData("stage", stage += 1);
				return this;
			}

			return new ProcessComplete();
		}		
	}

	private class ProcessComplete extends MessagePrompt {
		public String getPromptText(ConversationContext context) {
			String name = context.getSessionData("name").toString().toLowerCase();

			for (int i=0; i < blockTypes.length; i++){
				Parkour.getParkourConfig().getConfig().set("ParkourBlocks." + name + "." + blockTypes[i] + ".Material", context.getSessionData(i));
			}
			Parkour.getPlugin().saveConfig();

			return ChatColor.GREEN + " " + name + ChatColor.LIGHT_PURPLE + " was successfully created!";
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext context) {
			return Prompt.END_OF_CONVERSATION;
		}
	}

	private int getBlockStage(ConversationContext context){
		return context.getSessionData("stage") == null ? 0 : (Integer) context.getSessionData("stage");
	}

}

package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Parkour;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class CoursePrizeConversation extends FixedSetPrompt {

	CoursePrizeConversation(){
		super("Material", "Command", "Cancel");
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return ChatColor.LIGHT_PURPLE + " What type of prize would you like to set? " + formatFixedSet();
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
		if (choice.equals("Cancel"))
			return Prompt.END_OF_CONVERSATION;

		if (choice.equalsIgnoreCase("Material"))
			return new ChooseBlock();
		
		if (choice.equalsIgnoreCase("Command"))
			return new ChooseCommand();

		return null;
	}

	/* BEGIN MATERIAL PRIZE */
	private class ChooseBlock extends StringPrompt {
		@Override
		public String getPromptText(ConversationContext context) {
			return ChatColor.LIGHT_PURPLE + " What Material do you want to reward the player with?";
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String message) {
			Material material = Material.getMaterial(message.toUpperCase());
			if (material == null){
				Conversation.sendErrorMessage(context, "This is not a valid material");
				return this;
			}

			context.setSessionData("material", message.toUpperCase());
			
			return new ChooseAmount();
		}		
	}

	private class ChooseAmount extends NumericPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			return ChatColor.LIGHT_PURPLE + " How many would you like to reward the player with?";
		}

		@Override
        protected boolean isNumberValid(ConversationContext context, Number input) {
            return input.intValue() > 0 && input.intValue() <= 255;
        }
		
		@Override
        protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
            return "Amount must be between 1 and 255.";
        }
		
		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, Number amount) {
			context.setSessionData("amount", amount.intValue());

			return new MaterialProcessComplete();
		}
		
	}
	
	private class MaterialProcessComplete extends MessagePrompt {
		public String getPromptText(ConversationContext context) {
			return " The Material prize for " + ChatColor.DARK_AQUA + context.getSessionData("courseName") + ChatColor.WHITE + " was set to " + ChatColor.AQUA + context.getSessionData("amount") + " " + context.getSessionData("material");
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext context) {
			return Prompt.END_OF_CONVERSATION;
		}
	}

	/* BEGIN COMMAND PRIZE */
	private class ChooseCommand extends StringPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			context.getForWhom().sendRawMessage("Remember you can include %PLAYER% to apply it to that player. Example '/pa kick %PLAYER%'");
			return ChatColor.LIGHT_PURPLE + " What would you like the Command prize to be?";
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String message) {
			String command = message.replace("/", "");
			context.setSessionData("command", command);
			
			return new ChooseRunNow();
		}
	}
	
	private class ChooseRunNow extends BooleanPrompt {

		@Override
		public String getPromptText(ConversationContext arg0) {
			return ChatColor.LIGHT_PURPLE + " Would you like to run this command now? (to test)";
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, boolean runNow) {
			if (runNow)
				Parkour.getPlugin().getServer().dispatchCommand(
						Parkour.getPlugin().getServer().getConsoleSender(), 
						context.getSessionData("command").toString().replace("%PLAYER%", context.getSessionData("playerName").toString()));
			
			return new CommandProcessComplete();
		}
		
	}
	
	private class CommandProcessComplete extends MessagePrompt {
		public String getPromptText(ConversationContext context) {
			return " The Command prize for " + ChatColor.DARK_AQUA + context.getSessionData("courseName") + ChatColor.WHITE + " was set to /" + ChatColor.AQUA + context.getSessionData("command");
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext context) {
			return Prompt.END_OF_CONVERSATION;
		}
	}
	
}

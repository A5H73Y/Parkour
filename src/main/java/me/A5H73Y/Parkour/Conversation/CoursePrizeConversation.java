package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Parkour;

import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class CoursePrizeConversation extends FixedSetPrompt {

	CoursePrizeConversation(){
		super("material", "command", "xp");
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return ChatColor.LIGHT_PURPLE + " What type of prize would you like to set?\n" 
				+ ChatColor.GREEN + formatFixedSet();
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
		if (choice.equalsIgnoreCase("material"))
			return new ChooseBlock();

		if (choice.equalsIgnoreCase("command"))
			return new ChooseCommand();

		if (choice.equalsIgnoreCase("xp"))
			return new ChooseXP();

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
			Material material = Utils.getMaterial(message.toUpperCase());
			if (material == null){
				ParkourConversation.sendErrorMessage(context, "This is not a valid material");
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
			Parkour.getParkourConfig().getCourseData().set(context.getSessionData("courseName") + ".Prize.Material", context.getSessionData("material"));
			Parkour.getParkourConfig().getCourseData().set(context.getSessionData("courseName") + ".Prize.Amount", context.getSessionData("amount"));
			Parkour.getParkourConfig().saveCourses();

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
			context.getForWhom().sendRawMessage(ChatColor.GRAY + "Remember you can include %PLAYER% to apply it to that player.\nExample: 'kick %PLAYER%'");
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
			return ChatColor.LIGHT_PURPLE + " Would you like to run this command now? (to test)\n" +
            ChatColor.GREEN + "[yes, no]";
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, boolean runNow) {
			if (runNow)
				Parkour.getPlugin().getServer().dispatchCommand(
						Parkour.getPlugin().getServer().getConsoleSender(), 
						context.getSessionData("command").toString()
						.replace("%PLAYER%", context.getSessionData("playerName").toString()));

			return new CommandProcessComplete();
		}

	}

	private class CommandProcessComplete extends MessagePrompt {
		public String getPromptText(ConversationContext context) {
			Parkour.getParkourConfig().getCourseData().set(context.getSessionData("courseName") + ".Prize.CMD", context.getSessionData("command"));
			Parkour.getParkourConfig().saveCourses();

			return " The Command prize for " + ChatColor.DARK_AQUA + context.getSessionData("courseName") + ChatColor.WHITE + " was set to /" + ChatColor.AQUA + context.getSessionData("command");
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext context) {
			return Prompt.END_OF_CONVERSATION;
		}
	}

	/* BEGIN XP PRIZE */
	private class ChooseXP extends NumericPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			return ChatColor.LIGHT_PURPLE + " How much XP would you like to reward the player with?";
		}

		@Override
		protected boolean isNumberValid(ConversationContext context, Number input) {
			return input.intValue() > 0 && input.intValue() <= 10000;
		}

		@Override
		protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
			return "Amount must be between 1 and 10,000.";
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, Number amount) {
			context.setSessionData("amount", amount.intValue());

			return new XPProcessComplete();
		}
	}

	private class XPProcessComplete extends MessagePrompt {
		public String getPromptText(ConversationContext context) {
			Parkour.getParkourConfig().getCourseData().set(context.getSessionData("courseName") + ".Prize.XP", context.getSessionData("amount"));
			Parkour.getParkourConfig().saveCourses();

			return " The XP prize for " + ChatColor.DARK_AQUA + context.getSessionData("courseName") + ChatColor.WHITE + " was set to " + ChatColor.AQUA + context.getSessionData("amount");
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext context) {
			return Prompt.END_OF_CONVERSATION;
		}
	}

}

package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class LeaderboardConversation extends StringPrompt {

	@Override
	public String getPromptText(ConversationContext context) {
		return ChatColor.LIGHT_PURPLE + " Which course would you to view?";
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String message) {
		if (!Static.getCourses().contains(message.toLowerCase())){
			ParkourConversation.sendErrorMessage(context, "This course does not exist");
			return this;
		}

		context.setSessionData("course", message.toLowerCase());
		return new ChooseType();
	}		

	private class ChooseType extends FixedSetPrompt {

		ChooseType(){
			super("personal", "global");
		}

		@Override
		public String getPromptText(ConversationContext context) {
			return ChatColor.LIGHT_PURPLE + " What type of leaderboards would you like to see?            " + formatFixedSet();
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
			/* not needed as 'cancel' will do the job
			if (choice.equals("Cancel"))
				return Prompt.END_OF_CONVERSATION;
			*/

			context.setSessionData("type", choice);

			return new ChooseAmount();
		}
	}

	private class ChooseAmount extends NumericPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			return ChatColor.LIGHT_PURPLE + " How many results would you like?";
		}

		@Override
		protected boolean isNumberValid(ConversationContext context, Number input) {
			return input.intValue() > 0 && input.intValue() <= 20;
		}

		@Override
		protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
			return "Amount must be between 1 and 20.";
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, Number amount) {
			context.setSessionData("amount", amount.intValue());

			return new DisplayLeaderboards();
		}
	}

	private class DisplayLeaderboards extends MessagePrompt {
		public String getPromptText(ConversationContext context) {
			final String leaderboardType = (String) context.getSessionData("type");
			final String courseName = (String) context.getSessionData("course");
			final Integer amount = (Integer) context.getSessionData("amount");
			final Player player = Bukkit.getPlayer((String) context.getSessionData("playerName"));

			Bukkit.getScheduler().runTaskLaterAsynchronously(Parkour.getPlugin(), new Runnable() {
				@Override
				public void run() {
					if (leaderboardType.equals("personal")) {
						Utils.displayLeaderboard(DatabaseMethods.getTopPlayerCourseResults(player.getName(), courseName, amount), player);
					} else if (leaderboardType.equals("global")) {
						Utils.displayLeaderboard(DatabaseMethods.getTopCourseResults(courseName, amount), player);
					}
				}
			}, 3);

			return "";
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext context) {
			return Prompt.END_OF_CONVERSATION;
		}
	}
}

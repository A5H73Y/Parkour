package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Course.CourseInfo;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

public class ParkourModeConversation extends FixedSetPrompt {

	ParkourModeConversation() {
		super("freedom", "darkness", "drunk", "speedy", "moon", "dropper", "rockets", "none");
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return ChatColor.LIGHT_PURPLE + " What type of ParkourMode would you like to set?\n" 
				+ ChatColor.GREEN + formatFixedSet();
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
		String courseName = (String) context.getSessionData("courseName");

		CourseInfo.setMode(courseName, choice);

		context.getForWhom().sendRawMessage(Utils.getTranslation("Parkour.SetMode")
				.replace("%COURSE%", courseName)
				.replace("%MODE%", choice));

		return Prompt.END_OF_CONVERSATION;
	}
}

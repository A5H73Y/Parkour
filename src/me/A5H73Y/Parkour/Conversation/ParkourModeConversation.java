package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

public class ParkourModeConversation extends FixedSetPrompt {

	ParkourModeConversation(){
		super("freedom", "darkness", "drunk");
	}

	@Override
	public String getPromptText(ConversationContext context) {
		return ChatColor.LIGHT_PURPLE + " What type of ParkourMode would you like to set?\n" 
				+ ChatColor.GREEN + formatFixedSet();
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
		String courseName = (String) context.getSessionData("courseName");
		
		Parkour.getParkourConfig().getCourseData().set(courseName + ".Mode", choice);
		Parkour.getParkourConfig().saveCourses();
		
		context.getForWhom().sendRawMessage(Utils.getTranslation("Parkour.SetMode").replace("%COURSE%", courseName).replace("%MODE%", choice));
		
		return Prompt.END_OF_CONVERSATION;
	}
}

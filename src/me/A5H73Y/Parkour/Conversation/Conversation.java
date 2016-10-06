package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Player.PlayerMethods;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class Conversation implements ConversationAbandonedListener {

	public enum ConversationType {
		PARKOURBLOCKS,
		COURSEPRIZE
	}
	
	private ConversationFactory conversationFactory;
	
	public Conversation(Player player, ConversationType conversationType){
		
		conversationFactory = new ConversationFactory(Parkour.getPlugin())
		.withModality(true)
		.withEscapeSequence("/quit")
		.withTimeout(30)
		.withLocalEcho(true)
		.thatExcludesNonPlayersWithMessage("This is only possible in game, sorry.")
		.withPrefix(new NullConversationPrefix())
		.addConversationAbandonedListener(this)
		.withFirstPrompt(getEntryPrompt(conversationType));

		org.bukkit.conversations.Conversation convo = conversationFactory.buildConversation(player);
		convo.getContext().setSessionData("playerName", player.getName());
		convo.getContext().setSessionData("courseName", PlayerMethods.getSelected(player.getName()));
		convo.begin();
	}
	
	private Prompt getEntryPrompt(ConversationType type){
		switch (type){
		case PARKOURBLOCKS:
				return new ParkourBlockConversation();
		case COURSEPRIZE:
				return new CoursePrizeConversation();
		default:
			return new ParkourBlockConversation();
		}
	}

	@Override
	public void conversationAbandoned(ConversationAbandonedEvent event) {
		if (!event.gracefulExit())
			event.getContext().getForWhom().sendRawMessage("Conversation Aborted...");
	}

	public static void sendErrorMessage(ConversationContext context, String message) {
		context.getForWhom().sendRawMessage(ChatColor.RED + message + ". Please try again...");
	}
}

package me.A5H73Y.Parkour.Conversation;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Enums.ConversationType;
import me.A5H73Y.Parkour.Utilities.Static;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class ParkourConversation implements ConversationAbandonedListener {

	private ConversationFactory conversationFactory;

	private String courseName;
	private Player player;

	public ParkourConversation(Player player, ConversationType conversationType){
		this.player = player;

		conversationFactory = new ConversationFactory(Parkour.getPlugin())
		.withModality(true)
		.withEscapeSequence("cancel")
		.withTimeout(30)
		.withLocalEcho(true)
		.thatExcludesNonPlayersWithMessage("This is only possible in game, sorry.")
		.withPrefix(new NullConversationPrefix())
		.addConversationAbandonedListener(this)
		.withFirstPrompt(getEntryPrompt(conversationType, player));
	}

	private Prompt getEntryPrompt(ConversationType type, Player player) {
        player.sendMessage(ChatColor.GRAY + "Nope: Enter 'cancel' to quit the conversation.");
		switch (type){
		case PARKOURBLOCKS:
			return new ParkourBlockConversation();
		case COURSEPRIZE:
			return new CoursePrizeConversation();
		case LEADERBOARD:
			return new LeaderboardConversation();
		case PARKOURMODE:
			return new ParkourModeConversation();
		default:
			player.sendMessage(ChatColor.RED + "Something went wrong.");
			return null;
		}
	}

	@Override
	public void conversationAbandoned(ConversationAbandonedEvent event) {
		if (!event.gracefulExit())
			event.getContext().getForWhom().sendRawMessage(Static.getParkourString() + "Conversation aborted...");
	}

	public static void sendErrorMessage(ConversationContext context, String message) {
		context.getForWhom().sendRawMessage(ChatColor.RED + message + ". Please try again...");
	}

	public ParkourConversation withCourseName(String courseName) {
		this.courseName = courseName;
		return this;
	}

	public void begin() {
		Conversation convo = conversationFactory.buildConversation(player);
		convo.getContext().setSessionData("playerName", player.getName());
		if (courseName != null)
			convo.getContext().setSessionData("courseName", courseName);

		convo.begin();
	}
}

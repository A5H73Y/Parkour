package io.github.a5h73y.parkour.conversation.other;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.ParkourConversation;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingleQuestionConversation extends ParkourConversation {

	private final Consumer<String> valueSetter;

	public SingleQuestionConversation(Conversable conversable,
	                                  Consumer<String> valueSetter) {
		super(conversable);
		this.valueSetter = valueSetter;
	}

	@Override
	public Prompt getEntryPrompt() {
		return new InputValue();
	}

	public class InputValue extends StringPrompt {

		@NotNull
		@Override
		public String getPromptText(@NotNull ConversationContext conversationContext) {
			return ChatColor.LIGHT_PURPLE + " What value would you like to set?";
		}

		@Nullable
		@Override
		public Prompt acceptInput(@NotNull ConversationContext conversationContext,
		                          @Nullable String input) {
			Bukkit.getScheduler().runTaskAsynchronously(Parkour.getInstance(),
					() -> valueSetter.accept(input));

			return END_OF_CONVERSATION;
		}
	}
}

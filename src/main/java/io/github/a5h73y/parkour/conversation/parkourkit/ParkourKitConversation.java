package io.github.a5h73y.parkour.conversation.parkourkit;

import io.github.a5h73y.parkour.conversation.other.ParkourConversation;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ParkourKitConversation extends ParkourConversation {

	protected static final String PARKOUR_KIT_NAME = "kit";

	protected String kitName;

	/**
	 * Construct a Parkour Conversation.
	 *
	 * @param conversable conversable user
	 */
	protected ParkourKitConversation(@NotNull Conversable conversable) {
		super(conversable);
	}

	protected abstract boolean isProvidedNameValid();

	protected abstract Prompt getProvideValidKitPrompt();

	protected abstract Prompt getFirstPrompt();

	@Override
	public Prompt getEntryPrompt() {
		return new CheckKitNameProvidedPrompt();
	}

	@Override
	public void preBeginStep(Conversation conversation) {
		if (this.kitName != null) {
			if (isProvidedNameValid()) {
				conversation.getContext().setSessionData(PARKOUR_KIT_NAME, this.kitName);
			} else {
				ParkourConversation.sendErrorMessage(conversation.getContext(),
						"The provided ParkourKit name is invalid.");
			}
		}
	}

	/**
	 * Interim prompt to confirm their ParkourKit name was valid / accepted.
	 */
	private class CheckKitNameProvidedPrompt extends MessagePrompt {

		@Nullable
		@Override
		protected Prompt getNextPrompt(@NotNull ConversationContext context) {
			if (context.getSessionData(PARKOUR_KIT_NAME) != null) {
				return getFirstPrompt();
			} else {
				return getProvideValidKitPrompt();
			}
		}

		@NotNull
		@Override
		public String getPromptText(@NotNull ConversationContext context) {
			Object parkourKitName = context.getSessionData(PARKOUR_KIT_NAME);
			if (parkourKitName != null) {
				context.getForWhom().sendRawMessage(TranslationUtils.getPluginPrefix()
						+ "Using ParkourKit name: " + parkourKitName);
			}
			return "";
		}
	}

	/**
	 * Provide kit name to bypass name input prompts.
	 * @param kitName kit name
	 * @return conversation instance
	 */
	public ParkourKitConversation withKitName(@Nullable String kitName) {
		if (kitName != null) {
			this.kitName = kitName.toLowerCase();
		}
		return this;
	}
}

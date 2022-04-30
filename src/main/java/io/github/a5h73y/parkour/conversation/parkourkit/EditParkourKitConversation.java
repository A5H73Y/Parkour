package io.github.a5h73y.parkour.conversation.parkourkit;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;

public class EditParkourKitConversation extends ParkourKitConversation {

    /**
     * Construct a Parkour Conversation.
     *
     * @param conversable conversable user
     */
    public EditParkourKitConversation(@NotNull Conversable conversable) {
        super(conversable);
    }

    protected boolean isProvidedNameValid() {
        return this.kitName != null
                && !this.kitName.isEmpty()
                && Parkour.getParkourKitConfig().doesParkourKitExist(this.kitName);
    }

    @Override
    protected Prompt getProvideValidKitPrompt() {
        return new ChooseKitToEditPrompt();
    }

    @Override
    protected Prompt getFirstPrompt() {
        return new ChooseAddRemovePrompt();
    }

    private class ChooseKitToEditPrompt extends FixedSetPrompt {

        ChooseKitToEditPrompt() {
            super(Parkour.getParkourKitConfig().getAllParkourKitNames().toArray(new String[0]));
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What ParkourKit would you like to edit?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String choice) {
            context.setSessionData(PARKOUR_KIT_NAME, choice);
            return new ChooseAddRemovePrompt();
        }
    }

    private class ChooseAddRemovePrompt extends FixedSetPrompt {
        ChooseAddRemovePrompt() {
            super("add", "remove");
        }

        ChooseAddRemovePrompt(boolean displayCancel) {
            super("add", "remove", "cancel");
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What option would you like to perform?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String choice) {
            if (choice.equals("add")) {
                String kitName = context.getSessionData(PARKOUR_KIT_NAME).toString();
                return new AddKitItemConversation(new ChooseAddRemovePrompt(true), kitName).startConversation();
            } else {
                return new RemoveMaterial(context);
            }
        }
    }

    private class RemoveMaterial extends FixedSetPrompt {

        RemoveMaterial(ConversationContext context) {
            super(Parkour.getParkourKitConfig().getParkourKitMaterials(context.getSessionData(PARKOUR_KIT_NAME).toString())
                    .toArray(new String[0]));
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Material would you like to remove?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
            return super.isInputValid(context, input.toUpperCase());
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String material) {
            String kitName = (String) context.getSessionData(PARKOUR_KIT_NAME);
            Parkour.getParkourKitConfig().removeMaterial(kitName, material);
            Parkour.getInstance().getParkourKitManager().clearCache(kitName);
            for (String courseName : Parkour.getParkourKitConfig().getDependentCourses(kitName)) {
                Parkour.getInstance().getCourseManager().clearCache(courseName);
            }
            context.getForWhom().sendRawMessage(TranslationUtils.getPluginPrefix() + material + " removed from " + kitName);
            return new ChooseAddRemovePrompt(true);
        }
    }
}

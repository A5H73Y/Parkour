package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.other.AddKitItemConversation;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.type.kit.ParkourKitInfo;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;

public class EditParkourKitConversation extends ParkourConversation {

    public EditParkourKitConversation(Conversable conversable) {
        super(conversable);
    }

    @Override
    public Prompt getEntryPrompt() {
        return new ChooseParkourKit();
    }

    private class ChooseParkourKit extends FixedSetPrompt {

        ChooseParkourKit() {
            super(ParkourKitInfo.getAllParkourKitNames().toArray(new String[0]));
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
            context.setSessionData("kit", choice);
            return new ChooseOption();
        }
    }

    private class ChooseOption extends FixedSetPrompt {
        ChooseOption() {
            super("add", "remove");
        }

        ChooseOption(boolean displayCancel) {
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
                String kitName = context.getSessionData("kit").toString();
                return new AddKitItemConversation(new ChooseOption(true), kitName).startConversation();
            }

            return new RemoveMaterial(context);
        }
    }

    private class RemoveMaterial extends FixedSetPrompt {

        // TODO super ugly, but can't find a better way?
        RemoveMaterial(ConversationContext context) {
            super(ParkourKitInfo.getParkourKitMaterials(context.getSessionData("kit").toString()).toArray(new String[0]));
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Material would you like to remove?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String material) {
            String kitName = (String) context.getSessionData("kit");

            Parkour.getConfig(ConfigType.PARKOURKIT).set("ParkourKit." + kitName + "." + material, null);
            Parkour.getConfig(ConfigType.PARKOURKIT).save();
            Parkour.getInstance().getParkourKitManager().clearCache(kitName);
            for (String courseName : ParkourKitInfo.getDependentCourses(kitName)) {
                Parkour.getInstance().getCourseManager().clearCache(courseName);
            }
            context.getForWhom().sendRawMessage(Parkour.getPrefix() + material + " removed from " + kitName);
            return new ChooseOption(true);
        }
    }
}

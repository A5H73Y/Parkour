package me.A5H73Y.Parkour.Conversation.other;

import me.A5H73Y.Parkour.Conversation.ParkourConversation;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.ParkourKit.ParkourKit;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.*;

import java.util.HashMap;
import java.util.Map;

public class AddKitItemConversation {

    private final String[] actionTypes = {"death", "finish", "climb", "launch", "bounce", "speed", "repulse", "norun", "nopotion"};

    private final static Map<String, Double> strengthDefault = new HashMap<>();
    private final static Map<String, Integer> durationDefault = new HashMap<>();

    private Prompt endingConversation;
    private String kitName;

    static {
        strengthDefault.put("climb", 0.4);
        strengthDefault.put("launch", 1.2);
        strengthDefault.put("bounce", 5.0);
        strengthDefault.put("speed", 5.0);
        strengthDefault.put("repulse", 0.4);
        durationDefault.put("speed", 200);
        durationDefault.put("bounce", 200);
    }

    public AddKitItemConversation(Prompt endingConversation, String kitName) {
        this.endingConversation = endingConversation;
        this.kitName = kitName;
    }

    public StringPrompt startConversation() {
        return new chooseMaterial();
    }

    private class chooseMaterial extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Material do you want to choose?";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String message) {
            Material material = Utils.lookupMaterial(message.toUpperCase());

            if (material == null) {
                ParkourConversation.sendErrorMessage(context, message.toUpperCase() + " is not a valid Material");
                return this;
            }
            if (Parkour.getParkourConfig().getParkourKitData().contains("ParkourKit." + kitName + "." + material.name())) {
                ParkourConversation.sendErrorMessage(context, "You've already used this Material");
                return this;
            }

            context.setSessionData("material", material.name());
            return new chooseAction();
        }
    }

    private class chooseAction extends FixedSetPrompt {

        chooseAction() {
            super(actionTypes);
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Action should " + context.getSessionData("material") + " do?\n" +
                    ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
            context.setSessionData("action", choice);

            if (choice.equals("climb") || choice.equals("launch") || choice.equals("bounce") || choice.equals("speed") || choice.equals("repulse")) {
                return new ChooseStrength();
            } else {
                return new ProcessComplete();
            }
        }
    }

    private class ChooseStrength extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String action = context.getSessionData("action").toString();
            Double defaultValue = strengthDefault.get(action);
            return ChatColor.LIGHT_PURPLE + String.format(" What strength should the action be? (default: %.2f)", defaultValue);
        }

        @Override
        protected boolean isNumberValid(ConversationContext context, Number input) {
            return input.doubleValue() >= 0 && input.doubleValue() <= 10;
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
            return "Amount must be between 0 and 10.";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number amount) {
            context.setSessionData("strength", amount);

            String action = context.getSessionData("action").toString();
            if (action.equals("speed") || action.equals("bounce")) {
                return new ChooseDuration();
            } else {
                return new ProcessComplete();
            }
        }
    }

    private class ChooseDuration extends NumericPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            String action = context.getSessionData("action").toString();
            Integer defaultValue = durationDefault.get(action);
            return ChatColor.LIGHT_PURPLE + String.format(" What duration do you want the action to last? (default: %d)", defaultValue);
        }

        @Override
        protected boolean isNumberValid(ConversationContext context, Number input) {
            return input.intValue() >= 0 && input.intValue() <= 500;
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
            return "Amount must be between 0 and 500.";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number amount) {
            context.setSessionData("duration", amount.intValue());
            return new ProcessComplete();
        }
    }

    private class ProcessComplete extends BooleanPrompt {

        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " ParkourKit saved! Would you like to add another Material?\n" +
                    ChatColor.GREEN + "[yes, no]";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean addAnother) {
            String material = context.getSessionData("material").toString();
            String action = context.getSessionData("action").toString();
            boolean hasStrength = context.getSessionData("strength") != null;
            boolean hasDuration = context.getSessionData("duration") != null;

            FileConfiguration config = Parkour.getParkourConfig().getParkourKitData();
            String path = "ParkourKit." + kitName + "." + material;

            config.set(path + ".Action", action);

            if (hasStrength) {
                config.set(path + ".Strength", context.getSessionData("strength"));
            }
            if (hasDuration) {
                config.set(path + ".Duration", context.getSessionData("duration"));
            }

            Parkour.getParkourConfig().saveParkourKit();

            if (addAnother) {
                context.setSessionData("strength", null);
                context.setSessionData("duration", null);
                return new chooseMaterial();
            }

            context.getForWhom().sendRawMessage(Static.getParkourString() + kitName + " ParkourKit has been successfully saved.");
            ParkourKit.clearMemory(kitName);
            return endingConversation;
        }
    }
}

package io.github.a5h73y.parkour.conversation.other;

import static io.github.a5h73y.parkour.conversation.ParkourConversation.sendErrorMessage;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.type.kit.ParkourKitInfo;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class AddKitItemConversation {

    private static final Map<String, Double> STRENGTH_DEFAULT = new HashMap<>();
    private static final Map<String, Integer> DURATION_DEFAULT = new HashMap<>();

    static {
        STRENGTH_DEFAULT.put("climb", 0.4);
        STRENGTH_DEFAULT.put("launch", 1.2);
        STRENGTH_DEFAULT.put("bounce", 5.0);
        STRENGTH_DEFAULT.put("speed", 5.0);
        STRENGTH_DEFAULT.put("repulse", 0.4);
        DURATION_DEFAULT.put("speed", 200);
        DURATION_DEFAULT.put("bounce", 200);
    }

    private final String[] actionTypes =
            {"death", "finish", "climb", "launch", "bounce", "speed", "repulse", "norun", "nopotion"};
    private final Prompt endingConversation;
    private final String kitName;

    public AddKitItemConversation(Prompt endingConversation, String kitName) {
        this.endingConversation = endingConversation;
        this.kitName = kitName;
    }

    public StringPrompt startConversation() {
        return new ChooseMaterial();
    }

    private class ChooseMaterial extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Material do you want to choose?";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String message) {
            Material material = MaterialUtils.lookupMaterial(message.toUpperCase());

            if (material == null) {
                sendErrorMessage(context, TranslationUtils.getValueTranslation("Error.UnknownMaterial",
                        message.toUpperCase(), false));
                return this;
            }

            if (Parkour.getDefaultConfig().contains("ParkourKit." + kitName + "." + material.name())) {
                sendErrorMessage(context, material.name() + " already exists in this ParkourKit!");
                return this;
            }

            context.setSessionData("material", material.name());
            return new ChooseAction();
        }
    }

    private class ChooseAction extends FixedSetPrompt {

        ChooseAction() {
            super(actionTypes);
        }

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Action should " + context.getSessionData("material") + " do?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
            context.setSessionData("action", choice);

            if (choice.equals("climb") || choice.equals("launch") || choice.equals("bounce") || choice.equals("speed")
                    || choice.equals("repulse")) {
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
            Double defaultValue = STRENGTH_DEFAULT.get(action);
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
            Integer defaultValue = DURATION_DEFAULT.get(action);
            return ChatColor.LIGHT_PURPLE + String.format(
                    " What duration do you want the action to last? (default: %d)", defaultValue);
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
            return ChatColor.LIGHT_PURPLE + " ParkourKit saved! Would you like to add another Material?\n"
                    + ChatColor.GREEN + "[yes, no]";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean addAnother) {
            String material = context.getSessionData("material").toString();
            String action = context.getSessionData("action").toString();
            boolean hasStrength = context.getSessionData("strength") != null;
            boolean hasDuration = context.getSessionData("duration") != null;

            ParkourConfiguration parkourKitConfig = Parkour.getConfig(ConfigType.PARKOURKIT);
            String path = "ParkourKit." + kitName + "." + material;

            parkourKitConfig.set(path + ".Action", action);

            if (hasStrength) {
                parkourKitConfig.set(path + ".Strength", context.getSessionData("strength"));
            }
            if (hasDuration) {
                parkourKitConfig.set(path + ".Duration", context.getSessionData("duration"));
            }

            parkourKitConfig.save();

            if (addAnother) {
                context.setSessionData("strength", null);
                context.setSessionData("duration", null);
                return new ChooseMaterial();
            }

            context.getForWhom().sendRawMessage(Parkour.getPrefix() + kitName + " ParkourKit has been successfully saved.");
            Parkour.getInstance().getParkourKitManager().clearCache(kitName);
            for (String courseName : ParkourKitInfo.getDependentCourses(kitName)) {
                Parkour.getInstance().getCourseManager().clearCache(courseName);
            }
            return endingConversation;
        }
    }
}

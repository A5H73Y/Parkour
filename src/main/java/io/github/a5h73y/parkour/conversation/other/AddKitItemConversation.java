package io.github.a5h73y.parkour.conversation.other;

import static io.github.a5h73y.parkour.configuration.impl.ParkourKitConfig.PARKOUR_KIT_CONFIG_PREFIX;
import static io.github.a5h73y.parkour.conversation.ParkourConversation.sendErrorMessage;
import static io.github.a5h73y.parkour.enums.ActionType.BOUNCE;
import static io.github.a5h73y.parkour.enums.ActionType.CLIMB;
import static io.github.a5h73y.parkour.enums.ActionType.LAUNCH;
import static io.github.a5h73y.parkour.enums.ActionType.REPULSE;
import static io.github.a5h73y.parkour.enums.ActionType.SPEED;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ActionType;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.type.kit.ParkourKitInfo;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Arrays;
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
import org.jetbrains.annotations.NotNull;

public class AddKitItemConversation {

    private static final Map<String, Double> STRENGTH_DEFAULT = new HashMap<>();
    private static final Map<String, Integer> DURATION_DEFAULT = new HashMap<>();

    private static final String ACTION = "action";
    private static final String DURATION = "duration";
    private static final String MATERIAL = "material";
    private static final String STRENGTH = "strength";

    static {
        STRENGTH_DEFAULT.put(CLIMB.getDisplayName(), 0.4);
        STRENGTH_DEFAULT.put(LAUNCH.getDisplayName(), 1.2);
        STRENGTH_DEFAULT.put(BOUNCE.getDisplayName(), 5.0);
        STRENGTH_DEFAULT.put(SPEED.getDisplayName(), 5.0);
        STRENGTH_DEFAULT.put(REPULSE.getDisplayName(), 0.4);
        DURATION_DEFAULT.put(SPEED.getDisplayName(), 200);
        DURATION_DEFAULT.put(BOUNCE.getDisplayName(), 200);
    }

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
        @NotNull
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Material do you want to choose?";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context, String message) {
            Material material = MaterialUtils.lookupMaterial(message.toUpperCase());

            if (material == null) {
                sendErrorMessage(context, TranslationUtils.getValueTranslation("Error.UnknownMaterial",
                        message.toUpperCase(), false));
                return this;
            }

            if (Parkour.getDefaultConfig().contains(PARKOUR_KIT_CONFIG_PREFIX + kitName + "." + material.name())) {
                sendErrorMessage(context, material.name() + " already exists in this ParkourKit!");
                return this;
            }

            context.setSessionData(MATERIAL, material.name());
            return new ChooseAction();
        }
    }

    private class ChooseAction extends FixedSetPrompt {

        ChooseAction() {
            super(Arrays.stream(ActionType.values()).map(ActionType::getDisplayName).toArray(String[]::new));
        }

        @Override
        @NotNull
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What Action should " + context.getSessionData(MATERIAL) + " do?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String choice) {
            context.setSessionData(ACTION, choice);

            if (choice.equals(CLIMB.getDisplayName())
                    || choice.equals(LAUNCH.getDisplayName())
                    || choice.equals(BOUNCE.getDisplayName())
                    || choice.equals(SPEED.getDisplayName())
                    || choice.equals(REPULSE.getDisplayName())) {
                return new ChooseStrength();
            } else {
                return new ProcessComplete();
            }
        }
    }

    private class ChooseStrength extends NumericPrompt {

        @Override
        @NotNull
        public String getPromptText(@NotNull ConversationContext context) {
            String action = context.getSessionData(ACTION).toString();
            Double defaultValue = STRENGTH_DEFAULT.get(action);
            return ChatColor.LIGHT_PURPLE + String.format(" What strength should the action be? (default: %.2f)", defaultValue);
        }

        @Override
        protected boolean isNumberValid(@NotNull ConversationContext context, @NotNull Number input) {
            return input.doubleValue() >= 0 && input.doubleValue() <= 10;
        }

        @Override
        protected String getFailedValidationText(@NotNull ConversationContext context, @NotNull Number invalidInput) {
            return "Amount must be between 0 and 10.";
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull Number amount) {
            context.setSessionData(STRENGTH, amount);

            String action = context.getSessionData(ACTION).toString();
            if (action.equals(SPEED.getDisplayName()) || action.equals(BOUNCE.getDisplayName())) {
                return new ChooseDuration();
            } else {
                return new ProcessComplete();
            }
        }
    }

    private class ChooseDuration extends NumericPrompt {

        @Override
        @NotNull
        public String getPromptText(@NotNull ConversationContext context) {
            String action = context.getSessionData(ACTION).toString();
            Integer defaultValue = DURATION_DEFAULT.get(action);
            return ChatColor.LIGHT_PURPLE + String.format(
                    " What duration do you want the action to last? (default: %d)", defaultValue);
        }

        @Override
        protected boolean isNumberValid(@NotNull ConversationContext context, @NotNull Number input) {
            return input.intValue() >= 0 && input.intValue() <= 500;
        }

        @Override
        protected String getFailedValidationText(@NotNull ConversationContext context, @NotNull Number invalidInput) {
            return "Amount must be between 0 and 500.";
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull Number amount) {
            context.setSessionData(DURATION, amount.intValue());
            return new ProcessComplete();
        }
    }

    private class ProcessComplete extends BooleanPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " ParkourKit saved! Would you like to add another Material?\n"
                    + ChatColor.GREEN + "[yes, no]";
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, boolean addAnother) {
            String material = context.getSessionData(MATERIAL).toString();
            String action = context.getSessionData(ACTION).toString();
            boolean hasStrength = context.getSessionData(STRENGTH) != null;
            boolean hasDuration = context.getSessionData(DURATION) != null;

            ParkourConfiguration parkourKitConfig = Parkour.getConfig(ConfigType.PARKOURKIT);
            String path = PARKOUR_KIT_CONFIG_PREFIX + kitName + "." + material;

            parkourKitConfig.set(path + ".Action", action);

            if (hasStrength) {
                parkourKitConfig.set(path + ".Strength", context.getSessionData(STRENGTH));
            }
            if (hasDuration) {
                parkourKitConfig.set(path + ".Duration", context.getSessionData(DURATION));
            }

            parkourKitConfig.save();

            if (addAnother) {
                context.setSessionData(STRENGTH, null);
                context.setSessionData(DURATION, null);
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

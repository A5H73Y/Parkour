package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.other.ParkourConversation;
import io.github.a5h73y.parkour.type.course.ParkourEventType;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetCourseConversation extends ParkourConversation {

    private static final String SESSION_EVENT_NAME = "eventName";
    public static final List<String> PARKOUR_EVENT_TYPE_NAMES =
            Stream.of(ParkourEventType.values())
                    .map(parkourEventType -> parkourEventType.getConfigEntry().toLowerCase()).collect(Collectors.toList());

    public SetCourseConversation(Conversable conversable) {
        super(conversable);
    }

    @NotNull
    @Override
    public Prompt getEntryPrompt() {
        return new ChooseSetCourseOption();
    }

    private static class ChooseSetCourseOption extends FixedSetPrompt {

        ChooseSetCourseOption() {
            super(Stream.concat(Parkour.getInstance().getCourseSettingsManager().getCourseSettingActions().stream(),
                    Stream.of("message", "command")).toArray(String[]::new));
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What course option would you like to set?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String choice) {
            context.setSessionData("setOption", choice);

            if (choice.equals("message")) {
                return new ChooseCourseMessageOption();
            } else if (choice.equals("command")) {
                return new ChooseCourseCommandOption();
            } else {
                return new SetCourseOptionValue();
            }
        }
    }

    private static class SetCourseOptionValue extends StringPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What value would you like to set?";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context,
                                  @Nullable String input) {

            if (!ValidationUtils.isStringValid(input)) {
                return null;
            }

            String courseName = (String) context.getSessionData(SESSION_COURSE_NAME);
            String setOption = (String) context.getSessionData("setOption");

            Parkour parkour = Parkour.getInstance();
            Bukkit.getScheduler().runTaskAsynchronously(parkour, () ->
                    // for messages to be sent - do it async
                    parkour.getCourseSettingsManager().performAction(
                            (CommandSender) context.getForWhom(), courseName, setOption, input)
            );

            return Prompt.END_OF_CONVERSATION;
        }
    }

    public static class CourseCommandConversation extends ParkourConversation {

        public CourseCommandConversation(Conversable conversable) {
            super(conversable);
        }

        @NotNull
        @Override
        public Prompt getEntryPrompt() {
            return new ChooseCourseCommandOption();
        }
    }

    private static class ChooseCourseCommandOption extends FixedSetPrompt {

        ChooseCourseCommandOption() {
            super(PARKOUR_EVENT_TYPE_NAMES.toArray(new String[0]));
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Which event would you like to add a command to?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String choice) {
            context.setSessionData(SESSION_EVENT_NAME, choice);
            return new ChooseCourseCommandValue();
        }
    }

    private static class ChooseCourseCommandValue extends StringPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What command would you like to set?\n"
                    + ChatColor.GREEN + "Use the placeholder %PLAYER% to insert the Player's name.";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context,
                                  @Nullable String input) {

            String courseName = (String) context.getSessionData(SESSION_COURSE_NAME);
            String eventName = (String) context.getSessionData(SESSION_EVENT_NAME);

            Bukkit.getScheduler().runTaskAsynchronously(Parkour.getInstance(), () ->
                    Parkour.getInstance().getCourseSettingsManager().addCourseEventCommand(
                            (CommandSender) context.getForWhom(), courseName, eventName, input));
            return Prompt.END_OF_CONVERSATION;
        }
    }

    public static class CourseMessageConversation extends ParkourConversation {

        public CourseMessageConversation(Conversable conversable) {
            super(conversable);
        }

        @NotNull
        @Override
        public Prompt getEntryPrompt() {
            return new ChooseCourseMessageOption();
        }
    }

    private static class ChooseCourseMessageOption extends FixedSetPrompt {

        ChooseCourseMessageOption() {
            super(PARKOUR_EVENT_TYPE_NAMES.toArray(new String[0]));
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What message option would you like to set?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String choice) {
            context.setSessionData(SESSION_EVENT_NAME, choice);
            return new ChooseCourseMessageValue();
        }
    }

    private static class ChooseCourseMessageValue extends StringPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What message would you like to set?";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context,
                                  @Nullable String input) {

            String courseName = (String) context.getSessionData(SESSION_COURSE_NAME);
            String eventName = (String) context.getSessionData(SESSION_EVENT_NAME);

            Bukkit.getScheduler().runTaskAsynchronously(Parkour.getInstance(), () ->
                    Parkour.getInstance().getCourseSettingsManager().setCourseEventMessage(
                            (CommandSender) context.getForWhom(), courseName, eventName, input));
            return Prompt.END_OF_CONVERSATION;
        }
    }
}

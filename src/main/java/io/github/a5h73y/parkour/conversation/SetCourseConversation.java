package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.course.ParkourEventType;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.Arrays;
import java.util.Collections;
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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetCourseConversation extends ParkourConversation {

    public static final List<String> SET_COURSE_OPTIONS = Collections.unmodifiableList(
            Arrays.asList("displayname", "creator", "minlevel", "maxdeath", "maxtime", "maxfallticks", "command", "message"));

    public static final List<String> PARKOUR_EVENT_TYPE_NAMES =
            Stream.of(ParkourEventType.values()).map(ParkourEventType::getDisplayName).collect(Collectors.toList());

    public SetCourseConversation(Player player) {
        super(player);
    }

    @NotNull
    @Override
    public Prompt getEntryPrompt() {
        return new ChooseSetCourseOption();
    }

    private static class ChooseSetCourseOption extends FixedSetPrompt {

        ChooseSetCourseOption() {
            super(SET_COURSE_OPTIONS.toArray(new String[0]));
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

            String playerName = (String) context.getSessionData(SESSION_PLAYER_NAME);
            String courseName = (String) context.getSessionData(SESSION_COURSE_NAME);
            String setOption = (String) context.getSessionData("setOption");
            Player player = Bukkit.getPlayer(playerName);

            performAction(player, courseName, setOption, input);

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
            context.setSessionData("setCommandOption", choice);
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
            ParkourEventType type = ParkourEventType.valueOf(context.getSessionData("setCommandOption").toString().toUpperCase());
            CourseConfig.getConfig(courseName).addEventCommand(type, input);
            context.getForWhom().sendRawMessage(TranslationUtils.getPropertySet(
                    type.getConfigEntry() + " command", courseName, "/" + input));
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
            context.setSessionData("setMessageOption", choice);
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
            String messageValue = (String) context.getSessionData("setMessageOption");
            CourseConfig.getConfig(courseName).setEventMessage(messageValue, input);
            context.getForWhom().sendRawMessage(TranslationUtils.getPropertySet(messageValue + " message", courseName, input));
            return Prompt.END_OF_CONVERSATION;
        }
    }

    /**
     * Perform Set Course Command.
     *
     * @param sender command sender
     * @param courseName course name
     * @param setOption option to set
     * @param input input value
     */
    public static void performAction(CommandSender sender, String courseName, String setOption, String input) {
        Parkour parkour = Parkour.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(parkour, () -> {
            switch (setOption) {
                case "creator":
                    parkour.getCourseManager().setCreator(sender, courseName, input);
                    break;

                case "minlevel":
                    parkour.getCourseManager().setMinimumParkourLevel(sender, courseName, input);
                    break;

                case "maxdeath":
                    parkour.getCourseManager().setMaxDeaths(sender, courseName, input);
                    break;

                case "maxtime":
                    parkour.getCourseManager().setMaxTime(sender, courseName, input);
                    break;

                case "maxfallticks":
                    parkour.getCourseManager().setMaxFallTicks(sender, courseName, input);
                    break;

                case "displayname":
                    parkour.getCourseManager().setDisplayName(sender, courseName, input);
                    break;

                default:
                    TranslationUtils.sendInvalidSyntax(sender, "setcourse",
                            "(courseName) [displayname, creator, minlevel, maxdeath, maxtime] [value]");
            }
        });
    }
}

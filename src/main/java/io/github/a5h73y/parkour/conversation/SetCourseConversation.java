package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetCourseConversation extends ParkourConversation {

    public static final List<String> SET_COURSE_OPTIONS =
            Arrays.asList("creator", "minlevel", "maxdeath", "maxtime", "message");

    public static final List<String> MESSAGE_OPTIONS =
            Arrays.asList("join", "leave", "finish", "checkpoint", "checkpointall");

    public SetCourseConversation(Player player) {
        super(player);
    }

    @Override
    public Prompt getEntryPrompt() {
        return new ChooseSetCourseOption();
    }

    private static class ChooseSetCourseOption extends FixedSetPrompt {

        ChooseSetCourseOption() {
            super(SET_COURSE_OPTIONS.toArray(new String[0]));
        }

        @Override
        @NotNull
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
            } else {
                return new SetCourseOptionValue();
            }
        }
    }

    private static class SetCourseOptionValue extends StringPrompt {

        @Override
        @NotNull
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What value would you like to set?";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context,
                                  @Nullable String input) {

            String setOption = (String) context.getSessionData("setOption");

            if (input == null || input.trim().isEmpty()) {
                return null;
            }

            String playerName = (String) context.getSessionData(SESSION_PLAYER_NAME);
            String courseName = (String) context.getSessionData(SESSION_COURSE_NAME);
            Player player = Bukkit.getPlayer(playerName);

            performAction(player, courseName, setOption, input);

            return Prompt.END_OF_CONVERSATION;
        }
    }

    private static class ChooseCourseMessageOption extends FixedSetPrompt {

        ChooseCourseMessageOption() {
            super(MESSAGE_OPTIONS.toArray(new String[0]));
        }

        @Override
        @NotNull
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

        @Override
        @NotNull
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What message would you like to set?";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context,
                                  @Nullable String input) {

            String courseName = (String) context.getSessionData(SESSION_COURSE_NAME);
            String messageValue = (String) context.getSessionData("setMessageOption");
            CourseInfo.setJoinMessage(courseName, messageValue, input);
            return Prompt.END_OF_CONVERSATION;
        }
    }

    public static void performAction(CommandSender sender, String courseName, String setOption, String input) {
        Parkour parkour = Parkour.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(parkour, () -> {
            switch (setOption) {
                case "creator":
                    parkour.getCourseManager().setCreator(sender, courseName, input);
                    break;

                case "minlevel":
                    parkour.getCourseManager().setMinLevel(sender, courseName, input);
                    break;

                case "maxdeath":
                    parkour.getCourseManager().setMaxDeaths(sender, courseName, input);
                    break;

                case "maxtime":
                    parkour.getCourseManager().setMaxTime(sender, courseName, input);
                    break;

                default:
                    TranslationUtils.sendInvalidSyntax(sender, "setcourse", "(courseName) [creator, minlevel, maxdeath, maxtime] [value]");
            }
        });
    }
}

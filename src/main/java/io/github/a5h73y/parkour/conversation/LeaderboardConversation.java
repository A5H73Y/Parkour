package io.github.a5h73y.parkour.conversation;

import io.github.a5h73y.parkour.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaderboardConversation extends ParkourConversation {

    public LeaderboardConversation(Conversable conversable) {
        super(conversable);
    }

    @Override
    public Prompt getEntryPrompt() {
        return new ChooseCourse();
    }

    private static class ChooseCourse extends StringPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " Which course would you like to view?";
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context, String message) {
            if (!Parkour.getInstance().getCourseManager().getCourseNames().contains(message.toLowerCase())) {
                ParkourConversation.sendErrorMessage(context, "This course does not exist");
                return this;
            }

            context.setSessionData(SESSION_COURSE_NAME, message.toLowerCase());
            return new ChooseType();
        }
    }

    private static class ChooseType extends FixedSetPrompt {

        ChooseType() {
            super("personal", "global");
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " What type of leaderboards would you like to see?\n"
                    + ChatColor.GREEN + formatFixedSet();
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context,
                                              @NotNull String choice) {
            context.setSessionData("type", choice);
            return new ChooseAmount();
        }
    }

    private static class ChooseAmount extends NumericPrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            return ChatColor.LIGHT_PURPLE + " How many results would you like?";
        }

        @Override
        protected boolean isNumberValid(@NotNull ConversationContext context,
                                        @NotNull Number input) {
            return input.intValue() > 0 && input.intValue() <= Parkour.getDefaultConfig().getMaximumCoursesCached();
        }

        @Override
        protected String getFailedValidationText(@NotNull ConversationContext context,
                                                 @NotNull Number invalidInput) {
            return "Amount must be between 1 and " + Parkour.getDefaultConfig().getMaximumCoursesCached() + ".";
        }

        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext context, Number amount) {
            context.setSessionData("amount", amount.intValue());
            return new DisplayLeaderboards();
        }
    }

    private static class DisplayLeaderboards extends MessagePrompt {

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext context) {
            final String leaderboardType = (String) context.getSessionData("type");
            final String courseName = (String) context.getSessionData(SESSION_COURSE_NAME);
            final Integer amount = (Integer) context.getSessionData("amount");
            final Player player = Bukkit.getPlayer((String) context.getSessionData(SESSION_PLAYER_NAME));

            Bukkit.getScheduler().runTaskAsynchronously(Parkour.getInstance(), () -> {
                if (leaderboardType.equals("personal")) {
                    Parkour.getInstance().getDatabaseManager().displayTimeEntries(player, courseName,
                            Parkour.getInstance().getDatabaseManager().getTopPlayerCourseResults(player, courseName, amount));
                } else if (leaderboardType.equals("global")) {
                    Parkour.getInstance().getDatabaseManager().displayTimeEntries(player, courseName,
                            Parkour.getInstance().getDatabaseManager().getTopCourseResults(courseName, amount));
                }
            });

            return "";
        }

        @Override
        protected Prompt getNextPrompt(@NotNull ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }
    }
}

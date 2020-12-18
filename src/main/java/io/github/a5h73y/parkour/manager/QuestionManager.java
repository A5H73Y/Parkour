package io.github.a5h73y.parkour.manager;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.QuestionType;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Question Manager.
 * Manage the questions that require confirmation from the player.
 * Usually caused by actions that could change the outcome of the plugin / course.
 */
public class QuestionManager extends AbstractPluginReceiver {

    private final Map<CommandSender, Question> questionMap = new WeakHashMap<>();

    public QuestionManager(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Has the Sender been asked a Question.
     *
     * @param sender command sender
     * @return sender has a outstanding question
     */
    public boolean hasBeenAskedQuestion(CommandSender sender) {
        return questionMap.containsKey(sender);
    }

    /**
     * Submit an answer to the Question.
     *
     * @param sender command sender
     * @param answer question answer
     */
    public void answerQuestion(@NotNull CommandSender sender, @NotNull String answer) {
        if ("yes".equalsIgnoreCase(answer)) {
            questionMap.get(sender).doIt(sender);
            removeQuestion(sender);

        } else if ("no".equalsIgnoreCase(answer)) {
            TranslationUtils.sendMessage(sender, "Question cancelled!");
            removeQuestion(sender);

        } else {
            TranslationUtils.sendTranslation("Error.InvalidQuestionAnswer", sender);
            TranslationUtils.sendTranslation("Error.QuestionAnswerChoices", false, sender);
        }
    }

    public void askDeleteCourseQuestion(CommandSender sender, String courseName) {
        askGenericQuestion(sender, QuestionType.DELETE_COURSE, courseName.toLowerCase());
    }

    /**
     * Ask the Delete Checkpoint Question.
     *
     * @param sender command sender
     * @param courseName course name
     * @param checkpoint checkpoint to delete
     */
    public void askDeleteCheckpointQuestion(CommandSender sender, String courseName, int checkpoint) {
        QuestionType type = QuestionType.DELETE_CHECKPOINT;
        TranslationUtils.sendMessage(sender, String.format(type.getActionSummary(), checkpoint, courseName));
        TranslationUtils.sendMessage(sender, type.getDescription(), false);
        submitQuestion(sender, courseName.toLowerCase(), type);
    }

    public void askDeleteLobbyQuestion(CommandSender sender, String lobbyName) {
        askGenericQuestion(sender, QuestionType.DELETE_LOBBY, lobbyName.toLowerCase());
    }

    public void askDeleteKitQuestion(CommandSender sender, String kitName) {
        askGenericQuestion(sender, QuestionType.DELETE_PARKOUR_KIT, kitName.toLowerCase());
    }

    public void askDeleteAutoStartQuestion(CommandSender sender, String coordinates) {
        askGenericQuestion(sender, QuestionType.DELETE_AUTOSTART, coordinates);
    }

    public void askResetCourseQuestion(CommandSender sender, String courseName) {
        askGenericQuestion(sender, QuestionType.RESET_COURSE, courseName.toLowerCase());
    }

    public void askResetPlayerQuestion(CommandSender sender, String targetName) {
        askGenericQuestion(sender, QuestionType.RESET_PLAYER, targetName);
    }

    public void askResetLeaderboardQuestion(CommandSender sender, String courseName) {
        askGenericQuestion(sender, QuestionType.RESET_LEADERBOARD, courseName.toLowerCase());
    }

    /**
     * Ask the Reset Player Leaderboards Question.
     *
     * @param sender sender
     * @param courseName course name
     * @param targetPlayerName target player name
     */
    public void askResetPlayerLeaderboardQuestion(CommandSender sender, String courseName, String targetPlayerName) {
        QuestionType type = QuestionType.RESET_PLAYER_LEADERBOARD;
        TranslationUtils.sendMessage(sender, String.format(type.getActionSummary(), targetPlayerName, courseName));
        TranslationUtils.sendMessage(sender, type.getDescription(), false);
        submitQuestion(sender, targetPlayerName + ";" + courseName.toLowerCase(), type);
    }

    public void askResetPrizeQuestion(CommandSender sender, String courseName) {
        askGenericQuestion(sender, QuestionType.RESET_PRIZES, courseName.toLowerCase());
    }

    public void removeQuestion(CommandSender sender) {
        questionMap.remove(sender);
    }

    private void askGenericQuestion(CommandSender sender, QuestionType questionType, String value) {
        TranslationUtils.sendMessage(sender, String.format(questionType.getActionSummary(), value));
        TranslationUtils.sendMessage(sender, questionType.getDescription(), false);
        submitQuestion(sender, value, questionType);
    }

    private void submitQuestion(CommandSender sender, String argument, QuestionType type) {
        TranslationUtils.sendTranslation("Parkour.Question", false, sender);
        questionMap.put(sender, new Question(type, argument));
    }

    /**
     * The Question Model.
     */
    private static class Question {

        private final QuestionType type;
        private final String argument;

        public Question(QuestionType type, String argument) {
            this.type = type;
            this.argument = argument;
        }

        protected void doIt(CommandSender sender) {
            this.type.confirm(sender, this.argument);
        }

        public QuestionType getType() {
            return type;
        }

        public String getArgument() {
            return argument;
        }
    }
}

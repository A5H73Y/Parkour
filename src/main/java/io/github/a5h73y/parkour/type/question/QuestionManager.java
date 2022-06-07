package io.github.a5h73y.parkour.type.question;

import io.github.a5h73y.parkour.Parkour;
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

    public static final String YES = "yes";
    public static final String NO = "no";

    private final Map<CommandSender, Question> questionMap = new WeakHashMap<>();

    public QuestionManager(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Has the Sender been asked a Question.
     *
     * @param commandSender command sender
     * @return sender has an outstanding question
     */
    public boolean hasBeenAskedQuestion(CommandSender commandSender) {
        return questionMap.containsKey(commandSender);
    }

    public boolean hasBeenAskedQuestion(CommandSender commandSender, QuestionType questionType) {
        return hasBeenAskedQuestion(commandSender)
                && questionMap.get(commandSender).getType().equals(questionType);
    }

    /**
     * Submit an answer to the Question.
     *
     * @param commandSender command sender
     * @param answer question answer
     */
    public void answerQuestion(@NotNull CommandSender commandSender, @NotNull String answer) {
        if (YES.equalsIgnoreCase(answer)) {
            questionMap.get(commandSender).performAction(commandSender);
            removeQuestion(commandSender);

        } else if (NO.equalsIgnoreCase(answer)) {
            TranslationUtils.sendMessage(commandSender, "Question cancelled!");
            removeQuestion(commandSender);

        } else {
            TranslationUtils.sendTranslation("Error.InvalidQuestionAnswer", commandSender);
            TranslationUtils.sendTranslation("Error.QuestionAnswerChoices", false, commandSender);
        }
    }

    public void askDeleteCourseQuestion(CommandSender commandSender, String courseName) {
        askGenericQuestion(commandSender, QuestionType.DELETE_COURSE, courseName.toLowerCase());
    }

    /**
     * Ask the "Delete Checkpoint" Question.
     *
     * @param commandSender command sender
     * @param courseName course name
     * @param checkpoint checkpoint to delete
     */
    public void askDeleteCheckpointQuestion(CommandSender commandSender, String courseName, int checkpoint) {
        QuestionType type = QuestionType.DELETE_CHECKPOINT;
        TranslationUtils.sendMessage(commandSender, String.format(type.getActionSummary(), checkpoint, courseName));
        TranslationUtils.sendMessage(commandSender, type.getDescription(), false);
        submitQuestion(commandSender, courseName.toLowerCase(), type);
    }

    public void askDeleteLobbyQuestion(CommandSender commandSender, String lobbyName) {
        askGenericQuestion(commandSender, QuestionType.DELETE_LOBBY, lobbyName.toLowerCase());
    }

    public void askDeleteKitQuestion(CommandSender commandSender, String kitName) {
        askGenericQuestion(commandSender, QuestionType.DELETE_PARKOUR_KIT, kitName.toLowerCase());
    }

    public void askDeleteParkourRank(CommandSender commandSender, String parkourLevel) {
        askGenericQuestion(commandSender, QuestionType.DELETE_PARKOUR_RANK, parkourLevel);
    }

    public void askResetCourseQuestion(CommandSender commandSender, String courseName) {
        askGenericQuestion(commandSender, QuestionType.RESET_COURSE, courseName.toLowerCase());
    }

    public void askResetPlayerQuestion(CommandSender commandSender, String targetName) {
        askGenericQuestion(commandSender, QuestionType.RESET_PLAYER, targetName);
    }

    public void askResetLeaderboardQuestion(CommandSender commandSender, String courseName) {
        askGenericQuestion(commandSender, QuestionType.RESET_LEADERBOARD, courseName.toLowerCase());
    }

    /**
     * Ask the "Reset Player Leaderboards" Question.
     *
     * @param commandSender command sender
     * @param courseName course name
     * @param targetPlayerName target player name
     */
    public void askResetPlayerLeaderboardQuestion(CommandSender commandSender, String courseName, String targetPlayerName) {
        QuestionType type = QuestionType.RESET_PLAYER_LEADERBOARD;
        TranslationUtils.sendMessage(commandSender, String.format(type.getActionSummary(), targetPlayerName, courseName));
        TranslationUtils.sendMessage(commandSender, type.getDescription(), false);
        submitQuestion(commandSender, targetPlayerName + ";" + courseName.toLowerCase(), type);
    }

    public void askResetPrizeQuestion(CommandSender commandSender, String courseName) {
        askGenericQuestion(commandSender, QuestionType.RESET_PRIZES, courseName.toLowerCase());
    }

    /**
     * Ask the "Restart Progress" Question..
     *
     * @param commandSender command sender
     * @param courseName course name
     */
    public void askRestartProgressQuestion(CommandSender commandSender, String courseName) {
        QuestionType restart = QuestionType.RESTART_COURSE;
        TranslationUtils.sendMessage(commandSender, String.format(restart.getActionSummary(), courseName.toLowerCase()));
        TranslationUtils.sendMessage(commandSender, restart.getDescription(), false);
        TranslationUtils.sendTranslation("ParkourTool.RestartConfirmation", false, commandSender);
        questionMap.put(commandSender, new Question(restart, courseName.toLowerCase()));
    }

    public void removeQuestion(CommandSender commandSender) {
        questionMap.remove(commandSender);
    }

    private void askGenericQuestion(CommandSender commandSender, QuestionType questionType, String value) {
        TranslationUtils.sendMessage(commandSender, String.format(questionType.getActionSummary(), value));
        TranslationUtils.sendMessage(commandSender, questionType.getDescription(), false);
        submitQuestion(commandSender, value, questionType);
    }

    private void submitQuestion(CommandSender commandSender, String argument, QuestionType type) {
        TranslationUtils.sendTranslation("Parkour.Question", false, commandSender);
        questionMap.put(commandSender, new Question(type, argument));
    }

    public void askDeleteLeaderboardRow(CommandSender commandSender, String argument, String detail) {
        QuestionType type = QuestionType.DELETE_LEADERBOARD_ROW;
        TranslationUtils.sendMessage(commandSender, String.format(type.getActionSummary(), detail));
        TranslationUtils.sendMessage(commandSender, type.getDescription(), false);
        submitQuestion(commandSender, argument + ";" + detail, type);
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

        protected void performAction(CommandSender commandSender) {
            this.type.confirm(commandSender, this.argument);
        }

        public QuestionType getType() {
            return type;
        }

        public String getArgument() {
            return argument;
        }
    }
}

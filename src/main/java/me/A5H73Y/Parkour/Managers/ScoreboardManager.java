package me.A5H73Y.Parkour.Managers;

import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.TimeObject;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardManager {

    // For some reason the Scoreboard API is so stupid you have to use a blank string to identify the objective.
    // So each objective has a unique ChatColor for identifying it, which will be overwritten anyway
    private final String COURSE_NAME = ChatColor.AQUA.toString();
    private final String BEST_TIME_EVER = ChatColor.BLACK.toString();
    private final String BEST_TIME_EVER_NAME = ChatColor.BLUE.toString();
    private final String BEST_TIME_EVER_ME = ChatColor.DARK_AQUA.toString();
    private final String CURRENT_TIME = ChatColor.DARK_BLUE.toString();

    private final String titleFormat;
    private final String textFormat;

    private final boolean enabled;
    private final int numberOfRowsNeeded;

    private Map<String, Boolean> configKey = new HashMap<>();
    private Map<String, String> translationKey = new HashMap<>();

    public ScoreboardManager() {
        FileConfiguration defaultConfig = Parkour.getPlugin().getConfig();
        FileConfiguration stringsConfig = Parkour.getParkourConfig().getStringData();

        this.enabled = defaultConfig.getBoolean("Scoreboard.Enabled");
        this.titleFormat = stringsConfig.getString("Scoreboard.TitleFormat");
        this.textFormat = stringsConfig.getString("Scoreboard.TextFormat");

        configKey.put(COURSE_NAME, defaultConfig.getBoolean("Scoreboard.Display.CourseName"));
        configKey.put(BEST_TIME_EVER, defaultConfig.getBoolean("Scoreboard.Display.BestTimeEver"));
        configKey.put(BEST_TIME_EVER_NAME, defaultConfig.getBoolean("Scoreboard.Display.BestTimeEverName"));
        configKey.put(BEST_TIME_EVER_ME, defaultConfig.getBoolean("Scoreboard.Display.BestTimeByMe"));
        configKey.put(CURRENT_TIME, defaultConfig.getBoolean("Scoreboard.Display.CurrentTime")
                && defaultConfig.getBoolean("OnCourse.DisplayLiveTime"));

        translationKey.put("mainHeading", stringsConfig.getString("Scoreboard.MainHeading"));
        translationKey.put("notCompleted", stringsConfig.getString("Scoreboard.NotCompleted"));
        translationKey.put(COURSE_NAME, stringsConfig.getString("Scoreboard.CourseTitle"));
        translationKey.put(BEST_TIME_EVER, stringsConfig.getString("Scoreboard.BestTimeTitle"));
        translationKey.put(BEST_TIME_EVER_NAME, stringsConfig.getString("Scoreboard.BestTimeNameTitle"));
        translationKey.put(BEST_TIME_EVER_ME, stringsConfig.getString("Scoreboard.MyBestTimeTitle"));
        translationKey.put(CURRENT_TIME, stringsConfig.getString("Scoreboard.CurrentTimeTitle"));

        this.numberOfRowsNeeded = calculateNumberOfRowsNeeded();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void addScoreboard(Player player) {
        if (!this.enabled)
            return;

        Scoreboard board = setupScoreboard(player);
        player.setScoreboard(board);
    }

    public void updateScoreboardTimer(Player player, String liveTime) {
        Scoreboard board = player.getScoreboard();

        if (board == null || !configKey.get(CURRENT_TIME))
            return;

        board.getTeam(CURRENT_TIME).setPrefix(convertText(liveTime));
    }

    private Scoreboard setupScoreboard(Player player) {
        String mainHeading = Utils.colour(translationKey.get("mainHeading"));

        // Set up the scoreboard itself
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective(player.getName(), "Parkour");
        objective.setDisplayName(mainHeading);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        PlayerScoreboard playerScoreboard = new PlayerScoreboard(player.getName(), board, objective);

        addCourseName(playerScoreboard);
        addBestTimeEver(playerScoreboard);
        addBestTimeEverName(playerScoreboard);
        addBestTimeEverMe(playerScoreboard);
        addCurrentTime(playerScoreboard);

        return board;
    }

    private void addCourseName(PlayerScoreboard playerBoard) {
        if (!configKey.get(COURSE_NAME))
            return;

        print(playerBoard, playerBoard.courseName, COURSE_NAME);
    }

    private void addBestTimeEver(PlayerScoreboard playerBoard) {
        if (!configKey.get(BEST_TIME_EVER))
            return;

        List<TimeObject> result = DatabaseMethods.getTopCourseResults(playerBoard.courseName, 1);
        String bestTimeEver = result.size() > 0 ? Utils.displayCurrentTime(result.get(0).getTime()) : translationKey.get("notCompleted");
        print(playerBoard, bestTimeEver, BEST_TIME_EVER);
    }

    private void addBestTimeEverName(PlayerScoreboard playerBoard) {
        if (!configKey.get(BEST_TIME_EVER_NAME))
            return;

        List<TimeObject> result = DatabaseMethods.getTopCourseResults(playerBoard.courseName, 1);
        String bestTimeName = result.size() > 0 ? result.get(0).getPlayer() : translationKey.get("notCompleted");
        print(playerBoard, bestTimeName, BEST_TIME_EVER_NAME);
    }

    private void addBestTimeEverMe(PlayerScoreboard playerBoard) {
        if (!configKey.get(BEST_TIME_EVER_ME))
            return;

        List<TimeObject> result = DatabaseMethods.getTopPlayerCourseResults(playerBoard.playerName, playerBoard.courseName, 1);
        String bestTime = result.size() > 0 ? Utils.displayCurrentTime(result.get(0).getTime()) : translationKey.get("notCompleted");
        print(playerBoard, bestTime, BEST_TIME_EVER_ME);
    }

    private void addCurrentTime(PlayerScoreboard playerBoard) {
        if (!configKey.get(CURRENT_TIME))
            return;

        print(playerBoard, "00:00:00", CURRENT_TIME);
    }

    private void print(PlayerScoreboard playerBoard, String result, String scoreboardKey) {
        Score onlineName = playerBoard.objective.getScore(convertTitle(translationKey.get(scoreboardKey)));
        onlineName.setScore(playerBoard.getDecreaseCount());

        Team displayName = playerBoard.scoreboard.registerNewTeam(scoreboardKey);
        displayName.addEntry(scoreboardKey);
        displayName.setPrefix(convertText(result));
        playerBoard.objective.getScore(scoreboardKey).setScore(playerBoard.getDecreaseCount());
    }

    private String convertTitle(String title) {
        title = titleFormat.replace("%TITLE%", title);
        return cropAndColour(title);
    }

    private String convertText(String value) {
        value = textFormat.replace("%TEXT%", value);
        return cropAndColour(value);
    }

    private String cropAndColour(String text) {
        text = Utils.colour(text);
        if (Utils.getMinorServerVersion() < 13) {
            text = text.substring(0, Math.min(15, text.length() - 1));
        }
        return text;
    }

    public void removeScoreboard(Player player) {
        if (this.enabled) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    /**
     * Each row needs a heading and a text entry
     */
    private int calculateNumberOfRowsNeeded() {
        int rowsNeeded = 0;
        if (configKey.get(COURSE_NAME)) rowsNeeded += 2;
        if (configKey.get(BEST_TIME_EVER)) rowsNeeded += 2;
        if (configKey.get(BEST_TIME_EVER_NAME)) rowsNeeded += 2;
        if (configKey.get(BEST_TIME_EVER_ME)) rowsNeeded += 2;
        if (configKey.get(CURRENT_TIME)) rowsNeeded += 2;
        return rowsNeeded;
    }

    private class PlayerScoreboard {

        private final String playerName;
        private final String courseName;

        private int scoreboardCount = numberOfRowsNeeded;
        private Scoreboard scoreboard;
        private Objective objective;

        public PlayerScoreboard(String playerName, Scoreboard scoreboard, Objective objective) {
            this.playerName = playerName;
            this.courseName = CourseMethods.findByPlayer(playerName).getName();
            this.scoreboard = scoreboard;
            this.objective = objective;
        }

        public int getDecreaseCount() {
            return --scoreboardCount;
        }
    }
}

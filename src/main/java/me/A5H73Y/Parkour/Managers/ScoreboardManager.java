package me.A5H73Y.Parkour.Managers;

import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.TimeObject;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    Map<String, Integer> scoreboardCount = new HashMap<>();

    public ScoreboardManager() {
        this.enabled = Parkour.getPlugin().getConfig().getBoolean("Scoreboard.Enabled");
        this.titleFormat = Parkour.getPlugin().getConfig().getString("Scoreboard.TitleFormat");
        this.textFormat = Parkour.getPlugin().getConfig().getString("Scoreboard.TextFormat");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void addScoreboard(Player player) {
        if (!this.enabled)
            return;

        scoreboardCount.put(player.getName(), 10); //TODO, actually count the amount of items we need
        Scoreboard board = setupScoreboard(player);
        player.setScoreboard(board);
    }

    public void updateScoreboardTimer(Player player, String liveTime) {
        Scoreboard board = player.getScoreboard();

        if (board == null)
            return;

        board.getTeam(CURRENT_TIME).setPrefix(liveTime);
    }

    private Scoreboard setupScoreboard(Player player) {
        String mainHeading = Utils.colour(Parkour.getPlugin().getConfig().getString("Scoreboard.MainHeading"));

        // Set up the scoreboard itself
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective(player.getName(), "Parkour", mainHeading);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        //TODO poc - This can be refactored to be much smaller
        addCourseName(board, player, objective);
        addBestTimeEver(board, player, objective);
        addBestTimeEverName(board, player, objective);
        addBestTimeEverMe(board, player, objective);
        addCurrentTime(board, player, objective);

        return board;
    }

    private void addCourseName(Scoreboard board, Player player, Objective objective) {
        if (!Parkour.getPlugin().getConfig().getBoolean("Scoreboard.Display.CourseName"))
            return;

        String courseName = CourseMethods.findByPlayer(player.getName()).getName();

        Score onlineName = objective.getScore(convertTitle("Course:"));
        onlineName.setScore(reduceAndReturnScoreboardCount(player.getName()));

        Team displayName = board.registerNewTeam(COURSE_NAME);
        displayName.addEntry(COURSE_NAME);
        displayName.setPrefix(convertText(courseName));
        objective.getScore(COURSE_NAME).setScore(reduceAndReturnScoreboardCount(player.getName()));
    }

    private void addBestTimeEver(Scoreboard board, Player player, Objective objective) {
        if (!Parkour.getPlugin().getConfig().getBoolean("Scoreboard.Display.BestTimeEver"))
            return;

        String courseName = CourseMethods.findByPlayer(player.getName()).getName();
        List<TimeObject> result = DatabaseMethods.getTopCourseResults(courseName, 1);
        String bestTime = result.size() > 0 ? Utils.displayCurrentTime(result.get(0).getTime()) : "Not completed yet";

        Score onlineName = objective.getScore(convertTitle("Best Time:"));
        onlineName.setScore(reduceAndReturnScoreboardCount(player.getName()));

        Team displayName = board.registerNewTeam(BEST_TIME_EVER);
        displayName.addEntry(BEST_TIME_EVER);
        displayName.setPrefix(convertText(bestTime));
        objective.getScore(BEST_TIME_EVER).setScore(reduceAndReturnScoreboardCount(player.getName()));
    }

    private void addBestTimeEverName(Scoreboard board, Player player, Objective objective) {
        if (!Parkour.getPlugin().getConfig().getBoolean("Scoreboard.Display.BestTimeEverName"))
            return;

        String courseName = CourseMethods.findByPlayer(player.getName()).getName();
        List<TimeObject> result = DatabaseMethods.getTopCourseResults(courseName, 1);
        String bestTimeName = result.size() > 0 ? result.get(0).getPlayer() : "Not completed yet";

        Score onlineName = objective.getScore(convertTitle("Best Player:"));
        onlineName.setScore(reduceAndReturnScoreboardCount(player.getName()));

        Team displayName = board.registerNewTeam(BEST_TIME_EVER_NAME);
        displayName.addEntry(BEST_TIME_EVER_NAME);
        displayName.setPrefix(convertText(bestTimeName));
        objective.getScore(BEST_TIME_EVER_NAME).setScore(reduceAndReturnScoreboardCount(player.getName()));
    }

    private void addBestTimeEverMe(Scoreboard board, Player player, Objective objective) {
        if (!Parkour.getPlugin().getConfig().getBoolean("Scoreboard.Display.BestTimeByMe"))
            return;

        String courseName = CourseMethods.findByPlayer(player.getName()).getName();
        List<TimeObject> result = DatabaseMethods.getTopPlayerCourseResults(player.getName(), courseName, 1);
        String bestTime = result.size() > 0 ? Utils.displayCurrentTime(result.get(0).getTime()) : "Not completed yet";

        Score onlineName = objective.getScore(convertTitle("My Best Time:"));
        onlineName.setScore(reduceAndReturnScoreboardCount(player.getName()));

        Team displayName = board.registerNewTeam(BEST_TIME_EVER_ME);
        displayName.addEntry(BEST_TIME_EVER_ME);
        displayName.setPrefix(convertText(bestTime));
        objective.getScore(BEST_TIME_EVER_ME).setScore(reduceAndReturnScoreboardCount(player.getName()));
    }

    private void addCurrentTime(Scoreboard board, Player player, Objective objective) {
        if (!Parkour.getPlugin().getConfig().getBoolean("Scoreboard.Display.CurrentTime"))
            return;

        Score onlineName = objective.getScore(convertTitle("Course Time:"));
        onlineName.setScore(reduceAndReturnScoreboardCount(player.getName()));

        Team displayName = board.registerNewTeam(CURRENT_TIME);
        displayName.addEntry(CURRENT_TIME);
        displayName.setPrefix(convertText("00:00:00"));
        objective.getScore(CURRENT_TIME).setScore(reduceAndReturnScoreboardCount(player.getName()));
    }

    private String convertTitle(String heading) {
        return Utils.colour(titleFormat.replace("%TITLE%", heading));
    }

    private String convertText(String variable) {
        return Utils.colour(textFormat.replace("%TEXT%", variable));
    }

    //TODO perhaps think of a better way to manage a decreasing integer per-player
    private int reduceAndReturnScoreboardCount(String playerName) {
        int count = scoreboardCount.get(playerName);
        return scoreboardCount.put(playerName, --count);
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}

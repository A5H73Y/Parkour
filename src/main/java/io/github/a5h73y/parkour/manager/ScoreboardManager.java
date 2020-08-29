package io.github.a5h73y.parkour.manager;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.database.TimeEntry;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager extends AbstractPluginReceiver {

    // For some reason the Scoreboard API is so stupid you have to use a blank string to identify the objective.
    // So each objective has a unique ChatColor for identifying it, which will be overwritten anyway
    private static final String COURSE_NAME = ChatColor.AQUA.toString();
    private static final String BEST_TIME_EVER = ChatColor.BLACK.toString();
    private static final String BEST_TIME_EVER_NAME = ChatColor.BLUE.toString();
    private static final String BEST_TIME_EVER_ME = ChatColor.DARK_AQUA.toString();
    private static final String CURRENT_TIME = ChatColor.DARK_BLUE.toString();
    private static final String CURRENT_DEATHS = ChatColor.DARK_GREEN.toString();
    private static final String CHECKPOINTS = ChatColor.DARK_RED.toString();

    private final String titleFormat;
    private final String textFormat;

    private final boolean enabled;
    private final int numberOfRowsNeeded;

    private final Map<String, Boolean> configKey = new HashMap<>();
    private final Map<String, String> translationKey = new HashMap<>();

    public ScoreboardManager(Parkour parkour) {
        super(parkour);
        ParkourConfiguration defaultConfig = Parkour.getConfig(ConfigType.DEFAULT);
        ParkourConfiguration stringsConfig = Parkour.getConfig(ConfigType.STRINGS);

        this.enabled = defaultConfig.getBoolean("Scoreboard.Enabled");
        this.titleFormat = stringsConfig.getString("Scoreboard.TitleFormat");
        this.textFormat = stringsConfig.getString("Scoreboard.TextFormat");

        configKey.put(COURSE_NAME, defaultConfig.getBoolean("Scoreboard.Display.CourseName"));
        configKey.put(BEST_TIME_EVER, defaultConfig.getBoolean("Scoreboard.Display.BestTimeEver"));
        configKey.put(BEST_TIME_EVER_NAME, defaultConfig.getBoolean("Scoreboard.Display.BestTimeEverName"));
        configKey.put(BEST_TIME_EVER_ME, defaultConfig.getBoolean("Scoreboard.Display.BestTimeByMe"));
        configKey.put(CURRENT_TIME, defaultConfig.getBoolean("Scoreboard.Display.CurrentTime"));
        configKey.put(CURRENT_DEATHS, defaultConfig.getBoolean("Scoreboard.Display.CurrentDeaths"));
        configKey.put(CHECKPOINTS, defaultConfig.getBoolean("Scoreboard.Display.Checkpoints"));

        translationKey.put("mainHeading", stringsConfig.getString("Scoreboard.MainHeading"));
        translationKey.put("notCompleted", stringsConfig.getString("Scoreboard.NotCompleted"));
        translationKey.put(COURSE_NAME, stringsConfig.getString("Scoreboard.CourseTitle"));
        translationKey.put(BEST_TIME_EVER, stringsConfig.getString("Scoreboard.BestTimeTitle"));
        translationKey.put(BEST_TIME_EVER_NAME, stringsConfig.getString("Scoreboard.BestTimeNameTitle"));
        translationKey.put(BEST_TIME_EVER_ME, stringsConfig.getString("Scoreboard.MyBestTimeTitle"));
        translationKey.put(CURRENT_TIME, stringsConfig.getString("Scoreboard.CurrentTimeTitle"));
        translationKey.put(CURRENT_DEATHS, stringsConfig.getString("Scoreboard.CurrentDeathsTitle"));
        translationKey.put(CHECKPOINTS, stringsConfig.getString("Scoreboard.CheckpointsTitle"));

        this.numberOfRowsNeeded = calculateNumberOfRowsNeeded();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void addScoreboard(Player player) {
        if (!this.enabled) {
            return;
        }

        Scoreboard board = setupScoreboard(player);

        if (Parkour.getDefaultConfig().isPreventPlayerCollisions() && PluginUtils.getMinorServerVersion() > 8) {
            Team team = board.registerNewTeam("parkour");
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.addEntry(player.getName());
        }

        player.setScoreboard(board);
    }

    public void updateScoreboardTimer(Player player, String liveTime) {
        Scoreboard board = player.getScoreboard();

        if (board == null || !configKey.get(CURRENT_TIME)) {
            return;
        }

        board.getTeam(CURRENT_TIME).setPrefix(convertText(liveTime));
    }

    public void updateScoreboardDeaths(Player player, String deaths) {
        Scoreboard board = player.getScoreboard();

        if (board == null || !configKey.get(CURRENT_DEATHS)) {
            return;
        }

        board.getTeam(CURRENT_DEATHS).setPrefix(convertText(deaths));
    }

    public void updateScoreboardCheckpoints(Player player, String checkpoints) {
        Scoreboard board = player.getScoreboard();

        if (board == null || !configKey.get(CHECKPOINTS)) {
            return;
        }

        board.getTeam(CHECKPOINTS).setPrefix(convertText(checkpoints));
    }

    private Scoreboard setupScoreboard(Player player) {
        String mainHeading = StringUtils.colour(translationKey.get("mainHeading"));

        // Set up the scoreboard itself
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective(player.getName(), "Parkour");
        objective.setDisplayName(mainHeading);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        PlayerScoreboard playerScoreboard = new PlayerScoreboard(player, board, objective);

        addCourseName(playerScoreboard);
        addBestTimeEver(playerScoreboard);
        addBestTimeEverMe(playerScoreboard);
        addCurrentTime(playerScoreboard);
        addCurrentDeaths(playerScoreboard);
        addCheckpoints(playerScoreboard);

        return board;
    }

    private void addCourseName(PlayerScoreboard playerBoard) {
        if (!configKey.get(COURSE_NAME)) {
            return;
        }

        print(playerBoard, playerBoard.getCourseName(), COURSE_NAME);
    }

    private void addBestTimeEver(PlayerScoreboard playerBoard) {
        if (!configKey.get(BEST_TIME_EVER) && !configKey.get(BEST_TIME_EVER_NAME)) {
            return;
        }

        TimeEntry result = Parkour.getInstance().getDatabase().getNthBestTime(playerBoard.getCourseName(), 1);

        if (configKey.get(BEST_TIME_EVER)) {
            String bestTimeEver = result != null ? DateTimeUtils.displayCurrentTime(result.getTime())
                    : translationKey.get("notCompleted");
            print(playerBoard, bestTimeEver, BEST_TIME_EVER);
        }
        if (configKey.get(BEST_TIME_EVER_NAME)) {
            String bestTimeName = result != null ? result.getPlayerName() :
                    translationKey.get("notCompleted");
            print(playerBoard, bestTimeName, BEST_TIME_EVER_NAME);
        }
    }

    private void addBestTimeEverMe(PlayerScoreboard playerBoard) {
        if (!configKey.get(BEST_TIME_EVER_ME)) {
            return;
        }

        List<TimeEntry> result = Parkour.getInstance().getDatabase()
                .getTopPlayerCourseResults(playerBoard.getPlayer(), playerBoard.getCourseName(), 1);
        String bestTime = result.size() > 0 ? DateTimeUtils.displayCurrentTime(result.get(0).getTime()) : translationKey.get("notCompleted");
        print(playerBoard, bestTime, BEST_TIME_EVER_ME);
    }

    private void addCurrentTime(PlayerScoreboard playerBoard) {
        if (!configKey.get(CURRENT_TIME)) {
            return;
        }

        String start = "00:00:00";
        if (Parkour.getConfig(ConfigType.COURSES).contains(playerBoard.getCourseName() + ".MaxTime")) {
            start = DateTimeUtils.convertSecondsToTime(
                    Parkour.getConfig(ConfigType.COURSES).getInt(playerBoard.getCourseName() + ".MaxTime", 0));
        }
        print(playerBoard, start, CURRENT_TIME);
    }

    private void addCurrentDeaths(PlayerScoreboard playerBoard) {
        if (!configKey.get(CURRENT_DEATHS)) {
            return;
        }

        print(playerBoard, "0", CURRENT_DEATHS);
    }

    private void addCheckpoints(PlayerScoreboard playerBoard) {
        if (!configKey.get(CHECKPOINTS)) {
            return;
        }

        int checkpoints = playerBoard.getCourse().getNumberOfCheckpoints();
        print(playerBoard, "0 / " + checkpoints, CHECKPOINTS);
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
        title = titleFormat.replace("%VALUE%", title);
        return cropAndColour(title);
    }

    private String convertText(String value) {
        value = textFormat.replace("%VALUE%", value);
        return cropAndColour(value);
    }

    private String cropAndColour(String text) {
        text = StringUtils.colour(text);
        if (PluginUtils.getMinorServerVersion() < 10) {
            text = text.substring(0, Math.min(15, text.length()));
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

        // TODO for each key add 2
        int rowsNeeded = 0;
        if (configKey.get(COURSE_NAME)) {
            rowsNeeded += 2;
        }
        if (configKey.get(BEST_TIME_EVER)) {
            rowsNeeded += 2;
        }
        if (configKey.get(BEST_TIME_EVER_NAME)) {
            rowsNeeded += 2;
        }
        if (configKey.get(BEST_TIME_EVER_ME)) {
            rowsNeeded += 2;
        }
        if (configKey.get(CURRENT_TIME)) {
            rowsNeeded += 2;
        }
        if (configKey.get(CURRENT_DEATHS)) {
            rowsNeeded += 2;
        }
        if (configKey.get(CHECKPOINTS)) {
            rowsNeeded += 2;
        }
        return rowsNeeded;
    }

    private class PlayerScoreboard {

        private final Player player;
        private final Course course;

        private int scoreboardCount = numberOfRowsNeeded;
        private final Scoreboard scoreboard;
        private final Objective objective;

        public PlayerScoreboard(Player player, Scoreboard scoreboard, Objective objective) {
            this.player = player;
            this.course = parkour.getPlayerManager().getParkourSession(player).getCourse();
            this.scoreboard = scoreboard;
            this.objective = objective;
        }

        public int getDecreaseCount() {
            return --scoreboardCount;
        }

        public Player getPlayer() {
            return player;
        }

        public Course getCourse() {
            return course;
        }

        public String getCourseName() {
            return course.getName();
        }
    }
}

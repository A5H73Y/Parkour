package io.github.a5h73y.parkour.manager;

import static io.github.a5h73y.parkour.Parkour.PLUGIN_NAME;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.database.TimeEntry;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Comparator;
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
    private static final String MY_BEST_TIME_EVER = ChatColor.DARK_AQUA.toString();
    private static final String CURRENT_DEATHS = ChatColor.DARK_GREEN.toString();
    private static final String CHECKPOINTS = ChatColor.DARK_RED.toString();
    private static final String LIVE_TIMER = ChatColor.DARK_BLUE.toString();

    // final translations
    private final String titleFormat = TranslationUtils.getTranslation("Scoreboard.TitleFormat", false);
    private final String textFormat = TranslationUtils.getTranslation("Scoreboard.TextFormat", false);
    private final String mainHeading = TranslationUtils.getTranslation("Scoreboard.MainHeading", false);
    private final String notCompleted = TranslationUtils.getTranslation("Scoreboard.NotCompleted", false);

    private final boolean enabled;
    private final int numberOfRowsNeeded;
    private final Map<String, ScoreboardEntry> scoreboardDetails = new HashMap<>();

    public ScoreboardManager(final Parkour parkour) {
        super(parkour);
        this.enabled = parkour.getConfig().getBoolean("Scoreboard.Enabled");

        scoreboardDetails.put(COURSE_NAME, generateScoreboard("CourseName"));
        scoreboardDetails.put(BEST_TIME_EVER, generateScoreboard("BestTimeEver"));
        scoreboardDetails.put(BEST_TIME_EVER_NAME, generateScoreboard("BestTimeEverName"));
        scoreboardDetails.put(MY_BEST_TIME_EVER, generateScoreboard("MyBestTime"));
        scoreboardDetails.put(CURRENT_DEATHS, generateScoreboard("CurrentDeaths"));
        scoreboardDetails.put(CHECKPOINTS, generateScoreboard("Checkpoints"));
        scoreboardDetails.put(LIVE_TIMER, generateScoreboard("LiveTimer"));

        this.numberOfRowsNeeded = calculateNumberOfRowsNeeded();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void addScoreboard(Player player, ParkourSession session) {
        if (!this.enabled || session == null) {
            return;
        }

        Scoreboard board = setupScoreboard(player, session);

        if (parkour.getConfig().isPreventPlayerCollisions()
                && PluginUtils.getMinorServerVersion() > 8) {
            Team team = board.registerNewTeam(PLUGIN_NAME);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.addEntry(player.getName());
        }
    }

    public void updateScoreboardTimer(Player player, String liveTime) {
        Scoreboard board = player.getScoreboard();

        if (!enabled || !scoreboardDetails.get(LIVE_TIMER).isEnabled()) {
            return;
        }

        board.getTeam(LIVE_TIMER).setPrefix(convertText(liveTime));
    }

    public void updateScoreboardDeaths(Player player, int deaths) {
        Scoreboard board = player.getScoreboard();

        if (!enabled || !scoreboardDetails.get(CURRENT_DEATHS).isEnabled()) {
            return;
        }

        board.getTeam(CURRENT_DEATHS).setPrefix(convertText(String.valueOf(deaths)));
    }

    public void updateScoreboardCheckpoints(Player player, ParkourSession session) {
        Scoreboard board = player.getScoreboard();

        if (!enabled || !scoreboardDetails.get(CHECKPOINTS).isEnabled()) {
            return;
        }

        board.getTeam(CHECKPOINTS).setPrefix(convertText(session.getCurrentCheckpoint()
                + " / " + session.getCourse().getNumberOfCheckpoints()));
    }

    private Scoreboard setupScoreboard(Player player, ParkourSession session) {
        // Set up the scoreboard itself
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective(player.getName(), PLUGIN_NAME);
        objective.setDisplayName(mainHeading);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        PlayerScoreboard playerScoreboard = new PlayerScoreboard(player, session, board, objective);
        registerAllEntries(playerScoreboard);

        // set the static info
        setCourseName(playerScoreboard);
        setBestTimeEver(playerScoreboard);
        setMyBestTimeEver(playerScoreboard);

        player.setScoreboard(board);

        // update the dynamic results with their session (could be pre-populated)
        updateScoreboardCheckpoints(player, playerScoreboard.getSession());
        updateScoreboardDeaths(player, playerScoreboard.getSession().getDeaths());

        return board;
    }

    private void registerAllEntries(PlayerScoreboard playerBoard) {
        scoreboardDetails.entrySet().stream()
                .filter(detail -> detail.getValue().isEnabled())
                .sorted(Comparator.comparingInt(detail -> detail.getValue().getSequence()))
                .forEach(detail -> registerTeam(playerBoard, detail.getKey(), detail.getValue()));
    }

    private ScoreboardEntry generateScoreboard(String keyName) {
        ScoreboardEntry entry = new ScoreboardEntry();
        entry.setEnabled(parkour.getConfig().getBoolean("Scoreboard." + keyName + ".Enabled"));
        entry.setSequence(parkour.getConfig().getInt("Scoreboard." + keyName + ".Sequence"));
        entry.setTranslation(TranslationUtils.getTranslation("Scoreboard." + keyName + "Title", false));
        return entry;
    }

    private void setCourseName(PlayerScoreboard playerBoard) {
        if (!scoreboardDetails.get(COURSE_NAME).isEnabled()) {
            return;
        }

        playerBoard.scoreboard.getTeam(COURSE_NAME).setPrefix(playerBoard.getSession().getCourseName());
    }

    private void setBestTimeEver(PlayerScoreboard playerBoard) {
        if (!scoreboardDetails.get(BEST_TIME_EVER).isEnabled()
                && !scoreboardDetails.get(BEST_TIME_EVER_NAME).isEnabled()) {
            return;
        }

        TimeEntry result = parkour.getDatabase().getNthBestTime(playerBoard.getSession().getCourseName(), 1);

        if (scoreboardDetails.get(BEST_TIME_EVER).isEnabled()) {
            String bestTimeEver = result != null ? DateTimeUtils.displayCurrentTime(result.getTime()) : notCompleted;
            playerBoard.scoreboard.getTeam(BEST_TIME_EVER).setPrefix(bestTimeEver);
        }
        if (scoreboardDetails.get(BEST_TIME_EVER_NAME).isEnabled()) {
            String bestTimeName = result != null ? result.getPlayerName() : notCompleted;
            playerBoard.scoreboard.getTeam(BEST_TIME_EVER_NAME).setPrefix(bestTimeName);
        }
    }

    private void setMyBestTimeEver(PlayerScoreboard playerBoard) {
        if (!scoreboardDetails.get(MY_BEST_TIME_EVER).isEnabled()) {
            return;
        }

        List<TimeEntry> result = parkour.getDatabase().getTopPlayerCourseResults(
                playerBoard.getPlayer(), playerBoard.getSession().getCourseName(), 1);
        String bestTime = !result.isEmpty() ? DateTimeUtils.displayCurrentTime(result.get(0).getTime()) : notCompleted;
        playerBoard.scoreboard.getTeam(MY_BEST_TIME_EVER).setPrefix(bestTime);
    }

    private void registerTeam(PlayerScoreboard playerBoard, String key, ScoreboardEntry scoreboardKey) {
        Score titleText = playerBoard.objective.getScore(convertTitle(scoreboardKey.getTranslation()));
        titleText.setScore(playerBoard.getDecreaseCount());

        Team valueText = playerBoard.scoreboard.registerNewTeam(key);
        valueText.addEntry(key);
        playerBoard.objective.getScore(key).setScore(playerBoard.getDecreaseCount());
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
        return (int) (scoreboardDetails.values().stream().filter(ScoreboardEntry::isEnabled).count() * 2);
    }

    private class PlayerScoreboard {

        private final Player player;
        private final ParkourSession session;

        private int scoreboardCount = numberOfRowsNeeded;
        private final Scoreboard scoreboard;
        private final Objective objective;

        public PlayerScoreboard(Player player, ParkourSession session, Scoreboard scoreboard, Objective objective) {
            this.player = player;
            this.session = session;
            this.scoreboard = scoreboard;
            this.objective = objective;
        }

        public int getDecreaseCount() {
            return --scoreboardCount;
        }

        public Player getPlayer() {
            return player;
        }

        public ParkourSession getSession() {
            return session;
        }
    }

    private static class ScoreboardEntry {

        private boolean enabled;

        private int sequence;

        private String translation;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public String getTranslation() {
            return translation;
        }

        public void setTranslation(String translation) {
            this.translation = translation;
        }
    }
}

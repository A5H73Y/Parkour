package io.github.a5h73y.parkour.database;

/**
 * Representation model of a `Time` stored in the database.
 */
public class TimeEntry {

    private final String courseId;
    private final String playerId;
    private final String playerName;
    private final long time;
    private final int deaths;

    public TimeEntry(String playerId, String playerName, long time, int deaths) {
        this(null, playerId, playerName, time, deaths);
    }

    public TimeEntry(String courseId, String playerId, String playerName, long time, int deaths) {
        this.courseId = courseId;
        this.playerId = playerId;
        this.playerName = playerName;
        this.time = time;
        this.deaths = deaths;
    }

    public String getCourseId() {
        return courseId;
    }

    /**
     * The player's UUID of the time result.
     * @return player UUID
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * The player's name of the time result (at the time).
     * @return player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * The total time taken of the time result.
     * @return time
     */
    public long getTime() {
        return time;
    }

    /**
     * The deaths accumulated of the time result.
     * @return deaths
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * The player's full UUID including hyphens.
     * @return player UUID including hyphens
     */
    public String getPlayerUUID() {
        StringBuilder uuid = new StringBuilder(playerId);
        uuid.insert(20, "-");
        uuid.insert(16, "-");
        uuid.insert(12, "-");
        uuid.insert(8, "-");
        return uuid.toString();
    }
}

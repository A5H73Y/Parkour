package io.github.a5h73y.database;

/**
 * Representation model of a `Time` stored in the database.
 */
public class TimeObject {

    private final String player;
    private final long time;
    private final int deaths;

    public TimeObject(String player, long time, int deaths) {
        this.player = player;
        this.time = time;
        this.deaths = deaths;
    }

    /**
     * The player of the time result.
     * @return player name
     */
    public String getPlayer() {
        return player;
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
}

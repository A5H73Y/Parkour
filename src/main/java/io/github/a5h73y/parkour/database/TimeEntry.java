package io.github.a5h73y.parkour.database;

import io.github.a5h73y.parkour.utility.PlayerUtils;
import java.util.Date;

/**
 * Representation model of a `Time` stored in the database.
 */
public class TimeEntry {

    private final String courseId;
    private final String playerId;
    private final long time;
    private final int deaths;
    private final Date achieved;

    private String playerName;

    /**
     * Construct a Time Entry.
     * Results from the Database will be inserted.
     *
     * @param courseId course ID in the database
     * @param playerId player UUID
     * @param time time in ms
     * @param deaths deaths accumulated
     * @param achieved datetime achieved
     */
    public TimeEntry(String courseId, String playerId, long time, int deaths, Date achieved) {
        this.courseId = courseId;
        this.playerId = playerId;
        this.time = time;
        this.deaths = deaths;
        this.achieved = achieved;
    }

    public String getPlayerName() {
        if (playerName == null) {
            playerName = PlayerUtils.findPlayerName(this.playerId);
        }
        return playerName;
    }

    /**
     * The Course ID in the database.
     * @return course ID
     */
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

    public Date getAchievedDate() {
        return achieved;
    }
}

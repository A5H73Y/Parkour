package io.github.a5h73y.parkour.type.player;

import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import java.io.Serializable;
import org.bukkit.Location;

/**
 * Parkour Session.
 * Keeps track of a Player's progress while on a Course.
 * The Session is serialized and can be persisted against the Player's UUID.
 * The Course is transient as it's safer and quicker to retrieve it when needed.
 * The time is tracked based on start time and any time accumulated.
 * The secondsAccumulated are used to display a Live Timer to the Player.
 */
public class ParkourSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private int deaths;
    private int currentCheckpoint;
    private int secondsAccumulated;

    private long timeStarted;
    private long timeAccumulated;
    private long timeFinished;

    private final String courseName;

    private transient Course course;
    private transient Location freedomLocation;
    private transient boolean markedForDeletion;
    private transient boolean startTimer;


    /**
     * Construct a ParkourSession from a Course.
     * @param course {@link Course}
     */
    public ParkourSession(Course course) {
        this.course = course;
        this.courseName = course.getName();
        resetTime();
    }

    /**
     * Check if all the Course Checkpoints have been achieved.
     * @return all checkpoints achieved
     */
    public boolean hasAchievedAllCheckpoints() {
        return currentCheckpoint == course.getNumberOfCheckpoints();
    }

    /**
     * Get the current {@link Checkpoint}.
     * @return current checkpoint
     */
    public Checkpoint getCheckpoint() {
        return course.getCheckpoints().get(currentCheckpoint);
    }

    /**
     * Reset all Time accumulated.
     */
    public void resetTime() {
        this.timeStarted = System.currentTimeMillis();
        this.timeAccumulated = 0;
        this.secondsAccumulated = course.hasMaxTime() ? course.getMaxTime() : 0;
    }

    /**
     * Reset the number of accumulated deaths.
     */
    public void resetDeaths() {
        this.deaths = 0;
    }

    /**
     * Increase current Checkpoint.
     */
    public void increaseCheckpoint() {
        currentCheckpoint++;
    }

    /**
     * Increase number of Deaths.
     */
    public void increaseDeath() {
        deaths++;
    }

    public void markTimeAccumulated() {
        timeAccumulated = getCurrentTime();
    }

    public void markTimeFinished() {
        timeFinished = getCurrentTime();
    }

    public long getCurrentTime() {
        return System.currentTimeMillis() - timeStarted;
    }

    public String getDisplayTime() {
        return DateTimeUtils.displayCurrentTime(getCurrentTime());
    }

    public void recalculateTime() {
        timeStarted = System.currentTimeMillis() - timeAccumulated;
        timeAccumulated = 0;
    }

    public int calculateSeconds() {
        return course.hasMaxTime() ? secondsAccumulated-- : secondsAccumulated++;
    }

    public int getRemainingDeaths() {
        int remainingDeaths = getCourse().getMaxDeaths() - getDeaths();
        return Math.max(remainingDeaths, 0);
    }

    /**
     * Get {@link ParkourMode} for associated Course.
     * @return ParkourMode
     */
    public ParkourMode getParkourMode() {
        return course.getParkourMode();
    }

    /**
     * Get number of Deaths accumulated.
     * @return number of deaths
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * Get current checkpoint number.
     * @return current checkpoint
     */
    public int getCurrentCheckpoint() {
        return currentCheckpoint;
    }

    /**
     * Set current checkpoint number.
     * @param currentCheckpoint new checkpoint
     */
    public void setCurrentCheckpoint(int currentCheckpoint) {
        this.currentCheckpoint = currentCheckpoint;
    }

    /**
     * Get associated {@link Course}.
     * @return course
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Set associated {@link Course}.
     * @param course course
     */
    public void setCourse(Course course) {
        this.course = course;
    }

    /**
     * Get name of the Course.
     * @return course name
     */
    public String getCourseName() {
        return courseName;
    }

    public Location getFreedomLocation() {
        return freedomLocation;
    }

    public void setFreedomLocation(Location freedomLocation) {
        this.freedomLocation = freedomLocation;
    }

    public long getTimeFinished() {
        return timeFinished;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion(boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }

    public boolean isStartTimer() {
        return startTimer;
    }

    public void setStartTimer(boolean startTimer) {
        this.startTimer = startTimer;
    }
}

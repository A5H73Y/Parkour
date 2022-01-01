package io.github.a5h73y.parkour.type.player.session;

import static io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable.getMapValue;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parkour Session.
 * Keeps track of a Player's progress while on a Course.
 * The Session is serialized and can be persisted against the Player's UUID.
 * The Course is transient as it's safer and quicker to retrieve it when needed.
 * The time is tracked based on start time and any time accumulated.
 * The secondsAccumulated are used to display a Live Timer to the Player.
 */
public class ParkourSession implements ParkourSerializable {

    private static final long serialVersionUID = 1L;

    private transient Course course;

    private int deaths;
    private int currentCheckpoint;
    private int secondsAccumulated;

    private long timeStarted;
    private long timeAccumulated;
    private long timeFinished;

    private transient Location freedomLocation;
    private transient boolean markedForDeletion;
    private transient boolean startTimer;

    /**
     * Construct a ParkourSession from a Course.
     * @param course {@link Course}
     */
    public ParkourSession(Course course) {
        this.course = course;
        resetTime();
    }

    public ParkourSession(Course course,
                          int deaths,
                          int currentCheckpoint,
                          int secondsAccumulated,
                          long timeStarted,
                          long timeAccumulated,
                          Location freedomLocation) {
        this.course = course;
        this.deaths = deaths;
        this.currentCheckpoint = currentCheckpoint;
        this.secondsAccumulated = secondsAccumulated;
        this.timeStarted = timeStarted;
        this.timeAccumulated = timeAccumulated;
        this.freedomLocation = freedomLocation;
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

    @Nullable
    public Checkpoint getNextCheckpoint() {
        Checkpoint checkpoint = null;
        if (currentCheckpoint + 1 < course.getCheckpoints().size()) {
            checkpoint = course.getCheckpoints().get(currentCheckpoint + 1);
        }
        return checkpoint;
    }

    /**
     * Reset all Time accumulated.
     */
    public void resetTime() {
        this.timeStarted = System.currentTimeMillis();
        this.timeAccumulated = 0;
        this.timeFinished = 0;
        this.secondsAccumulated = course.getSettings().hasMaxTime() ? course.getSettings().getMaxTime() : 0;
    }

    /**
     * Reset the number of accumulated deaths.
     */
    public void resetDeaths() {
        this.deaths = 0;
    }

    /**
     * Reset the accumulated time and deaths.
     */
    public void resetProgress() {
        resetTime();
        resetDeaths();
        this.currentCheckpoint = 0;
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
        return DateTimeUtils.displayCurrentTime(hasFinished() ? getTimeFinished() : getCurrentTime());
    }

    public void recalculateTime() {
        timeStarted = System.currentTimeMillis() - timeAccumulated;
        timeAccumulated = 0;
    }

    public int calculateSeconds() {
        return course.getSettings().hasMaxTime() ? secondsAccumulated-- : secondsAccumulated++;
    }

    public int getRemainingDeaths() {
        int remainingDeaths = getCourse().getSettings().getMaxDeaths() - getDeaths();
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
        return course.getName();
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

    private boolean hasFinished() {
        return timeFinished != 0;
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

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("CourseName", course.getName());
        data.put("Deaths", deaths);
        data.put("CurrentCheckpoint", currentCheckpoint);
        data.put("SecondsAccumulated", secondsAccumulated);
        data.put("TimeStarted", timeStarted);
        data.put("TimeAccumulated", timeAccumulated);
        if (freedomLocation != null) {
            data.put("FreedomLocation", freedomLocation.serialize());
        }
        return data;
    }

    public static ParkourSession deserialize(Map<String, Object> input) {
        Course course = Parkour.getInstance().getCourseManager().findByName(String.valueOf(input.get("CourseName")));
        int deaths = NumberConversions.toInt(input.get("Deaths"));
        int currentCheckpoint = NumberConversions.toInt(input.get("CurrentCheckpoint"));
        int secondsAccumulated = NumberConversions.toInt(input.get("SecondsAccumulated"));

        long timeStarted = NumberConversions.toLong(input.get("TimeStarted"));
        long timeAccumulated = NumberConversions.toLong(input.get("TimeAccumulated"));

        Location freedomLocation = null;
        if (input.containsKey("FreedomLocation")) {
            freedomLocation = Location.deserialize(getMapValue(input.get("FreedomLocation")));
        }

        return new ParkourSession(course, deaths, currentCheckpoint, secondsAccumulated, timeStarted, timeAccumulated, freedomLocation);
    }
}

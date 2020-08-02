package io.github.a5h73y.parkour.type.course;

import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.type.kit.ParkourKit;
import java.io.Serializable;
import java.util.List;

public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final List<Checkpoint> checkpoints;
    private final ParkourKit parkourKit;
    private final ParkourMode parkourMode;

    private int maxDeaths;
    private int maxTime;

    /**
     * Construct a Parkour Course.
     *
     * @param name course name
     */
    public Course(String name, List<Checkpoint> checkpoints, ParkourKit parkourKit, ParkourMode parkourMode) {
        this.name = name;
        this.checkpoints = checkpoints;
        this.parkourKit = parkourKit;
        this.parkourMode = parkourMode;
        setMaxDeaths(CourseInfo.getMaximumDeaths(name));
        setMaxTime(CourseInfo.getMaximumTime(name));
    }

    /**
     * Get Course's name.
     * This will be lowercase
     *
     * @return Course Name
     */
    public String getName() {
        return name;
    }

    /**
     * Get number of checkpoints.
     *
     * @return checkpoints
     */
    public int getNumberOfCheckpoints() {
        return checkpoints.size() - 1;
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public ParkourKit getParkourKit() {
        return parkourKit;
    }

    public ParkourMode getParkourMode() {
        return parkourMode;
    }

    /**
     * Check if Course has a configured Max Death.
     *
     * @return has maximum deaths set
     */
    public boolean hasMaxDeaths() {
        return maxDeaths > 0;
    }

    /**
     * Get Course's maximum deaths.
     * Maximum number of deaths a player can accumulate before failing the course
     *
     * @return maximum deaths for course
     */
    public int getMaxDeaths() {
        return maxDeaths;
    }

    /**
     * Set Course's maximum deaths.
     *
     * @param maxDeaths
     */
    public void setMaxDeaths(int maxDeaths) {
        this.maxDeaths = maxDeaths;
    }

    /**
     * Check if Course has a configured Max Time.
     *
     * @return has maximum time set
     */
    public boolean hasMaxTime() {
        return maxTime > 0;
    }

    /**
     * Get Course's maximum time.
     * Maximum number of seconds a player has to complete the course
     *
     * @return maximum time in seconds
     */
    public int getMaxTime() {
        return maxTime;
    }

    /**
     * Set Course's maximum time
     *
     * @param maxTime
     */
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

}

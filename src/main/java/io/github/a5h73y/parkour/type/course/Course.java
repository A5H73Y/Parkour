package io.github.a5h73y.parkour.type.course;

import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.kit.ParkourKit;
import java.io.Serializable;
import java.util.List;

/**
 * Parkour Course.
 * Stores references to the Checkpoints that consist of the Course.
 * The ParkourKit will be used to apply the appropriate actions while on the course.
 */
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final List<Checkpoint> checkpoints;
    private final ParkourKit parkourKit;
    private final ParkourMode parkourMode;

    private final int maxDeaths;
    private final int maxTime;

    /**
     * Construct a Course from the details.
     * Max time and deaths will be determined from config.
     *
     * @param name course name
     * @param checkpoints {@link Checkpoint}s within the Course
     * @param parkourKit the {@link ParkourKit} for the Course
     * @param parkourMode the {@link ParkourMode} to apply
     */
    public Course(String name, List<Checkpoint> checkpoints, ParkourKit parkourKit, ParkourMode parkourMode) {
        this.name = name;
        this.checkpoints = checkpoints;
        this.parkourKit = parkourKit;
        this.parkourMode = parkourMode;
        this.maxDeaths = CourseInfo.getMaximumDeaths(name);
        this.maxTime = CourseInfo.getMaximumTime(name);
    }

    /**
     * Get Course's name.
     * @return course name in lowercase
     */
    public String getName() {
        return name;
    }

    /**
     * Get the Course's display name.
     * @return display course name
     */
    public String getDisplayName() {
        return CourseInfo.getCourseDisplayName(name);
    }

    /**
     * Get number of Checkpoints on Course.
     * @return number of checkpoints
     */
    public int getNumberOfCheckpoints() {
        return checkpoints.size() - 1;
    }

    /**
     * Get {@link Checkpoint}s on the Course.
     * @return Course Checkpoints
     */
    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    /**
     * Get the {@link ParkourKit} for the Course.
     * @return parkour kit
     */
    public ParkourKit getParkourKit() {
        return parkourKit;
    }

    /**
     * Get the {@link ParkourMode} for the Course.
     * @return parkour mode
     */
    public ParkourMode getParkourMode() {
        return parkourMode;
    }

    /**
     * Determine if Course has a configured maximum deaths.
     * @return has maximum deaths set
     */
    public boolean hasMaxDeaths() {
        return maxDeaths > 0;
    }

    /**
     * Get Course's maximum deaths.
     * Maximum number of deaths a player can accumulate before failing the Course.
     * @return maximum deaths for course
     */
    public int getMaxDeaths() {
        return maxDeaths;
    }

    /**
     * Determine if Course has a configured maximum time limit.
     * @return has maximum time set
     */
    public boolean hasMaxTime() {
        return maxTime > 0;
    }

    /**
     * Get Course's maximum time.
     * Maximum number of seconds a player can accumulate before failing the Course.
     * @return maximum time in seconds
     */
    public int getMaxTime() {
        return maxTime;
    }

}

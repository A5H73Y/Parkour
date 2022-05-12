package io.github.a5h73y.parkour.type.course;

import static io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable.getMapValue;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.ParkourConstants;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.kit.ParkourKit;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Parkour Course.
 * Stores reference to the Checkpoints that consist of the Course.
 * The ParkourKit will be used to apply the appropriate actions while on the course.
 */
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String displayName;
    private final List<Checkpoint> checkpoints;
    private final ParkourKit parkourKit;
    private final ParkourMode parkourMode;
    private final CourseSettings settings;

    /**
     * Construct a Course from the details.
     * Max time and deaths will be determined from config.
     *
     * @param name course name
     * @param checkpoints {@link Checkpoint}s within the Course
     * @param parkourKit the {@link ParkourKit} for the Course
     * @param parkourMode the {@link ParkourMode} to apply
     */
    public Course(String name, String displayName, List<Checkpoint> checkpoints,
                  ParkourKit parkourKit, ParkourMode parkourMode, CourseSettings settings) {
        this.name = name;
        this.displayName = displayName;
        this.checkpoints = checkpoints;
        this.parkourKit = parkourKit;
        this.parkourMode = parkourMode;
        this.settings = settings;
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
     * @return course display name
     */
    public String getDisplayName() {
        return StringUtils.colour(displayName);
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
     * Get the {@link CourseSettings} for the Course.
     * @return course settings
     */
    public CourseSettings getSettings() {
        return settings;
    }

    /**
     * Deserialize the Course instane from the JSON.
     * @param input config key / values
     * @return populated Course
     */
    public static Course deserialize(Map<String, Object> input) {
        String name = String.valueOf(input.get("Name"));
        String displayName = String.valueOf(input.getOrDefault("DisplayName", input.get("Name")));
        String parkourModeName = String.valueOf(input.getOrDefault("ParkourMode", ParkourMode.NONE));
        String parkourKitName = String.valueOf(input.getOrDefault("ParkourKit", ParkourConstants.DEFAULT));

        List<Checkpoint> checkpoints = new ArrayList<>();
        Parkour parkour = Parkour.getInstance();
        ParkourKit parkourKit = parkour.getParkourKitManager().getParkourKit(parkourKitName);

        if (input.containsKey("Checkpoint")) {
            checkpoints = getMapValue(input.get("Checkpoint")).entrySet().stream()
                    .filter(checkpointMap -> ValidationUtils.isPositiveInteger(checkpointMap.getKey()))
                    .sorted(Comparator.comparing(checkpointMap -> Integer.valueOf(checkpointMap.getKey())))
                    .map(checkpointMap -> getMapValue(checkpointMap.getValue()))
                    .map(Checkpoint::deserialize)
                    .collect(Collectors.toList());
        }

        CourseSettings settings = parkour.getCourseSettingsManager().getCourseSettings(name);

        return new Course(name, displayName, checkpoints, parkourKit,
                ParkourMode.valueOf(parkourModeName), settings);
    }
}

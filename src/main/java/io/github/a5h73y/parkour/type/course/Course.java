package io.github.a5h73y.parkour.type.course;

import static io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable.getMapValue;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable;
import io.github.a5h73y.parkour.other.ParkourConstants;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.kit.ParkourKit;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

/**
 * Parkour Course.
 * Stores references to the Checkpoints that consist of the Course.
 * The ParkourKit will be used to apply the appropriate actions while on the course.
 */
public class Course implements ParkourSerializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String displayName;
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
    public Course(String name, String displayName, List<Checkpoint> checkpoints,
                  ParkourKit parkourKit, ParkourMode parkourMode, int maxDeaths, int maxTime) {
        this.name = name;
        this.displayName = displayName;
        this.checkpoints = checkpoints;
        this.parkourKit = parkourKit;
        this.parkourMode = parkourMode;
        this.maxDeaths = maxDeaths;
        this.maxTime = maxTime;
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
        return displayName;
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

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("Name", this.getName());
        data.put("DisplayName", this.getDisplayName());
        data.put("ParkourMode", this.getParkourMode().name());
        data.put("ParkourKit", this.getParkourKit().getName());
        data.put("MaxDeaths", this.getMaxDeaths());
        data.put("MaxTime", this.getMaxTime());
        data.put("Checkpoint", this.getCheckpoints().stream().map(Checkpoint::serialize).collect(Collectors.toList()));

        return data;
    }

    public static Course deserialize(Map<String, Object> input) {
        String name = String.valueOf(input.get("Name"));
        String displayName = String.valueOf(input.getOrDefault("DisplayName", input.get("Name")));
        String parkourModeName = String.valueOf(input.getOrDefault("ParkourMode", ParkourMode.NONE));
        String parkourKitName = String.valueOf(input.getOrDefault("ParkourKit", ParkourConstants.DEFAULT));
        int maxDeaths = NumberConversions.toInt(input.get("MaxDeaths"));
        int maxTime = NumberConversions.toInt(input.get("MaxTime"));

        List<Checkpoint> checkpoints = new ArrayList<>();
        ParkourKit parkourKit = Parkour.getInstance().getParkourKitManager().getParkourKit(parkourKitName);

        if (input.containsKey("Checkpoint")) {
            checkpoints = getMapValue(input.get("Checkpoint")).entrySet().stream()
                    .filter(checkpointMap -> ValidationUtils.isPositiveInteger(checkpointMap.getKey()))
                    .sorted(Comparator.comparing(checkpointMap -> Integer.valueOf(checkpointMap.getKey())))
                    .map(checkpointMap -> getMapValue(checkpointMap.getValue()))
                    .map(Checkpoint::deserialize)
                    .collect(Collectors.toList());
        }

        return new Course(name, displayName, checkpoints, parkourKit,
                ParkourMode.valueOf(parkourModeName), maxDeaths, maxTime);
    }
}

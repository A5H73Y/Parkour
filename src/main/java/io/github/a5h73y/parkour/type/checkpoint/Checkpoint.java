package io.github.a5h73y.parkour.type.checkpoint;

import static io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable.getMapValue;

import io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

/**
 * A Course Checkpoint.
 * Consists of fields that create a {@link Location} to teleport the player to.
 * The {@code next} coordinates are used for detection of the next pressure plate.
 */
public class Checkpoint implements ParkourSerializable {

    private static final long serialVersionUID = 1L;

    private final Location location;

    private final double checkpointX;
    private final double checkpointY;
    private final double checkpointZ;

    /**
     * Construct a Checkpoint from a location and next checkpoint coordinates.
     *
     * @param location player's location.
     * @param checkpointX checkpoint x coordinate.
     * @param checkpointY checkpoint y coordinate.
     * @param checkpointZ checkpoint z coordinate.
     */
    public Checkpoint(Location location, double checkpointX, double checkpointY, double checkpointZ) {
        this.location = location;
        this.checkpointX = checkpointX;
        this.checkpointY = checkpointY;
        this.checkpointZ = checkpointZ;
    }

    /**
     * Get the World of the Checkpoint.
     * @return checkpoint world
     */
    public String getWorldName() {
        return this.location.getWorld().getName();
    }

    public Location getLocation() {
        return location;
    }

    /**
     * The next checkpoint's X location.
     * @return next X location
     */
    public double getCheckpointX() {
        return checkpointX;
    }

    /**
     * The next checkpoint's Y location.
     * @return next Y location
     */
    public double getCheckpointY() {
        return checkpointY;
    }

    /**
     * The next checkpoint's Z location.
     * @return next Z location
     */
    public double getCheckpointZ() {
        return checkpointZ;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("Location", this.getLocation().serialize());
        data.put("CheckpointX", this.getCheckpointX());
        data.put("CheckpointY", this.getCheckpointY());
        data.put("CheckpointZ", this.getCheckpointZ());

        return data;
    }

    /**
     * Deserialize the Checkpoint from JSON.
     * @param input key value map
     * @return populated Checkpoint
     */
    public static Checkpoint deserialize(Map<String, Object> input) {
        Location checkpointLocation = Location.deserialize(getMapValue(input.get("Location")));
        double checkpointX = NumberConversions.toDouble(input.get("CheckpointX"));
        double checkpointY = NumberConversions.toDouble(input.get("CheckpointY"));
        double checkpointZ = NumberConversions.toDouble(input.get("CheckpointZ"));
        return new Checkpoint(checkpointLocation, checkpointX, checkpointY, checkpointZ);
    }

}

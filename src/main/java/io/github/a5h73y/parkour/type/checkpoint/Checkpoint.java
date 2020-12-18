package io.github.a5h73y.parkour.type.checkpoint;

import java.io.Serializable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * A Course Checkpoint.
 * Consists of fields that create a {@link Location} to teleport the player to.
 * The {@code next} coordinates are used for detection of the next pressure plate.
 */
public class Checkpoint implements Serializable {

    private static final long serialVersionUID = 1L;

    private final double x;
    private final double y;
    private final double z;
    private final float pitch;
    private final float yaw;
    private final String world;

    private final double nextCheckpointX;
    private final double nextCheckpointY;
    private final double nextCheckpointZ;

    /**
     * Construct a Checkpoint from a location and next checkpoint coordinates.
     *
     * @param location player's location.
     * @param nextCheckpointX next checkpoint x coordinate.
     * @param nextCheckpointY next checkpoint y coordinate.
     * @param nextCheckpointZ next checkpoint z coordinate.
     */
    public Checkpoint(Location location, double nextCheckpointX, double nextCheckpointY, double nextCheckpointZ) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.world = location.getWorld().getName();
        this.nextCheckpointX = nextCheckpointX;
        this.nextCheckpointY = nextCheckpointY;
        this.nextCheckpointZ = nextCheckpointZ;
    }

    /**
     * Create a {@link Location} from Checkpoint coordinates.
     * @return checkpoint location
     */
    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    /**
     * Get the World of the Checkpoint.
     * @return checkpoint world
     */
    public String getWorld() {
        return world;
    }

    /**
     * The next checkpoint's X location.
     * @return next X location
     */
    public double getNextCheckpointX() {
        return nextCheckpointX;
    }

    /**
     * The next checkpoint's Y location.
     * @return next Y location
     */
    public double getNextCheckpointY() {
        return nextCheckpointY;
    }

    /**
     * The next checkpoint's Z location.
     * @return next Z location
     */
    public double getNextCheckpointZ() {
        return nextCheckpointZ;
    }
}

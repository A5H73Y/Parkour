package me.A5H73Y.Parkour.Course;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;

public class Checkpoint implements Serializable {

	private static final long serialVersionUID = 1L;

	private double x, y, z;
	private float pitch, yaw;
	private String world;
	private double nextCheckpointX, nextCheckpointY, nextCheckpointZ;

	/**
	 * The location is used for where the player is physically teleported.
	 * The nextCheckpoint coordinates are used for the detection of the next pressure plate.
	 * As locations are transient, we must reconstruct the Checkpoints after a reload.
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

	public String getWorld() {
		return world;
	}

	public double getNextCheckpointX() {
		return nextCheckpointX;
	}

	public double getNextCheckpointY() {
		return nextCheckpointY;
	}

	public double getNextCheckpointZ() {
		return nextCheckpointZ;
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}
}

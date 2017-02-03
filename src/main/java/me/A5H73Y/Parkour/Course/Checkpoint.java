package me.A5H73Y.Parkour.Course;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Checkpoint implements Serializable {

	private static final long serialVersionUID = 1L;

	private double x, y, z;
	private float pitch, yaw;
	private String world;
	private double nextCheckpointX, nextCheckpointY, nextCheckpointZ;

	/**
	 * The location is used for where the player is phycially teleported. The
	 * coordinates are used for the detection of the pressure plate.
	 * As locations are transient, when retrieving all the sessions, the plugin was forgetting the locations after a reload.
	 * Although it's ugly, it will fix all the problems.
	 */
	public Checkpoint(double x, double y, double z, float yaw, float pitch, String world, double nextCheckpointX, double nextCheckpointY, double nextCheckpointZ){
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.world = world;
		this.nextCheckpointX = nextCheckpointX;
		this.nextCheckpointY = nextCheckpointY;
		this.nextCheckpointZ = nextCheckpointZ;
	}

	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	public float getYaw() {
		return yaw;
	}
	public float getPitch() {
		return pitch;
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

	public Location getLocation(){
		return new Location(Bukkit.getWorld(world), x, y, z , yaw, pitch);
	}
}

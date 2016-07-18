package me.A5H73Y.Parkour.Course;

import java.io.Serializable;

import org.bukkit.Location;

public class Checkpoint implements Serializable{

	private static final long serialVersionUID = 1L;
	
	transient Location location;
	private double x, y, z;
	
	/**
	 * The location is used for where the player is phycially teleported.
	 * The coordinates are used for the detection of the pressure plate.
	 * 
	 * @param location
	 * @param x
	 * @param y
	 * @param z
	 */
	public Checkpoint(Location location, double x, double y, double z){
		this.location = location;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Location getLocation(){
		return location;
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
}

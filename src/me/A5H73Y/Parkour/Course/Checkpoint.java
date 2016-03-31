package me.A5H73Y.Parkour.Course;

import java.io.Serializable;

import org.bukkit.Location;

public class Checkpoint implements Serializable{

	private static final long serialVersionUID = 1L;
	
	transient Location location;
	private double x, y, z;
	
	//location - Where the player teleports
	//checkpoint - The co-ords for the activation of pressure plate

	public Checkpoint(Location location, double x, double y, double z){
		this.location = location;
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

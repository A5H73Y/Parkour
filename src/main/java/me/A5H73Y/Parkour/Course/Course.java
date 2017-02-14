package me.A5H73Y.Parkour.Course;

import java.io.Serializable;

import me.A5H73Y.Parkour.Other.ParkourBlocks;
import me.A5H73Y.Parkour.Utilities.Static;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class Course implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private Checkpoint checkpoint;
	private Integer maxDeaths;
	private ParkourBlocks parkourBlocks;
	private int checkpoints;

	public Course(String name, Checkpoint checkpoint) {
		this.name = name;
		this.checkpoint = checkpoint;
		this.parkourBlocks = Static.getParkourBlocks();
		this.checkpoints = CheckpointMethods.getNumberOfCheckpoints(name);
	}

	public String getName() {
		return name;
	}

	public Checkpoint getCheckpoint() {
		return checkpoint;
	}

	public void setMaxDeaths(Integer maxDeaths) {
		this.maxDeaths = maxDeaths;
	}

	public Integer getMaxDeaths() {
		return maxDeaths;
	}

	public ParkourBlocks getParkourBlocks() {
		return parkourBlocks;
	}

	public void setParkourBlocks(ParkourBlocks parkourBlocks) {
		this.parkourBlocks = parkourBlocks;
	}

	public int getCheckpoints(){
		return checkpoints;
	}

	public void setCheckpoint(Checkpoint checkpoint){
		this.checkpoint = checkpoint;
	}

}

package me.A5H73Y.Parkour.Course;

import java.io.Serializable;
import java.util.List;

import me.A5H73Y.Parkour.Other.ParkourBlocks;

public class Course implements Serializable{	
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private List<Checkpoint> checkpoints;
	private Integer maxDeaths;
	private ParkourBlocks parkourBlocks;
	
	public Course(String name, List<Checkpoint> checkpoints, ParkourBlocks parkourBlocks) {
		this.name = name;
		this.checkpoints = checkpoints;
		this.parkourBlocks = parkourBlocks;
	}

	public String getName() {
		return name;
	}
	
	public List<Checkpoint> getCheckpoints() {
		return checkpoints;
	}
	
	public void setMaxDeaths(Integer maxDeaths){
		this.maxDeaths = maxDeaths;
	}
	
	public Integer getMaxDeaths(){
		return maxDeaths;
	}
	
	public ParkourBlocks getParkourBlocks(){
		return parkourBlocks;
	}
	
}

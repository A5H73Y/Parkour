package me.A5H73Y.Parkour.Player;

import java.io.Serializable;

import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Utilities.Utils;


public class PPlayer implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int deaths;
	private int checkpoint;
	private long timestarted;
	private Course course;
	private String mode;
	
	/**
	 * This is the PPlayer, which kind of stands for Parkour Player..? 
	 * We use this object to track all the players information while on a course, how many times they've died etc.
	 * When we want to find out what course a player is on, we will retrieve it through here, Rather than doing a course lookup for a player name.
	 * There is also a "Mode" string, this will be used to remember different gametypes (in potential updates), one example is CodJumper mode (Which allows you to set checkpoints whereever)
	 * @param course
	 */
	public PPlayer(Course course){
		this.deaths = 0;
		this.checkpoint = 0;
		this.timestarted = System.currentTimeMillis();
		this.course = course;
	}

	public int getDeaths(){
		return deaths;
	}

	public int getCheckpoint(){
		return checkpoint;
	}
	
	public Course getCourse(){
		return course;
	}
	
	public void resetTimeStarted(){
		this.timestarted = System.currentTimeMillis();
	}

	public void increaseCheckpoint(){
		checkpoint++;
	}

	public void increaseDeath(){
		deaths++;
	}
	
	public long getTime(){
		return System.currentTimeMillis() - timestarted;
	}
	
	public String displayTime(){
		return Utils.calculateTime(getTime());
	}
	
	public void setMode(String mode){
		this.mode = mode;
	}
	
	public String getMode(){
		return mode;
	}
}

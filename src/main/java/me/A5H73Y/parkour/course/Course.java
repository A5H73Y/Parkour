package me.A5H73Y.parkour.course;

import me.A5H73Y.parkour.kit.ParkourKit;
import me.A5H73Y.parkour.other.Constants;

import java.io.Serializable;

public class Course implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private Checkpoint currentCheckpoint;
	private Integer maxDeaths;
	private Integer maxTime;
	private int checkpoints;

	// Because of the Material Enum changes in 1.13 this will be transient
	// To stop the errors occurring when loading the ParkourSessions the
	// ParkourKit will be loaded on startup and set manually
	private transient ParkourKit parkourKit;

	/**
	 * Construct a Parkour Course.
	 * @param name course name
	 * @param checkpoint starting checkpoint
	 * @param parkourKit linked ParkourKit
	 */
	public Course(String name, Checkpoint checkpoint, ParkourKit parkourKit) {
		this.name = name;
		this.currentCheckpoint = checkpoint;
		this.parkourKit = parkourKit;
		this.checkpoints = CourseInfo.getCheckpointAmount(name);
		setMaxDeaths(CourseInfo.getMaximumDeaths(name));
		setMaxTime(CourseInfo.getMaximumTime(name));
	}

	/**
	 * Construct a Parkour Course.
	 * The ParkourKit will use 'default'.
	 * @param name course name
	 * @param checkpoint starting checkpoint
	 */
	public Course(String name, Checkpoint checkpoint) {
		this(name, checkpoint, ParkourKit.getParkourKit(Constants.DEFAULT));
	}

	/**
	 * Get Course's name.
	 * This will be lowercase
	 * @return Course Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the current checkpoint achieved.
	 * @return Checkpoint
	 */
	public Checkpoint getCurrentCheckpoint() {
		return currentCheckpoint;
	}

	/**
	 * Set the current achieved checkpoint.
	 * @param checkpoint
	 */
	public void setCheckpoint(Checkpoint checkpoint) {
		this.currentCheckpoint = checkpoint;
	}

	/**
	 * Get Course's maximum deaths.
	 * Maximum number of deaths a player can accumulate before failing the course
	 * @return maximum deaths for course
	 */
	public Integer getMaxDeaths() {
		return maxDeaths;
	}

	/**
	 * Set Course's maximum deaths.
	 * @param maxDeaths
	 */
	public void setMaxDeaths(Integer maxDeaths) {
		if (maxDeaths > 0) {
			this.maxDeaths = maxDeaths;
		}
	}

	/**
	 * Check if Course has a configured Max Death.
	 * @return has maximum deaths set
	 */
	public boolean hasMaxDeaths() {
		return maxDeaths != null;
	}

	/**
	 * Get Course's maximum time.
	 * Maximum number of seconds a player has to complete the course
	 * @return maximum time in seconds
	 */
	public Integer getMaxTime() {
		return maxTime;
	}

	/**
	 * Set Course's maximum time
	 * @param maxTime
	 */
	public void setMaxTime(Integer maxTime) {
		if (maxTime > 0) {
			this.maxTime = maxTime;
		}
	}

	/**
	 * Check if Course has a configured Max Time.
	 * @return has maximum time set
	 */
	public boolean hasMaxTime() {
		return maxTime != null;
	}

	/**
	 * ParkourKit for the Course.
	 * This will be loaded when the course object is created
	 * @return ParkourKit
	 */
	public ParkourKit getParkourKit() {
		return parkourKit;
	}

	/**
	 * Set ParkourKit for the Course.
	 * @param parkourKit
	 */
	public void setParkourKit(ParkourKit parkourKit) {
		this.parkourKit = parkourKit;
	}

	/**
	 * Number of checkpoints on the course.
	 * @return Count of Course's checkpoints
	 */
	public int getCheckpoints() {
		return checkpoints;
	}
}

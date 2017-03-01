package me.A5H73Y.Parkour.Player;

import java.io.Serializable;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CheckpointMethods;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Enums.ParkourMode;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class ParkourSession implements Serializable {

	private static final long serialVersionUID = 1L;

	private int deaths;
	private int checkpoint;
	private long timestarted;
	private Course course;
	private ParkourMode mode;
	private boolean useTrail;
	private int seconds;

	/**
	 * This is the ParkourSession object.
	 * We use this object to track all the players information while on a course, how
	 * many times they've died etc. When we want to find out what course a
	 * player is on, we will retrieve it through here, Rather than doing a
	 * course lookup for a player name. There is also a "Mode" string, this will
	 * be used to remember different gametypes (in potential updates), one
	 * example is Freedom mode (Which allows you to set checkpoints whereever)
	 * 
	 * @param course
	 */
	public ParkourSession(Course course) {
		this.deaths = 0;
		this.checkpoint = 0;
		this.timestarted = System.currentTimeMillis();
		this.course = course;
		this.mode = CourseMethods.getCourseMode(course.getName());
	}
	
	public void startVisualTimer(final Player player) {
		if (!Static.getBountifulAPI() ||
				!Parkour.getParkourConfig().getConfig().getBoolean("OnCourse.DisplayLiveTime") || 
				Static.containsQuiet(player.getName()))
			return;
		
		new BukkitRunnable() {
            @Override
            public void run() {
            	if (!PlayerMethods.isPlaying(player.getName())) {
            		Utils.sendActionBar(player, "");
            		cancel();
            	} else {
            		Utils.sendActionBar(player, Utils.convertSecondsToTime(++seconds));
            	}
            }
        }.runTaskTimer(Parkour.getPlugin(), 20, 20);
	}

	public int getDeaths() {
		return deaths;
	}

	public int getCheckpoint() {
		return checkpoint;
	}

	public void setCheckpoint(int checkpoint) {
		this.checkpoint = checkpoint;
	}

	public Course getCourse() {
		return course;
	}

	public void resetTimeStarted() {
		this.timestarted = System.currentTimeMillis();
	}

	public void increaseCheckpoint() {
		checkpoint++;
		course.setCheckpoint(CheckpointMethods.getNextCheckpoint(course.getName(), checkpoint));
	}

	public void increaseDeath() {
		deaths++;
	}

	public long getTime() {
		return System.currentTimeMillis() - timestarted;
	}

	public String displayTime() {
		return Utils.calculateTime(getTime());
	}

	public void setMode(ParkourMode mode) {
		this.mode = mode;
	}

	public ParkourMode getMode() {
		return mode;
	}

	public boolean getUseTrail() {
		return useTrail;
	}
}

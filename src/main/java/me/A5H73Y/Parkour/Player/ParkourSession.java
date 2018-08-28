package me.A5H73Y.Parkour.Player;

import java.io.Serializable;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CheckpointMethods;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Enums.ParkourMode;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ParkourSession implements Serializable {

	private static final long serialVersionUID = 1L;

	private int deaths;
	private int checkpoint;
	private long timestarted;
	private Course course;
	private ParkourMode mode;
    private int seconds;

    private String liveTime;

    private int taskId = 0;

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
				!Parkour.getPlugin().getConfig().getBoolean("OnCourse.DisplayLiveTime") ||
				Static.containsQuiet(player.getName()))
			return;
		
		BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                liveTime = Utils.convertSecondsToTime(++seconds);

                //TODO if scoreboard is uninstalled, or disabled
                Utils.sendActionBar(player, liveTime, true);
            }
        }.runTaskTimer(Parkour.getPlugin(), 20, 20);

		taskId = task.getTaskId();
	}

	public void cancelVisualTimer() {
	    if (taskId > 0) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = 0;
        }
    }

    /**
     * Get the current time of the ParkourSession
     * @return String in %02d:%02d:%02d format
     */
    public String getLiveTime() { return liveTime; }

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
		seconds = 0;
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
		return Utils.displayCurrentTime(getTime());
	}

	public void setMode(ParkourMode mode) {
		this.mode = mode;
	}

	public ParkourMode getMode() {
		return mode;
	}

	public void restartSession() {
	    checkpoint = 0;
	    deaths = 0;
	    resetTimeStarted();
    }
}

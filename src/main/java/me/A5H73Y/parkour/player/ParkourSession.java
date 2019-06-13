package me.A5H73Y.parkour.player;

import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.course.CheckpointMethods;
import me.A5H73Y.parkour.course.Course;
import me.A5H73Y.parkour.course.CourseMethods;
import me.A5H73Y.parkour.enums.ParkourMode;
import me.A5H73Y.parkour.manager.QuietModeManager;
import me.A5H73Y.parkour.utilities.Static;
import me.A5H73Y.parkour.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.Serializable;

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
		if (!Parkour.getPlugin().getConfig().getBoolean("OnCourse.DisplayLiveTime")
				|| QuietModeManager.getInstance().isInQuietMode(player.getName())) {
			return;
		}

		BukkitTask task = new BukkitRunnable() {
			@Override
			public void run() {
				liveTime = Utils.convertSecondsToTime(++seconds);

				if (!player.isOnline()) {
					cancelVisualTimer();
					return;
				}

				boolean useScoreboard = Parkour.getPlugin().getConfig().getBoolean("Scoreboard.Display.CurrentTime");

				if (Parkour.getScoreboardManager().isEnabled() && useScoreboard) {
					Parkour.getScoreboardManager().updateScoreboardTimer(player, liveTime);

				} else if (Static.getBountifulAPI()) {
					Utils.sendActionBar(player, liveTime, true);
				}

				if (course.hasMaxTime()) {
					if (seconds >= course.getMaxTime()) {
						player.sendMessage(Utils.getTranslation("Parkour.MaxTime")
								.replace("%TIME%", Utils.convertSecondsToTime(course.getMaxTime())));
						PlayerMethods.playerLeave(player);
					}
				}
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
	public String getLiveTime() {
		return liveTime;
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
		course.setCheckpoint(CheckpointMethods.getNextCheckpoint(course.getName(), checkpoint));
		resetTimeStarted();
	}
}

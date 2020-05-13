package io.github.a5h73y.player;

import java.io.Serializable;

import io.github.a5h73y.Parkour;
import io.github.a5h73y.course.CheckpointMethods;
import io.github.a5h73y.course.Course;
import io.github.a5h73y.course.CourseMethods;
import io.github.a5h73y.enums.ParkourMode;
import io.github.a5h73y.manager.QuietModeManager;
import io.github.a5h73y.utilities.Static;
import io.github.a5h73y.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ParkourSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private int deaths;
    private int checkpoint;
    private long timeStarted;
    private long timeFinished;
    private final Course course;
    private final ParkourMode mode;
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
        this.timeStarted = System.currentTimeMillis();
        this.timeFinished = 0;
        this.course = course;
        this.mode = CourseMethods.getCourseMode(course.getName());
    }

    public void startVisualTimer(final Player player) {
        if (!Parkour.getInstance().getConfig().getBoolean("OnCourse.DisplayLiveTime")
                || QuietModeManager.getInstance().isInQuietMode(player.getName())) {
            return;
        }

        if (course.hasMaxTime()) {
            seconds = course.getMaxTime();
        }
        final String soundName = Utils.getTimerSound();
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (course.hasMaxTime()) {
                    liveTime = Utils.convertSecondsToTime(--seconds);
                } else {
                    liveTime = Utils.convertSecondsToTime(++seconds);
                }

                if (!player.isOnline()) {
                    cancelVisualTimer();
                    return;
                }
                if (course.hasMaxTime() && (seconds <= 5 || seconds == 10)) {
                    liveTime = ChatColor.RED + liveTime;

                    if (Parkour.getSettings().isSoundEnabled()) {
                        player.playSound(player.getLocation(), Sound.valueOf(soundName), 2.0f, 1.75f);
                    }
                }

                if (Parkour.getScoreboardManager().isEnabled()) {
                    Parkour.getScoreboardManager().updateScoreboardTimer(player, liveTime);
                }

                if (Static.getBountifulAPI()) {
                    Utils.sendActionBar(player, liveTime, true);
                }

                if (course.hasMaxTime()) {
                    if (seconds == 0) {
                        player.sendMessage(Utils.getTranslation("Parkour.MaxTime")
                                .replace("%TIME%", Utils.convertSecondsToTime(course.getMaxTime())));
                        PlayerMethods.playerLeave(player);
                    }
                }
            }
        }.runTaskTimer(Parkour.getInstance(), 20, 20);

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
     *
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
        this.timeStarted = System.currentTimeMillis();
        seconds = 0;
        if (course.hasMaxTime()) {
            seconds = course.getMaxTime();
        }
    }

    public void increaseCheckpoint() {
        checkpoint++;
        course.setCheckpoint(CheckpointMethods.getNextCheckpoint(course.getName(), checkpoint));
    }

    public void increaseDeath() {
        deaths++;
    }

    public void setTimeFinished() {
        timeFinished = getCurrentTime();
    }

    public long getTimeFinished() {
        return timeFinished;
    }

    public long getCurrentTime() {
        return System.currentTimeMillis() - timeStarted;
    }

    public String displayTime() {
        return Utils.displayCurrentTime(getCurrentTime());
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

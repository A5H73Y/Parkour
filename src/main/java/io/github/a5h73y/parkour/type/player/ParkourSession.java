package io.github.a5h73y.parkour.type.player;

import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import java.io.Serializable;
import org.bukkit.Location;

public class ParkourSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private String liveTime; //TODO what do?

    private int deaths;
    private int currentCheckpoint;

    private long timeStarted;
    private long timeFinished;

    private final Course course;
    private Location freedomLocation;

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
        this.timeStarted = System.currentTimeMillis();
        this.course = course;
    }

    public boolean hasAchievedAllCheckpoints() {
        return currentCheckpoint == course.getNumberOfCheckpoints();
    }

    public ParkourMode getParkourMode() {
        return course.getParkourMode();
    }

    public Checkpoint getCheckpoint() {
        return course.getCheckpoints().get(currentCheckpoint);
    }

//    /**
//     * Start the visual timer either on the ActionBar if DisplayLiveTime is true, or in the scoreboard
//     * if the scoreboard is enabled and the display current time option is true.
//     */
//    public void startVisualTimer(final Player player) {
//        if (Parkour.getInstance().getPlayerManager().isInQuietMode(player.getName()) ) {
//            return;
//        }
//        if (!Parkour.getDefaultConfig().getBoolean("OnCourse.DisplayLiveTime")
//                && (!Parkour.getInstance().getScoreboardManager().isEnabled()
//                || !Parkour.getDefaultConfig().getBoolean("Scoreboard.Display.CurrentTime"))) {
//            return;
//        }
//
//        if (course.hasMaxTime()) {
//            seconds = course.getMaxTime();
//        }
//        final String soundName = SoundUtils.getTimerSound();
//        BukkitTask task = new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (course.hasMaxTime()) {
//                    liveTime = DateTimeUtils.convertSecondsToTime(--seconds);
//                } else {
//                    liveTime = DateTimeUtils.convertSecondsToTime(++seconds);
//                }
//
//                if (!player.isOnline()) {
//                    cancelVisualTimer();
//                    return;
//                }
//                if (course.hasMaxTime() && (seconds <= 5 || seconds == 10)) {
//                    liveTime = ChatColor.RED + liveTime;
//
//                    if (Parkour.getDefaultConfig().isSoundEnabled()) {
//                        player.playSound(player.getLocation(), Sound.valueOf(soundName), 2.0f, 1.75f);
//                    }
//                }
//
//                if (Parkour.getInstance().getScoreboardManager().isEnabled()) {
//                    Parkour.getInstance().getScoreboardManager().updateScoreboardTimer(player, liveTime);
//                }
//
//                // TODO check config flag,
//                // if it's true, check server version, if unsupported action bar message, check for BountifulAPI
//                if (PluginUtils.getMinorServerVersion() > 13
//                        && Parkour.getDefaultConfig().getBoolean("OnCourse.DisplayLiveTime")) {
//                    Parkour.getInstance().getBountifulApi().sendActionBar(player, liveTime, true);
//                }
//
//                if (course.hasMaxTime() && seconds == 0) {
//                    String maxTime = DateTimeUtils.convertSecondsToTime(course.getMaxTime());
//                    TranslationUtils.sendValueTranslation("Parkour.MaxTime", maxTime, player);
//                    Parkour.getInstance().getPlayerManager().leaveCourse(player);
//                }
//            }
//        }.runTaskTimer(Parkour.getInstance(), 20, 20);
//
//        taskId = task.getTaskId();
//    }
//
//    public void cancelVisualTimer() {
//        if (taskId > 0) {
//            Bukkit.getScheduler().cancelTask(taskId);
//            taskId = 0;
//        }
//    }

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

    public int getCurrentCheckpoint() {
        return currentCheckpoint;
    }

    public void setCurrentCheckpoint(int currentCheckpoint) {
        this.currentCheckpoint = currentCheckpoint;
    }

    public Course getCourse() {
        return course;
    }

    public Location getFreedomLocation() {
        return freedomLocation;
    }

    public void setFreedomLocation(Location freedomLocation) {
        this.freedomLocation = freedomLocation;
    }

    public void resetTimeStarted() {
        this.timeStarted = System.currentTimeMillis();
//        if (course.hasMaxTime()) { //TODO
//            seconds = course.getMaxTime();
//        }
    }

    public void increaseCheckpoint() {
        currentCheckpoint++;
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
        return DateTimeUtils.displayCurrentTime(getCurrentTime());
    }

    public void restartSession() {
        currentCheckpoint = 0;
        deaths = 0;
        resetTimeStarted();
    }
}

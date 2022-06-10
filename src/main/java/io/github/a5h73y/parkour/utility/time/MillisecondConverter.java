package io.github.a5h73y.parkour.utility.time;

/**
 * Millisecond converter.
 * Converts ms to date time representations.
 */
public class MillisecondConverter {

    private long milliseconds;
    private long seconds;
    private long minutes;
    private long hours;
    private long days;

    /**
     * Convert milliseconds into different divisions.
     *
     * @param milliseconds milliseconds
     */
    public MillisecondConverter(long milliseconds) {
        this.milliseconds = milliseconds;
        this.seconds = milliseconds / 1000;
        this.minutes = seconds / 60;
        this.hours = minutes / 60;
        this.days = hours / 24;
    }

    public long getMilliseconds() {
        return milliseconds % 1000;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public long getSeconds() {
        return seconds % 60;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public long getMinutes() {
        return minutes % 60;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getHours() {
        return hours % 24;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }
}

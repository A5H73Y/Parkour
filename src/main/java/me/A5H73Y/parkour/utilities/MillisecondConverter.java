package me.A5H73Y.parkour.utilities;

public class MillisecondConverter {

    private long milliseconds;
    private long seconds;
    private long minutes;
    private long hours;
    private long days;

    /**
     * Convert milliseconds into different divisions
     * @param millis
     */
    public MillisecondConverter(long millis) {
        this.milliseconds = millis;
        this.seconds = millis / 1000;
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

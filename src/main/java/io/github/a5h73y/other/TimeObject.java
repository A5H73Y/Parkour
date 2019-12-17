package io.github.a5h73y.other;

public class TimeObject {

    private String player;
    private long time;
    private int deaths;

    public TimeObject(String player, long time, int deaths) {
        this.player = player;
        this.time = time;
        this.deaths = deaths;
    }

    public String getPlayer() {
        return player;
    }

    public long getTime() {
        return time;
    }

    public int getDeaths() {
        return deaths;
    }
}

package me.A5H73Y.Parkour.Other;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
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

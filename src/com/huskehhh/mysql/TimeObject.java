package com.huskehhh.mysql;

public class TimeObject {

	private String course, player;
	private long time;
	private int deaths;
	
	public TimeObject(String course, String player, long time, int deaths) {
		this.course = course;
		this.player = player;
		this.time = time;
		this.deaths = deaths;
	}
	
	public String getCourse() {
		return course;
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

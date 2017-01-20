package me.A5H73Y.Parkour.Other;

public class Challenge {

	private String player;
	private String targetPlayer;
	private String courseName;

	/**
	 * Challenge player
	 * Created to manage who started the challenge, who's the recipient and which on course.
	 * 
	 * @param player
	 * @param targetPlayer
	 * @param courseName
	 */
	public Challenge(String player, String targetPlayer, String courseName){
		this.player = player;
		this.targetPlayer = targetPlayer;
		this.courseName = courseName;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getTargetPlayer() {
		return targetPlayer;
	}

	public void setTargetPlayer(String targetPlayer) {
		this.targetPlayer = targetPlayer;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
}

package me.A5H73Y.Parkour.Other;

import java.util.ArrayList;
import java.util.List;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class Challenge {

    private static final List<Challenge> challenges = new ArrayList<>();

	private final String player;
	private final String targetPlayer;
	private final String courseName;

	/**
	 * Challenge player
	 * Created to manage who started the challenge, who's the recipient and which on course.
	 * 
	 * @param player
	 * @param targetPlayer
	 * @param courseName
	 */
	private Challenge(String player, String targetPlayer, String courseName){
		this.player = player;
		this.targetPlayer = targetPlayer;
		this.courseName = courseName;
	}

	public String getPlayer() {
		return player;
	}

	public String getTargetPlayer() {
		return targetPlayer;
	}

	public String getCourseName() {
		return courseName;
	}

	public static Challenge challengePlayer(String player1, String player2, String courseName) {
	    Challenge challenge = new Challenge(player1, player2, courseName);
	    challenges.add(challenge);
	    return challenge;
    }

    public static void removeChallenge(Challenge challenge){
        if (challenges.contains(challenge))
            challenges.remove(challenge);
    }

    /**
     * Find the challenge the recipient player has recieved.
     * @param targetPlayer
     * @return
     */
    public static Challenge getChallenge(String targetPlayer){
        for (Challenge challenge : challenges){
            if (challenge.getTargetPlayer().equals(targetPlayer))
                return challenge;
        }
        return null;
    }
}

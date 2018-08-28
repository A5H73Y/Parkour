package me.A5H73Y.Parkour.Other;

import java.util.HashSet;
import java.util.Set;

public class Challenge {

    private static final Set<Challenge> challenges = new HashSet<>();

	private final String senderPlayer;
	private final String receiverPlayer;
	private final String courseName;

	/**
	 * Challenge player
	 * Created to manage who started the challenge, who's the receiver and which on course.
	 * 
	 * @param senderPlayer
	 * @param receiverPlayer
	 * @param courseName
	 */
	private Challenge(String senderPlayer, String receiverPlayer, String courseName) {
		this.senderPlayer = senderPlayer;
		this.receiverPlayer = receiverPlayer;
		this.courseName = courseName;
	}

	public String getSenderPlayer() {
		return senderPlayer;
	}

	public String getReceiverPlayer() {
		return receiverPlayer;
	}

	public String getCourseName() {
		return courseName;
	}

	/**
	 * Challenge two players to a course
	 * @param senderPlayer
	 * @param receiverPlayer
	 * @param courseName
	 * @return
	 */
	public static Challenge challengePlayer(String senderPlayer, String receiverPlayer, String courseName) {
	    Challenge challenge = new Challenge(senderPlayer, receiverPlayer, courseName);
	    challenges.add(challenge);
	    return challenge;
    }

    public static void removeChallenge(Challenge challenge) {
        if (challenges.contains(challenge))
            challenges.remove(challenge);
    }

    /**
     * Find the challenge the recipient senderPlayer has received.
     * @param playerName
     * @return
     */
    public static Challenge getChallenge(String playerName) {
        for (Challenge challenge : challenges) {
            if (challenge.getReceiverPlayer().equals(playerName) || challenge.getSenderPlayer().equals(playerName))
                return challenge;
        }
        return null;
    }

    public static boolean isPlayerInChallenge(String playerName) {
		return getChallenge(playerName) != null;
	}
}

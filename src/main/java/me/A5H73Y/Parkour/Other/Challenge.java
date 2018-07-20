package me.A5H73Y.Parkour.Other;

import java.util.ArrayList;
import java.util.List;

public class Challenge {

    private static final List<Challenge> challenges = new ArrayList<>();

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
	private Challenge(String senderPlayer, String receiverPlayer, String courseName){
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

	public static Challenge challengePlayer(String senderPlayer, String receiverPlayer, String courseName) {
	    Challenge challenge = new Challenge(senderPlayer, receiverPlayer, courseName);
	    challenges.add(challenge);
	    return challenge;
    }

    public static void removeChallenge(Challenge challenge){
        if (challenges.contains(challenge))
            challenges.remove(challenge);
    }

    /**
     * Find the challenge the recipient senderPlayer has received.
     * @param receiverPlayer
     * @return
     */
    public static Challenge getChallenge(String receiverPlayer){
        for (Challenge challenge : challenges){
            if (challenge.getReceiverPlayer().equals(receiverPlayer))
                return challenge;
        }
        return null;
    }
}

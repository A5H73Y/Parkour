package me.A5H73Y.Parkour.Managers;

import java.util.HashSet;
import java.util.Set;

public class ChallengeManager {

    private static ChallengeManager instance;

    private static final Set<Challenge> challenges = new HashSet<>();

    public static ChallengeManager getInstance() {
        if (instance == null) {
            instance = new ChallengeManager();
        }

        return instance;
    }

    private ChallengeManager() {}

	/**
	 * Challenge two players to a course
	 * @param senderPlayer
	 * @param receiverPlayer
	 * @param courseName
	 * @return
	 */
	public Challenge challengePlayer(String senderPlayer, String receiverPlayer, String courseName) {
	    Challenge challenge = new Challenge(senderPlayer, receiverPlayer, courseName);
	    challenges.add(challenge);
	    return challenge;
    }

    public void removeChallenge(Challenge challenge) {
        if (challenges.contains(challenge))
            challenges.remove(challenge);
    }

    /**
     * Find the challenge the recipient senderPlayer has received.
     * @param playerName
     * @return
     */
    public Challenge getChallengeForPlayer(String playerName) {
        for (Challenge challenge : challenges) {
            if (challenge.getReceiverPlayer().equals(playerName) || challenge.getSenderPlayer().equals(playerName))
                return challenge;
        }
        return null;
    }

    public boolean isPlayerInChallenge(String playerName) {
        return getChallengeForPlayer(playerName) != null;
    }

    public class Challenge {

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

    }
}

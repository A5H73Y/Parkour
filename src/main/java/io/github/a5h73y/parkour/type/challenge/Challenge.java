package io.github.a5h73y.parkour.type.challenge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.Player;

/**
 * The Challenge Model.
 * Contains the initial sender and receiver, and the course requested.
 * If a Wager is specified, this will only be processed if one of the players completes the course.
 */
public class Challenge {

    private final Player challengeHost;
    private final Map<Player, Boolean> participants = new HashMap<>();
    private final String courseName;
    private final Double wager;
    private boolean started;

    /**
     * Parkour Challenge.
     * Created to manage who started the challenge, who's the receiver and on which course.
     *
     * @param challengeHost sending player
     * @param courseName course name
     */
    public Challenge(Player challengeHost, String courseName, Double wager) {
        this.challengeHost = challengeHost;
        this.courseName = courseName;
        this.wager = wager;
    }

    public void addParticipant(Player player) {
        participants.put(player, false);
    }

    public Set<Player> getParticipatingPlayers() {
        return participants.keySet();
    }

    public boolean isPlayerParticipating(Player player) {
        return participants.containsKey(player);
    }

    public Map<Player, Boolean> getParticipantsForfeit() {
        return participants;
    }

    public int getNumberOfParticipants() {
        return participants.size();
    }

    public void markStarted() {
        started = true;
    }

    public boolean hasStarted() {
        return started;
    }

    public Player getChallengeHost() {
        return challengeHost;
    }

    public String getCourseName() {
        return courseName;
    }

    public Double getWager() {
        return wager;
    }

    public boolean isForfeited(Player player) {
        return participants.get(player);
    }

    public void setForfeited(Player player, boolean forfeited) {
        this.participants.put(player, forfeited);
    }

    public boolean allPlayersForfeited() {
        return !participants.containsValue(false);
    }

    @Override
    public String toString() {
        return "Challenge Host: " + challengeHost.getName()
                + ", \nParticipants: " + participants.size()
                + ", \nCourse: " + courseName
                + ", \nWager: " + wager
                + ", \nStarted: " + started;
    }
}

package io.github.a5h73y.parkour.manager;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChallengeManager extends AbstractPluginReceiver {

    private static final Set<Challenge> challenges = new HashSet<>();

    public ChallengeManager(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Challenge two players to a course.
     *
     * @param senderPlayer sending player
     * @param receiverPlayer receiving player
     * @param courseName course name
     * @param wager challenge wager
     * @return populated {@link Challenge}
     */
    public Challenge createChallenge(String senderPlayer, String receiverPlayer, String courseName, Double wager) {
        Challenge challenge = new Challenge(senderPlayer, receiverPlayer, courseName, wager);
        challenges.add(challenge);
        return challenge;
    }

    /**
     * Remove an instance of a Challenge.
     *
     * @param challenge {@link Challenge}
     */
    public void removeChallenge(Challenge challenge) {
        challenges.remove(challenge);
    }

    /**
     * Find the challenge the recipient senderPlayer has received.
     *
     * @param playerName target player name
     * @return matching {@link Challenge}
     */
    public Challenge getChallengeForPlayer(String playerName) {
        for (Challenge challenge : challenges) {
            if (challenge.getReceiverPlayer().equals(playerName)
                    || challenge.getSenderPlayer().equals(playerName)) {
                return challenge;
            }
        }
        return null;
    }

    /**
     * Determine if the player is currently in a Challenge.
     *
     * @param playerName target player name
     * @return player is in challenge
     */
    public boolean isPlayerInChallenge(String playerName) {
        return getChallengeForPlayer(playerName) != null;
    }

    /**
     * Terminate a Challenge.
     * In the event that someone leaves the course / server or
     * exceeds the maximum deaths set for the course. The first player to
     * leave forfeits the challenge. The remaining player must complete
     * the course to win the challenge.
     *
     * @param player terminating player
     */
    public void terminateChallenge(Player player) {
        Challenge challenge = getChallengeForPlayer(player.getName());

        if (challenge != null) {
            Player opponent = calculateOpponent(player.getName(), challenge);

            // if no player has forfeited yet
            if (!challenge.isForfeited()) {
                challenge.setForfeited(true);

                TranslationUtils.sendValueTranslation("Parkour.Challenge.Forfeited",
                        opponent.getName(), player);
                TranslationUtils.sendValueTranslation("Parkour.Challenge.Forfeited",
                        player.getName(), opponent);

            } else {
                // otherwise the challenge is completely terminated
                TranslationUtils.sendValueTranslation("Parkour.Challenge.Terminated",
                        player.getName(), player, opponent);
                removeChallenge(challenge);
            }
        }
    }

    /**
     * Complete the Challenge.
     * The winner has completed the course first.
     * If a wager is set, it will be deposited & withdrawn here.
     *
     * @param winner winning player
     */
    public void completeChallenge(Player winner) {
        Challenge challenge = getChallengeForPlayer(winner.getName());

        if (challenge != null) {
            Player opponent = calculateOpponent(winner.getName(), challenge);
            removeChallenge(challenge);

            winner.sendMessage(TranslationUtils.getTranslation("Parkour.Challenge.Winner")
                    .replace("%PLAYER%", opponent.getName())
                    .replace("%COURSE%", challenge.getCourseName()));
            opponent.sendMessage(TranslationUtils.getTranslation("Parkour.Challenge.Loser")
                    .replace("%PLAYER%", winner.getName())
                    .replace("%COURSE%", challenge.getCourseName()));

            if (challenge.getWager() != null) {
                parkour.getEconomyApi().rewardPlayer(winner, challenge.getWager());
                parkour.getEconomyApi().chargePlayer(opponent, challenge.getWager());
            }

            if (!challenge.isForfeited()) {
                parkour.getPlayerManager().leaveCourse(opponent);
            }
        }
    }

    /**
     * Derive the opponent player from the Challenge.
     *
     * @param playerName target player name
     * @param challenge {@link Challenge}
     * @return opponent player
     */
    public Player calculateOpponent(String playerName, Challenge challenge) {
        String opponent = challenge.getSenderPlayer().equals(playerName)
                ? challenge.getReceiverPlayer() : challenge.getSenderPlayer();

        return Bukkit.getPlayer(opponent);
    }

    /**
     * Accept a challenge.
     * Executed by the recipient of a challenge invite.
     * Will prepare each player for the challenge.
     *
     * @param receivingPlayer receiving player
     */
    public void acceptChallenge(final Player receivingPlayer) {
        Challenge challenge = getChallengeForPlayer(receivingPlayer.getName());

        if (challenge == null) {
            receivingPlayer.sendMessage(Parkour.getPrefix() + "You have not been invited!");
            return;
        }
        if (Bukkit.getPlayer(challenge.getSenderPlayer()) == null) {
            receivingPlayer.sendMessage(Parkour.getPrefix() + "Player is not online!");
            return;
        }

        final Player senderPlayer = Bukkit.getPlayer(challenge.getSenderPlayer());

        if (parkour.getConfig().getBoolean("ParkourModes.Challenge.hidePlayers")) {
            senderPlayer.hidePlayer(parkour, receivingPlayer);
            receivingPlayer.hidePlayer(parkour, senderPlayer);
        }

        parkour.getPlayerManager().joinCourse(senderPlayer, challenge.getCourseName());
        parkour.getPlayerManager().joinCourse(receivingPlayer, challenge.getCourseName());

        final float playerSpeed = senderPlayer.getWalkSpeed();
        final float targetSpeed = receivingPlayer.getWalkSpeed();

        senderPlayer.setWalkSpeed(0f);
        receivingPlayer.setWalkSpeed(0f);

        new Runnable() {
            final int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(parkour, this, 0L, 20L);

            int count = parkour.getConfig().getInt("ParkourModes.Challenge.CountdownFrom") + 1;

            @Override
            public void run() {
                if (count > 1) {
                    count--;
                    TranslationUtils.sendValueTranslation("Parkour.Countdown", String.valueOf(count),
                            senderPlayer, receivingPlayer);

                } else {
                    Bukkit.getScheduler().cancelTask(taskID);

                    TranslationUtils.sendTranslation("Parkour.Go", senderPlayer, receivingPlayer);
                    senderPlayer.setWalkSpeed(playerSpeed);
                    receivingPlayer.setWalkSpeed(targetSpeed);

                    parkour.getPlayerManager().getParkourSession(senderPlayer).resetTimeStarted();
                    parkour.getPlayerManager().getParkourSession(receivingPlayer).resetTimeStarted();
                }
            }
        };
    }

    /**
     * The Challenge Model.
     * Contains the initial sender and receiver, and the course requested.
     * If a Wager is specified, this will only be processed if one of the players completes the course.
     */
    public static class Challenge {

        private final String senderPlayer;
        private final String receiverPlayer;
        private final String courseName;

        private final Double wager;
        private boolean forfeited;

        /**
         * Parkour Challenge.
         * Created to manage who started the challenge, who's the receiver and on which course.
         *
         * @param senderPlayer sending player
         * @param receiverPlayer receiving player
         * @param courseName course name
         */
        private Challenge(String senderPlayer, String receiverPlayer, String courseName, Double wager) {
            this.senderPlayer = senderPlayer;
            this.receiverPlayer = receiverPlayer;
            this.courseName = courseName;
            this.wager = wager;
            this.forfeited = false;
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

        public Double getWager() {
            return wager;
        }

        public boolean isForfeited() {
            return forfeited;
        }

        public void setForfeited(boolean forfeited) {
            this.forfeited = forfeited;
        }

    }
}

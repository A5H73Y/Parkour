package me.A5H73Y.Parkour.Managers;

import java.util.HashSet;
import java.util.Set;

import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
     * Challenge two players to a course.
     *
     * @param senderPlayer
     * @param receiverPlayer
     * @param courseName
     * @return
     */
    public Challenge createChallenge(String senderPlayer, String receiverPlayer, String courseName, Double wager) {
        Challenge challenge = new Challenge(senderPlayer, receiverPlayer, courseName, wager);
        challenges.add(challenge);
        return challenge;
    }

    /**
     * Remove an instance of a Challenge.
     *
     * @param challenge
     */
    public void removeChallenge(Challenge challenge) {
        challenges.remove(challenge);
    }

    /**
     * Find the challenge the recipient senderPlayer has received.
     *
     * @param playerName
     * @return
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
     * Find if the player is currently in a Challenge.
     *
     * @param playerName
     * @return player is in challenge
     */
    public boolean isPlayerInChallenge(String playerName) {
        return getChallengeForPlayer(playerName) != null;
    }

    /**
     * Terminate a Challenge.
     * In the event that someone leaves the course / server
     *
     * @param leaver
     */
    public void terminateChallenge(Player leaver) {
        Challenge challenge = getChallengeForPlayer(leaver.getName());

        if (challenge != null) {
            Player opponent = calculateOpponent(leaver.getName(), challenge);
            String terminateMessage = Utils.getTranslation("Parkour.Challenge.Terminated")
                    .replace("%PLAYER%", leaver.getName());
            leaver.sendMessage(terminateMessage);
            opponent.sendMessage(terminateMessage);
            removeChallenge(challenge);
        }
    }

    /**
     * Complete the Challenge.
     * The winner has completed the course first.
     * The opponent will be derived from the Challenge.
     * If a wager is set, it will be deposited & withdrawn here.
     *
     * @param winner
     */
    public void completeChallenge(Player winner) {
        Challenge challenge = getChallengeForPlayer(winner.getName());

        if (challenge != null) {
            Player opponent = calculateOpponent(winner.getName(), challenge);
            removeChallenge(challenge);

            winner.sendMessage(Utils.getTranslation("Parkour.Challenge.Winner")
                    .replace("%PLAYER%", opponent.getName())
                    .replace("%COURSE%", challenge.getCourseName()));
            opponent.sendMessage(Utils.getTranslation("Parkour.Challenge.Loser")
                    .replace("%PLAYER%", winner.getName())
                    .replace("%COURSE%", challenge.getCourseName()));

            if (challenge.wager != null) {
                Parkour.getEconomy().depositPlayer(winner, challenge.wager);
                Parkour.getEconomy().withdrawPlayer(opponent, challenge.wager);
            }

            PlayerMethods.playerLeave(opponent);
        }
    }

    /**
     * Derive the opponent from the Challenge.
     *
     * @param winner
     * @param challenge
     * @return
     */
    public Player calculateOpponent(String winner, Challenge challenge) {
        String opponent = challenge.getSenderPlayer().equals(winner) ?
                challenge.getReceiverPlayer() : challenge.getSenderPlayer();

        return Bukkit.getPlayer(opponent);
    }

    /**
     * Accept a challenge.
     * Executed by the recipient of a challenge invite.
     * Will prepare each player for the challenge.
     *
     * @param receiverPlayer
     */
    public void acceptChallenge(final Player receiverPlayer) {
        Challenge challenge = ChallengeManager.getInstance().getChallengeForPlayer(receiverPlayer.getName());

        if (challenge == null) {
            receiverPlayer.sendMessage(Static.getParkourString() + "You have not been invited!");
            return;
        }
        if (!PlayerMethods.isPlayerOnline(challenge.getSenderPlayer())) {
            receiverPlayer.sendMessage(Static.getParkourString() + "Player is not online!");
            return;
        }

        final Player senderPlayer = Bukkit.getPlayer(challenge.getSenderPlayer());

        if (Parkour.getPlugin().getConfig().getBoolean("ParkourModes.Challenge.hidePlayers")) {
            senderPlayer.hidePlayer(receiverPlayer);
            receiverPlayer.hidePlayer(senderPlayer);
        }

        CourseMethods.joinCourse(senderPlayer, challenge.getCourseName());
        CourseMethods.joinCourse(receiverPlayer, challenge.getCourseName());

        final float playerSpeed = senderPlayer.getWalkSpeed();
        final float targetSpeed = receiverPlayer.getWalkSpeed();

        senderPlayer.setWalkSpeed(0f);
        receiverPlayer.setWalkSpeed(0f);

        new Runnable() {
            int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Parkour.getPlugin(), this, 0L, 20L);

            int count = Parkour.getPlugin().getConfig().getInt("ParkourModes.Challenge.CountdownFrom") + 1;
            @Override
            public void run() {
                if (count > 1) {
                    count--;

                    String translation = Utils.getTranslation("Parkour.Countdown", false).replace("%AMOUNT%", String.valueOf(count));
                    senderPlayer.sendMessage(translation);
                    receiverPlayer.sendMessage(translation);

                } else {
                    Bukkit.getScheduler().cancelTask(taskID);

                    String translation = Utils.getTranslation("Parkour.Go", false);
                    senderPlayer.sendMessage(translation);
                    receiverPlayer.sendMessage(translation);
                    senderPlayer.setWalkSpeed(playerSpeed);
                    receiverPlayer.setWalkSpeed(targetSpeed);

                    PlayerMethods.getParkourSession(senderPlayer.getName()).resetTimeStarted();
                    PlayerMethods.getParkourSession(receiverPlayer.getName()).resetTimeStarted();
                }
            }
        };
    }

    public class Challenge {

        private final String senderPlayer;
        private final String receiverPlayer;
        private final String courseName;

        private final Double wager;

        /**
         * Challenge player
         * Created to manage who started the challenge, who's the receiver and which on course.
         *
         * @param senderPlayer
         * @param receiverPlayer
         * @param courseName
         */
        private Challenge(String senderPlayer, String receiverPlayer, String courseName, Double wager) {
            this.senderPlayer = senderPlayer;
            this.receiverPlayer = receiverPlayer;
            this.courseName = courseName;
            this.wager = wager;
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

    }
}

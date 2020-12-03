package io.github.a5h73y.parkour.type.challenge;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Constants;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

/**
 * Challenge Manager.
 * No need for cache as invites are temporary and get cleaned on a schedule.
 * Challenges live as long as there is a player within one.
 */
public class ChallengeManager extends AbstractPluginReceiver {

    private final Map<Player, Challenge> challenges = new HashMap<>();
    private final Map<Player, ChallengeInvite> invites = new HashMap<>();

    public ChallengeManager(final Parkour parkour) {
        super(parkour);
        initialiseInviteTimeout();
    }

    /**
     * Create a Challenge.
     * The requesting player is considered the 'host' of the Challenge.
     * They will have the ability to invite others and start the Challenge.
     *
     * @param hostPlayer host of Challenge
     * @param courseName course name
     * @param wager optional monetary wager
     * @return created {@link Challenge}
     */
    public Challenge createChallenge(Player hostPlayer, String courseName, @Nullable Double wager) {
        Challenge challenge = new Challenge(hostPlayer, courseName, wager);
        challenges.put(hostPlayer, challenge);
        addParticipantToChallenge(challenge, hostPlayer);
        return challenge;
    }

    /**
     * Create or Join existing Challenge.
     * If there is a Challenge that exists for the course, then it will be joined.
     * Otherwise a new Challenge will be created and the Player becomes the host.
     *
     * @param requestingPlayer requesting Player
     * @param courseName course name
     */
    public void createOrJoinChallenge(Player requestingPlayer, String courseName) {
        if (getChallengeForPlayer(requestingPlayer) != null) {
            requestingPlayer.sendMessage("You are already on a Challenge!");
            return;
        }

        // find a Challenge for the Course which hasn't started yet
        Optional<Challenge> match = challenges.values().stream()
                .filter(challenge -> challenge.getCourseName().equals(courseName))
                .filter(challenge -> !challenge.hasStarted())
                .findAny();

        if (match.isPresent()) {
            addParticipantToChallenge(match.get(), requestingPlayer);
            TranslationUtils.sendValueTranslation("Parkour.Challenge.Joined", courseName, requestingPlayer);

        } else {
            createChallenge(requestingPlayer, courseName, null);
            TranslationUtils.sendValueTranslation("Parkour.Challenge.Created", courseName, requestingPlayer);
            TranslationUtils.sendTranslation("Parkour.Challenge.StartCommand", requestingPlayer);
        }
    }

    /**
     * Find the Challenge for the Player.
     * Found by searching the participants for each existing Challenge.
     *
     * @param targetPlayer target player
     * @return matching {@link Challenge}
     */
    @Nullable
    public Challenge getChallengeForPlayer(Player targetPlayer) {
        return challenges.values().stream()
                .filter(challenge -> challenge.isPlayerParticipating(targetPlayer))
                .findAny()
                .orElse(null);
    }

    /**
     * Send a Challenge Invite to the target Player.
     * The receiving player will have a message sent on how to accept / decline.
     *
     * @param challenge challenge
     * @param targetPlayer target Player
     */
    public void sendInviteToPlayer(Challenge challenge, @Nullable Player targetPlayer) {
        if (targetPlayer != null) {
            invites.put(targetPlayer, new ChallengeInvite(challenge));
            targetPlayer.sendMessage(TranslationUtils.getTranslation("Parkour.Challenge.InviteReceived")
                    .replace(Constants.COURSE_PLACEHOLDER, challenge.getCourseName())
                    .replace(Constants.PLAYER_PLACEHOLDER, challenge.getChallengeHost().getName()));
            TranslationUtils.sendTranslation("Parkour.Challenge.AcceptDecline", false, targetPlayer);
        }
    }

    /**
     * Remove the Challenge that is hosted by the Player.
     * @param hostPlayer host player
     */
    public void removeChallenge(Player hostPlayer) {
        challenges.remove(hostPlayer);
    }

    /**
     * Get the Challenge Invite for the Player.
     *
     * @param player player
     * @return matching {@link ChallengeInvite}
     */
    public ChallengeInvite getInviteForPlayer(Player player) {
        return invites.get(player);
    }

    /**
     * Check if the Player has an outstanding Challenge Invite.
     *
     * @param player requesting player
     * @return challenge invite exists
     */
    public boolean hasPlayerBeenInvited(Player player) {
        return invites.containsKey(player);
    }

    /**
     * Check if the Player been invited to or is currently on a Challenge.
     *
     * @param player player
     * @return player has been challenged
     */
    public boolean hasPlayerBeenChallenged(Player player) {
        return hasPlayerBeenInvited(player)
                || challenges.values().stream().anyMatch(challenge -> challenge.isPlayerParticipating(player));
    }

    /**
     * Forfeit the Challenge for the Player.
     * If the Player leaves or fails the course, they have forfeited their change to win the Challenge.
     * The playing Challenge participants will be notified of the Player's forfeiting.
     * If all the Players forfeit, the Challenge will be terminated with no winners.
     *
     * @param player player
     */
    public void forfeitChallenge(Player player) {
        Challenge challenge = getChallengeForPlayer(player);

        if (challenge != null) {
            challenge.setForfeited(player, true);

            if (challenge.allPlayersForfeited()) {
                removeChallenge(challenge.getChallengeHost());
                for (Player participant : challenge.getParticipatingPlayers()) {
                    TranslationUtils.sendValueTranslation("Parkour.Challenge.Terminated", player.getName(), participant);
                }

            } else {
                TranslationUtils.sendTranslation("Parkour.Challenge.Quit", player);

                challenge.getParticipantsForfeit().forEach((participant, forfeited) -> {
                    if (!forfeited) {
                        TranslationUtils.sendValueTranslation("Parkour.Challenge.Forfeited",
                                player.getName(), participant);
                    }
                });
            }
        }
    }

    /**
     * Complete the Challenge.
     * The Player has completed the course first, becoming the Winner.
     * If a wager is set, it will be deposited & withdrawn here.
     *
     * @param winner winning player
     */
    public void completeChallenge(Player winner) {
        Challenge challenge = getChallengeForPlayer(winner);

        if (challenge != null) {
            removeChallenge(challenge.getChallengeHost());

            TranslationUtils.sendValueTranslation("Parkour.Challenge.Winner",
                    challenge.getCourseName(), winner);

            for (Player loser : challenge.getParticipatingPlayers()) {
                if (loser == winner) {
                    continue;
                }

                loser.sendMessage(TranslationUtils.getTranslation("Parkour.Challenge.Loser")
                        .replace(Constants.PLAYER_PLACEHOLDER, winner.getName())
                        .replace(Constants.COURSE_PLACEHOLDER, challenge.getCourseName()));

                if (challenge.getWager() != null) {
                    parkour.getEconomyApi().chargePlayer(loser, challenge.getWager());
                }
            }

            if (challenge.getWager() != null) {
                parkour.getEconomyApi().rewardPlayer(winner, challenge.getWager() * challenge.getNumberOfParticipants());
            }

            // TODO do we want to do anything with the losers?
        }
    }

    /**
     * Accept a Challenge Invite.
     * The Player has accepted their Challenge invitation and will be added to the list of participants.
     * The Challenge host will be notified.
     *
     * @param receivingPlayer receiving player
     */
    public void acceptChallengeInvite(final Player receivingPlayer) {
        ChallengeInvite invite = getInviteForPlayer(receivingPlayer);

        if (invite == null) {
            receivingPlayer.sendMessage(Parkour.getPrefix() + "You have not been challenged!");
            return;
        }

        invites.remove(receivingPlayer);

        // TODO if main player has left
        if (invite.getChallenge() == null) {
            receivingPlayer.sendMessage("Ummm...");
            return;
        }

        if (invite.getChallenge().hasStarted()) {
            receivingPlayer.sendMessage("The Challenge has already started!");
            return;
        }

        addParticipantToChallenge(invite.getChallenge(), receivingPlayer);

        TranslationUtils.sendValueTranslation("Parkour.Challenge.Joined",
                invite.getChallenge().getCourseName(), receivingPlayer);
        invite.getChallenge().getChallengeHost().sendMessage(receivingPlayer.getName() + " has accepted the challenge!");
    }

    /**
     * Decline a Challenge Invite.
     * The Player has declined their Challenge invitation and will NOT be added to the list of participants.
     * The Challenge host will be notified.
     *
     * @param receivingPlayer receiving player
     */
    public void declineChallenge(Player receivingPlayer) {
        ChallengeInvite invite = getInviteForPlayer(receivingPlayer);

        if (invite == null) {
            receivingPlayer.sendMessage(Parkour.getPrefix() + "You have not been challenged!");
            return;
        }

        receivingPlayer.sendMessage("You have declined the challenge...");
        invite.getChallenge().getChallengeHost().sendMessage(receivingPlayer.getName() + " has declined the challenge!");
        invites.remove(receivingPlayer);
    }

    /**
     * Prepare the Challenge participants.
     * Each of the participating Players will be teleported to the start of the Course.
     *
     * @param challenge {@link Challenge}
     */
    public void prepareParticipants(Challenge challenge) {
        for (Player participant : challenge.getParticipatingPlayers()) {
            prepareParticipant(challenge, participant);
        }
    }

    /**
     * Prepare the Challenge participant.
     * The player will be teleported to the start of the Course and unable to move.
     *
     * @param challenge challenge
     * @param participant participating Player
     */
    public void prepareParticipant(Challenge challenge, Player participant) {
        if (parkour.getConfig().getBoolean("ParkourChallenge.HidePlayers")) {
            parkour.getPlayerManager().forceInvisible(participant);
        }

        parkour.getPlayerManager().joinCourse(participant, challenge.getCourseName());
        participant.setWalkSpeed(0f);
        PlayerUtils.applyPotionEffect(PotionEffectType.JUMP, 100000, 100000, participant);
    }

    /**
     * Begin Challenge Countdown.
     * A visual countdown will commence to all participating Players.
     * Once finished, the players will have their movement returned and the Course timer started.
     *
     * @param challenge challenge
     */
    public void beginCountdown(Challenge challenge) {
        new BukkitRunnable() {
            int count = parkour.getConfig().getInt("ParkourChallenge.CountdownFrom") + 1;
            @Override
            public void run() {
                if (count > 1) {
                    count--;
                    TranslationUtils.sendValueTranslation("Parkour.Countdown", String.valueOf(count),
                            challenge.getParticipatingPlayers().toArray(new Player[0]));

                } else {
                    this.cancel();

                    for (Player participant : challenge.getParticipatingPlayers()) {
                        participant.setWalkSpeed(challenge.getPlayerWalkSpeed());
                        PlayerUtils.removePotionEffect(PotionEffectType.JUMP, participant);
                        ParkourSession session = parkour.getPlayerManager().getParkourSession(participant);
                        session.resetTime();
                        session.setStartTimer(true);
                        TranslationUtils.sendTranslation("Parkour.Go", participant);
                    }
                }
            }
        }.runTaskTimer(parkour, 0L, 20L);
    }

    /**
     * Process Challenge Command input.
     * Each of the valid commands will be processed based on input.
     *
     * @param player player
     * @param args command arguments
     */
    public void processCommand(Player player, String... args) {
        switch (args[1].toLowerCase()) {
            case "create":
                processCreateCommand(player, args[2], args.length == 4 ? args[3] : null);
                break;

            case "invite":
                processSendInviteCommand(player, args);
                break;

            case "start":
            case "begin":
                processStartCommand(player);
                break;

            case "accept":
                processInviteReceiveCommand(player, true);
                break;

            case "decline":
                processInviteReceiveCommand(player, false);
                break;

            case "terminate":
                processTerminateCommand(player);
                break;

            case "info":
                displayChallengeInfo(player);
                break;

            default:
                player.sendMessage("Unknown command.");
        }
    }

    private void initialiseInviteTimeout() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(parkour, () -> {
            Iterator<Player> iterable = invites.keySet().iterator();
            while (iterable.hasNext()) {
                Player player = iterable.next();

                if (invites.get(player).getTimeInvited() + 15000 < System.currentTimeMillis()) {
                    invites.get(player).getChallenge().getChallengeHost()
                            .sendMessage("The invite sent to " + player.getName() + " has expired.");
                    player.sendMessage("Your Challenge invite has expired"); //TODO remove me
                    iterable.remove();
                }
            }
        }, 0, 5 * 20);
    }

    private void addParticipantToChallenge(Challenge challenge, Player player) {
        if (challenge != null) {
            challenge.addParticipant(player);
            if (parkour.getConfig().getBoolean("ParkourChallenge.PrepareOnAccept")) {
                prepareParticipant(challenge, player);
            }
        }
    }

    private void displayChallengeInfo(Player player) {
        Challenge challenge = getChallengeForPlayer(player);

        if (challenge == null) {
            player.sendMessage("You are not on a Challenge.");
        } else {
            player.sendMessage(challenge.toString());
        }
    }

    private void processCreateCommand(Player player, String courseName, @Nullable String wager) {
        if (challenges.containsKey(player)) {
            player.sendMessage("You have already created a Challenge.");
            player.sendMessage("To Terminate it, enter /pa challenge terminate");
            return;

        } else if (!Validation.createChallenge(player, courseName, wager)) {
            return;
        }

        Double wagerValue = ValidationUtils.isDouble(wager) ? Double.parseDouble(wager) : null;
        createChallenge(player, courseName, wagerValue);

        String translation = TranslationUtils.getValueTranslation("Parkour.Challenge.Created", courseName);
        if (wagerValue != null && wagerValue > 0) {
            translation += TranslationUtils.getValueTranslation("Parkour.Challenge.Wager", wager, false);
        }
        player.sendMessage(translation);
        TranslationUtils.sendTranslation("Parkour.Challenge.StartCommand", player);
    }

    private void processSendInviteCommand(Player player, String... args) {
        Challenge challenge = challenges.get(player);

        if (challenge == null) {
            player.sendMessage("You have not created a challenge.");
            return;
        }

        if (challenge.hasStarted()) {
            player.sendMessage("Your challenge has already started.");
            return;
        }

        for (String playerName : Arrays.asList(args).subList(2, args.length)) {
            if (Validation.challengePlayer(player, challenge, playerName)) {
                sendInviteToPlayer(challenge, Bukkit.getPlayer(playerName));
                TranslationUtils.sendValueTranslation("Parkour.Challenge.InviteSent", playerName, player);
            }
        }
    }

    private void processStartCommand(Player player) {
        Challenge challenge = challenges.get(player);

        if (challenge == null) {
            player.sendMessage("You have not created a challenge.");
            return;
        }

        if (challenge.getNumberOfParticipants() < 2) {
            player.sendMessage("You need at least 2 players to start a Challenge.");
            return;
        }

        prepareParticipants(challenge);
        beginCountdown(challenge);
    }

    private void processInviteReceiveCommand(Player player, boolean accepted) {
        ChallengeInvite invite = getInviteForPlayer(player);

        if (invite == null) {
            player.sendMessage("You have not been invited to challenge.");
            return;
        }

        if (accepted) {
            acceptChallengeInvite(player);
        } else {
            declineChallenge(player);
        }
    }

    private void processTerminateCommand(Player player) {
        Challenge challenge = challenges.get(player);

        if (challenge == null) {
            player.sendMessage("You have not created a challenge.");
            return;
        }

        challenge.getParticipatingPlayers().forEach(player1 -> player1.sendMessage("Terminated"));
        challenges.remove(player);
    }

    /**
     * Challenge Invite.
     * Will contain the Challenge they are invited to, and the time the invite was sent.
     * If the time sent exceeds the timeout limit, the invite will be revoked.
     */
    private static class ChallengeInvite {

        private final Challenge challenge;

        private final long timeInvited;

        public ChallengeInvite(Challenge challenge) {
            this.timeInvited = System.currentTimeMillis();
            this.challenge = challenge;
        }

        public Challenge getChallenge() {
            return challenge;
        }

        public long getTimeInvited() {
            return timeInvited;
        }
    }
}

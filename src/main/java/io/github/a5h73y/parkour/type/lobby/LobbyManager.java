package io.github.a5h73y.parkour.type.lobby;

import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.ParkourValidation;
import io.github.a5h73y.parkour.type.Cacheable;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parkour Lobby Manager.
 * Keeps a lazy Cache of {@link Lobby} which can be reused by other players.
 */
public class LobbyManager extends AbstractPluginReceiver implements Cacheable<Lobby> {

    private final Map<String, Lobby> lobbyCache = new HashMap<>();

    public LobbyManager(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Create or Overwrite Parkour Lobby.
     * Player's location will be used to create a Lobby with the given name.
     * Optional ParkourLevel requirement can be provided.
     *
     * @param player requesting player
     * @param lobbyName desired lobby name
     * @param requiredLevel ParkourLevel requirement
     */
    public void createLobby(Player player, String lobbyName, @Nullable String requiredLevel) {
        setLobby(player, lobbyName);
        TranslationUtils.sendValueTranslation("Lobby.Created", lobbyName, player);

        if (requiredLevel != null && ValidationUtils.isPositiveInteger(requiredLevel)) {
            LobbyInfo.setRequiredLevel(lobbyName, Integer.parseInt(requiredLevel));
            TranslationUtils.sendValueTranslation("Lobby.RequiredLevelSet", requiredLevel, player);
        }
    }

    /**
     * Join the Parkour Lobby.
     * Teleport the Player to the lobby location if ParkourLevel requirements are met.
     * Lobby name will be 'default' if not provided.
     *
     * @param player requesting player
     * @param lobbyName lobby name
     */
    public void joinLobby(Player player, @Nullable String lobbyName) {
        lobbyName = lobbyName == null ? DEFAULT : lobbyName.toLowerCase();

        if (!ParkourValidation.isDefaultLobbySet(player)) {
            return;
        }

        // if they are on a course, force them to leave, which will ultimately run this method again.
        if (parkour.getPlayerManager().isPlaying(player)) {
            PlayerInfo.resetJoinLocation(player);
            parkour.getPlayerManager().leaveCourse(player);
            return;
        }

        if (!ParkourValidation.canJoinLobby(player, lobbyName)) {
            return;
        }

        Lobby lobby = lobbyCache.getOrDefault(lobbyName, populateLobby(lobbyName));
        PlayerUtils.teleportToLocation(player, lobby.getLocation());

        if (lobbyName.equals(DEFAULT)) {
            TranslationUtils.sendTranslation("Parkour.Lobby", player);
        } else {
            TranslationUtils.sendValueTranslation("Parkour.LobbyOther", lobbyName, player);
        }
    }

    /**
     * Teleport the Player to the Default Lobby.
     * Used as a fallback to quickly get the player back somewhere safe.
     *
     * @param player player
     */
    public void justTeleportToDefaultLobby(Player player) {
        if (!ParkourValidation.isDefaultLobbySet(player)) {
            return;
        }
        Lobby lobby = lobbyCache.getOrDefault(DEFAULT, populateLobby(DEFAULT));
        PlayerUtils.teleportToLocation(player, lobby.getLocation());
        TranslationUtils.sendTranslation("Parkour.Lobby", player);
    }

    /**
     * Teleport the Player to the nearest Lobby available.
     * @param player player
     */
    public void teleportToNearestLobby(Player player) {
        Lobby lobby = getNearestLobby(player);
        if (lobby != null) {
            PlayerUtils.teleportToLocation(player, lobby.getLocation());
            TranslationUtils.sendValueTranslation("Parkour.LobbyOther", lobby.getName(), player);
        }
    }

    /**
     * Delete a Parkour Lobby.
     * All references to the Lobby will be deleted.
     *
     * @param sender requesting sender
     * @param lobbyName lobby name
     */
    public void deleteLobby(CommandSender sender, String lobbyName) {
        if (!ParkourValidation.canDeleteLobby(sender, lobbyName)) {
            return;
        }

        LobbyInfo.deleteLobby(lobbyName);
        clearCache(lobbyName);
        TranslationUtils.sendValueTranslation("Parkour.Delete", lobbyName + " Lobby", sender);
        PluginUtils.logToFile(lobbyName + " lobby was deleted by " + sender.getName());
    }

    /**
     * Teleport the Player to the determined destination.
     * When failing or leaving a Course the location destination will be calculated.
     *
     * @param player requesting player
     * @param session parkour session
     */
    public void teleportToLeaveDestination(Player player, ParkourSession session) {
        String lobbyName = null;

        if (parkour.getConfig().getBoolean("OnLeave.TeleportToLinkedLobby")) {
            lobbyName = CourseInfo.getLinkedLobby(session.getCourse().getName());
        }

        joinLobby(player, lobbyName);
    }

    /**
     * Display all Parkour Lobbies to sender.
     * @param sender requesting sender
     */
    public void displayLobbies(CommandSender sender) {
        TranslationUtils.sendHeading("Available Lobbies", sender);
        LobbyInfo.getAllLobbyNames().forEach(s -> sender.sendMessage("* " + s));
    }

    @Override
    public int getCacheSize() {
        return lobbyCache.size();
    }

    @Override
    public void clearCache() {
        lobbyCache.clear();
    }

    /**
     * Clear the specified Lobby information.
     *
     * @param lobbyName target lobby name
     */
    public void clearCache(String lobbyName) {
        lobbyCache.remove(lobbyName.toLowerCase());
    }

    /**
     * Populate Lobby info and add to Cache.
     * @param lobbyName lobby name
     * @return populated Lobby
     */
    private Lobby populateLobby(@NotNull String lobbyName) {
        Lobby lobby = new Lobby(lobbyName.toLowerCase(),
                LobbyInfo.getLobbyLocation(lobbyName), LobbyInfo.getRequiredLevel(lobbyName));
        lobbyCache.put(lobbyName.toLowerCase(), lobby);
        return lobby;
    }

    /**
     * Set the Lobby to Player's Location.
     * A log entry will be created.
     * @param player requesting player
     * @param lobbyName lobby name
     */
    private void setLobby(Player player, String lobbyName) {
        LobbyInfo.setLobby(lobbyName, player.getLocation());
        PluginUtils.logToFile(lobbyName + " lobby was set by " + player.getName());
    }

    /**
     * Find the nearest valid Lobby to the Player.
     * If no nearest lobby was found, the default lobby will be returned.
     * If no lobby was set, null is returned.
     *
     * @param player player
     * @return nearest lobby to Location
     */
    @Nullable
    private Lobby getNearestLobby(Player player) {
        return lobbyCache.values().stream()
                .filter(lobby -> lobby.getLocation().getWorld() == player.getWorld())
                .filter(lobby -> ParkourValidation.canJoinLobbySilent(player, lobby.getName()))
                .min(Comparator.comparingDouble(o -> player.getLocation().distanceSquared(o.getLocation())))
                .orElse(lobbyCache.getOrDefault(DEFAULT, populateLobby(DEFAULT)));
    }
}

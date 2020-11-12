package io.github.a5h73y.parkour.type.lobby;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Constants;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.type.Cacheable;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyManager extends AbstractPluginReceiver implements Cacheable<Lobby> {

    private final Map<String, Lobby> lobbyCache = new HashMap<>();

    public LobbyManager(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Creating or overwriting a Parkour lobby.
     * Optional parameters include a name for a custom lobby, as well as a minimum level requirement.
     *
     * @param args
     * @param player
     */
    public void createLobby(Player player, String[] args) {
        String lobbyName = args.length > 1 ? args[1] : Constants.DEFAULT;
        setLobby(player, lobbyName);
        TranslationUtils.sendValueTranslation("Lobby.Created", lobbyName, player);

        if (args.length > 2 && Validation.isPositiveInteger(args[2])) {
            LobbyInfo.setRequiredLevel(lobbyName, Integer.parseInt(args[2]));
            TranslationUtils.sendValueTranslation("Lobby.RequiredLevelSet", args[2], player);
        }
    }

    /**
     * Joining the Parkour lobby.
     * Can be accessed from the commands, and from the framework.
     * The arguments will be null if its from the framework, if this is
     * the case we don't send them a message. (Finishing a course etc)
     *
     * @param args
     * @param player
     */
    public void joinLobby(Player player, String lobbyName) {
        lobbyName = lobbyName == null ? Constants.DEFAULT : lobbyName.toLowerCase();

        if (!Validation.isDefaultLobbySet(player)) {
            return;
        }

        // if they are on a course, force them to leave, which will ultimately run this method again.
        if (parkour.getPlayerManager().isPlaying(player)) {
            PlayerInfo.resetJoinLocation(player);
            parkour.getPlayerManager().leaveCourse(player);
            return;
        }

        if (!Validation.canJoinLobby(player, lobbyName)) {
            return;
        }

        Lobby lobby = lobbyCache.getOrDefault(lobbyName, populateLobby(lobbyName));
        player.teleport(lobby.getLocation());

        if (lobbyName.equals(Constants.DEFAULT)) {
            TranslationUtils.sendTranslation("Parkour.Lobby", player);
        } else {
            TranslationUtils.sendValueTranslation("Parkour.LobbyOther", lobbyName, player);
        }
    }

    private Lobby populateLobby(String lobbyName) {
        Lobby lobby = LobbyInfo.getLobby(lobbyName);
        lobbyCache.put(lobbyName.toLowerCase(), lobby);
        return lobby;
    }

    /**
     * Set or overwrite a lobby.
     * Arguments will determine if it's a custom lobby.
     *
     * @param args
     * @param player
     */
    private void setLobby(Player player, String lobbyName) {
        LobbyInfo.setLobby(lobbyName, player.getLocation());
        PluginUtils.logToFile(lobbyName + " lobby was set by " + player.getName());
    }

    /**
     * Delete a Parkour lobby.
     *
     * @param lobbyName
     * @param sender
     */
    public void deleteLobby(CommandSender sender, String lobbyName) {
        if (!LobbyInfo.doesLobbyExist(lobbyName)) {
            TranslationUtils.sendValueTranslation("Error.UnknownLobby", lobbyName, sender);
            return;
        }

        LobbyInfo.deleteLobby(lobbyName);
        TranslationUtils.sendValueTranslation("Parkour.Delete", lobbyName + " Lobby", sender);
    }

    /**
     * Calculate the location when the player leaves / quits a course.
     * Used for finding the lobby if a custom is configured.
     *
     * @param player
     * @param session
     */
    public void teleportToLeaveDestination(Player player, ParkourSession session) {
        String lobbyName = null;

        if (parkour.getConfig().getBoolean("OnLeave.TeleportToLinkedLobby")) {
            lobbyName = CourseInfo.getLinkedLobby(session.getCourse().getName());
        }

        joinLobby(player, lobbyName);
    }

    /**
     * Send a list of custom Lobbies to the player.
     *
     * @param sender
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
}

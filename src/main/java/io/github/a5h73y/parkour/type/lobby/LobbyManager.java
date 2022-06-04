package io.github.a5h73y.parkour.type.lobby;

import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.CacheableParkourManager;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parkour Lobby Manager.
 * Keeps a lazy Cache of {@link Lobby} which can be reused by other players.
 */
public class LobbyManager extends CacheableParkourManager {

    private final Map<String, Lobby> lobbyCache = new HashMap<>();

    public LobbyManager(final Parkour parkour) {
        super(parkour);
    }

    @Override
    protected LobbyConfig getConfig() {
        return parkour.getConfigManager().getLobbyConfig();
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

        if (ValidationUtils.isPositiveInteger(requiredLevel)) {
            getConfig().setRequiredLevel(lobbyName, Integer.parseInt(requiredLevel));
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

        if (!isDefaultLobbySet(player)) {
            return;
        }

        // if they are on a course, force them to leave, which will ultimately run this method again.
        if (parkour.getParkourSessionManager().isPlaying(player)) {
            parkour.getConfigManager().getPlayerConfig(player).resetSessionJoinLocation();
            parkour.getPlayerManager().leaveCourse(player);
            return;
        }

        if (!canJoinLobby(player, lobbyName)) {
            return;
        }

        Lobby lobby = lobbyCache.getOrDefault(lobbyName, populateLobby(lobbyName));
        PlayerUtils.teleportToLocation(player, lobby.getLocation());

        if (getConfig().hasLobbyCommand(lobbyName)) {
            for (String command : getConfig().getLobbyCommands(lobbyName)) {
                PlayerUtils.dispatchServerPlayerCommand(command, player);
            }
        }

        if (lobbyName.equals(DEFAULT)) {
            TranslationUtils.sendTranslation("Lobby.Joined", player);
        } else {
            TranslationUtils.sendValueTranslation("Lobby.JoinedOther", lobbyName, player);
        }
    }

    /**
     * Add a Command to be executed when the Player visits a Lobby.
     *
     * @param commandSender command sender
     * @param lobbyName lobby name
     * @param command command to run
     */
    public void addLobbyCommand(CommandSender commandSender, String lobbyName, String command) {
        if (!getConfig().doesLobbyExist(lobbyName)) {
            TranslationUtils.sendValueTranslation("Error.UnknownLobby", lobbyName, commandSender);
            return;
        }

        getConfig().addLobbyCommand(lobbyName, command);
        TranslationUtils.sendPropertySet(commandSender, "Lobby Command", lobbyName, "/" + command);
    }

    /**
     * Teleport the Player to the Default Lobby.
     * Used as a fallback to quickly get the player back somewhere safe.
     *
     * @param player player
     */
    public void justTeleportToDefaultLobby(Player player) {
        if (!isDefaultLobbySet(player)) {
            return;
        }
        Lobby lobby = lobbyCache.getOrDefault(DEFAULT, populateLobby(DEFAULT));
        PlayerUtils.teleportToLocation(player, lobby.getLocation());
        TranslationUtils.sendTranslation("Lobby.Joined", player);
    }

    /**
     * Teleport the Player to the nearest Lobby available.
     * @param player player
     */
    public void teleportToNearestLobby(Player player) {
        Lobby lobby = getNearestLobby(player);
        if (lobby != null) {
            PlayerUtils.teleportToLocation(player, lobby.getLocation());
            TranslationUtils.sendValueTranslation("Lobby.JoinedOther", lobby.getName(), player);
        }
    }

    /**
     * Delete a Parkour Lobby.
     * All references to the Lobby will be deleted.
     *
     * @param commandSender command sender
     * @param lobbyName lobby name
     */
    public void deleteLobby(CommandSender commandSender, String lobbyName) {
        if (!parkour.getAdministrationManager().canDeleteLobby(commandSender, lobbyName)) {
            return;
        }

        getConfig().deleteLobby(lobbyName);
        clearCache(lobbyName);
        TranslationUtils.sendValueTranslation("Parkour.Delete", lobbyName + " Lobby", commandSender);
        PluginUtils.logToFile(lobbyName + " lobby was deleted by " + commandSender.getName());
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

        if (parkour.getParkourConfig().getBoolean("OnLeave.TeleportToLinkedLobby")) {
            lobbyName = parkour.getConfigManager().getCourseConfig(session.getCourse().getName()).getLinkedLobby();
        }

        joinLobby(player, lobbyName);
    }

    /**
     * Display all Parkour Lobbies to sender.
     * @param commandSender command sender
     */
    public void displayLobbies(CommandSender commandSender) {
        TranslationUtils.sendHeading("Available Lobbies", commandSender);
        getConfig().getAllLobbyNames().forEach(s -> commandSender.sendMessage("* " + s));
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
                getConfig().getLobbyLocation(lobbyName), getConfig().getRequiredLevel(lobbyName));
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
        getConfig().setLobbyLocation(lobbyName, player.getLocation());
        PluginUtils.logToFile(lobbyName + " lobby was set by " + player.getName());
    }

    /**
     * Find the nearest valid Lobby to the Player.
     * If no lobby was found, the default lobby will be returned.
     * If no lobby was set, null is returned.
     *
     * @param player player
     * @return nearest lobby to Location
     */
    @Nullable
    private Lobby getNearestLobby(Player player) {
        return lobbyCache.values().stream()
                .filter(lobby -> lobby.getLocation().getWorld() == player.getWorld())
                .filter(lobby -> canJoinLobbySilent(player, lobby.getName()))
                .min(Comparator.comparingDouble(o -> player.getLocation().distanceSquared(o.getLocation())))
                .orElse(lobbyCache.getOrDefault(DEFAULT, populateLobby(DEFAULT)));
    }

    /**
     * Validate the Default Lobby is set.
     *
     * @param player player
     * @return default lobby set
     */
    private boolean isDefaultLobbySet(Player player) {
        boolean lobbySet = Parkour.getLobbyConfig().doesDefaultLobbyExist();

        if (!lobbySet) {
            if (PermissionUtils.hasPermission(player, Permission.ADMIN_ALL, false)) {
                TranslationUtils.sendMessage(player, "&cDefault Lobby has not been set!");
                TranslationUtils.sendMessage(player, "Type &b'/pa create lobby' &fwhere you want the lobby to be set.");

            } else {
                TranslationUtils.sendMessage(player, "&cDefault Lobby has not been set! Please tell the Owner!");
            }
        } else if (Bukkit.getWorld(Parkour.getLobbyConfig().getLobbyWorld(DEFAULT)) == null) {
            TranslationUtils.sendTranslation("Error.UnknownWorld", player);
            lobbySet = false;
        }
        return lobbySet;
    }

    /**
     * Validate Player joining Lobby.
     *
     * @param player player
     * @param lobbyName lobby name
     * @return player can join lobby
     */
    private boolean canJoinLobby(Player player, String lobbyName) {
        if (!Parkour.getLobbyConfig().doesLobbyExist(lobbyName)) {
            TranslationUtils.sendValueTranslation("Error.UnknownLobby", lobbyName, player);
            return false;
        }

        if (Bukkit.getWorld(Parkour.getLobbyConfig().getLobbyWorld(lobbyName)) == null) {
            TranslationUtils.sendTranslation("Error.UnknownWorld", player);
            return false;
        }

        if (Parkour.getDefaultConfig().getBoolean("LobbySettings.EnforceWorld")
                && !player.getWorld().getName().equals(Parkour.getLobbyConfig().getLobbyWorld(lobbyName))) {
            TranslationUtils.sendTranslation("Error.WrongWorld", player);
            return false;
        }

        int level = Parkour.getLobbyConfig().getRequiredLevel(lobbyName);

        if (level > 0 && parkour.getConfigManager().getPlayerConfig(player).getParkourLevel() < level
                && !PermissionUtils.hasPermission(player, Permission.ADMIN_LEVEL_BYPASS, false)) {
            TranslationUtils.sendValueTranslation("Error.RequiredLvl", String.valueOf(level), player);
            return false;
        }

        return true;
    }

    /**
     * Validate Player joining Lobby Silently.
     *
     * @param player player
     * @param lobbyName lobby name
     * @return player can join lobby
     */
    private boolean canJoinLobbySilent(Player player, String lobbyName) {
        if (!Parkour.getLobbyConfig().doesLobbyExist(lobbyName)) {
            return false;
        }

        if (Bukkit.getWorld(Parkour.getDefaultConfig().getString("Lobby." + lobbyName + ".World")) == null) {
            return false;
        }

        if (Parkour.getDefaultConfig().getBoolean("LobbySettings.EnforceWorld")
                && !player.getWorld().getName().equals(Parkour.getLobbyConfig().getLobbyWorld(lobbyName))) {
            return false;
        }

        int level = Parkour.getLobbyConfig().getRequiredLevel(lobbyName);

        return level <= 0 || parkour.getConfigManager().getPlayerConfig(player).getParkourLevel() >= level
                || PermissionUtils.hasPermission(player, Permission.ADMIN_LEVEL_BYPASS, false);
    }
}

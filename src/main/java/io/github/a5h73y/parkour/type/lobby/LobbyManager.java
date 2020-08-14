package io.github.a5h73y.parkour.type.lobby;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyManager extends AbstractPluginReceiver {

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
    public void createLobby(String[] args, Player player) {
        String created = "Lobby ";
        setLobby(args, player);

        // TODO rewrite all of this, make it translatable
        // 'required rank' whut?
        if (args.length > 1) {
            if (args.length > 2 && Validation.isPositiveInteger(args[2])) {
                created = created.concat(ChatColor.AQUA + args[1] + ChatColor.WHITE + " created, with a required rank of " + ChatColor.DARK_AQUA + Integer.parseInt(args[2]));
                parkour.getConfig().set("Lobby." + args[1] + ".RequiredLevel", Integer.parseInt(args[2]));
            } else {
                created = created.concat(ChatColor.AQUA + args[1] + ChatColor.WHITE + " created");
            }
        } else {
            parkour.getConfig().set("Lobby.Set", true);
            created = created.concat("was created");
        }
        parkour.saveConfig();
        player.sendMessage(Parkour.getPrefix() + created);
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
    public void joinLobby(String[] args, Player player) {
        if (!Validation.lobbyJoiningSet(player)) {
            return;
        }

        // if they are on a course, force them to leave, which will ultimately run this method again.
        if (parkour.getPlayerManager().isPlaying(player)) {
            parkour.getPlayerManager().leaveCourse(player);
            return;
        }

        boolean customLobby = (args != null && args.length > 1 && args[1] != null);
        Location lobby;

        if (customLobby) {
            if (!Validation.lobbyJoiningCustom(player, args[1])) {
                return;
            }

            lobby = getLobby("Lobby." + args[1]);

        } else if (parkour.getConfig().isTeleportToJoinLocation()) {
            lobby = PlayerInfo.getJoinLocation(player);

        } else {
            lobby = getLobby("Lobby");
        }

        if (parkour.getConfig().getBoolean("Lobby.EnforceWorld") &&
                !lobby.getWorld().getName().equals(player.getWorld().getName())) {
            TranslationUtils.sendTranslation("Error.WrongWorld", player);
            return;
        }

        player.teleport(lobby);

        // Only continue if player intentionally joined the lobby e.g /pa lobby
        if (args == null) {
            return;
        }

        if (customLobby) {
            TranslationUtils.sendValueTranslation("Parkour.LobbyOther", args[1], player);

        } else if (parkour.getConfig().isTeleportToJoinLocation()) {
            TranslationUtils.sendTranslation("Parkour.JoinLocation", player);

        } else {
            TranslationUtils.sendTranslation("Parkour.Lobby", player);
        }
    }

    /**
     * Get the lobby Location based on the path.
     *
     * @param path
     * @return Location
     */
    private Location getLobby(String path) {
        World world = Bukkit.getWorld(parkour.getConfig().getString(path + ".World"));
        double x = parkour.getConfig().getDouble(path + ".X");
        double y = parkour.getConfig().getDouble(path + ".Y");
        double z = parkour.getConfig().getDouble(path + ".Z");
        float yaw = parkour.getConfig().getInt(path + ".Yaw");
        float pitch = parkour.getConfig().getInt(path + ".Pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Set or overwrite a lobby.
     * Arguments will determine if it's a custom lobby.
     *
     * @param args
     * @param player
     */
    private void setLobby(String[] args, Player player) {
        Location loc = player.getLocation();
        String path = args.length > 1 ? "Lobby." + args[1] : "Lobby";
        parkour.getConfig().set(path + ".World", loc.getWorld().getName());
        parkour.getConfig().set(path + ".X", loc.getX());
        parkour.getConfig().set(path + ".Y", loc.getY());
        parkour.getConfig().set(path + ".Z", loc.getZ());
        parkour.getConfig().set(path + ".Pitch", loc.getPitch());
        parkour.getConfig().set(path + ".Yaw", loc.getYaw());
        parkour.getConfig().save();
        PluginUtils.logToFile(path + " was set by " + player.getName());
    }

    /**
     * Delete a Parkour lobby.
     *
     * @param lobby
     * @param player
     */
    public void deleteLobby(String lobby, Player player) {
        if (!parkour.getConfig().contains("Lobby." + lobby + ".World")) {
            player.sendMessage(Parkour.getPrefix() + "This lobby does not exist!");
            return;
        }

        parkour.getConfig().set("Lobby." + lobby, null);
        parkour.saveConfig();

        player.sendMessage(Parkour.getPrefix() + "Lobby " + lobby + " was deleted successfully.");
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
        String[] args = new String[]{null, lobbyName};
        joinLobby(args, player);
    }

    /**
     * Get list of custom Lobbies.
     * I'm aware this looks like shit, but it's the best solution without a rewrite of lobbies.
     *
     * @return Set<String>
     */
    public Set<String> getCustomLobbies() {
        Set<String> lobbyListSet = parkour.getConfig().getConfigurationSection("Lobby").getKeys(false);

        lobbyListSet.remove("Set");
        lobbyListSet.remove("World");
        lobbyListSet.remove("EnforceWorld");
        lobbyListSet.remove("X");
        lobbyListSet.remove("Y");
        lobbyListSet.remove("Z");
        lobbyListSet.remove("Pitch");
        lobbyListSet.remove("Yaw");

        return lobbyListSet;
    }

    /**
     * Send a list of custom Lobbies to the player.
     *
     * @param sender
     */
    public void displayLobbies(CommandSender sender) {
        TranslationUtils.sendHeading("Custom Lobbies", sender);
        getCustomLobbies().forEach(s -> sender.sendMessage("* " + s));
    }
}

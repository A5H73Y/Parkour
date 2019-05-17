package me.A5H73Y.Parkour.Course;

import java.util.Set;

import me.A5H73Y.Parkour.Other.Validation;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Player.ParkourSession;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyMethods {

    /**
     * Creating or overwriting a Parkour lobby.
     * Optional parameters include a name for a custom lobby, as well as a minimum level requirement.
     *
     * @param args
     * @param player
     */
    public static void createLobby(String[] args, Player player) {
        String created = "Lobby ";
        setLobby(args, player);

        if (args.length > 1) {
            if (args.length > 2 && Validation.isPositiveInteger(args[2])) {
                created = created.concat(ChatColor.AQUA + args[1] + ChatColor.WHITE + " created, with a required rank of " + ChatColor.DARK_AQUA + Integer.parseInt(args[2]));
                Parkour.getPlugin().getConfig().set("Lobby." + args[1] + ".Level", Integer.parseInt(args[2]));
            } else {
                created = created.concat(ChatColor.AQUA + args[1] + ChatColor.WHITE + " created");
            }
        } else {
            Parkour.getPlugin().getConfig().set("Lobby.Set", true);
            created = created.concat("was successfully created!");
        }
        Parkour.getPlugin().saveConfig();
        player.sendMessage(Static.getParkourString() + created);
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
    public static void joinLobby(String[] args, Player player) {
        if (!Validation.lobbyJoiningSet(player)) {
            return;
        }

        // if they are on a course, force them to leave, which will ultimately run this method again.
        if (PlayerMethods.isPlaying(player.getName())) {
            PlayerMethods.playerLeave(player);
            return;
        }

        boolean customLobby = (args != null && args.length > 1 && args[1] != null);
        Location lobby;

        if (customLobby) {
            if (!Validation.lobbyJoiningCustom(player, args[1])) {
                return;
            }

            lobby = getLobby("Lobby." + args[1]);
        } else {
            lobby = getLobby("Lobby");
        }

        if (Parkour.getPlugin().getConfig().getBoolean("Lobby.EnforceWorld") &&
                !lobby.getWorld().getName().equals(player.getWorld().getName())) {
            player.sendMessage(Utils.getTranslation("Error.WrongWorld"));
            return;
        }

        player.teleport(lobby);

        // Only continue if player intentionally joined the lobby e.g /pa lobby
        if (args == null) {
            return;
        }

        if (customLobby) {
            player.sendMessage(Utils.getTranslation("Parkour.LobbyOther").replace("%LOBBY%", args[1]));
        } else {
            player.sendMessage(Utils.getTranslation("Parkour.Lobby"));
        }
    }

    /**
     * Get the lobby Location based on the path.
     *
     * @param path
     * @return Location
     */
    private static Location getLobby(String path) {
        World world = Bukkit.getWorld(Parkour.getPlugin().getConfig().getString(path + ".World"));
        double x = Parkour.getPlugin().getConfig().getDouble(path + ".X");
        double y = Parkour.getPlugin().getConfig().getDouble(path + ".Y");
        double z = Parkour.getPlugin().getConfig().getDouble(path + ".Z");
        float yaw = Parkour.getPlugin().getConfig().getInt(path + ".Yaw");
        float pitch = Parkour.getPlugin().getConfig().getInt(path + ".Pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Set or overwrite a lobby.
     * Arguments will determine if it's a custom lobby.
     *
     * @param args
     * @param player
     */
    private static void setLobby(String[] args, Player player) {
        Location loc = player.getLocation();
        String path = args.length > 1 ? "Lobby." + args[1] : "Lobby";
        Parkour.getPlugin().getConfig().set(path + ".World", loc.getWorld().getName());
        Parkour.getPlugin().getConfig().set(path + ".X", loc.getX());
        Parkour.getPlugin().getConfig().set(path + ".Y", loc.getY());
        Parkour.getPlugin().getConfig().set(path + ".Z", loc.getZ());
        Parkour.getPlugin().getConfig().set(path + ".Pitch", loc.getPitch());
        Parkour.getPlugin().getConfig().set(path + ".Yaw", loc.getYaw());
        Utils.logToFile(path + " was set by " + player.getName());
    }

    /**
     * Delete a Parkour lobby.
     *
     * @param lobby
     * @param player
     */
    public static void deleteLobby(String lobby, Player player) {
        if (!Parkour.getPlugin().getConfig().contains("Lobby." + lobby + ".World")) {
            player.sendMessage(Static.getParkourString() + "This lobby does not exist!");
            return;
        }

        Parkour.getPlugin().getConfig().set("Lobby." + lobby, null);
        Parkour.getPlugin().saveConfig();

        player.sendMessage(Static.getParkourString() + "Lobby " + lobby + " was deleted successfully.");
    }

    /**
     * Calculate the location when the player leaves / quits a course.
     * Used for finding the lobby if a custom is configured.
     *
     * @param player
     * @param session
     */
    public static void teleportToLeaveDestination(Player player, ParkourSession session) {
        String lobbyName = null;

        if (Parkour.getPlugin().getConfig().getBoolean("OnLeave.TeleportToLinkedLobby")) {
            lobbyName = CourseInfo.getLinkedLobby(session.getCourse().getName());
        }
        String[] args = new String[]{null, lobbyName};
        joinLobby(args, player);
    }

    /**
     * Get list of custom Lobbies.
     * I'm aware this looks like shit, but it's the best solution without a rewrite of lobbies.
     * @return Set<String>
     */
    public static Set<String> getCustomLobbies() {
        Set<String> lobbyListSet = Parkour.getPlugin().getConfig().getConfigurationSection("Lobby").getKeys(false);

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
     * @param sender
     */
    public static void displayLobbies(CommandSender sender) {
        sender.sendMessage(Utils.getStandardHeading("Custom Lobbies"));
        getCustomLobbies().forEach(s -> sender.sendMessage("* " + s));
    }
}

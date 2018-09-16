package me.A5H73Y.Parkour.Managers;

import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuietModeManager {

    private static QuietModeManager instance;

    private List<String> quietPlayers = new ArrayList<>();

    private final String quietOnMessage = Utils.getTranslation("Parkour.QuietOn");
    private final String quietOffMessage = Utils.getTranslation("Parkour.QuietOff");

    public static QuietModeManager getInstance() {
        if (instance == null) {
            instance = new QuietModeManager();

        }
        return instance;
    }

    private QuietModeManager() {}

    public static void enableQuietMode(Player player) {
        getInstance().quietPlayers.add(player.getName());
        Utils.sendActionBar(player, getInstance().quietOnMessage, true);
    }

    public static void disableQuietMode(Player player) {
        getInstance().quietPlayers.remove(player.getName());
        Utils.sendActionBar(player, getInstance().quietOffMessage, true);
    }

    public static boolean isInQuiteMode(String playerName) {
        return getInstance().quietPlayers.contains(playerName);
    }

    /**
     * Toggle quiet mode
     * Will add / remove the player from the list of quiet players.
     * If enabled, will limit the amount of Parkour messages displayed to the player.
     *
     * @param player
     */
    public static void toggleQuietMode(Player player) {
        if (isInQuiteMode(player.getName())) {
            enableQuietMode(player);
        } else {
            disableQuietMode(player);
        }
    }
}

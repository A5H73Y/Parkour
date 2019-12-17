package io.github.a5h73y.manager;

import java.util.ArrayList;
import java.util.List;

import io.github.a5h73y.utilities.Utils;
import org.bukkit.entity.Player;

public class QuietModeManager {

    private static QuietModeManager instance;
    private final String quietOnMessage = Utils.getTranslation("Parkour.QuietOn", false);
    private final String quietOffMessage = Utils.getTranslation("Parkour.QuietOff", false);
    private List<String> quietPlayers = new ArrayList<>();

    private QuietModeManager() {
    }

    public static QuietModeManager getInstance() {
        if (instance == null) {
            instance = new QuietModeManager();
        }

        return instance;
    }

    public void enableQuietMode(Player player) {
        Utils.sendActionBar(player, getInstance().quietOnMessage, true);
        quietPlayers.add(player.getName());
    }

    public void disableQuietMode(Player player) {
        quietPlayers.remove(player.getName());
        Utils.sendActionBar(player, getInstance().quietOffMessage, true);
    }

    public boolean isInQuietMode(String playerName) {
        return quietPlayers.contains(playerName);
    }

    /**
     * Toggle quiet mode
     * Will add / remove the player from the list of quiet players.
     * If enabled, will limit the amount of Parkour messages displayed to the player.
     *
     * @param player
     */
    public void toggleQuietMode(Player player) {
        if (isInQuietMode(player.getName())) {
            disableQuietMode(player);
        } else {
            enableQuietMode(player);
        }
    }
}

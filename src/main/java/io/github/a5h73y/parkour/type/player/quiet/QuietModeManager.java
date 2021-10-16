package io.github.a5h73y.parkour.type.player.quiet;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.CacheableParkourManager;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class QuietModeManager extends CacheableParkourManager {

    private List<String> quietPlayers = new ArrayList<>();

    public QuietModeManager(Parkour parkour) {
        super(parkour);
        initialiseQuietMode();
    }

    @Override
    protected QuietModeConfig getConfig() {
        return parkour.getConfigManager().getQuietModeConfig();
    }

    @Override
    public void teardown() {
        getConfig().setQuietPlayers(quietPlayers);
    }

    private void initialiseQuietMode() {
        this.quietPlayers = getConfig().getQuietPlayers();
    }

    public boolean isQuietMode(Player player) {
        return quietPlayers.contains(player.getName());
    }

    public void addPlayer(Player player) {
        this.quietPlayers.add(player.getName());
    }

    public void removePlayer(Player player) {
        this.quietPlayers.remove(player.getName());
    }

    /**
     * Toggle Player Quiet Mode.
     * Strange method because the action bar notification is only sent if they aren't currently in Quiet Mode.
     *
     * @param player requesting player
     */
    public void toggleQuietMode(Player player) {
        boolean currentlyQuiet = isQuietMode(player);

        if (currentlyQuiet) {
            removePlayer(player);
        }

        String messageKey = currentlyQuiet ? "Parkour.QuietOff" : "Parkour.QuietOn";

        parkour.getBountifulApi().sendActionBar(player,
                TranslationUtils.getTranslation(messageKey, false), true);

        if (!currentlyQuiet) {
            addPlayer(player);
        }
    }

    @Override
    public int getCacheSize() {
        return quietPlayers.size();
    }

    @Override
    public void clearCache() {
        quietPlayers.clear();
        initialiseQuietMode();
    }
}

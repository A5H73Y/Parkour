package io.github.a5h73y.parkour.listener;

import com.cryptomorin.xseries.XBlock;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.kit.ParkourKit;
import io.github.a5h73y.parkour.type.kit.ParkourKitAction;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerMoveListener extends AbstractPluginReceiver implements Listener {

    public PlayerMoveListener(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Handle Player Move.
     * While the Player is moving on a Course, handle any actions that should be performed.
     *
     * @param event PlayerMoveEvent
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!parkour.getParkourSessionManager().isPlaying(event.getPlayer())) {
            return;
        }

        Player player = event.getPlayer();
        ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

        // Only do fall checks if mode is not 'dropper' course
        if (session.getParkourMode() != ParkourMode.DROPPER
                && player.getFallDistance() > session.getCourse().getSettings().getMaxFallTicks()) {
            parkour.getPlayerManager().playerDie(player);
            return;
        }

        if (player.getLocation().getBlock().isLiquid()
                && session.getCourse().getSettings().isDieInLiquid()) {
            parkour.getPlayerManager().playerDie(player);
        }
    }
}

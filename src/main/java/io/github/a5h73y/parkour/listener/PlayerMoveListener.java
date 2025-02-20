package io.github.a5h73y.parkour.listener;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.course.CourseSettings;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

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
        CourseSettings courseSettings = session.getCourse().getSettings();

        if (courseSettings.hasMaxFallTicks() && player.getFallDistance() > courseSettings.getMaxFallTicks()) {
            parkour.getPlayerManager().playerDie(player);
            return;
        }

        Block block = player.getLocation().getBlock();
        if (block.isLiquid()) {
            Material material = block.getType();
            if ((material == Material.WATER && courseSettings.isDieInWater())
                    || (material == Material.LAVA && courseSettings.isDieInLava())) {
                parkour.getPlayerManager().playerDie(player);
            }
        }
    }
}

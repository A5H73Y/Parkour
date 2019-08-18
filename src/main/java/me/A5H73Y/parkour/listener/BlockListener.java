package me.A5H73Y.parkour.listener;

import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.player.PlayerMethods;
import me.A5H73Y.parkour.utilities.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        handleBlockPlaceBreakEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        handleBlockPlaceBreakEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }

        handleBlockPlaceBreakEvent((Player) event.getRemover(), event);
    }

    private void handleBlockPlaceBreakEvent(Player player, Cancellable event) {
        if (!PlayerMethods.isPlaying(player.getName())) {
            return;
        }

        if (!Utils.hasPermission(player, "Parkour.Admin")
                || (!Parkour.getInstance().getConfig().getBoolean("OnCourse.AdminPlaceBreakBlocks"))) {
            event.setCancelled(true);
        }
    }
}

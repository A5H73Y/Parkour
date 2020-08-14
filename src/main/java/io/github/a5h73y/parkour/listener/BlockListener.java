package io.github.a5h73y.parkour.listener;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class BlockListener extends AbstractPluginReceiver implements Listener {

    public BlockListener(final Parkour parkour) {
        super(parkour);
    }

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
        if (!parkour.getPlayerManager().isPlaying(player)) {
            return;
        }

        if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL, false)
                || (!parkour.getConfig().getBoolean("OnCourse.AdminPlaceBreakBlocks"))) {
            event.setCancelled(true);
        }
    }
}

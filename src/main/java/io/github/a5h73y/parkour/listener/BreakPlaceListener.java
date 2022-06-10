package io.github.a5h73y.parkour.listener;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class BreakPlaceListener extends AbstractPluginReceiver implements Listener {

    public BreakPlaceListener(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Prevent the Player from Placing Blocks.
     * @param event BlockPlaceEvent
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        handleBlockPlaceBreakEvent(event.getPlayer(), event);
    }

    /**
     * Prevent the Player from Breaking Blocks.
     * @param event BlockBreakEvent
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        handleBlockPlaceBreakEvent(event.getPlayer(), event);
    }

    /**
     * Prevent the Player from breaking Hanging Items.
     * @param event HangingBreakByEntityEvent
     */
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }

        handleBlockPlaceBreakEvent((Player) event.getRemover(), event);
    }

    private void handleBlockPlaceBreakEvent(Player player, Cancellable event) {
        if (!parkour.getParkourSessionManager().isPlaying(player)) {
            return;
        }

        if (parkour.getParkourConfig().getBoolean("OnCourse.AnybodyPlaceBreakBlocks")) {
            return;
        }

        if (!PermissionUtils.hasPermission(player, Permission.ADMIN_ALL, false)
                || !parkour.getParkourConfig().getBoolean("OnCourse.AdminPlaceBreakBlocks")) {
            event.setCancelled(true);
        }
    }
}

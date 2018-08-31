package me.A5H73Y.Parkour.Listeners;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
            return;

        if (!Utils.hasPermission(event.getPlayer(), "Parkour.Admin") ||
                (!Parkour.getPlugin().getConfig().getBoolean("OnCourse.AdminPlaceBreakBlocks")))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
            return;

        if (!Utils.hasPermission(event.getPlayer(), "Parkour.Admin") ||
                (!Parkour.getPlugin().getConfig().getBoolean("OnCourse.AdminPlaceBreakBlocks")))
            event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player) ||
                !PlayerMethods.isPlaying(event.getRemover().getName()))
            return;

        if (!Utils.hasPermission((Player) event.getRemover(), "Parkour.Admin")
                || (!Parkour.getPlugin().getConfig().getBoolean("OnCourse.AdminPlaceBreakBlocks")))
            event.setCancelled(true);
    }
}

package io.github.a5h73y.parkour.listener;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class PlayerListener extends AbstractPluginReceiver implements Listener {

    public PlayerListener(final Parkour parkour) {
        super(parkour);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (parkour.getPlayerManager().isPlaying((Player) event.getEntity())) {
                event.setCancelled(true);
            }

        } else if (event.getDamager() instanceof Player) {
            if (parkour.getPlayerManager().isPlaying((Player) event.getDamager())
                    && parkour.getConfig().isPreventAttackingEntities()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Player
                && parkour.getPlayerManager().isPlaying((Player) event.getEntity())) {
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!parkour.getPlayerManager().isPlaying(player)) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID
                && parkour.getConfig().getBoolean("OnCourse.DieInVoid")) {
            parkour.getPlayerManager().playerDie(player);
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (parkour.getConfig().getBoolean("OnCourse.DisableFallDamage") ||
                    (parkour.getPlayerManager().getParkourSession(player).getParkourMode() == ParkourMode.DROPPER
                            && !parkour.getConfig().getBoolean("ParkourModes.Dropper.FallDamage"))) {
                event.setDamage(0);
                event.setCancelled(true);
                return;
            }
        }

        if (parkour.getConfig().isDisablePlayerDamage()) {
            event.setDamage(0);
            event.setCancelled(true);
            return;
        }

        Damageable playerDamage = player;
        if (playerDamage.getHealth() <= event.getDamage()) {
            event.setDamage(0);
            event.setCancelled(true);
            parkour.getPlayerManager().playerDie(player);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (parkour.getPlayerManager().isPlaying((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (parkour.getConfig().getBoolean("OnCourse.DisableItemDrop")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (parkour.getConfig().getBoolean("OnCourse.DisableItemPickup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (parkour.getConfig().isDisplayWelcomeMessage()) {
            TranslationUtils.sendValueTranslation("Event.Join", parkour.getDescription().getVersion(), event.getPlayer());
        }

        parkour.getPlayerManager().loadParkourSession(event.getPlayer());

        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        String currentCourse = parkour.getPlayerManager().getParkourSession(event.getPlayer()).getCourse().getName();
        TranslationUtils.sendValueTranslation("Parkour.Continue", currentCourse, event.getPlayer());

        parkour.getScoreboardManager().addScoreboard(event.getPlayer());

        if (parkour.getConfig().getBoolean("OnLeaveServer.TeleportToLastCheckpoint")) {
            parkour.getPlayerManager().playerDie(event.getPlayer());
        }

        if (parkour.getConfig().isPlayerLeaveCourseOnLeaveServer()) {
            parkour.getPlayerManager().leaveCourse(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        parkour.getPlayerManager().teardownParkourPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (parkour.getPlayerManager().isPlayerInTestMode(event.getPlayer())) {
            return;
        }

        if (!parkour.getConfig().isCourseEnforceWorld()) {
            return;
        }

        if (event.getFrom().getWorld() != event.getTo().getWorld()) {
            if (parkour.getConfig().isCourseEnforceWorldLeaveCourse()) {
                parkour.getPlayerManager().leaveCourse(event.getPlayer(), false);

            } else {
                event.setCancelled(true);
                TranslationUtils.sendTranslation("Error.WorldTeleport", event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (!parkour.getConfig().getBoolean("OnCourse.DisableFly")) {
            return;
        }

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        if (!parkour.getPlayerManager().isPlaying((Player) event.getPlayer())) {
            return;
        }

        if (!parkour.getConfig().getBoolean("OnCourse.PreventOpeningOtherInventories")) {
            return;
        }

        if (event.getInventory().getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
        }
    }
}

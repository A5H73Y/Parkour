package io.github.a5h73y.parkour.listener;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class PlayerListener extends AbstractPluginReceiver implements Listener {

    public PlayerListener(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Handle Entity damages Entity.
     * Prevent a Parkour Player damaging others, also others damaging the Parkour Player.
     *
     * @param event EntityDamageByEntityEvent
     */
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

    /**
     * Handle Player Combust Event.
     * Prevent the Player from being set on Fire while on a Course.
     *
     * @param event EntityCombustEvent
     */
    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Player
                && parkour.getPlayerManager().isPlaying((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle Player Damage Event.
     * Prepare the Player when they receive a certain type of Damage.
     *
     * @param event EntityDamageEvent
     */
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

        // if the player takes fall damage
        // cancel damage if globally disabled or the ParkourMode is Dropper and fall damage is enabled
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL
                && (parkour.getConfig().getBoolean("OnCourse.DisableFallDamage")
                || (ParkourMode.DROPPER == parkour.getPlayerManager().getParkourSession(player).getParkourMode()
                && !parkour.getConfig().getBoolean("ParkourModes.Dropper.FallDamage")))) {
            event.setDamage(0);
            event.setCancelled(true);
            return;
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

    /**
     * Handle Player Respawn Event.
     * If the Player actually dies, once they respawn treat it like a Parkour death.
     *
     * @param event PlayerRespawnEvent
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            parkour.getPlayerManager().playerDie(event.getPlayer());
        }
    }

    /**
     * Handle Player Food Level Change.
     * Cancel the player from losing their food level while on a Course.
     *
     * @param event FoodLevelChangeEvent
     */
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (parkour.getPlayerManager().isPlaying((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle Player Drops Item.
     * Cancel the Player from dropping items while on a Course.
     *
     * @param event PlayerDropItemEvent
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (parkour.getConfig().getBoolean("OnCourse.DisableItemDrop")) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle Player Pickup Item.
     * Cancel the Player from dropping items while on a Course.
     *
     * @param event PlayerPickupItemEvent
     */
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (parkour.getConfig().getBoolean("OnCourse.DisableItemPickup")) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle Player Joining Server.
     * When the Player joins the server, they may have a ParkourSession to resume.
     *
     * @param event PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (parkour.getConfig().isDisplayWelcomeMessage()) {
            TranslationUtils.sendValueTranslation("Event.Join",
                    parkour.getDescription().getVersion(), event.getPlayer());
        }

        ParkourSession session = parkour.getPlayerManager().loadParkourSession(event.getPlayer());

        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        parkour.getScoreboardManager().addScoreboard(event.getPlayer(), session);

        String currentCourse = session.getCourse().getName();
        TranslationUtils.sendValueTranslation("Parkour.Continue", currentCourse, event.getPlayer());

        if (parkour.getConfig().getBoolean("OnLeaveServer.TeleportToLastCheckpoint")) {
            parkour.getPlayerManager().playerDie(event.getPlayer());
        }

        if (parkour.getConfig().isPlayerLeaveCourseOnLeaveServer()) {
            parkour.getPlayerManager().leaveCourse(event.getPlayer());
        }
    }

    /**
     * Handle Player Quitting Server.
     * Teardown the Player to remove any of their references before leaving.
     * If the Player is banned, attempt to delete all their Parkour data.
     *
     * @param event PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        if (parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            parkour.getPlayerManager().teardownParkourPlayer(event.getPlayer());
        }

        if (event.getPlayer().isBanned()
                && parkour.getConfig().getBoolean("Other.OnPlayerBan.ResetParkourInfo")) {
            parkour.getPlayerManager().resetPlayer(event.getPlayer());
        }
    }

    /**
     * Handle Player Teleporting.
     * Prevent the Player from leaving the Parkour world while on a Course.
     *
     * @param event PlayerTeleportEvent
     */
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
                parkour.getPlayerManager().leaveCourse(event.getPlayer(), true);

            } else {
                event.setCancelled(true);
                TranslationUtils.sendTranslation("Error.WorldTeleport", event.getPlayer());
            }
        }
    }

    /**
     * Handle Player Toggling Flight.
     * Prevent the Player from attempting to fly while on a Course.
     *
     * @param event PlayerToggleFlightEvent
     */
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

    /**
     * Handle Player opening Inventory.
     * Prevent the Player from opening non-player inventories.
     *
     * @param event InventoryOpenEvent
     */
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

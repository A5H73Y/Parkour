package io.github.a5h73y.parkour.listener;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
        if (event.getEntity() instanceof Player
                && parkour.getParkourSessionManager().isPlaying((Player) event.getEntity())
                && parkour.getParkourConfig().isPreventEntitiesAttacking()) {
            event.setCancelled(true);

        } else if (event.getDamager() instanceof Player
                && parkour.getParkourSessionManager().isPlaying((Player) event.getDamager())
                && parkour.getParkourConfig().isPreventAttackingEntities()) {
            event.setCancelled(true);
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
                && parkour.getParkourSessionManager().isPlaying((Player) event.getEntity())
                && parkour.getParkourConfig().getBoolean("OnCourse.PreventFireDamage")) {
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
        boolean playing = parkour.getParkourSessionManager().isPlaying(player);

        // they aren't on a Course and took void damage
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID
                && !playing && parkour.getParkourConfig().isVoidTeleportToLobby()) {
            parkour.getServer().getScheduler().runTaskLater(parkour, () ->
                    parkour.getLobbyManager().teleportToNearestLobby(player),1L);
        }

        if (!playing) {
            return;
        }

        ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

        // should the Player take fall damage
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL
                && !session.getCourse().getSettings().isHasFallDamage()) {
            event.setDamage(0);
            event.setCancelled(true);
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID
                && session.getCourse().getSettings().isDieInVoid()) {
            event.setDamage(0);
            event.setCancelled(true);
            parkour.getPlayerManager().playerDie(player);
            return;
        }

        if (parkour.getParkourConfig().isDisablePlayerDamage()) {
            event.setDamage(0);
            event.setCancelled(true);
            return;
        }

        Damageable playerDamage = player;
        if (playerDamage.getHealth() <= event.getFinalDamage()) {
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
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (parkour.getParkourSessionManager().isPlaying(event.getPlayer())) {
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

        if (parkour.getParkourSessionManager().isPlaying((Player) event.getEntity())) {
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
        if (!parkour.getParkourSessionManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (parkour.getParkourConfig().getBoolean("OnCourse.DisableItemDrop")) {
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
        if (!parkour.getParkourSessionManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (parkour.getParkourConfig().getBoolean("OnCourse.DisableItemPickup")) {
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
        Player player = event.getPlayer();

        if (parkour.getParkourConfig().isDisplayWelcomeMessage()) {
            TranslationUtils.sendValueTranslation("Event.Join",
                    parkour.getDescription().getVersion(), player);
        }

        if (!PlayerConfig.hasPlayerConfig(player)) {
            return;
        }

        PlayerConfig playerConfig = parkour.getConfigManager().getPlayerConfig(player);

        if (!playerConfig.hasExistingSessionCourseName()) {
            return;
        }

        ParkourSession session = parkour.getParkourSessionManager().loadParkourSession(player,
                playerConfig.getExistingSessionCourseName());
        playerConfig.removeExistingSessionCourseName();

        if (!parkour.getParkourSessionManager().isPlaying(player)) {
            return;
        }

        parkour.getScoreboardManager().addScoreboard(player, session);
        parkour.getPlayerManager().setupParkourMode(player);

        if (parkour.getParkourConfig().isPlayerLeaveCourseOnLeaveServer()) {
            parkour.getPlayerManager().leaveCourse(player);
            return;
        }

        String currentCourse = session.getCourse().getName();
        TranslationUtils.sendValueTranslation("Parkour.Continue", currentCourse, player);

        if (parkour.getParkourConfig().getBoolean("OnLeaveServer.TeleportToLastCheckpoint")) {
            parkour.getPlayerManager().playerDie(player);
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
        Player player = event.getPlayer();

        parkour.getPlayerManager().teardownParkourPlayer(player);

        if (parkour.getParkourConfig().getBoolean("Other.OnPlayerBan.ResetParkourInfo")
                && player.isBanned()) {
            parkour.getPlayerManager().resetPlayer(player);
        }
    }

    /**
     * Handle Player Teleporting.
     * Prevent the Player from leaving the Parkour world while on a Course.
     *
     * @param event PlayerTeleportEvent
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!parkour.getParkourSessionManager().isPlayingParkourCourse(player)) {
            return;
        }

        if (!parkour.getParkourConfig().isCourseEnforceWorld()) {
            return;
        }

        ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);
        World nextCheckpointWorld = session.getNextCheckpoint() != null
                ? session.getNextCheckpoint().getLocation().getWorld() : null;

        if (player.getWorld() != event.getTo().getWorld()
                && (nextCheckpointWorld == null || nextCheckpointWorld != event.getTo().getWorld())) {
            if (parkour.getParkourConfig().isCourseEnforceWorldLeaveCourse()) {
                parkour.getPlayerManager().leaveCourse(player, true);

            } else {
                event.setCancelled(true);
                TranslationUtils.sendTranslation("Error.WorldTeleport", player);
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
        Player player = event.getPlayer();

        if (!parkour.getParkourSessionManager().isPlaying(player)) {
            return;
        }

        if (!parkour.getParkourConfig().getBoolean("OnCourse.DisableFly")) {
            return;
        }

        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            player.setAllowFlight(false);
            player.setFlying(false);
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

        if (!parkour.getParkourSessionManager().isPlaying((Player) event.getPlayer())) {
            return;
        }

        if (!parkour.getParkourConfig().getBoolean("OnCourse.PreventOpeningOtherInventories")) {
            return;
        }

        if (event.getInventory().getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
        }
    }
}

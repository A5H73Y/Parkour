package io.github.a5h73y.listener;

import io.github.a5h73y.Parkour;
import io.github.a5h73y.enums.ParkourMode;
import io.github.a5h73y.manager.ChallengeManager;
import io.github.a5h73y.player.PlayerMethods;
import io.github.a5h73y.utilities.Static;
import io.github.a5h73y.utilities.Utils;
import org.bukkit.ChatColor;
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

public class PlayerListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (PlayerMethods.isPlaying(event.getEntity().getName())) {
                event.setCancelled(true);
            }

        } else if (event.getDamager() instanceof Player) {
            if (PlayerMethods.isPlaying(event.getDamager().getName())) {
                if (Parkour.getSettings().isPreventAttackingEntities()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Player) {
            if (PlayerMethods.isPlaying(event.getEntity().getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!PlayerMethods.isPlaying(player.getName())) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            if (Parkour.getInstance().getConfig().getBoolean("OnCourse.DieInVoid")) {
                PlayerMethods.playerDie(player);
                return;
            }
        }

        if (Parkour.getSettings().isDisablePlayerDamage()) {
            event.setDamage(0);
            return;
        }

        if (PlayerMethods.getParkourSession(player.getName()).getMode() == ParkourMode.DROPPER
                && event.getCause() == EntityDamageEvent.DamageCause.FALL
                && !Parkour.getInstance().getConfig().getBoolean("ParkourModes.Dropper.FallDamage")) {
            event.setDamage(0);
            event.setCancelled(true);
            return;
        }

        Damageable playerDamage = player;
        if (playerDamage.getHealth() <= event.getDamage()) {
            event.setDamage(0);
            event.setCancelled(true);
            PlayerMethods.playerDie(player);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (PlayerMethods.isPlaying(event.getEntity().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        if (Parkour.getInstance().getConfig().getBoolean("OnCourse.DisableItemDrop")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        if (Parkour.getInstance().getConfig().getBoolean("OnCourse.DisableItemPickup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Parkour.getSettings().isDisplayWelcomeMessage()) {
            event.getPlayer().sendMessage(Utils.getTranslation("Event.Join")
                    .replace("%VERSION%", Static.getVersion().toString()));
        }

        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        event.getPlayer().sendMessage(Utils.getTranslation("Parkour.Continue")
                .replace("%COURSE%", PlayerMethods.getParkourSession(event.getPlayer().getName()).getCourse().getName()));

        Parkour.getScoreboardManager().addScoreboard(event.getPlayer());

        if (Parkour.getInstance().getConfig().getBoolean("OnLeaveServer.TeleportToLastCheckpoint")) {
            PlayerMethods.playerDie(event.getPlayer());
        }

        if (Parkour.getSettings().isPlayerLeaveCourseOnLeaveServer()) {
            PlayerMethods.playerLeave(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        if (ChallengeManager.getInstance().isPlayerInChallenge(event.getPlayer().getName())) {
            ChallengeManager.getInstance().terminateChallenge(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        if (PlayerMethods.isPlayerInTestmode(event.getPlayer().getName())) {
            return;
        }

        if (event.getTo().getBlockX() == 0 && event.getTo().getBlockY() == 0 && event.getTo().getBlockZ() == 0) {
            event.getPlayer().sendMessage(Static.getParkourString() + ChatColor.RED + "This checkpoint is invalid. For safety you have been teleported to the lobby.");
            event.setCancelled(true);
            PlayerMethods.playerLeave(event.getPlayer());
            return;
        }

        if (!Parkour.getSettings().isEnforceWorld()) {
            return;
        }

        if (event.getFrom().getWorld() != event.getTo().getWorld()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Utils.getTranslation("Error.WorldTeleport"));
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        if (!Parkour.getInstance().getConfig().getBoolean("OnCourse.DisableFly")) {
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
        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        if (!Parkour.getInstance().getConfig().getBoolean("OnCourse.PreventOpeningOtherInventories")) {
            return;
        }

        if (event.getInventory().getType() != InventoryType.PLAYER) {
            event.setCancelled(true);
        }
    }
}

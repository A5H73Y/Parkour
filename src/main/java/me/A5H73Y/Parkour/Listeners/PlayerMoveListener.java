package me.A5H73Y.Parkour.Listeners;

import java.util.Arrays;
import java.util.List;

import me.A5H73Y.Parkour.Enums.ParkourMode;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.ParkourKit.ParkourKit;
import me.A5H73Y.Parkour.Player.ParkourSession;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerMoveListener implements Listener {

    private static final List<BlockFace> BLOCK_FACES =
            Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    @EventHandler
    public void onPlayerMove_Trails(PlayerMoveEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        if (!Parkour.getSettings().isTrailsEnabled()) {
            return;
        }

        Location loc = event.getPlayer().getLocation().add(0, 0.4, 0);
        event.getPlayer().getWorld().spawnParticle(Parkour.getSettings().getTrailParticle(), loc, 1);
    }

    @EventHandler
    public void onPlayerMove_ParkourMode(PlayerMoveEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        ParkourSession session = PlayerMethods.getParkourSession(event.getPlayer().getName());

        if (session == null || session.getMode() == ParkourMode.NONE) {
            return;
        }

        if (session.getMode() == ParkourMode.DRUNK) {
            if (event.getPlayer().hasPotionEffect(PotionEffectType.CONFUSION)) {
                return;
            }

            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10000, 1));

        } else if (session.getMode() == ParkourMode.DARKNESS) {
            if (event.getPlayer().hasPotionEffect(PotionEffectType.BLINDNESS)) {
                return;
            }

            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10000, 1));

        } else if (session.getMode() == ParkourMode.MOON) {
            if (event.getPlayer().hasPotionEffect(PotionEffectType.JUMP)) {
                return;
            }

            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000,
                    Parkour.getPlugin().getConfig().getInt("ParkourModes.Moon.Strength")));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        Player player = event.getPlayer();
        ParkourSession session = PlayerMethods.getParkourSession(player.getName());

        // Only do fall checks if mode is not 'dropper' course
        if (session.getMode() != ParkourMode.DROPPER &&
                player.getFallDistance() > Parkour.getSettings().getMaxFallTicks()) {
            PlayerMethods.playerDie(player);
            return;
        }

        if (player.getLocation().getBlock().isLiquid() &&
                Parkour.getPlugin().getConfig().getBoolean("OnCourse.DieInLiquid")) {
            PlayerMethods.playerDie(player);
        }

        if (!Parkour.getSettings().isUseParkourKit()) {
            return;
        }

        if (Parkour.getSettings().isAttemptLessChecks()) {
            if (event.getTo().getBlockX() == event.getFrom().getBlockX() &&
                    event.getTo().getBlockY() == event.getFrom().getBlockY() &&
                    event.getTo().getBlockZ() == event.getFrom().getBlockZ()) {
                return;
            }
        }

        Material belowMaterial = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        // if player is on half-block or jumping, get actual location.
        if (player.getLocation().getBlock().getType() != Material.AIR || !player.isOnGround()) {
            belowMaterial = player.getLocation().getBlock().getType();
        }
        ParkourKit kit = session.getCourse().getParkourKit();

        if (belowMaterial.equals(Material.SPONGE)) {
            player.setFallDistance(0);
        }

        if (kit.getMaterials().contains(belowMaterial)) {
            String action = kit.getAction(belowMaterial);

            switch (action) {
                case "finish":
                    PlayerMethods.playerFinish(player);
                    break;

                case "death":
                    PlayerMethods.playerDie(player);
                    break;

                case "launch":
                    player.setVelocity(new Vector(0, kit.getStrength(belowMaterial), 0));
                    break;

                case "bounce":
                    if (!player.hasPotionEffect(PotionEffectType.JUMP)) {
                        player.addPotionEffect(
                                new PotionEffect(PotionEffectType.JUMP,
                                        kit.getDuration(belowMaterial),
                                        kit.getStrength(belowMaterial).intValue()));
                    }
                    break;

                case "speed":
                    if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
                        player.addPotionEffect(
                                new PotionEffect(PotionEffectType.SPEED,
                                        kit.getDuration(belowMaterial),
                                        kit.getStrength(belowMaterial).intValue()));
                    }
                    break;

                case "norun":
                    player.setSprinting(false);
                    break;

                case "nopotion":
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        player.removePotionEffect(effect.getType());
                    }

                    player.setFireTicks(0);
                    break;
            }
        }

        for (BlockFace blockFace : BLOCK_FACES) {
            Material material = player.getLocation().getBlock().getRelative(blockFace).getType();

            if (kit.getMaterials().contains(material)) {
                String action = kit.getAction(material);

                switch (action) {
                    case "climb":
                        if (!player.isSneaking()) {
                            player.setVelocity(new Vector(0, kit.getStrength(material), 0));
                        }
                        break;
                    case "repulse":
                        double strength = kit.getStrength(material);
                        double x = blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH ? 0
                                : blockFace == BlockFace.EAST ? -strength : strength;
                        double z = blockFace == BlockFace.EAST || blockFace == BlockFace.WEST ? 0
                                : blockFace == BlockFace.NORTH ? strength : -strength;

                        player.setVelocity(new Vector(x, 0.1, z));
                        break;
                }
            }
        }
    }
}

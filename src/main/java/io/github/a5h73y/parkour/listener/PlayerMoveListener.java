package io.github.a5h73y.parkour.listener;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.kit.ParkourKit;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.player.ParkourSession;
import io.github.a5h73y.parkour.utility.support.XMaterial;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerMoveListener extends AbstractPluginReceiver implements Listener {

    private static final List<BlockFace> BLOCK_FACES =
            Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    public PlayerMoveListener(final Parkour parkour) {
        super(parkour);
    }

    @EventHandler
    public void onPlayerMove_ParkourMode(PlayerMoveEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer().getName())) {
            return;
        }

        ParkourMode courseMode = parkour.getPlayerManager().getParkourSession(event.getPlayer().getName()).getParkourMode();

        if (courseMode == ParkourMode.NONE) {
            return;
        }

        if (courseMode == ParkourMode.POTION_EFFECT) {
            // check they still have the potion effect, if not reapply it
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer().getName())) {
            return;
        }

        Player player = event.getPlayer();
        ParkourSession session = parkour.getPlayerManager().getParkourSession(player.getName());

        // Only do fall checks if mode is not 'dropper' course
        if (session.getParkourMode() != ParkourMode.DROPPER
                && player.getFallDistance() > parkour.getConfig().getMaxFallTicks()) {
            parkour.getPlayerManager().playerDie(player);
            return;
        }

        if (player.getLocation().getBlock().isLiquid()
                && parkour.getConfig().getBoolean("OnCourse.DieInLiquid")) {
            parkour.getPlayerManager().playerDie(player);
        }

        if (!parkour.getConfig().isUseParkourKit()) {
            return;
        }

        if (parkour.getConfig().isAttemptLessChecks()) {
            if (event.getTo().getBlockX() == event.getFrom().getBlockX() &&
                    event.getTo().getBlockY() == event.getFrom().getBlockY() &&
                    event.getTo().getBlockZ() == event.getFrom().getBlockZ()) {
                return;
            }
        }

        Material belowMaterial = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();

        // if player is on half-block or jumping, get actual location.
        if ((player.getLocation().getBlock().getType() != Material.AIR
                && player.getLocation().getBlock().getType() != XMaterial.CAVE_AIR.parseMaterial())
                || !player.isOnGround()) {
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
                    parkour.getPlayerManager().finishCourse(player);
                    break;

                case "death":
                    parkour.getPlayerManager().playerDie(player);
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

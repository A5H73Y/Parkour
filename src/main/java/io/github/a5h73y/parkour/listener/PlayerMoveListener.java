package io.github.a5h73y.parkour.listener;

import com.cryptomorin.xseries.XBlock;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.kit.ParkourKit;
import io.github.a5h73y.parkour.type.kit.ParkourKitAction;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerMoveListener extends AbstractPluginReceiver implements Listener {

    private static final List<BlockFace> BLOCK_FACES =
            Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    public PlayerMoveListener(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Handle Player Move.
     * While the Player is moving on a Course, handle any actions that should be performed.
     *
     * @param event PlayerMoveEvent
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!parkour.getParkourSessionManager().isPlaying(event.getPlayer())) {
            return;
        }

        Player player = event.getPlayer();
        ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

        // Only do fall checks if mode is not 'dropper' course
        if (session.getParkourMode() != ParkourMode.DROPPER
                && player.getFallDistance() > session.getCourse().getSettings().getMaxFallTicks()) {
            parkour.getPlayerManager().playerDie(player);
            return;
        }

        if (player.getLocation().getBlock().isLiquid()
                && parkour.getParkourConfig().getBoolean("OnCourse.DieInLiquid")) {
            parkour.getPlayerManager().playerDie(player);
        }

        if (!parkour.getParkourConfig().isUseParkourKit()) {
            return;
        }

        ParkourKit kit = session.getCourse().getParkourKit();

        if (kit == null) {
            return;
        }

        if (parkour.getParkourConfig().isAttemptLessChecks()
                && MaterialUtils.sameBlockLocations(event.getFrom(), event.getTo())) {
            return;
        }

        Material belowMaterial = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();

        // if player is on half-block or jumping, get actual location.
        if (!parkour.getParkourConfig().isLegacyGroundDetection()
                && (!XBlock.isAir(player.getLocation().getBlock().getType()) || !player.isOnGround())) {
            belowMaterial = player.getLocation().getBlock().getType();
        }

        // they are clearly hovering and another block is holding them up
        if (player.isOnGround() && XBlock.isAir(belowMaterial)) {
            belowMaterial = calculateClosestBlock(player);
        }

        if (belowMaterial.equals(Material.SPONGE)) {
            player.setFallDistance(0);
        }

        ParkourKitAction kitAction = kit.getAction(belowMaterial);

        if (kitAction != null) {
            switch (kitAction.getActionType()) {
                case FINISH:
                    parkour.getPlayerManager().finishCourse(player);
                    break;

                case DEATH:
                    parkour.getPlayerManager().playerDie(player);
                    break;

                case LAUNCH:
                    player.setVelocity(new Vector(0, kitAction.getStrength(), 0));
                    break;

                case BOUNCE:
                    if (!player.hasPotionEffect(PotionEffectType.JUMP)) {
                        PlayerUtils.applyPotionEffect(PotionEffectType.JUMP, kitAction.getDuration(),
                                (int) kitAction.getStrength(), player);
                    }
                    break;

                case SPEED:
                    if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
                        PlayerUtils.applyPotionEffect(PotionEffectType.SPEED, kitAction.getDuration(),
                                (int) kitAction.getStrength(), player);
                    }
                    break;

                case POTION:
                    PotionEffectType effect = PotionEffectType.getByName(kitAction.getEffect());
                    if (effect != null) {
                        PlayerUtils.applyPotionEffect(effect, kitAction.getDuration(),
                                (int) kitAction.getStrength(), player);
                    }
                    break;

                case NORUN:
                    player.setSprinting(false);
                    break;

                case NOPOTION:
                    PlayerUtils.removeAllPotionEffects(player);
                    player.setFireTicks(0);
                    break;

                default:
                    break;
            }
        } else {
            for (BlockFace blockFace : BLOCK_FACES) {
                Material material = player.getLocation().getBlock().getRelative(blockFace).getType();
                kitAction = kit.getAction(material);

                if (kitAction != null) {
                    switch (kitAction.getActionType()) {
                        case CLIMB:
                            if (!player.isSneaking()) {
                                player.setVelocity(new Vector(0, kitAction.getStrength(), 0));
                            }
                            break;

                        case REPULSE:
                            double strength = kitAction.getStrength();
                            double x = blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH ? 0
                                    : blockFace == BlockFace.EAST ? -strength : strength;
                            double z = blockFace == BlockFace.EAST || blockFace == BlockFace.WEST ? 0
                                    : blockFace == BlockFace.NORTH ? strength : -strength;

                            player.setVelocity(new Vector(x, 0.1, z));
                            break;

                        default:
                    }
                }
            }
        }
    }

    private Material calculateClosestBlock(Player player) {
        Block blockBelow = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        BlockFace result = BLOCK_FACES.stream()
                .filter(blockFace -> !XBlock.isAir(blockBelow.getRelative(blockFace).getType()))
                .min(Comparator.comparing(blockFace ->
                        blockBelow.getRelative(blockFace).getLocation().distance(player.getLocation())))
                .orElse(BlockFace.NORTH);

        return blockBelow.getRelative(result).getType();
    }
}

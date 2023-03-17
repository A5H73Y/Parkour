package io.github.a5h73y.parkour.listener.move;

import static io.github.a5h73y.parkour.listener.move.ParkourBlockListener.BLOCK_FACES;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.kit.ParkourKit;
import io.github.a5h73y.parkour.type.kit.ParkourKitAction;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class VehicleMoveListener extends AbstractPluginReceiver implements Listener {

    public VehicleMoveListener(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Handle Player Move.
     * While the Player is moving on a Course, handle any actions that should be performed.
     *
     * @param event VehicleMoveEvent
     */
    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!(event.getVehicle().getPassenger() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getVehicle().getPassenger();

        if (!parkour.getParkourSessionManager().isPlaying(player)) {
            return;
        }

        ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);
        ParkourKit parkourKit = session.getCourse().getParkourKit();

        if (parkourKit == null) {
            return;
        }

        if (parkour.getParkourConfig().isAttemptLessChecks()
                && MaterialUtils.sameBlockLocations(event.getFrom(), event.getTo())) {
            return;
        }

        Material belowMaterial = event.getVehicle().getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        ParkourKitAction kitAction = parkourKit.getAction(belowMaterial);

        if (kitAction != null && parkourKit.isHasFloorActions()) {
            performFloorAction(player, event.getVehicle(), kitAction);
        } else if (parkourKit.isHasWallActions()) {
            performWallAction(player, event.getVehicle(), parkourKit);
        }
    }

    private void performFloorAction(Player player, Vehicle vehicle, ParkourKitAction kitAction) {
        switch (kitAction.getActionType()) {
            case FINISH:
                parkour.getPlayerManager().finishCourse(player);
                break;

            case DEATH:
                parkour.getPlayerManager().playerDie(player);
                break;

            case LAUNCH:
                vehicle.setVelocity(new Vector(0, 0.5, 0));
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
    }

    private void performWallAction(Player player, Vehicle vehicle, ParkourKit parkourKit) {
        for (BlockFace blockFace : BLOCK_FACES) {
            Material material = player.getLocation().getBlock().getRelative(blockFace).getType();
            ParkourKitAction kitAction = parkourKit.getAction(material);

            if (kitAction != null) {
                switch (kitAction.getActionType()) {
                    case CLIMB:
                        vehicle.setVelocity(new Vector(0, kitAction.getStrength(), 0));
                        break;

                    case REPULSE:
                        double strength = kitAction.getStrength();
                        double x = blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH ? 0
                                : blockFace == BlockFace.EAST ? -strength : strength;
                        double z = blockFace == BlockFace.EAST || blockFace == BlockFace.WEST ? 0
                                : blockFace == BlockFace.NORTH ? strength : -strength;

                        vehicle.setVelocity(new Vector(x, 0.5, z));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}

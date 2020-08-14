package io.github.a5h73y.parkour.listener;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.support.XMaterial;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener extends AbstractPluginReceiver implements Listener {

    public PlayerInteractListener(final Parkour parkour) {
        super(parkour);
    }

    @EventHandler
    public void onInventoryInteract(PlayerInteractEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        Player player = event.getPlayer();

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        if (!player.isSneaking() && parkour.getConfig().getBoolean("OnCourse.SneakToInteractItems")) {
            return;
        }

        if (parkour.getPlayerManager().isPlayerInTestMode(player)) {
            return;
        }

        if (event.getClickedBlock() != null
                && event.getClickedBlock().getState() instanceof Sign) {
            return;
        }

        if (MaterialUtils.getMaterialInPlayersHand(player) == parkour.getConfig().getLastCheckpointTool()) {
            if (parkour.getPlayerManager().delayPlayer(player, 1, false)) {
                event.setCancelled(true);
                parkour.getPlayerManager().playerDie(player);
            }

        } else if (MaterialUtils.getMaterialInPlayersHand(player) == parkour.getConfig().getHideallTool()) {
            if (parkour.getPlayerManager().delayPlayer(player, 1, false)) {
                event.setCancelled(true);
                parkour.getPlayerManager().toggleVisibility(player);
            }

        } else if (MaterialUtils.getMaterialInPlayersHand(player) == parkour.getConfig().getLeaveTool()) {
            if (parkour.getPlayerManager().delayPlayer(player, 1, false)) {
                event.setCancelled(true);
                parkour.getPlayerManager().leaveCourse(player);
            }

        } else if (MaterialUtils.getMaterialInPlayersHand(player) == parkour.getConfig().getRestartTool()) {
            if (parkour.getPlayerManager().delayPlayer(player, 1, false)) {
                event.setCancelled(true);
                parkour.getPlayerManager().restartCourse(player);
            }
        }
    }

    @EventHandler
    public void onInventoryInteract_ParkourMode(PlayerInteractEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)
                && !event.getAction().equals(Action.LEFT_CLICK_AIR) && !event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        ParkourMode mode = parkour.getPlayerManager().getParkourSession(event.getPlayer()).getParkourMode();

        if (mode != ParkourMode.FREEDOM && mode != ParkourMode.ROCKETS) {
            return;
        }

        Player player = event.getPlayer();

        if (parkour.getPlayerManager().isPlayerInTestMode(player)) {
            return;
        }

        event.setCancelled(true);

        if (mode == ParkourMode.FREEDOM && MaterialUtils.getMaterialInPlayersHand(player) == XMaterial.REDSTONE_TORCH.parseMaterial()) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                parkour.getPlayerManager().getParkourSession(player)
                        .setFreedomLocation(parkour.getCheckpointManager().createCheckpointFromPlayerLocation(player).getLocation());
                TranslationUtils.sendTranslation("Mode.Freedom.Save", player);

            } else {
                player.teleport(parkour.getPlayerManager().getParkourSession(player).getFreedomLocation());
                TranslationUtils.sendTranslation("Mode.Freedom.Load", player);
            }

        } else if (mode == ParkourMode.ROCKETS && MaterialUtils.getMaterialInPlayersHand(player) == XMaterial.FIREWORK_ROCKET.parseMaterial()) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (parkour.getPlayerManager().delayPlayer(player, 1, false)) {
                    parkour.getPlayerManager().rocketLaunchPlayer(player);
                }
            }
        }
    }

    @EventHandler
    public void onCheckpointEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL ||
                !parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (event.getClickedBlock().getType() !=
                XMaterial.fromString(parkour.getConfig().getCheckpointMaterial()).parseMaterial()) {
            return;
        }

        if (parkour.getConfig().getBoolean("OnCourse.PreventPlateStick")) {
            event.setCancelled(true);
        }

        ParkourSession session = parkour.getPlayerManager().getParkourSession(event.getPlayer());
        Course course = session.getCourse();

        if (session.hasAchievedAllCheckpoints()) {
            return;
        }

        Checkpoint checkpoint = session.getCheckpoint();

        Location below = event.getClickedBlock().getRelative(BlockFace.DOWN).getLocation();

        if (checkpoint.getNextCheckpointX() == below.getBlockX()
                && checkpoint.getNextCheckpointY() == below.getBlockY()
                && checkpoint.getNextCheckpointZ() == below.getBlockZ()) {
            if (parkour.getConfig().isFirstCheckAsStart() && session.getCurrentCheckpoint() == 0) {
                session.resetTimeStarted();
                parkour.getBountifulApi().sendActionBar(event.getPlayer(), TranslationUtils.getTranslation("Parkour.TimerStarted", false), true);
            }
            parkour.getPlayerManager().increaseCheckpoint(event.getPlayer());
        }
    }

    @EventHandler
    public void onAutoStartEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        if (parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (!parkour.getConfig().isAutoStartEnabled()) {
            return;
        }

        Block below = event.getClickedBlock().getRelative(BlockFace.DOWN);

        if (below.getType() != parkour.getConfig().getAutoStartMaterial()) {
            return;
        }

        // Prevent a user spamming the joins
        if (!parkour.getPlayerManager().delayPlayer(event.getPlayer(), 3, false)) {
            return;
        }

        String courseName = parkour.getCourseManager().getAutoStartCourse(event.getClickedBlock().getLocation());

        if (courseName != null) {
            parkour.getPlayerManager().joinCourseButDelayed(event.getPlayer(), courseName, parkour.getConfig().getAutoStartDelay());
        }
    }
}

package io.github.a5h73y.listener;

import io.github.a5h73y.Parkour;
import io.github.a5h73y.course.Checkpoint;
import io.github.a5h73y.course.CheckpointMethods;
import io.github.a5h73y.course.Course;
import io.github.a5h73y.course.CourseMethods;
import io.github.a5h73y.enums.ParkourMode;
import io.github.a5h73y.player.ParkourSession;
import io.github.a5h73y.player.PlayerMethods;
import io.github.a5h73y.utilities.Utils;
import io.github.a5h73y.utilities.XMaterial;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInventoryInteract(PlayerInteractEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        Player player = event.getPlayer();

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        if (!player.isSneaking() && Parkour.getInstance().getConfig().getBoolean("OnCourse.SneakToInteractItems")) {
            return;
        }

        if (PlayerMethods.isPlayerInTestmode(player.getName())) {
            return;
        }

        if (event.getClickedBlock() != null
                && event.getClickedBlock().getState() instanceof Sign) {
            return;
        }

        if (Utils.getMaterialInPlayersHand(player) == Parkour.getSettings().getLastCheckpointTool()) {
            if (Utils.delayPlayerEvent(player, 1)) {
                event.setCancelled(true);
                PlayerMethods.playerDie(player);
            }

        } else if (Utils.getMaterialInPlayersHand(player) == Parkour.getSettings().getHideallTool()) {
            if (Utils.delayPlayerEvent(player, 1)) {
                event.setCancelled(true);
                Utils.toggleVisibility(player);
            }

        } else if (Utils.getMaterialInPlayersHand(player) == Parkour.getSettings().getLeaveTool()) {
            if (Utils.delayPlayerEvent(player, 1)) {
                event.setCancelled(true);
                PlayerMethods.playerLeave(player);
            }

        } else if (Utils.getMaterialInPlayersHand(player) == Parkour.getSettings().getRestartTool()) {
            if (Utils.delayPlayerEvent(player, 1)) {
                event.setCancelled(true);
                PlayerMethods.restartCourse(player);
            }
        }
    }

    @EventHandler
    public void onInventoryInteract_ParkourMode(PlayerInteractEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)
                && !event.getAction().equals(Action.LEFT_CLICK_AIR) && !event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        ParkourMode mode = PlayerMethods.getParkourSession(event.getPlayer().getName()).getMode();

        if (mode != ParkourMode.FREEDOM && mode != ParkourMode.ROCKETS) {
            return;
        }

        Player player = event.getPlayer();

        if (PlayerMethods.isPlayerInTestmode(player.getName())) {
            return;
        }

        event.setCancelled(true);

        if (mode == ParkourMode.FREEDOM && Utils.getMaterialInPlayersHand(player) == XMaterial.REDSTONE_TORCH.parseMaterial()) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                PlayerMethods.getParkourSession(player.getName()).getCourse().setCheckpoint(CheckpointMethods.createCheckpointFromPlayerLocation(player));
                player.sendMessage(Utils.getTranslation("Mode.Freedom.Save"));
            } else {
                player.teleport(PlayerMethods.getParkourSession(player.getName()).getCourse().getCurrentCheckpoint().getLocation());
                player.sendMessage(Utils.getTranslation("Mode.Freedom.Load"));
            }

        } else if (mode == ParkourMode.ROCKETS && Utils.getMaterialInPlayersHand(player) == XMaterial.FIREWORK_ROCKET.parseMaterial()) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (Utils.delayPlayerEvent(player, 1)) {
                    PlayerMethods.rocketLaunchPlayer(player);
                }
            }
        }
    }

    @EventHandler
    public void onCheckpointEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL ||
                !PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        if (event.getClickedBlock().getType() !=
                XMaterial.fromString(Parkour.getSettings().getCheckpointMaterial()).parseMaterial()) {
            return;
        }

        if (Parkour.getInstance().getConfig().getBoolean("OnCourse.PreventPlateStick")) {
            event.setCancelled(true);
        }

        ParkourSession session = PlayerMethods.getParkourSession(event.getPlayer().getName());
        Course course = session.getCourse();

        if (session.getCheckpoint() == course.getCheckpoints()) {
            return;
        }

        Checkpoint check = course.getCurrentCheckpoint();

        if (check == null) {
            return;
        }

        Location below = event.getClickedBlock().getRelative(BlockFace.DOWN).getLocation();

        if (check.getNextCheckpointX() == below.getBlockX()
                && check.getNextCheckpointY() == below.getBlockY()
                && check.getNextCheckpointZ() == below.getBlockZ()) {
            if (Parkour.getSettings().isFirstCheckAsStart() && session.getCheckpoint() == 0) {
                session.resetTimeStarted();
                Utils.sendActionBar(event.getPlayer(), Utils.getTranslation("Parkour.TimerStarted", false), true);
            }
            PlayerMethods.increaseCheckpoint(session, event.getPlayer());
        }
    }

    @EventHandler
    public void onAutoStartEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        if (PlayerMethods.isPlaying(event.getPlayer().getName())) {
            return;
        }

        if (!Parkour.getSettings().isAutoStartEnabled()) {
            return;
        }

        Block below = event.getClickedBlock().getRelative(BlockFace.DOWN);

        if (below.getType() != Parkour.getSettings().getAutoStartMaterial()) {
            return;
        }

        // Prevent a user spamming the joins
        if (!Utils.delayPlayer(event.getPlayer(), 3, false)) {
            return;
        }

        String courseName = CourseMethods.getAutoStartCourse(event.getClickedBlock().getLocation());

        if (courseName != null) {
            CourseMethods.joinCourseButDelayed(event.getPlayer(), courseName, Parkour.getSettings().getAutoStartDelay());
        }
    }
}

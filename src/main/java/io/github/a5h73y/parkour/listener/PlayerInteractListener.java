package io.github.a5h73y.parkour.listener;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.ParkourEventType;
import io.github.a5h73y.parkour.enums.ParkourMode;
import io.github.a5h73y.parkour.enums.QuestionType;
import io.github.a5h73y.parkour.enums.SoundType;
import io.github.a5h73y.parkour.manager.QuestionManager;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener extends AbstractPluginReceiver implements Listener {

    public PlayerInteractListener(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Handle Player Interaction Event.
     * Used for the Parkour Tools whilst on a Course.
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onInventoryInteract(PlayerInteractEvent event) {
        if (!parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        if (PluginUtils.getMinorServerVersion() > 8 && !EquipmentSlot.HAND.equals(event.getHand())) {
            return;
        }

        Player player = event.getPlayer();
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

        Material materialInHand = MaterialUtils.getMaterialInPlayersHand(player);

        if (XBlock.isAir(materialInHand)) {
            return;
        }

        if (materialInHand == parkour.getConfig().getLastCheckpointTool()) {
            if (parkour.getPlayerManager().delayPlayer(player, 1)) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTask(parkour, () -> parkour.getPlayerManager().playerDie(player));
            }

        } else if (materialInHand == parkour.getConfig().getHideAllDisabledTool()
                || materialInHand == parkour.getConfig().getHideAllEnabledTool()) {
            if (parkour.getPlayerManager().delayPlayer(player, 1)) {
                event.setCancelled(true);
                parkour.getPlayerManager().toggleVisibility(player);
                player.getInventory().remove(materialInHand);
                String configPath = parkour.getPlayerManager().hasHiddenPlayers(player)
                        ? "ParkourTool.HideAllEnabled" : "ParkourTool.HideAll";
                parkour.getPlayerManager().giveParkourTool(player, configPath, configPath);
            }

        } else if (materialInHand == parkour.getConfig().getLeaveTool()) {
            if (parkour.getPlayerManager().delayPlayer(player, 1)) {
                event.setCancelled(true);
                parkour.getPlayerManager().leaveCourse(player);
            }

        } else if (materialInHand == parkour.getConfig().getRestartTool()) {
            if (parkour.getPlayerManager().delayPlayer(player,
                    parkour.getConfig().getInt("ParkourTool.Restart.SecondCooldown"))) {

                if (parkour.getConfig().getBoolean("OnRestart.RequireConfirmation")) {
                    if (!parkour.getQuestionManager().hasBeenAskedQuestion(player, QuestionType.RESTART_COURSE)) {
                        String courseName = parkour.getPlayerManager().getParkourSession(player).getCourseName();
                        parkour.getQuestionManager().askRestartProgressQuestion(player, courseName);
                    } else {
                        parkour.getQuestionManager().answerQuestion(player, QuestionManager.YES);
                    }
                } else {
                    event.setCancelled(true);
                    Bukkit.getScheduler().runTask(parkour, () -> parkour.getPlayerManager().restartCourse(player));
                }
            }
        }
    }

    /**
     * Handle Player Interaction Event.
     * Used to support the ParkourMode while on a Course.
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onInventoryInteractParkourMode(PlayerInteractEvent event) {
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

        if (mode == ParkourMode.FREEDOM
                && MaterialUtils.getMaterialInPlayersHand(player) == XMaterial.REDSTONE_TORCH.parseMaterial()) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                parkour.getPlayerManager().getParkourSession(player).setFreedomLocation(
                        parkour.getCheckpointManager().createCheckpointFromPlayerLocation(player).getLocation());
                TranslationUtils.sendTranslation("Mode.Freedom.Save", player);

            } else {
                PlayerUtils.teleportToLocation(player,
                        parkour.getPlayerManager().getParkourSession(player).getFreedomLocation());
                TranslationUtils.sendTranslation("Mode.Freedom.Load", player);
            }

        } else if (mode == ParkourMode.ROCKETS
                && MaterialUtils.getMaterialInPlayersHand(player) == XMaterial.FIREWORK_ROCKET.parseMaterial()) {

            int secondDelay = parkour.getConfig().getInt("ParkourModes.Rockets.Delay");
            if (parkour.getPlayerManager().delayPlayer(player, secondDelay, "Mode.Rockets.Reloading", false)) {
                parkour.getPlayerManager().rocketLaunchPlayer(player);
            }
        }
    }

    /**
     * Handle Player Interaction Event.
     * Used to handle the pressure plate interaction while on a Course.
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onCheckpointEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL
                || !parkour.getPlayerManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (event.getClickedBlock().getType() != parkour.getConfig().getCheckpointMaterial()) {
            return;
        }

        if (parkour.getConfig().getBoolean("OnCourse.PreventPlateStick")) {
            event.setCancelled(true);
        }

        ParkourSession session = parkour.getPlayerManager().getParkourSession(event.getPlayer());

        if (session.getParkourMode() == ParkourMode.FREE_CHECKPOINT
                && parkour.getPlayerManager().delayPlayer(event.getPlayer(), 1)
                && (session.getFreedomLocation() == null
                || !MaterialUtils.sameBlockLocations(event.getPlayer().getLocation(), session.getFreedomLocation()))) {

            session.setFreedomLocation(event.getPlayer().getLocation());
            if (parkour.getConfig().isTreatFirstCheckpointAsStart() && session.getFreedomLocation() == null) {
                session.resetTime();
                session.setStartTimer(true);
                parkour.getBountifulApi().sendActionBar(event.getPlayer(),
                        TranslationUtils.getTranslation("Parkour.TimerStarted", false), true);
            }
            parkour.getSoundsManager().playSound(event.getPlayer(), SoundType.CHECKPOINT_ACHIEVED);
            boolean showTitle = parkour.getConfig().getBoolean("DisplayTitle.Checkpoint");

            String checkpointMessage = TranslationUtils.getCourseEventMessage(session,
                    ParkourEventType.CHECKPOINT, "Event.FreeCheckpoints");

            parkour.getBountifulApi().sendSubTitle(event.getPlayer(), checkpointMessage, showTitle);
            return;
        }

        if (session.hasAchievedAllCheckpoints()) {
            return;
        }

        Checkpoint checkpoint = session.getCheckpoint();
        Location below = event.getClickedBlock().getRelative(BlockFace.DOWN).getLocation();

        if (checkpoint.getNextCheckpointX() == below.getBlockX()
                && checkpoint.getNextCheckpointY() == below.getBlockY()
                && checkpoint.getNextCheckpointZ() == below.getBlockZ()) {
            if (parkour.getConfig().isTreatFirstCheckpointAsStart() && session.getCurrentCheckpoint() == 0) {
                session.resetTime();
                session.setStartTimer(true);
                parkour.getBountifulApi().sendActionBar(event.getPlayer(),
                        TranslationUtils.getTranslation("Parkour.TimerStarted", false), true);
            }
            parkour.getPlayerManager().increaseCheckpoint(event.getPlayer());
        }
    }

    /**
     * Handle Player Interaction Event.
     * Used to handle the pressure plate interaction while NOT on a Course.
     * This is used to identify if the plate matches an AutoStart location.
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onAutoStartEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        if (!parkour.getConfig().isAutoStartEnabled()) {
            return;
        }

        Block below = event.getClickedBlock().getRelative(BlockFace.DOWN);

        if (below.getType() != parkour.getConfig().getAutoStartMaterial()) {
            return;
        }

        if (parkour.getConfig().getBoolean("OnCourse.PreventPlateStick")) {
            event.setCancelled(true);
        }

        // Prevent a user spamming the joins
        if (!parkour.getPlayerManager().delayPlayer(event.getPlayer(), 1)) {
            return;
        }

        String courseName = parkour.getCourseManager().getAutoStartCourse(event.getClickedBlock().getLocation());

        if (courseName != null) {
            ParkourSession session = parkour.getPlayerManager().getParkourSession(event.getPlayer());
            if (session != null) {
                // we only want to do something if the names match
                if (session.getCourseName().equals(courseName)) {
                    Bukkit.getScheduler().runTask(parkour, () -> {
                        session.resetProgress();
                        session.setFreedomLocation(null);
                    });
                }
            } else {
                parkour.getPlayerManager().joinCourseButDelayed(
                        event.getPlayer(), courseName, parkour.getConfig().getAutoStartDelay());
            }
        }
    }
}

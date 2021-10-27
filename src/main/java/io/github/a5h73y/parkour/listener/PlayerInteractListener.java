package io.github.a5h73y.parkour.listener;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.course.ParkourEventType;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.type.question.QuestionManager;
import io.github.a5h73y.parkour.type.question.QuestionType;
import io.github.a5h73y.parkour.type.sounds.SoundType;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

// TODO combine some of these methods, as most of them start the same way
// TODO breakout some of the crazy big if statements into a method. (Freedom tool etc.)
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
        if (!player.isSneaking() && parkour.getParkourConfig().getBoolean("OnCourse.SneakToInteractItems")) {
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

        if (materialInHand == parkour.getParkourConfig().getLastCheckpointTool()) {
            if (parkour.getPlayerManager().delayPlayer(player, 1)) {
                event.setCancelled(true);
                parkour.getPlayerManager().playerDie(player);
            }

        } else if (materialInHand == parkour.getParkourConfig().getHideAllDisabledTool()
                || materialInHand == parkour.getParkourConfig().getHideAllEnabledTool()) {
            if (parkour.getPlayerManager().delayPlayer(player, 1)) {
                event.setCancelled(true);
                parkour.getPlayerManager().toggleVisibility(player);
                player.getInventory().remove(materialInHand);
                String configPath = parkour.getPlayerManager().hasHiddenPlayers(player)
                        ? "ParkourTool.HideAllEnabled" : "ParkourTool.HideAll";
                parkour.getPlayerManager().giveParkourTool(player, configPath, configPath);
            }

        } else if (materialInHand == parkour.getParkourConfig().getLeaveTool()) {
            if (parkour.getPlayerManager().delayPlayer(player, 1)) {
                event.setCancelled(true);
                parkour.getPlayerManager().leaveCourse(player);
            }

        } else if (materialInHand == parkour.getParkourConfig().getRestartTool()) {
            if (parkour.getPlayerManager().delayPlayer(player,
                    parkour.getParkourConfig().getInt("ParkourTool.Restart.SecondCooldown"))) {

                if (parkour.getParkourConfig().getBoolean("OnRestart.RequireConfirmation")) {
                    if (!parkour.getQuestionManager().hasBeenAskedQuestion(player, QuestionType.RESTART_COURSE)) {
                        String courseName = parkour.getPlayerManager().getParkourSession(player).getCourseName();
                        parkour.getQuestionManager().askRestartProgressQuestion(player, courseName);
                    } else {
                        parkour.getQuestionManager().answerQuestion(player, QuestionManager.YES);
                    }
                } else {
                    event.setCancelled(true);
                    parkour.getPlayerManager().restartCourse(player);
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
            if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                    || event.getAction().equals(Action.RIGHT_CLICK_AIR))
                    && player.isOnGround()
                    && parkour.getPlayerManager().delayPlayer(event.getPlayer(), parkour.getParkourConfig().getInt(
                    "ParkourTool.Freedom.SecondCooldown"))) {
                parkour.getPlayerManager().getParkourSession(player).setFreedomLocation(
                        parkour.getCheckpointManager().createCheckpointFromPlayerLocation(player).getLocation());
                TranslationUtils.sendTranslation("Mode.Freedom.Save", player);

            } else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)
                    || event.getAction().equals(Action.LEFT_CLICK_AIR)) {
                PlayerUtils.teleportToLocation(player,
                        parkour.getPlayerManager().getParkourSession(player).getFreedomLocation());
                TranslationUtils.sendTranslation("Mode.Freedom.Load", player);
            }

        } else if (mode == ParkourMode.ROCKETS
                && MaterialUtils.getMaterialInPlayersHand(player) == XMaterial.FIREWORK_ROCKET.parseMaterial()) {

            int secondDelay = parkour.getParkourConfig().getInt("ParkourModes.Rockets.SecondCooldown");
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

        if (event.getClickedBlock().getType() != parkour.getParkourConfig().getCheckpointMaterial()) {
            return;
        }

        if (parkour.getParkourConfig().getBoolean("OnCourse.PreventPlateStick")) {
            event.setCancelled(true);
        }

        ParkourSession session = parkour.getPlayerManager().getParkourSession(event.getPlayer());

        if (session.getParkourMode() == ParkourMode.FREE_CHECKPOINT
                && parkour.getPlayerManager().delayPlayer(event.getPlayer(), 1)
                && (session.getFreedomLocation() == null
                || !MaterialUtils.sameBlockLocations(event.getPlayer().getLocation(), session.getFreedomLocation()))) {

            session.setFreedomLocation(event.getPlayer().getLocation());
            if (parkour.getParkourConfig().isTreatFirstCheckpointAsStart() && session.getFreedomLocation() == null) {
                session.resetTime();
                session.setStartTimer(true);
                parkour.getBountifulApi().sendActionBar(event.getPlayer(),
                        TranslationUtils.getTranslation("Parkour.TimerStarted", false), true);
            }
            parkour.getSoundsManager().playSound(event.getPlayer(), SoundType.CHECKPOINT_ACHIEVED);
            boolean showTitle = parkour.getParkourConfig().getBoolean("DisplayTitle.Checkpoint");

            String checkpointMessage = TranslationUtils.getCourseEventMessage(session,
                    ParkourEventType.CHECKPOINT, "Event.FreeCheckpoints");

            parkour.getBountifulApi().sendSubTitle(event.getPlayer(), checkpointMessage, showTitle);
            return;
        }

        if (session.hasAchievedAllCheckpoints()) {
            return;
        }

        Location below = event.getClickedBlock().getRelative(BlockFace.DOWN).getLocation();

        if (parkour.getParkourConfig().getBoolean("OnCourse.SequentialCheckpoints")) {
            Checkpoint checkpoint = session.getNextCheckpoint();

            if (checkpoint != null
                    && checkpoint.getCheckpointX() == below.getBlockX()
                    && checkpoint.getCheckpointY() == below.getBlockY()
                    && checkpoint.getCheckpointZ() == below.getBlockZ()) {
                if (parkour.getParkourConfig().isTreatFirstCheckpointAsStart() && session.getCurrentCheckpoint() == 0) {
                    session.resetTime();
                    session.setStartTimer(true);
                    parkour.getBountifulApi().sendActionBar(event.getPlayer(),
                            TranslationUtils.getTranslation("Parkour.TimerStarted", false), true);
                }
                parkour.getPlayerManager().increaseCheckpoint(event.getPlayer(), null);
            }
        } else {
            for (int i = session.getCurrentCheckpoint(); i < session.getCourse().getCheckpoints().size(); i++) {
                Checkpoint checkpoint = session.getCourse().getCheckpoints().get(i);
                if (checkpoint.getCheckpointX() == below.getBlockX()
                        && checkpoint.getCheckpointY() == below.getBlockY()
                        && checkpoint.getCheckpointZ() == below.getBlockZ()) {
                    parkour.getPlayerManager().increaseCheckpoint(event.getPlayer(), i);
                }
            }
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

        if (!parkour.getParkourConfig().isAutoStartEnabled()) {
            return;
        }

        Block below = event.getClickedBlock().getRelative(BlockFace.DOWN);

        if (below.getType() != parkour.getParkourConfig().getAutoStartMaterial()) {
            return;
        }

        if (parkour.getParkourConfig().getBoolean("OnCourse.PreventPlateStick")) {
            event.setCancelled(true);
        }

        // Prevent a user spamming the joins
        if (!parkour.getPlayerManager().delayPlayer(event.getPlayer(), 1)) {
            return;
        }

        String courseName = parkour.getAutoStartManager().getAutoStartCourse(event.getClickedBlock().getLocation());

        if (courseName != null) {
            ParkourSession session = parkour.getPlayerManager().getParkourSession(event.getPlayer());
            if (session != null) {
                // we only want to do something if the names match
                if (session.getCourseName().equals(courseName)) {
                    session.resetProgress();
                    session.setFreedomLocation(null);

                    boolean displayTitle = parkour.getParkourConfig().getBoolean("DisplayTitle.JoinCourse");
                    parkour.getBountifulApi().sendSubTitle(event.getPlayer(),
                            TranslationUtils.getTranslation("Parkour.Restarting", false), displayTitle);
                }
            } else {
                parkour.getPlayerManager().joinCourseButDelayed(
                        event.getPlayer(), courseName, parkour.getParkourConfig().getAutoStartDelay());
            }
        }
    }

    /**
     * On pressure plate break event.
     * @param event block break event
     */
    @EventHandler
    public void onPlateBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().name().endsWith("PRESSURE_PLATE")
                && !event.getPlayer().isSneaking()
                && parkour.getAutoStartManager().doesAutoStartExist(event.getBlock().getLocation())) {
            if (!PermissionUtils.hasPermission(event.getPlayer(), Permission.ADMIN_DELETE)) {
                event.setCancelled(true);

            } else {
                Location location = event.getBlock().getLocation();
                String coordinates = location.getBlockX() + "/" + location.getBlockY() + "/" + location.getBlockZ();
                parkour.getAutoStartManager().deleteAutoStart(event.getPlayer(), coordinates);
            }
        }
    }
}

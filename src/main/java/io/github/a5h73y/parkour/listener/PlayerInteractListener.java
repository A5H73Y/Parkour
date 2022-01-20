package io.github.a5h73y.parkour.listener;

import com.cryptomorin.xseries.XBlock;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.course.ParkourEventType;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.type.question.QuestionManager;
import io.github.a5h73y.parkour.type.question.QuestionType;
import io.github.a5h73y.parkour.type.sounds.SoundType;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
        if (!parkour.getParkourSessionManager().isPlaying(event.getPlayer())) {
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

        if (parkour.getParkourSessionManager().isPlayerInTestMode(player)) {
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
                        String courseName = parkour.getParkourSessionManager().getParkourSession(player).getCourseName();
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
        if (!parkour.getParkourSessionManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)
                && !event.getAction().equals(Action.LEFT_CLICK_AIR) && !event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        ParkourMode mode = parkour.getParkourSessionManager().getParkourSession(event.getPlayer()).getParkourMode();

        if (mode != ParkourMode.FREEDOM && mode != ParkourMode.ROCKETS) {
            return;
        }

        Player player = event.getPlayer();

        if (parkour.getParkourSessionManager().isPlayerInTestMode(player)) {
            return;
        }

        event.setCancelled(true);
        Material materialInHand = MaterialUtils.getMaterialInPlayersHand(player);

        if (mode == ParkourMode.FREEDOM
                && materialInHand == parkour.getParkourConfig().getFreedomTool()) {
            if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                    || event.getAction().equals(Action.RIGHT_CLICK_AIR))
                    && player.isOnGround()
                    && parkour.getPlayerManager().delayPlayer(event.getPlayer(), parkour.getParkourConfig().getInt(
                    "ParkourTool.Freedom.SecondCooldown"))) {
                parkour.getParkourSessionManager().getParkourSession(player).setFreedomLocation(
                        parkour.getCheckpointManager().createCheckpointFromPlayerLocation(player).getLocation());
                TranslationUtils.sendTranslation("Mode.Freedom.Save", player);

            } else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)
                    || event.getAction().equals(Action.LEFT_CLICK_AIR)) {
                Location location = parkour.getParkourSessionManager().getParkourSession(player).getFreedomLocation();
                if (location == null) {
                    TranslationUtils.sendTranslation("Error.UnknownCheckpoint", player);
                    return;
                }
                PlayerUtils.teleportToLocation(player, location);
                TranslationUtils.sendTranslation("Mode.Freedom.Load", player);
            }

        } else if (mode == ParkourMode.ROCKETS
                && materialInHand == parkour.getParkourConfig().getRocketTool()) {

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
                || !parkour.getParkourSessionManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (event.getClickedBlock().getType() != parkour.getParkourConfig().getCheckpointMaterial()) {
            return;
        }

        if (parkour.getParkourConfig().getBoolean("OnCourse.PreventPlateStick")) {
            event.setCancelled(true);
        }

        ParkourSession session = parkour.getParkourSessionManager().getParkourSession(event.getPlayer());

        if (session.getCourse().getSettings().isManualCheckpoints()
                && parkour.getPlayerManager().delayPlayer(event.getPlayer(), 1)
                && (session.getFreedomLocation() == null
                || !MaterialUtils.sameBlockLocations(event.getPlayer().getLocation(), session.getFreedomLocation()))) {

            session.setFreedomLocation(event.getPlayer().getLocation());
            if (parkour.getParkourConfig().isTreatFirstCheckpointAsStart() && session.getFreedomLocation() == null) {
                session.resetTime();
                session.setStartTimer(true);
                parkour.getBountifulApi().sendActionBar(event.getPlayer(),
                        TranslationUtils.getTranslation("Parkour.TimerStarted", false));
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
                            TranslationUtils.getTranslation("Parkour.TimerStarted", false));
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
}

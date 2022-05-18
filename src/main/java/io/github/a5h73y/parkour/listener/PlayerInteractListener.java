package io.github.a5h73y.parkour.listener;

import com.cryptomorin.xseries.XBlock;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.type.question.QuestionManager;
import io.github.a5h73y.parkour.type.question.QuestionType;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TaskCooldowns;
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

        int secondsDelay = materialInHand == parkour.getParkourConfig().getRestartTool() ?
                parkour.getParkourConfig().getInt("ParkourTool.Restart.SecondCooldown") : 1;

        if (!TaskCooldowns.getInstance().delayPlayer(player, "parkourtool", secondsDelay)) {
            return;
        }

        if (materialInHand == parkour.getParkourConfig().getLastCheckpointTool()) {
            event.setCancelled(true);
            parkour.getPlayerManager().playerDie(player);

        } else if (materialInHand == parkour.getParkourConfig().getHideAllDisabledTool()
                || materialInHand == parkour.getParkourConfig().getHideAllEnabledTool()) {

            event.setCancelled(true);
            parkour.getParkourSessionManager().toggleVisibility(player);
            player.getInventory().remove(materialInHand);
            String configPath = parkour.getParkourSessionManager().hasHiddenPlayers(player)
                    ? "ParkourTool.HideAllEnabled" : "ParkourTool.HideAll";
            parkour.getPlayerManager().giveParkourTool(player, configPath, configPath);

        } else if (materialInHand == parkour.getParkourConfig().getLeaveTool()) {
            event.setCancelled(true);
            parkour.getPlayerManager().leaveCourse(player);

        } else if (materialInHand == parkour.getParkourConfig().getRestartTool()) {
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

        Player player = event.getPlayer();

        ParkourMode mode = parkour.getParkourSessionManager().getParkourSession(player).getParkourMode();

        if (mode != ParkourMode.FREEDOM && mode != ParkourMode.ROCKETS) {
            return;
        }

        if (parkour.getParkourSessionManager().isPlayerInTestMode(player)) {
            return;
        }

        event.setCancelled(true);
        Material materialInHand = MaterialUtils.getMaterialInPlayersHand(player);

        if (mode == ParkourMode.FREEDOM && materialInHand == parkour.getParkourConfig().getFreedomTool()) {
            handleFreedomTool(player, event.getAction());

        } else if (mode == ParkourMode.ROCKETS && materialInHand == parkour.getParkourConfig().getRocketTool()) {
            handleRocketTool(player);
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

        Player player = event.getPlayer();

        if (parkour.getParkourConfig().getBoolean("OnCourse.PreventPlateStick")) {
            event.setCancelled(true);

            // make sure to cooldown each event fired by 1 second
            if (!TaskCooldowns.getInstance().delayPlayer(player, "checkpoint", 1)) {
                return;
            }
        }

        ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

        if (session.getCourse().getSettings().isManualCheckpoints()) {
            setManualCheckpoint(player, event.getClickedBlock().getLocation(), session);
            return;
        }

        if (session.hasAchievedAllCheckpoints()) {
            return;
        }

        Location below = event.getClickedBlock().getRelative(BlockFace.DOWN).getLocation();
        validateAchieveCheckpoint(player, session, below);
    }

    private void validateAchieveCheckpoint(Player player, ParkourSession session, Location below) {
        for (int i = session.getCurrentCheckpoint() + 1; i < session.getCourse().getCheckpoints().size(); i++) {
            Checkpoint checkpoint = session.getCourse().getCheckpoints().get(i);

            if (checkpoint.getCheckpointX() == below.getBlockX()
                    && checkpoint.getCheckpointY() == below.getBlockY()
                    && checkpoint.getCheckpointZ() == below.getBlockZ()) {

                if (parkour.getParkourConfig().getBoolean("OnCourse.SequentialCheckpoints.Enabled")) {
                    if ((session.getCurrentCheckpoint() + 1) == i) {
                        achieveCheckpoint(player, session, i);

                    } else if (parkour.getParkourConfig().getBoolean("OnCourse.SequentialCheckpoints.AlertPlayer")) {
                        TranslationUtils.sendValueTranslation("Error.MissedCheckpoints",
                                String.valueOf(i - (session.getCurrentCheckpoint() + 1)), player);
                    }
                } else {
                    achieveCheckpoint(player, session, i);
                }
            }
        }
    }

    private void achieveCheckpoint(Player player, ParkourSession session, int desiredCheckpoint) {
        if (parkour.getParkourConfig().isTreatFirstCheckpointAsStart() && session.getCurrentCheckpoint() == 0) {
            session.resetTime();
            session.setStartTimer(true);
            parkour.getBountifulApi().sendActionBar(player,
                    TranslationUtils.getTranslation("Parkour.TimerStarted", false));
        }
        parkour.getPlayerManager().increaseCheckpoint(player, desiredCheckpoint);
    }

    private void setManualCheckpoint(Player player, Location location, ParkourSession session) {
        if (session.getFreedomLocation() == null
                || !MaterialUtils.sameBlockLocations(location, session.getFreedomLocation())) {
            location.setPitch(player.getLocation().getPitch());
            location.setYaw(player.getLocation().getYaw());
            parkour.getPlayerManager().setManualCheckpoint(player, location);
        }
    }

    private void handleFreedomTool(Player player, Action action) {
        int freedomCooldown = parkour.getParkourConfig().getInt("ParkourTool.Freedom.SecondCooldown");

        if ((action.equals(Action.RIGHT_CLICK_BLOCK)
                || action.equals(Action.RIGHT_CLICK_AIR))
                && player.isOnGround()
                && TaskCooldowns.getInstance().delayPlayer(player, "freedom", freedomCooldown)) {
            parkour.getParkourSessionManager().getParkourSession(player).setFreedomLocation(
                    parkour.getCheckpointManager().createCheckpointFromPlayerLocation(player).getLocation());
            TranslationUtils.sendTranslation("Mode.Freedom.Save", player);

        } else if (action.equals(Action.LEFT_CLICK_BLOCK)
                || action.equals(Action.LEFT_CLICK_AIR)) {
            Location location = parkour.getParkourSessionManager().getParkourSession(player).getFreedomLocation();
            if (location == null) {
                TranslationUtils.sendTranslation("Error.UnknownCheckpoint", player);
                return;
            }
            PlayerUtils.teleportToLocation(player, location);
            TranslationUtils.sendTranslation("Mode.Freedom.Load", player);
        }
    }

    private void handleRocketTool(Player player) {
        int secondDelay = parkour.getParkourConfig().getInt("ParkourModes.Rockets.SecondCooldown");
        if (TaskCooldowns.getInstance().delayPlayer(player, "reloading",
                secondDelay, "Mode.Rockets.Reloading", false) ){
            parkour.getPlayerManager().rocketLaunchPlayer(player);
        }
    }
}

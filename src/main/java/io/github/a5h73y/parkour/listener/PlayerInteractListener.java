package io.github.a5h73y.parkour.listener;

import com.cryptomorin.xseries.XBlock;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.impl.DefaultConfig;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.type.question.QuestionManager;
import io.github.a5h73y.parkour.type.question.QuestionType;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TaskCooldowns;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.EnumMap;
import java.util.function.BiConsumer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public class PlayerInteractListener extends AbstractPluginReceiver implements Listener {

    private final EnumMap<Material, ParkourToolAction> parkourTools = new EnumMap<>(Material.class);

    /**
     * Initialise the PlayerInteractListener.
     * Register each ParkourTool and the associated action.
     * @param parkour parkour instance
     */
    public PlayerInteractListener(final Parkour parkour) {
        super(parkour);
        DefaultConfig config = parkour.getParkourConfig();
        registerParkourTool(config.getLastCheckpointTool(), "LastCheckpoint",
                (player, rightClick) -> parkour.getPlayerManager().playerDie(player));

        registerParkourTool(config.getHideAllDisabledTool(), "HideAll",
                (player, rightClick) -> handleHideAllTool(player));

        registerParkourTool(config.getHideAllEnabledTool(), "HideAll",
                (player, rightClick) -> handleHideAllTool(player));

        registerParkourTool(config.getLeaveTool(), "Leave",
                (player, rightClick) -> parkour.getPlayerManager().leaveCourse(player));

        registerParkourTool(config.getRestartTool(), "Restart",
                (player, rightClick) -> handleRestartTool(player));

        registerParkourTool(config.getRocketTool(), "Rockets", true, false, ParkourMode.ROCKETS,
                (player, rightClick) -> handleRocketTool(player));

        registerParkourTool(config.getFreedomTool(), "Freedom", false, false, ParkourMode.FREEDOM,
                this::handleFreedomTool);
    }

    @EventHandler
    public void onProjectileThrownEvent(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        if (!parkour.getParkourSessionManager().isPlaying(player)) {
            return;
        }

        if (parkour.getParkourSessionManager().isPlayerInTestMode(player)) {
            return;
        }

        ParkourToolAction toolAction = getMatchingToolAction(player);

        if (toolAction == null) {
            return;
        }

        // we know they are using a valid ParkourTool - cancel any default behaviour
        event.setCancelled(true);
        handleParkourToolAction(player, toolAction, true);
    }

    /**
     * Handle Player Interaction Event.
     * Used for the Parkour Tools whilst on a Course.
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onParkourToolInteract(PlayerInteractEvent event) {
        if (!parkour.getParkourSessionManager().isPlaying(event.getPlayer())) {
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)
                && !event.getAction().equals(Action.LEFT_CLICK_AIR) && !event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }

        Player player = event.getPlayer();

        if (parkour.getParkourSessionManager().isPlayerInTestMode(player)) {
            return;
        }

        if (PluginUtils.getMinorServerVersion() > 8 && !EquipmentSlot.HAND.equals(event.getHand())) {
            return;
        }

        if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Sign) {
            return;
        }

        ParkourToolAction toolAction = getMatchingToolAction(player);

        if (toolAction == null) {
            return;
        }

        // we know they are using a valid ParkourTool - cancel any default behaviour
        event.setCancelled(true);

        if (!parkour.getParkourConfig().getBoolean("ParkourTool.RemoveRightClickRestriction")
                && (toolAction.isRightClickOnly()
                && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && !event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            return;
        }

        handleParkourToolAction(player, toolAction, event.getAction().name().startsWith("RIGHT"));
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

        if (session.getCourse().getSettings().isManualCheckpoints()
                && parkour.getParkourConfig().getBoolean("OnCourse.ManualCheckpointAnyPressurePlate")) {
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

    private void handleFreedomTool(Player player, Boolean rightClick) {
        if (rightClick && player.isOnGround()) {
            parkour.getParkourSessionManager().getParkourSession(player).setFreedomLocation(
                    parkour.getCheckpointManager().createCheckpointFromPlayerLocation(player).getLocation());
            TranslationUtils.sendTranslation("Mode.Freedom.Save", player);

        } else if (!rightClick) {
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
        ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);
        CourseConfig courseConfig = parkour.getConfigManager().getCourseConfig(session.getCourseName());
        Integer maximumRockets = courseConfig.get("MaximumRockets", null);

        if (maximumRockets != null) {
            PlayerConfig config = parkour.getConfigManager().getPlayerConfig(player);
            int rocketsUsed = config.getRocketsUsedInSession();

            if (rocketsUsed >= maximumRockets) {
                TranslationUtils.sendMessage(player, "You have run out of Rockets!");
                return;
            }

            config.increaseRocketsUsedInSession();
        }

        parkour.getPlayerManager().rocketLaunchPlayer(player);
    }

    private void handleHideAllTool(Player player) {
        parkour.getParkourSessionManager().toggleVisibility(player);
        player.getInventory().remove(MaterialUtils.getMaterialInPlayersHand(player));
        String configPath = parkour.getParkourSessionManager().hasHiddenPlayers(player)
                ? "ParkourTool.HideAllEnabled" : "ParkourTool.HideAll";
        parkour.getPlayerManager().giveParkourTool(player, configPath, configPath);
    }

    private void handleRestartTool(Player player) {
        if (parkour.getParkourConfig().getBoolean("OnRestart.RequireConfirmation")) {
            if (!parkour.getQuestionManager().hasBeenAskedQuestion(player, QuestionType.RESTART_COURSE)) {
                String courseName = parkour.getParkourSessionManager().getParkourSession(player).getCourseName();
                parkour.getQuestionManager().askRestartProgressQuestion(player, courseName);
            } else {
                parkour.getQuestionManager().answerQuestion(player, QuestionManager.YES);
            }
        } else {
            parkour.getPlayerManager().restartCourse(player);
        }
    }

    private void handleParkourToolAction(Player player, ParkourToolAction toolAction, boolean rightClick) {
        if (toolAction.isIncludeSneakCheck()
                && !player.isSneaking()
                && parkour.getParkourConfig().getBoolean("OnCourse.SneakToInteractItems")) {
            return;
        }

        int secondsDelay = parkour.getParkourConfig().get("ParkourTool." + toolAction.getActionName() + ".SecondCooldown", 1);
        String messageKey = "ParkourTool." + toolAction.getActionName() + ".Cooldown";

        if (!TaskCooldowns.getInstance().delayPlayer(player, toolAction.getActionName(), secondsDelay, messageKey, false)) {
            return;
        }

        toolAction.getPlayerConsumer().accept(player, rightClick);
    }

    @Nullable
    private ParkourToolAction getMatchingToolAction(Player player) {
        Material materialInHand = MaterialUtils.getMaterialInPlayersHand(player);

        if (XBlock.isAir(materialInHand)) {
            return null;
        }

        ParkourToolAction toolAction = parkourTools.get(materialInHand);

        if (toolAction == null) {
            return null;
        }

        ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

        if (toolAction.getRequiredParkourMode() != null
                && toolAction.getRequiredParkourMode() != session.getParkourMode()) {
            return null;
        }
        return toolAction;
    }

    private void registerParkourTool(Material material,
                                     String toolName,
                                     boolean rightClickOnly,
                                     boolean includeSneakCheck,
                                     ParkourMode requiredParkourMode,
                                     BiConsumer<Player, Boolean> playerConsumer) {
        if (material != null && material != Material.AIR) {
            parkourTools.put(material,
                    new ParkourToolAction(toolName, requiredParkourMode, rightClickOnly, includeSneakCheck, playerConsumer));
        }
    }

    private void registerParkourTool(Material material, String toolName, BiConsumer<Player, Boolean> playerConsumer) {
        registerParkourTool(material, toolName, true, true, null, playerConsumer);
    }

    public static class ParkourToolAction {

        private final String actionName;

        private final ParkourMode requiredParkourMode;

        private final boolean rightClickOnly;

        private final boolean includeSneakCheck;

        private final BiConsumer<Player, Boolean> playerConsumer;

        public ParkourToolAction(String actionName,
                                 ParkourMode requiredParkourMode,
                                 boolean rightClickOnly,
                                 boolean includeSneakCheck,
                                 BiConsumer<Player, Boolean> playerConsumer) {
            this.actionName = actionName;
            this.requiredParkourMode = requiredParkourMode;
            this.rightClickOnly = rightClickOnly;
            this.includeSneakCheck = includeSneakCheck;
            this.playerConsumer = playerConsumer;
        }

        public String getActionName() {
            return actionName;
        }

        public ParkourMode getRequiredParkourMode() {
            return requiredParkourMode;
        }

        public boolean isRightClickOnly() {
            return rightClickOnly;
        }

        public boolean isIncludeSneakCheck() {
            return includeSneakCheck;
        }

        public BiConsumer<Player, Boolean> getPlayerConsumer() {
            return playerConsumer;
        }
    }
}

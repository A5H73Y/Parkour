package io.github.a5h73y.parkour.type.checkpoint;

import static io.github.a5h73y.parkour.other.ParkourConstants.CHECKPOINT_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.COURSE_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;

import com.cryptomorin.xseries.XBlock;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.course.Course;
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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Checkpoint Manager.
 * No need for cache as they are part of a Course's cache.
 */
public class CheckpointManager extends AbstractPluginReceiver {

    public CheckpointManager(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Create (or overwrite) a checkpoint.
     * If valid numeric argument is supplied, will attempt to overwrite the existing checkpoint.
     * Otherwise, a new checkpoint will be generated.
     * The block on which the checkpoint is created must be able to have a pressure plate
     * placed on it (if configured).
     *
     * @param player requesting player
     * @param checkpoint optional checkpoint number to override
     */
    public void createCheckpoint(Player player, String courseName, @Nullable Integer checkpoint) {
        if (!canCreateCheckpoint(player, courseName, checkpoint)) {
            return;
        }
        // the checkpoint number to overwrite / create
        checkpoint = checkpoint != null ? checkpoint
                : parkour.getConfigManager().getCourseConfig(courseName).getCheckpointAmount() + 1;
        Location location = player.getLocation();
        Block block = location.getBlock();

        if (parkour.getParkourConfig().isEnforceSafeCheckpoints()) {
            try {
                // attempt to check if the player is able to create the checkpoint at their current location.
                if (!MaterialUtils.isCheckpointSafe(player, block)) {
                    return;
                }
            } catch (NoSuchFieldError ex) {
                // using an older version of server - disable the option to stop error appearing
                PluginUtils.log("Safe Checkpoints has been disabled due to old server", 2);
                parkour.getParkourConfig().set("Other.EnforceSafeCheckpoints", false);
                parkour.getConfigManager().reloadConfigs();
            }
        }

        Block blockUnder = block.getRelative(BlockFace.DOWN);

        // if they are floating in air, place stone below them
        if (XBlock.isAir(blockUnder.getType())) {
            blockUnder.setType(Material.STONE);
        }

        block.setType(parkour.getParkourConfig().getCheckpointMaterial());

        parkour.getConfigManager().getCourseConfig(courseName).createCheckpointData(location, checkpoint);
        parkour.getCourseManager().clearCache(courseName);
        player.sendMessage(TranslationUtils.getTranslation("Parkour.CheckpointCreated")
                .replace(CHECKPOINT_PLACEHOLDER, String.valueOf(checkpoint))
                .replace(COURSE_PLACEHOLDER, courseName));
    }

    /**
     * Create a Checkpoint from the Player's current Location.
     * This can be used to mark a temporary Checkpoint that isn't persisted.
     *
     * @param player requesting player
     * @return created Checkpoint
     */
    public Checkpoint createCheckpointFromPlayerLocation(Player player) {
        return new Checkpoint(player.getLocation(), 0, 0, 0);
    }

    /**
     * Teleport Player to a checkpoint.
     * If a checkpoint number is provided that will be the checkpoint loaded.
     * Otherwise, the Player will be teleported to the course start (checkpoint 0).
     *
     * @param player requesting player
     * @param courseName the desired course
     * @param checkpoint optional checkpoint number
     */
    public void teleportCheckpoint(Player player, String courseName, @Nullable Integer checkpoint) {
        if (!parkour.getCourseManager().doesCourseExist(courseName)) {
            TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, player);
            return;
        }

        Course course = parkour.getCourseManager().findByName(courseName);
        checkpoint = checkpoint == null ? 0 : checkpoint;

        if (course.getNumberOfCheckpoints() < checkpoint) {
            TranslationUtils.sendTranslation("Error.UnknownCheckpoint", player);
            return;
        }

        PlayerUtils.teleportToLocation(player, course.getCheckpoints().get(checkpoint).getLocation());
        String message = TranslationUtils.getValueTranslation("Parkour.Teleport", courseName);
        TranslationUtils.sendMessage(player, checkpoint > 0 ? message + " &f(&3" + checkpoint + "&f)" : message, false);
    }

    /**
     * Delete a Checkpoint from the Course.
     * This will only delete the highest Checkpoint, decreasing the amount of Checkpoints.
     *
     * @param commandSender command sender
     * @param courseName the desired course
     */
    public void deleteCheckpoint(CommandSender commandSender, String courseName) {
        if (!parkour.getCourseManager().doesCourseExist(courseName)) {
            return;
        }

        Integer checkpoint = parkour.getConfigManager().getCourseConfig(courseName).getCheckpointAmount();

        if (checkpoint <= 0) {
            TranslationUtils.sendMessage(commandSender, courseName + " has no Checkpoints!");
            return;
        }

        parkour.getConfigManager().getCourseConfig(courseName).deleteCheckpoint();
        parkour.getCourseManager().clearCache(courseName);

        commandSender.sendMessage(TranslationUtils.getTranslation("Parkour.DeleteCheckpoint")
                .replace(CHECKPOINT_PLACEHOLDER, String.valueOf(checkpoint))
                .replace(COURSE_PLACEHOLDER, courseName));

        PluginUtils.logToFile("Checkpoint " + checkpoint + " was deleted on " + courseName + " by " + commandSender.getName());
    }

    /**
     * Validate Player creating a Course Checkpoint.
     *
     * @param player player
     * @param checkpoint checkpoint
     * @return player is able to create checkpoint
     */
    public boolean canCreateCheckpoint(Player player, String courseName, @Nullable Integer checkpoint) {
        if (!PermissionUtils.hasPermissionOrCourseOwnership(player, Permission.ADMIN_COURSE, courseName)) {
            return false;
        }

        int checkpoints = parkour.getConfigManager().getCourseConfig(courseName).getCheckpointAmount() + 1;

        if (checkpoint != null) {
            if (checkpoint < 1) {
                TranslationUtils.sendMessage(player, "Checkpoint specified is not valid!");
                return false;
            }
            if (checkpoints < checkpoint) {
                TranslationUtils.sendMessage(player, "This checkpoint does not exist! &4Creation cancelled.");
                return false;
            }

            checkpoints = checkpoint;
        }

        if (checkpoints < 1) {
            TranslationUtils.sendMessage(player, "Invalid checkpoint number.");
            return false;
        }
        return true;
    }
}

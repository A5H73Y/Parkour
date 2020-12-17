package io.github.a5h73y.parkour.type.checkpoint;

import com.cryptomorin.xseries.XBlock;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Constants;
import io.github.a5h73y.parkour.other.ParkourValidation;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Checkpoint Manager.
 * No need for cache as they are part of a course's cache.
 */
public class CheckpointManager extends AbstractPluginReceiver {

    public CheckpointManager(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Create (or overwrite) a checkpoint.
     * If valid numeric argument is supplied, will attempt to overwrite the existing checkpoint.
     * Otherwise a new checkpoint will be generated.
     * The block on which the checkpoint is created must be able to have a pressure plate
     * placed on it (if configured).
     *
     * @param player requesting player
     * @param checkpoint optional checkpoint number to override
     */
    public void createCheckpoint(Player player, @Nullable Integer checkpoint) {
        if (!ParkourValidation.canCreateCheckpoint(player, checkpoint)) {
            return;
        }

        String selectedCourse = PlayerInfo.getSelectedCourse(player);
        // the checkpoint number to overwrite / create
        checkpoint = checkpoint != null ? checkpoint : CourseInfo.getCheckpointAmount(selectedCourse) + 1;
        Location location = player.getLocation();
        Block block = location.getBlock();
        Block blockUnder = block.getRelative(BlockFace.DOWN);

        if (parkour.getConfig().isEnforceSafeCheckpoints()) {
            try {
                // attempt to check if the player is able to create the checkpoint at their current location.
                if (!MaterialUtils.isCheckpointSafe(player, block)) {
                    return;
                }
            } catch (NoSuchFieldError ex) {
                // using an older version of server - disable the option to stop error appearing
                PluginUtils.log("Safe Checkpoints has been disabled due to old server", 2);
                parkour.getConfig().set("Other.EnforceSafeCheckpoints", false);
                parkour.getConfig().save();

                parkour.getConfigManager().reloadConfigs();
            }
        }

        // if they are floating in air, place stone below them
        if (XBlock.isAir(blockUnder.getType())) {
            blockUnder.setType(Material.STONE);
        }

        block.setType(parkour.getConfig().getCheckpointMaterial());

        createCheckpointData(selectedCourse, location, checkpoint);
        parkour.getCourseManager().clearCache(selectedCourse);
        player.sendMessage(TranslationUtils.getTranslation("Parkour.CheckpointCreated")
                .replace(Constants.CHECKPOINT_PLACEHOLDER, String.valueOf(checkpoint))
                .replace(Constants.COURSE_PLACEHOLDER, selectedCourse));
    }

    /**
     * Create and persist Checkpoint data.
     * The Location will be used to store the location to teleport to, and to mark the position of the pressure plate.
     *
     * @param courseName player's selected course
     * @param location checkpoint location
     * @param checkpoint checkpoint being saved
     */
    public void createCheckpointData(String courseName, Location location, int checkpoint) {
        ParkourConfiguration checkpointConfig = Parkour.getConfig(ConfigType.CHECKPOINTS);
        courseName = courseName.toLowerCase();

        int points = checkpointConfig.getInt(courseName + ".Checkpoints");
        int maximumCheckpoints = Math.max(points, checkpoint);

        checkpointConfig.set(courseName + ".Checkpoints", maximumCheckpoints);
        checkpointConfig.set(courseName + "." + checkpoint + ".X", location.getBlockX() + 0.5);
        checkpointConfig.set(courseName + "." + checkpoint + ".Y", location.getBlockY() + 0.5);
        checkpointConfig.set(courseName + "." + checkpoint + ".Z", location.getBlockZ() + 0.5);
        checkpointConfig.set(courseName + "." + checkpoint + ".Yaw", location.getYaw());
        checkpointConfig.set(courseName + "." + checkpoint + ".Pitch", location.getPitch());

        checkpointConfig.set(courseName + "." + checkpoint + ".PlateX", location.getBlockX());
        checkpointConfig.set(courseName + "." + checkpoint + ".PlateY", location.getBlockY() - 1);
        checkpointConfig.set(courseName + "." + checkpoint + ".PlateZ", location.getBlockZ());

        checkpointConfig.save();
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
     * Find the Checkpoints for a Course.
     *
     * @param courseName requested course
     * @return matching checkpoints
     */
    public List<Checkpoint> getCheckpoints(String courseName) {
        courseName = courseName.toLowerCase();

        List<Checkpoint> checkpoints = new ArrayList<>();
        ParkourConfiguration checkpointsConfig = Parkour.getConfig(ConfigType.CHECKPOINTS);
        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);

        int points = checkpointsConfig.getInt(courseName + ".Checkpoints") + 1;
        World world = Bukkit.getWorld(courseConfig.getString(courseName + ".World"));

        if (world == null) {
            return checkpoints;
        }

        for (int i = 0; i < points; i++) {
            String checkpointPath = courseName + "." + i + ".";

            // the 'current' checkpoint location, i.e. where to teleport back to
            double x = checkpointsConfig.getDouble(checkpointPath + "X");
            double y = checkpointsConfig.getDouble(checkpointPath + "Y");
            double z = checkpointsConfig.getDouble(checkpointPath + "Z");
            float yaw = (float) checkpointsConfig.getDouble(checkpointPath + "Yaw");
            float pitch = (float) checkpointsConfig.getDouble(checkpointPath + "Pitch");

            Location location = new Location(world, x, y, z, yaw, pitch);

            // get the next checkpoint pressure plate location
            checkpointPath = courseName + "." + (i + 1) + ".";

            double plateX = checkpointsConfig.getDouble(checkpointPath + "PlateX");
            double plateY = checkpointsConfig.getDouble(checkpointPath + "PlateY");
            double plateZ = checkpointsConfig.getDouble(checkpointPath + "PlateZ");

            checkpoints.add(new Checkpoint(location, plateX, plateY, plateZ));
        }

        return checkpoints;
    }

    /**
     * Teleport Player to a checkpoint.
     * If a checkpoint number is provided that will be the checkpoint loaded.
     * Otherwise the Player will teleported to the course start (checkpoint 0).
     *
     * @param player requesting player
     * @param courseName the desired course
     * @param checkpoint optional checkpoint number
     */
    public void teleportCheckpoint(Player player, String courseName, @Nullable Integer checkpoint) {
        if (!parkour.getCourseManager().doesCourseExists(courseName)) {
            TranslationUtils.sendValueTranslation("Error.NoExist", courseName, player);
            return;
        }

        ParkourConfiguration checkpointsConfig = Parkour.getConfig(ConfigType.CHECKPOINTS);
        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);

        courseName = courseName.toLowerCase();
        // if a checkpoint is specified, or default to 0 (start)
        String path = checkpoint == null ? courseName + ".0" : courseName + "." + checkpoint;

        World world = Bukkit.getWorld(courseConfig.getString(courseName + ".World"));
        double x = checkpointsConfig.getDouble(path + ".X");
        double y = checkpointsConfig.getDouble(path + ".Y");
        double z = checkpointsConfig.getDouble(path + ".Z");
        float yaw = checkpointsConfig.getInt(path + ".Yaw");
        float pitch = checkpointsConfig.getInt(path + ".Pitch");

        if (x == 0 && y == 0 && z == 0) {
            TranslationUtils.sendTranslation("Error.UnknownCheckpoint", player);
            return;
        }

        player.teleport(new Location(world, x, y, z, yaw, pitch));
        String message = TranslationUtils.getValueTranslation("Parkour.Teleport", courseName);
        TranslationUtils.sendMessage(player, checkpoint != null ? message + " &f(&3" + checkpoint + "&f)" : message, false);
    }

    /**
     * Delete a Checkpoint from the Course.
     * This will only delete the highest Checkpoint, decreasing the amount of Checkpoints.
     *
     * @param sender requesting sender
     * @param courseName the desired course
     */
    public void deleteCheckpoint(CommandSender sender, String courseName) {
        if (!parkour.getCourseManager().doesCourseExists(courseName)) {
            return;
        }

        courseName = courseName.toLowerCase();
        int point = CourseInfo.getCheckpointAmount(courseName);

        if (point <= 0) {
            TranslationUtils.sendMessage(sender, courseName + " has no Checkpoints!");
            return;
        }

        ParkourConfiguration checkpointsConfig = Parkour.getConfig(ConfigType.CHECKPOINTS);

        checkpointsConfig.set(courseName + "." + point, null);
        checkpointsConfig.set(courseName + ".Checkpoints", point - 1);
        checkpointsConfig.save();
        parkour.getCourseManager().clearCache(courseName);

        sender.sendMessage(TranslationUtils.getTranslation("Parkour.DeleteCheckpoint")
                .replace(Constants.CHECKPOINT_PLACEHOLDER, String.valueOf(point))
                .replace(Constants.COURSE_PLACEHOLDER, courseName));

        PluginUtils.logToFile("Checkpoint " + point + " was deleted on " + courseName + " by " + sender.getName());
    }

    /**
     * Delete the Course checkpoint data.
     * When the course is deleted, delete the checkpoint data for the course.
     *
     * @param courseName course checkpoint to delete
     */
    public void deleteCheckpointData(String courseName) {
        ParkourConfiguration checkpointConfig = Parkour.getConfig(ConfigType.CHECKPOINTS);
        checkpointConfig.set(courseName, null);
        checkpointConfig.save();
    }
}

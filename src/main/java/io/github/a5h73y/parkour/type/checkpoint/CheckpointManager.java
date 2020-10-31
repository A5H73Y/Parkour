package io.github.a5h73y.parkour.type.checkpoint;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.ArrayList;
import java.util.List;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
     * @param args
     * @param player
     */
    public void createCheckpoint(Player player, String[] args) {
        if (!Validation.createCheckpoint(player, args)) {
            return;
        }

        String selectedCourse = PlayerInfo.getSelectedCourse(player);
        Location location = player.getLocation();
        Block block = location.getBlock();
        Block blockUnder = block.getRelative(BlockFace.DOWN);

        // the checkpoint number to overwrite / create
        int checkpoint = args.length == 2 ? Integer.parseInt(args[1]) :
                CourseInfo.getCheckpointAmount(selectedCourse) + 1;

        if (parkour.getConfig().isEnforceSafeCheckpoints()) {
            try {
                // attempt to check if the player is able to create the checkpoint
                // on their current location.
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
        if (blockUnder.getType().equals(Material.AIR)
                || blockUnder.getType().equals(XMaterial.CAVE_AIR.parseMaterial())) {
            blockUnder.setType(Material.STONE);
        }

        Material pressurePlate = MaterialUtils.lookupMaterial(parkour.getConfig().getCheckpointMaterial());
        block.setType(pressurePlate);

        createCheckpointData(selectedCourse, location, checkpoint);
        parkour.getCourseManager().clearCache(selectedCourse);
        player.sendMessage(Parkour.getPrefix() + "Checkpoint " + ChatColor.DARK_AQUA + checkpoint + ChatColor.WHITE + " set on " + ChatColor.AQUA + selectedCourse);
    }

    /**
     * Create and save the checkpoint data.
     * The location for the player to teleport to and the location for the pressure plate will be created.
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
     * Delete the course checkpoint data.
     * When the course is deleted, delete the checkpoint data for the course.
     *
     * @param courseName
     */
    public void deleteCheckpointData(String courseName) {
        ParkourConfiguration checkpointConfig = Parkour.getConfig(ConfigType.CHECKPOINTS);
        checkpointConfig.set(courseName, null);
        checkpointConfig.save();
    }

    /**
     * Teleport player to a checkpoint.
     * If the checkpoint flag is false, it will teleport the player to the start.
     * Otherwise the player will teleport to the chosen checkpoint.
     *
     * @param args
     * @param player
     * @param toCheckpoint
     */
    public void teleportCheckpoint(Player player, String courseName, @Nullable String checkpoint) {
        if (!parkour.getCourseManager().courseExists(courseName)) {
            TranslationUtils.sendValueTranslation("Error.NoExist", courseName, player);
            return;
        }

        ParkourConfiguration checkpointsConfig = Parkour.getConfig(ConfigType.CHECKPOINTS);
        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);

        courseName = courseName.toLowerCase();
        // if a checkpoint is specified, or default to 0 (start)
        String path = checkpoint == null ? courseName + ".0" : courseName + "." + checkpoint;

        World world = Bukkit.getWorld(courseConfig.getString(courseName + "." + "World"));
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
        player.sendMessage(checkpoint != null ? message + StringUtils.colour(" &f(&3" + checkpoint + "&f)") : message);
    }

    /**
     * Delete a checkpoint from the course.
     * This will only delete the last checkpoint, decreasing the amount of checkpoints.
     *
     * @param courseName
     * @param player
     */
    public void deleteCheckpoint(Player player, String courseName) {
        if (!parkour.getCourseManager().courseExists(courseName)) {
            return;
        }

        courseName = courseName.toLowerCase();
        int point = CourseInfo.getCheckpointAmount(courseName);

        if (point <= 0) {
            player.sendMessage(Parkour.getPrefix() + courseName + " has no checkpoints!");
            return;
        }

        ParkourConfiguration checkpointsConfig = Parkour.getConfig(ConfigType.CHECKPOINTS);

        checkpointsConfig.set(courseName + "." + point, null);
        checkpointsConfig.set(courseName + ".Checkpoints", point - 1);
        checkpointsConfig.save();
        parkour.getCourseManager().clearCache(courseName);

        player.sendMessage(TranslationUtils.getTranslation("Parkour.DeleteCheckpoint")
                .replace("%CHECKPOINT%", String.valueOf(point))
                .replace("%COURSE%", courseName));

        PluginUtils.logToFile("Checkpoint " + point + " was deleted on " + courseName + " by " + player.getName());
    }

    /**
     * Create a new checkpoint based on the players current location.
     *
     * @param player
     * @return checkpoint
     */
    public Checkpoint createCheckpointFromPlayerLocation(Player player) {
        return new Checkpoint(player.getLocation(), 0, 0, 0);
    }

    public List<Checkpoint> getCheckpoints(String courseName) {
        courseName = courseName.toLowerCase();

        List<Checkpoint> checkpoints = new ArrayList<>();
        ParkourConfiguration checkpointsConfig = Parkour.getConfig(ConfigType.CHECKPOINTS);
        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);

        int points = checkpointsConfig.getInt(courseName + ".Checkpoints") + 1;
        World world = Bukkit.getWorld(courseConfig.getString(courseName + "." + "World"));

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
}

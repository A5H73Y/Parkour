package io.github.a5h73y.parkour.course;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.config.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.player.PlayerInfo;
import io.github.a5h73y.parkour.utilities.Static;
import io.github.a5h73y.parkour.utilities.Utils;
import io.github.a5h73y.parkour.utilities.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class CheckpointMethods {

    /**
     * Retrieval of next Checkpoint.
     * Each Checkpoint has a Location to teleport back to and an X,Y,Z coordinate for pressureplate detection.
     * NOTE: Checkpoint 0 will be made, this is JUST for the joining of the course, the X,Y,Z will be 0.
     *
     * @param courseName   the course
     * @param currentPoint the current checkpoint number
     * @return Checkpoint
     */
    public static Checkpoint getNextCheckpoint(String courseName, int currentPoint) {
        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);
        ParkourConfiguration checkConfig = Parkour.getConfig(ConfigType.CHECKPOINTS);

        String checkpointPath = courseName + "." + currentPoint + ".";

        // the 'current' checkpoint location, i.e. where to teleport back to
        double x = courseConfig.getDouble(checkpointPath + "X");
        double y = courseConfig.getDouble(checkpointPath + "Y");
        double z = courseConfig.getDouble(checkpointPath + "Z");
        float yaw = (float) courseConfig.getDouble(checkpointPath + "Yaw");
        float pitch = (float) courseConfig.getDouble(checkpointPath + "Pitch");
        World world = Bukkit.getWorld(courseConfig.getString(courseName + "." + "World"));
        Location location = new Location(world, x, y, z, yaw, pitch);

        // get the next checkpoint pressure plate location
        checkpointPath = courseName + "." + (currentPoint + 1) + ".";

        double nCheckX = checkConfig.getDouble(checkpointPath + "X");
        double nCheckY = checkConfig.getDouble(checkpointPath + "Y");
        double nCheckZ = checkConfig.getDouble(checkpointPath + "Z");

        return new Checkpoint(location, nCheckX, nCheckY, nCheckZ);
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
    public static void createCheckpoint(String[] args, Player player) {
        if (!Validation.createCheckpoint(args, player)) {
            return;
        }

        String selected = PlayerInfo.getSelected(player);
        Location location = player.getLocation();
        Block block = location.getBlock();
        Block blockUnder = block.getRelative(BlockFace.DOWN);

        // the checkpoint number to overwrite / create
        int checkpoint = args.length == 2 ? Integer.parseInt(args[1]) :
                CourseInfo.getCheckpointAmount(selected) + 1;

        if (Parkour.getSettings().isEnforceSafeCheckpoints()) {
            try {
                // attempt to check if the player is able to create the checkpoint
                // on their current location.
                if (!Utils.isCheckpointSafe(player, block)) {
                    return;
                }
            } catch (NoSuchFieldError ex) {
                // using an older version of server - disable the option to stop error appearing
                Utils.log("Safe Checkpoints has been disabled due to old server", 2);
                Parkour.getInstance().getConfig().set("Other.EnforceSafeCheckpoints", false);
                Parkour.getInstance().saveConfig();

                Parkour.getInstance().reloadConfigurations();
            }
        }

        if (blockUnder.getType().equals(Material.AIR) || blockUnder.getType().equals(XMaterial.CAVE_AIR.parseMaterial())) {
            blockUnder.setType(Material.STONE);
        }

        Material pressurePlate = XMaterial.fromString(Parkour.getSettings().getCheckpointMaterial()).parseMaterial();
        block.setType(pressurePlate);

        createCheckpointData(selected, location, checkpoint);
        player.sendMessage(Static.getParkourString() + "Checkpoint " + ChatColor.DARK_AQUA + checkpoint + ChatColor.WHITE + " set on " + ChatColor.AQUA + selected);
    }

    /**
     * Create and save the checkpoint data.
     * The location for the player to teleport to and the location for the pressure plate will be created.
     *
     * @param selected   player's selected course
     * @param location   checkpoint location
     * @param checkpoint checkpoint being saved
     */
    private static void createCheckpointData(String selected, Location location, int checkpoint) {
        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);
        ParkourConfiguration checkConfig = Parkour.getConfig(ConfigType.CHECKPOINTS);

        int points = courseConfig.getInt(selected + ".Points");
        int pointmax = Math.max(points, checkpoint);

        courseConfig.set(selected + ".Points", pointmax);
        courseConfig.set(selected + "." + checkpoint + ".X", location.getBlockX() + 0.5);
        courseConfig.set(selected + "." + checkpoint + ".Y", location.getBlockY() + 0.5);
        courseConfig.set(selected + "." + checkpoint + ".Z", location.getBlockZ() + 0.5);
        courseConfig.set(selected + "." + checkpoint + ".Yaw", location.getYaw());
        courseConfig.set(selected + "." + checkpoint + ".Pitch", location.getPitch());

        checkConfig.set(selected + "." + checkpoint + ".X", location.getBlockX());
        checkConfig.set(selected + "." + checkpoint + ".Y", location.getBlockY() - 1);
        checkConfig.set(selected + "." + checkpoint + ".Z", location.getBlockZ());

        courseConfig.save();
        checkConfig.save();
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
    public static void teleportCheckpoint(String[] args, Player player, boolean toCheckpoint) {
        if (!CourseMethods.exist(args[1])) {
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
            return;
        }

        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);
        String courseName = args[1].toLowerCase();
        String path = toCheckpoint ? courseName + "." + args[2] : courseName + ".0";

        World world = Bukkit.getWorld(courseConfig.getString(courseName + ".World"));
        double x = courseConfig.getDouble(path + ".X");
        double y = courseConfig.getDouble(path + ".Y");
        double z = courseConfig.getDouble(path + ".Z");
        float yaw = courseConfig.getInt(path + ".Yaw");
        float pitch = courseConfig.getInt(path + ".Pitch");

        if (x == 0 && y == 0 && z == 0) {
            player.sendMessage(Static.getParkourString() + ChatColor.RED + "ERROR: " + ChatColor.WHITE + "This checkpoint is invalid or doesn't exist!");
            return;
        }

        player.teleport(new Location(world, x, y, z, yaw, pitch));
        String message = Utils.getTranslation("Parkour.Teleport").replace("%COURSE%", args[1]);
        player.sendMessage(toCheckpoint ? message + Utils.colour(" &f(&3" + args[2] + "&f)") : message);
    }

    /**
     * Delete a checkpoint from the course.
     * This will only delete the last checkpoint, decreasing the amount of checkpoints.
     *
     * @param courseName
     * @param player
     */
    public static void deleteCheckpoint(String courseName, Player player) {
        if (!CourseMethods.exist(courseName)) {
            return;
        }

        courseName = courseName.toLowerCase();
        int point = CourseInfo.getCheckpointAmount(courseName);
        if (point <= 0) {
            player.sendMessage(Static.getParkourString() + courseName + " has no checkpoints!");
            return;
        }

        ParkourConfiguration courseConfig = Parkour.getConfig(ConfigType.COURSES);
        ParkourConfiguration checkConfig = Parkour.getConfig(ConfigType.COURSES);

        courseConfig.set(courseName + "." + point, null);
        courseConfig.set(courseName + ".Points", point - 1);
        checkConfig.set(courseName + "." + point, null);

        courseConfig.save();
        checkConfig.save();

        player.sendMessage(Utils.getTranslation("Parkour.DeleteCheckpoint")
                .replace("%CHECKPOINT%", String.valueOf(point))
                .replace("%COURSE%", courseName));

        Utils.logToFile("Checkpoint " + point + " was deleted on " + courseName + " by " + player.getName());
    }

    /**
     * Create a new checkpoint based on the players current location.
     *
     * @param player
     * @return checkpoint
     */
    public static Checkpoint createCheckpointFromPlayerLocation(Player player) {
        return new Checkpoint(player.getLocation(), 0, 0, 0);
    }
}

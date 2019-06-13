package me.A5H73Y.parkour.course;

import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.other.Validation;
import me.A5H73Y.parkour.player.PlayerInfo;
import me.A5H73Y.parkour.utilities.Static;
import me.A5H73Y.parkour.utilities.Utils;
import me.A5H73Y.parkour.utilities.XMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CheckpointMethods {

	/**
	 * Retrieval of next Checkpoint. 
	 * Each Checkpoint has a Location to teleport back to and an X,Y,Z coordinate for pressureplate detection.
	 * NOTE: Checkpoint 0 will be made, this is JUST for the joining of the course, the X,Y,Z will be 0.
	 * @param courseName the course
	 * @param currentPoint the current checkpoint number
	 * @return Checkpoint
	 */
	public static Checkpoint getNextCheckpoint(String courseName, int currentPoint) {
		FileConfiguration courseData = Parkour.getParkourConfig().getCourseData();
		FileConfiguration checkData = Parkour.getParkourConfig().getCheckData();

		String checkpointPath = courseName + "." + currentPoint + ".";

		// the 'current' checkpoint location, i.e. where to teleport back to
		double x = courseData.getDouble(checkpointPath + "X");
		double y = courseData.getDouble(checkpointPath + "Y");
		double z = courseData.getDouble(checkpointPath + "Z");
		float yaw = (float) courseData.getDouble(checkpointPath + "Yaw");
		float pitch = (float) courseData.getDouble(checkpointPath + "Pitch");
		World world = Bukkit.getWorld(courseData.getString(courseName + "." + "World"));
		Location location = new Location(world, x, y, z, yaw, pitch);

		// get the next checkpoint pressure plate location
		checkpointPath = courseName + "." + (currentPoint + 1) + ".";

		double nCheckX = checkData.getDouble(checkpointPath + "X");
		double nCheckY = checkData.getDouble(checkpointPath + "Y");
		double nCheckZ = checkData.getDouble(checkpointPath + "Z");

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
				Parkour.getPlugin().getConfig().set("Other.EnforceSafeCheckpoints", false);
				Parkour.getPlugin().saveConfig();

				Utils.reloadConfig();
			}
		}

		if (blockUnder.getType().equals(Material.AIR)) {
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
	 * @param selected player's selected course
	 * @param location checkpoint location
	 * @param checkpoint checkpoint being saved
	 */
	private static void createCheckpointData(String selected, Location location, int checkpoint) {
		FileConfiguration courseData = Parkour.getParkourConfig().getCourseData();
		FileConfiguration checkData = Parkour.getParkourConfig().getCheckData();

		int points = courseData.getInt(selected + ".Points");
		int pointmax = points >= checkpoint ? points : checkpoint;

		courseData.set(selected + ".Points", pointmax);
		courseData.set(selected + "." + checkpoint + ".X", location.getBlockX() + 0.5);
		courseData.set(selected + "." + checkpoint + ".Y", location.getBlockY() + 0.5);
		courseData.set(selected + "." + checkpoint + ".Z", location.getBlockZ() + 0.5);
		courseData.set(selected + "." + checkpoint + ".Yaw", location.getYaw());
		courseData.set(selected + "." + checkpoint + ".Pitch", location.getPitch());

		checkData.set(selected + "." + checkpoint + ".X", location.getBlockX());
		checkData.set(selected + "." + checkpoint + ".Y", location.getBlockY() - 1);
		checkData.set(selected + "." + checkpoint + ".Z", location.getBlockZ());

		Parkour.getParkourConfig().saveCheck();
		Parkour.getParkourConfig().saveCourses();
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

		FileConfiguration courseData = Parkour.getParkourConfig().getCourseData();
		String courseName = args[1].toLowerCase();
		String path = toCheckpoint ? courseName + "." + args[2] : courseName + ".0";

		World world = Bukkit.getWorld(courseData.getString(courseName + ".World"));
		double x = courseData.getDouble(path + ".X");
		double y = courseData.getDouble(path + ".Y");
		double z = courseData.getDouble(path + ".Z");
		float yaw = courseData.getInt(path + ".Yaw");
		float pitch = courseData.getInt(path + ".Pitch");

		if (x == 0 && y == 0 && z == 0) {
			player.sendMessage(Static.getParkourString() + ChatColor.RED + "ERROR: " + ChatColor.WHITE+ "This checkpoint is invalid or doesn't exist!");
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

		Parkour.getParkourConfig().getCourseData().set(courseName + "." + point, null);
		Parkour.getParkourConfig().getCourseData().set(courseName + ".Points", point - 1);
		Parkour.getParkourConfig().getCheckData().set(courseName + "." + point, null);
		Parkour.getParkourConfig().saveCourses();
		Parkour.getParkourConfig().saveCheck();

		player.sendMessage(Utils.getTranslation("Parkour.DeleteCheckpoint")
				.replace("%CHECKPOINT%", String.valueOf(point))
				.replace("%COURSE%", courseName));

		Utils.logToFile("Checkpoint " + point + " was deleted on " + courseName + " by " + player.getName());
	}

	/**
	 * Create a new checkpoint based on the players current location.
	 * @param player
	 * @return checkpoint
	 */
	public static Checkpoint createCheckpointFromPlayerLocation(Player player) {
		return new Checkpoint(player.getLocation(),0, 0, 0);
	}
}

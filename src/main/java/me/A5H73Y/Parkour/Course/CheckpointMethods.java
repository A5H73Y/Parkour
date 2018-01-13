package me.A5H73Y.Parkour.Course;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Other.ValidationMethods;
import me.A5H73Y.Parkour.Player.PlayerInfo;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class CheckpointMethods {

	/**
	 * Retrieval of next Checkpoint. 
	 * Each Checkpoint has a Location to teleport back to and an X,Y,Z coordinate for pressureplate detection.
	 * NOTE: Checkpoint 0 will be made, this is JUST for the joining of the course, the X,Y,Z will be 0.
	 * @param courseName
	 * @return Checkpoint
	 */
	public static Checkpoint getNextCheckpoint(String courseName, int currentPoint){
		FileConfiguration courseData = Parkour.getParkourConfig().getCourseData();
		FileConfiguration checkData = Parkour.getParkourConfig().getCheckData();

		String path = courseName + "." + currentPoint + ".";

		double x = courseData.getDouble(path + "X");
		double y = courseData.getDouble(path + "Y");
		double z = courseData.getDouble(path + "Z");
		float yaw = (float) courseData.getDouble(path + "Yaw");
		float pitch = (float) courseData.getDouble(path + "Pitch");
		World world = Bukkit.getWorld(courseData.getString(courseName + "." + "World"));
		Location location = new Location(world, x, y, z, yaw, pitch);

		path = courseName + "." + (currentPoint + 1) + ".";

		double nCheckX = checkData.getDouble(path + "X");
		double nCheckY = checkData.getDouble(path + "Y");
		double nCheckZ = checkData.getDouble(path + "Z");

		return new Checkpoint(location, nCheckX, nCheckY, nCheckZ);
	}

	/**
	 * Create a checkpoint
	 * If valid numeric argument is supplied, will attempt to override the existing checkpoint.
	 * Otherwise a new checkpoint will be generated.
	 * The block on which the checkpoint is created must be able to have  a pressure plate
	 * placed on it.
	 * 
	 * @param args
	 * @param player
	 */
	public static void createCheckpoint(String[] args, Player player) {
		if (!ValidationMethods.createCheckpoint(args, player))
			return;

		String selected = PlayerInfo.getSelected(player);
		Location location = player.getLocation();
		int checkpoint = args.length == 2 ? Integer.parseInt(args[1]) :
			CourseInfo.getCheckpointAmount(selected) + 1;
		
		Block block = location.getBlock();
		Block blockUnder = block.getRelative(BlockFace.DOWN);
		
		if (Parkour.getSettings().isEnforceSafeCheckpoints()) {
			if (! Utils.isCheckpointSafe(player, block)) {
				return;
			}
		}
			
		if (blockUnder.getType().equals(Material.AIR))
			blockUnder.setType(Material.STONE);

		block.setType(Material.STONE_PLATE);		
		createCheckpointData(selected, location, checkpoint);
		player.sendMessage(Static.getParkourString() + "Checkpoint " + ChatColor.DARK_AQUA + checkpoint + ChatColor.WHITE + " set on " + ChatColor.AQUA + selected);
	}

	/**
	 * Create the actual checkpoint data
	 * The location for the player to teleport to and the location for the pressure plate will be created.
	 * 
	 * @param selected
	 * @param location
	 * @param checkpoint
	 */
	private static void createCheckpointData(String selected, Location location, int checkpoint){
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
	 * Teleport the player to a checkpoint
	 * If the checkpoint flag is false, it will teleport the player to the start.
	 * Otherwise the player will teleport to the chosen checkpoint.
	 * 
	 * @param args
	 * @param player
	 * @param checkpoint
	 */
	public static void teleportCheckpoint(String[] args, Player player, boolean checkpoint) {
		if (!CourseMethods.exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}

		String courseName = args[1].toLowerCase();
		FileConfiguration courseData = Parkour.getParkourConfig().getCourseData();
		String path = checkpoint ? courseName + "." + args[2] : courseName + ".0";

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
		player.sendMessage(checkpoint ? message + Utils.colour(" &f(&3" + args[2] + "&f)") : message);
	}

	/**
	 * Delete a checkpoint from the course
	 * This will only delete the last checkpoint, decreasing the amount of checkpoints.
	 * 
	 * @param courseName
	 * @param player
	 */
	public static void deleteCheckpoint(String courseName, Player player) {
		if (!CourseMethods.exist(courseName))
			return;

		courseName = courseName.toLowerCase();
		int point = CourseInfo.getCheckpointAmount(courseName);
		if (point <= 0){
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
     * @return
     */
    public static Checkpoint createCheckpointFromPlayerLocation(Player player) {
        return new Checkpoint(player.getLocation(),0, 0, 0);
    }
}

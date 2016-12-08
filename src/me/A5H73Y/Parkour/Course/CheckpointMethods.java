package me.A5H73Y.Parkour.Course;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CheckpointMethods {

	/**
	 * Retrieval of Checkpoints. Each checkpoint object has a Location (for the player to teleport to on death)
	 * and an XYZ coordinate for pressureplate detection.
	 * NOTE: Checkpoint 0 will be made, this is JUST for the joining of the course, the XYZ will be 0.
	 * @param courseName
	 * @return
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
		String world = courseData.getString(courseName + "." + "World");

		path = courseName + "." + (currentPoint + 1) + ".";

		double nCheckX = checkData.getDouble(path + "X");
		double nCheckY = checkData.getDouble(path + "Y");
		double nCheckZ = checkData.getDouble(path + "Z");

		return new Checkpoint(x, y, z, yaw, pitch, world, nCheckX, nCheckY, nCheckZ);
	}

	public static void createCheckpoint(String[] args, Player player) {
		String selected = PlayerMethods.getSelected(player.getName());

		if (!CourseMethods.exist(selected)){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", selected));
			return;
		}
		
		selected = selected.toLowerCase();
		int pointcount = Parkour.getParkourConfig().getCourseData().getInt((selected + "." + "Points")) + 1;
		Location location = player.getLocation();

		if (!(args.length <= 1)) {
			if (!Utils.isNumber(args[1])){
				player.sendMessage(Static.getParkourString() + "Checkpoint specified is not numeric!");
				return;
			}
			if (pointcount < Integer.parseInt(args[1])){
				player.sendMessage(Static.getParkourString() + "This point does not exist! " + ChatColor.RED + "Creation cancelled.");
				return;
			}

			pointcount = Integer.parseInt(args[1]);
		}

		if (pointcount < 1){
			player.sendMessage(Static.getParkourString() + "Invalid checkpoint number.");
			return;
		}

		createCheckpointData(selected, location, pointcount);

		Block block = location.getBlock();
		block.setType(Material.STONE_PLATE);
		location.setY(location.getBlockY() - 1);
		block = location.getBlock();
		if (block.getType().equals(Material.AIR))
			block.setType(Material.STONE);

		player.sendMessage(Static.getParkourString() + "Checkpoint " + ChatColor.DARK_AQUA + pointcount + ChatColor.WHITE + " set on " + ChatColor.AQUA + selected);
	}

	private static void createCheckpointData(String selected, Location location, int pointcount){
		FileConfiguration courseData = Parkour.getParkourConfig().getCourseData();
		FileConfiguration checkData = Parkour.getParkourConfig().getCheckData();

		int points = courseData.getInt(selected + ".Points");
		int pointmax = points >= pointcount ? points : pointcount;

		courseData.set(selected + ".Points", pointmax);
		courseData.set(selected + "." + pointcount + ".X", location.getBlockX() + 0.5);
		courseData.set(selected + "." + pointcount + ".Y", location.getBlockY() + 0.5);
		courseData.set(selected + "." + pointcount + ".Z", location.getBlockZ() + 0.5);
		courseData.set(selected + "." + pointcount + ".Yaw", location.getYaw());
		courseData.set(selected + "." + pointcount + ".Pitch", location.getPitch());

		checkData.set(selected + "." + pointcount + ".X", location.getBlockX());
		checkData.set(selected + "." + pointcount + ".Y", location.getBlockY() - 1);
		checkData.set(selected + "." + pointcount + ".Z", location.getBlockZ());

		Parkour.getParkourConfig().saveCheck();
		Parkour.getParkourConfig().saveCourses();
	}

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

	public static void deleteCheckpoint(String courseName, Player player) {
		if (!CourseMethods.exist(courseName))
			return;

		courseName = courseName.toLowerCase();
		int point = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Points");
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
				.replace("%CHECKPOINT%", point+"")
				.replace("%COURSE%", courseName));

		Utils.logToFile("Checkpoint " + point + " was deleted on " + courseName + " by " + player.getName());

	}

	public static int getNumberOfCheckpoints(String courseName){
		Integer number = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Points");
		return number != null ? number : -1;
	}
}

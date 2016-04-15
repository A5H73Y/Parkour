package me.A5H73Y.Parkour.Course;

import java.util.ArrayList;
import java.util.List;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Other.Configurations;
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
	public static List<Checkpoint> getCheckpoints(String courseName){
		List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
		Configurations config = Parkour.getParkourConfig();
		FileConfiguration checkData = config.getCheckData();

		int current = config.getCourseData().getInt(courseName + ".Points") + 1;

		for (int i=0; i < current; i++){

			Location location = Utils.getLocation(courseName, "."+i+".");

			if (location == null){
				Utils.log("[Parkour] Invalid checkpoint: "+courseName+"."+i);
				Utils.logToFile("Invalid checkpoint: "+courseName+"."+i);
				break;
			}

			Checkpoint checkpoint = 
					new Checkpoint(location, checkData.getDouble(courseName + "." + i + ".X"), checkData.getDouble(courseName + "." + i + ".Y"), checkData.getDouble(courseName + "." + i + ".Z"));
			
			checkpoints.add(checkpoint);
		}

		return checkpoints;
	}

	public static void createCheckpoint(String[] args, Player player) {
		String selected = PlayerMethods.getSelected(player.getName());

		if (selected==null || selected.length() == 0){
			player.sendMessage(Static.getParkourString() + "You have not selected a course!");
			return;
		}
		if (!CourseMethods.exist(selected)){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", selected));
			return;
		}

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
		block.setType(Material.STONE);

		player.sendMessage(Static.getParkourString() + "Checkpoint " + ChatColor.DARK_AQUA + pointcount + ChatColor.WHITE + " set on " + ChatColor.AQUA + selected);
	}

	private static void createCheckpointData(String selected, Location location, int pointcount){
		FileConfiguration courseData = Parkour.getParkourConfig().getCourseData();
		FileConfiguration checkData = Parkour.getParkourConfig().getCheckData();

		int points = courseData.getInt(selected + ".Points");
		pointcount = points >= pointcount ? points : pointcount;
		
		courseData.set(selected + ".Points", pointcount);
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
		
		String arenaname = args[1].toLowerCase();
		FileConfiguration courseData = Parkour.getParkourConfig().getCourseData();
		String path = checkpoint ? arenaname + "." + args[2] : arenaname + ".0";

		World world = Bukkit.getWorld(courseData.getString(arenaname + ".World"));
		double x = courseData.getDouble(path + ".X");
		double y = courseData.getDouble(path + ".Y");
		double z = courseData.getDouble(path + ".Z");
		float yaw = courseData.getInt(path + ".Yaw");
		float pitch = courseData.getInt(path + ".Pitch");

		if (x == 0 && y == 0 && z == 0) {
			player.sendMessage(Static.getParkourString() + ChatColor.RED + "ERROR: " + Static.White + "This checkpoint is invalid or doesn't exist");
			return;
		}

		Location l = new Location(world, x, y, z, yaw, pitch);
		player.teleport(l);
		String message = Static.getParkourString() + "You have teleported to " + Static.Aqua + arenaname;
		player.sendMessage(checkpoint ? message + Static.White + " (" + Static.Daqua + args[2] + Static.White + ")" : message);
	}

}

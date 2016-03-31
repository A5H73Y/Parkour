package me.A5H73Y.Parkour.Course;

import java.util.List;
import java.util.Map;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Other.ParkourBlocks;
import me.A5H73Y.Parkour.Other.Validation;
import me.A5H73Y.Parkour.Player.PPlayer;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CourseMethods {

	public static boolean exist(String courseName){
		for (String course : Static.getCourses()) {
			if (courseName.toLowerCase().equals(course)) return true;
		}
		return false;
	}

	public static Course findByName(String courseName){
		if (exist(courseName)){
			courseName = courseName.toLowerCase();
			//Get course information from config.yml
			ParkourBlocks pblocks = Static.getParkourBlocks();

			if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".customBlocks"))//TODO
				pblocks = Static.getParkourBlocks();

			Course course = 
					new Course(courseName, CheckpointMethods.getCheckpoints(courseName.toLowerCase()), pblocks);
			return course;
		}
		return null;
	}

	public static Course findByNumber(int courseNumber){	
		if (courseNumber > 0 && courseNumber <= Static.getCourses().size()){
			String courseName = Static.getCourses().get(courseNumber - 1);
			return findByName(courseName);
		}
		return null;
	}

	public static Course findByPlayer(String playerName){
		if (PlayerMethods.isPlaying(playerName)){
			Course course = PlayerMethods.getPlaying().get(playerName).getCourse();
			return course;
		}
		return null;
	}


	public static void createCourse(String[] args, Player player){
		if (!Validation.courseCreation(args, player))
			return;

		String name = args[1].toLowerCase();
		Location location = player.getLocation();
		FileConfiguration courseData = Parkour.getParkourConfig().getCourseData();

		courseData.set(name + ".Creator", player.getName());
		courseData.set(name + ".Views", 0);
		courseData.set(name + ".Completed", 0);
		courseData.set(name + ".XP", 0);
		courseData.set(name + ".Points", 0);
		courseData.set(name + ".World", location.getWorld().getName());
		courseData.set(name + ".0.X", location.getBlockX() + 0.5);
		courseData.set(name + ".0.Y", location.getBlockY() + 0.5);
		courseData.set(name + ".0.Z", location.getBlockZ() + 0.5);
		courseData.set(name + ".0.Yaw", location.getYaw());
		courseData.set(name + ".0.Pitch", location.getPitch());

		List<String> courseList = Static.getCourses();
		courseList.add(name);
		Static.setCourses(courseList);
		courseData.set("Courses", courseList);

		Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Selected", name);
		Parkour.getParkourConfig().saveUsers();
		Parkour.getParkourConfig().saveCourses();

		player.sendMessage(Utils.getTranslation("Parkour.Created").replace("%COURSE%", name));
	}

	/**
	 * This method only serves as a way of validating the course before joining the course as a player
	 * 
	 * @param player
	 * @param courseName
	 */
	public static void joinCourse(Player player, String courseName){
		Course course;
		if (Utils.isNumber(courseName))
			course = findByNumber(Integer.parseInt(courseName));
		else
			course = findByName(courseName);

		if (course == null){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
			return;
		}
		
		if (!Validation.courseJoining(player, courseName))
			return;

		PlayerMethods.playerJoin(player, course);
	}

	public static void finishCourse(Player player){

	}

	public static void displayCourseInfo(String[] args, Player player) {

		//Maybe display straight from config? Avoid checkpoints slamming IO
		/*
		player.sendMessage("=== " + course.getName() + " ===");
		player.sendMessage("Checkpoints:  " + course.getCheckpoints().size());
		player.sendMessage("Views:          " + course.getViews());
		player.sendMessage("Completed:    " + course.getCompleted());
		player.sendMessage("Lobby:        " + course.getLobby());
		player.sendMessage("Creator:      " + course.getCreator());
		% completed = complete / views * 100
		 */
	}

	public static void createLobby(String[] args, Player player) {
		String created = Static.getParkourString() + "Lobby ";
		setLobby(args, player);

		if (args.length > 1) {
			if (args.length > 2 && Utils.isNumber(args[2])){
				created = created.concat(ChatColor.AQUA + args[1] + ChatColor.WHITE + " created, with a required rank of " + ChatColor.DARK_AQUA + Integer.parseInt(args[2]));
				Parkour.getParkourConfig().getConfig().set("Lobby." + args[1] + ".Level", Integer.parseInt(args[2]));
			}else{
				created = created.concat(ChatColor.AQUA + args[1] + ChatColor.WHITE + " created");
			}
		}else{
			Parkour.getParkourConfig().getConfig().set("Lobby.Set", true);
			created = created.concat("was successfully created!");
		}
		Parkour.getPlugin().saveConfig();
		player.sendMessage(created);
	}

	private final static void setLobby(String[] args, Player player){
		Location loc = player.getLocation();
		String path = args.length > 1 ? "Lobby." + args[1]  : "Lobby";
		Parkour.getParkourConfig().getConfig().set(path + ".World", loc.getWorld().getName());
		Parkour.getParkourConfig().getConfig().set(path + ".X", loc.getX());
		Parkour.getParkourConfig().getConfig().set(path + ".Y", loc.getY());
		Parkour.getParkourConfig().getConfig().set(path + ".Z", loc.getZ());
		Parkour.getParkourConfig().getConfig().set(path + ".Pitch", loc.getPitch());
		Parkour.getParkourConfig().getConfig().set(path + ".Yaw", loc.getYaw());
		Utils.Log(path + " was set by " + player.getName());
	}

	public static void joinLobby(String[] args, Player player) {
		//If args != null if sent from commands
		FileConfiguration getConfig = Parkour.getParkourConfig().getConfig();

		if (!getConfig.getBoolean("Lobby.Set")){
			if (Utils.hasPermission(player, "Parkour.Admin")){
				player.sendMessage(Static.getParkourString() + ChatColor.RED + "Lobby has not been set!");
				player.sendMessage(Utils.Colour("Type &b'/pa setlobby'&f where you want the lobby to be set."));
			} else {
				player.sendMessage(Static.getParkourString() + ChatColor.RED + "Lobby has not been set! Please tell the Owner!");
			}
			return;
		}

		//variables
		Location lobby;
		boolean custom = false;
		int level = 0;

		if (args != null && args.length > 1) {
			custom = true;
			if (!getConfig.contains("Lobby." + args[1] + ".World")){
				player.sendMessage(Static.getParkourString() + "Lobby does not exist!");
				return;
			}
			if (getConfig.contains("Lobby." + args[1] + ".Level")){
				level = getConfig.getInt("Lobby." + args[1] + ".Level");
			}

			if (level > 0 && !player.hasPermission("Parkour.MinBypass")){
				if (Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Level") < level){
					player.sendMessage(Utils.getTranslation("Error.RequiredLvl").replaceAll("%LEVEL%", String.valueOf(level)));
					return;
				}

			}
			lobby = getLobby("Lobby." + args[1]);
		}else{
			//Default lobby
			lobby = getLobby("Lobby");
		}

		if (lobby == null){
			player.sendMessage(Static.getParkourString() + "Lobby is corrupt, please investigate.");
			return;
		}
		if (PlayerMethods.isPlaying(player.getName())) {
			PlayerMethods.playerLeave(player);
		}

		player.teleport(lobby);

		//Continue if player intentionally joined the lobby e.g /pa lobby
		if (args == null)
			return;

		if (custom){
			player.sendMessage(Utils.getTranslation("Parkour.LobbyOther").replaceAll("%LOBBY%", args[1]));
		}else{
			player.sendMessage(Utils.getTranslation("Parkour.Lobby"));

		}
	}

	private final static Location getLobby(String path){
		World world = Bukkit.getWorld(Parkour.getParkourConfig().getConfig().getString(path + ".World"));
		double x = Parkour.getParkourConfig().getConfig().getDouble(path + ".X");
		double y = Parkour.getParkourConfig().getConfig().getDouble(path + ".Y");
		double z = Parkour.getParkourConfig().getConfig().getDouble(path + ".Z");
		float yaw = Parkour.getParkourConfig().getConfig().getInt(path + ".Yaw");
		float pitch = Parkour.getParkourConfig().getConfig().getInt(path + ".Pitch");
		return new Location(world, x, y, z, yaw, pitch);
	}

	public static void deleteCourse(String[] args, Player player) {
		if (!exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		player.sendMessage(Static.getParkourString() + "You are about to delete course " + Static.Aqua + args[1] + ChatColor.WHITE + "...");
		player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
		Static.addQuestion(player.getName(), 1, args[1]);
		Utils.Log(args[1] + " was deleted by " + player.getName());
	}

	public static void list(String[] args, Player player) {
		if (!(args.length == 1)) {
			if (args[1].equalsIgnoreCase("players")) {
				if (PlayerMethods.getPlaying().size() == 0){
					player.sendMessage(Static.getParkourString() + "Nobody is playing Parkour!");
					return;
				}

				player.sendMessage(Static.getParkourString() + PlayerMethods.getPlaying().size() + " players using Parkour: ");

				for (Map.Entry<String, PPlayer> entry : PlayerMethods.getPlaying().entrySet()) { 
					player.sendMessage(" " + Static.Aqua + entry.getKey() + ChatColor.WHITE + " - " +
							ChatColor.DARK_GRAY + " C: " + ChatColor.GRAY + entry.getValue().getCourse().getName() + 
							ChatColor.DARK_GRAY + " D: " + ChatColor.GRAY + entry.getValue().getDeaths() + 
							ChatColor.DARK_GRAY + " T: " + ChatColor.GRAY + entry.getValue().displayTime());
				}

			} else if (args[1].equalsIgnoreCase("courses")) {
				if (Static.getCourses().size() == 0){
					player.sendMessage(Static.getParkourString() + "There are no Parkour courses!");
					return;
				}


				List<String> courseList = Static.getCourses();
				int page = (args.length == 3 && args[2] != null ? Integer.parseInt(args[2]) : 1);
				int pages = 1;
				int x = 0;
				for (int i=0;i<courseList.size();i++){
					x++;
					if (x == 8){x = 0;pages++;}
				}
				player.sendMessage(Static.getParkourString() + courseList.size() + " courses:");
				if (page < 1 || page > pages){
					player.sendMessage(Static.getParkourString() + "Invalid page. Maximum: " + pages); 
				}else{
					// max of 8?
					for (int i=0;i<courseList.size();i++){
						int max = page * 8;
						if(i >= (max - 8) && i < (max)){
							player.sendMessage((i + 1) + ") " + Static.Aqua + courseList.get(i));	
						}
					}
					player.sendMessage("== " + ChatColor.GRAY + page + Static.White + " / " + ChatColor.DARK_GRAY + pages + Static.White + " ==");
				}
			} else {
				player.sendMessage(Utils.invalidSyntax("list", "(players / courses)"));
			}
		}else{
			player.sendMessage(Utils.invalidSyntax("list", "(players / courses)"));
		}
	}

	public static void selectCourse(String[] args, Player player) {
		if (!exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}

		String arenaname = args[1];
		player.sendMessage(Static.getParkourString() + "Now Editing: " + Static.Aqua + arenaname);
		Integer pointcount = Parkour.getParkourConfig().getCourseData().getInt((arenaname + "." + "Points"));
		player.sendMessage(Static.getParkourString() + "Checkpoints created: " + Static.Aqua + pointcount);
		Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Selected", arenaname);
		Parkour.getParkourConfig().saveUsers();
	}

	public static void deselectCourse(String[] args, Player player){
		if (Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + player.getName() + ".Selected")){
			Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Selected", null);
			Parkour.getParkourConfig().saveUsers();
			player.sendMessage(Static.getParkourString() + "Finished editing.");
		}
	}

	public static void setFinished(String[] args, Player player) {
		if (Parkour.getParkourConfig().getCourseData().contains(args[1] + ".Finished")){
			player.sendMessage(Static.getParkourString() + Static.Aqua + args[1] + Static.White + " has already been set to finished!");
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(args[1] + ".Finished", true);
		Parkour.getParkourConfig().saveCourses();

		player.sendMessage(Utils.getTranslation("Parkour.Finish").replace("%COURSE%", args[1]));
		Utils.Log(args[1] + " was set to finished by " + player.getName());
	}

	public static void setPrize(String[] args, Player player) {
		if (!(Utils.hasPermissionOrOwnership(player, "Parkour.Admin", "Finish", args[1])))
			return;

		if (!exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}

		if (args[2].equalsIgnoreCase("command")){
			StringBuilder arguments = new StringBuilder();
			for (int i = 3; i < args.length; i++) {
				arguments.append(args[i]);
				arguments.append(" ");
			}
			String commandString = arguments.toString().replaceAll("/", "");
			Parkour.getParkourConfig().getCourseData().set(args[1] + ".Prize.CMD", commandString);
			Parkour.getParkourConfig().saveCourses();
			player.sendMessage(Static.getParkourString() + "Finish command set to " + arguments.toString());

		}else if (Utils.isNumber(args[2]) && Utils.isNumber(args[3])) {
			//TODO update to Material

			int id = Integer.parseInt(args[2].toString());
			int amount = Integer.parseInt(args[3].toString());

			Parkour.getParkourConfig().getCourseData().set(args[1] + ".Prize.ID", id);
			Parkour.getParkourConfig().getCourseData().set(args[1] + ".Prize.Amount", amount);
			Parkour.getParkourConfig().saveCourses();
			player.sendMessage(Static.getParkourString() + "Prize set to " + id + " (" + amount + ")");
		} else {
			player.sendMessage(Utils.invalidSyntax("prize", "(course) (id) (amount)"));
		}

	}

	public static void increaseComplete(String courseName){
		int completed = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Completed");
		Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Completed", completed++);
		Parkour.getParkourConfig().saveCourses();
	}
	public static void increaseView(String courseName){
		int views = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Views");
		Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Views", views+=1);
		Parkour.getParkourConfig().saveCourses();
	}

	public static void setStart(String[] args, Player player) {
		if (!Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + player.getName() + ".Selected")) 
			return;

		String selected = Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + player.getName() + ".Selected");

		if (!CourseMethods.exist(selected)){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", selected));
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(selected + ".0.X", player.getLocation().getX());
		Parkour.getParkourConfig().getCourseData().set(selected + ".0.Y", player.getLocation().getY());
		Parkour.getParkourConfig().getCourseData().set(selected + ".0.Z", player.getLocation().getZ());
		Parkour.getParkourConfig().getCourseData().set(selected + ".0.Yaw", player.getLocation().getYaw());
		Parkour.getParkourConfig().getCourseData().set(selected + ".0.Pitch", player.getLocation().getPitch());
		Utils.Log(selected + " spawn was reset by " + player.getName());
		player.sendMessage(Static.getParkourString() + "Spawn for " + Static.Aqua + selected + Static.White + " has been set to your position");

	}

	public static void setCreator(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		
		Parkour.getParkourConfig().getCourseData().set(args[1] + ".Creator", args[2]);
		Parkour.getParkourConfig().saveCourses();
		player.sendMessage(Static.getParkourString() + "Creator of " + args[1] + " was set to " + Static.Aqua +  args[2]);
	}
}
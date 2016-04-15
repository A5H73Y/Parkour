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

	/**
	 * This is used very often, if this could be optimized it would be awesome.
	 * I can see this becoming a problem when there are 100+ courses.
	 * @param courseName
	 * @return
	 */
	public static boolean exist(String courseName){
		courseName = courseName.toLowerCase();
		for (String course : Static.getCourses()) {
			if (courseName.equals(course)) return true;
		}
		return false;
	}

	/**
	 * The main method of course retrieval, everything is populated from this method (all checkpoints & information).
	 * So with this in mind, this should only be used when necessary.
	 * @param courseName
	 * @return
	 */
	public static Course findByName(String courseName){
		if (!exist(courseName))
			return null;
		
		courseName = courseName.toLowerCase();
		//Get course information from config.yml
		ParkourBlocks pblocks;

		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".customBlocks"))//TODO
			pblocks = Static.getParkourBlocks();
		else
			pblocks = Static.getParkourBlocks();

		List<Checkpoint> checkpoints = CheckpointMethods.getCheckpoints(courseName);
		Course course = new Course(courseName, checkpoints, pblocks);
		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".MaxDeaths"))
			course.setMaxDeaths(Parkour.getParkourConfig().getCourseData().getInt(courseName + ".MaxDeaths"));

		return course;
	}


	/**
	 * Same as the method above but uses the list index which is displayed in "/pa list courses"
	 * @param courseNumber
	 * @return
	 */
	public static Course findByNumber(int courseNumber){	
		if (courseNumber > 0 && courseNumber <= Static.getCourses().size()){
			String courseName = Static.getCourses().get(courseNumber - 1);
			return findByName(courseName);
		}
		return null;
	}

	/**
	 * This is used several times to find out what / how the player is currently doing.
	 * @param playerName
	 * @return
	 */
	public static Course findByPlayer(String playerName){
		if (PlayerMethods.isPlaying(playerName)){
			return PlayerMethods.getPlaying().get(playerName).getCourse();
		}
		return null;
	}

	/**
	 * This is the course creation. The spawn of the course is written in the courses.yml as checkpoint '0'. 
	 * The world is assumed once the course is joined, so having 2 different checkpoints in 2 different worlds is not an option.
	 * We don't assume or default any options, so there is less to process on completion / join etc. (SetMinLevel, rewards etc.)
	 * All courses names are stored in a string list, which is held in memory then added / removed from. 
	 * @param args
	 * @param player
	 */
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

	/**
	 * This method will be executed when the finish course is triggered (standing on the finish block, clicking a finish sign)
	 * @param player
	 */
	public static void finishCourse(Player player){

	}

	/**
	 * This is accessed via the "/pa info (course)", not sure the best way of doing this yet.
	 * @param args
	 * @param player
	 */
	public static void displayCourseInfo(String[] args, Player player) {
		//TODO

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
		System.out.println("-= Course info =-");
	}

	/**
	 * This is creating the lobby, everything is calculated based on the arguments.
	 * @param args
	 * @param player
	 */
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

	/**
	 * Joining the lobby. Can be accessed from the commands, and from the framework. 
	 * The args will be null if it's from the framework, if this is the case we don't send them a message. (Finishing a course etc)
	 * @param args
	 * @param player
	 */
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


	/**
	 * Based on the path passed in, will return a lobby location object from the config.
	 * @param path
	 * @return
	 */
	private final static Location getLobby(String path){
		World world = Bukkit.getWorld(Parkour.getParkourConfig().getConfig().getString(path + ".World"));
		double x = Parkour.getParkourConfig().getConfig().getDouble(path + ".X");
		double y = Parkour.getParkourConfig().getConfig().getDouble(path + ".Y");
		double z = Parkour.getParkourConfig().getConfig().getDouble(path + ".Z");
		float yaw = Parkour.getParkourConfig().getConfig().getInt(path + ".Yaw");
		float pitch = Parkour.getParkourConfig().getConfig().getInt(path + ".Pitch");
		return new Location(world, x, y, z, yaw, pitch);
	}

	/**
	 * Based on the args, it will calculate whether or not it is setting a custom lobby.
	 * @param args
	 * @param player
	 */
	private final static void setLobby(String[] args, Player player){
		Location loc = player.getLocation();
		String path = args.length > 1 ? "Lobby." + args[1]  : "Lobby";
		Parkour.getParkourConfig().getConfig().set(path + ".World", loc.getWorld().getName());
		Parkour.getParkourConfig().getConfig().set(path + ".X", loc.getX());
		Parkour.getParkourConfig().getConfig().set(path + ".Y", loc.getY());
		Parkour.getParkourConfig().getConfig().set(path + ".Z", loc.getZ());
		Parkour.getParkourConfig().getConfig().set(path + ".Pitch", loc.getPitch());
		Parkour.getParkourConfig().getConfig().set(path + ".Yaw", loc.getYaw());
		Utils.logToFile(path + " was set by " + player.getName());
	}

	/**
	 * Adds a question against the player for them to confirm. This is so they don't enter the command by accident, or something?
	 * @param args
	 * @param player
	 */
	public static void deleteCourse(String[] args, Player player) {
		if (!exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		player.sendMessage(Static.getParkourString() + "You are about to delete course " + Static.Aqua + args[1] + ChatColor.WHITE + "...");
		player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
		Static.addQuestion(player.getName(), 1, args[1]);
		Utils.logToFile(args[1] + " was deleted by " + player.getName());
	}

	/**
	 * Displays all the Parkour courses or all the players using the plugin.
	 * @param args
	 * @param player
	 */
	public static void displayList(String[] args, Player player) {
		if (!(args.length == 1)) {
			if (args[1].equalsIgnoreCase("players")) {
				if (PlayerMethods.getPlaying().size() == 0){
					player.sendMessage(Static.getParkourString() + "Nobody is playing Parkour!");
					return;
				}

				player.sendMessage(Static.getParkourString() + PlayerMethods.getPlaying().size() + " players using Parkour: ");

				//TODO pages
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
				int pages = 1, x = 0;

				for (int i=0;i<courseList.size();i++){
					x++;
					if (x == 8){x = 0;pages++;}
				}
				player.sendMessage(Static.getParkourString() + courseList.size() + " courses:");
				if (page < 1 || page > pages){
					player.sendMessage(Static.getParkourString() + "Invalid page. Maximum: " + pages); 
					return;
				}

				for (int i=0;i<courseList.size();i++){
					int max = page * 8;
					if(i >= (max - 8) && i < (max)){
						player.sendMessage((i + 1) + ") " + Static.Aqua + courseList.get(i));	
					}
				}
				player.sendMessage("== " + ChatColor.GRAY + page + Static.White + " / " + ChatColor.DARK_GRAY + pages + Static.White + " ==");

			} else {
				player.sendMessage(Utils.invalidSyntax("list", "(players / courses)"));
			}
		}else{
			player.sendMessage(Utils.invalidSyntax("list", "(players / courses)"));
		}
	}

	/**
	 * This is for functions that don't require a course parameter, for example creating a checkpoint.
	 * @param args
	 * @param player
	 */
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

	/**
	 * No real purpose of this anymore, but it will stop the player from having a course selected.
	 * @param args
	 * @param player
	 */
	public static void deselectCourse(String[] args, Player player){
		if (Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + player.getName() + ".Selected")){
			Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Selected", null);
			Parkour.getParkourConfig().saveUsers();
			player.sendMessage(Static.getParkourString() + "Finished editing.");
		}
	}

	/**
	 * Sets the Parkours course status to Complete, otherwise the player will get a notification saying it's still not finished on joining.
	 * @param args
	 * @param player
	 */
	public static void setFinished(String[] args, Player player) {
		if (Parkour.getParkourConfig().getCourseData().contains(args[1] + ".Finished")){
			player.sendMessage(Static.getParkourString() + Static.Aqua + args[1] + Static.White + " has already been set to finished!");
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(args[1] + ".Finished", true);
		Parkour.getParkourConfig().saveCourses();

		player.sendMessage(Utils.getTranslation("Parkour.Finish").replace("%COURSE%", args[1]));
		Utils.logToFile(args[1] + " was set to finished by " + player.getName());
	}

	/**
	 * Set the prize that the player gets awarded with when the complete the course.
	 * Can be an item with any amount and/or a command.
	 * @param args
	 * @param player
	 */
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

	/**
	 * Below methods are self explanitory, they will increase the view and completion count when appropriate.
	 * @param courseName
	 */
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

	/**
	 * Overwrite the original start of a course.
	 * @param args
	 * @param player
	 */
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
		Utils.logToFile(selected + " spawn was reset by " + player.getName());
		player.sendMessage(Static.getParkourString() + "Spawn for " + Static.Aqua + selected + Static.White + " has been set to your position");

	}

	/**
	 * Overwrite the original Author of the course.
	 * @param args
	 * @param player
	 */
	public static void setCreator(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(args[1].toLowerCase() + ".Creator", args[2]);
		Parkour.getParkourConfig().saveCourses();
		player.sendMessage(Static.getParkourString() + "Creator of " + args[1] + " was set to " + Static.Aqua +  args[2]);
	}

	/**
	 * Set the maximum amount of deaths the player has before automatically leaving the course.
	 * @param args
	 * @param player
	 */
	public static void setMaxDeaths(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])){
			player.sendMessage(Static.getParkourString() + "Amount of deaths is not valid.");
			return;
		}

		player.sendMessage(Static.getParkourString() + args[1] + " maximum deaths was set to " + Static.Aqua + args[2]);
		Parkour.getParkourConfig().getCourseData().set(args[1] + ".MaxDeaths", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * Set the mimimum Parkour level required to join a course.
	 * @param args
	 * @param player
	 */
	public static void setMinLevel(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])){
			player.sendMessage(Static.getParkourString() + "Amount of deaths is not valid.");
			return;
		}

		player.sendMessage(Static.getParkourString() + args[1] + " minimum level requirement was set to " + Static.Aqua + args[2]);
		Parkour.getParkourConfig().getCourseData().set(args[1] + ".MinimumLevel", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * This is the Parkour level the player gets awarded with on course completion.
	 * @param args
	 * @param player
	 */
	public static void rewardLevel(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])){
			player.sendMessage(Static.getParkourString() + "Amount of deaths is not valid.");
			return;
		}

		player.sendMessage(Static.getParkourString() + args[1] + " reward level was set to " + Static.Aqua + args[2]);
		Parkour.getParkourConfig().getCourseData().set(args[1] + ".RewardLevel", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * Not to be confused with rewardLevel, this associates a Parkour level with a message prefix.
	 * E.g. Level 10: Pro; Level 99: God
	 * @param args
	 * @param player
	 */
	public static void rewardRank(String[] args, Player player) {
		/*
	} else if (isInt(args[1])) {
		if (args.length == 3){

			int rank = Integer.parseInt(args[1]);

			usersData.set("ServerInfo.Levels." + rank + ".Rank", args[2]);


			player.sendMessage(ParkourString + "Level " + args[1] + "s rank was set to " + Aqua + args[2]);
			saveCourses();

		}else{
			player.sendMessage(ParkourString + "Invalid Syntax: /pa rewardrank (level) (rank)");
		}
		 */
	}
}
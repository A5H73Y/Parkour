package me.A5H73Y.Parkour.Course;

import java.util.List;
import java.util.Map;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Other.Validation;
import me.A5H73Y.Parkour.Player.PPlayer;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
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

import com.huskehhh.mysql.TimeObject;

public class CourseMethods {

	/**
	 * This is used very often, if this could be optimized it would be awesome.
	 * I can see this becoming a problem when there are 100+ courses.
	 * @param courseName
	 * @return
	 */
	public static boolean exist(String courseName){
		if (courseName == null) return false;
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

		List<Checkpoint> checkpoints = CheckpointMethods.getCheckpoints(courseName);
		Course course = new Course(courseName, checkpoints);

		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".customBlocks"))//TODO
			course.setParkourBlocks(Static.getParkourBlocks());//TODO

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
		if (courseNumber <= 0 || courseNumber > Static.getCourses().size())
			return null;

		String courseName = Static.getCourses().get(courseNumber - 1);
		return findByName(courseName);
	}

	/**
	 * This is used several times to find out what / how the player is currently doing.
	 * @param playerName
	 * @return
	 */
	public static Course findByPlayer(String playerName){
		if (!PlayerMethods.isPlaying(playerName))
			return null;

		return PlayerMethods.getPlaying().get(playerName).getCourse();
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
		DatabaseMethods.insertCourse(name, player.getName());
	}

	/**
	 * This method only serves as a way of validating the course before joining the course as a player.
	 * This will be the only way of joining a course; The processing will be done via this message.
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

		if (!Validation.courseJoining(player, course))
			return;

		PlayerMethods.playerJoin(player, course);
	}

	/**
	 * Will return a boolean whether the course is ready or not
	 * @param courseName
	 * @return
	 */
	public static boolean isReady(String courseName){
		return Parkour.getParkourConfig().getCourseData().getBoolean(courseName + ".Finished");
	}

	/**
	 * This is accessed via the "/pa info (course)", not sure the best way of doing this yet.
	 * @param args
	 * @param player
	 */
	public static void displayCourseInfo(String[] args, Player player) {
		if (!exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.Unknown"));
			return;
		}
		
		ChatColor aqua = ChatColor.AQUA;
		
		int views = 		Parkour.getParkourConfig().getCourseData().getInt(args[1] + ".Views");
		int completed = 	Parkour.getParkourConfig().getCourseData().getInt(args[1] + ".Completed");
		int checkpoints = 	Parkour.getParkourConfig().getCourseData().getInt(args[1] + ".Points");
		int maxDeaths = 	Parkour.getParkourConfig().getCourseData().getInt(args[1] + ".MaxDeaths");
		int minLevel =		Parkour.getParkourConfig().getCourseData().getInt(args[1] + ".MinimumLevel");
		int rewardLevel = 	Parkour.getParkourConfig().getCourseData().getInt(args[1] + ".Level");
		int XP =			Parkour.getParkourConfig().getCourseData().getInt(args[1] + ".XP");
		String lobby = 		Parkour.getParkourConfig().getCourseData().getString(args[1] + ".Lobby");
		String nextCourse =	Parkour.getParkourConfig().getCourseData().getString(args[1] + ".Course");
		String creator =	Parkour.getParkourConfig().getCourseData().getString(args[1] + ".Creator");
		
		double completePercent = Math.round(((completed * 1.0/ views) * 100));
		
		player.sendMessage(Utils.colour("-= &b" + Utils.standardizeText(args[1]) + " &f=-"));
		
		player.sendMessage("Views: " + aqua + views);
		player.sendMessage("Completed: " + aqua + completed + " times. (" + completePercent + "%)");
		player.sendMessage("Checkpoints: " + aqua + checkpoints);
		player.sendMessage("Creator: " + aqua + creator);
		
		if (minLevel > 0)
			player.sendMessage("Required level: " + aqua + maxDeaths);
		
		if (maxDeaths > 0)
			player.sendMessage("Max Deaths: " + aqua + maxDeaths);
		
		if (lobby != null && lobby.length() > 0)
			player.sendMessage("Lobby: " + aqua + lobby);
		
		if (nextCourse != null && nextCourse.length() > 0)
			player.sendMessage("Next course: " + aqua + nextCourse);
		
		if (rewardLevel > 0)
			player.sendMessage("Level Reward: " + aqua + rewardLevel);
		
		if (XP > 0)
			player.sendMessage("XP Reward: " + aqua + XP);
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
		FileConfiguration getConfig = Parkour.getParkourConfig().getConfig();

		if (!getConfig.getBoolean("Lobby.Set")){
			if (Utils.hasPermission(player, "Parkour.Admin")){
				player.sendMessage(Static.getParkourString() + ChatColor.RED + "Lobby has not been set!");
				player.sendMessage(Utils.colour("Type &b'/pa setlobby'&f where you want the lobby to be set."));
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
			
			level = getConfig.getInt("Lobby." + args[1] + ".Level");
			
			if (level > 0 && !player.hasPermission("Parkour.MinBypass")){
				if (Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Level") < level){
					player.sendMessage(Utils.getTranslation("Error.RequiredLvl").replace("%LEVEL%", String.valueOf(level)));
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

		//Only continue if player intentionally joined the lobby e.g /pa lobby
		if (args == null || args[0] == null) return;

		if (custom){
			player.sendMessage(Utils.getTranslation("Parkour.LobbyOther").replace("%LOBBY%", args[1]));
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
		player.sendMessage(Static.getParkourString() + "You are about to delete course " + ChatColor.AQUA + args[1] + ChatColor.WHITE + "...");
		player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
		Static.addQuestion(player.getName(), 1, args[1]);
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

				displayPlaying(player);

			} else if (args[1].equalsIgnoreCase("courses")) {
				if (Static.getCourses().size() == 0){
					player.sendMessage(Static.getParkourString() + "There are no Parkour courses!");
					return;
				}

				int page = (args.length == 3 && args[2] != null ? Integer.parseInt(args[2]) : 1);
		
				displayCourses(player, page);
				
			} else {
				player.sendMessage(Utils.invalidSyntax("list", "(players / courses)"));
			}
		}else{
			player.sendMessage(Utils.invalidSyntax("list", "(players / courses)"));
		}
	}
	
	private static void displayPlaying(Player player){
		player.sendMessage(Static.getParkourString() + PlayerMethods.getPlaying().size() + " players using Parkour: ");

		//TODO pages
		for (Map.Entry<String, PPlayer> entry : PlayerMethods.getPlaying().entrySet()) { 
			player.sendMessage(Utils.getTranslation("Parkour.Player")
					.replace("%PLAYER%", entry.getKey())
					.replace("%COURSE%", entry.getValue().getDeaths()+"")
					.replace("%TIME%", entry.getValue().displayTime()));
	
		}
	}
	
	private static void displayCourses(Player player, int page){
		List<String> courseList = Static.getCourses();
		if(page <= 0) {
			player.sendMessage(Static.getParkourString() + "Please enter a valid page number.");
	        return;
	    }

	    int fromIndex = (page - 1) * 8;
	    if(courseList.size() < fromIndex){
	    	player.sendMessage(Static.getParkourString() + "This page doesn't exist.");
	    	return;
	    }
	    
	    player.sendMessage(Static.getParkourString() + courseList.size() + " courses available.");
	    List<String> limited = courseList.subList(fromIndex, Math.min(fromIndex + 8, courseList.size()));
	
	    for (int i=0; i < limited.size(); i++){
	    	player.sendMessage(((fromIndex) + (i + 1)) + ") " + ChatColor.AQUA +  limited.get(i));
	    }
	    
	    player.sendMessage("== " + page + " / " + ((courseList.size() / 8) + 1) + " ==");
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
		player.sendMessage(Static.getParkourString() + "Now Editing: " + ChatColor.AQUA + arenaname);
		Integer pointcount = Parkour.getParkourConfig().getCourseData().getInt((arenaname + "." + "Points"));
		player.sendMessage(Static.getParkourString() + "Checkpoints: " + ChatColor.AQUA + pointcount);
		Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Selected", arenaname);
		Parkour.getParkourConfig().saveUsers();
	}

	/**
	 * This is now the main way of modifying a course now. 
	 * Most things will no longer require a course argument, but instead use the course you are editing.
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
	public static void setReady(String[] args, Player player) {
		String course = PlayerMethods.getSelected(player.getName());
		
		if (Parkour.getParkourConfig().getCourseData().contains(course + ".Finished")){
			player.sendMessage(Static.getParkourString() + ChatColor.AQUA + course + ChatColor.WHITE + " has already been set to finished!");
			return;
		}

		Parkour.getParkourConfig().getCourseData().set(course + ".Finished", true);
		Parkour.getParkourConfig().saveCourses();

		player.sendMessage(Utils.getTranslation("Parkour.Finish").replace("%COURSE%", course));
		Utils.logToFile(course + " was set to finished by " + player.getName());
	}

	/**
	 * Set the prize that the player gets awarded with when the complete the course.
	 * Can be an item with any amount and/or a command.
	 * @param args
	 * @param player
	 */
	public static void setPrize(String[] args, Player player) {
		if (!exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		
		} else if (!(args.length == 4 || args.length == 5)){
			player.sendMessage(Utils.invalidSyntax("prize", "(course) (material / command) [(Material) (amount) / (commands)]"));
			return;
		}
		
		String courseName = args[1].toLowerCase();
		
		//TODO test this.
		if (args[2].equalsIgnoreCase("command")){
			//TODO What happens "/pa prize command"

			StringBuilder arguments = new StringBuilder();
			for (int i = 3; i < args.length; i++) {
				arguments.append(args[i]);
				arguments.append(" ");
			}
			String commandString = arguments.toString().replace("/", "");
			Parkour.getParkourConfig().getCourseData().set(courseName + ".Prize.CMD", commandString);
			Parkour.getParkourConfig().saveCourses();
			player.sendMessage(Static.getParkourString() + "Finish command set to " + arguments.toString());

		} else if (args[2].equalsIgnoreCase("material")){
			if (!Utils.isNumber(args[4])) {
				player.sendMessage(Static.getParkourString() + "Amount needs to be numeric!");
				return;
			}

			Material prize = Material.getMaterial(args[3].toUpperCase());
			if (prize == null){
				player.sendMessage(Static.getParkourString() + "Invalid Material!");
				return;
			}

			int amount = Integer.parseInt(args[4]);

			Parkour.getParkourConfig().getCourseData().set(courseName + ".Prize.Material", prize.name());
			Parkour.getParkourConfig().getCourseData().set(courseName + ".Prize.Amount", amount);
			Parkour.getParkourConfig().saveCourses();
			player.sendMessage(Static.getParkourString() + "Prize for " + courseName + " set to " + prize.name() + " (" + amount + ")");
		} else {
			player.sendMessage(Utils.invalidSyntax("prize", "(course) (material / command) [(Material) (amount) / (commands)]"));
		}

	}

	/**
	 * Below methods are self explanitory, they will increase the view and completion count when appropriate.
	 * @param courseName
	 */
	public static void increaseComplete(String courseName){
		int completed = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Completed");
		Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Completed", completed+=1);
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
		String selected = PlayerMethods.getSelected(player.getName());

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
		player.sendMessage(Static.getParkourString() + "Spawn for " + ChatColor.AQUA + selected + ChatColor.WHITE + " has been set to your position");
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
		player.sendMessage(Static.getParkourString() + "Creator of " + args[1] + " was set to " + ChatColor.AQUA +  args[2]);
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

		player.sendMessage(Static.getParkourString() + ChatColor.AQUA + args[1] + ChatColor.WHITE + " maximum deaths was set to " + ChatColor.AQUA + args[2]);
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

		player.sendMessage(Static.getParkourString() + args[1] + " minimum level requirement was set to " + ChatColor.AQUA + args[2]);
		Parkour.getParkourConfig().getCourseData().set(args[1] + ".MinimumLevel", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * This is the Parkour level the player gets awarded with on course completion.
	 * @param args
	 * @param player
	 */
	public static void setRewardLevel(String[] args, Player player) {
		if (!CourseMethods.exist(args[1])){
			player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[1]));
			return;
		}
		if (!Utils.isNumber(args[2])){
			player.sendMessage(Static.getParkourString() + "Reward level needs to be numeric.");
			return;
		}

		player.sendMessage(Static.getParkourString() + args[1] + " reward level was set to " + ChatColor.AQUA + args[2]);
		Parkour.getParkourConfig().getCourseData().set(args[1] + ".Level", Integer.parseInt(args[2]));
		Parkour.getParkourConfig().saveCourses();
	}

	/**
	 * Not to be confused with rewardLevel or rewardPrize, this associates a Parkour level with a message prefix.
	 * A rank is just a visual prefix.
	 * E.g. Level 10: Pro; Level 99: God
	 * @param args
	 * @param player
	 */
	public static void setRewardRank(String[] args, Player player) {
		if (!Utils.isNumber(args[1])){
			player.sendMessage(Static.getParkourString() + "Reward level needs to be numeric.");
			return;
		}

		Parkour.getParkourConfig().getUsersData().set("ServerInfo.Levels." + args[1] + ".Rank", args[2]);
		Parkour.getParkourConfig().saveUsers();
		player.sendMessage(Static.getParkourString() + "Level " + args[1] + "'s rank was set to " + Utils.colour(args[2]));
	}

	/**
	 * So everything can be achieved from commands, you can type this command to place the finish block.
	 * It will also notify the player how they can make it ready for players to join.
	 * @param args
	 * @param player
	 */
	public static void setFinish(String[] args, Player player) {
		String courseName = PlayerMethods.getSelected(player.getName());
		
		Location location = player.getLocation();
		location.setY(location.getBlockY() - 1);
		Block block = location.getBlock();
		block.setType(Static.getParkourBlocks().getFinish());

		String message = "Finish block for " + courseName + " has been placed.";
		
		if (!isReady(courseName))
			message = message.concat(" If the course is ready for users to join, enter '/pa ready'");
		
		player.sendMessage(Static.getParkourString() + message);
	}

	public static void displayLeaderboard(String[] args, Player player) {
		if (!CourseMethods.exist(args[1]))
			return;
		
		List<TimeObject> times = DatabaseMethods.getTopCourseResults(args[1]);
		
		if (times.size() == 0){
			player.sendMessage(Static.getParkourString() + "Nobody has completed " + args[1] + " yet!");
			return;
		}
		
		player.sendMessage(Utils.colour("=== &b" + args[1] + "&f ==="));
		
		for (int i=0; i < times.size(); i++){
			player.sendMessage(Utils.colour((i + 1) + ") &b" + times.get(i).getPlayer() + "&f in &3" + Utils.calculateTime(times.get(i).getTime()) + "&f, dying &7" + times.get(i).getDeaths() + " &ftimes"));
		}
	}

	public static void deleteCheckpoint(String[] args, Player player) {
		Course course = findByName(PlayerMethods.getSelected(player.getName()));
		
		//if it has no checkpoints
		if ((course.getCheckpoints().size() - 1) <= 0){
			player.sendMessage(Static.getParkourString() + course.getName() + " has no checkpoints!");
			return;
		}
		
		player.sendMessage(Static.getParkourString() + "You are about to delete checkpoint " + ChatColor.AQUA + (course.getCheckpoints().size() - 1) + ChatColor.WHITE + " for course " + ChatColor.AQUA + course.getName() + ChatColor.WHITE + "...");
		player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
		Static.addQuestion(player.getName(), 2, (course.getCheckpoints().size() - 1) + ";" + course.getName());
	}

	public static void linkCourse(String[] args, Player player) {
		String selected = PlayerMethods.getSelected(player.getName());
		
		if (args[1].equalsIgnoreCase("course")){
			if (!CourseMethods.exist(args[2])){
				player.sendMessage(Utils.getTranslation("Error.Unknown"));
				return;
			}
			
			if (Parkour.getParkourConfig().getCourseData().contains(selected + ".LinkedLobby")){
				player.sendMessage(Static.getParkourString() + "This course is linked to a lobby!");
				return;
			}
			
			Parkour.getParkourConfig().getCourseData().set(selected + ".LinkedCourse", args[2].toLowerCase());
			Parkour.getParkourConfig().saveCourses();
			player.sendMessage(Static.getParkourString() + selected + " is now linked to " + args[2]);
			
		} else if (args[1].equalsIgnoreCase("lobby")){
			if (!Parkour.getParkourConfig().getConfig().contains("Lobby." + args[2] + ".World")){ //TODO
				player.sendMessage(Static.getParkourString() + "Lobby does not exist.");
				return;
			}
			
			if (Parkour.getParkourConfig().getCourseData().contains(selected + ".LinkedCourse")){
				player.sendMessage(Static.getParkourString() + "This course is linked to a course!");
				return;
			}
			
			Parkour.getParkourConfig().getCourseData().set(selected + ".LinkedLobby", args[2]);
			Parkour.getParkourConfig().saveCourses();
			player.sendMessage(Static.getParkourString() + selected + " is now linked to " + args[2]);
			
		} else {
			Utils.invalidSyntax("Link", "(course / lobby) (courseName / lobbyName)");
		}
		
	}

	public static void rateCourse(String[] args, Player player) {
		String courseName = Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + player.getName() + ".LastPlayed");
		
		if (courseName == null || !CourseMethods.exist(courseName)){
			player.sendMessage(Static.getParkourString() + "Invalid course.");
			return;
		}
		
		if (args[1].equalsIgnoreCase("like")){
			DatabaseMethods.insertVote(courseName, player.getName(), true);
		} else if (args[1].equalsIgnoreCase("dislike")){
			DatabaseMethods.insertVote(courseName, player.getName(), false);
		} else {
			player.sendMessage(Static.getParkourString() + "Invalid vote. Use either: /pa (like / dislike)");
		}
	}
}
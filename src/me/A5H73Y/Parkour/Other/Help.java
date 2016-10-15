package me.A5H73Y.Parkour.Other;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class Help {

	public static void lookupCommandHelp(String[] args, Player player){

		if (args.length == 1){
			player.sendMessage(Static.getParkourString() + "Find help about any Parkour command:");
			player.sendMessage("             /pa help " + ChatColor.AQUA + "(command)");
			return;
		}

		if (args[1].equalsIgnoreCase("join")){
			displayHelpMessage(player, "Joining a Course", "/pa join (courseName)", "/pa join Tutorial",
					" You are able to join a course using its name, no longer having to use the correct case. Each course has a unique numeric identifier (courseID) which can be used to join the course instead of its name. Once you have joined a course, you are in 'Parkour Mode' which allows you to interact with the 'Parkour Blocks' and track your statistics.");

		} else if (args[1].equalsIgnoreCase("create")){
			displayHelpMessage(player, "Creating a Course", "/pa create (courseName)", "/pa create Tutorial",
					" Creating a new Parkour course only takes 1 command, all the setup is automatic. Remember that your location and the way you're facing is saved and then loaded once the course is joined. By default the course will be 'unfinished' until set otherwise using '/pa ready'.");

		} else if (args[1].equalsIgnoreCase("leave")){
			displayHelpMessage(player, "Leaving a Course", "/pa leave", null,
					" Leaving the course you are currently playing will terminate all information tracking your current progress and you will be teleported back to the Parkour lobby.");

		} else if (args[1].equalsIgnoreCase("info")){
			displayHelpMessage(player, "Display Parkour information", "/pa info [player]", null,
					" Display all your Parkour statistics, which can include your current progress through a course as well as the saved information, such as your Parkour level. Using the extra parameter will allow you to display the information of a different player.");

		} else if (args[1].equalsIgnoreCase("course")){
			displayHelpMessage(player, "Display course information", "/pa course (course)", "/pa course Tutorial",
					" Display all course information, ");

		} else if (args[1].equalsIgnoreCase("lobby")){	
			displayHelpMessage(player, "Teleport to Parkour lobby", "/pa lobby [lobby]", null,
					" Teleport to the chosen lobby. If you do not specify a lobby it will take you to the default lobby, otherwise it will attempt to join the Lobby specified in the argument. Note that some lobbies can have a Parkour level requirement.");

		} else if (args[1].equalsIgnoreCase("")){	
			displayHelpMessage(player, "", "", "",
					" ");

		} else if (args[1].equalsIgnoreCase("")){	
			displayHelpMessage(player, "", "", "",
					" ");

		} else if (args[1].equalsIgnoreCase("")){	
			displayHelpMessage(player, "", "", "",
					" ");

		} else if (args[1].equalsIgnoreCase("")){	
			displayHelpMessage(player, "", "", "",
					" ");

		} else if (args[1].equalsIgnoreCase("")){	
			displayHelpMessage(player, "", "", "",
					" ");

		} else if (args[1].equalsIgnoreCase("")){	
			displayHelpMessage(player, "", "", "",
					" ");

		} else if (args[1].equalsIgnoreCase("")){	
			displayHelpMessage(player, "", "", "",
					" ");

		} else if (args[1].equalsIgnoreCase("")){	
			displayHelpMessage(player, "", "", "",
					" ");

		} else if (args[1].equalsIgnoreCase("")){	
			displayHelpMessage(player, "", "", "",
					" ");

		} else if (args[1].equalsIgnoreCase("")){	
			displayHelpMessage(player, "", "", "",
					" ");

		} else{
			player.sendMessage(Static.getParkourString() + "Sorry, I haven't written a help guide for that command yet.");
		}
	}

	private static void displayCommandUsage(Player player, String title, String arguments, String description){
		player.sendMessage(ChatColor.DARK_AQUA + "/pa " + ChatColor.AQUA + title + 
				(arguments != null ? ChatColor.YELLOW + " " + arguments : "") +
				ChatColor.BLACK + " : " + ChatColor.WHITE + description);
	}

	private static void displaySignCommandUsage(Player player, String title, String shortcut, String description){
		player.sendMessage(ChatColor.AQUA + title + ChatColor.YELLOW + " " + shortcut + ChatColor.BLACK + " : " + ChatColor.WHITE + description);
	}

	private static void displayHelpMessage(Player player, String title, String syntax, String example, String description){
		player.sendMessage("=== " + ChatColor.AQUA + title + ChatColor.WHITE + " ===");
		player.sendMessage(ChatColor.GRAY + " Syntax: " + ChatColor.WHITE + syntax);
		if (example != null)
			player.sendMessage(ChatColor.GRAY + " Example: " + ChatColor.WHITE + example);
		player.sendMessage("=== " + ChatColor.DARK_AQUA + "Description" + ChatColor.WHITE + " ===");
		player.sendMessage(description);
	}


	public static final void processCommandsInput(String[] args, Player player) {
		if (args.length > 1 && args[1].equalsIgnoreCase("signs")){
			displaySignCommands(args, player);
			return;
		}

		if (args.length == 1){
			displayCommandsIndex(player);	

		} else if (args[1].equals("1")){
			displayBasicCommands(player);

		} else if (args[1].equals("2")){
			displayCreatingCommands(player);

		} else if (args[1].equals("3")){
			displayConfigureCommands(player);

		} else if (args[1].equals("4")){
			displayAdminCommands(player);

		} else {
			player.sendMessage(Static.getParkourString() + "Invalid page!");
			displayCommandsIndex(player);

		}

		/*
		if (args.length == 1) {
				} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("1")) {							
				player.sendMessage("-=- " + Static.getParkourString() + "-=-");
				player.sendMessage(Daqua + "/pa " + AQUA + "join " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Join the course");
				player.sendMessage(Daqua + "/pa " + AQUA + "leave " + ChatColor.YELLOW + "[player]" + Black + " : " + White + "Leave the course");
				player.sendMessage(Daqua + "/pa " + AQUA + "create " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Create and select a course");
				player.sendMessage(Daqua + "/pa " + AQUA + "checkpoint " + ChatColor.YELLOW + "[point]" + Black + " : " + White + "Create a checkpoint");
				player.sendMessage(Daqua + "/pa " + AQUA + "select " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Edit a course, " + ChatColor.GRAY + "done" + White + " to stop");
				player.sendMessage(Daqua + "/pa " + AQUA + "delete " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Delete the course");
				player.sendMessage(Daqua + "/pa " + AQUA + "lobby" + Black + " : " + White + "Teleport to the Lobby");
				player.sendMessage(Daqua + "/pa " + AQUA + "invite " + ChatColor.YELLOW + "(player)" + Black + " : " + White + "Invite a player to the course");
				player.sendMessage(Daqua + "/pa " + AQUA + "kit" + Black + " : " + White + "Fill hotbar with Parkour blocks");
				player.sendMessage(Daqua + "/pa " + AQUA + "info " + ChatColor.YELLOW + "[Player]" + Black + " : " + White + "Display your information");
				player.sendMessage(Daqua + "/pa " + AQUA + "test " + ChatColor.YELLOW + "(on / off)" + Black + " : " + White + "Toggle Test mode");
				player.sendMessage(Daqua + "/pa " + AQUA + "tp " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Teleport to the course");
				player.sendMessage(Daqua + "/pa " + AQUA + "tpc " + ChatColor.YELLOW + "(course) " + ChatColor.BLUE + "(number)" + Black + " : " + White + "Teleport to a specific checkpoint");
				player.sendMessage(Daqua + "/pa " + AQUA + "prize " + ChatColor.YELLOW + "(course) " + ChatColor.BLUE + "(id) (amount)" + Black + " : " + White + "Set a custom prize");
				player.sendMessage(Daqua + "/pa " + AQUA + "list " + ChatColor.YELLOW + "(players/courses)" + Black + " : " + White + "List all Parkour players/courses");
				player.sendMessage(Daqua + "/pa " + AQUA + "cmds [1/3]" + Black + " : " + White + "Display the Parkour commands menu");
				player.sendMessage("-=- Page " + AQUA + "1" + White + " / " + Daqua + "3" + White + " -=-");
			} else if (args[1].equalsIgnoreCase("2")) {
				player.sendMessage("-=- " + Static.getParkourString() + "-=-");
				player.sendMessage(Daqua + "/pa " + AQUA + "finish " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Set the course to finished");
				player.sendMessage(Daqua + "/pa " + AQUA + "add " + ChatColor.YELLOW + "(command/death) (argument)" + Black + " : " + White + "Add command/deathid");
				player.sendMessage(Daqua + "/pa " + AQUA + "setstart" + Black + " : " + White + "Set the selected courses new start point");
				player.sendMessage(Daqua + "/pa " + AQUA + "setcreator " + ChatColor.YELLOW + "(course) (name)"+Black + " : " + White + "Set creator of a course");
				player.sendMessage(Daqua + "/pa " + AQUA + "link " + ChatColor.YELLOW + "(course) (lobby)" + Black + " : " + White + "Link a course with a custom lobby");	
				player.sendMessage(Daqua + "/pa " + AQUA + "reset " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Reset the course leaderboards");
				player.sendMessage(Daqua + "/pa " + AQUA + "resetall" + Black + " : " + White + "Reset all course's leaderboards");
				player.sendMessage(Daqua + "/pa " + AQUA + "resetcourse " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Reset all data about the course");
				player.sendMessage(Daqua + "/pa " + AQUA + "resetplayer " + ChatColor.YELLOW + "(player)" + Black + " : " + White + "Reset all data about the player");
				player.sendMessage(Daqua + "/pa " + AQUA + "quiet" + Black + " : " + White + "Disable death messages");
				player.sendMessage(Daqua + "/pa " + AQUA + "econ | economy" + Black + " : " + White + "Display Economy information");
				player.sendMessage(Daqua + "/pa " + AQUA + "request | bug" + Black + " : " + White + "Request a feature for Parkour");
				player.sendMessage(Daqua + "/pa " + AQUA + "help | contact" + Black + " : " + White + "To get help or contact me");
				player.sendMessage(Daqua + "/pa " + AQUA + "tutorial | tut" + Black + " : " + White + "Link to the official tutorial page");
				player.sendMessage(Daqua + "/pa " + AQUA + "mysql " + ChatColor.YELLOW + "[connect/status/info/recreate]" + Black + " : " + White + "MySQL commands");
				player.sendMessage(Daqua + "/pa " + AQUA + "reload" + Black + " : " + White + "Reload Parkour config");
				player.sendMessage("-=- Page " + AQUA + "2" + White + " / " + Daqua + "3" + White + " -=-");
			} else if (args[1].equalsIgnoreCase("3")) {
				player.sendMessage("-=- " + Static.getParkourString() + "-=-");
				player.sendMessage(ChatColor.GRAY + "Please remember these are setting Parkour levels and XP, not minecrafts.");
				player.sendMessage(Daqua + "/pa " + AQUA + "givexp " + ChatColor.YELLOW + "(amount) [player]" + Black + " : " + White + "Give yourself / player XP");
				player.sendMessage(Daqua + "/pa " + AQUA + "setxp " + ChatColor.YELLOW + "(amount) [player]" + Black + " : " + White + "Set yours or others XP");
				player.sendMessage(Daqua + "/pa " + AQUA + "setlevel " + ChatColor.YELLOW + "(amount) [player]" + Black + " : " + White + "Set yours or others Level");
				player.sendMessage(Daqua + "/pa " + AQUA + "rewardxp " + ChatColor.YELLOW + "(course) (amount)" + Black + " : " + White + "Set the XP reward for a course");
				player.sendMessage(Daqua + "/pa " + AQUA + "rewardlevel " + ChatColor.YELLOW + "(course) (level)" + Black + " : " + White + "Set the level reward for a course");
				player.sendMessage(Daqua + "/pa " + AQUA + "rewardrank " + ChatColor.YELLOW + "(course) (rank)" + Black + " : " + White + "Set the rank reward for a course");
				player.sendMessage(Daqua + "/pa " + AQUA + "setminlevel " + ChatColor.YELLOW + "(course) (level)" + Black + " : " + White + "Set a minimum level requirement for a course");
				player.sendMessage(Daqua + "/pa " + AQUA + "setmaxdeath " + ChatColor.YELLOW + "(course) (amount)" + Black + " : " + White + "Set a maximum death for a course");
				player.sendMessage(Daqua + "/pa " + AQUA + "spectate " + ChatColor.YELLOW + "(player) [-alert]" + Black + " : " + ChatColor.RED + "BETA " + White + "Spectate the Player");
				player.sendMessage(Daqua + "Remember: " + AQUA + "()" + White + " means required" + ChatColor.YELLOW + " : " + AQUA + "[]" + White + " means optional.");
				player.sendMessage("-=- Page " + AQUA + "3" + White + " / " + Daqua + "3" + White + " -=-");
			} else {
				player.sendMessage(Static.getParkourString() + "Page doesn't exist!");
			}
		} else if (args.length >= 3) {
			player.sendMessage(Utils.invalidSyntax("cmds", "[1-3]"));
		}
		player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "These commands are out of date. They may have been changed / removed. I will update them when I am happy they will not change.");
		 */
	}


	private static void displayCommandsIndex(Player player) {
		player.sendMessage(Utils.getStandardHeading("Parkour Commands Menu"));

		player.sendMessage("Please choose the desired command type:");
		player.sendMessage(" 1" + ChatColor.DARK_GRAY + " : " + ChatColor.GRAY + "Basics");
		player.sendMessage(" 2" + ChatColor.DARK_GRAY + " : " + ChatColor.GRAY + "Creating a course");
		player.sendMessage(" 3" + ChatColor.DARK_GRAY + " : " + ChatColor.GRAY + "Configuring a course");
		player.sendMessage(" 4" + ChatColor.DARK_GRAY + " : " + ChatColor.GRAY + "Admin");
		player.sendMessage("");
		player.sendMessage(ChatColor.DARK_GRAY + "Example: " + ChatColor.GRAY + "/pa cmds 1");
		player.sendMessage(ChatColor.DARK_GRAY + "Remember: " + ChatColor.AQUA + "()" + ChatColor.GRAY + " means required, " + ChatColor.AQUA + "[]" + ChatColor.GRAY + " means optional.");
	}

	private static void displayBasicCommands(Player player){
		player.sendMessage(Utils.getStandardHeading("Basic Commands"));

		displayCommandUsage(player, "join", "(course / courseId)", "Join the course");
		displayCommandUsage(player, "leave", null, "Leave the course");
		displayCommandUsage(player, "info", "[Player]", "Display your players Parkour information");
		displayCommandUsage(player, "course", "(course)", "Display the course information");
		displayCommandUsage(player, "lobby", "[lobby]", "Teleport to the specified lobby");
		displayCommandUsage(player, "perms", null, "Display your Parkour permissions");
		displayCommandUsage(player, "like / dislike", null, "Vote if liked the course you finished");
		displayCommandUsage(player, "list", "(players / courses)", "Display appropriate list");
		displayCommandUsage(player, "quiet", null, "Toggle visibility of Parkour messages");
		displayCommandUsage(player, "invite", "(player)", "Invite the player to the course");
		displayCommandUsage(player, "help | contact", null, "To get help or contact me");
		displayCommandUsage(player, "about | version", null, "Display Parkour information");
	}

	private static void displayCreatingCommands(Player player){
		player.sendMessage(Utils.getStandardHeading("Create Commands"));

		displayCommandUsage(player, "create", "(course)", "Create and select a course");
		displayCommandUsage(player, "checkpoint", "[point]", "Create (or overwrite) a checkpoint");
		displayCommandUsage(player, "kit", "[PB]", "Retrieve relevant Parkour Blocks set");
		displayCommandUsage(player, "select", "(course)", "Start editing the course");
		displayCommandUsage(player, "done", null, "Stop editing the course");
		displayCommandUsage(player, "setstart", "(course)", "Set course start to current position");
		displayCommandUsage(player, "setcreator", "(course) (player)", "Set creator of course");
		displayCommandUsage(player, "setlobby", "[name] [level]", "Create / overwrite Parkour lobby");
		displayCommandUsage(player, "finish", "(course)", "Set the status of the course to finished");
		displayCommandUsage(player, "prize", null, "Initiate a new prize conversation");
		displayCommandUsage(player, "test", null, "Toggle Parkour test mode");
		displayCommandUsage(player, "leaderboard", "(course)", "Display the course leaderboards");
		displayCommandUsage(player, "tutorial", null, "Link to the official tutorial page");
	}

	private static void displayConfigureCommands(Player player){
		player.sendMessage(Utils.getStandardHeading("Configure Commands"));

		displayCommandUsage(player, "tp / tpc", "", "");
		displayCommandUsage(player, "link", "", "");
		displayCommandUsage(player, "linkPB", "", "");
		displayCommandUsage(player, "setminlevel", "", "");
		displayCommandUsage(player, "setmaxdeath", "", "");
		displayCommandUsage(player, "firstreward", "", "");
		displayCommandUsage(player, "rewardlevel", "", "");
		displayCommandUsage(player, "rewardrank", "", "");
		displayCommandUsage(player, "rewardparkoins", "", "");
		displayCommandUsage(player, "", "", "");

	}

	private static void displayAdminCommands(Player player){
		player.sendMessage(Utils.getStandardHeading("Admin Commands"));
		
		displayCommandUsage(player, "delete", "", "");
		displayCommandUsage(player, "reset", "", "");
		displayCommandUsage(player, "economy", "", "");
		displayCommandUsage(player, "createPB", "", "");
		displayCommandUsage(player, "sql", "", "");
		displayCommandUsage(player, "settings", "", "");
		displayCommandUsage(player, "request / bug", "", "");
	}

	private static void displaySignCommands(String[] args, Player player) {
		player.sendMessage(Utils.getStandardHeading("Parkour Sign Commands"));

		player.sendMessage(ChatColor.DARK_AQUA + "[pa]");
		displaySignCommandUsage(player, "Join", "(j)", "Join sign for a Parkour course");
		displaySignCommandUsage(player, "Finish", "(f)", "Optional finish sign for a Parkour course");
		displaySignCommandUsage(player, "Lobby", "(l)", "Teleport to Parkour lobby");
		displaySignCommandUsage(player, "Leave", "(le)", "Leave the current course");
		displaySignCommandUsage(player, "JoinAll", "(ja)", "Displays all courses to join");
		displaySignCommandUsage(player, "Effect", "(e)", "Apply a Parkour effect");
		displaySignCommandUsage(player, "Stats", "(s)", "Display course stats");
		
		player.sendMessage(ChatColor.YELLOW + "() = shortcuts");
	}

	public static void displayEconomy(String[] args, Player player) {
		if (StartPlugin.vault == null){
			player.sendMessage(Static.getParkourString() + "Vault has not been linked.");
			return;
		}
		
		if (args.length < 2){
			player.sendMessage(Utils.invalidSyntax("econ", "(info / recreate / setprize / setfee)"));
			return;
		}
		
		if (args[1].equalsIgnoreCase("info")){
			player.sendMessage(Static.getParkourString() + "Linked with Vault v" + StartPlugin.vault.getDescription().getVersion());

		} else if (args[1].equalsIgnoreCase("setprize")) {
			if (!(args.length > 2)){
				player.sendMessage(Utils.invalidSyntax("setprize", "(course) (amount)"));
				return;
			}
			if (!(CourseMethods.exist(args[2]) && Utils.isNumber(args[3]))){
				player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[2]));
				return;
			}

			Parkour.getParkourConfig().getEconData().set("Price." + args[2] + ".Finish", Integer.parseInt(args[3]));
			Parkour.getParkourConfig().saveEcon();
			player.sendMessage(Static.getParkourString() + "Prize for " + args[2] + " set to " + args[3]);

		} else if (args[1].equalsIgnoreCase("setfee")) {
			if (!(args.length > 2)){
				player.sendMessage(Utils.invalidSyntax("setfee", "(course) (amount)"));
				return;
			}
			if (!(CourseMethods.exist(args[2]) && Utils.isNumber(args[3]))){
				player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[2]));
				return;
			}

			Parkour.getParkourConfig().getEconData().set("Price." + args[2] + ".Join", Integer.parseInt(args[3]));
			Parkour.getParkourConfig().saveEcon();
			player.sendMessage(Static.getParkourString() + "Fee for " + args[2] + " set to " + args[3]);

		} else if (args[1].equalsIgnoreCase("recreate")) {
			player.sendMessage(Static.getParkourString() + "Starting Recreation...");
			int changed = recreateEconomy();
			player.sendMessage(Static.getParkourString() + "Process Complete! " + changed + " courses updated.");

		} else {
			player.sendMessage(Utils.invalidSyntax("econ", "(info / recreate / setprize / setfee)"));
		}

	}

	private static int recreateEconomy(){
		FileConfiguration econ = Parkour.getParkourConfig().getEconData();

		int updated = 0;
		for (String course : Static.getCourses()) {
			try {
				if (!(Parkour.getParkourConfig().getEconData().contains("Price." + course + ".Join"))) {
					updated++;
					econ.set("Price." + course + ".Join", 0);
				}
				if (!(Parkour.getParkourConfig().getEconData().contains("Price." + course + ".Finish"))) {
					econ.set("Price." + course + ".Finish", 0);
				}
			} catch (Exception ex) {
				Utils.log(Utils.getTranslation("Error.Something", false).replace("%ERROR%", ex.getMessage()));	
			}
		}

		Parkour.getParkourConfig().saveEcon();
		return updated;
	}

	public static void displaySQL(String[] args, Player player) {
		player.sendMessage(Utils.getStandardHeading("SQL Details"));
		String type = Parkour.getDatabaseObj().getType();
		player.sendMessage("Type: " + type);
		player.sendMessage("Connected: " + (Parkour.getDatabaseObj().getConnection() != null));

		//TODO 
	}

}

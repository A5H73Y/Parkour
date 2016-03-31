package me.A5H73Y.Parkour.Other;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class Help {

	public static void lookupCommandHelp(String[] args, Player player){

		if (args.length == 1){
			player.sendMessage(Static.getParkourString() + "Find help about any Parkour command:");
			player.sendMessage(Static.Daqua + "             /pa " + Static.White + "help " + ChatColor.WHITE + "(command)");
			return;
		}

		if (args[1].equalsIgnoreCase("join")){
			displayHelpMessage(player, "Joining a Course", "/pa join (courseName)", "/pa join Tutorial",
					" You are now able to join a course using its name, no longer having to use the correct case. Each course has a unique numeric identifier which can be used to join the course, to display these use the '/pa list courses [page]' command. Using the course ID you can use '/pa join (ID)' to quickly join the course.");

		}else if (args[1].equalsIgnoreCase("create")){
			displayHelpMessage(player, "Creating a Course", "/pa create (courseName)", "/pa create Tutorial",
					" Creating a new Parkour course only takes 1 command, all the setup is automatic. Remember that your location and the way you're facing is saved and then loaded once the course is joined. By default the course will be 'unfinished' until set otherwise.");

		}else if (args[1].equalsIgnoreCase("leave")){
			displayHelpMessage(player, "Leaving a Course", "/pa leave", null,
					" Leaving the course you are currently playing will terminate all information tracking your current progress and you will be teleported back to the Parkour lobby. Players with permission have the ability to force other players to leave by using an extra argument: '/pa leave [player]'");

		}else if (args[1].equalsIgnoreCase("info")){
			displayHelpMessage(player, "Leaving a Course", "/pa info", null,
					" Display all the info, lol");

		}else if (args[1].equalsIgnoreCase("leave")){

		}else if (args[1].equalsIgnoreCase("leave")){

		}else{
			player.sendMessage(Static.getParkourString() + "This is not a valid Parkour command");
		}
	}


	private static void displayHelpMessage(Player player, String title, String syntax, String example, String description){
		player.sendMessage("=== " + Static.Aqua + title + Static.White + " ===");
		player.sendMessage(Static.Gray + " Syntax: " + Static.White + syntax);
		if (example != null)
			player.sendMessage(Static.Gray + " Example: " + Static.White + example);
		player.sendMessage("=== " + Static.Daqua + "Description" + Static.White + " ===");
		player.sendMessage(description);
		
		player.sendMessage(ChatColor.BOLD + " " + ChatColor.RED + " Please remember this is a beta build, please report any inconsistancies or problems to A5H73Y.");
	}


	public static final void displayCommands(String[] args, Player player) {
		ChatColor Daqua = Static.Daqua;
		ChatColor Aqua = Static.Aqua;
		ChatColor Black = ChatColor.BLACK;
		ChatColor White = Static.White;
		if (args.length == 1) {
			player.sendMessage("-=- " + Static.getParkourString() + "-=-");
			player.sendMessage(Daqua + "/pa " + Aqua + "join " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Join the course");
			player.sendMessage(Daqua + "/pa " + Aqua + "leave " + ChatColor.YELLOW + "[player]" + Black + " : " + White + "Leave the course");
			player.sendMessage(Daqua + "/pa " + Aqua + "create " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Create and select a course");
			player.sendMessage(Daqua + "/pa " + Aqua + "checkpoint " + ChatColor.YELLOW + "[point]" + Black + " : " + White + "Create a checkpoint");
			player.sendMessage(Daqua + "/pa " + Aqua + "select " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Edit a course, " + ChatColor.GRAY + "done" + White + " to stop");
			player.sendMessage(Daqua + "/pa " + Aqua + "delete " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Delete the course");
			player.sendMessage(Daqua + "/pa " + Aqua + "lobby" + Black + " : " + White + "Teleport to the Lobby");
			player.sendMessage(Daqua + "/pa " + Aqua + "invite " + ChatColor.YELLOW + "(player)" + Black + " : " + White + "Invite a player to the course");
			player.sendMessage(Daqua + "/pa " + Aqua + "kit" + Black + " : " + White + "Fill hotbar with Parkour blocks");
			player.sendMessage(Daqua + "/pa " + Aqua + "course " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Display the course information");
			player.sendMessage(Daqua + "/pa " + Aqua + "info " + ChatColor.YELLOW + "[Player]" + Black + " : " + White + "Display your information");
			player.sendMessage(Daqua + "/pa " + Aqua + "test " + ChatColor.YELLOW + "(on / off)" + Black + " : " + White + "Toggle Test mode");
			player.sendMessage(Daqua + "/pa " + Aqua + "tp " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Teleport to the course");
			player.sendMessage(Daqua + "/pa " + Aqua + "tpc " + ChatColor.YELLOW + "(course) " + ChatColor.BLUE + "(number)" + Black + " : " + White + "Teleport to a specific checkpoint");
			player.sendMessage(Daqua + "/pa " + Aqua + "prize " + ChatColor.YELLOW + "(course) " + ChatColor.BLUE + "(id) (amount)" + Black + " : " + White + "Set a custom prize");
			player.sendMessage(Daqua + "/pa " + Aqua + "list " + ChatColor.YELLOW + "(players/courses)" + Black + " : " + White + "List all Parkour players/courses");
			player.sendMessage(Daqua + "/pa " + Aqua + "cmds [1/3]" + Black + " : " + White + "Display the Parkour commands menu");
			player.sendMessage("-=- Page " + Aqua + "1" + White + " / " + Daqua + "3" + White + " -=-");
		} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("1")) {							
				player.sendMessage("-=- " + Static.getParkourString() + "-=-");
				player.sendMessage(Daqua + "/pa " + Aqua + "join " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Join the course");
				player.sendMessage(Daqua + "/pa " + Aqua + "leave " + ChatColor.YELLOW + "[player]" + Black + " : " + White + "Leave the course");
				player.sendMessage(Daqua + "/pa " + Aqua + "create " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Create and select a course");
				player.sendMessage(Daqua + "/pa " + Aqua + "checkpoint " + ChatColor.YELLOW + "[point]" + Black + " : " + White + "Create a checkpoint");
				player.sendMessage(Daqua + "/pa " + Aqua + "select " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Edit a course, " + ChatColor.GRAY + "done" + White + " to stop");
				player.sendMessage(Daqua + "/pa " + Aqua + "delete " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Delete the course");
				player.sendMessage(Daqua + "/pa " + Aqua + "lobby" + Black + " : " + White + "Teleport to the Lobby");
				player.sendMessage(Daqua + "/pa " + Aqua + "invite " + ChatColor.YELLOW + "(player)" + Black + " : " + White + "Invite a player to the course");
				player.sendMessage(Daqua + "/pa " + Aqua + "kit" + Black + " : " + White + "Fill hotbar with Parkour blocks");
				player.sendMessage(Daqua + "/pa " + Aqua + "course " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Display the course information");
				player.sendMessage(Daqua + "/pa " + Aqua + "info " + ChatColor.YELLOW + "[Player]" + Black + " : " + White + "Display your information");
				player.sendMessage(Daqua + "/pa " + Aqua + "test " + ChatColor.YELLOW + "(on / off)" + Black + " : " + White + "Toggle Test mode");
				player.sendMessage(Daqua + "/pa " + Aqua + "tp " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Teleport to the course");
				player.sendMessage(Daqua + "/pa " + Aqua + "tpc " + ChatColor.YELLOW + "(course) " + ChatColor.BLUE + "(number)" + Black + " : " + White + "Teleport to a specific checkpoint");
				player.sendMessage(Daqua + "/pa " + Aqua + "prize " + ChatColor.YELLOW + "(course) " + ChatColor.BLUE + "(id) (amount)" + Black + " : " + White + "Set a custom prize");
				player.sendMessage(Daqua + "/pa " + Aqua + "list " + ChatColor.YELLOW + "(players/courses)" + Black + " : " + White + "List all Parkour players/courses");
				player.sendMessage(Daqua + "/pa " + Aqua + "cmds [1/3]" + Black + " : " + White + "Display the Parkour commands menu");
				player.sendMessage("-=- Page " + Aqua + "1" + White + " / " + Daqua + "3" + White + " -=-");
			} else if (args[1].equalsIgnoreCase("2")) {
				player.sendMessage("-=- " + Static.getParkourString() + "-=-");
				player.sendMessage(Daqua + "/pa " + Aqua + "finish " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Set the course to finished");
				player.sendMessage(Daqua + "/pa " + Aqua + "add " + ChatColor.YELLOW + "(command/death) (argument)" + Black + " : " + White + "Add command/deathid");
				player.sendMessage(Daqua + "/pa " + Aqua + "setstart" + Black + " : " + White + "Set the selected courses new start point");
				player.sendMessage(Daqua + "/pa " + Aqua + "setcreator " + ChatColor.YELLOW + "(course) (name)"+Black + " : " + White + "Set creator of a course");
				player.sendMessage(Daqua + "/pa " + Aqua + "link " + ChatColor.YELLOW + "(course) (lobby)" + Black + " : " + White + "Link a course with a custom lobby");	
				player.sendMessage(Daqua + "/pa " + Aqua + "reset " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Reset the course leaderboards");
				player.sendMessage(Daqua + "/pa " + Aqua + "resetall" + Black + " : " + White + "Reset all course's leaderboards");
				player.sendMessage(Daqua + "/pa " + Aqua + "resetcourse " + ChatColor.YELLOW + "(course)" + Black + " : " + White + "Reset all data about the course");
				player.sendMessage(Daqua + "/pa " + Aqua + "resetplayer " + ChatColor.YELLOW + "(player)" + Black + " : " + White + "Reset all data about the player");
				player.sendMessage(Daqua + "/pa " + Aqua + "quiet" + Black + " : " + White + "Disable death messages");
				player.sendMessage(Daqua + "/pa " + Aqua + "econ | economy" + Black + " : " + White + "Display Economy information");
				player.sendMessage(Daqua + "/pa " + Aqua + "about | ver | version" + Black + " : " + White + "Display plugin about page");
				player.sendMessage(Daqua + "/pa " + Aqua + "request | bug" + Black + " : " + White + "Request a feature for Parkour");
				player.sendMessage(Daqua + "/pa " + Aqua + "help | contact" + Black + " : " + White + "To get help or contact me");
				player.sendMessage(Daqua + "/pa " + Aqua + "tutorial | tut" + Black + " : " + White + "Link to the official tutorial page");
				player.sendMessage(Daqua + "/pa " + Aqua + "mysql " + ChatColor.YELLOW + "[connect/status/info/recreate]" + Black + " : " + White + "MySQL commands");
				player.sendMessage(Daqua + "/pa " + Aqua + "reload" + Black + " : " + White + "Reload Parkour config");
				player.sendMessage("-=- Page " + Aqua + "2" + White + " / " + Daqua + "3" + White + " -=-");
			} else if (args[1].equalsIgnoreCase("3")) {
				player.sendMessage("-=- " + Static.getParkourString() + "-=-");
				player.sendMessage(ChatColor.GRAY + "Please remember these are setting Parkour levels and XP, not minecrafts.");
				player.sendMessage(Daqua + "/pa " + Aqua + "givexp " + ChatColor.YELLOW + "(amount) [player]" + Black + " : " + White + "Give yourself / player XP");
				player.sendMessage(Daqua + "/pa " + Aqua + "setxp " + ChatColor.YELLOW + "(amount) [player]" + Black + " : " + White + "Set yours or others XP");
				player.sendMessage(Daqua + "/pa " + Aqua + "setlevel " + ChatColor.YELLOW + "(amount) [player]" + Black + " : " + White + "Set yours or others Level");
				player.sendMessage(Daqua + "/pa " + Aqua + "rewardxp " + ChatColor.YELLOW + "(course) (amount)" + Black + " : " + White + "Set the XP reward for a course");
				player.sendMessage(Daqua + "/pa " + Aqua + "rewardlevel " + ChatColor.YELLOW + "(course) (level)" + Black + " : " + White + "Set the level reward for a course");
				player.sendMessage(Daqua + "/pa " + Aqua + "rewardrank " + ChatColor.YELLOW + "(course) (rank)" + Black + " : " + White + "Set the rank reward for a course");
				player.sendMessage(Daqua + "/pa " + Aqua + "setminlevel " + ChatColor.YELLOW + "(course) (level)" + Black + " : " + White + "Set a minimum level requirement for a course");
				player.sendMessage(Daqua + "/pa " + Aqua + "setmaxdeath " + ChatColor.YELLOW + "(course) (amount)" + Black + " : " + White + "Set a maximum death for a course");
				player.sendMessage(Daqua + "/pa " + Aqua + "spectate " + ChatColor.YELLOW + "(player) [-alert]" + Black + " : " + ChatColor.RED + "BETA " + White + "Spectate the Player");
				player.sendMessage(Daqua + "Remember: " + Aqua + "()" + White + " means required" + ChatColor.YELLOW + " : " + Aqua + "[]" + White + " means optional.");
				player.sendMessage("-=- Page " + Aqua + "3" + White + " / " + Daqua + "3" + White + " -=-");
			} else {
				player.sendMessage(Static.getParkourString() + "Page doesn't exist!");
			}
		} else if (args.length >= 3) {
			player.sendMessage(Utils.invalidSyntax("cmds", "[1-3]"));
		}

	}


	public static void displayEconomy(String[] args, Player player) {
		if (StartPlugin.vault == null){
			player.sendMessage(Static.getParkourString() + "Vault has not been linked.");
			return;
		}

		if (args[1].equalsIgnoreCase("info")){
			player.sendMessage(Static.getParkourString() + "Linked with Vault v" + StartPlugin.vault.getDescription().getVersion());
			return;
		}

		if (args[1].equalsIgnoreCase("setprize")) {
			if (!(args.length > 2)){
				player.sendMessage(Utils.invalidSyntax("setprize", "(course) (amount)"));
				return;
			}
			if (!(CourseMethods.exist(args[2]) && Utils.isNumber(args[3]))){
				player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[2]));
				return;
			}

			try {
				Parkour.getParkourConfig().getEconData().set("Price." + args[2] + ".Finish", Integer.parseInt(args[3]));
				Parkour.getParkourConfig().saveEcon();
				player.sendMessage(Static.getParkourString() + "Prize for " + args[2] + " set to " + args[3]);
			} catch (Exception ex) {
				player.sendMessage(Utils.getTranslation("Error.Something").replaceAll("%ERROR%", ex.getMessage()));	
			}

		}else if (args[1].equalsIgnoreCase("setfee")) {
			if (!(args.length > 2)){
				player.sendMessage(Utils.invalidSyntax("setfee", "(course) (amount)"));
				return;
			}
			if (!(CourseMethods.exist(args[2]) && Utils.isNumber(args[3]))){
				player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", args[2]));
				return;
			}
			
			try {
				Parkour.getParkourConfig().getEconData().set("Price." + args[2] + ".Join", Integer.parseInt(args[3]));
				Parkour.getParkourConfig().saveEcon();
				player.sendMessage(Static.getParkourString() + "Fee for " + args[2] + " set to " + args[3]);
			} catch (Exception ex) {
				player.sendMessage(Utils.getTranslation("Error.Something").replaceAll("%ERROR%", ex.getMessage()));	
			}

		} else if (args[1].equalsIgnoreCase("recreate")) {
			player.sendMessage(Static.getParkourString() + "Starting Recreation...");
			recreateEconomy();
			player.sendMessage(Static.getParkourString() + "Process Complete!");
		} else {
			player.sendMessage(Utils.invalidSyntax("econ", "(info / recreate / setprize / setfee)"));
		}

	}
	
	private static void recreateEconomy(){
		for (int i = 0; i < Static.getCourses().size(); i++) {
			String s = Static.getCourses().get(i);
			try {
				if (!(Parkour.getParkourConfig().getEconData().contains("Price." + s + ".Join"))) {
					Parkour.getParkourConfig().getEconData().set("Price." + s + ".Join", 0);
				}
				if (!(Parkour.getParkourConfig().getEconData().contains("Price." + s + ".Finish"))) {
					Parkour.getParkourConfig().getEconData().set("Price." + s + ".Finish", 0);
				}
				Parkour.getParkourConfig().saveEcon();
			} catch (Exception ex) {
				System.out.println(Utils.getTranslation("Error.Something", false).replaceAll("%ERROR%", ex.getMessage()));	
			}
		}
	}

	public static void displaySQL(String[] args, Player player) {
		player.sendMessage(Static.getParkourString() + "- SQL Details -");
		player.sendMessage("Type: " + Parkour.getDatabaseObj().getType());
		//TODO 
	}

}

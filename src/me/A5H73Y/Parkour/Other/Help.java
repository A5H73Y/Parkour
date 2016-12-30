package me.A5H73Y.Parkour.Other;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class Help {

	public static void lookupCommandHelp(String[] args, Player player){
		if (args.length == 1){
			player.sendMessage(Static.getParkourString() + "Find helpful information about any Parkour command:");
			player.sendMessage("             /pa help " + ChatColor.AQUA + "(command)");
			return;
		}

		if (args[1].equalsIgnoreCase("join")){
			displayHelpMessage(player, "Join a Course", "/pa join (courseName)", "/pa join Tutorial",
					" You are able to join a course using its name, no longer having to use the correct case. Each course has a unique numeric identifier (courseID) which can be used to join the course instead of its name. Once you have joined a course, you are in 'Parkour Mode' which allows you to interact with the 'Parkour Blocks' and track your statistics.");

		} else if (args[1].equalsIgnoreCase("leave")){
			displayHelpMessage(player, "Leave a Course", "/pa leave", null,
					" Leaving the course you are currently playing will terminate all information tracking your current progress and you will be teleported back to the Parkour lobby.");

		} else if (args[1].equalsIgnoreCase("info")){
			displayHelpMessage(player, "Display Parkour information", "/pa info [player]", null,
					" Display all your Parkour statistics, which can include your current progress through a course as well as the saved information, such as your Parkour level. Using the extra parameter will allow you to display the information of a different player.");

		} else if (args[1].equalsIgnoreCase("course")){
			displayHelpMessage(player, "Display course information", "/pa course (course)", "/pa course Tutorial",
					" Display all the course information, including the requirements to join and the rewards given on completion.");

		} else if (args[1].equalsIgnoreCase("lobby")){	
			displayHelpMessage(player, "Teleport to Parkour lobby", "/pa lobby [lobby]", null,
					" Teleport to the chosen lobby. If you do not specify a lobby it will take you to the default lobby, otherwise it will attempt to join the Lobby specified in the argument. Note that some lobbies can have a Parkour level requirement.");

		} else if (args[1].equalsIgnoreCase("perms")){	
			displayHelpMessage(player, "Display Parkour Permissions", "/pa perms", null,
					" Your Parkour permissions will be displayed based on the group permissions you have. For example if you have 'Parkour.Admin.*', then you are a part of the Admin group, same for 'Parkour.Basic.*' etc. However if you have been given only a selection of permissiosn from that group then it will not display, for example 'Parkour.Admin.Testmode' does not make you an admin. 'Parkour.*' will give you permission for everything.");

		} else if (args[1].equalsIgnoreCase("like / dislike")){	
			displayHelpMessage(player, "Vote opinion of course", "/pa like", "/pa dislike",
					" Once you complete a course, you will have the ability to submit your vote on whether you liked the course or not. You only have one for each course. The only purpose of this is statistics, i.e. 70% of people liked this course.");

		} else if (args[1].equalsIgnoreCase("list")){	
			displayHelpMessage(player, "Display Courses / Parkour players", "/pa list courses", "/pa list players", 
					" This command will display either all the courses saved on the server in a page format, ordered by date of creation, each having their own unique numerical ID which can be used to join the course; or display all the players that are currently using the plugin, this includes which course, and how many times they've died.");

		} else if (args[1].equalsIgnoreCase("quiet")){	
			displayHelpMessage(player, "Toggle Quiet mode", "/pa quiet", null, 
					" If the Parkour messages are getting annoying i.e. Seeing 'You died! ...' regularly, you can toggle visibility of these messages using this command.");

		} else if (args[1].equalsIgnoreCase("invite")){	
			displayHelpMessage(player, "Invite a player to a course", "/pa invite (player)", "/pa invite A5H73Y", 
					" If another player is interested on which course you are on, simply send them an invite and it will instruct them on how to join. If you want to challenge eachother, check out the '/pa challenge' command.");

		} else if (args[1].equalsIgnoreCase("challenge")){	
			displayHelpMessage(player, "Challenge a player to a course", "/pa challenge (course) (player)", "/pa challenge Tutorial A5H73Y", 
					" Fancy adding an element of competition? Simply execute the command above to send a challenge to the player, if they accept using '/pa accept' then you'll both be teleported to the beginning of the course and a countdown will initiate, when the counter reaches 0 the race will begin. The visibility of each player is configurable.");

		} else if (args[1].equalsIgnoreCase("create")){
			displayHelpMessage(player, "Create a Course", "/pa create (courseName)", "/pa create Tutorial",
					" Creating a new Parkour course only takes 1 command, all the setup is automatic. Remember that your location and the way you're facing is saved and then loaded once the course is joined. By default the course will be 'unfinished' until set otherwise using '/pa ready'.");

		} else if (args[1].equalsIgnoreCase("checkpoint")){	
			displayHelpMessage(player, "Create a checkpoint", "/pa checkpoint [number]", "/pa checkpoint 1",
					" Made to be as automated and easy as possible, all you do is simply select (edit) a course using '/pa select (course)', then stand where you want a checkpoint and enter '/pa checkpoint' and as if by magic it's all done! If you mess up a checkpoint, you can simply override it using '/pa checkpoint (number)'. A pressureplate will be automatically placed.");

		} else if (args[1].equalsIgnoreCase("kit")){	
			displayHelpMessage(player, "Retrieve ParkourBlocks", "/pa kit [PB]", "/pa kit FireKit",
					" You can create a set of ParkourBlocks and call it whatever you want. Using this command you can fill your inventory with the blocks you configured, if you don't specify a ParkourBlocks set it will use the Default blocks.");

		} else if (args[1].equalsIgnoreCase("select")){	
			displayHelpMessage(player, "Edit a course", "/pa select (course)", "/pa select Tutorial",
					" Many of the commands don't require a course parameter as they will use the course you are editing to make things a bit easier. For example '/pa checkpoint' will use the course you are editing, if you want to find out which course you are currently editing use '/pa select'. When you create a course, it will automatically select it for editing.");

		} else if (args[1].equalsIgnoreCase("done")){	
			displayHelpMessage(player, "Finish editing a course", "/pa done", null,
					" Will finish editing whatever course you were editing.");
			
		} else if (args[1].equalsIgnoreCase("setstart")){	
			displayHelpMessage(player, "Set start of a course", "/pa setstart", null,
					" The start of the selected course will be overwritten to your current position, rather than having to delete the course.");

		} else if (args[1].equalsIgnoreCase("setcreator")){	
			displayHelpMessage(player, "Set creator of a course", "/pa setcreator (course) (playerName)", "/pa setcreator Tutorial A5H73Y",
					" The creator of the course will be overwritten to what you've specified. Helpful if an Admin has to setup the course which a non-admin player created. The creator of a course will have certain permissions for that course, regardless of if they are an admin.");

		} else if (args[1].equalsIgnoreCase("setlobby")){	
			displayHelpMessage(player, "Set a Parkour lobby", "/pa setlobby [name] [levelRequired]", "/pa setlobby City 10",
					" Create a lobby where you are stood, specifying its name and a level requirement to join. You are able to link courses to lobbies after completion.");

		} else if (args[1].equalsIgnoreCase("finish")){	
			displayHelpMessage(player, "Set Course status to Finish", "/pa finish", null,
					" When you first create a course, it will not be joinable until it has been set to finished by its creator (configurable). The command will set the status to ready to join and will place the default finish block to where you are stood.");
		
		} else if (args[1].equalsIgnoreCase("prize")){	
			displayHelpMessage(player, "Start prize configuration conversation", "/pa prize", null,
					" A conversation will be started to allow you to setup a course prize exactly how you want, without having to enter long ugly commands.");

		} else if (args[1].equalsIgnoreCase("test")){	
			displayHelpMessage(player, "Toggle Parkour Test Mode", "/pa test", null,
					" When wanting to test your course in 'ParkourMode' to similate how each ParkourBlocks will respond, you can enable TestMode, which will basically fake being on a course to allow you to test it without having to join / leave the course repeatedly.");

		} else if (args[1].equalsIgnoreCase("leaderboard")){	
			displayHelpMessage(player, "Display course leaderboards", "/pa leaderboard", null,
					" Start the conversation to display the leaderboards you want, whether it's the best global or personal times.");

		} else if (args[1].equalsIgnoreCase("tutorial")){	
			displayHelpMessage(player, "Display links to tutorials", "/pa leaderboard", null,
					" If you wish to learn from the offical Parkour tutorials, click the link to be navigated to the tutorial section of the bukkit plugin page.");

		} else if (args[1].equalsIgnoreCase("tp")){	
			displayHelpMessage(player, "Teleport to Course", "/pa tp (course)", "/pa tp Example",
					" Teleport to the start of the chosen course. This will NOT activate Parkour Mode, but simply move you to the course.");

		} else if (args[1].equalsIgnoreCase("tpc")){	
			displayHelpMessage(player, "Teleport to Course checkpoint", "/pa tp (course) (point)", "/pa tpc Example 2",
					" Teleport to the chosen checkpoint on the course. This will NOT activate Parkour Mode, but simply move you to the checkpoint on the course.");
			
		} else if (args[1].equalsIgnoreCase("link")){	
			displayHelpMessage(player, "Link the course after completion", "/pa link (argument) (argument)", "/pa link course Level2",
					" You are now able to link the selected course to either a custom lobby, or to join a different course straight after you complete the selected course. For example if you selected a course '/pa select Level1', you would be able to make the player join Level2 after they complete Level1 by doing '/pa link course Level2', or if you wish for them to teleport to a custom lobby '/pa link lobby Admin'.");
			
		} else if (args[1].equalsIgnoreCase("linkPB")){	
			displayHelpMessage(player, "Link a course to ParkourBlocks", "/pa linkPB (course) (PB)", "/pa link Example FireKit",
					" Each course has the ability to have a unique set of ParkourBlocks, created using the '/pa createPB' command. Once linked, each type of ParkourBlock for that course will be configured to what you set.");
			
		} else if (args[1].equalsIgnoreCase("setmode")){
			displayHelpMessage(player, "Set Mode for course", "/pa setmode (course) (mode)", "/pa setmode Example Freedom",
					" By default, a course does not have a special mode attached. Each mode can affect the interaction with the course, an example being the 'Freedom' mode allows you to create and load your own checkpoints.");
			
		} else if (args[1].equalsIgnoreCase("setminlevel")){	
			displayHelpMessage(player, "Set minimum required level for course", "/pa setminlevel (course) (level)", "/pa setminlevel Example 5",
					" By default, a course does not have a minimum level requirement to join. However, if you want to enforce the progression of Parkour courses, you can require the player to have a Parkour level greater than or equal to the minimum level specified.");
			
		} else if (args[1].equalsIgnoreCase("setmaxdeath")){	
			displayHelpMessage(player, "Set maximum amount of deaths for course", "/pa setmaxdeath (course) (amount)", "/pa setmaxdeath Example 5",
					" By default, a course does not have a maximum amount of deaths. However, you can enforce a limit on the amount of deaths the player can accumulate before being forced to leave the course.");
			
		} else if (args[1].equalsIgnoreCase("rewardonce")){	
			displayHelpMessage(player, "Reward only once for that course", "/pa rewardonce (course)", "/pa rewardonce Example",
					" Prevent a player from rewarding themselves multiple times for completing a course, by only allowing them to claim the reward the first time they complete the course.");
			
		} else if (args[1].equalsIgnoreCase("rewardlevel")){	
			displayHelpMessage(player, "Reward a Parkour Level", "/pa rewardlevel (course) (level)", "/pa rewardlevel Example 5",
					" Not overriden if x > y etc.");
			
		} else if (args[1].equalsIgnoreCase("rewardrank")){	
			displayHelpMessage(player, "Reward a Parkour Rank", "/pa rewardrank (level) (rank)", "/pa rewardrank 4 &4Pro",
					" ");
			
		} else if (args[1].equalsIgnoreCase("rewardparkoins")){	
			displayHelpMessage(player, "Reward Parkoins", "/pa rewardparkoins (course) (amount)", "/pa rewardparkoins Example 10",
					" ");
			
		} else if (args[1].equalsIgnoreCase("recreate")){	
			displayHelpMessage(player, "Recreate course database", "/pa recreate", null,
					" ");
			
		} else if (args[1].equalsIgnoreCase("delete")){	
			displayHelpMessage(player, "Delete a course / lobby", "/pa delete (course / lobby) (argument)", "/pa delete course Example",
					" ");
			
		} else if (args[1].equalsIgnoreCase("reset")){	
			displayHelpMessage(player, "Reset a course / player", "/pa reset (course / player) (argument)", "/pa reset player A5H73Y",
					" ");
			
		} else if (args[1].equalsIgnoreCase("economy")){	
			displayHelpMessage(player, "Display Economy information", "/pa economy (info / recreate / setprize / setfee)", "/pa economy info",
					" ");
			
		} else if (args[1].equalsIgnoreCase("createPB")){	
			displayHelpMessage(player, "Create Parkour Blocks", "/pa createPB", null,
					" ");
			
		} else if (args[1].equalsIgnoreCase("validatePB")){	
			displayHelpMessage(player, "Validate Parkour Blocks", "/pa validatePB [kit]", "/pa validatePB FireKit",
					" ");
			
		} else if (args[1].equalsIgnoreCase("sql")){	
			displayHelpMessage(player, "Display SQL information", "/pa SQL", null,
					" ");
			
		} else if (args[1].equalsIgnoreCase("settings")){	
			displayHelpMessage(player, "Display Parkour Settings", "/pa settings", null,
					" ");
			
		} else if (args[1].equalsIgnoreCase("request")){	
			displayHelpMessage(player, "Request a Feature / Report a Bug", "/pa request", null,
					" ");
			
		} else {
			player.sendMessage(Static.getParkourString() + "Unrecognised command. Please find all available commands using '/pa cmds'");
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
		displayCommandUsage(player, "like / dislike", null, "Vote for course you finished");
		displayCommandUsage(player, "list", "(players / courses)", "Display appropriate list");
		displayCommandUsage(player, "quiet", null, "Toggle visibility of Parkour messages");
		displayCommandUsage(player, "invite", "(player)", "Invite the player to the course");
		displayCommandUsage(player, "challenge", "(course) (player)", "Challenge player to course");
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
		displayCommandUsage(player, "setstart", null, "Set selected course start to current position");
		displayCommandUsage(player, "setcreator", "(course) (player)", "Set creator of course");
		displayCommandUsage(player, "setlobby", "[name] [level]", "Create / overwrite Parkour lobby");
		displayCommandUsage(player, "finish", null, "Set the status of the selected course to finished");
		displayCommandUsage(player, "prize", null, "Initiate a new prize conversation");
		displayCommandUsage(player, "test", null, "Toggle Parkour test mode");
		displayCommandUsage(player, "leaderboard", "[course] [amount] [type]", "Show leaderboards");
		displayCommandUsage(player, "tutorial", null, "Link to the official tutorial page");
	}

	private static void displayConfigureCommands(Player player){
		player.sendMessage(Utils.getStandardHeading("Configure Commands"));

		displayCommandUsage(player, "tp / tpc", "(course)", "Teleport to course / checkpoint");
		displayCommandUsage(player, "link", "(argument) (argument)", "Link a course");
		displayCommandUsage(player, "linkPB", "(course) (PB)", "Link ParkourBlocks");
		displayCommandUsage(player, "setmode", "(course) (mode)", "Set Parkour Mode");
		displayCommandUsage(player, "setminlevel", "(course) (level)", "Set course minimum level");
		displayCommandUsage(player, "setmaxdeath", "(course) (death)", "Set course max deaths");
		displayCommandUsage(player, "rewardonce", "(course)", "Toggle if the prize is given once");
		displayCommandUsage(player, "rewardlevel", "(course) (level)", "Reward level on complete");
		displayCommandUsage(player, "rewardrank", "(level) (rank)", "Reward rank on complete");
		displayCommandUsage(player, "rewardparkoins", "(course) (amount)", "Reward Parkoins");
	}

	private static void displayAdminCommands(Player player){
		player.sendMessage(Utils.getStandardHeading("Admin Commands"));

		displayCommandUsage(player, "recreate", null, "Fix course database");
		displayCommandUsage(player, "delete", "(argument)", "Delete course / lobby");
		displayCommandUsage(player, "reset", "(argument)", "Delete course / player");
		displayCommandUsage(player, "economy", null, "Display economy menu");
		displayCommandUsage(player, "createPB", null, "Start ParkourBlocks creation");
		displayCommandUsage(player, "validatePB", "[PB]", "Validate ParkourBlocks");
		displayCommandUsage(player, "sql", null, "Display SQL menu");
		displayCommandUsage(player, "settings", null, "Display Parkour Settings");
		displayCommandUsage(player, "request / bug", null, "Display relevant info");
	}

	private static void displaySignCommands(String[] args, Player player) {
		player.sendMessage(Utils.getStandardHeading("Parkour Sign Commands"));

		player.sendMessage(ChatColor.DARK_AQUA + "[pa]");
		displaySignCommandUsage(player, "Join", "(j)", "Join sign for a Parkour course");
		displaySignCommandUsage(player, "Finish", "(f)", "Optional finish sign for a Parkour course");
		displaySignCommandUsage(player, "Lobby", "(l)", "Teleport to Parkour lobby");
		displaySignCommandUsage(player, "Leave", "(le)", "Leave the current course");
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

			Parkour.getParkourConfig().getEconData().set("Price." + args[2].toLowerCase() + ".Finish", Integer.parseInt(args[3]));
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

			Parkour.getParkourConfig().getEconData().set("Price." + args[2].toLowerCase() + ".Join", Integer.parseInt(args[3]));
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
		player.sendMessage("Type: " + DatabaseMethods.type);
		player.sendMessage("Connected: " + (Parkour.getDatabaseObj().getConnection() != null));
		//TODO show path to SQLite.
	}
	
	public static void displaySettings(Player player){
		player.sendMessage(Utils.getStandardHeading("Parkour Settings"));
		
		player.sendMessage("Version: " + ChatColor.AQUA + Static.getVersion());
		player.sendMessage("DevBuild: " + ChatColor.AQUA + Static.getDevBuild());
		player.sendMessage("Economy: " + ChatColor.AQUA + Static.getEconomy());
		player.sendMessage("BountifulAPI: " + ChatColor.AQUA + Static.getBountifulAPI());
		player.sendMessage("Disable Commands: " + ChatColor.AQUA + Parkour.getSettings().isDisableCommands());
		player.sendMessage("Enforce world: " + ChatColor.AQUA + Parkour.getSettings().isEnforceWorld());
		
		player.sendMessage(ChatColor.GRAY + "If you want more settings displayed, please ask");
	}

}

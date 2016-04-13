package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Course.CheckpointMethods;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.Backup;
import me.A5H73Y.Parkour.Other.Help;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ParkourCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("parkour")) {
			// Player commands ============================================
			if(sender instanceof Player){
				Player player = (Player)sender;

				if (Parkour.getParkourConfig().getConfig().getBoolean("Other.Use.CmdPermission") 
						&& !Utils.hasPermission(player, "Parkour.Basic", "Commands"))
					return false;


				if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("create")) {
						if (!Utils.hasPermission(player, "Parkour.Create"))
							return false;

						CourseMethods.createCourse(args, player);

					} else if (args[0].equalsIgnoreCase("join")) {
						if (!Utils.hasPermission(player, "Parkour.Join"))
							return false;

						if (!Utils.validateArgs(player, args.length, 2))
							return false;

						CourseMethods.joinCourse(player, args[1]);

					} else if (args[0].equalsIgnoreCase("leave")) {
						PlayerMethods.playerLeave(player);

					} else if (args[0].equalsIgnoreCase("info")) {
						PlayerMethods.displayPlayerInfo(args, player);

					} else if (args[0].equalsIgnoreCase("course")) {
						if (!Utils.validateArgs(player, args.length, 2))
							return false;

						CourseMethods.displayCourseInfo(args, player);

					} else if (args[0].equalsIgnoreCase("lobby")) {
						CourseMethods.joinLobby(args, player);

					} else if (args[0].equalsIgnoreCase("setlobby")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						CourseMethods.createLobby(args, player);
						
					} else if (args[0].equalsIgnoreCase("setcreator")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;
						
						if (!Utils.validateArgs(player, args.length, 3))
							return false;
						
						CourseMethods.setCreator(args, player);

					} else if (args[0].equalsIgnoreCase("checkpoint")) {
						if (!Utils.hasPermission(player, "Parkour.Admin", "Delete"))
							return false;

						CheckpointMethods.createCheckpoint(args, player);

					} else if (args[0].equalsIgnoreCase("setstart")) {
						if (!Utils.hasPermission(player, "Parkour.Admin", "SetStart"))
							return false;
						
						if (!Utils.validateArgs(player, args.length, 1))
							return false;

						CourseMethods.setStart(args, player);
						
					} else if (args[0].equalsIgnoreCase("finish")) {
						if (!Utils.hasPermissionOrOwnership(player, "Parkour.Admin", "Finish", args[1]))
							return false;
						
						if (!Utils.validateArgs(player, args.length, 2))
							return false;

						CourseMethods.setFinished(args, player);

					} else if (args[0].equalsIgnoreCase("prize")) {
						if (!Utils.validateArgs(player, args.length, 4))
							return false;

						CourseMethods.setPrize(args, player);

					} else if (args[0].equalsIgnoreCase("perms")) {
						PlayerMethods.getPermissions(player);

					} else if (args[0].equalsIgnoreCase("kit")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "Kit"))
							return false;

						PlayerMethods.givePlayerKit(player);

					} else if (args[0].equalsIgnoreCase("delete")) {
						if (!Utils.hasPermission(player, "Parour.Admin", "Delete"))
							return false;

						if (!Utils.validateArgs(player, args.length, 2))
							return false;

						CourseMethods.deleteCourse(args, player);

					} else if (args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("edit")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "Select"))
							return false;

						if (!Utils.validateArgs(player, args.length, 2))
							return false;

						CourseMethods.selectCourse(args, player);

					} else if (args[0].equalsIgnoreCase("done") || args[0].equalsIgnoreCase("deselect") || args[0].equalsIgnoreCase("stopselect")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "Select"))
							return false;

						CourseMethods.deselectCourse(args, player);

					} else if (args[0].equalsIgnoreCase("tp")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "TP"))
							return false;

						if (!Utils.validateArgs(player, args.length, 2))
							return false;

						CheckpointMethods.teleportCheckpoint(args, player, false);

					} else if (args[0].equalsIgnoreCase("tpc")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "TPC"))
							return false;

						if (!Utils.validateArgs(player, args.length, 3))
							return false;

						CheckpointMethods.teleportCheckpoint(args, player, true);
						
					} else if (args[0].equalsIgnoreCase("link")) {
						if (!Utils.hasPermission(player, "Parkour.Admin", "Testmode"))
							return false;

						if (!Utils.validateArgs(player, args.length, 4))
							return false;
						
						//TODO Link to lobby or course.

					} else if (args[0].equalsIgnoreCase("setminlevel")){
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args.length, 3))
							return false;
						
						CourseMethods.setMinLevel(args, player);
						
					} else if (args[0].equalsIgnoreCase("setmaxdeath")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args.length, 3))
							return false;
						
						CourseMethods.setMaxDeaths(args, player);
						

					} else if (args[0].equalsIgnoreCase("rewardlevel")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args.length, 3))
							return false;
						
						CourseMethods.rewardLevel(args, player);
						
					} else if (args[0].equalsIgnoreCase("rewardrank")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args.length, 3))
							return false;
						
						CourseMethods.rewardRank(args, player);
						
					} else if (args[0].equalsIgnoreCase("quiet")) {
						PlayerMethods.toggleQuiet(player);
					
					} else if (args[0].equalsIgnoreCase("test")) {
						if (!Utils.hasPermission(player, "Parkour.Admin", "Testmode"))
							return false;

						//targetPlayer = Bukkit.getPlayer(cmdtarget);
						
						PlayerMethods.toggleTestmode(player);
						
					} else if (args[0].equalsIgnoreCase("economy") || args[0].equalsIgnoreCase("econ")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args.length, 2))
							return false;

						Help.displayEconomy(args, player);
						
					} else if (args[0].equalsIgnoreCase("invite")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "Invite"))
							return false;
						
						if (!Utils.validateArgs(player, args.length, 2))
							return false;
						
						PlayerMethods.invitePlayer(args, player);
						
					} else if (args[0].equalsIgnoreCase("list")) {
						CourseMethods.displayList(args, player);

					} else if (args[0].equalsIgnoreCase("help")) {
						Help.lookupCommandHelp(args, player);
						
					} else if (args[0].equalsIgnoreCase("resetplayer") || args[0].equalsIgnoreCase("removeplayer") || args[0].equalsIgnoreCase("deleteplayer")){
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args.length, 2))
							return false;
						
						PlayerMethods.resetPlayer(args, player);
						
						//Other commands//	
					} else if (args[0].equalsIgnoreCase("about")) {
						player.sendMessage(Static.getParkourString() + "Server is running Parkour " + Static.Gray + Static.getVersion());
						if (Static.getDevBuild())
							player.sendMessage(Static.Aqua + "- You are running a development build -");
						player.sendMessage("This plugin was developed by " + ChatColor.GOLD + "A5H73Y");

					} else if (args[0].equalsIgnoreCase("contact")) {
						player.sendMessage(Static.getParkourString() + "For information or help please contact me:");
						player.sendMessage(" DevBukkit: " + Static.Aqua + "A5H73Y");
						player.sendMessage(" Skype: " + Static.Aqua + "iA5H73Y");
						player.sendMessage(" Parkour URL: " + Static.Aqua + "http://dev.bukkit.org/server-mods/parkour/");

					} else if (args[0].equalsIgnoreCase("request") || args[0].equalsIgnoreCase("bug")) {

						player.sendMessage(Static.getParkourString() + "To Request a feature or to Report a bug...");
						player.sendMessage("Click here: " + ChatColor.DARK_AQUA + "http://dev.bukkit.org/server-mods/parkour/forum/");
						
					} else if (args[0].equalsIgnoreCase("sql")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;
						
						Help.displaySQL(args, player);

					} else if (args[0].equalsIgnoreCase("settings")) {
						if (!Utils.hasPermission(player, "Parkour.Admin")) 
							return false;

						player.sendMessage("=- Parkour v" + Static.getVersion() + " Settings -=");
						player.sendMessage(Static.Daqua + "Finish settings! ");
						//TODO
					} else if (args[0].equalsIgnoreCase("cmds")) {
						Help.displayCommands(args, player);

					} else if (args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("no")) {
						player.sendMessage(Static.getParkourString() + "You have not been asked a question!");

					} else if (args[0].equalsIgnoreCase("reload")){
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						Parkour.getParkourConfig().reload();
						player.sendMessage(Utils.getTranslation("Other.Reload"));
						Utils.logToFile(player.getName() + " reloaded the Parkour config");

					} else {
						player.sendMessage(Static.getParkourString() + "Unknown command!");
						player.sendMessage(Static.Daqua + "/pa " + Static.Aqua + "cmds [1-3]" + ChatColor.BLACK + " : " + Static.White + "To display all available commands");
					}

				} else {
					player.sendMessage(Static.getParkourString() + "Plugin proudly created by " + Static.Aqua + "A5H73Y");
					player.sendMessage(Static.Daqua + "/pa " + Static.Aqua + "cmds [1-3]" + ChatColor.BLACK + " : " + Static.White + "To display all available commands");
				}

				// Console Commands ==========================================
			}else if(sender instanceof ConsoleCommandSender){
				if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("backup")) {
						Backup.backupNow();
						
					} else if (args[0].equalsIgnoreCase("setlevel")){
						//TODO
						
					} else if (args[0].equalsIgnoreCase("cmds")) {
						System.out.println("pa backup : Create a backup zip of the Parkour config folder");
						System.out.println("pa setlevel (player) : Set a players Parkour Level");
						
					} else {
						System.out.println("[Parkour] Unknown Command. Enter 'pa cmds' to display all commands.");
					}
				}else{
					System.out.println("[Parkour] v" + Static.getVersion() + " installed. Plugin created by A5H73Y.");
				}
			}
		}
		return false;
	}
}
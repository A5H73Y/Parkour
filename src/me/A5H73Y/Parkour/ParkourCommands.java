package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Conversation.ParkourConversation.ConversationType;
import me.A5H73Y.Parkour.Course.CheckpointMethods;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.Backup;
import me.A5H73Y.Parkour.Other.Help;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Settings;
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
			if (sender instanceof Player){
				Player player = (Player)sender;

				if (Parkour.getSettings().isCommandPermission() && !Utils.hasPermission(player, "Parkour.Basic", "Commands"))
					return false;

				if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("join")) {
						if (!Utils.validateArgs(player, args, 2))
							return false;

						if (!Parkour.getParkourConfig().getConfig().getBoolean("OnJoin.AllowViaCommand"))
							return false;

						CourseMethods.joinCourse(player, args[1]);

					} else if (args[0].equalsIgnoreCase("create")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "Create"))
							return false;

						CourseMethods.createCourse(args, player);

					} else if (args[0].equalsIgnoreCase("leave")) {
						PlayerMethods.playerLeave(player);

					} else if (args[0].equalsIgnoreCase("info")) {
						PlayerMethods.displayPlayerInfo(args, player);

					} else if (args[0].equalsIgnoreCase("course")) {
						if (!Utils.validateArgs(player, args, 2))
							return false;

						CourseMethods.displayCourseInfo(args[1], player);

					} else if (args[0].equalsIgnoreCase("lobby")) {
						CourseMethods.joinLobby(args, player);

					} else if (args[0].equalsIgnoreCase("setlobby")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						CourseMethods.createLobby(args, player);

					} else if (args[0].equalsIgnoreCase("setcreator")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.setCreator(args, player);

					} else if (args[0].equalsIgnoreCase("checkpoint")) {
						if (!PlayerMethods.hasSelected(player))
							return false;

						if (!Utils.hasPermissionOrOwnership(player, "Parkour.Admin", "Course", PlayerMethods.getSelected(player.getName())))
							return false;

						CheckpointMethods.createCheckpoint(args, player);

					} else if (args[0].equalsIgnoreCase("finish")) {
						if (!PlayerMethods.hasSelected(player))
							return false;

						if (!Utils.hasPermissionOrOwnership(player, "Parkour.Admin", "Course", PlayerMethods.getSelected(player.getName())))
							return false;

						CourseMethods.setFinish(args, player);

					} else if (args[0].equalsIgnoreCase("setstart")) {
						if (!PlayerMethods.hasSelected(player))
							return false;

						if (!Utils.hasPermissionOrOwnership(player, "Parkour.Admin", "Course", PlayerMethods.getSelected(player.getName())))
							return false;

						CourseMethods.setStart(args, player);

					} else if (args[0].equalsIgnoreCase("prize")) {
						if (!Utils.hasPermission(player, "Parkour.Admin", "Prize"))
							return false;
						
						if (!Utils.validateArgs(player, args, 2))
							return false;

						CourseMethods.setPrize(args, player);

					} else if (args[0].equalsIgnoreCase("like") || args[0].equalsIgnoreCase("dislike")) {
						CourseMethods.rateCourse(args, player);

					} else if (args[0].equalsIgnoreCase("perms")) {
						PlayerMethods.getPermissions(player);

					} else if (args[0].equalsIgnoreCase("kit")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "Kit"))
							return false;

						PlayerMethods.givePlayerKit(args, player);

					} else if (args[0].equalsIgnoreCase("delete")) {
						if (!Utils.hasPermission(player, "Parkour.Admin", "Delete"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						Utils.deleteCommand(args, player);

					} else if (args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("edit")) {
						if (!Utils.validateArgs(player, args, 2))
							return false;

						if (!Utils.hasPermissionOrOwnership(player, "Parkour.Admin", "Select", args[1]))
							return false;

						CourseMethods.selectCourse(args, player);

					} else if (args[0].equalsIgnoreCase("done") || args[0].equalsIgnoreCase("deselect") || args[0].equalsIgnoreCase("stopselect")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "Select"))
							return false;

						CourseMethods.deselectCourse(args, player);

					} else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "TP"))
							return false;

						if (!Utils.validateArgs(player, args, 2))
							return false;

						CheckpointMethods.teleportCheckpoint(args, player, false);

					} else if (args[0].equalsIgnoreCase("tpc")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "TPC"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						CheckpointMethods.teleportCheckpoint(args, player, true);

					} else if (args[0].equalsIgnoreCase("link")) {
						if (!PlayerMethods.hasSelected(player))
							return false;

						if (!Utils.hasPermissionOrOwnership(player, "Parkour.Admin", "Course", PlayerMethods.getSelected(player.getName())))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.linkCourse(args, player);

					} else if (args[0].equalsIgnoreCase("setminlevel")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.setMinLevel(args, player);

					} else if (args[0].equalsIgnoreCase("setmaxdeath")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.setMaxDeaths(args, player);

					} else if (args[0].equalsIgnoreCase("setjoinitem")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args, 4))
							return false;

						CourseMethods.setJoinItem(args, player);

					} else if (args[0].equalsIgnoreCase("rewardonce")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args, 2))
							return false;

						CourseMethods.setRewardOnce(args, player);

					} else if (args[0].equalsIgnoreCase("rewardlevel")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.setRewardLevel(args, player);

					} else if (args[0].equalsIgnoreCase("rewardrank")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.setRewardRank(args, player);

					} else if (args[0].equalsIgnoreCase("rewardparkoins")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.setRewardParkoins(args, player);

					} else if (args[0].equalsIgnoreCase("quiet")) {
						PlayerMethods.toggleQuiet(player);

					} else if (args[0].equalsIgnoreCase("reset")) {
						if (!Utils.hasPermission(player, "Parkour.Admin", "Reset"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						Utils.resetCommand(args, player);

					} else if (args[0].equalsIgnoreCase("test")) {
						if (!Utils.hasPermission(player, "Parkour.Admin", "Testmode"))
							return false;

						PlayerMethods.toggleTestmode(player);

					} else if (args[0].equalsIgnoreCase("economy") || args[0].equalsIgnoreCase("econ")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						Help.displayEconomy(args, player);

					} else if (args[0].equalsIgnoreCase("invite")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "Invite"))
							return false;

						if (!Utils.validateArgs(player, args, 2))
							return false;

						PlayerMethods.invitePlayer(args, player);
						
					} else if (args[0].equalsIgnoreCase("setmode")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;
						
						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.setCourseMode(args, player);
						
					} else if (args[0].equalsIgnoreCase("createparkourblocks") || args[0].equalsIgnoreCase("createpb")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						Utils.startConversation(player, ConversationType.PARKOURBLOCKS);

					} else if (args[0].equalsIgnoreCase("linkpb")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.linkParkourBlocks(args, player);

					} else if (args[0].equalsIgnoreCase("validatepb")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						Utils.validateParkourBlocks(args, player);

					} else if (args[0].equalsIgnoreCase("challenge")) {
						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.challengePlayer(args, player);

					} else if (args[0].equalsIgnoreCase("list")) {
						CourseMethods.displayList(args, player);

					} else if (args[0].equalsIgnoreCase("help")) {
						Help.lookupCommandHelp(args, player);

					} else if (args[0].equalsIgnoreCase("leaderboard")){
						if (!Utils.hasPermission(player, "Parkour.Basic", "Leaderboard"))
							return false;

						CourseMethods.getLeaderboards(args, player);
						
					} else if (args[0].equalsIgnoreCase("sql")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						Help.displaySQL(args, player);
						
					} else if (args[0].equalsIgnoreCase("recreate")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;
						
						player.sendMessage(Static.getParkourString() + "Recreating courses...");
						DatabaseMethods.recreateAllCourses();

						//Other commands//	
					} else if (args[0].equalsIgnoreCase("about")) {
						player.sendMessage(Static.getParkourString() + "Server is running Parkour " + ChatColor.GRAY + Static.getVersion());
						if (Static.getDevBuild())
							player.sendMessage(ChatColor.RED + "- You are running a development build -");
						player.sendMessage("This plugin was developed by " + ChatColor.GOLD + "A5H73Y");

					} else if (args[0].equalsIgnoreCase("contact")) {
						player.sendMessage(Static.getParkourString() + "For information or help please contact me:");
						player.sendMessage(" DevBukkit: " + ChatColor.AQUA + "A5H73Y");
						player.sendMessage(" Skype: " + ChatColor.AQUA + "iA5H73Y");
						player.sendMessage(" Parkour URL: " + ChatColor.AQUA + "http://dev.bukkit.org/server-mods/parkour/");

					} else if (args[0].equalsIgnoreCase("request") || args[0].equalsIgnoreCase("bug")) {
						player.sendMessage(Static.getParkourString() + "To Request a feature or to Report a bug...");
						player.sendMessage("Click here: " + ChatColor.DARK_AQUA + "http://dev.bukkit.org/server-mods/parkour/forum/");
					
					} else if (args[0].equalsIgnoreCase("tutorial")) {
						player.sendMessage(Static.getParkourString() + "Coming soon...");

					} else if (args[0].equalsIgnoreCase("settings")) {
						if (!Utils.hasPermission(player, "Parkour.Admin")) 
							return false;

						Help.displaySettings(player);

					} else if (args[0].equalsIgnoreCase("cmds")) {
						Help.processCommandsInput(args, player);

					} else if (args[0].equalsIgnoreCase("accept")) {
						PlayerMethods.acceptChallenge(player);

					} else if (args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("no")) {
						player.sendMessage(Static.getParkourString() + "You have not been asked a question!");

					} else if (args[0].equalsIgnoreCase("reload")){
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						Parkour.getParkourConfig().reload();
						Parkour.setSettings(new Settings());
						Static.initiate();
						player.sendMessage(Utils.getTranslation("Other.Reload"));
						Utils.logToFile(player.getName() + " reloaded the Parkour config");

					} else {
						player.sendMessage(Utils.getTranslation("Error.UnknownCommand"));
						player.sendMessage(Utils.getTranslation("Help.Commands", false));
					}

				} else {
					player.sendMessage(Static.getParkourString() + "Plugin proudly created by " + ChatColor.AQUA + "A5H73Y");
					player.sendMessage(Utils.getTranslation("Help.Commands", false));
				}

				// Console Commands ==========================================
			}else if(sender instanceof ConsoleCommandSender){
				if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("backup")) {
						Backup.backupNow();

					} else if (args[0].equalsIgnoreCase("setlevel")) {
						ParkourConsoleCommands.setLevel(args);

					} else if (args[0].equalsIgnoreCase("reload")) {
						Parkour.getParkourConfig().reload();
						Parkour.setSettings(new Settings());
						Static.initiate();
						Utils.log("Config reloaded!");

					} else if (args[0].equalsIgnoreCase("cmds")) {
						Utils.log("pa setlevel (player) (level) : Set a players Parkour Level");
						Utils.log("pa backup : Create a backup zip of the Parkour config folder");
						Utils.log("pa reload : Reload the Parkour config");
						
					} else if (args[0].equalsIgnoreCase("recreate")) {
						DatabaseMethods.recreateAllCourses();
						
					} else {
						Utils.log("Unknown Command. Enter 'pa cmds' to display all commands.");
					}
				}else{
					Utils.log("v" + Static.getVersion() + " installed. Plugin created by A5H73Y.");
				}
			} else {
				sender.sendMessage(Static.getParkourString() + "Unsupported sender.");
			}
		}
		return false;
	}
}
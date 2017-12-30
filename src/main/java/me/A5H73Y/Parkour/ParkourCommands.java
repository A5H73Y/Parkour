package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Conversation.ParkourConversation;

import me.A5H73Y.Parkour.Course.CheckpointMethods;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Course.LobbyMethods;
import me.A5H73Y.Parkour.Enums.ConversationType;
import me.A5H73Y.Parkour.Other.Help;
import me.A5H73Y.Parkour.Player.PlayerInfo;
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

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
class ParkourCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("parkour")) {
			// Player commands ============================================
			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (Parkour.getSettings().isCommandPermission() && !Utils.hasPermission(player, "Parkour.Basic", "Commands"))
					return false;

				if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("join")) {
						if (!Utils.validateArgs(player, args, 2))
							return false;

						if (!Parkour.getPlugin().getConfig().getBoolean("OnJoin.AllowViaCommand"))
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

					} else if (args[0].equalsIgnoreCase("stats")) {
						if (!Utils.validateArgs(player, args, 2))
							return false;

						CourseMethods.displayCourseInfo(args[1], player);

					} else if (args[0].equalsIgnoreCase("lobby")) {
						LobbyMethods.joinLobby(args, player);

					} else if (args[0].equalsIgnoreCase("setlobby")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						LobbyMethods.createLobby(args, player);

					} else if (args[0].equalsIgnoreCase("setcreator")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.setCreator(args, player);

					} else if (args[0].equalsIgnoreCase("checkpoint")) {
						if (!PlayerInfo.hasSelected(player))
							return false;

						if (!Utils.hasPermissionOrCourseOwnership(player, "Parkour.Admin", "Course", PlayerInfo.getSelected(player.getName())))
							return false;

						CheckpointMethods.createCheckpoint(args, player);

					} else if (args[0].equalsIgnoreCase("finish")) {
						CourseMethods.setFinish(args, player);

					} else if (args[0].equalsIgnoreCase("setstart")) {
						if (!PlayerInfo.hasSelected(player))
							return false;

						if (!Utils.hasPermissionOrCourseOwnership(player, "Parkour.Admin", "Course", PlayerInfo.getSelected(player.getName())))
							return false;

						CourseMethods.setStart(player);

					} else if (args[0].equalsIgnoreCase("prize")) {
						if (!Utils.hasPermission(player, "Parkour.Admin", "Prize"))
							return false;

						if (!Utils.validateArgs(player, args, 2))
							return false;

						CourseMethods.setPrize(args, player);

					} else if (args[0].equalsIgnoreCase("like") || args[0].equalsIgnoreCase("dislike")) {
						CourseMethods.rateCourse(args, player);

					} else if (args[0].equalsIgnoreCase("perms")) {
						PlayerMethods.displayPermissions(player);

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

						if (!Utils.hasPermissionOrCourseOwnership(player, "Parkour.Admin", "Course", args[1]))
							return false;

						CourseMethods.selectCourse(args, player);

					} else if (args[0].equalsIgnoreCase("done") || args[0].equalsIgnoreCase("deselect") || args[0].equalsIgnoreCase("stopselect")) {
						CourseMethods.deselectCourse(player);

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
						if (!PlayerInfo.hasSelected(player))
							return false;

						if (!Utils.hasPermissionOrCourseOwnership(player, "Parkour.Admin", "Course", PlayerInfo.getSelected(player.getName())))
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

					} else if (args[0].equalsIgnoreCase("rewardleveladd")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.setRewardLevelAdd(args, player);

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

						PlayerMethods.toggleTestmode(args, player);

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

						if (!Utils.validateArgs(player, args, 2))
							return false;

						CourseMethods.setCourseMode(args, player);

					} else if (args[0].equalsIgnoreCase("createparkourkit") || args[0].equalsIgnoreCase("createkit")) {
                        if (!Utils.hasPermission(player, "Parkour.Admin"))
                            return false;

                        new ParkourConversation(player, ConversationType.PARKOURKIT).begin();

                    } else if (args[0].equalsIgnoreCase("editparkourkit") || args[0].equalsIgnoreCase("editkit")) {
                        if (!Utils.hasPermission(player, "Parkour.Admin"))
                            return false;

                        new ParkourConversation(player, ConversationType.EDITPARKOURKIT).begin();

					} else if (args[0].equalsIgnoreCase("linkkit")) {
						if (!Utils.validateArgs(player, args, 3))
							return false;

						if (!Utils.hasPermissionOrCourseOwnership(player, "Parkour.Admin", "Course", args[1]))
							return false;
						
						CourseMethods.linkParkourKit(args, player);
						
					} else if (args[0].equalsIgnoreCase("listkit")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "Kit"))
							return false;

						Utils.listParkourKit(args, player);

					} else if (args[0].equalsIgnoreCase("validatekit")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						Utils.validateParkourKit(args, player);

					} else if (args[0].equalsIgnoreCase("challenge")) {
						if (!Utils.hasPermission(player, "Parkour.Basic", "Challenge"))
							return false;
						
						if (!Utils.validateArgs(player, args, 3))
							return false;

						CourseMethods.challengePlayer(args, player);

					} else if (args[0].equalsIgnoreCase("list")) {
						CourseMethods.displayList(args, player);

					} else if (args[0].equalsIgnoreCase("help")) {
						Help.lookupCommandHelp(args, player);

					} else if (args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("leaderboards")){
						if (!Utils.hasPermission(player, "Parkour.Basic", "Leaderboard"))
							return false;

						CourseMethods.getLeaderboards(args, player);

					} else if (args[0].equalsIgnoreCase("sql")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						Help.displaySQL(player);

					} else if (args[0].equalsIgnoreCase("recreate")) {
						if (!Utils.hasPermission(player, "Parkour.Admin"))
							return false;

						player.sendMessage(Static.getParkourString() + "Recreating courses...");
						DatabaseMethods.recreateAllCourses();

					} else if (args[0].equalsIgnoreCase("whitelist")) {
                        if (!Utils.hasPermission(player, "Parkour.Admin"))
                            return false;

                        if (!Utils.validateArgs(player, args, 2))
                            return false;

                        Utils.addWhitelistedCommand(args, player);

                    } else if (args[0].equalsIgnoreCase("setlevel")) {
                        if (!Utils.hasPermission(player, "Parkour.Admin"))
                            return false;

                        if (!Utils.validateArgs(player, args, 3))
                            return false;

                        PlayerMethods.setLevel(args, player);

                    } else if (args[0].equalsIgnoreCase("setrank")) {
                        if (!Utils.hasPermission(player, "Parkour.Admin"))
                            return false;

                        if (!Utils.validateArgs(player, args, 3))
                            return false;

                        PlayerMethods.setRank(args, player);

                    } else if (args[0].equalsIgnoreCase("material")) {
					    Utils.lookupMaterial(args, player);

						//Other commands//	
					} else if (args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("version")) {
						player.sendMessage(Static.getParkourString() + "Server is running Parkour " + ChatColor.GRAY + Static.getVersion());
						if (Static.getDevBuild())
							player.sendMessage(ChatColor.RED + "- You are running a development build -");
						player.sendMessage("This plugin was developed by " + ChatColor.GOLD + "A5H73Y");

					} else if (args[0].equalsIgnoreCase("contact")) {
						player.sendMessage(Static.getParkourString() + "For information or help please contact me:");
						player.sendMessage("Bukkit: " + ChatColor.AQUA + "A5H73Y");
						player.sendMessage("Spigot: " + ChatColor.AQUA + "A5H73Y");
						player.sendMessage("Skype: " + ChatColor.AQUA + "iA5H73Y");
						player.sendMessage("Parkour URL: " + ChatColor.AQUA + "http://dev.bukkit.org/projects/parkour/");

					} else if (args[0].equalsIgnoreCase("request") || args[0].equalsIgnoreCase("bug")) {
						player.sendMessage(Static.getParkourString() + "To Request a feature or to Report a bug...");
						player.sendMessage("Click here: " + ChatColor.DARK_AQUA + "https://github.com/A5H73Y/Parkour/issues");

					} else if (args[0].equalsIgnoreCase("tutorial")) {
						player.sendMessage(Static.getParkourString() + "To follow the official Parkour tutorials...");
						player.sendMessage("Click here: " + ChatColor.DARK_AQUA + "https://dev.bukkit.org/projects/parkour/pages/tutorial");

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

					} else if (args[0].equalsIgnoreCase("reload")) {
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
			} else if (sender instanceof ConsoleCommandSender) {
				if (args.length >= 1) {
					
					if (args[0].equalsIgnoreCase("reload")) {
						ParkourConsoleCommands.reloadConfig();

					} else if (args[0].equalsIgnoreCase("recreate")) {
						ParkourConsoleCommands.recreateCourses();
						
					} else if (args[0].equalsIgnoreCase("setminlevel")) {
						ParkourConsoleCommands.setCourseMinimumlevel(args, sender);
						
					} else if (args[0].equalsIgnoreCase("setmaxdeath")) {
						ParkourConsoleCommands.setCourseMaximumDeath(args, sender);
						
					} else if (args[0].equalsIgnoreCase("setjoinitem")) {
						ParkourConsoleCommands.setCourseJoinItem(args, sender);
					
					} else if (args[0].equalsIgnoreCase("rewardonce")) {
						ParkourConsoleCommands.setCourseToRewardOnce(args, sender);
						
					} else if (args[0].equalsIgnoreCase("rewardlevel")) {
						ParkourConsoleCommands.setRewardLevel(args, sender);
						
					} else if (args[0].equalsIgnoreCase("rewardleveladd")) {
						ParkourConsoleCommands.setRewardLevelAdd(args, sender);
						
					} else if (args[0].equalsIgnoreCase("rewardrank")) {
						ParkourConsoleCommands.setRewardRank(args, sender);
						
					} else if (args[0].equalsIgnoreCase("rewardparkoins")) {
                        ParkourConsoleCommands.setRewardParkoins(args, sender);

                    } else if (args[0].equalsIgnoreCase("setlevel")) {
                        ParkourConsoleCommands.setPlayerLevel(args, sender);

                    } else if (args[0].equalsIgnoreCase("setrank")) {
					    ParkourConsoleCommands.setPlayerRank(args, sender);

					} else if (args[0].equalsIgnoreCase("list")) {
						ParkourConsoleCommands.displayList(args, sender);
						
					} else if (args[0].equalsIgnoreCase("listkit")) {
						ParkourConsoleCommands.displayParkourKit(args, sender);
						
					} else if (args[0].equalsIgnoreCase("settings")) {
						ParkourConsoleCommands.displaySettings(sender);
						
					} else if (args[0].equalsIgnoreCase("help")) {
						ParkourConsoleCommands.displayHelp(args, sender);

					} else if (args[0].equalsIgnoreCase("cmds")) {
						ParkourConsoleCommands.displayCommands();
						
					} else if (args[0].equalsIgnoreCase("backup")) {
						ParkourConsoleCommands.startBackup();

					} else {
						Utils.log("Unknown Command. Enter 'pa cmds' to display all console commands.");
					}
				} else {
					Utils.log("v" + Static.getVersion() + " installed. Plugin created by A5H73Y.");
					Utils.log("Enter 'pa cmds' to display all console commands.");
				}
			} else {
				sender.sendMessage(Static.getParkourString() + "Unsupported sender.");
			}
		}
		return false;
	}
}
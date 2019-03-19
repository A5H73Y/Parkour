package me.A5H73Y.Parkour.Commands;

import me.A5H73Y.Parkour.Course.CourseInfo;
import me.A5H73Y.Parkour.ParkourKit.ParkourKitInfo;
import me.A5H73Y.Parkour.Player.PlayerInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class ParkourAutoTabCompleter implements TabCompleter {

    private static final Set<String> basicCmds = new HashSet<>(
            Arrays.asList("challenge", "leaderboard", "invite", "kit", "listkit", "tp", "tpc"));

    private static final Set<String> adminCmds = new HashSet<>(
            Arrays.asList("setstart", "setlobby", "economy", "createkit", "editkit", "validatekit", "recreate",
            "sql", "settings", "reload", "rewardrank", "whitelist", "setlevel", "setrank", "delete"));

    @Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		
		Set<String> list = new HashSet<>();
		Set<String> auto = new HashSet<>();
		Set<String> courseCmds = getCourseCmds(sender);
		
		if (args.length == 1) {
			if(args[0].indexOf("help") == 0) {
				list.add("help");
			}
			if(args[0].indexOf("info") == 0) {
				list.add("info");
			}
			if(args[0].indexOf("contact") == 0) {
				list.add("contact");
			}
			if(args[0].indexOf("about") == 0) {
				list.add("about");
			}
			if(args[0].indexOf("version") == 0) {
				list.add("version");
			}
			if(args[0].indexOf("material") == 0) {
				list.add("material");
			}
			if(args[0].indexOf("quiet") == 0) {
				list.add("quiet");
			}
			if(args[0].indexOf("like") == 0) {
				list.add("like");
			}
			if(args[0].indexOf("dislike") == 0) {
				list.add("dislike");
			}
			if(args[0].indexOf("list") == 0) {
				list.add("list");
			}
			if(args[0].indexOf("lobby") == 0) {
				list.add("lobby");
			}
			if(args[0].indexOf("perms") == 0) {
				list.add("perms");
			}
			if(args[0].indexOf("leave") == 0) {	
				list.add("leave");
			}
			if(args[0].indexOf("done") == 0) {	
				list.add("done");
			}
			if(args[0].indexOf("tutorial") == 0) {
				list.add("tutorial");
			}
			if(args[0].indexOf("request") == 0) {
				list.add("request");
			}
			if(args[0].indexOf("accept") == 0) {
				list.add("accept");
			}
			if(args[0].indexOf("bug") == 0) {
				list.add("bug");
			}
			if(args[0].indexOf("cmds") == 0) {
				list.add("cmds");
			}
			
			if (sender.hasPermission("Parkour.Basic.*") || sender.hasPermission("Parkour.*")) {
				list.addAll(basicCmds);
			} else {
				if (sender.hasPermission("Parkour.Basic.Create")) {
					list.add("create");
				}
				if (sender.hasPermission("Parkour.Basic.Invite")) {
					list.add("invite");
				}
				if (sender.hasPermission("Parkour.Basic.Kit")) {
					list.add("kit");
					list.add("listkit");
				}
				if (sender.hasPermission("Parkour.Basic.Challenge")) {
					list.add("challenge");
				}
				if (sender.hasPermission("Parkour.Basic.TPC")) {
					list.add("tpc");
				}
				if (sender.hasPermission("Parkour.Basic.TP")) {
					list.add("tp");
				}
				if (sender.hasPermission("Parkour.Basic.Leaderboard")) {
					list.add("leaderboard");
				}
			}
			
			if (sender.hasPermission("Parkour.Admin") || sender.hasPermission("Parkour.*")) {
				list.addAll(adminCmds);
			}
			if (sender.hasPermission("Parkour.Admin.*") || sender.hasPermission("Parkour.*")) {
				list.add("test");
				list.add("reset");
				list.add("delete");
				list.add("checkpoint");
				list.add("link");
			} else {
				if (sender.hasPermission("Parkour.Admin.Testmode")) {
					list.add("test");
				}
				if (sender.hasPermission("Parkour.Admin.Reset")) {
					list.add("reset");
				}
				if (sender.hasPermission("Parkour.Admin.Delete")) {
					list.add("delete");
				}
				if (sender.hasPermission("Parkour.Admin.Course") || isSelectedCourseOwner(sender)) {
					list.add("checkpoint");
					list.add("link");
				}
			}
			
			list.addAll(courseCmds);
			
		} else if (args.length == 2) {
			if (courseCmds.contains(args[0])) {
                list.addAll(CourseInfo.getAllCourses());
			} else if (args[0].equalsIgnoreCase("list")) {
				list.add("courses");
				list.add("players");
				list.add("ranks");
				list.add("lobbies");
			} else if (args[0].equalsIgnoreCase("delete")) {
				list.add("course");
				list.add("checkpoint");
				list.add("lobby");
				list.add("kit");
			} else if (args[0].equalsIgnoreCase("kit") || args[0].equalsIgnoreCase("listkit") || args[0].equalsIgnoreCase("validatekit")) {
				list.addAll(ParkourKitInfo.getParkourKitNames());
			} else if (args[0].equalsIgnoreCase("reset")) {
				list.add("course");
				list.add("player");
				list.add("leaderboard");
				list.add("prize");
			}
		} else if (args.length == 3) {
			if ((args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("delete")) && args[1].equalsIgnoreCase("course")) {
				 list.addAll(CourseInfo.getAllCourses());
			} else if ((args[0].equalsIgnoreCase("delete") && args[1].equalsIgnoreCase("kit")) || args[0].equalsIgnoreCase("linkkit")) {
				list.addAll(ParkourKitInfo.getParkourKitNames());
			}
		}
		
		for (String s : list) {
			if (s.startsWith(args[args.length - 1])) {
				auto.add(s);
			}
		}
		
		return auto.isEmpty() ? new ArrayList<>(list) : new ArrayList<>(auto);
	}
	
	private Set<String> getCourseCmds(CommandSender sender) {
		Set<String> cmds = new HashSet<>();
		Set<String> adminCourseCmds = new HashSet<>(Arrays.asList("setcreator", "prize", "setmode", "setjoinitem", "setminlevel",
				"setmaxdeath", "setmaxtime", "rewardonce", "rewardlevel", "rewardleveladd", "rewardparkoins", "rewarddelay"));
		
		cmds.add("join");
		cmds.add("joinall");
		cmds.add("stats");
		cmds.add("course");
		cmds.add("select"); // so course owner can select own course
		
		if (sender.hasPermission("Parkour.Basic.*") || sender.hasPermission("Parkour.*")) {
			cmds.add("challenge");
			cmds.add("tp");
			cmds.add("tpc");
			cmds.add("leaderboard");
		} else {
			if (sender.hasPermission("Parkour.Basic.Challenge")) {
				cmds.add("challenge");
			}
			if (sender.hasPermission("Parkour.Basic.TP")) {
				cmds.add("tp");
			}
			if (sender.hasPermission("Parkour.Basic.TPC")) {
				cmds.add("tpc");
			}
			if (sender.hasPermission("Parkour.Basic.Leaderboard")) {
				cmds.add("leaderboard");
			}
		}
		
		if (sender.hasPermission("Parkour.Admin") || sender.hasPermission("Parkour.*")) {
			cmds.addAll(adminCourseCmds);
		}
		if (sender.hasPermission("Parkour.Admin.*") || sender.hasPermission("Parkour.*") || sender.hasPermission("Parkour.Admin.Course") || isSelectedCourseOwner(sender)) {
			cmds.add("edit");
			cmds.add("linkkit");
			cmds.add("setautostart");
			cmds.add("finish");
		} 
		if (sender.hasPermission("Parkour.Admin.*") || sender.hasPermission("Parkour.Admin.Prize") || sender.hasPermission("Parkour.*")) {
			cmds.add("prize");
		}
		return cmds;
	}
	
	private boolean isSelectedCourseOwner(CommandSender sender) {
		Player player = (Player) sender;
		String courseName = PlayerInfo.getSelected(player);
		if (courseName == null) {
			return false;
		}
		return player.getName().equals(CourseInfo.getCreator(courseName));
	}
}

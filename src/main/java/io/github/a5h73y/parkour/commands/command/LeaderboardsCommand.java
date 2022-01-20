package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardsCommand extends BasicParkourCommand {

	public LeaderboardsCommand(Parkour parkour) {
		super(parkour, AllowedSender.ANY, "leaderboard", "leaderboards");
	}

	@Override
	public Permission getRequiredPermission() {
		return Permission.BASIC_LEADERBOARD;
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		return commandSender instanceof Player || !ValidationUtils.validateArgs(commandSender, args, 3);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player) {
			parkour.getCourseManager().displayLeaderboards((Player) commandSender, args);
		} else {
			parkour.getDatabaseManager().displayTimeEntries(commandSender, args[1],
					parkour.getDatabaseManager().getTopCourseResults(args[1], Integer.parseInt(args[2])));;
		}
	}
}

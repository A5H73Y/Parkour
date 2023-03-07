package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardCommand extends BasicParkourCommand {

	public LeaderboardCommand(Parkour parkour) {
		super(parkour, "leaderboard",
				AllowedCommandSender.ANY,
				"leaderboards");
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.BASIC_LEADERBOARD;
	}

	@Override
	public boolean validateConsoleArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 3);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getCourseManager().displayLeaderboards((Player) commandSender, args);
	}

	@Override
	public void performConsoleAction(CommandSender commandSender, String[] args) {
		parkour.getDatabaseManager().displayTimeEntries(commandSender, args[1],
				parkour.getDatabaseManager().getTopCourseResults(args[1], Integer.parseInt(args[2])));
	}
}

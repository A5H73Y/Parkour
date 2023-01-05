package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends BasicParkourCommand {

	public JoinCommand(Parkour parkour) {
		super(parkour, "join",
				AllowedCommandSender.ANY);
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADMIN_ALL;
	}

	@Override
	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2);
	}

	@Override
	public boolean validateConsoleArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 3)
				&& findPlayer(commandSender, args[2]) != null;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getPlayerManager().joinCourse((Player) commandSender, args[1]);
	}

	@Override
	public void performConsoleAction(CommandSender commandSender, String[] args) {
		parkour.getPlayerManager().joinCourse(findPlayer(commandSender, args[2]), args[1]);
	}
}

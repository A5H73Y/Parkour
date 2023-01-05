package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;

public class FinishCommand extends BasicParkourCommand {

	public FinishCommand(Parkour parkour) {
		super(parkour, "finish",
				AllowedCommandSender.CONSOLE);
	}

	@Override
	public boolean validateConsoleArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2)
				&& findPlayer(commandSender, args[1]) != null;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getPlayerManager().finishCourse(findPlayer(commandSender, args[1]));
	}
}

package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;

public class ConfigCommand extends BasicParkourCommand {

	public ConfigCommand(Parkour parkour) {
		super(parkour, "config",
				AllowedCommandSender.ANY);
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.PARKOUR_ALL;
	}

	@Override
	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		return !ValidationUtils.validateArgs(commandSender, args, 2);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (!args[1].startsWith("MySQL")) {
			TranslationUtils.sendValue(commandSender, args[1], parkour.getParkourConfig().getString(args[1]));
		}
	}
}

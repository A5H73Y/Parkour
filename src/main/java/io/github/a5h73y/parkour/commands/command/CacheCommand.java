package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;

public class CacheCommand extends BasicParkourCommand {

	public CacheCommand(Parkour parkour) {
		super(parkour, "cache",
				AllowedCommandSender.ANY);
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADMIN_ALL;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getAdministrationManager().processCacheCommand(commandSender,
				args.length == 2 ? args[1] : null);
	}
}

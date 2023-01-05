package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;

public class AdministratorCommand extends BasicParkourCommand {

	public AdministratorCommand(Parkour parkour) {
		super(parkour, "admin",
				AllowedCommandSender.ANY,
				"administrator");
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADMIN_ALL;
	}

	@Override
	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 3);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getAdministrationManager().processAdminCommand(commandSender, args[1], args[2]);
	}
}

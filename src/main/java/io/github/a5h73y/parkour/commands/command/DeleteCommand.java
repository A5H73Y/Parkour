package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;

public class DeleteCommand extends BasicParkourCommand {

	public DeleteCommand(Parkour parkour) {
		super(parkour, "delete",
				AllowedCommandSender.ANY);
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADMIN_DELETE;
	}

	@Override
	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 3, 4);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getAdministrationManager().processDeleteCommand(
				commandSender, args[1], args[2], args.length > 3 ? args[3] : null);
	}
}

package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;

public class SetCourseCommand extends BasicParkourCommand {

	public SetCourseCommand(Parkour parkour) {
		super(parkour, AllowedSender.ANY, "setcourse");
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADMIN_ALL;
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2, 100);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getCourseSettingsManager().processSetCommand(commandSender, args);
	}
}

package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoStartCreateCommand extends BasicParkourCommand {

	public AutoStartCreateCommand(Parkour parkour) {
		super(parkour, AllowedSender.PLAYER, "setautostart");
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.BASIC_CREATE;
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		return PermissionUtils.hasPermissionOrCourseOwnership((Player) commandSender,
				Permission.ADMIN_COURSE, getChosenCourseName((Player) commandSender, args, 1));
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getAutoStartManager().createAutoStart((Player) commandSender,
				getChosenCourseName((Player) commandSender, args, 1));
	}
}

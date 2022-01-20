package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CourseSettingsCommand extends BasicParkourCommand {

	public CourseSettingsCommand(Parkour parkour) {
		super(parkour, AllowedSender.PLAYER, "settings");
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		return !PermissionUtils.hasPermissionOrCourseOwnership((Player) commandSender,
				Permission.ADMIN_COURSE, getChosenCourseName((Player) commandSender, args, 1));
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player) {
			parkour.getCourseManager().displaySettingsGui(
					(Player) commandSender, getChosenCourseName((Player) commandSender, args, 1));
		}
	}
}

package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CourseSelectCommand extends BasicParkourCommand {

	public CourseSelectCommand(Parkour parkour) {
		super(parkour, AllowedSender.PLAYER, "select", "edit");
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		if (!ValidationUtils.validateArgs(commandSender, args, 2)) {
			return false;

		} else return PermissionUtils.hasPermissionOrCourseOwnership(
				(Player) commandSender, Permission.ADMIN_COURSE, args[1]);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player) {
			parkour.getPlayerManager().selectCourse((Player) commandSender, args[1]);
		}
	}
}

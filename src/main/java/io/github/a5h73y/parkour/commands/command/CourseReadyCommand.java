package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CourseReadyCommand extends BasicParkourCommand {

	public CourseReadyCommand(Parkour parkour) {
		super(parkour, AllowedSender.ANY, "ready", "finish");
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		return args.length > 1 || (commandSender instanceof Player && PermissionUtils.hasPermissionOrCourseOwnership((Player) commandSender,
				Permission.ADMIN_COURSE, getChosenCourseName((Player) commandSender, args, 1)));
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		String courseName;

		if (commandSender instanceof Player) {
			courseName = getChosenCourseName((Player) commandSender, args, 1);
		} else {
			courseName = args[1];
		}

		boolean readyStatus = CourseConfig.getConfig(courseName).getReadyStatus();
		parkour.getCourseSettingsManager().setReadyStatus(commandSender, courseName, !readyStatus);
	}
}

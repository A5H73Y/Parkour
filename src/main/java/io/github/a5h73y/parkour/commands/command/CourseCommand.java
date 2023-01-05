package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;

public class CourseCommand extends BasicParkourCommand {

	public CourseCommand(Parkour parkour) {
		super(parkour, "course",
				AllowedCommandSender.ANY,
				"stats");
	}

	@Override
	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		CourseConfig.displayCourseInfo(commandSender, args[1]);
	}
}

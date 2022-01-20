package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CourseCreateCommand extends BasicParkourCommand {

	public CourseCreateCommand(Parkour parkour) {
		super(parkour, AllowedSender.PLAYER, "create");
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.BASIC_CREATE;
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player) {
			parkour.getCourseManager().createCourse((Player) commandSender, args[1]);
		}
	}
}

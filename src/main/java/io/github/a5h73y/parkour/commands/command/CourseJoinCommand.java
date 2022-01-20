package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CourseJoinCommand extends BasicParkourCommand {

	public CourseJoinCommand(Parkour parkour) {
		super(parkour, AllowedSender.PLAYER, "join");
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player) {
			parkour.getPlayerManager().joinCourse((Player) commandSender, args[1]);
		}
	}
}

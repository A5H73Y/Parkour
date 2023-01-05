package io.github.a5h73y.parkour.commands.command;

import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_INVALID_AMOUNT;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckpointCommand extends BasicParkourCommand {

	public CheckpointCommand(Parkour parkour) {
		super(parkour, "checkpoint",
				AllowedCommandSender.PLAYER);
	}

	@Override
	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		if (!ValidationUtils.validateArgs(commandSender, args, 2, 3)) {
			return false;

		} else if (!PermissionUtils.hasPermissionOrCourseOwnership(
				(Player) commandSender, Permission.ADMIN_COURSE, args[1])) {
			return false;

		} else if (args.length == 3 && !ValidationUtils.isPositiveInteger(args[2])) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return false;
		}

		return true;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getCheckpointManager().createCheckpoint((Player) commandSender,
				args[1], args.length == 3 ? Integer.parseInt(args[2]) : null);
	}
}

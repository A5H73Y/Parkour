package io.github.a5h73y.parkour.commands.command;

import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_INVALID_AMOUNT;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckpointCommand extends BasicParkourCommand {

	public CheckpointCommand(Parkour parkour) {
		super(parkour, AllowedSender.PLAYER, "setcheckpoint");
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		Player player = (Player) commandSender;

		if (!parkour.getPlayerManager().hasSelectedValidCourse(player)) {
			TranslationUtils.sendTranslation("Error.Selected", player);
			return false;

		} else if (!PermissionUtils.hasPermissionOrCourseOwnership(
				player, Permission.ADMIN_COURSE, PlayerConfig.getConfig(player).getSelectedCourse())) {
			return false;

		} else if (args.length == 2 && !ValidationUtils.isPositiveInteger(args[1])) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, player);
			return false;
		}

		return true;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player) {
			parkour.getCheckpointManager().createCheckpoint(
					(Player) commandSender, args.length == 2 ? Integer.parseInt(args[1]) : null);
		}
	}
}

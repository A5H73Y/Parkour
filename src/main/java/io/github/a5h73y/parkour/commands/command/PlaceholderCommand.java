package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaceholderCommand extends BasicParkourCommand {

	public PlaceholderCommand(Parkour parkour) {
		super(parkour, AllowedSender.ANY, "placeholder");
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADMIN_ALL;
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2, 3);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		OfflinePlayer targetPlayer = null;

		if (args.length == 2 && commandSender instanceof Player) {
			targetPlayer = (OfflinePlayer) commandSender;
		} else if (args.length == 3) {
			targetPlayer = Bukkit.getOfflinePlayer(args[2]);
		}

		parkour.getPlaceholderApi().evaluatePlaceholder(commandSender, targetPlayer, args[1]);
	}
}

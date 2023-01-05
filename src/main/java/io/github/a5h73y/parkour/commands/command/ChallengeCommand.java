package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChallengeCommand extends BasicParkourCommand {

	public ChallengeCommand(Parkour parkour) {
		super(parkour, "challenge",
				AllowedCommandSender.PLAYER);
	}

	@Override
	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2, 10);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getChallengeManager().processCommand((Player) commandSender, args);
	}
}

package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdsCommand extends BasicParkourCommand {

	public CmdsCommand(Parkour parkour) {
		super(parkour, "cmds", AllowedCommandSender.ANY);
	}

	@Override
	public boolean validateCommand(CommandSender commandSender, String[] args) {
		return true;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getParkourCommands().processListCommands((Player) commandSender, args);
	}

	@Override
	public void performConsoleAction(CommandSender commandSender, String[] args) {
		parkour.getParkourCommands().displayConsoleCommands(commandSender);
	}
}

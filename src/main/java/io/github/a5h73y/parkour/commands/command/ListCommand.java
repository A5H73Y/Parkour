package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import org.bukkit.command.CommandSender;

public class ListCommand extends BasicParkourCommand {

	public ListCommand(Parkour parkour) {
		super(parkour, "list",
				AllowedCommandSender.ANY);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getAdministrationManager().processListCommand(commandSender, args);
	}
}

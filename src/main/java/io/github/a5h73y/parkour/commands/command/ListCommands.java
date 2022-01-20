package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommands extends BasicParkourCommand {

	public ListCommands(Parkour parkour) {
		super(parkour, AllowedSender.ANY, "cmds", "commands");
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player) {
			parkour.getParkourCommands().processListCommands((Player) commandSender, args);
		} else {
			parkour.getParkourCommands().displayConsoleCommands(commandSender);
		}
	}
}

package io.github.a5h73y.parkour.commands.type;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface ParkourCommand {
	void executeCommand(CommandSender commandSender, String[] args);
}

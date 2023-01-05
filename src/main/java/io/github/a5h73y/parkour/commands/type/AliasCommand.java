package io.github.a5h73y.parkour.commands.type;

import io.github.a5h73y.parkour.Parkour;
import java.util.function.BiConsumer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class ConsoleOnlyCommand extends AbstractParkourCommand {

	final BiConsumer<CommandSender, String[]> boop;

	public ConsoleOnlyCommand(@NotNull Parkour parkour,
	                          @NotNull String commandName,
	                          @NotNull BiConsumer<CommandSender, String[]> boop) {
		super(parkour, commandName);
		this.boop = boop;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (commandSender instanceof ConsoleCommandSender) {
			boop.accept(commandSender, args);
		} else {
			// do something a bit better
		}
	}
}

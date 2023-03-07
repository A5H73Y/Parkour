package io.github.a5h73y.parkour.commands.type;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.function.BiConsumer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class ConsoleOnlyCommand extends AbstractParkourCommand {

	final BiConsumer<CommandSender, String[]> commandConsumer;

	public ConsoleOnlyCommand(@NotNull Parkour parkour,
	                          @NotNull String commandName,
	                          @NotNull BiConsumer<CommandSender, String[]> commandConsumer) {
		super(parkour, commandName);
		this.commandConsumer = commandConsumer;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (commandSender instanceof ConsoleCommandSender) {
			commandConsumer.accept(commandSender, args);
		} else {
			TranslationUtils.sendTranslation("Error.ConsoleOnlyCommand", commandSender);
		}
	}
}

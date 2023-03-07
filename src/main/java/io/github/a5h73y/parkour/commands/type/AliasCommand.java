package io.github.a5h73y.parkour.commands.type;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AliasCommand extends AbstractParkourCommand {

	private final int minimumArgs;
	private final Function<String[], String> commandFunction;

	public AliasCommand(@NotNull Parkour parkour,
	                    @NotNull String commandName,
	                    int minimumArgs,
	                    @NotNull Function<String[], String> commandFunction) {
		super(parkour, commandName);
		this.minimumArgs = minimumArgs;
		this.commandFunction = commandFunction;
	}

	@Override
	public boolean validateCommand(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, minimumArgs, 99);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getParkourCommands().executeCommand(args[0].toLowerCase(), commandSender,
				this.commandFunction.apply(args).split(" "));
	}
}

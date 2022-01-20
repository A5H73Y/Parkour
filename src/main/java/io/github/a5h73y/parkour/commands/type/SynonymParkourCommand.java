package io.github.a5h73y.parkour.commands.type;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import org.bukkit.command.CommandSender;

public abstract class SynonymParkourCommand extends AbstractParkourCommand {

	private final int minimumArgs;

	public SynonymParkourCommand(Parkour parkour, int minimumArgs) {
		super(parkour);
		this.minimumArgs = minimumArgs;
	}

	@Override
	public boolean validateCommand(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, minimumArgs);
	}
}

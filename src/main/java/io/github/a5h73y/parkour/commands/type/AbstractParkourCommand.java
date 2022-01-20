package io.github.a5h73y.parkour.commands.type;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import org.bukkit.command.CommandSender;

public abstract class AbstractParkourCommand extends AbstractPluginReceiver {

	protected AbstractParkourCommand(Parkour parkour) {
		super(parkour);
	}

	public boolean validateCommand(CommandSender commandSender, String[] args) {
		return true;
	}

	public void executeCommand(CommandSender commandSender, String[] args) {
		if (validateCommand(commandSender, args)) {
			performAction(commandSender, args);
		}
	}

	public abstract void performAction(CommandSender commandSender, String[] args);
}

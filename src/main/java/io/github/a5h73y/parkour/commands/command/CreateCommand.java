package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.conversation.parkourkit.CreateParkourKitConversation;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

public class CreateCommand extends BasicParkourCommand {

	public CreateCommand(Parkour parkour) {
		super(parkour, "create",
				AllowedCommandSender.ANY,
				"createkit");
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.BASIC_CREATE;
	}

	@Override
	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2, 5);
	}

	@Override
	public boolean validateConsoleArguments(CommandSender commandSender, String[] args) {
		return true;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
			parkour.getAdministrationManager().processCreateCommand((Player) commandSender,
					args[1],
					args.length > 2 ? args[2] : null,
					args.length > 3 ? args[3] : null);
	}

	@Override
	public void performConsoleAction(CommandSender commandSender, String[] args) {
		// console command can only create kits, everything else requires Player
		new CreateParkourKitConversation((Conversable) commandSender).begin();
	}
}

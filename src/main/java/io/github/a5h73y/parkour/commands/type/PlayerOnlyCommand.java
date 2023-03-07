package io.github.a5h73y.parkour.commands.type;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import java.util.function.BiConsumer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerOnlyCommand extends ConsoleOnlyCommand {

	private final Permission requiredPermission;

	public PlayerOnlyCommand(@NotNull Parkour parkour,
	                         @NotNull String commandName,
	                         @Nullable Permission requiredPermission,
	                         @NotNull BiConsumer<CommandSender, String[]> commandConsumer,) {
		super(parkour, commandName, commandConsumer);
		this.requiredPermission = requiredPermission;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player) {
			commandConsumer.accept(commandSender, args);
		} else {
			TranslationUtils.sendTranslation("Error.PlayerOnlyCommand", commandSender);
		}
	}

	@Override
	public boolean validateCommand(CommandSender commandSender, String[] args) {
		return this.requiredPermission == null
				|| PermissionUtils.hasPermission(commandSender, this.requiredPermission);
	}
}

package io.github.a5h73y.parkour.commands.type;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BasicParkourCommand extends AbstractParkourCommand {

	private final String[] aliases;
	private final AllowedCommandSender allowedSender;

	protected BasicParkourCommand(@NotNull Parkour parkour,
	                              @NotNull String commandName,
	                              @NotNull AllowedCommandSender allowedSender,
	                              @Nullable String... aliases) {
		super(parkour, commandName);
		this.aliases = aliases;
		this.allowedSender = allowedSender;
	}

	@Override
	public boolean validateCommand(CommandSender commandSender, String[] args) {
		if (!isCommandEnabled(commandSender)) {
			TranslationUtils.sendMessage(commandSender, "This Command has been disabled.");
			return false;
		}

		if (!isValidSender(commandSender)) {
			TranslationUtils.sendMessage(commandSender, "You are not able to perform this command.");
			return false;
		}

		if (!hasPermission(commandSender)) {
			return false;
		}

		return validateArguments(commandSender, args);
	}

	@Override
	public void executeCommand(CommandSender commandSender, String[] args) {
		if (validateCommand(commandSender, args)) {
			if (commandSender instanceof Player) {
				performAction(commandSender, args);
			} else {
				performConsoleAction(commandSender, args);
			}
		}
	}

	public void performConsoleAction(CommandSender commandSender, String[] args) {
		this.performAction(commandSender, args);
	}

	protected boolean validateArguments(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player) {
			return validatePlayerArguments(commandSender, args);
		} else {
			return validateConsoleArguments(commandSender, args);
		}
	}

	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		return true;
	}

	public boolean validateConsoleArguments(CommandSender commandSender, String[] args) {
		return validatePlayerArguments(commandSender, args);
	}

	protected Permission getRequiredPermission() {
		return null;
	}

	private boolean isCommandEnabled(CommandSender commandSender) {
		return commandSender.isOp() ||
				parkour.getParkourConfig().getOrDefault("Command." + getCommandName().toLowerCase() + ".Enabled", true);
	}

	private boolean isValidSender(CommandSender commandSender) {
		return allowedSender == AllowedCommandSender.ANY
				|| ((commandSender instanceof ConsoleCommandSender)
				&& allowedSender == AllowedCommandSender.CONSOLE)
				|| ((commandSender instanceof Player)
				&& allowedSender == AllowedCommandSender.PLAYER);
	}

	private boolean hasPermission(CommandSender commandSender) {
		return getRequiredPermission() == null
				|| commandSender instanceof ConsoleCommandSender
				|| PermissionUtils.hasPermission(commandSender, getRequiredPermission());
	}

	@Nullable
	public String[] getAliases() {
		return aliases;
	}

	@NotNull
	public AllowedCommandSender getAllowedSender() {
		return allowedSender;
	}

	/**
	 * Attempt to find matching Player by name.
	 *
	 * @param commandSender command sender
	 * @param playerName target player name
	 * @return {@link Player}
	 */
	@Nullable
	protected Player findPlayer(CommandSender commandSender, String playerName) {
		Player targetPlayer = Bukkit.getPlayer(playerName);

		if (targetPlayer == null) {
			TranslationUtils.sendMessage(commandSender, "Player is not online");
		}
		return targetPlayer;
	}
}

package io.github.a5h73y.parkour.commands.type;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class BasicParkourCommand extends AbstractParkourCommand {

	private final String[] commandLabels;
	private final AllowedSender allowedSender;

	protected BasicParkourCommand(Parkour parkour, AllowedSender allowedSender, String... commandLabels) {
		super(parkour);
		this.commandLabels = commandLabels;
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

		return hasValidArguments(commandSender, args);
	}

	protected Permission getRequiredPermission() {
		return null;
	}

	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		return true;
	}

	private boolean isCommandEnabled(CommandSender commandSender) {
		return commandSender.isOp() ||
				parkour.getParkourConfig().getOrDefault("Command." + commandLabels[0].toLowerCase() + ".Enabled", true);
	}

	private boolean isValidSender(CommandSender commandSender) {
		return allowedSender == AllowedSender.ANY
				|| ((commandSender instanceof ConsoleCommandSender) && allowedSender == AllowedSender.CONSOLE)
				|| ((commandSender instanceof Player) && allowedSender == AllowedSender.PLAYER);
	}

	private boolean hasPermission(CommandSender commandSender) {
		return getRequiredPermission() == null
				|| commandSender instanceof ConsoleCommandSender
				|| PermissionUtils.hasPermission(commandSender, getRequiredPermission());
	}

	public String[] getCommandLabels() {
		return commandLabels;
	}

	public AllowedSender getAllowedSender() {
		return allowedSender;
	}

	/**
	 * Get the Player's chosen course.
	 * If they have provided a course parameter, use that.
	 * Otherwise fallback to the player's selected course.
	 *
	 * @param player player
	 * @param args command args
	 * @param courseArg position of course argument
	 * @return chosen course name
	 */
	protected String getChosenCourseName(Player player, String[] args, int courseArg) {
		return args.length != courseArg + 1 ? PlayerConfig.getConfig(player).getSelectedCourse() : args[courseArg];
	}

	// TODO - come up with better name
	public enum AllowedSender {
		PLAYER, CONSOLE, ANY
	}
}

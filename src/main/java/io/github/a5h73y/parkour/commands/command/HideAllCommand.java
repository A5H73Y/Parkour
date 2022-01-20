package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideAllCommand extends BasicParkourCommand {

	public HideAllCommand(Parkour parkour) {
		super(parkour, AllowedSender.PLAYER, "hideall");
	}

	@Override
	protected boolean hasValidArguments(CommandSender commandSender, String[] args) {
		if (!parkour.getParkourSessionManager().isPlaying((Player) commandSender)) {
			TranslationUtils.sendTranslation("Error.NotOnCourse", commandSender);
			return false;
		}

		return true;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player) {
			parkour.getPlayerManager().toggleVisibility((Player) commandSender);
		}
	}
}

package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;

public class DatabaseRecreateCommand extends BasicParkourCommand {

	public DatabaseRecreateCommand(Parkour parkour) {
		super(parkour, AllowedSender.ANY, "recreate");
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADMIN_ALL;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		TranslationUtils.sendMessage(commandSender, "Recreating courses...");
		parkour.getDatabaseManager().recreateAllCourses(true);
	}
}

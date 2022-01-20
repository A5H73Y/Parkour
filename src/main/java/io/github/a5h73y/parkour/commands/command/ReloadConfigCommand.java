package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;

public class ReloadConfigCommand extends BasicParkourCommand {

	public ReloadConfigCommand(Parkour parkour) {
		super(parkour, AllowedSender.ANY, "reload");
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADMIN_ALL;
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		parkour.getConfigManager().reloadConfigs();
		PluginUtils.clearAllCache();
		TranslationUtils.sendTranslation("Parkour.ConfigReloaded", commandSender);
		PluginUtils.logToFile(commandSender.getName() + " reloaded the Parkour config");
	}
}

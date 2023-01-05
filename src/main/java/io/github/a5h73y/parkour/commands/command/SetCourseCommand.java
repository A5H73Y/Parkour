package io.github.a5h73y.parkour.commands.command;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.commands.type.AllowedCommandSender;
import io.github.a5h73y.parkour.commands.type.BasicParkourCommand;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import org.bukkit.command.CommandSender;

public class SetCourseCommand extends BasicParkourCommand {

	public SetCourseCommand(Parkour parkour) {
		super(parkour, "setcourse",
				AllowedCommandSender.ANY,
				"rewarddelay",
				"rewardlevel",
				"rewardleveladd",
				"rewardparkoins",
				"setplayerlimit",
				"setstart",
				"resumable",
				"rewardonce",
				"ready",
				"setautostart",
				"setcreator",
				"setmaxdeaths",
				"setmaxtime",
				"setminimumlevel",
				"maxfallticks",
				"challengeonly",
				"prize",
				"setprize",
				"setparkourmode");
	}

	@Override
	protected Permission getRequiredPermission() {
		return Permission.ADMIN_COURSE;
	}

	@Override
	public boolean validatePlayerArguments(CommandSender commandSender, String[] args) {
		return ValidationUtils.validateArgs(commandSender, args, 2, 100);
	}

	@Override
	public void performAction(CommandSender commandSender, String[] args) {
		if ("setcourse".equalsIgnoreCase(args[0])) {
			parkour.getCourseSettingsManager().processCommand(commandSender, args);

		} else {
			parkour.getCourseSettingsManager().performAction(commandSender, args[1],
                        args[0].toLowerCase().replace("set", ""), args.length > 2 ? args[2] : null);
		}
	}
}

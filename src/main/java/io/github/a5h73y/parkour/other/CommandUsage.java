package io.github.a5h73y.parkour.other;

import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.command.CommandSender;

public class CommandUsage {

	private String command;

	private String title;

	private String arguments;

	private String example;

	private String description;

	private String permission;

	private String commandGroup;

	private String consoleSyntax;

	public void displayHelpInformation(CommandSender sender) {
		TranslationUtils.sendHeading(title, sender);
		String commandSyntax = arguments != null ? command + " " + arguments : command;
		sender.sendMessage(TranslationUtils.getValueTranslation("Help.CommandSyntax", commandSyntax, false));
		sender.sendMessage(TranslationUtils.getValueTranslation("Help.CommandExample", example, false));
		TranslationUtils.sendHeading("Description", sender);
		sender.sendMessage(description);
	}

	public void displayCommandUsage(CommandSender sender) {
		sender.sendMessage(TranslationUtils.getTranslation("Help.CommandUsage", false)
				.replace("%COMMAND%", command)
				.replace("%ARGUMENTS%", arguments != null ? " " + arguments : "")
				.replace("%TITLE%", title));
	}

	public String getCommand() {
		return command;
	}

	public String getTitle() {
		return title;
	}

	public String getArguments() {
		return arguments;
	}

	public String getExample() {
		return example;
	}

	public String getDescription() {
		return description;
	}

	public String getPermission() {
		return permission;
	}

	public String getCommandGroup() {
		return commandGroup;
	}

	public String getConsoleSyntax() {
		return consoleSyntax;
	}
}

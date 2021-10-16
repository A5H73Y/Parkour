package io.github.a5h73y.parkour.commands;

import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandUsage {

	private String command;
	private String title;
	private String arguments;
	private String example;
	private String description;
	private String permission;
	private String commandGroup;
	private String consoleSyntax;

	/**
	 * Display Help Information for the Command.
	 * Includes usage information and a description of the command.
	 * Will display appropriate information based on the type of sender.
	 *
	 * @param sender requesting sender
	 */
	public void displayHelpInformation(CommandSender sender) {
		TranslationUtils.sendHeading(title, sender);

		if (sender instanceof ConsoleCommandSender) {
			TranslationUtils.sendValueTranslation("Help.ConsoleCommandSyntax", consoleSyntax, false, sender);

		} else {
			String commandSyntax = arguments != null ? command + " " + arguments : command;
			TranslationUtils.sendValueTranslation("Help.CommandSyntax", commandSyntax, false, sender);
		}
		TranslationUtils.sendValueTranslation("Help.CommandExample", example, false, sender);
		TranslationUtils.sendHeading("Description", sender);
		sender.sendMessage(description);
	}

	/**
	 * Display Command Usage.
	 * Formats the information to display command syntax and brief command title.
	 * @param sender requesting sender
	 */
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

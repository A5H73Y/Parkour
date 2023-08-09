package io.github.a5h73y.parkour.commands;

import static io.github.a5h73y.parkour.other.ParkourConstants.ARGUMENTS_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.COMMAND_PLACEHOLDER;
import static io.github.a5h73y.parkour.utility.permission.PermissionUtils.WILDCARD;

import java.util.List;

import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandUsage {

	public static final String ARRAY_OPEN = "[";
	public static final String ARRAY_CLOSE = "]";
	public static final String SUBSTITUTION_OPEN = "(";
	public static final String SUBSTITUTION_CLOSE = ")";
	public static final String FORMULA_OPEN = "{";
	public static final String FORMULA_CLOSE = "}";

	private static final String COMMA = ",";
	private static final String SPACE = " ";

	private String command;
	private String title;
	private String arguments;
	private List<String> examples;
	private String description;
	private String permission;
	private String commandGroup;
	private String consoleSyntax;
	private String autoTabSyntax;
	private String deprecated;

	/**
	 * Display Help Information for the Command.
	 * Includes usage information and a description of the command.
	 * Will display appropriate information based on the type of sender.
	 *
	 * @param commandSender command sender
	 */
	public void displayHelpInformation(CommandSender commandSender) {
		TranslationUtils.sendHeading(title, commandSender);

		if (commandSender instanceof ConsoleCommandSender) {
			TranslationUtils.sendValueTranslation("Help.ConsoleCommandSyntax", consoleSyntax, false, commandSender);

		} else {
			String commandSyntax = arguments != null ? command + SPACE + arguments : command;
			TranslationUtils.sendValueTranslation("Help.CommandSyntax", commandSyntax, false, commandSender);
		}
		if (getExamples() != null && !getExamples().isEmpty()) {
			TranslationUtils.sendTranslation("Help.CommandExamples", false, commandSender);
			getExamples().forEach(example -> commandSender.sendMessage("  " + example));
		}
		TranslationUtils.sendHeading("Description", commandSender);
		commandSender.sendMessage(description);
	}

	/**
	 * Display Command Usage.
	 * Formats the information to display command syntax and brief command title.
	 * @param commandSender command sender
	 */
	public void displayCommandUsage(CommandSender commandSender) {
		commandSender.sendMessage(TranslationUtils.getTranslation("Help.CommandUsage", false)
				.replace(COMMAND_PLACEHOLDER, command)
				.replace(ARGUMENTS_PLACEHOLDER, arguments != null ? SPACE + arguments : "")
				.replace("%TITLE%", title));
	}

	/**
	 * Display invalid syntax error.
	 *
	 * @param commandSender command sender
	 */
	public void sendInvalidSyntax(CommandSender commandSender) {
		commandSender.sendMessage(TranslationUtils.getTranslation("Error.Syntax")
				.replace(COMMAND_PLACEHOLDER, getCommand())
				.replace(ARGUMENTS_PLACEHOLDER, getArguments()));
	}

	public String[] getAutoTabArraySelection(String input) {
		return StringUtils.substringBetween(input, ARRAY_OPEN, ARRAY_CLOSE).split(COMMA);
	}

	public String[] getAutoTabSyntaxArgs() {
		return autoTabSyntax.split(SPACE);
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

	public List<String> getExamples() {
		return examples;
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

	public String getAutoTabSyntax() {
		return autoTabSyntax;
	}

	public String getDeprecated() {
		return deprecated;
	}

	/**
	 * Calculate Formula Value from AutoTab completion.
	 * @param input input
	 * @param args arguments
	 * @return resolved formula value
	 */
	public String resolveFormulaValue(String input, String[] args) {
		String[] possibleReplacements = StringUtils.substringBetween(input, FORMULA_OPEN, FORMULA_CLOSE).split(COMMA);
		for (String replacement : possibleReplacements) {
			String[] assignmentSplit = replacement.split("=");
			String[] indexValueSplit = assignmentSplit[0].split(":");

			// wildcard - match any
			if (indexValueSplit[0].equals(WILDCARD)) {
				return assignmentSplit[1];
			} else {
				int index = Integer.parseInt(indexValueSplit[0]);

				if (args[index].equals(indexValueSplit[1])) {
					return assignmentSplit[1];
				}
			}
		}
		return "";
	}
}

package io.github.a5h73y.parkour.utility;

import org.bukkit.command.CommandSender;

/**
 * Validation related utility methods.
 */
public class ValidationUtils {

	/**
	 * Validate if the input is a valid String.
	 *
	 * @param input text
	 * @return input is a valid String
	 */
	public static boolean isStringValid(String input) {
		return input != null && !input.trim().isEmpty();
	}

	/**
	 * Validate if the input is a valid Integer.
	 *
	 * @param input text
	 * @return input is an Integer
	 */
	public static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception ignored) { }
		return false;
	}

	/**
	 * Validate if the input is a valid Double.
	 *
	 * @param input text
	 * @return input is a Double
	 */
	public static boolean isDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (Exception ignored) { }
		return false;
	}

	/**
	 * Validate the length of the arguments before allowing it to be processed further.
	 *
	 * @param sender command sender
	 * @param args command arguments
	 * @param required required args length
	 * @return whether the arguments match the criteria
	 */
	public static boolean validateArgs(CommandSender sender, String[] args, int required) {
		return validateArgs(sender, args, required, required);
	}

	/**
	 * Validate the range of the arguments before allowing it to be processed further.
	 *
	 * @param sender command sender
	 * @param args command arguments
	 * @param minimum minimum args length
	 * @param maximum maximum args length
	 * @return whether the arguments match the criteria
	 */
	public static boolean validateArgs(CommandSender sender, String[] args, int minimum, int maximum) {
		if (args.length > maximum) {
			TranslationUtils.sendValueTranslation("Error.TooMany", String.valueOf(maximum), sender);
			TranslationUtils.sendValueTranslation("Help.Command", StringUtils.standardizeText(args[0]), sender);
			return false;

		} else if (args.length < minimum) {
			TranslationUtils.sendValueTranslation("Error.TooLittle", String.valueOf(minimum), sender);
			TranslationUtils.sendValueTranslation("Help.Command", StringUtils.standardizeText(args[0]), sender);
			return false;
		}
		return true;
	}
}

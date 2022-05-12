package io.github.a5h73y.parkour.utility;

import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;

/**
 * String / Message related utility methods.
 */
public class StringUtils {

	/**
	 * Translate colour codes of provided message.
	 *
	 * @param message message
	 * @return colourised message
	 */
	public static String colour(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	/**
	 * Format and standardize text to a constant case.
	 * Will transform "hElLO" into "Hello".
	 *
	 * @param text message
	 * @return standardized input
	 */
	public static String standardizeText(String text) {
		return !ValidationUtils.isStringValid(text) ? text
				: text.substring(0, 1).toUpperCase()
				.concat(text.substring(1).toLowerCase())
				.replace("_", " ");
	}

	public static String extractMessageFromArgs(String[] args, int startIndex) {
		return extractMessageFromArgs(Arrays.asList(args), startIndex);
	}

	public static String extractMessageFromArgs(List<String> args, int startIndex) {
		return String.join(" ", args.subList(startIndex, args.size()));
	}

	/**
	 * Get the Substring between both strings.
	 * @param input input value
	 * @param start start string
	 * @param end end string
	 * @return substring
	 */
	public static String substringBetween(String input, String start, String end) {
		String result = null;
		if (input != null && start != null && end != null) {
			int startIndex = input.indexOf(start);
			if (startIndex != -1) {
				int endIndex = input.indexOf(end, startIndex + start.length());
				if (endIndex != -1) {
					result = input.substring(startIndex + start.length(), endIndex);
				}
			}
		}
		return result;
	}

	private StringUtils() {}
}

package io.github.a5h73y.parkour.utility;

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
		return ValidationUtils.isStringValid(text)
				? text.substring(0, 1).toUpperCase().concat(text.substring(1).toLowerCase())
				: text;
	}
}

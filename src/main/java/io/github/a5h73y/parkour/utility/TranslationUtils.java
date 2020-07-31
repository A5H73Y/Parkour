package io.github.a5h73y.parkour.utility;

import static io.github.a5h73y.parkour.utility.StringUtils.colour;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.ConfigType;
import java.util.regex.Pattern;
import org.bukkit.command.CommandSender;

/**
 * Translations related utility methods.
 */
public class TranslationUtils {

	private static final Pattern valuePlaceholder = Pattern.compile("%(?i)value%");

	/**
	 * Get translation of string key.
	 * The string parameter will be matched to an entry in the Strings.yml.
	 * The boolean will determine whether to display the Parkour prefix.
	 *
	 * @param translationKey to translate
	 * @param prefix display Parkour prefix
	 * @return String of appropriate translation
	 */
	public static String getTranslation(String translationKey, boolean prefix) {
		if (!ValidationUtils.isStringValid(translationKey)) {
			return "Invalid translation.";
		}

		String translated = Parkour.getConfig(ConfigType.STRINGS).getString(translationKey);
		translated = translated != null ? colour(translated) : "String not found: " + translationKey;
		return prefix ? Parkour.getPrefix().concat(translated) : translated;
	}

	/**
	 * Get translation of string key with prefix.
	 * The string parameter will be matched to an entry in the Strings.yml.
	 *
	 * @param translationKey to translate
	 * @return String of appropriate translation
	 */
	public static String getTranslation(String translationKey) {
		return getTranslation(translationKey, true);
	}

	/**
	 * Get translation of string key, replacing a value placeholder.
	 * The string parameter will be matched to an entry in the Strings.yml.
	 * The boolean will determine whether to display the Parkour prefix.
	 *
	 * @param translationKey to translate
	 * @param value to populate
	 * @param prefix display Parkour prefix
	 * @return String of appropriate translation
	 */
	public static String getValueTranslation(String translationKey, String value, boolean prefix) {
		return valuePlaceholder.matcher(getTranslation(translationKey, prefix))
				.replaceAll(value == null ? "" : value);
	}

	/**
	 * Get translation of string key with prefix, replacing a value placeholder.
	 * The string parameter will be matched to an entry in the Strings.yml.
	 * The boolean will determine whether to display the Parkour prefix.
	 *
	 * @param translationKey to translate
	 * @param value to populate
	 * @return String of appropriate translation
	 */
	public static String getValueTranslation(String translationKey, String value) {
		return getValueTranslation(translationKey, value, true);
	}

	/**
	 * Send the translated message to the player(s).
	 *
	 * @param translationKey to translate
	 * @param prefix display prefix
	 * @param players targets to receive the message
	 */
	public static void sendTranslation(String translationKey, boolean prefix, CommandSender... players) {
		String translation = getTranslation(translationKey, prefix);
		for (CommandSender player : players) {
			player.sendMessage(translation);
		}
	}

	/**
	 * Send the translated message to the player(s) with prefix.
	 *
	 * @param translationKey translationKey to translate
	 * @param players to receive the message
	 */
	public static void sendTranslation(String translationKey, CommandSender... players) {
		sendTranslation(translationKey, true, players);
	}

	/**
	 * Send the translated message to the player(s), replacing a value placeholder.
	 *
	 * @param translationKey to translate
	 * @param value to replace
	 * @param players targets to receive the message
	 */
	public static void sendValueTranslation(String translationKey, String value, CommandSender... players) {
		String translation = getValueTranslation(translationKey, value);
		for (CommandSender player : players) {
			if (player != null) {
				player.sendMessage(translation);
			}
		}
	}

	/**
	 * Send the translated message to the player with a heading template.
	 *
	 * @param message to display
	 * @param player to receive the message
	 */
	public static void sendHeading(String message, CommandSender player) {
		player.sendMessage(getValueTranslation("Parkour.Heading", message, false));
	}

	/**
	 * Display invalid syntax error.
	 * Using parameters to populate the translation message
	 *
	 * @param sender target command sender
	 * @param command requested command
	 * @param arguments command arguments
	 */
	public static void sendInvalidSyntax(CommandSender sender, String command, String arguments) {
		sender.sendMessage(getTranslation("Error.Syntax")
				.replace("%COMMAND%", command)
				.replace("%ARGUMENTS%", arguments));
	}
}

package io.github.a5h73y.parkour.utility;

import static io.github.a5h73y.parkour.utility.StringUtils.colour;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.enums.ParkourEventType;
import io.github.a5h73y.parkour.other.Constants;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
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
	 * The translation key will be matched to an entry in the Strings.yml.
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
	 * Get translation of string key, replacing a value placeholder.
	 * The translation key will be matched to an entry in the Strings.yml.
	 * The boolean will determine whether to display the Parkour prefix.
	 *
	 * @param translationKey to translate
	 * @param value to populate
	 * @param prefix display Parkour prefix
	 * @return String of appropriate translation
	 */
	public static String getValueTranslation(String translationKey, Number value, boolean prefix) {
		return getValueTranslation(translationKey, String.valueOf(value), prefix);
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
	 * Get Course Event Message.
	 * Will find the corresponding custom Event message.
	 * Fallback to the default event message if not found.
	 *
	 * @param courseName course name
	 * @param eventType event type
	 * @param fallbackKey fallback translation key
	 * @return course event message
	 */
	public static String getCourseEventMessage(String courseName, ParkourEventType eventType, String fallbackKey) {
		String result = CourseInfo.getEventMessage(courseName, eventType);

		// if there is no custom message, fallback to default
		if (result == null) {
			result = getTranslation(fallbackKey, false);
		}

		return valuePlaceholder.matcher(StringUtils.colour(result)).replaceAll(courseName == null ? "" : courseName);
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
		sendValueTranslation(translationKey, value, true, players);
	}

	/**
	 * Send the translated message to the player(s), replacing a value placeholder.
	 *
	 *
	 * @param translationKey to translate
	 * @param value to replace
	 * @param players targets to receive the message
	 */
	public static void sendValueTranslation(String translationKey, String value, boolean prefix, CommandSender... players) {
		String translation = getValueTranslation(translationKey, value, prefix);
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
		sendValueTranslation("Parkour.Heading", message, false, player);
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

	/**
	 * Send the Property Set translation, replacing placeholders.
	 *
	 * @param sender command sender
	 * @param property property name
	 * @param courseName course name
	 * @param value value set
	 */
	public static void sendPropertySet(CommandSender sender, String property, String courseName, String value) {
		sender.sendMessage(getPropertySet(property, courseName, value));
	}

	/**
	 * Get the Property Set translation, replacing placeholders.
	 *
	 * @param property property name
	 * @param courseName course name
	 * @param value value set
	 * @return property set translation
	 */
	public static String getPropertySet(String property, String courseName, String value) {
		return getTranslation("Other.PropertySet")
				.replace("%PROPERTY%", property)
				.replace(Constants.COURSE_PLACEHOLDER, courseName)
				.replace("%VALUE%", value);
	}

	/**
	 * Send the Player a Parkour prefixed Message.
	 * For messages that don't require a Translation entry.
	 *
	 * @param sender command sender
	 * @param message message to send
	 */
	public static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(Parkour.getPrefix().concat(colour(message)));
	}

	/**
	 * Send the Player a Message.
	 * For messages that don't require a Translation entry.
	 *
	 * @param sender command sender
	 * @param message message to send
	 * @param prefix display prefix
	 */
	public static void sendMessage(CommandSender sender, String message, boolean prefix) {
		if (prefix) {
			sendMessage(sender, message);
		} else {
			sender.sendMessage(colour(message));
		}
	}

	/**
	 * Send a Title Value Summary.
	 *
	 * @param sender command sender
	 * @param title value title
	 * @param value value
	 */
	public static void sendValue(CommandSender sender, String title, String value) {
		sender.sendMessage(title + ": " + ChatColor.AQUA + value);
	}

	/**
	 * Send a Title Value Summary.
	 *
	 * @param sender command sender
	 * @param title value title
	 * @param value value
	 */
	public static void sendValue(CommandSender sender, String title, Number value) {
		sendValue(sender, title, String.valueOf(value));
	}

	/**
	 * Send conditional Value Summary.
	 * Message is sent if the condition is met.
	 *
	 * @param sender command sender
	 * @param title value title
	 * @param conditionMet condition is met
	 * @param value value
	 */
	public static void sendConditionalValue(CommandSender sender, String title, Boolean conditionMet, String value) {
		if (conditionMet) {
			sendValue(sender, title, value);
		}
	}

	/**
	 * Send conditional Value Summary.
	 * Message is sent if the numeric value is positive.
	 *
	 * @param sender command sender
	 * @param title value title
	 * @param value value
	 */
	public static void sendConditionalValue(CommandSender sender, String title, Number value) {
		if (value != null && value.doubleValue() > 0) {
			sendValue(sender, title, String.valueOf(value));
		}
	}

	/**
	 * Send conditional Value Summary.
	 * Message is sent if the value is valid.
	 *
	 * @param sender command sender
	 * @param title value title
	 * @param value value
	 */
	public static void sendConditionalValue(CommandSender sender, String title, String value) {
		if (ValidationUtils.isStringValid(value)) {
			sendValue(sender, title, value);
		}
	}
}

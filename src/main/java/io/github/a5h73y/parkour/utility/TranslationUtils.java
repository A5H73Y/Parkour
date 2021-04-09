package io.github.a5h73y.parkour.utility;

import static io.github.a5h73y.parkour.other.ParkourConstants.CHECKPOINT_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.COURSE_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.DEATHS_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PLAYER_DISPLAY_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PLAYER_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TIME_PLACEHOLDER;
import static io.github.a5h73y.parkour.utility.StringUtils.colour;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.enums.ParkourEventType;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Translations related utility methods.
 */
public class TranslationUtils {

	private static final Pattern VALUE_PLACEHOLDER = Pattern.compile("%(?i)value%");

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

		return prefix && ValidationUtils.isStringValid(translated)
				? Parkour.getPrefix().concat(translated) : translated;
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
		return VALUE_PLACEHOLDER.matcher(getTranslation(translationKey, prefix))
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
	 * @param session parkour session
	 * @param eventType event type
	 * @param fallbackKey fallback translation key
	 * @return course event message
	 */
	public static String getCourseEventMessage(ParkourSession session, ParkourEventType eventType, String fallbackKey) {
		String result = CourseInfo.getEventMessage(session.getCourseName(), eventType);

		// if there is no custom message, fallback to default
		if (result == null) {
			result = getTranslation(fallbackKey, false);
		}

		return VALUE_PLACEHOLDER.matcher(colour(result)).replaceAll(session.getCourse().getDisplayName());
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
		if (!translation.isEmpty()) {
			for (CommandSender player : players) {
				player.sendMessage(translation);
			}
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
		if (!translation.isEmpty()) {
			for (CommandSender player : players) {
				if (player != null) {
					player.sendMessage(translation);
				}
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
				.replace(COURSE_PLACEHOLDER, courseName)
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
		if (!message.isEmpty()) {
			sender.sendMessage(Parkour.getPrefix().concat(colour(message)));
		}
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
		if (Boolean.TRUE.equals(conditionMet)) {
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

	/**
	 * Announce Parkour Message with a scope.
	 * If an invalid scope is provided, no message will be sent.
	 *
	 * @param player player
	 * @param scope message scope
	 * @param message message
	 */
	public static void announceParkourMessage(Player player, String scope, String message) {
		if (!ValidationUtils.isStringValid(message) || !ValidationUtils.isStringValid(scope)) {
			return;
		}

		switch (scope.toUpperCase()) {
			case "GLOBAL":
				for (Player players : Bukkit.getServer().getOnlinePlayers()) {
					players.sendMessage(message);
				}
				return;
			case "WORLD":
				for (Player players : player.getWorld().getPlayers()) {
					players.sendMessage(message);
				}
				return;
			case "PARKOUR":
				for (Player players : Parkour.getInstance().getPlayerManager().getOnlineParkourPlayers()) {
					players.sendMessage(message);
				}
				return;
			case "PLAYER":
				player.sendMessage(message);
				return;
			default:
		}
	}

	/**
	 * Replace all Parkour Placeholders with their value counterpart.
	 * Used for inserting values before sending messages internally.
	 *
	 * @param input input
	 * @param player player
	 * @param session parkour session
	 * @return updated input message
	 */
	public static String replaceAllParkourPlaceholders(String input, Player player, ParkourSession session) {
		return input.replace(PLAYER_PLACEHOLDER, player.getName())
				.replace(PLAYER_DISPLAY_PLACEHOLDER, player.getDisplayName())
				.replace(COURSE_PLACEHOLDER, session.getCourse().getDisplayName())
				.replace(DEATHS_PLACEHOLDER, String.valueOf(session.getDeaths()))
				.replace(TIME_PLACEHOLDER, session.getDisplayTime())
				.replace(CHECKPOINT_PLACEHOLDER, String.valueOf(session.getCurrentCheckpoint()));
	}
}

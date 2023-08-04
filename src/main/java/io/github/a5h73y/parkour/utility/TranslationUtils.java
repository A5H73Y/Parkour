package io.github.a5h73y.parkour.utility;

import static io.github.a5h73y.parkour.other.ParkourConstants.ARGUMENTS_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.CHECKPOINT_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.COMMAND_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.COURSE_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.DEATHS_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PLAYER_DISPLAY_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PLAYER_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TIME_H_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TIME_MS_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TIME_M_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TIME_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TIME_S_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TOTAL_CHECKPOINT_PLACEHOLDER;
import static io.github.a5h73y.parkour.utility.StringUtils.colour;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.ParkourEventType;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.a5h73y.parkour.utility.time.MillisecondConverter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	public static String getTranslation(@Nullable String translationKey,
	                                    boolean prefix) {
		if (!ValidationUtils.isStringValid(translationKey)) {
			return "Invalid translation.";
		}

		String translated = Parkour.getInstance().getConfigManager().getStringsConfig().getString(translationKey);
		translated = translated != null ? colour(translated) : "String not found: " + translationKey;

		return prefix && ValidationUtils.isStringValid(translated)
				? getPluginPrefix().concat(translated) : translated;
	}

	public static boolean containsTranslation(@Nullable String translationKey) {
		return translationKey != null
				&& Parkour.getInstance().getConfigManager().getStringsConfig().contains(translationKey);
	}

	/**
	 * Get translation of string key with prefix.
	 * The string parameter will be matched to an entry in the Strings.yml.
	 *
	 * @param translationKey to translate
	 * @return String of appropriate translation
	 */
	public static String getTranslation(@Nullable String translationKey) {
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
	public static String getValueTranslation(@Nullable String translationKey,
	                                         @Nullable String value,
	                                         boolean prefix) {
		return VALUE_PLACEHOLDER.matcher(getTranslation(translationKey, prefix))
				.replaceAll(value == null ? "" : Matcher.quoteReplacement(value));
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
	public static String getValueTranslation(@Nullable String translationKey,
	                                         @Nullable Number value,
	                                         boolean prefix) {
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
	public static String getValueTranslation(@Nullable String translationKey,
	                                         @Nullable String value) {
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
	public static String getCourseEventMessage(@NotNull ParkourSession session,
	                                           @NotNull ParkourEventType eventType,
	                                           @Nullable String fallbackKey) {
		String result = Parkour.getInstance().getConfigManager().getCourseConfig(session.getCourseName())
				.getEventMessage(eventType);

		// if there is no custom message, fallback to default
		if (result == null) {
			result = getTranslation(fallbackKey, false);
		}

		return colour(VALUE_PLACEHOLDER.matcher(result).replaceAll(session.getCourse().getDisplayName()));
	}

	/**
	 * Send the translated message to the recipient(s).
	 *
	 * @param translationKey to translate
	 * @param prefix display prefix
	 * @param recipients targets to receive the message
	 */
	public static void sendTranslation(@Nullable String translationKey, boolean prefix,
	                                   @Nullable CommandSender... recipients) {
		String translation = getTranslation(translationKey, prefix);
		if (recipients != null && !translation.isEmpty()) {
			for (CommandSender recipient : recipients) {
				recipient.sendMessage(translation);
			}
		}
	}

	/**
	 * Send the translated message to the player(s) with prefix.
	 *
	 * @param translationKey translationKey to translate
	 * @param players to receive the message
	 */
	public static void sendTranslation(@Nullable String translationKey,
	                                   @Nullable CommandSender... players) {
		sendTranslation(translationKey, true, players);
	}

	/**
	 * Send the translated message to the player(s), replacing a value placeholder.
	 *
	 * @param translationKey to translate
	 * @param value to replace
	 * @param players targets to receive the message
	 */
	public static void sendValueTranslation(@Nullable String translationKey,
	                                        @Nullable String value,
	                                        @Nullable CommandSender... players) {
		sendValueTranslation(translationKey, value, true, players);
	}

	/**
	 * Send the translated message to the recipient(s), replacing a value placeholder.
	 *
	 * @param translationKey to translate
	 * @param value to replace
	 * @param recipients targets to receive the message
	 */
	public static void sendValueTranslation(@Nullable String translationKey,
	                                        @Nullable String value, boolean prefix,
	                                        @Nullable CommandSender... recipients) {
		String translation = getValueTranslation(translationKey, value, prefix);
		if (recipients != null && !translation.isEmpty()) {
			for (CommandSender recipient : recipients) {
				if (recipient != null) {
					recipient.sendMessage(translation);
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
	 * @param commandSender command sender
	 * @param command requested command
	 * @param arguments command arguments
	 */
	public static void sendInvalidSyntax(CommandSender commandSender, String command, String arguments) {
		commandSender.sendMessage(getTranslation("Error.Syntax")
				.replace(COMMAND_PLACEHOLDER, command)
				.replace(ARGUMENTS_PLACEHOLDER, arguments));
	}

	/**
	 * Send the Property Set translation, replacing placeholders.
	 *
	 * @param commandSender command sender
	 * @param property property name
	 * @param courseName course name
	 * @param value value set
	 */
	public static void sendPropertySet(CommandSender commandSender, String property, String courseName, String value) {
		commandSender.sendMessage(getPropertySet(property, courseName, value));
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
	 * @param commandSender command sender
	 * @param message message to send
	 */
	public static void sendMessage(CommandSender commandSender, String message) {
		if (commandSender != null && ValidationUtils.isStringValid(message)) {
			commandSender.sendMessage(getPluginPrefix().concat(colour(message)));
		}
	}

	/**
	 * Send the Player a Message.
	 * For messages that don't require a Translation entry.
	 *
	 * @param commandSender command sender
	 * @param message message to send
	 * @param prefix display prefix
	 */
	public static void sendMessage(CommandSender commandSender, String message, boolean prefix) {
		if (prefix) {
			sendMessage(commandSender, message);
		} else if (commandSender != null) {
			commandSender.sendMessage(colour(message));
		}
	}

	/**
	 * Send a Title Value Summary.
	 *
	 * @param commandSender command sender
	 * @param title value title
	 * @param value value
	 */
	public static void sendValue(CommandSender commandSender, String title, String value) {
		String displayValue = getValueTranslation("Help.DisplayValue", value, false);
		sendMessage(commandSender, displayValue.replace("%TITLE%", title), false);
	}

	/**
	 * Send a Title Value Summary.
	 *
	 * @param commandSender command sender
	 * @param title value title
	 * @param value value
	 */
	public static void sendValue(CommandSender commandSender, String title, Number value) {
		sendValue(commandSender, title, String.valueOf(value));
	}

	/**
	 * Send conditional Value Summary.
	 * Message is sent if the condition is met.
	 *
	 * @param commandSender command sender
	 * @param title value title
	 * @param conditionMet condition is met
	 * @param value value
	 */
	public static void sendConditionalValue(CommandSender commandSender, String title, Boolean conditionMet, String value) {
		if (Boolean.TRUE.equals(conditionMet)) {
			sendValue(commandSender, title, value);
		}
	}

	/**
	 * Send conditional Value Summary.
	 * Message is sent if the numeric value is positive.
	 *
	 * @param commandSender command sender
	 * @param title value title
	 * @param value value
	 */
	public static void sendConditionalValue(CommandSender commandSender, String title, Number value) {
		if (value != null && value.doubleValue() > 0) {
			sendValue(commandSender, title, String.valueOf(value));
		}
	}

	/**
	 * Send conditional Value Summary.
	 * Message is sent if the value is valid.
	 *
	 * @param commandSender command sender
	 * @param title value title
	 * @param value value
	 */
	public static void sendConditionalValue(CommandSender commandSender, String title, String value) {
		if (ValidationUtils.isStringValid(value)) {
			sendValue(commandSender, title, value);
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
				for (Player players :
						Parkour.getInstance().getParkourSessionManager().getOnlineParkourPlayers()) {
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
		MillisecondConverter converter = new MillisecondConverter(session.getAccumulatedTime());
		String result = input.replace(PLAYER_PLACEHOLDER, player.getName())
				.replace(PLAYER_DISPLAY_PLACEHOLDER, player.getDisplayName())
				.replace(COURSE_PLACEHOLDER, session.getCourse().getDisplayName())
				.replace(DEATHS_PLACEHOLDER, String.valueOf(session.getDeaths()))
				.replace(TIME_PLACEHOLDER, session.getDisplayTime())
				.replace(TIME_MS_PLACEHOLDER, String.valueOf(session.getAccumulatedTime()))
				.replace(TIME_S_PLACEHOLDER, String.valueOf(converter.getTotalSeconds()))
				.replace(TIME_M_PLACEHOLDER, String.valueOf(converter.getTotalMinutes()))
				.replace(TIME_H_PLACEHOLDER, String.valueOf(converter.getTotalHours()))
				.replace(CHECKPOINT_PLACEHOLDER, String.valueOf(session.getCurrentCheckpoint()))
				.replace(TOTAL_CHECKPOINT_PLACEHOLDER, String.valueOf(session.getCourse().getNumberOfCheckpoints()));
		return Parkour.getInstance().getPlaceholderApi().parsePlaceholders(player, result);
	}

	/**
	 * Replace all Player Placeholders with their value counterpart.
	 * Used for inserting values before sending messages internally.
	 *
	 * @param input input
	 * @param player player
	 * @return updated input message
	 */
	public static String replaceAllPlayerPlaceholders(String input, Player player) {
		return input.replace(PLAYER_PLACEHOLDER, player.getName())
				.replace(PLAYER_DISPLAY_PLACEHOLDER, player.getDisplayName());
	}

	public static String getPluginPrefix() {
		return getTranslation("Parkour.Prefix", false);
	}

	private TranslationUtils() {}
}

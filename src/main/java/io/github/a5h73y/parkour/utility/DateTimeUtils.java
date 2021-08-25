package io.github.a5h73y.parkour.utility;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.StringJoiner;
import org.bukkit.OfflinePlayer;

/**
 * Date Time Utility methods.
 */
public class DateTimeUtils {

	public static final String DD_MM_YYYY = "dd-MM-yyyy";
	public static final String DD_MM_YYYY_HH_MM_SS = "[dd/MM/yyyy | HH:mm:ss]";

	/**
	 * Display time representation of milliseconds.
	 *
	 * @param milliseconds milliseconds
	 * @return detailed formatted time
	 */
	public static String displayCurrentTime(long milliseconds) {
		return StringUtils.colour(Parkour.getDefaultConfig().getDetailedTimeOutput().format(new Date(milliseconds)));
	}

	/**
	 * Convert the number of seconds into a standard time format.
	 *
	 * @param totalSeconds number of seconds
	 * @return standard formatted time
	 */
	public static String convertSecondsToTime(int totalSeconds) {
		return StringUtils.colour(Parkour.getDefaultConfig().getStandardTimeOutput().format(new Date(totalSeconds * 1000L)));
	}

	/**
	 * Display current date.
	 *
	 * @return formatted date DD-MM-YYYY
	 */
	public static String getDisplayDate() {
		LocalDate localDate = LocalDate.now();
		return localDate.format(DateTimeFormatter.ofPattern(DD_MM_YYYY));
	}

	/**
	 * Display current date and time.
	 *
	 * @return formatted datetime DD/MM/YYYY | HH:MM:SS
	 */
	public static String getDisplayDateTime() {
		LocalDateTime localDateTime = LocalDateTime.now();
		return localDateTime.format(DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS));
	}

	/**
	 * Display the Prize Delay time remaining.
	 * @param player player
	 * @param courseName course name
	 * @return formatted time left
	 */
	public static String getDelayTimeRemaining(OfflinePlayer player, String courseName) {
		long hoursDelay = convertHoursToMilliseconds(CourseInfo.getRewardDelay(courseName));
		long timeDifference = System.currentTimeMillis() - PlayerInfo.getLastRewardedTime(player, courseName);
		return convertMillisecondsToDateTime(hoursDelay - timeDifference);
	}

	/**
	 * Convert the milliseconds to Time Display.
	 * Will dynamically build a sentence of how many days / hours / minute / seconds.
	 *
	 * @param millis milliseconds
	 * @return time display
	 */
	public static String convertMillisecondsToDateTime(long millis) {
		MillisecondConverter time = new MillisecondConverter(millis);
		StringJoiner totalTime = new StringJoiner(", ");

		addString(totalTime, "Display.Day", time.getDays());

		if (time.getDays() > 1) {
			return totalTime.toString();
		}

		addString(totalTime, "Display.Hour", time.getHours());
		addString(totalTime, "Display.Minute", time.getMinutes());
		addString(totalTime, "Display.Second", time.getSeconds());

		return totalTime.toString().isEmpty() ? "0" : totalTime.toString();
	}

	private static void addString(StringJoiner joiner, String translationKey, long amount) {
		if (amount > 0) {
			String key = translationKey + (amount == 1 ? "" : "s");
			joiner.add(TranslationUtils.getValueTranslation(key, amount, false));
		}
	}

	public static long convertDaysToMilliseconds(int days) {
		return days * 86400000L; // 24 * 60 * 60 * 1000
	}

	public static long convertHoursToMilliseconds(double hours) {
		return (long) (hours * 3600000L); // 60 * 60 * 1000
	}

}

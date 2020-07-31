package io.github.a5h73y.parkour.utility;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.course.CourseInfo;
import io.github.a5h73y.parkour.player.PlayerInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.bukkit.entity.Player;

/**
 * Date Time Utility methods.
 */
public class DateTimeUtils {

	public static final String DD_MM_YYYY = "dd-MM-yyyy";
	public static final String DD_MM_YYYY_HH_MM_SS = "[dd/MM/yyyy | HH:mm:ss]";

	public static final String HH_MM_SS = "%02d:%02d:%02d";
	public static final String HH_MM_SS_MS = "%02d:%02d:%02d.%03d";

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
	 * Display time representation of milliseconds.
	 * Convert milliseconds into formatted time HH:MM:SS(.sss).
	 *
	 * @param milliseconds milliseconds
	 * @return formatted time: HH:MM:SS.(sss)
	 */
	public static String displayCurrentTime(long milliseconds) {
		MillisecondConverter time = new MillisecondConverter(milliseconds);
		String pattern = Parkour.getDefaultConfig().isDisplayMilliseconds() ? HH_MM_SS_MS : HH_MM_SS;
		return String.format(pattern, time.getHours(), time.getMinutes(), time.getSeconds(), time.getMilliseconds());
	}

	/**
	 * Convert the number of seconds into a HH:MM:SS format.
	 *
	 * @param totalSeconds number of seconds
	 * @return formatted time HH:MM:SS
	 */
	public static String convertSecondsToTime(int totalSeconds) {
		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds % 3600) / 60;
		int seconds = totalSeconds % 60;

		return String.format(HH_MM_SS, hours, minutes, seconds);
	}

	public static String getTimeRemaining(Player player, String courseName) {
		long daysDelay = convertDaysToMilliseconds(CourseInfo.getRewardDelay(courseName));
		long timeDifference = System.currentTimeMillis() - PlayerInfo.getLastRewardedTime(player, courseName);
		return displayTimeRemaining(daysDelay - timeDifference);
	}

	private static String displayTimeRemaining(long millis) {
		MillisecondConverter time = new MillisecondConverter(millis);
		StringBuilder totalTime = new StringBuilder();

		if (time.getDays() > 2) {
			totalTime.append(time.getDays());
			totalTime.append(" days"); //todo translate
			return totalTime.toString();
		}

		if (time.getDays() > 0) {
			totalTime.append(1);
			totalTime.append(" day, ");
		}
		if (time.getHours() > 0) {
			totalTime.append(time.getHours());
			totalTime.append(" hours, ");
		}
		totalTime.append(time.getMinutes());
		totalTime.append(" minutes");
		return totalTime.toString();
	}

	public static long convertDaysToMilliseconds(int days) {
		return days * 86400000; //(24*60*60*1000)
	}

}

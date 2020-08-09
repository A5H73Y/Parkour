package io.github.a5h73y.parkour;

import io.github.a5h73y.parkour.database.TimeEntry;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.List;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

/**
 * Parkour's implementation of {@link PlaceholderExpansion}.
 * Various Parkour related information can be retrieved through placeholders.
 */
public class ParkourPlaceholders extends PlaceholderExpansion {

    private static final String INVALID_SYNTAX = TranslationUtils.getTranslation("Placeholder.InvalidSyntax", false);
    private static final String NO_TIME_RECORDED = TranslationUtils.getTranslation("Placeholder.NoTimeRecorded", false);

    private final Parkour parkour;

    public ParkourPlaceholders(Parkour parkour) {
        this.parkour = parkour;
    }

    @Override
    public String getIdentifier() {
        return parkour.getName();
    }

    @Override
    public String getAuthor() {
        return parkour.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return parkour.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String command) {
        command = command.toLowerCase();
        String[] arguments = command.split("_");

        if (command.equals("version")) {
            return getVersion();

        } else if (command.equals("course_count")) {
            return String.valueOf(CourseInfo.getAllCourses().size());

        } else if (command.equals("player_count")) {
            return String.valueOf(parkour.getPlayerManager().getNumberOfParkourPlayer());

        } else if (command.startsWith("course_record")) {
            if (arguments.length != 3) {
                return INVALID_SYNTAX;
            }

            TimeEntry result = getTopResultForCourse(arguments[2]);
            return result == null ? NO_TIME_RECORDED : DateTimeUtils.displayCurrentTime(result.getTime());

        } else if (command.startsWith("course_record_deaths")) {
            if (arguments.length != 4) {
                return INVALID_SYNTAX;
            }

            TimeEntry result = getTopResultForCourse(arguments[3]);
            return result == null ? NO_TIME_RECORDED : String.valueOf(result.getDeaths());

        } else if (command.startsWith("leader_")) {
            if (arguments.length != 2) {
                return INVALID_SYNTAX;
            }

            TimeEntry result = getTopResultForCourse(arguments[1]);
            return result == null ? NO_TIME_RECORDED : result.getPlayer();
        }

        // Player specific
        if (player == null) {
            return "";
        }

        if (command.equals("last_completed")) {
            return PlayerInfo.getLastCompletedCourse(player);

        } else if (command.equals("courses_completed")) {
            return PlayerInfo.getNumberOfCoursesCompleted(player);

        } else if (command.equals("last_played")) {
            return PlayerInfo.getLastPlayedCourse(player);

        } else if (command.equals("parkoins")) {
            return String.valueOf(PlayerInfo.getParkoins(player));

        } else if (command.equals("level")) {
            return String.valueOf(PlayerInfo.getParkourLevel(player));

        } else if (command.equals("rank")) {
            return PlayerInfo.getRank(player);

        } else if (command.equals("current_course")) {
            Course course = parkour.getCourseManager().findByPlayer(player.getName());
            return course == null ? "" : course.getName();

        } else if (command.equals("current_checkpoint")) {
            ParkourSession session = parkour.getPlayerManager().getParkourSession(player.getName());
            return session == null ? "" : String.valueOf(session.getCurrentCheckpoint());

        } else if (command.equals("current_course_record_deaths")) {
            Course course = parkour.getCourseManager().findByPlayer(player.getName());
            if (course != null) {
                TimeEntry result = getTopResultForCourse(course.getName());
                return result == null ? NO_TIME_RECORDED : String.valueOf(result.getDeaths());
            }
            return "";

        } else if (command.equals("current_course_record")) {
            Course course = parkour.getCourseManager().findByPlayer(player.getName());
            if (course != null) {
                TimeEntry result = getTopResultForCourse(course.getName());
                return result == null ? NO_TIME_RECORDED : DateTimeUtils.displayCurrentTime(result.getTime());
            }
            return "";

        } else if (command.startsWith("personal_best_deaths")) {
            if (arguments.length != 4) {
                return INVALID_SYNTAX;
            }

            TimeEntry result = getTopPlayerResultForCourse(player.getName(), arguments[3]);
            return result == null ? NO_TIME_RECORDED : String.valueOf(result.getDeaths());

        } else if (command.startsWith("personal_best")) {
            if (arguments.length != 3) {
                return INVALID_SYNTAX;
            }

            TimeEntry result = getTopPlayerResultForCourse(player.getName(), arguments[2]);
            return result == null ? NO_TIME_RECORDED : DateTimeUtils.displayCurrentTime(result.getTime());

        } else if (command.equals("current_personal_best_deaths")) {
            Course course = parkour.getCourseManager().findByPlayer(player.getName());
            if (course != null) {
                TimeEntry result = getTopPlayerResultForCourse(player.getName(), course.getName());
                return result == null ? NO_TIME_RECORDED : String.valueOf(result.getDeaths());
            }
            return "";

        } else if (command.equals("current_personal_best")) {
            Course course = parkour.getCourseManager().findByPlayer(player.getName());
            if (course != null) {
                TimeEntry result = getTopPlayerResultForCourse(player.getName(), course.getName());
                return result == null ? NO_TIME_RECORDED : DateTimeUtils.displayCurrentTime(result.getTime());
            }
            return "";

        } else if (command.equals("current_course_leader")) {
            Course course = parkour.getCourseManager().findByPlayer(player.getName());
            if (course != null) {
                TimeEntry result = getTopResultForCourse(course.getName());
                return result == null ? NO_TIME_RECORDED : result.getPlayer();
            }
            return "";

        } else if (command.equals("current_course_timer")) {
            ParkourSession session = parkour.getPlayerManager().getParkourSession(player.getName());
//            return session == null ? "" : session.getLiveTime(); TODO
            return "";

        } else if (command.equals("current_course_deaths")) {
            ParkourSession session = parkour.getPlayerManager().getParkourSession(player.getName());
            return session == null ? "" : String.valueOf(session.getDeaths());

        } else if (command.startsWith("topten")) {
            if (arguments.length != 3) {
                return INVALID_SYNTAX;
            }
            if (!Validation.isInteger(arguments[2])) {
                return INVALID_SYNTAX;
            }
            int pos = Integer.parseInt(arguments[2]);
            if (pos < 1) {
                return INVALID_SYNTAX;
            }
            String courseName = arguments[1];
            if (!parkour.getCourseManager().courseExists(courseName)) {
                return NO_TIME_RECORDED;
            }

            TimeEntry result = parkour.getDatabase().getNthBestTime(courseName, pos);

            if (result == null) {
                return NO_TIME_RECORDED;
            }

            if (command.startsWith("toptenx")) {
                String nCol = "&f";
                String tCol = "&f";
                //check if colour codes specified
                if (arguments[0].length() == 9 && arguments[0].substring(7).matches("[0-9a-f]+")) {
                    nCol = "&" + arguments[0].substring(7, 8);
                    tCol = "&" + arguments[0].substring(8);
                }
                return nCol + result.getPlayer() + "&7 - " + tCol + DateTimeUtils.displayCurrentTime(result.getTime());
            }
            return "&f" + pos + ") &b" + result.getPlayer() + "&f in &a" + DateTimeUtils.displayCurrentTime(result.getTime()) + "&f";

        } else if (command.startsWith("course_prize_delay")) {
            if (arguments.length != 4) {
                return INVALID_SYNTAX;
            }
            String courseName = arguments[3];
            if (!parkour.getCourseManager().courseExists(courseName)) {
                return null;
            }
            if (!CourseInfo.hasRewardDelay(courseName)
                    || parkour.getPlayerManager().hasPrizeCooldownDurationPassed(player, courseName, false)) {
                return "0";
            }
            return DateTimeUtils.getTimeRemaining(player, courseName);

        } else if (command.startsWith("course_global_completions")) {
            if (arguments.length != 4) {
                return INVALID_SYNTAX;
            }
            String courseName = arguments[3];
            if (!parkour.getCourseManager().courseExists(courseName)) {
                return null;
            }
            return String.valueOf(CourseInfo.getCompletions(courseName));

        } else if (command.startsWith("lb") || command.startsWith("leaderboard")) {
            if (arguments.length != 4) {
                return INVALID_SYNTAX;
            }
            if (!Validation.isInteger(arguments[3]) || Integer.parseInt(arguments[3]) < 1) {
                return INVALID_SYNTAX;
            }

            String courseName = arguments[1];
            if (!parkour.getCourseManager().courseExists(courseName)) {
                return null;
            }

            int pos = Integer.parseInt(arguments[3]);
            TimeEntry result = parkour.getDatabase().getNthBestTime(courseName, pos);

            if (result == null) {
                return NO_TIME_RECORDED;
            }

            switch (arguments[2]) {
                case "player":
                    return result.getPlayer();

                case "time":
                    return DateTimeUtils.displayCurrentTime(result.getTime());

                case "deaths":
                    return String.valueOf(result.getDeaths());

                default:
                    return INVALID_SYNTAX;
            }
        }

        return null;
    }

    private TimeEntry getTopResultForCourse(String courseName) {
        return parkour.getDatabase().getNthBestTime(courseName, 1);
    }

    private TimeEntry getTopPlayerResultForCourse(String playerName, String courseName) {
        if (!parkour.getCourseManager().courseExists(courseName)) {
            return null;
        }

        List<TimeEntry> time = parkour.getDatabase().getTopPlayerCourseResults(playerName, courseName, 1);
        return time.isEmpty() ? null : time.get(0);
    }
}

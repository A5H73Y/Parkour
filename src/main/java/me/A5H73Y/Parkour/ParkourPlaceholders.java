package me.A5H73Y.Parkour;

import java.util.List;

import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Course.CourseInfo;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.TimeObject;
import me.A5H73Y.Parkour.Other.Validation;
import me.A5H73Y.Parkour.Player.ParkourSession;
import me.A5H73Y.Parkour.Player.PlayerInfo;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class ParkourPlaceholders extends PlaceholderExpansion {

    private Parkour plugin;

    private static final String INVALID_SYNTAX = "Invalid Syntax";
    private static final String NO_TIME_RECORDED = "No time recorded";

    public ParkourPlaceholders(Parkour plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "Parkour";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String message) {
        if (message.equalsIgnoreCase("version")) {
            return String.valueOf(Static.getVersion());

        } else if (message.equalsIgnoreCase("course_count")) {
            return String.valueOf(CourseInfo.getAllCourses().size());

        } else if (message.equalsIgnoreCase("player_count")) {
            return String.valueOf(PlayerMethods.getPlaying().size());

        } else if (message.startsWith("course_record")) {
            String[] temp = message.split("_");
            if (temp.length != 3) {
                return INVALID_SYNTAX;
            }

            TimeObject result = getTopResultForCourse(temp[2]);
            return result == null ? NO_TIME_RECORDED : Utils.displayCurrentTime(result.getTime());

        } else if (message.startsWith("course_record_deaths")) {
            String[] temp = message.split("_");
            if (temp.length != 4) {
                return INVALID_SYNTAX;
            }

            TimeObject result = getTopResultForCourse(temp[3]);
            return result == null ? NO_TIME_RECORDED : String.valueOf(result.getDeaths());

        } else if (message.startsWith("leader")) {
            String[] temp = message.split("_");
            if (temp.length != 2) {
                return INVALID_SYNTAX;
            }

            TimeObject result = getTopResultForCourse(temp[1]);
            return result == null ? NO_TIME_RECORDED : result.getPlayer();
        }

        // Player specific
        if (player == null) {
            return "";
        }

        if (message.equalsIgnoreCase("last_completed")) {
            return PlayerInfo.getLastCompletedCourse(player);

        } else if (message.equalsIgnoreCase("courses_completed")) {
            return PlayerInfo.getNumberOfCoursesCompleted(player);

        } else if (message.equalsIgnoreCase("last_played")) {
            return PlayerInfo.getLastPlayedCourse(player);

        } else if (message.equalsIgnoreCase("parkoins")) {
            return String.valueOf(PlayerInfo.getParkoins(player));

        } else if (message.equalsIgnoreCase("level")) {
            return String.valueOf(PlayerInfo.getParkourLevel(player));

        } else if (message.equalsIgnoreCase("rank")) {
            return PlayerInfo.getRank(player);

        } else if (message.equals("current_course")) {
            Course course = CourseMethods.findByPlayer(player.getName());
            return course == null ? "" : course.getName();

        } else if (message.equals("current_course_record_deaths")) {
            Course course = CourseMethods.findByPlayer(player.getName());
            if (course != null) {
                TimeObject result = getTopResultForCourse(course.getName());
                return result == null ? NO_TIME_RECORDED : String.valueOf(result.getDeaths());
            }
            return "";

        } else if (message.equals("current_course_record")) {
            Course course = CourseMethods.findByPlayer(player.getName());
            if (course != null) {
                TimeObject result = getTopResultForCourse(course.getName());
                return result == null ? NO_TIME_RECORDED : Utils.displayCurrentTime(result.getTime());
            }
            return "";

        } else if (message.startsWith("personal_best_deaths")) {
            String[] temp = message.split("_");
            if (temp.length != 4) {
                return INVALID_SYNTAX;
            }

            TimeObject result = getTopPlayerResultForCourse(player.getName(), temp[3]);
            return result == null ? NO_TIME_RECORDED : String.valueOf(result.getDeaths());

        } else if (message.startsWith("personal_best")) {
            String[] temp = message.split("_");
            if (temp.length != 3) {
                return null;
            }

            TimeObject result = getTopPlayerResultForCourse(player.getName(), temp[2]);
            return result == null ? NO_TIME_RECORDED : Utils.displayCurrentTime(result.getTime());

        } else if (message.equals("current_personal_best_deaths")) {
            Course course = CourseMethods.findByPlayer(player.getName());
            if (course != null) {
                TimeObject result = getTopPlayerResultForCourse(player.getName(), course.getName());
                return result == null ? NO_TIME_RECORDED : String.valueOf(result.getDeaths());
            }
            return "";

        } else if (message.equals("current_personal_best")) {
            Course course = CourseMethods.findByPlayer(player.getName());
            if (course != null) {
                TimeObject result = getTopPlayerResultForCourse(player.getName(), course.getName());
                return result == null ? NO_TIME_RECORDED : Utils.displayCurrentTime(result.getTime());
            }
            return "";

        } else if (message.equals("current_course_leader")) {
            Course course = CourseMethods.findByPlayer(player.getName());
            if (course != null) {
                TimeObject result = getTopResultForCourse(course.getName());
                return result == null ? NO_TIME_RECORDED : result.getPlayer();
            }
            return "";

        } else if (message.equals("current_course_timer")) {
            ParkourSession session = PlayerMethods.getParkourSession(player.getName());
            return session == null ? "" : session.getLiveTime();

        } else if (message.startsWith("topten")) {
            String[] temp = message.split("_");
            if (temp.length != 3) {
                return INVALID_SYNTAX;
            }
            if (!Validation.isInteger(temp[2])) {
                return INVALID_SYNTAX;
            }
            int pos = Integer.parseInt(temp[2]);
            if (pos < 1 || pos > 10) {
                return INVALID_SYNTAX;
            }
            String courseName = temp[1];
            if (!CourseMethods.exist(courseName)) {
                return NO_TIME_RECORDED;
            }
            List<TimeObject> results = DatabaseMethods.getTopCourseResults(courseName, pos);
            if (results.isEmpty()) {
                return NO_TIME_RECORDED;

            } else if (pos > results.size()) {
                return " ";
            }
            TimeObject result = results.get(pos - 1);

            if (message.startsWith("toptenx")) {
                String nCol = "&f";
                String tCol = "&f";
                //check if colour codes specified
                if (temp[0].length() == 9 && temp[0].substring(7).matches("[0-9a-f]+")) {
                    nCol = "&" + temp[0].substring(7,8);
                    tCol = "&" + temp[0].substring(8);
                }
                return nCol + result.getPlayer() + "&7 - " + tCol + Utils.displayCurrentTime(result.getTime());
            }
            return "&f" + pos + ") &b" + result.getPlayer() + "&f in &a" + Utils.displayCurrentTime(result.getTime()) + "&f";
        }

        return null;
    }


    private TimeObject getTopResultForCourse(String courseName) {
        if (!CourseMethods.exist(courseName)) {
            return null;
        }

        List<TimeObject> time = DatabaseMethods.getTopCourseResults(courseName, 1);
        return time.isEmpty() ? null : time.get(0);
    }

    private TimeObject getTopPlayerResultForCourse(String playerName, String courseName) {
        if (!CourseMethods.exist(courseName)) {
            return null;
        }

        List<TimeObject> time = DatabaseMethods.getTopPlayerCourseResults(playerName, courseName, 1);
        return time.isEmpty() ? null : time.get(0);
    }
}

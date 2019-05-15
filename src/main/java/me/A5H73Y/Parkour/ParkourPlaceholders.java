package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Course.CourseInfo;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.TimeObject;
import me.A5H73Y.Parkour.Other.Validation;
import me.A5H73Y.Parkour.Player.PlayerInfo;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.List;

import org.bukkit.entity.Player;

public class ParkourPlaceholders extends PlaceholderExpansion {

    private Parkour plugin;

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
        		return null;
        	}
        	String courseName = temp[2];
        	if (!CourseMethods.exist(courseName)) {
        		return null;
        	}
        	List<TimeObject> time = DatabaseMethods.getTopCourseResults(courseName, 1);
        	if (time.size() == 0) {
        		return "No time recorded";
        	}
        	return Utils.displayCurrentTime(time.get(0).getTime());

        } else if (message.startsWith("course_record_deaths")) {
        	String[] temp = message.split("_");          
        	if (temp.length != 4) {
        		return null;
        	}
        	String courseName = temp[3];
        	if (!CourseMethods.exist(courseName)) {
        		return null;
        	}
        	List<TimeObject> time = DatabaseMethods.getTopCourseResults(courseName, 1);
        	if (time.size() == 0) {
        		return "No time recorded";
        	}
        	return String.valueOf(time.get(0).getDeaths());

        } else if (message.startsWith("leader")) {
        	String[] temp = message.split("_");          
        	if (temp.length != 2) {
        		return null;
        	}
        	String courseName = temp[1];
        	if (!CourseMethods.exist(courseName)) {
        		return null;
        	}
        	List<TimeObject> time = DatabaseMethods.getTopCourseResults(courseName, 1);
        	if (time.size() == 0) {
        		return "No time recorded";
        	}
        	return String.valueOf(time.get(0).getPlayer());
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
        	return course != null ? course.getName() : "";

        } else if (message.equals("current_course_record_deaths")) {
        	Course course = CourseMethods.findByPlayer(player.getName());
        	if (course != null) {
        		List<TimeObject> time = DatabaseMethods.getTopCourseResults(course.getName(), 1);
        		if (time.size() == 0) {
        			return "No time recorded";
        		}
        		return String.valueOf(time.get(0).getDeaths());
        	}
        	return "";

        } else if (message.equals("current_course_record")) {
        	Course course = CourseMethods.findByPlayer(player.getName());
        	if (course != null) {
        		List<TimeObject> time = DatabaseMethods.getTopCourseResults(course.getName(), 1);
        		if (time.size() == 0) {
        			return "No time recorded";
        		}
        		return Utils.displayCurrentTime(time.get(0).getTime());
        	}
        	return "";

        } else if (message.startsWith("personal_best_deaths")) {
        	String[] temp = message.split("_");          
        	if (temp.length != 4) {
        		return null;
        	}
        	String courseName = temp[3];
        	if (!CourseMethods.exist(courseName)) {
        		return null;
        	}
        	List<TimeObject> time = DatabaseMethods.getTopPlayerCourseResults(player.getName(),courseName, 1);
        	if (time.size() == 0) {
        		return "No time recorded";
        	}
        	return String.valueOf(time.get(0).getDeaths());

        } else if (message.startsWith("personal_best")) {
        	String[] temp = message.split("_");          
        	if (temp.length != 3) {
        		return null;
        	}
        	String courseName = temp[2];
        	if (!CourseMethods.exist(courseName)) {
        		return null;
        	}
        	List<TimeObject> time = DatabaseMethods.getTopPlayerCourseResults(player.getName(),courseName, 1);
        	if (time.size() == 0) {
        		return "No time recorded";
        	}
        	return Utils.displayCurrentTime(time.get(0).getTime());

        } else if (message.equals("current_personal_best_deaths")) {
        	Course course = CourseMethods.findByPlayer(player.getName());
        	if (course != null) {
        		List<TimeObject> time = DatabaseMethods.getTopPlayerCourseResults(player.getName(),course.getName(), 1);
        		if (time.size() == 0) {
        			return "No time recorded";
        		}
        		return String.valueOf(time.get(0).getDeaths());
        	}
        	return "";

        } else if (message.equals("current_personal_best")) {
        	Course course = CourseMethods.findByPlayer(player.getName());
        	if (course != null) {
        		List<TimeObject> time = DatabaseMethods.getTopPlayerCourseResults(player.getName(),course.getName(), 1);
        		if (time.size() == 0) {
        			return "No time recorded";
        		}
        		return Utils.displayCurrentTime(time.get(0).getTime());
        	}
        	return "";

        } else if (message.equals("current_course_leader")) {
        	Course course = CourseMethods.findByPlayer(player.getName());
        	if (course != null) {
        		List<TimeObject> time = DatabaseMethods.getTopCourseResults(course.getName(), 1);
        		if (time.size() == 0) {
        			return "No time recorded";
        		}
        		return String.valueOf(time.get(0).getPlayer());
        	}
        	return "";

        } else if (message.equals("current_course_timer")) {
        	if (PlayerMethods.getParkourSession(player.getName()) != null) {
        		return PlayerMethods.getParkourSession(player.getName()).getLiveTime();
        	}
        	return "";

        } else if (message.startsWith("topten")) {
        	String[] temp = message.split("_");
        	if (temp.length != 3) {
        		return null;
        	}
        	String courseName = temp[1];
        	if (!CourseMethods.exist(courseName)) {
        		return null;
        	}
        	if (!Validation.isInteger(temp[2])) {
        		return null;
        	}
        	int pos = Integer.parseInt(temp[2]);
        	if (pos < 1 || pos > 10) {
        		return null;
        	}
        	List<TimeObject> time = DatabaseMethods.getTopCourseResults(courseName, pos);
        	if (time.size() == 0) {
        		return "No time recorded";
        	} else if (pos > time.size()) {
        		return " ";		
        	}
        	if (message.startsWith("toptenx")) {
        		String nCol = "&f";
        		String tCol = "&f";
        		//check if colour codes specified
        		if (temp[0].length() == 9 && temp[0].substring(7).matches("[0-9a-f]+")) {
        			nCol = "&" + temp[0].substring(7,8);
        			tCol = "&" + temp[0].substring(8);
        		}
        		return String.valueOf(nCol + time.get(pos - 1).getPlayer() + "&7 - " + tCol + Utils.displayCurrentTime(time.get(pos - 1).getTime()));
        	}
        	return String.valueOf("&f" + pos + ") &b" +time.get(pos - 1).getPlayer() + "&f in &a" + Utils.displayCurrentTime(time.get(pos - 1).getTime()) + "&f");
        }    

        return null;
    }
}

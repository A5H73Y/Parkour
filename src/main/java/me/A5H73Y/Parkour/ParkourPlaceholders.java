package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Course.CourseInfo;
import me.A5H73Y.Parkour.Player.PlayerInfo;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
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
        }

        return null;
    }
}

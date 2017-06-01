package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.clip.placeholderapi.external.EZPlaceholderHook;

import org.bukkit.entity.Player;

public class ParkourPlaceholders extends EZPlaceholderHook {

    Parkour plugin;
	
    public ParkourPlaceholders(Parkour passplugin){
    	super(passplugin, "parkour");
    	this.plugin = passplugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String message) {
        if (message.equalsIgnoreCase("version")) {
            return String.valueOf(Static.getVersion());

        } else if (message.equalsIgnoreCase("course_count")) {
            return String.valueOf(Static.getCourses().size());

        } else if (message.equalsIgnoreCase("player_count")) {
            return String.valueOf(PlayerMethods.getPlaying().size());
        }

        // Player specific
        if (player == null)
            return "";

        if (message.equalsIgnoreCase("last_completed")) {
            return PlayerMethods.getLastCompletedCourse(player.getName());

        } else if (message.equalsIgnoreCase("parkoins")) {
            return String.valueOf(PlayerMethods.getParkoins(player.getName()));

        } else if (message.equalsIgnoreCase("level")) {
            return String.valueOf(PlayerMethods.getParkourLevel(player.getName()));
        }

        return null;
    }
}

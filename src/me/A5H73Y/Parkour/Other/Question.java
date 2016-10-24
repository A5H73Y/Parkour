package me.A5H73Y.Parkour.Other;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CheckpointMethods;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

public class Question {

	public enum QuestionType {
		DELETE_COURSE,
		DELETE_CHECKPOINT,
		DELETE_LOBBY,
		RESET_COURSE,
		RESET_PLAYER
	}


	private QuestionType type;
	private String argument;

	public Question(QuestionType type, String argument) {
		this.type = type;
		this.argument = argument;
	}

	public QuestionType getType() {
		return type;
	}

	public String getArgument() {
		return argument;
	}

	public void confirm(Player player, QuestionType type, String argument) {
		switch(type){

		case DELETE_COURSE:	
			CourseMethods.deleteCourse(argument, player);
			Utils.logToFile(argument + " was deleted by " + player.getName());
			return;

		case DELETE_CHECKPOINT:
			CheckpointMethods.deleteCheckpoint(argument, player);
			Utils.logToFile(argument + "'s checkpoint " + Parkour.getParkourConfig().getCourseData().getInt(argument + ".Points") + " was deleted by " + player.getName());
			return;

		case DELETE_LOBBY:
			CourseMethods.deleteLobby(argument, player);
			player.sendMessage("Lobby " + ChatColor.AQUA + argument + ChatColor.WHITE + " deleted...");
			Utils.logToFile("lobby " + argument + " was deleted by " + player.getName());

		case RESET_COURSE:
			CourseMethods.resetCourse(argument);
			player.sendMessage(Utils.getTranslation("Parkour.Reset").replace("%COURSE%", argument));
			Utils.logToFile(argument + " was reset by " + player.getName());
			return;

		case RESET_PLAYER:
			PlayerMethods.resetPlayer(argument);
			player.sendMessage(Static.getParkourString() + ChatColor.AQUA + argument + ChatColor.WHITE + " has been reset.");
			Utils.logToFile("player " + argument + " was reset by " + player.getName());
			return;
		}
	}
}

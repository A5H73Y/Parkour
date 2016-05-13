package me.A5H73Y.Parkour.Utilities;

import java.util.List;

import me.A5H73Y.Parkour.Parkour;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

	private boolean commandPermission, chatPrefix, disablePlayerDamage, resetOnLeave, enforceWorld, disableCommands;
	
	//Display
	private boolean displayWelcome;
	
	//Materials
	private Material suicide, hideall, leave;
	
	//Lists
	private List<String> cmdWhitelist; 
	
	//TODO rerun the getters generation when done
	public Settings(){
		FileConfiguration config = Parkour.getParkourConfig().getConfig();
		commandPermission = config.getBoolean("Other.CommandPermissions");
		chatPrefix = config.getBoolean("Other.Parkour.ChatRankPrefix");
		disablePlayerDamage = config.getBoolean("OnCourse.DisablePlayerDamage");
		resetOnLeave = config.getBoolean("OnLeave.ResetPlayer");
		enforceWorld = config.getBoolean("OnJoin.EnforceWorld");
		disableCommands = config.getBoolean("OnCourse.EnforceParkourCommands.Enabled");
		
		suicide = Material.getMaterial(config.getString("OnJoin.Item.Suicide.Material"));
		hideall = Material.getMaterial(config.getString("OnJoin.Item.HideAll.Material"));
		leave = Material.getMaterial(config.getString("OnJoin.Item.Leave.Material"));
		
		displayWelcome = config.getBoolean("Other.Display.JoinWelcomeMessage");
	}

	public boolean isDisableCommands() {
		return disableCommands;
	}

	public Material getSuicide() {
		return suicide;
	}
	
	public Material getHideall() {
		return hideall;
	}

	public Material getLeave() {
		return leave;
	}

	public boolean isEnforceWorld() {
		return enforceWorld;
	}

	public boolean isResetOnLeave() {
		return resetOnLeave;
	}

	public boolean isDisplayWelcome() {
		return displayWelcome;
	}

	public boolean isCommandPermission() {
		return commandPermission;
	}

	public boolean isChatPrefix() {
		return chatPrefix;
	}
	
	public boolean isDisablePlayerDamage() {
		return disablePlayerDamage;
	}

	public List<String> getCmdWhitelist() {
		return cmdWhitelist;
	}
	
}

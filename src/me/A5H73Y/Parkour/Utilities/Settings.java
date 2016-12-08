package me.A5H73Y.Parkour.Utilities;

import java.util.List;

import me.A5H73Y.Parkour.Parkour;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

	private boolean commandPermission, chatPrefix, disablePlayerDamage, resetOnLeave, enforceWorld, disableCommands, allowTrails, signPermission, attemptLessChecks;

	//Display
	private boolean displayWelcome;

	//Materials
	private Material suicide, hideall, leave;

	//Lists
	private List<String> cmdWhitelist; 
	
	//int
	private int maxFallTicks;

	public Settings(){
		FileConfiguration config = Parkour.getParkourConfig().getConfig();
		commandPermission = config.getBoolean("Other.CommandPermissions");
		chatPrefix = config.getBoolean("Other.Parkour.ChatRankPrefix");
		disablePlayerDamage = config.getBoolean("OnCourse.DisablePlayerDamage");
		resetOnLeave = config.getBoolean("OnLeaveServer.LeaveCourse");
		enforceWorld = config.getBoolean("OnJoin.EnforceWorld");
		disableCommands = config.getBoolean("OnCourse.EnforceParkourCommands.Enabled");
		allowTrails = config.getBoolean("OnCourse.AllowTrails");
		signPermission = config.getBoolean("Other.Parkour.SignPermissions");
		attemptLessChecks = config.getBoolean("OnCourse.AttemptLessChecks");

		suicide = Material.getMaterial(config.getString("OnJoin.Item.Suicide.Material"));
		hideall = Material.getMaterial(config.getString("OnJoin.Item.HideAll.Material"));
		leave = Material.getMaterial(config.getString("OnJoin.Item.Leave.Material"));

		displayWelcome = config.getBoolean("Other.Display.JoinWelcomeMessage");
		
		maxFallTicks = config.getInt("OnCourse.MaxFallTicks");
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

	public boolean isResetOnLeave() {
		return resetOnLeave;
	}

	public boolean isEnforceWorld() {
		return enforceWorld;
	}

	public boolean isDisableCommands() {
		return disableCommands;
	}

	public boolean isAllowTrails() {
		return allowTrails;
	}

	public boolean isSignPermission() {
		return signPermission;
	}

	public boolean isDisplayWelcome() {
		return displayWelcome;
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

	public List<String> getCmdWhitelist() {
		return cmdWhitelist;
	}	
	
	public int getMaxFallTicks(){
		return maxFallTicks;
	}
	
	public boolean isAttemptLessChecks(){
		return attemptLessChecks;
	}
}

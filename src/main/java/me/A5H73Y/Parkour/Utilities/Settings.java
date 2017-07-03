package me.A5H73Y.Parkour.Utilities;

import java.util.List;

import me.A5H73Y.Parkour.Parkour;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class Settings {

	private boolean commandPermission, chatPrefix, disablePlayerDamage, resetOnLeave, enforceWorld, disableCommands,
            allowTrails, signPermission, attemptLessChecks, useParkourBlocks;

	//Display
	private boolean displayWelcome;

	//Materials
	private Material lastCheckpoint, hideall, leave;

	//Lists
	private List<String> cmdWhitelist; 

	//int
	private int maxFallTicks, bounceStrength, bounceDuration, speedStrength, speedDuration, titleIn, titleStay, titleOut;

	//Strings
	private String defaultRank;

	//decimals
	private double climbStrength, launchStrength;

	public Settings(){
		FileConfiguration config = Parkour.getPlugin().getConfig();
		commandPermission = config.getBoolean("Other.Parkour.CommandPermissions");
		chatPrefix = config.getBoolean("Other.Parkour.ChatRankPrefix");
		disablePlayerDamage = config.getBoolean("OnCourse.DisablePlayerDamage");
		resetOnLeave = config.getBoolean("OnLeaveServer.LeaveCourse");
		enforceWorld = config.getBoolean("OnJoin.EnforceWorld");
		disableCommands = config.getBoolean("OnCourse.EnforceParkourCommands.Enabled");
		allowTrails = config.getBoolean("OnCourse.AllowTrails");
		signPermission = config.getBoolean("Other.Parkour.SignPermissions");
		attemptLessChecks = config.getBoolean("OnCourse.AttemptLessChecks");
        useParkourBlocks = config.getBoolean("OnCourse.UseParkourBlocks");

		lastCheckpoint = Material.getMaterial(config.getString("OnJoin.Item.LastCheckpoint.Material"));
		hideall = Material.getMaterial(config.getString("OnJoin.Item.HideAll.Material"));
		leave = Material.getMaterial(config.getString("OnJoin.Item.Leave.Material"));

		displayWelcome = config.getBoolean("Other.Display.JoinWelcomeMessage");

		maxFallTicks = config.getInt("OnCourse.MaxFallTicks");

		defaultRank = config.getString("Event.DefaultRank");

		climbStrength = config.getDouble("DefaultBlocks.Climb.Strength");
		launchStrength = config.getDouble("DefaultBlocks.Launch.Strength");
		bounceStrength = config.getInt("DefaultBlocks.Bounce.Strength");
		bounceDuration = config.getInt("DefaultBlocks.Bounce.Duration");
		speedStrength = config.getInt("DefaultBlocks.Speed.Strength");
		speedDuration = config.getInt("DefaultBlocks.Speed.Duration");

		titleIn = config.getInt("DisplayTitle.FadeIn");
		titleStay = config.getInt("DisplayTitle.Stay");
		titleOut = config.getInt("DisplayTitle.FadeOut");
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

	public Material getLastCheckpoint() {
		return lastCheckpoint;
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

	public String getDefaultRank() {
		return defaultRank;
	}

	public int getBounceStrength() {
		return bounceStrength;
	}

	public int getBounceDuration() {
		return bounceDuration;
	}

	public int getSpeedStrength() {
		return speedStrength;
	}

	public int getSpeedDuration() {
		return speedDuration;
	}

	public double getClimbStrength() {
		return climbStrength;
	}

	public double getLaunchStrength() {
		return launchStrength;
	}

	public int getTitleIn() {
		return titleIn;
	}

	public int getTitleStay() {
		return titleStay;
	}

	public int getTitleOut() {
		return titleOut;
	}

    public boolean isUseParkourBlocks() {
        return useParkourBlocks;
    }
}

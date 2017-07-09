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
            allowTrails, signPermission, attemptLessChecks, useParkourKit;

	//Display
	private boolean displayWelcome;

	//Materials
	private Material lastCheckpoint, hideall, leave;

	//Lists
	private List<String> cmdWhitelist; 

	//int
	private int maxFallTicks, titleIn, titleStay, titleOut;

	//Strings
	private String defaultRank;

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
        useParkourKit = config.getBoolean("OnCourse.UseParkourKit");

		lastCheckpoint = Material.getMaterial(config.getString("OnJoin.Item.LastCheckpoint.Material"));
		hideall = Material.getMaterial(config.getString("OnJoin.Item.HideAll.Material"));
		leave = Material.getMaterial(config.getString("OnJoin.Item.Leave.Material"));

		displayWelcome = config.getBoolean("Other.Display.JoinWelcomeMessage");

		maxFallTicks = config.getInt("OnCourse.MaxFallTicks");

		defaultRank = config.getString("Event.DefaultRank");

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

	public int getTitleIn() {
		return titleIn;
	}

	public int getTitleStay() {
		return titleStay;
	}

	public int getTitleOut() {
		return titleOut;
	}

    public boolean isUseParkourKit() {
        return useParkourKit;
    }
}

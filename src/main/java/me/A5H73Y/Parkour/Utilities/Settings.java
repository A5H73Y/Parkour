package me.A5H73Y.Parkour.Utilities;

import java.util.List;

import me.A5H73Y.Parkour.Parkour;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

	private boolean commandPermission, chatPrefix, disablePlayerDamage, resetOnLeave, enforceWorld, disableCommands,
            allowTrails, signPermission, attemptLessChecks, useParkourKit, preventAttackingEntities, displayMilliseconds,
            enforceSafeCheckpoints, chatPrefixOverride, firstCheckAsStart;

	//Display
	private boolean displayWelcome, displayPrizeCooldown;

	//Materials
	private Material lastCheckpointTool, hideallTool, leaveTool, restartTool;

	//Lists
	private List<String> cmdWhitelist; 

	//int
	private int maxFallTicks, titleIn, titleStay, titleOut;

	public Settings(){
		FileConfiguration config = Parkour.getPlugin().getConfig();
		commandPermission = config.getBoolean("Other.Parkour.CommandPermissions");
		chatPrefix = config.getBoolean("Other.Parkour.ChatRankPrefix.Enabled");
		chatPrefixOverride = config.getBoolean("Other.Parkour.ChatRankPrefix.OverrideChat");
		disablePlayerDamage = config.getBoolean("OnCourse.DisablePlayerDamage");
		resetOnLeave = config.getBoolean("OnLeaveServer.LeaveCourse");
		enforceWorld = config.getBoolean("OnJoin.EnforceWorld");
		disableCommands = config.getBoolean("OnCourse.EnforceParkourCommands.Enabled");
		allowTrails = config.getBoolean("OnCourse.AllowTrails");
		signPermission = config.getBoolean("Other.Parkour.SignPermissions");
		attemptLessChecks = config.getBoolean("OnCourse.AttemptLessChecks");
        useParkourKit = config.getBoolean("OnCourse.UseParkourKit");
        preventAttackingEntities = config.getBoolean("OnCourse.PreventAttackingEntities");
        displayMilliseconds = config.getBoolean("Other.Display.ShowMilliseconds");
        enforceSafeCheckpoints = config.getBoolean("Other.EnforceSafeCheckpoints");
        firstCheckAsStart = config.getBoolean("OnJoin.TreatFirstCheckpointAsStart");

		lastCheckpointTool = Material.getMaterial(config.getString("OnJoin.Item.LastCheckpoint.Material"));
		hideallTool = Material.getMaterial(config.getString("OnJoin.Item.HideAll.Material"));
		leaveTool = Material.getMaterial(config.getString("OnJoin.Item.Leave.Material"));
        restartTool = Material.getMaterial(config.getString("OnJoin.Item.Restart.Material"));

		displayWelcome = config.getBoolean("Other.Display.JoinWelcomeMessage");
		displayPrizeCooldown = config.getBoolean("Other.Display.PrizeCooldown");

		maxFallTicks = config.getInt("OnCourse.MaxFallTicks");

		titleIn = config.getInt("DisplayTitle.FadeIn");
		titleStay = config.getInt("DisplayTitle.Stay");
		titleOut = config.getInt("DisplayTitle.FadeOut");

		cmdWhitelist = config.getStringList("OnCourse.EnforceParkourCommands.Whitelist");
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

	public boolean isPreventAttackingEntities() {
		return preventAttackingEntities;
	}

	public Material getLastCheckpointTool() {
        return lastCheckpointTool == Material.AIR ? null : lastCheckpointTool;
	}

	public Material getHideallTool() {
        return hideallTool == Material.AIR ? null : hideallTool;
	}

	public Material getLeaveTool() {
        return leaveTool == Material.AIR ? null : leaveTool;
	}

    public Material getRestartTool() {
        return restartTool == Material.AIR ? null : restartTool;
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

    public boolean isDisplayMilliseconds() {
        return displayMilliseconds;
    }
    
    public boolean isEnforceSafeCheckpoints() {
    	return enforceSafeCheckpoints;
    }

    public boolean isChatPrefixOverride() {
        return chatPrefixOverride;
    }

    public boolean isDisplayPrizeCooldown() {
        return displayPrizeCooldown;
    }

    public boolean isFirstCheckAsStart() {
        return firstCheckAsStart;
    }
}

package me.A5H73Y.Parkour.Utilities;

import java.util.List;

import me.A5H73Y.Parkour.Parkour;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Quickly access Parkour Settings without knowing the property name
 * Bukkit implementation caches the property files for us, so there's no need for us to.
 */
public class Settings {

	FileConfiguration config = Parkour.getPlugin().getConfig();

	/* booleans */

	public boolean isPermissionsForCommands() {
		return config.getBoolean("Other.Parkour.CommandPermissions");
	}

	public boolean isPermissionForSignInteraction() {
		return config.getBoolean("Other.Parkour.SignPermissions");
	}

	public boolean isUseParkourKit() {
		return config.getBoolean("OnCourse.UseParkourKit");
	}

	public boolean isChatPrefix() {
		return config.getBoolean("Other.Parkour.ChatRankPrefix.Enabled");
	}

	public boolean isChatPrefixOverride() {
		return config.getBoolean("Other.Parkour.ChatRankPrefix.OverrideChat");
	}

	public boolean isDisablePlayerDamage() {
		return config.getBoolean("OnCourse.DisablePlayerDamage");
	}

	public boolean isPlayerLeaveCourseOnLeaveServer() {
		return config.getBoolean("OnLeaveServer.LeaveCourse");
	}

	public boolean isEnforceWorld() {
		return config.getBoolean("OnJoin.EnforceWorld");
	}

	public boolean isDisableCommandsOnCourse() {
		return config.getBoolean("OnCourse.EnforceParkourCommands.Enabled");
	}

	public boolean isTrailsEnabled() {
		return config.getBoolean("OnCourse.AllowTrails");
	}

	public boolean isAttemptLessChecks(){
		return config.getBoolean("OnCourse.AttemptLessChecks");
	}

	public boolean isDisplayWelcomeMessage() {
		return config.getBoolean("Other.Display.JoinWelcomeMessage");
	}

	public boolean isDisplayPrizeCooldown() {
		return config.getBoolean("Other.Display.PrizeCooldown");
	}

	public boolean isPreventAttackingEntities() {
		return config.getBoolean("OnCourse.PreventAttackingEntities");
	}

	public boolean isDisplayMilliseconds() {
		return config.getBoolean("Other.Display.ShowMilliseconds");
	}

	public boolean isEnforceSafeCheckpoints() {
		return config.getBoolean("Other.EnforceSafeCheckpoints");
	}

	public boolean isFirstCheckAsStart() {
		return config.getBoolean("OnJoin.TreatFirstCheckpointAsStart");
	}

	/* Materials */

	public Material getLastCheckpointTool() {
		Material lastCheckpointTool = Material.getMaterial(config.getString("OnJoin.Item.LastCheckpoint.Material"));
        return lastCheckpointTool == Material.AIR ? null : lastCheckpointTool;
	}

	public Material getHideallTool() {
		Material hideallTool = Material.getMaterial(config.getString("OnJoin.Item.HideAll.Material"));
        return hideallTool == Material.AIR ? null : hideallTool;
	}

	public Material getLeaveTool() {
		Material leaveTool = Material.getMaterial(config.getString("OnJoin.Item.Leave.Material"));
        return leaveTool == Material.AIR ? null : leaveTool;
	}

    public Material getRestartTool() {
		Material restartTool = Material.getMaterial(config.getString("OnJoin.Item.Restart.Material"));
        return restartTool == Material.AIR ? null : restartTool;
    }

    public Material getCheckpointMaterial() {
		return Material.getMaterial(Parkour.getPlugin().getConfig().getString("OnCourse.CheckpointMaterial"));
	}

    /* Lists */

	public List<String> getWhitelistedCommands() {
		return config.getStringList("OnCourse.EnforceParkourCommands.Whitelist");
	}	

	/* ints */

	public int getMaxFallTicks(){
		return config.getInt("OnCourse.MaxFallTicks");
	}

	public int getTitleIn() {
		return config.getInt("DisplayTitle.FadeIn");
	}

	public int getTitleStay() {
		return config.getInt("DisplayTitle.Stay");
	}

	public int getTitleOut() {
		return config.getInt("DisplayTitle.FadeOut");
	}
}

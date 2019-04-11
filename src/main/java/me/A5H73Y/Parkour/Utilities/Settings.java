package me.A5H73Y.Parkour.Utilities;

import me.A5H73Y.Parkour.Parkour;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Quickly access Parkour Settings without knowing the property name
 * Bukkit implementation caches the property files for us, so there's no need for us to.
 */
public class Settings {

	private FileConfiguration getConfig() {
		return Parkour.getPlugin().getConfig();
	}

	private Particle trailParticle;

	/* booleans */

	public boolean isPermissionsForCommands() {
		return getConfig().getBoolean("Other.Parkour.CommandPermissions");
	}

	public boolean isPermissionForSignInteraction() {
		return getConfig().getBoolean("Other.Parkour.SignPermissions");
	}

	public boolean isUseParkourKit() {
		return getConfig().getBoolean("OnCourse.UseParkourKit");
	}

	public boolean isChatPrefix() {
		return getConfig().getBoolean("Other.Parkour.ChatRankPrefix.Enabled");
	}

	public boolean isChatPrefixOverride() {
		return getConfig().getBoolean("Other.Parkour.ChatRankPrefix.OverrideChat");
	}

	public boolean isDisablePlayerDamage() {
		return getConfig().getBoolean("OnCourse.DisablePlayerDamage");
	}

	public boolean isPlayerLeaveCourseOnLeaveServer() {
		return getConfig().getBoolean("OnLeaveServer.LeaveCourse");
	}

	public boolean isEnforceWorld() {
		return getConfig().getBoolean("OnJoin.EnforceWorld");
	}

	public boolean isDisableCommandsOnCourse() {
		return getConfig().getBoolean("OnCourse.EnforceParkourCommands.Enabled");
	}

	public boolean isTrailsEnabled() {
		return getConfig().getBoolean("OnCourse.Trails.Enabled");
	}

	public boolean isAttemptLessChecks() {
		return getConfig().getBoolean("OnCourse.AttemptLessChecks");
	}

	public boolean isDisplayWelcomeMessage() {
		return getConfig().getBoolean("Other.Display.JoinWelcomeMessage");
	}

	public boolean isDisplayPrizeCooldown() {
		return getConfig().getBoolean("Other.Display.PrizeCooldown");
	}

	public boolean isPreventAttackingEntities() {
		return getConfig().getBoolean("OnCourse.PreventAttackingEntities");
	}

	public boolean isPreventPlayerCollisions() {
		return getConfig().getBoolean("OnCourse.PreventPlayerCollisions");
	}
	public boolean isDisplayMilliseconds() {
		return getConfig().getBoolean("Other.Display.ShowMilliseconds");
	}

	public boolean isEnforceSafeCheckpoints() {
		return getConfig().getBoolean("Other.EnforceSafeCheckpoints");
	}

	public boolean isFirstCheckAsStart() {
		return getConfig().getBoolean("OnJoin.TreatFirstCheckpointAsStart");
	}

	public boolean isAutoStartEnabled() {
		return getConfig().getBoolean("AutoStart.Enabled");
	}

	/* Materials */

	public Material getLastCheckpointTool() {
		Material lastCheckpointTool = Utils.lookupMaterial(getConfig().getString("OnJoin.Item.LastCheckpoint.Material"));
		return lastCheckpointTool == Material.AIR ? null : lastCheckpointTool;
	}

	public int getLastCheckPointToolSlot() {
		return Parkour.getPlugin().getConfig().getInt("OnJoin.Item.LastCheckpoint.Slot", 0);
	}

	public Material getHideallTool() {
		Material hideallTool = Utils.lookupMaterial(getConfig().getString("OnJoin.Item.HideAll.Material"));
		return hideallTool == Material.AIR ? null : hideallTool;
	}

	public int getHideallToolSlot() {
		return Parkour.getPlugin().getConfig().getInt("OnJoin.Item.HideAll.Slot", 1);
	}

	public Material getLeaveTool() {
		Material leaveTool = Utils.lookupMaterial(getConfig().getString("OnJoin.Item.Leave.Material"));
		return leaveTool == Material.AIR ? null : leaveTool;
	}

	public int getLeaveToolSlot() {
		return Parkour.getPlugin().getConfig().getInt("OnJoin.Item.Leave.Slot", 2);
	}

	public Material getRestartTool() {
		Material restartTool = Utils.lookupMaterial(getConfig().getString("OnJoin.Item.Restart.Material"));
		return restartTool == Material.AIR ? null : restartTool;
	}

	public int getRestartToolSlot() {
		return Parkour.getPlugin().getConfig().getInt("OnJoin.Item.Restart.Slot", 3);
	}

	public Material getAutoStartMaterial() {
		return Utils.lookupMaterial(getConfig().getString("AutoStart.Material"));
	}

	public Material getGUIMaterial() {
		Material guiMaterial = Utils.lookupMaterial(getConfig().getString("ParkourGUI.Material"));
		return guiMaterial == null ? Material.BOOK : guiMaterial;
	}

	/* Strings */

	public String getCheckpointMaterial() {
		return Parkour.getPlugin().getConfig().getString("OnCourse.CheckpointMaterial");
	}

	/* Lists */

	public List<String> getWhitelistedCommands() {
		return getConfig().getStringList("OnCourse.EnforceParkourCommands.Whitelist");
	}

	/* ints */

	public int getMaxFallTicks() {
		return getConfig().getInt("OnCourse.MaxFallTicks");
	}

	public int getTitleIn() {
		return getConfig().getInt("DisplayTitle.FadeIn");
	}

	public int getTitleStay() {
		return getConfig().getInt("DisplayTitle.Stay");
	}

	public int getTitleOut() {
		return getConfig().getInt("DisplayTitle.FadeOut");
	}

	public int getAutoStartDelay() {
		return getConfig().getInt("AutoStart.TickDelay");
	}

	public void resetSettings() {
		trailParticle = null;
	}

	public Particle getTrailParticle() {
		if (trailParticle == null) {
			trailParticle = Particle.valueOf(getConfig().getString("OnCourse.Trails.Particle").toUpperCase());
		}
		return trailParticle;
	}
}

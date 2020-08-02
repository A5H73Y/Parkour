package io.github.a5h73y.parkour.configuration.impl;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class DefaultConfig extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "config.yml";
	}

	/**
	 * Initialise the config.yml on startup.
	 * Values will be defaulted if not set.
	 */
	@Override
	protected void initializeConfig() {
		this.options().header("==== Parkour Config ==== #");

		this.addDefault("OnJoin.AllowViaCommand", true);
		this.addDefault("OnJoin.EnforceWorld", false);
		this.addDefault("OnJoin.EnforceFinished", true);
		this.addDefault("OnJoin.FillHealth", true);
		this.addDefault("OnJoin.Item.LastCheckpoint.Material", "ARROW");
		this.addDefault("OnJoin.Item.LastCheckpoint.Slot", 0);
		this.addDefault("OnJoin.Item.HideAll.Material", "BONE");
		this.addDefault("OnJoin.Item.HideAll.Slot", 1);
		this.addDefault("OnJoin.Item.HideAll.Global", true);
		this.addDefault("OnJoin.Item.Leave.Material", "SAPLING");
		this.addDefault("OnJoin.Item.Leave.Slot", 2);
		this.addDefault("OnJoin.Item.Restart.Material", "STICK");
		this.addDefault("OnJoin.Item.Restart.Slot", 3);
		this.addDefault("OnJoin.SetGameMode", "SURVIVAL");
		this.addDefault("OnJoin.TreatFirstCheckpointAsStart", false);

		this.addDefault("OnCourse.AdminPlaceBreakBlocks", true);
		this.addDefault("OnCourse.AttemptLessChecks", false);
		this.addDefault("OnCourse.CheckpointMaterial", "STONE_PLATE");
		this.addDefault("OnCourse.DieInLiquid", false);
		this.addDefault("OnCourse.DieInVoid", false);
		this.addDefault("OnCourse.DisableItemDrop", false);
		this.addDefault("OnCourse.DisableItemPickup", false);
		this.addDefault("OnCourse.DisablePlayerDamage", false);
		this.addDefault("OnCourse.DisableFallDamage", false);
		this.addDefault("OnCourse.DisableFly", true);
		this.addDefault("OnCourse.DisplayLiveTime", false);
		this.addDefault("OnCourse.EnforceParkourSigns", true);
		this.addDefault("OnCourse.EnforceWorld.Enabled", true);
		this.addDefault("OnCourse.EnforceWorld.LeaveCourse", false);
		this.addDefault("OnCourse.MaxFallTicks", 80);
		this.addDefault("OnCourse.PreventPlateStick", false);
		this.addDefault("OnCourse.PreventOpeningOtherInventories", false);
		this.addDefault("OnCourse.PreventAttackingEntities", false);
		this.addDefault("OnCourse.PreventJoiningDifferentCourse", false);
		this.addDefault("OnCourse.PreventPlayerCollisions", false);
		this.addDefault("OnCourse.SneakToInteractItems", true);
		this.addDefault("OnCourse.UseParkourKit", true);
		this.addDefault("OnCourse.EnforceParkourCommands.Enabled", true);
		this.addDefault("OnCourse.EnforceParkourCommands.Whitelist", Collections.singletonList("login"));

		this.addDefault("OnFinish.BroadcastLevel", 3);
		this.addDefault("OnFinish.DefaultPrize.Material", "DIAMOND");
		this.addDefault("OnFinish.DefaultPrize.Amount", 1);
		this.addDefault("OnFinish.DefaultPrize.XP", 0);
		this.addDefault("OnFinish.DisplayNewRecords", false);
		this.addDefault("OnFinish.DisplayStats", true);
		this.addDefault("OnFinish.EnablePrizes", true);
		this.addDefault("OnFinish.EnforceCompletion", true);
		this.addDefault("OnFinish.SaveUserCompletedCourses", false);
		this.addDefault("OnFinish.SetGameMode", "SURVIVAL");
		this.addDefault("OnFinish.TeleportAway", true);
		this.addDefault("OnFinish.TeleportDelay", 0);
		this.addDefault("OnFinish.TeleportToJoinLocation", false);
		this.addDefault("OnFinish.UpdatePlayerDatabaseTime", false);

		this.addDefault("OnLeave.TeleportToLinkedLobby", false);

		this.addDefault("OnDie.ResetTimeWithNoCheckpoint", false);
		this.addDefault("OnDie.SetXPBarToDeathCount", false);

		this.addDefault("OnLeaveServer.LeaveCourse", false);
		this.addDefault("OnLeaveServer.TeleportToLastCheckpoint", false);

		this.addDefault("ParkourModes.Challenge.HidePlayers", true);
		this.addDefault("ParkourModes.Challenge.CountdownFrom", 5);
		this.addDefault("ParkourModes.Moon.Strength", 5);
		this.addDefault("ParkourModes.Speedy.SetSpeed", 0.8);
		this.addDefault("ParkourModes.Speedy.ResetSpeed", 0.2);
		this.addDefault("ParkourModes.Dropper.FallDamage", false);

		this.addDefault("DisplayTitle.FadeIn", 5);
		this.addDefault("DisplayTitle.Stay", 20);
		this.addDefault("DisplayTitle.FadeOut", 5);
		this.addDefault("DisplayTitle.JoinCourse", true);
		this.addDefault("DisplayTitle.Checkpoint", true);
		this.addDefault("DisplayTitle.RewardLevel", true);
		this.addDefault("DisplayTitle.Death", true);
		this.addDefault("DisplayTitle.Leave", true);
		this.addDefault("DisplayTitle.Finish", true);

		this.addDefault("AutoStart.Enabled", true);
		this.addDefault("AutoStart.Material", "BEDROCK");
		this.addDefault("AutoStart.TickDelay", 0);

		this.addDefault("Scoreboard.Enabled", false);
		this.addDefault("Scoreboard.Display.CourseName", true);
		this.addDefault("Scoreboard.Display.BestTimeEver", true);
		this.addDefault("Scoreboard.Display.BestTimeEverName", true);
		this.addDefault("Scoreboard.Display.BestTimeByMe", true);
		this.addDefault("Scoreboard.Display.CurrentTime", true);
		this.addDefault("Scoreboard.Display.CurrentDeaths", true);
		this.addDefault("Scoreboard.Display.Checkpoints", true);

		this.addDefault("ParkourGUI.Enabled", false);
		this.addDefault("ParkourGUI.Rows", 2);
		this.addDefault("ParkourGUI.Material", "BOOK");

		this.addDefault("Other.CheckForUpdates", true);
		this.addDefault("Other.BountifulAPI.Enabled", true);
		this.addDefault("Other.Economy.Enabled", true);
		this.addDefault("Other.PlaceholderAPI.Enabled", true);
		this.addDefault("Other.LogToFile", true);
		this.addDefault("Other.UseSounds", true);
		this.addDefault("Other.EnforceSafeCheckpoints", true);
		this.addDefault("Other.UseAutoTabCompletion", true);
		this.addDefault("Other.ParkourKit.ReplaceInventory", true);
		this.addDefault("Other.ParkourKit.GiveSign", true);
		this.addDefault("Other.Parkour.ChatRankPrefix.Enabled", false);
		this.addDefault("Other.Parkour.ChatRankPrefix.OverrideChat", true);
		this.addDefault("Other.Parkour.SignProtection", true);
		this.addDefault("Other.Parkour.InventoryManagement", true);
		this.addDefault("Other.Parkour.SignPermissions", false);
		this.addDefault("Other.Parkour.CommandPermissions", false);
		this.addDefault("Other.Display.JoinWelcomeMessage", true);
		this.addDefault("Other.Display.LevelReward", true);
		this.addDefault("Other.Display.ShowMilliseconds", false);
		this.addDefault("Other.Display.PrizeCooldown", true);
		this.addDefault("Other.OnServerShutdown.BackupFiles", false);
		this.addDefault("Other.Leaderboard.MaxEntries", 10);

		this.addDefault("SQLite.PathOverride", "");
		this.addDefault("MySQL.Use", false);
		this.addDefault("MySQL.Host", "Host");
		this.addDefault("MySQL.Port", 3306);
		this.addDefault("MySQL.User", "Username");
		this.addDefault("MySQL.Password", "Password");
		this.addDefault("MySQL.Database", "Database");
		this.addDefault("MySQL.Params", "?useSSL=false");

		this.addDefault("Version", Double.parseDouble(Parkour.getInstance().getDescription().getVersion()));

		this.addDefault("Lobby.Set", false);
		this.addDefault("Lobby.EnforceWorld", false);

		this.options().copyDefaults(true);
	}

	public String getSignHeader() {
		return TranslationUtils.getTranslation("Parkour.SignHeader", false);
	}

	public String getStrippedSignHeader() {
		return ChatColor.stripColor(getSignHeader());
	}

	public boolean isPermissionsForCommands() {
		return this.getBoolean("Other.Parkour.CommandPermissions");
	}

	public boolean isPermissionForSignInteraction() {
		return this.getBoolean("Other.Parkour.SignPermissions");
	}

	public boolean isUseParkourKit() {
		return this.getBoolean("OnCourse.UseParkourKit");
	}

	public boolean isChatPrefix() {
		return this.getBoolean("Other.Parkour.ChatRankPrefix.Enabled");
	}

	public boolean isChatPrefixOverride() {
		return this.getBoolean("Other.Parkour.ChatRankPrefix.OverrideChat");
	}

	public boolean isDisablePlayerDamage() {
		return this.getBoolean("OnCourse.DisablePlayerDamage");
	}

	public boolean isPlayerLeaveCourseOnLeaveServer() {
		return this.getBoolean("OnLeaveServer.LeaveCourse");
	}

	public boolean isJoinEnforceWorld() {
		return this.getBoolean("OnJoin.EnforceWorld");
	}

	public boolean isCourseEnforceWorld() {
		return this.getBoolean("OnCourse.EnforceWorld.Enabled");
	}

	public boolean isCourseEnforceWorldLeaveCourse() {
		return this.getBoolean("OnCourse.EnforceWorld.LeaveCourse");
	}

	public boolean isDisableCommandsOnCourse() {
		return this.getBoolean("OnCourse.EnforceParkourCommands.Enabled");
	}

	public boolean isAttemptLessChecks() {
		return this.getBoolean("OnCourse.AttemptLessChecks");
	}

	public boolean isDisplayWelcomeMessage() {
		return this.getBoolean("Other.Display.JoinWelcomeMessage");
	}

	public boolean isDisplayPrizeCooldown() {
		return this.getBoolean("Other.Display.PrizeCooldown");
	}

	public boolean isPreventAttackingEntities() {
		return this.getBoolean("OnCourse.PreventAttackingEntities");
	}

	public boolean isPreventPlayerCollisions() {
		return this.getBoolean("OnCourse.PreventPlayerCollisions");
	}

	public boolean isDisplayMilliseconds() {
		return this.getBoolean("Other.Display.ShowMilliseconds");
	}

	public boolean isEnforceSafeCheckpoints() {
		return this.getBoolean("Other.EnforceSafeCheckpoints");
	}

	public boolean isFirstCheckAsStart() {
		return this.getBoolean("OnJoin.TreatFirstCheckpointAsStart");
	}

	public boolean isAutoStartEnabled() {
		return this.getBoolean("AutoStart.Enabled");
	}

	public boolean isTeleportToJoinLocation() {
		return this.getBoolean("OnFinish.TeleportToJoinLocation");
	}

	public boolean isSoundEnabled() {
		return this.getBoolean("Other.UseSounds");
	}

	/* Materials */
	public Material getLastCheckpointTool() {
		Material lastCheckpointTool = MaterialUtils.lookupMaterial(this.getString("OnJoin.Item.LastCheckpoint.Material"));
		return lastCheckpointTool == Material.AIR ? null : lastCheckpointTool;
	}

	public int getLastCheckPointToolSlot() {
		return this.getInt("OnJoin.Item.LastCheckpoint.Slot", 0);
	}

	public Material getHideallTool() {
		Material hideallTool = MaterialUtils.lookupMaterial(this.getString("OnJoin.Item.HideAll.Material"));
		return hideallTool == Material.AIR ? null : hideallTool;
	}

	public int getHideallToolSlot() {
		return this.getInt("OnJoin.Item.HideAll.Slot", 1);
	}

	public Material getLeaveTool() {
		Material leaveTool = MaterialUtils.lookupMaterial(this.getString("OnJoin.Item.Leave.Material"));
		return leaveTool == Material.AIR ? null : leaveTool;
	}

	public int getLeaveToolSlot() {
		return this.getInt("OnJoin.Item.Leave.Slot", 2);
	}

	public Material getRestartTool() {
		Material restartTool = MaterialUtils.lookupMaterial(this.getString("OnJoin.Item.Restart.Material"));
		return restartTool == Material.AIR ? null : restartTool;
	}

	public int getRestartToolSlot() {
		return this.getInt("OnJoin.Item.Restart.Slot", 3);
	}

	public Material getAutoStartMaterial() {
		return MaterialUtils.lookupMaterial(this.getString("AutoStart.Material"));
	}

	public Material getGUIMaterial() {
		Material guiMaterial = MaterialUtils.lookupMaterial(this.getString("ParkourGUI.Material"));
		return guiMaterial == null ? Material.BOOK : guiMaterial;
	}

	/* Strings */

	public String getCheckpointMaterial() {
		return this.getString("OnCourse.CheckpointMaterial");
	}

	/* Lists */

	public List<String> getWhitelistedCommands() {
		return this.getStringList("OnCourse.EnforceParkourCommands.Whitelist");
	}

	/* ints */

	public int getMaxFallTicks() {
		return this.getInt("OnCourse.MaxFallTicks");
	}

	public int getTitleIn() {
		return this.getInt("DisplayTitle.FadeIn");
	}

	public int getTitleStay() {
		return this.getInt("DisplayTitle.Stay");
	}

	public int getTitleOut() {
		return this.getInt("DisplayTitle.FadeOut");
	}

	public int getAutoStartDelay() {
		return this.getInt("AutoStart.TickDelay");
	}

	public int getLeaderboardMaxEntries() {
		return this.getInt("Other.Leaderboard.MaxEntries");
	}
}

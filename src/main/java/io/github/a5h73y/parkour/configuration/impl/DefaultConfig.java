package io.github.a5h73y.parkour.configuration.impl;

import com.cryptomorin.xseries.XMaterial;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.SoundType;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

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
		this.addDefault("OnJoin.EnforceReady", true);
		this.addDefault("OnJoin.FillHealth.Enabled", true);
		this.addDefault("OnJoin.FillHealth.Amount", 20);
		this.addDefault("OnJoin.SetGameMode", "SURVIVAL");
		this.addDefault("OnJoin.TreatFirstCheckpointAsStart", false);
		this.addDefault("OnJoin.PerCoursePermission", false);
		this.addDefault("OnJoin.TeleportPlayer", true);
		this.addDefault("OnJoin.BroadcastLevel", "NONE");

		this.addDefault("OnCourse.AnybodyPlaceBreakBlocks", false);
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
		this.addDefault("OnCourse.PreventEntitiesAttacking", true);
		this.addDefault("OnCourse.PreventJoiningDifferentCourse", false);
		this.addDefault("OnCourse.PreventPlayerCollisions", false);
		this.addDefault("OnCourse.SneakToInteractItems", true);
		this.addDefault("OnCourse.TreatLastCheckpointAsFinish", false);
		this.addDefault("OnCourse.UseParkourKit", true);
		this.addDefault("OnCourse.EnforceParkourCommands.Enabled", true);
		this.addDefault("OnCourse.EnforceParkourCommands.Whitelist", Collections.singletonList("login"));

		this.addDefault("OnFinish.BroadcastLevel", "GLOBAL");
		this.addDefault("OnFinish.DefaultPrize.Material", "DIAMOND");
		this.addDefault("OnFinish.DefaultPrize.Amount", 1);
		this.addDefault("OnFinish.DefaultPrize.XP", 0);
		this.addDefault("OnFinish.DefaultPrize.Command", "");
		this.addDefault("OnFinish.DisplayNewRecords", false);
		this.addDefault("OnFinish.DisplayStats", true);
		this.addDefault("OnFinish.EnablePrizes", true);
		this.addDefault("OnFinish.EnforceCompletion", true);
		this.addDefault("OnFinish.CompletedCourses.Enabled", true);
		this.addDefault("OnFinish.CompletedCourses.JoinMessage", false);
		this.addDefault("OnFinish.SetGameMode", "SURVIVAL");
		this.addDefault("OnFinish.TeleportAway", true);
		this.addDefault("OnFinish.TeleportDelay", 0);
		this.addDefault("OnFinish.TeleportToJoinLocation", false);
		this.addDefault("OnFinish.UpdatePlayerDatabaseTime", true);

		this.addDefault("OnLeave.TeleportToLinkedLobby", false);
		this.addDefault("OnLeave.DestroyCourseProgress", true);
		this.addDefault("OnLeave.TeleportAway", true);

		this.addDefault("OnDie.ResetProgressWithNoCheckpoint", false);
		this.addDefault("OnDie.SetXPBarToDeathCount", false);

		this.addDefault("OnLeaveServer.LeaveCourse", false);
		this.addDefault("OnLeaveServer.TeleportToLastCheckpoint", false);

		this.addDefault("ParkourTool.LastCheckpoint.Material", "ARROW");
		this.addDefault("ParkourTool.LastCheckpoint.Slot", 0);
		this.addDefault("ParkourTool.HideAll.Material", "BONE");
		this.addDefault("ParkourTool.HideAll.Slot", 1);
		this.addDefault("ParkourTool.HideAll.Global", true);
		this.addDefault("ParkourTool.HideAll.ActivateOnJoin", false);
		this.addDefault("ParkourTool.HideAllEnabled.Material", "BONE");
		this.addDefault("ParkourTool.HideAllEnabled.Slot", 1);
		this.addDefault("ParkourTool.Leave.Material", XMaterial.OAK_SAPLING.parseMaterial().name());
		this.addDefault("ParkourTool.Leave.Slot", 2);
		this.addDefault("ParkourTool.Restart.Material", "STICK");
		this.addDefault("ParkourTool.Restart.Slot", 3);
		this.addDefault("ParkourTool.Restart.SecondCooldown", 6);
		this.addDefault("ParkourTool.Freedom.Material", XMaterial.REDSTONE_TORCH.parseMaterial().name());
		this.addDefault("ParkourTool.Freedom.Slot", 4);
		this.addDefault("ParkourTool.Rockets.Material", XMaterial.FIREWORK_ROCKET.parseMaterial().name());
		this.addDefault("ParkourTool.Rockets.Slot", 4);

		this.addDefault("ParkourChallenge.HidePlayers", true);
		this.addDefault("ParkourChallenge.CountdownFrom", 5);
		this.addDefault("ParkourChallenge.PrepareOnAccept", false);

		this.addDefault("ParkourModes.Speedy.SetSpeed", 0.7);
		this.addDefault("ParkourModes.Speedy.ResetSpeed", 0.2);
		this.addDefault("ParkourModes.Dropper.FallDamage", false);
		this.addDefault("ParkourModes.Rockets.Invert", false);
		this.addDefault("ParkourModes.Rockets.Delay", 1);
		this.addDefault("ParkourModes.FreeCheckpoint.ManualCheckpointCommandEnabled", false);

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
		this.addDefault("AutoStart.IncludeWorldName", false);

		this.addDefault("Scoreboard.Enabled", false);
		this.addDefault("Scoreboard.CourseName.Enabled", true);
		this.addDefault("Scoreboard.CourseName.Sequence", 1);
		this.addDefault("Scoreboard.BestTimeEver.Enabled", true);
		this.addDefault("Scoreboard.BestTimeEver.Sequence", 2);
		this.addDefault("Scoreboard.BestTimeEverName.Enabled", true);
		this.addDefault("Scoreboard.BestTimeEverName.Sequence", 3);
		this.addDefault("Scoreboard.MyBestTime.Enabled", true);
		this.addDefault("Scoreboard.MyBestTime.Sequence", 4);
		this.addDefault("Scoreboard.CurrentDeaths.Enabled", true);
		this.addDefault("Scoreboard.CurrentDeaths.Sequence", 5);
		this.addDefault("Scoreboard.Checkpoints.Enabled", true);
		this.addDefault("Scoreboard.Checkpoints.Sequence", 6);
		this.addDefault("Scoreboard.LiveTimer.Enabled", true);
		this.addDefault("Scoreboard.LiveTimer.Sequence", 7);
		this.addDefault("Scoreboard.RemainingDeaths.Enabled", false);
		this.addDefault("Scoreboard.RemainingDeaths.Sequence", 8);

		this.addDefault("Sounds.Enabled", true);
		this.addDefault("Sounds.JoinCourse.Enabled", false);
		this.addDefault("Sounds.JoinCourse.Sound", "BLOCK_NOTE_BLOCK_PLING");
		this.addDefault("Sounds.JoinCourse.Volume", 0.05f);
		this.addDefault("Sounds.JoinCourse.Pitch", 1.75f);
		this.addDefault("Sounds.SecondIncrement.Enabled", true);
		this.addDefault("Sounds.SecondIncrement.Sound", "BLOCK_NOTE_BLOCK_PLING");
		this.addDefault("Sounds.SecondIncrement.Volume", 0.05f);
		this.addDefault("Sounds.SecondIncrement.Pitch", 1.75f);
		this.addDefault("Sounds.SecondDecrement.Enabled", true);
		this.addDefault("Sounds.SecondDecrement.Sound", "BLOCK_NOTE_BLOCK_PLING");
		this.addDefault("Sounds.SecondDecrement.Volume", 0.05f);
		this.addDefault("Sounds.SecondDecrement.Pitch", 4f);
		this.addDefault("Sounds.PlayerDeath.Enabled", true);
		this.addDefault("Sounds.PlayerDeath.Sound", "ENTITY_PLAYER_DEATH");
		this.addDefault("Sounds.PlayerDeath.Volume", 0.1f);
		this.addDefault("Sounds.PlayerDeath.Pitch", 1.75f);
		this.addDefault("Sounds.CheckpointAchieved.Enabled", true);
		this.addDefault("Sounds.CheckpointAchieved.Sound", "BLOCK_NOTE_BLOCK_CHIME");
		this.addDefault("Sounds.CheckpointAchieved.Volume", 0.1f);
		this.addDefault("Sounds.CheckpointAchieved.Pitch", 1.75f);
		this.addDefault("Sounds.CourseFinished.Enabled", true);
		this.addDefault("Sounds.CourseFinished.Sound", "BLOCK_CONDUIT_ACTIVATE");
		this.addDefault("Sounds.CourseFinished.Volume", 0.1f);
		this.addDefault("Sounds.CourseFinished.Pitch", 1.75f);
		this.addDefault("Sounds.CourseFailed.Enabled", true);
		this.addDefault("Sounds.CourseFailed.Sound", "BLOCK_CONDUIT_DEACTIVATE");
		this.addDefault("Sounds.CourseFailed.Volume", 0.1f);
		this.addDefault("Sounds.CourseFailed.Pitch", 1.75f);

		this.addDefault("ParkourGUI.Material", "BOOK");
		this.addDefault("ParkourGUI.FillerMaterial", "CYAN_STAINED_GLASS_PANE");

		this.addDefault("Other.CheckForUpdates", true);
		this.addDefault("Other.LogToFile", true);
		this.addDefault("Other.EnforceSafeCheckpoints", true);
		this.addDefault("Other.UseAutoTabCompletion", true);
		this.addDefault("Other.ParkourKit.ReplaceInventory", true);
		this.addDefault("Other.ParkourKit.GiveSign", true);
		this.addDefault("Other.ParkourKit.LegacyGroundDetection", false);
		this.addDefault("Other.Parkour.ChatRankPrefix.Enabled", false);
		this.addDefault("Other.Parkour.ChatRankPrefix.OverrideChat", true);
		this.addDefault("Other.Parkour.SignProtection", true);
		this.addDefault("Other.Parkour.InventoryManagement", true);
		this.addDefault("Other.Parkour.SignUsePermissions", false);
		this.addDefault("Other.Parkour.CommandUsePermissions", false);
		this.addDefault("Other.Parkour.MaximumParkourLevel", 99999999);
		this.addDefault("Other.Display.JoinWelcomeMessage", true);
		this.addDefault("Other.Display.LevelReward", true);
		this.addDefault("Other.Display.ShowMilliseconds", false);
		this.addDefault("Other.Display.PrizeCooldown", true);
		this.addDefault("Other.Display.OnlyReadyCourses", false);
		this.addDefault("Other.OnServerShutdown.BackupFiles", false);
		this.addDefault("Other.OnPlayerBan.ResetParkourInfo", false);
		this.addDefault("Other.OnSetPlayerParkourLevel.UpdateParkourRank", true);
		this.addDefault("Other.OnVoid.TeleportToLobby", false);

		this.addDefault("Plugin.BountifulAPI.Enabled", true);
		this.addDefault("Plugin.Vault.Enabled", true);
		this.addDefault("Plugin.PlaceholderAPI.Enabled", true);
		this.addDefault("Plugin.PlaceholderAPI.CacheTime", 15);
		this.addDefault("Plugin.AAC.Enabled", true);

		this.addDefault("Database.MaximumCoursesCached", 10);
		this.addDefault("SQLite.PathOverride", "");
		this.addDefault("MySQL.Use", false);
		this.addDefault("MySQL.URL", "jdbc:mysql://(HOST):(PORT)/(DATABASE)?useSSL=false");
		this.addDefault("MySQL.Username", "Username");
		this.addDefault("MySQL.Password", "Password");
		this.addDefault("MySQL.LegacyDriver", false);

		this.addDefault("Version", Parkour.getInstance().getDescription().getVersion());

		this.addDefault("LobbySettings.EnforceWorld", false);

		this.options().copyDefaults(true);
	}

	/**
	 * Add a command to the whitelist.
	 *
	 * @param sender requesting player
	 * @param command command to whitelist
	 */
	public void addWhitelistedCommand(CommandSender sender, String command) {
		List<String> whitelistedCommands = getWhitelistedCommands();
		if (whitelistedCommands.contains(command.toLowerCase())) {
			TranslationUtils.sendMessage(sender, "This command is already whitelisted!");
			return;
		}

		whitelistedCommands.add(command.toLowerCase());
		set("OnCourse.EnforceParkourCommands.Whitelist", whitelistedCommands);
		save();

		TranslationUtils.sendMessage(sender, "Command &b" + command + "&f added to the whitelisted commands!");
	}

	public String getSignHeader() {
		return TranslationUtils.getTranslation("Parkour.SignHeader", false);
	}

	public String getStrippedSignHeader() {
		return ChatColor.stripColor(getSignHeader());
	}

	public String getDefaultPrizeCommand() {
		return this.getString("OnFinish.DefaultPrize.Command");
	}

	public boolean isPermissionsForCommands() {
		return this.getBoolean("Other.Parkour.CommandUsePermissions");
	}

	public boolean isPermissionForSignInteraction() {
		return this.getBoolean("Other.Parkour.SignUsePermissions");
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

	public boolean isPreventEntitiesAttacking() {
		return this.getBoolean("OnCourse.PreventEntitiesAttacking");
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

	public boolean isTreatFirstCheckpointAsStart() {
		return this.getBoolean("OnJoin.TreatFirstCheckpointAsStart");
	}

	public boolean isAutoStartEnabled() {
		return this.getBoolean("AutoStart.Enabled");
	}

	public boolean isAutoStartIncludeWorld() {
		return this.getBoolean("AutoStart.IncludeWorldName");
	}

	public boolean isTeleportToJoinLocation() {
		return this.getBoolean("OnFinish.TeleportToJoinLocation");
	}

	public boolean isSoundEnabled() {
		return this.getBoolean("Sounds.Enabled");
	}

	public boolean isSoundEnabled(SoundType soundType) {
		return this.getBoolean("Sounds." + soundType.getConfigEntry() + ".Enabled");
	}

	public boolean isCompletedCoursesEnabled() {
		return this.getBoolean("OnFinish.CompletedCourses.Enabled");
	}

	public boolean isLegacyGroundDetection() {
		return this.getBoolean("Other.ParkourKit.LegacyGroundDetection");
	}

	public boolean isVoidDetection() {
		return this.getBoolean("Other.OnVoid.TeleportToLobby");
	}

	public boolean isLeaveDestroyCourseProgress() {
		return this.getBoolean("OnLeave.DestroyCourseProgress");
	}

	/* Materials */
	public Material getLastCheckpointTool() {
		return getMaterialOrDefault("ParkourTool.LastCheckpoint.Material", Material.AIR);
	}

	public Material getHideAllDisabledTool() {
		return getMaterialOrDefault("ParkourTool.HideAll.Material", Material.AIR);
	}

	public Material getHideAllEnabledTool() {
		return getMaterialOrDefault("ParkourTool.HideAllEnabled.Material", Material.AIR);
	}

	public Material getLeaveTool() {
		return getMaterialOrDefault("ParkourTool.Leave.Material", Material.AIR);
	}

	public Material getRestartTool() {
		return getMaterialOrDefault("ParkourTool.Restart.Material", Material.AIR);
	}

	public Material getAutoStartMaterial() {
		return MaterialUtils.lookupMaterial(this.getString("AutoStart.Material"));
	}

	public Material getGuiMaterial() {
		return getMaterialOrDefault("ParkourGUI.Material", Material.BOOK);
	}

	public Material getGuiFillerMaterial() {
		return getMaterialOrDefault("ParkourGUI.FillerMaterial", Material.AIR);
	}

	private Material getMaterialOrDefault(String configPath, Material defaultMaterial) {
		Material matchingMaterial = MaterialUtils.lookupMaterial(this.getString(configPath));
		return matchingMaterial != null ? matchingMaterial : defaultMaterial;
	}

	public Material getCheckpointMaterial() {
		return MaterialUtils.lookupMaterial(this.getString("OnCourse.CheckpointMaterial"));
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

	public int getMaximumCoursesCached() {
		return this.getInt("Database.MaximumCoursesCached");
	}
}

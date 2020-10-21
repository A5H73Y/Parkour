package io.github.a5h73y.parkour.configuration.impl;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
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
		this.addDefault("OnJoin.FillHealth", true);
		this.addDefault("OnJoin.Item.LastCheckpoint.Material", "ARROW");
		this.addDefault("OnJoin.Item.LastCheckpoint.Slot", 0);
		this.addDefault("OnJoin.Item.HideAll.Material", "BONE");
		this.addDefault("OnJoin.Item.HideAll.Slot", 1);
		this.addDefault("OnJoin.Item.HideAll.Global", true);
		this.addDefault("OnJoin.Item.HideAllEnabled.Material", "BONE");
		this.addDefault("OnJoin.Item.Leave.Material", "OAK_SAPLING");
		this.addDefault("OnJoin.Item.Leave.Slot", 2);
		this.addDefault("OnJoin.Item.Restart.Material", "STICK");
		this.addDefault("OnJoin.Item.Restart.Slot", 3);
		this.addDefault("OnJoin.SetGameMode", "SURVIVAL");
		this.addDefault("OnJoin.TreatFirstCheckpointAsStart", false);
		this.addDefault("OnJoin.PerCoursePermission", false);

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
		this.addDefault("OnCourse.PreventJoiningDifferentCourse", false);
		this.addDefault("OnCourse.PreventPlayerCollisions", false);
		this.addDefault("OnCourse.SneakToInteractItems", true);
		this.addDefault("OnCourse.UseParkourKit", true);
		this.addDefault("OnCourse.EnforceParkourCommands.Enabled", true);
		this.addDefault("OnCourse.EnforceParkourCommands.Whitelist", Collections.singletonList("login"));

		this.addDefault("OnFinish.BroadcastLevel", "GLOBAL");
		this.addDefault("OnFinish.DefaultPrize.Material", "DIAMOND");
		this.addDefault("OnFinish.DefaultPrize.Amount", 1);
		this.addDefault("OnFinish.DefaultPrize.XP", 0);
		this.addDefault("OnFinish.DisplayNewRecords", false);
		this.addDefault("OnFinish.DisplayStats", true);
		this.addDefault("OnFinish.EnablePrizes", true);
		this.addDefault("OnFinish.EnforceCompletion", true);
		this.addDefault("OnFinish.CompletedCourses.Enabled", false);
		this.addDefault("OnFinish.CompletedCourses.JoinMessage", true);
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
		this.addDefault("ParkourGUI.FillerMaterial", "CYAN_STAINED_GLASS_PANE");

		this.addDefault("Other.CheckForUpdates", true);
		this.addDefault("Other.LogToFile", true);
		this.addDefault("Other.UseSounds", true);
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
		this.addDefault("Other.Display.JoinWelcomeMessage", true);
		this.addDefault("Other.Display.LevelReward", true);
		this.addDefault("Other.Display.ShowMilliseconds", false);
		this.addDefault("Other.Display.PrizeCooldown", true);
		this.addDefault("Other.Display.OnlyReadyCourses", false);
		this.addDefault("Other.Display.CourseCompleted", false);
		this.addDefault("Other.OnServerShutdown.BackupFiles", false);

		this.addDefault("Plugin.BountifulAPI.Enabled", true);
		this.addDefault("Plugin.Vault.Enabled", true);
		this.addDefault("Plugin.PlaceholderAPI.Enabled", true);
		this.addDefault("Plugin.AAC.Enabled", true);

		this.addDefault("Database.MaximumCoursesCached", 10);
		this.addDefault("SQLite.PathOverride", "");
		this.addDefault("MySQL.Use", false);
		this.addDefault("MySQL.URL", "jdbc:mysql://(HOST):(PORT)/(DATABASE)?useSSL=false");
		this.addDefault("MySQL.Username", "Username");
		this.addDefault("MySQL.Password", "Password");
		this.addDefault("MySQL.LegacyDriver", false);

		this.addDefault("Version", Double.parseDouble(Parkour.getInstance().getDescription().getVersion()));

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
	        sender.sendMessage(Parkour.getPrefix() + "This command is already whitelisted!");
	        return;
        }

		whitelistedCommands.add(command.toLowerCase());
		set("OnCourse.EnforceParkourCommands.Whitelist", whitelistedCommands);
		save();

        sender.sendMessage(Parkour.getPrefix() + "Command " + ChatColor.AQUA + command + ChatColor.WHITE + " added to the whitelisted commands!");
	}

	public String getSignHeader() {
		return TranslationUtils.getTranslation("Parkour.SignHeader", false);
	}

	public String getStrippedSignHeader() {
		return ChatColor.stripColor(getSignHeader());
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

	public boolean isCompletedCoursesEnabled() {
		return this.getBoolean("OnFinish.CompletedCourses.Enabled");
	}

	public boolean isLegacyGroundDetection() {
		return this.getBoolean("Other.ParkourKit.LegacyGroundDetection");
	}

	/* Materials */
	public Material getLastCheckpointTool() {
		return getMaterialOrDefault("OnJoin.Item.LastCheckpoint.Material", Material.AIR);
	}

	public Material getHideAllDisabledTool() {
		return getMaterialOrDefault("OnJoin.Item.HideAll.Material", Material.AIR);
	}

	public Material getHideAllEnabledTool() {
		return getMaterialOrDefault("OnJoin.Item.HideAllEnabled.Material", Material.AIR);
	}

	public Material getLeaveTool() {
		return getMaterialOrDefault("OnJoin.Item.Leave.Material", Material.AIR);
	}

	public Material getRestartTool() {
		return getMaterialOrDefault("OnJoin.Item.Restart.Material", Material.AIR);
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

	public int getMaximumCoursesCached() {
		return this.getInt("Database.MaximumCoursesCached");
	}
}

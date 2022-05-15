package io.github.a5h73y.parkour.configuration.impl;

import static io.github.a5h73y.parkour.type.course.CourseConfig.DIE_IN_LIQUID;
import static io.github.a5h73y.parkour.type.course.CourseConfig.DIE_IN_VOID;
import static io.github.a5h73y.parkour.type.course.CourseConfig.HAS_FALL_DAMAGE;
import static io.github.a5h73y.parkour.type.course.CourseConfig.JOIN_ITEMS;
import static io.github.a5h73y.parkour.type.course.CourseConfig.MAX_FALL_TICKS;
import static io.github.a5h73y.parkour.type.course.CourseConfig.REWARD_DELAY;
import static io.github.a5h73y.parkour.type.course.CourseConfig.REWARD_LEVEL_ADD;
import static io.github.a5h73y.parkour.type.course.CourseConfig.REWARD_ONCE;

import com.cryptomorin.xseries.XMaterial;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import de.leonhard.storage.util.FileUtils;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.ParkourEventType;
import io.github.a5h73y.parkour.type.sounds.SoundType;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default Parkour configuration.
 * Stored in config.yml and covers a range of customisable functionality across the plugin.
 * Sectioned by Parkour Events and functions.
 * Yaml config that automatically reloads itself when a change detected, order retained.
 */
public class DefaultConfig extends Yaml {

	private static final String WHITELISTED_COMMANDS = "OnCourse.EnforceParkourCommands.Whitelist";

	private final DateFormat detailedTimeFormat;
	private final DateFormat standardTimeFormat;
	private final DateFormat achievedFormat;

	/**
	 * Create the config.yml instance.
	 * @param file file
	 */
	public DefaultConfig(File file) {
		super(file.getName(), FileUtils.getParentDirPath(file), null,
				ReloadSettings.INTELLIGENT, ConfigSettings.SKIP_COMMENTS, DataType.SORTED);

		this.setDefault("OnJoin.EnforceWorld", false);
		this.setDefault("OnJoin.EnforceReady", true);
		this.setDefault("OnJoin.FillHealth.Enabled", true);
		this.setDefault("OnJoin.FillHealth.Amount", 20);
		this.setDefault("OnJoin.SetGameMode", "SURVIVAL");
		this.setDefault("OnJoin.TreatFirstCheckpointAsStart", false);
		this.setDefault("OnJoin.PerCoursePermission", false);
		this.setDefault("OnJoin.TeleportPlayer", true);
		this.setDefault("OnJoin.BroadcastLevel", "NONE");

		this.setDefault("OnCourse.AnybodyPlaceBreakBlocks", false);
		this.setDefault("OnCourse.AdminPlaceBreakBlocks", true);
		this.setDefault("OnCourse.AttemptLessChecks", false);
		this.setDefault("OnCourse.CheckpointMaterial", "STONE_PLATE");
		this.setDefault("OnCourse.DisableItemDrop", false);
		this.setDefault("OnCourse.DisableItemPickup", false);
		this.setDefault("OnCourse.DisablePlayerDamage", false);
		this.setDefault("OnCourse.DisableFallDamage", false);
		this.setDefault("OnCourse.DisableFly", true);
		this.setDefault("OnCourse.DisplayLiveTime", false);
		this.setDefault("OnCourse.EnforceParkourSigns", true);
		this.setDefault("OnCourse.EnforceWorld.Enabled", true);
		this.setDefault("OnCourse.EnforceWorld.LeaveCourse", false);
		this.setDefault("OnCourse.PreventPlateStick", false);
		this.setDefault("OnCourse.PreventOpeningOtherInventories", false);
		this.setDefault("OnCourse.PreventAttackingEntities", false);
		this.setDefault("OnCourse.PreventEntitiesAttacking", true);
		this.setDefault("OnCourse.PreventJoiningDifferentCourse", false);
		this.setDefault("OnCourse.PreventPlayerCollisions", false);
		this.setDefault("OnCourse.PreventFireDamage", true);
		this.setDefault("OnCourse.SequentialCheckpoints", true);
		this.setDefault("OnCourse.SneakToInteractItems", true);
		this.setDefault("OnCourse.TreatLastCheckpointAsFinish", false);
		this.setDefault("OnCourse.UseParkourKit", true);
		this.setDefault("OnCourse.EnforceParkourCommands.Enabled", true);
		this.setDefault("OnCourse.EnforceParkourCommands.Whitelist", Collections.singletonList("login"));

		this.setDefault("OnFinish.BroadcastLevel", "GLOBAL");
		this.setDefault("OnFinish.DisplayNewRecords", false);
		this.setDefault("OnFinish.DisplayStats", true);
		this.setDefault("OnFinish.EnablePrizes", true);
		this.setDefault("OnFinish.EnforceCompletion", true);
		this.setDefault("OnFinish.SetGameMode", "SURVIVAL");
		this.setDefault("OnFinish.TeleportAway", true);
		this.setDefault("OnFinish.TeleportBeforePrize", false);
		this.setDefault("OnFinish.TeleportDelay", 0);
		this.setDefault("OnFinish.TeleportToJoinLocation", false);
		this.setDefault("OnFinish.UpdatePlayerDatabaseTime", true);

		this.setDefault("OnLeave.TeleportToLinkedLobby", false);
		this.setDefault("OnLeave.DestroyCourseProgress", true);
		this.setDefault("OnLeave.TeleportAway", true);

		this.setDefault("OnRestart.FullPlayerRestart", false);
		this.setDefault("OnRestart.RequireConfirmation", false);

		this.setDefault("OnDie.ResetProgressWithNoCheckpoint", false);
		this.setDefault("OnDie.SetXPBarToDeathCount", false);

		this.setDefault("OnLeaveServer.LeaveCourse", false);
		this.setDefault("OnLeaveServer.TeleportToLastCheckpoint", false);

		this.setDefault("OnServerRestart.KickPlayerFromCourse", false);

		this.setDefault("CourseDefault.Settings." + HAS_FALL_DAMAGE, true);
		this.setDefault("CourseDefault.Settings." + MAX_FALL_TICKS, 80);
		this.setDefault("CourseDefault.Settings." + DIE_IN_LIQUID, false);
		this.setDefault("CourseDefault.Settings." + DIE_IN_VOID, false);
		this.setDefault("CourseDefault.Settings." + REWARD_ONCE, false);
		this.setDefault("CourseDefault.Settings." + REWARD_DELAY, 0);
		this.setDefault("CourseDefault.Settings." + REWARD_LEVEL_ADD, 0);
		this.setDefault("CourseDefault.Settings." + JOIN_ITEMS, new ArrayList<String>());

		this.setDefault("CourseDefault.Prize.Material", "DIAMOND");
		this.setDefault("CourseDefault.Prize.Amount", 1);
		this.setDefault("CourseDefault.Prize.XP", 0);
		this.setDefault("CourseDefault.Commands.CombinePerCourseCommands", true);
		Arrays.stream(ParkourEventType.values()).forEach(eventType ->
				this.setDefault("CourseDefault.Command." + eventType.getConfigEntry(),
						Collections.singletonList("")));

		this.setDefault("ParkourTool.LastCheckpoint.Material", "ARROW");
		this.setDefault("ParkourTool.LastCheckpoint.Slot", 0);
		this.setDefault("ParkourTool.HideAll.Material", "BONE");
		this.setDefault("ParkourTool.HideAll.Slot", 1);
		this.setDefault("ParkourTool.HideAll.Global", true);
		this.setDefault("ParkourTool.HideAll.ActivateOnJoin", false);
		this.setDefault("ParkourTool.HideAllEnabled.Material", "BONE");
		this.setDefault("ParkourTool.HideAllEnabled.Slot", 1);
		this.setDefault("ParkourTool.Leave.Material", XMaterial.OAK_SAPLING.parseMaterial().name());
		this.setDefault("ParkourTool.Leave.Slot", 2);
		this.setDefault("ParkourTool.Restart.Material", "STICK");
		this.setDefault("ParkourTool.Restart.Slot", 3);
		this.setDefault("ParkourTool.Restart.SecondCooldown", 1);
		this.setDefault("ParkourTool.Freedom.Material", XMaterial.REDSTONE_TORCH.parseMaterial().name());
		this.setDefault("ParkourTool.Freedom.Slot", 4);
		this.setDefault("ParkourTool.Freedom.SecondCooldown", 1);
		this.setDefault("ParkourTool.Rockets.Material", XMaterial.FIREWORK_ROCKET.parseMaterial().name());
		this.setDefault("ParkourTool.Rockets.Slot", 4);

		this.setDefault("ParkourChallenge.HidePlayers", true);
		this.setDefault("ParkourChallenge.CountdownFrom", 5);
		this.setDefault("ParkourChallenge.PrepareOnAccept", false);

		this.setDefault("ParkourModes.Speedy.SetSpeed", 0.7);
		this.setDefault("ParkourModes.Speedy.ResetSpeed", 0.2);
		this.setDefault("ParkourModes.Rockets.Invert", false);
		this.setDefault("ParkourModes.Rockets.SecondCooldown", 1);
		this.setDefault("ParkourModes.Rockets.LaunchForce", 1.5);

		this.setDefault("DisplayTitle.FadeIn", 5);
		this.setDefault("DisplayTitle.FadeOut", 5);
		this.setDefault("DisplayTitle.JoinCourse.Enabled", true);
		this.setDefault("DisplayTitle.JoinCourse.Stay", 20);
		this.setDefault("DisplayTitle.Checkpoint.Enabled", true);
		this.setDefault("DisplayTitle.Checkpoint.Stay", 20);
		this.setDefault("DisplayTitle.Death.Enabled", true);
		this.setDefault("DisplayTitle.Death.Stay", 20);
		this.setDefault("DisplayTitle.Leave.Enabled", true);
		this.setDefault("DisplayTitle.Leave.Stay", 20);
		this.setDefault("DisplayTitle.Finish.Enabled", true);
		this.setDefault("DisplayTitle.Finish.Stay", 20);

		this.setDefault("AutoStart.Enabled", true);
		this.setDefault("AutoStart.Material", "BEDROCK");
		this.setDefault("AutoStart.TickDelay", 0);
		this.setDefault("AutoStart.IncludeWorldName", true);

		this.setDefault("Scoreboard.Enabled", false);
		this.setDefault("Scoreboard.CourseName.Enabled", true);
		this.setDefault("Scoreboard.CourseName.Sequence", 1);
		this.setDefault("Scoreboard.BestTimeEver.Enabled", true);
		this.setDefault("Scoreboard.BestTimeEver.Sequence", 2);
		this.setDefault("Scoreboard.BestTimeEverName.Enabled", true);
		this.setDefault("Scoreboard.BestTimeEverName.Sequence", 3);
		this.setDefault("Scoreboard.MyBestTime.Enabled", true);
		this.setDefault("Scoreboard.MyBestTime.Sequence", 4);
		this.setDefault("Scoreboard.CurrentDeaths.Enabled", true);
		this.setDefault("Scoreboard.CurrentDeaths.Sequence", 5);
		this.setDefault("Scoreboard.Checkpoints.Enabled", true);
		this.setDefault("Scoreboard.Checkpoints.Sequence", 6);
		this.setDefault("Scoreboard.LiveTimer.Enabled", true);
		this.setDefault("Scoreboard.LiveTimer.Sequence", 7);
		this.setDefault("Scoreboard.RemainingDeaths.Enabled", false);
		this.setDefault("Scoreboard.RemainingDeaths.Sequence", 8);

		this.setDefault("Sounds.Enabled", false);
		this.setDefault("Sounds.JoinCourse.Enabled", true);
		this.setDefault("Sounds.JoinCourse.Sound", "BLOCK_NOTE_BLOCK_PLING");
		this.setDefault("Sounds.JoinCourse.Volume", 0.05f);
		this.setDefault("Sounds.JoinCourse.Pitch", 1.75f);
		this.setDefault("Sounds.SecondIncrement.Enabled", true);
		this.setDefault("Sounds.SecondIncrement.Sound", "BLOCK_NOTE_BLOCK_PLING");
		this.setDefault("Sounds.SecondIncrement.Volume", 0.05f);
		this.setDefault("Sounds.SecondIncrement.Pitch", 1.75f);
		this.setDefault("Sounds.SecondDecrement.Enabled", true);
		this.setDefault("Sounds.SecondDecrement.Sound", "BLOCK_NOTE_BLOCK_PLING");
		this.setDefault("Sounds.SecondDecrement.Volume", 0.05f);
		this.setDefault("Sounds.SecondDecrement.Pitch", 4f);
		this.setDefault("Sounds.PlayerDeath.Enabled", true);
		this.setDefault("Sounds.PlayerDeath.Sound", "ENTITY_PLAYER_DEATH");
		this.setDefault("Sounds.PlayerDeath.Volume", 0.1f);
		this.setDefault("Sounds.PlayerDeath.Pitch", 1.75f);
		this.setDefault("Sounds.CheckpointAchieved.Enabled", true);
		this.setDefault("Sounds.CheckpointAchieved.Sound", "BLOCK_NOTE_BLOCK_CHIME");
		this.setDefault("Sounds.CheckpointAchieved.Volume", 0.1f);
		this.setDefault("Sounds.CheckpointAchieved.Pitch", 1.75f);
		this.setDefault("Sounds.CourseFinished.Enabled", true);
		this.setDefault("Sounds.CourseFinished.Sound", "BLOCK_CONDUIT_ACTIVATE");
		this.setDefault("Sounds.CourseFinished.Volume", 0.1f);
		this.setDefault("Sounds.CourseFinished.Pitch", 1.75f);
		this.setDefault("Sounds.CourseFailed.Enabled", true);
		this.setDefault("Sounds.CourseFailed.Sound", "BLOCK_CONDUIT_DEACTIVATE");
		this.setDefault("Sounds.CourseFailed.Volume", 0.1f);
		this.setDefault("Sounds.CourseFailed.Pitch", 1.75f);
		this.setDefault("Sounds.ReloadRocket.Enabled", true);
		this.setDefault("Sounds.ReloadRocket.Sound", "TODO"); // TODO nice reload sound
		this.setDefault("Sounds.ReloadRocket.Volume", 0.1f);
		this.setDefault("Sounds.ReloadRocket.Pitch", 1.75f);

		this.setDefault("ParkourGUI.Material", "BOOK");
		this.setDefault("ParkourGUI.FillerMaterial", "CYAN_STAINED_GLASS_PANE");

		this.setDefault("ParkourKit.ReplaceInventory", true);
		this.setDefault("ParkourKit.GiveSign", true);
		this.setDefault("ParkourKit.LegacyGroundDetection", false);

		this.setDefault("ParkourRankChat.Enabled", false);
		this.setDefault("ParkourRankChat.OverrideChat", true);

		this.setDefault("Other.UseAutoTabCompletion", true);
		this.setDefault("Other.CheckForUpdates", true);
		this.setDefault("Other.LogAdminTasksToFile", true);
		this.setDefault("Other.EnforceSafeCheckpoints", true);
		this.setDefault("Other.PlayerConfigUsePlayerUUID", true);
		this.setDefault("Other.Parkour.SignProtection", true);
		this.setDefault("Other.Parkour.InventoryManagement", true);
		this.setDefault("Other.Parkour.SignUsePermissions", false);
		this.setDefault("Other.Parkour.CommandUsePermissions", false);
		this.setDefault("Other.Parkour.MaximumParkourLevel", 99999999);
		this.setDefault("Other.Parkour.ResetPotionEffects", true);

		this.setDefault("Other.Display.JoinWelcomeMessage", true);
		this.setDefault("Other.Display.LevelReward", true);
		this.setDefault("Other.Display.PrizeCooldown", true);
		this.setDefault("Other.Display.OnlyReadyCourses", false);
		this.setDefault("Other.Display.CompletedCourseJoinMessage", false);
		this.setDefault("Other.Display.IncludeDeprecatedCommands", false);

		this.setDefault("Other.Time.StandardFormat", "HH:mm:ss");
		this.setDefault("Other.Time.DetailedFormat", "HH:mm:ss:SSS");
		this.setDefault("Other.Time.AchievedFormat", "dd/MM/yyyy HH:mm:ss");
		this.setDefault("Other.Time.TimeZone", "GMT");

		this.setDefault("Other.OnServerShutdown.BackupFiles", false);
		this.setDefault("Other.OnPlayerBan.ResetParkourInfo", false);
		this.setDefault("Other.OnSetPlayerParkourLevel.UpdateParkourRank", true);
		this.setDefault("Other.OnVoid.TeleportToLobby", false);

		this.setDefault("Plugin.BountifulAPI.Enabled", true);
		this.setDefault("Plugin.Vault.Enabled", true);
		this.setDefault("Plugin.PlaceholderAPI.Enabled", true);
		this.setDefault("Plugin.PlaceholderAPI.CacheTime", 15);

		this.setDefault("Database.MaximumCoursesCached", 10);
		this.setDefault("SQLite.PathOverride", "");
		this.setDefault("MySQL.Use", false);
		this.setDefault("MySQL.URL", "jdbc:mysql://HOST:PORT/DATABASE?useSSL=false");
		this.setDefault("MySQL.Username", "Username");
		this.setDefault("MySQL.Password", "Password");
		this.setDefault("MySQL.LegacyDriver", false);

		this.setDefault("Version", Parkour.getInstance().getDescription().getVersion());

		this.setDefault("LobbySettings.EnforceWorld", false);

		TimeZone timeZone = TimeZone.getTimeZone(this.getTimeZone());
		detailedTimeFormat = setupDateFormat(this.getTimeDetailedFormatValue(), timeZone);
		standardTimeFormat = setupDateFormat(this.getTimeStandardFormatValue(), timeZone);
		achievedFormat = setupDateFormat(this.getTimeAchievedFormatValue(), timeZone);
	}

	private DateFormat setupDateFormat(String format, TimeZone timeZone) {
		DateFormat result = new SimpleDateFormat(format);
		result.setTimeZone(timeZone);
		return result;
	}

	/**
	 * Generates the file name for the Player config file.
	 * Can either be the Player's UUID or the Player's name as the file name.
	 * @param player player
	 * @return player file name
	 */
	public String getPlayerConfigName(OfflinePlayer player) {
		return this.getBoolean("Other.PlayerConfigUsePlayerUUID")
				? player.getUniqueId().toString() : player.getName();
	}

	/**
	 * Add a command to the whitelist.
	 *
	 * @param command command to add
	 */
	public void addWhitelistCommand(@Nullable String command) {
		List<String> whitelistedCommands = getWhitelistedCommands();
		if (command != null && !whitelistedCommands.contains(command.toLowerCase())) {
			whitelistedCommands.add(command.toLowerCase());
			set(WHITELISTED_COMMANDS, whitelistedCommands);
		}
	}

	/**
	 * Remove a command from the whitelist.
	 *
	 * @param command command to remove
	 */
	public void removeWhitelistCommand(@Nullable String command) {
		List<String> whitelistedCommands = getWhitelistedCommands();
		if (command != null && whitelistedCommands.contains(command.toLowerCase())) {
			whitelistedCommands.remove(command.toLowerCase());
			set(WHITELISTED_COMMANDS, whitelistedCommands);
		}
	}

	public void addDisabledParkourCommand(@NotNull String command) {
		this.set("DisableCommand." + command.toLowerCase(), true);
	}

	public void removeDisabledParkourCommand(@NotNull String command) {
		this.remove("DisableCommand." + command.toLowerCase());
	}

	public String getSignHeader() {
		return TranslationUtils.getTranslation("Parkour.SignHeader", false);
	}

	public String getStrippedSignHeader() {
		return ChatColor.stripColor(getSignHeader());
	}

	@NotNull
	public List<String> getDefaultEventCommands(@NotNull ParkourEventType eventType) {
		return this.get("CourseDefault.Command." + eventType.getConfigEntry(), new ArrayList<>());
	}

	public String getTimeStandardFormatValue() {
		return this.getString("Other.Time.StandardFormat");
	}

	public String getTimeDetailedFormatValue() {
		return this.getString("Other.Time.DetailedFormat");
	}

	public String getTimeAchievedFormatValue() {
		return this.getString("Other.Time.AchievedFormat");
	}

	public String getTimeZone() {
		return this.getString("Other.Time.TimeZone");
	}

	public boolean isPermissionsForCommands() {
		return this.getBoolean("Other.Parkour.CommandUsePermissions");
	}

	public boolean isPermissionForSignInteraction() {
		return this.getBoolean("Other.Parkour.SignUsePermissions");
	}

	public boolean isChatPrefix() {
		return this.getBoolean("ParkourRankChat.Enabled");
	}

	public boolean isChatPrefixOverride() {
		return this.getBoolean("ParkourRankChat.OverrideChat");
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

	public boolean isEnforceSafeCheckpoints() {
		return this.getBoolean("Other.EnforceSafeCheckpoints");
	}

	public boolean isTreatFirstCheckpointAsStart() {
		return this.getBoolean("OnJoin.TreatFirstCheckpointAsStart");
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

	public boolean isLegacyGroundDetection() {
		return this.getBoolean("ParkourKit.LegacyGroundDetection");
	}

	public boolean isVoidTeleportToLobby() {
		return this.getBoolean("Other.OnVoid.TeleportToLobby");
	}

	public boolean isLeaveDestroyCourseProgress() {
		return this.getBoolean("OnLeave.DestroyCourseProgress");
	}

	public boolean isCombinePerCourseCommands() {
		return this.getBoolean("CourseDefault.Commands.CombinePerCourseCommands");
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

	public Material getFreedomTool() {
		return getMaterialOrDefault("ParkourTool.Freedom.Material", Material.AIR);
	}

	public Material getRocketTool() {
		return getMaterialOrDefault("ParkourTool.Rockets.Material", Material.AIR);
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
		return this.getStringList(WHITELISTED_COMMANDS);
	}

	/* ints */

	public int getTitleIn() {
		return this.getInt("DisplayTitle.FadeIn");
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

	/* Time formats */

	public DateFormat getDetailedTimeFormat() {
		return detailedTimeFormat;
	}

	public DateFormat getStandardTimeFormat() {
		return standardTimeFormat;
	}

	public DateFormat getAchievedDateTimeFormat() {
		return achievedFormat;
	}
}

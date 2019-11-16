package me.A5H73Y.parkour.config.impl;

import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.config.ParkourConfiguration;
import me.A5H73Y.parkour.utilities.Utils;
import org.bukkit.Particle;

public class DefaultFile extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "config.yml";
	}

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
		this.addDefault("OnJoin.SetGamemode", 0);
		this.addDefault("OnJoin.TreatFirstCheckpointAsStart", false);

		this.addDefault("OnCourse.AdminPlaceBreakBlocks", true);
		this.addDefault("OnCourse.AttemptLessChecks", false);
		this.addDefault("OnCourse.CheckpointMaterial", "STONE_PLATE");
		this.addDefault("OnCourse.DieInLiquid", false);
		this.addDefault("OnCourse.DieInVoid", false);
		this.addDefault("OnCourse.DisableItemDrop", false);
		this.addDefault("OnCourse.DisableItemPickup", false);
		this.addDefault("OnCourse.DisablePlayerDamage", false);
		this.addDefault("OnCourse.DisableFly", true);
		this.addDefault("OnCourse.DisplayLiveTime", false);
		this.addDefault("OnCourse.EnforceParkourSigns", true);
		this.addDefault("OnCourse.MaxFallTicks", 80);
		this.addDefault("OnCourse.PreventPlateStick", false);
		this.addDefault("OnCourse.PreventOpeningOtherInventories", false);
		this.addDefault("OnCourse.PreventAttackingEntities", false);
		this.addDefault("OnCourse.PreventJoiningDifferentCourse", false);
		this.addDefault("OnCourse.PreventPlayerCollisions", false);
		this.addDefault("OnCourse.SneakToInteractItems", true);
		this.addDefault("OnCourse.UseParkourKit", true);
		this.addDefault("OnCourse.Trails.Enabled", false);
		this.addDefault("OnCourse.Trails.Particle", "DRIP_LAVA");
		this.addDefault("OnCourse.EnforceParkourCommands.Enabled", true);
		String[] whitelisted = {"login"};
		this.addDefault("OnCourse.EnforceParkourCommands.Whitelist", whitelisted);

		this.addDefault("OnFinish.BroadcastLevel", 3);
		this.addDefault("OnFinish.DefaultPrize.Material", "DIAMOND");
		this.addDefault("OnFinish.DefaultPrize.Amount", 1);
		this.addDefault("OnFinish.DefaultPrize.XP", 0);
		this.addDefault("OnFinish.DisplayNewRecords", false);
		this.addDefault("OnFinish.DisplayStats", true);
		this.addDefault("OnFinish.EnablePrizes", true);
		this.addDefault("OnFinish.EnforceCompletion", true);
		this.addDefault("OnFinish.SaveUserCompletedCourses", false);
		this.addDefault("OnFinish.SetGamemode", 0);
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

		this.addDefault("ParkourGUI.Enabled", false);
		this.addDefault("ParkourGUI.Rows", 2);
		this.addDefault("ParkourGUI.Material", "BOOK");

		this.addDefault("Other.CheckForUpdates", true);
		this.addDefault("Other.BountifulAPI.Enabled", true);
		this.addDefault("Other.Economy.Enabled", true);
		this.addDefault("Other.PlaceholderAPI.Enabled", true);
		this.addDefault("Other.LogToFile", true);
		this.addDefault("Other.EnforceSafeCheckpoints", true);
		this.addDefault("Other.UseAutoTabCompletion", true);
		this.addDefault("Other.ParkourKit.ReplaceInventory", true);
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

		this.addDefault("SQLite.PathOverride", "");
		this.addDefault("MySQL.Use", false);
		this.addDefault("MySQL.Host", "Host");
		this.addDefault("MySQL.Port", 3306);
		this.addDefault("MySQL.User", "Username");
		this.addDefault("MySQL.Password", "Password");
		this.addDefault("MySQL.Database", "Database");
		this.addDefault("MySQL.Table", "Table");

		this.addDefault("Version", Double.parseDouble(Parkour.getInstance().getDescription().getVersion()));

		this.addDefault("Lobby.Set", false);
		this.addDefault("Lobby.EnforceWorld", false);

		this.options().copyDefaults(true);

		validateConfigProperties();
	}

	@Override
	protected void reload() {
		super.reload();
		validateConfigProperties();
	}

	private void validateConfigProperties() {
		// First check if the Trail is valid
		if (this.getBoolean("OnCourse.Trails.Enabled")) {
			String trail = this.getString("OnCourse.Trails.Particle").toUpperCase();

			try {
				Particle particle = Particle.valueOf(trail);
				Parkour.getInstance().getServer().getWorlds().get(0).spawnParticle(particle, 0, 0, 0, 1);
			} catch (NoClassDefFoundError | Exception ex) {
				Utils.log("Particle: " + trail + " is invalid. Disabling Trails.", 2);
				this.set("OnCourse.Trails.Enabled", false);
			}
		}
	}
}

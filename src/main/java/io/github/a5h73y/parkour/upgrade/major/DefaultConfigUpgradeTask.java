package io.github.a5h73y.parkour.upgrade.major;

import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedConfigUpgradeTask;

public class DefaultConfigUpgradeTask extends TimedConfigUpgradeTask {

	public DefaultConfigUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader, parkourUpgrader.getDefaultConfig());
	}

	@Override
	protected String getTitle() {
		return "Default Config";
	}

	@Override
	protected boolean doWork() {
		updateConfigEntry("OnFinish.CompletedCourses.JoinMessage", "Other.Display.CompletedCourseJoinMessage");
		updateConfigEntry("Other.LogToFile", "Other.LogAdminTasksToFile");
		updateConfigEntry("Other.ParkourKit.ReplaceInventory", "ParkourKit.ReplaceInventory");
		updateConfigEntry("Other.ParkourKit.GiveSign", "ParkourKit.GiveSign");
		updateConfigEntry("Other.ParkourKit.LegacyGroundDetection", "ParkourKit.LegacyGroundDetection");
		updateConfigEntry("Other.Parkour.ChatRankPrefix.Enabled", "ParkourRankChat.Enabled");
		updateConfigEntry("Other.Parkour.ChatRankPrefix.OverrideChat", "ParkourRankChat.OverrideChat");

		getConfig().set("OnJoin.AllowViaCommand", null);
		getConfig().set("OnCourse.DieInLiquid", null);
		getConfig().set("OnCourse.DieInVoid", null);
		getConfig().set("OnCourse.MaxFallTicks", null);
		getConfig().set("OnFinish.DefaultPrize", null);
		getConfig().set("OnFinish.CompletedCourses", null);
		getConfig().set("ParkourModes.Dropper", null);
		getConfig().set("ParkourModes.FreeCheckpoint", null);
		getConfig().set("DisplayTitle", null); // laziness
		getConfig().set("Other.ParkourKit", null);
		getConfig().set("Other.Parkour.ChatRankPrefix", null);
		getConfig().set("Lobby", null);
		getConfig().set("GUI.JoinCourses.Setup", null);
		getConfig().set("GUI.CourseSettings.Setup", null);

		return true;
	}
}

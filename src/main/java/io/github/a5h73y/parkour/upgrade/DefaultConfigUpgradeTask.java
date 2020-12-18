package io.github.a5h73y.parkour.upgrade;

import io.github.a5h73y.parkour.Parkour;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.bukkit.GameMode;

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
		boolean success = true;

		try {
			upgradeDefaultLobby();

			// straight up replacements (renames etc.)
			transferAndDelete("OnJoin.EnforceFinished", "OnJoin.EnforceReady");
			transferAndDelete("OnFinish.SaveUserCompletedCourses", "OnFinish.CompletedCourses.Enabled");
			transferAndDelete("Other.Economy.Enabled", "Other.Vault.Enabled");
			transferAndDelete("Other.Parkour.SignPermissions", "Other.Parkour.SignUsePermissions");
			transferAndDelete("Other.Parkour.CommandPermissions", "Other.Parkour.CommandUsePermissions");
			transferAndDelete("MySQL.User", "MySQL.Username");
			transferAndDelete("Lobby.EnforceWorld", "LobbySettings.EnforceWorld");
			transferAndDelete("ParkourModes.Challenge.HidePlayers", "ParkourChallenge.HidePlayers");
			transferAndDelete("ParkourModes.Challenge.CountdownFrom", "ParkourChallenge.CountdownFrom");

			transferAndDelete("Scoreboard.Display.CurrentTime", "Scoreboard.LiveTimer.Enabled");
			transferAndDelete("Scoreboard.Display.CourseName", "Scoreboard.CourseName.Enabled");
			transferAndDelete("Scoreboard.Display.BestTimeEver", "Scoreboard.BestTimeEver.Enabled");
			transferAndDelete("Scoreboard.Display.BestTimeEverName", "Scoreboard.BestTimeEverName.Enabled");
			transferAndDelete("Scoreboard.Display.BestTimeByMe", "Scoreboard.MyBestTime.Enabled");

			// update int to actual value
			getConfig().set("OnJoin.SetGameMode", getMatchingGameMode(getConfig().getInt("OnJoin.SetGamemode")));
			getConfig().set("OnFinish.BroadcastLevel", getBroadcastLevel(
					getConfig().getInt("OnFinish.BroadcastLevel")));
			getConfig().set("OnFinish.SetGameMode", getMatchingGameMode(getConfig().getInt("OnFinish.SetGamemode")));
			getConfig().set("MySQL.URL", "jdbc:mysql://" + getConfig().getString("MySQL.Host") + ":"
					+ getConfig().getString("MySQL.Port") + "/" + getConfig().getString("MySQL.Database"));

			// miscellaneous
			getConfig().set("OnJoin.Item.HideAllEnabled.Material",
					getConfig().getString("OnJoin.Item.HideAll.Material"));
			getConfig().set("Version", Double.valueOf(Parkour.getInstance().getDescription().getVersion()));

			// deletions
			getConfig().set("OnCourse.Trails", null);
			getConfig().set("OnJoin.SetGamemode", null);
			getConfig().set("OnFinish.SetGamemode", null);
			getConfig().set("MySQL.User", null);
			getConfig().set("MySQL.Host", null);
			getConfig().set("MySQL.Port", null);
			getConfig().set("MySQL.Database", null);
			getConfig().set("MySQL.Table", null);
			getConfig().set("Other.Economy", null);
			getConfig().set("Lobby.Set", null);
			getConfig().set("Lobby.EnforceWorld", null);
			getConfig().set("ParkourModes.Challenge", null);
			getParkourUpgrader().saveDefaultConfig();
		} catch (IOException e) {
			getParkourUpgrader().getLogger().severe("An error occurred during upgrade: " + e.getMessage());
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	private String getMatchingGameMode(int value) {
		return GameMode.getByValue(value).name();
	}

	private String getBroadcastLevel(int value) {
		if (value == 0) {
			return "PLAYER";
		} else if (value == 1) {
			return "PARKOUR";
		} else if (value == 2) {
			return "WORLD";
		} else if (value == 3) {
			return "GLOBAL";
		}
		return "NOBODY";
	}

	private void upgradeDefaultLobby() {
		List<String> details = Arrays.asList("World", "X", "Y", "Z", "Pitch", "Yaw");

		for (String detail : details) {
			getConfig().set("Lobby.default." + detail, getConfig().get("Lobby." + detail));
			getConfig().set("Lobby." + detail, null);
		}
	}
}

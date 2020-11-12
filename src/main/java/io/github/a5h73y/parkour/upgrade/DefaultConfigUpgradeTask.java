package io.github.a5h73y.parkour.upgrade;

import io.github.a5h73y.parkour.Parkour;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;

public class DefaultConfigUpgradeTask extends TimedUpgradeTask {

	private final FileConfiguration defaultConfig;

	public DefaultConfigUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader);
		this.defaultConfig = parkourUpgrader.getDefaultConfig();
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

			transferAndDelete("Scoreboard.Display.CurrentTime", "Scoreboard.LiveTimer.Enabled");
			// TODO more scoreboard

			// update int to actual value
			defaultConfig.set("OnJoin.SetGameMode", getMatchingGameMode(defaultConfig.getInt("OnJoin.SetGamemode")));
			defaultConfig.set("OnFinish.BroadcastLevel", getBroadcastLevel(defaultConfig.getInt("OnFinish.BroadcastLevel")));
			defaultConfig.set("OnFinish.SetGameMode", getMatchingGameMode(defaultConfig.getInt("OnFinish.SetGamemode")));
			defaultConfig.set("MySQL.URL", "jdbc:mysql://" + defaultConfig.getString("MySQL.Host") + ":" +
					defaultConfig.getString("MySQL.Port") + "/" + defaultConfig.getString("MySQL.Database"));

			// miscellaneous
			defaultConfig.set("OnJoin.Item.HideAllEnabled.Material",
					defaultConfig.getString("OnJoin.Item.HideAll.Material"));
			defaultConfig.set("Version", Double.valueOf(Parkour.getInstance().getDescription().getVersion()));

			// deletions
			defaultConfig.set("OnCourse.Trails", null);
			defaultConfig.set("OnJoin.SetGamemode", null);
			defaultConfig.set("OnFinish.SetGamemode", null);
			defaultConfig.set("MySQL.User", null);
			defaultConfig.set("MySQL.Host", null);
			defaultConfig.set("MySQL.Port", null);
			defaultConfig.set("MySQL.Database", null);
			defaultConfig.set("MySQL.Table", null);
			defaultConfig.set("Other.Economy", null);
			defaultConfig.set("Lobby.Set", null);
			defaultConfig.set("Lobby.EnforceWorld", null);
			getParkourUpgrader().saveDefaultConfig();
		} catch (IOException e) {
			getParkourUpgrader().getLogger().severe("An error occurred during upgrade: " + e.getMessage());
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	private void transferAndDelete(String fromPath, String toPath) {
		defaultConfig.set(toPath, defaultConfig.get(fromPath));
		defaultConfig.set(fromPath, null);
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
			defaultConfig.set("Lobby.default." + detail, defaultConfig.get("Lobby." + detail));
			defaultConfig.set("Lobby." + detail, null);
		}
	}
}

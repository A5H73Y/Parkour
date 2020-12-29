package io.github.a5h73y.parkour.upgrade.minor;

import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedUpgradeTask;
import java.io.IOException;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PartialUpgradeTask extends TimedUpgradeTask {

	private final String previousVersion;

	public PartialUpgradeTask(ParkourUpgrader parkourUpgrader, String previousVersion) {
		super(parkourUpgrader);
		this.previousVersion = previousVersion;
	}

	@Override
	protected String getTitle() {
		return previousVersion + " Config";
	}

	@Override
	protected boolean doWork() {
		switch (previousVersion) {
			case "6.0":
			case "6.1":
				convertPlayerParkourLevelConfig();
				convertCourseParkourLevelConfig();
				break;

			default:
				break;
		}

		return true;
	}

	private void convertPlayerParkourLevelConfig() {
		FileConfiguration playerConfig = getParkourUpgrader().getPlayerConfig();
		ConfigurationSection playerSection = playerConfig.getConfigurationSection("");

		if (playerSection != null) {
			Set<String> playerIds = playerSection.getKeys(false);
			for (String playerId : playerIds) {
				if (playerConfig.contains(playerId + ".Level")) {
					playerConfig.set(playerId + ".ParkourLevel", playerConfig.getInt(playerId + ".Level"));
					playerConfig.set(playerId + ".Level", null);
				}
				if (playerConfig.contains(playerId + ".Rank")) {
					playerConfig.set(playerId + ".ParkourRank", playerConfig.getString(playerId + ".Rank"));
					playerConfig.set(playerId + ".Rank", null);
				}
			}

			try {
				getParkourUpgrader().savePlayerConfig();
			} catch (IOException e) {
				getParkourUpgrader().getLogger().severe("An error occurred during upgrade: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void convertCourseParkourLevelConfig() {
		FileConfiguration coursesConfig = getParkourUpgrader().getCoursesConfig();

		for (String courseName : coursesConfig.getStringList("Courses")) {
			if (coursesConfig.contains(courseName + ".Level")) {
				coursesConfig.set(courseName + ".RewardLevel", coursesConfig.getInt(courseName + ".Level"));
				coursesConfig.set(courseName + ".Level", null);
			}

			if (coursesConfig.contains(courseName + ".LevelAdd")) {
				coursesConfig.set(courseName + ".RewardLevelAdd", coursesConfig.getInt(courseName + ".LevelAdd"));
				coursesConfig.set(courseName + ".LevelAdd", null);
			}
		}

		try {
			getParkourUpgrader().saveCoursesConfig();
		} catch (IOException e) {
			getParkourUpgrader().getLogger().severe("An error occurred during upgrade: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

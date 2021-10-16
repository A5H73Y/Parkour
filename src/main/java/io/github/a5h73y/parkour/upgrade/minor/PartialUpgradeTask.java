package io.github.a5h73y.parkour.upgrade.minor;

import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedUpgradeTask;
import io.github.a5h73y.parkour.utility.PluginUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
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
				convertParkourToolsConfig();
				convertParkourSessions();
				removeOldBinFile();
				break;

			case "6.2":
			case "6.3":
				convertParkourToolsConfig();
				convertParkourSessions();
				removeOldBinFile();
				break;

			default:
				break;
		}

		convertOldConfigEntries();
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

	private void convertParkourToolsConfig() {
		FileConfiguration config = getParkourUpgrader().getDefaultConfig();
		FileConfiguration strings = getParkourUpgrader().getStringsConfig();

		ConfigurationSection oldJoinItems = config.getConfigurationSection("OnJoin.Item");

		if (oldJoinItems != null) {
			Set<String> tools = oldJoinItems.getKeys(false);

			for (String toolName : tools) {
				ConfigurationSection joinItems = config.getConfigurationSection("OnJoin.Item." + toolName);

				if (joinItems != null) {
					Set<String> options = joinItems.getKeys(false);

					for (String option : options) {
						config.set("ParkourTool." + toolName + "." + option, config.get("OnJoin.Item."
								+ toolName + "." + option));
					}

					strings.set("ParkourTool." + toolName, strings.get("Other.Item." + toolName));
				}
			}
			config.set("OnJoin.Item", null);
			config.set("OnJoin.FillHealth", null);
			strings.set("Other.Item", null);

			try {
				getParkourUpgrader().saveCoursesConfig();
				getParkourUpgrader().saveStringsConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void convertParkourSessions() {
		File sessionsDir = new File(getParkourUpgrader().getParkour().getDataFolder() + "/sessions/");

		if (sessionsDir.isDirectory()) {
			// find the ones that are only the uuids
			for (File sessionFile : sessionsDir.listFiles(pathname -> !pathname.isDirectory()
					&& pathname.getName().split("-").length == 5)) {
				try (
						FileInputStream fileInputStream = new FileInputStream(sessionFile);
						ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)
				) {
					ParkourSession session = (ParkourSession) objectInputStream.readObject();
					Files.delete(sessionFile.toPath());

					if (session.getCourseName() != null) {
						File newSessionFile = new File(sessionsDir + sessionFile.getName(),
								session.getCourseName());
						saveNewSession(newSessionFile, session);
					}

				} catch (Exception e) {
					PluginUtils.log("Player's session couldn't be loaded: " + e.getMessage(), 2);
					e.printStackTrace();
				}
			}
		}
	}

	private void saveNewSession(File newSessionFile, ParkourSession session) throws IOException {
		if (!newSessionFile.getParentFile().exists()) {
			newSessionFile.getParentFile().mkdirs();
			newSessionFile.createNewFile();
		}

		try (
				FileOutputStream fileOutputStream = new FileOutputStream(newSessionFile);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)
		) {
			objectOutputStream.writeObject(session);
		} catch (IOException e) {
			PluginUtils.log("Player's session couldn't be saved: " + e.getMessage(), 2);
			e.printStackTrace();
		}
	}

	private void removeOldBinFile() {
		File binPath = new File(getParkourUpgrader().getParkour().getDataFolder(), "playing.bin");

		if (binPath.exists()) {
			binPath.delete();
		}
	}

	/**
	 * Convert old config entries.
	 * Can be run at any previous server version as it will check they exist before replacing.
	 */
	private void convertOldConfigEntries() {
		FileConfiguration defaultConfig = getParkourUpgrader().getDefaultConfig();

		convert(defaultConfig, "OnDie.ResetTimeWithNoCheckpoint", "OnDie.ResetProgressWithNoCheckpoint");
	}

	private void convert(FileConfiguration config, String oldPath, String newPath) {
		if (config.contains(oldPath)) {
			config.set(newPath, config.get(oldPath));
			config.set(oldPath, null);
		}
	}
}

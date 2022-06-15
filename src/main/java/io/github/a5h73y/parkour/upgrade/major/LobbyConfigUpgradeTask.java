package io.github.a5h73y.parkour.upgrade.major;

import io.github.a5h73y.parkour.type.lobby.LobbyConfig;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedUpgradeTask;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;

public class LobbyConfigUpgradeTask extends TimedUpgradeTask {

	public LobbyConfigUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader);
	}

	@Override
	protected String getTitle() {
		return "Lobby";
	}

	@Override
	protected boolean doWork() {
		ConfigurationSection section = getParkourUpgrader().getDefaultConfig().getConfigurationSection("Lobby");
		LobbyConfig lobbyConfig = getParkourUpgrader().getNewConfigManager().getLobbyConfig();

		if (section != null) {
			Set<String> lobbies = section.getKeys(false);

			for (String lobbyName : lobbies) {
				if (!section.contains(lobbyName + ".World")) {
					continue;
				}

				// extract the old location manually
				lobbyConfig.set(lobbyName + ".Location.x", section.getString(lobbyName + ".X"));
				lobbyConfig.set(lobbyName + ".Location.y", section.getString(lobbyName + ".Y"));
				lobbyConfig.set(lobbyName + ".Location.z", section.getString(lobbyName + ".Z"));
				lobbyConfig.set(lobbyName + ".Location.yaw", section.getString(lobbyName + ".Yaw"));
				lobbyConfig.set(lobbyName + ".Location.pitch", section.getString(lobbyName + ".Pitch"));
				lobbyConfig.set(lobbyName + ".Location.world", section.getString(lobbyName + ".World"));

				// transfer details across
				if (section.contains(lobbyName + ".RequiredLevel")) {
					lobbyConfig.setRequiredLevel(lobbyName, section.getInt(lobbyName + ".RequiredLevel"));
				}
				if (section.contains(lobbyName + ".Commands")) {
					section.getStringList(lobbyName + ".Commands")
							.forEach(command -> lobbyConfig.addLobbyCommand(lobbyName, command));
				}
			}
		}

		// remove old section from config.yml
		getParkourUpgrader().getDefaultConfig().set("Lobby", null);

		return true;
	}
}

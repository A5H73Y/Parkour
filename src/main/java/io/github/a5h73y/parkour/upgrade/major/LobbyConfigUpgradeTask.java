package io.github.a5h73y.parkour.upgrade.major;

import io.github.a5h73y.parkour.type.lobby.LobbyConfig;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedUpgradeTask;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
				// extract the old location manually
				double x = section.getDouble(lobbyName + ".X");
				double y = section.getDouble(lobbyName + ".Y");
				double z = section.getDouble(lobbyName + ".Z");
				float yaw = section.getLong(lobbyName + ".Yaw");
				float pitch = section.getLong(lobbyName + ".Pitch");
				World world = Bukkit.getWorld(section.getString(lobbyName + ".World"));

				// create new lobby at the location
				lobbyConfig.setLobbyLocation(lobbyName, new Location(world, x, y, z, yaw, pitch));

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

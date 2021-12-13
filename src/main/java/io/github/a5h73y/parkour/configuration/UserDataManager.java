package io.github.a5h73y.parkour.configuration;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;

import io.github.a5h73y.parkour.configuration.impl.UserDataConfig;

/**
 * Parkour Userdata Manager.
 * Manages and stores user's data.
 */
public class UserDataManager {
	
	private final File dataFolder;
	private final Map<UUID, UserDataConfig> userdata = new ConcurrentHashMap<>();
	
	/**
	 * Initialise the UserData Manager.
	 *
	 * @param dataFolder where to store the configs
	 */
	public UserDataManager(File dataFolder) {
		this.dataFolder = dataFolder;
		createDataFolder();
	}
	
	
	/**
	 * Get cached userdata or create new one.
	 * 
	 * @param uuid unique id of user's to load
	 * @return userdata config
	 */
	public UserDataConfig get(UUID uuid) {
		Objects.requireNonNull(uuid, "uuid cannot be null");
		return userdata.computeIfAbsent(uuid, (u) -> {
			// uuid not already cached, load new userdata
			UserDataConfig cfg = new UserDataConfig(uuid);
			cfg.setupFile(this.dataFolder);
			return cfg;
		});
	}
	
	
	public void cleanupCache() {
		this.userdata.entrySet().removeIf(e -> Bukkit.getPlayer(e.getKey()) == null && e.getValue().getUpdateTime() > 10000); // 10 seconds of caching
	}
	
	
	private void createDataFolder() {
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}
	}

}

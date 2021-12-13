package io.github.a5h73y.parkour.configuration.migration;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.configuration.impl.UserDataConfig;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.utility.PluginUtils;

public class PlayersMigration {
	
	
	private final File dataFolder;
	private final File oldFile;
	
	
	public PlayersMigration(File dataFolder) {
		this.dataFolder = dataFolder;
		this.oldFile = new File(this.dataFolder, "players.yml");
	}
	
	
	/**
	 * Check if given module requires migration.
	 * 
	 * @return true if migration can be processed
	 */
	public boolean isApplicable() {
		return this.oldFile.exists();
	}
	
	
	/**
	 * Process players migration.
	 * 
	 * @param newFolder userdata directory
	 */
	public void process(File newFolder) {
		
		PluginUtils.log("Processing players migration...");
		
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(this.oldFile);
		
		// Migrate server info
		ConfigurationSection serverInfo = cfg.getConfigurationSection("ServerInfo");
		if (serverInfo != null) {
			final ParkourConfiguration newServerInfo = Parkour.getConfig(ConfigType.SERVER_INFO);
			serverInfo.getKeys(false).parallelStream().forEach(key -> newServerInfo.set(key, serverInfo.get(key)));
			newServerInfo.save();
			// remove ServerInfo key from cached configuration because ServerInfo is not UUID
			cfg.set("ServerInfo", null);
		}
		
		Set<String> keys = cfg.getKeys(false);
		if (!keys.isEmpty()) for (String key : keys) {
			ConfigurationSection sec = cfg.getConfigurationSection(key);
			UUID uuid = UUID.fromString(key); // assume all data is correct
			UserDataConfig data = Parkour.getUserdata(uuid);
			sec.getKeys(false).parallelStream().forEach(path -> data.set(path, sec.get(path)));
			data.save();
		}
		
		File elderDir = new File(this.dataFolder, "old");
		if (!elderDir.exists()) elderDir.mkdirs();
		this.oldFile.renameTo(new File(elderDir, this.oldFile.getName())); // move data-file to old-backup folder
		PluginUtils.log("Players migration ended!");
	}
	
	
	
	

}

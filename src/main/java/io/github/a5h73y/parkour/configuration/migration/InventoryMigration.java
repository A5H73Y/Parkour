package io.github.a5h73y.parkour.configuration.migration;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.impl.UserDataConfig;
import io.github.a5h73y.parkour.utility.PluginUtils;

public class InventoryMigration {
	
	
	private final File dataFolder;
	private final File oldFile;
	
	
	public InventoryMigration(File dataFolder) {
		this.dataFolder = dataFolder;
		this.oldFile = new File(this.dataFolder, "inventory.yml");
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
	 * Process inventory migration.
	 * 
	 * @param newFolder userdata directory
	 */
	public void process(File newFolder) {
		
		PluginUtils.log("Processing inventory migration...");
		
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(this.oldFile);
		
		Set<String> keys = cfg.getKeys(false);
		if (!keys.isEmpty()) for (String key : keys) {
			ConfigurationSection sec = cfg.getConfigurationSection(key);
			UUID uuid = UUID.fromString(key); // assume all data is correct
			UserDataConfig data = Parkour.getUserdata(uuid);
			for (String path : sec.getKeys(false)) {
				data.set(path, sec.get(path));
			}
			data.save();
		}
		
		File elderDir = new File(this.dataFolder, "old");
		if (!elderDir.exists()) elderDir.mkdirs();
		this.oldFile.renameTo(new File(elderDir, this.oldFile.getName())); // move data-file to old-backup folder
		PluginUtils.log("Inventory migration ended!");
	}
	
	
	
	

}

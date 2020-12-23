package io.github.a5h73y.parkour.configuration;

import io.github.a5h73y.parkour.utility.PluginUtils;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * Base Parkour configuration file.
 */
public abstract class ParkourConfiguration extends YamlConfiguration {

	protected File file;

	/**
	 * The config file's name.
	 *
	 * @return file name
	 */
	protected abstract String getFileName();

	/**
	 * Initialise the configuration file.
	 */
	protected abstract void initializeConfig();

	/**
	 * Setup the file.
	 */
	void setupFile(File dataFolder) {
		file = new File(dataFolder, getFileName());
		createIfNotExists();
		// load it if it already exists
		reload();
		// default any missing values
		initializeConfig();
		// persist any changes
		save();
	}

	/**
	 * Persist any changes to the file.
	 */
	public void save() {
		try {
			this.save(file);
		} catch (IOException | YAMLException e) {
			PluginUtils.log("Failed to save file: " + getFileName(), 2);
			e.printStackTrace();
			reload();
		}
	}

	/**
	 * Reload the configuration file.
	 */
	protected void reload() {
		try {
			this.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			PluginUtils.log("Failed to load file: " + getFileName(), 2);
			e.printStackTrace();
		}
	}

	/**
	 * Create the physical file if it doesn't exist.
	 */
	private void createIfNotExists() {
		if (file.exists()) {
			return;
		}

		try {
			file.createNewFile();
			PluginUtils.log("Created " + getFileName());
		} catch (Exception e) {
			PluginUtils.log("Failed to create file: " + getFileName(), 2);
			e.printStackTrace();
		}
	}
}

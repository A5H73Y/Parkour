package io.github.a5h73y.parkour.configuration;

import io.github.a5h73y.parkour.utility.PluginUtils;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

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
	protected abstract void initializeConfig() throws IOException;

	/**
	 * Setup the file.
	 */
	void setupFile(File dataFolder) {
		file = new File(dataFolder, getFileName());
		createIfNotExists();
		reload();

		try {
			initializeConfig();
			save();
		} catch (IOException e) {
			PluginUtils.log("Failed to load " + getFileName(), 2);
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			this.save(file);
		} catch (IOException e) {
			PluginUtils.log("Failed to save file: " + getFileName(), 2);
			e.printStackTrace();
		}
	}

	protected void reload() {
		try {
			this.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			PluginUtils.log("Failed to load file: " + getFileName(), 2);
			e.printStackTrace();
		}
	}

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

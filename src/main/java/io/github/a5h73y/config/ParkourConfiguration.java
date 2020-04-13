package io.github.a5h73y.config;

import java.io.File;
import java.io.IOException;

import io.github.a5h73y.Parkour;
import io.github.a5h73y.utilities.Utils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class ParkourConfiguration extends YamlConfiguration {

	private static final File dataFolder = Parkour.getInstance().getDataFolder();

	protected File file;

	protected abstract String getFileName();

	protected abstract void initializeConfig() throws IOException;

	void setupFile() {
		file = new File(dataFolder, getFileName());
		createIfNotExists();
		reload();

		try {
			initializeConfig();
			save();
		} catch (IOException e) {
			Utils.log("Failed to load " + getFileName(), 2);
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			this.save(file);
		} catch (IOException e) {
			Utils.log("Failed to save file: " + getFileName(), 2);
			e.printStackTrace();
		}
	}

	protected void reload() {
		try {
			this.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			Utils.log("Failed to load file: " + getFileName(), 2);
			e.printStackTrace();
		}
	}

	private void createIfNotExists() {
		if (file.exists()) {
			return;
		}

		try {
			file.createNewFile();
			Utils.log("Created " + getFileName());
		} catch (Exception e) {
			Utils.log("Failed to create file: " + getFileName(), 2);
			e.printStackTrace();
		}
	}
}

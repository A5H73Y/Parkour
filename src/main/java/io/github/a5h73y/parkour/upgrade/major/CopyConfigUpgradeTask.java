package io.github.a5h73y.parkour.upgrade.major;

import de.leonhard.storage.Yaml;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedUpgradeTask;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

public class CopyConfigUpgradeTask extends TimedUpgradeTask {

	protected final String title;
	protected final FileConfiguration oldConfig;
	protected final Yaml newConfig;
	protected final String startsWith;

	/**
	 * Copy values from one config to another.
	 *
	 * @param parkourUpgrader upgrader
	 * @param title title of upgrade process
	 * @param oldConfig old config to search
	 * @param newConfig new config to create
	 * @param startsWith configurable clause to select config that matches
	 */
	public CopyConfigUpgradeTask(ParkourUpgrader parkourUpgrader,
	                             String title,
	                             FileConfiguration oldConfig,
	                             Yaml newConfig,
	                             @Nullable String startsWith) {
		super(parkourUpgrader);
		this.title = title;
		this.oldConfig = oldConfig;
		this.newConfig = newConfig;
		this.startsWith = startsWith;
	}

	@Override
	protected String getTitle() {
		return title;
	}

	@Override
	protected boolean doWork() {
		Set<String> allKeys = oldConfig.getKeys(true);

		for (String key : allKeys) {
			String stringValue = oldConfig.getString(key);
			// we only want to save actual values, not the section (as it will spam errors)
			if (stringValue != null && !stringValue.startsWith("MemorySection")) {
				if (startsWith == null) {
					newConfig.set(key, oldConfig.get(key));
				} else if (key.startsWith(startsWith)) {
					newConfig.set(key.replace(startsWith, ""), oldConfig.get(key));
				}
			}
		}

		return true;
	}
}

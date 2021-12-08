package io.github.a5h73y.parkour.upgrade.major;

import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedUpgradeTask;
import java.util.Set;
import de.leonhard.storage.Yaml;
import org.bukkit.configuration.file.FileConfiguration;

public class CopyConfigUpgradeTask extends TimedUpgradeTask {

	private final String title;
	private final FileConfiguration oldConfig;
	private final Yaml newConfig;
	private final String startsWith;

	public CopyConfigUpgradeTask(ParkourUpgrader parkourUpgrader, String title, FileConfiguration oldConfig, Yaml newConfig, String startsWith) {
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

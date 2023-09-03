package io.github.a5h73y.parkour.upgrade.minor;

import de.leonhard.storage.internal.FlatFile;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedUpgradeTask;

public abstract class TimedConfigUpgradeTask<E extends FlatFile> extends TimedUpgradeTask {

	E config;

	protected TimedConfigUpgradeTask(ParkourUpgrader parkourUpgrader, E config) {
		super(parkourUpgrader);
		this.config = config;
	}

	public void updateConfigEntry(String fromKey, String toKey) {
		updateConfigEntry(config, fromKey, toKey);
	}

	/**
	 * Copy value from one key to another in given config.
	 * @param config config file
	 * @param fromKey from config key
	 * @param toKey to config key
	 */
	public static void updateConfigEntry(FlatFile config, String fromKey, String toKey) {
		if (keyExists(config, fromKey)) {
			config.set(toKey, config.get(fromKey));
			config.remove(fromKey);
		}
	}

	public boolean keyExists(String key) {
		return keyExists(config, key);
	}

	public static boolean keyExists(FlatFile config, String key) {
		return key != null && config.contains(key);
	}
}

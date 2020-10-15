package io.github.a5h73y.parkour.upgrade;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.configuration.file.FileConfiguration;

public class StringsConfigUpgradeTask extends TimedUpgradeTask {

	public StringsConfigUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader);
	}

	@Override
	protected String getTitle() {
		return "Strings Config";
	}

	@Override
	protected boolean doWork() {
		boolean success = true;
		Set<String> strings = getParkourUpgrader().getStringsConfig().getConfigurationSection("").getKeys(true);
		Pattern pattern = Pattern.compile("%.*?%", Pattern.DOTALL);
		FileConfiguration config = getParkourUpgrader().getStringsConfig();

		for (String string : strings) {
			String value = getParkourUpgrader().getStringsConfig().getString(string);

			if (value != null && !value.isEmpty() && !value.startsWith("MemorySection")) {
				Matcher matcher = pattern.matcher(value);
				int results = countMatches(matcher);

				// we only want to replace the entries with a single value placeholder
				if (results == 1) {
					config.set(string, matcher.replaceAll("%VALUE%"));
				}
			}
		}

		config.set("Event.Checkpoint", "Checkpoint set to &b%CURRENT% &8/ &7%TOTAL%");

		try {
			getParkourUpgrader().saveStringsConfig();
		} catch (IOException e) {
			getParkourUpgrader().getLogger().severe("An error occurred during upgrade: " + e.getMessage());
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	int countMatches(Matcher matcher) {
		int counter = 0;
		while (matcher.find())
			counter++;
		return counter;
	}
}

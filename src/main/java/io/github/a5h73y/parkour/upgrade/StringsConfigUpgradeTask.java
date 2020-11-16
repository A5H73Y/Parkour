package io.github.a5h73y.parkour.upgrade;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringsConfigUpgradeTask extends TimedConfigUpgradeTask {

	public StringsConfigUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader, parkourUpgrader.getStringsConfig());
	}

	@Override
	protected String getTitle() {
		return "Strings Config";
	}

	@Override
	protected boolean doWork() {
		boolean success = true;
		Set<String> strings = getConfig().getConfigurationSection("").getKeys(true);
		Pattern pattern = Pattern.compile("%.*?%", Pattern.DOTALL);

		for (String string : strings) {
			String value = getConfig().getString(string);

			if (value != null && !value.isEmpty() && !value.startsWith("MemorySection")) {
				Matcher matcher = pattern.matcher(value);
				int results = countMatches(matcher);

				// we only want to replace the entries with a single value placeholder
				if (results == 1) {
					getConfig().set(string, matcher.replaceAll("%VALUE%"));
				}
			}
		}

		getConfig().set("Event.Checkpoint", "Checkpoint set to &b%CURRENT% &8/ &7%TOTAL%");
		transferAndDelete("Other.Item_LastCheckpoint", "Other.Item.LastCheckpoint");
		transferAndDelete("Other.Item_HideAll", "Other.Item.HideAll");
		transferAndDelete("Other.Item_Leave", "Other.Item.Leave");
		transferAndDelete("Other.Item_Restart", "Other.Item.Restart");

		transferAndDelete("Scoreboard.CourseTitle", "Scoreboard.CourseNameTitle");
		transferAndDelete("Scoreboard.BestTimeTitle", "Scoreboard.BestTimeEverTitle");
		transferAndDelete("Scoreboard.BestTimeNameTitle", "Scoreboard.BestTimeEverNameTitle");
		transferAndDelete("Scoreboard.BestTimeNameTitle", "Scoreboard.BestTimeEverNameTitle");
		// TODO finish me

		// deletes
		getConfig().set("Parkour.Invite", null);
		getConfig().set("Mode.Drunk", null);
		getConfig().set("Mode.Darkness", null);
		getConfig().set("ParkourGUI", null);

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
		while (matcher.find()) {
			counter++;
		}
		return counter;
	}
}

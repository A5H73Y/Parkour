package io.github.a5h73y.parkour.upgrade;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;

public class CourseInfoUpgradeTask extends TimedConfigUpgradeTask {

	public CourseInfoUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader, parkourUpgrader.getCoursesConfig());
	}

	@Override
	protected String getTitle() {
		return "CourseInfo";
	}

	@Override
	protected boolean doWork() {
		boolean success = true;
		List<String> courseNames = getConfig().getStringList("Courses");

		getParkourUpgrader().getLogger().info("Converting " + courseNames.size() + " courses.");
		int interval = Math.max(courseNames.size() / 10, 1);
		int count = 0;

		for (String courseName : courseNames) {
			if (count % interval == 0) {
				double percent = Math.ceil((count * 100.0d) / courseNames.size());
				getParkourUpgrader().getLogger().info(percent + "% complete...");
			}

			transferAndDelete(courseName + ".Finished", courseName + ".Ready");
			if (getConfig().contains(courseName + ".RewardDelay")) {
				getConfig().set(courseName + ".RewardDelay",
						getConfig().getInt(courseName + ".RewardDelay") * 24);
			}

			if (getConfig().contains(courseName + ".Mode")) {
				transferAndDelete(courseName + ".Mode", courseName + ".ParkourMode");

				String parkourMode = getConfig().getString(courseName + ".ParkourMode");
				if ("DRUNK".equals(parkourMode)) {
					getConfig().set(courseName + ".ParkourMode", "POTION");
					getConfig().set(courseName + ".PotionParkourMode.Effects", Collections.singletonList("CONFUSION,1000000,1"));
					getConfig().set(courseName + ".PotionParkourMode.JoinMessage", "You feel strange...");

				} else if ("DARKNESS".equals(parkourMode)) {
					getConfig().set(courseName + ".ParkourMode", "POTION");
					getConfig().set(courseName + ".PotionParkourMode.Effects", Collections.singletonList("BLINDNESS,1000000,1"));
					getConfig().set(courseName + ".PotionParkourMode.JoinMessage", "It suddenly becomes dark...");

				} else if ("MOON".equals(parkourMode)) {
					getConfig().set(courseName + ".ParkourMode", "POTION");
					getConfig().set(courseName + ".PotionParkourMode.Effects", Collections.singletonList("JUMP,1000000,5"));
					getConfig().set(courseName + ".PotionParkourMode.JoinMessage", "You feel weightless...");
				}
			}

			int checkpoints = getConfig().getInt(courseName + ".Points");
			getParkourUpgrader().getCheckpointsConfig().set(courseName + ".Checkpoints", checkpoints);
			getConfig().set(courseName + ".Points", null);

			for (int i = 0; i < checkpoints + 1; i++) {
				// first we do the checkpoints.yml file
				ConfigurationSection courseConfigSection = getConfig().getConfigurationSection(courseName + "." + i);

				if (courseConfigSection == null) {
					getParkourUpgrader().getLogger().info("Course " + courseName + " is already upgraded...");
					continue;
				}

				if (i > 0) {
					ConfigurationSection checkpointConfigSection = getParkourUpgrader().getCheckpointsConfig()
							.getConfigurationSection(courseName + "." + i);
					if (checkpointConfigSection != null) {
						Set<String> xyz = checkpointConfigSection.getKeys(false);
						for (String coordinate : xyz) {
							getParkourUpgrader().getCheckpointsConfig().set(courseName + "." + i + ".Plate" + coordinate,
									getParkourUpgrader().getCheckpointsConfig().get(courseName + "." + i + "." + coordinate));
							getParkourUpgrader().getCheckpointsConfig().set(courseName + "." + i + "." + coordinate, null);
						}
					}
				}

				// then transfer data from courses.yml to checkpoints.yml
				Set<String> checkpointData = courseConfigSection.getKeys(false);

				for (String checkpointDatum : checkpointData) {
					getParkourUpgrader().getCheckpointsConfig().set(courseName + "." + i + "." + checkpointDatum,
							getConfig().getDouble(courseName + "." + i + "." + checkpointDatum));
				}
				getConfig().set(courseName + "." + i, null);
			}

			count++;
		}

		try {
			getParkourUpgrader().saveCheckpointsConfig();
			getParkourUpgrader().saveCoursesConfig();
		} catch (IOException e) {
			getParkourUpgrader().getLogger().severe("An error occurred during upgrade: " + e.getMessage());
			e.printStackTrace();
			success = false;
		}
		return success;
	}
}

package io.github.a5h73y.parkour.upgrade;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;

public class CourseInfoUpgradeTask extends TimedUpgradeTask {

	public CourseInfoUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader);
	}

	@Override
	protected String getTitle() {
		return "CourseInfo";
	}

	@Override
	protected boolean doWork() {
		boolean success = true;
		List<String> courseNames = getParkourUpgrader().getCoursesConfig().getStringList("Courses");

		getParkourUpgrader().getLogger().info("Converting " + courseNames.size() + " courses.");
		int interval = Math.max(courseNames.size() / 10, 1);
		int count = 0;

		for (String courseName : courseNames) {
			if (count % interval == 0) {
				double percent = Math.ceil((count * 100.0d) / courseNames.size());
				getParkourUpgrader().getLogger().info(percent + "% complete...");
			}

			int checkpoints = getParkourUpgrader().getCoursesConfig().getInt(courseName + ".Points");
			getParkourUpgrader().getCoursesConfig().set(courseName + ".Checkpoints", checkpoints);
			getParkourUpgrader().getCoursesConfig().set(courseName + ".Points", null);

			for (int i = 0; i < checkpoints + 1; i++) {
				// first we do the checkpoints.yml file
				ConfigurationSection courseConfigSection = getParkourUpgrader().getCoursesConfig().getConfigurationSection(courseName + "." + i);

				if (courseConfigSection == null) {
					getParkourUpgrader().getLogger().info("Course " + courseName + " is already upgraded...");
					continue;
				}

				if (i > 0) {
					ConfigurationSection checkpointConfigSection = getParkourUpgrader().getCheckpointsConfig().getConfigurationSection(courseName + "." + i);
					Set<String> xyz = checkpointConfigSection.getKeys(false);
					for (String coordinate : xyz) {
						getParkourUpgrader().getCheckpointsConfig().set(courseName + "." + i + ".Plate" + coordinate, getParkourUpgrader().getCheckpointsConfig().get(courseName + "." + i + "." + coordinate));
						getParkourUpgrader().getCheckpointsConfig().set(courseName + "." + i + "." + coordinate, null);
					}
				}

				// then transfer data from courses.yml to checkpoints.yml
				Set<String> checkpointData = courseConfigSection.getKeys(false);

				for (String checkpointDatum : checkpointData) {
					getParkourUpgrader().getCheckpointsConfig().set(courseName + "." + i + "." + checkpointDatum, getParkourUpgrader().getCoursesConfig().getDouble(courseName + "." + i + "." + checkpointDatum));
				}
				getParkourUpgrader().getCoursesConfig().set(courseName + "." + i, null);
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

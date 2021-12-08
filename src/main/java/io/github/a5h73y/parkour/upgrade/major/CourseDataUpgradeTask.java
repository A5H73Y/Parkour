package io.github.a5h73y.parkour.upgrade.major;

import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.course.autostart.AutoStartConfig;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedConfigUpgradeTask;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;

public class CourseDataUpgradeTask extends TimedConfigUpgradeTask {

	public CourseDataUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader, parkourUpgrader.getCoursesConfig());
	}

	@Override
	protected String getTitle() {
		return "Course Data";
	}

	@Override
	protected boolean doWork() {
		boolean success = true;
		List<String> courseNames = getConfig().getStringList("Courses");

		getParkourUpgrader().getLogger().info("Converting " + courseNames.size() + " courses.");
		int interval = Math.max(courseNames.size() / 10, 1);
		int count = 0;

		for (String courseName : courseNames) {
			courseName = courseName.toLowerCase();
			if (count % interval == 0) {
				double percent = Math.ceil((count * 100.0d) / courseNames.size());
				getParkourUpgrader().getLogger().info(percent + "% complete...");
			}

			ConfigurationSection courseSection = getConfig().getConfigurationSection(courseName);

			if (courseSection != null) {
				CourseConfig newCourseConfig = CourseConfig.getConfig(courseName);

				Set<String> courseKeys = courseSection.getKeys(true); // this will mean we get a deep config entry
				for (String key : courseKeys) {
					newCourseConfig.set(key, courseSection.get(key));
				}

				newCourseConfig.set("Name", courseName);
				updateCheckpointSection(newCourseConfig, courseName);
				updateEconomySection(newCourseConfig, courseName);
			}

			count++;
		}

		updateAutoStartSection();

		return success;
	}

	private void updateCheckpointSection(CourseConfig courseConfig, String courseName) {
		ConfigurationSection checkpointSection =
				getParkourUpgrader().getCheckpointsConfig().getConfigurationSection(courseName);

		if (checkpointSection != null) {
			Set<String> checkpoint = checkpointSection.getKeys(true); // this will mean we get a deep config entry
			for (String key : checkpoint) {
				String newKey = key;

				if (newKey.contains("Plate")) {
					newKey = newKey.replace("Plate", "Checkpoint");
				} else {
					newKey = newKey.replaceAll("\\d+", "$0.Location");
				}

				// it's the final detail
				if (newKey.split("\\.").length == 3) {
					int lastIndex = newKey.lastIndexOf(".");
					newKey = newKey.substring(0, lastIndex) + newKey.substring(lastIndex).toLowerCase();
				}

				courseConfig.set("Checkpoint." + newKey, checkpointSection.get(key));
			}

			// move location of 'Checkpoints' count
			Integer numberOfCheckpoints = courseConfig.getInt("Checkpoint.Checkpoints");
			courseConfig.set("Checkpoints", numberOfCheckpoints);
			courseConfig.remove("Checkpoint.Checkpoints");

			// update each checkpoint to include world name
			String courseWorld = courseConfig.getString("World");
			Set<String> eachCheckpoint = courseConfig.getSection("Checkpoint").singleLayerKeySet();
			eachCheckpoint.forEach(checkpointNumber -> courseConfig.set("Checkpoint." + checkpointNumber + ".Location.world", courseWorld));
		}
	}

	private void updateEconomySection(CourseConfig courseConfig, String courseName) {
		ConfigurationSection section = getParkourUpgrader().getEconomyConfig()
				.getConfigurationSection("Price." + courseName);

		if (section != null) {
			courseConfig.set("EconomyJoiningFee", section.getDouble("JoinFee"));
			courseConfig.set("EconomyFinishReward", section.getDouble("Finish"));
		}
	}

	private void updateAutoStartSection() {
		ConfigurationSection autoStartSection = getConfig().getConfigurationSection("CourseInfo.AutoStart");

		if (autoStartSection != null) {
			Set<String> autoStarts = autoStartSection.getKeys(false);
			getParkourUpgrader().getLogger().info("Converting " + autoStarts.size() + " AutoStarts.");

			AutoStartConfig autoStartConfig = getParkourUpgrader().getNewConfigManager().getAutoStartConfig();

			for (String coordinates : autoStarts) {
				String newCoordinates = coordinates;
				if (coordinates.startsWith("-")) {
					newCoordinates = newCoordinates.replaceFirst("-", "|");
				}

				newCoordinates = newCoordinates.replace("--", "-|");

				if (coordinates.split("-").length >= 2) {
					newCoordinates = newCoordinates.replace("-", "/");
				}

				autoStartConfig.setAutoStartCourse(newCoordinates.replace("|", "-"), autoStartSection.getString(coordinates));
			}
		}
	}
}

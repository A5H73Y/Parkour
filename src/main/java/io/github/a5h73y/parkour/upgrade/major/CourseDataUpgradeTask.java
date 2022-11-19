package io.github.a5h73y.parkour.upgrade.major;

import de.leonhard.storage.sections.FlatFileSection;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.course.ParkourEventType;
import io.github.a5h73y.parkour.type.course.autostart.AutoStartConfig;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class CourseDataUpgradeTask extends TimedLegacyConfigUpgradeTask {

	public CourseDataUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader, parkourUpgrader.getCoursesConfig());
	}

	@Override
	protected String getTitle() {
		return "Course Data";
	}

	@Override
	protected boolean doWork() {
		List<String> courseNames = getConfig().getStringList("Courses");

		getParkourUpgrader().getLogger().log(Level.INFO, "Converting {0} courses.", courseNames.size());
		int interval = Math.max(courseNames.size() / 10, 1);
		int count = 0;

		for (String courseName : courseNames) {
			courseName = courseName.toLowerCase();
			if (count % interval == 0) {
				double percent = Math.ceil((count * 100.0d) / courseNames.size());
				getParkourUpgrader().getLogger().log(Level.INFO, "{0}% complete...", percent);
			}

			ConfigurationSection courseSection = getConfig().getConfigurationSection(courseName);

			if (courseSection != null) {
				CourseConfig newCourseConfig = CourseConfig.getConfig(courseName);

				Set<String> courseKeys = courseSection.getKeys(true); // this will mean we get a deep config entry
				for (String key : courseKeys) {
					newCourseConfig.set(key, courseSection.get(key));
				}

				if ("DROPPER".equals(newCourseConfig.getParkourModeName())) {
					newCourseConfig.setMaximumFallTicks(0);
					newCourseConfig.remove(CourseConfig.PARKOUR_MODE);
				}

				updateEventsSection(newCourseConfig);
				updateCheckpointSection(newCourseConfig, courseName);
				updateEconomySection(newCourseConfig, courseName);
				updateJoinItems(newCourseConfig);

				newCourseConfig.set("Name", courseName);
				newCourseConfig.remove("World");
			}

			count++;
		}

		updateAutoStartSection();
		return true;
	}

	private void updateJoinItems(CourseConfig newCourseConfig) {
		FlatFileSection section = newCourseConfig.getSection(CourseConfig.JOIN_ITEMS);
		if (section != null && section.singleLayerKeySet().size() > 0) {
			Set<String> materialNames = section.singleLayerKeySet();
			List<ItemStack> itemStacks = new ArrayList<>();

			for (String materialName : materialNames) {
				Material material = Material.getMaterial(materialName);
				if (material != null) {
					int amount = section.getInt(materialName + ".Amount");
					String label = section.getString(materialName + ".Label");
					boolean unbreakable = section.getBoolean(materialName + ".Unbreakable");
					itemStacks.add(MaterialUtils.createItemStack(material, amount, label, unbreakable, null));
				}
			}

			newCourseConfig.remove(CourseConfig.JOIN_ITEMS);

			List<String> results = itemStacks.stream()
					.map(itemStack ->
							getParkourUpgrader().getNewConfigManager().getItemStackSerializable().serialize(itemStack))
					.collect(Collectors.toList());

			newCourseConfig.set(CourseConfig.JOIN_ITEMS, results);
		}
	}

	private void updateEventsSection(CourseConfig newCourseConfig) {
		Arrays.stream(ParkourEventType.values()).forEach(parkourEventType -> {
			String oldEventName = StringUtils.standardizeText(parkourEventType.getDisplayName());

			if (newCourseConfig.contains(oldEventName + "Command")) {
				newCourseConfig.set("Command." + parkourEventType.getConfigEntry(),
						newCourseConfig.getStringList(oldEventName + "Command"));
				newCourseConfig.remove(oldEventName + "Command");
			}

			if (newCourseConfig.contains(oldEventName + "Message")) {
				newCourseConfig.set("Message." + parkourEventType.getConfigEntry(),
						newCourseConfig.getString(oldEventName + "Message"));
				newCourseConfig.remove(oldEventName + "Message");
			}
		});
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
			eachCheckpoint.forEach(checkpointNumber ->
					courseConfig.set("Checkpoint." + checkpointNumber + ".Location.world", courseWorld));
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
			getParkourUpgrader().getLogger().log(Level.INFO, "Converting {0} AutoStarts.", autoStarts.size());

			AutoStartConfig autoStartConfig = getParkourUpgrader().getNewConfigManager().getAutoStartConfig();

			for (String coordinates : autoStarts) {
				String courseName = autoStartSection.getString(coordinates);
				String newCoordinates = coordinates;

				if (courseName == null) {
					continue;
				}

				if (coordinates.startsWith("-")) {
					newCoordinates = newCoordinates.replaceFirst("-", "|");
				}

				newCoordinates = newCoordinates.replace("--", "-|");

				if (coordinates.split("-").length >= 2) {
					newCoordinates = newCoordinates.replace("-", "/");
				}

				autoStartConfig.setAutoStartCourse(newCoordinates.replace("|", "-"), courseName);
			}
		}
	}
}

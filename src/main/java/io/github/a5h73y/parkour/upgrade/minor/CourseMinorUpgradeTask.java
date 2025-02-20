package io.github.a5h73y.parkour.upgrade.minor;

import static io.github.a5h73y.parkour.type.course.CourseConfig.DIE_IN_LAVA;
import static io.github.a5h73y.parkour.type.course.CourseConfig.DIE_IN_WATER;

import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedUpgradeTask;
import java.util.List;
import java.util.Objects;

public class CourseMinorUpgradeTask extends TimedUpgradeTask {

	public CourseMinorUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader);
	}

	@Override
	protected String getTitle() {
		return "Course Configs";
	}

	@Override
	protected boolean doWork() {
		List<String> courseNames = getParkourUpgrader().getNewConfigManager().getAllCourseNames();

		courseNames.stream()
				.filter(Objects::nonNull)
				.forEach(courseName -> {
					try {
						CourseConfig config = getParkourUpgrader().getNewConfigManager().getCourseConfig(courseName);

						if (config.contains("DieInLiquid")) {
                            boolean dieInLiquid = config.getBoolean("DieInLiquid");
                            config.set(DIE_IN_WATER, dieInLiquid);
                            config.set(DIE_IN_LAVA, dieInLiquid);
                            config.remove("DieInLiquid");

							getParkourUpgrader().getLogger().info("DONE!");
						}
					} catch (Exception ex) {
						getParkourUpgrader().getLogger().severe("Failed to upgrade Course Config for: " + courseName);
						getParkourUpgrader().getLogger().severe(ex.getMessage());
					}
				});

		return true;
	}
}

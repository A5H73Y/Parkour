package io.github.a5h73y.parkour.enums;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum ParkourEventType {
	JOIN("Join"),
	LEAVE("Leave"),
	PRIZE("Prize"),
	FINISH("Finish"),
	CHECKPOINT("Checkpoint"),
	CHECKPOINT_ALL("CheckpointAll"),
	DEATH("Death"),
	COURSE_RECORD("CourseRecord");

	private final String configEntry;

	ParkourEventType(String configEntry) {
		this.configEntry = configEntry;
	}

	public String getConfigEntry() {
		return configEntry;
	}

	@NotNull
	public String getDisplayName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
}

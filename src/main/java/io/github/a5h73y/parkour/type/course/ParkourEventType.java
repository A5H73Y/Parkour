package io.github.a5h73y.parkour.type.course;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ParkourEventType {
	JOIN("Join"),
	LEAVE("Leave"),
	PRIZE("Prize"),
	NO_PRIZE("NoPrize"),
	FINISH("Finish"),
	CHECKPOINT("Checkpoint"),
	CHECKPOINT_ALL("CheckpointAll"),
	DEATH("Death"),
	PLAYER_COURSE_RECORD("PlayerCourseRecord"),
	GLOBAL_COURSE_RECORD("GlobalCourseRecord");

	private final String configEntry;

	ParkourEventType(String configEntry) {
		this.configEntry = configEntry;
	}

	public String getConfigEntry() {
		return configEntry;
	}

	@NotNull
	public String getDisplayName() {
		return this.name().toLowerCase().replace("_", " ");
	}

	/**
	 * Get possible event types, comma separated.
	 * @return event types list
	 */
	public static String getAllParkourEventTypes() {
		return Arrays.stream(values())
				.map(ParkourEventType::getConfigEntry)
				.sorted().collect(Collectors.joining(", "))
				.toLowerCase();
	}

	/**
	 * Find ParkourEventType by config entry value.
	 * @param eventConfigEntry event config entry
	 * @return matching ParkourEventType
	 */
	@Nullable
	public static ParkourEventType findByConfigEntry(@Nullable String eventConfigEntry) {
		return Arrays.stream(values())
				.filter(parkourEventType -> parkourEventType.getConfigEntry().equalsIgnoreCase(eventConfigEntry))
				.findFirst().orElse(null);
	}
}

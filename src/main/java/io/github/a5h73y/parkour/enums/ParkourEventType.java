package io.github.a5h73y.parkour.enums;

public enum ParkourEventType {
	JOIN("Join"),
	LEAVE("Leave"),
	PRIZE("Prize"),
	FINISH("Finish"),
	CHECKPOINT("Checkpoint"),
	DEATH("Death");

	private final String configEntry;

	ParkourEventType(String configEntry) {
		this.configEntry = configEntry;
	}

	public String getConfigEntry() {
		return configEntry;
	}
}

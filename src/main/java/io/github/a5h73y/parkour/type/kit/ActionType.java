package io.github.a5h73y.parkour.type.kit;

import org.jetbrains.annotations.NotNull;

public enum ActionType {
	FINISH(true),
	DEATH(true),
	LAUNCH(true),
	BOUNCE(true),
	SPEED(true),
	NORUN(true),
	NOPOTION(true),
	CLIMB(false),
	REPULSE(false),
	POTION(true);

	private final boolean isFloorType;

	ActionType(boolean isFloorType) {
		this.isFloorType = isFloorType;
	}

	public boolean isFloorType() {
		return isFloorType;
	}

	@NotNull
	public String getDisplayName() {
		return this.name().toLowerCase();
	}
}

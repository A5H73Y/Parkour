package io.github.a5h73y.parkour.type.kit;

import org.jetbrains.annotations.NotNull;

public enum ActionType {
	FINISH,
	DEATH,
	LAUNCH,
	BOUNCE,
	SPEED,
	NORUN,
	NOPOTION,
	CLIMB,
	REPULSE,
	POTION;

	@NotNull
	public String getDisplayName() {
		return this.name().toLowerCase();
	}
}

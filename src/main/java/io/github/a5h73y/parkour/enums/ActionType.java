package io.github.a5h73y.parkour.enums;

import java.util.Locale;
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
	REPULSE;

	@NotNull
	public String getDisplayName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
}

package io.github.a5h73y.parkour.enums;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum ParkourMode {
    NONE,
    SPEEDY,
    FREEDOM,
    DROPPER,
    ROCKETS,
    NORUN,
    FREE_CHECKPOINT,
    POTION;

    @NotNull
    public String getDisplayName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}

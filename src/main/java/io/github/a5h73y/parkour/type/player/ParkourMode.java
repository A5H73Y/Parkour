package io.github.a5h73y.parkour.type.player;

import org.jetbrains.annotations.NotNull;

public enum ParkourMode {
    NONE,
    SPEEDY,
    FREEDOM,
    DROPPER,
    ROCKETS,
    NORUN,
    POTION;

    @NotNull
    public String getDisplayName() {
        return this.name().toLowerCase();
    }
}

package io.github.a5h73y.parkour.enums;

public enum ParkourMode {
    NONE,
    SPEEDY,
    FREEDOM,
    DROPPER,
    ROCKETS,
    NORUN, // cancel run toggle event
    POTION_EFFECT
        // has PotionEffectType (BLINDNESS)
        // has duration (10000)
        // has strength (1)
        // has optional join text (i.e. "It suddenly becomes dark...")
}

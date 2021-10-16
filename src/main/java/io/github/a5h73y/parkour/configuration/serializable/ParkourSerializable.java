package io.github.a5h73y.parkour.configuration.serializable;

import java.io.Serializable;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public interface ParkourSerializable extends Serializable {

    @NotNull
    Map<String, Object> serialize();

    default Object defaultValue(Object value, Object defaultValue) {
        if (value == null || String.valueOf(value) == null) {
            return defaultValue;
        } else {
            return value;
        }
    }
}

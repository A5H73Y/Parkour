package io.github.a5h73y.parkour.configuration.serializable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Allow for implementations to specify their method of serialization.
 */
public interface ParkourSerializable extends Serializable {

    @NotNull
    Map<String, Object> serialize();

    /**
     * Get the Map value of input.
     * @param input hashmap input
     * @return hashmap value
     */
    static HashMap<String, Object> getMapValue(Object input) {
        return (HashMap<String, Object>) input;
    }

}

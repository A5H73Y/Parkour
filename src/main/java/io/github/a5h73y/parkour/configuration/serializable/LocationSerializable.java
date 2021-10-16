package io.github.a5h73y.parkour.configuration.serializable;

import java.util.Map;
import de.leonhard.storage.internal.serialize.LightningSerializable;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class LocationSerializable implements LightningSerializable<Location> {

	@Override
	public Location deserialize(@NotNull Object input) throws ClassCastException {
		Location result = null;
		if (input instanceof Map) {
			Map<String, Object> testing = (Map<String, Object>) input;
			result = Location.deserialize(testing);
		}
		return result;
	}

	@Override
	public Map<String, Object> serialize(@NotNull Location location) throws ClassCastException {
		return location.serialize();
	}

	@Override
	public Class<Location> getClazz() {
		return Location.class;
	}
}

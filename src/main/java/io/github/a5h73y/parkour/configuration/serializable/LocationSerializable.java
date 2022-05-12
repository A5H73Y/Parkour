package io.github.a5h73y.parkour.configuration.serializable;

import static io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable.getMapValue;

import de.leonhard.storage.internal.serialize.LightningSerializable;
import java.util.Map;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class LocationSerializable implements LightningSerializable<Location> {

	@Override
	public Location deserialize(@NotNull Object input) throws ClassCastException {
		Location result = null;
		if (input instanceof Map) {
			result = Location.deserialize(getMapValue(input));
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

package io.github.a5h73y.parkour.configuration.serializable;

import static io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable.getMapValue;

import de.leonhard.storage.internal.serialize.LightningSerializable;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class ParkourSessionSerializable implements LightningSerializable<ParkourSession> {

	@Override
	public Map<String, Object> serialize(@NotNull ParkourSession parkourSession) throws ClassCastException {
		return parkourSession.serialize();
	}

	@Override
	public ParkourSession deserialize(@NotNull Object input) throws ClassCastException {
		ParkourSession parkourSession = null;
		if (input instanceof Map) {
			parkourSession = ParkourSession.deserialize(getMapValue(input));
		}
		return parkourSession;
	}

	@Override
	public Class<ParkourSession> getClazz() {
		return ParkourSession.class;
	}
}

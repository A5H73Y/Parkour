package io.github.a5h73y.parkour.type;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import de.leonhard.storage.internal.FlatFile;

public abstract class ParkourManager extends AbstractPluginReceiver {

	protected ParkourManager(Parkour parkour) {
		super(parkour);
	}

	protected abstract FlatFile getConfig();

}

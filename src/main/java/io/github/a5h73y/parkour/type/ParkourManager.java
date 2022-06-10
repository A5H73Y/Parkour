package io.github.a5h73y.parkour.type;

import de.leonhard.storage.internal.FlatFile;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;

public abstract class ParkourManager extends AbstractPluginReceiver {

	protected ParkourManager(Parkour parkour) {
		super(parkour);
	}

	protected abstract FlatFile getConfig();

}

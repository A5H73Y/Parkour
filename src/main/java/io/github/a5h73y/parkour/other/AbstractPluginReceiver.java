package io.github.a5h73y.parkour.other;

import io.github.a5h73y.parkour.Parkour;

/**
 * Ensure the concrete class receives an instance of the Parkour plugin.
 */
public abstract class AbstractPluginReceiver {

	protected final Parkour parkour;

	protected AbstractPluginReceiver(final Parkour parkour) {
		this.parkour = parkour;
	}

}

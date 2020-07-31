package io.github.a5h73y.parkour.plugin;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.ParkourPlaceholders;

/**
 * {@link me.clip.placeholderapi.PlaceholderAPI} integration.
 * Allow for usage of Parkour placeholders.
 */
public class PlaceholderApi extends PluginWrapper {

	@Override
	public String getPluginName() {
		return "PlaceholderAPI";
	}

	@Override
	protected void initialise() {
		super.initialise();

		if (isEnabled()) {
			new ParkourPlaceholders(Parkour.getInstance()).register();
		}
	}
}

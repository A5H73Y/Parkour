package io.github.a5h73y.parkour.plugin;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.ParkourPlaceholders;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * {@link me.clip.placeholderapi.PlaceholderAPI} integration.
 * Allow for usage of Parkour placeholders.
 */
public class PlaceholderApi extends PluginWrapper {

	private ParkourPlaceholders placeholders;

	public PlaceholderApi(Parkour parkour) {
		super(parkour);
	}

	@Override
	public String getPluginName() {
		return "PlaceholderAPI";
	}

	@Override
	protected void initialise() {
		super.initialise();

		if (isEnabled()) {
			placeholders = new ParkourPlaceholders(Parkour.getInstance());
			placeholders.register();
		}
	}

	/**
	 * Evaluate the Placeholder value.
	 * Serves as a shorthand to "/papi parse me (placeholder)" but with a precursor to check integration.
	 *
	 * @param player player
	 * @param placeholder placeholder to evaluate
	 */
	public void evaluatePlaceholder(Player player, String placeholder) {
		if (!isEnabled()) {
			TranslationUtils.sendMessage(player, "PlaceholderAPI hasn't been linked.");
		} else if (!placeholder.startsWith("%") && !placeholder.endsWith("%")) {
			TranslationUtils.sendMessage(player, "Invalid Placeholder syntax, must start and end with &b%&f.");
		} else {
			TranslationUtils.sendMessage(player, PlaceholderAPI.setPlaceholders(player, placeholder), false);
		}
	}

	/**
	 * Parse PlaceholderAPI variables in the input.
	 * Any Placeholders in the text input will be replaced by PlaceholderAPI.
	 *
	 * @param player player context
	 * @param input input
	 * @return updated input
	 */
	public String parsePlaceholders(Player player, String input) {
		if (isEnabled()) {
			return PlaceholderAPI.setPlaceholders(player, input);
		} else {
			return input;
		}
	}

	public ParkourPlaceholders getPlaceholders() {
		return placeholders;
	}

	/**
	 * Clear PlaceholderAPI cache.
	 */
	public void clearCache() {
		if (isEnabled()) {
			this.getPlaceholders().clearCache();
		}
	}
}

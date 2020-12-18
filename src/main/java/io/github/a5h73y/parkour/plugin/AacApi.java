package io.github.a5h73y.parkour.plugin;

import static org.bukkit.Bukkit.getServer;

import io.github.a5h73y.parkour.Parkour;
import me.konsolas.aac.api.PlayerViolationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Advanced Anti Cheat Integration.
 */
public class AacApi extends PluginWrapper implements Listener {

	@Override
	public String getPluginName() {
		return "AAC";
	}

	@Override
	protected void initialise() {
		super.initialise();

		if (isEnabled()) {
			getServer().getPluginManager().registerEvents(this, Parkour.getInstance());
		}
	}

	/**
	 * Handle Player Violation Event.
	 * If a Player is on a Course, prevent the violation.
	 *
	 * @param event violation event
	 */
	@EventHandler
	public void onViolation(PlayerViolationEvent event) {
		if (Parkour.getInstance().getPlayerManager().isPlaying(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
}

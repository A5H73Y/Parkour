package io.github.a5h73y.parkour.plugin;

import static org.bukkit.Bukkit.getServer;

import io.github.a5h73y.parkour.Parkour;
import me.konsolas.aac.api.PlayerViolationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AACApi extends PluginWrapper implements Listener {

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

	@EventHandler
	public void onViolation(PlayerViolationEvent event) {
		if (Parkour.getInstance().getPlayerManager().isPlaying(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
}

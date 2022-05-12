package io.github.a5h73y.parkour.listener.interact;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.plugin.BountifulApi;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AutoStartListener extends AbstractPluginReceiver implements Listener {

	public AutoStartListener(Parkour parkour) {
		super(parkour);
	}

	/**
	 * Handle Player Interaction Event.
	 * Used to handle the pressure plate interaction while NOT on a Course.
	 * This is used to identify if the plate matches an AutoStart location.
	 *
	 * @param event PlayerInteractEvent
	 */
	@EventHandler
	public void onAutoStartEvent(PlayerInteractEvent event) {
		if (event.getAction() != Action.PHYSICAL) {
			return;
		}

		Block below = event.getClickedBlock().getRelative(BlockFace.DOWN);

		if (below.getType() != parkour.getParkourConfig().getAutoStartMaterial()) {
			return;
		}

		if (parkour.getParkourConfig().getBoolean("OnCourse.PreventPlateStick")) {
			event.setCancelled(true);
		}

		// Prevent a user spamming the joins
		if (!parkour.getPlayerManager().delayPlayer(event.getPlayer(), 1)) {
			return;
		}

		String courseName = parkour.getAutoStartManager().getAutoStartCourse(event.getClickedBlock().getLocation());

		if (courseName != null) {
			ParkourSession session = parkour.getParkourSessionManager().getParkourSession(event.getPlayer());
			if (session != null) {
				// we only want to do something if the names match
				if (session.getCourseName().equals(courseName)) {
					session.resetProgress();
					session.setFreedomLocation(null);

					parkour.getBountifulApi().sendSubTitle(event.getPlayer(),
							TranslationUtils.getTranslation("Parkour.Restarting", false),
							BountifulApi.JOIN_COURSE);
				}
			} else {
				parkour.getPlayerManager().joinCourseButDelayed(
						event.getPlayer(), courseName, parkour.getParkourConfig().getAutoStartDelay());
			}
		}
	}
}

package io.github.a5h73y.parkour.listener.interact;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.plugin.BountifulApi;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.utility.TaskCooldowns;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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

		Player player = event.getPlayer();

		// Prevent a user spamming the joins
		if (!TaskCooldowns.getInstance().delayPlayer(player, "autostart", 1)) {
			return;
		}

		String courseName = parkour.getAutoStartManager().getAutoStartCourse(event.getClickedBlock().getLocation());

		if (courseName != null) {
			ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);
			if (session != null) {
				// we only want to do something if the names match
				if (session.getCourseName().equals(courseName)
						&& parkour.getParkourConfig().getBoolean("AutoStart.RestartWhenOnCourse.Enabled")) {
					if (parkour.getParkourConfig().getBoolean("AutoStart.RestartWhenOnCourse.Teleport")) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(parkour, () ->
								parkour.getPlayerManager().fastRestartCourse(player), 1);
					} else {
						parkour.getPlayerManager().resetCourseProgress(player);
					}

					parkour.getBountifulApi().sendSubTitle(event.getPlayer(),
							TranslationUtils.getTranslation("Parkour.Restarting", false),
							BountifulApi.JOIN_COURSE);
				}
			} else {
				parkour.getPlayerManager().joinCourseButDelayed(
						player, courseName, parkour.getParkourConfig().getAutoStartDelay());
			}
		}
	}

	/**
	 * On AutoStart plate break.
	 * @param event block break event
	 */
	@EventHandler
	public void onBreakAutoStart(BlockBreakEvent event) {
		if (event.getBlock().getType().name().endsWith("PRESSURE_PLATE")
				&& parkour.getAutoStartManager().doesAutoStartExist(event.getBlock().getLocation())) {
			if (!PermissionUtils.hasPermission(event.getPlayer(), Permission.ADMIN_DELETE)) {
				event.setCancelled(true);

			} else if (!event.getPlayer().isSneaking()) {
				Location location = event.getBlock().getLocation();
				String coordinates = parkour.getAutoStartManager().getAutoStartCoordinates(location.getBlock());
				parkour.getAutoStartManager().deleteAutoStart(event.getPlayer(), coordinates);
			}
		}
	}
}

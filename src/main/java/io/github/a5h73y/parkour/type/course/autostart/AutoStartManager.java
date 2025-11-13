package io.github.a5h73y.parkour.type.course.autostart;

import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;

import com.cryptomorin.xseries.XMaterial;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.ParkourManager;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * AutoStart Manager.
 */
public class AutoStartManager extends ParkourManager {

	public AutoStartManager(final Parkour parkour) {
		super(parkour);
	}

	@Override
	protected AutoStartConfig getConfig() {
		return parkour.getConfigManager().getAutoStartConfig();
	}

	/**
	 * Get AutoStart coordinates for Block.
	 * The X/Y/Z(/World) format for the provided Block.
	 *
	 * @param block block
	 * @return autostart coordinates
	 */
	public String getAutoStartCoordinates(@NotNull Block block) {
		String coordinates = block.getX() + "/" + block.getY() + "/" + block.getZ();

		if (parkour.getParkourConfig().getBoolean("AutoStart.IncludeWorldName")) {
			coordinates += "/" + block.getWorld().getName();
		}

		return coordinates;
	}

	/**
	 * Find the matching Course name for the AutoStart Location.
	 * Request to match the location coordinates to a known AutoStart location, and retrieve the corresponding Course.
	 *
	 * @param location location
	 * @return course name
	 */
	@Nullable
	public String getAutoStartCourse(final Location location) {
		String coordinates = getAutoStartCoordinates(location.getBlock());
		return getConfig().getAutoStartCourse(coordinates);
	}

	/**
	 * Does AutoStart exist for given Location.
	 *
	 * @param location location
	 * @return autostart exists
	 */
	public boolean doesAutoStartExist(Location location) {
		String coordinates = getAutoStartCoordinates(location.getBlock());
		return getConfig().doesAutoStartExist(coordinates);
	}

	/**
	 * Create an AutoStart for the Course.
	 * A pressure plate will be placed at the player's location and trigger an AutoStart for the Course.
	 *
	 * @param player requesting player
	 * @param courseName course name
	 */
	public void createAutoStart(final Player player, final String courseName) {
		if (!parkour.getCourseManager().doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, player);
			return;
		}

		Block block = player.getLocation().getBlock();
		String coordinates = getAutoStartCoordinates(block);

		if (getConfig().doesAutoStartExist(coordinates)) {
			TranslationUtils.sendMessage(player, "There is already an AutoStart here!");
			return;
		}

		getConfig().setAutoStartCourse(coordinates, courseName);

		block.setType(XMaterial.STONE_PRESSURE_PLATE.get());
		Block blockUnder = block.getRelative(BlockFace.DOWN);
		blockUnder.setType(parkour.getParkourConfig().getAutoStartMaterial());

		TranslationUtils.sendPropertySet(player, "AutoStart", courseName, "your position");
	}

	/**
	 * Delete the AutoStart at the given coordinates.
	 *
	 * @param commandSender command sender
	 * @param coordinates coordinates
	 */
	public void deleteAutoStart(CommandSender commandSender, String coordinates) {
		getConfig().deleteAutoStart(coordinates);
		TranslationUtils.sendValueTranslation("Parkour.Delete", "AutoStart", commandSender);
		PluginUtils.logToFile("AutoStart at " + coordinates + " was deleted by " + commandSender.getName());
	}
}

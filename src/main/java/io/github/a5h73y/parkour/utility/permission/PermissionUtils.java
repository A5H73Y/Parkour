package io.github.a5h73y.parkour.utility.permission;

import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_NO_EXIST;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Player Permission related utility methods.
 */
public class PermissionUtils {

	public static final String WILDCARD = "*";

	/**
	 * Check if the player has the specified permission.
	 * The player will be sent a message if they don't have the permission.
	 *
	 * @param commandSender command sender
	 * @param permission required {@link Permission}
	 * @return player has permission
	 */
	public static boolean hasPermission(CommandSender commandSender, Permission permission) {
		return hasPermission(commandSender, permission, true);
	}

	/**
	 * Check if the player has the specified permission.
	 *
	 * @param commandSender command sender
	 * @param permission the required {@link Permission}
	 * @param displayMessage display failure message
	 * @return player has permission
	 */
	public static boolean hasPermission(CommandSender commandSender, Permission permission, boolean displayMessage) {
		if (commandSender.isOp()
				|| commandSender.hasPermission(Permission.PARKOUR_ALL.getPermission())
				|| commandSender.hasPermission(permission.getPermissionRoot() + "." + WILDCARD)
				|| commandSender.hasPermission(permission.getPermission())) {
			return true;
		}

		if (displayMessage) {
			TranslationUtils.sendValueTranslation("Error.NoPermission",
					permission.getPermission(), commandSender);
		}
		return false;
	}

	/**
	 * Check if the player has a specific permission.
	 *
	 * @param commandSender command sender
	 * @param permission specified permission
	 * @param displayMessage display failure message
	 * @return player has permission
	 */
	public static boolean hasSpecificPermission(CommandSender commandSender, Permission permission, String permissionNode,
	                                            boolean displayMessage) {
		if (commandSender.isOp()
				|| commandSender.hasPermission(Permission.PARKOUR_ALL.getPermission())
				|| commandSender.hasPermission(permission.getPermissionRoot() + "." + WILDCARD)
				|| commandSender.hasPermission(permission.getPermissionRoot() + "." + permissionNode)) {
			return true;
		}

		if (displayMessage) {
			TranslationUtils.sendValueTranslation("Error.NoPermission",
					permission.getPermissionRoot() + "." + permissionNode, commandSender);
		}
		return false;
	}

	/**
	 * Check if the player has the specified permission or ownership of course.
	 * The player has elevated permissions for courses they created.
	 *
	 * @param player target player
	 * @param permission the required {@link Permission}
	 * @param courseName the course name
	 * @return player has permission or ownership
	 */
	public static boolean hasPermissionOrCourseOwnership(Player player, Permission permission, String courseName) {
		if (!Parkour.getInstance().getCourseManager().doesCourseExist(courseName)) {
			TranslationUtils.sendValueTranslation(ERROR_NO_EXIST, courseName, player);
			return false;
		}

		String creator = Parkour.getInstance().getConfigManager().getCourseConfig(courseName).getCreator();
		return player.getName().equals(creator) || hasPermission(player, permission, false);
	}

	/**
	 * Check if the player has the specified permission.
	 * SignChangeEvent is used to destroy sign if the permissions aren't granted.
	 *
	 * @param player target player
	 * @return player has permission
	 */
	public static boolean hasSignPermission(Player player, SignChangeEvent sign) {
		if (!hasPermission(player, Permission.ADMIN_CREATESIGN)) {
			sign.setCancelled(true);
			sign.getBlock().breakNaturally();
			return false;
		}
		return true;
	}

	private PermissionUtils() {}
}

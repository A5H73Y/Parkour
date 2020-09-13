package io.github.a5h73y.parkour.utility;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.enums.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Player Permission related utility methods.
 */
public class PermissionUtils {

	private static final String WILDCARD = "*";

	/**
	 * Check if the player has the specified permission.
	 * The player will be sent a message if they don't have the permission.
	 *
	 * @param sender target command sender
	 * @param permission required {@link Permission}
	 * @return player has permission
	 */
	public static boolean hasPermission(CommandSender sender, Permission permission) {
		return hasPermission(sender, permission, true);
	}

	/**
	 * Check if the player has the specified permission.
	 *
	 * @param sender target command sender
	 * @param permission the required {@link Permission}
	 * @param displayMessage display failure message
	 * @return player has permission
	 */
	public static boolean hasPermission(CommandSender sender, Permission permission, boolean displayMessage) {
		if (sender.isOp()
				|| sender.hasPermission(Permission.PARKOUR_ALL.getPermission())
				|| sender.hasPermission(permission.getPermissionRoot() + "." + WILDCARD)
				|| sender.hasPermission(permission.getPermission())) {
			return true;
		}

		if (displayMessage) {
			TranslationUtils.sendValueTranslation("Error.NoPermission",
					permission.getPermission(), sender);
		}
		return false;
	}

	/**
	 * Check if the player has a specific permission.
	 *
	 * @param sender target command sender
	 * @param permission specified permission
	 * @param displayMessage display failure message
	 * @return player has permission
	 */
	public static boolean hasSpecificPermission(CommandSender sender, Permission permission, String permissionNode, boolean displayMessage) {
		if (sender.isOp()
				|| sender.hasPermission(Permission.PARKOUR_ALL.getPermission())
				|| sender.hasPermission(permission.getPermissionRoot() + "." + permissionNode)) {
			return true;
		}

		if (displayMessage) {
			TranslationUtils.sendValueTranslation("Error.NoPermission",
					permission.getPermissionRoot() + "." + permissionNode, sender);
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
		if (!(Parkour.getInstance().getCourseManager().courseExists(courseName))) {
			TranslationUtils.sendValueTranslation("Error.NoExist", courseName, player);
			return false;
		}

		String creator = CourseInfo.getCreator(courseName);
		return player.getName().equals(creator) || hasPermission(player, permission);
	}

	/**
	 * Check if the player has the specified permission.
	 * SignChangeEvent is used to destroy sign if the permissions aren't granted.
	 *
	 * @param player target player
	 * @param permissionNode sign permission node
	 * @return player has permission
	 */
	public static boolean hasSignPermission(Player player, String permissionNode, SignChangeEvent sign) {
		Permission matchingPermission = Permission.valueOf("CREATE_SIGN_" + permissionNode.toUpperCase());

		if (!hasPermission(player, Permission.CREATE_SIGN_ALL, false) && !hasPermission(player, matchingPermission, false)) {
			sign.setCancelled(true);
			sign.getBlock().breakNaturally();
			return false;
		}
		return true;
	}
}

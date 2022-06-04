package io.github.a5h73y.parkour.utility.permission;

/**
 * All Parkour related permissions.
 */
public enum Permission {

	// All Permissions
	PARKOUR_ALL("parkour", "*"),

	// Parkour Basic
	BASIC_ALL(Constants.PARKOUR_BASIC, "*"),
	BASIC_CHALLENGE(Constants.PARKOUR_BASIC, "challenge"),
	BASIC_CREATE(Constants.PARKOUR_BASIC, "create"),
	BASIC_KIT(Constants.PARKOUR_BASIC, "kit"),
	BASIC_TELEPORT(Constants.PARKOUR_BASIC, "tp"),
	BASIC_TELEPORT_CHECKPOINT(Constants.PARKOUR_BASIC, "tpc"),
	BASIC_LEADERBOARD(Constants.PARKOUR_BASIC, "leaderboard"),
	BASIC_SIGNS(Constants.PARKOUR_BASIC, "signs"),
	BASIC_JOINALL(Constants.PARKOUR_BASIC, "joinall"),
	BASIC_COMMANDS(Constants.PARKOUR_BASIC, "commands"),

	// Parkour Admins
	ADMIN_ALL(Constants.PARKOUR_ADMIN, "*"),
	ADMIN_READY_BYPASS(Constants.PARKOUR_ADMIN, "readybypass"),
	ADMIN_LEVEL_BYPASS(Constants.PARKOUR_ADMIN, "levelbypass"),
	ADMIN_COURSE(Constants.PARKOUR_ADMIN, "course"),
	ADMIN_PRIZE(Constants.PARKOUR_ADMIN, "prize"),
	ADMIN_DELETE(Constants.PARKOUR_ADMIN, "delete"),
	ADMIN_RESET(Constants.PARKOUR_ADMIN, "reset"),
	ADMIN_TESTMODE(Constants.PARKOUR_ADMIN, "testmode"),
	ADMIN_CREATESIGN(Constants.PARKOUR_ADMIN, "createsign"),

	// Other
	PARKOUR_LEVEL("parkour.level", "99999"),
	PARKOUR_COURSE("parkour.course", "*");

	private final String permissionNode;
	private final String permissionRoot;

	Permission(String permissionRoot, String permissionNode) {
		this.permissionRoot = permissionRoot;
		this.permissionNode = permissionNode;
	}

	public String getPermissionRoot() {
		return permissionRoot;
	}

	public String getPermissionNode() {
		return permissionNode;
	}

	public String getPermission() {
		return permissionRoot + "." + permissionNode;
	}

	private static class Constants {
		private static final String PARKOUR_BASIC = "parkour.basic";
		private static final String PARKOUR_ADMIN = "parkour.admin";
	}
}


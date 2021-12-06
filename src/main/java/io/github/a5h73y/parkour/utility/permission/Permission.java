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

	// Create Parkour Signs
	CREATE_SIGN_ALL(Constants.PARKOUR_CREATE_SIGN, "*"),
	CREATE_SIGN_JOIN(Constants.PARKOUR_CREATE_SIGN, "join"),
	CREATE_SIGN_JOINALL(Constants.PARKOUR_CREATE_SIGN, "joinall"),
	CREATE_SIGN_FINISH(Constants.PARKOUR_CREATE_SIGN, "finish"),
	CREATE_SIGN_LEAVE(Constants.PARKOUR_CREATE_SIGN, "leave"),
	CREATE_SIGN_EFFECT(Constants.PARKOUR_CREATE_SIGN, "effect"),
	CREATE_SIGN_STATS(Constants.PARKOUR_CREATE_SIGN, "stats"),
	CREATE_SIGN_LEADERBOARDS(Constants.PARKOUR_CREATE_SIGN, "leaderboards"),
	CREATE_SIGN_LOBBY(Constants.PARKOUR_CREATE_SIGN, "lobby"),
	CREATE_SIGN_CHALLENGE(Constants.PARKOUR_CREATE_SIGN, "challenge"),
	CREATE_SIGN_CHECKPOINT(Constants.PARKOUR_CREATE_SIGN, "checkpoint"),

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
		private static final String PARKOUR_CREATE_SIGN = "parkour.createsign";
	}
}


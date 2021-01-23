package io.github.a5h73y.parkour.enums;

/**
 * All Parkour related permissions.
 */
public enum Permission {

	// All Permissions
	PARKOUR_ALL("Parkour", "*"),

	// Parkour Basic
	BASIC_ALL(Constants.PARKOUR_BASIC, "*"),
	BASIC_CHALLENGE(Constants.PARKOUR_BASIC, "Challenge"),
	BASIC_CREATE(Constants.PARKOUR_BASIC, "Create"),
	BASIC_KIT(Constants.PARKOUR_BASIC, "Kit"),
	BASIC_TELEPORT(Constants.PARKOUR_BASIC, "TP"),
	BASIC_TELEPORT_CHECKPOINT(Constants.PARKOUR_BASIC, "TPC"),
	BASIC_LEADERBOARD(Constants.PARKOUR_BASIC, "Leaderboard"),
	BASIC_SIGNS(Constants.PARKOUR_BASIC, "Signs"),
	BASIC_JOINALL(Constants.PARKOUR_BASIC, "JoinAll"),
	BASIC_COMMANDS(Constants.PARKOUR_BASIC, "Commands"),

	// Parkour Admins
	ADMIN_ALL(Constants.PARKOUR_ADMIN, "*"),
	ADMIN_READY_BYPASS(Constants.PARKOUR_ADMIN, "ReadyBypass"),
	ADMIN_LEVEL_BYPASS(Constants.PARKOUR_ADMIN, "LevelBypass"),
	ADMIN_COURSE(Constants.PARKOUR_ADMIN, "Course"),
	ADMIN_PRIZE(Constants.PARKOUR_ADMIN, "Prize"),
	ADMIN_DELETE(Constants.PARKOUR_ADMIN, "Delete"),
	ADMIN_RESET(Constants.PARKOUR_ADMIN, "Reset"),
	ADMIN_TESTMODE(Constants.PARKOUR_ADMIN, "Testmode"),

	// Create Parkour Signs
	CREATE_SIGN_ALL(Constants.PARKOUR_CREATE_SIGN, "*"),
	CREATE_SIGN_JOIN(Constants.PARKOUR_CREATE_SIGN, "Join"),
	CREATE_SIGN_JOINALL(Constants.PARKOUR_CREATE_SIGN, "JoinAll"),
	CREATE_SIGN_FINISH(Constants.PARKOUR_CREATE_SIGN, "Finish"),
	CREATE_SIGN_LEAVE(Constants.PARKOUR_CREATE_SIGN, "Leave"),
	CREATE_SIGN_EFFECT(Constants.PARKOUR_CREATE_SIGN, "Effect"),
	CREATE_SIGN_STATS(Constants.PARKOUR_CREATE_SIGN, "Stats"),
	CREATE_SIGN_LEADERBOARDS(Constants.PARKOUR_CREATE_SIGN, "Leaderboards"),
	CREATE_SIGN_LOBBY(Constants.PARKOUR_CREATE_SIGN, "Lobby"),
	CREATE_SIGN_CHALLENGE(Constants.PARKOUR_CREATE_SIGN, "Challenge"),
	CREATE_SIGN_CHECKPOINT(Constants.PARKOUR_CREATE_SIGN, "Checkpoint"),

	// Other
	PARKOUR_LEVEL("Parkour.Level", "99999"),
	PARKOUR_COURSE("Parkour.Course", "*");

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
		private static final String PARKOUR_BASIC = "Parkour.Basic";
		private static final String PARKOUR_ADMIN = "Parkour.Admin";
		private static final String PARKOUR_CREATE_SIGN = "Parkour.CreateSign";
	}
}


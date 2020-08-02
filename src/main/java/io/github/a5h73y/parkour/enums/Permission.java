package io.github.a5h73y.parkour.enums;

/**
 * All Parkour related permissions.
 */
public enum Permission {

	// All Permissions
	PARKOUR_ALL("Parkour", "*"),

	// Parkour Basic
	BASIC_ALL("Parkour.Basic", "*"),
	BASIC_CHALLENGE("Parkour.Basic", "Challenge"),
	BASIC_CREATE("Parkour.Basic", "Create"),
	BASIC_KIT("Parkour.Basic", "Kit"),
	BASIC_TELEPORT("Parkour.Basic", "TP"),
	BASIC_TELEPORT_CHECKPOINT("Parkour.Basic", "TPC"),
	BASIC_LEADERBOARD("Parkour.Basic", "Leaderboard"),
	BASIC_SIGNS("Parkour.Basic", "Signs"),
	BASIC_JOINALL("Parkour.Basic", "JoinAll"),
	BASIC_COMMANDS("Parkour.Basic", "Commands"),

	// Parkour Admins
	ADMIN_ALL("Parkour.Admin", "*"),
	ADMIN_FINISH_BYPASS("Parkour.Admin", "FinishBypass"),
	ADMIN_LEVEL_BYPASS("Parkour.Admin", "LevelBypass"),
	ADMIN_COURSE("Parkour.Admin", "Course"),
	ADMIN_PRIZE("Parkour.Admin", "Prize"),
	ADMIN_DELETE("Parkour.Admin", "Delete"),
	ADMIN_RESET("Parkour.Admin", "Reset"),
	ADMIN_TESTMODE("Parkour.Admin", "Testmode"),

	// Create Parkour Signs
	SIGN_ALL("Parkour.CreateSign", "*"),
	SIGN_JOIN("Parkour.CreateSign", "Join"),
	SIGN_FINISH("Parkour.CreateSign", "Finish"),
	SIGN_LEAVE("Parkour.CreateSign", "Leave"),
	SIGN_EFFECT("Parkour.CreateSign", "Effect"),
	SIGN_STATS("Parkour.CreateSign", "Stats"),
	SIGN_LEADERBOARDS("Parkour.CreateSign", "Leaderboards"),
	SIGN_LOBBY("Parkour.CreateSign", "Lobby"),

	PARKOUR_LEVEL("Parkour.Level", "99999");

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
}


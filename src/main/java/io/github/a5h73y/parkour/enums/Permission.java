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
	ADMIN_READY_BYPASS("Parkour.Admin", "ReadyBypass"),
	ADMIN_LEVEL_BYPASS("Parkour.Admin", "LevelBypass"),
	ADMIN_COURSE("Parkour.Admin", "Course"),
	ADMIN_PRIZE("Parkour.Admin", "Prize"),
	ADMIN_DELETE("Parkour.Admin", "Delete"),
	ADMIN_RESET("Parkour.Admin", "Reset"),
	ADMIN_TESTMODE("Parkour.Admin", "Testmode"),

	// Create Parkour Signs
	CREATE_SIGN_ALL("Parkour.CreateSign", "*"),
	CREATE_SIGN_JOIN("Parkour.CreateSign", "Join"),
	CREATE_SIGN_FINISH("Parkour.CreateSign", "Finish"),
	CREATE_SIGN_LEAVE("Parkour.CreateSign", "Leave"),
	CREATE_SIGN_EFFECT("Parkour.CreateSign", "Effect"),
	CREATE_SIGN_STATS("Parkour.CreateSign", "Stats"),
	CREATE_SIGN_LEADERBOARDS("Parkour.CreateSign", "Leaderboards"),
	CREATE_SIGN_LOBBY("Parkour.CreateSign", "Lobby"),

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
}


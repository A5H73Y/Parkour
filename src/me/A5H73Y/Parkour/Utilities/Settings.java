package me.A5H73Y.Parkour.Utilities;

import org.bukkit.configuration.file.FileConfiguration;

import me.A5H73Y.Parkour.Parkour;

public class Settings {

	//Main
	private static boolean log, debug, checkForUpdates, economy, forceFullComplete, parkourBlocks, signProtection, invManagement, scoreboard, playerDamage, sounds, forceWorld, disableCommands;
	private static int maxFall, leaderboardType, gamemodeFinish;
	
	//OnEvents
	private static boolean forceFinished, giveSuicide, giveHideAll, giveLeave, giveStatBook, resetPlayer, resetTimeOnDie, setXPBar, tpToLobby, broadcastFinish;
	
	//Display
	private static boolean welcomeMessage, creatorJoin, finishedError, XPReward, LevelReward, TitleOnJoin, stayInvisibleOnCancel, teleportToLobby;
	
	//Database
	private static boolean useMySQL;
	
	public static void initialize(){
		FileConfiguration config = Parkour.getParkourConfig().getConfig();
		log = config.getBoolean("Log");
		debug = config.getBoolean("Debug");
		checkForUpdates = config.getBoolean("CheckForUpdates");
		maxFall = config.getInt("MaxFall");
		leaderboardType = config.getInt("LeaderboardType");
		economy = config.getBoolean("Economy");
		forceFullComplete = config.getBoolean("ForceFullCompletion");
		parkourBlocks = config.getBoolean("ParkourBlocks");
		
		//TODO finish me
	}

	public static boolean isLog() {
		return log;
	}

	public static boolean isDebug() {
		return debug;
	}

	public static boolean isCheckForUpdates() {
		return checkForUpdates;
	}

	public static boolean isEconomy() {
		return economy;
	}

	public static boolean isParkourBlocks() {
		return parkourBlocks;
	}

	public static boolean isSignProtection() {
		return signProtection;
	}

	public static boolean isInvManagement() {
		return invManagement;
	}

	public static boolean isScoreboard() {
		return scoreboard;
	}

	public static boolean isPlayerDamage() {
		return playerDamage;
	}

	public static boolean isSounds() {
		return sounds;
	}

	public static boolean isForceWorld() {
		return forceWorld;
	}

	public static boolean isDisableCommands() {
		return disableCommands;
	}

	public static int getMaxFall() {
		return maxFall;
	}

	public static int getLeaderboardType() {
		return leaderboardType;
	}

	public static int getGamemodeFinish() {
		return gamemodeFinish;
	}

	public static boolean isForceFinished() {
		return forceFinished;
	}

	public static boolean isGiveSuicide() {
		return giveSuicide;
	}

	public static boolean isGiveHideAll() {
		return giveHideAll;
	}

	public static boolean isGiveLeave() {
		return giveLeave;
	}

	public static boolean isGiveStatBook() {
		return giveStatBook;
	}

	public static boolean isResetPlayer() {
		return resetPlayer;
	}

	public static boolean isResetTimeOnDie() {
		return resetTimeOnDie;
	}

	public static boolean isSetXPBar() {
		return setXPBar;
	}

	public static boolean isTpToLobby() {
		return tpToLobby;
	}

	public static boolean isBroadcastFinish() {
		return broadcastFinish;
	}

	public static boolean isWelcomeMessage() {
		return welcomeMessage;
	}

	public static boolean isCreatorJoin() {
		return creatorJoin;
	}

	public static boolean isFinishedError() {
		return finishedError;
	}

	public static boolean isXPReward() {
		return XPReward;
	}

	public static boolean isLevelReward() {
		return LevelReward;
	}

	public static boolean isTitleOnJoin() {
		return TitleOnJoin;
	}

	public static boolean isStayInvisibleOnCancel() {
		return stayInvisibleOnCancel;
	}

	public static boolean isTeleportToLobby() {
		return teleportToLobby;
	}

	public static boolean isForceFullComplete() {
		return forceFullComplete;
	}

	public static boolean isUseMySQL() {
		return useMySQL;
	}
	
}

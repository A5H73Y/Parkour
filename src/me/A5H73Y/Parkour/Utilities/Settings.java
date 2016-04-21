package me.A5H73Y.Parkour.Utilities;

import org.bukkit.configuration.file.FileConfiguration;

import me.A5H73Y.Parkour.Parkour;

public class Settings {

	//Main
	private boolean log, debug, checkForUpdates, economy, forceFullComplete, parkourBlocks, signProtection, invManagement, scoreboard, playerDamage, sounds, forceWorld, disableCommands;
	private int maxFall, leaderboardType, gamemodeFinish;
	
	//OnEvents
	private boolean forceFinished, giveSuicide, giveHideAll, giveLeave, giveStatBook, resetPlayer, resetTimeOnDie, setXPBar, tpToLobby, broadcastFinish;
	
	//Display
	private boolean welcomeMessage, creatorJoin, finishedError, XPReward, LevelReward, TitleOnJoin, stayInvisibleOnCancel, teleportToLobby;
	
	//Database
	private boolean useMySQL;
	
	public Settings(){
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

	public boolean isLog() {
		return log;
	}

	public boolean isDebug() {
		return debug;
	}

	public boolean isCheckForUpdates() {
		return checkForUpdates;
	}

	public boolean isEconomy() {
		return economy;
	}

	public boolean isParkourBlocks() {
		return parkourBlocks;
	}

	public boolean isSignProtection() {
		return signProtection;
	}

	public boolean isInvManagement() {
		return invManagement;
	}

	public boolean isScoreboard() {
		return scoreboard;
	}

	public boolean isPlayerDamage() {
		return playerDamage;
	}

	public boolean isSounds() {
		return sounds;
	}

	public boolean isForceWorld() {
		return forceWorld;
	}

	public boolean isDisableCommands() {
		return disableCommands;
	}

	public int getMaxFall() {
		return maxFall;
	}

	public int getLeaderboardType() {
		return leaderboardType;
	}

	public int getGamemodeFinish() {
		return gamemodeFinish;
	}

	public boolean isForceFinished() {
		return forceFinished;
	}

	public boolean isGiveSuicide() {
		return giveSuicide;
	}

	public boolean isGiveHideAll() {
		return giveHideAll;
	}

	public boolean isGiveLeave() {
		return giveLeave;
	}

	public boolean isGiveStatBook() {
		return giveStatBook;
	}

	public boolean isResetPlayer() {
		return resetPlayer;
	}

	public boolean isResetTimeOnDie() {
		return resetTimeOnDie;
	}

	public boolean isSetXPBar() {
		return setXPBar;
	}

	public boolean isTpToLobby() {
		return tpToLobby;
	}

	public boolean isBroadcastFinish() {
		return broadcastFinish;
	}

	public boolean isWelcomeMessage() {
		return welcomeMessage;
	}

	public boolean isCreatorJoin() {
		return creatorJoin;
	}

	public boolean isFinishedError() {
		return finishedError;
	}

	public boolean isXPReward() {
		return XPReward;
	}

	public boolean isLevelReward() {
		return LevelReward;
	}

	public boolean isTitleOnJoin() {
		return TitleOnJoin;
	}

	public boolean isStayInvisibleOnCancel() {
		return stayInvisibleOnCancel;
	}

	public boolean isTeleportToLobby() {
		return teleportToLobby;
	}

	public boolean isForceFullComplete() {
		return forceFullComplete;
	}

	public boolean isUseMySQL() {
		return useMySQL;
	}
	
}

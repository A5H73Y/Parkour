package me.A5H73Y.Parkour.Utilities;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import me.A5H73Y.Parkour.Parkour;

public class Settings {

	//Main
	private boolean useParkourBlocks, useMySQL, economy, logtofile, debug, signProtect, invManage, commandPerm;
	
	//OnEvents
	private boolean playerDamage, forceParkourSigns, forceParkourCommands, forceFullCompletion, requireFinished, forceWorld, disableFly;
	
	//Display
	private boolean welcomeMessage, creatorJoin, notFinished, levelReward, titleOnJoin;
	
	//Materials
	private Material suicide, hideAll, leave;
	
	//Lists
	private List<String> cmdWhitelist; 
	
	public Settings(){
		FileConfiguration config = Parkour.getParkourConfig().getConfig();
		
	}
}

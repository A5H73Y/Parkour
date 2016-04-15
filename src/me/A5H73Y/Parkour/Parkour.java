package me.A5H73Y.Parkour;

import java.util.logging.Logger;

import me.A5H73Y.Parkour.Other.Configurations;
import me.A5H73Y.Parkour.Other.StartPlugin;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.java.JavaPlugin;

import com.huskehhh.mysql.Database;

public class Parkour extends JavaPlugin {

	private static JavaPlugin plugin;
	private static Configurations config;
	private static Database database;
	private static Economy economy;

	public void onEnable() {
		System.out.println("Thank you for using a Parkour development build.");
		System.out.println("Please add me on skype iA5H73Y to discuss anything.");
		
		//Option to populate entire courseList on join. Check if list is populated, then set static savedLocally = false. Method to return will check if set and retrieve appropriately
		plugin = this;
		config = new Configurations();
		StartPlugin.run();

		getServer().getPluginManager().registerEvents(new ParkourListener(), this);
		getCommand("parkour").setExecutor(new ParkourCommands());
	}

	public void onDisable() {
		Utils.log("[Parkour] Disabled Parkour v" + Static.getVersion() + "!");
		Utils.saveAllPlaying(PlayerMethods.getPlaying(), Static.PATH);
		config.saveAll();
		getParkourConfig().reload();
	}

	public static void setDatabaseObj(Database databaseObj){
		database = databaseObj;
	}

	public static void setEconomy(Economy newEconomy){
		economy = newEconomy;
	}

	//Getters
	public static Database getDatabaseObj(){
		return database;
	}

	public static JavaPlugin getPlugin() {
		return plugin;
	}

	public static Configurations getParkourConfig(){
		return config;
	}

	public static Economy getEconomy(){
		return economy;
	}
}
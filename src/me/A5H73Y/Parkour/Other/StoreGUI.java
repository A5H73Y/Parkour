package me.A5H73Y.Parkour.Other;

import java.util.HashMap;

import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum StoreGUI {

	MAIN_MENU,
	TRAILS,
	UPGRADES,
	SINGLE_USE,
	PURCHASED;

	public static HashMap<String, StoreGUI> invOpen = new HashMap<String, StoreGUI>();
	
	/* Headings */
	public static final String PARKOUR_TITLE = ChatColor.AQUA + "Parkour Store"; 
	public static final String MAIN_MENU_HEADING = PARKOUR_TITLE + ChatColor.WHITE + " Main Menu";
	public static final String TRAILS_HEADING = PARKOUR_TITLE + ChatColor.WHITE + " Trails";
	public static final String UPGRADES_HEADING = PARKOUR_TITLE + ChatColor.WHITE + " Upgrades";
	public static final String SINGLE_USE_HEADING = PARKOUR_TITLE + ChatColor.WHITE + " Single Use";
	public static final String PURCHASED_HEADING = PARKOUR_TITLE + ChatColor.WHITE + " Purchased";

	/* Menus */
	public static Inventory getMainMenu(){
		Inventory mainMenu = Bukkit.createInventory(null, 27, MAIN_MENU_HEADING);
		mainMenu.setItem(10, createItem(Material.GLOWSTONE_DUST, "&bTrails"));
		mainMenu.setItem(12, createItem(Material.WORKBENCH, "&1Upgrades"));
		mainMenu.setItem(14, createItem(Material.RABBIT_FOOT, "&cSingle Use"));
		mainMenu.setItem(16, createItem(Material.CHEST, "&aPurchased"));
		mainMenu.setItem(26, createItem(Material.BARRIER, "&4Close"));
		return mainMenu;
	}

	public static Inventory getTrailsMenu(){
		Inventory mainMenu = Bukkit.createInventory(null, 9, TRAILS_HEADING);
		mainMenu.setItem(1, createItem(Material.REDSTONE, "&bRedstone - £10"));
		mainMenu.setItem(8, createItem(Material.BARRIER, "&4Back"));
		return mainMenu;
	}

	public static Inventory getUpgradesMenu(){
		Inventory mainMenu = Bukkit.createInventory(null, 9, UPGRADES_HEADING);
		mainMenu.setItem(1, createItem(Material.ELYTRA, "&bFlight"));
		mainMenu.setItem(8, createItem(Material.BARRIER, "&4Back"));
		return mainMenu;
	}
	
	public static Inventory getSingleUseMenu(){
		Inventory mainMenu = Bukkit.createInventory(null, 9, SINGLE_USE_HEADING);
		mainMenu.setItem(0, createItem(Material.SUGAR, "&bFaster Run"));
		mainMenu.setItem(8, createItem(Material.BARRIER, "&4Back"));
		return mainMenu;
	}
	
	public static Inventory getPurchasedMenu(){
		Inventory mainMenu = Bukkit.createInventory(null, 9, PURCHASED_HEADING);
		mainMenu.setItem(0, createItem(Material.SUGAR, "&bFaster Run"));
		mainMenu.setItem(8, createItem(Material.BARRIER, "&4Back"));
		return mainMenu;
	}

	/* Decide which menu to open */
	public static void openInventory(Player player){
		if (!invOpen.containsKey(player.getName()))
			invOpen.put(player.getName(), StoreGUI.MAIN_MENU);

		switch(invOpen.get(player.getName())){
		case MAIN_MENU:
			player.openInventory(getMainMenu());
			break;

		case TRAILS:
			player.openInventory(getTrailsMenu());
			break;

		case UPGRADES:
			player.openInventory(getUpgradesMenu());
			break;
			
		case SINGLE_USE:
			player.openInventory(getSingleUseMenu());
			break;
			
		case PURCHASED:
			player.openInventory(getPurchasedMenu());
			break;
			
		default:
			break;
		}
	}

	/* Handle Clicks while in menus */
	public static void processClick(Player player, Material material) {
		player.closeInventory();

		switch(invOpen.get(player.getName())){
		case MAIN_MENU:
			processMainMenu(player, material);
			break;

		case TRAILS:
			processTrails(player, material);
			break;

		case UPGRADES:
			processUpgrades(player, material);
			break;
			
		case SINGLE_USE:
			processSingleUse(player, material);
			break;
			
		case PURCHASED:
			processPurchased(player, material);
			break;

		default:
			break;
		}
	}
	
	/* Processing of each item per Menu */
	public static void processMainMenu(Player player, Material material){
		if (material.equals(Material.GLOWSTONE_DUST)){
			player.closeInventory();
			invOpen.put(player.getName(), TRAILS);
			player.openInventory(getTrailsMenu());

		} else if (material.equals(Material.WORKBENCH)){
			player.closeInventory();
			invOpen.put(player.getName(), UPGRADES);
			player.openInventory(getUpgradesMenu());

		} else if (material.equals(Material.RABBIT_FOOT)){
			player.closeInventory();
			invOpen.put(player.getName(), SINGLE_USE);
			player.openInventory(getSingleUseMenu());
			
		} else if (material.equals(Material.CHEST)){
			player.closeInventory();
			invOpen.put(player.getName(), PURCHASED);
			player.openInventory(getPurchasedMenu());

		} else if (material.equals(Material.BARRIER)){
			player.closeInventory();
			invOpen.remove(player.getName());
		}
	}

	public static void processTrails(Player player, Material material){
		if (material.equals(Material.BARRIER)){
			player.closeInventory();
			invOpen.put(player.getName(), MAIN_MENU);
			player.openInventory(getMainMenu());
		}
	}

	public static void processUpgrades(Player player, Material material){
		if (material.equals(Material.BARRIER)){
			player.closeInventory();
			invOpen.put(player.getName(), MAIN_MENU);
			player.openInventory(getMainMenu());
		}
	}
	
	public static void processSingleUse(Player player, Material material) { 
		if (material.equals(Material.BARRIER)){
			player.closeInventory();
			invOpen.put(player.getName(), MAIN_MENU);
			player.openInventory(getMainMenu());
		}
	}
	
	public static void processPurchased(Player player, Material material) { 
		if (material.equals(Material.BARRIER)){
			player.closeInventory();
			invOpen.put(player.getName(), MAIN_MENU);
			player.openInventory(getMainMenu());
		}
	}

	/* Utils */
	public static ItemStack createItem(Material material, String name) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.colour(name));
		item.setItemMeta(meta);
		return item;
	}
}

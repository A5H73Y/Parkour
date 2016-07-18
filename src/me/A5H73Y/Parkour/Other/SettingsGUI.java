package me.A5H73Y.Parkour.Other;

import java.util.HashMap;

import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The purpose of this will be to change all toggles via GUI.
 * Admin settings will appear for players with certain permission.
 * 
 * @author A5H73Y
 *
 */
public enum SettingsGUI {

	MAIN_MENU;

	public static HashMap<String, SettingsGUI> invOpen = new HashMap<String, SettingsGUI>();
	
	/* Headings */
	public static final String PARKOUR_TITLE = ChatColor.AQUA + "Parkour Settings"; 
	public static final String MAIN_MENU_HEADING = PARKOUR_TITLE + ChatColor.WHITE + " Main Menu";

	/* Menus */
	public static Inventory getMainMenu(Player player){
		Inventory mainMenu = Bukkit.createInventory(null, 27, MAIN_MENU_HEADING);
		
		boolean hidden = Static.containsHidden(player.getName());
		mainMenu.setItem(1, createToggleItem(hidden, "Hidden"));
		
		mainMenu.setItem(26, createItem(Material.BARRIER, "&4Close"));
		return mainMenu;
	}


	/* Decide which menu to open */
	public static void openInventory(Player player){
		if (!invOpen.containsKey(player.getName()))
			invOpen.put(player.getName(), SettingsGUI.MAIN_MENU);

		switch(invOpen.get(player.getName())){
		case MAIN_MENU:
			player.openInventory(getMainMenu(player));
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

		default:
			break;
		}
	}
	
	/* Processing of each item per Menu */
	public static void processMainMenu(Player player, Material material){
		
		player.sendMessage(material.getData().getName());
		
		if (material.equals(Material.BARRIER)){
			player.closeInventory();
			invOpen.remove(player.getName());
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
	
	public static ItemStack createToggleItem(boolean enabled, String title){
		byte data = (byte) (enabled ? 5 : 14);
		String colour = (enabled ? "&a" : "&c");
		
		Material material = Material.WOOL;
		ItemStack item = new ItemStack(material, 1, data);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.colour(colour.concat(title)));
		item.setItemMeta(meta);
		return item;
	}
}

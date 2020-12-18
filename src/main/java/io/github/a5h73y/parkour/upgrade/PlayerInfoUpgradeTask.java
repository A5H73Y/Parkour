package io.github.a5h73y.parkour.upgrade;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class PlayerInfoUpgradeTask extends TimedConfigUpgradeTask {

	public PlayerInfoUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader, parkourUpgrader.getPlayerConfig());
	}

	@Override
	protected String getTitle() {
		return "PlayerInfo";
	}

	@Override
	protected boolean doWork() {
		ConfigurationSection playerInfoSection = getConfig().getConfigurationSection("PlayerInfo");

		if (playerInfoSection == null) {
			getParkourUpgrader().getLogger().info("Players are already upgraded...");
			return true;
		}

		boolean success = true;
		Set<String> playerNames = playerInfoSection.getKeys(false);

		getParkourUpgrader().getLogger().info("Converting " + playerNames.size() + " players.");
		getParkourUpgrader().getLogger().info("This might take a few minutes...");
		int interval = Math.max(playerNames.size() / 10, 1);
		int count = 0;

		for (String playerName : playerNames) {
			if (count % interval == 0) {
				double percent = Math.ceil((count * 100.0d) / playerNames.size());
				getParkourUpgrader().getLogger().info(percent + "% complete...");
			}

			OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName);
			Set<String> settings = getConfig().getConfigurationSection("PlayerInfo." + playerName).getKeys(false);

			for (String setting : settings) {
				getConfig().set(targetPlayer.getUniqueId() + "." + setting,
						getConfig().get("PlayerInfo." + playerName + "." + setting));
			}

			if (getParkourUpgrader().getInventoryConfig().contains(playerName)) {
				convertPlayerInventory(playerName, targetPlayer);
			}

			count++;
		}

		getConfig().set("PlayerInfo", null);
		try {
			getParkourUpgrader().savePlayerConfig();
			getParkourUpgrader().saveInventoryConfig();
		} catch (IOException e) {
			getParkourUpgrader().getLogger().severe("An error occurred during upgrade: " + e.getMessage());
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	private void convertPlayerInventory(String playerName, OfflinePlayer player) {
		Object inventoryData = getParkourUpgrader().getInventoryConfig().get(playerName + ".Inventory");
		Object armorData = getParkourUpgrader().getInventoryConfig().get(playerName + ".Armor");

		if (inventoryData == null) {
			getParkourUpgrader().getLogger().info(playerName + "'s Inventory couldn't be found.");
			return;
		}

		// this is how it was done in < 6.0
		ItemStack[] inventory = null;
		ItemStack[] armor = null;
		if (inventoryData instanceof ItemStack[]) {
			inventory = (ItemStack[]) inventoryData;
		} else if (inventoryData instanceof List) {
			List<?> lista = (List<?>) inventoryData;
			inventory = lista.toArray(new ItemStack[0]);
		}
		if (armorData instanceof ItemStack[]) {
			armor = (ItemStack[]) armorData;
		} else if (armorData instanceof List) {
			List<?> listb = (List<?>) armorData;
			armor = listb.toArray(new ItemStack[0]);
		}

		getParkourUpgrader().getInventoryConfig().set(player.getUniqueId() + ".Inventory", Arrays.asList(inventory));
		getParkourUpgrader().getInventoryConfig().set(player.getUniqueId() + ".Armor", Arrays.asList(armor));
		getParkourUpgrader().getInventoryConfig().set(playerName, null);
	}
}

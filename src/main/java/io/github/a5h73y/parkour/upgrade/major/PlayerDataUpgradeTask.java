package io.github.a5h73y.parkour.upgrade.major;

import io.github.a5h73y.parkour.configuration.serializable.ItemStackSerializable;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedConfigUpgradeTask;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class PlayerDataUpgradeTask extends TimedConfigUpgradeTask {

	ItemStackSerializable itemStackSerializable = new ItemStackSerializable();

	public PlayerDataUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader, parkourUpgrader.getPlayerConfig());
	}

	@Override
	protected String getTitle() {
		return "Player Data";
	}

	@Override
	protected boolean doWork() {
		boolean success = true;
		// every player uuid
		Set<String> playerIds = getConfig().getKeys(false);

		upgradeParkourRanks();

		getParkourUpgrader().getLogger().info("Converting " + playerIds.size() + " players.");
		int interval = Math.max(playerIds.size() / 10, 1);
		int count = 0;

		for (String playerId : playerIds) {
			if (count % interval == 0) {
				double percent = Math.ceil((count * 100.0d) / playerIds.size());
				getParkourUpgrader().getLogger().info(percent + "% complete...");
			}

			// if it isn't a valid uuid - ignore it
			if (playerId.split("-").length != 5) {
				continue;
			}

			ConfigurationSection playerSection = getConfig().getConfigurationSection(playerId);
			OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playerId));

			if (playerSection != null) {
				PlayerConfig newPlayerConfig = PlayerConfig.getConfig(player);

				Set<String> playerKeys = playerSection.getKeys(true); // this will mean we get a deep config entry
				for (String key : playerKeys) {
					newPlayerConfig.set(key, playerSection.get(key));
				}

				updateInventorySection(newPlayerConfig, playerId);
				updateCompletedCoursesSection(playerId);
			}

			count++;
		}

		return success;
	}

	private void updateCompletedCoursesSection(String playerId) {
		// TODO transfer completed courses to new file

	}

	private void upgradeParkourRanks() {
		ConfigurationSection parkourRankSection = getConfig().getConfigurationSection("ServerInfo.Levels");
		if (parkourRankSection != null) {
			getParkourUpgrader().getLogger().info("Upgrading ParkourRanks");

			Set<String> parkourRanks = parkourRankSection.getKeys(false);

			for (String parkourRank : parkourRanks) {
				getParkourUpgrader().getNewConfigManager().getParkourRankConfig().set(parkourRank,
						parkourRankSection.getString(parkourRank + ".Rank"));
			}
		}
	}

	private void updateInventorySection(PlayerConfig playerConfig, String playerId) {
		ConfigurationSection inventorySection =
				getParkourUpgrader().getInventoryConfig().getConfigurationSection(playerId);

		if (inventorySection != null) {
			ItemStack[] inventoryStack = getItemStackContents(inventorySection, "Inventory");
			if (inventoryStack != null) {
				playerConfig.set("Inventory", itemStackSerializable.serialize(inventoryStack));
			}

			ItemStack[] armorStack = getItemStackContents(inventorySection, "Armor");
			if (armorStack != null) {
				playerConfig.set("Armor", itemStackSerializable.serialize(armorStack));
			}

			if (inventorySection.contains("Health")) {
				playerConfig.set("Health", inventorySection.getDouble("Health"));
			}
			if (inventorySection.contains("Hunger")) {
				playerConfig.set("Hunger", inventorySection.getInt("Hunger"));
			}
		}
	}

	private ItemStack[] getItemStackContents(ConfigurationSection configSection, String configEntry) {
		List<ItemStack> contents = (List<ItemStack>) configSection.getList(configEntry);
		return contents != null ? contents.toArray(new ItemStack[0]) : null;
	}
}

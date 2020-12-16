package io.github.a5h73y.parkour.utility;

import com.cryptomorin.xseries.XMaterial;
import io.github.a5h73y.parkour.Parkour;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Stairs;

public class MaterialUtils {

	private static List<Material> validCheckpointMaterials;

	/**
	 * Get the Material in the player's hand.
	 *
	 * @param player target player
	 * @return {@link Material}
	 */
	public static Material getMaterialInPlayersHand(Player player) {
		return getItemStackInPlayersHand(player).getType();
	}

	/**
	 * Get the ItemStack in the player's hand.
	 *
	 * @param player target player
	 * @return {@link ItemStack}
	 */
	public static ItemStack getItemStackInPlayersHand(Player player) {
		ItemStack stack;

		try {
			// support for 1.8 requires fallback for missing method.
			stack = player.getInventory().getItemInMainHand();
		} catch (NoSuchMethodError ex) {
			stack = player.getItemInHand();
		}

		return stack;
	}

	/**
	 * Parse ItemStack amount within bounds.
	 * Value is adjusted to be between 1 and 64.
	 *
	 * @param requestedAmount the requested stack amount
	 * @return parsed amount
	 */
	public static int parseItemStackAmount(String requestedAmount) {
		int amount = Integer.parseInt(requestedAmount);
		return amount < 1 ? 1 : Math.min(amount, 64);
	}

	/**
	 * Create an ItemStack for a single Material.
	 * Item label is set as display name.
	 *
	 *
	 * @param material {@link Material}
	 * @param itemLabel item display name
	 * @return created {@link ItemStack}
	 */
	public static ItemStack createItemStack(Material material, String itemLabel) {
		return createItemStack(material, 1, itemLabel);
	}

	/**
	 * Create an ItemStack for the requested Material amount.
	 *
	 * @param material  {@link Material}
	 * @param amount requested amount
	 * @param itemLabel display name
	 * @return created {@link ItemStack}
	 */
	public static ItemStack createItemStack(Material material, Integer amount, String itemLabel) {
		ItemStack item = new ItemStack(material, amount);
		if (itemLabel != null) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(itemLabel);
			item.setItemMeta(meta);
		}
		return item;
	}

	/**
	 * Lookup the matching Material
	 * Use the 1.13 API to lookup the Material,
	 * It will fall back to XMaterial if it fails to find it
	 *
	 * @param materialName
	 * @return matching Material
	 */
	public static Material lookupMaterial(String materialName) {
		Material material = Material.getMaterial(materialName);

		if (material == null && ValidationUtils.isStringValid(materialName)) {
			Optional<XMaterial> matching = XMaterial.matchXMaterial(materialName);

			if (matching.isPresent()) {
				material = matching.get().parseMaterial();
			}
		}

		return material;
	}


	/**
	 * Lookup the Material information requested by player
	 * Will either lookup the provided argument
	 * Or lookup the ItemStack in the players main hand
	 *
	 * @param args
	 * @param player
	 */
	public static void lookupMaterialInformation(Player player, String... args) {
		Material material;
		ItemStack data = null;

		if (args.length > 1) {
			material = Material.getMaterial(args[1]);

		} else {
			data = getItemStackInPlayersHand(player);
			material = data.getType();
		}
		if (material != null) {
			TranslationUtils.sendValue(player, "Material", material.name());
			if (data != null) {
				TranslationUtils.sendValue(player, "Data", data.toString());
			}
		} else {
			TranslationUtils.sendMessage(player, "Invalid Material!");
		}
	}

	public static boolean isCheckpointSafe(Player player, Block block) {
		List<Material> validMaterials = getValidCheckpointMaterials();
		Block blockUnder = block.getRelative(BlockFace.DOWN);

		//check if player is standing in a half-block
		if (!block.getType().equals(Material.AIR) && !block.getType().equals(XMaterial.CAVE_AIR.parseMaterial())
				&& !block.getType().equals(lookupMaterial(Parkour.getDefaultConfig().getString("OnCourse.CheckpointMaterial")))) {
			TranslationUtils.sendMessage(player, "Invalid Material for Checkpoint: &b" + block.getType());
			return false;
		}

		if (!blockUnder.getType().isOccluding()) {
			if (blockUnder.getState().getData() instanceof Stairs) {
				Stairs stairs = (Stairs) blockUnder.getState().getData();
				if (!stairs.isInverted()) {
					TranslationUtils.sendMessage(player, "Invalid Material for Checkpoint: &b" + blockUnder.getType());
					return false;
				}
			} else if (!validMaterials.contains(blockUnder.getType())) {
				TranslationUtils.sendMessage(player, "Invalid Material for Checkpoint: &b" + blockUnder.getType());
				return false;
			}
		}
		return true;
	}

	public static boolean sameBlockLocations(Location location1, Location location2) {
		return location1.getBlockX() == location2.getBlockX()
				&& location1.getBlockY() == location2.getBlockY()
				&& location1.getBlockZ() == location2.getBlockZ();
	}

	public static List<Material> getValidCheckpointMaterials() {
		if (validCheckpointMaterials == null) {
			validCheckpointMaterials = Arrays.asList(Material.AIR, XMaterial.CAVE_AIR.parseMaterial(), Material.REDSTONE_BLOCK,
					XMaterial.ACACIA_SLAB.parseMaterial(), XMaterial.BIRCH_SLAB.parseMaterial(), XMaterial.BRICK_SLAB.parseMaterial(),
					XMaterial.COBBLESTONE_SLAB.parseMaterial(), XMaterial.DARK_OAK_SLAB.parseMaterial(), XMaterial.DARK_PRISMARINE_SLAB.parseMaterial(),
					XMaterial.JUNGLE_SLAB.parseMaterial(), XMaterial.NETHER_BRICK_SLAB.parseMaterial(), XMaterial.OAK_SLAB.parseMaterial(),
					XMaterial.PETRIFIED_OAK_SLAB.parseMaterial(), XMaterial.PRISMARINE_BRICK_SLAB.parseMaterial(), XMaterial.PRISMARINE_SLAB.parseMaterial(),
					XMaterial.QUARTZ_SLAB.parseMaterial(), XMaterial.RED_SANDSTONE_SLAB.parseMaterial(), XMaterial.SANDSTONE_SLAB.parseMaterial(),
					XMaterial.SPRUCE_SLAB.parseMaterial(), XMaterial.STONE_BRICK_SLAB.parseMaterial(), XMaterial.STONE_SLAB.parseMaterial());

			if (!Bukkit.getBukkitVersion().contains("1.8")) {
				validCheckpointMaterials.add(Material.PURPUR_SLAB);
			}
		}

		return validCheckpointMaterials;
	}
}

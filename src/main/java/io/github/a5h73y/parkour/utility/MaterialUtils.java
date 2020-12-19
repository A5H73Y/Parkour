package io.github.a5h73y.parkour.utility;

import static com.cryptomorin.xseries.XMaterial.ACACIA_SLAB;
import static com.cryptomorin.xseries.XMaterial.BIRCH_SLAB;
import static com.cryptomorin.xseries.XMaterial.BRICK_SLAB;
import static com.cryptomorin.xseries.XMaterial.CAVE_AIR;
import static com.cryptomorin.xseries.XMaterial.COBBLESTONE_SLAB;
import static com.cryptomorin.xseries.XMaterial.DARK_OAK_SLAB;
import static com.cryptomorin.xseries.XMaterial.DARK_PRISMARINE_SLAB;
import static com.cryptomorin.xseries.XMaterial.JUNGLE_SLAB;
import static com.cryptomorin.xseries.XMaterial.NETHER_BRICK_SLAB;
import static com.cryptomorin.xseries.XMaterial.OAK_SLAB;
import static com.cryptomorin.xseries.XMaterial.PETRIFIED_OAK_SLAB;
import static com.cryptomorin.xseries.XMaterial.PRISMARINE_BRICK_SLAB;
import static com.cryptomorin.xseries.XMaterial.PRISMARINE_SLAB;
import static com.cryptomorin.xseries.XMaterial.PURPUR_SLAB;
import static com.cryptomorin.xseries.XMaterial.QUARTZ_SLAB;
import static com.cryptomorin.xseries.XMaterial.RED_SANDSTONE_SLAB;
import static com.cryptomorin.xseries.XMaterial.SANDSTONE_SLAB;
import static com.cryptomorin.xseries.XMaterial.SPRUCE_SLAB;
import static com.cryptomorin.xseries.XMaterial.STONE_BRICK_SLAB;
import static com.cryptomorin.xseries.XMaterial.STONE_SLAB;
import static com.cryptomorin.xseries.XMaterial.matchXMaterial;

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

/**
 * Material Utility methods.
 */
public class MaterialUtils {

	private static List<Material> validCheckpointMaterials;

	/**
	 * Get the Material in the Player's hand.
	 *
	 * @param player target player
	 * @return {@link Material}
	 */
	public static Material getMaterialInPlayersHand(Player player) {
		return getItemStackInPlayersHand(player).getType();
	}

	/**
	 * Get the ItemStack in the Player's hand.
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
	 * Lookup the matching Material.
	 * Uses the {@link XMaterial} library to match a known name.
	 *
	 * @param materialName material name
	 * @return matching Material
	 */
	public static Material lookupMaterial(String materialName) {
		Material material = Material.getMaterial(materialName);

		if (material == null && ValidationUtils.isStringValid(materialName)) {
			Optional<XMaterial> matching = matchXMaterial(materialName);

			if (matching.isPresent()) {
				material = matching.get().parseMaterial();
			}
		}

		return material;
	}


	/**
	 * Lookup the Material information requested by Player.
	 * If arguments are provided it will try to match a Material based on name.
	 * Otherwise it will display the Material information of the item in the Player's main hand.
	 *
	 * @param player player
	 * @param args command arguments
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

	/**
	 * Check if the current Block is safe for a Checkpoint.
	 *
	 * @param player player
	 * @param block block
	 * @return block is valid
	 */
	public static boolean isCheckpointSafe(Player player, Block block) {
		List<Material> validMaterials = getValidCheckpointMaterials();
		Block blockUnder = block.getRelative(BlockFace.DOWN);

		//check if player is standing in a half-block
		if (!block.getType().equals(Material.AIR) && !block.getType().equals(CAVE_AIR.parseMaterial())
				&& !block.getType().equals(lookupMaterial(
						Parkour.getDefaultConfig().getString("OnCourse.CheckpointMaterial")))) {
			TranslationUtils.sendMessage(player, "Invalid Material for Checkpoint: &b" + block.getType());
			return false;
		}

		if (!blockUnder.getType().isOccluding()) {
			if (blockUnder.getState().getData() instanceof Stairs) {
				Stairs stairs = (Stairs) blockUnder.getState().getData();
				if (!stairs.isInverted()) {
					TranslationUtils.sendMessage(player,
							"Invalid Material for Checkpoint: &b" + blockUnder.getType());
					return false;
				}
			} else if (!validMaterials.contains(blockUnder.getType())) {
				TranslationUtils.sendMessage(player,
						"Invalid Material for Checkpoint: &b" + blockUnder.getType());
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the Locations have the identical block coordinates.
	 *
	 * @param location1 location 1
	 * @param location2 location 2
	 * @return same block locations
	 */
	public static boolean sameBlockLocations(Location location1, Location location2) {
		return location1.getBlockX() == location2.getBlockX()
				&& location1.getBlockY() == location2.getBlockY()
				&& location1.getBlockZ() == location2.getBlockZ();
	}

	/**
	 * Get the known valid checkpoint materials.
	 * We should update XBlock to include `isSlab` check.
	 *
	 * @return valid materials
	 */
	public static List<Material> getValidCheckpointMaterials() {
		if (validCheckpointMaterials == null) {
			validCheckpointMaterials = Arrays.asList(Material.AIR,
					CAVE_AIR.parseMaterial(),
					Material.REDSTONE_BLOCK,
					ACACIA_SLAB.parseMaterial(),
					BIRCH_SLAB.parseMaterial(),
					BRICK_SLAB.parseMaterial(),
					COBBLESTONE_SLAB.parseMaterial(),
					DARK_OAK_SLAB.parseMaterial(),
					DARK_PRISMARINE_SLAB.parseMaterial(),
					JUNGLE_SLAB.parseMaterial(),
					OAK_SLAB.parseMaterial(),
					NETHER_BRICK_SLAB.parseMaterial(),
					PETRIFIED_OAK_SLAB.parseMaterial(),
					PRISMARINE_BRICK_SLAB.parseMaterial(),
					PRISMARINE_SLAB.parseMaterial(),
					PURPUR_SLAB.parseMaterial(),
					QUARTZ_SLAB.parseMaterial(),
					RED_SANDSTONE_SLAB.parseMaterial(),
					SANDSTONE_SLAB.parseMaterial(),
					SPRUCE_SLAB.parseMaterial(),
					STONE_BRICK_SLAB.parseMaterial(),
					STONE_SLAB.parseMaterial());

		}

		return validCheckpointMaterials;
	}
}

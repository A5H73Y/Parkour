package io.github.a5h73y.parkour.utility;

import static com.cryptomorin.xseries.XMaterial.ACACIA_SLAB;
import static com.cryptomorin.xseries.XMaterial.BIRCH_SLAB;
import static com.cryptomorin.xseries.XMaterial.BRICK_SLAB;
import static com.cryptomorin.xseries.XMaterial.CAVE_AIR;
import static com.cryptomorin.xseries.XMaterial.COBBLESTONE_SLAB;
import static com.cryptomorin.xseries.XMaterial.DARK_OAK_SLAB;
import static com.cryptomorin.xseries.XMaterial.JUNGLE_SLAB;
import static com.cryptomorin.xseries.XMaterial.NETHER_BRICK_SLAB;
import static com.cryptomorin.xseries.XMaterial.OAK_SLAB;
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
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Stairs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
		return createItemStack(material, 1, itemLabel, false, null);
	}

	/**
	 * Create an ItemStack for the requested Material amount.
	 *
	 * @param material  {@link Material}
	 * @param amount requested amount
	 * @param itemLabel display name
	 * @return created {@link ItemStack}
	 */
	public static ItemStack createItemStack(@NotNull Material material,
	                                        int amount,
	                                        @Nullable String itemLabel,
	                                        @Nullable Boolean unbreakable,
											@Nullable Integer customModelData) {
		ItemStack itemStack = new ItemStack(material, amount);
		ItemMeta itemMeta = itemStack.getItemMeta();

		if (itemMeta != null) {
			if (itemLabel != null) {
				itemMeta.setDisplayName(itemLabel);
			}
			if (Boolean.TRUE.equals(unbreakable)) {
				itemMeta.setUnbreakable(true);
			}
			if (customModelData != null) {
				itemMeta.setCustomModelData(customModelData);
			}
			itemStack.setItemMeta(itemMeta);
		}
		return itemStack;
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
	 * Otherwise, it will display the Material information of the item in the Player's main hand.
	 *
	 * @param player player
	 * @param materialName optional material name
	 */
	public static void lookupMaterialInformation(Player player, @Nullable String materialName) {
		Material material;
		ItemStack data = null;

		if (materialName != null) {
			material = Material.getMaterial(materialName.toUpperCase());

		} else {
			data = getItemStackInPlayersHand(player);
			material = data.getType();
		}
		if (material != null) {
			TranslationUtils.sendValue(player, "Material", material.name());
			if (data != null) {
				TranslationUtils.sendValue(player, "Data", data.toString());
				TranslationUtils.sendValue(player, "Base 64", Parkour.getInstance()
						.getConfigManager().getItemStackSerializable().serialize(data));
			}
		} else {
			TranslationUtils.sendMessage(player, "Invalid Material!");
		}
	}

	/**
	 * Check if the current Block is safe for a Checkpoint.
	 * In all versions, invalid blocks are doors, trapdoors, fences, fence_gates and walls - the
	 * pressure plate will either break when placed, break in-game or not trigger when stepped on.
	 * In pre-1.13 versions, checkpoints created on non-occluding blocks will either replace the
	 * block with the pressure plate, or register a checkpoint but fail to create the pressure
	 * plate on the block. Exceptions to this are inverted stairs and 'upper' slabs.
	 *
	 * @param player player
	 * @param block block
	 * @return block is valid
	 */
	public static boolean isCheckpointSafe(Player player, Block block) {
		//check if player is standing in a half-block
		if (!block.getType().equals(Material.AIR) && !block.getType().equals(CAVE_AIR.parseMaterial())
				&& !block.getType().equals(lookupMaterial(
				Parkour.getDefaultConfig().getString("OnCourse.CheckpointMaterial")))) {
			TranslationUtils.sendMessage(player, "Invalid Material for Checkpoint: &b" + block.getType());
			return false;
		}

		final String[] invalidBlocks = {"door", "wall", "fence"};
		Block blockUnder = block.getRelative(BlockFace.DOWN);
		if (Stream.of(invalidBlocks).anyMatch(blockUnder.getType().toString().toLowerCase()::contains)) {
			TranslationUtils.sendMessage(player, "Invalid Material for Checkpoint: &b" + blockUnder.getType());
			return false;
		}

		if (PluginUtils.getMinorServerVersion() > 12) {
			return true;
		}

		List<Material> validMaterials = getValidCheckpointMaterials();
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
	public static boolean sameBlockLocations(@Nullable Location location1, @Nullable Location location2) {
		return location1 != null && location2 != null
				&& location1.getBlockX() == location2.getBlockX()
				&& location1.getBlockY() == location2.getBlockY()
				&& location1.getBlockZ() == location2.getBlockZ();
	}

	public static MaterialData getMaterialData(String materialData) {
		MaterialData result = new MaterialData();
		String materialName;

		if (materialData.contains(":")) {
			String[] dataParts = materialData.split(":");
			materialName = dataParts[0];
			if (ValidationUtils.isPositiveInteger(dataParts[1])) {
				result.customModelData = Integer.valueOf(dataParts[1]);
			}
		} else {
			materialName = materialData;
		}

		result.material = Material.getMaterial(materialName.toUpperCase());
		return result;
	}

	/**
	 * Get the known valid checkpoint materials.
	 * We should update XBlock to include `isSlab` check.
	 *
	 * @return valid materials
	 */
	private static List<Material> getValidCheckpointMaterials() {
		if (validCheckpointMaterials == null) {
			validCheckpointMaterials = Arrays.asList(Material.AIR,
					CAVE_AIR.parseMaterial(),
					Material.REDSTONE_BLOCK,
					ACACIA_SLAB.parseMaterial(),
					BIRCH_SLAB.parseMaterial(),
					BRICK_SLAB.parseMaterial(),
					COBBLESTONE_SLAB.parseMaterial(),
					DARK_OAK_SLAB.parseMaterial(),
					JUNGLE_SLAB.parseMaterial(),
					OAK_SLAB.parseMaterial(),
					NETHER_BRICK_SLAB.parseMaterial(),
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

	private MaterialUtils() {}

	public static final class MaterialData {

		private Material material;
		private Integer customModelData;

		public Material getMaterial() {
			return material;
		}

		public void setMaterial(Material material) {
			this.material = material;
		}

		public Integer getCustomModelData() {
			return customModelData;
		}

		public void setCustomModelData(Integer customModelData) {
			this.customModelData = customModelData;
		}
	}
}

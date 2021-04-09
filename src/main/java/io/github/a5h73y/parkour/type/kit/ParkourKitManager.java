package io.github.a5h73y.parkour.type.kit;

import static io.github.a5h73y.parkour.configuration.impl.ParkourKitConfig.PARKOUR_KIT_CONFIG_PREFIX;

import com.cryptomorin.xseries.XMaterial;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ActionType;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.Cacheable;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * ParkourKit Manager.
 * Keeps a lazy Cache of {@link ParkourKit} which can be reused by other Courses.
 */
public class ParkourKitManager extends AbstractPluginReceiver implements Cacheable<ParkourKit> {

	private final Map<String, ParkourKit> parkourKitCache = new HashMap<>();

	public ParkourKitManager(final Parkour parkour) {
		super(parkour);
	}

	/**
	 * Find ParkourKit by unique name.
	 * If the ParkourKit is cached it can be returned directly.
	 * Otherwise the entire ParkourKit will be populated from the config.
	 *
	 * @param name parkour kit name
	 * @return populated {@link ParkourKit}
	 */
	@Nullable
	public ParkourKit getParkourKit(String name) {
		name = name.toLowerCase();

		if (parkourKitCache.containsKey(name)) {
			return parkourKitCache.get(name);
		}

		if (!ParkourKitInfo.doesParkourKitExist(name)) {
			return null;
		}

		ParkourKit kit = populateParkourKit(name);
		parkourKitCache.put(name, kit);
		return kit;
	}

	/**
	 * Give ParkourKit to Player.
	 * The Contents of the Player's inventory will be filled with the ParkourKit items.
	 * If configured, the Player's original inventory will be wiped.
	 *
	 * @param player requesting player
	 * @param kitName parkour kit name
	 */
	public void giveParkourKit(Player player, String kitName) {
		if (parkour.getConfig().getBoolean("Other.ParkourKit.ReplaceInventory")) {
			player.getInventory().clear();
		}

		ParkourKit kit = getParkourKit(kitName);

		if (kit == null) {
			TranslationUtils.sendMessage(player, "Invalid ParkourKit: &4" + kitName);
			return;
		}

		for (Material material : kit.getMaterials()) {
			String actionName = ParkourKitInfo.getActionTypeForMaterial(kitName, material.name());

			if (actionName == null) {
				continue;
			}

			actionName = StringUtils.standardizeText(actionName);

			ItemStack itemStack = MaterialUtils.createItemStack(material,
					TranslationUtils.getTranslation("Kit." + actionName, false));
			player.getInventory().addItem(itemStack);
		}

		if (parkour.getConfig().getBoolean("Other.ParkourKit.GiveSign")) {
			ItemStack itemStack = MaterialUtils.createItemStack(XMaterial.OAK_SIGN.parseMaterial(),
					TranslationUtils.getTranslation("Kit.Sign", false));
			player.getInventory().addItem(itemStack);
		}

		player.updateInventory();
		TranslationUtils.sendValueTranslation("Other.Kit", kitName, player);
		PluginUtils.logToFile(player.getName() + " received the kit");
	}

	/**
	 * Validate a ParkourKit.
	 * Used to identify if there are any problems with the ParkourKit config.
	 * Examples include an unknown Material, or an unknown Action type.
	 *
	 * @param sender requesting sender
	 * @param kitName parkour kit name
	 */
	public void validateParkourKit(CommandSender sender, String kitName) {
		if (!ParkourKitInfo.doesParkourKitExist(kitName)) {
			TranslationUtils.sendTranslation("Error.UnknownParkourKit", sender);
			return;
		}

		List<String> invalidTypes = new ArrayList<>();
		Set<String> materialList = ParkourKitInfo.getParkourKitMaterials(kitName);

		for (String material : materialList) {
			// try to get the server lookup version first
			if (Material.getMaterial(material) == null) {
				invalidTypes.add("Unknown Material: " + material);

				// try to see if we have a matching legacy version
				Material matching = MaterialUtils.lookupMaterial(material);
				if (matching != null) {
					invalidTypes.add(" Could you have meant: " + matching.name() + "?");
				}
			} else {
				String actionType = ParkourKitInfo.getActionTypeForMaterial(kitName, material);

				if (validateActionType(actionType) == null) {
					invalidTypes.add("Material: " + material + ", Unknown Action: " + actionType);
				}
			}
		}

		TranslationUtils.sendMessage(sender, invalidTypes.size() + " problems with &b" + kitName + "&f found.");

		for (String type : invalidTypes) {
			TranslationUtils.sendMessage(sender, "&4" + type, false);
		}
	}

	/**
	 * Display ParkourKits and their contents.
	 * Can be used to display available ParkourKits.
	 * Alternatively can display the contents of a specified ParkourKit, including the Materials and corresponding Actions.
	 *
	 * @param sender requesting sender
	 * @param args command arguments
	 */
	public void displayParkourKits(CommandSender sender, String... args) {
		Set<String> parkourKit = ParkourKitInfo.getAllParkourKitNames();

		// specifying a kit
		if (args.length == 2) {
			String kitName = args[1].toLowerCase();
			if (!parkourKit.contains(kitName)) {
				TranslationUtils.sendMessage(sender, "This ParkourKit set does not exist!");
				return;
			}

			TranslationUtils.sendHeading("ParkourKit: " + kitName, sender);
			Set<String> materials = ParkourKitInfo.getParkourKitMaterials(kitName);

			for (String material : materials) {
				String actionTypeName = ParkourKitInfo.getActionTypeForMaterial(kitName, material);
				ActionType actionType = validateActionType(actionTypeName);
				if (actionType != null) {
					TranslationUtils.sendValue(sender, material, actionTypeName);
				} else {
					TranslationUtils.sendMessage(sender, "Invalid Action Type: " + actionTypeName);
				}
			}

		} else {
			//displaying all available kits
			TranslationUtils.sendHeading(parkourKit.size() + " ParkourKits found", sender);
			for (String kit : parkourKit) {
				sender.sendMessage("* " + kit);
			}
		}
	}

	/**
	 * Delete the requested ParkourKit.
	 *
	 * @param sender command sender
	 * @param kitName kit name
	 */
	public void deleteParkourKit(CommandSender sender, String kitName) {
		if (!ParkourKitInfo.doesParkourKitExist(kitName)) {
			TranslationUtils.sendTranslation("Error.UnknownParkourKit", sender);
			return;
		}

		ParkourKitInfo.deleteKit(kitName);
		clearCache(kitName);
		TranslationUtils.sendValueTranslation("Parkour.Delete", kitName + " ParkourKit", sender);
		PluginUtils.logToFile(kitName + " parkourkit was deleted by " + sender.getName());
	}

	@Override
	public int getCacheSize() {
		return parkourKitCache.size();
	}

	@Override
	public void clearCache() {
		parkourKitCache.clear();
	}

	/**
	 * Clear the specified ParkourKit information.
	 *
	 * @param kitName target ParkourKit name
	 */
	public void clearCache(String kitName) {
		parkourKitCache.remove(kitName.toLowerCase());
	}

	/**
	 * Construct the {@link ParkourKit} object, retrieving it's relevant information.
	 * If the Material provided is invalid, then it won't be added to our list of materials.
	 * If the Action name provided is invalid, then it won't be added to our list of materials.
	 *
	 * @param kitName parkour kit name
	 * @return populated ParkourKit
	 */
	private ParkourKit populateParkourKit(String kitName) {
		ParkourConfiguration config = Parkour.getConfig(ConfigType.PARKOURKIT);
		Set<String> rawMaterials = ParkourKitInfo.getParkourKitMaterials(kitName);
		EnumMap<Material, ParkourKitAction> actionTypes = new EnumMap<>(Material.class);

		for (String rawMaterial : rawMaterials) {
			Material material = validateAndGetMaterial(kitName, rawMaterial);

			if (material == null) {
				continue;
			}

			String configPath = PARKOUR_KIT_CONFIG_PREFIX + kitName + "." + material.name() + ".";
			ActionType actionType = validateActionType(config.getString(configPath + "Action"));

			if (actionType != null) {
				double strength = config.getDouble(configPath + "Strength", 1);
				int duration = config.getInt(configPath + "Duration", 200);
				actionTypes.put(material, new ParkourKitAction(actionType, strength, duration));
			}
		}

		return new ParkourKit(kitName, actionTypes);
	}

	/**
	 * Find and Validate matching Material.
	 * Checks if the found Material is invalid and try to replace it with it's correct name.
	 * If a valid match cannot be found, it will be ignored from the ParkourKit creation.
	 *
	 * @param kitName parkour kit name
	 * @param materialName material name
	 * @return matching Material
	 */
	@Nullable
	private Material validateAndGetMaterial(String kitName, String materialName) {
		Material material = Material.getMaterial(materialName);

		// Try the official look up
		if (material == null) {
			// if that fails, try our custom lookup
			material = MaterialUtils.lookupMaterial(materialName);

			if (material != null) {
				// if we find a old matching version, replace it with the new version
				PluginUtils.log("Outdated Material found " + materialName + " found new version "
						+ material.name(), 1);
				updateOutdatedMaterial(kitName, materialName, material.name());
				PluginUtils.log("Action has been transferred to use " + material.name());

			} else {
				PluginUtils.log("Material " + materialName + " in kit " + kitName + " is invalid.", 2);
			}
		}
		return material;
	}

	private ActionType validateActionType(String actionTypeName) {
		ActionType actionType = null;

		try {
			actionType = ActionType.valueOf(actionTypeName.toUpperCase());
		} catch (IllegalArgumentException ignored) {
			PluginUtils.log("Unknown ParkourKit Action Type: " + actionTypeName, 2);
		}
		return actionType;
	}

	private void updateOutdatedMaterial(String kitName, String oldMaterial, String newMaterial) {
		ParkourConfiguration parkourKitConfig = Parkour.getConfig(ConfigType.PARKOURKIT);
		Set<String> oldAction = parkourKitConfig.getConfigurationSection(
				PARKOUR_KIT_CONFIG_PREFIX + kitName + "." + oldMaterial).getKeys(false);

		// we copy all of the attributes from the old action (strength, duration, etc)
		for (String attribute : oldAction) {
			String matchingValue = parkourKitConfig.getString(
					PARKOUR_KIT_CONFIG_PREFIX + kitName + "." + oldMaterial + "." + attribute);

			parkourKitConfig.set(PARKOUR_KIT_CONFIG_PREFIX + kitName + "." + newMaterial + "." + attribute,
					ValidationUtils.isInteger(matchingValue) ? Integer.parseInt(matchingValue)
							: matchingValue);
		}

		// remove the old material
		parkourKitConfig.set(PARKOUR_KIT_CONFIG_PREFIX + kitName + "." + oldMaterial, null);
		parkourKitConfig.save();
	}
}

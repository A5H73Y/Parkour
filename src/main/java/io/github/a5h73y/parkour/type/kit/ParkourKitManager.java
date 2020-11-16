package io.github.a5h73y.parkour.type.kit;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ActionType;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.type.Cacheable;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class ParkourKitManager extends AbstractPluginReceiver implements Cacheable<ParkourKit> {

	private final transient Map<String, ParkourKit> parkourKitCache = new HashMap<>();

	public ParkourKitManager(final Parkour parkour) {
		super(parkour);
	}

	/**
	 * Find ParkourKit by name.
	 * If it's already loaded, then just return that, otherwise create the set and load it.
	 * Hopefully this is better for performance
	 *
	 * @param name
	 * @return ParkourKit
	 */
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
	 * ParkourKit
	 * Each ParkourKit set has a unique name to refer to it, apart from the default set.
	 * The format being ParkourKit.(name).MATERIAL.Action = "action"
	 * If the Material provided is invalid, then it won't be added to our list of materials
	 * Also if the Action provided is invalid, then it won't be added to our list of materials.
	 * This is so ParkourKit remain safe while in use on a course.
	 *
	 * @param kitName
	 */
	private ParkourKit populateParkourKit(String kitName) {
		ParkourConfiguration config = Parkour.getConfig(ConfigType.PARKOURKIT);
		Set<String> rawMaterials = ParkourKitInfo.getParkourKitContents(kitName);
		Map<Material, ParkourKitAction> actionTypes = new HashMap<>();

		for (String rawMaterial : rawMaterials) {
			Material material = validateAndGetMaterial(kitName, rawMaterial);

			if (material == null) {
				continue;
			}

			String configPath = "ParkourKit." + kitName + "." + material.name() + ".";
			ActionType actionType = validateActionType(config.getString(configPath + "Action"));

			if (actionType != null) {
				double strength = config.getDouble(configPath + "Strength", 1);
				int duration = config.getInt(configPath + "Duration", 200);
				actionTypes.put(material, new ParkourKitAction(actionType, strength, duration));
			}
		}

		return new ParkourKit(kitName, actionTypes);
	}

	public void deleteKit(String argument) {
		Parkour.getConfig(ConfigType.PARKOURKIT).set("ParkourKit." + argument, null);
		Parkour.getConfig(ConfigType.PARKOURKIT).save();
		clearCache(argument);
	}

	/**
	 * Look up the stored material.
	 * Will check if the Material is invalid and try to replace it with its new version
	 * If it can't match, it will be ignored from the kit
	 *
	 * @param rawMaterial
	 * @return
	 */
	private Material validateAndGetMaterial(String kitName, String rawMaterial) {
		Material material = Material.getMaterial(rawMaterial);

		// Try the official look up
		if (material == null) {
			// if that fails, try our custom lookup
			material = MaterialUtils.lookupMaterial(rawMaterial);

			if (material != null) {
				// if we find a old matching version, replace it with the new version
				PluginUtils.log("Outdated Material found " + rawMaterial + " found new version " + material.name(), 1);
				updateOutdatedMaterial(kitName, rawMaterial, material.name());
				PluginUtils.log("Action has been transferred to use " + material.name());

			} else {
				PluginUtils.log("Material " + rawMaterial + " in kit " + kitName + " is invalid.", 2);
			}
		}
		return material;
	}

	/**
	 * Validate ParkourKit set
	 * Used for identifying what the problem with the ParkourKit is.
	 *
	 * @param args
	 * @param sender
	 */
	public void validateParkourKit(CommandSender sender, String kitName) {
		if (!ParkourKitInfo.doesParkourKitExist(kitName)) {
			TranslationUtils.sendTranslation("Error.UnknownParkourKit", sender);
			return;
		}

		List<String> invalidTypes = new ArrayList<>();
		String path = "ParkourKit." + kitName.toLowerCase();

		Set<String> materialList = ParkourKitInfo.getParkourKitContents(kitName);

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
				ParkourConfiguration parkourKitConfig = Parkour.getConfig(ConfigType.PARKOURKIT);
				String actionType = parkourKitConfig.getString(path + "." + material + ".Action");

				if (validateActionType(actionType) == null) {
					invalidTypes.add("Material: " + material + ", Unknown Action: " + actionType);
				}
			}
		}

		sender.sendMessage(Parkour.getPrefix() + invalidTypes.size() + " problems with " + ChatColor.AQUA + kitName + ChatColor.WHITE + " found.");
		if (invalidTypes.size() > 0) {
			for (String type : invalidTypes) {
				sender.sendMessage(ChatColor.RED + type);
			}
		}
	}

	/**
	 * Display ParkourKit
	 * Can either display all the ParkourKit available
	 * Or specify which ParkourKit you want to see and which materials are used and their corresponding action
	 *
	 * @param args
	 * @param sender
	 */
	public void listParkourKit(CommandSender sender, String[] args) {
		Set<String> parkourKit = ParkourKitInfo.getAllParkourKitNames();

		// specifying a kit
		if (args.length == 2) {
			String kitName = args[1].toLowerCase();
			if (!parkourKit.contains(kitName)) {
				sender.sendMessage(Parkour.getPrefix() + "This ParkourKit set does not exist!");
				return;
			}

			TranslationUtils.sendHeading("ParkourKit: " + kitName, sender);
			Set<String> materials = ParkourKitInfo.getParkourKitContents(kitName);

			for (String material : materials) {
				String actionTypeName = ParkourKitInfo.getActionTypeForMaterial(kitName, material);
				ActionType actionType = validateActionType(actionTypeName);
				if (actionType != null) {
					sender.sendMessage(material + ": " + ChatColor.GRAY + actionTypeName);
				} else {
					sender.sendMessage("Invalid Action Type: " + actionTypeName);
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

	private void updateOutdatedMaterial(String kitName, String oldMaterial, String newMaterial) {
		ParkourConfiguration parkourKitConfig = Parkour.getConfig(ConfigType.PARKOURKIT);
		Set<String> oldAction = parkourKitConfig.getConfigurationSection("ParkourKit." + kitName + "." + oldMaterial).getKeys(false);

		// we copy all of the attributes from the old action (strength, duration, etc)
		for (String attribute : oldAction) {
			String matchingValue = parkourKitConfig.getString("ParkourKit." + kitName + "." + oldMaterial + "." + attribute);

			parkourKitConfig.set("ParkourKit." + kitName + "." + newMaterial + "." + attribute,
					ValidationUtils.isInteger(matchingValue) ? Integer.parseInt(matchingValue) : matchingValue);
		}

		// remove the old kit
		parkourKitConfig.set("ParkourKit." + kitName + "." + oldMaterial, null);
		parkourKitConfig.save();
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

	@Override
	public int getCacheSize() {
		return parkourKitCache.size();
	}

	@Override
	public void clearCache() {
		parkourKitCache.clear();
	}

	/**
	 * Clear the ParkourKit cache
	 *
	 * @param kitName
	 */
	public void clearCache(String kitName) {
		parkourKitCache.remove(kitName);
	}
}

package io.github.a5h73y.parkour.type.kit;

import static io.github.a5h73y.parkour.other.ParkourConstants.DEFAULT;

import com.cryptomorin.xseries.XMaterial;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.parkourkit.CreateParkourKitConversation;
import io.github.a5h73y.parkour.conversation.parkourkit.EditParkourKitConversation;
import io.github.a5h73y.parkour.other.TriConsumer;
import io.github.a5h73y.parkour.type.CacheableParkourManager;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ParkourKit Manager.
 * Keeps a lazy Cache of {@link ParkourKit} which can be reused by other Courses.
 */
public class ParkourKitManager extends CacheableParkourManager {

	private final Map<String, TriConsumer<CommandSender, String, String>> parkourKitActions = new HashMap<>();
	private final Map<String, ParkourKit> parkourKitCache = new HashMap<>();

	public ParkourKitManager(final Parkour parkour) {
		super(parkour);
	}

	public Set<String> getParkourKitActions() {
		return parkourKitActions.keySet();
	}

	@Override
	protected ParkourKitConfig getConfig() {
		return parkour.getConfigManager().getParkourKitConfig();
	}

	public boolean doesParkourKitExist(String kitName) {
		return getConfig().doesParkourKitExist(kitName);
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

		if (!getConfig().doesParkourKitExist(name)) {
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
		if (parkour.getParkourConfig().getBoolean("ParkourKit.ReplaceInventory")) {
			player.getInventory().clear();
		}

		ParkourKit kit = getParkourKit(kitName);

		if (kit == null) {
			TranslationUtils.sendMessage(player, "Invalid ParkourKit: &4" + kitName);
			return;
		}

		for (Material material : kit.getMaterials()) {
			String actionName = getConfig().getActionTypeForMaterial(kitName, material.name());

			if (actionName == null) {
				continue;
			}

			actionName = StringUtils.standardizeText(actionName);

			ItemStack itemStack = MaterialUtils.createItemStack(material,
					TranslationUtils.getTranslation("Kit." + actionName, false));
			player.getInventory().addItem(itemStack);
		}

		if (parkour.getParkourConfig().getBoolean("ParkourKit.GiveSign")) {
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
	 * @param commandSender command sender
	 * @param kitName parkour kit name
	 */
	public void validateParkourKit(CommandSender commandSender, String kitName) {
		if (!PermissionUtils.hasPermission(commandSender, Permission.ADMIN_COURSE)) {
			return;
		}

		if (!getConfig().doesParkourKitExist(kitName)) {
			TranslationUtils.sendTranslation("Error.UnknownParkourKit", commandSender);
			return;
		}

		List<String> invalidTypes = new ArrayList<>();
		Set<String> materialList = getConfig().getParkourKitMaterials(kitName);

		for (String material : materialList) {
			// try to get the server lookup version first
			if (Material.getMaterial(material) == null) {
				invalidTypes.add("Unknown Material: &b" + material);

				// try to see if we have a matching legacy version
				Material matching = MaterialUtils.lookupMaterial(material);
				if (matching != null) {
					invalidTypes.add(" Could you have meant: " + matching.name() + "?");
				}
			} else {
				String actionType = getConfig().getActionTypeForMaterial(kitName, material);

				if (validateActionType(actionType) == null) {
					invalidTypes.add("Material: &b" + material + "&f, Unknown Action: &b" + actionType);
				}
			}
		}

		TranslationUtils.sendMessage(commandSender, invalidTypes.size() + " problems with &b" + kitName + "&f found.");

		for (String type : invalidTypes) {
			TranslationUtils.sendMessage(commandSender, type, false);
		}
	}

	/**
	 * Display ParkourKits and their contents.
	 * Can be used to display available ParkourKits.
	 * Alternatively can display the contents of a specified ParkourKit, including the Materials and corresponding Actions.
	 *
	 * @param commandSender command sender
	 * @param kitName parkour kit name
	 */
	public void displayParkourKits(CommandSender commandSender, @Nullable String kitName) {
		if (!PermissionUtils.hasPermission(commandSender, Permission.ADMIN_COURSE)) {
			return;
		}

		Set<String> parkourKit = getConfig().getAllParkourKitNames();

		// specifying a kit
		if (kitName != null) {
			kitName = kitName.toLowerCase();

			if (!parkourKit.contains(kitName)) {
				TranslationUtils.sendMessage(commandSender, "This ParkourKit set does not exist!");
				return;
			}

			TranslationUtils.sendHeading("ParkourKit: " + kitName, commandSender);
			Set<String> materials = getConfig().getParkourKitMaterials(kitName);

			for (String material : materials) {
				String actionTypeName = getConfig().getActionTypeForMaterial(kitName, material);
				ActionType actionType = validateActionType(actionTypeName);
				if (actionType == null) {
					TranslationUtils.sendMessage(commandSender, "Invalid Action Type: " + actionTypeName);
					return;
				}
				if (actionTypeName.equalsIgnoreCase("potion")) {
					actionTypeName += " (" + getConfig().getEffectTypeForMaterial(kitName, material) + ")";
				}
				TranslationUtils.sendValue(commandSender, material, actionTypeName);
			}

		} else {
			// displaying all available kits
			TranslationUtils.sendHeading(parkourKit.size() + " ParkourKits found", commandSender);
			for (String kit : parkourKit) {
				commandSender.sendMessage("* " + kit);
			}
		}
	}

	/**
	 * Delete the requested ParkourKit.
	 *
	 * @param commandSender command sender
	 * @param kitName kit name
	 */
	public void deleteParkourKit(CommandSender commandSender, String kitName) {
		if (!getConfig().doesParkourKitExist(kitName)) {
			TranslationUtils.sendTranslation("Error.UnknownParkourKit", commandSender);
			return;
		}

		getConfig().deleteKit(kitName);
		clearCache(kitName);
		TranslationUtils.sendValueTranslation("Parkour.Delete", kitName + " ParkourKit", commandSender);
		PluginUtils.logToFile(kitName + " parkourkit was deleted by " + commandSender.getName());
	}

	/**
	 * Process ParkourKit command.
	 *
	 * @param commandSender command sender
	 * @param command command
	 * @param argument argument
	 * @param detail detail
	 */
	public void processParkourKitCommand(@NotNull CommandSender commandSender,
	                                     @NotNull String command,
	                                     @Nullable String argument,
	                                     @Nullable String detail) {
		// TODO - turn into actions

		switch (command.toLowerCase()) {
			case "create":
			case "createkit":
				startCreateKitConversation(commandSender, argument);
				break;

			case "edit":
				startEditKitConversation(commandSender, argument);
				break;

			case "validate":
				validateParkourKit(commandSender, argument != null ? argument : DEFAULT);
				break;

			case "list":
				displayParkourKits(commandSender, argument);
				break;

			case "link":
				setLinkedParkourKit(commandSender, argument, detail);
				break;

			case "give":
				giveParkourKit((Player) commandSender, argument != null ? argument : DEFAULT);
				break;

			default:
				parkour.getParkourCommands().sendInvalidSyntax(commandSender, "parkourkit");
		}
	}

	/**
	 * Start the Create Kit Conversation.
	 * @param commandSender command sender
	 * @param kitName optional kit name
	 */
	public void startCreateKitConversation(@NotNull CommandSender commandSender,
	                                       @Nullable String kitName) {
		if (!PermissionUtils.hasPermission(commandSender, Permission.ADMIN_COURSE)) {
			return;
		}

		new CreateParkourKitConversation((Conversable) commandSender).withKitName(kitName).begin();
	}

	/**
	 * Start the Edit Kit Conversation.
	 * @param commandSender command sender
	 * @param kitName optional kit name
	 */
	public void startEditKitConversation(CommandSender commandSender, @Nullable String kitName) {
		if (!PermissionUtils.hasPermission(commandSender, Permission.ADMIN_COURSE)) {
			return;
		}

		new EditParkourKitConversation((Conversable) commandSender).withKitName(kitName).begin();
	}

	/**
	 * Set the Course's linked ParkourKit.
	 * @param commandSender command sender
	 * @param kitName parkour kit name
	 * @param courseName course name
	 */
	public void setLinkedParkourKit(CommandSender commandSender, String kitName, String courseName) {
		if (commandSender instanceof Player
				&& !PermissionUtils.hasPermissionOrCourseOwnership(
				(Player) commandSender, Permission.ADMIN_COURSE, courseName)) {
			return;
		}

		parkour.getCourseSettingsManager().setParkourKit(commandSender, courseName, kitName);
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
		Set<String> rawMaterials = getConfig().getParkourKitMaterials(kitName);
		EnumMap<Material, ParkourKitAction> actionTypes = new EnumMap<>(Material.class);

		for (String rawMaterial : rawMaterials) {
			Material material = validateAndGetMaterial(kitName, rawMaterial);

			if (material == null) {
				continue;
			}

			String pathPrefix = kitName + "." + material.name() + ".";
			ActionType actionType = validateActionType(getConfig().getString(pathPrefix + "Action"));

			if (actionType != null) {
				double strength = getConfig().getOrDefault(pathPrefix + "Strength", 1.0);
				int duration = getConfig().getOrDefault(pathPrefix + "Duration", 200);
				String effect = getConfig().getOrDefault(pathPrefix + "Effect", "");
				actionTypes.put(material, new ParkourKitAction(actionType, strength, duration, effect));
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
		Set<String> oldAction = getConfig().getSection(kitName + "." + oldMaterial).singleLayerKeySet();

		// we copy all of the attributes from the old action (strength, duration, etc)
		for (String attribute : oldAction) {
			String matchingValue = getConfig().getString(
					kitName + "." + oldMaterial + "." + attribute);

			getConfig().set(kitName + "." + newMaterial + "." + attribute,
					ValidationUtils.isInteger(matchingValue) ? Integer.parseInt(matchingValue)
							: matchingValue);
		}

		// remove the old material
		getConfig().remove(kitName + "." + oldMaterial);
	}
}

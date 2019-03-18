package me.A5H73Y.Parkour.ParkourKit;

import java.io.Serializable;
import java.util.*;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ParkourKit implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Map<String, ParkourKit> loaded = new HashMap<>();

    public static final List<String> validActions =
            Arrays.asList("death", "finish", "climb", "launch", "speed", "repulse", "norun", "nopotion", "bounce");

    // ParkourKit attributes
    private String name;
    private List<Material> materials = new ArrayList<>();

    /**
     * New point of accessing ParkourKit
     * If it's already loaded, then just return that, otherwise create the set and load it.
     * Hopefully this is better for performance
     * @param name
     * @return ParkourKit
     */
    public static ParkourKit getParkourKit(String name) {
        name = name.toLowerCase();

        if (loaded.containsKey(name)) {
            return loaded.get(name);
        }

        if (!ParkourKitInfo.doesParkourKitExist(name)) {
            return null;
        }

        ParkourKit kit = new ParkourKit(name);
        loaded.put(name, kit);
        return kit;
    }

    /**
     * Get a list of all the action possibilities
     * @return List of actions
     */
    public static List<String> getValidActions() {
        return validActions;
    }

    /**
     * Get the materials that this ParkourKit is made up of
     * @return List<Material>
     */
    public List<Material> getMaterials() {
        return materials;
    }

    /**
     * Get the corresponding action for the material
     * @param material
     * @return corresponding action for material
     */
    public String getAction(Material material) {
        if (!materials.contains(material)) {
            return null;
        }

        return getParkourKitData().getString("ParkourKit." + name + "." + material.name() + ".Action").toLowerCase();
    }

    /**
     * Get Strength of the ParkourKit action
     * @param material
     * @return strength double
     */
    public Double getStrength(Material material) {
        if (!materials.contains(material)) {
            return 0.0;
        }

        return getParkourKitData().getDouble("ParkourKit." + name + "." + material.name() + ".Strength", 1);
    }

    /**
     * Get Duration of the ParkourKit action
     * @param material
     * @return
     */
    public Integer getDuration(Material material) {
        if (!materials.contains(material)) {
            return 0;
        }

        return getParkourKitData().getInt("ParkourKit." + name + "." + material.name() + ".Duration", 200);
    }

    /**
     * Clear the ParkourKit cache
     * @param kitName
     */
    public static void clearMemory(String kitName) {
        loaded.remove(kitName);
    }

    /**
     * ParkourKit
     * Each ParkourKit set has a unique name to refer to it, apart from the default set.
     * The format being ParkourKit.(name).MATERIAL.Action = "action"
     * If the Material provided is invalid, then it won't be added to our list of materials
     * Also if the Action provided is invalid, then it won't be added to our list of materials.
     * This is so ParkourKit remain safe while in use on a course.
     *
     * @param name
     */
    private ParkourKit(String name) {
        this.name = name;

        Set<String> rawMaterials = getParkourKitData().getConfigurationSection("ParkourKit." + name).getKeys(false);

        for (String rawMaterial : rawMaterials) {
            Material material = validateAndGetMaterial(rawMaterial);

            if (material == null)
                continue;

            String action = getParkourKitData().getString("ParkourKit." + name + "." + material.name() + ".Action").toLowerCase();

            if (!validActions.contains(action)) {
                Utils.log("Action " + action + " in kit " + name + " is invalid.", 1);
                continue;
            }

            // we only add the material once we know it's valid
            materials.add(material);
        }
    }

    /**
     * Look up the stored material
     * Will check if the Material is invalid and try to replace it with its new version
     * If it can't match, it will be ignored from the kit
     * @param rawMaterial
     * @return
     */
    private Material validateAndGetMaterial(String rawMaterial) {
        Material material = Material.getMaterial(rawMaterial);

        // Try the official look up
        if (material == null) {

            // if that fails, try our custom lookup
            material = Utils.lookupMaterial(rawMaterial);

            if (material != null) {
                // if we find a old matching version, replace it with the new version
                Utils.log("Outdated Material found " + rawMaterial + " found new version " + material.name(), 1);
                updateOutdatedMaterial(rawMaterial, material.name());
                Utils.log("Action has been transferred to use " + material.name());
            } else {
                Utils.log("Material " + rawMaterial + " in kit " + name + " is invalid.", 2);
            }
        }
        return material;
    }

    private void updateOutdatedMaterial(String oldMaterial, String newMaterial) {
        Set<String> oldAction = getParkourKitData().getConfigurationSection("ParkourKit." + name + "." + oldMaterial).getKeys(false);

        // we copy all of the attributes from the old action (strength, duration, etc)
        for (String attribute : oldAction) {
            String matchingValue = getParkourKitData().getString("ParkourKit." + name + "." + oldMaterial + "." + attribute);

            getParkourKitData().set("ParkourKit." + name + "." + newMaterial + "." + attribute,
                    Utils.isInteger(matchingValue) ? Integer.valueOf(matchingValue) : matchingValue);
        }

        // remove the old kit
        getParkourKitData().set("ParkourKit." + name + "." + oldMaterial, null);
        Parkour.getParkourConfig().saveParkourKit();
    }

    private static FileConfiguration getParkourKitData() {
        return Parkour.getParkourConfig().getParkourKitData();
    }
}

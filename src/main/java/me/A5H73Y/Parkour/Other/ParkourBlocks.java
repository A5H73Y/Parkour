package me.A5H73Y.Parkour.Other;

import java.io.Serializable;
import java.util.*;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.Material;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public class ParkourBlocks implements Serializable {

    private static final long serialVersionUID = 1L;

    private static List<String> validActions =
            Arrays.asList("death", "finish", "climb", "launch", "speed", "norun", "nopotion", "bounce");

    private static Map<String, ParkourBlocks> loaded = new HashMap<>();

    // object attributes
    private String name;
    private List<Material> materials = new ArrayList<Material>();

    /**
     * ParkourBlocks
     * Each ParkourBlocks set has a unique name to refer to it, apart from the default set.
     * The format being ParkourBlock.MATERIAL.Action = "action"
     * If the Material provided is invalid, then it won't be added to our list of materials
     * Also if the Action provided is invalid, then it won't be added to our list of materials.
     * This is so ParkourBlocks remain safe while in use on a course.
     *
     * @param name
     */
    private ParkourBlocks(String name) {
        this.name = name;

        Set<String> rawMaterials = Parkour.getParkourConfig().getParkourBlocksData()
                .getConfigurationSection("ParkourBlocks." + name).getKeys(false);

        for (String rawMaterial : rawMaterials) {
            Material material = Material.getMaterial(rawMaterial);

            if (material == null) {
                Utils.log("Material " + rawMaterial + " is invalid.", 1);
                continue;
            }

            String action = Parkour.getParkourConfig().getParkourBlocksData()
                    .getString("ParkourBlocks." + name + "." + material.name() + ".Action").toLowerCase();

            if (!validActions.contains(action)) {
                Utils.log("Action " + action + " is invalid.", 1);
                continue;
            }

            // we only add the material once we know it's valid
            materials.add(material);
        }
    }

    /**
     * Get the materials that this ParkourBlocks is made up of
     * @return List<Material>
     */
    public List<Material> getMaterials() {
        return materials;
    }

    /**
     * Get the corresponding action for the material
     * @param material
     * @return
     */
    public String getAction(Material material) {
        if (!materials.contains(material)) {
            return null;
        }

        return Parkour.getParkourConfig().getParkourBlocksData()
                .getString("ParkourBlocks." + name + "." + material.name() + ".Action").toLowerCase();
    }

    /**
     * New point of accessing ParkourBlocks
     * If it's already loaded, then just return that, otherwise create the set and load it.
     * Hopefully this is better for performance
     * @param name
     * @return ParkourBlocks
     */
    public static ParkourBlocks getParkourBlocks(String name) {
        if (loaded.containsKey(name)) {
            return loaded.get(name);
        }

        if (!Parkour.getParkourConfig().getParkourBlocksData().contains("ParkourBlocks." + name)) {
            return null;
        }

        ParkourBlocks pb = new ParkourBlocks(name);
        loaded.put(name, pb);
        return pb;
    }
}

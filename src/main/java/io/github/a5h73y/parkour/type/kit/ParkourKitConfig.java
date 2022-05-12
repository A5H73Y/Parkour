package io.github.a5h73y.parkour.type.kit;

import de.leonhard.storage.Yaml;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.ParkourConstants;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParkourKitConfig extends Yaml {

	/**
	 * Create parkour-kits.yml config.
	 * @param path path
	 */
	public ParkourKitConfig(File path) {
		super(path);

		if (!this.contains(ParkourConstants.DEFAULT)) {
			createStandardKit(ParkourConstants.DEFAULT);
		}
	}

	/**
	 * Get all available ParkourKit names.
	 * @return parkour kit names
	 */
	public Set<String> getAllParkourKitNames() {
		return this.singleLayerKeySet();
	}

	/**
	 * Check if ParkourKit exists.
	 * @param kitName parkour kit name
	 * @return parkour kit exists
	 */
	public boolean doesParkourKitExist(String kitName) {
		return kitName != null && getAllParkourKitNames().contains(kitName.toLowerCase());
	}

	public boolean doesMaterialExistInParkourKit(String kitName, Material material) {
		return doesMaterialExistInParkourKit(kitName, material.name());
	}

	public boolean doesMaterialExistInParkourKit(String kitName, String materialName) {
		return this.contains(kitName.toLowerCase() + "." + materialName.toUpperCase());
	}

	/**
	 * Get the Material names for the ParkourKit.
	 * @param kitName parkour kit name
	 * @return material names for kit
	 */
	public Set<String> getParkourKitMaterials(String kitName) {
		return this.getSection(kitName.toLowerCase()).singleLayerKeySet();
	}

	/**
	 * Get the ActionType name for Material for the ParkourKit.
	 * @param kitName parkour kit name
	 * @param material material name
	 * @return matching action type name
	 */
	public String getActionTypeForMaterial(String kitName, String material) {
		return this.getString(kitName.toLowerCase() + "." + material.toUpperCase() + ".Action");
	}

	/**
	 * Get the PotionEffectType name for Material for the ParkourKit.
	 * @param kitName parkour kit name
	 * @param material material name
	 * @return matching action type name
	 */
	public String getEffectTypeForMaterial(String kitName, String material) {
		return this.getString(kitName.toLowerCase() + "." + material.toUpperCase() + ".Effect");
	}

	/**
	 * Get Parkour Courses linked to the ParkourKit.
	 * @param kitName parkour kit name
	 * @return List Parkour course names
	 */
	public List<String> getDependentCourses(String kitName) {
		List<String> dependentCourses = new ArrayList<>();
		for (String courseName : Parkour.getInstance().getCourseManager().getCourseNames()) {
			String linkedKitName = Parkour.getInstance().getConfigManager().getCourseConfig(courseName)
					.getParkourKit();
			if (kitName.equals(linkedKitName)) {
				dependentCourses.add(courseName);
			}
		}
		return dependentCourses;
	}

	/**
	 * Delete the ParkourKit and all associated data.
	 * @param kitName parkour kit name
	 */
	public void deleteKit(String kitName) {
		this.remove(kitName.toLowerCase());
		Parkour.getInstance().getParkourKitManager().clearCache(kitName);
	}

	public void removeMaterial(String kitName, String material) {
		this.remove(kitName.toLowerCase() + "." + material.toUpperCase());
	}

	/**
	 * Add Material to ParkourKit.
	 *
	 * @param kitName parkour kit name
	 * @param materialName material name
	 * @param action kit action
	 * @param strength action strength
	 * @param duration action duration
	 * @param potion potion effect
	 */
	public void addMaterialToParkourKit(@NotNull String kitName,
	                                    @NotNull String materialName,
	                                    @NotNull String action,
	                                    @Nullable String strength,
	                                    @Nullable String duration,
	                                    @Nullable String potion) {

		String pathPrefix = kitName.toLowerCase() + "." + materialName.toUpperCase() + ".";
		this.set(pathPrefix + "Action", action);
		if (strength != null) {
			this.set(pathPrefix + "Strength", strength);
		}
		if (duration != null) {
			this.set(pathPrefix + "Duration", duration);
		}
		if (potion != null) {
			this.set(pathPrefix + "Effect", potion);
		}
	}


	/**
	 * Create standard ParkourKit.
	 * @param name of ParkourKit
	 */
	public void createStandardKit(String name) {
		Material matching = MaterialUtils.lookupMaterial("SMOOTH_BRICK");
		this.set(name + "." + matching.name() + ".Action", "death");
		matching = MaterialUtils.lookupMaterial("BRICKS");
		this.set(name + "." + matching.name() + ".Action", "climb");
		this.set(name + "." + matching.name() + ".Strength", 0.4);
		matching = MaterialUtils.lookupMaterial("EMERALD_BLOCK");
		this.set(name + "." + matching.name() + ".Action", "launch");
		this.set(name + "." + matching.name() + ".Strength", 1.2);
		matching = MaterialUtils.lookupMaterial("MOSSY_COBBLESTONE");
		this.set(name + "." + matching.name() + ".Action", "bounce");
		this.set(name + "." + matching.name() + ".Strength", (double) 5);
		this.set(name + "." + matching.name() + ".Duration", 200);
		matching = MaterialUtils.lookupMaterial("OBSIDIAN");
		this.set(name + "." + matching.name() + ".Action", "speed");
		this.set(name + "." + matching.name() + ".Strength", (double) 5);
		this.set(name + "." + matching.name() + ".Duration", 200);
		matching = MaterialUtils.lookupMaterial("ENDER_STONE");
		this.set(name + "." + matching.name() + ".Action", "repulse");
		this.set(name + "." + matching.name() + ".Strength", 0.4);
		matching = MaterialUtils.lookupMaterial("GOLD_BLOCK");
		this.set(name + "." + matching.name() + ".Action", "norun");

		if (PluginUtils.getMinorServerVersion() <= 12) {
			this.set(name + ".HUGE_MUSHROOM_2.Action", "finish");
			this.set(name + ".HUGE_MUSHROOM_1.Action", "nopotion");

		} else {
			matching = MaterialUtils.lookupMaterial("RED_MUSHROOM_BLOCK");
			this.set(name + "." + matching.name() + ".Action", "finish");
			matching = MaterialUtils.lookupMaterial("BROWN_MUSHROOM_BLOCK");
			this.set(name + "." + matching.name() + ".Action", "nopotion");
		}
	}
}

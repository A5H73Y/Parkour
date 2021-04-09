package io.github.a5h73y.parkour.configuration.impl;

import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.other.ParkourConstants;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.PluginUtils;
import org.bukkit.Material;

public class ParkourKitConfig extends ParkourConfiguration {
	
	public static final String PARKOUR_KIT_CONFIG_PREFIX = "ParkourKit.";

	@Override
	protected String getFileName() {
		return "parkourkit.yml";
	}

	@Override
	protected void initializeConfig() {
		if (!this.contains("ParkourKit.default")) {
			createStandardKit(ParkourConstants.DEFAULT);
		}
	}

	/**
	 * Create standard ParkourKit.
	 * @param name of ParkourKit
	 */
	public void createStandardKit(String name) {
		Material matching = MaterialUtils.lookupMaterial("SMOOTH_BRICK");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Action", "death");
		matching = MaterialUtils.lookupMaterial("BRICKS");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Action", "climb");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Strength", 0.4);
		matching = MaterialUtils.lookupMaterial("EMERALD_BLOCK");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Action", "launch");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Strength", 1.2);
		matching = MaterialUtils.lookupMaterial("MOSSY_COBBLESTONE");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Action", "bounce");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Strength", (double) 5);
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Duration", 200);
		matching = MaterialUtils.lookupMaterial("OBSIDIAN");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Action", "speed");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Strength", (double) 5);
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Duration", 200);
		matching = MaterialUtils.lookupMaterial("ENDER_STONE");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Action", "repulse");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Strength", 0.4);
		matching = MaterialUtils.lookupMaterial("GOLD_BLOCK");
		this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Action", "norun");

		if (PluginUtils.getMinorServerVersion() <= 12) {
			this.set(PARKOUR_KIT_CONFIG_PREFIX + name + ".HUGE_MUSHROOM_2.Action", "finish");
			this.set(PARKOUR_KIT_CONFIG_PREFIX + name + ".HUGE_MUSHROOM_1.Action", "nopotion");

		} else {
			matching = MaterialUtils.lookupMaterial("RED_MUSHROOM_BLOCK");
			this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Action", "finish");
			matching = MaterialUtils.lookupMaterial("BROWN_MUSHROOM_BLOCK");
			this.set(PARKOUR_KIT_CONFIG_PREFIX + name + "." + matching.name() + ".Action", "nopotion");
		}
	}
}

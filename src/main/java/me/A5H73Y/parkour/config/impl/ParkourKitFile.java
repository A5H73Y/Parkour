package me.A5H73Y.parkour.config.impl;

import java.io.IOException;

import me.A5H73Y.parkour.config.ParkourConfiguration;
import me.A5H73Y.parkour.other.Constants;
import me.A5H73Y.parkour.utilities.Utils;
import org.bukkit.Material;

public class ParkourKitFile extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "parkourkit.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {
		if (!this.contains("ParkourKit.default")) {
			createStandardKit(Constants.DEFAULT);
		}
		this.save(file);
	}

	/**
	 * Create standard ParkourKit.
	 * @param name of ParkourKit
	 */
	public void createStandardKit(String name) {
		//TODO is there a better way to do this, because it's nasty
		Material matching = Utils.lookupMaterial("SMOOTH_BRICK");
		this.set("ParkourKit." + name + "." + matching.name() + ".Action", "death");
		matching = Utils.lookupMaterial("BRICKS");
		this.set("ParkourKit." + name + "." + matching.name() + ".Action", "climb");
		this.set("ParkourKit." + name + "." + matching.name() + ".Strength", 0.4);
		matching = Utils.lookupMaterial("EMERALD_BLOCK");
		this.set("ParkourKit." + name + "." + matching.name() + ".Action", "launch");
		this.set("ParkourKit." + name + "." + matching.name() + ".Strength", 1.2);
		matching = Utils.lookupMaterial("MOSSY_COBBLESTONE");
		this.set("ParkourKit." + name + "." + matching.name() + ".Action", "bounce");
		this.set("ParkourKit." + name + "." + matching.name() + ".Strength", (double) 5);
		this.set("ParkourKit." + name + "." + matching.name() + ".Duration", 200);
		matching = Utils.lookupMaterial("OBSIDIAN");
		this.set("ParkourKit." + name + "." + matching.name() + ".Action", "speed");
		this.set("ParkourKit." + name + "." + matching.name() + ".Strength", (double) 5);
		this.set("ParkourKit." + name + "." + matching.name() + ".Duration", 200);
		matching = Utils.lookupMaterial("ENDER_STONE");
		this.set("ParkourKit." + name + "." + matching.name() + ".Action", "repulse");
		this.set("ParkourKit." + name + "." + matching.name() + ".Strength", 0.4);
		matching = Utils.lookupMaterial("GOLD_BLOCK");
		this.set("ParkourKit." + name + "." + matching.name() + ".Action", "norun");

		if (Utils.getMinorServerVersion() <= 12) {
			this.set("ParkourKit." + name + ".HUGE_MUSHROOM_2.Action", "finish");
			this.set("ParkourKit." + name + ".HUGE_MUSHROOM_1.Action", "nopotion");
		} else {
			matching = Utils.lookupMaterial("RED_MUSHROOM_BLOCK");
			this.set("ParkourKit." + name + "." + matching.name() + ".Action", "finish");
			matching = Utils.lookupMaterial("BROWN_MUSHROOM_BLOCK");
			this.set("ParkourKit." + name + "." + matching.name() + ".Action", "nopotion");
		}
	}
}

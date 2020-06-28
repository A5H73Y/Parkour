package io.github.a5h73y.parkour.config.impl;

import java.io.IOException;

import io.github.a5h73y.parkour.config.ParkourConfiguration;

public class InventoryFile extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "inventory.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {

	}
}

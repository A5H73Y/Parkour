package io.github.a5h73y.config.impl;

import java.io.IOException;

import io.github.a5h73y.config.ParkourConfiguration;

public class InventoryFile extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "inventory.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {

	}
}

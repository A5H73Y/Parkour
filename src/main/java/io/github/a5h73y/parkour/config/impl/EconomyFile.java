package io.github.a5h73y.parkour.config.impl;

import java.io.IOException;

import io.github.a5h73y.parkour.config.ParkourConfiguration;

public class EconomyFile extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "economy.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {

	}
}

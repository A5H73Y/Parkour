package io.github.a5h73y.parkour.configuration.impl;

import java.io.IOException;

import io.github.a5h73y.parkour.configuration.ParkourConfiguration;

public class EconomyConfig extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "economy.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {

	}
}

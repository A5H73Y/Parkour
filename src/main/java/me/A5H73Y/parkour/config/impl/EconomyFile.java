package me.A5H73Y.parkour.config.impl;

import java.io.IOException;

import me.A5H73Y.parkour.config.ParkourConfiguration;

public class EconomyFile extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "economy.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {

	}
}

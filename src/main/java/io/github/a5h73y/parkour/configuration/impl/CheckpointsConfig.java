package io.github.a5h73y.parkour.configuration.impl;

import java.io.IOException;

import io.github.a5h73y.parkour.configuration.ParkourConfiguration;

public class CheckpointsConfig extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "checkpoints.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {

	}
}

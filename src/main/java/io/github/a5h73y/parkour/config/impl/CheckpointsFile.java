package io.github.a5h73y.parkour.config.impl;

import java.io.IOException;

import io.github.a5h73y.parkour.config.ParkourConfiguration;

public class CheckpointsFile extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "checkpoints.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {

	}
}

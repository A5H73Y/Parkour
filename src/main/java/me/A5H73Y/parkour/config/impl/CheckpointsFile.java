package me.A5H73Y.parkour.config.impl;

import java.io.IOException;

import me.A5H73Y.parkour.config.ParkourConfiguration;

public class CheckpointsFile extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "checkpoints.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {

	}
}

package io.github.a5h73y.parkour.configuration.impl;

import io.github.a5h73y.parkour.configuration.ParkourConfiguration;

public class PlayersConfig extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "players.yml";
	}

	@Override
	protected void initializeConfig() {

	}
}

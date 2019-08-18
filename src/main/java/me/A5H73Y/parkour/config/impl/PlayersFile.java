package me.A5H73Y.parkour.config.impl;

import java.io.IOException;

import me.A5H73Y.parkour.config.ParkourConfiguration;

public class PlayersFile extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "players.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {

	}
}

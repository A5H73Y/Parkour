package me.A5H73Y.parkour.config.impl;

import java.io.IOException;
import java.util.ArrayList;

import me.A5H73Y.parkour.config.ParkourConfiguration;

public class CoursesFile extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "courses.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {
		this.addDefault("Courses", new ArrayList<String>());
		this.options().copyDefaults(true);
		this.save(file);
	}
}

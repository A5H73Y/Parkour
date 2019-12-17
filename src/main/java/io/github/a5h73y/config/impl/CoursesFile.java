package io.github.a5h73y.config.impl;

import java.io.IOException;
import java.util.ArrayList;

import io.github.a5h73y.config.ParkourConfiguration;

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

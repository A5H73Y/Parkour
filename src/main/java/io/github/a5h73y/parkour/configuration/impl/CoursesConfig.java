package io.github.a5h73y.parkour.configuration.impl;

import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import java.util.ArrayList;

public class CoursesConfig extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "courses.yml";
	}

	@Override
	protected void initializeConfig() {
		this.addDefault("Courses", new ArrayList<String>());
		this.options().copyDefaults(true);
	}
}

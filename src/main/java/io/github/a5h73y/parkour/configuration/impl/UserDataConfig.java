package io.github.a5h73y.parkour.configuration.impl;

import java.util.UUID;

import io.github.a5h73y.parkour.configuration.ParkourConfiguration;

public class UserDataConfig extends ParkourConfiguration {

	
	private final UUID userUniqueId;
	private long loadTime = System.currentTimeMillis();
	
	
	public UserDataConfig(UUID uuid) {
		this.userUniqueId = uuid;
	}
	
	
	public long getUpdateTime() {
		return this.loadTime;
	}
	
	
	public long bumpUpdateTime() {
		return this.loadTime = System.currentTimeMillis();
	}
	
	
	/*
	 * Keeps last update time up to date.
	 */
	@Override
	public void set(String path, Object value) {
		super.set(path, value);
		this.bumpUpdateTime();
	}
	
	
	@Override
	public String getFileName() {
		return this.userUniqueId + ".yml";
	}

	@Override
	protected void initializeConfig() {
		
	}

}

package io.github.a5h73y.parkour.type;

import io.github.a5h73y.parkour.Parkour;

public abstract class CacheableParkourManager extends ParkourManager implements Cacheable, Teardownable {

	protected CacheableParkourManager(Parkour parkour) {
		super(parkour);
	}

	public void teardown() {

	}
}

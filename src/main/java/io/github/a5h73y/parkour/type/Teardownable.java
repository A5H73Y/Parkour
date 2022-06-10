package io.github.a5h73y.parkour.type;

/**
 * Cacheable interface.
 * Allows a manager to tear itself down, ready for server shut down.
 */
@FunctionalInterface
public interface Teardownable {
	
	void teardown();

}

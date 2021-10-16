package io.github.a5h73y.parkour.type;

/**
 * Cacheable interface.
 * Allows Managers to declare they cache a certain type of Entity.
 */
@FunctionalInterface
public interface Teardownable {
	
	void teardown();

}

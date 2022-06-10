package io.github.a5h73y.parkour.type;

/**
 * Cacheable interface.
 * Allows Managers to declare they cache a certain type of Entity.
 */
public interface Cacheable {

	/**
	 * Get the number of entries in the Cache.
	 * @return cache size
	 */
	int getCacheSize();

	/**
	 * Clear the contents of the Cache.
	 */
	void clearCache();

}

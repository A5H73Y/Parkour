package io.github.a5h73y.parkour.type;

public interface Cacheable<E> {

	int getCacheSize();

	void clearCache();

}

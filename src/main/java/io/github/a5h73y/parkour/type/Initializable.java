package io.github.a5h73y.parkour.type;

/**
 * Initializable interface.
 * Allows a manager to initialize itself, when all other dependency manager are ready.
 */
public interface Initializable {

	int getInitializeSequence();
	
	void initialize();

}

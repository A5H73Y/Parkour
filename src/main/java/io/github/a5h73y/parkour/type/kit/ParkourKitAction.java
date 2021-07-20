package io.github.a5h73y.parkour.type.kit;

import io.github.a5h73y.parkour.enums.ActionType;
import java.io.Serializable;

/**
 * ParkourKitAction.
 * Each Material in the ParkourKit must have an associated Action to trigger.
 * Some Actions may require a strength and a duration.
 */
public class ParkourKitAction implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ActionType actionType;
	private final double strength;
	private final int duration;
	private final String effect;

	/**
	 * Construct a ParkourKitAction from the details.
	 * The strength and duration are optional based on the type of Action.
	 *
	 * @param actionType associated action type
	 * @param strength action strength
	 * @param duration action duration
	 * @param effect action potion effect type
	 */
	public ParkourKitAction(ActionType actionType, double strength, int duration, String effect) {
		this.actionType = actionType;
		this.strength = strength;
		this.duration = duration;
		this.effect = effect;
	}

	/**
	 * Get the associated {@link ActionType}.
	 * @return action type
	 */
	public ActionType getActionType() {
		return actionType;
	}

	/**
	 * Get the Action Strength.
	 * @return strength
	 */
	public double getStrength() {
		return strength;
	}

	/**
	 * Get the Action duration.
	 * @return duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Get the Potion effect.
	 * @return effect
	 */
	public String getEffect() {
		return effect;
	}
}

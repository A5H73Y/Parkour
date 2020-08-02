package io.github.a5h73y.parkour.type.kit;

import io.github.a5h73y.parkour.enums.ActionType;

public class ParkourKitAction {

	private final ActionType actionType;
	private final double strength;
	private final int duration;

	public ParkourKitAction(ActionType actionType, double strength, int duration) {
		this.actionType = actionType;
		this.strength = strength;
		this.duration = duration;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public double getStrength() {
		return strength;
	}

	public int getDuration() {
		return duration;
	}
}

package io.github.a5h73y.parkour.type.course;

import java.io.Serializable;
import java.util.Map;

public class CourseSettings implements Serializable {

	private int maxDeaths;
	private int maxTime;
	private int maxFallTicks;
	private boolean manualCheckpoints;
	private boolean dieInLiquid;
	private boolean dieInVoid;

	/**
	 * Determine if the Course has a configured maximum deaths.
	 * @return has maximum deaths set
	 */
	public boolean hasMaxDeaths() {
		return maxDeaths > 0;
	}

	/**
	 * Get Course's maximum deaths.
	 * Maximum number of deaths a player can accumulate before failing the Course.
	 * @return maximum deaths for course
	 */
	public int getMaxDeaths() {
		return maxDeaths;
	}

	public void setMaxDeaths(int maxDeaths) {
		this.maxDeaths = maxDeaths;
	}

	/**
	 * Determine if the Course has a configured maximum time limit.
	 * @return has maximum time set
	 */
	public boolean hasMaxTime() {
		return maxTime > 0;
	}

	/**
	 * Get Course's maximum time.
	 * Maximum number of seconds a player can accumulate before failing the Course.
	 * @return maximum time in seconds
	 */
	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public boolean hasMaxFallTicks() {
		return this.maxFallTicks > 0;
	}

	public int getMaxFallTicks() {
		return maxFallTicks;
	}

	public void setMaxFallTicks(int maxFallTicks) {
		this.maxFallTicks = maxFallTicks;
	}

	public boolean isManualCheckpoints() {
		return manualCheckpoints;
	}

	public void setManualCheckpoints(boolean manualCheckpoints) {
		this.manualCheckpoints = manualCheckpoints;
	}

	public boolean isDieInLiquid() {
		return dieInLiquid;
	}

	public void setDieInLiquid(boolean dieInLiquid) {
		this.dieInLiquid = dieInLiquid;
	}

	public boolean isDieInVoid() {
		return dieInVoid;
	}

	public void setDieInVoid(boolean dieInVoid) {
		this.dieInVoid = dieInVoid;
	}
}

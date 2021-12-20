package io.github.a5h73y.parkour.type.course;

import java.io.Serializable;
import java.util.Map;

public class CourseSettings implements Serializable {

	private final int maxDeaths;
	private final int maxTime;
	private final int maxFallTicks;

	public CourseSettings(int maxDeaths, int maxTime, int maxFallTicks) {
		this.maxDeaths = maxDeaths;
		this.maxTime = maxTime;
		this.maxFallTicks = maxFallTicks;
	}

	public static CourseSettings deserialize(Map<String, Object> input) {
		int maxDeaths = (int) input.getOrDefault("MaxDeaths", 0);
		int maxTime = (int) input.getOrDefault("MaxTime", 0);
		int maxFallTicks = (int) input.getOrDefault("MaxFallTicks", 80);

		return new CourseSettings(maxDeaths, maxTime, maxFallTicks);
	}

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

	public int getMaxFallTicks() {
		return maxFallTicks;
	}
}

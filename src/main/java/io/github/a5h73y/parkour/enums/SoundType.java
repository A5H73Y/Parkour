package io.github.a5h73y.parkour.enums;

public enum SoundType {

	JOIN_COURSE("JoinCourse"),
	SECOND_INCREMENT("SecondIncrement"),
	SECOND_DECREMENT("SecondDecrement"),
	PLAYER_DEATH("PlayerDeath"),
	CHECKPOINT_ACHIEVED("CheckpointAchieved"),
	COURSE_FINISHED("CourseFinished"),
	COURSE_FAILED("CourseFailed");

	private final String configEntry;

	SoundType(String configEntry) {
		this.configEntry = configEntry;
	}

	public String getConfigEntry() {
		return this.configEntry;
	}
}

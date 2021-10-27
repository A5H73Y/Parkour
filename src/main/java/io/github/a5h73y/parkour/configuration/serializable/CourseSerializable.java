package io.github.a5h73y.parkour.configuration.serializable;

import static io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable.getMapValue;

import io.github.a5h73y.parkour.type.course.Course;
import java.util.Map;
import de.leonhard.storage.internal.serialize.LightningSerializable;
import org.jetbrains.annotations.NotNull;

public class CourseSerializable implements LightningSerializable<Course> {

	@Override
	public Map<String, Object> serialize(@NotNull Course course) throws ClassCastException {
		return course.serialize();
	}

	@Override
	public Course deserialize(@NotNull Object input) throws ClassCastException {
		Course course = null;
		if (input instanceof Map) {
			course = Course.deserialize(getMapValue(input));
		}
		return course;
	}

	@Override
	public Class<Course> getClazz() {
		return Course.class;
	}
}

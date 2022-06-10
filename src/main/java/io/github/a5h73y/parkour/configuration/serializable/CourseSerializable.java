package io.github.a5h73y.parkour.configuration.serializable;

import static io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable.getMapValue;

import de.leonhard.storage.internal.serialize.SimplixSerializable;
import io.github.a5h73y.parkour.type.course.Course;
import java.util.Collections;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class CourseSerializable implements SimplixSerializable<Course> {

	@Override
	public Map<String, Object> serialize(@NotNull Course course) throws ClassCastException {
		// we don't create a whole Course then serialize it
		// we create it in stages and only really care about deserializing it
		// we could change the way a Course is created, setting the first checkpoint then serialize it
		return Collections.emptyMap();
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

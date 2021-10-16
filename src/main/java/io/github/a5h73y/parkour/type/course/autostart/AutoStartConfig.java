package io.github.a5h73y.parkour.type.course.autostart;

import java.io.File;
import de.leonhard.storage.Yaml;

/**
 * AutoStart Information Utility class.
 * Convenience methods for accessing autostart configuration file.
 */
public class AutoStartConfig extends Yaml {

    public AutoStartConfig(File file) {
        super(file);
    }

    public boolean doesAutoStartExist(String coordinates) {
        return this.contains(coordinates);
    }

    public String getAutoStartCourse(String coordinates) {
        return this.get(coordinates, null);
    }

    public void setAutoStartCourse(String coordinates, String courseName) {
        this.set(coordinates, courseName.toLowerCase());
    }

    public void deleteAutoStart(String coordinates) {
        this.set(coordinates, null);
    }
}

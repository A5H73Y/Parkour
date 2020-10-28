package io.github.a5h73y.parkour.type.kit;

import static io.github.a5h73y.parkour.enums.ConfigType.PARKOURKIT;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;

public class ParkourKitInfo {

    /**
     * Get a list of possible ParkourKit
     *
     * @return Set<String> of ParkourKit names
     */
    public static Set<String> getAllParkourKitNames() {
        ConfigurationSection section = Parkour.getConfig(PARKOURKIT).getConfigurationSection("ParkourKit");
        return section != null ? section.getKeys(false) : new HashSet<>();
    }

    public static boolean doesParkourKitExist(String kitName) {
        return getAllParkourKitNames().contains(kitName.toLowerCase());
    }

    public static Set<String> getParkourKitContents(String kitName) {
        ConfigurationSection section = Parkour.getConfig(PARKOURKIT).getConfigurationSection("ParkourKit." + kitName.toLowerCase());
        return section != null ? section.getKeys(false) : new HashSet<>();
    }

    public static String getActionTypeForMaterial(String kitName, String material) {
        return Parkour.getConfig(PARKOURKIT).getString("ParkourKit." + kitName + "." + material + ".Action");
    }

    /**
     * Get list of Parkour courses linked to a Parkour kit.
     *
     * @param parkourKitName name of ParkourKit
     * @return List Parkour course names
     */
    public static List<String> getDependentCourses(String parkourKitName) {
        List<String> dependentCourses = new ArrayList<>();
        for (String courseName : CourseInfo.getAllCourseNames()) {
            String linkedKitName = CourseInfo.getParkourKit(courseName);
            if (parkourKitName.equals(linkedKitName)) {
                dependentCourses.add(courseName);
            }
        }
        return dependentCourses;
    }
}

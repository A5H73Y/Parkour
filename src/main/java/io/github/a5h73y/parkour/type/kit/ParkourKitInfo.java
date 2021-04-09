package io.github.a5h73y.parkour.type.kit;

import static io.github.a5h73y.parkour.configuration.impl.ParkourKitConfig.PARKOUR_KIT_CONFIG_PREFIX;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;

/**
 * ParkourKit Information Utility class.
 * Convenience methods for accessing parkourkit configuration file.
 */
public class ParkourKitInfo {

    /**
     * Get all available ParkourKit names.
     * @return parkour kit names
     */
    public static Set<String> getAllParkourKitNames() {
        ConfigurationSection section = getParkourKitConfig().getConfigurationSection("ParkourKit");
        return section != null ? section.getKeys(false) : new HashSet<>();
    }

    /**
     * Check if ParkourKit exists.
     * @param kitName parkour kit name
     * @return parkour kit exists
     */
    public static boolean doesParkourKitExist(String kitName) {
        return getAllParkourKitNames().contains(kitName.toLowerCase());
    }

    /**
     * Get the Material names for the ParkourKit.
     * @param kitName parkour kit name
     * @return material names for kit
     */
    public static Set<String> getParkourKitMaterials(String kitName) {
        ConfigurationSection section =
                getParkourKitConfig().getConfigurationSection(PARKOUR_KIT_CONFIG_PREFIX + kitName.toLowerCase());
        return section != null ? section.getKeys(false) : new HashSet<>();
    }

    /**
     * Get the ActionType name for Material for the ParkourKit.
     * @param kitName parkour kit name
     * @param material material name
     * @return matching action type name
     */
    public static String getActionTypeForMaterial(String kitName, String material) {
        return getParkourKitConfig().getString(PARKOUR_KIT_CONFIG_PREFIX + kitName.toLowerCase()
                + "." + material.toUpperCase() + ".Action");
    }

    /**
     * Get Parkour Courses linked to the ParkourKit.
     * @param kitName parkour kit name
     * @return List Parkour course names
     */
    public static List<String> getDependentCourses(String kitName) {
        List<String> dependentCourses = new ArrayList<>();
        for (String courseName : CourseInfo.getAllCourseNames()) {
            String linkedKitName = CourseInfo.getParkourKit(courseName);
            if (kitName.equals(linkedKitName)) {
                dependentCourses.add(courseName);
            }
        }
        return dependentCourses;
    }

    /**
     * Delete the ParkourKit and all associated data.
     * @param kitName parkour kit name
     */
    public static void deleteKit(String kitName) {
        getParkourKitConfig().set(PARKOUR_KIT_CONFIG_PREFIX + kitName.toLowerCase(), null);
        getParkourKitConfig().save();
        Parkour.getInstance().getParkourKitManager().clearCache(kitName);
    }

    /**
     * Get the ParkourKit {@link ParkourConfiguration}.
     * @return the parkourkit.yml configuration
     */
    private static ParkourConfiguration getParkourKitConfig() {
        return Parkour.getConfig(ConfigType.PARKOURKIT);
    }

    private ParkourKitInfo() {
        throw new IllegalStateException("Utility class");
    }
}

package io.github.a5h73y.parkour.type.player.completion;

import io.github.a5h73y.parkour.Parkour;
import java.io.File;
import java.util.List;
import de.leonhard.storage.Yaml;
import org.bukkit.OfflinePlayer;

public class CourseCompletionConfig extends Yaml {

    public CourseCompletionConfig(File file) {
        super(file);
    }

    private String getPlayerKey(OfflinePlayer player) {
        return (Parkour.getDefaultConfig().getBoolean("Other.PlayerConfigUsePlayerUUID")
                ? player.getUniqueId().toString() : player.getName());
    }

    /**
     * Get the number of Courses completed by Player.
     * @return number of courses completed
     */
    public int getNumberOfCompletedCourses(OfflinePlayer player) {
        return getCompletedCourses(player).size();
    }

    /**
     * Get the Completed Course names for Player.
     * @return completed course names
     */
    public List<String> getCompletedCourses(OfflinePlayer player) {
        return this.getStringList(getPlayerKey(player));
    }

    public boolean hasCompletedCourse(OfflinePlayer player, String name) {
        return getCompletedCourses(player).contains(name.toLowerCase());
    }

    /**
     * Add a Course name to the Player's completions.
     * @param courseName completed course name
     */
    public void addCompletedCourse(OfflinePlayer player, String courseName) {
        List<String> completedCourses = getCompletedCourses(player);

        if (!completedCourses.contains(courseName)) {
            completedCourses.add(courseName);
            this.set(getPlayerKey(player), completedCourses);
        }
    }

    /**
     * Remove a Course from each Player's completed courses.
     * When a Course gets deleted, it must be removed from each player's completed courses.
     * @param courseName course name
     */
    public void removeCompletedCourse(String courseName) {
        for (String playerKey : this.singleLayerKeySet()) {
            List<String> completedCourses = this.getStringList(playerKey);

            if (completedCourses.contains(courseName)) {
                completedCourses.remove(courseName);
                this.set(playerKey, completedCourses);
            }
        }
    }
}

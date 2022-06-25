package io.github.a5h73y.parkour.type.player.completion;

import de.leonhard.storage.Yaml;
import io.github.a5h73y.parkour.Parkour;
import java.io.File;
import java.util.List;
import org.bukkit.OfflinePlayer;

public class CourseCompletionConfig extends Yaml {

    public CourseCompletionConfig(File file) {
        super(file);
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
        return this.getStringList(Parkour.getDefaultConfig().getPlayerConfigName(player));
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
            this.set(Parkour.getDefaultConfig().getPlayerConfigName(player), completedCourses);
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

    /**
     * Remove the Player's completed courses.
     * @param targetPlayer target player
     */
    public void removePlayer(OfflinePlayer targetPlayer) {
        this.remove(Parkour.getDefaultConfig().getPlayerConfigName(targetPlayer));
    }
}

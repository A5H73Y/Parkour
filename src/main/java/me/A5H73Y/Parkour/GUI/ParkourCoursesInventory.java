package me.A5H73Y.Parkour.GUI;

import me.A5H73Y.Parkour.Course.CourseInfo;

import java.util.List;

public class ParkourCoursesInventory extends InventoryBuilder {

    @Override
    public List<String> getCourses() {
        return CourseInfo.getAllCourses();
    }

}
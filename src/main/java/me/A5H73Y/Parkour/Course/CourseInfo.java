package me.A5H73Y.Parkour.Course;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import org.bukkit.Material;

public class CourseInfo {

    /**
     * Return the linked course of the specified course
     * Will validate it's a valid course before returning
     * @param courseName
     * @return linkedCourse
     */
    public static String getLinkedCourse(String courseName) {
        String linkedCourse = Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".LinkedCourse");

        if (linkedCourse != null && CourseMethods.exist(linkedCourse))
            return linkedCourse;

        return null;
    }

    public static boolean hasLinkedCourse(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".LinkedCourse");
    }

    public static void setLinkedCourse(String courseName, String linkedCourse) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".LinkedCourse", linkedCourse.toLowerCase());
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Return the linked course of the specified lobby
     * Will validate it's a valid lobby before returning
     * @param courseName
     * @return linkedLobby
     */
    public static String getLinkedLobby(String courseName) {
        String linkedLobby = Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".LinkedLobby");

        if (linkedLobby != null && Static.getLobbyList().contains(linkedLobby))
            return linkedLobby;

        return null;
    }

    public static boolean hasLinkedLobby(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".LinkedLobby");
    }

    public static void setLinkedLobby(String courseName, String lobbyName) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".LinkedLobby", lobbyName);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Get the Mode for the course
     * @param courseName
     * @return mode
     */
    public static String getMode(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".Mode");
    }

    public static void setMode(String courseName, String mode) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Mode", mode);
        Parkour.getParkourConfig().saveCourses();
    }

    public static int getCheckpointAmount(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Points");
    }

    public static String getCreator(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".Creator");
    }

    public static void setCreator(String courseName, String playerName) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Creator", playerName);
        Parkour.getParkourConfig().saveCourses();
    }

    public static int getMinimumLevel(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".MinimumLevel");
    }

    public static void setMinimumLevel(String courseName, String level) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".MinimumLevel", Integer.parseInt(level));
        Parkour.getParkourConfig().saveCourses();
    }

    public static String getParkourKit(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".ParkourKit");
    }

    public static boolean hasParkourKit(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".ParkourKit");
    }

    public static void setParkourKit(String courseName, String parkourKitName) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".ParkourKit", parkourKitName.toLowerCase());
        Parkour.getParkourConfig().saveCourses();
    }

    public static int getMaximumDeaths(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".MaxDeaths", 0);
    }

    public static void setMaximumDeaths(String courseName, String amount) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".MaxDeaths", Integer.parseInt(amount));
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Returns whether the course is ready or not.
     *
     * @param courseName
     * @return boolean
     */
    public static boolean getFinished(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getBoolean(courseName.toLowerCase() + ".Finished");
    }

    public static void setFinished(String courseName, boolean finished) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Finished", finished);
        Parkour.getParkourConfig().saveCourses();
    }

    public static String getCommandPrize(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".Prize.CMD");
    }

    public static boolean hasCommandPrize(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".Prize.CMD");
    }

    public static void setCommandPrize(String courseName, String command) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Prize.CMD", command);
        Parkour.getParkourConfig().saveCourses();
    }

    public static void setMaterialPrize(String courseName, String material, String amount) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Prize.Material", material);
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Prize.Amount", Integer.parseInt(amount));
        Parkour.getParkourConfig().saveCourses();
    }

    public static int getXPPrize(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Prize.XP");
    }

    public static void setXPPrize(String courseName, String amount) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Prize.XP", Integer.parseInt(amount));
        Parkour.getParkourConfig().saveCourses();
    }



    /**
     * Increase the Complete count of the course
     *
     * @param courseName
     */
    public static void increaseComplete(String courseName) {
        int completed = Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Completed");
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Completed", completed + 1);
        Parkour.getParkourConfig().saveCourses();
    }

    /**
     * Increase the amount of views of the course
     *
     * @param courseName
     */
    public static void increaseView(String courseName) {
        int views = Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Views");
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Views", views + 1);
        Parkour.getParkourConfig().saveCourses();
    }

    public static int getRewardLevel(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Level");
    }

    public static void setRewardLevel(String courseName, String level) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Level", Integer.parseInt(level));
        Parkour.getParkourConfig().saveCourses();
    }

    public static int getRewardLevelAdd(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".LevelAdd");
    }

    public static void setRewardLevelAdd(String courseName, String amount) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".LevelAdd", Integer.parseInt(amount));
        Parkour.getParkourConfig().saveCourses();
    }

    public static boolean getRewardOnce(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getBoolean(courseName.toLowerCase() + ".RewardOnce");
    }

    public static void setRewardOnce(String courseName, boolean enabled) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".RewardOnce", enabled);
        Parkour.getParkourConfig().saveCourses();
    }

    public static String getRewardRank(int level) {
        return Parkour.getParkourConfig().getUsersData().getString("ServerInfo.Levels." + level + ".Rank");
    }

    public static void setRewardRank(String level, String rank) {
        Parkour.getParkourConfig().getUsersData().set("ServerInfo.Levels." + level + ".Rank", rank);
        Parkour.getParkourConfig().saveUsers();
    }

    public static int getRewardParkoins(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Parkoins");
    }

    public static void setRewardParkoins(String courseName, String parkoins) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".Parkoins", Integer.parseInt(parkoins));
        Parkour.getParkourConfig().saveCourses();
    }

    public static Material getJoinItem(String courseName) {
        return Material.getMaterial(Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".JoinItemMaterial"));
    }

    public static int getJoinItemAmount(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".JoinItemAmount", 1);
    }

    public static String getJoinItemLabel(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".JoinItemLabel");
    }

    public static boolean hasJoinItem(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".JoinItemMaterial");
    }

    public static void setJoinItem(String courseName, String material, int amount) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".JoinItemMaterial", material.toUpperCase());
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".JoinItemAmount", amount);
        Parkour.getParkourConfig().saveCourses();
    }


    public static int getEconomyFinishReward(String courseName) {
        return Parkour.getParkourConfig().getEconData().getInt("Price." + courseName.toLowerCase() + ".Finish");
    }

    public static int getEconomyJoiningFee(String courseName) {
        return Parkour.getParkourConfig().getEconData().getInt("Price." + courseName.toLowerCase() + ".JoinFee");
    }

    public static String getWorld(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".World");
    }

    public static Material getMaterialPrize(String courseName) {
        return Material.getMaterial(Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".Prize.Material"));
    }

    public static int getMaterialPrizeAmount(String courseName) {
        return Parkour.getParkourConfig().getCourseData().getInt(courseName.toLowerCase() + ".Prize.Amount", 0);
    }

    public static boolean hasMaterialPrize(String courseName) {
        return Parkour.getParkourConfig().getCourseData().contains(courseName.toLowerCase() + ".Prize.Material");
    }

    public static void resetLinks(String courseName) {
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".LinkedLobby", null);
        Parkour.getParkourConfig().getCourseData().set(courseName.toLowerCase() + ".LinkedCourse", null);
        Parkour.getParkourConfig().saveCourses();
    }

    public static void deleteCourse(String courseName) {
        courseName = courseName.toLowerCase();

        Static.getCourses().remove(courseName);
        Parkour.getParkourConfig().getCourseData().set(courseName, null);
        Parkour.getParkourConfig().getCourseData().set("Courses", Static.getCourses());
        Parkour.getParkourConfig().saveCourses();
        DatabaseMethods.deleteCourseAndReferences(courseName);
    }
}

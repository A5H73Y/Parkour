package me.A5H73Y.parkour.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.A5H73Y.parkour.Parkour;

public final class Static {

    public static final String PLAYING_BIN_PATH = Parkour.getInstance().getDataFolder() + File.separator + "playing.bin";
    private static List<String> hidden = new ArrayList<>();
    private static Map<String, Long> delay = new HashMap<>();
    private static boolean economy = false;
    private static boolean bountifulAPI = false;
    private static boolean placeholderAPI = false;
    private static String parkourString;
    private static String parkourSignString;
    private static Double version;

    public static void initiate() {
        version = Double.parseDouble(Parkour.getInstance().getDescription().getVersion());
        parkourString = Utils.getTranslation("Parkour.Prefix", false);
        parkourSignString = Utils.getTranslation("Parkour.SignHeading", false);
    }

    public static String getParkourString() {
        return parkourString;
    }

    public static String getParkourSignString() {
        return parkourSignString;
    }

    public static Double getVersion() {
        return version;
    }

    public static void enableEconomy() {
        economy = true;
    }

    public static boolean getEconomy() {
        return economy;
    }

    public static void enableBountifulAPI() {
        bountifulAPI = true;
    }

    public static boolean getBountifulAPI() {
        return bountifulAPI;
    }

    public static boolean isPlaceholderAPI() {
        return placeholderAPI;
    }

    public static void enablePlaceholderAPI() {
        Static.placeholderAPI = true;
    }

    public static boolean containsHidden(String playerName) {
        return hidden.contains(playerName);
    }

    public static void addHidden(String playerName) {
        hidden.add(playerName);
    }

    public static void removeHidden(String playerName) {
        hidden.remove(playerName);
    }

    /**
     * Get the current delay map
     *
     * @return
     */
    public static Map<String, Long> getDelay() {
        return delay;
    }

    public static void addWhitelistedCommand(String command) {
        List<String> commands = Parkour.getSettings().getWhitelistedCommands();
        commands.add(command);
        Parkour.getInstance().getConfig().set("OnCourse.EnforceParkourCommands.Whitelist", commands);
        Parkour.getInstance().saveConfig();
    }
}

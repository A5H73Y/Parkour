package me.A5H73Y.Parkour.ParkourKit;

import me.A5H73Y.Parkour.Other.Constants;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParkourKitInfo {

    /**
     * Get a list of possible ParkourKit
     * @return Set<String> of ParkourKit names
     */
    public static Set<String> getParkourKitNames() {
        ConfigurationSection section = getParkourKitData().getConfigurationSection("ParkourKit");
        return section != null ? section.getKeys(false) : new HashSet<>();
    }

    public static boolean doesParkourKitExist(String kitName) {
        return getParkourKitNames().contains(kitName.toLowerCase());
    }

    public static Set<String> getParkourKitContents(String kitName) {
        ConfigurationSection section = getParkourKitData().getConfigurationSection("ParkourKit." + kitName);
        return section != null ? section.getKeys(false) : null;
    }

    public static String getActionForMaterial(String kitName, String material) {
        return getParkourKitData().getString("ParkourKit." + kitName + "." + material + ".Action");
    }

    private static FileConfiguration getParkourKitData() {
        return Parkour.getParkourConfig().getParkourKitData();
    }

    public static void deleteKit(String argument) {
        Parkour.getParkourConfig().getParkourKitData().set("ParkourKit." + argument, null);
        Parkour.getParkourConfig().saveParkourKit();
        ParkourKit.clearMemory(argument);
    }

    /**
     * Validate ParkourKit set
     * Used for identifying what the problem with the ParkourKit is.
     *
     * @param args
     * @param player
     */
    public static void validateParkourKit(String[] args, Player player) {
        String kitName = (args.length == 2 ? args[1].toLowerCase() : Constants.DEFAULT);

        if (!doesParkourKitExist(kitName)) {
            player.sendMessage(Static.getParkourString() + kitName + " ParkourKit does not exist!");
            return;
        }

        String path = "ParkourKit." + kitName;

        List<String> invalidTypes = new ArrayList<>();

        Set<String> materialList = getParkourKitData().getConfigurationSection(path).getKeys(false);

        for (String material : materialList) {
            // try to get the server lookup version first
            if (Material.getMaterial(material) == null) {
                invalidTypes.add("Unknown Material: " + material);

                // try to see if we have a matching legacy version
                Material matching = Utils.lookupMaterial(material);
                if (matching != null)
                    invalidTypes.add(" Could you have meant: " + matching.name() + "?");
            } else {
                String action = Parkour.getParkourConfig().getParkourKitData().getString(path + "." + material + ".Action");
                if (!ParkourKit.getValidActions().contains(action)) {
                    invalidTypes.add("Material: " + material + ", Unknown Action: " + action);
                }
            }
        }

        player.sendMessage(Static.getParkourString() + invalidTypes.size() + " problems with " + ChatColor.AQUA + kitName + ChatColor.WHITE + " found.");
        if (invalidTypes.size() > 0) {
            for (String type : invalidTypes) {
                player.sendMessage(ChatColor.RED + type);
            }
        }
    }

    /**
     * The need to pass config as the plugin has not initialised fully at this stage
     * @param config
     * @param name
     */
    public static void createStandardKit(FileConfiguration config, String name) {
        //TODO is there a better way to do this, because it's nasty
        Material matching = Utils.lookupMaterial("SMOOTH_BRICK");
        config.set("ParkourKit." + name + "." + matching.name() + ".Action", "death");
        matching = Utils.lookupMaterial("BRICKS");
        config.set("ParkourKit." + name + "." + matching.name() + ".Action", "climb");
        config.set("ParkourKit." + name + "." + matching.name() + ".Strength", 0.4);
        matching = Utils.lookupMaterial("EMERALD_BLOCK");
        config.set("ParkourKit." + name + "." + matching.name() + ".Action", "launch");
        config.set("ParkourKit." + name + "." + matching.name() + ".Strength", 1.2);
        matching = Utils.lookupMaterial("MOSSY_COBBLESTONE");
        config.set("ParkourKit." + name + "." + matching.name() + ".Action", "bounce");
        config.set("ParkourKit." + name + "." + matching.name() + ".Strength", (double) 5);
        config.set("ParkourKit." + name + "." + matching.name() + ".Duration", 200);
        matching = Utils.lookupMaterial("OBSIDIAN");
        config.set("ParkourKit." + name + "." + matching.name() + ".Action", "speed");
        config.set("ParkourKit." + name + "." + matching.name() + ".Strength", (double) 5);
        config.set("ParkourKit." + name + "." + matching.name() + ".Duration", 200);
        matching = Utils.lookupMaterial("ENDER_STONE");
        config.set("ParkourKit." + name + "." + matching.name() + ".Action", "repulse");
        config.set("ParkourKit." + name + "." + matching.name() + ".Strength", 0.4);
        matching = Utils.lookupMaterial("GOLD_BLOCK");
        config.set("ParkourKit." + name + "." + matching.name() + ".Action", "norun");

        if (Utils.getMinorServerVersion() <= 12) {
            config.set("ParkourKit." + name + ".HUGE_MUSHROOM_2.Action", "finish");
            config.set("ParkourKit." + name + ".HUGE_MUSHROOM_1.Action", "nopotion");
        } else {
            matching = Utils.lookupMaterial("RED_MUSHROOM_BLOCK");
            config.set("ParkourKit." + name + "." + matching.name() + ".Action", "finish");
            matching = Utils.lookupMaterial("BROWN_MUSHROOM_BLOCK");
            config.set("ParkourKit." + name + "." + matching.name() + ".Action", "nopotion");
        }
    }
}

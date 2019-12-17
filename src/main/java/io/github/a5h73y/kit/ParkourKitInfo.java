package io.github.a5h73y.kit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.a5h73y.Parkour;
import io.github.a5h73y.config.ParkourConfiguration;
import io.github.a5h73y.enums.ConfigType;
import io.github.a5h73y.other.Constants;
import io.github.a5h73y.utilities.Static;
import io.github.a5h73y.utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ParkourKitInfo {

    /**
     * Get a list of possible ParkourKit
     *
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

    public static void deleteKit(String argument) {
        getParkourKitData().set("ParkourKit." + argument, null);
        getParkourKitData().save();
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
                if (matching != null) {
                    invalidTypes.add(" Could you have meant: " + matching.name() + "?");
                }
            } else {
                String action = getParkourKitData().getString(path + "." + material + ".Action");
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

    private static ParkourConfiguration getParkourKitData() {
        return Parkour.getConfig(ConfigType.PARKOURKIT);
    }
}

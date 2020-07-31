package io.github.a5h73y.parkour.kit;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.configuration.ParkourConfiguration;
import io.github.a5h73y.parkour.enums.ConfigType;
import io.github.a5h73y.parkour.other.Constants;
import io.github.a5h73y.parkour.utility.MaterialUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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
            player.sendMessage(Parkour.getPrefix() + kitName + " ParkourKit does not exist!");
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
                Material matching = MaterialUtils.lookupMaterial(material);
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

        player.sendMessage(Parkour.getPrefix() + invalidTypes.size() + " problems with " + ChatColor.AQUA + kitName + ChatColor.WHITE + " found.");
        if (invalidTypes.size() > 0) {
            for (String type : invalidTypes) {
                player.sendMessage(ChatColor.RED + type);
            }
        }
    }

    /**
     * Display ParkourKit
     * Can either display all the ParkourKit available
     * Or specify which ParkourKit you want to see and which materials are used and their corresponding action
     *
     * @param args
     * @param sender
     */
    public static void listParkourKit(String[] args, CommandSender sender) {
        Set<String> parkourKit = ParkourKitInfo.getParkourKitNames();

        // specifying a kit
        if (args.length == 2) {
            String kitName = args[1].toLowerCase();
            if (!parkourKit.contains(kitName)) {
                sender.sendMessage(Parkour.getPrefix() + "This ParkourKit set does not exist!");
                return;
            }

            TranslationUtils.sendHeading("ParkourKit: " + kitName, sender);
            Set<String> materials = ParkourKitInfo.getParkourKitContents(kitName);

            for (String material : materials) {
                String action = ParkourKitInfo.getActionForMaterial(kitName, material);
                sender.sendMessage(material + ": " + ChatColor.GRAY + action);
            }

        } else {
            //displaying all available kits
            TranslationUtils.sendHeading(parkourKit.size() + " ParkourKits found", sender);
            for (String kit : parkourKit) {
                sender.sendMessage("* " + kit);
            }
        }
    }

    private static ParkourConfiguration getParkourKitData() {
        return Parkour.getConfig(ConfigType.PARKOURKIT);
    }
}

package io.github.a5h73y.parkour.utility;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;

/**
 * Parkour Plugin related utility methods.
 */
public class PluginUtils {

    /**
     * Broadcasts a message to each online player and the server.
     *
     * @param message message to broadcast
     * @param permission permission to receive message
     */
    public static void broadcastMessage(String message, Permission permission) {
        Bukkit.broadcast(message, permission.getPermission());
        log(message);
    }

    /**
     * Log plugin events, varying in severity.
     * 0 - Info; 1 - Warn; 2 - Severe; 3 - Debug.
     *
     * @param message log message
     * @param severity (0 - 3)
     */
    public static void log(String message, int severity) {
        switch (severity) {
            case 1:
                Parkour.getInstance().getLogger().warning(message);
                break;
            case 2:
                Parkour.getInstance().getLogger().severe("! " + message);
                break;
            case 3:
                Parkour.getInstance().getLogger().info("~ " + message);
                break;
            case 0:
            default:
                Parkour.getInstance().getLogger().info(message);
                break;
        }
    }

    /**
     * Log plugin info message.
     *
     * @param message log message
     */
    public static void log(String message) {
        log(message, 0);
    }

    /**
     * Debug a message to the console.
     * Has to be manually enabled in the config.
     *
     * @param message message to log
     */
    public static void debug(String message) {
        if (Parkour.getDefaultConfig().getOrDefault("Debug", false)) {
            log(message, 3);
        }
    }

    /**
     * Write message to `Parkour.log` file.
     * Used to log 'incriminating' events to a separate file that can't be erased.
     * Examples: playerA deleted courseB
     *
     * @param message message to log
     */
    public static void logToFile(String message) {
        if (!Parkour.getDefaultConfig().getBoolean("Other.LogAdminTasksToFile")) {
            return;
        }

        File saveTo = new File(Parkour.getInstance().getDataFolder(), "Parkour.log");
        if (!saveTo.exists()) {
            try {
                saveTo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter writer = new FileWriter(saveTo, true);
             BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write(DateTimeUtils.generateCurrentDateTimeLog() + " - " + message + System.lineSeparator());
        } catch (IOException e) {
            log(e.getMessage(), 2);
            log(e.toString(), 2);
        }
    }

    public static String readContentsOfResource(String resourcePath) throws IOException {
        InputStream inputStream = Parkour.getInstance().getResource(resourcePath);
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Convert a list of material names to a Set of Materials.
     *
     * @param rawMaterials list of material strings
     * @return Set of {@link Material}
     */
    public static Set<Material> convertToValidMaterials(Collection<String> rawMaterials) {
        Set<Material> validMaterials = new HashSet<>();

        for (String rawMaterial : rawMaterials) {
            Material material = Material.getMaterial(rawMaterial.toUpperCase());
            if (material != null) {
                validMaterials.add(material);
            } else {
                log("Material '" + rawMaterial + "' is invalid", 2);
            }
        }
        return validMaterials;
    }

    /**
     * Get the Server's minor version.
     * Will strip the Bukkit version to just the distinguishable version (14, 15, etc.).
     * If the version is unrecognisable, assume it is under 1.13 (where most things drastically changed).
     *
     * @return server version
     */
    public static int getMinorServerVersion() {
        String version = Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1];
        return ValidationUtils.isInteger(version) ? Integer.parseInt(version) : 12;
    }

    /**
     * Find {@link GameMode} by name.
     *
     * @param gameMode requested gamemode
     * @return matching {@link GameMode}
     */
    public static GameMode getGameMode(String gameMode) {
        return GameMode.valueOf(gameMode.toUpperCase());
    }

    /**
     * Check if the GameMode exists.
     * @param gameMode gamemode
     * @return gamemode is valid
     */
    public static boolean doesGameModeExist(String gameMode) {
        boolean valid = true;

        try {
            GameMode.valueOf(gameMode.toUpperCase());
        } catch (IllegalArgumentException e) {
            valid = false;
        }

        return valid;
    }

    private PluginUtils() {}
}

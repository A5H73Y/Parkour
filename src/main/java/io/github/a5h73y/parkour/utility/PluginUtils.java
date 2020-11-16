package io.github.a5h73y.parkour.utility;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.other.Validation;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
        if (Parkour.getDefaultConfig().getBoolean("Debug", false)) {
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
        if (!Parkour.getDefaultConfig().getBoolean("Other.LogToFile")) {
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
            bw.write(DateTimeUtils.getDisplayDateTime() + " " + message + System.lineSeparator());
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
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
     * Replace with Parkour snapshots.
     */
    @Deprecated
    public static void saveAllPlaying(Object obj, String path) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(obj);
            oos.flush();
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Replace with Parkour snapshots.
     */
    @Deprecated
    public static Object loadAllPlaying(String path) {
        Object result = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            result = ois.readObject();
            ois.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
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

    public static boolean doesGameModeExist(String gameMode) {
        return getGameMode(gameMode) != null;
    }

    /**
     * Delete command method
     * Possible arguments include Course, Checkpoint, Lobby, Kit and AutoStart
     * This will only add a Question object with the relevant data until the player confirms the action later on.
     *
     * @param sender
     */
    public static void deleteCommand(CommandSender sender, String command, String argument) {
        Parkour parkour = Parkour.getInstance();

        switch (command.toLowerCase()) {
            case "course":
                if (!Validation.deleteCourse(argument, sender)) {
                    return;
                }

                parkour.getQuestionManager().askDeleteCourseQuestion(sender, argument);
                break;

            case "checkpoint":
                if (!Validation.deleteCheckpoint(sender, argument)) {
                    return;
                }

                int checkpoints = CourseInfo.getCheckpointAmount(argument);
                parkour.getQuestionManager().askDeleteCheckpointQuestion(sender, argument, checkpoints);
                break;

            case "lobby":
                if (!Validation.deleteLobby(sender, argument)) {
                    return;
                }

                parkour.getQuestionManager().askDeleteLobbyQuestion(sender, argument);
                break;

            case "kit":
                if (!Validation.deleteParkourKit(sender, argument)) {
                    return;
                }

                parkour.getQuestionManager().askDeleteKitQuestion(sender, argument);
                break;

            case "autostart":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Parkour.getPrefix() + "This command can only be performed by players");
                    return;
                }

                Location location = ((Player) sender).getLocation();
                String coordinates = location.getBlockX() + "-" + location.getBlockY() + "-" + location.getBlockZ();
                if (!Validation.deleteAutoStart((Player) sender, argument, coordinates)) {
                    return;
                }

                parkour.getQuestionManager().askDeleteAutoStartQuestion(sender, coordinates);
                break;

            default:
                TranslationUtils.sendInvalidSyntax(sender, "delete", "(course / checkpoint / lobby / kit / autostart) (name)");
                break;
        }
    }

    /**
     * Reset command method
     * Possible arguments include Course, Player and Leaderboard
     * This will only add a Question object with the relevant data until the player confirms the action later on.
     *
     * @param sender
     */
    public static void resetCommand(CommandSender sender, String command, String argument, @Nullable String extraArgument) {
        Parkour parkour = Parkour.getInstance();

        switch (command.toLowerCase()) {
            case "course":
                if (!parkour.getCourseManager().doesCourseExists(argument)) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", argument, sender);
                    return;
                }

                parkour.getQuestionManager().askResetCourseQuestion(sender, argument);
                break;

            case "player":
                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(argument);

                if (!PlayerInfo.hasPlayerInfo(targetPlayer) || !targetPlayer.hasPlayedBefore()) {
                    TranslationUtils.sendTranslation("Error.UnknownPlayer", sender);
                    return;
                }

                parkour.getQuestionManager().askResetPlayerQuestion(sender, argument);
                break;

            case "leaderboard":
                if (!parkour.getCourseManager().doesCourseExists(argument)) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", argument, sender);
                    return;
                }

                if (extraArgument != null) {
                    parkour.getQuestionManager().askResetPlayerLeaderboardQuestion(sender, argument, extraArgument);
                } else {
                    parkour.getQuestionManager().askResetLeaderboardQuestion(sender, argument);
                }
                break;

            case "prize":
                if (!parkour.getCourseManager().doesCourseExists(argument)) {
                    TranslationUtils.sendValueTranslation("Error.NoExist", argument, sender);
                    return;
                }

                parkour.getQuestionManager().askResetPrizeQuestion(sender, argument);
                break;

            default:
                TranslationUtils.sendInvalidSyntax(sender, "reset", "(course / player / leaderboard / prize) (argument)");
                break;
        }
    }

    public static void cacheCommand(CommandSender player, @Nullable String argument) {
        Parkour parkour = Parkour.getInstance();
        if (argument != null) {
            switch (argument.toLowerCase()) {
                case "course":
                case "courses":
                    parkour.getCourseManager().clearCache();
                    break;
                case "database":
                    parkour.getDatabase().clearCache();
                    break;
                case "lobby":
                case "lobbies":
                    parkour.getLobbyManager().clearCache();
                    break;
                case "parkourkit":
                case "parkourkits":
                    parkour.getParkourKitManager().clearCache();
                    break;
                case "sound":
                case "sounds":
                    parkour.getSoundsManager().clearCache();
                    break;
                case "all":
                case "clear":
                    clearAllCache();
                    break;
                default:
                    TranslationUtils.sendInvalidSyntax(player, "cache", "[course / database / lobby / parkourkit / sound]");
                    return;
            }
            TranslationUtils.sendPropertySet(player, "Cache", StringUtils.standardizeText(argument), "empty");

        } else {
            TranslationUtils.sendHeading("Parkour Cache", player);
            player.sendMessage("Courses Cached: " + parkour.getCourseManager().getCacheSize());
            player.sendMessage("Database Cached: " + parkour.getDatabase().getCacheSize());
            player.sendMessage("Lobbies Cached: " + parkour.getLobbyManager().getCacheSize());
            player.sendMessage("ParkourKits Cached: " + parkour.getParkourKitManager().getCacheSize());
            player.sendMessage("Sounds Cached: " + parkour.getSoundsManager().getCacheSize());
        }
    }

    public static void clearAllCache() {
        Parkour parkour = Parkour.getInstance();
        parkour.getCourseManager().clearCache();
        parkour.getDatabase().clearCache();
        parkour.getLobbyManager().clearCache();
        parkour.getParkourKitManager().clearCache();
        parkour.getSoundsManager().clearCache();
    }
}

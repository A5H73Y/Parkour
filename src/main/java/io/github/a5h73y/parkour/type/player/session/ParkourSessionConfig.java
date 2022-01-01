package io.github.a5h73y.parkour.type.player.session;

import static io.github.a5h73y.parkour.configuration.serializable.ParkourSerializable.getMapValue;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.PluginUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import de.leonhard.storage.Json;
import de.leonhard.storage.internal.FileType;
import de.leonhard.storage.internal.serialize.LightningSerializer;
import org.bukkit.OfflinePlayer;

/**
 * ParkourSession Config class.
 * Convenience methods for accessing the ParkourSession configuration file.
 */
public class ParkourSessionConfig extends Json {

    public ParkourSessionConfig(File file) {
        super(file);
    }

    public static File getPlayerParkourSessionsFolder(OfflinePlayer player) {
        return new File(Parkour.getInstance().getConfigManager().getParkourSessionsDir()
                + File.separator + Parkour.getDefaultConfig().getPlayerConfigName(player));
    }

    public static File getPlayerParkourSessionFile(OfflinePlayer player, String courseName) {
        return new File(getPlayerParkourSessionsFolder(player),
                courseName.toLowerCase() + "." + FileType.JSON.getExtension());
    }

    public static boolean hasParkourSessionConfig(OfflinePlayer player, String courseName) {
        return getPlayerParkourSessionFile(player, courseName).exists();
    }

    public static ParkourSessionConfig getConfig(OfflinePlayer player, String courseName) {
        return new ParkourSessionConfig(getPlayerParkourSessionFile(player, courseName));
    }

    /**
     * Delete ParkourSession data.
     */
    public static void deleteParkourSessionFile(OfflinePlayer player, String courseName) {
        File sessionConfigFile = getPlayerParkourSessionFile(player, courseName);

        if (sessionConfigFile.exists()) {
            sessionConfigFile.delete();
        }
    }

    /**
     * Delete all Parkour Sessions for Player.
     * Individually removes all files from within the folder, then the folder itself.
     *
     * @param player player
     */
    public static void deleteParkourSessions(OfflinePlayer player) {
        File playerSessionFolder = ParkourSessionConfig.getPlayerParkourSessionsFolder(player);
        if (Files.notExists(playerSessionFolder.toPath())) {
            return;
        }

        try (Stream<Path> paths = Files.walk(playerSessionFolder.toPath())) {
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (SecurityException | IOException e) {
            PluginUtils.log("Player's session couldn't be deleted: " + e.getMessage(), 2);
            e.printStackTrace();
        }
    }

    public ParkourSession getParkourSession() {
        return LightningSerializer.deserialize(this.getData(), ParkourSession.class);
    }

    public void saveParkourSession(ParkourSession parkourSession) {
        getMapValue(LightningSerializer.serialize(parkourSession)).forEach(this::set);
    }
}

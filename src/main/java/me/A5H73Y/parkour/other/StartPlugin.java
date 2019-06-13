package me.A5H73Y.parkour.other;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import com.huskehhh.mysql.Database;
import com.huskehhh.mysql.mysql.MySQL;
import com.huskehhh.mysql.sqlite.SQLite;
import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.ParkourPlaceholders;
import me.A5H73Y.parkour.course.CourseInfo;
import me.A5H73Y.parkour.enums.DatabaseType;
import me.A5H73Y.parkour.kit.ParkourKit;
import me.A5H73Y.parkour.player.ParkourSession;
import me.A5H73Y.parkour.player.PlayerMethods;
import me.A5H73Y.parkour.utilities.DatabaseMethods;
import me.A5H73Y.parkour.utilities.Static;
import me.A5H73Y.parkour.utilities.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

public class StartPlugin {

    public static void run() {
        checkConvertToLatest();
        Parkour.getParkourConfig().setupConfig();
        Static.initiate();
        initiateSQL();
        setupExternalPlugins();
        populatePlayers();
        Utils.log("Enabled Parkour v" + Static.getVersion());
    }

    private static void setupExternalPlugins() {
        setupVault();
        setupBountifulAPI();
        setupPlaceholderAPI();
    }

    private static void setupVault() {
        if (!Parkour.getPlugin().getConfig().getBoolean("Other.Economy.Enabled")) {
            return;
        }

        PluginManager pm = Parkour.getPlugin().getServer().getPluginManager();
        Plugin vault = pm.getPlugin("Vault");
        if (vault != null && vault.isEnabled()) {
            if (setupEconomy()) {
                Utils.log("[Vault] Successfully linked. Version: " + vault.getDescription().getVersion());
                Parkour.getParkourConfig().initiateEconomy();
            } else {
                Utils.log("[Vault] Attempted to link with Vault, but something went wrong.", 2);
                Parkour.getPlugin().getConfig().set("Other.Economy.Enabled", false);
                Parkour.getPlugin().saveConfig();
            }
        } else {
            Utils.log("[Vault] Plugin is missing, disabling Economy Use.", 1);
            Parkour.getPlugin().getConfig().set("Other.Economy.Enabled", false);
            Parkour.getPlugin().saveConfig();
        }
    }

    private static void initiateSQL() {
        initiateSQL(false);
    }

    private static void initiateSQL(boolean forceSQLite) {
        Database database;
        FileConfiguration config = Parkour.getPlugin().getConfig();

        // Only use MySQL if they have enabled it, configured it, and we aren't
        // forcing SQLite (MySQL failed)
        if (!forceSQLite && config.getBoolean("MySQL.Use") && !config.getString("MySQL.Host").equals("Host")) {
            database = new MySQL(config.getString("MySQL.Host"), config.getString("MySQL.Port"), config.getString("MySQL.Database"), config.getString("MySQL.User"), config.getString("MySQL.Password"));
            DatabaseMethods.type = DatabaseType.MySQL;
        } else {
            database = new SQLite("parkour.db");
            DatabaseMethods.type = DatabaseType.SQLite;
        }

        try {
            database.openConnection();
            Parkour.setDatabase(database);
            DatabaseMethods.setupTables();
        } catch (Exception ex) {
            failedSQL(ex);
        }
    }

    private static void failedSQL(Exception ex) {
        Utils.log("[SQL] Connection problem: " + ex.getMessage(), 2);
        Utils.log("[SQL] Defaulting to SQLite...", 1);
        Parkour.getPlugin().getConfig().set("MySQL.Use", false);
        Parkour.getPlugin().saveConfig();
        initiateSQL(true);
    }

    private static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Parkour.getPlugin().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            Parkour.setEconomy(economyProvider.getProvider());
        }
        return (Parkour.getEconomy() != null);
    }

    private static void setupBountifulAPI() {
        if (!Parkour.getPlugin().getConfig().getBoolean("Other.BountifulAPI.Enabled")) {
            return;
        }

        Plugin bountifulAPI = Parkour.getPlugin().getServer().getPluginManager()
                .getPlugin("BountifulAPI");

        if (bountifulAPI != null && bountifulAPI.isEnabled()) {
            Utils.log("[BountifulAPI] Successfully linked. Version: " + bountifulAPI.getDescription().getVersion());
            Static.enableBountifulAPI();
        } else {
            Utils.log("[BountifulAPI] Plugin is missing, disabling config option.", 1);
            Parkour.getPlugin().getConfig().set("Other.BountifulAPI.Enabled", false);
            Parkour.getPlugin().saveConfig();
        }
    }

    private static void setupPlaceholderAPI() {
        if (!Parkour.getPlugin().getConfig().getBoolean("Other.PlaceholderAPI.Enabled")) {
            return;
        }

        Plugin placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");

        if (placeholderAPI != null && placeholderAPI.isEnabled()) {
            Utils.log("[PlaceholderAPI] Successfully linked. Version: " + placeholderAPI.getDescription().getVersion());
            new ParkourPlaceholders(Parkour.getPlugin()).register();
            Static.enablePlaceholderAPI();

        } else {
            Utils.log("[PlaceholderAPI] Plugin is missing, disabling config option.", 1);
            Parkour.getPlugin().getConfig().set("Other.PlaceholderAPI.Enabled", false);
            Parkour.getPlugin().saveConfig();
        }
    }

    private static void populatePlayers() {
        if (!new File(Static.PLAYING_BIN_PATH).exists()) {
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            HashMap<String, ParkourSession> players = (HashMap<String, ParkourSession>) Utils.loadAllPlaying(Static.PLAYING_BIN_PATH);
            fixParkourBlocks(players);

            PlayerMethods.setPlaying(players);

            for (Entry<String, ParkourSession> entry : players.entrySet()) {
                Player playingp = Parkour.getPlugin().getServer().getPlayer(entry.getKey());
                if (playingp == null) {
                    continue;
                }

                playingp.sendMessage(Utils.getTranslation("Parkour.Continue")
                        .replace("%COURSE%", entry.getValue().getCourse().getName()));
            }
        } catch (Exception e) {
            Utils.log("Failed to load players: " + e.getMessage(), 2);
            PlayerMethods.setPlaying(new HashMap<>());
        }
    }

    /**
     * We only want to update completely, if the config version (previous version) is less than 4.0 (new system)
     */
    private static void checkConvertToLatest() {
        if (Parkour.getParkourConfig().isFreshInstall()) {
            return;
        }

        double configVersion = Parkour.getPlugin().getConfig().getDouble("Version");
        double currentVersion = Double.parseDouble(Parkour.getPlugin().getDescription().getVersion());

        if (configVersion >= currentVersion) {
            return;
        }

        boolean fromBeforeVersion4 = configVersion < 4.0;

        // We backup all their files first before touching them
        Backup.backupNow(false);

        if (fromBeforeVersion4) {
            Utils.log("Your config is too outdated.", 2);
            Utils.log("You must update the plugin to v4.8, and then to " + currentVersion, 2);
            Utils.log("Disabling the plugin to prevent corruption.", 2);
            Bukkit.getPluginManager().disablePlugin(Parkour.getPlugin());
            return;
        }

        Utils.log("[Backup] Updating config to " + currentVersion + "...");
        Parkour.getPlugin().getConfig().set("Version", currentVersion);
        Parkour.getPlugin().saveConfig();
    }

    private static void fixParkourBlocks(HashMap<String, ParkourSession> players) {
        for (String playerName : players.keySet()) {
            ParkourSession session = players.get(playerName);
            String parkourKitName = CourseInfo.getParkourKit(session.getCourse().getName());
            ParkourKit kit = ParkourKit.getParkourKit(parkourKitName);
            players.get(playerName).getCourse().setParkourKit(kit);
        }
    }
}

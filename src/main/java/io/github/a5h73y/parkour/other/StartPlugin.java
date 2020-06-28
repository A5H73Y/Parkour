package io.github.a5h73y.parkour.other;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.ParkourPlaceholders;
import io.github.a5h73y.parkour.course.CourseInfo;
import io.github.a5h73y.parkour.kit.ParkourKit;
import io.github.a5h73y.parkour.utilities.Utils;
import io.github.a5h73y.parkour.player.ParkourSession;
import io.github.a5h73y.parkour.player.PlayerMethods;
import io.github.a5h73y.parkour.utilities.Static;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

public class StartPlugin {

    public static void run() {
        checkConvertToLatest();
        Static.initiate();
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
        if (!Parkour.getInstance().getConfig().getBoolean("Other.Economy.Enabled")) {
            return;
        }

        PluginManager pm = Parkour.getInstance().getServer().getPluginManager();
        Plugin vault = pm.getPlugin("Vault");
        if (vault != null && vault.isEnabled()) {
            if (setupEconomy()) {
                Utils.log("[Vault] Successfully linked. Version: " + vault.getDescription().getVersion());
                Static.enableEconomy();
            } else {
                Utils.log("[Vault] Attempted to link with Vault, but something went wrong.", 2);
                Parkour.getInstance().getConfig().set("Other.Economy.Enabled", false);
                Parkour.getInstance().saveConfig();
            }
        } else {
            Utils.log("[Vault] Plugin is missing, disabling Economy Use.", 1);
            Parkour.getInstance().getConfig().set("Other.Economy.Enabled", false);
            Parkour.getInstance().saveConfig();
        }
    }

    private static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Parkour.getInstance().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            Parkour.setEconomy(economyProvider.getProvider());
        }
        return (Parkour.getEconomy() != null);
    }

    private static void setupBountifulAPI() {
        if (!Parkour.getInstance().getConfig().getBoolean("Other.BountifulAPI.Enabled")) {
            return;
        }

        Plugin bountifulAPI = Parkour.getInstance().getServer().getPluginManager()
                .getPlugin("BountifulAPI");

        if (bountifulAPI != null && bountifulAPI.isEnabled()) {
            Utils.log("[BountifulAPI] Successfully linked. Version: " + bountifulAPI.getDescription().getVersion());
            Static.enableBountifulAPI();
        } else {
            Utils.log("[BountifulAPI] Plugin is missing, disabling config option.", 1);
            Parkour.getInstance().getConfig().set("Other.BountifulAPI.Enabled", false);
            Parkour.getInstance().saveConfig();
        }
    }

    private static void setupPlaceholderAPI() {
        if (!Parkour.getInstance().getConfig().getBoolean("Other.PlaceholderAPI.Enabled")) {
            return;
        }

        Plugin placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");

        if (placeholderAPI != null && placeholderAPI.isEnabled()) {
            Utils.log("[PlaceholderAPI] Successfully linked. Version: " + placeholderAPI.getDescription().getVersion());
            new ParkourPlaceholders(Parkour.getInstance()).register();
            Static.enablePlaceholderAPI();

        } else {
            Utils.log("[PlaceholderAPI] Plugin is missing, disabling config option.", 1);
            Parkour.getInstance().getConfig().set("Other.PlaceholderAPI.Enabled", false);
            Parkour.getInstance().saveConfig();
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
                Player playingp = Parkour.getInstance().getServer().getPlayer(entry.getKey());
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
//        if (Parkour.getInstance().getConfig().isFreshInstall()) {
//            return;
//        }
	    // TODO complete rewrite of upgrade system
	    // see UpgradeParkour class
        if (true) return;

        double configVersion = Parkour.getInstance().getConfig().getDouble("Version");
        double currentVersion = Double.parseDouble(Parkour.getInstance().getDescription().getVersion());

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
            Bukkit.getPluginManager().disablePlugin(Parkour.getInstance());
            return;
        }

        Utils.log("[Backup] Updating config to " + currentVersion + "...");
        Parkour.getInstance().getConfig().set("Version", currentVersion);
        Parkour.getInstance().saveConfig();
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

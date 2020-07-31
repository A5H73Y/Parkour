package io.github.a5h73y.parkour.other;

@Deprecated
public class StartPlugin {

//    public static void run() {
//        checkConvertToLatest();
//        populatePlayers();
//    }
//
//    private static void populatePlayers() {
//        if (!new File(Static.PLAYING_BIN_PATH).exists()) {
//            return;
//        }
//
//        try {
//            @SuppressWarnings("unchecked")
//            HashMap<String, ParkourSession> players = (HashMap<String, ParkourSession>) Utils.loadAllPlaying(Static.PLAYING_BIN_PATH);
//            fixParkourBlocks(players);
//
//            PlayerMethods.setPlaying(players);
//
//            for (Entry<String, ParkourSession> entry : players.entrySet()) {
//                Player playingp = Parkour.getInstance().getServer().getPlayer(entry.getKey());
//                if (playingp == null) {
//                    continue;
//                }
//
//                playingp.sendMessage(Utils.getTranslation("Parkour.Continue")
//                        .replace("%COURSE%", entry.getValue().getCourse().getName()));
//            }
//        } catch (Exception e) {
//            PluginUtils.log("Failed to load players: " + e.getMessage(), 2);
//            PlayerMethods.setPlaying(new HashMap<>());
//        }
//    }
//
//    /**
//     * We only want to update completely, if the config version (previous version) is less than 4.0 (new system)
//     */
//    private static void checkConvertToLatest() {
////        if (Parkour.getInstance().getConfig().isFreshInstall()) {
////            return;
////        }
//	    // TODO complete rewrite of upgrade system
//	    // see UpgradeParkour class
//        if (true) return;
//
//        double configVersion = Parkour.getInstance().getConfig().getDouble("Version");
//        double currentVersion = Double.parseDouble(Parkour.getInstance().getDescription().getVersion());
//
//        if (configVersion >= currentVersion) {
//            return;
//        }
//
//        boolean fromBeforeVersion4 = configVersion < 4.0;
//
//        // We backup all their files first before touching them
//        Backup.backupNow(false);
//
//        if (fromBeforeVersion4) {
//            PluginUtils.log("Your config is too outdated.", 2);
//            PluginUtils.log("You must update the plugin to v4.8, and then to " + currentVersion, 2);
//            PluginUtils.log("Disabling the plugin to prevent corruption.", 2);
//            Bukkit.getPluginManager().disablePlugin(Parkour.getInstance());
//            return;
//        }
//
//        PluginUtils.log("[Backup] Updating config to " + currentVersion + "...");
//        Parkour.getInstance().getConfig().set("Version", currentVersion);
//        Parkour.getInstance().saveConfig();
//    }
//
//    private static void fixParkourBlocks(HashMap<String, ParkourSession> players) {
//        for (String playerName : players.keySet()) {
//            ParkourSession session = players.get(playerName);
//            String parkourKitName = CourseInfo.getParkourKit(session.getCourse().getName());
//            ParkourKit kit = ParkourKit.getParkourKit(parkourKitName);
//            players.get(playerName).getCourse().setParkourKit(kit);
//        }
//    }
}

package me.A5H73Y.parkour.player;

import java.util.HashMap;
import java.util.List;

import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.config.ParkourConfiguration;
import me.A5H73Y.parkour.course.Checkpoint;
import me.A5H73Y.parkour.course.Course;
import me.A5H73Y.parkour.course.CourseInfo;
import me.A5H73Y.parkour.course.CourseMethods;
import me.A5H73Y.parkour.course.LobbyMethods;
import me.A5H73Y.parkour.enums.ParkourMode;
import me.A5H73Y.parkour.event.PlayerAchieveCheckpointEvent;
import me.A5H73Y.parkour.event.PlayerDeathEvent;
import me.A5H73Y.parkour.event.PlayerFinishCourseEvent;
import me.A5H73Y.parkour.event.PlayerJoinCourseEvent;
import me.A5H73Y.parkour.event.PlayerLeaveCourseEvent;
import me.A5H73Y.parkour.kit.ParkourKit;
import me.A5H73Y.parkour.manager.ChallengeManager;
import me.A5H73Y.parkour.manager.QuietModeManager;
import me.A5H73Y.parkour.other.Constants;
import me.A5H73Y.parkour.other.TimeObject;
import me.A5H73Y.parkour.other.Validation;
import me.A5H73Y.parkour.utilities.DatabaseMethods;
import me.A5H73Y.parkour.utilities.Static;
import me.A5H73Y.parkour.utilities.Utils;
import me.A5H73Y.parkour.utilities.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import static me.A5H73Y.parkour.enums.ConfigType.INVENTORY;

public class PlayerMethods {

    /**
     * Map a player username to their ParkourSession
     */
    private static HashMap<String, ParkourSession> parkourPlayers = new HashMap<>();

    /**
     * This method is only called from the CourseMethods after course
     * validation. It will retrieve a course object which will then be
     * referenced against the player. We prepare the player for the course here.
     *
     * @param player
     * @param course
     */
    public static void playerJoin(Player player, Course course) {
        if (Parkour.getSettings().isTeleportToJoinLocation()) {
            PlayerInfo.setJoinLocation(player);
        }
        player.teleport(course.getCurrentCheckpoint().getLocation());
        prepareJoinPlayer(player, course.getName());
        CourseInfo.increaseView(course.getName());

        if (getParkourSession(player.getName()) == null) {
            boolean displayTitle = Parkour.getInstance().getConfig().getBoolean("DisplayTitle.JoinCourse");

            if (course.getMaxDeaths() == null) {
                Utils.sendTitle(player, Utils.getTranslation("Parkour.Join", false)
                                .replace("%COURSE%", course.getName()),
                        displayTitle);
            } else {
                Utils.sendFullTitle(player, Utils.getTranslation("Parkour.Join", false)
                                .replace("%COURSE%", course.getName()),
                        Utils.getTranslation("Parkour.JoinLives", false)
                                .replace("%AMOUNT%", course.getMaxDeaths().toString()),
                        displayTitle);
            }
        } else {
            removePlayer(player.getName());
            if (!QuietModeManager.getInstance().isInQuietMode(player.getName())) {
                player.sendMessage(Utils.getTranslation("Parkour.TimeReset"));
            }
        }

        ParkourSession session = addPlayer(player.getName(), new ParkourSession(course));
        PlayerInfo.setLastPlayedCourse(player, course.getName());
        setupPlayerMode(player);
        Parkour.getScoreboardManager().addScoreboard(player);
        session.startVisualTimer(player);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinCourseEvent(player, course.getName()));
    }

    /**
     * Leave a course
     * Will remove the player from the players, which will also dispose of their course session.
     *
     * @param player
     */
    public static void playerLeave(Player player) {
        if (!isPlaying(player.getName())) {
            player.sendMessage(Utils.getTranslation("Error.NotOnAnyCourse"));
            return;
        }

        ParkourSession session = getParkourSession(player.getName());
        Utils.sendSubTitle(player, Utils.getTranslation("Parkour.Leave", false)
                        .replace("%COURSE%", session.getCourse().getName()),
                Parkour.getInstance().getConfig().getBoolean("DisplayTitle.Leave"));

        teardownPlayerMode(player);
        removePlayer(player.getName());
        preparePlayer(player, Parkour.getInstance().getConfig().getInt("OnFinish.SetGamemode"));
        restoreHealth(player);
        loadInventory(player);

        if (ChallengeManager.getInstance().isPlayerInChallenge(player.getName())) {
            ChallengeManager.getInstance().terminateChallenge(player);
        }

        if (Parkour.getInstance().getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
            player.setLevel(0);
        }

        LobbyMethods.teleportToLeaveDestination(player, session);

        if (Static.containsHidden(player.getName())) {
            Utils.toggleVisibility(player, true);
        }

        Utils.forceVisible(player);
        Parkour.getScoreboardManager().removeScoreboard(player);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerLeaveCourseEvent(player, session.getCourse().getName()));
    }

    /**
     * Player dies while on a course
     * Called when the player 'dies' this can be from real events (Like falling
     * from too high), or native Parkour deaths (walking on a deathblock)
     *
     * @param player
     */
    public static void playerDie(Player player) {
        if (!isPlaying(player.getName())) {
            return;
        }

        ParkourSession session = getParkourSession(player.getName());
        session.increaseDeath();

        if (session.getCourse().hasMaxDeaths()) {
            if (session.getCourse().getMaxDeaths() > session.getDeaths()) {
                int remainingLives = session.getCourse().getMaxDeaths() - session.getDeaths();

                Utils.sendSubTitle(player, Utils.getTranslation("Parkour.LifeCount", false)
                                .replace("%AMOUNT%", String.valueOf(remainingLives)),
                        Parkour.getInstance().getConfig().getBoolean("DisplayTitle.Death"));
            } else {
                player.sendMessage(Utils.getTranslation("Parkour.MaxDeaths")
                        .replace("%AMOUNT%", session.getCourse().getMaxDeaths().toString()));
                playerLeave(player);
                return;
            }
        }

        player.teleport(session.getCourse().getCurrentCheckpoint().getLocation());

        // if it's the first checkpoint
        if (session.getCheckpoint() == 0) {
            if (Parkour.getInstance().getConfig().getBoolean("OnDie.ResetTimeWithNoCheckpoint")) {
                session.resetTimeStarted();
                if (!QuietModeManager.getInstance().isInQuietMode(player.getName())) {
                    player.sendMessage(Utils.getTranslation("Parkour.Die1") + Utils.getTranslation("Parkour.TimeReset", false));
                }
            } else {
                if (!QuietModeManager.getInstance().isInQuietMode(player.getName())) {
                    player.sendMessage(Utils.getTranslation("Parkour.Die1"));
                }
            }
        } else {
            if (!QuietModeManager.getInstance().isInQuietMode(player.getName())) {
                player.sendMessage(Utils.getTranslation("Parkour.Die2")
                        .replace("%POINT%", String.valueOf(session.getCheckpoint())));
            }
        }

        if (Parkour.getInstance().getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
            player.setLevel(session.getDeaths());
        }

        //TODO sounds

        preparePlayer(player, Parkour.getInstance().getConfig().getInt("OnJoin.SetGamemode"));
        Bukkit.getServer().getPluginManager().callEvent(new PlayerDeathEvent(player, session.getCourse().getName()));
    }

    /**
     * Player finishes a course
     * This will be called when the player completes the course.
     * Their reward will be given here, as well as a time entry to the database.
     * Inventory is restored before the player is teleported. If the teleport is delayed,
     * restore the inventory after the delay.
     *
     * @param player
     */
    public static void playerFinish(final Player player) {
        if (!isPlaying(player.getName())) {
            return;
        }

        if (isPlayerInTestmode(player.getName())) {
            return;
        }

        ParkourSession session = getParkourSession(player.getName());
        final String courseName = session.getCourse().getName();
        final long timeTaken = session.getTime();

        if (Parkour.getInstance().getConfig().getBoolean("OnFinish.EnforceCompletion")
                && session.getCheckpoint() != (session.getCourse().getCheckpoints())) {

            player.sendMessage(Utils.getTranslation("Error.Cheating1"));
            player.sendMessage(Utils.getTranslation("Error.Cheating2", false)
                    .replace("%AMOUNT%", String.valueOf(session.getCourse().getCheckpoints())));
            playerDie(player);
            return;
        }

        preparePlayer(player, Parkour.getInstance().getConfig().getInt("OnFinish.SetGamemode"));

        if (Static.containsHidden(player.getName())) {
            Utils.toggleVisibility(player, true);
        }

        displayFinishMessage(player, session);
        CourseInfo.increaseComplete(courseName);
        teardownPlayerMode(player);
        removePlayer(player.getName());

        if (ChallengeManager.getInstance().isPlayerInChallenge(player.getName())) {
            ChallengeManager.getInstance().completeChallenge(player);
        }

        if (Parkour.getInstance().getConfig().getBoolean("OnDie.SetXPBarToDeathCount")) {
            player.setLevel(0);
        }

        final long delay = Parkour.getInstance().getConfig().getLong("OnFinish.TeleportDelay");
        final boolean teleportAway = Parkour.getInstance().getConfig().getBoolean("OnFinish.TeleportAway");

        if (delay <= 0) {
            restoreHealth(player);
            loadInventory(player);
            givePrize(player, courseName);
            if (teleportAway) {
                courseCompleteLocation(player, courseName);
            }
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Parkour.getInstance(), () -> {
                restoreHealth(player);
                loadInventory(player);
                givePrize(player, courseName);
                if (teleportAway) {
                    courseCompleteLocation(player, courseName);
                }
            }, delay);
        }

        DatabaseMethods.insertOrUpdateTime(courseName, player, timeTaken, session.getDeaths());

        PlayerInfo.setCompletedCourseInfo(player, courseName);

        Utils.forceVisible(player);
        Parkour.getScoreboardManager().removeScoreboard(player);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerFinishCourseEvent(player, courseName));
    }

    /**
     * Check if the player's time is a new course or personal record.
     *
     * @param player
     * @param courseName
     * @param timeTaken
     */
    public static boolean isNewRecord(Player player, String courseName, long timeTaken) {
        List<TimeObject> courseRecord = DatabaseMethods.getTopCourseResults(courseName, 1);
        TimeObject record = null;

        if (courseRecord != null && !courseRecord.isEmpty()) {
            record = courseRecord.get(0);
        }
        if (record == null || record.getTime() > timeTaken) {
            Utils.sendFullTitle(player, Utils.getTranslation("Parkour.CourseRecord", false), Utils.displayCurrentTime(timeTaken), true);
            return true;
        }

        List<TimeObject> playerRecord = DatabaseMethods.getTopPlayerCourseResults(player.getName(), courseName, 1);
        TimeObject result = null;

        if (playerRecord != null && !playerRecord.isEmpty()) {
            result = playerRecord.get(0);
        }
        if (result == null || result.getTime() > timeTaken) {
            Utils.sendFullTitle(player, Utils.getTranslation("Parkour.BestTime", false), Utils.displayCurrentTime(timeTaken), true);
            return true;
        }
        return false;
    }

    /**
     * Restart the course progress
     * Time and deaths are reset.
     * Will take into account treating the first checkpoint as start
     *
     * @param player
     */
    public static void restartCourse(Player player) {
        if (!isPlaying(player.getName())) {
            return;
        }

        ParkourSession session = getParkourSession(player.getName());
        session.restartSession();

        if (Parkour.getSettings().isFirstCheckAsStart()) {
            session.increaseCheckpoint();
        }

        player.sendMessage(Utils.getTranslation("Parkour.Restarting"));
        player.teleport(session.getCourse().getCurrentCheckpoint().getLocation());
    }

    /**
     * Teleport player after course completion
     * Based on the linked course or lobby
     *
     * @param player
     * @param courseName
     */
    private static void courseCompleteLocation(Player player, String courseName) {
        if (CourseInfo.hasLinkedCourse(courseName)) {
            String linkedCourseName = CourseInfo.getLinkedCourse(courseName);

            if (CourseMethods.exist(linkedCourseName)) {
                CourseMethods.joinCourse(player, linkedCourseName);
                return;
            }
        } else if (CourseInfo.hasLinkedLobby(courseName)) {
            String lobbyName = CourseInfo.getLinkedLobby(courseName);

            if (Parkour.getInstance().getConfig().contains("Lobby." + lobbyName + ".World")) {
                String[] args = {null, lobbyName};
                LobbyMethods.joinLobby(args, player);
                return;
            }
        }

        LobbyMethods.joinLobby(null, player);
    }

    /**
     * Display the course finish information
     * Will send to the chosen amount of players
     *
     * @param player
     * @param session
     */
    private static void displayFinishMessage(Player player, ParkourSession session) {
        if (Parkour.getInstance().getConfig().getBoolean("OnFinish.DisplayStats") && Static.getBountifulAPI()) {

            // we only want to display this if they have titles enabled, otherwise it sends the message in chat twice, which is dumb
            Utils.sendFullTitle(player,
                    Utils.getTranslation("Parkour.FinishCourse1", false)
                            .replace("%COURSE%", session.getCourse().getName()),
                    Utils.getTranslation("Parkour.FinishCourse2", false)
                            .replace("%DEATHS%", String.valueOf(session.getDeaths()))
                            .replace("%TIME%", session.displayTime()),
                    Parkour.getInstance().getConfig().getBoolean("DisplayTitle.Finish"));
        }

        String finishBroadcast = Utils.getTranslation("Parkour.FinishBroadcast")
                .replace("%PLAYER%", player.getName())
                .replace("%COURSE%", session.getCourse().getName())
                .replace("%DEATHS%", String.valueOf(session.getDeaths()))
                .replace("%TIME%", session.displayTime());

        switch (Parkour.getInstance().getConfig().getInt("OnFinish.BroadcastLevel")) {
            case 4:
                for (Player players : player.getWorld().getPlayers()) {
                    players.sendMessage(finishBroadcast);
                }
                return;
            case 3:
                for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                    players.sendMessage(finishBroadcast);
                }
                return;
            case 2:
                for (Player players : Utils.getOnlineParkourPlayers()) {
                    players.sendMessage(finishBroadcast);
                }
                return;
            case 1:
            default:
                player.sendMessage(finishBroadcast);
        }

    }

    /**
     * Reward a player with several forms of prize after course completion.
     *
     * @param player
     * @param courseName
     */
    private static void givePrize(Player player, String courseName) {
        if (!Parkour.getInstance().getConfig().getBoolean("OnFinish.EnablePrizes")) {
            return;
        }

        if (CourseInfo.getRewardOnce(courseName) &&
                DatabaseMethods.hasPlayerCompleted(player.getName(), courseName)) {
            return;
        }

        // Check how often prize can be rewarded
        if (CourseInfo.hasRewardDelay(courseName)) {
            // if we still have to wait, return out of this function
            if (!Utils.hasPrizeCooldownDurationPassed(player, courseName, true)) {
                return;
            }
            // otherwise make a note of last time rewarded, and let them continue
            PlayerInfo.setLastRewardedTime(player, courseName, System.currentTimeMillis());
        }

        Material material;
        int amount;

        // Use Custom prize
        if (CourseInfo.hasMaterialPrize(courseName)) {
            material = CourseInfo.getMaterialPrize(courseName);
            amount = CourseInfo.getMaterialPrizeAmount(courseName);
        } else {
            material = Utils.lookupMaterial(Parkour.getInstance().getConfig().getString("OnFinish.DefaultPrize.Material"));
            amount = Parkour.getInstance().getConfig().getInt("OnFinish.DefaultPrize.Amount", 0);
        }

        if (material != null && amount > 0) {
            player.getInventory().addItem(new ItemStack(material, amount));
        }

        // Give XP to player
        int xp = CourseInfo.getXPPrize(courseName);

        if (xp == 0) {
            xp = Parkour.getInstance().getConfig().getInt("OnFinish.DefaultPrize.XP");
        }

        if (xp > 0) {
            player.giveExp(xp);
        }

        // Level player
        int rewardLevel = CourseInfo.getRewardLevel(courseName);
        if (rewardLevel > 0) {
            int current = PlayerInfo.getParkourLevel(player);

            if (current < rewardLevel) {
                PlayerInfo.setParkourLevel(player, rewardLevel);
                if (Parkour.getInstance().getConfig().getBoolean("Other.Display.LevelReward")) {
                    player.sendMessage(Utils.getTranslation("Parkour.RewardLevel")
                            .replace("%LEVEL%", String.valueOf(rewardLevel))
                            .replace("%COURSE%", courseName));
                }
            }
        }
        // Level increment
        int addLevel = CourseInfo.getRewardLevelAdd(courseName);
        if (addLevel > 0) {
            int newLevel = PlayerInfo.getParkourLevel(player) + addLevel;

            PlayerInfo.setParkourLevel(player, newLevel);
            player.sendMessage(Utils.getTranslation("Parkour.RewardLevel")
                    .replace("%LEVEL%", String.valueOf(newLevel))
                    .replace("%COURSE%", courseName));
        }

        // check if there is a rank upgrade
        // update - this should be based on their new level, and not the course level
        int newLevel = PlayerInfo.getParkourLevel(player);

        String rewardRank = PlayerInfo.getRewardRank(newLevel);
        if (rewardRank != null) {
            PlayerInfo.setRank(player, rewardRank);
            player.sendMessage(Utils.colour(Utils.getTranslation("Parkour.RewardRank").replace("%RANK%", rewardRank)));
        }

        // Execute the command
        if (CourseInfo.hasCommandPrize(courseName)) {
            for (String command : CourseInfo.getCommandsPrize(courseName)) {
                Parkour.getInstance().getServer().dispatchCommand(
                        Parkour.getInstance().getServer().getConsoleSender(),
                        command.replace("%PLAYER%", player.getName()));
            }
        }

        // Give player Parkoins
        int parkoins = CourseInfo.getRewardParkoins(courseName);
        if (parkoins > 0) {
            PlayerMethods.rewardParkoins(player, parkoins);
        }

        giveEconomyPrize(player, courseName);
        player.updateInventory();
    }

    /**
     * Increase the amount of Parkoins the player has by the parameter value.
     *
     * @param player
     * @param parkoins
     */
    public static void rewardParkoins(Player player, int parkoins) {
        if (parkoins <= 0) {
            return;
        }

        int total = parkoins + PlayerInfo.getParkoins(player);
        PlayerInfo.setParkoins(player, total);
        player.sendMessage(Utils.getTranslation("Parkour.RewardParkoins")
                .replace("%AMOUNT%", String.valueOf(parkoins))
                .replace("%TOTAL%", String.valueOf(total)));
    }

    /**
     * Decrease the amount of Parkoins the player has by the parameter value.
     *
     * @param player
     * @param parkoins
     */
    public static void deductParkoins(Player player, int parkoins) {
        if (parkoins <= 0) {
            return;
        }

        int current = PlayerInfo.getParkoins(player);
        current = (current < parkoins) ? 0 : (current - parkoins);

        PlayerInfo.setParkoins(player, current);
        player.sendMessage(Static.getParkourString() + parkoins + " Parkoins deducted! New total: " + ChatColor.AQUA + current);
    }

    /**
     * Give the player the economy prize for the course.
     *
     * @param player
     * @param courseName
     */
    private static void giveEconomyPrize(Player player, String courseName) {
        if (!Static.getEconomy()) {
            return;
        }

        int reward = CourseInfo.getEconomyFinishReward(courseName);

        if (reward > 0) {
            Parkour.getEconomy().depositPlayer(player, reward);
            String currencyName = Parkour.getEconomy().currencyNamePlural() == null ?
                    "" : " " + Parkour.getEconomy().currencyNamePlural();

            player.sendMessage(Utils.getTranslation("Economy.Reward")
                    .replace("%AMOUNT%", reward + currencyName)
                    .replace("%COURSE%", courseName));
        }
    }

    /**
     * Retrieve ParkourSession for player based on their name.
     *
     * @param playerName
     * @return ParkourSession
     */
    public static ParkourSession getParkourSession(String playerName) {
        if (isPlaying(playerName)) {
            return parkourPlayers.get(playerName);
        }

        return null;
    }

    /**
     * Return if a player is on a course
     *
     * @param playerName
     * @return boolean
     */
    public static boolean isPlaying(String playerName) {
        return parkourPlayers.get(playerName) != null;
    }

    /**
     * Get the Map of players using the plugin
     *
     * @return HashMap<playerName, ParkourSession>
     */
    public static HashMap<String, ParkourSession> getPlaying() {
        return parkourPlayers;
    }

    /**
     * Overwrite the playing players, populates when the plugin starts
     *
     * @param players
     */
    public static void setPlaying(HashMap<String, ParkourSession> players) {
        parkourPlayers = players;
    }

    /**
     * Lookup and display the Player's Parkour information.
     * Will display their stored statistics as well as their current information if they're on a course.
     *
     * @param args
     * @param player
     */
    public static void displayPlayerInfo(String[] args, Player player) {
        OfflinePlayer target = args.length <= 1 ? player : Bukkit.getOfflinePlayer(args[1]);

        ParkourSession session = PlayerMethods.getParkourSession(target.getName());

        if (session == null && !PlayerInfo.hasPlayerInfo(target)) {
            player.sendMessage(Static.getParkourString() + "Player has never played Parkour. What is wrong with them?!");
            return;
        }

        player.sendMessage(Utils.getStandardHeading(target.getName() + "'s information"));

        if (session != null) {
            player.sendMessage("Course: " + ChatColor.AQUA + session.getCourse().getName());
            player.sendMessage("Deaths: " + ChatColor.AQUA + session.getDeaths());
            player.sendMessage("Time: " + ChatColor.AQUA + session.displayTime());
            player.sendMessage("Checkpoint: " + ChatColor.AQUA + session.getCheckpoint());
        }

        if (PlayerInfo.hasPlayerInfo(target)) {
            int level = PlayerInfo.getParkourLevel(target);
            String selected = PlayerInfo.getSelected(target);

            if (level > 0) {
                player.sendMessage("Level: " + ChatColor.AQUA + level);
            }

            if (selected != null && selected.length() > 0) {
                player.sendMessage("Editing: " + ChatColor.AQUA + selected);
            }

            if (PlayerInfo.getParkoins(target) > 0) {
                player.sendMessage("Parkoins: " + ChatColor.AQUA + PlayerInfo.getParkoins(target));
            }

            if (Parkour.getInstance().getConfig().getBoolean("OnFinish.SaveUserCompletedCourses")) {
                player.sendMessage("Courses Completed: " + ChatColor.AQUA + PlayerInfo.getNumberOfCoursesCompleted(player) + " / " + CourseInfo.getAllCourses().size());
            }
        }
    }

    /**
     * Add a player and their session to the playing players.
     *
     * @param playerName
     * @param session
     */
    private static ParkourSession addPlayer(String playerName, ParkourSession session) {
        parkourPlayers.put(playerName, session);
        return session;
    }

    /**
     * Remove a player and their session from the playing players.
     *
     * @param playerName
     */
    private static void removePlayer(String playerName) {
        ParkourSession session = parkourPlayers.get(playerName);
        if (session != null) {
            session.cancelVisualTimer();
            parkourPlayers.remove(playerName);
        }
    }

    /**
     * Executed via "/pa kit", will clear and populate the players inventory
     * with the default Parkour tools.
     *
     * @param player
     */
    public static void givePlayerKit(String[] args, Player player) {
        if (Parkour.getInstance().getConfig().getBoolean("Other.ParkourKit.ReplaceInventory")) {
            player.getInventory().clear();
        }
        ParkourKit kit;

        if (args != null && args.length == 2) {
            kit = ParkourKit.getParkourKit(args[1]);
            if (kit == null) {
                player.sendMessage(Static.getParkourString() + "Invalid ParkourKit: " + ChatColor.RED + args[1]);
                return;
            }
        } else {
            kit = ParkourKit.getParkourKit(Constants.DEFAULT);
        }

        for (Material material : kit.getMaterials()) {
            String action = kit.getAction(material);

            if (action == null) {
                continue;
            }

            action = Utils.standardizeText(action);

            ItemStack s = new ItemStack(material);
            ItemMeta m = s.getItemMeta();
            m.setDisplayName(Utils.getTranslation("Kit." + action, false));
            s.setItemMeta(m);
            player.getInventory().addItem(s);
        }

        ItemStack s = new ItemStack(XMaterial.OAK_SIGN.parseMaterial());
        ItemMeta m = s.getItemMeta();
        m.setDisplayName(Utils.getTranslation("Kit.Sign", false));
        s.setItemMeta(m);
        player.getInventory().addItem(s);

        player.updateInventory();
        player.sendMessage(Utils.getTranslation("Other.Kit"));
        Utils.logToFile(player.getName() + " recieved the kit");
    }

    /**
     * Prepare a player for joining a course
     * Will save and clear the inventory of the player,
     * then populate their inventory with appropriate Parkour tools.
     *
     * @param player
     * @param courseName
     */
    private static void prepareJoinPlayer(Player player, String courseName) {
        saveInventory(player);
        saveHealth(player);
        preparePlayer(player, Parkour.getInstance().getConfig().getInt("OnJoin.SetGamemode"));

        if (Parkour.getInstance().getConfig().getBoolean("OnJoin.FillHealth")) {
            player.setFoodLevel(20);
        }

        if (Parkour.getInstance().getConfig().getBoolean("OnCourse.DisableFly")) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        if (Parkour.getSettings().getLastCheckpointTool() != null && !player.getInventory().contains(Parkour.getSettings().getLastCheckpointTool())) {
            player.getInventory().setItem(Parkour.getSettings().getLastCheckPointToolSlot(), Utils.getItemStack(
                    Parkour.getSettings().getLastCheckpointTool(), Utils.getTranslation("Other.Item_LastCheckpoint", false)));
        }

        if (Parkour.getSettings().getHideallTool() != null && !player.getInventory().contains(Parkour.getSettings().getHideallTool())) {
            player.getInventory().setItem(Parkour.getSettings().getHideallToolSlot(), Utils.getItemStack(
                    Parkour.getSettings().getHideallTool(), Utils.getTranslation("Other.Item_HideAll", false)));
        }

        if (Parkour.getSettings().getLeaveTool() != null && !player.getInventory().contains(Parkour.getSettings().getLeaveTool())) {
            player.getInventory().setItem(Parkour.getSettings().getLeaveToolSlot(), Utils.getItemStack(
                    Parkour.getSettings().getLeaveTool(), Utils.getTranslation("Other.Item_Leave", false)));
        }

        if (Parkour.getSettings().getRestartTool() != null && !player.getInventory().contains(Parkour.getSettings().getRestartTool())) {
            player.getInventory().setItem(Parkour.getSettings().getRestartToolSlot(), Utils.getItemStack(
                    Parkour.getSettings().getRestartTool(), Utils.getTranslation("Other.Item_Restart", false)));
        }

        if (CourseInfo.hasJoinItem(courseName)) {
            Material joinItem = CourseInfo.getJoinItem(courseName);
            if (joinItem != null) {
                String label = CourseInfo.getJoinItemLabel(courseName);
                Integer amount = CourseInfo.getJoinItemAmount(courseName);

                ItemStack joinItemStack = Utils.getItemStack(joinItem, label, amount);
                player.getInventory().addItem(joinItemStack);
            }
        }

        player.updateInventory();
    }

    /**
     * Prepare the player for Parkour
     * Store the player's health and hunger.
     *
     * @param player
     */
    private static void saveHealth(Player player) {
        ParkourConfiguration config = Parkour.getConfig(INVENTORY);
        config.set(player.getName() + ".Health", player.getHealth());
        config.set(player.getName() + ".Hunger", player.getFoodLevel());
        config.save();
    }

    /**
     * Prepare the player for Parkour
     * Executed when the player dies, will reset them to a normal state so they can continue.
     *
     * @param player
     * @param gamemode
     */
    @SuppressWarnings("deprecation")
    public static void preparePlayer(Player player, int gamemode) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (!isPlayerInTestmode(player.getName())) {
            player.setGameMode(Utils.getGamemode(gamemode));
        }

        Damageable playerDamage = player;
        playerDamage.setHealth(playerDamage.getMaxHealth());
        player.setFallDistance(0);
        player.setFireTicks(0);
        player.eject();
    }

    /**
     * Save the player Inventory and Armour
     * Once saved, the players inventory and armour is cleared.
     * Will not overwrite the inventory if one is already saved. Can be disabled.
     *
     * @param player
     */
    public static void saveInventory(Player player) {
        if (!Parkour.getInstance().getConfig().getBoolean("Other.Parkour.InventoryManagement")) {
            return;
        }

        ParkourConfiguration config = Parkour.getConfig(INVENTORY);
        if (config.contains(player.getName() + ".Inventory")) {
            return;
        }

        PlayerInfo.saveInventoryArmor(player);

        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        player.updateInventory();
    }

    /**
     * Load the players original inventory
     * When they leave or finish a course, their inventory and armour will be restored to them.
     * Will delete the inventory from the config once loaded.
     *
     * @param player
     */
    private static void loadInventory(Player player) {
        if (!Parkour.getInstance().getConfig().getBoolean("Other.Parkour.InventoryManagement")) {
            return;
        }

        ItemStack[] inventoryContents = PlayerInfo.getSavedInventoryContents(player);
        ItemStack[] armorContents = PlayerInfo.getSavedArmorContents(player);

        if (inventoryContents == null) {
            player.sendMessage(Static.getParkourString() + "No saved inventory to load");
            return;
        }

        player.getInventory().clear();
        player.getInventory().setContents(inventoryContents);
        player.getInventory().setArmorContents(armorContents);

        player.updateInventory();

        Parkour.getConfig(INVENTORY).set(player.getName(), null);
        Parkour.getConfig(INVENTORY).save();
    }

    /**
     * Load the players original health.
     * When they leave or finish a course, their hunger and exhaustion will be restored to them.
     * Will delete the health from the config once loaded.
     *
     * @param player
     */
    private static void restoreHealth(Player player) {
        ParkourConfiguration invConfig = Parkour.getConfig(INVENTORY);

        double health = Math.min(player.getMaxHealth(), invConfig.getDouble(player.getName() + ".Health"));
        player.setHealth(health);
        player.setFoodLevel(invConfig.getInt(player.getName() + ".Hunger"));
        invConfig.set(player.getName() + ".Health", null);
        invConfig.set(player.getName() + ".Hunger", null);
        invConfig.save();
    }

    /**
     * Toggle Test Mode
     * This will enable / disable the testmode functionality, by creating a
     * dummy "Test Mode" course for the player.
     *
     * @param player
     */
    public static void toggleTestmode(String[] args, Player player) {
        if (isPlaying(player.getName())) {
            if (isPlayerInTestmode(player.getName())) {
                removePlayer(player.getName());
                Utils.sendActionBar(player, Utils.colour("Test Mode &4disabled"), true);
            } else {
                player.sendMessage(Static.getParkourString() + "You are not in Test Mode.");
            }
        } else {
            String kitName = args.length == 2 ? args[1].toLowerCase() : Constants.DEFAULT;
            ParkourKit kit = ParkourKit.getParkourKit(kitName);

            if (kit == null) {
                player.sendMessage(Static.getParkourString() + "ParkourKit " + kitName + " doesn't exist!");
            } else {
                Checkpoint checkpoint = new Checkpoint(player.getLocation(), 0, 0, 0);
                ParkourSession session = new ParkourSession(new Course(Constants.TEST_MODE, checkpoint, kit));
                addPlayer(player.getName(), session);
                Utils.sendActionBar(player, Utils.colour("Test Mode &2enabled&f. Simulating &b" + kitName + "&f ParkourKit."), true);
            }
        }
    }

    /**
     * Invite a player to the current course
     *
     * @param args
     * @param player
     */
    public static void invitePlayer(String[] args, Player player) {
        if (!isPlaying(player.getName())) {
            player.sendMessage(Static.getParkourString() + "You aren't on a course.");
            return;
        }

        Course course = CourseMethods.findByPlayer(player.getName());
        Player target = Bukkit.getPlayer(args[1]);

        if (course == null || target == null || isPlayerInTestmode(player.getName())) {
            player.sendMessage(Static.getParkourString() + "You are unable to invite right now.");
            return;
        }

        player.sendMessage(Utils.getTranslation("Parkour.Invite.Send")
                .replace("%COURSE%", course.getName())
                .replace("%TARGET%", target.getName()));
        target.sendMessage(Utils.getTranslation("Parkour.Invite.Recieve1")
                .replace("%COURSE%", course.getName())
                .replace("%PLAYER%", player.getName()));
        target.sendMessage(Utils.getTranslation("Parkour.Invite.Recieve2")
                .replace("%COURSE%", course.getName()));
    }

    /**
     * Returns whether the player is in Test Mode.
     * Used for validation, not to be treated as a normal Parkour course.
     *
     * @param playerName
     * @return boolean
     */
    public static boolean isPlayerInTestmode(String playerName) {
        ParkourSession session = getParkourSession(playerName);

        if (session == null) {
            return false;
        }

        return session.getCourse().getName().equals(Constants.TEST_MODE);
    }

    /**
     * Check if the player is currently online
     *
     * @param playerName
     * @return boolean
     */
    public static boolean isPlayerOnline(String playerName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Setup the outcome of having a Parkour Mode
     *
     * @param player
     */
    private static void setupPlayerMode(Player player) {
        ParkourSession session = getParkourSession(player.getName());

        if (session.getMode() == ParkourMode.NONE) {
            return;
        }

        if (session.getMode() == ParkourMode.FREEDOM) {
            player.sendMessage(Utils.getTranslation("Mode.Freedom.JoinText"));
            player.getInventory().addItem(Utils.getItemStack(
                    XMaterial.REDSTONE_TORCH.parseMaterial(), Utils.getTranslation("Mode.Freedom.ItemName", false)));

        } else if (session.getMode() == ParkourMode.DRUNK) {
            player.sendMessage(Utils.getTranslation("Mode.Drunk.JoinText"));

        } else if (session.getMode() == ParkourMode.DARKNESS) {
            player.sendMessage(Utils.getTranslation("Mode.Darkness.JoinText"));

        } else if (session.getMode() == ParkourMode.SPEEDY) {
            float speed = Float.parseFloat(Parkour.getInstance().getConfig().getString("ParkourModes.Speedy.SetSpeed"));
            player.setWalkSpeed(speed);

        } else if (session.getMode() == ParkourMode.ROCKETS) {
            player.sendMessage(Utils.getTranslation("Mode.Rockets.JoinText"));
            player.getInventory().addItem(Utils.getItemStack(
                    XMaterial.FIREWORK_ROCKET.parseMaterial(), Utils.getTranslation("Mode.Rockets.ItemName", false)));
        }
    }

    private static void teardownPlayerMode(Player player) {
        ParkourSession session = getParkourSession(player.getName());

        if (session.getMode() == ParkourMode.NONE) {
            return;
        }

        if (session.getMode() == ParkourMode.SPEEDY) {
            float speed = Float.parseFloat(Parkour.getInstance().getConfig().getString("ParkourModes.Speedy.ResetSpeed"));
            player.setWalkSpeed(speed);
        }
    }

    /**
     * Increase the ParkourSession checkpoint number
     * Once a player activates a new checkpoint, it will setup the next checkpoint ready.
     * A message will be sent to the player notifying them of their progression.
     *
     * @param session
     * @param player
     */
    public static void increaseCheckpoint(ParkourSession session, Player player) {
        session.increaseCheckpoint();

        boolean showTitle = Parkour.getInstance().getConfig().getBoolean("DisplayTitle.Checkpoint");
        if (session.getCourse().getCheckpoints() == session.getCheckpoint()) {
            Utils.sendSubTitle(player, Utils.getTranslation("Event.AllCheckpoints", false),
                    showTitle);
        } else {
            Utils.sendSubTitle(player, Utils.getTranslation("Event.Checkpoint", false) +
                            session.getCheckpoint() + " / " + session.getCourse().getCheckpoints(),
                    showTitle);
        }
        Bukkit.getServer().getPluginManager().callEvent(
                new PlayerAchieveCheckpointEvent(player, session.getCourse().getName(), session.getCourse().getCurrentCheckpoint()));
    }

    /**
     * Apply an effect to the player
     *
     * @param lines
     * @param player
     */
    @SuppressWarnings("deprecation")
    public static void applyEffect(String[] lines, Player player) {
        if (lines[2].equalsIgnoreCase("heal")) {
            Damageable playerDamage = player;
            playerDamage.setHealth(playerDamage.getMaxHealth());
            player.sendMessage(Static.getParkourString() + "Healed!");

        } else if (lines[2].equalsIgnoreCase("gamemode")) {
            if (Utils.doesGameModeEnumExist(lines[3].toUpperCase())) {
                GameMode gameMode = GameMode.valueOf(lines[3].toUpperCase());
                if (gameMode != player.getGameMode()) {
                    player.setGameMode(gameMode);
                    player.sendMessage(Static.getParkourString() + "GameMode set to " + Utils.standardizeText(gameMode.name()));
                }
            } else {
                player.sendMessage(Static.getParkourString() + "GameMode not recognised.");
            }

        } else {
            // if the user enters 'FIRE_RESISTANCE' or 'DAMAGE_RESIST' treat them the same
            String effect = lines[2].toUpperCase().replace("RESISTANCE", "RESIST").replace("RESIST", "RESISTANCE");
            PotionEffectType potionType = PotionEffectType.getByName(effect);

            if (potionType == null) {
                player.sendMessage(Static.getParkourString() + "Unknown Effect!");
                return;
            }
            String[] args = lines[3].split(":");
            if (args.length == 2) {
                player.addPotionEffect(new PotionEffect(potionType, Integer.parseInt(args[1]), Integer.parseInt(args[0])));
                player.sendMessage(Static.getParkourString() + potionType.getName() + " Effect Applied!");
            } else {
                player.sendMessage(Static.getParkourString() + "Invalid syntax, must follow '(duration):(strength)' example '1000:6'.");
            }
        }
    }

    public static void setLevel(String[] args, CommandSender sender) {
        if (!Validation.isPositiveInteger(args[2])) {
            sender.sendMessage(Static.getParkourString() + "Minimum level is not valid.");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(Static.getParkourString() + "That player is not online.");
            return;
        }

        int newLevel = Integer.parseInt(args[2]);
        PlayerInfo.setParkourLevel(target, newLevel);

        sender.sendMessage(Static.getParkourString() + target.getName() + "'s Level was set to " + newLevel);
    }

    public static void setRank(String[] args, CommandSender sender) {
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(Static.getParkourString() + "That player is not online.");
            return;
        }

        PlayerInfo.setRank(target, args[2]);
        sender.sendMessage(Static.getParkourString() + target.getName() + "'s Rank was set to " + args[2]);
    }

    /**
     * Display the players Parkour permissions.
     *
     * @param player
     */
    public static void displayPermissions(Player player) {
        player.sendMessage(Utils.getStandardHeading("Parkour Permissions"));
        if (player.hasPermission("Parkour.*") || player.isOp()) {
            player.sendMessage("* All Permissions");
        } else {
            boolean anyPerms = false;
            if (player.hasPermission("Parkour.Basic.*")) {
                player.sendMessage("* Basic");
                anyPerms = true;
            }
            if (player.hasPermission("Parkour.Sign.*")) {
                player.sendMessage("* Signs");
                anyPerms = true;
            }
            if (player.hasPermission("Parkour.Testmode.*")) {
                player.sendMessage("* Testmode");
                anyPerms = true;
            }
            if (player.hasPermission("Parkour.Admin.*")) {
                player.sendMessage("* Admin");
                anyPerms = true;
            }
            if (!anyPerms) {
                player.sendMessage("* You don't have any Parkour permissions.");
            }
        }
    }

    public static void rocketLaunchPlayer(Player player) {
        Vector velocity = player.getLocation().getDirection().normalize();
        velocity = velocity.multiply(-1.5);
        velocity = velocity.setY(velocity.getY() / 2);
        player.setVelocity(velocity);
        player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 500);
    }
}

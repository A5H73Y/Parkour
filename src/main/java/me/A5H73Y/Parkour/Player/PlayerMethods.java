package me.A5H73Y.Parkour.Player;

import java.util.HashMap;
import java.util.List;

import me.A5H73Y.Parkour.Course.*;
import me.A5H73Y.Parkour.Events.PlayerFinishCourseEvent;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Enums.ParkourMode;
import me.A5H73Y.Parkour.Other.Challenge;
import me.A5H73Y.Parkour.Other.Constants;
import me.A5H73Y.Parkour.Other.ParkourKit;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
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
        player.teleport(course.getCurrentCheckpoint().getLocation());
        prepareJoinPlayer(player, course.getName());
        CourseInfo.increaseView(course.getName());

        if (getParkourSession(player.getName()) == null) {
            boolean displayTitle = Parkour.getPlugin().getConfig().getBoolean("DisplayTitle.JoinCourse");

            if (course.getMaxDeaths() == null){
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
            if (!Static.containsQuiet(player.getName()))
                player.sendMessage(Utils.getTranslation("Parkour.TimeReset"));
        }

        ParkourSession session = addPlayer(player.getName(), new ParkourSession(course));
        PlayerInfo.setLastPlayedCourse(player.getName(), course.getName());
        setupPlayerMode(player);
        session.startVisualTimer(player);
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
                Parkour.getPlugin().getConfig().getBoolean("DisplayTitle.Leave"));

        teardownPlayerMode(player);
        removePlayer(player.getName());
        preparePlayer(player, Parkour.getPlugin().getConfig().getInt("OnFinish.SetGamemode"));
        loadInventory(player);

        if (Parkour.getPlugin().getConfig().getBoolean("OnDie.SetXPBarToDeathCount"))
            player.setLevel(0);

        LobbyMethods.leaveCourse(player, session);

        if (Static.containsHidden(player.getName()))
            Utils.toggleVisibility(player, true);

        Utils.forceVisible(player);
    }

    /**
     * Player dies while on a course
     * Called when the player 'dies' this can be from real events (Like falling
     * from too high), or native Parkour deaths (walking on a deathblock)
     *
     * @param player
     */
    public static void playerDie(Player player) {
        if (!isPlaying(player.getName()))
            return;

        ParkourSession session = getParkourSession(player.getName());
        session.increaseDeath();

        if (session.getCourse().hasMaxDeaths()) {
            if (session.getCourse().getMaxDeaths() > session.getDeaths()) {
                int remainingLives = session.getCourse().getMaxDeaths() - session.getDeaths();

                Utils.sendSubTitle(player, Utils.getTranslation("Parkour.LifeCount", false)
                                .replace("%AMOUNT%", String.valueOf(remainingLives)),
                        Parkour.getPlugin().getConfig().getBoolean("DisplayTitle.Death"));
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
            if (Parkour.getPlugin().getConfig().getBoolean("OnDie.ResetTimeWithNoCheckpoint")) {
                session.resetTimeStarted();
                if (!Static.containsQuiet(player.getName()))
                    player.sendMessage(Utils.getTranslation("Parkour.Die1") + Utils.getTranslation("Parkour.TimeReset", false));
            } else {
                if (!Static.containsQuiet(player.getName()))
                    player.sendMessage(Utils.getTranslation("Parkour.Die1"));
            }
        } else {
            if (!Static.containsQuiet(player.getName()))
                player.sendMessage(Utils.getTranslation("Parkour.Die2")
                        .replace("%POINT%", String.valueOf(session.getCheckpoint())));
        }

        if (Parkour.getPlugin().getConfig().getBoolean("OnDie.SetXPBarToDeathCount"))
            player.setLevel(session.getDeaths());

        //TODO sounds

        preparePlayer(player, Parkour.getPlugin().getConfig().getInt("OnJoin.SetGamemode"));
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
        if (!isPlaying(player.getName()))
            return;

        if (isPlayerInTestmode(player.getName()))
            return;

        ParkourSession session = getParkourSession(player.getName());
        final String courseName = session.getCourse().getName();
        final long timeTaken = session.getTime();

        if (Parkour.getPlugin().getConfig().getBoolean("OnFinish.EnforceCompletion")
                && session.getCheckpoint() != (session.getCourse().getCheckpoints())) {

            player.sendMessage(Utils.getTranslation("Error.Cheating1"));
            player.sendMessage(Utils.getTranslation("Error.Cheating2", false)
                    .replace("%AMOUNT%", String.valueOf(session.getCourse().getCheckpoints())));
            playerDie(player);
            return;
        }

        preparePlayer(player, Parkour.getPlugin().getConfig().getInt("OnFinish.SetGamemode"));

        if (Static.containsHidden(player.getName()))
            Utils.toggleVisibility(player, true);

        displayFinishMessage(player, session);
        CourseInfo.increaseComplete(courseName);
        teardownPlayerMode(player);
        removePlayer(player.getName());

        if (Parkour.getPlugin().getConfig().getBoolean("OnDie.SetXPBarToDeathCount"))
            player.setLevel(0);
        
        Long delay = Parkour.getPlugin().getConfig().getLong("OnFinish.TeleportDelay");
        if (delay <= 0 || !Parkour.getPlugin().getConfig().getBoolean("OnFinish.TeleportAway")) {
            loadInventory(player);
            givePrize(player, courseName);
        }
        if (Parkour.getPlugin().getConfig().getBoolean("OnFinish.TeleportAway")) {
            if (delay > 0) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Parkour.getPlugin(), new Runnable() {
                    public void run() {
                    	loadInventory(player);
                    	givePrize(player, courseName);
                    	courseCompleteLocation(player, courseName);
                    }
                }, delay);

            } else {
                courseCompleteLocation(player, courseName);
            }
        }

        if (Parkour.getPlugin().getConfig().getBoolean("OnFinish.UpdatePlayerDatabaseTime")) {
            DatabaseMethods.updateTime(courseName, player, timeTaken, session.getDeaths());
        } else {
            DatabaseMethods.insertTime(courseName, player.getName(), timeTaken, session.getDeaths());
        }

        PlayerInfo.setLastCompletedCourse(player.getName(), courseName);

        PlayerFinishCourseEvent finishEvent = new PlayerFinishCourseEvent(player, courseName);
        Bukkit.getServer().getPluginManager().callEvent(finishEvent);
        Utils.forceVisible(player);
    }

    /**
     * Teleport player after course completion
     * Based on the linked course or lobby
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

            if (Parkour.getPlugin().getConfig().contains("Lobby." + lobbyName + ".World")) {
                String[] args = { null, lobbyName };
                LobbyMethods.joinLobby(args, player);
                return;
            }
        }

        LobbyMethods.joinLobby(null, player);
    }

    /**
     * Display the course finish information
     * Will send to the chosen amount of players
     * @param player
     * @param session
     */
    private static void displayFinishMessage(Player player, ParkourSession session) {
        if (Parkour.getPlugin().getConfig().getBoolean("OnFinish.DisplayStats")) {
            Utils.sendFullTitle(player,
                    Utils.getTranslation("Parkour.FinishCourse1", false)
                            .replace("%COURSE%", session.getCourse().getName()),
                    Utils.getTranslation("Parkour.FinishCourse2", false)
                            .replace("%DEATHS%", String.valueOf(session.getDeaths()))
                            .replace("%TIME%", session.displayTime()),
                    Parkour.getPlugin().getConfig().getBoolean("DisplayTitle.Finish"));
        }

        String finishBroadcast = Utils.getTranslation("Parkour.FinishBroadcast")
                .replace("%PLAYER%", player.getName())
                .replace("%COURSE%", session.getCourse().getName())
                .replace("%DEATHS%", String.valueOf(session.getDeaths()))
                .replace("%TIME%", session.displayTime());

        switch (Parkour.getPlugin().getConfig().getInt("OnFinish.BroadcastLevel")) {
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
        if (!Parkour.getPlugin().getConfig().getBoolean("OnFinish.Prize.Enabled"))
            return;

        if (CourseInfo.getRewardOnce(courseName) &&
                DatabaseMethods.hasPlayerCompleted(player.getName(), courseName))
            return;

        Material material;
        int amount;

        // Use Custom prize
        if (CourseInfo.hasMaterialPrize(courseName)) {
            material = CourseInfo.getMaterialPrize(courseName);
            amount = CourseInfo.getMaterialPrizeAmount(courseName);
        } else {
            material = Material.getMaterial(Parkour.getPlugin().getConfig().getString("OnFinish.DefaultPrize.Material"));
            amount = Parkour.getPlugin().getConfig().getInt("OnFinish.DefaultPrize.Amount", 0);
        }

        if (material != null && amount > 0)
            player.getInventory().addItem(new ItemStack(material, amount));

        // Give XP to player
        int xp = CourseInfo.getXPPrize(courseName);

        if (xp == 0)
            xp = Parkour.getPlugin().getConfig().getInt("OnFinish.DefaultPrize.XP");

        if (xp > 0)
            player.giveExp(xp);

        // Level player
        int rewardLevel = CourseInfo.getRewardLevel(courseName);
        if (rewardLevel > 0) {
            int current = PlayerInfo.getParkourLevel(player.getName());

            if (current < rewardLevel) {
                PlayerInfo.setParkourLevel(player.getName(), rewardLevel);
                if (Parkour.getPlugin().getConfig().getBoolean("Other.Display.LevelReward")) {
                    player.sendMessage(Utils.getTranslation("Parkour.RewardLevel")
                            .replace("%LEVEL%", String.valueOf(rewardLevel))
                            .replace("%COURSE%", courseName));
                }
            }
        }
        // Level increment
        int addLevel = CourseInfo.getRewardLevelAdd(courseName);
        if (addLevel > 0) {
            int newLevel = PlayerInfo.getParkourLevel(player.getName()) + addLevel;

            PlayerInfo.setParkourLevel(player.getName(), newLevel);
            player.sendMessage(Utils.getTranslation("Parkour.RewardLevel")
                    .replace("%LEVEL%", String.valueOf(newLevel))
                    .replace("%COURSE%", courseName));
        }

        // check if there is a rank upgrade
        // update - this should be based on their new level, and not the course level
        int newLevel = PlayerInfo.getParkourLevel(player.getName());

        String rewardRank = CourseInfo.getRewardRank(newLevel);
        if (rewardRank != null) {
            PlayerInfo.setRank(player.getName(), rewardRank);
            player.sendMessage(Utils.colour(Utils.getTranslation("Parkour.RewardRank").replace("%RANK%", rewardRank)));
        }

        // Execute the command
        if (CourseInfo.hasCommandPrize(courseName)) {
            Parkour.getPlugin().getServer().dispatchCommand(
                    Parkour.getPlugin().getServer().getConsoleSender(),
                    CourseInfo.getCommandPrize(courseName)
                            .replace("%PLAYER%", player.getName()));
        }

        // Give player Parkoins
        int parkoins = CourseInfo.getRewardParkoins(courseName);
        if (parkoins > 0)
            PlayerMethods.rewardParkoins(player, parkoins);

        giveEconomyPrize(player, courseName);

        player.updateInventory();
        Parkour.getParkourConfig().saveUsers();
    }

    /**
     * Increase the amount of Parkoins the player has by the parameter value.
     * @param player
     * @param parkoins
     */
    public static void rewardParkoins(Player player, int parkoins) {
        if (parkoins <= 0)
            return;

        int total = parkoins + PlayerInfo.getParkoins(player.getName());
        PlayerInfo.setParkoins(player.getName(), total);
        player.sendMessage(Utils.getTranslation("Parkour.RewardParkoins")
                .replace("%AMOUNT%", String.valueOf(parkoins))
                .replace("%TOTAL", String.valueOf(total)));
    }

    /**
     * Decrease the amount of Parkoins the player has by the parameter value.
     * @param player
     * @param parkoins
     */
    public static void deductParkoins(Player player, int parkoins) {
        if (parkoins <= 0)
            return;

        int current = PlayerInfo.getParkoins(player.getName());
        current = (current < parkoins) ? 0 : (current - parkoins);

        PlayerInfo.setParkoins(player.getName(), current);
        player.sendMessage(Static.getParkourString() + parkoins + " Parkoins deducted! New total: " + ChatColor.AQUA + current);
    }

    /**
     * Give the player the economy prize for the course.
     * @param player
     * @param courseName
     */
    private static void giveEconomyPrize(Player player, String courseName) {
        if (!Static.getEconomy())
            return;

        int reward = CourseInfo.getEconomyFinishReward(courseName);

        if (reward > 0) {
            Parkour.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), reward);
            String currencyName = Parkour.getEconomy().currencyNamePlural() == null ?
                    "" : " " + Parkour.getEconomy().currencyNamePlural();

            player.sendMessage(Utils.getTranslation("Economy.Reward")
                    .replace("%AMOUNT%", reward + currencyName)
                    .replace("%COURSE%", courseName));
        }
    }

    /**
     * Retrieve ParkourSession for player based on their name.
     * @param playerName
     * @return ParkourSession
     */
    public static ParkourSession getParkourSession(String playerName) {
        if (isPlaying(playerName))
            return parkourPlayers.get(playerName);

        return null;
    }

    /**
     * Return if a player is on a course
     * @param playerName
     * @return boolean
     */
    public static boolean isPlaying(String playerName) {
        return parkourPlayers.get(playerName) != null;
    }

    /**
     * Get the Map of players using the plugin
     * @return HashMap<playerName, ParkourSession>
     */
    public static HashMap<String, ParkourSession> getPlaying() {
        return parkourPlayers;
    }

    /**
     * Overwrite the playing players, populates when the plugin starts
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
        String playerName = args.length <= 1 ? player.getName() : args[1];

        ParkourSession session = PlayerMethods.getParkourSession(playerName);

        if (session == null && !PlayerInfo.hasPlayerInfo(playerName)) {
            player.sendMessage(Static.getParkourString() + "Player has never played Parkour. What is wrong with them?!");
            return;
        }

        player.sendMessage(Utils.getStandardHeading(playerName + "'s information"));

        if (session != null) {
            player.sendMessage("Course: " + ChatColor.AQUA + session.getCourse().getName());
            player.sendMessage("Deaths: " + ChatColor.AQUA + session.getDeaths());
            player.sendMessage("Time: " + ChatColor.AQUA + session.displayTime());
            player.sendMessage("Checkpoint: " + ChatColor.AQUA + session.getCheckpoint());
        }

        if (PlayerInfo.hasPlayerInfo(playerName)) {
            int level = PlayerInfo.getParkourLevel(playerName);
            String selected = PlayerInfo.getSelected(playerName);

            if (level > 0)
                player.sendMessage("Level: " + ChatColor.AQUA + level);

            if (selected != null && selected.length() > 0)
                player.sendMessage("Editing: " + ChatColor.AQUA + selected);

            if (PlayerInfo.getParkoins(playerName) > 0)
                player.sendMessage("Parkoins: " + ChatColor.AQUA + PlayerInfo.getParkoins(playerName));
        }
    }

    /**
     * Add a player and their session to the playing players.
     * @param playerName
     * @param session
     */
    private static ParkourSession addPlayer(String playerName, ParkourSession session) {
        parkourPlayers.put(playerName, session);
        return session;
    }

    /**
     * Remove a player and their session from the playing players.
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
        player.getInventory().clear();
        ParkourKit kit;

        if (args != null && args.length == 2) {
            kit = ParkourKit.getParkourKit(args[1].toLowerCase());
            if (kit == null) {
                player.sendMessage(Static.getParkourString() + "Invalid ParkourKit: " + ChatColor.RED + args[1]);
                return;
            }
        } else {
            kit = ParkourKit.getParkourKit(Constants.DEFAULT);
        }

        for (Material material : kit.getMaterials()) {
            String action = kit.getAction(material);

            if (action == null)
                continue;

            action = Utils.standardizeText(action);

            ItemStack s = new ItemStack(material);
            ItemMeta m = s.getItemMeta();
            m.setDisplayName(Utils.getTranslation("Kit." + action, false));
            s.setItemMeta(m);
            player.getInventory().addItem(s);
        }

        ItemStack s = new ItemStack(Material.SIGN);
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
     * @param player
     * @param courseName
     */
    private static void prepareJoinPlayer(Player player, String courseName) {
        saveInventory(player);
        preparePlayer(player, Parkour.getPlugin().getConfig().getInt("OnJoin.SetGamemode"));

        if (Parkour.getPlugin().getConfig().getBoolean("OnJoin.FillHealth"))
            player.setFoodLevel(20);

        if (Parkour.getPlugin().getConfig().getBoolean("OnCourse.DisableFly")) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        if (Parkour.getSettings().getLastCheckpoint() != null && !player.getInventory().contains(Parkour.getSettings().getLastCheckpoint()))
            player.getInventory().addItem(Utils.getItemStack(
                    Parkour.getSettings().getLastCheckpoint(), Utils.getTranslation("Other.Item_LastCheckpoint", false)));

        if (Parkour.getSettings().getHideall() != null && !player.getInventory().contains(Parkour.getSettings().getHideall()))
            player.getInventory().addItem(Utils.getItemStack(
                    Parkour.getSettings().getHideall(), Utils.getTranslation("Other.Item_HideAll", false)));

        if (Parkour.getSettings().getLeave() != null && !player.getInventory().contains(Parkour.getSettings().getLeave()))
            player.getInventory().addItem(Utils.getItemStack(
                    Parkour.getSettings().getLeave(), Utils.getTranslation("Other.Item_Leave", false)));

        if (CourseInfo.hasJoinItem(courseName)){
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
     * Executed when the player dies, will reset them to a normal state so they can continue.
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
    }

    /**
     * Save the player Inventory and Armour
     * Once saved, the players inventory and armour is cleared.
     * Will not overwrite the inventory if one is already saved. Can be disabled.
     *
     * @param player
     */
    public static void saveInventory(Player player) {
        if (!Parkour.getPlugin().getConfig().getBoolean("Other.Parkour.InventoryManagement"))
            return;

        if (Parkour.getParkourConfig().getInvData().contains(player.getName() + ".Inventory"))
            return;

        Parkour.getParkourConfig().getInvData().set(player.getName() + ".Inventory", player.getInventory().getContents());
        Parkour.getParkourConfig().getInvData().set(player.getName() + ".Armor", player.getInventory().getArmorContents());
        Parkour.getParkourConfig().saveInv();
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
    public static void loadInventory(Player player) {
        if (!Parkour.getPlugin().getConfig().getBoolean("Other.Parkour.InventoryManagement"))
            return;

        Object a = Parkour.getParkourConfig().getInvData().get(player.getName() + ".Inventory");
        Object b = Parkour.getParkourConfig().getInvData().get(player.getName() + ".Armor");

        if (a == null) {
            player.sendMessage(Static.getParkourString() + "No saved inventory to load");
            return;
        }

        ItemStack[] inventory = null;
        ItemStack[] armor = null;
        if (a instanceof ItemStack[]) {
            inventory = (ItemStack[]) a;
        } else if (a instanceof List) {
            List<?> lista = (List<?>) a;
            inventory = (ItemStack[]) lista.toArray(new ItemStack[0]);
        }
        if (b instanceof ItemStack[]) {
            armor = (ItemStack[]) b;
        } else if (b instanceof List) {
            List<?> listb = (List<?>) b;
            armor = (ItemStack[]) listb.toArray(new ItemStack[0]);
        }
        player.getInventory().clear();
        player.getInventory().setContents(inventory);
        player.getInventory().setArmorContents(armor);

        player.updateInventory();

        Parkour.getParkourConfig().getInvData().set(player.getName(), null);
        Parkour.getParkourConfig().saveInv();
    }

    /**
     * Toggle quiet mode
     * Will add / remove the player from the list of quiet players.
     * If enabled, will limit the amount of Parkour messages displayed to the player.
     *
     * @param player
     */
    public static void toggleQuiet(Player player) {
        if (Static.containsQuiet(player.getName()))
            Static.removeQuiet(player);
        else
            Static.addQuiet(player);
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
                ParkourSession session = new ParkourSession(new Course(Constants.TEST_MODE, checkpoint));
                session.getCourse().setParkourKit(kit);
                addPlayer(player.getName(), session);
                Utils.sendActionBar(player, Utils.colour("Test Mode &2enabled&f. Simulating &b" + kitName + "&f ParkourKit."), true);
            }
        }
    }

    /**
     * Invite a player to the current course
     * @param args
     * @param player
     */
    public static void invitePlayer(String[] args, Player player) {
        if (!isPlaying(player.getName())){
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
     * @param playerName
     * @return boolean
     */
    public static boolean isPlayerInTestmode(String playerName) {
        ParkourSession session = getParkourSession(playerName);

        if (session == null)
            return false;

        return session.getCourse().getName().equals(Constants.TEST_MODE);
    }

    /**
     * Check if the player is currently online
     * @param playerName
     * @return boolean
     */
    public static boolean isPlayerOnline(String playerName) {
        for (Player player : Bukkit.getOnlinePlayers()){
            if (player.getName().equalsIgnoreCase(playerName))
                return true;
        }

        return false;
    }

    /**
     * Setup the outcome of having a Parkour Mode
     * @param player
     */
    private static void setupPlayerMode(Player player) {
        ParkourSession session = getParkourSession(player.getName());

        if (session.getMode() == ParkourMode.NONE)
            return;

        if (session.getMode() == ParkourMode.FREEDOM) {
            player.sendMessage(Utils.getTranslation("Mode.Freedom.JoinText"));
            player.getInventory().addItem(Utils.getItemStack(
                    Material.REDSTONE_TORCH_ON, Utils.getTranslation("Mode.Freedom.ItemName", false)));

        } else if (session.getMode() == ParkourMode.DRUNK) {
            player.sendMessage(Utils.getTranslation("Mode.Drunk.JoinText"));

        } else if (session.getMode() == ParkourMode.DARKNESS) {
            player.sendMessage(Utils.getTranslation("Mode.Darkness.JoinText"));

        } else if (session.getMode() == ParkourMode.SPEEDY) {
            float speed = Float.valueOf(Parkour.getPlugin().getConfig().getString("ParkourModes.Speedy.SetSpeed"));
            player.setWalkSpeed(speed);
        }
    }

    private static void teardownPlayerMode(Player player) {
        ParkourSession session = getParkourSession(player.getName());

        if (session.getMode() == ParkourMode.NONE)
            return;

        if (session.getMode() == ParkourMode.SPEEDY) {
            float speed = Float.valueOf(Parkour.getPlugin().getConfig().getString("ParkourModes.Speedy.ResetSpeed"));
            player.setWalkSpeed(speed);
        }
    }

    /**
     * Accept a challenge
     * Executed by the recipient of a challenge invite.
     * Will prepare each player for the challenge.
     *
     * @param targetPlayer
     */
    public static void acceptChallenge(final Player targetPlayer){
        Challenge challenge = Challenge.getChallenge(targetPlayer.getName());

        if (challenge == null){
            targetPlayer.sendMessage(Static.getParkourString() + "You have not been invited!");
            return;
        }
        if (!PlayerMethods.isPlayerOnline(challenge.getPlayer())){
            targetPlayer.sendMessage(Static.getParkourString() + "Player is not online!");
            return;
        }

        Challenge.removeChallenge(challenge);
        final Player player = Bukkit.getPlayer(challenge.getPlayer());

        if (Parkour.getPlugin().getConfig().getBoolean("ParkourModes.Challenge.hidePlayers")){
            player.hidePlayer(targetPlayer);
            targetPlayer.hidePlayer(player);
        }

        CourseMethods.joinCourse(player, challenge.getCourseName());
        CourseMethods.joinCourse(targetPlayer, challenge.getCourseName());

        final float playerSpeed = player.getWalkSpeed();
        final float targetSpeed = targetPlayer.getWalkSpeed();

        player.setWalkSpeed(0f);
        targetPlayer.setWalkSpeed(0f);

        new Runnable() {
            public int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Parkour.getPlugin(), this, 0L, 20L);

            int count = 6;
            @Override
            public void run() {
                if (count > 1) {
                    count--;

                    player.sendMessage("Starting in " + count + " seconds...");
                    targetPlayer.sendMessage("Starting in " + count + " seconds...");
                } else {
                    Bukkit.getScheduler().cancelTask(taskID);
                    player.sendMessage("Go!");
                    targetPlayer.sendMessage("Go!");
                    player.setWalkSpeed(playerSpeed);
                    targetPlayer.setWalkSpeed(targetSpeed);
                }
            }
        };
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

        boolean showTitle = Parkour.getPlugin().getConfig().getBoolean("DisplayTitle.Checkpoint");
        if (session.getCourse().getCheckpoints() == session.getCheckpoint()) {
            Utils.sendSubTitle(player, Utils.getTranslation("Event.AllCheckpoints", false),
                    showTitle);
        } else {
            Utils.sendSubTitle(player, Utils.getTranslation("Event.Checkpoint", false) +
                            session.getCheckpoint() + " / " + session.getCourse().getCheckpoints(),
                    showTitle);
        }
    }

    /**
     * Apply an effect to the player
     * @param lines
     * @param player
     */
    @SuppressWarnings("deprecation")
    public static void applyEffect(String[] lines, Player player) {
        if (lines[2].equalsIgnoreCase("heal")) {
            Damageable playerDamage = player;
            playerDamage.setHealth(playerDamage.getMaxHealth());
            player.sendMessage(Static.getParkourString() + "Healed!");

        } else if (lines[2].equalsIgnoreCase("jump")) {
            if (Utils.isNumber(lines[3])) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, Integer.parseInt(lines[3])));
                player.sendMessage(Static.getParkourString() + "Jump Effect Applied!");
            } else {
                player.sendMessage(Static.getParkourString() + "Invalid Number");
            }
        } else if (lines[2].equalsIgnoreCase("speed")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 6));
            player.sendMessage(Static.getParkourString() + "Speed Effect Applied!");

        } else if (lines[2].equalsIgnoreCase("fire")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 500, 6));
            player.sendMessage(Static.getParkourString() + "Fire Resistance Applied!");

        } else if (lines[2].equalsIgnoreCase("pain")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 500, 10));
            player.sendMessage(Static.getParkourString() + "Pain Resistance Applied!");

        } else if (lines[2].equalsIgnoreCase("gamemode")) {
            if (lines[3].equalsIgnoreCase("creative")) {
                if (!(player.getGameMode().equals(GameMode.CREATIVE))) {
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(Static.getParkourString() + "GameMode set to Creative!");
                }
            } else {
                if (!(player.getGameMode().equals(GameMode.SURVIVAL))) {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(Static.getParkourString() + "GameMode set to Survival!");
                }
            }
        } else {
            player.sendMessage(Static.getParkourString() + "Unknown Effect!");
        }
    }

    public static void setLevel(String[] args, CommandSender sender) {
        if (!Utils.isNumber(args[2])) {
            sender.sendMessage(Static.getParkourString() + "Minimum level is not valid.");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(Static.getParkourString() + "That player is not online.");
            return;
        }

        int newLevel = Integer.valueOf(args[2]);
        PlayerInfo.setParkourLevel(target.getName(), newLevel);

        sender.sendMessage(Static.getParkourString() + target.getName() + "'s Level was set to " + newLevel);
    }

    public static void setRank(String[] args, CommandSender sender) {
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(Static.getParkourString() + "That player is not online.");
            return;
        }

        PlayerInfo.setRank(target.getName(), args[2]);
        sender.sendMessage(Static.getParkourString() + target.getName() + "'s Rank was set to " + args[2]);
    }

    /**
     * Display the players Parkour permissions.
     * @param player
     */
    public static void displayPermissions(Player player) {
        player.sendMessage(Utils.getStandardHeading("Parkour Permissions"));
        if (player.hasPermission("Parkour.*") || player.isOp()) {
            player.sendMessage("* Everything");
        } else {
            boolean anyPerms = false;
            if (player.hasPermission("Parkour.Basic.*")) {
                player.sendMessage("* Basic");
                anyPerms = true;
            }
            if (player.hasPermission("Parkour.Signs.*")) {
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
            if (!anyPerms)
                player.sendMessage("* You don't have any Parkour permissions.");
        }
    }
}

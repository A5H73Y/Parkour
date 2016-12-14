package me.A5H73Y.Parkour.Player;

import java.util.HashMap;
import java.util.List;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.Checkpoint;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Other.Challenge;
import me.A5H73Y.Parkour.Other.ParkourBlocks;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class PlayerMethods {

	private static HashMap<String, ParkourSession> parkourPlayers = new HashMap<String, ParkourSession>();

	/**
	 * This method is only called from the CourseMethods after course
	 * validation. It will retrieve a course object which will then be
	 * referenced against the player. We prepare the player for the course here.
	 * 
	 * @param player
	 * @param course
	 */
	public static void playerJoin(Player player, Course course) {
		prepareJoinPlayer(player, course.getName());
		player.teleport(course.getCheckpoint().getLocation());
		CourseMethods.increaseView(course.getName());

		if (getParkourSession(player.getName()) == null) {
			if (course.getMaxDeaths() == null){
				Utils.sendTitle(player, Utils.getTranslation("Parkour.Join", false).replace("%COURSE%", course.getName()));
			} else {
				Utils.sendFullTitle(player, Utils.getTranslation("Parkour.Join", false).replace("%COURSE%", course.getName()), 
						Utils.getTranslation("Parkour.JoinLives", false).replace("%AMOUNT%", course.getMaxDeaths().toString()));
			}

			addPlayer(player.getName(), new ParkourSession(course));
		} else {
			removePlayer(player.getName());
			addPlayer(player.getName(), new ParkourSession(course));
			if (!Static.containsQuiet(player.getName()))
				player.sendMessage(Utils.getTranslation("Parkour.TimeReset"));
		}
	}

	/**
	 * Called when the player requests to leave a course. Will remove the player
	 * from the players which will also dispose of their course session.
	 * 
	 * @param player
	 */
	public static void playerLeave(Player player) {
		if (!isPlaying(player.getName())) {
			player.sendMessage(Static.getParkourString() + "You aren't on a course.");
			return;
		}

		ParkourSession session = getParkourSession(player.getName());
		Utils.sendSubTitle(player, Utils.getTranslation("Parkour.Leave", false).replace("%COURSE%", session.getCourse().getName()));

		removePlayer(player.getName());
		preparePlayer(player, Parkour.getParkourConfig().getConfig().getInt("Other.onFinish.Gamemode"));
		CourseMethods.joinLobby(null, player);
		loadInventory(player);
	}

	/**
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

		if (session.getCourse().getMaxDeaths() != null) {
			if (session.getCourse().getMaxDeaths() > session.getDeaths()) {
				Utils.sendActionBar(player, Utils.getTranslation("Parkour.LifeCount", false).replace("%AMOUNT%", String.valueOf(session.getCourse().getMaxDeaths() - session.getDeaths())));
			} else {
				player.sendMessage(Utils.getTranslation("Parkour.MaxDeaths").replace("%AMOUNT%", session.getCourse().getMaxDeaths().toString()));
				playerLeave(player);
				return;
			}
		}

		player.teleport(session.getCourse().getCheckpoint().getLocation());

		// if it's the first checkpoint
		if (session.getCheckpoint() == 0) {
			if (Parkour.getParkourConfig().getConfig().getBoolean("OnDie.ResetTimeWithNoCheckpoint")) {
				session.resetTimeStarted();
				if (!Static.containsQuiet(player.getName()))
					player.sendMessage(Utils.getTranslation("Parkour.Die1") + Utils.getTranslation("Parkour.TimeReset", false));
			} else {
				if (!Static.containsQuiet(player.getName()))
					player.sendMessage(Utils.getTranslation("Parkour.Die1"));
			}
		} else {
			if (!Static.containsQuiet(player.getName()))
				player.sendMessage(Utils.getTranslation("Parkour.Die2").replace("%POINT%", String.valueOf(session.getCheckpoint())));
		}

		if (Parkour.getParkourConfig().getConfig().getBoolean("Other.onDie.SetAsXPBar"))
			player.setLevel(session.getDeaths());

		if (Parkour.getParkourConfig().getConfig().getBoolean("Other.Use.Sounds")) // TODO
			// Experiment
			// with
			// sounds
			player.playSound(player.getLocation(), Sound.ENCHANT_THORNS_HIT, 1L, 1L);

		preparePlayer(player, 0);
	}

	/**
	 * This will be called when the player completes the course. Their reward
	 * will be given here.
	 * 
	 * @param player
	 */
	public static void playerFinish(Player player) {
		if (!isPlaying(player.getName()))
			return;

		if (isPlayerInTestmode(player.getName()))
			return;

		ParkourSession session = getParkourSession(player.getName());
		String courseName = session.getCourse().getName();

		if (Parkour.getParkourConfig().getConfig().getBoolean("OnFinish.EnforceCompletion") && session.getCheckpoint() != (session.getCourse().getCheckpoints())) {
			player.sendMessage(Utils.getTranslation("Error.Cheating1"));
			player.sendMessage(Utils.getTranslation("Error.Cheating2", false).replace("%AMOUNT%", session.getCourse().getCheckpoints()+""));
			playerDie(player);
			return;
		}

		preparePlayer(player, Parkour.getParkourConfig().getConfig().getInt("OnFinish.SetGamemode"));
		loadInventory(player);

		if (Static.containsHidden(player.getName()))
			toggleVisibility(player, true);

		givePrize(player, courseName);
		displayFinishMessage(player, session);
		CourseMethods.increaseComplete(courseName);
		removePlayer(player.getName());

		DatabaseMethods.insertTime(courseName, player.getName(), session.getTime(), session.getDeaths());

		if (Parkour.getParkourConfig().getConfig().getBoolean("OnFinish.TeleportToLobby")) {
			if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".LinkedCourse")) {
				String linkedCourseName = Parkour.getParkourConfig().getCourseData().getString(courseName + ".LinkedCourse").toLowerCase();

				if (CourseMethods.exist(linkedCourseName)) {
					CourseMethods.joinCourse(player, linkedCourseName);
					return;
				}
			} else if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".LinkedLobby")) {
				String lobbyName = Parkour.getParkourConfig().getCourseData().getString(courseName + ".LinkedLobby");

				if (Parkour.getParkourConfig().getConfig().contains("Lobby." + lobbyName + ".World")) {
					String[] args = { null, lobbyName };
					CourseMethods.joinLobby(args, player);
					return;
				}
			}

			CourseMethods.joinLobby(null, player);
		}

		Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".LastPlayed", courseName);
		Parkour.getParkourConfig().saveUsers();

		// TODO find the best order to run these.
		// i.e player can not be playing when joining lobby
	}

	private static void displayFinishMessage(Player player, ParkourSession session) {
		String finishBroadcast = Static.getParkourString() + Utils.colour(Parkour.getParkourConfig().getStringData().getString("Parkour.FinishBroadcast").replace("%PLAYER%", player.getName()).replace("%COURSE%", session.getCourse().getName()).replace("%DEATHS%", String.valueOf(session.getDeaths())).replace("%TIME%", session.displayTime()));

		switch (Parkour.getParkourConfig().getConfig().getInt("OnFinish.BroadcastLevel")) {
		case 3:
			for (Player players : Bukkit.getServer().getOnlinePlayers()) {
				players.sendMessage(finishBroadcast);
			}
			break;
		case 2:
			for (Player players : Bukkit.getServer().getOnlinePlayers()) {
				if (PlayerMethods.isPlaying(players.getPlayerListName())) {
					players.sendMessage(finishBroadcast);
				}
			}
			break;
		case 1:
		default:
			player.sendMessage(finishBroadcast);
		}
		if (Parkour.getParkourConfig().getConfig().getBoolean("OnFinish.TeleportToLobby")) {
			player.sendMessage("Teleporting to lobby....");
			/** Allow player to bask in the glory of completing course before teleport to lobby **/
			Utils.sleep(3500);
		}
	}

	/**
	 * Reward a player with several forms of prize after course completion.
	 * 
	 * @param player
	 * @param courseName
	 */
	private static void givePrize(Player player, String courseName) {
		// Give player items
		if (!Parkour.getParkourConfig().getConfig().getBoolean("OnFinish.Prize.Enabled"))
			return;

		if (Parkour.getParkourConfig().getCourseData().getBoolean(courseName + ".FirstReward"))
			if (DatabaseMethods.hasPlayerCompleted(player.getName(), courseName))
				return;

		int amount = 0;
		Material material;

		// Use Custom prize
		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".Prize.Material")) {
			material = Material.getMaterial(Parkour.getParkourConfig().getCourseData().getString(courseName + ".Prize.Material"));
			amount = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Prize.Amount");
		} else {
			material = Material.getMaterial(Parkour.getParkourConfig().getConfig().getString("OnFinish.DefaultPrize.Material"));
			amount = Parkour.getParkourConfig().getConfig().getInt("OnFinish.DefaultPrize.Amount");
		}

		if (amount < 0)
			amount = 1;

		if (material != null)
			player.getInventory().addItem(new ItemStack(material, amount));

		// Give XP to player
		int xp = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Prize.XP");

		if (xp == 0)
			xp = Parkour.getParkourConfig().getConfig().getInt("OnFinish.DefaultPrize.XP");

		if (xp > 0)
			player.giveExp(xp);

		// Level player
		int rewardLevel = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Level");
		if (rewardLevel > 0) {
			int current = Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Level");

			if (current < rewardLevel) {
				Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Level", rewardLevel);
				player.sendMessage(Utils.getTranslation("Parkour.RewardLevel").replace("%LEVEL%", rewardLevel+"").replace("%COURSE%", courseName));
				
				// check if there is a rank upgrade
				String rewardRank = Parkour.getParkourConfig().getUsersData().getString("ServerInfo.Levels." + rewardLevel + ".Rank");
				if (rewardRank != null) {
					Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Rank", rewardRank);
					player.sendMessage(Utils.getTranslation("Parkour.RewardRank").replace("%RANK%", rewardRank));
				}
			}
		}

		// Execute the command
		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".Prize.CMD")) {
			Parkour.getPlugin().getServer().dispatchCommand(Parkour.getPlugin().getServer().getConsoleSender(), Parkour.getParkourConfig().getCourseData().getString(courseName + ".Prize.CMD").replace("%PLAYER%", player.getName()));
		}

		// Give player Parkoins
		int parkoins = Parkour.getParkourConfig().getCourseData().getInt(courseName + ".Parkoins");
		if (parkoins > 0)
			PlayerMethods.rewardParkoins(player, parkoins);

		giveEconomyPrize(player, courseName);

		player.updateInventory();
		Parkour.getParkourConfig().saveUsers();
	}

	public static void rewardParkoins(Player player, int parkoins) {
		int total = parkoins + Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Parkoins");
		Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Parkoins", total);
		player.sendMessage(Static.getParkourString() + parkoins + " Parkoins rewarded! New total: " + ChatColor.AQUA + total);
	}

	public static void deductParkoins(Player player, int parkoins) {
		if (parkoins <= 0)
			return;

		int current = Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + player.getName() + ".Parkoins");
		current = (current < parkoins) ? 0 : (current - parkoins);

		Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName() + ".Parkoins", current);
		player.sendMessage(Static.getParkourString() + parkoins + " Parkoins deducted! New total: " + ChatColor.AQUA + current);
	}

	private static void giveEconomyPrize(Player player, String courseName) {
		if (!Static.getEconomy())
			return;

		int reward = Parkour.getParkourConfig().getEconData().getInt("Price." + courseName + ".Finish");

		if (reward > 0) {
			Parkour.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), reward);
			player.sendMessage(Utils.getTranslation("Economy.Reward").replace("%AMOUNT%", reward + " " + Parkour.getEconomy().currencyNamePlural()).replace("%COURSE%", courseName));
		}
	}

	/**
	 * The following methods are used throughout the plugin. Please see the
	 * ParkourSession object for more information. Will return null if player isn't
	 * playing.
	 * 
	 * @param playerName
	 * @return
	 */
	public static ParkourSession getParkourSession(String playerName) {
		if (isPlaying(playerName))
			return parkourPlayers.get(playerName);

		return null;
	}

	public static boolean isPlaying(String playerName) {
		return parkourPlayers.get(playerName) != null;
	}

	public static HashMap<String, ParkourSession> getPlaying() {
		return parkourPlayers;
	}

	public static void setPlaying(HashMap<String, ParkourSession> players) {
		parkourPlayers = players;
	}

	/**
	 * This is new as of 4.0. Thanks to the new system we can easily see what
	 * the player (or another player) is doing with the plugin. We lookup their
	 * ParkourSession object and interrogate it. We also check their offline stats from
	 * the config. (Their level etc.)
	 * 
	 * @param args
	 * @param player
	 */
	public static void displayPlayerInfo(String[] args, Player player) {
		String playerName = args.length <= 1 ? player.getName() : args[1];

		ParkourSession session = PlayerMethods.getParkourSession(playerName);

		if (session == null && !Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + playerName)) {
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

		if (Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + playerName)) {
			int level = Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + playerName + ".Level");
			String selected = Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + playerName + ".Selected");

			if (level > 0)
				player.sendMessage("Level: " + ChatColor.AQUA + level);

			if (selected != null && selected.length() > 0)
				player.sendMessage("Editing: " + ChatColor.AQUA + selected);

			if (getParkoins(playerName) > 0)
				player.sendMessage("Parkoins: " + ChatColor.AQUA + getParkoins(playerName));
		}
	}

	/**
	 * Private methods, these will only be used by the PlayerMethods class
	 * 
	 * @param playerName
	 * @param player
	 */
	private static void addPlayer(String playerName, ParkourSession player) {
		parkourPlayers.put(playerName, player);
	}

	private static void removePlayer(String player) {
		parkourPlayers.remove(player);
	}

	/**
	 * Retrieve the player's selected course for editing.
	 * 
	 * @param playerName
	 * @return selected course
	 */
	public static String getSelected(String playerName) {
		return Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + playerName + ".Selected");
	}

	public static void setSelected(String playerName, String courseName) {
		Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + playerName + ".Selected", courseName.toLowerCase());
		Parkour.getParkourConfig().saveUsers();
	}

	public static boolean hasSelected(Player player) {
		String selected = getSelected(player.getName());
		if (selected == null || selected.length() == 0) {
			player.sendMessage(Utils.getTranslation("Error.Selected"));
			return false;
		}
		return true;
	}

	/**
	 * Executed via "/pa kit", will clear and populate the players inventory
	 * with the default Parkour tools.
	 * 
	 * @param player
	 */
	public static void givePlayerKit(String[] args, Player player) {
		player.getInventory().clear();
		ParkourBlocks pb = null;

		if (args != null && args.length == 2) {
			pb = Utils.populateParkourBlocks("ParkourBlocks." + args[1]);
			if (pb == null)
				player.sendMessage(Static.getParkourString() + "Invalid ParkourBlocks: " + ChatColor.RED + args[1]);
		}
		if (pb == null) {
			pb = Utils.populateParkourBlocks();
		}

		// Speed Block
		ItemStack s = new ItemStack(pb.getSpeed());
		ItemMeta m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Speed", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(pb.getClimb());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Climb", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(pb.getLaunch());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Launch", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(pb.getFinish());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Finish", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(pb.getRepulse());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Repulse", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(pb.getNorun());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.NoRun", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(pb.getNopotion());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.NoPotion", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(pb.getDeath());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Death", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Material.SIGN);
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Sign", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);	

		player.updateInventory();
		player.sendMessage(Utils.getTranslation("Other.Kit"));
		Utils.logToFile(player.getName() + " recieved the kit");
	}

	/**
	 * Display the players Parkour permissions
	 * 
	 * @param player
	 */
	public static void getPermissions(Player player) {
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

	/**
	 * This method is only used on the course join, whereas the
	 * preparePlayer(player, int) can be called anytime.
	 * 
	 * @param player
	 */
	private static void prepareJoinPlayer(Player player, String courseName) {
		saveInventory(player);
		preparePlayer(player, 0);

		ItemStack item;
		ItemMeta meta;

		if (Parkour.getSettings().getSuicide() != null) {
			item = new ItemStack(Parkour.getSettings().getSuicide(), 1);
			meta = item.getItemMeta();
			meta.setDisplayName(Utils.getTranslation("Other.Item_Suicide", false));
			item.setItemMeta(meta);
			player.getInventory().setItem(0, item);
		}

		if (Parkour.getSettings().getHideall() != null) {
			item = new ItemStack(Parkour.getSettings().getHideall(), 1);
			meta = item.getItemMeta();
			meta.setDisplayName(Utils.getTranslation("Other.Item_HideAll", false));
			item.setItemMeta(meta);
			player.getInventory().setItem(1, item);
		}

		if (Parkour.getSettings().getLeave() != null) {
			item = new ItemStack(Parkour.getSettings().getLeave(), 1);
			meta = item.getItemMeta();
			meta.setDisplayName(Utils.getTranslation("Other.Item_Leave", false));
			item.setItemMeta(meta);
			player.getInventory().setItem(2, item);
		}

		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".JoinItemMaterial")){
			Material joinItem = Material.getMaterial(Parkour.getParkourConfig().getCourseData().getString(courseName + ".JoinItemMaterial"));
			if (joinItem != null){
				item = new ItemStack(joinItem, Parkour.getParkourConfig().getCourseData().getInt(courseName + ".JoinItemAmount", 1));
				player.getInventory().setItem(3, item);
			}
		}

		// TODO did they join with a mode?
		// Make ParkourModes enum (CodJumper, Drunk)

		player.updateInventory();

	}

	/**
	 * This is called often during the course, for example when the player dies
	 * we fully prepare them to resume the course from the last checkpoint.
	 * 
	 * @param player
	 * @param gamemode
	 */
	public static void preparePlayer(Player player, int gamemode) {
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

		Damageable damag = player;
		damag.setHealth(damag.getMaxHealth());
		player.setGameMode(Utils.getGamemode(gamemode));
		player.setFoodLevel(20);
		player.setFallDistance(0);
		player.setFireTicks(0);
	}

	/**
	 * This is called when the player joins a course. Based on the config, this
	 * can be disabled. I've now done a check to see if the inv is already
	 * saved, if it is then don't overwrite it. This is because a player can
	 * join CourseA then join CourseB and potentially have their inv
	 * overwritten.
	 * 
	 * @param player
	 */
	public static void saveInventory(Player player) {
		if (!Parkour.getParkourConfig().getConfig().getBoolean("Other.Parkour.InventoryManagement"))
			return;

		if (Parkour.getParkourConfig().getInvData().contains(player.getName() + ".Inventory"))
			return;

		Parkour.getParkourConfig().getInvData().set(player.getName() + ".Inventory", player.getInventory().getContents());
		Parkour.getParkourConfig().getInvData().set(player.getName() + ".Armor", player.getInventory().getArmorContents());
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);

		player.updateInventory();
	}

	/**
	 * This will load the inventory for the player, then delete it from the
	 * file.
	 * 
	 * @param player
	 */
	public static void loadInventory(Player player) {
		if (!Parkour.getParkourConfig().getConfig().getBoolean("Other.Parkour.InventoryManagement"))
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
	 * This will enable / disable Parkour notifications for the the player when
	 * they are using the plugin.
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
	 * This will remove all trace of the player from the plugin. All SQL time
	 * entries from the player will be removed, and their parkour stats will be
	 * deleted from the config.
	 * 
	 * @param args
	 * @param player
	 */
	public static void resetPlayer(String[] args, Player player) {
		Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + player.getName(), null);
		DatabaseMethods.deleteAllTimesForPlayer(args[1]);
		player.sendMessage(Static.getParkourString() + args[1] + " has been removed!");
	}

	/**
	 * This will enable / disable the testmode functionality, by creating a
	 * dummy "Test Mode" course for the player.
	 * 
	 * @param player
	 */
	public static void toggleTestmode(Player player) {
		if (isPlaying(player.getName())) {
			removePlayer(player.getName());
			Utils.sendActionBar(player, Utils.colour("Test Mode: &bOFF"));
		} else {
			Location location = player.getLocation();
			player.teleport(new Location(player.getWorld(), location.getX(),location.getY(),location.getZ(),location.getYaw(), location.getPitch()));
			Checkpoint checkpoint = new Checkpoint(location.getX(),location.getY(),location.getZ(),location.getYaw(),location.getPitch(),player.getWorld().getName(),0,0,0);

			addPlayer(player.getName(), new ParkourSession(new Course("Test Mode", checkpoint)));
			Utils.sendActionBar(player, Utils.colour("Test Mode: &bON"));
		}
	}

	/**
	 * This will enable / disable the HideAll functionality.
	 * 
	 * @param player
	 */
	public static void toggleVisibility(Player player) {
		toggleVisibility(player, false);
	}

	public static void toggleVisibility(Player player, boolean override) {
		boolean enabled = override ? true : Static.containsHidden(player.getName());

		for (Player players : Bukkit.getOnlinePlayers()) {
			if (enabled)
				player.showPlayer(players);
			else
				player.hidePlayer(players);
		}
		if (enabled) {
			Static.removeHidden(player.getName());
			player.sendMessage(Utils.getTranslation("Event.HideAll1"));
		} else {
			Static.addHidden(player.getName());
			player.sendMessage(Utils.getTranslation("Event.HideAll2"));
		}
	}

	/**
	 * This allows the player to invite another onto the course they are using.
	 * 
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

		player.sendMessage(Utils.getTranslation("Parkour.Invite.Send").replace("%COURSE%", course.getName()).replace("%TARGET%", target.getName()));
		target.sendMessage(Utils.getTranslation("Parkour.Invite.Recieve1").replace("%COURSE%", course.getName()).replace("%PLAYER%", player.getName()));
		target.sendMessage(Utils.getTranslation("Parkour.Invite.Recieve2").replace("%COURSE%", course.getName()));
	}

	/**
	 * Remove the player from the various config files. Delete their times from
	 * all courses. (if they cheated all the times etc).
	 * 
	 * @param playerName
	 */
	public final static void resetPlayer(String playerName) {
		Parkour.getParkourConfig().getUsersData().set("PlayerInfo." + playerName, null);
		Parkour.getParkourConfig().saveUsers();
		DatabaseMethods.deleteAllTimesForPlayer(playerName);
	}

	/**
	 * Used for validation. Example you can't invite a player to "Test Mode" as
	 * it isn't a valid course.
	 * 
	 * @param playerName
	 * @return
	 */
	public static boolean isPlayerInTestmode(String playerName) {
		ParkourSession session = getParkourSession(playerName);

		if (session == null)
			return false;

		return session.getCourse().getName().equals("Test Mode");
	}

	/**
	 * New for Parkour 4.0, Parkoins allow you to interact with the new store,
	 * making purchases etc. Points will be rewarded on course completion etc.
	 * 
	 * @param playerName
	 * @return
	 */
	public static int getParkoins(String playerName) {
		return Parkour.getParkourConfig().getUsersData().getInt("PlayerInfo." + playerName + ".Parkoins");
	}

	public static boolean isPlayerOnline(String playerName) {
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getName().equalsIgnoreCase(playerName))
				return true;
		}

		return false;
	}

	public static void acceptChallenge(final Player targetPlayer){
		Challenge challenge = Static.getChallenge(targetPlayer.getName());

		if (challenge == null){
			targetPlayer.sendMessage("You have not been invited"); //TODO
			return;
		}
		if (!PlayerMethods.isPlayerOnline(challenge.getPlayer())){
			targetPlayer.sendMessage("Player is not online!"); //TODO
			return;
		}

		Static.removeChallenge(challenge);
		final Player player = Bukkit.getPlayer(challenge.getPlayer());

		if (Parkour.getParkourConfig().getConfig().getBoolean("ParkourModes.Challenge.hidePlayers")){
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

	public static void increaseCheckpoint(ParkourSession session, Player player) {
		session.increaseCheckpoint();

		if (session.getCourse().getCheckpoints() == session.getCheckpoint())
			player.sendMessage(Utils.getTranslation("Event.AllCheckpoints"));
		else
			player.sendMessage(Utils.getTranslation("Event.Checkpoint") + session.getCheckpoint() + " / " + session.getCourse().getCheckpoints());
	}

}

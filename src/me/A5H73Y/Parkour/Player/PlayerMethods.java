package me.A5H73Y.Parkour.Player;

import java.util.HashMap;
import java.util.List;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class PlayerMethods {

	private static HashMap<String, PPlayer> playing = new HashMap<String, PPlayer>();	

	/**
	 * This method is only called from the CourseMethods after course validation.
	 * It will retrieve a course object which will then be referenced against the player. 
	 * We prepare the player for the course here.
	 * 
	 * @param player
	 * @param course
	 */
	public static void playerJoin(Player player, Course course){
		prepareJoinPlayer(player);
		player.teleport(course.getCheckpoints().get(0).getLocation());
		CourseMethods.increaseView(course.getName());

		if (getPlayerInfo(player.getName()) == null){
			Utils.sendTitle(player, "Joining " + course.getName());
			addPlayer(player.getName(), new PPlayer(course));
		}else{
			removePlayer(player.getName());
			addPlayer(player.getName(), new PPlayer(course));
			if (!Static.containsQuiet(player.getName()))
				player.sendMessage(Static.getParkourString() + "Your time has been restarted!");
		}
		player.sendMessage(Utils.getTranslation("Parkour.Join").replace("%COURSE%", course.getName()));
	}

	/**
	 * Called when the player requests to leave a course. 
	 * Will remove the player from the players which will also dispose of their course session.
	 * @param player
	 */
	public static void playerLeave(Player player){
		//TODO Add admin forcing other players to leave
		if (!isPlaying(player.getName())){
			player.sendMessage(Static.getParkourString() + "You aren't on a course.");
			return;
		}

		PPlayer pplayer = getPlayerInfo(player.getName());
		Utils.sendSubTitle(player, "Leaving " + pplayer.getCourse().getName());

		player.sendMessage(Utils.getTranslation("Parkour.Leave").replace("%COURSE%", pplayer.getCourse().getName()));
		removePlayer(player.getName());
		preparePlayer(player, Parkour.getParkourConfig().getConfig().getInt("Other.onFinish.Gamemode"));
		CourseMethods.joinLobby(null, player);
		loadInventory(player);
	}

	/**
	 * Called when the player 'dies' this can be from real events (Like falling from too high), or native 
	 * Parkour deaths (walking on a deathblock)
	 * @param player
	 */
	public static void playerDie(Player player){
		if (!isPlaying(player.getName()))
			return;

		PPlayer pplayer = getPlayerInfo(player.getName());
		pplayer.increaseDeath();

		if (pplayer.getCourse().getMaxDeaths() != null && pplayer.getCourse().getMaxDeaths() <= pplayer.getDeaths()){
			player.sendMessage(Utils.getTranslation("Parkour.MaxDeaths").replace("%AMOUNT%", pplayer.getCourse().getMaxDeaths().toString()));
			playerLeave(player);
			return;
		}

		player.teleport(pplayer.getCourse().getCheckpoints().get(pplayer.getCheckpoint()).getLocation());

		//if it's the first checkpoint
		if (pplayer.getCheckpoint() == 0){
			if (Parkour.getParkourConfig().getConfig().getBoolean("Other.onDie.ResetTimeOnStart")){
				pplayer.resetTimeStarted();
				if (!Static.containsQuiet(player.getName())) 
					player.sendMessage(Utils.getTranslation("Parkour.Die1") + " &fTime Restarted.");
			}else{
				if (!Static.containsQuiet(player.getName())) 
					player.sendMessage(Utils.getTranslation("Parkour.Die1"));
			}
		}else{
			if (!Static.containsQuiet(player.getName())) 
				player.sendMessage(Utils.getTranslation("Parkour.Die2").replace("%POINT%", String.valueOf(pplayer.getCheckpoint())));
		}

		if (Parkour.getParkourConfig().getConfig().getBoolean("Other.onDie.SetAsXPBar"))
			player.setLevel(pplayer.getDeaths());

		if (Parkour.getParkourConfig().getConfig().getBoolean("Other.Use.Sounds")) //TODO Experiment with sounds
			player.playSound(player.getLocation(), Sound.ENCHANT_THORNS_HIT, 1L, 1L);
		
		preparePlayer(player, 0);
	}

	/**
	 * This will be called when the player completes the course.
	 * Their reward will be given here.
	 * @param player
	 */
	public static void playerFinish(Player player){
		if (!isPlaying(player.getName()))
			return;

		PPlayer pplayer = getPlayerInfo(player.getName());
		
		//TODO Validate - check if they've got all checkpoints
		if (Parkour.getSettings() != null && pplayer.getCheckpoint() != (pplayer.getCourse().getCheckpoints().size() - 1)){
			player.sendMessage(Static.getParkourString() + "Please do not cheat.");
			player.sendMessage(ChatColor.BOLD + "You must achieve all " + (pplayer.getCourse().getCheckpoints().size() - 1) + " points!");
			playerDie(player);
			return;
		}	
		
		String courseName = pplayer.getCourse().getName();
		CourseMethods.increaseComplete(courseName);

		//TODO prize
		//TODO economy

		DatabaseMethods.insertTime(courseName, player.getName(), pplayer.getTime(), pplayer.getDeaths());

		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".Lobby")){
			//TODO, load custom lobby OR linked course
		} else {
			CourseMethods.joinLobby(null, player);
		}

		preparePlayer(player, Parkour.getParkourConfig().getConfig().getInt("Other.onFinish.Gamemode")); //TODO get leave gamemode
		loadInventory(player);
		removePlayer(player.getName());
	}

	/**
	 * The following methods are used throughout the plugin.
	 * Please see the PPlayer object for more information. 
	 * Will return null if player isn't playing.
	 * @param playerName
	 * @return
	 */
	public static PPlayer getPlayerInfo(String playerName){
		if (isPlaying(playerName))
			return playing.get(playerName);

		return null;
	}

	public static boolean isPlaying(String playerName){
		return playing.get(playerName) != null;
	}

	public static HashMap<String, PPlayer> getPlaying(){
		return playing;
	}

	public static void setPlaying(HashMap<String, PPlayer> pplayers){
		playing = pplayers;
	}

	/**
	 * This is new as of 4.0. Thanks to the new system we can easily see what the player (or another player) is doing with the plugin.
	 * We lookup their PPlayer object and interrogate it. We also check their offline stats from the config. (Their level etc.)
	 * @param args
	 * @param player
	 */
	public static void displayPlayerInfo(String[] args, Player player){
		String playerName = args.length <= 1 ? player.getName() : args[1];

		PPlayer pplayer = PlayerMethods.getPlayerInfo(playerName);

		if (pplayer == null && !Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + playerName)){
			player.sendMessage(Static.getParkourString() + "Player has never played Parkour. What is wrong with them?!");
			return;
		}

		if (pplayer != null){
			player.sendMessage(Static.getParkourString() + playerName + "'s information:");
			player.sendMessage("Course: "+Static.Aqua+pplayer.getCourse().getName());
			player.sendMessage("Deaths: "+Static.Aqua+pplayer.getDeaths());
			player.sendMessage("Time: "+Static.Aqua+pplayer.displayTime());
			player.sendMessage("Checkpoint: "+Static.Aqua+pplayer.getCheckpoint());
		}
	
		player.sendMessage("-= Player information =-");
		/*if (usersData.contains("PlayerInfo." + targetPlayer.getName() + ".Selected")) {
		if (usersData.contains("PlayerInfo." + targetPlayer.getName() + ".Level")){
		if (usersData.contains("PlayerInfo." + targetPlayer.getName() + ".Points")){
		 */
	}

	/**
	 * Private methods, these will only be used by the PlayerMethods class
	 * @param playerName
	 * @param player
	 */
	private static void addPlayer(String playerName, PPlayer player){
		playing.put(playerName, player);
	}

	private static void removePlayer(String player){
		playing.remove(player);
	}

	private static void savePlayer(String playerName){
		//Save player info - XP etc
	}

	/**
	 * Retrieve the player's selected course for editing.
	 * @param playerName
	 * @return selected course
	 */
	public static String getSelected(String playerName){
		String selected = null;
		try{
			selected = Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + playerName + ".Selected");
		}catch(Exception ex){}
		return selected;
	}

	/**
	 * Executed via "/pa kit", will clear and populate the players inventory with the default Parkour tools.
	 * @param player
	 */
	public static void givePlayerKit(Player player){
		player.getInventory().clear();
		
		// Speed Block
		ItemStack s = new ItemStack(Static.getParkourBlocks().getSpeed());
		ItemMeta m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Speed", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Static.getParkourBlocks().getClimb());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Climb", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Static.getParkourBlocks().getLaunch());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Launch", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Static.getParkourBlocks().getFinish());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Finish", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Static.getParkourBlocks().getRepulse());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Repulse", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Static.getParkourBlocks().getNorun());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.NoRun", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Static.getParkourBlocks().getNopotion());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.NoPotion", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Static.getParkourBlocks().getDoublejump());
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.DoubleJump", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Material.SIGN);
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Sign", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		/*
		 * BRICK - Climb Block
		 * HUGE_MUSHROOM_2 - Finish Block - New
		 * EMERALD_BLOCK - Launch Block
		 * MOSSY_COBBLESTONE - Bounch - Change to double jump?
		 * OBSIDIAN - Run
		 * ENDER_STONE - Repulse
		 * GOLD_BLOCK - No Run
		 * HUGE_MUSHROOM_2 - No Potion
		 * 
		 */

		/*

		// Death Blocks
		for (int i = 0; i < config.getIntegerList("Block.Death.ID").size(); i++) {
			int s = config.getIntegerList("Block.Death.ID").get(i);

			try {
				int first = player.getInventory().firstEmpty();
				ItemStack iss = new ItemStack(Material.getMaterial(s));
				ItemMeta mm = iss.getItemMeta();
				mm.setDisplayName(Colour(stringData.getString("Kit.Death")));
				iss.setItemMeta(mm);
				player.getInventory().setItem(first, iss);
				player.updateInventory();
			} catch (Exception ex) {
				player.sendMessage(ParkourString + Colour(stringData.getString("Error.Something").replace("%ERROR%", ex.getMessage())));
			}
		}
		player.sendMessage("Deathblock(s): " + config.getIntegerList("Block.Death.ID"));
		 */
		player.updateInventory();
		player.sendMessage(Utils.getTranslation("Other.Kit"));
		Utils.logToFile(player.getName() + " recieved the kit");
	}

	/**
	 * Display the players Parkour permissions
	 * @param player
	 */
	public static void getPermissions(Player player) {
		player.sendMessage(ChatColor.BLACK + "[" + ChatColor.AQUA + "Parkour Permissions" + ChatColor.BLACK + "]");
		if (player.hasPermission("Parkour.*") || player.isOp()) {
			player.sendMessage("- Everything");
		}else{
			boolean anyPerms = false;
			if (player.hasPermission("Parkour.Basic.*")) {
				player.sendMessage("- Basic");
				anyPerms = true;
			}
			if (player.hasPermission("Parkour.Signs.*")) {
				player.sendMessage("- Signs");
				anyPerms = true;
			}
			if (player.hasPermission("Parkour.Testmode.*")) {
				player.sendMessage("- Testmode");
				anyPerms = true;
			}
			if (player.hasPermission("Parkour.Admin.*")) {
				player.sendMessage("- Admin");
				anyPerms = true;
			}
			if (!anyPerms)
				player.sendMessage("- You don't have any Parkour permissions.");
		}
	}

	/**
	 * This method is only used on the course join, whereas the preparePlayer(player, int) can be called anytime.
	 * @param player
	 */
	private static void prepareJoinPlayer(Player player){
		FileConfiguration config = Parkour.getParkourConfig().getConfig();
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

		//TODO did they join with a mode?
		/*
				if (usersData.contains("PlayerInfo." + player.getName() + ".Mode")){
					if (usersData.getString("PlayerInfo." + player.getName() + ".Mode").toString() == "CJ"){
						ItemStack suicide = new ItemStack(76, 1);
						ItemMeta meta = suicide.getItemMeta();
						meta.setDisplayName(ChatColor.RED + "CodJumper Tool");
						suicide.setItemMeta(meta);
						player.getInventory().setItem(3, suicide);
					}
				}*/

		/*

			if (getConfig().getBoolean("Other.onJoin.GiveStatBook")) {
				int leadertime1 = leaderData.getInt(course + ".1.time");
				String leadername1 = leaderData.getString(course + ".1.player");
				int leadertime2 = leaderData.getInt(course + ".2.time");
				String leadername2 = leaderData.getString(course + ".2.player");
				int leadertime3 = leaderData.getInt(course + ".3.time");
				String leadername3 = leaderData.getString(course + ".3.player");

				ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
				BookMeta bm = (BookMeta) book.getItemMeta();
				bm.setPages(Arrays.asList((ChatColor.RED + course + ChatColor.GRAY + "\n\nStats" + ChatColor.GRAY + "\n-------------------\n" + ChatColor.BLACK + "Views: " + Aqua + viewcount + 
						ChatColor.BLACK + "\nCheckpoints: " + Aqua + courseData.getInt(course + ".Points") +
						ChatColor.BLACK + "\nCreator: " + Aqua + courseData.getString(course + ".Creator") +
						ChatColor.GRAY + "\n\n\nLeaderboards " + ChatColor.GRAY + "\n-------------------" +
						ChatColor.BLACK + "\n1) " + Time(leadertime1) + Daqua + " " + leadername1 +
						ChatColor.BLACK + "\n2) " + Time(leadertime2) + Daqua + " " + leadername2 +
						ChatColor.BLACK + "\n3) " + Time(leadertime3) + Daqua + " " + leadername3))); 
				bm.setAuthor("A5H73Y");
				bm.setTitle(Colour(stringData.getString("Other.Item_Book")));
				book.setItemMeta(bm);
				player.getInventory().setItem(8, book);
			}
			player.updateInventory();
		 */

		//TODO charge player
		/*
		if (elinked) {
			if (econData.getInt("Price." + course + ".Join") != 0) {
				economy.withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), econData.getInt("Price." + course + ".Join"));
				player.sendMessage(econData.getInt("Price." + course + ".Join") + " was taken from your account for joining " + course);
			}
		}*/
		player.updateInventory();

	}

	/**
	 * This is called often during the course, 
	 * for example when the player dies we fully prepare them to resume the course from the last checkpoint.
	 * @param player
	 * @param gamemode
	 */
	public static void preparePlayer(Player player, int gamemode){
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		while (player.getFireTicks() > 0){
			player.sendMessage(": Waiting... ");
			player.setFireTicks(0);
		}
		
		Damageable damag = player;
		damag.setHealth(damag.getMaxHealth());
		player.setGameMode(Utils.getGamemode(gamemode));
		player.setFoodLevel(20);
		player.setFallDistance(0);
		player.setFireTicks(0);
	}

	/**
	 * This is called when the player joins a course. Based on the config, this can be disabled.
	 * I've now done a check to see if the inv is already saved, if it is then don't overwrite it.
	 * This is because a player can join CourseA then join CourseB and potentially have their inv overwritten.
	 * @param player
	 */
	private static void saveInventory(Player player){
		if (!Parkour.getParkourConfig().getConfig().getBoolean("Other.Parkour.InventoryManagement"))
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
	 * This will load the inventory for the player, then delete it from the file.
	 * @param player
	 */
	private static void loadInventory(Player player){
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
	 * This will enable / disable Parkour notifications for the the player when they are using the plugin.
	 * @param player
	 */
	public static void toggleQuiet(Player player) {
		if (Static.containsQuiet(player.getName()))
			Static.removeQuiet(player);
		else
			Static.addQuiet(player);
	}

	/**
	 * This will remove all trace of the player from the plugin.
	 * All SQL time entries from the player will be removed, and their parkour stats will be deleted from the config.
	 * @param args
	 * @param player
	 */
	public static void resetPlayer(String[] args, Player player) {
		//TODO Delete from players.yml 
		DatabaseMethods.deleteAllTimesForPlayer(args[1]);
		player.sendMessage(Static.getParkourString() + args[1] + " has been removed!");
	}

	/**
	 * This will enable / disable the testmode functionality, by creating a dummy "Test Mode" course for the player.
	 * @param player
	 */
	public static void toggleTestmode(Player player) {
		// TODO Change to testmode
		if (isPlaying(player.getName())){
			removePlayer(player.getName());
			Utils.sendActionBar(player, Static.getParkourString() + Utils.Colour("Test Mode: &bOFF"));
		}else{
			Course course = new Course("Test Mode", null);
			addPlayer(player.getName(), new PPlayer(course));
			Utils.sendActionBar(player, Static.getParkourString() + Utils.Colour("Test Mode: &bON"));
		}

	}

	/**
	 * This allows the player to invite another onto the course they are using.
	 * @param args
	 * @param player
	 */
	public static void invitePlayer(String[] args, Player player) {
		if (!isPlaying(player.getName()))
			return;

		Course course = CourseMethods.findByPlayer(player.getName());
		Player target = Bukkit.getPlayer(args[1]);

		if (course == null || target == null)
			return;

		//TODO Check if = player or testmode

		player.sendMessage(Utils.getTranslation("Parkour.Invite.Send")
				.replace("%COURSE%", course.getName()
						.replace("%TARGET%", target.getName())));

		target.sendMessage(Utils.getTranslation("Parkour.Invite.Recieve1")
				.replace("%COURSE%", course.getName()
						.replace("%PLAYER%", player.getName())));

		target.sendMessage(Utils.getTranslation("Parkour.Invite.Recieve2")
				.replace("%COURSE%", course.getName()));
	}
	
	public static boolean isPlayerInTestmode(String playerName){
		PPlayer pplayer = getPlayerInfo(playerName);
		
		if (pplayer == null)
			return false;
		
		return pplayer.getCourse().getName().equals("Test Mode");
	}
}

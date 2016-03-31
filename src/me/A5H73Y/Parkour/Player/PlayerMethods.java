package me.A5H73Y.Parkour.Player;

import java.util.HashMap;
import java.util.List;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Utilities.DatabaseMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

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
	 * 
	 * @param player
	 * @param course
	 */
	public static void playerJoin(Player player, Course course){
		String playerName = player.getName();
		player.sendMessage(Utils.getTranslation("Parkour.Join").replace("%COURSE%", course.getName()));
		prepareJoinPlayer(player);
		player.teleport(course.getCheckpoints().get(0).getLocation());
		CourseMethods.increaseView(course.getName());
		
		PPlayer pplayer = getPlayerInfo(playerName);
		
		//TODO Send Title (API Needed)
		
		if (pplayer == null){
			addPlayer(playerName, new PPlayer(course));
		}else{
			pplayer.resetTimeStarted();
			if (!Static.containsQuiet(playerName))
				player.sendMessage(Static.getParkourString() + "Your time has been restarted!");
		}
	}

	public static void playerLeave(Player player){
		//TODO Add admin forcing other players to leave
		if (!isPlaying(player.getName()))
			return;

		PPlayer pplayer = getPlayerInfo(player.getName());

		player.sendMessage(Utils.getTranslation("Parkour.Leave").replace("%COURSE%", pplayer.getCourse().getName()));
		removePlayer(player.getName());
		preparePlayer(player, Parkour.getParkourConfig().getConfig().getInt("Other.onFinish.Gamemode"));
		CourseMethods.joinLobby(null, player);
		loadInventory(player);
	}

	public static void playerDie(Player player){
		if (!isPlaying(player.getName()))
			return;

		PPlayer pplayer = getPlayerInfo(player.getName());
		pplayer.increaseDeath();

		if (pplayer.getCourse().getMaxDeaths() != null && pplayer.getCourse().getMaxDeaths() <= pplayer.getDeaths()){
			player.sendMessage(Utils.getTranslation("Parkour.MaxDeaths").replaceAll("%AMOUNT%", pplayer.getCourse().getMaxDeaths().toString()));
			playerLeave(player);
			return;
		}

		//if it's their first death
		if (pplayer.getDeaths() == 1){
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
				player.sendMessage(Utils.getTranslation("Parkour.Die2").replaceAll("%POINT%", String.valueOf(pplayer.getDeaths())));
		}

		if (Parkour.getParkourConfig().getConfig().getBoolean("Other.onDie.SetAsXPBar"))
			player.setLevel(pplayer.getDeaths());


		if (Parkour.getParkourConfig().getConfig().getBoolean("Other.Use.Sounds")) {
			//TODO Play with sounds
			player.playSound(player.getLocation(), Sound.ENCHANT_THORNS_HIT, 1L, 1L);
		}
	}

	public static void playerFinish(Player player){
		if (!isPlaying(player.getName()))
			return;

		PPlayer pplayer = getPlayerInfo(player.getName());
		String courseName = pplayer.getCourse().getName();
		CourseMethods.increaseComplete(courseName);

		//TODO 
		/*
		if (getConfig().getBoolean("Other.Use.Sounds")) {
			player.playEffect(player.getLocation(), Effect.FIREWORKS_SPARK, 5);
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 1L, 1L);
		}
		*/
		
		if (Parkour.getParkourConfig().getCourseData().contains(courseName + ".Lobby")){
			//TODO, load custom lobby
		}
		loadInventory(player);

		//TODO prize
		
		DatabaseMethods.insertTime(courseName, player.getName(), pplayer.getTime(), pplayer.getDeaths());
	}

	public static boolean isPlaying(String playerName){
		return playing.containsKey(playerName);
	}

	public static HashMap<String, PPlayer> getPlaying(){
		return playing;
	}
	
	public static void setPlaying(HashMap<String, PPlayer> pplayers){
		playing = pplayers;
	}

	public static PPlayer getPlayerInfo(String playerName){
		if (isPlaying(playerName))
			return playing.get(playerName);

		return null;
	}

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

		/*if (usersData.contains("PlayerInfo." + targetPlayer.getName() + ".Selected")) {
		if (usersData.contains("PlayerInfo." + targetPlayer.getName() + ".Level")){
		if (usersData.contains("PlayerInfo." + targetPlayer.getName() + ".Points")){
		 */
	}

	//private methods

	private static void addPlayer(String playerName, PPlayer player){
		playing.put(playerName, player);
	}

	private static void removePlayer(String player){
		playing.remove(player);
	}

	private static void savePlayer(String playerName){
		//Save player info - XP etc


	}

	//Other
	public static String getSelected(String playerName){
		String selected = null;
		try{
			selected = Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + playerName + ".Selected");
		}catch(Exception ex){}
		return selected;
	}

	public static void givePlayerKit(Player player){
		player.getInventory().clear();
		FileConfiguration config = Parkour.getParkourConfig().getConfig();

		// Speed Block
		ItemStack s = new ItemStack(Material.getMaterial(config.getString("Block.Speed.ID")));
		ItemMeta m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Speed", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Material.getMaterial(config.getString("Block.Climb.ID")));
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Climb", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Material.getMaterial(config.getString("Block.Launch.ID")));
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Launch", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Material.getMaterial(config.getString("Block.Finish.ID")));
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Finish", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Material.getMaterial(config.getString("Block.Repulse.ID")));
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.Repulse", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Material.getMaterial(config.getString("Block.NoRun.ID")));
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.NoRun", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Material.getMaterial(config.getString("Block.NoPotion.ID")));
		m = s.getItemMeta();
		m.setDisplayName(Utils.getTranslation("Kit.NoPotion", false));
		s.setItemMeta(m);
		player.getInventory().addItem(s);

		s = new ItemStack(Material.getMaterial(config.getString("Block.DoubleJump.ID")));
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
				player.sendMessage(ParkourString + Colour(stringData.getString("Error.Something").replaceAll("%ERROR%", ex.getMessage())));
			}
		}
		player.sendMessage("Deathblock(s): " + config.getIntegerList("Block.Death.ID"));
		 */
		player.updateInventory();
		player.sendMessage(Utils.getTranslation("Other.Kit"));
		Utils.Log(player.getName() + " recieved the kit");
	}

	public static void getPermissions(Player player) {
		player.sendMessage(ChatColor.BLACK + "[" + ChatColor.AQUA + "Parkour Permissions" + ChatColor.BLACK + "]");
		if (player.hasPermission("Parkour.*") || player.isOp()) {
			player.sendMessage("- Everything");
		}else{
			if (player.hasPermission("Parkour.Basic")) {
				player.sendMessage("- Basic");
			}
			if (player.hasPermission("Parkour.Signs")) {
				player.sendMessage("- Signs");
			}
			if (player.hasPermission("Parkour.Testmode")) {
				player.sendMessage("- Testmode");
			}
			if (player.hasPermission("Parkour.Admin")) {
				player.sendMessage("- Admin");
			}
		}
	}
	
	private static void prepareJoinPlayer(Player player){
		saveInventory(player);
		preparePlayer(player, 0);
		
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
		
		//TODO inventory items
		/*
		if (getConfig().getBoolean("Other.onJoin.GiveSuicideID")) {
			ItemStack suicide = new ItemStack(getConfig().getInt("SuicideID"), 1);
			ItemMeta meta = suicide.getItemMeta();
			meta.setDisplayName(Colour(stringData.getString("Other.Item_Suicide")));
			suicide.setItemMeta(meta);
			player.getInventory().setItem(0, suicide);
		}

		if (getConfig().getBoolean("Other.onJoin.GiveHideAllID")) {
			ItemStack suicide = new ItemStack(getConfig().getInt("HideAllID"), 1);
			ItemMeta meta = suicide.getItemMeta();
			meta.setDisplayName(Colour(stringData.getString("Other.Item_HideAll")));
			suicide.setItemMeta(meta);
			player.getInventory().setItem(1, suicide);
		}

		if (getConfig().getBoolean("Other.onJoin.GiveLeaveID")) {
			ItemStack suicide = new ItemStack(getConfig().getInt("LeaveID"), 1);
			ItemMeta meta = suicide.getItemMeta();
			meta.setDisplayName(Colour(stringData.getString("Other.Item_Leave")));
			suicide.setItemMeta(meta);
			player.getInventory().setItem(2, suicide);
		}
		
		
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
		
	}

	public static void preparePlayer(Player player, int gamemode){
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.setGameMode(Utils.getGamemode(gamemode));
		Damageable damag = player;
		damag.setHealth(damag.getMaxHealth());
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setFallDistance(0);
	}

	private static void saveInventory(Player player){
		if (!Parkour.getParkourConfig().getConfig().getBoolean("Other.Use.InvManagement"))
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

	private static void loadInventory(Player player){
		if (!Parkour.getParkourConfig().getConfig().getBoolean("Other.Use.InvManagement"))
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

	public static void toggleQuiet(Player player) {
		if (Static.containsQuiet(player.getName()))
			Static.removeQuiet(player);
		else
			Static.addQuiet(player);
	}

	/**
	 * This will remove all said players times from the table
	 * @param args
	 * @param player
	 */
	public static void deletePlayer(String[] args, Player player) {
		//TODO
		DatabaseMethods.deleteAllTimesForPlayer(args[1]);
		player.sendMessage(Static.getParkourString() + args[1] + " has been removed!");
	}

	public static void toggleTestmode(Player player) {
		// TODO Change to testmode
		if (isPlaying(player.getName())){
			removePlayer(player.getName());
			player.sendMessage(Static.getParkourString() + "Test mode: " + Static.Aqua + "OFF");
		}else{
			Course course = new Course("Test Mode", null, Static.getParkourBlocks());
			addPlayer(player.getName(), new PPlayer(course));
			player.sendMessage(Static.getParkourString() + "Test mode: " + Static.Aqua + "ON");
		}

	}
}

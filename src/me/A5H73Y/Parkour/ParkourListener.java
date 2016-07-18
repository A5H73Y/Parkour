package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Course.Checkpoint;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Other.ParkourBlocks;
import me.A5H73Y.Parkour.Other.SettingsGUI;
import me.A5H73Y.Parkour.Other.StoreGUI;
import me.A5H73Y.Parkour.Player.PPlayer;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ParkourListener implements Listener {

	@EventHandler
	public void onPlayerMoveTrails(PlayerMoveEvent event){
		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		if (!Parkour.getSettings().isAllowTrails())
			return;

		//Redstone - Best
		//Drip_Lava - Awesome
		//Drip_Water - Awesome
		//Crit_magic - not bad
		//Heart - interesting...
		//Snowball - bit crap
		//Slime - also a bit crap

		Location loc = event.getPlayer().getLocation().add(0, 0.4, 0);
		event.getPlayer().getWorld().spawnParticle(Particle.REDSTONE, loc, 1);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		if (event.getInventory().getName().contains(StoreGUI.PARKOUR_TITLE)) {
			event.setCancelled(true);
			StoreGUI.processClick((Player) event.getWhoClicked(), event.getCurrentItem().getType());

		} else if (event.getInventory().getName().contains(SettingsGUI.PARKOUR_TITLE)) {
			event.setCancelled(true);
			SettingsGUI.processClick((Player) event.getWhoClicked(), event.getCurrentItem().getType());
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		if (event.getPlayer().getFallDistance() > 30){ //TODO get from config
			PlayerMethods.playerDie(event.getPlayer());
			return;
		}

		Material belowMaterial = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType();

		ParkourBlocks pb = PlayerMethods.getPlayerInfo(event.getPlayer().getName()).getCourse().getParkourBlocks();

		if (belowMaterial.equals(pb.getFinish())){
			PlayerMethods.playerFinish(event.getPlayer());

		} else if (belowMaterial.equals(pb.getDeath())){
			PlayerMethods.playerDie(event.getPlayer());

		} else if (belowMaterial.equals(pb.getLaunch())){
			event.getPlayer().setVelocity(new Vector(0, 1.2, 0));

		} else if (belowMaterial.equals(pb.getDoublejump())){
			event.getPlayer().setVelocity(new Vector(0, 0.6, 0));

		} else if (belowMaterial.equals(pb.getSpeed())){
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 5));

		} else if (belowMaterial.equals(Material.SPONGE)){
			event.getPlayer().setFallDistance(0);

		} else if (belowMaterial.equals(pb.getNorun())){
			event.getPlayer().setSprinting(false);

		} else if (belowMaterial.equals(pb.getNopotion())){
			for (PotionEffect effect : event.getPlayer().getActivePotionEffects()) 
				event.getPlayer().removePotionEffect(effect.getType());

			event.getPlayer().setFireTicks(0);

		} else {
			Material matEast = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.EAST).getType();
			Material matNorth = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.NORTH).getType();
			Material matWest = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.WEST).getType();
			Material matSouth = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.SOUTH).getType();

			if (matEast.equals(pb.getClimb()) || matNorth.equals(pb.getClimb()) || matWest.equals(pb.getClimb()) || matSouth.equals(pb.getClimb())){
				if (!event.getPlayer().isSneaking())
					event.getPlayer().setVelocity(new Vector(0, 0.4, 0));


			} else if (matNorth.equals(pb.getRepulse())) {
				event.getPlayer().setVelocity(new Vector(0, 0.1, 0.4));
			} else if (matSouth.equals(pb.getRepulse())) {
				event.getPlayer().setVelocity(new Vector(0, 0.1, -0.4));
			} else if (matEast.equals(pb.getRepulse())) {
				event.getPlayer().setVelocity(new Vector(-0.4, 0.1, 0));
			} else if (matWest.equals(pb.getRepulse())) {
				event.getPlayer().setVelocity(new Vector(0.4, 0.1, 0));
			}
		}

	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (!Parkour.getSettings().isChatPrefix())
			return;

		String rank = Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + event.getPlayer().getName() + ".Rank");
		rank = rank == null ? "Newbie" : rank;

		event.setFormat(Utils.colour(Utils.getTranslation("Event.Chat", false)
				.replace("%RANK%", rank)
				.replace("%PLAYER%", event.getPlayer().getName())
				.replace("%MESSAGE%", event.getMessage())));
	}


	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (PlayerMethods.isPlaying(event.getPlayer().getName()) && !Utils.hasPermission(event.getPlayer(), "Parkour.Admin")) 
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (PlayerMethods.isPlaying(event.getPlayer().getName()) && !Utils.hasPermission(event.getPlayer(), "Parkour.Admin")) 
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) 
			if (PlayerMethods.isPlaying(event.getEntity().getName()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {
		if (event.getEntity() instanceof Player)
			if (PlayerMethods.isPlaying(event.getEntity().getName()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (!PlayerMethods.isPlaying(event.getEntity().getName()))
			return;

		if (Parkour.getSettings().isDisablePlayerDamage()){
			event.setDamage(0);
			return;
		}
		
		if(event.getCause() == DamageCause.FIRE)
			event.setCancelled(true);

		Damageable player = (Player) event.getEntity();
		if (player.getHealth() <= event.getDamage()) {
			event.setDamage(0);
			event.setCancelled(true);
			PlayerMethods.playerDie((Player) event.getEntity()); 
		}
	}

	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (PlayerMethods.isPlaying(event.getEntity().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;
		
		if (Parkour.getParkourConfig().getConfig().getBoolean("OnCourse.DisableItemDrop"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (Parkour.getSettings().isDisplayWelcome())
			event.getPlayer().sendMessage(Utils.getTranslation("Event.Join").replace("%VERSION%", Static.getVersion().toString()));

		if (PlayerMethods.isPlaying(event.getPlayer().getName())){
			event.getPlayer().sendMessage(Utils.getTranslation("Parkour.Continue")
					.replace("%COURSE%", PlayerMethods.getPlayerInfo(event.getPlayer().getName()).getCourse().getName()));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!Parkour.getSettings().isResetOnLeave())
			return;

		if (PlayerMethods.isPlaying(event.getPlayer().getName()))
			//TODO Add them to a list, then leave on join
			PlayerMethods.playerLeave(event.getPlayer());
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		if (!Parkour.getSettings().isEnforceWorld())
			return;

		if (event.getFrom().getWorld() != event.getTo().getWorld()){
			event.setCancelled(true);
			event.getPlayer().sendMessage(Utils.getTranslation("Error.WorldTeleport"));
		}

	}

	@EventHandler
	public void onFlyToggle(PlayerToggleFlightEvent event) {
		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.getPlayer().setAllowFlight(false);
			event.getPlayer().setFlying(false);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryInteract(PlayerInteractEvent event) {
		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		Player player = event.getPlayer();

		if (!player.isSneaking())
			return;

		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR))
			return;

		if (PlayerMethods.isPlayerInTestmode(player.getName()))
			return;

		if (player.getInventory().getItemInMainHand().getType() == Parkour.getSettings().getSuicide()) {
			PlayerMethods.playerDie(player);
			player.getInventory().setHeldItemSlot(4);

		} else if (player.getInventory().getItemInMainHand().getType() == Parkour.getSettings().getHideall()) {
			Utils.toggleVisibility(player, Static.containsHidden(player.getName()));

		} else if (player.getInventory().getItemInMainHand().getType() == Parkour.getSettings().getLeave()) {
			PlayerMethods.playerLeave(player);

		} else if (player.getInventory().getItemInMainHand().getType() == Material.REDSTONE_TORCH_ON) {
			//TODO CodJumper
		}
	}


	@EventHandler
	public void onCheckpointEvent(PlayerInteractEvent event) {
		if (event.getAction() != Action.PHYSICAL)
			return;

		Block plate = event.getClickedBlock();
		Block below = plate.getRelative(BlockFace.DOWN);

		if (below == null) 
			return;

		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		event.setCancelled(true);
		PPlayer pplayer = PlayerMethods.getPlayerInfo(event.getPlayer().getName());
		Course course = pplayer.getCourse();

		//This is so we don't include the spawn
		int checkpoints = course.getCheckpoints().size() - 1;

		if (checkpoints <= pplayer.getCheckpoint())
			return;

		Checkpoint check = course.getCheckpoints().get(pplayer.getCheckpoint() + 1);

		if (check == null)
			return;

		if (check.getX() == below.getLocation().getBlockX() &&
				check.getY() == below.getLocation().getBlockY() &&
				check.getZ() == below.getLocation().getBlockZ()){

			pplayer.increaseCheckpoint();

			if (checkpoints == pplayer.getCheckpoint())
				event.getPlayer().sendMessage(Utils.getTranslation("Event.AllCheckpoints"));
			else
				event.getPlayer().sendMessage(Utils.getTranslation("Event.Checkpoint") + pplayer.getCheckpoint() +  " / " + checkpoints);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		boolean commandIsPa = event.getMessage().startsWith("/pa");
		Player player = event.getPlayer();

		if (commandIsPa){
			if (Static.containsQuestion(player.getName())) {
				questionPlayer(player, event.getMessage());
				event.setCancelled(true);
			} else if (Static.containsCreatePB(player.getName())) {
				createPB(player);
			}
			return;
		}

		if (!commandIsPa && PlayerMethods.isPlaying(player.getName())) {
			if (!Parkour.getSettings().isDisableCommands())
				return;

			if (player.hasPermission("Parkour.Admin") || player.hasPermission("Parkour.*"))
				return;

			boolean allowed = false;
			for (String word : Parkour.getParkourConfig().getConfig().getStringList("Other.Commands.Whitelist")) {
				if (event.getMessage().startsWith("/" + word)) {
					allowed = true;
					break;
				}
			}
			if (allowed == false) {
				event.setCancelled(true);
				player.sendMessage(Utils.getTranslation("Error.Command"));
			}

		}
	}

	private final void questionPlayer(Player player, String message) {
		if (message.startsWith("/pa yes")) {
			String question[] = Static.getQuestion(player.getName()).split(",");
			Utils.questionConfirm(Integer.parseInt(question[0]), question[1], player);
			Static.removeQuestion(player.getName());

		} else if (message.startsWith("/pa no")) {
			player.sendMessage(Static.getParkourString() + "Question cancelled!");
			Static.removeQuestion(player.getName());

		} else {
			player.sendMessage(Static.getParkourString() + ChatColor.RED + "Invalid question answer.");
			player.sendMessage("Please use either " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " or " + ChatColor.AQUA + "/pa no");
		}
	}


	private final void createPB(Player player) {
		String[] ParkourBlocks = { "a","a","a","a" };   
		for (String s: ParkourBlocks) {           

			Parkour.getParkourConfig().getUpgData().set("ParkourBlocks.name" , s);

		}
	}
}


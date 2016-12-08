package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Course.Checkpoint;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Other.ParkourBlocks;
import me.A5H73Y.Parkour.Other.Question;
import me.A5H73Y.Parkour.Player.ParkourSession;
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
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ParkourListener implements Listener {

	@EventHandler
	public void onPlayerMoveTrails(PlayerMoveEvent event) {
		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		if (!Parkour.getSettings().isAllowTrails())
			return;

		Location loc = event.getPlayer().getLocation().add(0, 0.4, 0);
		event.getPlayer().getWorld().spawnParticle(Particle.REDSTONE, loc, 1);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		//TODO work on extreme sponge height
		//event.getPlayer().setFallDistance(0);

		if (event.getPlayer().getFallDistance() > Parkour.getSettings().getMaxFallTicks()) {
			PlayerMethods.playerDie(event.getPlayer());
			return;
		}

		if (Parkour.getSettings().isAttemptLessChecks()){
			if (event.getTo().getBlockX() == event.getFrom().getBlockX() && 
					event.getTo().getBlockY() == event.getFrom().getBlockY() && 
					event.getTo().getBlockZ() == event.getFrom().getBlockZ())
				return;
		}

		Material belowMaterial = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType();

		ParkourBlocks pb = PlayerMethods.getParkourSession(event.getPlayer().getName()).getCourse().getParkourBlocks();

		if (belowMaterial.equals(pb.getFinish())) {
			PlayerMethods.playerFinish(event.getPlayer());

		} else if (belowMaterial.equals(pb.getDeath())) {
			PlayerMethods.playerDie(event.getPlayer());

		} else if (belowMaterial.equals(pb.getLaunch())) {
			event.getPlayer().setVelocity(new Vector(0, 1.2, 0));

		} else if (belowMaterial.equals(pb.getDoublejump())) {
			event.getPlayer().setVelocity(new Vector(0, 0.6, 0));

		} else if (belowMaterial.equals(pb.getSpeed())) {
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 5));

		} else if (belowMaterial.equals(Material.SPONGE)) {
			event.getPlayer().setFallDistance(0);

		} else if (belowMaterial.equals(pb.getNorun())) {
			event.getPlayer().setSprinting(false);

		} else if (belowMaterial.equals(pb.getNopotion())) {
			for (PotionEffect effect : event.getPlayer().getActivePotionEffects())
				event.getPlayer().removePotionEffect(effect.getType());

			event.getPlayer().setFireTicks(0);

		} else if (event.getPlayer().getLocation().getBlock().isLiquid() && Parkour.getParkourConfig().getConfig().getBoolean("OnCourse.DieInLiquid")){
			PlayerMethods.playerDie(event.getPlayer());

		} else {
			Material matEast = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.EAST).getType();
			Material matNorth = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.NORTH).getType();
			Material matWest = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.WEST).getType();
			Material matSouth = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.SOUTH).getType();

			if (matEast.equals(pb.getClimb()) || matNorth.equals(pb.getClimb()) || matWest.equals(pb.getClimb()) || matSouth.equals(pb.getClimb())) {
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

		event.setFormat(Utils.colour(Utils.getTranslation("Event.Chat", false).replace("%RANK%", rank).replace("%PLAYER%", event.getPlayer().getName()).replace("%MESSAGE%", event.getMessage())));
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
		
		Player player = (Player) event.getEntity();

		if (!PlayerMethods.isPlaying(player.getName()))
			return;

		if (Parkour.getSettings().isDisablePlayerDamage()) {
			event.setDamage(0);
			return;
		}

		Damageable playerDamage = player;
		if (playerDamage.getHealth() <= event.getDamage()) {
			event.setDamage(0);
			event.setCancelled(true);
			PlayerMethods.playerDie(player);
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

		if (PlayerMethods.isPlaying(event.getPlayer().getName())) {
			event.getPlayer().sendMessage(Utils.getTranslation("Parkour.Continue").replace("%COURSE%", PlayerMethods.getParkourSession(event.getPlayer().getName()).getCourse().getName()));
		}

		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		if (Parkour.getParkourConfig().getConfig().getBoolean("OnLeaveServer.TeleportToLastCheckpoint"))
			PlayerMethods.playerDie(event.getPlayer());

		if (Parkour.getSettings().isResetOnLeave())
			PlayerMethods.playerLeave(event.getPlayer());
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		if (PlayerMethods.isPlayerInTestmode(event.getPlayer().getName()))
			return;

		if (event.getTo().getBlockX() == 0 && event.getTo().getBlockY() == 0 && event.getTo().getBlockZ() == 0){
			event.getPlayer().sendMessage(Static.getParkourString() + ChatColor.RED + "This checkpoint is invalid. For safety you have been teleported to the lobby.");
			event.setCancelled(true);
			PlayerMethods.playerLeave(event.getPlayer());
			return;
		}

		if (!Parkour.getSettings().isEnforceWorld())
			return;

		if (event.getFrom().getWorld() != event.getTo().getWorld()) {
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
			PlayerMethods.toggleVisibility(player);

		} else if (player.getInventory().getItemInMainHand().getType() == Parkour.getSettings().getLeave()) {
			PlayerMethods.playerLeave(player);

		} else if (player.getInventory().getItemInMainHand().getType() == Material.REDSTONE_TORCH_ON) {
			// TODO CodJumper
		}
	}

	@EventHandler
	public void onCheckpointEvent(PlayerInteractEvent event) {
		if (event.getAction() != Action.PHYSICAL)
			return;

		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		Block below = event.getClickedBlock().getRelative(BlockFace.DOWN);

		if (below == null)
			return;

		if (Parkour.getParkourConfig().getConfig().getBoolean("OnCourse.PreventPlateStick"))
			event.setCancelled(true);

		ParkourSession session = PlayerMethods.getParkourSession(event.getPlayer().getName());
		Course course = session.getCourse();

		if (session.getCheckpoint() == course.getCheckpoints())
			return;

		Checkpoint check = course.getCheckpoint();

		if (check == null)
			return;

		if (check.getNextCheckpointX() == below.getLocation().getBlockX() && check.getNextCheckpointY() == below.getLocation().getBlockY() && check.getNextCheckpointZ() == below.getLocation().getBlockZ())
			PlayerMethods.increaseCheckpoint(session, event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		boolean commandIsPa = event.getMessage().startsWith("/pa");
		Player player = event.getPlayer();

		if (commandIsPa && Static.containsQuestion(player.getName())) {
			questionPlayer(player, event.getMessage());
			event.setCancelled(true);
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
			Question question = Static.getQuestion(player.getName());
			question.confirm(player, question.getType(), question.getArgument());
			Static.removeQuestion(player.getName());

		} else if (message.startsWith("/pa no")) {
			player.sendMessage(Static.getParkourString() + "Question cancelled!");
			Static.removeQuestion(player.getName());

		} else {
			player.sendMessage(Static.getParkourString() + ChatColor.RED + "Invalid question answer.");
			player.sendMessage("Please use either " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " or " + ChatColor.AQUA + "/pa no");
		}
	}
}

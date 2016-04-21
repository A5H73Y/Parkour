package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Course.Checkpoint;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Player.PPlayer;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Settings;
import me.A5H73Y.Parkour.Utilities.SignMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class ParkourListener implements Listener {

	SignMethods sm = new SignMethods();

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		//if (pl.getConfig().getBoolean("Other.Use.Prefix")) {
		//TODO
		if (Parkour.getSettings().isDebug())
			return;

		String rank = Parkour.getParkourConfig().getUsersData().getString("PlayerInfo." + event.getPlayer().getName() + ".Rank");
		rank = rank == null ? "Newbie" : rank;

		event.setFormat(Utils.getTranslation("Event.Chat", false)
				.replace("%RANK%", rank)
				.replace("%PLAYER%", event.getPlayer().getName())
				.replace("%MESSAGE%", event.getMessage()));
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
	public void onPlayerDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (!PlayerMethods.isPlaying(event.getEntity().getName()))
			return;

		//TODO "Other.Use.PlayerDamage"
		if (false){
			event.setDamage(0);
			return;
		}

		Damageable player = (Player) event.getEntity();
		if (player.getHealth() <= event.getDamage()) {
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
	public void onPlayerJoin(PlayerJoinEvent event) {
		//TODO actual joinonMessage settings
		if (Parkour.getSettings().isWelcomeMessage())
			event.getPlayer().sendMessage(Utils.getTranslation("Event.Join").replace("%VERSION%", Static.getVersion().toString()));

		//TODO check how performance is
		if (PlayerMethods.isPlaying(event.getPlayer().getName())){
			event.getPlayer().sendMessage(Utils.getTranslation("Parkour.Continue")
					.replace("%COURSE%", PlayerMethods.getPlayerInfo(event.getPlayer().getName()).getCourse().getName()));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		//TODO "Other.onLeave.ResetPlayer"
		if (!Parkour.getSettings().isWelcomeMessage())
			return;

		if (PlayerMethods.isPlaying(event.getPlayer().getName()))
			PlayerMethods.playerLeave(event.getPlayer());
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (!Parkour.getSettings().isForceWorld())
			return;

		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		if (event.getFrom().getWorld() != event.getTo().getWorld()){
			event.setCancelled(true);
			event.getPlayer().sendMessage(Utils.getTranslation(""));
			//TODO Translation "Teleporting to a different world has been cancelled."
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

		//Check if test mode

		if (player.getInventory().getItemInMainHand().getType() == Material.ARROW){
			//pl.getConfig().getInt("SuicideID")){
			PlayerMethods.playerDie(player);

		} else if (player.getInventory().getItemInMainHand().getType() == Material.BONE){
			Utils.toggleVisibility(player, Static.containsHidden(player.getName()));

		} else if (player.getInventory().getItemInMainHand().getType() == Material.LEAVES) {
			PlayerMethods.playerLeave(player);

		} else if (player.getInventory().getItemInMainHand().getType() == Material.REDSTONE_TORCH_ON) {
			//TODO CodJumper
		}
	}

	@EventHandler
	public void onSignCreate(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[parkour]") || event.getLine(0).equalsIgnoreCase("[pa]")) {
			Player player = event.getPlayer();

			event.setLine(0, ChatColor.BLACK + "[" + ChatColor.AQUA + "Parkour" + ChatColor.BLACK + "]");

			if (event.getLine(1).equalsIgnoreCase("join") || event.getLine(1).equalsIgnoreCase("j")) {
				sm.joinCourse(event, player);

			} else if (event.getLine(1).equalsIgnoreCase("finish") || event.getLine(1).equalsIgnoreCase("f")) {
				sm.createStandardCourseSign(event, player, "Finish");

			} else if (event.getLine(1).equalsIgnoreCase("lobby") || event.getLine(1).equalsIgnoreCase("l")) {
				sm.joinLobby(event, player);

			} else if (event.getLine(1).equalsIgnoreCase("store") || event.getLine(1).equalsIgnoreCase("s")) {
				sm.createStandardSign(event, player, "Store");

			} else if (event.getLine(1).equalsIgnoreCase("leave")) {
				sm.createStandardSign(event, player, "Leave");

			} else if (event.getLine(1).equalsIgnoreCase("joinall") || event.getLine(1).equalsIgnoreCase("ja")) {
				sm.createStandardSign(event, player, "JoinAll");

			} else if (event.getLine(1).equalsIgnoreCase("effect") || event.getLine(1).equalsIgnoreCase("e")) {
				sm.createEffectSign(event, player, "Effect");

			} else if (event.getLine(1).equalsIgnoreCase("stats") || event.getLine(1).equalsIgnoreCase("s")) {
				//TODO - Not sure what to do for this.

			} else {
				player.sendMessage(Static.getParkourString() + "Unknown Sign Command");
				event.setLine(1, ChatColor.RED + "Unknown cmd");
				event.setLine(2, "");
				event.setLine(3, "");
			}
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

		if (checkpoints <= (pplayer.getCheckpoint()))
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
		Player player = event.getPlayer();

		//TODO
		boolean paDisabled = true; //pl.getConfig().getBoolean("Other.DisableCommands.OnParkour");
		boolean commandIsPa = event.getMessage().startsWith("/pa");

		if (commandIsPa && Static.containsQuestion(player.getName())) {
			if (event.getMessage().startsWith("/pa yes")) {
				String question[] = Static.getQuestion(player.getName()).split(",");
				int command = Integer.parseInt(question[0]);
				String argument = question[1];
				Static.removeQuestion(player.getName());

				Utils.Confirm(command, argument, player);
			} else if (event.getMessage().startsWith("/pa no")) {
				player.sendMessage(Static.getParkourString() + "Question cancelled!");
				Static.removeQuestion(player.getName());
			} else {
				player.sendMessage(Static.getParkourString() + ChatColor.RED + "Invalid question answer.");
				player.sendMessage("Please use either " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " or " + ChatColor.AQUA + "/pa no");
			}
			event.setCancelled(true);
			return;
		}

		if (!commandIsPa && PlayerMethods.isPlaying(player.getName())) {
			if (!paDisabled)
				return;

			if (!(player.hasPermission("Parkour.Admin") || player.hasPermission("Parkour.*"))) {
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
	}
}


package me.A5H73Y.Parkour;

import me.A5H73Y.Parkour.Course.Checkpoint;
import me.A5H73Y.Parkour.Course.CheckpointMethods;
import me.A5H73Y.Parkour.Course.Course;
import me.A5H73Y.Parkour.Enums.ParkourMode;
import me.A5H73Y.Parkour.Other.ParkourKit;
import me.A5H73Y.Parkour.Other.QuestionManager;
import me.A5H73Y.Parkour.Player.PlayerInfo;
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
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class ParkourListener implements Listener {

    private static final List<BlockFace> blockFaces =
            Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

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
    public void onPlayerModeMove(PlayerMoveEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
            return;

        ParkourSession session = PlayerMethods.getParkourSession(event.getPlayer().getName());

        if (session == null || session.getMode() == ParkourMode.NONE)
            return;

        if (session.getMode() == ParkourMode.DRUNK) {
            if (event.getPlayer().hasPotionEffect(PotionEffectType.CONFUSION))
                return;

            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10000, 1));

        } else if (session.getMode() == ParkourMode.DARKNESS) {
            if (event.getPlayer().hasPotionEffect(PotionEffectType.BLINDNESS))
                return;

            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10000, 1));

        } else if (session.getMode() == ParkourMode.MOON) {
            if (event.getPlayer().hasPotionEffect(PotionEffectType.JUMP))
                return;

            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000,
                    Parkour.getPlugin().getConfig().getInt("ParkourModes.Moon.Strength")));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
            return;

        Player player = event.getPlayer();
        ParkourSession session = PlayerMethods.getParkourSession(player.getName());

        // Only do fall checks if mode is not 'dropper' course
        if (session.getMode() != ParkourMode.DROPPER &&
                player.getFallDistance() > Parkour.getSettings().getMaxFallTicks()) {
            PlayerMethods.playerDie(player);
            return;
        }

        if (player.getLocation().getBlock().isLiquid() &&
                Parkour.getPlugin().getConfig().getBoolean("OnCourse.DieInLiquid")) {
            PlayerMethods.playerDie(player);
        }

        if (!Parkour.getSettings().isUseParkourKit())
            return;

        if (Parkour.getSettings().isAttemptLessChecks()) {
            if (event.getTo().getBlockX() == event.getFrom().getBlockX() &&
                    event.getTo().getBlockY() == event.getFrom().getBlockY() &&
                    event.getTo().getBlockZ() == event.getFrom().getBlockZ())
                return;
        }

        Material belowMaterial = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        ParkourKit kit = session.getCourse().getParkourKit();

        if (belowMaterial.equals(Material.SPONGE)) {
            player.setFallDistance(0);
        }

        if (kit.getMaterials().contains(belowMaterial)) {
            String action = kit.getAction(belowMaterial);

            switch (action) {
                case "finish":
                    PlayerMethods.playerFinish(player);
                    break;

                case "death":
                    PlayerMethods.playerDie(player);
                    break;

                case "launch":
                    player.setVelocity(new Vector(0, kit.getStrength(belowMaterial), 0));
                    break;

                case "bounce":
                    if (!player.hasPotionEffect(PotionEffectType.JUMP))
                        player.addPotionEffect(
                                new PotionEffect(PotionEffectType.JUMP,
                                        kit.getDuration(belowMaterial),
                                        kit.getStrength(belowMaterial).intValue()));
                    break;

                case "speed":
                    if (!player.hasPotionEffect(PotionEffectType.SPEED))
                        player.addPotionEffect(
                                new PotionEffect(PotionEffectType.SPEED,
                                        kit.getDuration(belowMaterial),
                                        kit.getStrength(belowMaterial).intValue()));
                    break;

                case "norun":
                    player.setSprinting(false);
                    break;

                case "nopotion":
                    for (PotionEffect effect : player.getActivePotionEffects())
                        player.removePotionEffect(effect.getType());

                    player.setFireTicks(0);
                    break;
            }
        }

        for (BlockFace blockFace : blockFaces) {
            Material material = player.getLocation().getBlock().getRelative(blockFace).getType();

            if (kit.getMaterials().contains(material)) {
                String action = kit.getAction(material);

                switch (action) {
                    case "climb":
                        if (!player.isSneaking()) {
                            player.setVelocity(new Vector(0, kit.getStrength(material), 0));
                        }
                        break;
                    case "repulse":
                        double strength = kit.getStrength(material);
                        double x = blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH ? 0
                                : blockFace == BlockFace.EAST ? -strength : strength;
                        double z = blockFace == BlockFace.EAST || blockFace == BlockFace.WEST ? 0
                                : blockFace == BlockFace.NORTH ? strength : -strength;

                        player.setVelocity(new Vector(x, 0.1, z));
                        break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!Parkour.getSettings().isChatPrefix())
            return;

        String finalMessage;
        String rank = PlayerInfo.getRank(event.getPlayer());

        // should we completely override the chat format
        if (Parkour.getSettings().isChatPrefixOverride()) {
            finalMessage = Utils.colour(Utils.getTranslation("Event.Chat", false)
                    .replace("%RANK%", rank)
                    .replace("%PLAYER%", event.getPlayer().getDisplayName())
                    .replace("%MESSAGE%", event.getMessage()));
        } else {
            // or do we use the existing format, just replacing the Parkour variables
            finalMessage = Utils.colour(event.getFormat()
                    .replace("%RANK%", rank)
                    .replace("%PLAYER%", event.getPlayer().getDisplayName()));
        }

        event.setFormat(finalMessage);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
            return;

        if (!Utils.hasPermission(event.getPlayer(), "Parkour.Admin")
                || (!Parkour.getPlugin().getConfig().getBoolean("OnCourse.AdminPlaceBreakBlocks")))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
            return;

        if (!Utils.hasPermission(event.getPlayer(), "Parkour.Admin")
                || (!Parkour.getPlugin().getConfig().getBoolean("OnCourse.AdminPlaceBreakBlocks")))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreakingHangingItem(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player) || !PlayerMethods.isPlaying(event.getRemover().getName()))
            return;

        if (!Utils.hasPermission((Player) event.getRemover(), "Parkour.Admin")
                || (!Parkour.getPlugin().getConfig().getBoolean("OnCourse.AdminPlaceBreakBlocks")))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (PlayerMethods.isPlaying(((Player)event.getEntity()).getName()))
                event.setCancelled(true);
        } else if (event.getDamager() instanceof Player) {
            if (PlayerMethods.isPlaying(((Player)event.getDamager()).getName()))
                if (Parkour.getSettings().isPreventAttackingEntities())
                    event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Player)
            if (PlayerMethods.isPlaying(((Player)event.getEntity()).getName()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (!PlayerMethods.isPlaying(player.getName()))
            return;

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            if (Parkour.getPlugin().getConfig().getBoolean("OnCourse.DieInVoid")) {
                PlayerMethods.playerDie(player);
                return;
            }
        }

        if (Parkour.getSettings().isDisablePlayerDamage()) {
            event.setDamage(0);
            return;
        }

        if (PlayerMethods.getParkourSession(player.getName()).getMode() == ParkourMode.DROPPER
                && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDamage(0);
            event.setCancelled(true);
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

        if (Parkour.getPlugin().getConfig().getBoolean("OnCourse.DisableItemDrop"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
            return;

        if (Parkour.getPlugin().getConfig().getBoolean("OnCourse.DisableItemPickup"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Parkour.getSettings().isDisplayWelcome())
            event.getPlayer().sendMessage(Utils.getTranslation("Event.Join")
                    .replace("%VERSION%", Static.getVersion().toString()));

        if (PlayerMethods.isPlaying(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(Utils.getTranslation("Parkour.Continue")
                    .replace("%COURSE%", PlayerMethods.getParkourSession(event.getPlayer().getName()).getCourse().getName()));
        }

        if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
            return;

        if (Parkour.getPlugin().getConfig().getBoolean("OnLeaveServer.TeleportToLastCheckpoint"))
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

        if (!Parkour.getPlugin().getConfig().getBoolean("OnCourse.DisableFly"))
            return;

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);
        }
    }

    @EventHandler
    public void onInventoryInteract(PlayerInteractEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
            return;

        Player player = event.getPlayer();

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR))
            return;

        if (!player.isSneaking() && Parkour.getPlugin().getConfig().getBoolean("OnCourse.SneakToInteractItems"))
            return;

        if (PlayerMethods.isPlayerInTestmode(player.getName()))
            return;

        if (Utils.getMaterialInPlayersHand(player) == Parkour.getSettings().getLastCheckpoint()) {
            if (Utils.delayPlayerEvent(player, 1)) {
                event.setCancelled(true);
                PlayerMethods.playerDie(player);
            }

        } else if (Utils.getMaterialInPlayersHand(player) == Parkour.getSettings().getHideall()) {
            if (Utils.delayPlayerEvent(player, 1)) {
                event.setCancelled(true);
                Utils.toggleVisibility(player);
            }

        } else if (Utils.getMaterialInPlayersHand(player) == Parkour.getSettings().getLeave()) {
            if (Utils.delayPlayerEvent(player, 1)) {
                event.setCancelled(true);
                PlayerMethods.playerLeave(player);
            }
        }
    }

    @EventHandler
    public void onInventoryInteractFreedom(PlayerInteractEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
            return;

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)
                && !event.getAction().equals(Action.LEFT_CLICK_AIR) && !event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            return;

        if (PlayerMethods.getParkourSession(event.getPlayer().getName()).getMode() != ParkourMode.FREEDOM)
            return;

        Player player = event.getPlayer();

        if (PlayerMethods.isPlayerInTestmode(player.getName()))
            return;

        event.setCancelled(true);

        if (Utils.getMaterialInPlayersHand(player) == Material.REDSTONE_TORCH_ON) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                PlayerMethods.getParkourSession(player.getName()).getCourse().setCheckpoint(CheckpointMethods.createCheckpointFromPlayerLocation(player));
                player.sendMessage(Utils.getTranslation("Mode.Freedom.Save"));
            } else {
                player.teleport(PlayerMethods.getParkourSession(player.getName()).getCourse().getCurrentCheckpoint().getLocation());
                player.sendMessage(Utils.getTranslation("Mode.Freedom.Load"));
            }
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

        if (Parkour.getPlugin().getConfig().getBoolean("OnCourse.PreventPlateStick"))
            event.setCancelled(true);

        ParkourSession session = PlayerMethods.getParkourSession(event.getPlayer().getName());
        Course course = session.getCourse();

        if (session.getCheckpoint() == course.getCheckpoints())
            return;

        Checkpoint check = course.getCurrentCheckpoint();

        if (check == null)
            return;

        if (check.getNextCheckpointX() == below.getLocation().getBlockX() && check.getNextCheckpointY() == below.getLocation().getBlockY() && check.getNextCheckpointZ() == below.getLocation().getBlockZ()) {
        	if (Parkour.getSettings().isFirstCheckAsStart() && session.getCheckpoint() == 0) {
                session.resetTimeStarted();
                Utils.sendActionBar(event.getPlayer(), Utils.getTranslation("Parkour.TimerStarted", false), true);
            }
        	PlayerMethods.increaseCheckpoint(session, event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
            return;

        if (!Parkour.getPlugin().getConfig().getBoolean("OnCourse.PreventOpeningOtherInventories"))
            return;

        if (event.getInventory().getType() != InventoryType.PLAYER)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        boolean commandIsPa = event.getMessage().startsWith("/pa ")
                || event.getMessage().startsWith("/parkour ")
                || event.getMessage().startsWith("/pkr ");

        Player player = event.getPlayer();

        if (commandIsPa && QuestionManager.hasPlayerBeenAskedQuestion(player.getName())) {
            String[] args = event.getMessage().split(" ");
            if (args.length <= 1) {
                player.sendMessage(Static.getParkourString() + "Invalid answer.");
                player.sendMessage("Please use either " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " or " + ChatColor.AQUA + "/pa no");
            } else {
                QuestionManager.answerQuestion(player, args[1]);
            }
            event.setCancelled(true);
        }

        if (!commandIsPa && PlayerMethods.isPlaying(player.getName())) {
            if (!Parkour.getSettings().isDisableCommands())
                return;

            if (player.hasPermission("Parkour.Admin.*") || player.hasPermission("Parkour.*"))
                return;

            boolean allowed = false;
            for (String word : Static.getWhitelistedCommands()) {
                if (event.getMessage().startsWith("/" + word + " ") || (event.getMessage().equalsIgnoreCase("/" + word))) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                event.setCancelled(true);
                player.sendMessage(Utils.getTranslation("Error.Command"));
            }
        }
    }
}

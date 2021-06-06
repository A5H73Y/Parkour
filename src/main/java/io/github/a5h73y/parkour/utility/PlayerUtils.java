package io.github.a5h73y.parkour.utility;

import com.cryptomorin.xseries.XPotion;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Player Utility methods.
 */
public class PlayerUtils {

	public static final String PLAYER_COMMAND_PREFIX = "player:";

	/**
	 * Dispatch a Server / Player command with Parkour internal placeholders.
	 * If the command start with "player:" it will be executed by the Player.
	 * @param command command to execute
	 * @param player player
	 */
	public static void dispatchServerPlayerCommand(String command, Player player, ParkourSession session) {
		command = TranslationUtils.replaceAllParkourPlaceholders(command, player, session);
		dispatchCommand(command, player);
	}

	/**
	 * Dispatch a Server / Player command.
	 * If the command start with "player:" it will be executed by the Player.
	 * @param command command to execute
	 * @param player player
	 */
	public static void dispatchServerPlayerCommand(String command, Player player) {
		command = TranslationUtils.replaceAllPlayerPlaceholders(command, player);
		dispatchCommand(command, player);
	}

	/**
	 * Apply Potion Effect to Player.
	 *
	 * @param potionType potion type
	 * @param duration duration
	 * @param amplifier amplifier
	 * @param players players
	 */
	public static void applyPotionEffect(PotionEffectType potionType, int duration, int amplifier, Player... players) {
		for (Player player : players) {
			player.addPotionEffect(new PotionEffect(potionType, duration, amplifier));
		}
	}

	/**
	 * Try to Apply Potion Effect to Player.
	 * If the name matches a known Potion Type, it will be applied.
	 *
	 * @param potionTypeName potion type name
	 * @param duration duration
	 * @param amplifier amplifier
	 * @param players players
	 */
	public static void applyPotionEffect(String potionTypeName, int duration, int amplifier, Player... players) {
		XPotion.matchXPotion(potionTypeName).ifPresent(action ->
				applyPotionEffect(action.parsePotionEffectType(), duration, amplifier, players));
	}

	/**
	 * Remove a certain Potion Type from Players.
	 *
	 * @param potionType potion type
	 * @param players players
	 */
	public static void removePotionEffect(PotionEffectType potionType, Player... players) {
		for (Player player : players) {
			player.removePotionEffect(potionType);
		}
	}

	/**
	 * Remove all Potion Types from Players.
	 *
	 * @param players players
	 */
	public static void removeAllPotionEffects(Player... players) {
		for (Player player : players) {
			for (PotionEffect effect : player.getActivePotionEffects()) {
				player.removePotionEffect(effect.getType());
			}
		}
	}

	/**
	 * Fill the Players Health.
	 *
	 * @param player player
	 */
	public static void fullyHealPlayer(Player player) {
		Damageable playerDamage = player;
		playerDamage.setHealth(playerDamage.getMaxHealth());
		TranslationUtils.sendMessage(player, "Healed!");
	}

	/**
	 * Change Player's GameMode.
	 * If the GameMode name matches, it will be applied.
	 *
	 * @param player player
	 * @param gameModeName game mode name
	 */
	public static void changeGameMode(Player player, String gameModeName) {
		if (PluginUtils.doesGameModeExist(gameModeName.toUpperCase())) {
			changeGameMode(player, GameMode.valueOf(gameModeName.toUpperCase()));
		} else {
			TranslationUtils.sendMessage(player, "GameMode not recognised.");
		}
	}

	/**
	 * Change Player's GameMode.
	 *
	 * @param player player
	 * @param gameMode game mode
	 */
	public static void changeGameMode(Player player, GameMode gameMode) {
		if (gameMode != player.getGameMode()) {
			player.setGameMode(gameMode);
			TranslationUtils.sendMessage(player, "GameMode set to &b" + StringUtils.standardizeText(gameMode.name()));
		}
	}

	/**
	 * Teleport Player to Location.
	 * Reset their fall distance so they don't take damage upon teleportation.
	 *
	 * @param player player
	 * @param location location
	 */
	public static void teleportToLocation(Player player, Location location) {
		player.setFallDistance(0);
		player.teleport(location);
	}

	/**
	 * Hide other player from Player.
	 * @param player player
	 * @param otherPlayer player to hide
	 */
	public static void hidePlayer(Player player, Player otherPlayer) {
		try {
			// 1.12+ servers
			player.hidePlayer(Parkour.getInstance(), otherPlayer);
		} catch (NoSuchMethodError e) {
			// 1.11 fallback
			player.hidePlayer(otherPlayer);
		}
	}

	/**
	 * Show other player to Player.
	 * @param player player
	 * @param otherPlayer player to show
	 */
	public static void showPlayer(Player player, Player otherPlayer) {
		try {
			// 1.12+ servers
			player.showPlayer(Parkour.getInstance(), otherPlayer);
		} catch (NoSuchMethodError e) {
			// 1.11 fallback
			player.showPlayer(otherPlayer);
		}
	}

	/**
	 * Dispatch command for Player.
	 * If the commands begins with "player:" it will be player executed, otherwise server executed.
	 * @param command command to run
	 * @param player player
	 */
	private static void dispatchCommand(String command, Player player) {
		if (command.startsWith(PLAYER_COMMAND_PREFIX)) {
			player.performCommand(command.split(PLAYER_COMMAND_PREFIX)[1]);

		} else {
			Server server = Parkour.getInstance().getServer();
			server.dispatchCommand(server.getConsoleSender(), command);
		}
	}
}

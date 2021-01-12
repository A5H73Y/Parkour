package io.github.a5h73y.parkour.utility;

import com.cryptomorin.xseries.XPotion;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Player Utility methods.
 */
public class PlayerUtils {

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
}

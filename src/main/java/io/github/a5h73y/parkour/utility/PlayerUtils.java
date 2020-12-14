package io.github.a5h73y.parkour.utility;

import com.cryptomorin.xseries.XPotion;
import io.github.a5h73y.parkour.Parkour;
import org.bukkit.GameMode;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerUtils {

	public static void applyPotionEffect(PotionEffectType potionType, int duration, int amplifier, Player... players) {
		for (Player player : players) {
			player.addPotionEffect(new PotionEffect(potionType, duration, amplifier));
		}
	}

	public static void applyPotionEffect(String potionTypeName, int duration, int amplifier, Player... players) {
		XPotion.matchXPotion(potionTypeName).ifPresent((action) ->
				applyPotionEffect(action.parsePotionEffectType(), duration, amplifier, players));
	}

	public static void removePotionEffect(PotionEffectType potionType, Player... players) {
		for (Player player : players) {
			player.removePotionEffect(potionType);
		}
	}

	public static void removeAllPotionEffects(Player... players) {
		for (Player player : players) {
			for (PotionEffect effect : player.getActivePotionEffects()) {
				player.removePotionEffect(effect.getType());
			}
		}
	}

	public static void fullyHealPlayer(Player player) {
		Damageable playerDamage = player;
		playerDamage.setHealth(playerDamage.getMaxHealth());
		player.sendMessage(Parkour.getPrefix() + "Healed!");
	}

	public static void applyGameModeChange(Player player, String gameModeName) {
		if (PluginUtils.doesGameModeExist(gameModeName.toUpperCase())) {
			GameMode gameMode = GameMode.valueOf(gameModeName.toUpperCase());
			if (gameMode != player.getGameMode()) {
				player.setGameMode(gameMode);
				player.sendMessage(Parkour.getPrefix() + "GameMode set to " + StringUtils.standardizeText(gameMode.name()));
			}
		} else {
			player.sendMessage(Parkour.getPrefix() + "GameMode not recognised.");
		}
	}
}

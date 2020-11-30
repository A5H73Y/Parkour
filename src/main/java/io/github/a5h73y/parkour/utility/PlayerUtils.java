package io.github.a5h73y.parkour.utility;

import com.cryptomorin.xseries.XPotion;
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
}

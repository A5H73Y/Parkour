package io.github.a5h73y.parkour.utility;

import io.github.a5h73y.parkour.Parkour;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtils {

	public static void playTimerSound(Player player) {
		if (Parkour.getDefaultConfig().isSoundEnabled()) {
			player.playSound(player.getLocation(), getTimerSound(), 2.0f, 1.75f);
		}
	}

	public static Sound getTimerSound() {
		String soundName = "BLOCK_NOTE_BLOCK_PLING";
		if (PluginUtils.getMinorServerVersion() <= 8) {
			soundName = "NOTE_PLING";
		} else if (PluginUtils.getMinorServerVersion() <= 12) {
			soundName = "BLOCK_NOTE_PLING";
		}
		return Sound.valueOf(soundName);
	}

}

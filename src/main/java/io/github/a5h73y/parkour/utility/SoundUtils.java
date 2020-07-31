package io.github.a5h73y.parkour.utility;

public class SoundUtils {

	public static String getTimerSound() {
		String snd = "BLOCK_NOTE_BLOCK_PLING";
		if (PluginUtils.getMinorServerVersion() <= 8) {
			snd = "NOTE_PLING";
		} else if (PluginUtils.getMinorServerVersion() <= 12) {
			snd = "BLOCK_NOTE_PLING";
		}
		return snd;
	}

}

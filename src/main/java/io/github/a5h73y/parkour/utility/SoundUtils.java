package io.github.a5h73y.parkour.utility;

public class SoundUtils {

	public static String getTimerSound() {
		String soundName = "BLOCK_NOTE_BLOCK_PLING";
		if (PluginUtils.getMinorServerVersion() <= 8) {
			soundName = "NOTE_PLING";
		} else if (PluginUtils.getMinorServerVersion() <= 12) {
			soundName = "BLOCK_NOTE_PLING";
		}
		return soundName;
	}

}

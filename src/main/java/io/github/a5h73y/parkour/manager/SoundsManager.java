package io.github.a5h73y.parkour.manager;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.SoundType;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.Cacheable;
import io.github.a5h73y.parkour.utility.PluginUtils;
import java.util.EnumMap;
import java.util.Optional;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundsManager extends AbstractPluginReceiver implements Cacheable<SoundsManager.SoundDetails> {

	private final EnumMap<SoundType, SoundDetails> soundTypes = new EnumMap<>(SoundType.class);

	public SoundsManager(Parkour parkour) {
		super(parkour);
		populateCache();
	}

	public void playSound(Player player, SoundType soundType) {
		if (!parkour.getPlayerManager().isInQuietMode(player)) {
			SoundDetails details = soundTypes.get(soundType);
			if (details != null) {
				player.playSound(player.getLocation(), details.getSound(), details.getVolume(), details.getPitch());
			}
		}
	}

	private void populateCache() {
		if (parkour.getConfig().isSoundEnabled()) {
			for (SoundType soundType : SoundType.values()) {
				if (parkour.getConfig().isSoundEnabled(soundType)) {
					String soundName = parkour.getConfig().getString("Sounds." + soundType.getConfigEntry() + ".Sound");
					Optional<XSound> xSound = XSound.matchXSound(soundName);

					if (xSound.isPresent()) {
						float volume = (float) parkour.getConfig().getDouble("Sounds." + soundType.getConfigEntry() + ".Volume");
						float pitch = (float) parkour.getConfig().getDouble("Sounds." + soundType.getConfigEntry() + ".Pitch");
						soundTypes.put(soundType, new SoundDetails(xSound.get().parseSound(), volume, pitch));

					} else {
						PluginUtils.log("Unknown Sound: " + soundName + ". Sound Type: " + soundType.name() + " disabled.", 1);
					}
				}
			}
		}
	}

	@Override
	public int getCacheSize() {
		return soundTypes.size();
	}

	@Override
	public void clearCache() {
		soundTypes.clear();
		populateCache();
	}

	protected static class SoundDetails {

		private final Sound sound;
		private final float volume;
		private final float pitch;

		public SoundDetails(Sound sound, float volume, float pitch) {
			this.sound = sound;
			this.volume = volume;
			this.pitch = pitch;
		}

		public Sound getSound() {
			return sound;
		}

		public float getVolume() {
			return volume;
		}

		public float getPitch() {
			return pitch;
		}
	}
}

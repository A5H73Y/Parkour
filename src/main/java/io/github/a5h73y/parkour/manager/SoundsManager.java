package io.github.a5h73y.parkour.manager;

import com.cryptomorin.xseries.XSound;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.SoundType;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.Cacheable;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.PluginUtils;
import java.util.EnumMap;
import java.util.Optional;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Sounds Manager.
 * Manage the sounds used throughout the Plugin.
 * Each sound can be changed, and the pitch and volume adjusted.
 * Keeps a Cache of {@link SoundType} with associated {@link SoundDetails} which can be reused.
 * If a sound is not enabled it will not be included in the Cache.
 */
public class SoundsManager extends AbstractPluginReceiver implements Cacheable<SoundsManager.SoundDetails> {

	private final EnumMap<SoundType, SoundDetails> soundTypes = new EnumMap<>(SoundType.class);

	public SoundsManager(final Parkour parkour) {
		super(parkour);
		populateCache();
	}

	/**
	 * Play the requested Sound.
	 * Will check if the player is not in Quiet Mode and the sound is enabled.
	 *
	 * @param player player
	 * @param soundType sound type
	 */
	public void playSound(Player player, SoundType soundType) {
		if (!PlayerInfo.isQuietMode(player)) {
			SoundDetails details = soundTypes.get(soundType);
			if (details != null) {
				player.playSound(player.getLocation(), details.getSound(),
						details.getVolume(), details.getPitch());
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

	/**
	 * Populate the cache.
	 * Only enabled sounds will be added, and their details retrieved.
	 */
	private void populateCache() {
		if (parkour.getConfig().isSoundEnabled()) {
			for (SoundType soundType : SoundType.values()) {
				if (!parkour.getConfig().isSoundEnabled(soundType)) {
					continue;
				}

				String soundName = parkour.getConfig().getString("Sounds."
						+ soundType.getConfigEntry() + ".Sound");
				Optional<XSound> matchingSound = XSound.matchXSound(soundName);

				if (matchingSound.isPresent()) {
					float volume = (float) parkour.getConfig().getDouble("Sounds."
							+ soundType.getConfigEntry() + ".Volume");
					float pitch = (float) parkour.getConfig().getDouble("Sounds."
							+ soundType.getConfigEntry() + ".Pitch");
					soundTypes.put(soundType, new SoundDetails(
							matchingSound.get().parseSound(), volume, pitch));

				} else {
					PluginUtils.log("Unknown Sound: " + soundName + ". Sound Type: "
							+ soundType.name() + " disabled.", 1);
				}
			}
		}
	}

	/**
	 * Sound Details Model.
	 * Each Sound must have a volume and pitch.
	 */
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

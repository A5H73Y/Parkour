package io.github.a5h73y.parkour.plugin;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * BountifulAPI wrapper to provide Title and Action Bar messages to the Player.
 * BountifulAPI integration will be used when Spigot's implementation isn't supported.
 */
public class BountifulApi extends PluginWrapper {

	public static final String JOIN_COURSE = "JoinCourse";
	public static final String CHECKPOINT = "Checkpoint";
	public static final String DEATH = "Death";
	public static final String LEAVE = "Leave";
	public static final String FINISH = "Finish";

	private boolean useSpigotMethods;
	private int inDuration;
	private int outDuration;

	public BountifulApi(Parkour parkour) {
		super(parkour);
	}

	@Override
	public String getPluginName() {
		return "BountifulAPI";
	}

	@Override
	protected void initialise() {
		super.initialise();

		useSpigotMethods = PluginUtils.getMinorServerVersion() > 10;
		inDuration = parkour.getParkourConfig().getTitleIn();
		outDuration = parkour.getParkourConfig().getTitleOut();
	}

	public boolean hasTitleSupport() {
		return useSpigotMethods || isEnabled();
	}

	/**
	 * Send the Player the title.
	 * Quiet Mode will be respected and not message the Player when enabled.
	 * Attempting the Title will mean either spigot's title implementation or BountifulAPI will be used.
	 *
	 * @param player target player
	 * @param title title text
	 * @param configEntry config entry for title
	 */
	public void sendTitle(Player player, String title, @Nullable String configEntry) {
		sendFullTitle(player, title, " ", configEntry);
	}

	/**
	 * Send the Player the subtitle.
	 * Quiet Mode will be respected and not message the Player when enabled.
	 * Attempting the Title will mean either spigot's title implementation or BountifulAPI will be used.
	 *
	 * @param player target player
	 * @param subTitle sub title text
	 * @param configEntry config entry for title
	 */
	public void sendSubTitle(Player player, String subTitle, @Nullable String configEntry) {
		sendFullTitle(player, " ", subTitle, configEntry);
	}

	/**
	 * Send the Player a title message.
	 * Empty strings can be passed to display 'nothing' in either sections.
	 * Quiet Mode will be respected and not message the Player when enabled.
	 * Attempting the Title will mean either spigot's title implementation or BountifulAPI will be used.
	 *
	 * @param player target player
	 * @param title main title text
	 * @param subTitle sub title text
	 * @param configEntry config entry for title
	 */
	public void sendFullTitle(Player player, String title, String subTitle, @Nullable String configEntry) {
		if (parkour.getQuietModeManager().isQuietMode(player)) {
			return;
		}

		if (isTitleEnabled(configEntry)) {
			int stayDuration = getStayDuration(configEntry);

			if (useSpigotMethods) {
				player.sendTitle(title, subTitle, inDuration, stayDuration, outDuration);
				return;

			} else if (isEnabled()) {
				BountifulAPI.sendTitle(player, inDuration, stayDuration, outDuration, title, subTitle);
				return;
			}
		}

		StringBuilder message = new StringBuilder();

		if (ValidationUtils.isStringValid(title)) {
			message.append(title).append(" ");
		}

		if (ValidationUtils.isStringValid(subTitle)) {
			message.append(subTitle);
		}

		TranslationUtils.sendMessage(player, message.toString().trim());
	}

	/**
	 * Send an Action Bar to the Player.
	 * Quiet Mode will be respected and not message the Player when enabled.
	 * Attempting the Title will mean either spigot's action bar implementation or BountifulAPI will be used.
	 *
	 * @param player target player
	 * @param title action bar text
	 * @param configEntry config entry for title
	 */
	public void sendActionBar(Player player, String title, @Nullable String configEntry) {
		if (parkour.getQuietModeManager().isQuietMode(player)) {
			return;
		}

		if (isTitleEnabled(configEntry)) {
			if (useSpigotMethods) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(title));

			} else if (isEnabled()) {
				BountifulAPI.sendActionBar(player, title);

			} else {
				TranslationUtils.sendMessage(player, title);
			}
		} else {
			TranslationUtils.sendMessage(player, title);
		}
	}

	public void sendActionBar(Player player, String title) {
		sendActionBar(player, title, null);
	}

	private boolean isTitleEnabled(@Nullable String configEntry) {
		return configEntry == null || parkour.getParkourConfig().getBoolean("DisplayTitle." + configEntry + ".Enabled");
	}

	private int getStayDuration(String configEntry) {
		return parkour.getParkourConfig().getInt("DisplayTitle." + configEntry + ".Stay");
	}
}

package io.github.a5h73y.parkour.plugin;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.PluginUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * TitleUtils wrapper to provide Title and Action Bar messages to the Player.
 */
// TODO have a proper tidy up
public class TitleUtils extends PluginWrapper {

	private boolean serverSupported;
	private int inDuration;
	private int stayDuration;
	private int outDuration;

	@Override
	public String getPluginName() {
		return "BountifulAPI";
	}

	@Override
	protected void initialise() {
		super.initialise();

		serverSupported = PluginUtils.getMinorServerVersion() > 10;
		inDuration = Parkour.getDefaultConfig().getTitleIn();
		stayDuration = Parkour.getDefaultConfig().getTitleStay();
		outDuration = Parkour.getDefaultConfig().getTitleOut();
	}

	/**
	 * Send the Player the title.
	 * Quiet Mode will be respected and not message the Player when enabled.
	 * Attempting the Title will mean either spigot's title implementation or BountifulAPI will be used.
	 *
	 * @param player target player
	 * @param title title text
	 * @param attemptTitle attempt to show the title
	 */
	public void sendTitle(Player player, String title, boolean attemptTitle) {
		sendFullTitle(player, title, "", attemptTitle);
	}

	/**
	 * Send the Player the sub title.
	 * Quiet Mode will be respected and not message the Player when enabled.
	 * Attempting the Title will mean either spigot's title implementation or BountifulAPI will be used.
	 *
	 * @param player target player
	 * @param subTitle sub title text
	 * @param attemptTitle attempt to show the title
	 */
	public void sendSubTitle(Player player, String subTitle, boolean attemptTitle) {
		sendFullTitle(player, "", subTitle, attemptTitle);
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
	 * @param attemptTitle attempt to show the title
	 */
	public void sendFullTitle(Player player, String title, String subTitle, boolean attemptTitle) {
		if (PlayerInfo.isQuietMode(player)) {
			return;
		}

		if (attemptTitle && serverSupported) {
			player.sendTitle(title, subTitle, inDuration, stayDuration, outDuration);
			return;
		}

		if (ValidationUtils.isStringValid(title)) {
			TranslationUtils.sendMessage(player, title);
		}
		if (ValidationUtils.isStringValid(subTitle)) {
			TranslationUtils.sendMessage(player, subTitle);
		}
	}

	/**
	 * Send an Action Bar to the Player.
	 * Quiet Mode will be respected and not message the Player when enabled.
	 * Attempting the Title will mean either spigot's action bar implementation or BountifulAPI will be used.
	 *
	 * @param player target player
	 * @param title action bar text
	 * @param attemptTitle attempt to show the title
	 */
	public void sendActionBar(Player player, String title, boolean attemptTitle) {
		if (PlayerInfo.isQuietMode(player)) {
			return;
		}

		if (attemptTitle) {
			if (serverSupported) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(title));

			} else {
				TranslationUtils.sendMessage(player, title);
			}
		} else {
			TranslationUtils.sendMessage(player, title);
		}
	}
}

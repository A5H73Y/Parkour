package io.github.a5h73y.parkour.plugin;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.PluginUtils;
import com.connorlinfoot.bountifulapi.BountifulAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * {@link com.connorlinfoot.bountifulapi.BountifulAPI} integration.
 * Allow for Titles and ActionBar messages.
 */
public class BountifulApi extends PluginWrapper {

	private boolean useSpigotMethods;
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

		useSpigotMethods = PluginUtils.getMinorServerVersion() > 9;
		inDuration = Parkour.getDefaultConfig().getTitleIn();
		stayDuration = Parkour.getDefaultConfig().getTitleStay();
		outDuration = Parkour.getDefaultConfig().getTitleOut();
	}

	/**
	 * The following methods use the BountifulAPI plugin
	 * If the user is has 'Quiet mode' enabled, no message will be sent
	 * If the BountifulAPI plugin is not installed, the message will be sent via chat
	 *
	 * @param player
	 * @param title
	 * @param attemptTitle
	 */
	public void sendTitle(Player player, String title, boolean attemptTitle) {
		sendFullTitle(player, title, "", attemptTitle);
	}

	public void sendSubTitle(Player player, String subTitle, boolean attemptTitle) {
		sendFullTitle(player, "", subTitle, attemptTitle);
	}

	/**
	 * Send a title message to the player.
	 * Empty strings can be passed to display 'nothing' in either sections.
	 *
	 * @param player target player
	 * @param title main title text
	 * @param subTitle sub title text
	 * @param attemptTitle attempt to show the title
	 */
	public void sendFullTitle(Player player, String title, String subTitle, boolean attemptTitle) {
		if (Parkour.getInstance().getPlayerManager().isInQuietMode(player.getName())) {
			return;
		}

		if (attemptTitle) {
			if (isEnabled()) {
				BountifulAPI.sendTitle(player, inDuration, stayDuration, outDuration, title, subTitle);

			} else if (useSpigotMethods) {
				player.sendTitle(title, subTitle, inDuration, stayDuration, outDuration);
			}
		} else {
			player.sendMessage(Parkour.getPrefix() + title);
		}
	}

	public void sendActionBar(Player player, String title, boolean attemptTitle) {
		if (Parkour.getInstance().getPlayerManager().isInQuietMode(player.getName())) {
			return;
		}

		if (attemptTitle) {
			if (isEnabled()) {
				BountifulAPI.sendActionBar(player, title);

			} else if (useSpigotMethods) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(title));
			}
		} else {
			player.sendMessage(Parkour.getPrefix() + title);
		}
	}
}

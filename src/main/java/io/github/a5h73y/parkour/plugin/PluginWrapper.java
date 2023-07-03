package io.github.a5h73y.parkour.plugin;

import static org.bukkit.Bukkit.getServer;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.utility.PluginUtils;
import org.bukkit.plugin.Plugin;

/**
 * 3rd party Plugin Wrapper.
 * Created to wrap start-up functionality of the plugins.
 */
public abstract class PluginWrapper extends AbstractPluginReceiver {

	private boolean enabled = false;

	/**
	 * The name of the 3rd party plugin.
	 *
	 * @return plugin name.
	 */
	public abstract String getPluginName();

	public String getPluginDisplayName() {
		return this.getPluginName();
	}

	/**
	 * Initialise the startup of the plugin on Construction of object.
	 */
	protected PluginWrapper(Parkour parkour) {
		super(parkour);
		initialise();
	}

	/**
	 * Initialise the setup of the 3rd party plugin.
	 */
	protected void initialise() {
		// if the config prevents integration, don't begin setup.
		if (!parkour.getParkourConfig().getBoolean("Plugin." + getPluginName() + ".Enabled")) {
			return;
		}

		// try to find the plugin running on the server.
		Plugin externalPlugin = getServer().getPluginManager().getPlugin(getPluginName());

		// if the plugin is found and enabled, allow usage
		// otherwise display error
		if (externalPlugin != null && externalPlugin.isEnabled()) {
			enabled = true;
			PluginUtils.log("[" + getPluginDisplayName() + "] Successfully linked. "
					+ "Version: " + externalPlugin.getDescription().getVersion(), 0);

		} else {
			PluginUtils.log("[" + getPluginDisplayName() + "] Plugin is missing, link was unsuccessful.", 1);
		}
	}

	/**
	 * Flag to indicate if the plugin started correctly.
	 *
	 * @return plugin enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Flag to indicate if the plugin is enabled.
	 *
	 * @param enabled plugin enabled.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}

package io.github.a5h73y.parkour.plugin;

import static org.bukkit.Bukkit.getServer;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.PluginUtils;
import org.bukkit.plugin.Plugin;

/**
 * 3rd party Plugin Wrapper.
 * Created to wrap start-up functionality of the plugins.
 */
public abstract class PluginWrapper {

	private boolean enabled = false;

	/**
	 * The name of the 3rd party plugin.
	 *
	 * @return plugin name.
	 */
	public abstract String getPluginName();

	/**
	 * Initialise the startup of the plugin on Construction of object.
	 */
	public PluginWrapper() {
		initialise();
	}

	/**
	 * Initialise the setup of the 3rd party plugin.
	 */
	protected void initialise() {
		// if the config prevents integration, don't begin setup.
		if (!Parkour.getDefaultConfig().getBoolean(getPluginName() + ".Enabled")) {
			return;
		}

		// try to find the plugin running on the server.
		Plugin externalPlugin = getServer().getPluginManager().getPlugin(getPluginName());

		// if the plugin is found and enabled, allow usage
		// otherwise display error and disable plugin usage.
		if (externalPlugin != null && externalPlugin.isEnabled()) {
			enabled = true;
			PluginUtils.log("[" + getPluginName() + "] Successfully linked. "
					+ "Version: " + externalPlugin.getDescription().getVersion(), 0);

		} else {
			PluginUtils.log("[" + getPluginName() + "] Plugin is missing, disabling config option.", 1);
			Parkour.getDefaultConfig().set(getPluginName() + ".Enabled", false);
			Parkour.getDefaultConfig().save();
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

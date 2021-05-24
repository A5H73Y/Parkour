package io.github.a5h73y.parkour.other;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import io.github.g00fy2.versioncompare.Version;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ParkourUpdater implements Listener {

	private static final String USER_AGENT  = "A5H73Y";
	private static final String REQUEST_URL = "https://api.spiget.org/v2/resources/%d/versions/latest";

	private final JavaPlugin plugin;
	private final int projectId;
	private Version latestVersion;

	/**
	 * Parkour Project Updater.
	 *
	 * @param plugin current plugin
	 * @param projectId unique project ID.
	 */
	public ParkourUpdater(JavaPlugin plugin, int projectId) {
		this.plugin = plugin;
		this.projectId = projectId;
	}

	/**
	 * Check if the project has an update available async.
	 */
	public void checkForUpdateAsync() {
		try {
			CompletableFuture.runAsync(this::checkForUpdate).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if the project has an update available.
	 */
	public void checkForUpdate() {
		try {
			plugin.getLogger().info("Checking for update...");
			URL url = new URL(String.format(REQUEST_URL, projectId));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("User-Agent", USER_AGENT);

			InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
			JsonElement element = new JsonParser().parse(reader);

			reader.close();
			connection.disconnect();

			if (!element.isJsonObject() || !element.getAsJsonObject().has("name")) {
				plugin.getLogger().info("Received JSON was invalid.");
				return;
			}

			JsonObject versionObject = element.getAsJsonObject();

			Version currentVersion = new Version(plugin.getDescription().getVersion());
			latestVersion = new Version(versionObject.get("name").getAsString());

			if (currentVersion.isLowerThan(latestVersion)) {
				plugin.getLogger().warning("==== " + plugin.getDescription().getName() + " ====");
				plugin.getLogger().warning("An update for Parkour is available: v"
						+ latestVersion.getOriginalString());
				plugin.getLogger().warning("Available at: https://www.spigotmc.org/resources/parkour.23685/");
				plugin.getLogger().warning("=================");
				Bukkit.getScheduler().runTask(plugin, () ->
						Bukkit.getPluginManager().registerEvents(this, plugin));
			} else {
				plugin.getLogger().info("No update available.");
			}
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to check for update.");
			e.printStackTrace();
		}
	}

	/**
	 * On Admin Join Server.
	 * Notify any admins that there is an update available.
	 * Only registered if there is an update available.
	 *
	 * @param event PlayerJoinEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onAdminJoin(PlayerJoinEvent event) {
		if (PermissionUtils.hasPermission(event.getPlayer(), Permission.ADMIN_ALL, false)) {
			TranslationUtils.sendMessage(event.getPlayer(),
					"&lAn update is available: &b&l" + latestVersion.getOriginalString());
		}
	}
}

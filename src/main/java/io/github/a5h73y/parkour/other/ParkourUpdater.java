package io.github.a5h73y.parkour.other;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.bukkit.plugin.java.JavaPlugin;

public class ParkourUpdater {

	private static final String USER_AGENT  = "A5H73Y";
	private static final String REQUEST_URL = "https://api.spiget.org/v2/resources/%d/versions/latest";

	private final JavaPlugin plugin;
	private final int projectId;

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

			InputStreamReader reader = new InputStreamReader(connection.getInputStream());
			JsonElement element = new JsonParser().parse(reader);

			reader.close();
			connection.disconnect();

			if (!element.isJsonObject() || !element.getAsJsonObject().has("name")) {
				plugin.getLogger().info("Received JSON was invalid.");
				return;
			}

			JsonObject versionObject = element.getAsJsonObject();

			double currentVersion = Double.parseDouble(plugin.getDescription().getVersion());
			double latestVersion = Double.parseDouble(versionObject.get("name").getAsString());

			if (latestVersion > currentVersion) {
				plugin.getLogger().info("==== " + plugin.getDescription().getName() + " ====");
				plugin.getLogger().info("An update is available: v" + latestVersion);
				plugin.getLogger().info("==============");
			} else {
				plugin.getLogger().info("No update available.");
			}
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to check for update.");
			e.printStackTrace();
		}
	}
}

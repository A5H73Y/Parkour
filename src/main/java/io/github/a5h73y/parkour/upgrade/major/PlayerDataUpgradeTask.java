package io.github.a5h73y.parkour.upgrade.major;

import io.github.a5h73y.parkour.configuration.serializable.ItemStackArraySerializable;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.type.player.session.ParkourSessionConfig;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedConfigUpgradeTask;
import io.github.a5h73y.parkour.utility.PluginUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class PlayerDataUpgradeTask extends TimedConfigUpgradeTask {

	private final ItemStackArraySerializable itemStackArraySerializable = new ItemStackArraySerializable();

	private final Set<OfflinePlayer> upgradedPlayers = new HashSet<>();

	public PlayerDataUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader, parkourUpgrader.getPlayerConfig());
	}

	@Override
	protected String getTitle() {
		return "Player Data";
	}

	@Override
	protected boolean doWork() {
		// every player uuid
		Set<String> playerIds = getConfig().getKeys(false);

		// upgrade and remove ranks from the config
		upgradeParkourRanks();

		getParkourUpgrader().getLogger().info("Converting " + playerIds.size() + " players.");
		int interval = Math.max(playerIds.size() / 10, 1);
		int count = 0;

		for (String playerId : playerIds) {
			if (count % interval == 0) {
				double percent = Math.ceil((count * 100.0d) / playerIds.size());
				getParkourUpgrader().getLogger().info(percent + "% complete...");
			}

			// if it isn't a valid uuid - ignore it
			if (playerId.split("-").length != 5) {
				continue;
			}

			ConfigurationSection playerSection = getConfig().getConfigurationSection(playerId);
			OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playerId));

			if (playerSection != null) {
				PlayerConfig newPlayerConfig = PlayerConfig.getConfig(player);

				Set<String> playerKeys = playerSection.getKeys(true); // this will mean we get a deep config entry
				for (String key : playerKeys) {
					newPlayerConfig.set(key, playerSection.get(key));
				}

				updateInventorySection(newPlayerConfig, playerId);
				updateCompletedCoursesSection(newPlayerConfig, player);
				// valid and upgraded players - will be used to update their parkour session later
				upgradedPlayers.add(player);
			}

			count++;
		}

		return true;
	}

	/**
	 * Update the Player's ParkourSessions.
	 * Session has to be deserialized then converted into JSON.
	 */
	public void updateParkourSessions() {
		for (OfflinePlayer player : upgradedPlayers) {
			File playerSessionsPath = new File(getParkourUpgrader().getNewConfigManager().getParkourSessionsDir(),
					player.getUniqueId().toString());

			if (!playerSessionsPath.exists()) {
				continue;
			}

			try (Stream<Path> paths = Files.walk(Paths.get(playerSessionsPath.toURI()), 1)) {
				paths.filter(Files::isRegularFile)
						.filter(path -> !path.getFileName().toString().toLowerCase().contains("."))
						.forEach(path -> updateParkourSession(player, path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateParkourSession(OfflinePlayer player, Path path) {
		ParkourSession session = loadParkourSession(path.toFile());

		// it's a valid session, which hasn't got an upgraded file
		// also the Course exists
		if (session != null && session.getCourseName() != null
				&& !ParkourSessionConfig.hasParkourSessionConfig(player, session.getCourseName())
				&& CourseConfig.hasCourseConfig(session.getCourseName())) {
			// deserialize Course from config
			session.setCourseName(session.getCourseName());
			ParkourSessionConfig.getConfig(player, session.getCourseName())
					.saveParkourSession(session);
		}
		path.toFile().delete();
	}

	private ParkourSession loadParkourSession(File sessionFile) {
		ParkourSession session = null;
		try (
				FileInputStream fout = new FileInputStream(sessionFile);
				RelocateSessionObjectInputStream oos = new RelocateSessionObjectInputStream(fout)
		) {
			session = (ParkourSession) oos.readObject();
		} catch (IOException | ClassNotFoundException e) {
			PluginUtils.log("Player's Session couldn't be loaded: " + e.getMessage(), 2);
			e.printStackTrace();
		}

		return session;
	}

	private void updateCompletedCoursesSection(PlayerConfig newPlayerConfig, OfflinePlayer player) {
		if (newPlayerConfig.contains("Completed")) {
			List<String> completedCourses = newPlayerConfig.getStringList("Completed");
			String playerKey = getParkourUpgrader().getNewConfigManager().getDefaultConfig().getPlayerConfigName(player);
			getParkourUpgrader().getNewConfigManager().getCourseCompletionsConfig().set(playerKey, completedCourses);
			newPlayerConfig.remove("Completed");
		}
	}

	private void upgradeParkourRanks() {
		ConfigurationSection parkourRankSection = getConfig().getConfigurationSection("ServerInfo.Levels");
		if (parkourRankSection != null) {
			getParkourUpgrader().getLogger().info("Upgrading ParkourRanks");

			Set<String> parkourRanks = parkourRankSection.getKeys(false);

			for (String parkourRank : parkourRanks) {
				getParkourUpgrader().getNewConfigManager().getParkourRankConfig().set(parkourRank,
						parkourRankSection.getString(parkourRank + ".Rank"));
			}
		}
	}

	private void updateInventorySection(PlayerConfig playerConfig, String playerId) {
		ConfigurationSection inventorySection =
				getParkourUpgrader().getInventoryConfig().getConfigurationSection(playerId);

		if (inventorySection != null) {
			ItemStack[] inventoryStack = getItemStackContents(inventorySection, "Inventory");
			if (inventoryStack != null) {
				playerConfig.set("Inventory", itemStackArraySerializable.serialize(inventoryStack));
			}

			ItemStack[] armorStack = getItemStackContents(inventorySection, "Armor");
			if (armorStack != null) {
				playerConfig.set("Armor", itemStackArraySerializable.serialize(armorStack));
			}

			if (inventorySection.contains("Health")) {
				playerConfig.set("Health", inventorySection.getDouble("Health"));
			}
			if (inventorySection.contains("Hunger")) {
				playerConfig.set("Hunger", inventorySection.getInt("Hunger"));
			}
		}
	}

	private ItemStack[] getItemStackContents(ConfigurationSection configSection, String configEntry) {
		List<ItemStack> contents = (List<ItemStack>) configSection.getList(configEntry);
		return contents != null ? contents.toArray(new ItemStack[0]) : null;
	}

	/**
	 * Package for ParkourSession has changed, need to fix it first.
	 */
	private static class RelocateSessionObjectInputStream extends ObjectInputStream {

		public RelocateSessionObjectInputStream(InputStream in) throws IOException {
			super(in);
		}

		@Override
		protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
			ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();

			if (resultClassDescriptor.getName().equals("io.github.a5h73y.parkour.type.player.ParkourSession")) {
				resultClassDescriptor = ObjectStreamClass.lookup(ParkourSession.class);
			}

			return resultClassDescriptor;
		}
	}
}

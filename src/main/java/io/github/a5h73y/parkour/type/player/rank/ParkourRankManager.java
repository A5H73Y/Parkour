package io.github.a5h73y.parkour.type.player.rank;

import static io.github.a5h73y.parkour.other.ParkourConstants.ERROR_INVALID_AMOUNT;
import static io.github.a5h73y.parkour.other.ParkourConstants.PARKOUR_LEVEL_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PARKOUR_RANK_PLACEHOLDER;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.CacheableParkourManager;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import de.leonhard.storage.Yaml;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class ParkourRankManager extends CacheableParkourManager {

	private final Map<Integer, String> parkourRanks = new TreeMap<>();

	public ParkourRankManager(Parkour parkour) {
		super(parkour);
		populateParkourRanks();
	}

	/**
	 * Find the unlocked ParkourRank for new ParkourLevel.
	 * The highest ParkourRank available will be found first, gradually decreasing until a match.
	 *
	 * @param player target player
	 * @param rewardLevel rewarded ParkourLevel
	 * @return unlocked ParkourRank
	 */
	@Nullable
	public String getUnlockedParkourRank(OfflinePlayer player, int rewardLevel) {
		int currentLevel = parkour.getConfigManager().getPlayerConfig(player).getParkourLevel();
		String result = null;

		while (currentLevel < rewardLevel) {
			if (parkourRanks.containsKey(rewardLevel)) {
				result = parkourRanks.get(rewardLevel);
				break;
			}
			rewardLevel--;
		}
		return result;
	}

	/**
	 * Display all ParkourRanks available.
	 *
	 * @param commandSender command sender
	 */
	public void displayParkourRanks(CommandSender commandSender) {
		TranslationUtils.sendHeading("Parkour Ranks", commandSender);
		parkourRanks.forEach((parkourLevel, parkourRank) ->
				commandSender.sendMessage(TranslationUtils.getTranslation("Parkour.RankInfo", false)
						.replace(PARKOUR_LEVEL_PLACEHOLDER, parkourLevel.toString())
						.replace(PARKOUR_RANK_PLACEHOLDER, parkourRank)));
	}

	/**
	 * Set a ParkourRank reward for a ParkourLevel.
	 * A ParkourRank will be awarded to the Player when the pass the threshold of the ParkourLevel required.
	 *
	 * @param commandSender command sender
	 * @param parkourLevel associated parkour level
	 * @param parkourRank parkour rank rewarded
	 */
	public void setRewardParkourRank(CommandSender commandSender, String parkourLevel, String parkourRank) {
		if (!ValidationUtils.isPositiveInteger(parkourLevel)) {
			TranslationUtils.sendTranslation(ERROR_INVALID_AMOUNT, commandSender);
			return;
		}

		if (!ValidationUtils.isStringValid(parkourRank)) {
			TranslationUtils.sendMessage(commandSender, "ParkourRank is not valid.");
			return;
		}

		parkour.getConfigManager().getParkourRankConfig().set(parkourLevel, parkourRank);
		populateParkourRanks();
		TranslationUtils.sendPropertySet(commandSender, "ParkourRank", "ParkourLevel " + parkourLevel,
				StringUtils.colour(parkourRank));
	}

	@Override
	protected ParkourRankConfig getConfig() {
		return parkour.getConfigManager().getParkourRankConfig();
	}

	@Override
	public int getCacheSize() {
		return parkourRanks.size();
	}

	@Override
	public void clearCache() {
		populateParkourRanks();
	}

	private void populateParkourRanks() {
		parkourRanks.clear();
		Yaml config = parkour.getConfigManager().getParkourRankConfig();

		Set<String> levels = config.singleLayerKeySet();
		List<Integer> orderedLevels = levels.stream()
				.mapToInt(Integer::parseInt).sorted().boxed()
				.collect(Collectors.toList());

		for (Integer level : orderedLevels) {
			String rank = config.getString(level.toString());
			if (rank != null) {
				parkourRanks.put(level, StringUtils.colour(rank));
			}
		}
	}
}

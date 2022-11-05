package io.github.a5h73y.parkour.upgrade.minor;

import static io.github.a5h73y.parkour.upgrade.minor.TimedConfigUpgradeTask.updateConfigEntry;

import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.upgrade.ParkourUpgrader;
import io.github.a5h73y.parkour.upgrade.TimedUpgradeTask;
import io.github.a5h73y.parkour.utility.PlayerUtils;
import java.util.List;
import org.bukkit.OfflinePlayer;

public class PlayerMinorUpgradeTask extends TimedUpgradeTask {

	public PlayerMinorUpgradeTask(ParkourUpgrader parkourUpgrader) {
		super(parkourUpgrader);
	}

	@Override
	protected String getTitle() {
		return "Player Configs";
	}

	@Override
	protected boolean doWork() {
		List<String> uuids = getParkourUpgrader().getNewConfigManager().getAllPlayerUuids();

		uuids.forEach(uuid -> {
			OfflinePlayer player = PlayerUtils.findPlayer(uuid);
			PlayerConfig config = getParkourUpgrader().getNewConfigManager().getPlayerConfig(player);

			updateConfigEntry(config, "Snapshot.JoinLocation", "JoinLocation");
		});

		return true;
	}
}

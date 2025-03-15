package io.github.a5h73y.parkour.event;

import io.github.a5h73y.parkour.type.player.TimeResult;
import org.bukkit.entity.Player;

public class ParkourTimeResultEvent extends ParkourEvent {

    private final TimeResult result;

    public ParkourTimeResultEvent(final Player player, final String courseName, final TimeResult result) {
        super(player, courseName);
        this.result = result;
    }

    public TimeResult getResult() {
        return result;
    }

    public boolean isPlayerRecord() {
        return result == TimeResult.PLAYER_BEST;
    }

    public boolean isGlobalRecord() {
        return result == TimeResult.GLOBAL_BEST;
    }
}

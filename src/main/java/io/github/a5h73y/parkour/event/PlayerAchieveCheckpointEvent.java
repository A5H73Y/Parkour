package io.github.a5h73y.parkour.event;

import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import org.bukkit.entity.Player;

public class PlayerAchieveCheckpointEvent extends ParkourEvent {

    private final Checkpoint checkpoint;

    public PlayerAchieveCheckpointEvent(final Player player, final String courseName, final Checkpoint checkpoint) {
        super(player, courseName);
        this.checkpoint = checkpoint;
    }

    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

}

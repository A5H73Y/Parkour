package io.github.a5h73y.parkour.event;

import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import org.bukkit.entity.Player;

public class ParkourCheckpointEvent extends ParkourEvent {

    private final Checkpoint checkpoint;

    public ParkourCheckpointEvent(final Player player, final String courseName, final Checkpoint checkpoint) {
        super(player, courseName);
        this.checkpoint = checkpoint;
    }

    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

}

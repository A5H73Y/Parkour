package io.github.a5h73y.parkour.event;

import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import org.bukkit.entity.Player;

public class ParkourCheckpointAllEvent extends ParkourCheckpointEvent {
    public ParkourCheckpointAllEvent(final Player player, final String courseName, final Checkpoint checkpoint) {
        super(player, courseName, checkpoint);
    }
}

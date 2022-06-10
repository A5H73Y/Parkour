package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class ParkourFinishEvent extends ParkourEvent {

    // TODO add enum with level of finish?

    public ParkourFinishEvent(final Player player, final String courseName) {
        super(player, courseName);
    }

}

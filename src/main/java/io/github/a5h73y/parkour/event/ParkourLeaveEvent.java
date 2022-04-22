package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;

public class ParkourLeaveEvent extends ParkourEvent {

    private final boolean silent;

    public ParkourLeaveEvent(final Player player, final String courseName, final boolean silent) {
        super(player, courseName);
        this.silent = silent;
    }

    public boolean isSilent() {
        return silent;
    }
}

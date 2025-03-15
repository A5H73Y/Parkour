package io.github.a5h73y.parkour.event;

import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import org.bukkit.entity.Player;

public class ParkourFinishEvent extends ParkourEvent {

    private final ParkourSession session;

    public ParkourFinishEvent(final Player player, final String courseName, final ParkourSession session) {
        super(player, courseName);
        this.session = session;
    }

    public ParkourSession getSession() {
        return session;
    }
}

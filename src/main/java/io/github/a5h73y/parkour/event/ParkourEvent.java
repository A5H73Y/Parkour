package io.github.a5h73y.parkour.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class ParkourEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final String courseName;
    private final Player player;

    public ParkourEvent(final Player player, final String courseName) {
        this.player = player;
        this.courseName = courseName;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getCourseName() {
        return courseName;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

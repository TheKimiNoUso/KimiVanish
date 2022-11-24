package com.kiminouso.kimivanish.events;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class HidePlayerEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    @Getter private final int vanishLevel;
    @Getter private final Location playerLocation;

    public HidePlayerEvent(Player player, int level, Location location) {
        super(player);
        this.player = player;
        this.vanishLevel = level;
        this.playerLocation = location;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}

package com.kiminouso.kimivanish.events;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * isVanished = True when the player is vanished.
 * location = Location when the player first vanished.
 * level = Player's level in the map (Their personal level or the default level.
 * player = Vanished player.
 */
public class VanishStatusUpdateEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    @Getter private final int vanishLevel;
    @Getter private final boolean vanished;
    @Getter private final Location playerLocation;

    public VanishStatusUpdateEvent(Player player, int level, boolean isVanished, Location location) {
        super(player);
        this.player = player;
        this.vanishLevel = level;
        this.vanished = isVanished;
        this.playerLocation = location;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}

package com.kiminouso.kimivanish;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerFlyEvent implements Listener {
    private final Set<UUID> warnedPlayers = new HashSet<>();

    @EventHandler
    private void onPlayerFly(PlayerMoveEvent event) {
        if (!event.getPlayer().hasPermission("kimivanish.hide"))
            return;

        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.warn-unhidden-flyers"))
            return;

        if (Essentials.getPlugin(Essentials.class).getUser(event.getPlayer().getUniqueId()).isHidden())
            return;

        if (!event.getPlayer().isFlying())
            return;

        if (warnedPlayers.contains(event.getPlayer().getUniqueId()))
            return;

        warnedPlayers.add(event.getPlayer().getUniqueId());
        event.getPlayer().sendMessage(ConfigUtils.getMessage("messages.vanish.flight.warning", event.getPlayer()));
        Bukkit.getScheduler().runTaskLater(KimiVanish.getPlugin(KimiVanish.class), () -> warnedPlayers.remove(event.getPlayer().getUniqueId()), 30 * 20L);
    }
}

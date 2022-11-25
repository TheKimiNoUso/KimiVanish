package com.kiminouso.kimivanish;

import com.kiminouso.kimivanish.events.VanishStatusUpdateEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class VanishListeners implements Listener {
    private final Set<UUID> recentlyUnhidden = new HashSet<>(); // Keeps recently unvanished players for 5 seconds.
    private final Map<UUID, String> pendingMessages = new HashMap<>();

    @EventHandler
    private void onVanish(VanishStatusUpdateEvent event) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.protect-recently-unvanished"))
            return;

        if (recentlyUnhidden.contains(event.getPlayer().getUniqueId()))
            return;

        if (event.isVanished())
            return;

        recentlyUnhidden.add(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLater(KimiVanish.getPlugin(KimiVanish.class), () -> recentlyUnhidden.remove(event.getPlayer().getUniqueId()), 5 * 20L);
    }

    @EventHandler
    private void onVanishPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("kimivanish.hide")) {
            if (event.getPlayer().hasPermission("kimivanish.hide-onjoin"))
                event.getPlayer().setInvisible(true); // Temporary invisiblity to not reveal player whilst loading


            KimiVanishPlayer.loadPlayer(event.getPlayer()).thenAccept((v) -> {
                if (event.getPlayer().hasPermission("kimivanish.hide-onjoin")) {
                    Bukkit.getScheduler().runTask(KimiVanish.getPlugin(KimiVanish.class), () -> {
                        event.getPlayer().setInvisible(false);
                        KimiVanish.getPlugin(KimiVanish.class).getHideManager().vanishPlayer(event.getPlayer());
                    });
                }
            });
        }
    }

    @EventHandler
    private void onVanishPlayerDamage(EntityDamageEvent event) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.protect-players"))
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        if (KimiVanish.getPlugin(KimiVanish.class).getHideManager().isVanished(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onRecentlyUnvanishedPlayerDamage(EntityDamageEvent event) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.protect-recently-unvanished"))
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        if (recentlyUnhidden.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onJoinMessage(PlayerJoinEvent event) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.hide-connection-message"))
            return;

        if (event.getPlayer().hasPermission("kimivanish.hide"))
            event.setJoinMessage(null);
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        KimiVanishPlayer.unloadPlayer(event.getPlayer());

        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.hide-connection-message"))
            return;

        if (event.getPlayer().hasPermission("kimivanish.hide"))
            event.setQuitMessage(null);
    }

    @EventHandler
    private void onKickMessage(PlayerKickEvent event) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.hide-connection-message"))
            return;

        if (event.getPlayer().hasPermission("kimivanish.hide"))
            event.setLeaveMessage(null);
    }

    @EventHandler
    private void onVanishHunger(FoodLevelChangeEvent event) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.prevent-hunger-loss"))
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        if (KimiVanish.getPlugin(KimiVanish.class).getHideManager().isVanished(player))
            event.setCancelled(true);
    }

    @EventHandler
    private void onVanishChat(AsyncPlayerChatEvent event) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getHideManager().isVanished(event.getPlayer()))
            return;

        Player player = event.getPlayer();

        if (pendingMessages.containsKey(player.getUniqueId()) && event.getMessage().equals(pendingMessages.get(player.getUniqueId()))) {
            pendingMessages.remove(player.getUniqueId());
        } else {
            event.setCancelled(true);
            pendingMessages.remove(player.getUniqueId());
            pendingMessages.put(player.getUniqueId(), event.getMessage());
            TextComponent message = new TextComponent(ConfigUtils.getMessage("messages.vanish.confirm-message", player, event.getMessage()));
            player.spigot().sendMessage(message);
        }
    }
}

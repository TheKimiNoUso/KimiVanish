package com.kiminouso.kimivanish;

import com.google.common.eventbus.AllowConcurrentEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class VanishManager {
    public final TreeMap<Integer, List<Player>> vanishLevels = new TreeMap<>();
    public final Set<UUID> currentlyVanished = new HashSet<>();
    public final Set<UUID> canVanish = new HashSet<>();
    public final Set<UUID> interactPlayers = new HashSet<>();
    public final Set<UUID> notifyPlayers = new HashSet<>();
    public final Set<UUID> itemPlayers = new HashSet<>();
    public final Set<UUID> flightPlayers = new HashSet<>();
    public final Set<UUID> locationPlayers = new HashSet<>();

    public void clearLists() {
        vanishLevels.clear();
        canVanish.clear();
        interactPlayers.clear();
        notifyPlayers.clear();
        itemPlayers.clear();
        flightPlayers.clear();
        locationPlayers.clear();
    }

    public void addPlayer(Player player, int level) {
        vanishLevels.compute(level, (key, value) -> {
            if (value == null) {
                value = new ArrayList<>();
            }
            value.add(player);
            return value;
        });

        vanishLevels.headMap(level, true).values().forEach(sublist -> sublist.forEach(p -> {
            if (!p.hasPermission("kimivanish.hide"))
                return;

            player.showPlayer(KimiVanish.getPlugin(KimiVanish.class), p);
        }));

        KimiVanish.getPlugin(KimiVanish.class).getStorage().findVanishUser(player.getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).interactSetting())
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().interactPlayers.add(player.getUniqueId());

            if (entry.get(0).notifySetting())
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().notifyPlayers.add(player.getUniqueId());

            if (entry.get(0).itemSetting())
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().itemPlayers.add(player.getUniqueId());

            if (entry.get(0).locationSetting())
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().locationPlayers.add(player.getUniqueId());

            if (entry.get(0).nightVisionSetting())
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false, false));

            if (entry.get(0).flightSetting())
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().flightPlayers.add(player.getUniqueId());
        });
    }

    public void removePlayer(Player player) {
        vanishLevels.values().forEach(sublist -> sublist.remove(player));
    }

    public boolean isVanished(Player player) {
        return currentlyVanished.contains(player.getUniqueId());
    }
}


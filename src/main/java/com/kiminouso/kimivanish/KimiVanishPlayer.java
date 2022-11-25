package com.kiminouso.kimivanish;

import lombok.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
@RequiredArgsConstructor
public class KimiVanishPlayer {
    private final static Map<UUID, KimiVanishPlayer> onlineKimiVanishPlayers = new HashMap<>();

    public static KimiVanishPlayer getOnlineVanishPlayer(UUID uuid) {
        return onlineKimiVanishPlayers.get(uuid);
    }

    public static List<KimiVanishPlayer> getOnlineVanishPlayers() {
        return new ArrayList<>(onlineKimiVanishPlayers.values());
    }

    static CompletableFuture<Void> loadPlayer(Player player) {
        KimiVanishPlayer vanishPlayer = new KimiVanishPlayer(player);
        onlineKimiVanishPlayers.put(player.getUniqueId(), vanishPlayer);
        HideManager manager = KimiVanish.getPlugin(KimiVanish.class).getHideManager();
        manager.addPlayer(player);
        return vanishPlayer.loadSettings();
    }

    static void unloadPlayer(Player player) {
        onlineKimiVanishPlayers.remove(player.getUniqueId());
        HideManager manager = KimiVanish.getPlugin(KimiVanish.class).getHideManager();
        manager.removePlayer(player);
    }

    private final Player player;
    private Settings settings;
    @Setter(AccessLevel.PACKAGE)
    private boolean vanished;

    public CompletableFuture<Void> saveSettings() {
        return settings.save(player.getUniqueId());
    }

    public CompletableFuture<Void> loadSettings() {
        return KimiVanish.getPlugin(KimiVanish.class).getStorage().loadSettings(player.getUniqueId()).thenAccept((settings) -> {
            this.settings = settings;
        });
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class Settings {
        private boolean fly = true;
        private boolean interact = false;
        private boolean item = true;
        private boolean location = true;
        private boolean nightvision = true;
        private boolean notify = false;

        private CompletableFuture<Void> save(UUID uuid) {
            return KimiVanish.getPlugin(KimiVanish.class).getStorage().saveSettings(uuid, this);
        }
    }
}

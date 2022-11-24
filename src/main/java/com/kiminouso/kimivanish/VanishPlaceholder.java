package com.kiminouso.kimivanish;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanishPlaceholder extends PlaceholderExpansion {
    private final KimiVanish plugin;

    public VanishPlaceholder(KimiVanish plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull
    String getIdentifier() {
        return "kimivanish";
    }

    @Override
    public @NotNull
    String getAuthor() {
        return "KimiNoUso";
    }

    @Override
    public @NotNull
    String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }


    @Override
    public @Nullable
    String onPlaceholderRequest(Player player, @NotNull String params) {
        return this.onRequest(player, params);
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player.getPlayer() == null) {
            return "not found";
        }

        if (params.equalsIgnoreCase("vanished")) {
            if (KimiVanish.getPlugin(KimiVanish.class).getHideManager().isVanished(player.getPlayer())) {
                return "true";
            } else {
                return "false";
            }
        } else if (params.equalsIgnoreCase("level")) {
            return String.valueOf(KimiVanish.getPlugin(KimiVanish.class).getHideManager().checkLevelFromPermission(player.getPlayer()));
        } else {
            return null;
        }
    }
}

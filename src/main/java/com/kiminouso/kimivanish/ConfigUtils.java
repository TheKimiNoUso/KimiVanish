package com.kiminouso.kimivanish;

import me.clip.placeholderapi.PlaceholderAPI;
import me.tippie.tippieutils.functions.ColorUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigUtils {
    public static String getMessage(String path, Player player, final String... vars) {
        String fromConfig = KimiVanish.getPlugin(KimiVanish.class).getConfig().getString(path);
        String prefix = KimiVanish.getPlugin(KimiVanish.class).getConfig().getString("prefix");

        if (fromConfig == null)
            return prefix + " Unknown Message";

        if (vars != null) {
            for (int i = 0; i < vars.length; i++) {
                fromConfig = fromConfig.replace("{" + i + "}", vars[i]);
            }
        }

        String finalMessage = prefix + " " + fromConfig;

        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, Stream.of(ColorUtils.translateColorCodes('&', finalMessage)).map(component -> component.toLegacyText()).collect(Collectors.joining()));
        } else {
            return Stream.of(ColorUtils.translateColorCodes('&', finalMessage)).map(component -> component.toLegacyText()).collect(Collectors.joining());
        }
    }

    public static String getMessage(String path, boolean showPrefix) {
        String fromConfig = KimiVanish.getPlugin(KimiVanish.class).getConfig().getString(path);
        String prefix = KimiVanish.getPlugin(KimiVanish.class).getConfig().getString("prefix");

        if (showPrefix) {
            return Stream.of(ColorUtils.translateColorCodes('&', prefix + " " + fromConfig)).map(component -> component.toLegacyText()).collect(Collectors.joining());
        } else {
            return Stream.of(ColorUtils.translateColorCodes('&', fromConfig)).map(component -> component.toLegacyText()).collect(Collectors.joining());
        }
    }
}

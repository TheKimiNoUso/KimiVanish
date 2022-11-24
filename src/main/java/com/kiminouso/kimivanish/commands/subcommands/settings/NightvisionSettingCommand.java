package com.kiminouso.kimivanish.commands.subcommands.settings;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.KimiVanishPlayer;
import com.kiminouso.kimivanish.Storage;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class NightvisionSettingCommand extends TippieCommand implements Listener {
    public NightvisionSettingCommand() {
        super.subLevel = 2;
        super.name = "nightvision";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "Toggle nightvision for vanish";
        super.permission = "kimivanish.settings.nightvision";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        KimiVanishPlayer vanishPlayer = KimiVanishPlayer.getOnlineVanishPlayer(player.getUniqueId());
        KimiVanishPlayer.Settings settings = vanishPlayer.getSettings();

        if (settings.isNightvision()) {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.nightvision.off", player));
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        } else {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.nightvision.on", player));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true,false,false));
        }

        settings.setNightvision(!settings.isNightvision());
        vanishPlayer.saveSettings();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(KimiVanish.getPlugin(KimiVanish.class), () -> {
            KimiVanishPlayer vanishPlayer = KimiVanishPlayer.getOnlineVanishPlayer(event.getPlayer().getUniqueId());
            if (vanishPlayer != null && vanishPlayer.getSettings().isNightvision()) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true,false,false));
            }
        },30L);
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        if (event.getPlayer().hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            event.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

}

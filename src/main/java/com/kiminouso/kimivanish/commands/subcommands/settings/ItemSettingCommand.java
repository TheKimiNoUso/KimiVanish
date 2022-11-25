package com.kiminouso.kimivanish.commands.subcommands.settings;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.KimiVanishPlayer;
import com.kiminouso.kimivanish.Storage;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

public class ItemSettingCommand extends TippieCommand implements Listener {
    public ItemSettingCommand() {
        super.subLevel = 2;
        super.name = "items";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "Toggle item pick-up and drop for vanished players";
        super.permission = "kimivanish.settings.item";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        KimiVanishPlayer vanishPlayer = KimiVanishPlayer.getOnlineVanishPlayer(player.getUniqueId());
        KimiVanishPlayer.Settings settings = vanishPlayer.getSettings();

        if (settings.isItem()) {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.item.off", player));
        } else {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.item.on", player));
        }

        settings.setItem(!settings.isItem());
        vanishPlayer.saveSettings();
    }

    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        KimiVanishPlayer vanishPlayer = KimiVanishPlayer.getOnlineVanishPlayer(player.getUniqueId());
        if (vanishPlayer == null)
            return;

        KimiVanishPlayer.Settings settings = vanishPlayer.getSettings();
        if (KimiVanish.getPlugin(KimiVanish.class).getHideManager().isVanished(player) && settings.isItem())
            event.setCancelled(true);
    }

    @EventHandler
    private void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        if (!KimiVanish.getPlugin(KimiVanish.class).getHideManager().isVanished(player))
            return;

        KimiVanishPlayer.Settings settings = KimiVanishPlayer.getOnlineVanishPlayer(player.getUniqueId()).getSettings();
        if (settings.isItem())
            event.setCancelled(true);
    }
}

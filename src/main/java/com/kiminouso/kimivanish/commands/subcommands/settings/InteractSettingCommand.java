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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class InteractSettingCommand extends TippieCommand implements Listener {
    public InteractSettingCommand() {
        super.subLevel = 2;
        super.name = "interact";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "Toggle physical interactions for vanished players";
        super.permission = "kimivanish.settings.interact";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        KimiVanishPlayer vanishPlayer = KimiVanishPlayer.getOnlineVanishPlayer(player.getUniqueId());
        KimiVanishPlayer.Settings settings = vanishPlayer.getSettings();

        if (settings.isInteract()) {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.interact.toggle-off", player));
        } else {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.interact.toggle-on", player));
        }

        settings.setInteract(!settings.isInteract());
        vanishPlayer.saveSettings();
    }

    @EventHandler
    private void onPhysicalInteraction(PlayerInteractEvent event) {
        if (KimiVanish.getPlugin(KimiVanish.class).getHideManager().isVanished(event.getPlayer())) {
            if (canInteract(event.getPlayer()))
                return;

            if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock() != null) {
                event.setCancelled(true);
            }
        }
    }

    private boolean canInteract(Player player) {
        return KimiVanishPlayer.getOnlineVanishPlayer(player.getUniqueId()).getSettings().isInteract();
    }
}

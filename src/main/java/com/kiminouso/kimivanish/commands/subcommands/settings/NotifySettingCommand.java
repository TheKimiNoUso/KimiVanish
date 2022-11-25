package com.kiminouso.kimivanish.commands.subcommands.settings;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.KimiVanishPlayer;
import com.kiminouso.kimivanish.Storage;
import com.kiminouso.kimivanish.events.HidePlayerEvent;
import com.kiminouso.kimivanish.events.UnhidePlayerEvent;
import com.kiminouso.kimivanish.events.VanishStatusUpdateEvent;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class NotifySettingCommand extends TippieCommand implements Listener {
    public NotifySettingCommand() {
        super.subLevel = 2;
        super.name = "notify";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "Toggle notifications for vanished players";
        super.permission = "kimivanish.settings.notify";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        KimiVanishPlayer vanishPlayer = KimiVanishPlayer.getOnlineVanishPlayer(player.getUniqueId());
        if (vanishPlayer == null)
            return;

        KimiVanishPlayer.Settings settings = vanishPlayer.getSettings();

        if (settings.isNotify()) {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.notify.off", player));
        } else {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.notify.on", player));
        }

        settings.setNotify(!settings.isNotify());
        vanishPlayer.saveSettings();
    }

    @EventHandler
    private void onVanishStatusChange(VanishStatusUpdateEvent event) {
        if (event.isVanished())
            sendMessage("messages.vanish.notify.player-vanished", event.getPlayer(), String.valueOf(KimiVanish.getPlugin(KimiVanish.class).getHideManager().checkLevelFromMap(event.getPlayer())));
        else
            sendMessage("messages.vanish.notify.player-unvanished", event.getPlayer(), "");
    }

    private void sendMessage(String path, Player vanishedPlayer, String level) {
        KimiVanishPlayer.getOnlineVanishPlayers().stream()
                .filter(p -> p.getSettings().isNotify())
                .forEach(p -> p.getPlayer().sendMessage(ConfigUtils.getMessage(path, vanishedPlayer, vanishedPlayer.getName(), level)));
    }
}

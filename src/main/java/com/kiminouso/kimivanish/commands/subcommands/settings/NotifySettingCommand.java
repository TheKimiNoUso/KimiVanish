package com.kiminouso.kimivanish.commands.subcommands.settings;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.Storage;
import com.kiminouso.kimivanish.events.HidePlayerEvent;
import com.kiminouso.kimivanish.events.UnhidePlayerEvent;
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

        Storage storage = KimiVanish.getPlugin(KimiVanish.class).getStorage();

        storage.findVanishUser(player.getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).notifySetting()) {
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.notify.off", player));
                storage.setNotifySetting(player.getUniqueId(), false);
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().notifyPlayers.remove(player.getUniqueId());
            } else {
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.notify.on", player));
                storage.setNotifySetting(player.getUniqueId(), true);
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().notifyPlayers.add(player.getUniqueId());
            }
        });
    }

    @EventHandler
    private void onHide(HidePlayerEvent event) {
        sendMessage("messages.vanish.notify.player-vanished", event.getPlayer(), String.valueOf(KimiVanish.getPlugin(KimiVanish.class).getHideManager().checkLevelFromMap(event.getPlayer())));
    }

    @EventHandler
    private void onUnhide(UnhidePlayerEvent event) {
        sendMessage("messages.vanish.notify.player-unvanished", event.getPlayer(), "");
    }

    private void sendMessage(String path, Player vanishedPlayer, String level) {
        if (KimiVanish.getPlugin(KimiVanish.class).getVanishManager().notifyPlayers.isEmpty())
            return;

        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().notifyPlayers.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null)
                return;

            player.sendMessage(ConfigUtils.getMessage(path, vanishedPlayer, vanishedPlayer.getName(), level));
        });
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().notifyPlayers.remove(event.getPlayer().getUniqueId());
    }
}

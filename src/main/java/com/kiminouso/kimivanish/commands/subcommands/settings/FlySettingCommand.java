package com.kiminouso.kimivanish.commands.subcommands.settings;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.Storage;
import com.kiminouso.kimivanish.events.HidePlayerEvent;
import com.kiminouso.kimivanish.events.UnhidePlayerEvent;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class FlySettingCommand extends TippieCommand {
    public FlySettingCommand() {
        super.subLevel = 2;
        super.name = "fly";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "Toggle fly for vanish";
        super.permission = "kimivanish.settings.fly";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        Storage storage = KimiVanish.getPlugin(KimiVanish.class).getStorage();

        storage.findVanishUser(player.getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).flightSetting()) {
                storage.setFightSetting(player.getUniqueId(), false);
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.flight.off", player));
            } else {
                if (KimiVanish.getPlugin(KimiVanish.class).getVanishManager().isVanished(player)) {
                    player.setFlying(true);
                }
                storage.setFightSetting(player.getUniqueId(), true);
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.flight.on", player));
            }
        });
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getStorage().findVanishUser(event.getPlayer().getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).flightSetting()) {
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().flightPlayers.add(event.getPlayer().getUniqueId());
            }
        });

        if (event.getPlayer().hasPermission("kimivanish.hide.onjoin")
                && KimiVanish.getPlugin(KimiVanish.class).getVanishManager().flightPlayers.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().setFlying(true);
        }
    }

    @EventHandler
    private void onHide(HidePlayerEvent event) {
        if (KimiVanish.getPlugin(KimiVanish.class).getVanishManager().flightPlayers.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().setFlying(true);
        }
    }

    @EventHandler
    private void onUnhide(UnhidePlayerEvent event) {
        if (KimiVanish.getPlugin(KimiVanish.class).getVanishManager().flightPlayers.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().setFlying(false);
        }
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().flightPlayers.remove(event.getPlayer().getUniqueId());
    }

}

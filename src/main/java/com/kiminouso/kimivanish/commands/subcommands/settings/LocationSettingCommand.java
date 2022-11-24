package com.kiminouso.kimivanish.commands.subcommands.settings;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.Storage;
import com.kiminouso.kimivanish.events.VanishStatusUpdateEvent;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LocationSettingCommand extends TippieCommand implements Listener {
    public LocationSettingCommand() {
        super.subLevel = 2;
        super.name = "save-location";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "Toggle location saving for when you exit vanish";
        super.permission = "kimivanish.settings.save-location";
    }

    private final Map<UUID, Location> savedLocations = new HashMap<>();

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        Storage storage = KimiVanish.getPlugin(KimiVanish.class).getStorage();

        storage.findVanishUser(player.getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty())
                return;

            if (entry.get(0).locationSetting()) {
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.location.off", player));
                storage.setLocationSetting(player.getUniqueId(), false);
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().locationPlayers.remove(player.getUniqueId());
            } else {
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.location.on", player));
                storage.setLocationSetting(player.getUniqueId(), true);
                KimiVanish.getPlugin(KimiVanish.class).getVanishManager().locationPlayers.add(player.getUniqueId());
            }
        });

    }

    @EventHandler
    private void onVanish(VanishStatusUpdateEvent event) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getVanishManager().locationPlayers.contains(event.getPlayer().getUniqueId()))
            return;

        Player player = event.getPlayer();
        Location loc;

        if (event.isVanished()) {
            loc = event.getPlayer().getLocation();
            savedLocations.put(player.getUniqueId(), loc);
            event.getPlayer().sendMessage(ConfigUtils.getMessage("messages.vanish.location.saved", player, String.valueOf(loc.getBlockX()), String.valueOf(loc.getBlockY()), String.valueOf(loc.getBlockZ())));
        } else {
            loc = savedLocations.get(player.getUniqueId());
            savedLocations.remove(player.getUniqueId());
            player.teleport(loc);
            event.getPlayer().sendMessage(ConfigUtils.getMessage("messages.vanish.location.teleported", player));
        }
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        savedLocations.remove(event.getPlayer().getUniqueId());
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().locationPlayers.remove(event.getPlayer().getUniqueId());
    }
}

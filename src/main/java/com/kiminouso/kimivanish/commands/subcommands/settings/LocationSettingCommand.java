package com.kiminouso.kimivanish.commands.subcommands.settings;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.KimiVanishPlayer;
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

        KimiVanishPlayer vanishPlayer = KimiVanishPlayer.getOnlineVanishPlayer(player.getUniqueId());
        KimiVanishPlayer.Settings settings = vanishPlayer.getSettings();

        if (settings.isLocation()) {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.location.toggle-off", player));
        } else {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.location.toggle-on", player));
        }

        settings.setLocation(!settings.isLocation());
        vanishPlayer.saveSettings();

    }

    @EventHandler
    private void onVanish(VanishStatusUpdateEvent event) {
        if (KimiVanishPlayer.getOnlineVanishPlayer(event.getPlayer().getUniqueId()) == null)
            return;

        if (!KimiVanishPlayer.getOnlineVanishPlayer(event.getPlayer().getUniqueId()).getSettings().isLocation())
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
}

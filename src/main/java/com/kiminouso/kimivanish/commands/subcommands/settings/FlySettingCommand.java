package com.kiminouso.kimivanish.commands.subcommands.settings;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.KimiVanishPlayer;
import com.kiminouso.kimivanish.Storage;
import com.kiminouso.kimivanish.events.HidePlayerEvent;
import com.kiminouso.kimivanish.events.UnhidePlayerEvent;
import com.kiminouso.kimivanish.events.VanishStatusUpdateEvent;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class FlySettingCommand extends TippieCommand implements Listener {
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

        KimiVanishPlayer vanishPlayer = KimiVanishPlayer.getOnlineVanishPlayer(player.getUniqueId());
        KimiVanishPlayer.Settings settings = vanishPlayer.getSettings();

        if (settings.isFly()) {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.flight.toggle-off", player));
        } else {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.flight.toggle-on", player));
        }

        settings.setFly(!settings.isFly());
        vanishPlayer.saveSettings();
    }


    @EventHandler
    private void onHide(VanishStatusUpdateEvent event) {
        KimiVanishPlayer player = KimiVanishPlayer.getOnlineVanishPlayer(event.getPlayer().getUniqueId());

        if (player == null)
            return;

        if (player.getSettings().isFly()) {
            event.getPlayer().setFlying(event.isVanished());
        }
    }
}

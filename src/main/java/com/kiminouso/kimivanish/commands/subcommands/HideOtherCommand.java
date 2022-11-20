package com.kiminouso.kimivanish.commands.subcommands;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HideOtherCommand extends TippieCommand {
    public HideOtherCommand() {
        super.subLevel = 1;
        super.name = "hideother";
        super.prefix = ConfigUtils.getMessage("prefix", null);
        super.description = "Hide someone else from other players";
        super.permission = "kimivanish.hide.others";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage("Player couldn't be found");
            return;
        }

        int level = KimiVanish.getPlugin(KimiVanish.class).getHideManager().checkLevel(player);

        if (KimiVanish.getPlugin(KimiVanish.class).getVanishManager().currentlyVanished.contains(player.getUniqueId())) {
            KimiVanish.getPlugin(KimiVanish.class).getHideManager().RemoveVanishStatus(player);
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.unhide", false));
        } else {
            KimiVanish.getPlugin(KimiVanish.class).getHideManager().VanishPlayer(player);
            sender.sendMessage(ConfigUtils.getMessage("messages.vanish.hideother", player, player.getName(), String.valueOf(level)));
            if (sender instanceof Player sentPlayer) {
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.hideother-other", sentPlayer, String.valueOf(level), sentPlayer.getName()));
            }
        }
    }
}

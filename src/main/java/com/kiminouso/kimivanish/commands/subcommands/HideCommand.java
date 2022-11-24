package com.kiminouso.kimivanish.commands.subcommands;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HideCommand extends TippieCommand {

    public HideCommand() {
        super.subLevel = 1;
        super.name = "hide";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "Hide yourself from other players";
        super.permission = "kimivanish.hide";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        int level = KimiVanish.getPlugin(KimiVanish.class).getHideManager().checkLevelFromPermission(player);

        if (KimiVanish.getPlugin(KimiVanish.class).getHideManager().isVanished(player)) {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.unhide", player, String.valueOf(level)));
            KimiVanish.getPlugin(KimiVanish.class).getHideManager().showPlayer(player);
        } else {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.hide", player, String.valueOf(level)));
            KimiVanish.getPlugin(KimiVanish.class).getHideManager().vanishPlayer(player);
        }
    }
}

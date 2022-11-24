package com.kiminouso.kimivanish.commands.subcommands;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.VanishManager;
import com.kiminouso.kimivanish.commands.HideManager;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SetLevelCommand extends TippieCommand {
    public SetLevelCommand() {
        super.subLevel = 1;
        super.name = "setlevel";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "Set your own vanish level";
        super.permission = "kimivanish.setlevel";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        VanishManager vanishManager = KimiVanish.getPlugin(KimiVanish.class).getVanishManager();
        HideManager hideManager = KimiVanish.getPlugin(KimiVanish.class).getHideManager();

        int level = hideManager.checkLevelFromPermission(player);
        int futureLevel;

        if (level == 1 || level == 0) {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.set-level.deny.lowest-level", player));
            return;
        }

        try {
            futureLevel = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.set-level.not-integer", player));
            return;
        }

        if (futureLevel > level) {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.set-level.deny.not-available", player));

            return;
        }

        vanishManager.removePlayer(player);
        player.sendMessage(ConfigUtils.getMessage("messages.vanish.set-level.allow", player, String.valueOf(futureLevel)));
        hideManager.RemoveVanishStatus(player);

        vanishManager.addPlayer(player, futureLevel);
        hideManager.VanishPlayer(player);
    }

    @Override
    public List<String> completes(CommandSender sender, Command command, String alias, String[] args) {
        Player player = (Player) sender;
        List<String> availableLevels = new ArrayList<>();
        for (int i = 1; i <= KimiVanish.getPlugin(KimiVanish.class).getHideManager().checkLevelFromPermission(player); i++) {
            availableLevels.add(String.valueOf(i));
        }

        return availableLevels;
    }
}
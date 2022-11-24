package com.kiminouso.kimivanish.commands.subcommands;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.KimiVanish;
import com.kiminouso.kimivanish.KimiVanishPlayer;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends TippieCommand {
    public ReloadCommand() {
        super.subLevel = 1;
        super.name = "reload";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "Reload config and vanish users";
        super.permission = "kimivanish.reload";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        KimiVanish.getPlugin(KimiVanish.class).reloadConfig();
        sender.sendMessage("Config has been reloaded");

//        sender.sendMessage("Vanish players have been reloaded");
    }
}

package com.kiminouso.kimivanish.commands.subcommands;

import com.kiminouso.kimivanish.KimiVanish;
import me.tippie.tippieutils.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class ListCommand extends TippieCommand {
    public ListCommand() {
        super.subLevel = 1;
        super.name = "list";
        super.prefix = "§6[§3KimiVanish§6]§r";
        super.description = "List all vanish users";
        super.permission = "kimivanish.list";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().vanishLevels.forEach((key, value) -> {
            if (value.isEmpty())
                return;

            sender.sendMessage(key + " - " + value.stream().map(Player::getName).collect(Collectors.joining(", ")));
        });
    }
}

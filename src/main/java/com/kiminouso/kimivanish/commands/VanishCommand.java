package com.kiminouso.kimivanish.commands;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.commands.subcommands.*;
import me.tippie.tippieutils.commands.TippieCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends TippieCommand {
    public VanishCommand() {
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.name = "vanish";
        super.getSubCommands().add(new HideCommand());
        super.getSubCommands().add(new HideOtherCommand());
        super.getSubCommands().add(new SettingsCommand());
        super.getSubCommands().add(new ListCommand());
        super.getSubCommands().add(new ReloadCommand());
        super.getSubCommands().add(new SetLevelCommand());
    }

    @Override
    protected void sendHelpMessage(CommandSender sender, String label, String prefix) {
        if (!(sender instanceof Player player))
            return;

        sender.sendMessage(ConfigUtils.getMessage("messages.vanish.help.general-header", player));

        getSubCommands().forEach(cmd -> {
            if (!player.hasPermission(cmd.getPermission()))
                return;

            TextComponent helpMessage = new TextComponent(ConfigUtils.getMessage("messages.vanish.help.commands", player, label, cmd.getName(), cmd.getDescription()));
            helpMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to execute command.")));
            helpMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " " + cmd.getName()));
            player.spigot().sendMessage(helpMessage);
        });
    }
}

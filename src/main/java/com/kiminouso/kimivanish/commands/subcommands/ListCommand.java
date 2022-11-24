package com.kiminouso.kimivanish.commands.subcommands;

import com.kiminouso.kimivanish.ConfigUtils;
import com.kiminouso.kimivanish.HideManager;
import com.kiminouso.kimivanish.KimiVanish;
import me.tippie.tippieutils.commands.TippieCommand;
import me.tippie.tippieutils.guis.GuiBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ListCommand extends TippieCommand {
    public ListCommand() {
        super.subLevel = 1;
        super.name = "list";
        super.prefix = ConfigUtils.getMessage("prefix", false);
        super.description = "List all vanish users";
        super.permission = "kimivanish.list";
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
        if (!(sender instanceof Player player))
            return;

        HideManager hideManager = KimiVanish.getPlugin(KimiVanish.class).getHideManager();

        var vanished = hideManager.getCurrentlyVanished();

        if (vanished.isEmpty()) {
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.list.empty", false));
            return;
        }

        if (KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.use-gui-list")) {
            Bukkit.getScheduler().runTaskLater(KimiVanish.getPlugin(KimiVanish.class), () -> openVanishGui(player, vanished), 10L);
        } else {
            hideManager.getVanishLevels().forEach((key, value) -> {
                if (value.isEmpty())
                    return;
                player.sendMessage(ConfigUtils.getMessage("messages.vanish.list.chat", player, String.valueOf(key), value.stream()
                        .filter(hideManager::isVanished)
                        .map(Player::getName)
                        .collect(Collectors.joining(", "))));
            });
        }
    }

    public static void openVanishGui(Player player, Set<UUID> vanished) {
        GuiBuilder builder = new GuiBuilder(5, "Currently vanished", null);

        int count = 0;
        if (vanished.size() > 0) {
            for (UUID user : vanished) {
                Player p = Bukkit.getPlayer(user);
                if (p == null)
                    return;

                ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta skull = (SkullMeta) item.getItemMeta();

                skull.setOwningPlayer(p);
                skull.setDisplayName("§9" + p.getName());
                skull.setLore(List.of("§7Actual Level: " + KimiVanish.getPlugin(KimiVanish.class).getHideManager().checkLevelFromPermission(p), "§7Effective Level: " + KimiVanish.getPlugin(KimiVanish.class).getHideManager().checkLevelFromMap(p)));
                item.setItemMeta(skull);

                builder.setSlot(count, item, (InventoryClickEvent, OpenGUI) -> {
                    if (InventoryClickEvent.isRightClick() || InventoryClickEvent.isLeftClick() && !InventoryClickEvent.isShiftClick()) {
                        player.teleport(p);
                    }
                    if (InventoryClickEvent.isShiftClick()) {
                        if (player.hasPermission("kimivanish.hide.others")) {
                            Bukkit.getScheduler().runTaskLater(KimiVanish.getPlugin(KimiVanish.class), () -> openVanishGui(player, vanished), 10L);
                            KimiVanish.getPlugin(KimiVanish.class).getHideManager().showPlayer(player);
                            player.sendMessage(ConfigUtils.getMessage("messages.vanish.unhide", false));
                        }
                    }
                });
                count++;
            }
        }
        builder.open(player, KimiVanish.getPlugin(KimiVanish.class).getGuiManager());
    }
}

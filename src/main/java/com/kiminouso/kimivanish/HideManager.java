package com.kiminouso.kimivanish;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.kiminouso.kimivanish.events.HidePlayerEvent;
import com.kiminouso.kimivanish.events.UnhidePlayerEvent;
import com.kiminouso.kimivanish.events.VanishStatusUpdateEvent;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class HideManager implements Listener {
    @Getter
    private final Set<UUID> currentlyVanished = new HashSet<>();
    @Getter
    private final TreeMap<Integer, List<Player>> vanishLevels = new TreeMap<>();

    public void addPlayer(Player player){
        int level = checkLevelFromPermission(player);
        vanishLevels.compute(level, (key, value) -> {
            if (value == null) {
                value = new ArrayList<>();
            }
            value.add(player);
            return value;
        });

//        vanishLevels.headMap(level, true).values().forEach(sublist -> sublist.forEach(p -> {
//            if (!p.hasPermission("kimivanish.hide"))
//                return;
//
//            player.showPlayer(KimiVanish.getPlugin(KimiVanish.class), p);
//        }));
    }

    public void removePlayer(Player player){
        vanishLevels.values().forEach(list -> list.remove(player));
    }


    private final BossBar vanishedBossBar = Bukkit.createBossBar(ConfigUtils.getMessage("messages.vanish.bossbar", false), BarColor.WHITE, BarStyle.SOLID);
    private final Set<UUID> recentlySneaked = new HashSet<>();

    public void vanishPlayer(Player player) {
        KimiVanishPlayer vanishPlayer = KimiVanishPlayer.getOnlineVanishPlayer(player.getUniqueId());
        if (vanishPlayer == null) throw new IllegalArgumentException("Given player is not a loaded KimiVanishPlayer");
        vanishPlayer.setVanished(true);

        List<Player> canSee = canSeeInVansish(player);

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> !canSee.contains(p))
                .forEach(p -> p.hidePlayer(KimiVanish.getPlugin(KimiVanish.class), player));

        showHideBossbar(player, true);
        currentlyVanished.add(player.getUniqueId());

        VanishStatusUpdateEvent updateEvent = new VanishStatusUpdateEvent(player, checkLevelFromMap(player), true, player.getLocation());
        Bukkit.getPluginManager().callEvent(updateEvent);

        HidePlayerEvent hideEvent = new HidePlayerEvent(player, checkLevelFromMap(player), player.getLocation());
        Bukkit.getPluginManager().callEvent(hideEvent);

        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null) {
            Essentials essentials = Essentials.getPlugin(Essentials.class);
            User essentialsUser = essentials.getUser(player.getUniqueId());
            essentialsUser.setHidden(true);
        }
    }

    public List<Player> canSeeInVansish(Player player){
        return vanishLevels.tailMap(checkLevelFromMap(player), true).values().stream().flatMap(Collection::stream).toList();
    }

    private void showHideBossbar(Player player, boolean shouldShow) {
        if (!KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.bossbar"))
            return;

        if (shouldShow) {
            vanishedBossBar.addPlayer(player);
        } else {
            vanishedBossBar.removePlayer(player);
        }
    }

    public void showPlayer(Player player) {
        KimiVanishPlayer vanishPlayer = KimiVanishPlayer.getOnlineVanishPlayer(player.getUniqueId());
        if (vanishPlayer == null) throw new IllegalArgumentException("Given player is not a loaded KimiVanishPlayer");
        vanishPlayer.setVanished(false);

        Bukkit.getOnlinePlayers().stream().filter(viewer -> viewer != player).forEach(viewer -> viewer.showPlayer(KimiVanish.getPlugin(KimiVanish.class), player));
        showHideBossbar(player, false);

        currentlyVanished.remove(player.getUniqueId());
        VanishStatusUpdateEvent updateEvent = new VanishStatusUpdateEvent(player, checkLevelFromMap(player), false, player.getLocation());
        Bukkit.getPluginManager().callEvent(updateEvent);


        UnhidePlayerEvent unhideEvent = new UnhidePlayerEvent(player, player.getLocation());
        Bukkit.getPluginManager().callEvent(unhideEvent);

        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null) {
            Essentials essentials = Essentials.getPlugin(Essentials.class);
            User essentialsUser = essentials.getUser(player.getUniqueId());
            essentialsUser.setHidden(false);
        }
    }

    public int checkLevelFromPermission(Player player) {
        int level = player.getEffectivePermissions().stream()
                .filter(perm -> perm.getPermission().startsWith("kimivanish.level."))
                .map(perm -> perm.getPermission().replace("kimivanish.level.", ""))
                .mapToInt(Integer::parseInt)
                .max().orElse(1);

        return Math.max(level, 1);

    }

    public int checkLevelFromMap(Player player) {
        var optional = vanishLevels
                .entrySet().stream()
                .filter(entry -> entry.getValue().contains(player)).findFirst();

        if (optional.isEmpty()) return checkLevelFromPermission(player);
        else return optional.get().getKey();
    }

    // Replaced with VanishListeners

//    @EventHandler
//    private void onPlayerJoin(PlayerJoinEvent event) {
//        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().currentlyVanished.forEach(uuid -> {
//            event.getPlayer().hidePlayer(KimiVanish.getPlugin(KimiVanish.class), Bukkit.getPlayer(uuid));
//        });
//
//        if (event.getPlayer().hasPermission("kimivanish.hide")) {
//            KimiVanish.getPlugin(KimiVanish.class).getVanishManager().addPlayer(event.getPlayer(), checkLevelFromPermission(event.getPlayer()));
//            KimiVanish.getPlugin(KimiVanish.class).getVanishManager().canVanish.add(event.getPlayer().getUniqueId());
//        }
//    }

//    @EventHandler
//    private void onPlayerLeave(PlayerQuitEvent event) {
//        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().removePlayer(event.getPlayer());
//        KimiVanish.getPlugin(KimiVanish.class).getVanishManager().canVanish.remove(event.getPlayer().getUniqueId());
//    }

    public void fixLevel(Player player) {
        int level = checkLevelFromMap(player);

        // Hide player for values below
        vanishLevels.headMap(level, false).values().stream().flatMap(Collection::stream).forEach(p -> {
            p.hidePlayer(KimiVanish.getPlugin(KimiVanish.class), player);
            player.showPlayer(KimiVanish.getPlugin(KimiVanish.class), p);
        });

        // Show player for values above
        vanishLevels.tailMap(level, true).values().stream().flatMap(Collection::stream).forEach(p -> p.showPlayer(KimiVanish.getPlugin(KimiVanish.class), player));

        // Show current level to player
        vanishLevels.get(level).forEach(p -> player.showPlayer(KimiVanish.getPlugin(KimiVanish.class), p));
    }

    @EventHandler
    private void onVanish(VanishStatusUpdateEvent event) {
        KimiVanish.getPlugin(KimiVanish.class).getStorage().findVanishUser(event.getPlayer().getUniqueId()).thenAccept((entry) -> {
            if (entry.isEmpty() || !entry.get(0).notifySetting())
                return;

            KimiVanishPlayer.getOnlineVanishPlayers().stream().filter(player -> player.getSettings().isNotify()).forEach(p -> { // Possibly want to include a permission check here
                Player player = p.getPlayer();

                if (event.isVanished()) {
                    player.sendMessage(ConfigUtils.getMessage("messages.vanish.notify.player-unvanished", player, player.getName()));
                } else {
                    player.sendMessage(ConfigUtils.getMessage("messages.vanish.notify.player-vanished", player, player.getName(), String.valueOf(checkLevelFromPermission(player))));
                }
            });
        });
    }

    /**
     * @deprecated Unsafe
     */
    @Deprecated
    public void reloadPlayers(){
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.hasPermission("kimivanish.hide"))
                .forEach(KimiVanishPlayer::unloadPlayer);


        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.hasPermission("kimivanish.hide"))
                .forEach(KimiVanishPlayer::loadPlayer);
    }

    @EventHandler
    private void onSpectatorInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player))
            return;

        if (!isVanished(player))
            return;

        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(false);
        }
    }

    public void unhideAll() {
        for (UUID uuid : currentlyVanished) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null)
                return;

            showPlayer(player);
            player.sendMessage(ConfigUtils.getMessage("messages.vanish.unhide-all", false));
        }
    }

    @EventHandler
    private void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("kimivanish.staffmode"))
            return;

        if (!isVanished(player))
            return;

        if (!event.isSneaking())
            return;

        if (recentlySneaked.contains(player.getUniqueId())) {
            recentlySneaked.remove(player.getUniqueId());
            if (player.getGameMode() != GameMode.SPECTATOR) {
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.setGameMode(Bukkit.getServer().getDefaultGameMode());
            }
        } else {
            recentlySneaked.add(player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(KimiVanish.getPlugin(KimiVanish.class), () -> recentlySneaked.remove(player.getUniqueId()), 10L);
        }
    }

    @EventHandler
    private void onPlayerRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (!isVanished(player))
            return;

        if (!player.hasPermission("kimivanish.staffmode") && player.getGameMode() != GameMode.SPECTATOR)
            return;

        if (!(event.getRightClicked() instanceof Player clicked))
            return;

        player.openInventory(clicked.getInventory());
    }

    private final Runnable actionBarTask = () -> currentlyVanished.forEach(uuid -> {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            return;

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ConfigUtils.getMessage("messages.vanish.bossbar", false)));
    });

//    @Deprecated // This task is probably unncessary and can be moved to be ran when someone who can vanish joins the server or when someone vanishes.
//    private final Runnable vanishTask = () -> KimiVanishPlayer.getOnlineVanishPlayers().forEach(vanishPlayer -> {
//        Player player = vanishPlayer.getPlayer();
//
//        vanishLevels.tailMap(checkLevelFromMap(player), true)
//                .values().forEach(sublist -> sublist.forEach(p -> player.showPlayer(KimiVanish.getPlugin(KimiVanish.class), p)));
//    });

    private BukkitTask activeActionBarTask = null;

    public void startActionBarTask() {
        if (activeActionBarTask != null)
            activeActionBarTask.cancel();

        activeActionBarTask = Bukkit.getScheduler().runTaskTimer(KimiVanish.getPlugin(KimiVanish.class), actionBarTask, 0, 20L);
    }

    public void endActionBarTask() {
        if (activeActionBarTask != null) {
            activeActionBarTask.cancel();
            activeActionBarTask = null;
        }
    }

    public boolean actionBarTaskIsActive() {
        return activeActionBarTask != null;
    }

    private BukkitTask activeVanishTask = null;

    public void startVanishTask() {
//        if (activeVanishTask != null)
//            activeVanishTask.cancel();
//
//        activeVanishTask = Bukkit.getScheduler().runTaskTimer(KimiVanish.getPlugin(KimiVanish.class), vanishTask, 0, 1L);
    }

    public void endVanishTask() {
//        if (activeVanishTask != null) {
//            activeVanishTask.cancel();
//            activeVanishTask = null;
//        }
    }

    public boolean vanishTaskIsActive() {
        return activeVanishTask != null;
    }

    public boolean isVanished(Player player) {
        return currentlyVanished.contains(player.getUniqueId());
    }

    public void setVanishLevel(Player player, int level) {
        vanishLevels.values().forEach(list -> list.remove(player));
        vanishLevels.compute(level, (key, value) -> {
            if (value == null) {
                value = new ArrayList<>();
            }
            value.add(player);
            return value;
        });

        if (isVanished(player)){
            fixLevel(player);
        }
    }
}

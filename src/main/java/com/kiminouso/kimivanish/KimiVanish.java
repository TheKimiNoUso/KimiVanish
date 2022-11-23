package com.kiminouso.kimivanish;

import com.kiminouso.kimivanish.commands.HideManager;
import com.kiminouso.kimivanish.commands.VanishCommand;
import com.kiminouso.kimivanish.commands.subcommands.settings.*;
import lombok.Getter;
import me.tippie.tippieutils.guis.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class KimiVanish extends JavaPlugin {
    @Getter
    private Storage storage;
    @Getter
    private VanishManager vanishManager;
    @Getter
    private HideManager hideManager;
    @Getter private GuiManager guiManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        vanishManager = new VanishManager();
        VanishCommand vanishCommand = new VanishCommand();
        hideManager = new HideManager();
        VanishListeners vanishListeners = new VanishListeners();
        ItemSettingCommand itemSettingCommand = new ItemSettingCommand();
        NotifySettingCommand notifySettingCommand = new NotifySettingCommand();
        FlySettingCommand flySettingCommand = new FlySettingCommand();
        InteractSettingCommand interactSettingCommand = new InteractSettingCommand();
        LocationSettingCommand locationSettingCommand = new LocationSettingCommand();
        NightvisionSettingCommand nightvisionSettingCommand = new NightvisionSettingCommand();
        guiManager = new GuiManager(this);
        storage = new Storage(this);

        Bukkit.getPluginCommand("vanish").setExecutor(vanishCommand);
        Bukkit.getServer().getPluginManager().registerEvents(vanishListeners, this);
        Bukkit.getServer().getPluginManager().registerEvents(itemSettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(notifySettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(interactSettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(nightvisionSettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(flySettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(locationSettingCommand, this);
        Bukkit.getServer().getPluginManager().registerEvents(this.hideManager, this);

        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getServer().getLogger().log(Level.INFO, "PlaceholderAPI has been found! Enabling placeholder expansion...");
            new VanishPlaceholder(this).register();
        } else {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not find PlaceholderAPI! Placeholder expansion is disabled.");
        }

        if (Bukkit.getServer().getPluginManager().getPlugin("Essentials") != null) {
            Bukkit.getServer().getLogger().log(Level.INFO, "Essentials has been found! Enabling Essentials vanish hook...");
        } else {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not find Essentials! Essentials hook is disabled.");
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> vanishManager.canVanish.forEach(player -> {
            int level = hideManager.checkLevel(Bukkit.getPlayer(player));
            vanishManager.vanishLevels.tailMap(level, true)
                    .values()
                    .forEach(sublist -> sublist.forEach(p -> Bukkit.getPlayer(player).showPlayer(this, p))
                    );
        }), 0L, 1L);

        if (KimiVanish.getPlugin(KimiVanish.class).getConfig().getBoolean("settings.vanish.actionbar")) {
            hideManager.start();
        }
    }

    @Override
    public void onDisable() {
        hideManager.unhideAll();
        if (hideManager.isActive()) {
            hideManager.end();
        }
    }
}
